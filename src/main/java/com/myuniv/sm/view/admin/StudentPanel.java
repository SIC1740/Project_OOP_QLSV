package com.myuniv.sm.view.admin;

import com.myuniv.sm.model.Student;
import com.myuniv.sm.service.StudentService;
import com.myuniv.sm.util.ExcelExporter;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StudentPanel extends JPanel {
    private static final Logger logger = Logger.getLogger(StudentPanel.class.getName());
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    private final DefaultTableModel model;
    private final JTable table;
    private final JButton btnAdd, btnEdit, btnDelete, btnExport;
    private final StudentService studentService;
    private List<Student> currentStudents; // Store the current list of students

    public StudentPanel() {
        setLayout(new BorderLayout());
        
        // Initialize service
        studentService = new StudentService();

        model = new DefaultTableModel(
                new Object[] {"MSV", "Họ Tên", "Ngày Sinh", "Email", "Số Điện Thoại", "Lớp"}, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        btnAdd    = new JButton("Thêm");
        btnEdit   = new JButton("Sửa");
        btnDelete = new JButton("Xóa");
        btnExport = new JButton("Xuất Excel");
        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);
        btnPanel.add(btnExport);
        add(btnPanel, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> onAdd());
        btnEdit.addActionListener(e -> onEdit());
        btnDelete.addActionListener(e -> onDelete());
        btnExport.addActionListener(e -> onExport());

        loadData();
    }

    private void loadData() {
        model.setRowCount(0);
        try {
            currentStudents = studentService.findAll();
            for (Student student : currentStudents) {
                model.addRow(new Object[]{
                    student.getMsv(),
                    student.getHoTen(),
                    student.getNgaySinh() != null ? DATE_FORMAT.format(student.getNgaySinh()) : "",
                    student.getEmail(),
                    student.getSoDienThoai(),
                    student.getTenLop() != null ? student.getTenLop() : student.getMaLop()
                });
            }
            logger.info("Đã tải " + currentStudents.size() + " sinh viên vào bảng");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Lỗi khi tải dữ liệu sinh viên", e);
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi tải dữ liệu: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onAdd() { 
        StudentDialog dialog = new StudentDialog(
            (Frame) SwingUtilities.getWindowAncestor(this), 
            null
        );
        dialog.setVisible(true);
        
        if (dialog.isSaved()) {
            loadData();
        }
    }
    
    private void onEdit() { 
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sinh viên để sửa", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String msv = (String) model.getValueAt(selectedRow, 0);
        try {
            Student student = studentService.findByMsv(msv);
            if (student == null) {
                JOptionPane.showMessageDialog(this, 
                    "Không tìm thấy thông tin của sinh viên " + msv, 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            StudentDialog dialog = new StudentDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), 
                student
            );
            dialog.setVisible(true);
            
            if (dialog.isSaved()) {
                loadData();
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy thông tin sinh viên", e);
            JOptionPane.showMessageDialog(this,
                "Lỗi: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void onDelete() { 
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sinh viên để xóa", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String msv = (String) model.getValueAt(selectedRow, 0);
        String name = (String) model.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc chắn muốn xóa sinh viên " + name + " (" + msv + ")?", 
            "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = studentService.deleteStudent(msv);
                if (success) {
                    model.removeRow(selectedRow);
                    JOptionPane.showMessageDialog(this, "Đã xóa sinh viên thành công");
                    logger.info("Đã xóa sinh viên " + msv);
                    loadData(); // Reload to ensure data consistency
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể xóa sinh viên", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Lỗi khi xóa sinh viên " + msv, e);
                JOptionPane.showMessageDialog(this, 
                    "Lỗi khi xóa sinh viên: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void onExport() {
        if (currentStudents == null || currentStudents.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Không có dữ liệu sinh viên để xuất",
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Create a file chooser
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn vị trí lưu file");
        
        // Set default file name with current datetime
        String defaultFileName = "DanhSachSinhVien_" + 
                                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + 
                                ".xlsx";
        fileChooser.setSelectedFile(new File(defaultFileName));
        
        // Show save dialog
        int userSelection = fileChooser.showSaveDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            
            // Ensure the file has .xlsx extension
            String filePath = fileToSave.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".xlsx")) {
                filePath += ".xlsx";
            }
            
            try {
                ExcelExporter.exportStudentsToExcel(currentStudents, filePath);
                JOptionPane.showMessageDialog(this, 
                    "File đã được xuất thành công tại: " + filePath,
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
                
                // Ask if the user wants to open the file
                int openFile = JOptionPane.showConfirmDialog(this,
                    "Bạn có muốn mở file này không?",
                    "Mở file", JOptionPane.YES_NO_OPTION);
                    
                if (openFile == JOptionPane.YES_OPTION) {
                    try {
                        Desktop.getDesktop().open(new File(filePath));
                    } catch (IOException e) {
                        logger.log(Level.WARNING, "Không thể mở file Excel", e);
                        JOptionPane.showMessageDialog(this,
                            "Không thể mở file Excel. Vui lòng mở thủ công.",
                            "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Lỗi khi xuất dữ liệu sinh viên ra Excel", e);
                JOptionPane.showMessageDialog(this, 
                    "Lỗi khi xuất file: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
