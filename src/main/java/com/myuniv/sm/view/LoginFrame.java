package com.myuniv.sm.view;

import com.myuniv.sm.dao.UserDao;
import com.myuniv.sm.dao.impl.UserDaoJdbc;
import com.myuniv.sm.model.User;
import com.myuniv.sm.util.PasswordUtils;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private final JTextField    txtUser = new JTextField(15);
    private final JPasswordField txtPass = new JPasswordField(15);
    private final JButton       btnLogin = new JButton("Đăng nhập");

    private final UserDao userDao = new UserDaoJdbc();

    public LoginFrame() {
        super("Login");
        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(350, 200);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        var gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);

        gbc.gridx = 0; gbc.gridy = 0; add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; add(txtUser, gbc);

        gbc.gridx = 0; gbc.gridy = 1; add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; add(txtPass, gbc);

        gbc.gridx = 1; gbc.gridy = 2; add(btnLogin, gbc);

        btnLogin.addActionListener(e -> authenticate());
    }

    private void authenticate() {
        String username = txtUser.getText().trim();
        String password = new String(txtPass.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nhập đầy đủ thông tin!");
            return;
        }

        User user = userDao.findByUsername(username);
        if (user == null) {
            JOptionPane.showMessageDialog(this, "User không tồn tại");
            return;
        }

        String hashInput = PasswordUtils.hash(password);
        if (!hashInput.equals(user.getPasswordHash())) {
            JOptionPane.showMessageDialog(this, "Sai mật khẩu");
            return;
        }

        // Cập nhật last_login
        userDao.updateLastLogin(user.getUserId());

        // Tùy role, mở cửa sổ khác
        JOptionPane.showMessageDialog(this,
                "Đăng nhập thành công với vai trò: " + user.getRole());
        // Ví dụ:
        // if ("admin".equals(user.getRole())) openAdminUI();
        // else if ("giangvien".equals(user.getRole())) openTeacherUI();
        // else openStudentUI();

        dispose(); // đóng login
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
