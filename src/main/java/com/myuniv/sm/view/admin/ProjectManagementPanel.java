package com.myuniv.sm.view.admin;

import com.myuniv.sm.model.Project;
import com.myuniv.sm.model.ProjectRegistrationPeriod;
import com.myuniv.sm.service.ProjectService;
import com.myuniv.sm.service.LecturerService;
import com.myuniv.sm.service.ServiceException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Panel for managing student projects
 */
public class ProjectManagementPanel extends JPanel {
    private static final Logger logger = Logger.getLogger(ProjectManagementPanel.class.getName());
    
    private final ProjectService projectService;
    private final LecturerService lecturerService;
    
    // UI components
    private JTable projectsTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private JButton startRegButton;
    private JButton endRegButton;
    private JTextField searchField;
    private JLabel statusLabel;
    private JSpinner durationSpinner;
    private JTextField periodDescField;
    
    // Constants
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    public ProjectManagementPanel() {
        projectService = new ProjectService();
        lecturerService = new LecturerService();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Create components
        initComponents();
        
        // Load data
        loadProjects();
        updateRegistrationStatus();
    }
    
    private void initComponents() {
        // Top panel with registration controls
        JPanel topPanel = createRegistrationPanel();
        add(topPanel, BorderLayout.NORTH);
        
        // Center panel with projects table
        JPanel centerPanel = createProjectsPanel();
        add(centerPanel, BorderLayout.CENTER);
        
        // Bottom panel with action buttons
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createRegistrationPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), 
                "Quản lý đợt đăng ký đồ án",
                TitledBorder.LEFT,
                TitledBorder.TOP
            ),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Status section
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusLabel = new JLabel("Trạng thái: Chưa có đợt đăng ký nào");
        statusLabel.setFont(new Font(statusLabel.getFont().getName(), Font.BOLD, 12));
        statusPanel.add(statusLabel);
        
        panel.add(statusPanel, BorderLayout.NORTH);
        
        // Controls section
        JPanel controlsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Description
        gbc.gridx = 0;
        gbc.gridy = 0;
        controlsPanel.add(new JLabel("Mô tả:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        periodDescField = new JTextField("Đợt đăng ký đồ án học kỳ hiện tại", 20);
        controlsPanel.add(periodDescField, gbc);
        
        // Duration
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        controlsPanel.add(new JLabel("Thời gian (giờ):"), gbc);
        
        gbc.gridx = 1;
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(24, 1, 168, 1); // 1 hour to 7 days
        durationSpinner = new JSpinner(spinnerModel);
        controlsPanel.add(durationSpinner, gbc);
        
        // Buttons
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        
        startRegButton = new JButton("Mở đăng ký");
        startRegButton.addActionListener(e -> startRegistration());
        btnPanel.add(startRegButton);
        
        endRegButton = new JButton("Đóng đăng ký");
        endRegButton.addActionListener(e -> endRegistration());
        endRegButton.setEnabled(false);
        btnPanel.add(endRegButton);
        
        controlsPanel.add(btnPanel, gbc);
        
        panel.add(controlsPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createProjectsPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), 
                "Danh sách đồ án",
                TitledBorder.LEFT,
                TitledBorder.TOP
            ),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Tìm kiếm:"));
        searchField = new JTextField(20);
        searchField.addActionListener(e -> filterProjects());
        searchPanel.add(searchField);
        
        JButton searchButton = new JButton("Tìm");
        searchButton.addActionListener(e -> filterProjects());
        searchPanel.add(searchButton);
        
        refreshButton = new JButton("Làm mới");
        refreshButton.addActionListener(e -> {
            searchField.setText("");
            loadProjects();
        });
        searchPanel.add(refreshButton);
        
        panel.add(searchPanel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"ID", "Mã SV", "Tên sinh viên", "Tên đề tài", "Giảng viên hướng dẫn", "Ngày đăng ký"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        projectsTable = new JTable(tableModel);
        projectsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        projectsTable.setAutoCreateRowSorter(true);
        projectsTable.getTableHeader().setReorderingAllowed(false);
        projectsTable.setRowHeight(25);
        
        // Adjust column widths
        projectsTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        projectsTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        projectsTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        projectsTable.getColumnModel().getColumn(3).setPreferredWidth(250);
        projectsTable.getColumnModel().getColumn(4).setPreferredWidth(150);
        projectsTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        
        JScrollPane scrollPane = new JScrollPane(projectsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        
        addButton = new JButton("Thêm đồ án");
        addButton.addActionListener(e -> showAddEditDialog(null));
        panel.add(addButton);
        
        editButton = new JButton("Sửa đồ án");
        editButton.addActionListener(e -> editSelectedProject());
        panel.add(editButton);
        
        deleteButton = new JButton("Xóa đồ án");
        deleteButton.addActionListener(e -> deleteSelectedProject());
        panel.add(deleteButton);
        
        return panel;
    }
    
    private void loadProjects() {
        // Clear the table
        tableModel.setRowCount(0);
        
        try {
            List<Project> projects = projectService.findAllProjects();
            
            for (Project project : projects) {
                Object[] row = {
                    project.getDoanId(),
                    project.getMsv(),
                    project.getTenSinhVien(),
                    project.getTenDeTai(),
                    project.getTenGiangVien(),
                    project.getNgayDangKy() != null ? project.getNgayDangKy().format(DATE_FORMATTER) : ""
                };
                tableModel.addRow(row);
            }
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(
                this,
                "Lỗi khi tải danh sách đồ án: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE
            );
            logger.log(Level.SEVERE, "Error loading projects", e);
        }
    }
    
    private void filterProjects() {
        String searchText = searchField.getText().toLowerCase().trim();
        
        if (searchText.isEmpty()) {
            loadProjects();
            return;
        }
        
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        projectsTable.setRowSorter(sorter);
        
        RowFilter<DefaultTableModel, Object> rowFilter = new RowFilter<>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Object> entry) {
                for (int i = 0; i < entry.getValueCount(); i++) {
                    if (entry.getStringValue(i).toLowerCase().contains(searchText)) {
                        return true;
                    }
                }
                return false;
            }
        };
        
        sorter.setRowFilter(rowFilter);
    }
    
    private void startRegistration() {
        try {
            String description = periodDescField.getText().trim();
            if (description.isEmpty()) {
                JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng nhập mô tả cho đợt đăng ký",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            
            int durationHours = (Integer) durationSpinner.getValue();
            
            // Confirm
            int choice = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn mở đợt đăng ký mới?\n" +
                "Thời gian: " + durationHours + " giờ\n" +
                "Mô tả: " + description,
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
            );
            
            if (choice == JOptionPane.YES_OPTION) {
                ProjectRegistrationPeriod period = projectService.startRegistrationPeriod(description, durationHours);
                updateRegistrationStatus();
                
                JOptionPane.showMessageDialog(
                    this,
                    "Đã mở đợt đăng ký đồ án thành công.\n" +
                    "Thời gian kết thúc: " + period.getEndTime().format(DATETIME_FORMATTER),
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(
                this,
                "Lỗi khi mở đợt đăng ký: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE
            );
            logger.log(Level.SEVERE, "Error starting registration period", e);
        }
    }
    
    private void endRegistration() {
        try {
            // Confirm
            int choice = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn đóng đợt đăng ký hiện tại?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
            );
            
            if (choice == JOptionPane.YES_OPTION) {
                boolean success = projectService.endRegistrationPeriod();
                
                if (success) {
                    updateRegistrationStatus();
                    
                    JOptionPane.showMessageDialog(
                        this,
                        "Đã đóng đợt đăng ký đồ án thành công.",
                        "Thành công",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                } else {
                    JOptionPane.showMessageDialog(
                        this,
                        "Không thể đóng đợt đăng ký. Vui lòng thử lại sau.",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(
                this,
                "Lỗi khi đóng đợt đăng ký: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE
            );
            logger.log(Level.SEVERE, "Error ending registration period", e);
        }
    }
    
    private void updateRegistrationStatus() {
        try {
            ProjectRegistrationPeriod period = projectService.getCurrentRegistrationPeriod();
            
            if (period == null) {
                statusLabel.setText("Trạng thái: Chưa có đợt đăng ký nào");
                statusLabel.setForeground(Color.BLACK);
                startRegButton.setEnabled(true);
                endRegButton.setEnabled(false);
            } else {
                boolean isOpen = period.isRegistrationOpen();
                String status = period.getStatusDescription();
                LocalDateTime endTime = period.getEndTime();
                
                statusLabel.setText("Trạng thái: " + status + " - Kết thúc: " + 
                                     endTime.format(DATETIME_FORMATTER));
                
                if (isOpen) {
                    statusLabel.setForeground(new Color(0, 128, 0)); // Dark green
                    startRegButton.setEnabled(false);
                    endRegButton.setEnabled(true);
                } else {
                    statusLabel.setForeground(Color.RED);
                    startRegButton.setEnabled(true);
                    endRegButton.setEnabled(false);
                }
            }
        } catch (ServiceException e) {
            statusLabel.setText("Trạng thái: Lỗi khi kiểm tra - " + e.getMessage());
            statusLabel.setForeground(Color.RED);
            logger.log(Level.SEVERE, "Error updating registration status", e);
            
            startRegButton.setEnabled(true);
            endRegButton.setEnabled(false);
        }
    }
    
    private void editSelectedProject() {
        int selectedRow = projectsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Vui lòng chọn đồ án để sửa",
                "Thông báo",
                JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }
        
        int modelRow = projectsTable.convertRowIndexToModel(selectedRow);
        int projectId = (Integer) tableModel.getValueAt(modelRow, 0);
        
        try {
            Project project = projectService.getProjectById(projectId);
            showAddEditDialog(project);
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(
                this,
                "Lỗi khi lấy thông tin đồ án: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE
            );
            logger.log(Level.SEVERE, "Error getting project for editing", e);
        }
    }
    
    private void deleteSelectedProject() {
        int selectedRow = projectsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Vui lòng chọn đồ án để xóa",
                "Thông báo",
                JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }
        
        int modelRow = projectsTable.convertRowIndexToModel(selectedRow);
        int projectId = (Integer) tableModel.getValueAt(modelRow, 0);
        String msv = (String) tableModel.getValueAt(modelRow, 1);
        String tenDeTai = (String) tableModel.getValueAt(modelRow, 3);
        
        // Confirm deletion
        int choice = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc chắn muốn xóa đồ án này?\n" +
            "Mã SV: " + msv + "\n" +
            "Đề tài: " + tenDeTai,
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (choice == JOptionPane.YES_OPTION) {
            try {
                boolean success = projectService.deleteProject(projectId);
                
                if (success) {
                    JOptionPane.showMessageDialog(
                        this,
                        "Đã xóa đồ án thành công",
                        "Thành công",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    loadProjects();
                } else {
                    JOptionPane.showMessageDialog(
                        this,
                        "Không thể xóa đồ án. Vui lòng thử lại sau.",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            } catch (ServiceException e) {
                JOptionPane.showMessageDialog(
                    this,
                    "Lỗi khi xóa đồ án: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
                );
                logger.log(Level.SEVERE, "Error deleting project", e);
            }
        }
    }
    
    private void showAddEditDialog(Project project) {
        // Create a dialog for adding/editing projects
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                                      project == null ? "Thêm đồ án mới" : "Sửa đồ án", 
                                      true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Student ID
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Mã sinh viên:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JTextField msvField = new JTextField(20);
        if (project != null) {
            msvField.setText(project.getMsv());
            msvField.setEditable(false); // Can't change student for existing project
        }
        formPanel.add(msvField, gbc);
        
        // Project title
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Tên đề tài:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JTextField titleField = new JTextField(20);
        if (project != null) {
            titleField.setText(project.getTenDeTai());
        }
        formPanel.add(titleField, gbc);
        
        // Lecturer
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Giảng viên hướng dẫn:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JComboBox<String> lecturerCombo = new JComboBox<>();
        
        // Add a placeholder item
        lecturerCombo.addItem("-- Chọn giảng viên --");
        
        // Load lecturer data
        try {
            lecturerService.findAll().forEach(lecturer -> {
                String item = lecturer.getMaGiangVien() + " - " + lecturer.getHoTen();
                lecturerCombo.addItem(item);
                
                // If editing, select the current lecturer
                if (project != null && project.getMaGiangvien() != null &&
                    project.getMaGiangvien().equals(lecturer.getMaGiangVien())) {
                    lecturerCombo.setSelectedItem(item);
                }
            });
        } catch (ServiceException e) {
            logger.log(Level.SEVERE, "Error loading lecturers for combo box", e);
        }
        
        formPanel.add(lecturerCombo, gbc);
        
        // Registration date
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Ngày đăng ký:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JTextField dateField = new JTextField(20);
        dateField.setEditable(false);
        
        if (project != null && project.getNgayDangKy() != null) {
            dateField.setText(project.getNgayDangKy().format(DATE_FORMATTER));
        } else {
            dateField.setText(LocalDate.now().format(DATE_FORMATTER));
        }
        formPanel.add(dateField, gbc);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton saveButton = new JButton("Lưu");
        saveButton.addActionListener(e -> {
            // Validate inputs
            String msv = msvField.getText().trim();
            String title = titleField.getText().trim();
            String selectedLecturer = (String) lecturerCombo.getSelectedItem();
            
            if (msv.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập mã sinh viên", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập tên đề tài", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Extract lecturer ID from selected item
            String lecturerId = null;
            if (selectedLecturer != null && !selectedLecturer.equals("-- Chọn giảng viên --")) {
                lecturerId = selectedLecturer.split(" - ")[0];
            }
            
            try {
                Project projectToSave;
                
                if (project == null) {
                    // Creating new project
                    projectToSave = new Project();
                    projectToSave.setMsv(msv);
                    projectToSave.setNgayDangKy(LocalDate.now());
                } else {
                    // Updating existing project
                    projectToSave = project;
                }
                
                projectToSave.setTenDeTai(title);
                projectToSave.setMaGiangvien(lecturerId);
                
                boolean success;
                if (project == null) {
                    // Use standard save for admin (bypassing registration checks)
                    success = projectService.saveProject(projectToSave);
                } else {
                    success = projectService.updateProject(projectToSave);
                }
                
                if (success) {
                    JOptionPane.showMessageDialog(
                        dialog,
                        "Đã lưu đồ án thành công",
                        "Thành công",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    dialog.dispose();
                    loadProjects();
                } else {
                    JOptionPane.showMessageDialog(
                        dialog,
                        "Không thể lưu đồ án. Vui lòng thử lại sau.",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            } catch (ServiceException ex) {
                JOptionPane.showMessageDialog(
                    dialog,
                    "Lỗi khi lưu đồ án: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
                );
                logger.log(Level.SEVERE, "Error saving project", ex);
            }
        });
        
        JButton cancelButton = new JButton("Hủy");
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
} 