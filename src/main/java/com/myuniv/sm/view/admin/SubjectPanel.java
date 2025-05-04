package com.myuniv.sm.view.admin;

import com.myuniv.sm.model.Subject;
import com.myuniv.sm.service.SubjectService;
import com.myuniv.sm.service.ServiceException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SubjectPanel extends JPanel {
    private static final Logger logger = Logger.getLogger(SubjectPanel.class.getName());
    
    private final SubjectService subjectService;
    private final DefaultTableModel model;
    private final JTable table;
    private final JButton btnAdd, btnEdit, btnDelete;

    public SubjectPanel() {
        setLayout(new BorderLayout());
        
        // Initialize services
        subjectService = new SubjectService();

        // Create table model
        model = new DefaultTableModel(
                new Object[] {"Mã môn", "Tên môn học", "Số tín chỉ"}, 0
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
        
        btnAdd    = new JButton("Thêm môn học");
        btnEdit   = new JButton("Sửa môn học");
        btnDelete = new JButton("Xóa môn học");
        
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
            List<Subject> subjects = subjectService.findAll();
            
            for (Subject subject : subjects) {
                model.addRow(new Object[] {
                    subject.getMaMon(),
                    subject.getTenMon(),
                    subject.getSoTinChi()
                });
            }
            
            if (subjects.isEmpty()) {
                logger.info("Không có dữ liệu môn học để hiển thị");
            } else {
                logger.info("Đã tải " + subjects.size() + " môn học");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Lỗi khi tải dữ liệu môn học", e);
            JOptionPane.showMessageDialog(this,
                "Lỗi khi tải dữ liệu môn học: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onAdd() {
        SubjectDialog dialog = new SubjectDialog(
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
                "Vui lòng chọn một môn học để sửa",
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            String maMon = model.getValueAt(selectedRow, 0).toString();
            Subject subject = subjectService.getSubjectByMaMon(maMon);
            
            SubjectDialog dialog = new SubjectDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), 
                subject
            );
            dialog.setVisible(true);
            
            if (dialog.isSaved()) {
                loadData();
            }
        } catch (ServiceException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy thông tin môn học", e);
            JOptionPane.showMessageDialog(this,
                "Lỗi: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDelete() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng chọn một môn học để xóa",
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String maMon = model.getValueAt(selectedRow, 0).toString();
        
        // Kiểm tra xem môn học có đang được phân công cho lớp nào không
        try {
            if (subjectService.isSubjectAssignedToClass(maMon)) {
                JOptionPane.showMessageDialog(this,
                    "Không thể xóa môn học đang được phân công cho lớp học.",
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (ServiceException e) {
            logger.log(Level.SEVERE, "Lỗi khi kiểm tra liên kết môn học", e);
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc muốn xóa môn học " + maMon + "?",
            "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (subjectService.deleteSubject(maMon)) {
                    JOptionPane.showMessageDialog(this,
                        "Xóa môn học thành công",
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Không thể xóa môn học",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (ServiceException e) {
                logger.log(Level.SEVERE, "Lỗi khi xóa môn học", e);
                JOptionPane.showMessageDialog(this,
                    "Lỗi: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
} 