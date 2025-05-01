package com.myuniv.sm.view.admin;

import com.myuniv.sm.model.FeeDebt;
import com.myuniv.sm.service.FeeDebtService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FeeDebtPanel extends JPanel {
    private final DefaultTableModel model;
    private final JTable table;
    private final JButton btnAdd, btnEdit, btnDelete, btnMarkAsPaid, btnMarkAsUnpaid;
    private final JRadioButton rdoAll, rdoUnpaid, rdoPaid, rdoOverdue;
    private final JLabel lblTotalFees, lblTotalPaid, lblTotalUnpaid, lblTotalOverdue;
    private final FeeDebtService feeDebtService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final NumberFormat CURRENCY_FORMAT = new DecimalFormat("#,###");

    public FeeDebtPanel() {
        setLayout(new BorderLayout());
        feeDebtService = new FeeDebtService();

        // Top panel with filter and statistics
        JPanel topPanel = new JPanel(new BorderLayout());
        
        // Filter panel
        JPanel filterPanel = new JPanel();
        filterPanel.setBorder(BorderFactory.createTitledBorder("Lọc"));
        
        rdoAll = new JRadioButton("Tất cả");
        rdoUnpaid = new JRadioButton("Chưa đóng", true);  // Default selection
        rdoPaid = new JRadioButton("Đã đóng");
        rdoOverdue = new JRadioButton("Quá hạn");
        
        ButtonGroup filterGroup = new ButtonGroup();
        filterGroup.add(rdoAll);
        filterGroup.add(rdoUnpaid);
        filterGroup.add(rdoPaid);
        filterGroup.add(rdoOverdue);
        
        filterPanel.add(rdoAll);
        filterPanel.add(rdoUnpaid);
        filterPanel.add(rdoPaid);
        filterPanel.add(rdoOverdue);
        
        // Add action listeners to radio buttons
        rdoAll.addActionListener(e -> loadData());
        rdoUnpaid.addActionListener(e -> loadData());
        rdoPaid.addActionListener(e -> loadData());
        rdoOverdue.addActionListener(e -> loadDataOverdue());
        
        // Statistics panel
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 10, 5));
        statsPanel.setBorder(BorderFactory.createTitledBorder("Thống kê"));
        
        lblTotalFees = new JLabel("Tổng khoản thu: 0 VNĐ");
        lblTotalPaid = new JLabel("Đã đóng: 0 VNĐ");
        lblTotalUnpaid = new JLabel("Chưa đóng: 0 VNĐ");
        lblTotalOverdue = new JLabel("Quá hạn: 0 VNĐ");
        
        lblTotalPaid.setForeground(new Color(0, 128, 0)); // Green for paid
        lblTotalUnpaid.setForeground(Color.BLUE); // Blue for unpaid
        lblTotalOverdue.setForeground(Color.RED); // Red for overdue
        
        statsPanel.add(lblTotalFees);
        statsPanel.add(lblTotalPaid);
        statsPanel.add(lblTotalUnpaid);
        statsPanel.add(lblTotalOverdue);
        
        topPanel.add(filterPanel, BorderLayout.WEST);
        topPanel.add(statsPanel, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);

        // Table configuration
        model = new DefaultTableModel(
                new Object[] {"ID", "MSV", "Họ tên SV", "Khoản thu", "Số tiền", "Hạn thu", "Trạng thái"}, 0
        ) {
            @Override 
            public boolean isCellEditable(int r, int c) { return false; }
            
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0) return Integer.class;
                return Object.class;
            }
        };
        
        table = new JTable(model);
        table.getColumnModel().getColumn(0).setMaxWidth(50); // ID column width
        table.getColumnModel().getColumn(1).setPreferredWidth(100); // MSV column width
        table.getColumnModel().getColumn(2).setPreferredWidth(200); // Name column width
        table.getColumnModel().getColumn(4).setPreferredWidth(120); // Amount column width
        
        // Custom renderer for status cell color
        table.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (value != null) {
                    String status = value.toString();
                    if ("đã đóng".equals(status)) {
                        c.setForeground(new Color(0, 128, 0));  // Green text for paid
                    } else {
                        // Check if overdue
                        try {
                            String hanThuStr = table.getValueAt(row, 5).toString();
                            LocalDate hanThu = LocalDate.parse(hanThuStr, DATE_FORMATTER);
                            if (hanThu.isBefore(LocalDate.now())) {
                                c.setForeground(Color.RED);  // Red text for overdue
                            } else {
                                c.setForeground(Color.BLUE);  // Blue text for not yet overdue
                            }
                        } catch (Exception e) {
                            c.setForeground(Color.BLUE);  // Default to blue if can't parse
                        }
                    }
                }
                
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel btnPanel = new JPanel();
        btnAdd = new JButton("Thêm khoản thu");
        btnEdit = new JButton("Sửa");
        btnDelete = new JButton("Xóa");
        btnMarkAsPaid = new JButton("Đánh dấu đã đóng");
        btnMarkAsUnpaid = new JButton("Đánh dấu chưa đóng");
        
        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);
        btnPanel.add(btnMarkAsPaid);
        btnPanel.add(btnMarkAsUnpaid);
        
        add(btnPanel, BorderLayout.SOUTH);

        // Add action listeners
        btnAdd.addActionListener(e -> onAdd());
        btnEdit.addActionListener(e -> onEdit());
        btnDelete.addActionListener(e -> onDelete());
        btnMarkAsPaid.addActionListener(e -> onMarkAsPaid());
        btnMarkAsUnpaid.addActionListener(e -> onMarkAsUnpaid());

        // Load initial data
        loadData();
    }

    private void loadData() {
        model.setRowCount(0);
        List<FeeDebt> feeDebts;
        
        if (rdoAll.isSelected()) {
            feeDebts = feeDebtService.findAllFeeDebts();
        } else if (rdoUnpaid.isSelected()) {
            feeDebts = feeDebtService.findUnpaidFeeDebts();
        } else {
            feeDebts = feeDebtService.findPaidFeeDebts();
        }
        
        populateTable(feeDebts);
        updateStatistics(feeDebtService.findAllFeeDebts());
    }
    
    private void loadDataOverdue() {
        model.setRowCount(0);
        List<FeeDebt> allUnpaidFeeDebts = feeDebtService.findUnpaidFeeDebts();
        
        // Filter for overdue fees
        List<FeeDebt> overdueFeeDebts = allUnpaidFeeDebts.stream()
                .filter(feeDebt -> feeDebt.getHanThu().isBefore(LocalDate.now()))
                .toList();
        
        populateTable(overdueFeeDebts);
        updateStatistics(feeDebtService.findAllFeeDebts());
    }
    
    private void populateTable(List<FeeDebt> feeDebts) {
        for (FeeDebt feeDebt : feeDebts) {
            model.addRow(new Object[]{
                    feeDebt.getDebtId(),
                    feeDebt.getMsv(),
                    feeDebt.getStudentName(),
                    feeDebt.getKhoanThu(),
                    CURRENCY_FORMAT.format(feeDebt.getSoTien()) + " VNĐ",
                    feeDebt.getHanThu().format(DATE_FORMATTER),
                    feeDebt.getStatus()
            });
        }
    }
    
    private void updateStatistics(List<FeeDebt> allFeeDebts) {
        BigDecimal totalFees = BigDecimal.ZERO;
        BigDecimal totalPaid = BigDecimal.ZERO;
        BigDecimal totalUnpaid = BigDecimal.ZERO;
        BigDecimal totalOverdue = BigDecimal.ZERO;
        
        LocalDate today = LocalDate.now();
        
        for (FeeDebt feeDebt : allFeeDebts) {
            BigDecimal amount = feeDebt.getSoTien();
            totalFees = totalFees.add(amount);
            
            if ("đã đóng".equals(feeDebt.getStatus())) {
                totalPaid = totalPaid.add(amount);
            } else {
                totalUnpaid = totalUnpaid.add(amount);
                
                // Check if overdue
                if (feeDebt.getHanThu().isBefore(today)) {
                    totalOverdue = totalOverdue.add(amount);
                }
            }
        }
        
        lblTotalFees.setText("Tổng khoản thu: " + CURRENCY_FORMAT.format(totalFees) + " VNĐ");
        lblTotalPaid.setText("Đã đóng: " + CURRENCY_FORMAT.format(totalPaid) + " VNĐ");
        lblTotalUnpaid.setText("Chưa đóng: " + CURRENCY_FORMAT.format(totalUnpaid) + " VNĐ");
        lblTotalOverdue.setText("Quá hạn: " + CURRENCY_FORMAT.format(totalOverdue) + " VNĐ");
    }

    private void onAdd() {
        FeeDebtDialog dialog = new FeeDebtDialog((JFrame) SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            boolean success = feeDebtService.saveFeeDebt(
                    dialog.getMsv(),
                    dialog.getKhoanThu(),
                    dialog.getSoTien(),
                    dialog.getHanThu(),
                    dialog.getStatus()
            );
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                        "Thêm khoản thu thành công", 
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, 
                        "Không thể thêm khoản thu", 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onEdit() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                    "Vui lòng chọn khoản thu để sửa", 
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int debtId = (Integer) model.getValueAt(selectedRow, 0);
        FeeDebt feeDebt = feeDebtService.findFeeDebtById(debtId);
        if (feeDebt == null) {
            JOptionPane.showMessageDialog(this, 
                    "Không tìm thấy thông tin khoản thu", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        FeeDebtDialog dialog = new FeeDebtDialog((JFrame) SwingUtilities.getWindowAncestor(this), feeDebt);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            boolean success = feeDebtService.updateFeeDebt(
                    debtId,
                    dialog.getMsv(),
                    dialog.getKhoanThu(),
                    dialog.getSoTien(),
                    dialog.getHanThu(),
                    dialog.getStatus()
            );
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                        "Cập nhật khoản thu thành công", 
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, 
                        "Không thể cập nhật khoản thu", 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onDelete() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                    "Vui lòng chọn khoản thu để xóa", 
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int debtId = (Integer) model.getValueAt(selectedRow, 0);
        String msv = (String) model.getValueAt(selectedRow, 1);
        String khoanThu = (String) model.getValueAt(selectedRow, 3);
        
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa khoản thu '" + khoanThu + "' của sinh viên " + msv + "?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = feeDebtService.deleteFeeDebt(debtId);
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                        "Xóa khoản thu thành công", 
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, 
                        "Không thể xóa khoản thu", 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void onMarkAsPaid() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                    "Vui lòng chọn khoản thu để đánh dấu đã đóng", 
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String status = (String) model.getValueAt(selectedRow, 6);
        if ("đã đóng".equals(status)) {
            JOptionPane.showMessageDialog(this, 
                    "Khoản thu này đã được đánh dấu là đã đóng", 
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int debtId = (Integer) model.getValueAt(selectedRow, 0);
        String msv = (String) model.getValueAt(selectedRow, 1);
        String khoanThu = (String) model.getValueAt(selectedRow, 3);
        
        int confirm = JOptionPane.showConfirmDialog(this,
                "Xác nhận đánh dấu khoản thu '" + khoanThu + "' của sinh viên " + msv + " là đã đóng?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = feeDebtService.markAsPaid(debtId);
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                        "Đã cập nhật trạng thái khoản thu thành công", 
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, 
                        "Không thể cập nhật trạng thái khoản thu", 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void onMarkAsUnpaid() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                    "Vui lòng chọn khoản thu để đánh dấu chưa đóng", 
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String status = (String) model.getValueAt(selectedRow, 6);
        if ("chưa đóng".equals(status)) {
            JOptionPane.showMessageDialog(this, 
                    "Khoản thu này đã được đánh dấu là chưa đóng", 
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int debtId = (Integer) model.getValueAt(selectedRow, 0);
        String msv = (String) model.getValueAt(selectedRow, 1);
        String khoanThu = (String) model.getValueAt(selectedRow, 3);
        
        int confirm = JOptionPane.showConfirmDialog(this,
                "Xác nhận đánh dấu khoản thu '" + khoanThu + "' của sinh viên " + msv + " là chưa đóng?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = feeDebtService.markAsUnpaid(debtId);
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                        "Đã cập nhật trạng thái khoản thu thành công", 
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, 
                        "Không thể cập nhật trạng thái khoản thu", 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
} 