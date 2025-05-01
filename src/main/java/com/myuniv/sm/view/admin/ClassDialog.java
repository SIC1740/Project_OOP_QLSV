package com.myuniv.sm.view.admin;

import com.myuniv.sm.model.Class;
import com.myuniv.sm.service.ClassService;
import com.myuniv.sm.service.ServiceException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Dialog for adding or editing a class
 */
public class ClassDialog extends JDialog {

    private final ClassService classService;
    private Class classObj;
    private boolean isNewClass;
    private boolean isSaved;
    
    private JTextField txtMaLop;
    private JTextField txtTenLop;
    
    public ClassDialog(Frame owner, Class classObj) {
        super(owner, true);
        this.classService = new ClassService();
        this.classObj = classObj;
        this.isNewClass = (classObj == null);
        this.isSaved = false;
        
        initComponents();
        loadData();
    }
    
    private void initComponents() {
        setTitle(isNewClass ? "Thêm lớp mới" : "Chỉnh sửa lớp");
        setSize(400, 200);
        setLocationRelativeTo(getOwner());
        setResizable(false);
        
        // Main panel
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        
        JLabel lblMaLop = new JLabel("Mã lớp:");
        txtMaLop = new JTextField(20);
        
        JLabel lblTenLop = new JLabel("Tên lớp:");
        txtTenLop = new JTextField(20);
        
        formPanel.add(lblMaLop);
        formPanel.add(txtMaLop);
        formPanel.add(lblTenLop);
        formPanel.add(txtTenLop);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton btnSave = new JButton("Lưu");
        JButton btnCancel = new JButton("Hủy");
        
        btnSave.addActionListener(e -> onSave());
        btnCancel.addActionListener(e -> dispose());
        
        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);
        
        // Add panels to main panel
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(panel);
    }
    
    private void loadData() {
        if (!isNewClass && classObj != null) {
            txtMaLop.setText(classObj.getMaLop());
            txtMaLop.setEditable(false); // Don't allow editing of class ID
            txtTenLop.setText(classObj.getTenLop());
        }
    }
    
    private void onSave() {
        // Validate input
        String maLop = txtMaLop.getText().trim();
        String tenLop = txtTenLop.getText().trim();
        
        if (maLop.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Mã lớp không được để trống",
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtMaLop.requestFocus();
            return;
        }
        
        // Create or update class object
        if (isNewClass) {
            classObj = new Class();
            classObj.setMaLop(maLop);
        }
        
        classObj.setTenLop(tenLop);
        
        // Save to database
        try {
            if (classService.saveClass(classObj)) {
                JOptionPane.showMessageDialog(this,
                    "Lưu lớp thành công",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                isSaved = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Không thể lưu thông tin lớp",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(this,
                "Lỗi: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean isSaved() {
        return isSaved;
    }
    
    public Class getClassObj() {
        return classObj;
    }
} 