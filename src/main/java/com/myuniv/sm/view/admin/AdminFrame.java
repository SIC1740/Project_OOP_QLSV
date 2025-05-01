package com.myuniv.sm.view.admin;

import com.myuniv.sm.model.User;
import com.myuniv.sm.view.LoginFrame;

import javax.swing.*;
import java.awt.*;

public class AdminFrame extends JFrame {
    private JPanel panel1;                    // Được map từ .form
    private JTabbedPane tabbedPane1;          // Được map từ .form
    private JButton logoutButton;             // Nút đăng xuất
    private JLabel welcomeLabel;              // Hiển thị tên người dùng
    private final User currentUser;           // Lưu thông tin người dùng hiện tại

    public AdminFrame(User user) {
        this.currentUser = user;
        
        // Tạo panel chính nếu chưa có
        if (panel1 == null) {
            panel1 = new JPanel(new BorderLayout());
        }
        
        // Tạo header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        // Hiển thị thông tin người dùng
        welcomeLabel = new JLabel("Xin chào, Admin " + user.getUsername());
        welcomeLabel.setFont(new Font(welcomeLabel.getFont().getName(), Font.BOLD, 14));
        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        
        // Thêm nút đăng xuất
        logoutButton = new JButton("Đăng xuất");
        logoutButton.addActionListener(e -> logout());
        headerPanel.add(logoutButton, BorderLayout.EAST);
        
        // Thêm header vào panel chính
        panel1.add(headerPanel, BorderLayout.NORTH);
        
        // Tạo tabbed pane nếu chưa có
        if (tabbedPane1 == null) {
            tabbedPane1 = new JTabbedPane();
            panel1.add(tabbedPane1, BorderLayout.CENTER);
        }
        
        setContentPane(panel1);               // Kết nối với panel trong .form
        setTitle("Admin Dashboard");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        // Thêm các Panel vào từng tab (phần logic)
        tabbedPane1.addTab("User Accounts", new UserAccountPanel());
        tabbedPane1.addTab("Giảng Viên", new LecturerPanel());
        tabbedPane1.addTab("Sinh Viên", new StudentPanel());
        tabbedPane1.addTab("Classes", new ClassPanel());
        tabbedPane1.addTab("Thống kê học phí", new FeeDebtPanel());
    }
    
    /**
     * Xử lý đăng xuất
     */
    private void logout() {
        int choice = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc muốn đăng xuất?",
            "Xác nhận đăng xuất",
            JOptionPane.YES_NO_OPTION
        );
        
        if (choice == JOptionPane.YES_OPTION) {
            dispose(); // Đóng AdminFrame
            SwingUtilities.invokeLater(() -> 
                new LoginFrame().setVisible(true)
            );
        }
    }

    public static void main(String[] args) {
        // For testing only - in production, should always pass a real User
        User testUser = new User();
        testUser.setUsername("testadmin");
        testUser.setRole("admin");
        
        SwingUtilities.invokeLater(() -> new AdminFrame(testUser).setVisible(true));
    }

    private void createUIComponents() {
        // Nếu có panel nào muốn custom thì tạo ở đây (IntelliJ tự gọi hàm này)
        // Ví dụ: userAccountPanel = new UserAccountPanel();
    }
}

