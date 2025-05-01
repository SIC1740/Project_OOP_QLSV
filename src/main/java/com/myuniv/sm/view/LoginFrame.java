package com.myuniv.sm.view;

import com.myuniv.sm.model.User;
import com.myuniv.sm.service.AuthenticationService;
import com.myuniv.sm.service.AuthException;
import com.myuniv.sm.dao.impl.UserDaoJdbc;
import com.myuniv.sm.view.admin.AdminFrame;
import com.myuniv.sm.view.teacher.TeacherFrame;
import com.myuniv.sm.view.student.StudentFrame;

import javax.swing.*;
import java.awt.*;

/**
 * LoginFrame – chỉ lo hiển thị và bắt event,
 * phần xác thực được đẩy vào AuthenticationService.
 */
public class LoginFrame extends JFrame {
    private final JTextField    txtUser  = new JTextField(15);
    private final JPasswordField txtPass  = new JPasswordField(15);
    private final JButton       btnLogin = new JButton("Đăng nhập");

    // Chỉ khởi AuthenticationService một lần
    private final AuthenticationService authService =
            new AuthenticationService(new UserDaoJdbc());

    public LoginFrame() {
        super("Đăng nhập hệ thống");
        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 220);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        var gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        gbc.gridx = 0; gbc.gridy = 0; add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; add(txtUser, gbc);

        gbc.gridx = 0; gbc.gridy = 1; add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; add(txtPass, gbc);

        gbc.gridx = 1; gbc.gridy = 2; add(btnLogin, gbc);

        btnLogin.addActionListener(e -> onLogin());
        
        // Set initial focus to username field
        txtUser.requestFocusInWindow();
        
        // Add Enter key support for login
        getRootPane().setDefaultButton(btnLogin);
    }

    private void onLogin() {
        String username = txtUser.getText().trim();
        String password = new String(txtPass.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        try {
            // Gọi service để xác thực và update last_login
            User user = authService.authenticate(username, password);

            // Đóng Login, mở UI tương ứng
            SwingUtilities.invokeLater(() -> {
                dispose();
                switch (user.getRole()) {
                    case "admin"     -> new AdminFrame(user).setVisible(true);
                    case "giangvien" -> new TeacherFrame(user).setVisible(true);
                    case "sinhvien"  -> new StudentFrame(user).setVisible(true);
                    default -> JOptionPane.showMessageDialog(
                            null, "Vai trò không hợp lệ: " + user.getRole());
                }
            });

        } catch (AuthException ex) {
            // Hiển thị lỗi (user không tồn tại hoặc sai mật khẩu)
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Lỗi đăng nhập",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new LoginFrame().setVisible(true)
        );
    }
}
