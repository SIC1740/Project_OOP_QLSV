package com.myuniv.sm.service;

import com.myuniv.sm.dao.util.DBConnection;
import com.myuniv.sm.model.Notification;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service for handling notifications
 */
public class NotificationService {
    private static final Logger logger = Logger.getLogger(NotificationService.class.getName());

    /**
     * Get GiangVien_Lop_MonHoc ID by lecturer ID, class ID, and subject ID
     */
    public int getGvLopMonId(String maGiangVien, String maLop, String maMon) throws ServiceException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT id FROM GiangVien_Lop_MonHoc " +
                     "WHERE ma_giangvien = ? AND ma_lop = ? AND ma_mon = ?")) {
            
            pstmt.setString(1, maGiangVien);
            pstmt.setString(2, maLop);
            pstmt.setString(3, maMon);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                } else {
                    throw new ServiceException("Không tìm thấy mối quan hệ giữa giảng viên, lớp và môn học");
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy ID giảng viên-lớp-môn học", e);
            throw new ServiceException("Lỗi truy vấn: " + e.getMessage());
        }
    }

    /**
     * Create a new notification
     */
    public boolean createNotification(Notification notification) throws ServiceException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO ThongBao (gv_lop_mon_id, tieu_de, noi_dung) " +
                     "VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, notification.getGvLopMonId());
            pstmt.setString(2, notification.getTieuDe());
            pstmt.setString(3, notification.getNoiDung());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        notification.setThongbaoId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
            
            return false;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi tạo thông báo", e);
            throw new ServiceException("Không thể tạo thông báo: " + e.getMessage());
        }
    }

    /**
     * Update an existing notification
     */
    public boolean updateNotification(Notification notification) throws ServiceException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "UPDATE ThongBao SET tieu_de = ?, noi_dung = ? " +
                     "WHERE thongbao_id = ?")) {
            
            pstmt.setString(1, notification.getTieuDe());
            pstmt.setString(2, notification.getNoiDung());
            pstmt.setInt(3, notification.getThongbaoId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi cập nhật thông báo", e);
            throw new ServiceException("Không thể cập nhật thông báo: " + e.getMessage());
        }
    }

    /**
     * Delete a notification
     */
    public boolean deleteNotification(int thongbaoId) throws ServiceException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "DELETE FROM ThongBao WHERE thongbao_id = ?")) {
            
            pstmt.setInt(1, thongbaoId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi xóa thông báo", e);
            throw new ServiceException("Không thể xóa thông báo: " + e.getMessage());
        }
    }

    /**
     * Get a notification by ID
     */
    public Notification getNotificationById(int thongbaoId) throws ServiceException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT tb.*, gvlm.ma_giangvien, gvlm.ma_lop, gvlm.ma_mon, " +
                     "gv.ho_ten AS ten_giangvien, mh.ten_mon " +
                     "FROM ThongBao tb " +
                     "JOIN GiangVien_Lop_MonHoc gvlm ON tb.gv_lop_mon_id = gvlm.id " +
                     "JOIN GiangVien gv ON gvlm.ma_giangvien = gv.ma_giangvien " +
                     "JOIN MonHoc mh ON gvlm.ma_mon = mh.ma_mon " +
                     "WHERE tb.thongbao_id = ?")) {
            
            pstmt.setInt(1, thongbaoId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToNotification(rs);
                } else {
                    throw new ServiceException("Không tìm thấy thông báo với ID: " + thongbaoId);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi truy vấn thông báo", e);
            throw new ServiceException("Không thể lấy thông báo: " + e.getMessage());
        }
    }

    /**
     * Get all notifications for a specific class related to a student
     */
    public List<Notification> getNotificationsForStudent(String msv) throws ServiceException {
        List<Notification> notifications = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT tb.*, gvlm.ma_giangvien, gvlm.ma_lop, gvlm.ma_mon, " +
                     "gv.ho_ten AS ten_giangvien, mh.ten_mon " +
                     "FROM ThongBao tb " +
                     "JOIN GiangVien_Lop_MonHoc gvlm ON tb.gv_lop_mon_id = gvlm.id " +
                     "JOIN GiangVien gv ON gvlm.ma_giangvien = gv.ma_giangvien " +
                     "JOIN MonHoc mh ON gvlm.ma_mon = mh.ma_mon " +
                     "JOIN SinhVien sv ON gvlm.ma_lop = sv.ma_lop " +
                     "WHERE sv.msv = ? " +
                     "ORDER BY tb.ngay_tao DESC")) {
            
            pstmt.setString(1, msv);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    notifications.add(mapResultSetToNotification(rs));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi truy vấn thông báo cho sinh viên", e);
            throw new ServiceException("Không thể lấy danh sách thông báo: " + e.getMessage());
        }
        
        return notifications;
    }

    /**
     * Get all notifications created by a specific lecturer
     */
    public List<Notification> getNotificationsForLecturer(String maGiangVien) throws ServiceException {
        List<Notification> notifications = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT tb.*, gvlm.ma_giangvien, gvlm.ma_lop, gvlm.ma_mon, " +
                     "gv.ho_ten AS ten_giangvien, mh.ten_mon " +
                     "FROM ThongBao tb " +
                     "JOIN GiangVien_Lop_MonHoc gvlm ON tb.gv_lop_mon_id = gvlm.id " +
                     "JOIN GiangVien gv ON gvlm.ma_giangvien = gv.ma_giangvien " +
                     "JOIN MonHoc mh ON gvlm.ma_mon = mh.ma_mon " +
                     "WHERE gvlm.ma_giangvien = ? " +
                     "ORDER BY tb.ngay_tao DESC")) {
            
            pstmt.setString(1, maGiangVien);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    notifications.add(mapResultSetToNotification(rs));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi truy vấn thông báo cho giảng viên", e);
            throw new ServiceException("Không thể lấy danh sách thông báo: " + e.getMessage());
        }
        
        return notifications;
    }

    /**
     * Get all notifications for a specific class and subject
     */
    public List<Notification> getNotificationsForClassAndSubject(String maLop, String maMon) throws ServiceException {
        List<Notification> notifications = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT tb.*, gvlm.ma_giangvien, gvlm.ma_lop, gvlm.ma_mon, " +
                     "gv.ho_ten AS ten_giangvien, mh.ten_mon " +
                     "FROM ThongBao tb " +
                     "JOIN GiangVien_Lop_MonHoc gvlm ON tb.gv_lop_mon_id = gvlm.id " +
                     "JOIN GiangVien gv ON gvlm.ma_giangvien = gv.ma_giangvien " +
                     "JOIN MonHoc mh ON gvlm.ma_mon = mh.ma_mon " +
                     "WHERE gvlm.ma_lop = ? AND gvlm.ma_mon = ? " +
                     "ORDER BY tb.ngay_tao DESC")) {
            
            pstmt.setString(1, maLop);
            pstmt.setString(2, maMon);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    notifications.add(mapResultSetToNotification(rs));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi truy vấn thông báo cho lớp và môn học", e);
            throw new ServiceException("Không thể lấy danh sách thông báo: " + e.getMessage());
        }
        
        return notifications;
    }

    /**
     * Get all notifications for admin
     */
    public List<Notification> getAllNotifications() throws ServiceException {
        List<Notification> notifications = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT tb.*, gvlm.ma_giangvien, gvlm.ma_lop, gvlm.ma_mon, " +
                     "gv.ho_ten AS ten_giangvien, mh.ten_mon " +
                     "FROM ThongBao tb " +
                     "JOIN GiangVien_Lop_MonHoc gvlm ON tb.gv_lop_mon_id = gvlm.id " +
                     "JOIN GiangVien gv ON gvlm.ma_giangvien = gv.ma_giangvien " +
                     "JOIN MonHoc mh ON gvlm.ma_mon = mh.ma_mon " +
                     "ORDER BY tb.ngay_tao DESC")) {
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    notifications.add(mapResultSetToNotification(rs));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi truy vấn tất cả thông báo", e);
            throw new ServiceException("Không thể lấy danh sách thông báo: " + e.getMessage());
        }
        
        return notifications;
    }

    /**
     * Helper method to map ResultSet to Notification object
     */
    private Notification mapResultSetToNotification(ResultSet rs) throws SQLException {
        Notification notification = new Notification();
        notification.setThongbaoId(rs.getInt("thongbao_id"));
        notification.setGvLopMonId(rs.getInt("gv_lop_mon_id"));
        notification.setTieuDe(rs.getString("tieu_de"));
        notification.setNoiDung(rs.getString("noi_dung"));
        
        // Convert SQL timestamp to LocalDateTime
        Timestamp timestamp = rs.getTimestamp("ngay_tao");
        if (timestamp != null) {
            notification.setNgayTao(timestamp.toLocalDateTime());
        }
        
        // Set additional fields for reference
        notification.setMaGiangVien(rs.getString("ma_giangvien"));
        notification.setTenGiangVien(rs.getString("ten_giangvien"));
        notification.setMaLop(rs.getString("ma_lop"));
        notification.setMaMon(rs.getString("ma_mon"));
        notification.setTenMon(rs.getString("ten_mon"));
        
        return notification;
    }
} 