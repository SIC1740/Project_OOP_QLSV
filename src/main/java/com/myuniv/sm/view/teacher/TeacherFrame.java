package com.myuniv.sm.view.teacher;

import com.myuniv.sm.model.Lecturer;
import com.myuniv.sm.model.User;
import com.myuniv.sm.service.LecturerService;
import com.myuniv.sm.service.ServiceException;
import com.myuniv.sm.view.LoginFrame;
import com.myuniv.sm.view.teacher.TeacherClassesPanel;
import com.myuniv.sm.view.teacher.NotificationPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;

public class TeacherFrame extends JFrame {
    private final User currentUser;
    private final LecturerService lecturerService;
    private Lecturer lecturerInfo;
    
    // Panel components
    private JPanel mainPanel;
    private JPanel infoPanel;
    private JLabel idLabel;
    private JLabel nameLabel;
    private JLabel dobLabel;
    private JLabel emailLabel;
    private JLabel phoneLabel;
    private JLabel degreeLabel;
    private JLabel statusLabel;
    private JTabbedPane tabbedPane;
    
    // UI Constants for consistent styling
    private static final Color PRIMARY_COLOR = new Color(155, 89, 182); // Purple shade
    private static final Color ACCENT_COLOR = new Color(52, 152, 219);   // Blue shade
    private static final Color BG_COLOR = new Color(250, 250, 250);     // Light gray
    private static final Color TEXT_COLOR = new Color(44, 62, 80);      // Dark blue/gray
    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font CONTENT_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public TeacherFrame(User user) {
        this.currentUser = user;
        this.lecturerService = new LecturerService();
        
        // Set look and feel to be more modern
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, fall back to default
            System.out.println("Nimbus look and feel not available");
        }
        
        // Load lecturer information based on user
        loadLecturerInfo();
        
        // Initialize UI
        initUI();
    }
    
    private void loadLecturerInfo() {
        try {
            // Assume username is the lecturer ID (ma_giangvien)
            lecturerInfo = lecturerService.getLecturerById(currentUser.getUsername());
        } catch (ServiceException e) {
            // Only show error dialog for real errors, not for missing lecturer
            if (!e.getMessage().contains("Không tìm thấy giảng viên")) {
                JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "Lỗi hệ thống",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Lỗi không xác định khi tải thông tin giảng viên: " + e.getMessage(),
                "Lỗi hệ thống",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void refreshLecturerInfo() {
        loadLecturerInfo();
        updateInfoLabels();
        
        // Update welcome label
        JLabel welcomeLabel = (JLabel) ((JPanel) mainPanel.getComponent(0)).getComponent(0);
        welcomeLabel.setText("Xin chào, " + (lecturerInfo != null ? lecturerInfo.getHoTen() : currentUser.getUsername()));
        
        // Refresh the info panel tab if it exists
        if (tabbedPane.getTabCount() > 0) {
            Component component = tabbedPane.getComponentAt(0);
            if (component instanceof LecturerInfoPanel) {
                tabbedPane.setComponentAt(0, new LecturerInfoPanel(lecturerInfo));
            }
        }
    }
    
    private void updateInfoLabels() {
        // Update the summary info labels
        if (idLabel != null) idLabel.setText(lecturerInfo != null ? lecturerInfo.getMaGiangVien() : "N/A");
        if (nameLabel != null) nameLabel.setText(lecturerInfo != null ? lecturerInfo.getHoTen() : "N/A");
        if (dobLabel != null) dobLabel.setText(lecturerInfo != null && lecturerInfo.getNgaySinh() != null ? 
                lecturerInfo.getNgaySinh().format(DATE_FORMATTER) : "N/A");
        if (emailLabel != null) emailLabel.setText(lecturerInfo != null ? lecturerInfo.getEmail() : "N/A");
        if (phoneLabel != null) phoneLabel.setText(lecturerInfo != null ? lecturerInfo.getSoDienThoai() : "N/A");
        if (degreeLabel != null) degreeLabel.setText(lecturerInfo != null ? lecturerInfo.getHocVi() : "N/A");
    }
    
    private void initUI() {
        setTitle("Hệ thống quản lý sinh viên - Giảng viên");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);
        
        // Create main panel with border layout and styling
        mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(BG_COLOR);
        
        // Create header panel
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Create a split pane for the main content
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(300);
        splitPane.setDividerSize(5);
        splitPane.setOneTouchExpandable(true);
        
        // Create info panel for the left side
        infoPanel = createInfoPanel();
        JScrollPane infoScrollPane = new JScrollPane(infoPanel);
        infoScrollPane.setBorder(null);
        infoScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        // Create tab panel for the right side
        tabbedPane = createTabbedPane();
        
        // Add panels to split pane
        splitPane.setLeftComponent(infoScrollPane);
        splitPane.setRightComponent(tabbedPane);
        
        // Add split pane to main panel
        mainPanel.add(splitPane, BorderLayout.CENTER);
        
        // Add status bar
        JPanel statusBar = createStatusBar();
        mainPanel.add(statusBar, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(0, 0, 10, 0)
        ));
        
        // Left side - welcome message and info
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setOpaque(false);
        
        // Teacher avatar as text icon
        JLabel avatarLabel = new JLabel("👨‍🏫");
        avatarLabel.setFont(new Font(avatarLabel.getFont().getName(), Font.PLAIN, 24));
        leftPanel.add(avatarLabel);
        
        // Welcome message
        JLabel welcomeLabel = new JLabel("Xin chào, " + (lecturerInfo != null ? lecturerInfo.getHoTen() : currentUser.getUsername()));
        welcomeLabel.setFont(HEADER_FONT);
        welcomeLabel.setForeground(PRIMARY_COLOR);
        leftPanel.add(welcomeLabel);
        
        panel.add(leftPanel, BorderLayout.WEST);
        
        // Right side - action buttons
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);
        
        // Refresh button
        JButton refreshButton = new JButton("Làm mới");
        styleButton(refreshButton, ACCENT_COLOR);
        refreshButton.addActionListener(e -> refreshLecturerInfo());
        
        // Logout button
        JButton logoutButton = new JButton("Đăng xuất");
        styleButton(logoutButton, PRIMARY_COLOR);
        logoutButton.addActionListener(e -> logout());
        
        rightPanel.add(refreshButton);
        rightPanel.add(logoutButton);
        
        panel.add(rightPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private void styleButton(JButton button, Color color) {
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font(button.getFont().getName(), Font.BOLD, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
    }
    
    private JPanel createInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 5, 5, 5));
        
        // Lecturer information card
        JPanel infoCard = new JPanel(new GridBagLayout());
        infoCard.setBackground(Color.WHITE);
        infoCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0;
        
        // Card header
        JLabel headerLabel = new JLabel("Thông tin giảng viên");
        headerLabel.setFont(new Font(headerLabel.getFont().getName(), Font.BOLD, 14));
        headerLabel.setForeground(PRIMARY_COLOR);
        headerLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        infoCard.add(headerLabel, gbc);
        
        // Add lecturer information fields with styled labels
        gbc.gridwidth = 1;
        
        // ID
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel idTitleLabel = new JLabel("Mã giảng viên:");
        idTitleLabel.setFont(LABEL_FONT);
        infoCard.add(idTitleLabel, gbc);
        
        gbc.gridx = 1;
        idLabel = new JLabel(lecturerInfo != null ? lecturerInfo.getMaGiangVien() : "N/A");
        idLabel.setFont(CONTENT_FONT);
        infoCard.add(idLabel, gbc);
        
        // Name
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel nameTitleLabel = new JLabel("Họ và tên:");
        nameTitleLabel.setFont(LABEL_FONT);
        infoCard.add(nameTitleLabel, gbc);
        
        gbc.gridx = 1;
        nameLabel = new JLabel(lecturerInfo != null ? lecturerInfo.getHoTen() : "N/A");
        nameLabel.setFont(CONTENT_FONT);
        infoCard.add(nameLabel, gbc);
        
        // Degree
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel degreeTitleLabel = new JLabel("Học vị:");
        degreeTitleLabel.setFont(LABEL_FONT);
        infoCard.add(degreeTitleLabel, gbc);
        
        gbc.gridx = 1;
        degreeLabel = new JLabel(lecturerInfo != null ? lecturerInfo.getHocVi() : "N/A");
        degreeLabel.setFont(CONTENT_FONT);
        infoCard.add(degreeLabel, gbc);
        
        // DOB
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel dobTitleLabel = new JLabel("Ngày sinh:");
        dobTitleLabel.setFont(LABEL_FONT);
        infoCard.add(dobTitleLabel, gbc);
        
        gbc.gridx = 1;
        dobLabel = new JLabel(lecturerInfo != null && lecturerInfo.getNgaySinh() != null ? 
                lecturerInfo.getNgaySinh().format(DATE_FORMATTER) : "N/A");
        dobLabel.setFont(CONTENT_FONT);
        infoCard.add(dobLabel, gbc);
        
        // Email
        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel emailTitleLabel = new JLabel("Email:");
        emailTitleLabel.setFont(LABEL_FONT);
        infoCard.add(emailTitleLabel, gbc);
        
        gbc.gridx = 1;
        emailLabel = new JLabel(lecturerInfo != null ? lecturerInfo.getEmail() : "N/A");
        emailLabel.setFont(CONTENT_FONT);
        infoCard.add(emailLabel, gbc);
        
        // Phone
        gbc.gridx = 0;
        gbc.gridy = 6;
        JLabel phoneTitleLabel = new JLabel("Số điện thoại:");
        phoneTitleLabel.setFont(LABEL_FONT);
        infoCard.add(phoneTitleLabel, gbc);
        
        gbc.gridx = 1;
        phoneLabel = new JLabel(lecturerInfo != null ? lecturerInfo.getSoDienThoai() : "N/A");
        phoneLabel.setFont(CONTENT_FONT);
        infoCard.add(phoneLabel, gbc);
        
        // Make the info card take up the full width but not the full height
        infoCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, infoCard.getPreferredSize().height));
        panel.add(infoCard);
        
        // Add some spacing
        panel.add(Box.createVerticalStrut(15));
        
        // Add quick links panel
        JPanel linksPanel = createQuickLinksPanel();
        linksPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, linksPanel.getPreferredSize().height));
        panel.add(linksPanel);
        
        // Add teaching statistics card
//        JPanel statsPanel = createStatsPanel();
//        statsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, statsPanel.getPreferredSize().height));
//        panel.add(Box.createVerticalStrut(15));
//        panel.add(statsPanel);
        
        // Add a glue to push everything up
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
//    private JPanel createStatsPanel() {
//        JPanel panel = new JPanel();
//        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
//        panel.setBackground(Color.WHITE);
//        panel.setBorder(BorderFactory.createCompoundBorder(
//            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true),
//            BorderFactory.createEmptyBorder(15, 15, 15, 15)
//        ));
//
//        // Stats header
//        JLabel headerLabel = new JLabel("Thống kê giảng dạy");
//        headerLabel.setFont(new Font(headerLabel.getFont().getName(), Font.BOLD, 14));
//        headerLabel.setForeground(PRIMARY_COLOR);
//        headerLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
//        headerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
//        panel.add(headerLabel);
//
//        // Add spacing
//        panel.add(Box.createVerticalStrut(10));
//
//        // Add stats in a grid
//        JPanel statsGrid = new JPanel(new GridLayout(3, 2, 10, 5));
//        statsGrid.setOpaque(false);
//        statsGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
//
//        addStatItem(statsGrid, "Số lớp đang dạy:", "4");
//        addStatItem(statsGrid, "Tổng số sinh viên:", "120");
//        addStatItem(statsGrid, "Số môn phụ trách:", "3");
//
//        panel.add(statsGrid);
//
//        return panel;
//    }
    
    private void addStatItem(JPanel panel, String label, String value) {
        JLabel titleLabel = new JLabel(label);
        titleLabel.setFont(LABEL_FONT);
        panel.add(titleLabel);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(CONTENT_FONT);
        valueLabel.setForeground(ACCENT_COLOR);
        panel.add(valueLabel);
    }
    
    private JPanel createQuickLinksPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Links header
        JLabel headerLabel = new JLabel("Truy cập nhanh");
        headerLabel.setFont(new Font(headerLabel.getFont().getName(), Font.BOLD, 14));
        headerLabel.setForeground(PRIMARY_COLOR);
        headerLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        headerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(headerLabel);
        
        // Add spacing
        panel.add(Box.createVerticalStrut(10));
        
        // Links
        addQuickLink(panel, "Lịch giảng dạy", e -> selectTab(1));
        addQuickLink(panel, "Lớp giảng dạy", e -> selectTab(2));
//        addQuickLink(panel, "Quản lý điểm số", e -> selectTab(3));
        addQuickLink(panel, "Báo cáo thống kê", e -> selectTab(4));
        
        return panel;
    }
    
    private void addQuickLink(JPanel panel, String text, java.awt.event.ActionListener listener) {
        JButton link = new JButton(text);
        link.setHorizontalAlignment(SwingConstants.LEFT);
        link.setBorderPainted(false);
        link.setContentAreaFilled(false);
        link.setForeground(PRIMARY_COLOR);
        link.setCursor(new Cursor(Cursor.HAND_CURSOR));
        link.setFont(new Font(link.getFont().getName(), Font.PLAIN, 12));
        link.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Add hover effect
        link.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                link.setForeground(PRIMARY_COLOR.darker());
                link.setText("➤ " + text);
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                link.setForeground(PRIMARY_COLOR);
                link.setText(text);
            }
        });
        
        link.addActionListener(listener);
        panel.add(link);
        panel.add(Box.createVerticalStrut(5));
    }
    
    private void selectTab(int index) {
        if (index >= 0 && index < tabbedPane.getTabCount()) {
            tabbedPane.setSelectedIndex(index);
        }
    }
    
    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        statusBar.setBackground(new Color(240, 240, 240));
        
        // Current status
        statusLabel = new JLabel("Trạng thái: Đang hoạt động");
        statusLabel.setFont(new Font(statusLabel.getFont().getName(), Font.PLAIN, 11));
        statusBar.add(statusLabel, BorderLayout.WEST);
        
        // Current date/time on the right
        JLabel timeLabel = new JLabel(java.time.LocalDateTime.now().format(
            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        timeLabel.setFont(new Font(timeLabel.getFont().getName(), Font.PLAIN, 11));
        statusBar.add(timeLabel, BorderLayout.EAST);
        
        return statusBar;
    }
    
    private JTabbedPane createTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.setFont(new Font(tabbedPane.getFont().getName(), Font.PLAIN, 12));
        
        // Set tab UI - more modern looking
        tabbedPane.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
            @Override
            protected void installDefaults() {
                super.installDefaults();
                this.lightHighlight = PRIMARY_COLOR;
                this.highlight = PRIMARY_COLOR;
            }
        });
        
        // Add tabs with actual functionality - no icons
        tabbedPane.addTab("Thông tin chi tiết", new LecturerInfoPanel(lecturerInfo));
        // tabbedPane.addTab("Lịch giảng dạy", new TeachingSchedulePanel(currentUser.getUsername()));
        tabbedPane.addTab("Lớp giảng dạy", new TeacherClassesPanel(currentUser.getUsername()));
        tabbedPane.addTab("Thông báo lớp học", new NotificationPanel(currentUser.getUsername()));
        tabbedPane.addTab("Quản lý điểm", createPlaceholderPanel("Quản lý điểm số được thực hiện từ mục Lớp giảng dạy"));
        
        return tabbedPane;
    }
    
    private JPanel createPlaceholderPanel(String message) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        JLabel label = new JLabel(message, SwingConstants.CENTER);
        label.setFont(new Font(label.getFont().getName(), Font.PLAIN, 14));
        label.setForeground(Color.GRAY);
        
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }
    
    private void logout() {
        int choice = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc muốn đăng xuất?",
            "Xác nhận đăng xuất",
            JOptionPane.YES_NO_OPTION
        );
        
        if (choice == JOptionPane.YES_OPTION) {
            dispose(); // Close this frame
            SwingUtilities.invokeLater(() -> 
                new LoginFrame().setVisible(true)
            );
        }
    }
    
    public static void main(String[] args) {
        // For testing only
        User testUser = new User();
        testUser.setUsername("GV001");
        testUser.setRole("giangvien");
        
        SwingUtilities.invokeLater(() -> new TeacherFrame(testUser).setVisible(true));
    }
} 