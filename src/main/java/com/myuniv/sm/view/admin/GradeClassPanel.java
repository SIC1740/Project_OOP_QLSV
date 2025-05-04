package com.myuniv.sm.view.admin;

import com.myuniv.sm.model.Grade;
import com.myuniv.sm.model.GradeEntryPeriod;
import com.myuniv.sm.model.Student;
import com.myuniv.sm.model.Subject;
import com.myuniv.sm.service.GradeEntryPeriodService;
import com.myuniv.sm.service.GradeService;
import com.myuniv.sm.service.ServiceException;
import com.myuniv.sm.service.StudentService;
import com.myuniv.sm.service.SubjectService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Panel for displaying and editing grades for a specific class and subject
 */
public class GradeClassPanel extends JPanel {
    private static final Logger logger = Logger.getLogger(GradeClassPanel.class.getName());
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    private final String maLop;
    private final String maMon;
    private String tenMon;
    private String adminUsername;
    
    private final GradeService gradeService;
    private final SubjectService subjectService;
    private final StudentService studentService;
    private final GradeEntryPeriodService periodService;
    
    private final DefaultTableModel gradeTableModel;
    private final JTable gradeTable;
    
    private final JButton btnSave;
    private final JButton btnRefresh;
    private final JButton btnAddStudent;
    private final JButton btnRemoveGrade;
    private final JButton btnManageEntryPeriod;
    
    private JLabel entryPeriodStatusLabel;
    private GradeEntryPeriod currentEntryPeriod;
    
    public GradeClassPanel(String maMon, String maLop, String adminUsername) {
        this.maMon = maMon;
        this.maLop = maLop;
        this.adminUsername = adminUsername;
        this.gradeService = new GradeService();
        this.subjectService = new SubjectService();
        this.studentService = new StudentService();
        this.periodService = new GradeEntryPeriodService();
        
        // Set name for card layout identification
        setName("grades_" + maMon + "_" + maLop);
        
        // Initialize layout
        setLayout(new BorderLayout());
        
        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Load subject information
        try {
            Subject subject = subjectService.getSubjectByMaMon(maMon);
            if (subject != null) {
                tenMon = subject.getTenMon();
                JLabel titleLabel = new JLabel("Quản lý điểm: " + tenMon + " - Lớp " + maLop);
                titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, 16));
                headerPanel.add(titleLabel, BorderLayout.WEST);
            }
        } catch (ServiceException e) {
            logger.log(Level.SEVERE, "Error loading subject information", e);
            JLabel titleLabel = new JLabel("Quản lý điểm: Môn " + maMon + " - Lớp " + maLop);
            titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, 16));
            headerPanel.add(titleLabel, BorderLayout.WEST);
        }
        
        // Add entry period status label
        entryPeriodStatusLabel = new JLabel();
        entryPeriodStatusLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        headerPanel.add(entryPeriodStatusLabel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Create table model with editable cells for grades
        gradeTableModel = new DefaultTableModel(
                new Object[]{"Mã SV", "Họ tên", "Điểm CC (10%)", "Điểm quá trình (30%)", 
                           "Điểm thi (60%)", "Điểm tổng kết", "Điểm hệ 4", "Xếp loại"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Only allow editing grade columns (2, 3, 4)
                return column >= 2 && column <= 4;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex >= 2 && columnIndex <= 6) {
                    return BigDecimal.class;
                }
                return Object.class;
            }
        };
        
        gradeTable = new JTable(gradeTableModel);
        gradeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        gradeTable.setRowHeight(25);
        gradeTable.getTableHeader().setReorderingAllowed(false);
        
        // Custom renderer for decimal values
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        DefaultTableCellRenderer decimalRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                
                setHorizontalAlignment(SwingConstants.RIGHT);
                if (value instanceof BigDecimal) {
                    setText(decimalFormat.format(value));
                }
                return c;
            }
        };
        
        // Set renderers for numeric columns
        for (int i = 2; i <= 6; i++) {
            gradeTable.getColumnModel().getColumn(i).setCellRenderer(decimalRenderer);
        }
        
        // Set column widths
        gradeTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        gradeTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        
        JScrollPane scrollPane = new JScrollPane(gradeTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        btnSave = new JButton("Lưu thay đổi");
        btnRefresh = new JButton("Làm mới");
        btnAddStudent = new JButton("Thêm sinh viên");
        btnRemoveGrade = new JButton("Xóa điểm");
        btnManageEntryPeriod = new JButton("Quản lý thời gian nhập điểm");
        
        buttonPanel.add(btnSave);
        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnAddStudent);
        buttonPanel.add(btnRemoveGrade);
        buttonPanel.add(btnManageEntryPeriod);
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Add action listeners
        btnSave.addActionListener(e -> saveGrades());
        btnRefresh.addActionListener(e -> loadGradeData());
        btnAddStudent.addActionListener(e -> addStudentGrade());
        btnRemoveGrade.addActionListener(e -> removeSelectedGrade());
        btnManageEntryPeriod.addActionListener(e -> manageEntryPeriod());
        
        // Check for entry period and load initial data
        checkEntryPeriod();
        loadGradeData();
    }
    
    /**
     * Check if there is an active entry period for this class and subject
     */
    private void checkEntryPeriod() {
        try {
            currentEntryPeriod = periodService.getCurrentEntryPeriod(maMon, maLop);
            
            if (currentEntryPeriod != null) {
                entryPeriodStatusLabel.setText("<html><b>Trạng thái nhập điểm:</b> " + 
                        "<font color='green'>Mở</font> (từ " + 
                        currentEntryPeriod.getThoi_gian_bat_dau_nhap().format(dateFormatter) + 
                        " đến " + currentEntryPeriod.getThoi_gian_ket_thuc_nhap().format(dateFormatter) + ")</html>");
            } else {
                entryPeriodStatusLabel.setText("<html><b>Trạng thái nhập điểm:</b> " + 
                        "<font color='red'>Đóng</font></html>");
            }
        } catch (ServiceException e) {
            logger.log(Level.SEVERE, "Error checking for entry period", e);
            entryPeriodStatusLabel.setText("<html><b>Trạng thái nhập điểm:</b> " + 
                    "<font color='gray'>Không xác định</font></html>");
        }
    }
    
    /**
     * Open dialog to manage entry periods for this class and subject
     */
    private void manageEntryPeriod() {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        GradeEntryPeriodDialog dialog = new GradeEntryPeriodDialog(
                parent, null, maMon, maLop);
        dialog.setVisible(true);
        
        if (dialog.isSaved()) {
            checkEntryPeriod();
        }
    }
    
    /**
     * Load grade data for the selected class and subject
     */
    private void loadGradeData() {
        // Clear existing data
        gradeTableModel.setRowCount(0);
        
        try {
            // Get grades for this subject and class
            List<Grade> grades = gradeService.getGradesBySubjectAndClass(maMon, maLop);
            
            for (Grade grade : grades) {
                gradeTableModel.addRow(new Object[]{
                    grade.getMsv(),
                    grade.getTenSinhVien(),
                    grade.getDiemCC(),
                    grade.getDiemQTrinh(),
                    grade.getDiemThi(),
                    calculateTotalGrade(grade),
                    calculateGPA(grade),
                    calculateLetterGrade(grade)
                });
            }
            
            if (grades.isEmpty()) {
                logger.info("Không có dữ liệu điểm cho lớp " + maLop + ", môn " + maMon);
            } else {
                logger.info("Đã tải " + grades.size() + " điểm sinh viên");
            }
        } catch (ServiceException e) {
            logger.log(Level.SEVERE, "Lỗi khi tải dữ liệu điểm", e);
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi tải dữ liệu điểm: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Save all grades in the table
     */
    private void saveGrades() {
        try {
            List<Grade> gradesToSave = new ArrayList<>();
            
            for (int row = 0; row < gradeTableModel.getRowCount(); row++) {
                String msv = (String) gradeTableModel.getValueAt(row, 0);
                BigDecimal diemCC = (BigDecimal) gradeTableModel.getValueAt(row, 2);
                BigDecimal diemQTrinh = (BigDecimal) gradeTableModel.getValueAt(row, 3);
                BigDecimal diemThi = (BigDecimal) gradeTableModel.getValueAt(row, 4);
                
                // Validate grade values
                if (!isValidGrade(diemCC) || !isValidGrade(diemQTrinh) || !isValidGrade(diemThi)) {
                    JOptionPane.showMessageDialog(this,
                            "Điểm không hợp lệ cho sinh viên " + msv + ". Điểm phải từ 0-10.",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                Grade grade = new Grade(msv, maLop, maMon, diemCC, diemQTrinh, diemThi);
                gradesToSave.add(grade);
            }
            
            // Save all grades
            for (Grade grade : gradesToSave) {
                gradeService.saveGrade(grade);
            }
            
            JOptionPane.showMessageDialog(this,
                    "Đã lưu điểm thành công!",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            
            // Reload to show calculated fields
            loadGradeData();
            
        } catch (ServiceException e) {
            logger.log(Level.SEVERE, "Lỗi khi lưu điểm", e);
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi lưu điểm: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Add a new student to the grade list
     */
    private void addStudentGrade() {
        try {
            // Get students in this class who don't have grades yet
            List<Student> allStudentsInClass = studentService.getStudentsByClass(maLop);
            List<Grade> existingGrades = gradeService.getGradesBySubjectAndClass(maMon, maLop);
            
            // Filter out students who already have grades
            List<Student> studentsWithoutGrades = new ArrayList<>();
            for (Student student : allStudentsInClass) {
                boolean hasGrade = false;
                for (Grade grade : existingGrades) {
                    if (grade.getMsv().equals(student.getMsv())) {
                        hasGrade = true;
                        break;
                    }
                }
                if (!hasGrade) {
                    studentsWithoutGrades.add(student);
                }
            }
            
            if (studentsWithoutGrades.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Tất cả sinh viên trong lớp đã có điểm cho môn học này.",
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // Create array of student names for the combo box
            String[] studentNames = new String[studentsWithoutGrades.size()];
            for (int i = 0; i < studentsWithoutGrades.size(); i++) {
                Student student = studentsWithoutGrades.get(i);
                studentNames[i] = student.getMsv() + " - " + student.getHoTen();
            }
            
            // Show dialog to select student
            String selectedStudent = (String) JOptionPane.showInputDialog(this,
                    "Chọn sinh viên để thêm điểm:",
                    "Thêm điểm sinh viên",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    studentNames,
                    studentNames[0]);
            
            if (selectedStudent != null) {
                // Extract student ID from selection
                String msv = selectedStudent.split(" - ")[0];
                String hoTen = selectedStudent.substring(msv.length() + 3);
                
                // Add empty grade to table
                gradeTableModel.addRow(new Object[]{
                    msv,
                    hoTen,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    "F"
                });
                
                // Select the new row
                int newRow = gradeTableModel.getRowCount() - 1;
                gradeTable.setRowSelectionInterval(newRow, newRow);
                gradeTable.scrollRectToVisible(gradeTable.getCellRect(newRow, 0, true));
            }
            
        } catch (ServiceException e) {
            logger.log(Level.SEVERE, "Lỗi khi tải danh sách sinh viên", e);
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi tải danh sách sinh viên: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Remove the selected grade
     */
    private void removeSelectedGrade() {
        int selectedRow = gradeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn một sinh viên để xóa điểm",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String msv = (String) gradeTableModel.getValueAt(selectedRow, 0);
        String hoTen = (String) gradeTableModel.getValueAt(selectedRow, 1);
        
        int confirmation = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa điểm của sinh viên " + msv + " - " + hoTen + "?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        
        if (confirmation == JOptionPane.YES_OPTION) {
            try {
                Grade grade = gradeService.getGradeByStudentSubjectAndClass(msv, maMon, maLop);
                if (grade != null) {
                    gradeService.deleteGrade(grade.getIdDiemMon());
                    
                    // Remove from table
                    gradeTableModel.removeRow(selectedRow);
                    
                    JOptionPane.showMessageDialog(this,
                            "Đã xóa điểm thành công!",
                            "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (ServiceException e) {
                logger.log(Level.SEVERE, "Lỗi khi xóa điểm", e);
                JOptionPane.showMessageDialog(this,
                        "Lỗi khi xóa điểm: " + e.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Calculate total grade based on component weights
     */
    private BigDecimal calculateTotalGrade(Grade grade) {
        BigDecimal diemCC = grade.getDiemCC().multiply(BigDecimal.valueOf(0.1));
        BigDecimal diemQTrinh = grade.getDiemQTrinh().multiply(BigDecimal.valueOf(0.3));
        BigDecimal diemThi = grade.getDiemThi().multiply(BigDecimal.valueOf(0.6));
        
        return diemCC.add(diemQTrinh).add(diemThi).setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Calculate GPA (scale 4.0) from total grade
     */
    private BigDecimal calculateGPA(Grade grade) {
        BigDecimal totalGrade = calculateTotalGrade(grade);
        
        if (totalGrade.compareTo(BigDecimal.valueOf(9.0)) >= 0) {
            return BigDecimal.valueOf(4.0);
        } else if (totalGrade.compareTo(BigDecimal.valueOf(8.5)) >= 0) {
            return BigDecimal.valueOf(3.7);
        } else if (totalGrade.compareTo(BigDecimal.valueOf(8.0)) >= 0) {
            return BigDecimal.valueOf(3.5);
        } else if (totalGrade.compareTo(BigDecimal.valueOf(7.0)) >= 0) {
            return BigDecimal.valueOf(3.0);
        } else if (totalGrade.compareTo(BigDecimal.valueOf(6.5)) >= 0) {
            return BigDecimal.valueOf(2.5);
        } else if (totalGrade.compareTo(BigDecimal.valueOf(5.5)) >= 0) {
            return BigDecimal.valueOf(2.0);
        } else if (totalGrade.compareTo(BigDecimal.valueOf(5.0)) >=  0) {
            return BigDecimal.valueOf(1.5);
        } else if (totalGrade.compareTo(BigDecimal.valueOf(4.0)) >= 0) {
            return BigDecimal.valueOf(1.0);
        } else {
            return BigDecimal.valueOf(0.0);
        }
    }
    
    /**
     * Calculate letter grade from total grade
     */
    private String calculateLetterGrade(Grade grade) {
        BigDecimal totalGrade = calculateTotalGrade(grade);
        
        if (totalGrade.compareTo(BigDecimal.valueOf(9)) >= 0) {
            return "A+";
        }else if (totalGrade.compareTo(BigDecimal.valueOf(8.5)) >= 0) {
            return "A";
        } else if (totalGrade.compareTo(BigDecimal.valueOf(8.0)) >= 0) {
            return "B+";
        } else if (totalGrade.compareTo(BigDecimal.valueOf(7.0)) >= 0) {
            return "B";
        } else if (totalGrade.compareTo(BigDecimal.valueOf(6.5)) >= 0) {
            return "C+";
        } else if (totalGrade.compareTo(BigDecimal.valueOf(5.5)) >= 0) {
            return "C";
        } else if (totalGrade.compareTo(BigDecimal.valueOf(5.0)) >= 0) {
            return "D+";
        } else if (totalGrade.compareTo(BigDecimal.valueOf(4.0)) >= 0) {
            return "D";
        } else {
            return "F";
        }
    }
    
    /**
     * Validate that a grade is between 0 and 10
     */
    private boolean isValidGrade(BigDecimal grade) {
        return grade.compareTo(BigDecimal.ZERO) >= 0 && grade.compareTo(BigDecimal.TEN) <= 0;
    }
} 