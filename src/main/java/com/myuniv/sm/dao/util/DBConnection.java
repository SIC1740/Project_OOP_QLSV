package com.myuniv.sm.dao.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DBConnection {

    private static final Properties props = new Properties();

    static {
        try (InputStream in = DBConnection.class
                .getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (in == null) {
                throw new RuntimeException("Không tìm thấy file db.properties trong classpath");
            }
            props.load(in);
            // Đăng ký driver (MySQL 8 trở lên tự động, nhưng gọi cho chắc)
            Class.forName(props.getProperty("db.driver"));
        } catch (Exception e) {
            throw new ExceptionInInitializerError("Lỗi khi load cấu hình DB: " + e.getMessage());
        }
    }

    /**
     * Trả về Connection, ném RuntimeException nếu không connect được
     */
    public static Connection getConnection() {
        try {
            String url      = props.getProperty("db.url");
            String user     = props.getProperty("db.username");
            String password = props.getProperty("db.password");

            Connection conn = DriverManager.getConnection(url, user, password);
            return conn;
        } catch (Exception ex) {
            throw new RuntimeException("Không thể kết nối tới DB: " + ex.getMessage(), ex);
        }
    }

    /**
     * Main test nhanh kết nối
     */
    public static void main(String[] args) {
        System.out.println("=== Kiểm tra kết nối DB ===");
        try (Connection conn = getConnection()) {
            String user = conn.getMetaData().getUserName();
            String url  = conn.getMetaData().getURL();
            System.out.printf("✅ Kết nối thành công!%n - URL  : %s%n - User : %s%n", url, user);
        } catch (Exception e) {
            System.err.println("❌ Kết nối thất bại: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
