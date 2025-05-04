package com.myuniv.sm.service;

import com.myuniv.sm.dao.util.DBConnection;
import com.myuniv.sm.model.Grade;
import com.myuniv.sm.model.Student;
import com.myuniv.sm.model.StudentGPASummary;
import com.myuniv.sm.model.Subject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GradeService {
    private static final Logger logger = Logger.getLogger(GradeService.class.getName());
    private final StudentService studentService;
    private final SubjectService subjectService;

    public GradeService() {
        this.studentService = new StudentService();
        this.subjectService = new SubjectService();
    }

    /**
     * Get all grades for a specific subject and class
     */
    public List<Grade> getGradesBySubjectAndClass(String maMon, String maLop) throws ServiceException {
        List<Grade> grades = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT d.*, s.ho_ten " +
                     "FROM DiemMon d " +
                     "JOIN SinhVien s ON d.msv = s.msv " +
                     "WHERE d.ma_mon = ? AND d.ma_lop = ? " +
                     "ORDER BY s.ho_ten")) {
            
            pstmt.setString(1, maMon);
            pstmt.setString(2, maLop);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Grade grade = mapResultSetToGrade(rs);
                    grade.setTenSinhVien(rs.getString("ho_ten"));
                    grades.add(grade);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi truy vấn điểm sinh viên", e);
            throw new ServiceException("Không thể lấy danh sách điểm: " + e.getMessage());
        }
        
        return grades;
    }

    /**
     * Get grade by student ID, subject, and class
     */
    public Grade getGradeByStudentSubjectAndClass(String msv, String maMon, String maLop) throws ServiceException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT d.*, s.ho_ten " +
                     "FROM DiemMon d " +
                     "JOIN SinhVien s ON d.msv = s.msv " +
                     "WHERE d.msv = ? AND d.ma_mon = ? AND d.ma_lop = ?")) {
            
            pstmt.setString(1, msv);
            pstmt.setString(2, maMon);
            pstmt.setString(3, maLop);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Grade grade = mapResultSetToGrade(rs);
                    grade.setTenSinhVien(rs.getString("ho_ten"));
                    return grade;
                } else {
                    // No grade found for this student
                    return null;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi truy vấn điểm sinh viên", e);
            throw new ServiceException("Không thể lấy điểm: " + e.getMessage());
        }
    }
    
    /**
     * Get list of subjects that have grades
     */
    public List<String> getSubjectsWithGrades() throws ServiceException {
        List<String> subjects = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT DISTINCT m.ma_mon, m.ten_mon " +
                     "FROM DiemMon d " +
                     "JOIN MonHoc m ON d.ma_mon = m.ma_mon " +
                     "ORDER BY m.ten_mon")) {
            
            while (rs.next()) {
                subjects.add(rs.getString("ma_mon"));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi truy vấn danh sách môn học có điểm", e);
            throw new ServiceException("Không thể lấy danh sách môn học: " + e.getMessage());
        }
        
        return subjects;
    }
    
    /**
     * Get list of classes for a subject that have grades
     */
    public List<String> getClassesForSubjectWithGrades(String maMon) throws ServiceException {
        List<String> classes = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT DISTINCT ma_lop " +
                     "FROM DiemMon " +
                     "WHERE ma_mon = ? " +
                     "ORDER BY ma_lop")) {
            
            pstmt.setString(1, maMon);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    classes.add(rs.getString("ma_lop"));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi truy vấn danh sách lớp học có điểm", e);
            throw new ServiceException("Không thể lấy danh sách lớp học: " + e.getMessage());
        }
        
        return classes;
    }

    /**
     * Save (create or update) a grade
     */
    public boolean saveGrade(Grade grade) throws ServiceException {
        // Check if this grade already exists
        Grade existingGrade = getGradeByStudentSubjectAndClass(
                grade.getMsv(), grade.getMaMon(), grade.getMaLop());
        
        if (existingGrade != null) {
            // Update existing grade
            return updateGrade(grade);
        } else {
            // Create new grade
            return createGrade(grade);
        }
    }
    
    /**
     * Create a new grade
     */
    public boolean createGrade(Grade grade) throws ServiceException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO DiemMon (msv, ma_lop, ma_mon, diem_cc, diem_qtrinh, diem_thi) " +
                     "VALUES (?, ?, ?, ?, ?, ?)")) {
            
            pstmt.setString(1, grade.getMsv());
            pstmt.setString(2, grade.getMaLop());
            pstmt.setString(3, grade.getMaMon());
            pstmt.setBigDecimal(4, grade.getDiemCC());
            pstmt.setBigDecimal(5, grade.getDiemQTrinh());
            pstmt.setBigDecimal(6, grade.getDiemThi());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi tạo điểm mới", e);
            throw new ServiceException("Không thể tạo điểm: " + e.getMessage());
        }
    }
    
    /**
     * Update an existing grade
     */
    public boolean updateGrade(Grade grade) throws ServiceException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "UPDATE DiemMon " +
                     "SET diem_cc = ?, diem_qtrinh = ?, diem_thi = ? " +
                     "WHERE msv = ? AND ma_lop = ? AND ma_mon = ?")) {
            
            pstmt.setBigDecimal(1, grade.getDiemCC());
            pstmt.setBigDecimal(2, grade.getDiemQTrinh());
            pstmt.setBigDecimal(3, grade.getDiemThi());
            pstmt.setString(4, grade.getMsv());
            pstmt.setString(5, grade.getMaLop());
            pstmt.setString(6, grade.getMaMon());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi cập nhật điểm", e);
            throw new ServiceException("Không thể cập nhật điểm: " + e.getMessage());
        }
    }
    
    /**
     * Delete a grade
     */
    public boolean deleteGrade(int idDiemMon) throws ServiceException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "DELETE FROM DiemMon WHERE id_diemmon = ?")) {
            
            pstmt.setInt(1, idDiemMon);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi xóa điểm", e);
            throw new ServiceException("Không thể xóa điểm: " + e.getMessage());
        }
    }
    
    /**
     * Import grades for a class and subject
     */
    public int importGradesForClassAndSubject(String maLop, String maMon, List<Grade> grades) throws ServiceException {
        Connection conn = null;
        int successCount = 0;
        
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            PreparedStatement pstmt = conn.prepareStatement(
                    "INSERT INTO DiemMon (msv, ma_lop, ma_mon, diem_cc, diem_qtrinh, diem_thi) " +
                    "VALUES (?, ?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE diem_cc = VALUES(diem_cc), " +
                    "diem_qtrinh = VALUES(diem_qtrinh), diem_thi = VALUES(diem_thi)");
            
            for (Grade grade : grades) {
                pstmt.setString(1, grade.getMsv());
                pstmt.setString(2, maLop);
                pstmt.setString(3, maMon);
                pstmt.setBigDecimal(4, grade.getDiemCC());
                pstmt.setBigDecimal(5, grade.getDiemQTrinh());
                pstmt.setBigDecimal(6, grade.getDiemThi());
                
                pstmt.addBatch();
                successCount++;
            }
            
            pstmt.executeBatch();
            conn.commit();
            
            return successCount;
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, "Lỗi khi rollback transaction", ex);
            }
            
            logger.log(Level.SEVERE, "Lỗi khi import điểm", e);
            throw new ServiceException("Không thể import điểm: " + e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Lỗi khi đóng kết nối", e);
            }
        }
    }
    
    /**
     * Calculate average GPA for all students across all subjects weighted by credit hours
     * Formula: (diem_he_4 * so_tin_chi) / total credits
     */
    public Map<String, StudentGPASummary> calculateStudentGPASummary() throws ServiceException {
        Map<String, StudentGPASummary> summaryMap = new HashMap<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT d.msv, s.ho_ten, d.ma_mon, d.diem_he4, m.so_tin_chi " +
                     "FROM DiemMon d " +
                     "JOIN SinhVien s ON d.msv = s.msv " +
                     "JOIN MonHoc m ON d.ma_mon = m.ma_mon " +
                     "WHERE d.diem_he4 IS NOT NULL AND d.diem_he4 > 0 " +
                     "ORDER BY s.ho_ten")) {
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String msv = rs.getString("msv");
                    String hoTen = rs.getString("ho_ten");
                    BigDecimal diemHe4 = rs.getBigDecimal("diem_he4");
                    int soTinChi = rs.getInt("so_tin_chi");
                    
                    // Skip if grade or credit hours are null or zero
                    if (diemHe4 == null || diemHe4.compareTo(BigDecimal.ZERO) <= 0 || soTinChi <= 0) {
                        continue;
                    }
                    
                    // Get or create student summary
                    StudentGPASummary summary = summaryMap.computeIfAbsent(
                            msv, k -> new StudentGPASummary(msv, hoTen));
                    
                    // Calculate weighted GPA components
                    BigDecimal weightedGPA = diemHe4.multiply(BigDecimal.valueOf(soTinChi));
                    
                    // Update running totals
                    int currentTotalCredits = summary.getTotalCredits();
                    BigDecimal currentTotalWeightedGPA = summary.getAverageGPA()
                            .multiply(BigDecimal.valueOf(currentTotalCredits));
                    
                    int newTotalCredits = currentTotalCredits + soTinChi;
                    BigDecimal newTotalWeightedGPA = currentTotalWeightedGPA.add(weightedGPA);
                    
                    // Update summary
                    summary.setTotalCredits(newTotalCredits);
                    
                    if (newTotalCredits > 0) {
                        BigDecimal newAverageGPA = newTotalWeightedGPA
                                .divide(BigDecimal.valueOf(newTotalCredits), 2, RoundingMode.HALF_UP);
                        summary.setAverageGPA(newAverageGPA);
                    }
                }
            }
            
            return summaryMap;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi tính điểm trung bình sinh viên", e);
            throw new ServiceException("Không thể tính điểm trung bình: " + e.getMessage());
        }
    }
    
    /**
     * Helper method to map ResultSet to Grade object
     */
    private Grade mapResultSetToGrade(ResultSet rs) throws SQLException {
        Grade grade = new Grade();
        grade.setIdDiemMon(rs.getInt("id_diemmon"));
        grade.setMsv(rs.getString("msv"));
        grade.setMaLop(rs.getString("ma_lop"));
        grade.setMaMon(rs.getString("ma_mon"));
        grade.setDiemCC(rs.getBigDecimal("diem_cc"));
        grade.setDiemQTrinh(rs.getBigDecimal("diem_qtrinh"));
        grade.setDiemThi(rs.getBigDecimal("diem_thi"));
        grade.setDiemTK(rs.getBigDecimal("diem_tk"));
        grade.setDiemHe4(rs.getBigDecimal("diem_he4"));
        grade.setXepLoai(rs.getString("xep_loai"));
        grade.setDanhGia(rs.getString("danh_gia"));
        
        return grade;
    }
} 