package com.myuniv.sm.service;

import com.myuniv.sm.model.AcademicClass;
import com.myuniv.sm.model.Subject;
import com.myuniv.sm.dao.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AcademicClassService {
    private static final Logger logger = Logger.getLogger(AcademicClassService.class.getName());
    private final SubjectService subjectService;

    public AcademicClassService() {
        this.subjectService = new SubjectService();
    }

    /**
     * Lấy tất cả lớp học từ DB
     */
    public List<AcademicClass> findAll() throws ServiceException {
        List<AcademicClass> classes = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM LopHoc ORDER BY ma_lop")) {
            
            while (rs.next()) {
                AcademicClass academicClass = new AcademicClass();
                academicClass.setMaLop(rs.getString("ma_lop"));
                academicClass.setSoLuongSinhVien(rs.getInt("so_luong_sinh_vien"));
                
                // Load subjects for this class
                try {
                    List<Subject> subjects = subjectService.getSubjectsByClass(academicClass.getMaLop());
                    academicClass.setSubjects(subjects);
                } catch (ServiceException e) {
                    logger.log(Level.WARNING, "Không thể tải môn học cho lớp " + academicClass.getMaLop(), e);
                }
                
                classes.add(academicClass);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi truy vấn danh sách lớp học", e);
            throw new ServiceException("Không thể lấy danh sách lớp học: " + e.getMessage());
        }
        
        return classes;
    }

    /**
     * Lấy một lớp học theo mã
     */
    public AcademicClass getClassByMaLop(String maLop) throws ServiceException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM LopHoc WHERE ma_lop = ?")) {
            
            pstmt.setString(1, maLop);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    AcademicClass academicClass = new AcademicClass();
                    academicClass.setMaLop(rs.getString("ma_lop"));
                    academicClass.setSoLuongSinhVien(rs.getInt("so_luong_sinh_vien"));
                    
                    // Load subjects for this class
                    try {
                        List<Subject> subjects = subjectService.getSubjectsByClass(academicClass.getMaLop());
                        academicClass.setSubjects(subjects);
                    } catch (ServiceException e) {
                        logger.log(Level.WARNING, "Không thể tải môn học cho lớp " + academicClass.getMaLop(), e);
                    }
                    
                    return academicClass;
                } else {
                    throw new ServiceException("Lớp học với mã " + maLop + " không tồn tại");
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi tìm lớp học theo mã", e);
            throw new ServiceException("Không thể tìm lớp học: " + e.getMessage());
        }
    }

    /**
     * Tạo lớp học mới
     */
    public boolean createClass(AcademicClass academicClass) throws ServiceException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            try (PreparedStatement pstmt = conn.prepareStatement(
                    "INSERT INTO LopHoc (ma_lop, so_luong_sinh_vien) VALUES (?, ?)")) {
                
                pstmt.setString(1, academicClass.getMaLop());
                pstmt.setInt(2, academicClass.getSoLuongSinhVien());
                
                int rowsAffected = pstmt.executeUpdate();
                
                // If class was created successfully and there are subjects to assign
                if (rowsAffected > 0 && academicClass.getSubjects() != null && !academicClass.getSubjects().isEmpty()) {
                    for (Subject subject : academicClass.getSubjects()) {
                        subjectService.assignSubjectToClass(academicClass.getMaLop(), subject.getMaMon());
                    }
                }
                
                conn.commit();
                return rowsAffected > 0;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi tạo lớp học mới", e);
            throw new ServiceException("Không thể tạo lớp học: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    logger.log(Level.WARNING, "Lỗi khi đóng kết nối", e);
                }
            }
        }
    }

    /**
     * Cập nhật thông tin lớp học
     */
    public boolean updateClass(AcademicClass academicClass) throws ServiceException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            try (PreparedStatement pstmt = conn.prepareStatement(
                    "UPDATE LopHoc SET so_luong_sinh_vien = ? WHERE ma_lop = ?")) {
                
                pstmt.setInt(1, academicClass.getSoLuongSinhVien());
                pstmt.setString(2, academicClass.getMaLop());
                
                int rowsAffected = pstmt.executeUpdate();
                
                // Get current subjects for this class
                List<Subject> currentSubjects = subjectService.getSubjectsByClass(academicClass.getMaLop());
                
                // Remove subjects that are in current but not in updated list
                for (Subject subject : currentSubjects) {
                    boolean found = false;
                    for (Subject updatedSubject : academicClass.getSubjects()) {
                        if (subject.getMaMon().equals(updatedSubject.getMaMon())) {
                            found = true;
                            break;
                        }
                    }
                    
                    if (!found) {
                        subjectService.unassignSubjectFromClass(academicClass.getMaLop(), subject.getMaMon());
                    }
                }
                
                // Add subjects that are in updated but not in current list
                for (Subject updatedSubject : academicClass.getSubjects()) {
                    boolean found = false;
                    for (Subject subject : currentSubjects) {
                        if (updatedSubject.getMaMon().equals(subject.getMaMon())) {
                            found = true;
                            break;
                        }
                    }
                    
                    if (!found) {
                        subjectService.assignSubjectToClass(academicClass.getMaLop(), updatedSubject.getMaMon());
                    }
                }
                
                conn.commit();
                return rowsAffected > 0;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi cập nhật lớp học", e);
            throw new ServiceException("Không thể cập nhật lớp học: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    logger.log(Level.WARNING, "Lỗi khi đóng kết nối", e);
                }
            }
        }
    }

    /**
     * Xóa lớp học
     */
    public boolean deleteClass(String maLop) throws ServiceException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM LopHoc WHERE ma_lop = ?")) {
            
            pstmt.setString(1, maLop);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi xóa lớp học", e);
            throw new ServiceException("Không thể xóa lớp học: " + e.getMessage());
        }
    }
} 