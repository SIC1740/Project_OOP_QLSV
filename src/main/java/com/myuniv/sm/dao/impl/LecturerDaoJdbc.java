package com.myuniv.sm.dao.impl;

import com.myuniv.sm.dao.LecturerDao;
import com.myuniv.sm.dao.util.DBConnection;
import com.myuniv.sm.model.Lecturer;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LecturerDaoJdbc implements LecturerDao {
    private static final String SELECT_ALL = "SELECT * FROM GiangVien";
    private static final String SELECT_BY_ID = "SELECT * FROM GiangVien WHERE ma_giangvien = ?";
    private static final String INSERT = "INSERT INTO GiangVien (ma_giangvien, ho_ten, ngay_sinh, email, so_dien_thoai, hoc_vi) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE GiangVien SET ho_ten = ?, ngay_sinh = ?, email = ?, so_dien_thoai = ?, hoc_vi = ? WHERE ma_giangvien = ?";
    private static final String DELETE = "DELETE FROM GiangVien WHERE ma_giangvien = ?";
    private static final String SELECT_BY_DEPARTMENT = "SELECT * FROM GiangVien WHERE hoc_vi = ?";

    @Override
    public List<Lecturer> findAll() {
        List<Lecturer> lecturers = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL)) {
            
            while (rs.next()) {
                Lecturer lecturer = extractLecturerFromResultSet(rs);
                lecturers.add(lecturer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lecturers;
    }

    @Override
    public Lecturer findById(String maGiangVien) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID)) {
            
            ps.setString(1, maGiangVien);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractLecturerFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean add(Lecturer lecturer) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT)) {
            
            ps.setString(1, lecturer.getMaGiangVien());
            ps.setString(2, lecturer.getHoTen());
            ps.setDate(3, Date.valueOf(lecturer.getNgaySinh()));
            ps.setString(4, lecturer.getEmail());
            ps.setString(5, lecturer.getSoDienThoai());
            ps.setString(6, lecturer.getHocVi());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Lecturer lecturer) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE)) {
            
            ps.setString(1, lecturer.getHoTen());
            ps.setDate(2, Date.valueOf(lecturer.getNgaySinh()));
            ps.setString(3, lecturer.getEmail());
            ps.setString(4, lecturer.getSoDienThoai());
            ps.setString(5, lecturer.getHocVi());
            ps.setString(6, lecturer.getMaGiangVien());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(String maGiangVien) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE)) {
            
            ps.setString(1, maGiangVien);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Lecturer> findByDepartment(String department) {
        List<Lecturer> lecturers = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_DEPARTMENT)) {
            
            ps.setString(1, department);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lecturers.add(extractLecturerFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lecturers;
    }
    
    @Override
    public boolean save(Lecturer lecturer) {
        if (findById(lecturer.getMaGiangVien()) == null) {
            return add(lecturer);
        } else {
            return update(lecturer);
        }
    }

    private Lecturer extractLecturerFromResultSet(ResultSet rs) throws SQLException {
        String maGiangVien = rs.getString("ma_giangvien");
        String hoTen = rs.getString("ho_ten");
        LocalDate ngaySinh = rs.getDate("ngay_sinh").toLocalDate();
        String email = rs.getString("email");
        String soDienThoai = rs.getString("so_dien_thoai");
        String hocVi = rs.getString("hoc_vi");
        
        return new Lecturer(maGiangVien, hoTen, ngaySinh, email, soDienThoai, hocVi);
    }
} 