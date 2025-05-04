package com.myuniv.sm.view.admin;

import com.myuniv.sm.model.AcademicClass;
import com.myuniv.sm.model.Subject;
import com.myuniv.sm.model.Student;
import com.myuniv.sm.service.AcademicClassService;
import com.myuniv.sm.service.StudentService;
import com.myuniv.sm.service.ServiceException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AcademicClassPanel extends JPanel {
    private static final Logger logger = Logger.getLogger(AcademicClassPanel.class.getName());
    
    private final AcademicClassService academicClassService;
    private final StudentService studentService;
    private final JTable classTable;
    private final DefaultTableModel classTableModel;
    private final JTree subjectTree;
    private final DefaultTreeModel treeModel;
    private final DefaultMutableTreeNode rootNode;
    private final JButton btnAdd, btnEdit, btnDelete, btnViewDetails;
    
    // Student table
    private final DefaultTableModel studentTableModel;
    private final JTable studentTable;

    public AcademicClassPanel() {
        setLayout(new BorderLayout());
        
        // Initialize services
        academicClassService = new AcademicClassService();
        studentService = new StudentService();

        // Create main split pane
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setDividerLocation(350);
        
        // Left panel - Class Table
        JPanel leftPanel = new JPanel(new BorderLayout());
        classTableModel = new DefaultTableModel(
                new Object[] {"Mã lớp", "Số sinh viên"}, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        classTable = new JTable(classTableModel);
        
        // Improve table appearance
        classTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        classTable.setRowHeight(25);
        classTable.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane tableScrollPane = new JScrollPane(classTable);
        tableScrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10),
            BorderFactory.createLineBorder(Color.LIGHT_GRAY)
        ));
        leftPanel.add(tableScrollPane, BorderLayout.CENTER);
        
        // Right panel - Split pane for subjects and students
        JPanel rightPanel = new JPanel(new BorderLayout());
        JSplitPane rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        rightSplitPane.setDividerLocation(200);
        
        // Top right panel - Subject Tree
        JPanel subjectPanel = new JPanel(new BorderLayout());
        JLabel subjectLabel = new JLabel("Danh sách môn học");
        subjectLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        subjectLabel.setFont(new Font(subjectLabel.getFont().getName(), Font.BOLD, 12));
        subjectPanel.add(subjectLabel, BorderLayout.NORTH);
        
        rootNode = new DefaultMutableTreeNode("Chọn một lớp học để xem môn học");
        treeModel = new DefaultTreeModel(rootNode);
        subjectTree = new JTree(treeModel);
        subjectTree.setRootVisible(true);
        subjectTree.setCellRenderer(new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, 
                    boolean leaf, int row, boolean hasFocus) {
                super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
                if (value instanceof DefaultMutableTreeNode) {
                    Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
                    if (userObject instanceof Subject) {
                        Subject subject = (Subject) userObject;
                        setText(subject.getTenMon() + " (" + subject.getSoTinChi() + " tín chỉ)");
                    }
                }
                return this;
            }
        });
        
        JScrollPane treeScrollPane = new JScrollPane(subjectTree);
        treeScrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(0, 5, 5, 5),
            BorderFactory.createLineBorder(Color.LIGHT_GRAY)
        ));
        subjectPanel.add(treeScrollPane, BorderLayout.CENTER);
        
        // Bottom right panel - Student Table
        JPanel studentPanel = new JPanel(new BorderLayout());
        JLabel studentLabel = new JLabel("Danh sách sinh viên");
        studentLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        studentLabel.setFont(new Font(studentLabel.getFont().getName(), Font.BOLD, 12));
        studentPanel.add(studentLabel, BorderLayout.NORTH);
        
        studentTableModel = new DefaultTableModel(
                new Object[] {"Mã SV", "Họ tên", "Ngày sinh", "Email", "SĐT"}, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        studentTable = new JTable(studentTableModel);
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentTable.setRowHeight(25);
        studentTable.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane studentScrollPane = new JScrollPane(studentTable);
        studentScrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(0, 5, 5, 5),
            BorderFactory.createLineBorder(Color.LIGHT_GRAY)
        ));
        studentPanel.add(studentScrollPane, BorderLayout.CENTER);
        
        // Add panels to right split pane
        rightSplitPane.setTopComponent(subjectPanel);
        rightSplitPane.setBottomComponent(studentPanel);
        rightPanel.add(rightSplitPane, BorderLayout.CENTER);
        
        // Add panels to main split pane
        mainSplitPane.setLeftComponent(leftPanel);
        mainSplitPane.setRightComponent(rightPanel);
        add(mainSplitPane, BorderLayout.CENTER);
        
        // Create button panel
        JPanel btnPanel = new JPanel();
        btnPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        
        btnAdd = new JButton("Thêm lớp");
        btnEdit = new JButton("Sửa lớp");
        btnDelete = new JButton("Xóa lớp");
        btnViewDetails = new JButton("Xem chi tiết");
        
        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);
        btnPanel.add(btnViewDetails);
        add(btnPanel, BorderLayout.SOUTH);

        // Add action listeners
        btnAdd.addActionListener(e -> onAdd());
        btnEdit.addActionListener(e -> onEdit());
        btnDelete.addActionListener(e -> onDelete());
        btnViewDetails.addActionListener(e -> onViewDetails());
        
        // Add selection listener for class table
        classTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                try {
                    updateDetailsForSelectedClass();
                } catch (ServiceException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        // Load data from database
        loadData();
    }

    public void loadData() {
        classTableModel.setRowCount(0);
        rootNode.removeAllChildren();
        treeModel.reload();
        studentTableModel.setRowCount(0);
        
        try {
            List<AcademicClass> classes = academicClassService.findAll();
            
            for (AcademicClass academicClass : classes) {
                classTableModel.addRow(new Object[] {
                    academicClass.getMaLop(),
                    academicClass.getSoLuongSinhVien()
                });
            }
            
            if (classes.isEmpty()) {
                logger.info("Không có dữ liệu lớp học để hiển thị");
            } else {
                logger.info("Đã tải " + classes.size() + " lớp học");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Lỗi khi tải dữ liệu lớp học", e);
            JOptionPane.showMessageDialog(this,
                "Lỗi khi tải dữ liệu lớp học: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateDetailsForSelectedClass() throws ServiceException {
        // Update subject tree
        updateSubjectTree();
        
        // Update student table
        updateStudentTable();
    }
    
    private void updateSubjectTree() {
        rootNode.removeAllChildren();
        
        int selectedRow = classTable.getSelectedRow();
        if (selectedRow >= 0) {
            String maLop = (String) classTableModel.getValueAt(selectedRow, 0);
            
            try {
                AcademicClass academicClass = academicClassService.getClassByMaLop(maLop);
                rootNode.setUserObject("Môn học của lớp " + academicClass.getMaLop());
                
                if (academicClass.getSubjects().isEmpty()) {
                    DefaultMutableTreeNode emptyNode = new DefaultMutableTreeNode("Chưa có môn học nào được phân công");
                    rootNode.add(emptyNode);
                } else {
                    for (Subject subject : academicClass.getSubjects()) {
                        DefaultMutableTreeNode subjectNode = new DefaultMutableTreeNode(subject);
                        rootNode.add(subjectNode);
                    }
                }
                
                treeModel.reload();
                subjectTree.expandPath(subjectTree.getPathForRow(0));
            } catch (ServiceException e) {
                logger.log(Level.SEVERE, "Lỗi khi tải thông tin môn học của lớp", e);
            }
        } else {
            rootNode.setUserObject("Chọn một lớp học để xem môn học");
            treeModel.reload();
        }
    }

    private void updateStudentTable() throws ServiceException {
        studentTableModel.setRowCount(0);

        int selectedRow = classTable.getSelectedRow();
        if (selectedRow < 0) return;

        String maLop = (String) classTableModel.getValueAt(selectedRow, 0);
        List<Student> students = studentService.getStudentsByClass(maLop);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for (Student s : students) {
            LocalDate dob = s.getNgaySinh();  // phải là LocalDate
            String dobStr = (dob != null ? dob.format(dtf) : "");
            studentTableModel.addRow(new Object[]{
                    s.getMsv(),
                    s.getHoTen(),
                    dobStr,
                    s.getEmail(),
                    s.getSoDienThoai()
            });
        }
    }


    private void onAdd() {
        AcademicClassDialog dialog = new AcademicClassDialog(
            (Frame) SwingUtilities.getWindowAncestor(this), 
            null
        );
        dialog.setVisible(true);
        
        if (dialog.isSaved()) {
            loadData();
        }
    }

    private void onEdit() {
        int selectedRow = classTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng chọn một lớp học để sửa",
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            String maLop = classTableModel.getValueAt(selectedRow, 0).toString();
            AcademicClass academicClass = academicClassService.getClassByMaLop(maLop);
            
            AcademicClassDialog dialog = new AcademicClassDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), 
                academicClass
            );
            dialog.setVisible(true);
            
            if (dialog.isSaved()) {
                loadData();
            }
        } catch (ServiceException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy thông tin lớp học", e);
            JOptionPane.showMessageDialog(this,
                "Lỗi: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDelete() {
        int selectedRow = classTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng chọn một lớp học để xóa",
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String maLop = classTableModel.getValueAt(selectedRow, 0).toString();
        int studentCount = Integer.parseInt(classTableModel.getValueAt(selectedRow, 1).toString());
        
        // Check if there are students in this class
        if (studentCount > 0) {
            JOptionPane.showMessageDialog(this,
                "Không thể xóa lớp học có sinh viên. Vui lòng chuyển sinh viên sang lớp khác trước.",
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc muốn xóa lớp học " + maLop + "?",
            "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (academicClassService.deleteClass(maLop)) {
                    JOptionPane.showMessageDialog(this,
                        "Xóa lớp học thành công",
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Không thể xóa lớp học",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (ServiceException e) {
                logger.log(Level.SEVERE, "Lỗi khi xóa lớp học", e);
                JOptionPane.showMessageDialog(this,
                    "Lỗi: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void onViewDetails() {
        int selectedRow = classTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng chọn một lớp học để xem chi tiết",
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        try {
            String maLop = classTableModel.getValueAt(selectedRow, 0).toString();
            AcademicClass academicClass = academicClassService.getClassByMaLop(maLop);
            List<Student> students = studentService.getStudentsByClass(maLop);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            
            StringBuilder details = new StringBuilder();
            details.append("Mã lớp: ").append(academicClass.getMaLop()).append("\n");
            details.append("Số lượng sinh viên: ").append(academicClass.getSoLuongSinhVien()).append("\n\n");
            
            // Add subject information
            details.append("DANH SÁCH MÔN HỌC:\n");
            if (academicClass.getSubjects().isEmpty()) {
                details.append("- Chưa có môn học nào được phân công\n");
            } else {
                for (Subject subject : academicClass.getSubjects()) {
                    details.append("- ").append(subject.getTenMon())
                          .append(" (").append(subject.getMaMon()).append(")")
                          .append(" - ").append(subject.getSoTinChi()).append(" tín chỉ\n");
                }
            }
            
            // Add student information
            details.append("\nDANH SÁCH SINH VIÊN:\n");
            if (students.isEmpty()) {
                details.append("- Chưa có sinh viên nào trong lớp\n");
            } else {
                for (Student student : students) {
                    details.append("- ").append(student.getMsv())
                          .append(" | ").append(student.getHoTen())
                          .append(" | ").append(dateFormat.format(student.getNgaySinh()))
                          .append(" | ").append(student.getEmail());
                    
                    if (student.getSoDienThoai() != null && !student.getSoDienThoai().isEmpty()) {
                        details.append(" | ").append(student.getSoDienThoai());
                    }
                    
                    details.append("\n");
                }
            }
            
            JTextArea textArea = new JTextArea(details.toString());
            textArea.setEditable(false);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(600, 400));
            
            JOptionPane.showMessageDialog(this,
                scrollPane,
                "Chi tiết lớp học " + academicClass.getMaLop(),
                JOptionPane.INFORMATION_MESSAGE);
            
        } catch (ServiceException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy thông tin chi tiết lớp học", e);
            JOptionPane.showMessageDialog(this,
                "Lỗi: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
} 