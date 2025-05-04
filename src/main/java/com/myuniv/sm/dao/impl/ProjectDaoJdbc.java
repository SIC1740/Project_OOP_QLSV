package com.myuniv.sm.dao.impl;

import com.myuniv.sm.dao.ProjectDao;
import com.myuniv.sm.dao.util.DBConnection;
import com.myuniv.sm.model.Project;
import com.myuniv.sm.model.ProjectRegistrationPeriod;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProjectDaoJdbc implements ProjectDao {
    private static final Logger logger = Logger.getLogger(ProjectDaoJdbc.class.getName());
    
    // SQL statements for DoAn table
    private static final String SELECT_ALL = "SELECT d.*, sv.ho_ten as ten_sinh_vien, gv.ho_ten as ten_giang_vien " +
                                             "FROM DoAn d " +
                                             "LEFT JOIN SinhVien sv ON d.msv = sv.msv " +
                                             "LEFT JOIN GiangVien gv ON d.ma_giangvien = gv.ma_giangvien";
    private static final String SELECT_BY_ID = "SELECT d.*, sv.ho_ten as ten_sinh_vien, gv.ho_ten as ten_giang_vien " +
                                              "FROM DoAn d " +
                                              "LEFT JOIN SinhVien sv ON d.msv = sv.msv " +
                                              "LEFT JOIN GiangVien gv ON d.ma_giangvien = gv.ma_giangvien " +
                                              "WHERE d.doan_id = ?";
    private static final String SELECT_BY_STUDENT = "SELECT d.*, sv.ho_ten as ten_sinh_vien, gv.ho_ten as ten_giang_vien " +
                                                  "FROM DoAn d " +
                                                  "LEFT JOIN SinhVien sv ON d.msv = sv.msv " +
                                                  "LEFT JOIN GiangVien gv ON d.ma_giangvien = gv.ma_giangvien " +
                                                  "WHERE d.msv = ?";
    private static final String SELECT_BY_LECTURER = "SELECT d.*, sv.ho_ten as ten_sinh_vien, gv.ho_ten as ten_giang_vien " +
                                                   "FROM DoAn d " +
                                                   "LEFT JOIN SinhVien sv ON d.msv = sv.msv " +
                                                   "LEFT JOIN GiangVien gv ON d.ma_giangvien = gv.ma_giangvien " +
                                                   "WHERE d.ma_giangvien = ?";
    private static final String INSERT = "INSERT INTO DoAn (msv, ten_de_tai, ma_giangvien, ngay_dang_ky) VALUES (?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE DoAn SET msv = ?, ten_de_tai = ?, ma_giangvien = ?, ngay_dang_ky = ? WHERE doan_id = ?";
    private static final String DELETE = "DELETE FROM DoAn WHERE doan_id = ?";
    
    // SQL statements for registration period (stored in a separate table that needs to be created)
    private static final String CREATE_PERIOD_TABLE = "CREATE TABLE IF NOT EXISTS ProjectRegistrationPeriod (" +
                                                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                                                    "start_time DATETIME NOT NULL, " +
                                                    "end_time DATETIME NOT NULL, " +
                                                    "is_active BOOLEAN DEFAULT FALSE, " +
                                                    "description VARCHAR(255))";
    private static final String SELECT_CURRENT_PERIOD = "SELECT * FROM ProjectRegistrationPeriod WHERE is_active = TRUE LIMIT 1";
    private static final String INSERT_PERIOD = "INSERT INTO ProjectRegistrationPeriod (start_time, end_time, is_active, description) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_PERIOD = "UPDATE ProjectRegistrationPeriod SET start_time = ?, end_time = ?, is_active = ?, description = ? WHERE id = ?";
    private static final String DEACTIVATE_ALL_PERIODS = "UPDATE ProjectRegistrationPeriod SET is_active = FALSE";
    
    public ProjectDaoJdbc() {
        // Ensure the period table exists
        createPeriodTableIfNotExists();
    }
    
    private void createPeriodTableIfNotExists() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(CREATE_PERIOD_TABLE);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error creating registration period table", e);
        }
    }

    @Override
    public Project findById(int id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractProjectFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding project by ID: " + id, e);
        }
        return null;
    }

    @Override
    public List<Project> findAll() {
        List<Project> projects = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL)) {
            
            while (rs.next()) {
                Project project = extractProjectFromResultSet(rs);
                projects.add(project);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding all projects", e);
        }
        return projects;
    }

    @Override
    public List<Project> findByStudent(String msv) {
        List<Project> projects = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_STUDENT)) {
            stmt.setString(1, msv);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Project project = extractProjectFromResultSet(rs);
                    projects.add(project);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding projects for student: " + msv, e);
        }
        return projects;
    }

    @Override
    public List<Project> findByLecturer(String maGiangvien) {
        List<Project> projects = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_LECTURER)) {
            stmt.setString(1, maGiangvien);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Project project = extractProjectFromResultSet(rs);
                    projects.add(project);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding projects for lecturer: " + maGiangvien, e);
        }
        return projects;
    }

    @Override
    public boolean save(Project project) {
        if (project.getDoanId() > 0) {
            return update(project);
        } else {
            return add(project);
        }
    }

    @Override
    public boolean add(Project project) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, project.getMsv());
            stmt.setString(2, project.getTenDeTai());
            stmt.setString(3, project.getMaGiangvien());
            stmt.setDate(4, project.getNgayDangKy() != null ? 
                        Date.valueOf(project.getNgayDangKy()) : 
                        Date.valueOf(LocalDate.now()));
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    project.setDoanId(generatedKeys.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error adding project", e);
            return false;
        }
    }

    @Override
    public boolean update(Project project) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE)) {
            stmt.setString(1, project.getMsv());
            stmt.setString(2, project.getTenDeTai());
            stmt.setString(3, project.getMaGiangvien());
            stmt.setDate(4, project.getNgayDangKy() != null ? 
                        Date.valueOf(project.getNgayDangKy()) : 
                        Date.valueOf(LocalDate.now()));
            stmt.setInt(5, project.getDoanId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating project", e);
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
            logger.log(Level.SEVERE, "Error deleting project: " + id, e);
            return false;
        }
    }

    @Override
    public ProjectRegistrationPeriod getCurrentRegistrationPeriod() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_CURRENT_PERIOD);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return extractPeriodFromResultSet(rs);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error getting current registration period", e);
        }
        return null;
    }

    @Override
    public boolean saveRegistrationPeriod(ProjectRegistrationPeriod period) {
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
            logger.log(Level.SEVERE, "Error saving registration period", e);
            return false;
        }
    }

    @Override
    public boolean updateRegistrationPeriod(ProjectRegistrationPeriod period) {
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
            logger.log(Level.SEVERE, "Error updating registration period", e);
            return false;
        }
    }

    @Override
    public boolean isRegistrationOpen() {
        ProjectRegistrationPeriod period = getCurrentRegistrationPeriod();
        return period != null && period.isRegistrationOpen();
    }
    
    private Project extractProjectFromResultSet(ResultSet rs) throws SQLException {
        Project project = new Project();
        project.setDoanId(rs.getInt("doan_id"));
        project.setMsv(rs.getString("msv"));
        project.setTenDeTai(rs.getString("ten_de_tai"));
        project.setMaGiangvien(rs.getString("ma_giangvien"));
        
        Date ngayDangKy = rs.getDate("ngay_dang_ky");
        if (ngayDangKy != null) {
            project.setNgayDangKy(ngayDangKy.toLocalDate());
        }
        
        // Set transient fields if available
        try {
            project.setTenSinhVien(rs.getString("ten_sinh_vien"));
            project.setTenGiangVien(rs.getString("ten_giang_vien"));
        } catch (SQLException e) {
            // Ignore if fields don't exist in the result set
        }
        
        return project;
    }
    
    private ProjectRegistrationPeriod extractPeriodFromResultSet(ResultSet rs) throws SQLException {
        ProjectRegistrationPeriod period = new ProjectRegistrationPeriod();
        period.setId(rs.getInt("id"));
        period.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
        period.setEndTime(rs.getTimestamp("end_time").toLocalDateTime());
        period.setActive(rs.getBoolean("is_active"));
        period.setDescription(rs.getString("description"));
        return period;
    }
} 