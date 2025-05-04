package com.myuniv.sm.view.admin;

import com.myuniv.sm.model.Subject;
import com.myuniv.sm.service.SubjectService;
import com.myuniv.sm.service.ServiceException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SubjectDialog extends JDialog {
    private static final Logger logger = Logger.getLogger(SubjectDialog.class.getName());
    
    private final JTextField txtMaMon;
    private final JTextField txtTenMon;
    private final JSpinner spnSoTinChi;
    private final JButton btnSave;
    private final JButton btnCancel;
    
    private final SubjectService subjectService;
    private final Subject subject;
    private boolean saved = false;
    
    public SubjectDialog(Frame parent, Subject subject) {
        super(parent, subject == null ? "Thêm môn học mới" : "Cập nhật môn học", true);
        
        this.subject = subject;
        this.subjectService = new SubjectService();
        
        // Create form layout
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Create form fields
        JLabel lblMaMon = new JLabel("Mã môn học:");
        JLabel lblTenMon = new JLabel("Tên môn học:");
        JLabel lblSoTinChi = new JLabel("Số tín chỉ:");
        
        txtMaMon = new JTextField(20);
        txtTenMon = new JTextField(20);
        spnSoTinChi = new JSpinner(new SpinnerNumberModel(3, 1, 10, 1));
        
        // Add fields to form
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(lblMaMon, gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(txtMaMon, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(lblTenMon, gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(txtTenMon, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(lblSoTinChi, gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(spnSoTinChi, gbc);
        
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
        setSize(400, 200);
        setLocationRelativeTo(parent);
        setResizable(false);
        
        // Fill form if editing
        if (subject != null) {
            txtMaMon.setText(subject.getMaMon());
            txtMaMon.setEditable(false); // Prevent changing ID for existing subjects
            txtTenMon.setText(subject.getTenMon());
            spnSoTinChi.setValue(subject.getSoTinChi());
        }
    }
    
    private void onSave(ActionEvent e) {
        if (!validateForm()) {
            return;
        }
        
        String maMon = txtMaMon.getText().trim();
        String tenMon = txtTenMon.getText().trim();
        int soTinChi = (Integer) spnSoTinChi.getValue();
        
        try {
            if (subject == null) {
                // Create new subject - check if the code already exists
                try {
                    Subject existingSubject = subjectService.getSubjectByMaMon(maMon);
                    
                    // If we found an existing subject, show an error
                    if (existingSubject != null) {
                        JOptionPane.showMessageDialog(this,
                                "Môn học với mã " + maMon + " đã tồn tại.",
                                "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (ServiceException ex) {
                    // This is expected for a subject code that doesn't exist
                    logger.log(Level.FINE, "Tạo mã môn học mới: " + maMon);
                }
                
                // Create new subject
                Subject newSubject = new Subject();
                newSubject.setMaMon(maMon);
                newSubject.setTenMon(tenMon);
                newSubject.setSoTinChi(soTinChi);
                
                if (subjectService.createSubject(newSubject)) {
                    saved = true;
                    JOptionPane.showMessageDialog(this, 
                            "Thêm môn học thành công",
                            "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Không thể thêm môn học",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // Update existing subject
                subject.setTenMon(tenMon);
                subject.setSoTinChi(soTinChi);
                
                if (subjectService.updateSubject(subject)) {
                    saved = true;
                    JOptionPane.showMessageDialog(this, 
                            "Cập nhật môn học thành công",
                            "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Không thể cập nhật môn học",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (ServiceException ex) {
            logger.log(Level.SEVERE, "Lỗi khi lưu môn học", ex);
            JOptionPane.showMessageDialog(this,
                    "Lỗi: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean validateForm() {
        if (txtMaMon.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập mã môn học",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtMaMon.requestFocus();
            return false;
        }
        
        if (txtTenMon.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập tên môn học",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtTenMon.requestFocus();
            return false;
        }
        
        return true;
    }
    
    public boolean isSaved() {
        return saved;
    }
} 