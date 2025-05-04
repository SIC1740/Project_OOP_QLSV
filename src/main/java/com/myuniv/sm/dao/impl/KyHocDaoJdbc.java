package com.myuniv.sm.dao.impl;

import com.myuniv.sm.dao.KyHocDao;
import com.myuniv.sm.dao.util.DBConnection;
import com.myuniv.sm.model.KyHoc;

import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KyHocDaoJdbc implements KyHocDao {
    private static final Logger logger = Logger.getLogger(KyHocDaoJdbc.class.getName());

    // SQL statements for KyHoc
    private static final String SELECT_ALL =
            "SELECT k.*, m.ten_mon, m.so_tin_chi " +
            "FROM KyHoc k " +
            "JOIN MonHoc m ON k.ma_mon = m.ma_mon";
    private static final String SELECT_BY_ID =
            "SELECT k.*, m.ten_mon, m.so_tin_chi " +
            "FROM KyHoc k " +
            "JOIN MonHoc m ON k.ma_mon = m.ma_mon " +
            "WHERE k.kyhoc_id = ?";
    private static final String SELECT_BY_TERM =
            "SELECT k.*, m.ten_mon, m.so_tin_chi " +
            "FROM KyHoc k " +
            "JOIN MonHoc m ON k.ma_mon = m.ma_mon " +
            "WHERE k.ten_kyhoc = ?";
    private static final String SELECT_ALL_TERM_NAMES =
            "SELECT DISTINCT ten_kyhoc FROM KyHoc ORDER BY ten_kyhoc";
    private static final String INSERT =
            "INSERT INTO KyHoc (ten_kyhoc, ma_mon) VALUES (?, ?)";
    private static final String UPDATE =
            "UPDATE KyHoc SET ten_kyhoc = ?, ma_mon = ? WHERE kyhoc_id = ?";
    private static final String DELETE =
            "DELETE FROM KyHoc WHERE kyhoc_id = ?";

    @Override
    public List<KyHoc> findAll() {
        List<KyHoc> kyHocList = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL)) {
            
            while (rs.next()) {
                KyHoc kyHoc = extractKyHocFromResultSet(rs);
                kyHocList.add(kyHoc);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding all academic terms", e);
        }
        return kyHocList;
    }

    @Override
    public KyHoc findById(int kyhocId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID)) {
            stmt.setInt(1, kyhocId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractKyHocFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding academic term by ID: " + kyhocId, e);
        }
        return null;
    }

    @Override
    public List<KyHoc> findByTerm(String tenKyhoc) {
        List<KyHoc> kyHocList = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_TERM)) {
            stmt.setString(1, tenKyhoc);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    KyHoc kyHoc = extractKyHocFromResultSet(rs);
                    kyHocList.add(kyHoc);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding academic terms by name: " + tenKyhoc, e);
        }
        return kyHocList;
    }

    @Override
    public Set<String> findAllTermNames() {
        Set<String> termNames = new LinkedHashSet<>(); // Use LinkedHashSet to maintain order
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_TERM_NAMES)) {
            
            while (rs.next()) {
                termNames.add(rs.getString("ten_kyhoc"));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding all academic term names", e);
        }
        return termNames;
    }

    @Override
    public boolean save(KyHoc kyHoc) {
        if (kyHoc.getKyhocId() > 0) {
            return update(kyHoc);
        } else {
            return add(kyHoc);
        }
    }

    @Override
    public boolean add(KyHoc kyHoc) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, kyHoc.getTenKyhoc());
            stmt.setString(2, kyHoc.getMaMon());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    kyHoc.setKyhocId(generatedKeys.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error adding academic term", e);
            return false;
        }
    }

    @Override
    public boolean update(KyHoc kyHoc) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE)) {
            stmt.setString(1, kyHoc.getTenKyhoc());
            stmt.setString(2, kyHoc.getMaMon());
            stmt.setInt(3, kyHoc.getKyhocId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating academic term", e);
            return false;
        }
    }

    @Override
    public boolean delete(int kyhocId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE)) {
            stmt.setInt(1, kyhocId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error deleting academic term: " + kyhocId, e);
            return false;
        }
    }
    
    private KyHoc extractKyHocFromResultSet(ResultSet rs) throws SQLException {
        KyHoc kyHoc = new KyHoc();
        kyHoc.setKyhocId(rs.getInt("kyhoc_id"));
        kyHoc.setTenKyhoc(rs.getString("ten_kyhoc"));
        kyHoc.setMaMon(rs.getString("ma_mon"));
        
        // Extract MonHoc information
        kyHoc.setTenMon(rs.getString("ten_mon"));
        kyHoc.setSoTinChi(rs.getInt("so_tin_chi"));
        
        return kyHoc;
    }
} 