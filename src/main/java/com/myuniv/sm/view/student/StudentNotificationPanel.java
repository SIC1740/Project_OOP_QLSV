package com.myuniv.sm.view.student;

import com.myuniv.sm.model.Notification;
import com.myuniv.sm.service.NotificationService;
import com.myuniv.sm.service.ServiceException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Panel for students to view notifications from their lecturers
 */
public class StudentNotificationPanel extends JPanel {
    private static final Logger logger = Logger.getLogger(StudentNotificationPanel.class.getName());
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    private final String studentId;
    private final NotificationService notificationService;
    
    // UI components
    private JTable notificationsTable;
    private DefaultTableModel tableModel;
    private JTextArea contentArea;
    private JLabel titleLabel;
    private JLabel infoLabel;
    private JLabel dateLabel;
    private JButton refreshButton;
    private JLabel countLabel;
    
    // Data storage
    private List<Notification> notifications;
    
    public StudentNotificationPanel(String studentId) {
        this.studentId = studentId;
        this.notificationService = new NotificationService();
        this.notifications = new ArrayList<>();
        
        initUI();
        loadNotifications();
    }
    
    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Top panel with title and refresh button
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);
        
        // Create a split pane to divide table and content view
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(400);
        splitPane.setOneTouchExpandable(true);
        
        // Left side - Notifications table
        JPanel tablePanel = createTablePanel();
        splitPane.setLeftComponent(tablePanel);
        
        // Right side - Notification content view
        JPanel contentPanel = createContentPanel();
        splitPane.setRightComponent(contentPanel);
        
        add(splitPane, BorderLayout.CENTER);
    }
    
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEtchedBorder(),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Title
        JLabel titleLabel = new JLabel("Thông báo của tôi", SwingConstants.CENTER);
        titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, 16));
        panel.add(titleLabel, BorderLayout.CENTER);
        
        // Refresh button
        refreshButton = new JButton("Làm mới");
        refreshButton.addActionListener(e -> loadNotifications());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(refreshButton);
        
        panel.add(buttonPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        // Create table model with columns
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
        };
        tableModel.addColumn("ID");
        tableModel.addColumn("Tiêu đề");
        tableModel.addColumn("Lớp");
        tableModel.addColumn("Môn học");
        tableModel.addColumn("Giảng viên");
        tableModel.addColumn("Ngày tạo");
        
        // Create table and scroll pane
        notificationsTable = new JTable(tableModel);
        notificationsTable.setRowHeight(25);
        notificationsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        notificationsTable.getTableHeader().setReorderingAllowed(false);
        
        // Hide ID column
        notificationsTable.getColumnModel().getColumn(0).setMinWidth(0);
        notificationsTable.getColumnModel().getColumn(0).setMaxWidth(0);
        notificationsTable.getColumnModel().getColumn(0).setWidth(0);
        
        // Set column widths
        notificationsTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Title
        notificationsTable.getColumnModel().getColumn(2).setPreferredWidth(60);  // Class
        notificationsTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Subject
        notificationsTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Lecturer
        notificationsTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Date
        
        // Add selection listener
        notificationsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = notificationsTable.getSelectedRow();
                if (selectedRow >= 0 && selectedRow < notifications.size()) {
                    displayNotificationContent(notifications.get(selectedRow));
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(notificationsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Add a label showing notification count
        countLabel = new JLabel("Tổng số thông báo: 0");
        countLabel.setBorder(new EmptyBorder(5, 5, 0, 0));
        panel.add(countLabel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Nội dung thông báo"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout(5, 5));
        headerPanel.setOpaque(false);
        
        titleLabel = new JLabel("Chọn một thông báo để xem nội dung");
        titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, 14));
        titleLabel.setForeground(new Color(41, 128, 185)); // Blue shade
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        
        infoLabel = new JLabel("");
        headerPanel.add(infoLabel, BorderLayout.CENTER);
        
        dateLabel = new JLabel("");
        dateLabel.setFont(new Font(dateLabel.getFont().getName(), Font.ITALIC, 11));
        dateLabel.setForeground(Color.GRAY);
        headerPanel.add(dateLabel, BorderLayout.SOUTH);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Content area
        contentArea = new JTextArea();
        contentArea.setEditable(false);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(contentArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void loadNotifications() {
        // Clear table
        tableModel.setRowCount(0);
        notifications.clear();
        
        // Reset content panel
        titleLabel.setText("Chọn một thông báo để xem nội dung");
        infoLabel.setText("");
        dateLabel.setText("");
        contentArea.setText("");
        
        try {
            // Load notifications for this student
            List<Notification> notificationList = notificationService.getNotificationsForStudent(studentId);
            
            for (Notification notification : notificationList) {
                notifications.add(notification);
                
                tableModel.addRow(new Object[]{
                    notification.getThongbaoId(),
                    notification.getTieuDe(),
                    notification.getMaLop(),
                    notification.getTenMon(),
                    notification.getTenGiangVien(),
                    notification.getNgayTao() != null ? 
                        notification.getNgayTao().format(DATE_FORMATTER) : ""
                });
            }
            
            // Update notification count
            countLabel.setText("Tổng số thông báo: " + notifications.size());
            
            // Select first row if available
            if (!notifications.isEmpty()) {
                notificationsTable.setRowSelectionInterval(0, 0);
            }
            
        } catch (ServiceException e) {
            logger.log(Level.SEVERE, "Error loading notifications", e);
            JOptionPane.showMessageDialog(this,
                "Không thể tải danh sách thông báo: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void displayNotificationContent(Notification notification) {
        if (notification == null) {
            return;
        }
        
        // Set title
        titleLabel.setText(notification.getTieuDe());
        
        // Set info (lecturer, class, subject)
        String info = String.format("Giảng viên: %s | Lớp: %s | Môn: %s", 
            notification.getTenGiangVien(), 
            notification.getMaLop(), 
            notification.getTenMon());
        infoLabel.setText(info);
        
        // Set date
        String date = notification.getNgayTao() != null ? 
            "Ngày tạo: " + notification.getNgayTao().format(DATE_FORMATTER) : "";
        dateLabel.setText(date);
        
        // Set content
        contentArea.setText(notification.getNoiDung());
        contentArea.setCaretPosition(0); // Scroll to top
    }
} 