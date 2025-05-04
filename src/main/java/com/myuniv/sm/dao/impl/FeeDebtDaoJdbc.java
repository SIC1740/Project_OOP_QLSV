package com.myuniv.sm.dao.impl;

import com.myuniv.sm.dao.FeeDebtDao;
import com.myuniv.sm.dao.util.DBConnection;
import com.myuniv.sm.model.FeeDebt;

import java.sql.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FeeDebtDaoJdbc implements FeeDebtDao {
    private static final String SELECT_ALL =
            "SELECT fd.*, sv.ho_ten FROM FeeDebt fd " +
            "LEFT JOIN SinhVien sv ON fd.msv = sv.msv " +
            "ORDER BY fd.han_thu DESC";
    private static final String SELECT_BY_STATUS =
            "SELECT fd.*, sv.ho_ten FROM FeeDebt fd " +
            "LEFT JOIN SinhVien sv ON fd.msv = sv.msv " +
            "WHERE fd.status = ? " +
            "ORDER BY fd.han_thu DESC";
    private static final String SELECT_BY_ID =
            "SELECT fd.*, sv.ho_ten FROM FeeDebt fd " +
            "LEFT JOIN SinhVien sv ON fd.msv = sv.msv " +
            "WHERE fd.debt_id = ?";
    private static final String SELECT_BY_STUDENT =
            "SELECT fd.*, sv.ho_ten FROM FeeDebt fd " +
            "LEFT JOIN SinhVien sv ON fd.msv = sv.msv " +
            "WHERE fd.msv = ? " +
            "ORDER BY fd.han_thu DESC";
    private static final String SELECT_UNPAID_BY_STUDENT =
            "SELECT COUNT(*) FROM FeeDebt " +
            "WHERE msv = ? AND status = 'chưa đóng'";
    private static final String INSERT =
            "INSERT INTO FeeDebt (msv, khoan_thu, so_tien, han_thu, status) " +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE =
            "UPDATE FeeDebt SET msv = ?, khoan_thu = ?, so_tien = ?, " +
            "han_thu = ?, status = ? WHERE debt_id = ?";
    private static final String DELETE =
            "DELETE FROM FeeDebt WHERE debt_id = ?";

    @Override
    public List<FeeDebt> findAll() {
        List<FeeDebt> feeDebts = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL)) {
            
            while (rs.next()) {
                FeeDebt feeDebt = extractFromResultSet(rs);
                feeDebt.setStudentName(rs.getString("ho_ten"));
                feeDebts.add(feeDebt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return feeDebts;
    }

    @Override
    public List<FeeDebt> findByStatus(FeeDebt.Status status) {
        return findByStatus(status.getValue());
    }

    @Override
    public List<FeeDebt> findByStatus(String statusStr) {
        List<FeeDebt> feeDebts = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_STATUS)) {
            ps.setString(1, statusStr);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    FeeDebt feeDebt = extractFromResultSet(rs);
                    feeDebt.setStudentName(rs.getString("ho_ten"));
                    feeDebts.add(feeDebt);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return feeDebts;
    }

    @Override
    public FeeDebt findById(int debtId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID)) {
            ps.setInt(1, debtId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    FeeDebt feeDebt = extractFromResultSet(rs);
                    feeDebt.setStudentName(rs.getString("ho_ten"));
                    return feeDebt;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<FeeDebt> findByStudent(String msv) {
        List<FeeDebt> feeDebts = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_STUDENT)) {
            ps.setString(1, msv);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    FeeDebt feeDebt = extractFromResultSet(rs);
                    feeDebt.setStudentName(rs.getString("ho_ten"));
                    feeDebts.add(feeDebt);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return feeDebts;
    }
    
    @Override
    public boolean hasUnpaidFees(String msv) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_UNPAID_BY_STUDENT)) {
            ps.setString(1, msv);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean save(FeeDebt feeDebt) {
        if (feeDebt.getDebtId() > 0) {
            return update(feeDebt);
        } else {
            return add(feeDebt);
        }
    }
    
    @Override
    public boolean add(FeeDebt feeDebt) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, feeDebt.getMsv());
            ps.setString(2, feeDebt.getKhoanThu());
            ps.setBigDecimal(3, feeDebt.getSoTien());
            ps.setDate(4, Date.valueOf(feeDebt.getHanThu()));
            ps.setString(5, feeDebt.getStatus().getValue());
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        feeDebt.setDebtId(rs.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(FeeDebt feeDebt) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE)) {
            ps.setString(1, feeDebt.getMsv());
            ps.setString(2, feeDebt.getKhoanThu());
            ps.setBigDecimal(3, feeDebt.getSoTien());
            ps.setDate(4, Date.valueOf(feeDebt.getHanThu()));
            ps.setString(5, feeDebt.getStatus().getValue());
            ps.setInt(6, feeDebt.getDebtId());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(int debtId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE)) {
            ps.setInt(1, debtId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private FeeDebt extractFromResultSet(ResultSet rs) throws SQLException {
        FeeDebt feeDebt = new FeeDebt();
        feeDebt.setDebtId(rs.getInt("debt_id"));
        feeDebt.setMsv(rs.getString("msv"));
        feeDebt.setKhoanThu(rs.getString("khoan_thu"));
        feeDebt.setSoTien(rs.getBigDecimal("so_tien"));
        feeDebt.setHanThu(rs.getDate("han_thu").toLocalDate());
        feeDebt.setStatus(rs.getString("status"));
        return feeDebt;
    }
} 