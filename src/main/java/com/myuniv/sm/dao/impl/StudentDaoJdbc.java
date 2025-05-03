package com.myuniv.sm.dao.impl;

import com.myuniv.sm.dao.StudentDao;
import com.myuniv.sm.model.Student;
import com.myuniv.sm.dao.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JDBC implementation of StudentDao
 */
public class StudentDaoJdbc implements StudentDao {
    private static final Logger logger = Logger.getLogger(StudentDaoJdbc.class.getName());

    // 1. Tìm theo MSV
    private static final String SELECT_BY_MSV =
            "SELECT STT, msv, ho_ten, ngay_sinh, email, so_dien_thoai, ma_lop FROM SinhVien WHERE msv = ?";

    @Override
    public Student findByMsv(String msv) {
        logger.info("Tìm sinh viên theo MSV: " + msv);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_MSV)) {
            ps.setString(1, msv);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi findByMsv: " + e.getMessage(), e);
        }
        return null;
    }

    // 2. Tất cả sinh viên
    private static final String SELECT_ALL =
            "SELECT STT, msv, ho_ten, ngay_sinh, email, so_dien_thoai, ma_lop FROM SinhVien ORDER BY ho_ten";

    @Override
    public List<Student> findAll() {
        List<Student> list = new ArrayList<>();
        logger.info("Lấy danh sách tất cả sinh viên");
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(SELECT_ALL)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi findAll: " + e.getMessage(), e);
        }
        return list;
    }

    // 3. Tìm theo lớp
    private static final String SELECT_BY_CLASS =
            "SELECT STT, msv, ho_ten, ngay_sinh, email, so_dien_thoai, ma_lop FROM SinhVien WHERE ma_lop = ? ORDER BY ho_ten";

    @Override
    public List<Student> findByClass(String maLop) {
        List<Student> list = new ArrayList<>();
        logger.info("Tìm sinh viên theo lớp: " + maLop);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_CLASS)) {
            ps.setString(1, maLop);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi findByClass: " + e.getMessage(), e);
        }
        return list;
    }

    // 4. Tìm theo STT
    private static final String SELECT_BY_ID =
            "SELECT STT, msv, ho_ten, ngay_sinh, email, so_dien_thoai, ma_lop FROM SinhVien WHERE STT = ?";

    public Student findById(int stt) {
        logger.info("Tìm sinh viên theo STT: " + stt);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID)) {
            ps.setInt(1, stt);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi findById: " + e.getMessage(), e);
        }
        return null;
    }

    // 5. Thêm mới
    private static final String INSERT_SQL =
            "INSERT INTO SinhVien (msv, ho_ten, ngay_sinh, email, so_dien_thoai, ma_lop) VALUES (?, ?, ?, ?, ?, ?)";

    @Override
    public boolean save(Student s) {
        if (findByMsv(s.getMsv()) == null) {
            // Nếu chưa tồn tại → insert
            return create(s);
        } else {
            // Nếu đã tồn tại → update
            return update(s);
        }
    }

    public boolean create(Student s) {
        logger.info("Thêm sinh viên: " + s.getMsv());
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, s.getMsv());
            ps.setString(2, s.getHoTen());
            ps.setDate(3, Date.valueOf(s.getNgaySinh()));
            ps.setString(4, s.getEmail());
            ps.setString(5, s.getSoDienThoai());
            ps.setString(6, s.getMaLop());
            int r = ps.executeUpdate();
            if (r > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) s.setSTT(keys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi create: " + e.getMessage(), e);
        }
        return false;
    }

    // 6. Cập nhật
    public boolean update(Student s) {
        String sql = "UPDATE SinhVien SET ho_ten=?, ngay_sinh=?, email=?, so_dien_thoai=?, ma_lop=? WHERE msv=?";
        logger.info("Cập nhật sinh viên: " + s.getMsv());
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getHoTen());
            ps.setDate(2, Date.valueOf(s.getNgaySinh()));
            ps.setString(3, s.getEmail());
            ps.setString(4, s.getSoDienThoai());
            ps.setString(5, s.getMaLop());
            ps.setString(6, s.getMsv());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi update: " + e.getMessage(), e);
        }
        return false;
    }

    // 7. Xóa theo MSV
    @Override
    public boolean delete(String msv) {
        logger.info("Xóa sinh viên (theo MSV): " + msv);
        Student s = findByMsv(msv);
        if (s == null) return false;
        return delete(s.getSTT());
    }

    // 8. Xóa theo STT
    public boolean delete(int stt) {
        String sql = "DELETE FROM SinhVien WHERE STT = ?";
        logger.info("Xóa sinh viên (theo STT): " + stt);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, stt);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi delete(int): " + e.getMessage(), e);
        }
        return false;
    }

    // Hàm chung ánh xạ ResultSet
    private Student mapRow(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setSTT(rs.getInt("STT"));
        s.setMsv(rs.getString("msv"));
        s.setHoTen(rs.getString("ho_ten"));
        Date d = rs.getDate("ngay_sinh");
        if (d != null) s.setNgaySinh(d.toLocalDate());
        s.setEmail(rs.getString("email"));
        s.setSoDienThoai(rs.getString("so_dien_thoai"));
        s.setMaLop(rs.getString("ma_lop"));
        return s;
    }
}
