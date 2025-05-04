package com.myuniv.sm.view.student;

import com.myuniv.sm.model.Lecturer;
import com.myuniv.sm.model.Project;
import com.myuniv.sm.model.ProjectRegistrationPeriod;
import com.myuniv.sm.service.LecturerService;
import com.myuniv.sm.service.ProjectService;
import com.myuniv.sm.service.ServiceException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Panel for students to manage and register projects
 */
public class StudentProjectPanel extends JPanel {
    private static final Logger logger = Logger.getLogger(StudentProjectPanel.class.getName());
    
    private final String studentId;
    private final ProjectService projectService;
    private final LecturerService lecturerService;
    
    // UI components
    private JPanel statusPanel;
    private JPanel registrationPanel;
    private JPanel currentProjectPanel;
    private JLabel statusLabel;
    private JTable projectsTable;
    private DefaultTableModel tableModel;
    private JTextField titleField;
    private JComboBox<String> lecturerCombo;
    private JButton registerButton;
    private JButton refreshButton;
    
    // Constants
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    public StudentProjectPanel(String studentId) {
        this.studentId = studentId;
        this.projectService = new ProjectService();
        this.lecturerService = new LecturerService();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Create UI components
        initComponents();
        
        // Load data
        loadData();
    }
    
    private void initComponents() {
        // Top panel - Status
        statusPanel = createStatusPanel();
        add(statusPanel, BorderLayout.NORTH);
        
        // Center panel - Project details or registration form
        JPanel centerPanel = new JPanel(new BorderLayout());
        
        // Initially create both panels, but only show the appropriate one based on current status
        currentProjectPanel = createCurrentProjectPanel();
        registrationPanel = createRegistrationPanel();
        
        centerPanel.add(currentProjectPanel, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
    }
    
    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), 
                "Trạng thái đăng ký đồ án",
                TitledBorder.LEFT,
                TitledBorder.TOP
            ),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        statusLabel = new JLabel("Đang kiểm tra trạng thái...");
        statusLabel.setFont(new Font(statusLabel.getFont().getName(), Font.BOLD, 12));
        panel.add(statusLabel, BorderLayout.CENTER);
        
        refreshButton = new JButton("Làm mới");
        refreshButton.addActionListener(e -> loadData());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(refreshButton);
        panel.add(buttonPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createCurrentProjectPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), 
                "Đồ án của bạn",
                TitledBorder.LEFT,
                TitledBorder.TOP
            ),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Table for existing projects
        String[] columns = {"Đề tài", "Giảng viên hướng dẫn", "Ngày đăng ký"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        projectsTable = new JTable(tableModel);
        projectsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        projectsTable.getTableHeader().setReorderingAllowed(false);
        projectsTable.setRowHeight(30);
        
        // Adjust column widths
        projectsTable.getColumnModel().getColumn(0).setPreferredWidth(250);
        projectsTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        projectsTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        
        JScrollPane scrollPane = new JScrollPane(projectsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JLabel noteLabel = new JLabel(
            "<html><i>Ghi chú: Sau khi đăng ký, đồ án sẽ được lưu vào hệ thống. " +
            "Nếu muốn thay đổi thông tin, vui lòng liên hệ quản trị viên.</i></html>"
        );
        noteLabel.setForeground(Color.GRAY);
        noteLabel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        panel.add(noteLabel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createRegistrationPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), 
                "Đăng ký đồ án mới",
                TitledBorder.LEFT,
                TitledBorder.TOP
            ),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Tên đề tài:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        titleField = new JTextField(20);
        formPanel.add(titleField, gbc);
        
        // Lecturer
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Giảng viên hướng dẫn:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        lecturerCombo = new JComboBox<>();
        
        // Add a placeholder item
        lecturerCombo.addItem("-- Chọn giảng viên --");
        
        // Load lecturer data
        try {
            lecturerService.findAll().forEach(lecturer -> {
                String item = lecturer.getMaGiangVien() + " - " + lecturer.getHoTen() + 
                              (lecturer.getHocVi() != null ? " (" + lecturer.getHocVi() + ")" : "");
                lecturerCombo.addItem(item);
            });
        } catch (ServiceException e) {
            logger.log(Level.SEVERE, "Error loading lecturers for combo box", e);
        }
        
        formPanel.add(lecturerCombo, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        registerButton = new JButton("Đăng ký đồ án");
        registerButton.addActionListener(e -> registerProject());
        buttonPanel.add(registerButton);
        
        // Add panels
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void loadData() {
        try {
            // Load current registration status
            ProjectRegistrationPeriod period = projectService.getCurrentRegistrationPeriod();
            boolean isRegistrationOpen = projectService.isRegistrationOpen();
            
            // Load student's projects
            List<Project> projects = projectService.findProjectsByStudent(studentId);
            
            // Update UI based on data
            updateStatusPanel(period, isRegistrationOpen);
            updateProjectTable(projects);
            
            // Update which panel to show
            Container parentContainer = currentProjectPanel.getParent();
            parentContainer.removeAll();
            currentProjectPanel.removeAll();   // xóa hết child components
            currentProjectPanel.revalidate();  // cập nhật layout
            currentProjectPanel.repaint();     // vẽ lại panel
            
            if (!projects.isEmpty()) {
                // Student already has a project, show the current project panel
                parentContainer.add(currentProjectPanel, BorderLayout.CENTER);
            } else if (isRegistrationOpen) {
                // No project yet and registration is open, show registration form
                parentContainer.add(registrationPanel, BorderLayout.CENTER);
            } else {
                // No project and registration closed, show message
                JPanel messagePanel = new JPanel(new BorderLayout());
                messagePanel.setBorder(new EmptyBorder(20, 20, 20, 20));
                
                JLabel messageLabel = new JLabel(
                    "<html><div style='text-align:center;'>" +
                    "<h3>Đăng ký đồ án hiện đang đóng</h3>" +
                    "<p>Bạn chưa đăng ký đồ án và hiện tại không có đợt đăng ký nào đang mở.</p>" +
                    "<p>Vui lòng chờ đến đợt đăng ký tiếp theo hoặc liên hệ quản trị viên để biết thêm chi tiết.</p>" +
                    "</div></html>"
                );
                messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
                messagePanel.add(messageLabel, BorderLayout.CENTER);
                
                parentContainer.add(messagePanel, BorderLayout.CENTER);
            }
            
            // Refresh the UI
            parentContainer.revalidate();
            parentContainer.repaint();
            
        } catch (ServiceException e) {
            logger.log(Level.SEVERE, "Error loading project data", e);
            statusLabel.setText("Lỗi khi tải dữ liệu: " + e.getMessage());
            statusLabel.setForeground(Color.RED);
        }
    }
    
    private void updateStatusPanel(ProjectRegistrationPeriod period, boolean isRegistrationOpen) {
        if (period == null) {
            statusLabel.setText("Trạng thái: Chưa có đợt đăng ký nào");
            statusLabel.setForeground(Color.BLACK);
        } else {
            String status = period.getStatusDescription();
            
            if (isRegistrationOpen) {
                statusLabel.setText("Trạng thái: Đang mở đăng ký - Kết thúc: " + 
                                    period.getEndTime().format(DATETIME_FORMATTER));
                statusLabel.setForeground(new Color(0, 128, 0)); // Dark green
            } else {
                statusLabel.setText("Trạng thái: " + status);
                statusLabel.setForeground(Color.RED);
            }
        }
    }
    
    private void updateProjectTable(List<Project> projects) {
        // Clear the table
        tableModel.setRowCount(0);
        
        for (Project project : projects) {
            Object[] row = {
                project.getTenDeTai(),
                project.getTenGiangVien(),
                project.getNgayDangKy() != null ? project.getNgayDangKy().format(DATE_FORMATTER) : ""
            };
            tableModel.addRow(row);
        }
    }
    
    private void registerProject() {
        // Validate input
        String title = titleField.getText().trim();
        String selectedLecturer = (String) lecturerCombo.getSelectedItem();
        
        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Vui lòng nhập tên đề tài",
                "Lỗi",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        
        if (selectedLecturer == null || selectedLecturer.equals("-- Chọn giảng viên --")) {
            JOptionPane.showMessageDialog(
                this,
                "Vui lòng chọn giảng viên hướng dẫn",
                "Lỗi",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        
        // Extract lecturer ID
        String lecturerId = selectedLecturer.split(" - ")[0];
        
        // Create project object
        Project project = new Project();
        project.setMsv(studentId);
        project.setTenDeTai(title);
        project.setMaGiangvien(lecturerId);
        
        try {
            // Check if registration is open
            if (!projectService.isRegistrationOpen()) {
                JOptionPane.showMessageDialog(
                    this,
                    "Đăng ký đồ án hiện đang đóng",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE
                );
                loadData(); // Refresh UI in case the status changed
                return;
            }
            
            // Register the project
            boolean success = projectService.registerProject(project);
            
            if (success) {
                JOptionPane.showMessageDialog(
                    this,
                    "Đăng ký đồ án thành công",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE
                );
                loadData(); // Refresh data to show new project
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "Đăng ký đồ án không thành công. Vui lòng thử lại sau.",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(
                this,
                "Lỗi khi đăng ký đồ án: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE
            );
            logger.log(Level.SEVERE, "Error registering project", e);
        }
    }
} 