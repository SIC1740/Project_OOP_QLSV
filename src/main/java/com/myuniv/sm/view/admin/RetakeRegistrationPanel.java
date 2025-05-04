package com.myuniv.sm.view.admin;

import com.myuniv.sm.model.RetakeRequest;
import com.myuniv.sm.model.RetakeRegistrationPeriod;
import com.myuniv.sm.service.RetakeService;
import com.myuniv.sm.service.ServiceException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Panel for administrators to manage retake registration periods and approve retake requests
 */
public class RetakeRegistrationPanel extends JPanel {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    private final RetakeService retakeService;
    private JTable requestsTable;
    private DefaultTableModel requestsTableModel;
    private JLabel periodStatusLabel;
    private JButton openPeriodButton;
    private JButton closePeriodButton;
    private JButton refreshButton;
    private JButton approveButton;
    private JButton rejectButton;
    
    public RetakeRegistrationPanel() {
        this.retakeService = new RetakeService();
        initUI();
        loadData();
    }
    
    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Registration period panel at top
        JPanel periodPanel = createPeriodPanel();
        add(periodPanel, BorderLayout.NORTH);
        
        // Requests table in center
        JPanel requestsPanel = createRequestsPanel();
        add(requestsPanel, BorderLayout.CENTER);
        
        // Actions panel at bottom
        JPanel actionsPanel = createActionsPanel();
        add(actionsPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createPeriodPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Thời gian đăng ký học lại"),
                new EmptyBorder(10, 10, 10, 10)));
        
        // Status label
        periodStatusLabel = new JLabel("Trạng thái: Đang kiểm tra...");
        panel.add(periodStatusLabel, BorderLayout.CENTER);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        openPeriodButton = new JButton("Mở đăng ký");
        openPeriodButton.addActionListener(this::openRegistrationPeriod);
        buttonsPanel.add(openPeriodButton);
        
        closePeriodButton = new JButton("Đóng đăng ký");
        closePeriodButton.addActionListener(this::closeRegistrationPeriod);
        buttonsPanel.add(closePeriodButton);
        
        refreshButton = new JButton("Làm mới");
        refreshButton.addActionListener(e -> loadData());
        buttonsPanel.add(refreshButton);
        
        panel.add(buttonsPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createRequestsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Danh sách yêu cầu đăng ký học lại"),
                new EmptyBorder(10, 10, 10, 10)));
        
        // Table model
        String[] columnNames = {"ID", "MSV", "Họ tên SV", "Mã lớp", "Tên lớp", "Ngày đăng ký", "Trạng thái"};
        requestsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        
        // Create table
        requestsTable = new JTable(requestsTableModel);
        requestsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        requestsTable.setAutoCreateRowSorter(true);
        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(requestsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Add description label
        JLabel descLabel = new JLabel("Danh sách này hiển thị các yêu cầu đăng ký học lại từ sinh viên. Nhấn \"Làm mới\" để cập nhật.");
        panel.add(descLabel, BorderLayout.NORTH);
        
        // Add status legend
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        legendPanel.add(new JLabel("Trạng thái: "));
        
        JLabel newLabel = new JLabel("Mới - chờ duyệt");
        newLabel.setForeground(Color.BLUE);
        legendPanel.add(newLabel);
        
        legendPanel.add(new JLabel(" | "));
        
        JLabel approvedLabel = new JLabel("Chấp nhận");
        approvedLabel.setForeground(new Color(0, 128, 0)); // Dark green
        legendPanel.add(approvedLabel);
        
        legendPanel.add(new JLabel(" | "));
        
        JLabel rejectedLabel = new JLabel("Từ chối");
        rejectedLabel.setForeground(Color.RED);
        legendPanel.add(rejectedLabel);
        
        panel.add(legendPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createActionsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        approveButton = new JButton("Duyệt đăng ký");
        approveButton.setEnabled(false);
        approveButton.addActionListener(this::approveSelectedRequest);
        panel.add(approveButton);
        
        rejectButton = new JButton("Từ chối đăng ký");
        rejectButton.setEnabled(false);
        rejectButton.addActionListener(this::rejectSelectedRequest);
        panel.add(rejectButton);
        
        return panel;
    }
    
    private void loadData() {
        // Check registration period status
        RetakeRegistrationPeriod period = retakeService.getCurrentRegistrationPeriod();
        updatePeriodStatus(period);
        
        // Load retake requests
        loadRequests();
        
        // Update button states based on selection
        updateButtonStates();
    }
    
    private void updatePeriodStatus(RetakeRegistrationPeriod period) {
        if (period == null) {
            periodStatusLabel.setText("Trạng thái: Chưa mở đăng ký");
            openPeriodButton.setEnabled(true);
            closePeriodButton.setEnabled(false);
        } else {
            periodStatusLabel.setText(String.format("Trạng thái: %s (Từ %s đến %s)",
                    period.getStatusDescription(),
                    period.getStartTime().format(DATE_FORMATTER),
                    period.getEndTime().format(DATE_FORMATTER)));
            
            openPeriodButton.setEnabled(!period.isActive());
            closePeriodButton.setEnabled(period.isActive());
        }
    }
    
    private void loadRequests() {
        // Clear table
        requestsTableModel.setRowCount(0);
        
        // Load all requests
        List<RetakeRequest> requests = retakeService.getAllRetakeRequests();
        
        // Add to table
        for (RetakeRequest request : requests) {
            // Use empty string for tenLop if it's null
            String tenLop = (request.getTenLop() != null) ? request.getTenLop() : "";
            
            Object[] rowData = {
                    request.getRequestId(),
                    request.getMsv(),
                    request.getTenSinhVien(),
                    request.getMaLop(),
                    tenLop,
                    request.getNgayDangKi().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    request.getStatus().getValue()
            };
            requestsTableModel.addRow(rowData);
            
            // Set row color based on status
            int row = requestsTableModel.getRowCount() - 1;
            switch (request.getStatus()) {
                case CHẤP_NHẬN:
                    // Green for accepted
                    requestsTable.setForeground(new Color(0, 128, 0));
                    break;
                case TỪ_CHỐI:
                    // Red for rejected
                    requestsTable.setForeground(Color.RED);
                    break;
                default:
                    // Default color for others
                    requestsTable.setForeground(UIManager.getColor("Table.foreground"));
                    break;
            }
        }
        
        // Update button states
        updateButtonStates();
    }
    
    private void updateButtonStates() {
        int selectedRow = requestsTable.getSelectedRow();
        boolean hasSelection = selectedRow != -1;
        
        if (hasSelection) {
            // Only enable approval/rejection for "new" requests
            int modelRow = requestsTable.convertRowIndexToModel(selectedRow);
            String status = (String) requestsTableModel.getValueAt(modelRow, 6); // Status column
            boolean isNewStatus = status.equalsIgnoreCase("mới");
            
            approveButton.setEnabled(isNewStatus);
            rejectButton.setEnabled(isNewStatus);
        } else {
            approveButton.setEnabled(false);
            rejectButton.setEnabled(false);
        }
        
        // Add selection listener
        requestsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateButtonStates();
            }
        });
    }
    
    private void openRegistrationPeriod(ActionEvent e) {
        // Show dialog to configure registration period
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        
        JTextField startDateField = new JTextField();
        JTextField endDateField = new JTextField();
        JTextField descriptionField = new JTextField();
        
        panel.add(new JLabel("Ngày bắt đầu (dd/MM/yyyy HH:mm):"));
        panel.add(startDateField);
        panel.add(new JLabel("Ngày kết thúc (dd/MM/yyyy HH:mm):"));
        panel.add(endDateField);
        panel.add(new JLabel("Mô tả:"));
        panel.add(descriptionField);
        
        // Pre-fill with default values
        LocalDateTime now = LocalDateTime.now();
        startDateField.setText(now.format(DATE_FORMATTER));
        endDateField.setText(now.plusDays(7).format(DATE_FORMATTER));
        descriptionField.setText("Đăng ký học lại học kỳ " + (now.getMonthValue() <= 6 ? "II" : "I") + 
                                " năm học " + now.getYear() + "-" + (now.getYear() + 1));
        
        int result = JOptionPane.showConfirmDialog(
                this, panel, "Cấu hình thời gian đăng ký",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                LocalDateTime startDate = LocalDateTime.parse(startDateField.getText(), DATE_FORMATTER);
                LocalDateTime endDate = LocalDateTime.parse(endDateField.getText(), DATE_FORMATTER);
                String description = descriptionField.getText();
                
                boolean success = retakeService.openRegistrationPeriod(startDate, endDate, description);
                
                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "Đã mở đăng ký học lại thành công",
                            "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Không thể mở đăng ký học lại",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this,
                        "Định dạng ngày không hợp lệ. Sử dụng định dạng dd/MM/yyyy HH:mm",
                        "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
            } catch (ServiceException ex) {
                JOptionPane.showMessageDialog(this,
                        ex.getMessage(),
                        "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void closeRegistrationPeriod(ActionEvent e) {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn đóng đăng ký học lại?",
                "Xác nhận đóng đăng ký",
                JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = retakeService.closeRegistrationPeriod();
                
                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "Đã đóng đăng ký học lại thành công",
                            "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Không thể đóng đăng ký học lại",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (ServiceException ex) {
                JOptionPane.showMessageDialog(this,
                        ex.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void approveSelectedRequest(ActionEvent e) {
        processSelectedRequest(true);
    }
    
    private void rejectSelectedRequest(ActionEvent e) {
        processSelectedRequest(false);
    }
    
    private void processSelectedRequest(boolean approve) {
        int selectedRow = requestsTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        // Convert view index to model index (in case of sorting)
        int modelRow = requestsTable.convertRowIndexToModel(selectedRow);
        
        // Get request ID
        int requestId = (Integer) requestsTableModel.getValueAt(modelRow, 0);
        String msv = (String) requestsTableModel.getValueAt(modelRow, 1);
        String tenSinhVien = (String) requestsTableModel.getValueAt(modelRow, 2);
        
        // Ask for confirmation
        String action = approve ? "chấp nhận" : "từ chối";
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn " + action + " đăng ký học lại của sinh viên " +
                        tenSinhVien + " (" + msv + ")?",
                "Xác nhận " + action,
                JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success;
                if (approve) {
                    success = retakeService.approveRetakeRequest(requestId);
                } else {
                    success = retakeService.rejectRetakeRequest(requestId);
                }
                
                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "Đã " + action + " đăng ký học lại thành công",
                            "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Không thể " + action + " đăng ký học lại",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (ServiceException ex) {
                JOptionPane.showMessageDialog(this,
                        ex.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
} 