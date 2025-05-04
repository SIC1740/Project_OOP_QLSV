package com.myuniv.sm.view.student;

import com.myuniv.sm.model.KyHoc;
import com.myuniv.sm.model.User;
import com.myuniv.sm.service.KyHocService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public class StudentAcademicTermPanel extends JPanel {
    private final KyHocService kyHocService;
    private final User currentUser;
    
    // UI Components
    private JTable termTable;
    private DefaultTableModel termTableModel;
    
    // Filter components
    private JComboBox<String> termFilterComboBox;
    private JButton filterButton;
    private JButton resetFilterButton;
    
    public StudentAcademicTermPanel(User user) {
        this.currentUser = user;
        kyHocService = new KyHocService();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Initialize UI components
        initComponents();
        
        // Load data
        loadTermFilter();
        loadTerms();
    }
    
    private void initComponents() {
        // Create table model with columns
        String[] columns = {"Tên Kỳ Học", "Mã Môn", "Tên Môn", "Số Tín Chỉ"};
        termTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        termTable = new JTable(termTableModel);
        termTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        termTable.getTableHeader().setReorderingAllowed(false);
        
        // Create scroll pane for table
        JScrollPane scrollPane = new JScrollPane(termTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Create filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JLabel titleLabel = new JLabel("Danh sách môn học theo kỳ học");
        titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, 16));
        
        termFilterComboBox = new JComboBox<>();
        termFilterComboBox.setPreferredSize(new Dimension(200, 25));
        filterButton = new JButton("Lọc");
        resetFilterButton = new JButton("Hiển thị tất cả");
        
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.add(titleLabel);
        
        filterPanel.add(new JLabel("Chọn kỳ học:"));
        filterPanel.add(termFilterComboBox);
        filterPanel.add(filterButton);
        filterPanel.add(resetFilterButton);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(titlePanel, BorderLayout.NORTH);
        topPanel.add(filterPanel, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Setup filter button actions
        filterButton.addActionListener(e -> {
            String selectedTerm = (String) termFilterComboBox.getSelectedItem();
            if (selectedTerm != null && !selectedTerm.isEmpty()) {
                filterTermsByName(selectedTerm);
            }
        });
        
        resetFilterButton.addActionListener(e -> loadTerms());
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
            row.add(term.getTenKyhoc());
            row.add(term.getMaMon());
            row.add(term.getTenMon());
            row.add(term.getSoTinChi());
            
            termTableModel.addRow(row);
        }
    }
    
    private void filterTermsByName(String termName) {
        // Clear table
        termTableModel.setRowCount(0);
        
        // Get filtered terms
        List<KyHoc> terms = kyHocService.getSubjectsByTerm(termName);
        
        // Add terms to table
        for (KyHoc term : terms) {
            Vector<Object> row = new Vector<>();
            row.add(term.getTenKyhoc());
            row.add(term.getMaMon());
            row.add(term.getTenMon());
            row.add(term.getSoTinChi());
            
            termTableModel.addRow(row);
        }
    }
} 