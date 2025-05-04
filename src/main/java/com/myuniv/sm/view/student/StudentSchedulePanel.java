package com.myuniv.sm.view.student;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Panel to display student schedules
 */
public class StudentSchedulePanel extends JPanel {
    private final String studentId;
    
    public StudentSchedulePanel(String studentId) {
        this.studentId = studentId;
        
        initUI();
    }
    
    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JLabel titleLabel = new JLabel("Thời khóa biểu", JLabel.CENTER);
        titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, 16));
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        add(titlePanel, BorderLayout.NORTH);
        
        // Placeholder content (will be replaced with actual schedule)
        JPanel placeholderPanel = new JPanel(new BorderLayout());
        placeholderPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        
        JLabel placeholderLabel = new JLabel("Thời khóa biểu sẽ được cập nhật sau", JLabel.CENTER);
        placeholderLabel.setFont(new Font(placeholderLabel.getFont().getName(), Font.ITALIC, 14));
        placeholderLabel.setForeground(Color.GRAY);
        placeholderPanel.add(placeholderLabel, BorderLayout.CENTER);
        
        add(placeholderPanel, BorderLayout.CENTER);
    }
} 