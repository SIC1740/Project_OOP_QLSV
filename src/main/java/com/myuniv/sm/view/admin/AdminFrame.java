// File: src/main/java/com/myuniv/sm/view/admin/AdminFrame.java
package com.myuniv.sm.view.admin;

import com.myuniv.sm.model.User;
import com.myuniv.sm.view.LoginFrame;
import javax.swing.*;
import java.awt.*;

public class AdminFrame extends JFrame {
    private JPanel panel1;
    private JTabbedPane tabbedPane1;
    private JButton logoutButton;
    private JLabel welcomeLabel;
    private final User currentUser;

    public AdminFrame(User user) {
        this.currentUser = user;
        if (panel1 == null) {
            panel1 = new JPanel(new BorderLayout());
        }
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        welcomeLabel = new JLabel("Xin chào, Admin " + user.getUsername());
        welcomeLabel.setFont(new Font(welcomeLabel.getFont().getName(), Font.BOLD, 14));
        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        logoutButton = new JButton("Đăng xuất");
        logoutButton.addActionListener(e -> logout());
        headerPanel.add(logoutButton, BorderLayout.EAST);
        panel1.add(headerPanel, BorderLayout.NORTH);

        if (tabbedPane1 == null) {
            tabbedPane1 = new JTabbedPane();
            panel1.add(tabbedPane1, BorderLayout.CENTER);
        }
        setContentPane(panel1);
        setTitle("Admin Dashboard");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        // Các use-case
        tabbedPane1.addTab("User Accounts", new UserAccountPanel());
        tabbedPane1.addTab("Giảng Viên", new LecturerPanel());
        tabbedPane1.addTab("Sinh Viên", new StudentPanel());
        tabbedPane1.addTab("Điểm TB", new ScorePanel());
        tabbedPane1.addTab("Thống kê Khá/Giỏi", new StatisticsPanel());
        tabbedPane1.addTab("Classes", new ClassPanel());
        tabbedPane1.addTab("Thống kê học phí", new FeeDebtPanel());
    }

    private void logout() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn đăng xuất?",
                "Xác nhận đăng xuất",
                JOptionPane.YES_NO_OPTION
        );
        if (choice == JOptionPane.YES_OPTION) {
            dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        }
    }
}
