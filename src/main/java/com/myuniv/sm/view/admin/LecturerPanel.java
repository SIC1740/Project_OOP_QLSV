package com.myuniv.sm.view.admin;

import com.myuniv.sm.dao.LecturerDao;
import com.myuniv.sm.dao.impl.LecturerDaoJdbc;
import com.myuniv.sm.model.Lecturer;
import com.myuniv.sm.util.ExcelExporter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class LecturerPanel extends JPanel {
    private final LecturerDao lecturerDao = new LecturerDaoJdbc();
    private final JTable table = new JTable();
    private final DefaultTableModel tableModel = new DefaultTableModel();
    
    private final JTextField txtMaGV = new JTextField(10);
    private final JTextField txtHoTen = new JTextField(20);
    private final JTextField txtNgaySinh = new JTextField(10);
    private final JTextField txtEmail = new JTextField(20);
    private final JTextField txtSoDT = new JTextField(10);
    private final JComboBox<String> cboHocVi = new JComboBox<>(new String[]{"Ths", "TS", "PGS", "GS", "Khác"});
    
    private final JButton btnAdd = new JButton("Thêm");
    private final JButton btnUpdate = new JButton("Cập nhật");
    private final JButton btnDelete = new JButton("Xóa");
    private final JButton btnClear = new JButton("Làm mới");
    private final JButton btnExportExcel = new JButton("Xuất Excel");
    
    public LecturerPanel() {
        setLayout(new BorderLayout());
        
        // Setup table
        setupTable();
        add(new JScrollPane(table), BorderLayout.CENTER);
        
        // Setup form
        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.NORTH);
        
        // Setup button panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Load data
        loadLecturerData();
        
        // Setup event handlers
        setupEventHandlers();
    }
    
    private void setupTable() {
        String[] columns = {"Mã GV", "Họ Tên", "Ngày Sinh", "Email", "Số ĐT", "Học Vị"};
        tableModel.setColumnIdentifiers(columns);
        table.setModel(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Set table selection listener
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    String maGV = table.getValueAt(selectedRow, 0).toString();
                    String hoTen = table.getValueAt(selectedRow, 1).toString();
                    String ngaySinh = table.getValueAt(selectedRow, 2).toString();
                    String email = table.getValueAt(selectedRow, 3).toString();
                    String soDT = table.getValueAt(selectedRow, 4) != null ? 
                                 table.getValueAt(selectedRow, 4).toString() : "";
                    String hocVi = table.getValueAt(selectedRow, 5).toString();
                    
                    txtMaGV.setText(maGV);
                    txtHoTen.setText(hoTen);
                    txtNgaySinh.setText(ngaySinh);
                    txtEmail.setText(email);
                    txtSoDT.setText(soDT);
                    cboHocVi.setSelectedItem(hocVi);
                    
                    txtMaGV.setEditable(false);
                }
            }
        });
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Thông tin Giảng Viên"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // First row
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Mã Giảng Viên:"), gbc);
        
        gbc.gridx = 1;
        panel.add(txtMaGV, gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("Họ Tên:"), gbc);
        
        gbc.gridx = 3;
        panel.add(txtHoTen, gbc);
        
        // Second row
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Ngày Sinh (dd/MM/yyyy):"), gbc);
        
        gbc.gridx = 1;
        panel.add(txtNgaySinh, gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 3;
        panel.add(txtEmail, gbc);
        
        // Third row
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Số Điện Thoại:"), gbc);
        
        gbc.gridx = 1;
        panel.add(txtSoDT, gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("Học Vị:"), gbc);
        
        gbc.gridx = 3;
        panel.add(cboHocVi, gbc);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        panel.add(btnAdd);
        panel.add(btnUpdate);
        panel.add(btnDelete);
        panel.add(btnClear);
        panel.add(btnExportExcel);
        
        return panel;
    }
    
    private void setupEventHandlers() {
        btnAdd.addActionListener(e -> addLecturer());
        btnUpdate.addActionListener(e -> updateLecturer());
        btnDelete.addActionListener(e -> deleteLecturer());
        btnClear.addActionListener(e -> clearForm());
        btnExportExcel.addActionListener(e -> exportToExcel());
    }
    
    private void loadLecturerData() {
        tableModel.setRowCount(0);
        List<Lecturer> lecturers = lecturerDao.findAll();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        for (Lecturer lecturer : lecturers) {
            String ngaySinh = lecturer.getNgaySinh() != null ? 
                             lecturer.getNgaySinh().format(formatter) : "";
            
            tableModel.addRow(new Object[]{
                lecturer.getMaGiangVien(),
                lecturer.getHoTen(),
                ngaySinh,
                lecturer.getEmail(),
                lecturer.getSoDienThoai(),
                lecturer.getHocVi()
            });
        }
    }
    
    private void addLecturer() {
        try {
            // Validate form
            if (!validateForm()) return;
            
            // Parse form data
            Lecturer lecturer = getFormData();
            
            // Save to database
            boolean success = lecturerDao.add(lecturer);
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                    "Thêm giảng viên thành công!", 
                    "Thành công", 
                    JOptionPane.INFORMATION_MESSAGE);
                    
                loadLecturerData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Thêm giảng viên thất bại!", 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi: " + ex.getMessage(), 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void updateLecturer() {
        try {
            int selectedRow = table.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, 
                    "Vui lòng chọn giảng viên cần cập nhật!", 
                    "Lỗi", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Validate form
            if (!validateForm()) return;
            
            // Parse form data
            Lecturer lecturer = getFormData();
            
            // Update in database
            boolean success = lecturerDao.update(lecturer);
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                    "Cập nhật giảng viên thành công!", 
                    "Thành công", 
                    JOptionPane.INFORMATION_MESSAGE);
                    
                loadLecturerData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Cập nhật giảng viên thất bại!", 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi: " + ex.getMessage(), 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void deleteLecturer() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn giảng viên cần xóa!", 
                "Lỗi", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String maGV = table.getValueAt(selectedRow, 0).toString();
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc muốn xóa giảng viên này?",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = lecturerDao.delete(maGV);
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                    "Xóa giảng viên thành công!", 
                    "Thành công", 
                    JOptionPane.INFORMATION_MESSAGE);
                    
                loadLecturerData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Xóa giảng viên thất bại!", 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void clearForm() {
        txtMaGV.setText("");
        txtHoTen.setText("");
        txtNgaySinh.setText("");
        txtEmail.setText("");
        txtSoDT.setText("");
        cboHocVi.setSelectedIndex(0);
        
        txtMaGV.setEditable(true);
        table.clearSelection();
    }
    
    private void exportToExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu file Excel");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String filePath = file.getAbsolutePath();
            if (!filePath.endsWith(".xlsx")) {
                filePath += ".xlsx";
            }
            
            try {
                List<Lecturer> lecturers = lecturerDao.findAll();
                ExcelExporter.exportLecturersToExcel(lecturers, filePath);
                
                JOptionPane.showMessageDialog(this, 
                    "Xuất Excel thành công!\nFile: " + filePath, 
                    "Thành công", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Xuất Excel thất bại: " + ex.getMessage(), 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
    
    private boolean validateForm() {
        if (txtMaGV.getText().trim().isEmpty() ||
            txtHoTen.getText().trim().isEmpty() ||
            txtNgaySinh.getText().trim().isEmpty() ||
            txtEmail.getText().trim().isEmpty()) {
            
            JOptionPane.showMessageDialog(this, 
                "Vui lòng điền đầy đủ thông tin bắt buộc!", 
                "Lỗi", 
                JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        // Validate email format
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        if (!txtEmail.getText().matches(emailPattern)) {
            JOptionPane.showMessageDialog(this, 
                "Email không đúng định dạng!", 
                "Lỗi", 
                JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        // Validate date format
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate.parse(txtNgaySinh.getText().trim(), formatter);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, 
                "Ngày sinh không đúng định dạng (dd/MM/yyyy)!", 
                "Lỗi", 
                JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    private Lecturer getFormData() {
        String maGV = txtMaGV.getText().trim();
        String hoTen = txtHoTen.getText().trim();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate ngaySinh = LocalDate.parse(txtNgaySinh.getText().trim(), formatter);
        
        String email = txtEmail.getText().trim();
        String soDT = txtSoDT.getText().trim();
        String hocVi = cboHocVi.getSelectedItem().toString();
        
        return new Lecturer(maGV, hoTen, ngaySinh, email, soDT, hocVi);
    }
} 