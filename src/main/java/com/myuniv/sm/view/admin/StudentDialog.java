package com.myuniv.sm.view.admin;

import com.myuniv.sm.model.Student;
import com.myuniv.sm.service.AcademicClassService;
import com.myuniv.sm.service.ServiceException;
import com.myuniv.sm.service.StudentService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StudentDialog extends JDialog {
    private static final Logger logger = Logger.getLogger(StudentDialog.class.getName());
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    private final JTextField txtMsv;
    private final JTextField txtHoTen;
    private final JTextField txtNgaySinh;
    private final JTextField txtEmail;
    private final JTextField txtSoDienThoai;
    private final JComboBox<String> cmbMaLop;
    private final JButton btnSave;
    private final JButton btnCancel;
    
    private final StudentService studentService;
    private final AcademicClassService classService;
    private final Student student;
    private boolean saved = false;
    
    public StudentDialog(Frame parent, Student student) {
        super(parent, student == null ? "Thêm sinh viên mới" : "Cập nhật sinh viên", true);
        
        this.student = student;
        this.studentService = new StudentService();
        this.classService = new AcademicClassService();
        
        // Create form layout
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Create form fields
        JLabel lblMsv = new JLabel("Mã sinh viên:");
        JLabel lblHoTen = new JLabel("Họ tên:");
        JLabel lblNgaySinh = new JLabel("Ngày sinh (dd/MM/yyyy):");
        JLabel lblEmail = new JLabel("Email:");
        JLabel lblSoDienThoai = new JLabel("Số điện thoại:");
        JLabel lblMaLop = new JLabel("Lớp:");
        
        txtMsv = new JTextField(20);
        txtHoTen = new JTextField(20);
        txtNgaySinh = new JTextField(20);
        txtEmail = new JTextField(20);
        txtSoDienThoai = new JTextField(20);
        cmbMaLop = new JComboBox<>();
        
        // Add fields to form
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(lblMsv, gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(txtMsv, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(lblHoTen, gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(txtHoTen, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(lblNgaySinh, gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(txtNgaySinh, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(lblEmail, gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(txtEmail, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(lblSoDienThoai, gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(txtSoDienThoai, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(lblMaLop, gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(cmbMaLop, gbc);
        
        // Create button panel
        JPanel buttonPanel = new JPanel();
        btnSave = new JButton("Lưu");
        btnCancel = new JButton("Hủy");
        
        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);
        
        // Add panels to dialog
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(formPanel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        
        // Add listeners
        btnSave.addActionListener(this::onSave);
        btnCancel.addActionListener(e -> dispose());
        
        // Set dialog properties
        setSize(400, 350);
        setLocationRelativeTo(parent);
        setResizable(false);
        
        // Load classes for combobox
        loadClasses();
        
        // Fill form if editing
        if (student != null) {
            txtMsv.setText(student.getMsv());
            txtMsv.setEditable(false); // Prevent changing ID
            txtHoTen.setText(student.getHoTen());
            txtNgaySinh.setText(student.getNgaySinh() != null 
                    ? DATE_FORMAT.format(student.getNgaySinh()) : "");
            txtEmail.setText(student.getEmail());
            txtSoDienThoai.setText(student.getSoDienThoai());
            cmbMaLop.setSelectedItem(student.getMaLop());
        }
    }
    
    private void loadClasses() {
        try {
            cmbMaLop.removeAllItems();
            classService.findAll().forEach(academicClass -> 
                cmbMaLop.addItem(academicClass.getMaLop())
            );
        } catch (ServiceException e) {
            logger.log(Level.SEVERE, "Lỗi khi tải danh sách lớp", e);
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi tải danh sách lớp: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void onSave(ActionEvent e) {
        if (!validateForm()) {
            return;
        }
        
        String msv = txtMsv.getText().trim();
        String hoTen = txtHoTen.getText().trim();
        LocalDate ngaySinh = parseDate(txtNgaySinh.getText().trim());
        String email = txtEmail.getText().trim();
        String soDienThoai = txtSoDienThoai.getText().trim();
        String maLop = cmbMaLop.getSelectedItem().toString();
        
        try {
            if (student == null) {
                // Create new student
                Student newStudent = new Student();
                newStudent.setMsv(msv);
                newStudent.setHoTen(hoTen);
                newStudent.setNgaySinh(ngaySinh);
                newStudent.setEmail(email);
                newStudent.setSoDienThoai(soDienThoai);
                newStudent.setMaLop(maLop);
                
                if (studentService.createStudent(newStudent)) {
                    saved = true;
                    JOptionPane.showMessageDialog(this, 
                            "Thêm sinh viên thành công",
                            "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Không thể thêm sinh viên",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // Update existing student
                student.setHoTen(hoTen);
                student.setNgaySinh(ngaySinh);
                student.setEmail(email);
                student.setSoDienThoai(soDienThoai);
                student.setMaLop(maLop);
                
                if (studentService.updateStudent(student)) {
                    saved = true;
                    JOptionPane.showMessageDialog(this, 
                            "Cập nhật sinh viên thành công",
                            "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Không thể cập nhật sinh viên",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (ServiceException ex) {
            logger.log(Level.SEVERE, "Lỗi khi lưu sinh viên", ex);
            JOptionPane.showMessageDialog(this,
                    "Lỗi: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        
        try {
            return LocalDate.parse(dateStr, DATE_FORMAT);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
    
    private boolean validateForm() {
        if (txtMsv.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập mã sinh viên",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtMsv.requestFocus();
            return false;
        }
        
        if (txtHoTen.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập họ tên sinh viên",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtHoTen.requestFocus();
            return false;
        }
        
        String dateStr = txtNgaySinh.getText().trim();
        if (dateStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập ngày sinh",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtNgaySinh.requestFocus();
            return false;
        } else {
            LocalDate date = parseDate(dateStr);
            if (date == null) {
                JOptionPane.showMessageDialog(this,
                        "Ngày sinh không hợp lệ. Sử dụng định dạng dd/MM/yyyy",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                txtNgaySinh.requestFocus();
                return false;
            }
        }
        
        if (txtEmail.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập email",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtEmail.requestFocus();
            return false;
        }
        
        if (cmbMaLop.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn lớp",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            cmbMaLop.requestFocus();
            return false;
        }
        
        // When creating new student, check if ID already exists
        if (student == null) {
            try {
                String msv = txtMsv.getText().trim();
                Student existingStudent = studentService.findByMsv(msv);
                if (existingStudent != null) {
                    JOptionPane.showMessageDialog(this,
                            "Mã sinh viên đã tồn tại",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    txtMsv.requestFocus();
                    return false;
                }
            } catch (Exception ex) {
                logger.log(Level.SEVERE, "Lỗi khi kiểm tra mã sinh viên", ex);
                JOptionPane.showMessageDialog(this,
                        "Lỗi kiểm tra mã sinh viên: " + ex.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        
        return true;
    }
    
    public boolean isSaved() {
        return saved;
    }
} 