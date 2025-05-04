package com.myuniv.sm.view.admin;

import com.myuniv.sm.model.Grade;
import com.myuniv.sm.model.StudentGPASummary;
import com.myuniv.sm.model.Subject;
import com.myuniv.sm.service.GradeService;
import com.myuniv.sm.service.ServiceException;
import com.myuniv.sm.service.SubjectService;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Panel for managing student grades by subject and class
 */
public class GradePanel extends JPanel {
    private static final Logger logger = Logger.getLogger(GradePanel.class.getName());
    
    private final GradeService gradeService;
    private final SubjectService subjectService;
    
    private JTree navigationTree;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode rootNode;
    
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private final String EMPTY_PANEL = "EMPTY_PANEL";
    private final String GPA_SUMMARY_PANEL = "GPA_SUMMARY_PANEL";
    private final String ENTRY_PERIODS_PANEL = "ENTRY_PERIODS_PANEL";
    
    private String adminUsername;
    private GradeEntryPeriodsPanel entryPeriodsPanel;
    
    public GradePanel(String adminUsername) {
        this.adminUsername = adminUsername;
        this.gradeService = new GradeService();
        this.subjectService = new SubjectService();
        
        // Set up the main panel with split pane layout
        setLayout(new BorderLayout());
        
        // Create split pane for navigation and content
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(250);
        
        // Set up navigation tree in the left panel
        JPanel navigationPanel = new JPanel(new BorderLayout());
        rootNode = new DefaultMutableTreeNode("Điểm số môn học");
        treeModel = new DefaultTreeModel(rootNode);
        navigationTree = new JTree(treeModel);
        navigationTree.setRootVisible(true);
        navigationTree.addTreeSelectionListener(this::onTreeNodeSelected);
        
        JScrollPane treeScrollPane = new JScrollPane(navigationTree);
        navigationPanel.add(treeScrollPane, BorderLayout.CENTER);
        
        // Set up content panel in the right with CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        
        // Add an empty panel as default
        JPanel emptyPanel = new JPanel(new BorderLayout());
        JLabel emptyLabel = new JLabel("Chọn một lớp học để xem điểm", SwingConstants.CENTER);
        emptyLabel.setFont(new Font(emptyLabel.getFont().getName(), Font.BOLD, 14));
        emptyPanel.add(emptyLabel, BorderLayout.CENTER);
        contentPanel.add(emptyPanel, EMPTY_PANEL);
        
        // Create and add entry periods panel
        entryPeriodsPanel = new GradeEntryPeriodsPanel(adminUsername);
        contentPanel.add(entryPeriodsPanel, ENTRY_PERIODS_PANEL);
        
        // Show empty panel by default
        cardLayout.show(contentPanel, EMPTY_PANEL);
        
        // Add panels to split pane
        splitPane.setLeftComponent(navigationPanel);
        splitPane.setRightComponent(contentPanel);
        
        // Add split pane to main panel
        add(splitPane, BorderLayout.CENTER);
        
        // Add button panel at the bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshButton = new JButton("Làm mới dữ liệu");
        refreshButton.addActionListener(e -> loadData());
        buttonPanel.add(refreshButton);
        
        // Add GPA summary button
        JButton gpaButton = new JButton("Xem điểm trung bình sinh viên");
        gpaButton.addActionListener(e -> showStudentGPASummary());
        buttonPanel.add(gpaButton);
        
        // Add entry periods management button
        JButton entryPeriodsButton = new JButton("Quản lý thời gian nhập điểm cho giảng viên");
        entryPeriodsButton.addActionListener(e -> showEntryPeriodsPanel());
        buttonPanel.add(entryPeriodsButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Load data into the tree
        loadData();
    }
    
    /**
     * Load data for the navigation tree
     */
    private void loadData() {
        // Clear existing tree
        rootNode.removeAllChildren();
        
        try {
            // Get list of subjects
            List<Subject> subjects = subjectService.findAll();
            
            for (Subject subject : subjects) {
                DefaultMutableTreeNode subjectNode = new DefaultMutableTreeNode(subject);
                rootNode.add(subjectNode);
                
                // Get list of classes for this subject that have grades
                try {
                    List<String> classIds = gradeService.getClassesForSubjectWithGrades(subject.getMaMon());
                    
                    for (String classId : classIds) {
                        DefaultMutableTreeNode classNode = new DefaultMutableTreeNode(
                                new ClassNodeData(classId, subject.getMaMon()));
                        subjectNode.add(classNode);
                    }
                } catch (ServiceException e) {
                    // Log error but continue with other subjects
                    logger.log(Level.WARNING, "Error loading classes for subject: " + subject.getMaMon(), e);
                }
            }
            
            // Refresh tree and expand root
            treeModel.reload();
            navigationTree.expandPath(new TreePath(rootNode.getPath()));
            
        } catch (ServiceException e) {
            logger.log(Level.SEVERE, "Error loading subjects", e);
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi tải danh sách môn học: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Handle tree node selection
     */
    private void onTreeNodeSelected(TreeSelectionEvent event) {
        TreePath path = event.getPath();
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
        
        // Show empty panel by default
        cardLayout.show(contentPanel, EMPTY_PANEL);
        
        if (selectedNode == null) return;
        
        Object userObject = selectedNode.getUserObject();
        
        // If a class node is selected, show grades for that class and subject
        if (userObject instanceof ClassNodeData) {
            ClassNodeData classData = (ClassNodeData) userObject;
            showGradesForClassAndSubject(classData.getMaLop(), classData.getMaMon());
        }
    }
    
    /**
     * Display grades for a specific class and subject
     */
    private void showGradesForClassAndSubject(String maLop, String maMon) {
        try {
            // Create panel ID based on class and subject
            String panelId = "grades_" + maMon + "_" + maLop;
            
            // Check if panel already exists
            Component existingPanel = findPanelById(panelId);
            if (existingPanel != null) {
                cardLayout.show(contentPanel, panelId);
                return;
            }
            
            // Create new panel to display grades
            GradeClassPanel gradeClassPanel = new GradeClassPanel(maMon, maLop, adminUsername);
            contentPanel.add(gradeClassPanel, panelId);
            cardLayout.show(contentPanel, panelId);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error showing grades for class " + maLop, e);
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi hiển thị điểm cho lớp: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Show summary of student GPAs across all subjects
     */
    private void showStudentGPASummary() {
        try {
            // Check if panel already exists
            Component existingPanel = findPanelById(GPA_SUMMARY_PANEL);
            if (existingPanel != null) {
                // If exists, refresh its data
                if (existingPanel instanceof JPanel) {
                    contentPanel.remove(existingPanel);
                }
            }
            
            // Create a new panel with student GPA summary
            JPanel summaryPanel = new JPanel(new BorderLayout());
            
            // Header for the panel
            JPanel headerPanel = new JPanel(new BorderLayout());
            JLabel titleLabel = new JLabel("Điểm trung bình sinh viên (tất cả môn học)");
            titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, 16));
            titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            headerPanel.add(titleLabel, BorderLayout.WEST);
            
            summaryPanel.add(headerPanel, BorderLayout.NORTH);
            
            // Create table model for GPA summary
            DefaultTableModel tableModel = new DefaultTableModel(
                    new Object[]{"Mã SV", "Họ tên", "Tổng số tín chỉ", "Điểm trung bình (hệ 4)", "Xếp loại"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
                
                @Override
                public Class<?> getColumnClass(int columnIndex) {
                    if (columnIndex == 2) return Integer.class;
                    if (columnIndex == 3) return BigDecimal.class;
                    return Object.class;
                }
            };
            
            JTable gpaTable = new JTable(tableModel);
            gpaTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            gpaTable.setRowHeight(25);
            gpaTable.getTableHeader().setReorderingAllowed(false);
            
            // Custom renderer for decimal values
            DefaultTableCellRenderer decimalRenderer = new DefaultTableCellRenderer();
            decimalRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
            gpaTable.getColumnModel().getColumn(3).setCellRenderer(decimalRenderer);
            
            // Set column widths
            gpaTable.getColumnModel().getColumn(0).setPreferredWidth(80);
            gpaTable.getColumnModel().getColumn(1).setPreferredWidth(200);
            gpaTable.getColumnModel().getColumn(2).setPreferredWidth(100);
            gpaTable.getColumnModel().getColumn(3).setPreferredWidth(120);
            
            JScrollPane scrollPane = new JScrollPane(gpaTable);
            summaryPanel.add(scrollPane, BorderLayout.CENTER);
            
            // Set panel name for identification
            summaryPanel.setName(GPA_SUMMARY_PANEL);
            
            // Add to card layout
            contentPanel.add(summaryPanel, GPA_SUMMARY_PANEL);
            cardLayout.show(contentPanel, GPA_SUMMARY_PANEL);
            
            // Load GPA data
            loadStudentGPAData(tableModel);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error showing student GPA summary", e);
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi hiển thị điểm trung bình sinh viên: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Show the grade entry periods management panel
     */
    private void showEntryPeriodsPanel() {
        // Show the entry periods panel
        cardLayout.show(contentPanel, ENTRY_PERIODS_PANEL);
        
        // Get the selected node from the tree
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) 
                navigationTree.getLastSelectedPathComponent();
        
        if (selectedNode != null && selectedNode.getUserObject() instanceof ClassNodeData) {
            ClassNodeData classData = (ClassNodeData) selectedNode.getUserObject();
            entryPeriodsPanel.setCurrentClass(classData.getMaMon(), classData.getMaLop());
        } else {
            // If no class is selected, show all periods
            entryPeriodsPanel.setCurrentClass(null, null);
        }
    }
    
    /**
     * Load student GPA data into the summary table
     */
    private void loadStudentGPAData(DefaultTableModel tableModel) {
        try {
            // Clear existing data
            tableModel.setRowCount(0);
            
            // Get all students with grades
            Map<String, StudentGPASummary> studentGPAs = gradeService.calculateStudentGPASummary();
            
            // Add rows to table
            for (Map.Entry<String, StudentGPASummary> entry : studentGPAs.entrySet()) {
                StudentGPASummary summary = entry.getValue();
                
                tableModel.addRow(new Object[]{
                    summary.getMsv(),
                    summary.getHoTen(),
                    summary.getTotalCredits(),
                    summary.getAverageGPA(),
                    getLetterGradeFromGPA(summary.getAverageGPA())
                });
            }
            
            if (studentGPAs.isEmpty()) {
                logger.info("Không có dữ liệu điểm trung bình sinh viên");
            } else {
                logger.info("Đã tải " + studentGPAs.size() + " điểm trung bình sinh viên");
            }
        } catch (ServiceException e) {
            logger.log(Level.SEVERE, "Lỗi khi tải dữ liệu điểm trung bình sinh viên", e);
            JOptionPane.showMessageDialog(null,
                    "Lỗi khi tải dữ liệu điểm trung bình sinh viên: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
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
    
    /**
     * Find a panel by its ID in the card layout
     */
    private Component findPanelById(String panelId) {
        for (Component comp : contentPanel.getComponents()) {
            if (comp.getName() != null && comp.getName().equals(panelId)) {
                return comp;
            }
        }
        return null;
    }
    
    /**
     * Class to hold class and subject data for tree nodes
     */
    private static class ClassNodeData {
        private final String maLop;
        private final String maMon;
        
        public ClassNodeData(String maLop, String maMon) {
            this.maLop = maLop;
            this.maMon = maMon;
        }
        
        public String getMaLop() {
            return maLop;
        }
        
        public String getMaMon() {
            return maMon;
        }
        
        @Override
        public String toString() {
            return "Lớp " + maLop;
        }
    }
} 