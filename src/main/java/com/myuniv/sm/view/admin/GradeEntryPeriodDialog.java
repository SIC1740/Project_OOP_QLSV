package com.myuniv.sm.view.admin;

import com.myuniv.sm.model.GradeEntryPeriod;
import com.myuniv.sm.model.Subject;
import com.myuniv.sm.service.GradeEntryPeriodService;
import com.myuniv.sm.service.ServiceException;
import com.myuniv.sm.service.SubjectService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Dialog for setting grade entry periods
 */
public class GradeEntryPeriodDialog extends JDialog {
    private static final Logger logger = Logger.getLogger(GradeEntryPeriodDialog.class.getName());
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    private final GradeEntryPeriodService periodService;
    private final SubjectService subjectService;
    
    private JComboBox<String> cmbSubject;
    private JTextField txtClass;
    private JTextField txtStartDate;
    private JTextField txtEndDate;
    private JButton btnSave;
    private JButton btnCancel;
    
    private GradeEntryPeriod currentPeriod;
    private boolean saved = false;

    public GradeEntryPeriodDialog(Frame parent, GradeEntryPeriod period, String preselectedMaMon, String preselectedMaLop) {
        super(parent, period == null ? "Thêm thời gian nhập điểm" : "Sửa thời gian nhập điểm", true);
        
        this.currentPeriod = period;
        this.periodService = new GradeEntryPeriodService();
        this.subjectService = new SubjectService();
        
        initComponents();
        
        // If editing existing period, fill form
        if (period != null) {
            populateFormWithPeriod(period);
        } else if (preselectedMaMon != null && preselectedMaLop != null) {
            // Set preselected values if provided
            selectSubjectInComboBox(preselectedMaMon);
            txtClass.setText(preselectedMaLop);
        }
        
        setSize(450, 350);
        setLocationRelativeTo(parent);
        setResizable(false);
    }
    
    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Create components
        JLabel lblSubject = new JLabel("Môn học:");
        JLabel lblClass = new JLabel("Lớp:");
        JLabel lblStartDate = new JLabel("Thời gian bắt đầu (dd/MM/yyyy HH:mm):");
        JLabel lblEndDate = new JLabel("Thời gian kết thúc (dd/MM/yyyy HH:mm):");
        
        cmbSubject = new JComboBox<>();
        txtClass = new JTextField(20);
        txtStartDate = new JTextField(20);
        txtEndDate = new JTextField(20);
        
        // Load subjects into combo box
        loadSubjects();
        
        // Set default dates to today and tomorrow
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrow = now.plusDays(1);
        txtStartDate.setText(now.format(dateFormatter));
        txtEndDate.setText(tomorrow.format(dateFormatter));
        
        // Add components to panel
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        panel.add(lblSubject, gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        panel.add(cmbSubject, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST;
        panel.add(lblClass, gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        panel.add(txtClass, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST;
        panel.add(lblStartDate, gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        panel.add(txtStartDate, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST;
        panel.add(lblEndDate, gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        panel.add(txtEndDate, gbc);
        
        // Create button panel
        JPanel buttonPanel = new JPanel();
        btnSave = new JButton("Lưu");
        btnCancel = new JButton("Hủy");
        
        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);
        
        // Add panels to dialog
        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Add listeners
        btnSave.addActionListener(this::onSave);
        btnCancel.addActionListener(e -> dispose());
        
        // Add help text
        JTextArea helpText = new JTextArea("Lưu ý: Thời gian nhập điểm cho phép giáo viên nhập điểm cho lớp học chỉ trong khoảng thời gian được thiết lập.");
        helpText.setWrapStyleWord(true);
        helpText.setLineWrap(true);
        helpText.setEditable(false);
        helpText.setBackground(panel.getBackground());
        helpText.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        add(helpText, BorderLayout.NORTH);
    }
    
    private void loadSubjects() {
        try {
            List<Subject> subjects = subjectService.findAll();
            for (Subject subject : subjects) {
                cmbSubject.addItem(subject.getMaMon() + " - " + subject.getTenMon());
            }
        } catch (ServiceException e) {
            logger.log(Level.SEVERE, "Error loading subjects", e);
            JOptionPane.showMessageDialog(this,
                    "Không thể tải danh sách môn học: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void populateFormWithPeriod(GradeEntryPeriod period) {
        selectSubjectInComboBox(period.getMaMon());
        txtClass.setText(period.getMaLop());
        txtStartDate.setText(period.getThoi_gian_bat_dau_nhap().format(dateFormatter));
        txtEndDate.setText(period.getThoi_gian_ket_thuc_nhap().format(dateFormatter));
        
        // Disable changing subject and class for existing period
        cmbSubject.setEnabled(false);
        txtClass.setEditable(false);
    }
    
    private void selectSubjectInComboBox(String maMon) {
        for (int i = 0; i < cmbSubject.getItemCount(); i++) {
            String item = cmbSubject.getItemAt(i);
            if (item.startsWith(maMon + " - ")) {
                cmbSubject.setSelectedIndex(i);
                break;
            }
        }
    }
    
    private void onSave(ActionEvent e) {
        if (!validateForm()) {
            return;
        }
        
        try {
            // Get values from form
            String selectedSubject = cmbSubject.getSelectedItem().toString();
            String maMon = selectedSubject.split(" - ")[0]; // Extract subject code
            String maLop = txtClass.getText().trim();
            
            LocalDateTime startTime = LocalDateTime.parse(txtStartDate.getText(), dateFormatter);
            LocalDateTime endTime = LocalDateTime.parse(txtEndDate.getText(), dateFormatter);
            
            if (currentPeriod == null) {
                // Create new period
                GradeEntryPeriod newPeriod = new GradeEntryPeriod();
                newPeriod.setMaMon(maMon);
                newPeriod.setMaLop(maLop);
                newPeriod.setThoi_gian_bat_dau_nhap(startTime);
                newPeriod.setThoi_gian_ket_thuc_nhap(endTime);
                
                if (periodService.createEntryPeriod(newPeriod)) {
                    saved = true;
                    JOptionPane.showMessageDialog(this,
                            "Đã thêm thời gian nhập điểm thành công!",
                            "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Không thể thêm thời gian nhập điểm.",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // We don't support updating periods in the new model
                // Instead, recommend to delete the old one and create a new entry
                JOptionPane.showMessageDialog(this,
                        "Không thể chỉnh sửa thời gian nhập điểm đã tạo. Hãy xóa và tạo mới.",
                        "Thông tin", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error saving grade entry period", ex);
            JOptionPane.showMessageDialog(this,
                    "Lỗi: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean validateForm() {
        // Check subject
        if (cmbSubject.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn môn học.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            cmbSubject.requestFocus();
            return false;
        }
        
        // Check class
        if (txtClass.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập mã lớp.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtClass.requestFocus();
            return false;
        }
        
        // Validate start date
        try {
            LocalDateTime.parse(txtStartDate.getText(), dateFormatter);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Định dạng thời gian bắt đầu không hợp lệ. Sử dụng định dạng: dd/MM/yyyy HH:mm",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtStartDate.requestFocus();
            return false;
        }
        
        // Validate end date
        try {
            LocalDateTime.parse(txtEndDate.getText(), dateFormatter);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Định dạng thời gian kết thúc không hợp lệ. Sử dụng định dạng: dd/MM/yyyy HH:mm",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtEndDate.requestFocus();
            return false;
        }
        
        // Check start time before end time
        LocalDateTime startTime = LocalDateTime.parse(txtStartDate.getText(), dateFormatter);
        LocalDateTime endTime = LocalDateTime.parse(txtEndDate.getText(), dateFormatter);
        
        if (!startTime.isBefore(endTime)) {
            JOptionPane.showMessageDialog(this,
                    "Thời gian bắt đầu phải trước thời gian kết thúc.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtStartDate.requestFocus();
            return false;
        }
        
        return true;
    }
    
    public boolean isSaved() {
        return saved;
    }
} 