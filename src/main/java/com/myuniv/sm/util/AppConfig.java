package com.myuniv.sm.util;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

public class AppConfig {
    private static final Properties props = new Properties();

    static {
        try (InputStream in = AppConfig.class
                .getClassLoader()
                .getResourceAsStream("app.properties")) {
            if (in == null) {
                throw new RuntimeException("Không tìm thấy app.properties");
            }
            props.load(in);
        } catch (Exception e) {
            throw new ExceptionInInitializerError("Lỗi load app.properties: " + e.getMessage());
        }
    }

    /** Trả về giá trị key trong app.properties */
    public static String get(String key) {
        return props.getProperty(key);
    }

    /** Khởi tạo Look‑And‑Feel theo cấu hình */
    public static void initLookAndFeel() {
        String laf = props.getProperty("app.laf", "system").toLowerCase();
        try {
            switch (laf) {
                case "flat-dark"  -> UIManager.setLookAndFeel(new FlatDarkLaf());
                case "flat-light" -> UIManager.setLookAndFeel(new FlatLightLaf());
                case "system"     -> UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                default           -> UIManager.setLookAndFeel(laf);
            }
        } catch (Exception e) {
            System.err.println("⚠️ Không thể set LAF '" + laf + "', dùng mặc định");
        }
    }

    /** Khởi tạo Locale theo cấu hình */
    public static void initLocale() {
        String loc = props.getProperty("app.locale", "en_US");
        String[] parts = loc.split("_");
        if (parts.length == 2) {
            Locale.setDefault(new Locale(parts[0], parts[1]));
        } else {
            Locale.setDefault(new Locale(parts[0]));
        }
    }
}
