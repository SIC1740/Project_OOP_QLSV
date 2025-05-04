package com.myuniv.sm.view.admin;

import com.myuniv.sm.model.StudentGPASummary;
import com.myuniv.sm.service.GradeService;
import com.myuniv.sm.service.ServiceException;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Panel for displaying top students eligible for scholarships
 */
public class ScholarshipPanel extends JPanel {
    private static final Logger logger = Logger.getLogger(ScholarshipPanel.class.getName());
    private final GradeService gradeService;
    
    private final DefaultTableModel tableModel;
    private final JTable scholarshipTable;
    private JSpinner percentageSpinner;
    private JLabel resultLabel;
    
    public ScholarshipPanel() {
        this.gradeService = new GradeService();
        
        // Set up layout
        setLayout(new BorderLayout());
        
        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("Danh sách sinh viên được học bổng");
        titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, 16));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Add header to main panel
        add(headerPanel, BorderLayout.NORTH);
        
        // Create table model for scholarship students
        tableModel = new DefaultTableModel(
                new Object[]{"Xếp hạng", "Mã SV", "Họ tên", "Tổng số tín chỉ", "Điểm trung bình (hệ 4)", "Xếp loại"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0 || columnIndex == 3) return Integer.class;
                if (columnIndex == 4) return BigDecimal.class;
                return Object.class;
            }
        };
        
        scholarshipTable = new JTable(tableModel);
        scholarshipTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scholarshipTable.setRowHeight(25);
        scholarshipTable.getTableHeader().setReorderingAllowed(false);
        
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
        
        DefaultTableCellRenderer rankRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                
                // Highlight top 3 students with colors
                if (column == 0) {
                    int rank = (int) value;
                    if (rank == 1) {
                        setBackground(new Color(255, 215, 0, 128)); // Gold
                        setForeground(Color.BLACK);
                    } else if (rank == 2) {
                        setBackground(new Color(192, 192, 192, 128)); // Silver
                        setForeground(Color.BLACK);
                    } else if (rank == 3) {
                        setBackground(new Color(205, 127, 50, 128)); // Bronze
                        setForeground(Color.BLACK);
                    } else {
                        setBackground(table.getBackground());
                        setForeground(table.getForeground());
                    }
                } else {
                    // Apply row's background color to all cells in the row
                    int rank = (int) table.getValueAt(row, 0);
                    if (rank == 1) {
                        setBackground(new Color(255, 215, 0, 128)); // Gold
                    } else if (rank == 2) {
                        setBackground(new Color(192, 192, 192, 128)); // Silver
                    } else if (rank == 3) {
                        setBackground(new Color(205, 127, 50, 128)); // Bronze
                    } else {
                        setBackground(table.getBackground());
                    }
                }
                
                return c;
            }
        };
        
        // Set renderers for all columns
        for (int i = 0; i < scholarshipTable.getColumnCount(); i++) {
            if (i == 4) {
                scholarshipTable.getColumnModel().getColumn(i).setCellRenderer(decimalRenderer);
            } else {
                scholarshipTable.getColumnModel().getColumn(i).setCellRenderer(rankRenderer);
            }
        }
        
        // Set column widths
        scholarshipTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        scholarshipTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        scholarshipTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        scholarshipTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        scholarshipTable.getColumnModel().getColumn(4).setPreferredWidth(140);
        scholarshipTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        
        JScrollPane scrollPane = new JScrollPane(scholarshipTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Create controls panel
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create spinner for percentage selection
        JLabel percentLabel = new JLabel("Phần trăm học bổng:");
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(10, 1, 50, 1);
        percentageSpinner = new JSpinner(spinnerModel);
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(percentageSpinner, "#'%'");
        percentageSpinner.setEditor(editor);
        
        JButton applyButton = new JButton("Áp dụng");
        applyButton.addActionListener(this::updateScholarshipList);
        
        JButton refreshButton = new JButton("Làm mới dữ liệu");
        refreshButton.addActionListener(e -> loadData());
        
        // Result label
        resultLabel = new JLabel("");
        resultLabel.setFont(new Font(resultLabel.getFont().getName(), Font.BOLD, 12));
        
        controlsPanel.add(percentLabel);
        controlsPanel.add(percentageSpinner);
        controlsPanel.add(applyButton);
        controlsPanel.add(refreshButton);
        controlsPanel.add(Box.createHorizontalStrut(20));
        controlsPanel.add(resultLabel);
        
        add(controlsPanel, BorderLayout.SOUTH);
        
        // Load initial data
        loadData();
    }
    
    /**
     * Load student GPA data and update the scholarship table
     */
    private void loadData() {
        try {
            // Get scholarship percentage from spinner
            int percentage = (Integer) percentageSpinner.getValue();
            updateScholarshipTable(percentage);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error loading scholarship data", e);
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi tải dữ liệu học bổng: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Update scholarship list when the percentage changes
     */
    private void updateScholarshipList(ActionEvent e) {
        try {
            int percentage = (Integer) percentageSpinner.getValue();
            updateScholarshipTable(percentage);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error updating scholarship list", ex);
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi cập nhật danh sách học bổng: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Update scholarship table with top N% of students
     */
    private void updateScholarshipTable(int percentage) throws ServiceException {
        // Clear existing data
        tableModel.setRowCount(0);
        
        // Get all students with grades
        Map<String, StudentGPASummary> studentGPAs = gradeService.calculateStudentGPASummary();
        
        if (studentGPAs.isEmpty()) {
            resultLabel.setText("Không có dữ liệu điểm cho sinh viên nào");
            return;
        }
        
        // Convert to list and sort by GPA (descending)
        List<StudentGPASummary> sortedStudents = studentGPAs.values().stream()
                .filter(s -> s.getTotalCredits() > 0) // Only include students with credits
                .sorted((s1, s2) -> s2.getAverageGPA().compareTo(s1.getAverageGPA()))
                .collect(Collectors.toList());
        
        if (sortedStudents.isEmpty()) {
            resultLabel.setText("Không có dữ liệu điểm hợp lệ");
            return;
        }
        
        // Calculate number of students to include (top N%)
        int totalStudents = sortedStudents.size();
        int scholarshipCount = Math.max(1, (int) Math.ceil(totalStudents * percentage / 100.0));
        
        // Get minimum GPA threshold for scholarship
        BigDecimal minGpaThreshold = BigDecimal.ZERO;
        if (scholarshipCount < totalStudents) {
            minGpaThreshold = sortedStudents.get(scholarshipCount - 1).getAverageGPA();
        } else {
            minGpaThreshold = sortedStudents.get(totalStudents - 1).getAverageGPA();
        }
        
        // Add top students to table
        for (int i = 0; i < scholarshipCount; i++) {
            if (i < sortedStudents.size()) {
                StudentGPASummary student = sortedStudents.get(i);
                
                // Only include students with the minimum GPA threshold
                if (student.getAverageGPA().compareTo(minGpaThreshold) >= 0) {
                    tableModel.addRow(new Object[]{
                        i + 1, // Rank
                        student.getMsv(),
                        student.getHoTen(),
                        student.getTotalCredits(),
                        student.getAverageGPA(),
                        getLetterGradeFromGPA(student.getAverageGPA())
                    });
                }
            }
        }
        
        // Update result label
        resultLabel.setText(String.format("Danh sách %d sinh viên được học bổng (top %d%%, điểm trung bình từ %.2f)",
                tableModel.getRowCount(), percentage, minGpaThreshold));
    }
    
    /**
     * Convert GPA to letter grade
     */
    private String getLetterGradeFromGPA(BigDecimal gpa) {
        if (gpa.compareTo(BigDecimal.valueOf(3.7)) >= 0) {
            return "A";
        } else if (gpa.compareTo(BigDecimal.valueOf(3.3)) >= 0) {
            return "B+";
        } else if (gpa.compareTo(BigDecimal.valueOf(3.0)) >= 0) {
            return "B";
        } else if (gpa.compareTo(BigDecimal.valueOf(2.7)) >= 0) {
            return "C+";
        } else if (gpa.compareTo(BigDecimal.valueOf(2.0)) >= 0) {
            return "C";
        } else if (gpa.compareTo(BigDecimal.valueOf(1.7)) >= 0) {
            return "D+";
        } else if (gpa.compareTo(BigDecimal.valueOf(1.0)) >= 0) {
            return "D";
        } else {
            return "F";
        }
    }
} 