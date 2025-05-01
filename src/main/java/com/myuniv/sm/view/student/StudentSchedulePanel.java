package com.myuniv.sm.view.student;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Panel to display student class schedule
 */
public class StudentSchedulePanel extends JPanel {
    
    private final String studentId;
    private final JTable scheduleTable;
    
    public StudentSchedulePanel(String studentId) {
        this.studentId = studentId;
        this.scheduleTable = new JTable();
        
        initUI();
        loadScheduleData();
    }
    
    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Add title label
        JLabel titleLabel = new JLabel("Lịch học của sinh viên", SwingConstants.CENTER);
        titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, 16));
        add(titleLabel, BorderLayout.NORTH);
        
        // Create schedule table with fixed column model
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Thứ/Buổi");
        model.addColumn("Sáng");
        model.addColumn("Chiều");
        model.addColumn("Tối");
        scheduleTable.setModel(model);
        
        // Make table look better
        scheduleTable.setRowHeight(40);
        scheduleTable.setShowGrid(true);
        scheduleTable.setGridColor(Color.LIGHT_GRAY);
        scheduleTable.getTableHeader().setReorderingAllowed(false);
        
        // Add table to a scroll pane
        JScrollPane scrollPane = new JScrollPane(scheduleTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Add a filter panel at the top with semester selection
        JPanel filterPanel = createFilterPanel();
        add(filterPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        panel.add(new JLabel("Học kỳ:"));
        
        String[] semesters = {"Học kỳ 1 - Năm 2023-2024", "Học kỳ 2 - Năm 2023-2024"};
        JComboBox<String> comboSemester = new JComboBox<>(semesters);
        panel.add(comboSemester);
        
        JButton btnViewSchedule = new JButton("Xem lịch");
        btnViewSchedule.addActionListener(e -> loadScheduleData());
        panel.add(btnViewSchedule);
        
        return panel;
    }
    
    private void loadScheduleData() {
        // In a real app, this would load data from a database or service
        DefaultTableModel model = (DefaultTableModel) scheduleTable.getModel();
        
        // Clear existing data
        model.setRowCount(0);
        
        // Add sample data for the week
        model.addRow(new Object[]{"Thứ 2", "Toán cao cấp (101-A5)", "Lập trình Java (305-A2)", ""});
        model.addRow(new Object[]{"Thứ 3", "", "Cơ sở dữ liệu (203-A1)", ""});
        model.addRow(new Object[]{"Thứ 4", "Tiếng Anh (105-A3)", "", "Mạng máy tính (301-A1)"});
        model.addRow(new Object[]{"Thứ 5", "", "Hệ điều hành (204-A2)", ""});
        model.addRow(new Object[]{"Thứ 6", "Kỹ thuật lập trình (102-A5)", "", ""});
        model.addRow(new Object[]{"Thứ 7", "", "", ""});
        model.addRow(new Object[]{"Chủ nhật", "", "", ""});
    }
} 