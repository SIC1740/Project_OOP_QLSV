package com.myuniv.sm.dao.impl;

import com.myuniv.sm.dao.StudentDao;
import com.myuniv.sm.model.Student;
import com.myuniv.sm.dao.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JDBC implementation of StudentDao
 */
public class StudentDaoJdbc implements StudentDao {
    private static final Logger logger = Logger.getLogger(StudentDaoJdbc.class.getName());

    @Override
    public Student findByMsv(String msv) {
        String sql = "SELECT STT, msv, ho_ten, ngay_sinh, email, so_dien_thoai, ma_lop " +
                    "FROM SinhVien " +
                    "WHERE msv = ?";
        
        logger.info("Tìm kiếm sinh viên với MSV: " + msv);
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, msv);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Student student = mapResultSetToStudent(rs);
                logger.info("Đã tìm thấy sinh viên: " + student.getHoTen());
                return student;
            } else {
                logger.warning("Không tìm thấy sinh viên với MSV: " + msv);
            }
            rs.close();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi tìm kiếm sinh viên với MSV " + msv + ": " + e.getMessage(), e);
        }
        
        return null;
    }

    @Override
    public List<Student> findAll() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT STT, msv, ho_ten, ngay_sinh, email, so_dien_thoai, ma_lop " +
                    "FROM SinhVien " +
                    "ORDER BY ho_ten";
        
        logger.info("Lấy danh sách tất cả sinh viên");
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
            
            logger.info("Đã tìm thấy " + students.size() + " sinh viên");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy danh sách sinh viên: " + e.getMessage(), e);
        }
        
        return students;
    }

    @Override
    public List<Student> findByClass(String maLop) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT STT, msv, ho_ten, ngay_sinh, email, so_dien_thoai, ma_lop " +
                    "FROM SinhVien " +
                    "WHERE ma_lop = ? ORDER BY ho_ten";
        
        logger.info("Tìm kiếm sinh viên theo lớp: " + maLop);
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, maLop);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
            
            logger.info("Đã tìm thấy " + students.size() + " sinh viên của lớp " + maLop);
            rs.close();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi tìm kiếm sinh viên theo lớp " + maLop + ": " + e.getMessage(), e);
        }
        
        return students;
    }

    @Override
    public boolean save(Student student) {
        if (student == null) {
            logger.warning("Không thể lưu thông tin sinh viên null");
            return false;
        }
        
        try {
            if (findByMsv(student.getMsv()) == null) {
                return insert(student);
            } else {
                return update(student);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lưu thông tin sinh viên: " + e.getMessage(), e);
            return false;
        }
    }

    private boolean insert(Student student) throws SQLException {
        String sql = "INSERT INTO SinhVien (msv, ho_ten, ngay_sinh, email, so_dien_thoai, ma_lop) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        logger.info("Thêm mới sinh viên: " + student.getHoTen() + " - MSV: " + student.getMsv());
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, student.getMsv());
            stmt.setString(2, student.getHoTen());
            stmt.setDate(3, java.sql.Date.valueOf(student.getNgaySinh()));
            stmt.setString(4, student.getEmail());
            stmt.setString(5, student.getSoDienThoai());
            stmt.setString(6, student.getMaLop());
            
            int result = stmt.executeUpdate();
            if (result > 0) {
                logger.info("Đã thêm thành công sinh viên với MSV: " + student.getMsv());
            } else {
                logger.warning("Không thể thêm sinh viên với MSV: " + student.getMsv());
            }
            return result > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi thêm sinh viên: " + e.getMessage(), e);
            throw e;
        }
    }

    private boolean update(Student student) throws SQLException {
        String sql = "UPDATE SinhVien SET ho_ten = ?, ngay_sinh = ?, email = ?, " +
                     "so_dien_thoai = ?, ma_lop = ? WHERE msv = ?";
        
        logger.info("Cập nhật thông tin sinh viên: " + student.getMsv());
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, student.getHoTen());
            stmt.setDate(2, java.sql.Date.valueOf(student.getNgaySinh()));
            stmt.setString(3, student.getEmail());
            stmt.setString(4, student.getSoDienThoai());
            stmt.setString(5, student.getMaLop());
            stmt.setString(6, student.getMsv());
            
            int result = stmt.executeUpdate();
            if (result > 0) {
                logger.info("Đã cập nhật thành công sinh viên với MSV: " + student.getMsv());
            } else {
                logger.warning("Không thể cập nhật sinh viên với MSV: " + student.getMsv());
            }
            return result > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi cập nhật sinh viên: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public boolean delete(String msv) {
        String sql = "DELETE FROM SinhVien WHERE msv = ?";
        
        logger.info("Xóa sinh viên với MSV: " + msv);
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, msv);
            int result = stmt.executeUpdate();
            if (result > 0) {
                logger.info("Đã xóa thành công sinh viên với MSV: " + msv);
            } else {
                logger.warning("Không thể xóa sinh viên với MSV: " + msv);
            }
            return result > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi xóa sinh viên: " + e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Maps a database result set to a Student object
     */
    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        Student student = new Student();
        
        // Trường STT
        try {
            student.setSTT(rs.getInt("STT"));
        } catch (SQLException e) {
            logger.fine("Không thể đọc trường STT: " + e.getMessage());
            // Bỏ qua nếu trường không tồn tại
        }
        
        student.setMsv(rs.getString("msv"));
        student.setHoTen(rs.getString("ho_ten"));
        
        // Handle date conversion
        Date dobDate = rs.getDate("ngay_sinh");
        if (dobDate != null) {
            student.setNgaySinh(dobDate.toLocalDate());
        }
        
        student.setEmail(rs.getString("email"));
        student.setSoDienThoai(rs.getString("so_dien_thoai"));
        student.setMaLop(rs.getString("ma_lop"));
        
        return student;
    }
} 