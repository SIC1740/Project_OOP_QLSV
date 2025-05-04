package com.myuniv.sm.view.student;

import com.myuniv.sm.model.Grade;
import com.myuniv.sm.service.GradeService;
import com.myuniv.sm.service.ServiceException;
import com.myuniv.sm.service.SubjectService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.myuniv.sm.dao.util.DBConnection;

/**
 * Panel to display student grades
 */
public class StudentGradesPanel extends JPanel {
    private static final Logger logger = Logger.getLogger(StudentGradesPanel.class.getName());
    
    private final String studentId;
    private final GradeService gradeService;
    private final SubjectService subjectService;
    
    // UI Components
    private JTable gradesTable;
    private DefaultTableModel tableModel;
    private JLabel semesterLabel;
    private JLabel gpaLabel;
    private JLabel totalCreditsLabel;
    private JComboBox<String> semesterComboBox;
    
    public StudentGradesPanel(String studentId) {
        this.studentId = studentId;
        this.gradeService = new GradeService();
        this.subjectService = new SubjectService();
        
        initUI();
        loadGradeData();
    }
    
    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Top panel with semester selection
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);
        
        // Center panel with grades table
        JPanel centerPanel = createGradeTablePanel();
        add(centerPanel, BorderLayout.CENTER);
        
        // Bottom panel with GPA summary
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEtchedBorder(),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Title
        JLabel titleLabel = new JLabel("Kết quả học tập", SwingConstants.CENTER);
        titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, 16));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Selection panel
        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        selectionPanel.add(new JLabel("Học kỳ:"));
        semesterComboBox = new JComboBox<>();
        semesterComboBox.addItem("Tất cả");
        semesterComboBox.addItem("Học kỳ 1 (2023-2024)");
        semesterComboBox.addItem("Học kỳ 2 (2023-2024)");
        semesterComboBox.setPreferredSize(new Dimension(200, 25));
        semesterComboBox.addActionListener(e -> loadGradeData());
        selectionPanel.add(semesterComboBox);
        
        JButton refreshButton = new JButton("Làm mới");
        refreshButton.addActionListener(e -> loadGradeData());
        selectionPanel.add(refreshButton);
        
        panel.add(selectionPanel, BorderLayout.CENTER);
        
        // Current semester info
        semesterLabel = new JLabel("Học kỳ hiện tại: Học kỳ 2 (2023-2024)");
        panel.add(semesterLabel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createGradeTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        // Create table model
        tableModel = new DefaultTableModel() {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex >= 3 && columnIndex <= 6) {
                    return BigDecimal.class; // Grade columns are BigDecimal
                }
                return String.class;
            }
            
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // All cells non-editable for student view
            }
        };
        
        // Add columns
        tableModel.addColumn("Mã môn");
        tableModel.addColumn("Tên môn học");
        tableModel.addColumn("Số tín chỉ");
        tableModel.addColumn("Điểm CC");
        tableModel.addColumn("Điểm QT");
        tableModel.addColumn("Điểm thi");
        tableModel.addColumn("Điểm TK");
        tableModel.addColumn("Điểm hệ 4");
        tableModel.addColumn("Xếp loại");
        tableModel.addColumn("Đánh giá");
        
        // Create the table
        gradesTable = new JTable(tableModel);
        gradesTable.setRowHeight(25);
        gradesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        gradesTable.getTableHeader().setReorderingAllowed(false);
        
        // Custom renderer for BigDecimal columns (align right, 2 decimal places)
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                // Format BigDecimal values to 2 decimal places
                if (value instanceof BigDecimal) {
                    value = ((BigDecimal) value).setScale(2, RoundingMode.HALF_UP).toString();
                }
                
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.RIGHT);
                return c;
            }
        };
        
        // Set custom renderer for grade columns
        for (int i = 3; i <= 7; i++) {
            gradesTable.getColumnModel().getColumn(i).setCellRenderer(rightRenderer);
        }
        
        // Center renderer for letter grade
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        gradesTable.getColumnModel().getColumn(8).setCellRenderer(centerRenderer);
        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(gradesTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEtchedBorder(),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Summary panel
        JPanel summaryPanel = new JPanel(new GridLayout(0, 2, 10, 5));
        
        // GPA
        summaryPanel.add(new JLabel("Điểm trung bình (hệ 4):"));
        gpaLabel = new JLabel("0.00");
        gpaLabel.setFont(new Font(gpaLabel.getFont().getName(), Font.BOLD, 14));
        gpaLabel.setForeground(new Color(41, 128, 185)); // Blue shade
        summaryPanel.add(gpaLabel);
        
        // Total credits
        summaryPanel.add(new JLabel("Tổng số tín chỉ đã học:"));
        totalCreditsLabel = new JLabel("0");
        totalCreditsLabel.setFont(new Font(totalCreditsLabel.getFont().getName(), Font.BOLD, 14));
        summaryPanel.add(totalCreditsLabel);
        
        panel.add(summaryPanel, BorderLayout.CENTER);
        
        // Note
        JLabel noteLabel = new JLabel(
            "<html><i>Ghi chú: Điểm trung bình được tính dựa trên hệ số tín chỉ của mỗi môn học</i></html>");
        noteLabel.setForeground(Color.GRAY);
        noteLabel.setFont(new Font(noteLabel.getFont().getName(), Font.ITALIC, 11));
        panel.add(noteLabel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void loadGradeData() {
        // Clear table
        tableModel.setRowCount(0);
        
        try {
            // Get student grades from database
            String sql = "SELECT d.*, m.ten_mon, m.so_tin_chi " +
                         "FROM DiemMon d " +
                         "JOIN MonHoc m ON d.ma_mon = m.ma_mon " +
                         "WHERE d.msv = ? " +
                         "ORDER BY m.ten_mon";
                     
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
                pstmt.setString(1, studentId);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    List<GradeEntry> grades = new ArrayList<>();
                    
                    while (rs.next()) {
                        GradeEntry entry = new GradeEntry();
                        
                        entry.maMon = rs.getString("ma_mon");
                        entry.tenMon = rs.getString("ten_mon");
                        entry.soTinChi = rs.getInt("so_tin_chi");
                        entry.diemCC = rs.getBigDecimal("diem_cc");
                        entry.diemQTrinh = rs.getBigDecimal("diem_qtrinh");
                        entry.diemThi = rs.getBigDecimal("diem_thi");
                        entry.diemTK = rs.getBigDecimal("diem_tk");
                        entry.diemHe4 = rs.getBigDecimal("diem_he4");
                        entry.xepLoai = rs.getString("xep_loai");
                        
                        // Set default values for null fields
                        if (entry.diemCC == null) entry.diemCC = BigDecimal.ZERO;
                        if (entry.diemQTrinh == null) entry.diemQTrinh = BigDecimal.ZERO;
                        if (entry.diemThi == null) entry.diemThi = BigDecimal.ZERO;
                        if (entry.diemTK == null) entry.diemTK = BigDecimal.ZERO;
                        if (entry.diemHe4 == null) entry.diemHe4 = BigDecimal.ZERO;
                        if (entry.xepLoai == null) entry.xepLoai = "F";
                        
                        // Determine result status
                        if (entry.diemHe4.compareTo(BigDecimal.ONE) >= 0) {
                            entry.danhGia = "Đạt";
                        } else {
                            entry.danhGia = "Không đạt";
                        }
                        
                        grades.add(entry);
                        
                        // Add to table
                        tableModel.addRow(new Object[] {
                            entry.maMon,
                            entry.tenMon,
                            entry.soTinChi,
                            entry.diemCC,
                            entry.diemQTrinh,
                            entry.diemThi,
                            entry.diemTK,
                            entry.diemHe4,
                            entry.xepLoai,
                            entry.danhGia
                        });
                    }
                    
                    // Calculate GPA and total credits
                    calculateGPA(grades);
                }
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error loading grade data", e);
            JOptionPane.showMessageDialog(this,
                "Không thể tải dữ liệu điểm: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void calculateGPA(List<GradeEntry> grades) {
        BigDecimal totalWeightedGPA = BigDecimal.ZERO;
        int totalCredits = 0;
        
        for (GradeEntry grade : grades) {
            // Only count passing grades
            if (grade.diemHe4.compareTo(BigDecimal.ONE) >= 0) {
                // Calculate weighted GPA (GPA × credits)
                BigDecimal weightedGPA = grade.diemHe4.multiply(BigDecimal.valueOf(grade.soTinChi));
                totalWeightedGPA = totalWeightedGPA.add(weightedGPA);
                totalCredits += grade.soTinChi;
            }
        }
        
        // Calculate average GPA
        BigDecimal averageGPA;
        if (totalCredits > 0) {
            averageGPA = totalWeightedGPA.divide(BigDecimal.valueOf(totalCredits), 2, RoundingMode.HALF_UP);
        } else {
            averageGPA = BigDecimal.ZERO;
        }
        
        // Update labels
        gpaLabel.setText(averageGPA.toString());
        totalCreditsLabel.setText(String.valueOf(totalCredits));
    }
    
    // Helper class to store grade data
    private static class GradeEntry {
        String maMon;
        String tenMon;
        int soTinChi;
        BigDecimal diemCC;
        BigDecimal diemQTrinh;
        BigDecimal diemThi;
        BigDecimal diemTK;
        BigDecimal diemHe4;
        String xepLoai;
        String danhGia;
    }
} 