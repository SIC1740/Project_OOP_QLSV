package com.myuniv.sm.service;

import com.myuniv.sm.dao.util.DBConnection;
import com.myuniv.sm.model.GradeEntryPeriod;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class for managing grade entry periods
 */
public class GradeEntryPeriodService {
    private static final Logger logger = Logger.getLogger(GradeEntryPeriodService.class.getName());

    /**
     * Create a new grade entry period
     */
    public boolean createEntryPeriod(GradeEntryPeriod period) throws ServiceException {
        try (Connection conn = DBConnection.getConnection()) {
            // First, get the gv_lop_mon_id for this subject and class
            Integer gvLopMonId = null;
            try (PreparedStatement pstmtFind = conn.prepareStatement(
                     "SELECT id FROM GiangVien_Lop_MonHoc " +
                     "WHERE ma_mon = ? AND ma_lop = ?")) {
                
                pstmtFind.setString(1, period.getMaMon());
                pstmtFind.setString(2, period.getMaLop());
                
                try (ResultSet rs = pstmtFind.executeQuery()) {
                    if (rs.next()) {
                        gvLopMonId = rs.getInt("id");
                    } else {
                        throw new ServiceException("Không tìm thấy thông tin giảng viên-lớp-môn học");
                    }
                }
            }
            
            // Insert new entry period
            try (PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO NhapDiem (gv_lop_mon_id, thoi_gian_bat_dau_nhap, thoi_gian_ket_thuc_nhap) " +
                     "VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                
                pstmt.setInt(1, gvLopMonId);
                pstmt.setTimestamp(2, Timestamp.valueOf(period.getThoi_gian_bat_dau_nhap()));
                pstmt.setTimestamp(3, Timestamp.valueOf(period.getThoi_gian_ket_thuc_nhap()));
                
                int affectedRows = pstmt.executeUpdate();
                
                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            period.setNhapdiem_id(generatedKeys.getInt(1));
                        }
                    }
                    return true;
                }
                return false;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error creating grade entry period", e);
            throw new ServiceException("Không thể tạo thời gian nhập điểm: " + e.getMessage());
        }
    }
    
    /**
     * Delete a grade entry period
     */
    public boolean deleteEntryPeriod(int nhapdiem_id) throws ServiceException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "DELETE FROM NhapDiem WHERE nhapdiem_id = ?")) {
            
            pstmt.setInt(1, nhapdiem_id);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error deleting grade entry period", e);
            throw new ServiceException("Không thể xóa thời gian nhập điểm: " + e.getMessage());
        }
    }
    
    /**
     * Get all grade entry periods with details
     */
    public List<GradeEntryPeriod> getAllEntryPeriods() throws ServiceException {
        List<GradeEntryPeriod> periods = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT nd.nhapdiem_id, nd.gv_lop_mon_id, nd.thoi_gian_bat_dau_nhap, nd.thoi_gian_ket_thuc_nhap, " +
                     "glm.ma_mon, mh.ten_mon, glm.ma_lop, gv.ho_ten " +
                     "FROM NhapDiem nd " +
                     "JOIN GiangVien_Lop_MonHoc glm ON nd.gv_lop_mon_id = glm.id " +
                     "JOIN MonHoc mh ON glm.ma_mon = mh.ma_mon " +
                     "JOIN GiangVien gv ON glm.ma_giangvien = gv.ma_giangvien " +
                     "ORDER BY nd.thoi_gian_bat_dau_nhap DESC")) {
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    GradeEntryPeriod period = new GradeEntryPeriod();
                    period.setNhapdiem_id(rs.getInt("nhapdiem_id"));
                    period.setGv_lop_mon_id(rs.getInt("gv_lop_mon_id"));
                    period.setMaMon(rs.getString("ma_mon"));
                    period.setTenMon(rs.getString("ten_mon"));
                    period.setMaLop(rs.getString("ma_lop"));
                    period.setTenGiangVien(rs.getString("ho_ten"));
                    period.setThoi_gian_bat_dau_nhap(rs.getTimestamp("thoi_gian_bat_dau_nhap").toLocalDateTime());
                    period.setThoi_gian_ket_thuc_nhap(rs.getTimestamp("thoi_gian_ket_thuc_nhap").toLocalDateTime());
                    period.setChoPhepNhapDiem(true);
                    
                    periods.add(period);
                }
            }
            
            return periods;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving grade entry periods", e);
            throw new ServiceException("Không thể lấy danh sách thời gian nhập điểm: " + e.getMessage());
        }
    }
    
    /**
     * Get grade entry periods for a specific subject
     */
    public List<GradeEntryPeriod> getEntryPeriodsForSubject(String maMon) throws ServiceException {
        List<GradeEntryPeriod> periods = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT nd.nhapdiem_id, nd.gv_lop_mon_id, nd.thoi_gian_bat_dau_nhap, nd.thoi_gian_ket_thuc_nhap, " +
                     "glm.ma_mon, mh.ten_mon, glm.ma_lop, gv.ho_ten " +
                     "FROM NhapDiem nd " +
                     "JOIN GiangVien_Lop_MonHoc glm ON nd.gv_lop_mon_id = glm.id " +
                     "JOIN MonHoc mh ON glm.ma_mon = mh.ma_mon " +
                     "JOIN GiangVien gv ON glm.ma_giangvien = gv.ma_giangvien " +
                     "WHERE glm.ma_mon = ? " +
                     "ORDER BY nd.thoi_gian_bat_dau_nhap DESC")) {
            
            pstmt.setString(1, maMon);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    GradeEntryPeriod period = new GradeEntryPeriod();
                    period.setNhapdiem_id(rs.getInt("nhapdiem_id"));
                    period.setGv_lop_mon_id(rs.getInt("gv_lop_mon_id"));
                    period.setMaMon(rs.getString("ma_mon"));
                    period.setTenMon(rs.getString("ten_mon"));
                    period.setMaLop(rs.getString("ma_lop"));
                    period.setTenGiangVien(rs.getString("ho_ten"));
                    period.setThoi_gian_bat_dau_nhap(rs.getTimestamp("thoi_gian_bat_dau_nhap").toLocalDateTime());
                    period.setThoi_gian_ket_thuc_nhap(rs.getTimestamp("thoi_gian_ket_thuc_nhap").toLocalDateTime());
                    period.setChoPhepNhapDiem(true);
                    
                    periods.add(period);
                }
            }
            
            return periods;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving grade entry periods for subject", e);
            throw new ServiceException("Không thể lấy thời gian nhập điểm cho môn học: " + e.getMessage());
        }
    }
    
    /**
     * Get grade entry periods for a specific subject and class
     */
    public List<GradeEntryPeriod> getEntryPeriodsForSubjectAndClass(String maMon, String maLop) throws ServiceException {
        List<GradeEntryPeriod> periods = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT nd.nhapdiem_id, nd.gv_lop_mon_id, nd.thoi_gian_bat_dau_nhap, nd.thoi_gian_ket_thuc_nhap, " +
                     "glm.ma_mon, mh.ten_mon, glm.ma_lop, gv.ho_ten " +
                     "FROM NhapDiem nd " +
                     "JOIN GiangVien_Lop_MonHoc glm ON nd.gv_lop_mon_id = glm.id " +
                     "JOIN MonHoc mh ON glm.ma_mon = mh.ma_mon " +
                     "JOIN GiangVien gv ON glm.ma_giangvien = gv.ma_giangvien " +
                     "WHERE glm.ma_mon = ? AND glm.ma_lop = ? " +
                     "ORDER BY nd.thoi_gian_bat_dau_nhap DESC")) {
            
            pstmt.setString(1, maMon);
            pstmt.setString(2, maLop);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    GradeEntryPeriod period = new GradeEntryPeriod();
                    period.setNhapdiem_id(rs.getInt("nhapdiem_id"));
                    period.setGv_lop_mon_id(rs.getInt("gv_lop_mon_id"));
                    period.setMaMon(rs.getString("ma_mon"));
                    period.setTenMon(rs.getString("ten_mon"));
                    period.setMaLop(rs.getString("ma_lop"));
                    period.setTenGiangVien(rs.getString("ho_ten"));
                    period.setThoi_gian_bat_dau_nhap(rs.getTimestamp("thoi_gian_bat_dau_nhap").toLocalDateTime());
                    period.setThoi_gian_ket_thuc_nhap(rs.getTimestamp("thoi_gian_ket_thuc_nhap").toLocalDateTime());
                    period.setChoPhepNhapDiem(true);
                    
                    periods.add(period);
                }
            }
            
            return periods;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving grade entry periods for subject and class", e);
            throw new ServiceException("Không thể lấy thời gian nhập điểm cho môn học và lớp: " + e.getMessage());
        }
    }
    
    /**
     * Check if a teacher can enter grades for a specific subject
     */
    public boolean canEnterGrades(String maMon) throws ServiceException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT COUNT(*) FROM NhapDiem nd " +
                     "JOIN GiangVien_Lop_MonHoc glm ON nd.gv_lop_mon_id = glm.id " +
                     "WHERE glm.ma_mon = ? " +
                     "AND ? BETWEEN nd.thoi_gian_bat_dau_nhap AND nd.thoi_gian_ket_thuc_nhap")) {
            
            LocalDateTime now = LocalDateTime.now();
            pstmt.setString(1, maMon);
            pstmt.setTimestamp(2, Timestamp.valueOf(now));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            
            return false;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error checking if teacher can enter grades", e);
            throw new ServiceException("Không thể kiểm tra quyền nhập điểm: " + e.getMessage());
        }
    }
    
    /**
     * Get the current active entry period for a subject
     */
    public GradeEntryPeriod getCurrentEntryPeriod(String maMon, String maLop) throws ServiceException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT nd.nhapdiem_id, nd.gv_lop_mon_id, nd.thoi_gian_bat_dau_nhap, nd.thoi_gian_ket_thuc_nhap, " +
                     "glm.ma_mon, mh.ten_mon, glm.ma_lop " +
                     "FROM NhapDiem nd " +
                     "JOIN GiangVien_Lop_MonHoc glm ON nd.gv_lop_mon_id = glm.id " +
                     "JOIN MonHoc mh ON glm.ma_mon = mh.ma_mon " +
                     "WHERE glm.ma_mon = ? AND glm.ma_lop = ? " +
                     "AND ? BETWEEN nd.thoi_gian_bat_dau_nhap AND nd.thoi_gian_ket_thuc_nhap " +
                     "ORDER BY nd.thoi_gian_ket_thuc_nhap DESC LIMIT 1")) {
            
            LocalDateTime now = LocalDateTime.now();
            pstmt.setString(1, maMon);
            pstmt.setString(2, maLop);
            pstmt.setTimestamp(3, Timestamp.valueOf(now));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    GradeEntryPeriod period = new GradeEntryPeriod();
                    period.setNhapdiem_id(rs.getInt("nhapdiem_id"));
                    period.setGv_lop_mon_id(rs.getInt("gv_lop_mon_id"));
                    period.setMaMon(rs.getString("ma_mon"));
                    period.setTenMon(rs.getString("ten_mon"));
                    period.setMaLop(rs.getString("ma_lop"));
                    period.setThoi_gian_bat_dau_nhap(rs.getTimestamp("thoi_gian_bat_dau_nhap").toLocalDateTime());
                    period.setThoi_gian_ket_thuc_nhap(rs.getTimestamp("thoi_gian_ket_thuc_nhap").toLocalDateTime());
                    period.setChoPhepNhapDiem(true);
                    
                    return period;
                }
            }
            
            return null;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error getting current entry period", e);
            throw new ServiceException("Không thể lấy thông tin thời gian nhập điểm hiện tại: " + e.getMessage());
        }
    }
} 