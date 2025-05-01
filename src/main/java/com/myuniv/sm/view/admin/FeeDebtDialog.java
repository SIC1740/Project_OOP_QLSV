package com.myuniv.sm.view.admin;

import com.myuniv.sm.model.FeeDebt;
import com.myuniv.sm.service.FeeDebtService;
import com.myuniv.sm.service.StudentService;
import com.myuniv.sm.model.Student;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class FeeDebtDialog extends JDialog {
    private final JTextField txtMsv;
    private final JTextField txtKhoanThu;
    private final JTextField txtSoTien;
    private final JTextField txtHanThu;
    private final JComboBox<String> cmbStatus;
    private final JButton btnFindStudent;
    private final JLabel lblStudentName;
    
    private boolean confirmed = false;
    private final FeeDebt feeDebt;
    private final boolean isNewDebt;
    private Student selectedStudent;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final StudentService studentService;

    public FeeDebtDialog(JFrame parent, FeeDebt feeDebt) {
        super(parent, feeDebt == null ? "Thêm Khoản Thu Mới" : "Sửa Khoản Thu", true);
        this.feeDebt = feeDebt;
        this.isNewDebt = (feeDebt == null);
        this.studentService = new StudentService();
        
        // Create components
        JLabel lblMsv = new JLabel("Mã sinh viên:");
        txtMsv = new JTextField(15);
        btnFindStudent = new JButton("Tìm");
        
        JLabel lblStudentNameLabel = new JLabel("Họ tên sinh viên:");
        lblStudentName = new JLabel("");
        lblStudentName.setFont(new Font(lblStudentName.getFont().getName(), Font.BOLD, 12));
        
        JLabel lblKhoanThu = new JLabel("Khoản thu:");
        txtKhoanThu = new JTextField(20);
        
        JLabel lblSoTien = new JLabel("Số tiền:");
        txtSoTien = new JTextField(15);
        
        JLabel lblHanThu = new JLabel("Hạn thu (dd/MM/yyyy):");
        txtHanThu = new JTextField(10);
        // Set default value to today
        txtHanThu.setText(LocalDate.now().format(DATE_FORMATTER));
        
        JLabel lblStatus = new JLabel("Trạng thái:");
        cmbStatus = new JComboBox<>(new String[]{"chưa đóng", "đã đóng"});
        
        JButton btnOK = new JButton("Lưu");
        JButton btnCancel = new JButton("Hủy");
        
        // Layout
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // First row - MSV and find button
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(lblMsv, gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(txtMsv, gbc);
        
        gbc.gridx = 2; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(btnFindStudent, gbc);
        
        // Second row - Student name
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(lblStudentNameLabel, gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.gridwidth = 2;
        panel.add(lblStudentName, gbc);
        gbc.gridwidth = 1;
        
        // Third row - Khoan thu
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(lblKhoanThu, gbc);
        
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(txtKhoanThu, gbc);
        gbc.gridwidth = 1;
        
        // Fourth row - So tien
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(lblSoTien, gbc);
        
        gbc.gridx = 1; gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(txtSoTien, gbc);
        
        // Fifth row - Han thu
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(lblHanThu, gbc);
        
        gbc.gridx = 1; gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(txtHanThu, gbc);
        
        // Sixth row - Status
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(lblStatus, gbc);
        
        gbc.gridx = 1; gbc.gridy = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(cmbStatus, gbc);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnOK);
        buttonPanel.add(btnCancel);
        
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(buttonPanel, gbc);
        
        getContentPane().add(panel);
        
        // Initialize form with fee debt data if editing
        if (!isNewDebt) {
            txtMsv.setText(feeDebt.getMsv());
            txtMsv.setEditable(false); // Don't allow changing MSV in edit mode
            lblStudentName.setText(feeDebt.getStudentName());
            txtKhoanThu.setText(feeDebt.getKhoanThu());
            txtSoTien.setText(feeDebt.getSoTien().toString());
            txtHanThu.setText(feeDebt.getHanThu().format(DATE_FORMATTER));
            cmbStatus.setSelectedItem(feeDebt.getStatus());
            
            // Disable find button in edit mode
            btnFindStudent.setEnabled(false);
            
            // Store student info for later use
            selectedStudent = new Student();
            selectedStudent.setMsv(feeDebt.getMsv());
            selectedStudent.setHoTen(feeDebt.getStudentName());
        }
        
        // Add action listeners
        btnFindStudent.addActionListener(e -> findStudent());
        
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
    
    private void findStudent() {
        String msv = txtMsv.getText().trim();
        if (msv.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                    "Vui lòng nhập mã sinh viên", 
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Find the student using StudentService
        selectedStudent = studentService.findByMsv(msv);
        
        if (selectedStudent != null) {
            lblStudentName.setText(selectedStudent.getHoTen());
        } else {
            lblStudentName.setText("");
            JOptionPane.showMessageDialog(this, 
                    "Không tìm thấy sinh viên với mã " + msv, 
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private boolean validateInput() {
        // Validate MSV
        if (txtMsv.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                    "Mã sinh viên không được để trống", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Validate student found
        if (lblStudentName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                    "Vui lòng tìm kiếm sinh viên hợp lệ", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Validate khoan thu
        if (txtKhoanThu.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                    "Khoản thu không được để trống", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Validate so tien
        try {
            BigDecimal amount = new BigDecimal(txtSoTien.getText().trim());
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(this, 
                        "Số tiền phải lớn hơn 0", 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                    "Số tiền không hợp lệ", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Validate han thu
        try {
            LocalDate hanThu = LocalDate.parse(txtHanThu.getText().trim(), DATE_FORMATTER);
            // Optional: Add additional validation if needed, e.g. not in the past
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, 
                    "Ngày hạn thu không hợp lệ (dd/MM/yyyy)", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public String getMsv() {
        return txtMsv.getText().trim();
    }
    
    public String getKhoanThu() {
        return txtKhoanThu.getText().trim();
    }
    
    public BigDecimal getSoTien() {
        return new BigDecimal(txtSoTien.getText().trim());
    }
    
    public LocalDate getHanThu() {
        return LocalDate.parse(txtHanThu.getText().trim(), DATE_FORMATTER);
    }
    
    public String getStatus() {
        return (String) cmbStatus.getSelectedItem();
    }
} 