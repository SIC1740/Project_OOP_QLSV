package com.myuniv.sm.dao.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for database connections
 */
public class DBConnection {
    private static final Logger logger = Logger.getLogger(DBConnection.class.getName());
    
    // Database connection parameters - cập nhật phù hợp với cấu hình MySQL của bạn
    private static String dbUrl = "jdbc:mysql://localhost:3306/DB_QLSV";
    private static String dbUser = "root";
    private static String dbPassword = "123456";
    
    static {
        try {
            // Load JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            logger.info("MySQL JDBC Driver loaded successfully");
            
            // Attempt connection when class is loaded to fail fast
            try (Connection testConn = getConnection()) {
                logger.info("Kết nối thành công đến CSDL: " + dbUrl);
            }
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Không tìm thấy MySQL JDBC Driver!", e);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Không thể kết nối đến CSDL: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get a database connection
     * @return A JDBC connection
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi kết nối CSDL: " + e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Configure database connection parameters
     * @param url Database URL
     * @param user Database username
     * @param password Database password
     */
    public static void configure(String url, String user, String password) {
        dbUrl = url;
        dbUser = user;
        dbPassword = password;
        logger.info("Đã cấu hình lại kết nối CSDL: " + url);
    }
    
    /**
     * Test database connection
     * @return true if connection is successful
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Test kết nối CSDL thất bại: " + e.getMessage(), e);
            return false;
        }
    }
    
    // Private constructor to prevent instantiation
    private DBConnection() {}
}

