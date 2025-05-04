package com.myuniv.sm.service;

import com.myuniv.sm.model.Subject;
import com.myuniv.sm.dao.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SubjectService {
    private static final Logger logger = Logger.getLogger(SubjectService.class.getName());

    /**
     * Lấy tất cả môn học từ DB
     */
    public List<Subject> findAll() throws ServiceException {
        List<Subject> subjects = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM MonHoc ORDER BY ten_mon")) {
            
            while (rs.next()) {
                Subject subject = new Subject();
                subject.setMaMon(rs.getString("ma_mon"));
                subject.setTenMon(rs.getString("ten_mon"));
                subject.setSoTinChi(rs.getInt("so_tin_chi"));
                subjects.add(subject);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi truy vấn danh sách môn học", e);
            throw new ServiceException("Không thể lấy danh sách môn học: " + e.getMessage());
        }
        
        return subjects;
    }
    
    /**
     * Lấy tất cả môn học từ DB không ném exception, trả về danh sách rỗng nếu có lỗi
     * Phương thức này hữu ích cho các thành phần UI không muốn xử lý exception
     */
    public List<Subject> getAllSubjects() {
        List<Subject> subjects = new ArrayList<>();
        
        try {
            subjects = findAll();
        } catch (ServiceException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy danh sách môn học", e);
        }
        
        return subjects;
    }

    /**
     * Lấy một môn học theo mã
     */
    public Subject getSubjectByMaMon(String maMon) throws ServiceException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM MonHoc WHERE ma_mon = ?")) {
            
            pstmt.setString(1, maMon);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Subject subject = new Subject();
                    subject.setMaMon(rs.getString("ma_mon"));
                    subject.setTenMon(rs.getString("ten_mon"));
                    subject.setSoTinChi(rs.getInt("so_tin_chi"));
                    return subject;
                } else {
                    throw new ServiceException("Môn học với mã " + maMon + " không tồn tại");
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi tìm môn học theo mã", e);
            throw new ServiceException("Không thể tìm môn học: " + e.getMessage());
        }
    }

    /**
     * Lấy một môn học theo mã (alias cho getSubjectByMaMon)
     */
    public Subject getSubjectById(String subjectId) throws ServiceException {
        return getSubjectByMaMon(subjectId);
    }

    /**
     * Tạo môn học mới
     */
    public boolean createSubject(Subject subject) throws ServiceException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO MonHoc (ma_mon, ten_mon, so_tin_chi) VALUES (?, ?, ?)")) {
            
            pstmt.setString(1, subject.getMaMon());
            pstmt.setString(2, subject.getTenMon());
            pstmt.setInt(3, subject.getSoTinChi());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi tạo môn học mới", e);
            throw new ServiceException("Không thể tạo môn học: " + e.getMessage());
        }
    }

    /**
     * Cập nhật thông tin môn học
     */
    public boolean updateSubject(Subject subject) throws ServiceException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "UPDATE MonHoc SET ten_mon = ?, so_tin_chi = ? WHERE ma_mon = ?")) {
            
            pstmt.setString(1, subject.getTenMon());
            pstmt.setInt(2, subject.getSoTinChi());
            pstmt.setString(3, subject.getMaMon());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi cập nhật môn học", e);
            throw new ServiceException("Không thể cập nhật môn học: " + e.getMessage());
        }
    }

    /**
     * Xóa môn học
     */
    public boolean deleteSubject(String maMon) throws ServiceException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM MonHoc WHERE ma_mon = ?")) {
            
            pstmt.setString(1, maMon);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi xóa môn học", e);
            throw new ServiceException("Không thể xóa môn học: " + e.getMessage());
        }
    }

    /**
     * Kiểm tra xem môn học có đang được phân công cho lớp nào không
     */
    public boolean isSubjectAssignedToClass(String maMon) throws ServiceException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT COUNT(*) FROM LopHoc_MonHoc WHERE ma_mon = ?")) {
            
            pstmt.setString(1, maMon);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                return false;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi kiểm tra liên kết của môn học", e);
            throw new ServiceException("Không thể kiểm tra liên kết môn học: " + e.getMessage());
        }
    }
    
    /**
     * Lấy danh sách môn học của một lớp
     */
    public List<Subject> getSubjectsByClass(String maLop) throws ServiceException {
        List<Subject> subjects = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT m.* FROM MonHoc m " +
                     "JOIN LopHoc_MonHoc lm ON m.ma_mon = lm.ma_mon " +
                     "WHERE lm.ma_lop = ? " +
                     "ORDER BY m.ten_mon")) {
            
            pstmt.setString(1, maLop);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Subject subject = new Subject();
                    subject.setMaMon(rs.getString("ma_mon"));
                    subject.setTenMon(rs.getString("ten_mon"));
                    subject.setSoTinChi(rs.getInt("so_tin_chi"));
                    subjects.add(subject);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy danh sách môn học của lớp", e);
            throw new ServiceException("Không thể lấy danh sách môn học của lớp: " + e.getMessage());
        }
        
        return subjects;
    }
    
    /**
     * Gán môn học cho lớp
     */
    public boolean assignSubjectToClass(String maLop, String maMon) throws ServiceException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO LopHoc_MonHoc (ma_lop, ma_mon) VALUES (?, ?)")) {
            
            pstmt.setString(1, maLop);
            pstmt.setString(2, maMon);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi gán môn học cho lớp", e);
            throw new ServiceException("Không thể gán môn học cho lớp: " + e.getMessage());
        }
    }
    
    /**
     * Hủy gán môn học cho lớp
     */
    public boolean unassignSubjectFromClass(String maLop, String maMon) throws ServiceException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "DELETE FROM LopHoc_MonHoc WHERE ma_lop = ? AND ma_mon = ?")) {
            
            pstmt.setString(1, maLop);
            pstmt.setString(2, maMon);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi hủy gán môn học khỏi lớp", e);
            throw new ServiceException("Không thể hủy gán môn học khỏi lớp: " + e.getMessage());
        }
    }
} 