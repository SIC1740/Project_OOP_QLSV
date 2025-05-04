package com.myuniv.sm.view.admin;

import com.myuniv.sm.model.GradeEntryPeriod;
import com.myuniv.sm.model.Subject;
import com.myuniv.sm.service.GradeEntryPeriodService;
import com.myuniv.sm.service.ServiceException;
import com.myuniv.sm.service.SubjectService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Panel to manage grade entry periods for classes
 */
public class GradeEntryPeriodsPanel extends JPanel {
    private static final Logger logger = Logger.getLogger(GradeEntryPeriodsPanel.class.getName());
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    private final GradeEntryPeriodService periodService;
    private final SubjectService subjectService;
    private final String adminUsername;
    
    private JTable periodsTable;
    private DefaultTableModel tableModel;
    private JButton btnAdd;
    private JButton btnEdit;
    private JButton btnDelete;
    private JButton btnRefresh;
    
    private String currentMaMon;
    private String currentMaLop;
    
    public GradeEntryPeriodsPanel(String adminUsername) {
        this.adminUsername = adminUsername;
        this.periodService = new GradeEntryPeriodService();
        this.subjectService = new SubjectService();
        
        initComponents();
        loadAllPeriods();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Create title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Quản lý thời gian nhập điểm cho giảng viên", SwingConstants.CENTER);
        titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        add(titlePanel, BorderLayout.NORTH);
        
        // Create table model
        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Môn học", "Tên môn", "Lớp", "Thời gian bắt đầu", 
                           "Thời gian kết thúc", "Trạng thái", "Người tạo"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        periodsTable = new JTable(tableModel);
        periodsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        periodsTable.setRowHeight(25);
        periodsTable.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane scrollPane = new JScrollPane(periodsTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        btnAdd = new JButton("Thêm thời gian nhập điểm");
        btnEdit = new JButton("Chỉnh sửa");
        btnDelete = new JButton("Xóa");
        btnRefresh = new JButton("Làm mới");
        
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Add action listeners
        btnAdd.addActionListener(e -> addEntryPeriod());
        btnEdit.addActionListener(e -> editEntryPeriod());
        btnDelete.addActionListener(e -> deleteEntryPeriod());
        btnRefresh.addActionListener(e -> loadAllPeriods());
    }
    
    public void setCurrentClass(String maMon, String maLop) {
        this.currentMaMon = maMon;
        this.currentMaLop = maLop;
        loadPeriodsForClass(maMon, maLop);
    }
    
    private void loadAllPeriods() {
        try {
            // Clear table
            tableModel.setRowCount(0);
            
            // Load all periods
            List<GradeEntryPeriod> periods = periodService.getAllEntryPeriods();
            
            // Get subject names
            for (GradeEntryPeriod period : periods) {
                try {
                    Subject subject = subjectService.getSubjectByMaMon(period.getMaMon());
                    String tenMon = subject != null ? subject.getTenMon() : "Không xác định";
                    
                    tableModel.addRow(new Object[]{
                        period.getNhapdiem_id(),
                        period.getMaMon(),
                        tenMon,
                        period.getMaLop(),
                        period.getThoi_gian_bat_dau_nhap().format(dateFormatter),
                        period.getThoi_gian_ket_thuc_nhap().format(dateFormatter),

                    });
                } catch (ServiceException e) {
                    logger.log(Level.WARNING, "Error loading subject details for period", e);
                }
            }
            
        } catch (ServiceException e) {
            logger.log(Level.SEVERE, "Error loading grade entry periods", e);
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi tải danh sách thời gian nhập điểm: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadPeriodsForClass(String maMon, String maLop) {
        try {
            // Clear table
            tableModel.setRowCount(0);
            
            // Load periods for specified class
            List<GradeEntryPeriod> periods = periodService.getEntryPeriodsForSubjectAndClass(maMon, maLop);
            
            // Get subject name
            Subject subject = subjectService.getSubjectByMaMon(maMon);
            String tenMon = subject != null ? subject.getTenMon() : "Không xác định";
            
            for (GradeEntryPeriod period : periods) {
                tableModel.addRow(new Object[]{
                    period.getNhapdiem_id(),
                    period.getMaMon(),
                    tenMon,
                    period.getMaLop(),
                    period.getThoi_gian_bat_dau_nhap().format(dateFormatter),
                    period.getThoi_gian_ket_thuc_nhap().format(dateFormatter),

                });
            }
            
        } catch (ServiceException e) {
            logger.log(Level.SEVERE, "Error loading grade entry periods for class", e);
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi tải danh sách thời gian nhập điểm cho lớp: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void addEntryPeriod() {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        GradeEntryPeriodDialog dialog = new GradeEntryPeriodDialog(
                parent, null, currentMaMon, currentMaLop);
        dialog.setVisible(true);
        
        if (dialog.isSaved()) {
            if (currentMaMon != null && currentMaLop != null) {
                loadPeriodsForClass(currentMaMon, currentMaLop);
            } else {
                loadAllPeriods();
            }
        }
    }
    
    private void editEntryPeriod() {
        int selectedRow = periodsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn một thời gian nhập điểm để chỉnh sửa",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int periodId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String maMon = (String) tableModel.getValueAt(selectedRow, 1);
        String maLop = (String) tableModel.getValueAt(selectedRow, 3);
        
        try {
            // Get all periods and find the selected one
            List<GradeEntryPeriod> periods = periodService.getAllEntryPeriods();
            GradeEntryPeriod selectedPeriod = null;
            
            for (GradeEntryPeriod period : periods) {
                if (period.getNhapdiem_id() == periodId) {
                    selectedPeriod = period;
                    break;
                }
            }
            
            if (selectedPeriod != null) {
                Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
                GradeEntryPeriodDialog dialog = new GradeEntryPeriodDialog(
                        parent, selectedPeriod, null, null);
                dialog.setVisible(true);
                
                if (dialog.isSaved()) {
                    if (currentMaMon != null && currentMaLop != null) {
                        loadPeriodsForClass(currentMaMon, currentMaLop);
                    } else {
                        loadAllPeriods();
                    }
                }
            }
            
        } catch (ServiceException e) {
            logger.log(Level.SEVERE, "Error getting grade entry period for editing", e);
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi lấy thông tin thời gian nhập điểm: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteEntryPeriod() {
        int selectedRow = periodsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn một thời gian nhập điểm để xóa",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int periodId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String maMon = (String) tableModel.getValueAt(selectedRow, 1);
        String tenMon = (String) tableModel.getValueAt(selectedRow, 2);
        String maLop = (String) tableModel.getValueAt(selectedRow, 3);
        
        int confirmation = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa thời gian nhập điểm cho môn " + 
                        maMon + " - " + tenMon + ", lớp " + maLop + "?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        
        if (confirmation == JOptionPane.YES_OPTION) {
            try {
                if (periodService.deleteEntryPeriod(periodId)) {
                    if (currentMaMon != null && currentMaLop != null) {
                        loadPeriodsForClass(currentMaMon, currentMaLop);
                    } else {
                        loadAllPeriods();
                    }
                    
                    JOptionPane.showMessageDialog(this,
                            "Đã xóa thời gian nhập điểm thành công",
                            "Thành công", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Không thể xóa thời gian nhập điểm",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (ServiceException e) {
                logger.log(Level.SEVERE, "Error deleting grade entry period", e);
                JOptionPane.showMessageDialog(this,
                        "Lỗi khi xóa thời gian nhập điểm: " + e.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
} 