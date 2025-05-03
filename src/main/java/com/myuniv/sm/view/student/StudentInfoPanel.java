package com.myuniv.sm.view.student;

import com.myuniv.sm.model.Student;
import com.myuniv.sm.service.ServiceException;
import com.myuniv.sm.service.StudentService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;

/**
 * Panel to display detailed student information
 */
public class StudentInfoPanel extends JPanel {
    
    private final Student student;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    // UI Constants for consistent styling
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185); // Blue shade
    private static final Color ACCENT_COLOR = new Color(39, 174, 96);   // Green shade
    private static final Color BG_COLOR = new Color(247, 249, 249);     // Light gray
    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font CONTENT_FONT = new Font("Segoe UI", Font.PLAIN, 12);

    /**
     * Constructor with student data
     * @param student The student whose information to display
     */
    public StudentInfoPanel(Student student) {
        this.student = student;
        initUI();
    }
    
    /**
     * Constructor that loads student data by ID
     * @param studentId The student ID to look up
     */
    public StudentInfoPanel(String studentId) throws ServiceException {
        StudentService service = new StudentService();
        this.student = service.findByMsv(studentId);
        initUI();
    }
    
    private void initUI() {
        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Header
        JLabel headerLabel = new JLabel("Thông tin chi tiết sinh viên");
        headerLabel.setFont(HEADER_FONT);
        headerLabel.setForeground(PRIMARY_COLOR);
        headerLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        add(headerLabel, BorderLayout.NORTH);
        
        // Main content - grid of labels
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 0.3;
        gbc.weighty = 0.0;
        
        // Có thể nếu student null thì hiện thông báo không có dữ liệu
        if (student == null) {
            JLabel noDataLabel = new JLabel("Không có dữ liệu sinh viên");
            noDataLabel.setFont(CONTENT_FONT);
            noDataLabel.setForeground(Color.RED);
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 2;
            contentPanel.add(noDataLabel, gbc);
        } else {
            // Thông tin cá nhân
            addSectionHeader(contentPanel, "Thông tin cá nhân", gbc, 0);
            
            // Mã sinh viên
            addLabelAndValue(contentPanel, "Mã sinh viên:", student.getMsv(), gbc, 1);
            
            // Họ tên
            addLabelAndValue(contentPanel, "Họ và tên:", student.getHoTen(), gbc, 2);
            
            // Ngày sinh
            String dob = student.getNgaySinh() != null ? student.getNgaySinh().format(DATE_FORMATTER) : "N/A";
            addLabelAndValue(contentPanel, "Ngày sinh:", dob, gbc, 3);
            
            // Thông tin liên hệ
            addSectionHeader(contentPanel, "Thông tin liên hệ", gbc, 4);
            
            // Email
            addLabelAndValue(contentPanel, "Email:", student.getEmail(), gbc, 5);
            
            // Số điện thoại
            addLabelAndValue(contentPanel, "Số điện thoại:", student.getSoDienThoai(), gbc, 6);
            
            // Thông tin học tập
            addSectionHeader(contentPanel, "Thông tin học tập", gbc, 7);
            
            // Mã lớp
            addLabelAndValue(contentPanel, "Mã lớp:", student.getMaLop(), gbc, 8);
        }
        
        // Add a filler at the bottom to push everything up
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        contentPanel.add(Box.createVerticalGlue(), gbc);
        
        // Add content to a scroll pane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void addSectionHeader(JPanel panel, String text, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        
        JLabel headerLabel = new JLabel(text);
        headerLabel.setFont(new Font(LABEL_FONT.getName(), Font.BOLD, 14));
        headerLabel.setForeground(PRIMARY_COLOR);
        headerLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));
        panel.add(headerLabel, gbc);
    }
    
    private void addLabelAndValue(JPanel panel, String labelText, String value, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        
        JLabel label = new JLabel(labelText);
        label.setFont(LABEL_FONT);
        panel.add(label, gbc);
        
        gbc.gridx = 1;
        JLabel valueLabel = new JLabel(value != null ? value : "N/A");
        valueLabel.setFont(CONTENT_FONT);
        panel.add(valueLabel, gbc);
    }
} 