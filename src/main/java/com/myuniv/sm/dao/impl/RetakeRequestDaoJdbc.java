package com.myuniv.sm.dao.impl;

import com.myuniv.sm.dao.RetakeRequestDao;
import com.myuniv.sm.dao.util.DBConnection;
import com.myuniv.sm.model.RetakeRequest;
import com.myuniv.sm.model.RetakeRegistrationPeriod;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RetakeRequestDaoJdbc implements RetakeRequestDao {
    private static final Logger logger = Logger.getLogger(RetakeRequestDaoJdbc.class.getName());

    // SQL statements for RetakeRequest table
    private static final String SELECT_ALL = 
            "SELECT r.*, sv.ho_ten as ten_sinh_vien " +
            "FROM RetakeRequest r " +
            "LEFT JOIN SinhVien sv ON r.msv = sv.msv";
    private static final String SELECT_BY_ID = 
            "SELECT r.*, sv.ho_ten as ten_sinh_vien " +
            "FROM RetakeRequest r " +
            "LEFT JOIN SinhVien sv ON r.msv = sv.msv " +
            "WHERE r.request_id = ?";
    private static final String SELECT_BY_STUDENT = 
            "SELECT r.*, sv.ho_ten as ten_sinh_vien " +
            "FROM RetakeRequest r " +
            "LEFT JOIN SinhVien sv ON r.msv = sv.msv " +
            "WHERE r.msv = ?";
    private static final String SELECT_BY_CLASS = 
            "SELECT r.*, sv.ho_ten as ten_sinh_vien " +
            "FROM RetakeRequest r " +
            "LEFT JOIN SinhVien sv ON r.msv = sv.msv " +
            "WHERE r.ma_lop = ?";
    private static final String SELECT_BY_STATUS = 
            "SELECT r.*, sv.ho_ten as ten_sinh_vien " +
            "FROM RetakeRequest r " +
            "LEFT JOIN SinhVien sv ON r.msv = sv.msv " +
            "WHERE r.status = ?";
    private static final String INSERT = 
            "INSERT INTO RetakeRequest (msv, ma_lop, ngay_dang_ki, status) VALUES (?, ?, ?, ?)";
    private static final String UPDATE = 
            "UPDATE RetakeRequest SET msv = ?, ma_lop = ?, ngay_dang_ki = ?, status = ? WHERE request_id = ?";
    private static final String DELETE = 
            "DELETE FROM RetakeRequest WHERE request_id = ?";
    
    // SQL statements for registration period (stored in a separate table that needs to be created)
    private static final String CREATE_PERIOD_TABLE = 
            "CREATE TABLE IF NOT EXISTS RetakeRegistrationPeriod (" +
            "id INT AUTO_INCREMENT PRIMARY KEY, " +
            "start_time DATETIME NOT NULL, " +
            "end_time DATETIME NOT NULL, " +
            "is_active BOOLEAN DEFAULT FALSE, " +
            "description VARCHAR(255))";
    private static final String SELECT_CURRENT_PERIOD = 
            "SELECT * FROM RetakeRegistrationPeriod WHERE is_active = TRUE LIMIT 1";
    private static final String INSERT_PERIOD = 
            "INSERT INTO RetakeRegistrationPeriod (start_time, end_time, is_active, description) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_PERIOD = 
            "UPDATE RetakeRegistrationPeriod SET start_time = ?, end_time = ?, is_active = ?, description = ? WHERE id = ?";
    private static final String DEACTIVATE_ALL_PERIODS = 
            "UPDATE RetakeRegistrationPeriod SET is_active = FALSE";

    public RetakeRequestDaoJdbc() {
        // Ensure the period table exists
        createPeriodTableIfNotExists();
    }

    private void createPeriodTableIfNotExists() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(CREATE_PERIOD_TABLE);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error creating retake registration period table", e);
        }
    }

    @Override
    public RetakeRequest findById(int id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractRetakeRequestFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding retake request by ID: " + id, e);
        }
        return null;
    }

    @Override
    public List<RetakeRequest> findAll() {
        List<RetakeRequest> requests = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL)) {
            
            while (rs.next()) {
                RetakeRequest request = extractRetakeRequestFromResultSet(rs);
                requests.add(request);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding all retake requests", e);
        }
        return requests;
    }

    @Override
    public List<RetakeRequest> findByStudent(String msv) {
        List<RetakeRequest> requests = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_STUDENT)) {
            stmt.setString(1, msv);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    RetakeRequest request = extractRetakeRequestFromResultSet(rs);
                    requests.add(request);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding retake requests for student: " + msv, e);
        }
        return requests;
    }

    @Override
    public List<RetakeRequest> findByClass(String maLop) {
        List<RetakeRequest> requests = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_CLASS)) {
            stmt.setString(1, maLop);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    RetakeRequest request = extractRetakeRequestFromResultSet(rs);
                    requests.add(request);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding retake requests for class: " + maLop, e);
        }
        return requests;
    }

    @Override
    public List<RetakeRequest> findByStatus(RetakeRequest.Status status) {
        List<RetakeRequest> requests = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_STATUS)) {
            stmt.setString(1, status.getValue());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    RetakeRequest request = extractRetakeRequestFromResultSet(rs);
                    requests.add(request);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding retake requests by status: " + status, e);
        }
        return requests;
    }

    @Override
    public boolean save(RetakeRequest request) {
        if (request.getRequestId() > 0) {
            return update(request);
        } else {
            return add(request);
        }
    }

    @Override
    public boolean add(RetakeRequest request) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, request.getMsv());
            stmt.setString(2, request.getMaLop());
            stmt.setDate(3, request.getNgayDangKi() != null ? 
                        Date.valueOf(request.getNgayDangKi()) : 
                        Date.valueOf(LocalDate.now()));
            stmt.setString(4, request.getStatus().getValue());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    request.setRequestId(generatedKeys.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error adding retake request", e);
            return false;
        }
    }

    @Override
    public boolean update(RetakeRequest request) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE)) {
            stmt.setString(1, request.getMsv());
            stmt.setString(2, request.getMaLop());
            stmt.setDate(3, request.getNgayDangKi() != null ? 
                        Date.valueOf(request.getNgayDangKi()) : 
                        Date.valueOf(LocalDate.now()));
            stmt.setString(4, request.getStatus().getValue());
            stmt.setInt(5, request.getRequestId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating retake request", e);
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error deleting retake request: " + id, e);
            return false;
        }
    }

    @Override
    public RetakeRegistrationPeriod getCurrentRegistrationPeriod() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_CURRENT_PERIOD);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return extractPeriodFromResultSet(rs);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error getting current retake registration period", e);
        }
        return null;
    }

    @Override
    public boolean saveRegistrationPeriod(RetakeRegistrationPeriod period) {
        try (Connection conn = DBConnection.getConnection()) {
            // If this period is active, deactivate all others first
            if (period.isActive()) {
                try (PreparedStatement deactivateStmt = conn.prepareStatement(DEACTIVATE_ALL_PERIODS)) {
                    deactivateStmt.executeUpdate();
                }
            }
            
            // Now insert the new period
            try (PreparedStatement insertStmt = conn.prepareStatement(INSERT_PERIOD, Statement.RETURN_GENERATED_KEYS)) {
                insertStmt.setTimestamp(1, Timestamp.valueOf(period.getStartTime()));
                insertStmt.setTimestamp(2, Timestamp.valueOf(period.getEndTime()));
                insertStmt.setBoolean(3, period.isActive());
                insertStmt.setString(4, period.getDescription());
                
                int affectedRows = insertStmt.executeUpdate();
                if (affectedRows == 0) {
                    return false;
                }
                
                try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        period.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error saving retake registration period", e);
            return false;
        }
    }

    @Override
    public boolean updateRegistrationPeriod(RetakeRegistrationPeriod period) {
        try (Connection conn = DBConnection.getConnection()) {
            // If this period is active, deactivate all others first
            if (period.isActive()) {
                try (PreparedStatement deactivateStmt = conn.prepareStatement(DEACTIVATE_ALL_PERIODS)) {
                    deactivateStmt.executeUpdate();
                }
            }
            
            // Now update the period
            try (PreparedStatement updateStmt = conn.prepareStatement(UPDATE_PERIOD)) {
                updateStmt.setTimestamp(1, Timestamp.valueOf(period.getStartTime()));
                updateStmt.setTimestamp(2, Timestamp.valueOf(period.getEndTime()));
                updateStmt.setBoolean(3, period.isActive());
                updateStmt.setString(4, period.getDescription());
                updateStmt.setInt(5, period.getId());
                
                return updateStmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating retake registration period", e);
            return false;
        }
    }

    @Override
    public boolean isRegistrationOpen() {
        RetakeRegistrationPeriod period = getCurrentRegistrationPeriod();
        return period != null && period.isRegistrationOpen();
    }
    
    private RetakeRequest extractRetakeRequestFromResultSet(ResultSet rs) throws SQLException {
        RetakeRequest request = new RetakeRequest();
        request.setRequestId(rs.getInt("request_id"));
        request.setMsv(rs.getString("msv"));
        request.setMaLop(rs.getString("ma_lop"));
        
        Date ngayDangKi = rs.getDate("ngay_dang_ki");
        if (ngayDangKi != null) {
            request.setNgayDangKi(ngayDangKi.toLocalDate());
        }
        
        String status = rs.getString("status");
        request.setStatus(RetakeRequest.Status.fromString(status));
        
        // Set transient fields if available
        try {
            request.setTenSinhVien(rs.getString("ten_sinh_vien"));
        } catch (SQLException e) {
            // Ignore if fields don't exist in the result set
        }
        
        return request;
    }
    
    private RetakeRegistrationPeriod extractPeriodFromResultSet(ResultSet rs) throws SQLException {
        RetakeRegistrationPeriod period = new RetakeRegistrationPeriod();
        period.setId(rs.getInt("id"));
        period.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
        period.setEndTime(rs.getTimestamp("end_time").toLocalDateTime());
        period.setActive(rs.getBoolean("is_active"));
        period.setDescription(rs.getString("description"));
        return period;
    }
} 