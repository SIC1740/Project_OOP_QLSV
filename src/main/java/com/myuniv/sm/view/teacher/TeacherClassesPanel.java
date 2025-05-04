package com.myuniv.sm.view.teacher;

import com.myuniv.sm.model.Student;
import com.myuniv.sm.service.ServiceException;
import com.myuniv.sm.service.StudentService;
import com.myuniv.sm.util.ExcelExporter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.myuniv.sm.dao.util.DBConnection;
import com.myuniv.sm.service.GradeEntryPeriodService;

/**
 * Panel to display classes taught by a lecturer and their students
 */
public class TeacherClassesPanel extends JPanel {
    private static final Logger logger = Logger.getLogger(TeacherClassesPanel.class.getName());
    
    private final String lecturerId;
    private final StudentService studentService;
    
    // UI Components
    private JComboBox<String> comboSubject;
    private JComboBox<String> comboClass;
    private JTable studentsTable;
    private DefaultTableModel tableModel;
    private JLabel totalStudentsLabel;
    
    // Data storage
    private Map<String, String> subjectNames; // maMon -> tenMon
    private Map<String, List<String>> subjectClassMap; // maMon -> List of maLop
    private List<Student> currentStudents; // Current list of displayed students
    
    public TeacherClassesPanel(String lecturerId) {
        this.lecturerId = lecturerId;
        this.studentService = new StudentService();
        this.subjectNames = new HashMap<>();
        this.subjectClassMap = new HashMap<>();
        this.currentStudents = new ArrayList<>();
        
        initUI();
        loadSubjectsForLecturer();
    }
    
    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Create top panel with filters
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        
        // Title
        JLabel titleLabel = new JLabel("Lớp giảng dạy", SwingConstants.CENTER);
        titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, 16));
        topPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Filter panel
        JPanel filterPanel = createFilterPanel();
        topPanel.add(filterPanel, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Create table panel
        JPanel tablePanel = new JPanel(new BorderLayout(5, 5));
        tablePanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        // Create table model with columns
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
        };
        tableModel.addColumn("MSV");
        tableModel.addColumn("Họ và tên");
        tableModel.addColumn("Email");
        tableModel.addColumn("Số điện thoại");
        
        // Create table and scroll pane
        studentsTable = new JTable(tableModel);
        studentsTable.setRowHeight(25);
        studentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentsTable.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane scrollPane = new JScrollPane(studentsTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // Add total students count at the bottom
        totalStudentsLabel = new JLabel("Tổng số sinh viên: 0");
        totalStudentsLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
        tablePanel.add(totalStudentsLabel, BorderLayout.SOUTH);
        
        add(tablePanel, BorderLayout.CENTER);
        
        // Bottom panel with action buttons
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        
        // Subject selection
        panel.add(new JLabel("Môn học:"));
        comboSubject = new JComboBox<>();
        comboSubject.setPreferredSize(new Dimension(200, 25));
        comboSubject.addActionListener(e -> {
            updateClassComboBox();
            loadStudentsForSelectedClass();
        });
        panel.add(comboSubject);
        
        // Class selection
        panel.add(new JLabel("Lớp:"));
        comboClass = new JComboBox<>();
        comboClass.setPreferredSize(new Dimension(150, 25));
        comboClass.addActionListener(e -> loadStudentsForSelectedClass());
        panel.add(comboClass);
        
        // Refresh button
        JButton refreshButton = new JButton("Làm mới");
        refreshButton.addActionListener(e -> {
            loadSubjectsForLecturer();
            loadStudentsForSelectedClass();
        });
        panel.add(refreshButton);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        
        // Button to view/manage grades for the selected class
        JButton gradesButton = new JButton("Quản lý điểm lớp này");
        gradesButton.addActionListener(e -> openGradeManagement());
        panel.add(gradesButton);
        
        // Button to export student list
        JButton exportButton = new JButton("Xuất danh sách");
        exportButton.addActionListener(e -> exportStudentList());
        panel.add(exportButton);
        
        return panel;
    }
    
    private void loadSubjectsForLecturer() {
        subjectNames.clear();
        subjectClassMap.clear();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT gl.ma_mon, m.ten_mon, gl.ma_lop " +
                     "FROM GiangVien_Lop_MonHoc gl " +
                     "JOIN MonHoc m ON gl.ma_mon = m.ma_mon " +
                     "WHERE gl.ma_giangvien = ? " +
                     "ORDER BY m.ten_mon, gl.ma_lop")) {
            
            pstmt.setString(1, lecturerId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String maMon = rs.getString("ma_mon");
                    String tenMon = rs.getString("ten_mon");
                    String maLop = rs.getString("ma_lop");
                    
                    // Store subject name
                    subjectNames.put(maMon, tenMon);
                    
                    // Store class for this subject
                    subjectClassMap.computeIfAbsent(maMon, k -> new ArrayList<>())
                                 .add(maLop);
                }
            }
            
            // Update subject combobox
            updateSubjectComboBox();
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error loading subjects for lecturer", e);
            JOptionPane.showMessageDialog(this,
                "Không thể tải danh sách môn học: " + e.getMessage(),
                "Lỗi truy vấn dữ liệu",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateSubjectComboBox() {
        comboSubject.removeAllItems();
        
        // Add subjects to combo box (format: maMon - tenMon)
        for (Map.Entry<String, String> entry : subjectNames.entrySet()) {
            String maMon = entry.getKey();
            String tenMon = entry.getValue();
            comboSubject.addItem(maMon + " - " + tenMon);
        }
        
        // Update class combo box based on first subject
        updateClassComboBox();
    }
    
    private void updateClassComboBox() {
        comboClass.removeAllItems();
        
        // Get selected subject code
        if (comboSubject.getSelectedItem() != null) {
            String selectedItem = comboSubject.getSelectedItem().toString();
            String maMon = selectedItem.split(" - ")[0]; // Extract maMon from "maMon - tenMon"
            
            // Add classes for this subject
            List<String> classes = subjectClassMap.get(maMon);
            if (classes != null) {
                for (String maLop : classes) {
                    comboClass.addItem(maLop);
                }
            }
        }
    }
    
    private void loadStudentsForSelectedClass() {
        // Clear table
        tableModel.setRowCount(0);
        currentStudents.clear();
        
        if (comboSubject.getSelectedItem() == null || comboClass.getSelectedItem() == null) {
            totalStudentsLabel.setText("Tổng số sinh viên: 0");
            return;
        }
        
        // Get selected values
        String selectedSubject = comboSubject.getSelectedItem().toString();
        String maMon = selectedSubject.split(" - ")[0]; // Extract maMon
        String maLop = comboClass.getSelectedItem().toString();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT sv.* FROM SinhVien sv " +
                     "WHERE sv.ma_lop = ? " +
                     "ORDER BY sv.ho_ten")) {
            
            pstmt.setString(1, maLop);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                int studentCount = 0;
                
                while (rs.next()) {
                    String msv = rs.getString("msv");
                    String hoTen = rs.getString("ho_ten");
                    String email = rs.getString("email");
                    String soDienThoai = rs.getString("so_dien_thoai");
                    
                    // Create Student object
                    Student student = new Student();
                    student.setMsv(msv);
                    student.setHoTen(hoTen);
                    student.setEmail(email);
                    student.setSoDienThoai(soDienThoai);
                    student.setMaLop(maLop);
                    
                    // Add to list
                    currentStudents.add(student);
                    
                    // Add to table
                    tableModel.addRow(new Object[]{msv, hoTen, email, soDienThoai});
                    studentCount++;
                }
                
                totalStudentsLabel.setText("Tổng số sinh viên: " + studentCount);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error loading students for class", e);
            JOptionPane.showMessageDialog(this,
                "Không thể tải danh sách sinh viên: " + e.getMessage(),
                "Lỗi truy vấn dữ liệu",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void openGradeManagement() {
        if (comboSubject.getSelectedItem() == null || comboClass.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng chọn môn học và lớp trước.",
                "Thông báo",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Get selected values
        String selectedSubject = comboSubject.getSelectedItem().toString();
        String maMon = selectedSubject.split(" - ")[0]; // Extract maMon
        String tenMon = selectedSubject.split(" - ")[1]; // Extract tenMon
        String maLop = comboClass.getSelectedItem().toString();
        
        // Check if the teacher can enter grades for this class and subject
        try {
            GradeEntryPeriodService periodService = new GradeEntryPeriodService();
            boolean canEnterGrades = periodService.canEnterGrades(maMon);
            
            if (!canEnterGrades) {
                // Show warning but still allow viewing the grades
                JOptionPane.showMessageDialog(this,
                    "Thời gian nhập điểm cho lớp này hiện đang đóng.\n" +
                    "Bạn có thể xem điểm nhưng không thể chỉnh sửa.\n" +
                    "Hệ thống sẽ tự động mở quyền nhập điểm khi đến thời gian đã thiết lập.",
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            }
            
            // Open the grade management panel
            GradeManagementPanel gradePanel = new GradeManagementPanel(lecturerId, maMon, maLop);
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                    "Quản lý điểm - " + tenMon + " - Lớp " + maLop, true);
            dialog.setContentPane(gradePanel);
            dialog.setSize(900, 600);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            
        } catch (ServiceException e) {
            logger.log(Level.SEVERE, "Lỗi khi kiểm tra quyền nhập điểm", e);
            JOptionPane.showMessageDialog(this,
                "Không thể kiểm tra quyền nhập điểm: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void exportStudentList() {
        if (comboSubject.getSelectedItem() == null || comboClass.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng chọn môn học và lớp trước.",
                "Thông báo",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        if (currentStudents.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Không có sinh viên nào trong lớp này để xuất.",
                "Thông báo",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Get selected values
        String selectedSubject = comboSubject.getSelectedItem().toString();
        String tenMon = selectedSubject.split(" - ")[1]; // Extract tenMon
        String maLop = comboClass.getSelectedItem().toString();
        
        // Create a file chooser
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn vị trí lưu file");
        
        // Set default file name with class and subject info plus current datetime
        String defaultFileName = "DanhSachLop_" + maLop + "_" + tenMon.replaceAll("[^a-zA-Z0-9]", "_") + "_" + 
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
                // Export the student list for this class specifically
                ExcelExporter.exportClassStudentsToExcel(maLop + " - " + tenMon, currentStudents, filePath);
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
                logger.log(Level.SEVERE, "Lỗi khi xuất danh sách sinh viên ra Excel", e);
                JOptionPane.showMessageDialog(this, 
                    "Lỗi khi xuất file: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
} 