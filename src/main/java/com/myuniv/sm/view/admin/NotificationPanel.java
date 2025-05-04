package com.myuniv.sm.view.admin;

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
 * Panel for admins to manage all notifications in the system
 */
public class NotificationPanel extends JPanel {
    private static final Logger logger = Logger.getLogger(NotificationPanel.class.getName());
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    private final NotificationService notificationService;
    
    // UI components
    private JTable notificationsTable;
    private DefaultTableModel tableModel;
    private JTextArea contentArea;
    private JLabel titleLabel;
    private JLabel infoLabel;
    private JLabel dateLabel;
    private JButton refreshButton;
    private JButton deleteButton;
    private JTextField searchField;
    private JLabel countLabel;
    
    // Data storage
    private List<Notification> notifications;
    private Notification selectedNotification;
    
    public NotificationPanel() {
        this.notificationService = new NotificationService();
        this.notifications = new ArrayList<>();
        
        initUI();
        loadNotifications();
    }
    
    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Top panel with title, search and refresh
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);
        
        // Create a split pane to divide table and content view
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(500);
        splitPane.setOneTouchExpandable(true);
        
        // Left side - Notifications table
        JPanel tablePanel = createTablePanel();
        splitPane.setLeftComponent(tablePanel);
        
        // Right side - Notification content view
        JPanel contentPanel = createContentPanel();
        splitPane.setRightComponent(contentPanel);
        
        add(splitPane, BorderLayout.CENTER);
        
        // Status bar
        JLabel statusLabel = new JLabel("Sẵn sàng");
        statusLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
        add(statusLabel, BorderLayout.SOUTH);
    }
    
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEtchedBorder(),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Title
        JLabel titleLabel = new JLabel("Quản lý thông báo hệ thống", SwingConstants.CENTER);
        titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, 16));
        panel.add(titleLabel, BorderLayout.CENTER);
        
        // Search and Refresh panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controlPanel.setOpaque(false);
        
        // Search field
        searchField = new JTextField(20);
        searchField.setToolTipText("Tìm kiếm theo tiêu đề hoặc nội dung");
        controlPanel.add(new JLabel("Tìm kiếm:"));
        controlPanel.add(searchField);
        
        JButton searchButton = new JButton("Tìm");
        searchButton.addActionListener(e -> searchNotifications());
        controlPanel.add(searchButton);
        
        // Refresh button
        refreshButton = new JButton("Làm mới");
        refreshButton.addActionListener(e -> loadNotifications());
        controlPanel.add(refreshButton);
        
        panel.add(controlPanel, BorderLayout.EAST);
        
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
        notificationsTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Title
        notificationsTable.getColumnModel().getColumn(2).setPreferredWidth(80);  // Class
        notificationsTable.getColumnModel().getColumn(3).setPreferredWidth(120); // Subject
        notificationsTable.getColumnModel().getColumn(4).setPreferredWidth(120); // Lecturer
        notificationsTable.getColumnModel().getColumn(5).setPreferredWidth(120); // Date
        
        // Add selection listener
        notificationsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = notificationsTable.getSelectedRow();
                if (selectedRow >= 0 && selectedRow < notifications.size()) {
                    selectedNotification = notifications.get(selectedRow);
                    displayNotificationContent(selectedNotification);
                    deleteButton.setEnabled(true);
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(notificationsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Add notification count and sort options
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        
        countLabel = new JLabel("Tổng số thông báo: 0");
        countLabel.setBorder(new EmptyBorder(5, 5, 0, 0));
        bottomPanel.add(countLabel, BorderLayout.WEST);
        
        String[] sortOptions = {"Mới nhất", "Cũ nhất", "Theo lớp", "Theo môn học", "Theo giảng viên"};
        JComboBox<String> sortComboBox = new JComboBox<>(sortOptions);
        sortComboBox.addActionListener(e -> sortNotifications(sortComboBox.getSelectedIndex()));
        
        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        sortPanel.add(new JLabel("Sắp xếp:"));
        sortPanel.add(sortComboBox);
        bottomPanel.add(sortPanel, BorderLayout.EAST);
        
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Chi tiết thông báo"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout(5, 5));
        headerPanel.setOpaque(false);
        
        titleLabel = new JLabel("Chọn một thông báo để xem chi tiết");
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
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        deleteButton = new JButton("Xóa thông báo");
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(e -> deleteNotification());
        buttonPanel.add(deleteButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void loadNotifications() {
        // Clear table
        tableModel.setRowCount(0);
        notifications.clear();
        
        // Reset content panel
        titleLabel.setText("Chọn một thông báo để xem chi tiết");
        infoLabel.setText("");
        dateLabel.setText("");
        contentArea.setText("");
        deleteButton.setEnabled(false);
        searchField.setText("");
        
        try {
            // Load all notifications
            List<Notification> notificationList = notificationService.getAllNotifications();
            
            for (Notification notification : notificationList) {
                notifications.add(notification);
                addNotificationToTable(notification);
            }
            
            // Update notification count
            updateNotificationCount();
            
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
    
    private void addNotificationToTable(Notification notification) {
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
    
    private void updateNotificationCount() {
        countLabel.setText("Tổng số thông báo: " + notifications.size());
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
    
    private void searchNotifications() {
        String searchText = searchField.getText().trim().toLowerCase();
        if (searchText.isEmpty()) {
            loadNotifications();
            return;
        }
        
        try {
            // Clear table
            tableModel.setRowCount(0);
            
            List<Notification> filteredList = new ArrayList<>();
            List<Notification> allNotifications = notificationService.getAllNotifications();
            
            for (Notification notification : allNotifications) {
                // Search in title, content, lecturer name, class, subject
                if (notification.getTieuDe().toLowerCase().contains(searchText) ||
                    notification.getNoiDung().toLowerCase().contains(searchText) ||
                    notification.getTenGiangVien().toLowerCase().contains(searchText) ||
                    notification.getMaLop().toLowerCase().contains(searchText) ||
                    notification.getTenMon().toLowerCase().contains(searchText)) {
                    
                    filteredList.add(notification);
                    addNotificationToTable(notification);
                }
            }
            
            // Update list and count
            notifications = filteredList;
            updateNotificationCount();
            
            // Select first row if available
            if (!notifications.isEmpty()) {
                notificationsTable.setRowSelectionInterval(0, 0);
            } else {
                titleLabel.setText("Không tìm thấy thông báo phù hợp");
                infoLabel.setText("");
                dateLabel.setText("");
                contentArea.setText("");
                deleteButton.setEnabled(false);
            }
            
        } catch (ServiceException e) {
            logger.log(Level.SEVERE, "Error searching notifications", e);
            JOptionPane.showMessageDialog(this,
                "Lỗi khi tìm kiếm thông báo: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void sortNotifications(int sortOption) {
        // If no notifications, do nothing
        if (notifications.isEmpty()) {
            return;
        }
        
        // Clear table
        tableModel.setRowCount(0);
        
        // Sort based on option
        switch (sortOption) {
            case 0: // Mới nhất
                notifications.sort((n1, n2) -> n2.getNgayTao().compareTo(n1.getNgayTao()));
                break;
            case 1: // Cũ nhất
                notifications.sort((n1, n2) -> n1.getNgayTao().compareTo(n2.getNgayTao()));
                break;
            case 2: // Theo lớp
                notifications.sort((n1, n2) -> n1.getMaLop().compareTo(n2.getMaLop()));
                break;
            case 3: // Theo môn học
                notifications.sort((n1, n2) -> n1.getTenMon().compareTo(n2.getTenMon()));
                break;
            case 4: // Theo giảng viên
                notifications.sort((n1, n2) -> n1.getTenGiangVien().compareTo(n2.getTenGiangVien()));
                break;
        }
        
        // Refill table
        for (Notification notification : notifications) {
            addNotificationToTable(notification);
        }
        
        // Reselect row
        if (selectedNotification != null) {
            for (int i = 0; i < notifications.size(); i++) {
                if (notifications.get(i).getThongbaoId() == selectedNotification.getThongbaoId()) {
                    notificationsTable.setRowSelectionInterval(i, i);
                    break;
                }
            }
        }
    }
    
    private void deleteNotification() {
        if (selectedNotification == null) {
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn xóa thông báo này?\n" +
            "Tiêu đề: " + selectedNotification.getTieuDe() + "\n" +
            "Giảng viên: " + selectedNotification.getTenGiangVien() + "\n" +
            "Lớp: " + selectedNotification.getMaLop(),
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        try {
            if (notificationService.deleteNotification(selectedNotification.getThongbaoId())) {
                JOptionPane.showMessageDialog(this,
                    "Đã xóa thông báo thành công",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
                
                loadNotifications(); // Reload all notifications
            } else {
                JOptionPane.showMessageDialog(this,
                    "Không thể xóa thông báo",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (ServiceException e) {
            logger.log(Level.SEVERE, "Error deleting notification", e);
            JOptionPane.showMessageDialog(this,
                "Lỗi khi xóa thông báo: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }
} 