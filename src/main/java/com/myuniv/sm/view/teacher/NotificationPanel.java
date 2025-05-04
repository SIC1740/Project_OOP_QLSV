package com.myuniv.sm.view.teacher;

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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.myuniv.sm.dao.util.DBConnection;

/**
 * Panel for teachers to create and manage notifications for their classes
 */
public class NotificationPanel extends JPanel {
    private static final Logger logger = Logger.getLogger(NotificationPanel.class.getName());
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    private final String lecturerId;
    private final NotificationService notificationService;
    
    // UI components
    private JComboBox<String> classSubjectComboBox;
    private JTable notificationsTable;
    private DefaultTableModel tableModel;
    private JTextField titleField;
    private JTextArea contentArea;
    private JButton createButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JLabel statusLabel;
    
    // Data storage
    private List<GvLopMonItem> gvLopMonItems;
    private List<Notification> notifications;
    private Notification selectedNotification;
    
    public NotificationPanel(String lecturerId) {
        this.lecturerId = lecturerId;
        this.notificationService = new NotificationService();
        this.gvLopMonItems = new ArrayList<>();
        this.notifications = new ArrayList<>();
        
        initUI();
        loadClassesAndSubjects();
    }
    
    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Top panel with title and class selection
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);
        
        // Create a split pane to divide table and edit form
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(500);
        
        // Left side - Notifications table
        JPanel tablePanel = createTablePanel();
        splitPane.setLeftComponent(tablePanel);
        
        // Right side - Form to create/edit notifications
        JPanel formPanel = createFormPanel();
        splitPane.setRightComponent(formPanel);
        
        add(splitPane, BorderLayout.CENTER);
        
        // Status bar at bottom
        statusLabel = new JLabel("Sẵn sàng");
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
        JLabel titleLabel = new JLabel("Quản lý thông báo", SwingConstants.CENTER);
        titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, 16));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Selection panel for class/subject
        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        selectionPanel.add(new JLabel("Lớp và môn học:"));
        classSubjectComboBox = new JComboBox<>();
        classSubjectComboBox.setPreferredSize(new Dimension(300, 25));
        classSubjectComboBox.addActionListener(e -> loadNotifications());
        selectionPanel.add(classSubjectComboBox);
        
        JButton refreshButton = new JButton("Làm mới");
        refreshButton.addActionListener(e -> {
            loadClassesAndSubjects();
            loadNotifications();
        });
        selectionPanel.add(refreshButton);
        
        panel.add(selectionPanel, BorderLayout.CENTER);
        
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
        notificationsTable.getColumnModel().getColumn(3).setPreferredWidth(150); // Subject
        notificationsTable.getColumnModel().getColumn(4).setPreferredWidth(120); // Date
        
        // Add selection listener
        notificationsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = notificationsTable.getSelectedRow();
                if (selectedRow >= 0 && selectedRow < notifications.size()) {
                    selectedNotification = notifications.get(selectedRow);
                    populateForm(selectedNotification);
                    updateButton.setEnabled(true);
                    deleteButton.setEnabled(true);
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(notificationsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Tạo/Chỉnh sửa thông báo"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Form fields
        JPanel formFields = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Title field
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        formFields.add(new JLabel("Tiêu đề:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        titleField = new JTextField(30);
        formFields.add(titleField, gbc);
        
        // Content field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        formFields.add(new JLabel("Nội dung:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        contentArea = new JTextArea(10, 30);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        JScrollPane contentScrollPane = new JScrollPane(contentArea);
        formFields.add(contentScrollPane, gbc);
        
        panel.add(formFields, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        createButton = new JButton("Tạo mới");
        createButton.addActionListener(e -> createNotification());
        buttonPanel.add(createButton);
        
        updateButton = new JButton("Cập nhật");
        updateButton.setEnabled(false);
        updateButton.addActionListener(e -> updateNotification());
        buttonPanel.add(updateButton);
        
        deleteButton = new JButton("Xóa");
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(e -> deleteNotification());
        buttonPanel.add(deleteButton);
        
        JButton clearButton = new JButton("Xóa form");
        clearButton.addActionListener(e -> clearForm());
        buttonPanel.add(clearButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void loadClassesAndSubjects() {
        gvLopMonItems.clear();
        classSubjectComboBox.removeAllItems();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT gvlm.id, gvlm.ma_lop, gvlm.ma_mon, mh.ten_mon " +
                     "FROM GiangVien_Lop_MonHoc gvlm " +
                     "JOIN MonHoc mh ON gvlm.ma_mon = mh.ma_mon " +
                     "WHERE gvlm.ma_giangvien = ? " +
                     "ORDER BY gvlm.ma_lop, mh.ten_mon")) {
            
            pstmt.setString(1, lecturerId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String maLop = rs.getString("ma_lop");
                    String maMon = rs.getString("ma_mon");
                    String tenMon = rs.getString("ten_mon");
                    
                    GvLopMonItem item = new GvLopMonItem(id, maLop, maMon, tenMon);
                    gvLopMonItems.add(item);
                    
                    String displayText = maLop + " - " + maMon + " - " + tenMon;
                    classSubjectComboBox.addItem(displayText);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error loading classes and subjects", e);
            JOptionPane.showMessageDialog(this,
                "Không thể tải danh sách lớp và môn học: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
        
        if (classSubjectComboBox.getItemCount() == 0) {
            classSubjectComboBox.addItem("Không có lớp/môn học nào");
        }
    }
    
    private void loadNotifications() {
        // Clear table
        tableModel.setRowCount(0);
        notifications.clear();
        
        int selectedIndex = classSubjectComboBox.getSelectedIndex();
        if (selectedIndex < 0 || selectedIndex >= gvLopMonItems.size()) {
            return;
        }
        
        GvLopMonItem selectedItem = gvLopMonItems.get(selectedIndex);
        
        try {
            // Load notifications for this lecturer, class, and subject
            List<Notification> notificationList = notificationService.getNotificationsForClassAndSubject(
                selectedItem.maLop, selectedItem.maMon);
            
            for (Notification notification : notificationList) {
                notifications.add(notification);
                
                tableModel.addRow(new Object[]{
                    notification.getThongbaoId(),
                    notification.getTieuDe(),
                    notification.getMaLop(),
                    notification.getTenMon(),
                    notification.getNgayTao() != null ? 
                        notification.getNgayTao().format(DATE_FORMATTER) : ""
                });
            }
            
            updateStatus("Đã tải " + notifications.size() + " thông báo", false);
            
        } catch (ServiceException e) {
            logger.log(Level.SEVERE, "Error loading notifications", e);
            JOptionPane.showMessageDialog(this,
                "Không thể tải danh sách thông báo: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void populateForm(Notification notification) {
        if (notification != null) {
            titleField.setText(notification.getTieuDe());
            contentArea.setText(notification.getNoiDung());
        }
    }
    
    private void clearForm() {
        titleField.setText("");
        contentArea.setText("");
        selectedNotification = null;
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
        notificationsTable.clearSelection();
    }
    
    private void createNotification() {
        int selectedIndex = classSubjectComboBox.getSelectedIndex();
        if (selectedIndex < 0 || selectedIndex >= gvLopMonItems.size()) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng chọn lớp và môn học trước",
                "Lỗi",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        GvLopMonItem selectedItem = gvLopMonItems.get(selectedIndex);
        
        String title = titleField.getText().trim();
        String content = contentArea.getText().trim();
        
        if (title.isEmpty() || content.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng nhập đầy đủ tiêu đề và nội dung",
                "Lỗi",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            Notification notification = new Notification(selectedItem.id, title, content);
            
            if (notificationService.createNotification(notification)) {
                updateStatus("Đã tạo thông báo mới thành công", false);
                clearForm();
                loadNotifications();
            } else {
                updateStatus("Không thể tạo thông báo", true);
            }
        } catch (ServiceException e) {
            logger.log(Level.SEVERE, "Error creating notification", e);
            updateStatus("Lỗi: " + e.getMessage(), true);
            JOptionPane.showMessageDialog(this,
                "Không thể tạo thông báo: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateNotification() {
        if (selectedNotification == null) {
            return;
        }
        
        String title = titleField.getText().trim();
        String content = contentArea.getText().trim();
        
        if (title.isEmpty() || content.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng nhập đầy đủ tiêu đề và nội dung",
                "Lỗi",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            selectedNotification.setTieuDe(title);
            selectedNotification.setNoiDung(content);
            
            if (notificationService.updateNotification(selectedNotification)) {
                updateStatus("Đã cập nhật thông báo thành công", false);
                loadNotifications();
            } else {
                updateStatus("Không thể cập nhật thông báo", true);
            }
        } catch (ServiceException e) {
            logger.log(Level.SEVERE, "Error updating notification", e);
            updateStatus("Lỗi: " + e.getMessage(), true);
            JOptionPane.showMessageDialog(this,
                "Không thể cập nhật thông báo: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteNotification() {
        if (selectedNotification == null) {
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn xóa thông báo này?",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        try {
            if (notificationService.deleteNotification(selectedNotification.getThongbaoId())) {
                updateStatus("Đã xóa thông báo thành công", false);
                clearForm();
                loadNotifications();
            } else {
                updateStatus("Không thể xóa thông báo", true);
            }
        } catch (ServiceException e) {
            logger.log(Level.SEVERE, "Error deleting notification", e);
            updateStatus("Lỗi: " + e.getMessage(), true);
            JOptionPane.showMessageDialog(this,
                "Không thể xóa thông báo: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateStatus(String message, boolean isError) {
        statusLabel.setText(message);
        statusLabel.setForeground(isError ? Color.RED : Color.BLUE);
    }
    
    /**
     * Helper class to store GiangVien_Lop_MonHoc information
     */
    private static class GvLopMonItem {
        int id;
        String maLop;
        String maMon;
        String tenMon;
        
        public GvLopMonItem(int id, String maLop, String maMon, String tenMon) {
            this.id = id;
            this.maLop = maLop;
            this.maMon = maMon;
            this.tenMon = tenMon;
        }
    }
} 