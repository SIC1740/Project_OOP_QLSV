package com.myuniv.sm.view.admin;

import com.myuniv.sm.model.KyHoc;
import com.myuniv.sm.service.KyHocService;
import com.myuniv.sm.service.SubjectService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public class AcademicTermPanel extends JPanel {
    private final KyHocService kyHocService;
    private final SubjectService subjectService;
    
    // UI Components
    private JTable termTable;
    private DefaultTableModel termTableModel;
    private JTextField termNameField;
    private JComboBox<String> subjectComboBox;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;
    
    // Current selected term for editing
    private KyHoc currentKyHoc;
    
    // Filter components
    private JComboBox<String> termFilterComboBox;
    private JButton filterButton;
    private JButton resetFilterButton;
    
    public AcademicTermPanel() {
        kyHocService = new KyHocService();
        subjectService = new SubjectService();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Initialize UI components
        initComponents();
        
        // Load data
        loadSubjects();
        loadTermFilter();
        loadTerms();
    }
    
    private void initComponents() {
        // Create table model with columns
        String[] columns = {"ID", "Tên Kỳ Học", "Mã Môn", "Tên Môn", "Số Tín Chỉ"};
        termTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        termTable = new JTable(termTableModel);
        termTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        termTable.getTableHeader().setReorderingAllowed(false);
        
        // Add table selection listener
        termTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = termTable.getSelectedRow();
                if (row >= 0) {
                    // Get term ID from first column
                    int kyhocId = (int) termTable.getValueAt(row, 0);
                    currentKyHoc = kyHocService.getAcademicTermById(kyhocId);
                    
                    // Populate fields with selected term
                    if (currentKyHoc != null) {
                        termNameField.setText(currentKyHoc.getTenKyhoc());
                        subjectComboBox.setSelectedItem(currentKyHoc.getMaMon());
                        
                        // Enable update and delete buttons
                        updateButton.setEnabled(true);
                        deleteButton.setEnabled(true);
                    }
                }
            }
        });
        
        // Create scroll pane for table
        JScrollPane scrollPane = new JScrollPane(termTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Create filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        termFilterComboBox = new JComboBox<>();
        termFilterComboBox.setPreferredSize(new Dimension(200, 25));
        filterButton = new JButton("Lọc");
        resetFilterButton = new JButton("Hiển thị tất cả");
        
        filterPanel.add(new JLabel("Lọc theo kỳ học:"));
        filterPanel.add(termFilterComboBox);
        filterPanel.add(filterButton);
        filterPanel.add(resetFilterButton);
        
        add(filterPanel, BorderLayout.NORTH);
        
        // Setup filter button actions
        filterButton.addActionListener(e -> {
            String selectedTerm = (String) termFilterComboBox.getSelectedItem();
            if (selectedTerm != null && !selectedTerm.isEmpty()) {
                filterTermsByName(selectedTerm);
            }
        });
        
        resetFilterButton.addActionListener(e -> loadTerms());
        
        // Create form panel for adding/editing
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin Kỳ học - Môn học"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Term name field
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Tên Kỳ Học:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        termNameField = new JTextField(20);
        formPanel.add(termNameField, gbc);
        
        // Subject combo box
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Môn Học:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        subjectComboBox = new JComboBox<>();
        formPanel.add(subjectComboBox, gbc);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        addButton = new JButton("Thêm");
        updateButton = new JButton("Cập nhật");
        deleteButton = new JButton("Xóa");
        clearButton = new JButton("Làm mới");
        
        // Initially disable update and delete buttons
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
        
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(buttonPanel, gbc);
        
        add(formPanel, BorderLayout.SOUTH);
        
        // Setup button actions
        addButton.addActionListener(e -> addTerm());
        updateButton.addActionListener(e -> updateTerm());
        deleteButton.addActionListener(e -> deleteTerm());
        clearButton.addActionListener(e -> clearForm());
    }
    
    private void loadSubjects() {
        // Clear combo box
        subjectComboBox.removeAllItems();
        
        // Get subjects from service and add to combo box
        subjectService.getAllSubjects().forEach(subject -> 
            subjectComboBox.addItem(subject.getMaMon() + " - " + subject.getTenMon())
        );
    }
    
    private void loadTermFilter() {
        // Clear combo box
        termFilterComboBox.removeAllItems();
        
        // Add "All" option
        termFilterComboBox.addItem("");
        
        // Get all term names and add to combo box
        Set<String> termNames = kyHocService.getAllTermNames();
        termNames.forEach(termFilterComboBox::addItem);
    }
    
    private void loadTerms() {
        // Clear table
        termTableModel.setRowCount(0);
        
        // Get all terms
        List<KyHoc> terms = kyHocService.getAllAcademicTerms();
        
        // Add terms to table
        for (KyHoc term : terms) {
            Vector<Object> row = new Vector<>();
            row.add(term.getKyhocId());
            row.add(term.getTenKyhoc());
            row.add(term.getMaMon());
            row.add(term.getTenMon());
            row.add(term.getSoTinChi());
            
            termTableModel.addRow(row);
        }
        
        // Update term filter
        loadTermFilter();
        
        // Clear form
        clearForm();
    }
    
    private void filterTermsByName(String termName) {
        // Clear table
        termTableModel.setRowCount(0);
        
        // Get filtered terms
        List<KyHoc> terms = kyHocService.getSubjectsByTerm(termName);
        
        // Add terms to table
        for (KyHoc term : terms) {
            Vector<Object> row = new Vector<>();
            row.add(term.getKyhocId());
            row.add(term.getTenKyhoc());
            row.add(term.getMaMon());
            row.add(term.getTenMon());
            row.add(term.getSoTinChi());
            
            termTableModel.addRow(row);
        }
    }
    
    private void addTerm() {
        // Validate input
        String termName = termNameField.getText().trim();
        String subjectIdWithName = (String) subjectComboBox.getSelectedItem();
        
        if (termName.isEmpty() || subjectIdWithName == null) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng nhập đầy đủ thông tin kỳ học và môn học.",
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Extract subject ID from combo box selection (Format: "ID - Name")
        String subjectId = subjectIdWithName.split(" - ")[0];
        
        // Add term
        boolean success = kyHocService.addTerm(termName, subjectId);
        
        if (success) {
            JOptionPane.showMessageDialog(this, 
                "Thêm kỳ học thành công.",
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            
            // Refresh data
            loadTerms();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Thêm kỳ học thất bại. Vui lòng thử lại.",
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateTerm() {
        // Check if a term is selected
        if (currentKyHoc == null) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn một kỳ học để cập nhật.",
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validate input
        String termName = termNameField.getText().trim();
        String subjectIdWithName = (String) subjectComboBox.getSelectedItem();
        
        if (termName.isEmpty() || subjectIdWithName == null) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng nhập đầy đủ thông tin kỳ học và môn học.",
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Extract subject ID from combo box selection (Format: "ID - Name")
        String subjectId = subjectIdWithName.split(" - ")[0];
        
        // Update current term
        currentKyHoc.setTenKyhoc(termName);
        currentKyHoc.setMaMon(subjectId);
        
        // Update term
        boolean success = kyHocService.updateTerm(currentKyHoc);
        
        if (success) {
            JOptionPane.showMessageDialog(this, 
                "Cập nhật kỳ học thành công.",
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            
            // Refresh data
            loadTerms();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Cập nhật kỳ học thất bại. Vui lòng thử lại.",
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteTerm() {
        // Check if a term is selected
        if (currentKyHoc == null) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn một kỳ học để xóa.",
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Confirm deletion
        int choice = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc muốn xóa kỳ học này không?",
            "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            // Delete term
            boolean success = kyHocService.deleteTerm(currentKyHoc.getKyhocId());
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                    "Xóa kỳ học thành công.",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                
                // Refresh data
                loadTerms();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Xóa kỳ học thất bại. Vui lòng thử lại.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void clearForm() {
        termNameField.setText("");
        if (subjectComboBox.getItemCount() > 0) {
            subjectComboBox.setSelectedIndex(0);
        }
        
        // Reset current term
        currentKyHoc = null;
        
        // Disable update and delete buttons
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
        
        // Enable add button
        addButton.setEnabled(true);
    }
} 