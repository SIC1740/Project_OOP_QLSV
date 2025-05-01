package com.myuniv.sm.dao.impl;

import com.myuniv.sm.dao.ClassDao;
import com.myuniv.sm.model.Class;
import com.myuniv.sm.dao.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JDBC implementation of ClassDao
 */
public class ClassDaoJdbc implements ClassDao {
    private static final Logger logger = Logger.getLogger(ClassDaoJdbc.class.getName());

    @Override
    public Class findByMaLop(String maLop) {
        String sql = "SELECT lh.ma_lop, lh.ten_lop, " +
                    "(SELECT COUNT(*) FROM SinhVien sv WHERE sv.ma_lop = lh.ma_lop) AS student_count " +
                    "FROM LopHoc lh " +
                    "WHERE lh.ma_lop = ?";
        
        logger.info("Tìm kiếm lớp với mã: " + maLop);
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, maLop);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Class classObj = mapResultSetToClass(rs);
                logger.info("Đã tìm thấy lớp: " + classObj.getMaLop());
                return classObj;
            } else {
                logger.warning("Không tìm thấy lớp với mã: " + maLop);
            }
            rs.close();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi tìm kiếm lớp với mã " + maLop + ": " + e.getMessage(), e);
        }
        
        return null;
    }

    @Override
    public List<Class> findAll() {
        List<Class> classes = new ArrayList<>();
        String sql = "SELECT lh.ma_lop, lh.ten_lop, " +
                    "(SELECT COUNT(*) FROM SinhVien sv WHERE sv.ma_lop = lh.ma_lop) AS student_count " +
                    "FROM LopHoc lh " +
                    "ORDER BY lh.ma_lop";
        
        logger.info("Lấy danh sách tất cả các lớp");
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                classes.add(mapResultSetToClass(rs));
            }
            
            logger.info("Đã tìm thấy " + classes.size() + " lớp");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy danh sách lớp: " + e.getMessage(), e);
        }
        
        return classes;
    }

    @Override
    public boolean save(Class classObj) {
        if (classObj == null) {
            logger.warning("Không thể lưu thông tin lớp null");
            return false;
        }
        
        try {
            if (findByMaLop(classObj.getMaLop()) == null) {
                return insert(classObj);
            } else {
                return update(classObj);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lưu thông tin lớp: " + e.getMessage(), e);
            return false;
        }
    }

    private boolean insert(Class classObj) throws SQLException {
        String sql = "INSERT INTO LopHoc (ma_lop, ten_lop) VALUES (?, ?)";
        
        logger.info("Thêm mới lớp: " + classObj.getMaLop());
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, classObj.getMaLop());
            stmt.setString(2, classObj.getTenLop());
            
            int result = stmt.executeUpdate();
            if (result > 0) {
                logger.info("Đã thêm thành công lớp với mã: " + classObj.getMaLop());
            } else {
                logger.warning("Không thể thêm lớp với mã: " + classObj.getMaLop());
            }
            return result > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi thêm lớp: " + e.getMessage(), e);
            throw e;
        }
    }

    private boolean update(Class classObj) throws SQLException {
        String sql = "UPDATE LopHoc SET ten_lop = ? WHERE ma_lop = ?";
        
        logger.info("Cập nhật thông tin lớp: " + classObj.getMaLop());
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, classObj.getTenLop());
            stmt.setString(2, classObj.getMaLop());
            
            int result = stmt.executeUpdate();
            if (result > 0) {
                logger.info("Đã cập nhật thành công lớp với mã: " + classObj.getMaLop());
            } else {
                logger.warning("Không thể cập nhật lớp với mã: " + classObj.getMaLop());
            }
            return result > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi cập nhật lớp: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public boolean delete(String maLop) {
        String sql = "DELETE FROM LopHoc WHERE ma_lop = ?";
        
        logger.info("Xóa lớp với mã: " + maLop);
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, maLop);
            int result = stmt.executeUpdate();
            if (result > 0) {
                logger.info("Đã xóa thành công lớp với mã: " + maLop);
            } else {
                logger.warning("Không thể xóa lớp với mã: " + maLop);
            }
            return result > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi xóa lớp: " + e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Maps a database result set to a Class object
     */
    private Class mapResultSetToClass(ResultSet rs) throws SQLException {
        Class classObj = new Class();
        
        classObj.setMaLop(rs.getString("ma_lop"));
        
        try {
            classObj.setTenLop(rs.getString("ten_lop"));
        } catch (SQLException e) {
            logger.fine("Không thể đọc trường ten_lop: " + e.getMessage());
            // Ignore if ten_lop column doesn't exist
        }
        
        try {
            classObj.setStudentCount(rs.getInt("student_count"));
        } catch (SQLException e) {
            logger.fine("Không thể đọc trường student_count: " + e.getMessage());
            // Ignore if student_count field doesn't exist
        }
        
        return classObj;
    }
} 