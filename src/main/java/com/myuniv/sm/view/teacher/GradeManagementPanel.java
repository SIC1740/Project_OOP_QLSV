package com.myuniv.sm.view.teacher;

import com.myuniv.sm.model.Grade;
import com.myuniv.sm.model.GradeEntryPeriod;
import com.myuniv.sm.model.Student;
import com.myuniv.sm.service.GradeEntryPeriodService;
import com.myuniv.sm.service.GradeService;
import com.myuniv.sm.service.ServiceException;
import com.myuniv.sm.service.StudentService;

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
 * Panel for teachers to manage grades for a specific class and subject
 */
public class GradeManagementPanel extends JPanel {
    private static final Logger logger = Logger.getLogger(GradeManagementPanel.class.getName());
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    private final String lecturerId;
    private final String maMon;
    private final String maLop;
    
    private final GradeService gradeService;
    private final StudentService studentService;
    private final GradeEntryPeriodService periodService;
    
    private final DefaultTableModel tableModel;
    private final JTable gradesTable;
    
    private JButton btnSave;
    private JLabel statusLabel;
    
    private boolean canEditGrades = false;
    private GradeEntryPeriod currentPeriod;
    
    public GradeManagementPanel(String lecturerId, String maMon, String maLop) {
        this.lecturerId = lecturerId;
        this.maMon = maMon;
        this.maLop = maLop;
        this.gradeService = new GradeService();
        this.studentService = new StudentService();
        this.periodService = new GradeEntryPeriodService();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        
        // Title
        JLabel titleLabel = new JLabel("Quản lý điểm lớp " + maLop + " - Môn " + maMon);
        titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, 16));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Status label
        statusLabel = new JLabel();
        headerPanel.add(statusLabel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Create table model for grades
        tableModel = new DefaultTableModel(
                new Object[]{"Mã SV", "Họ tên", "Điểm CC (10%)", "Điểm quá trình (30%)", 
                           "Điểm thi (60%)", "Điểm tổng kết", "Điểm hệ 4", "Xếp loại"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Only allow editing grade columns (2, 3, 4) if allowed by entry period
                return column >= 2 && column <= 4 && canEditGrades;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex >= 2 && columnIndex <= 6) {
                    return BigDecimal.class;
                }
                return Object.class;
            }
        };
        
        gradesTable = new JTable(tableModel);
        gradesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        gradesTable.setRowHeight(25);
        gradesTable.getTableHeader().setReorderingAllowed(false);
        
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
            gradesTable.getColumnModel().getColumn(i).setCellRenderer(decimalRenderer);
        }
        
        // Set column widths
        gradesTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        gradesTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        
        JScrollPane scrollPane = new JScrollPane(gradesTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Create button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        
        btnSave = new JButton("Lưu thay đổi");
        btnSave.addActionListener(e -> saveGrades());
        
        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.addActionListener(e -> loadGrades());
        
        buttonPanel.add(btnSave);
        buttonPanel.add(btnRefresh);
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Check if teacher can edit grades and load data
        checkEntryPeriod();
        loadGrades();
    }
    
    /**
     * Check if there is an active entry period for this class and subject
     */
    private void checkEntryPeriod() {
        try {
            currentPeriod = periodService.getCurrentEntryPeriod(maMon, maLop);
            
            if (currentPeriod != null) {
                canEditGrades = true;
                statusLabel.setText("<html><b>Trạng thái nhập điểm:</b> " + 
                        "<font color='green'>Mở</font> (từ " + 
                        currentPeriod.getThoi_gian_bat_dau_nhap().format(dateFormatter) +
                        " đến " + 
                        currentPeriod.getThoi_gian_ket_thuc_nhap().format(dateFormatter) + ")</html>");
                btnSave.setEnabled(true);
            } else {
                canEditGrades = false;
                statusLabel.setText("<html><b>Trạng thái nhập điểm:</b> " + 
                        "<font color='red'>Đóng</font> (Ngoài thời gian cho phép nhập điểm)</html>");
                btnSave.setEnabled(false);
            }
        } catch (ServiceException e) {
            logger.log(Level.SEVERE, "Error checking for entry period", e);
            canEditGrades = false;
            statusLabel.setText("<html><b>Trạng thái nhập điểm:</b> " + 
                    "<font color='gray'>Không xác định</font></html>");
            btnSave.setEnabled(false);
        }
    }
    
    /**
     * Load grades for the class and subject
     */
    private void loadGrades() {
        tableModel.setRowCount(0);
        
        try {
            // Get grades for this subject and class
            List<Grade> grades = gradeService.getGradesBySubjectAndClass(maMon, maLop);
            
            // Get all students in this class
            List<Student> students = studentService.getStudentsByClass(maLop);
            
            // Create a list to track which students already have grades
            List<String> studentsWithGrades = new ArrayList<>();
            
            // Add existing grades to the table
            for (Grade grade : grades) {
                studentsWithGrades.add(grade.getMsv());
                
                tableModel.addRow(new Object[]{
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
            
            // Add students without grades
            for (Student student : students) {
                if (!studentsWithGrades.contains(student.getMsv())) {
                    tableModel.addRow(new Object[]{
                        student.getMsv(),
                        student.getHoTen(),
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        "F"
                    });
                }
            }
            
        } catch (ServiceException e) {
            logger.log(Level.SEVERE, "Error loading grades", e);
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi tải dữ liệu điểm: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Save all grades in the table
     */
    private void saveGrades() {
        if (!canEditGrades) {
            JOptionPane.showMessageDialog(this,
                    "Không thể lưu điểm. Thời gian nhập điểm đã kết thúc hoặc chưa được mở.",
                    "Không có quyền", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            List<Grade> gradesToSave = new ArrayList<>();
            
            for (int row = 0; row < tableModel.getRowCount(); row++) {
                String msv = (String) tableModel.getValueAt(row, 0);
                BigDecimal diemCC = (BigDecimal) tableModel.getValueAt(row, 2);
                BigDecimal diemQTrinh = (BigDecimal) tableModel.getValueAt(row, 3);
                BigDecimal diemThi = (BigDecimal) tableModel.getValueAt(row, 4);
                
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
            
            // Reload grades
            loadGrades();
            
        } catch (ServiceException e) {
            logger.log(Level.SEVERE, "Error saving grades", e);
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi lưu điểm: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
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