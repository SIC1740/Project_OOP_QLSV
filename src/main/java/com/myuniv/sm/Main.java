package com.myuniv.sm;

import com.myuniv.sm.dao.util.DBConnection;
import com.myuniv.sm.util.AppConfig;
import com.myuniv.sm.view.MainFrame;
import com.myuniv.sm.view.LoginFrame;
import com.myuniv.sm.view.admin.AdminFrame;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());
        public static void main(String[] args) {
        // Thiết lập cấu hình logging
        System.setProperty("java.util.logging.SimpleFormatter.format", 
                "%1$tF %1$tT %4$s %2$s - %5$s%6$s%n");
        
        logger.info("Khởi động ứng dụng quản lý sinh viên");
        
        // Kiểm tra kết nối database
        try {
            if (DBConnection.testConnection()) {
                logger.info("Kết nối đến cơ sở dữ liệu thành công");
            } else {
                logger.severe("Không thể kết nối đến cơ sở dữ liệu");
                JOptionPane.showMessageDialog(null, 
                        "Không thể kết nối đến cơ sở dữ liệu.\nVui lòng kiểm tra cấu hình và thử lại sau.", 
                        "Lỗi kết nối", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Lỗi khi kiểm tra kết nối database", e);
            JOptionPane.showMessageDialog(null, 
                    "Lỗi khi kiểm tra kết nối cơ sở dữ liệu: " + e.getMessage(), 
                    "Lỗi kết nối", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        
        // Khởi chạy giao diện đăng nhập
        SwingUtilities.invokeLater(() -> {
            logger.info("Hiển thị màn hình đăng nhập");
            new LoginFrame().setVisible(true);
        });
    }
}
