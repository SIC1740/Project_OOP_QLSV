package com.myuniv.sm.view.admin;

import com.myuniv.sm.model.User;
import com.myuniv.sm.service.UserService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserDialog extends JDialog {
    private final JTextField txtUsername;
    private final JPasswordField txtPassword;
    private final JComboBox<String> cmbRole;
    private final JLabel lblPasswordHint;
    private boolean confirmed = false;
    private final User user;
    private final boolean isNewUser;

    public UserDialog(JFrame parent, User user) {
        super(parent, user == null ? "Add New User" : "Edit User", true);
        this.user = user;
        this.isNewUser = (user == null);
        
        // Create components
        JLabel lblUsername = new JLabel("Username:");
        txtUsername = new JTextField(20);
        
        JLabel lblPassword = new JLabel("Password:");
        txtPassword = new JPasswordField(20);
        lblPasswordHint = new JLabel(isNewUser ? "" : "(Leave empty to keep current password)");
        lblPasswordHint.setFont(new Font(lblPasswordHint.getFont().getName(), Font.ITALIC, 10));
        
        JLabel lblRole = new JLabel("Role:");
        cmbRole = new JComboBox<>(new String[]{"admin", "giangvien", "sinhvien"});
        
        JButton btnOK = new JButton("Save");
        JButton btnCancel = new JButton("Cancel");
        
        // Layout
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(lblUsername, gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(txtUsername, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(lblPassword, gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(txtPassword, gbc);
        
        gbc.gridx = 1; gbc.gridy = 2;
        panel.add(lblPasswordHint, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(lblRole, gbc);
        
        gbc.gridx = 1; gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(cmbRole, gbc);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnOK);
        buttonPanel.add(btnCancel);
        
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(buttonPanel, gbc);
        
        getContentPane().add(panel);
        
        // Initialize form with user data if editing
        if (!isNewUser) {
            txtUsername.setText(user.getUsername());
            cmbRole.setSelectedItem(user.getRole());
            lblPasswordHint.setVisible(true);
        } else {
            lblPasswordHint.setVisible(false);
        }
        
        // Add action listeners
        btnOK.addActionListener(e -> {
            if (validateInput()) {
                confirmed = true;
                dispose();
            }
        });
        
        btnCancel.addActionListener(e -> {
            confirmed = false;
            dispose();
        });
        
        // Set dialog properties
        pack();
        setLocationRelativeTo(parent);
        setResizable(false);
    }
    
    private boolean validateInput() {
        if (txtUsername.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                    "Username không được để trống", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (isNewUser && txtPassword.getPassword().length == 0) {
            JOptionPane.showMessageDialog(this, 
                    "Password không được để trống cho người dùng mới", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public String getUsername() {
        return txtUsername.getText().trim();
    }
    
    public String getPassword() {
        return new String(txtPassword.getPassword());
    }
    
    public String getRole() {
        return (String) cmbRole.getSelectedItem();
    }
} 