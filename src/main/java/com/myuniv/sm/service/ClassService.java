package com.myuniv.sm.service;

import com.myuniv.sm.dao.util.DBConnection;
import com.myuniv.sm.model.Class;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service to handle operations related to classes (LopHoc)
 */
public class ClassService {
    private static final Logger logger = Logger.getLogger(ClassService.class.getName());
    
    /**
     * Save a class (create or update)
     * 
     * @param classObj The class to save
     * @return true if successful, false otherwise
     * @throws ServiceException if an error occurs
     */
    public boolean saveClass(Class classObj) throws ServiceException {
        if (classObj == null) {
            throw new ServiceException("Lớp học không được phép null");
        }
        
        // Check if the class exists
        boolean exists = classExists(classObj.getMaLop());
        
        if (exists) {
            return updateClass(classObj);
        } else {
            return createClass(classObj);
        }
    }
    
    /**
     * Check if a class exists by ID
     * 
     * @param maLop Class ID
     * @return true if exists, false otherwise
     */
    private boolean classExists(String maLop) {
        String sql = "SELECT COUNT(*) FROM LopHoc WHERE ma_lop = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, maLop);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi kiểm tra lớp học tồn tại: " + maLop, e);
        }
        return false;
    }
    
    /**
     * Create a new class
     * 
     * @param classObj The class to create
     * @return true if successful, false otherwise
     * @throws ServiceException if an error occurs
     */
    private boolean createClass(Class classObj) throws ServiceException {
        String sql = "INSERT INTO LopHoc (ma_lop, ten_lop) VALUES (?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, classObj.getMaLop());
            pstmt.setString(2, classObj.getTenLop());
            
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi tạo lớp học mới", e);
            throw new ServiceException("Không thể tạo lớp học: " + e.getMessage());
        }
    }
    
    /**
     * Update an existing class
     * 
     * @param classObj The class to update
     * @return true if successful, false otherwise
     * @throws ServiceException if an error occurs
     */
    private boolean updateClass(Class classObj) throws ServiceException {
        String sql = "UPDATE LopHoc SET ten_lop = ? WHERE ma_lop = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, classObj.getTenLop());
            pstmt.setString(2, classObj.getMaLop());
            
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi cập nhật lớp học", e);
            throw new ServiceException("Không thể cập nhật lớp học: " + e.getMessage());
        }
    }
    
    /**
     * Get a valid class ID for a subject ID
     * When a student tries to register for a retake using a subject ID, 
     * we need to find a corresponding class ID to satisfy the foreign key constraint
     * 
     * @param maMon Subject ID
     * @return A valid class ID or null if none found
     */
    public String getValidClassIdForSubject(String maMon) {
        String sql = "SELECT lh.ma_lop FROM LopHoc lh " +
                     "JOIN LopHoc_MonHoc lhm ON lh.ma_lop = lhm.ma_lop " +
                     "WHERE lhm.ma_mon = ? LIMIT 1";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, maMon);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("ma_lop");
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi tìm lớp cho môn học: " + maMon, e);
        }
        return null;
    }
    
    /**
     * Get all class IDs for a subject ID
     * 
     * @param maMon Subject ID
     * @return List of class IDs
     */
    public List<String> getAllClassIdsForSubject(String maMon) {
        List<String> classIds = new ArrayList<>();
        String sql = "SELECT lh.ma_lop FROM LopHoc lh " +
                     "JOIN LopHoc_MonHoc lhm ON lh.ma_lop = lhm.ma_lop " +
                     "WHERE lhm.ma_mon = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, maMon);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    classIds.add(rs.getString("ma_lop"));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy danh sách lớp cho môn học: " + maMon, e);
        }
        return classIds;
    }
    
    /**
     * Get all classes
     * 
     * @return List of all classes
     * @throws ServiceException if an error occurs
     */
    public List<Class> getAllClasses() throws ServiceException {
        List<Class> classes = new ArrayList<>();
        String sql = "SELECT * FROM LopHoc ORDER BY ma_lop";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Class classObj = new Class();
                classObj.setMaLop(rs.getString("ma_lop"));
                classObj.setTenLop(rs.getString("ten_lop"));
                classes.add(classObj);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy danh sách lớp học", e);
            throw new ServiceException("Không thể lấy danh sách lớp học: " + e.getMessage());
        }
        
        return classes;
    }
    
    /**
     * Get a class by ID
     * 
     * @param maLop Class ID
     * @return The class or null if not found
     * @throws ServiceException if an error occurs
     */
    public Class getClassById(String maLop) throws ServiceException {
        String sql = "SELECT * FROM LopHoc WHERE ma_lop = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, maLop);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Class classObj = new Class();
                    classObj.setMaLop(rs.getString("ma_lop"));
                    classObj.setTenLop(rs.getString("ten_lop"));
                    return classObj;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy thông tin lớp học: " + maLop, e);
            throw new ServiceException("Không thể lấy thông tin lớp học: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Delete a class by ID
     * 
     * @param maLop Class ID
     * @return true if successful, false otherwise
     * @throws ServiceException if an error occurs
     */
    public boolean deleteClass(String maLop) throws ServiceException {
        String sql = "DELETE FROM LopHoc WHERE ma_lop = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, maLop);
            
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi xóa lớp học: " + maLop, e);
            throw new ServiceException("Không thể xóa lớp học: " + e.getMessage());
        }
    }
} 