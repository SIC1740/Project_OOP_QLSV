package com.myuniv.sm.service;

import com.myuniv.sm.model.Student;
import com.myuniv.sm.dao.StudentDao;
import com.myuniv.sm.dao.impl.StudentDaoJdbc;
import com.myuniv.sm.dao.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service for managing student data
 */
public class StudentService {
    private final StudentDao studentDao;
    private static final Logger logger = Logger.getLogger(StudentService.class.getName());
    
    /**
     * Default constructor uses JDBC implementation of StudentDao
     */
    public StudentService() {
        this.studentDao = new StudentDaoJdbc();
    }
    
    /**
     * Constructor with dependency injection for testing
     * @param studentDao The StudentDao implementation to use
     */
    public StudentService(StudentDao studentDao) {
        this.studentDao = studentDao;
    }
    
    /**
     * Find a student by their student ID (MSV)
     * @param msv The student ID to look up
     * @return The student if found, null otherwise
     */
    public Student findByMsv(String msv) {
        try {
            return studentDao.findByMsv(msv);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding student by MSV: " + msv, e);
            return null;
        }
    }
    
    /**
     * Get a student by their student ID (MSV) - alias for findByMsv for consistency
     * with other service classes
     * @param msv The student ID to look up
     * @return The student if found, null otherwise
     * @throws ServiceException if an error occurs
     */
    public Student getStudentByMsv(String msv) throws ServiceException {
        try {
            Student student = studentDao.findByMsv(msv);
            if (student == null) {
                if (msv.equals("admin") || msv.equals("giangvien")) {
                    throw new ServiceException("Tài khoản này không có quyền truy cập vào giao diện sinh viên");
                }

                logger.log(Level.WARNING, "Không tìm thấy sinh viên " + msv + " trong database!");
                throw new ServiceException("Không tìm thấy sinh viên với mã: " + msv);
            }
            return student;
        } catch (Exception e) {
            if (e instanceof ServiceException) {
                throw (ServiceException) e;
            }
            logger.log(Level.SEVERE, "Error getting student by MSV: " + msv, e);
            throw new ServiceException("Lỗi hệ thống khi tìm kiếm sinh viên: " + e.getMessage());
        }
    }
    
    /**
     * Get a list of all students
     * @return List of all students
     */
    public List<Student> findAll() throws ServiceException {
        List<Student> students = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM SinhVien ORDER BY ho_ten")) {
            
            while (rs.next()) {
                Student student = mapResultSetToStudent(rs);
                students.add(student);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi truy vấn danh sách sinh viên", e);
            throw new ServiceException("Không thể lấy danh sách sinh viên: " + e.getMessage());
        }
        
        return students;
    }
    
    /**
     * Find students by class ID
     * @param maLop The class ID to search for
     * @return List of students in the specified class
     */
    public List<Student> findByClass(String maLop) {
        try {
            return studentDao.findByClass(maLop);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding students by class: " + maLop, e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Save a student (create or update)
     * @param student The student to save
     * @return true if successful, false otherwise
     * @throws ServiceException if an error occurs
     */
    public boolean saveStudent(Student student) throws ServiceException {
        try {
            if (student == null) {
                throw new ServiceException("Không thể lưu thông tin sinh viên null");
            }
            
            if (student.getMsv() == null || student.getMsv().isEmpty()) {
                throw new ServiceException("Mã sinh viên không được để trống");
            }
            
            if (student.getHoTen() == null || student.getHoTen().isEmpty()) {
                throw new ServiceException("Họ tên sinh viên không được để trống");
            }
            
            return studentDao.save(student);
        } catch (Exception e) {
            if (e instanceof ServiceException) {
                throw (ServiceException) e;
            }
            logger.log(Level.SEVERE, "Error saving student: " + student, e);
            throw new ServiceException("Lỗi hệ thống khi lưu sinh viên: " + e.getMessage());
        }
    }
    
    /**
     * Delete a student
     * @param msv The student ID to delete
     * @return true if successful, false otherwise
     * @throws ServiceException if an error occurs
     */
    public boolean deleteStudent(String msv) throws ServiceException {
        try {
            if (msv == null || msv.isEmpty()) {
                throw new ServiceException("Mã sinh viên không được để trống");
            }
            
            return studentDao.delete(msv);
        } catch (Exception e) {
            if (e instanceof ServiceException) {
                throw (ServiceException) e;
            }
            logger.log(Level.SEVERE, "Error deleting student: " + msv, e);
            throw new ServiceException("Lỗi hệ thống khi xóa sinh viên: " + e.getMessage());
        }
    }

    /**
     * Lấy danh sách sinh viên theo lớp
     */
    public List<Student> getStudentsByClass(String maLop) throws ServiceException {
        List<Student> students = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT * FROM SinhVien WHERE ma_lop = ? ORDER BY ho_ten")) {
            
            pstmt.setString(1, maLop);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Student student = mapResultSetToStudent(rs);
                    students.add(student);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi truy vấn sinh viên theo lớp", e);
            throw new ServiceException("Không thể lấy danh sách sinh viên của lớp: " + e.getMessage());
        }
        
        return students;
    }

    /**
     * Tạo sinh viên mới
     */
    public boolean createStudent(Student student) throws ServiceException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO SinhVien (msv, ho_ten, ngay_sinh, email, so_dien_thoai, ma_lop) VALUES (?, ?, ?, ?, ?, ?)")) {
            
            pstmt.setString(1, student.getMsv());
            pstmt.setString(2, student.getHoTen());
            pstmt.setDate(3, java.sql.Date.valueOf(student.getNgaySinh()));
            pstmt.setString(4, student.getEmail());
            pstmt.setString(5, student.getSoDienThoai());
            pstmt.setString(6, student.getMaLop());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi tạo sinh viên mới", e);
            throw new ServiceException("Không thể tạo sinh viên: " + e.getMessage());
        }
    }

    /**
     * Cập nhật thông tin sinh viên
     */
    public boolean updateStudent(Student student) throws ServiceException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "UPDATE SinhVien SET ho_ten = ?, ngay_sinh = ?, email = ?, so_dien_thoai = ?, ma_lop = ? WHERE msv = ?")) {
            
            pstmt.setString(1, student.getHoTen());
            pstmt.setDate(2, java.sql.Date.valueOf(student.getNgaySinh()));
            pstmt.setString(3, student.getEmail());
            pstmt.setString(4, student.getSoDienThoai());
            pstmt.setString(5, student.getMaLop());
            pstmt.setString(6, student.getMsv());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi cập nhật sinh viên", e);
            throw new ServiceException("Không thể cập nhật sinh viên: " + e.getMessage());
        }
    }

    /**
     * Helper method để map ResultSet sang đối tượng Student
     */
    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setStt(rs.getInt("STT"));
        student.setMsv(rs.getString("msv"));
        student.setHoTen(rs.getString("ho_ten"));
        student.setNgaySinh(rs.getDate("ngay_sinh").toLocalDate());
        student.setEmail(rs.getString("email"));
        student.setSoDienThoai(rs.getString("so_dien_thoai"));
        student.setMaLop(rs.getString("ma_lop"));
        return student;
    }

    /**
     * Get the class ID for a student
     * @param msv Student ID
     * @return The class ID or null if not found
     */
    public String getStudentClass(String msv) {
        String sql = "SELECT ma_lop FROM SinhVien WHERE msv = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, msv);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("ma_lop");
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy thông tin lớp học của sinh viên: " + msv, e);
        }
        
        return null;
    }
} 