package com.myuniv.sm.view.admin;

import com.myuniv.sm.model.AcademicClass;
import com.myuniv.sm.model.Subject;
import com.myuniv.sm.service.AcademicClassService;
import com.myuniv.sm.service.ServiceException;
import com.myuniv.sm.service.SubjectService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AcademicClassDialog extends JDialog {
    private static final Logger logger = Logger.getLogger(AcademicClassDialog.class.getName());
    
    private final JTextField txtMaLop;
    private final JSpinner spnSoLuongSinhVien;
    private final JTable subjectTable;
    private final DefaultTableModel subjectTableModel;
    private final JButton btnAddSubject, btnRemoveSubject;
    private final JButton btnSave, btnCancel;
    
    private final AcademicClassService academicClassService;
    private final SubjectService subjectService;
    private final AcademicClass academicClass;
    private boolean saved = false;
    
    public AcademicClassDialog(Frame parent, AcademicClass academicClass) {
        super(parent, academicClass == null ? "Thêm lớp học mới" : "Cập nhật lớp học", true);
        
        this.academicClass = academicClass;
        this.academicClassService = new AcademicClassService();
        this.subjectService = new SubjectService();
        
        // Create form layout
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Create form fields
        JLabel lblMaLop = new JLabel("Mã lớp:");
        JLabel lblSoLuongSinhVien = new JLabel("Số lượng sinh viên:");
        
        txtMaLop = new JTextField(20);
        spnSoLuongSinhVien = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
        
        // Add fields to form
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(lblMaLop, gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(txtMaLop, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(lblSoLuongSinhVien, gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(spnSoLuongSinhVien, gbc);
        
        // Create subject table
        subjectTableModel = new DefaultTableModel(
                new Object[] {"Mã môn", "Tên môn học", "Số tín chỉ"}, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        subjectTable = new JTable(subjectTableModel);
        JScrollPane scrollPane = new JScrollPane(subjectTable);
        scrollPane.setPreferredSize(new Dimension(380, 150));
        
        // Create subject buttons
        JPanel subjectButtonPanel = new JPanel();
        btnAddSubject = new JButton("Thêm môn học");
        btnRemoveSubject = new JButton("Xóa môn học");
        subjectButtonPanel.add(btnAddSubject);
        subjectButtonPanel.add(btnRemoveSubject);
        
        // Add subject panel to form
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        JLabel lblMonHoc = new JLabel("Danh sách môn học:");
        formPanel.add(lblMonHoc, gbc);
        
        gbc.gridy = 3; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 1.0;
        formPanel.add(scrollPane, gbc);
        
        gbc.gridy = 4; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0; gbc.weighty = 0;
        formPanel.add(subjectButtonPanel, gbc);
        
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
        btnAddSubject.addActionListener(this::onAddSubject);
        btnRemoveSubject.addActionListener(this::onRemoveSubject);
        
        // Set dialog properties
        setSize(450, 450);
        setLocationRelativeTo(parent);
        setResizable(false);
        
        // Fill form if editing
        if (academicClass != null) {
            txtMaLop.setText(academicClass.getMaLop());
            txtMaLop.setEditable(false); // Prevent changing ID
            spnSoLuongSinhVien.setValue(academicClass.getSoLuongSinhVien());
            
            // Load existing subjects
            for (Subject subject : academicClass.getSubjects()) {
                addSubjectToTable(subject);
            }
        }
    }
    
    private void onSave(ActionEvent e) {
        if (!validateForm()) {
            return;
        }
        
        String maLop = txtMaLop.getText().trim();
        int soLuongSinhVien = (Integer) spnSoLuongSinhVien.getValue();
        
        try {
            if (academicClass == null) {
                // Create new class
                AcademicClass newClass = new AcademicClass();
                newClass.setMaLop(maLop);
                newClass.setSoLuongSinhVien(soLuongSinhVien);
                
                // Add selected subjects
                List<Subject> selectedSubjects = getSelectedSubjects();
                newClass.setSubjects(selectedSubjects);
                
                if (academicClassService.createClass(newClass)) {
                    saved = true;
                    JOptionPane.showMessageDialog(this, 
                            "Thêm lớp học thành công",
                            "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Không thể thêm lớp học",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // Update existing class
                academicClass.setSoLuongSinhVien(soLuongSinhVien);
                
                // Update selected subjects
                List<Subject> selectedSubjects = getSelectedSubjects();
                academicClass.setSubjects(selectedSubjects);
                
                if (academicClassService.updateClass(academicClass)) {
                    saved = true;
                    JOptionPane.showMessageDialog(this, 
                            "Cập nhật lớp học thành công",
                            "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Không thể cập nhật lớp học",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (ServiceException ex) {
            logger.log(Level.SEVERE, "Lỗi khi lưu lớp học", ex);
            JOptionPane.showMessageDialog(this,
                    "Lỗi: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void onAddSubject(ActionEvent e) {
        try {
            // Get all subjects
            List<Subject> allSubjects = subjectService.findAll();
            
            // Remove already added subjects
            List<Subject> availableSubjects = new ArrayList<>(allSubjects);
            for (int i = 0; i < subjectTableModel.getRowCount(); i++) {
                String maMon = (String) subjectTableModel.getValueAt(i, 0);
                availableSubjects.removeIf(subject -> subject.getMaMon().equals(maMon));
            }
            
            if (availableSubjects.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Tất cả môn học đã được thêm vào lớp",
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // Create a dialog to select subject
            Subject selectedSubject = (Subject) JOptionPane.showInputDialog(
                    this,
                    "Chọn môn học:",
                    "Thêm môn học vào lớp",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    availableSubjects.toArray(),
                    availableSubjects.get(0));
            
            if (selectedSubject != null) {
                addSubjectToTable(selectedSubject);
            }
        } catch (ServiceException ex) {
            logger.log(Level.SEVERE, "Lỗi khi lấy danh sách môn học", ex);
            JOptionPane.showMessageDialog(this,
                    "Lỗi: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void onRemoveSubject(ActionEvent e) {
        int selectedRow = subjectTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn một môn học để xóa",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        subjectTableModel.removeRow(selectedRow);
    }
    
    private void addSubjectToTable(Subject subject) {
        subjectTableModel.addRow(new Object[] {
                subject.getMaMon(),
                subject.getTenMon(),
                subject.getSoTinChi()
        });
    }
    
    private List<Subject> getSelectedSubjects() throws ServiceException {
        List<Subject> subjects = new ArrayList<>();
        
        for (int i = 0; i < subjectTableModel.getRowCount(); i++) {
            String maMon = (String) subjectTableModel.getValueAt(i, 0);
            Subject subject = subjectService.getSubjectByMaMon(maMon);
            subjects.add(subject);
        }
        
        return subjects;
    }
    
    private boolean validateForm() {
        if (txtMaLop.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập mã lớp",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtMaLop.requestFocus();
            return false;
        }
        
        // When creating new class, check if ID already exists
        if (academicClass == null) {
            try {
                String maLop = txtMaLop.getText().trim();
                academicClassService.getClassByMaLop(maLop);
                
                JOptionPane.showMessageDialog(this,
                        "Mã lớp đã tồn tại",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                txtMaLop.requestFocus();
                return false;
            } catch (ServiceException ex) {
                // If we get a "not found" exception, that's good
                // Otherwise, report the error
                if (!ex.getMessage().contains("không tồn tại")) {
                    logger.log(Level.SEVERE, "Lỗi khi kiểm tra mã lớp", ex);
                    JOptionPane.showMessageDialog(this,
                            "Lỗi kiểm tra mã lớp: " + ex.getMessage(),
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
        }
        
        return true;
    }
    
    public boolean isSaved() {
        return saved;
    }
} 