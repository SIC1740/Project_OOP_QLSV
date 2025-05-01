package com.myuniv.sm.view.admin;

import com.myuniv.sm.model.Student;
import com.myuniv.sm.service.StudentService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StudentPanel extends JPanel {
    private static final Logger logger = Logger.getLogger(StudentPanel.class.getName());
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    private final DefaultTableModel model;
    private final JTable table;
    private final JButton btnAdd, btnEdit, btnDelete;
    private final StudentService studentService;

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
        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);
        add(btnPanel, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> onAdd());
        btnEdit.addActionListener(e -> onEdit());
        btnDelete.addActionListener(e -> onDelete());

        loadData();
    }

    private void loadData() {
        model.setRowCount(0);
        try {
            List<Student> students = studentService.findAll();
            for (Student student : students) {
                model.addRow(new Object[]{
                    student.getMsv(),
                    student.getHoTen(),
                    student.getNgaySinh() != null ? DATE_FORMAT.format(student.getNgaySinh()) : "",
                    student.getEmail(),
                    student.getSoDienThoai(),
                    student.getTenLop() != null ? student.getTenLop() : student.getMaLop()
                });
            }
            logger.info("Đã tải " + students.size() + " sinh viên vào bảng");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Lỗi khi tải dữ liệu sinh viên", e);
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi tải dữ liệu: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onAdd() { 
        // TODO: Implement add student functionality
        JOptionPane.showMessageDialog(this, "Chức năng đang được phát triển");
    }
    
    private void onEdit() { 
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sinh viên để sửa", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String msv = (String) model.getValueAt(selectedRow, 0);
        // TODO: Implement edit student functionality 
        JOptionPane.showMessageDialog(this, "Chức năng đang được phát triển");
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
}
