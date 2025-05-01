package com.myuniv.sm.view.admin;

import com.myuniv.sm.model.Class;
import com.myuniv.sm.service.ClassService;
import com.myuniv.sm.service.ServiceException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClassPanel extends JPanel {
    private static final Logger logger = Logger.getLogger(ClassPanel.class.getName());
    
    private final ClassService classService;
    private final DefaultTableModel model;
    private final JTable table;
    private final JButton btnAdd, btnEdit, btnDelete;

    public ClassPanel() {
        setLayout(new BorderLayout());
        
        // Initialize services
        classService = new ClassService();

        // Create table model
        model = new DefaultTableModel(
                new Object[] {"Mã lớp", "Tên lớp", "Số sinh viên"}, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        
        // Improve table appearance
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);
        table.getTableHeader().setReorderingAllowed(false);
        
        // Add table to panel
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10),
            BorderFactory.createLineBorder(Color.LIGHT_GRAY)
        ));
        add(scrollPane, BorderLayout.CENTER);

        // Create button panel
        JPanel btnPanel = new JPanel();
        btnPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        
        btnAdd    = new JButton("Thêm lớp");
        btnEdit   = new JButton("Sửa lớp");
        btnDelete = new JButton("Xóa lớp");
        
        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);
        add(btnPanel, BorderLayout.SOUTH);

        // Add action listeners
        btnAdd.addActionListener(e -> onAdd());
        btnEdit.addActionListener(e -> onEdit());
        btnDelete.addActionListener(e -> onDelete());

        // Load data from database
        loadData();
    }

    public void loadData() {
        model.setRowCount(0);
        
        try {
            List<Class> classes = classService.findAll();
            
            for (Class classObj : classes) {
                model.addRow(new Object[] {
                    classObj.getMaLop(),
                    classObj.getTenLop(),
                    classObj.getStudentCount()
                });
            }
            
            if (classes.isEmpty()) {
                logger.info("Không có dữ liệu lớp để hiển thị");
            } else {
                logger.info("Đã tải " + classes.size() + " lớp");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Lỗi khi tải dữ liệu lớp", e);
            JOptionPane.showMessageDialog(this,
                "Lỗi khi tải dữ liệu lớp: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onAdd() {
        ClassDialog dialog = new ClassDialog(
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
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng chọn một lớp để sửa",
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            String maLop = model.getValueAt(selectedRow, 0).toString();
            Class classObj = classService.getClassByMaLop(maLop);
            
            ClassDialog dialog = new ClassDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), 
                classObj
            );
            dialog.setVisible(true);
            
            if (dialog.isSaved()) {
                loadData();
            }
        } catch (ServiceException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy thông tin lớp", e);
            JOptionPane.showMessageDialog(this,
                "Lỗi: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDelete() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng chọn một lớp để xóa",
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String maLop = model.getValueAt(selectedRow, 0).toString();
        int studentCount = Integer.parseInt(model.getValueAt(selectedRow, 2).toString());
        
        // Check if there are students in this class
        if (studentCount > 0) {
            JOptionPane.showMessageDialog(this,
                "Không thể xóa lớp có sinh viên. Vui lòng chuyển sinh viên sang lớp khác trước.",
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc muốn xóa lớp " + maLop + "?",
            "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (classService.deleteClass(maLop)) {
                    JOptionPane.showMessageDialog(this,
                        "Xóa lớp thành công",
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Không thể xóa lớp",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (ServiceException e) {
                logger.log(Level.SEVERE, "Lỗi khi xóa lớp", e);
                JOptionPane.showMessageDialog(this,
                    "Lỗi: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}