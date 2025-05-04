package com.myuniv.sm.view.student;

import com.myuniv.sm.model.FeeDebt;
import com.myuniv.sm.model.RetakeRequest;
import com.myuniv.sm.model.RetakeRegistrationPeriod;
import com.myuniv.sm.service.RetakeService;
import com.myuniv.sm.service.ServiceException;
import com.myuniv.sm.service.StudentService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Panel for students to register for class retakes
 */
public class StudentRetakePanel extends JPanel {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    private final String msv;
    private final RetakeService retakeService;
    private final StudentService studentService;
    
    private JTable feeDebtsTable;
    private DefaultTableModel feeDebtsTableModel;
    private JTable retakeRequestsTable;
    private DefaultTableModel retakeRequestsTableModel;
    private JButton registerButton;
    private JButton cancelButton;
    private JLabel periodStatusLabel;
    private JLabel eligibilityLabel;
    
    public StudentRetakePanel(String msv) {
        this.msv = msv;
        this.retakeService = new RetakeService();
        this.studentService = new StudentService();
        
        initUI();
        loadData();
    }
    
    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Registration period status at top
        JPanel periodPanel = createPeriodPanel();
        add(periodPanel, BorderLayout.NORTH);
        
        // Main split pane in center
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        mainSplitPane.setDividerLocation(200);
        mainSplitPane.setResizeWeight(0.3);
        
        // Top split pane for fee debts
        JPanel feeDebtsPanel = createFeeDebtsPanel();
        mainSplitPane.setTopComponent(feeDebtsPanel);
        
        // Bottom panel for retake requests
        JPanel retakeRequestsPanel = createRetakeRequestsPanel();
        mainSplitPane.setBottomComponent(retakeRequestsPanel);
        
        add(mainSplitPane, BorderLayout.CENTER);
        
        // Actions panel at bottom
        JPanel actionsPanel = createActionsPanel();
        add(actionsPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createPeriodPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Thông tin đăng ký học lại"),
                new EmptyBorder(10, 10, 10, 10)));
        
        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        
        periodStatusLabel = new JLabel("Trạng thái: Đang kiểm tra...");
        infoPanel.add(periodStatusLabel);
        
        eligibilityLabel = new JLabel("Bạn có thể đăng ký học lại: Đang kiểm tra...");
        infoPanel.add(eligibilityLabel);
        
        panel.add(infoPanel, BorderLayout.CENTER);
        
        JButton refreshButton = new JButton("Làm mới");
        refreshButton.addActionListener(e -> loadData());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(refreshButton);
        panel.add(buttonPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createFeeDebtsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Các khoản học phí chưa đóng"),
                new EmptyBorder(10, 10, 10, 10)));
        
        // Table model for fee debts
        String[] columnNames = {"ID", "Khoản thu", "Số tiền", "Hạn thu", "Trạng thái"};
        feeDebtsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        feeDebtsTable = new JTable(feeDebtsTableModel);
        feeDebtsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Add a listener for double click on fee debt
        feeDebtsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = feeDebtsTable.getSelectedRow();
                    if (row != -1) {
                        String khoanThu = (String) feeDebtsTable.getValueAt(row, 1);
                        JOptionPane.showMessageDialog(
                                StudentRetakePanel.this,
                                "Bạn cần đăng ký học lại cho khoản " + khoanThu + 
                                ".\nSử dụng nút \"Đăng ký học lại\" ở dưới để tiến hành đăng ký.",
                                "Thông tin học lại",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(feeDebtsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JLabel infoLabel = new JLabel("Bạn phải đăng ký học lại đối với các khoản học phí chưa đóng. Bấm đúp vào khoản thu để xem chi tiết.");
        infoLabel.setForeground(Color.RED);
        panel.add(infoLabel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createRetakeRequestsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Danh sách yêu cầu đăng ký học lại của bạn"),
                new EmptyBorder(10, 10, 10, 10)));
        
        // Table model for retake requests
        String[] columnNames = {"ID", "Mã lớp", "Tên lớp", "Ngày đăng ký", "Trạng thái"};
        retakeRequestsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        retakeRequestsTable = new JTable(retakeRequestsTableModel);
        retakeRequestsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Selection listener to enable/disable cancel button
        retakeRequestsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = retakeRequestsTable.getSelectedRow();
                if (row != -1) {
                    // Enable cancel button only for new requests
                    String status = (String) retakeRequestsTable.getValueAt(row, 4);
                    cancelButton.setEnabled(status.equalsIgnoreCase("mới"));
                } else {
                    cancelButton.setEnabled(false);
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(retakeRequestsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Add legend
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
        
        registerButton = new JButton("Đăng ký học lại");
        registerButton.addActionListener(this::registerForRetake);
        
        cancelButton = new JButton("Hủy đăng ký");
        cancelButton.setEnabled(false);
        cancelButton.addActionListener(this::cancelRetakeRequest);
        
        panel.add(registerButton);
        panel.add(cancelButton);
        
        return panel;
    }
    
    private void loadData() {
        // Check if registration is open
        RetakeRegistrationPeriod period = retakeService.getCurrentRegistrationPeriod();
        updatePeriodStatus(period);
        
        // Check eligibility
        boolean isEligible = retakeService.isEligibleForRetake(msv);
        updateEligibilityStatus(isEligible);
        
        // Load fee debts
        loadFeeDebts();
        
        // Load retake requests
        loadRetakeRequests();
        
        // Update button states
        boolean canRegister = retakeService.isRegistrationOpen() && isEligible;
        registerButton.setEnabled(canRegister);
        
        int selectedRequestRow = retakeRequestsTable.getSelectedRow();
        if (selectedRequestRow != -1) {
            String status = (String) retakeRequestsTable.getValueAt(selectedRequestRow, 4);
            cancelButton.setEnabled(status.equalsIgnoreCase("mới"));
        } else {
            cancelButton.setEnabled(false);
        }
    }
    
    private void updatePeriodStatus(RetakeRegistrationPeriod period) {
        if (period == null) {
            periodStatusLabel.setText("Trạng thái đợt đăng ký: Chưa mở đăng ký học lại");
            periodStatusLabel.setForeground(Color.RED);
        } else if (period.isRegistrationOpen()) {
            periodStatusLabel.setText(String.format("Trạng thái đợt đăng ký: Đang mở (từ %s đến %s)",
                    period.getStartTime().format(DATE_FORMATTER),
                    period.getEndTime().format(DATE_FORMATTER)));
            periodStatusLabel.setForeground(new Color(0, 128, 0)); // Dark green
        } else {
            periodStatusLabel.setText(String.format("Trạng thái đợt đăng ký: %s (từ %s đến %s)",
                    period.getStatusDescription(),
                    period.getStartTime().format(DATE_FORMATTER),
                    period.getEndTime().format(DATE_FORMATTER)));
            periodStatusLabel.setForeground(Color.BLUE);
        }
    }
    
    private void updateEligibilityStatus(boolean isEligible) {
        if (isEligible) {
            eligibilityLabel.setText("Trạng thái: Bạn có thể đăng ký học lại (có học phí chưa đóng)");
            eligibilityLabel.setForeground(new Color(0, 128, 0)); // Dark green
        } else {
            eligibilityLabel.setText("Trạng thái: Bạn không thể đăng ký học lại (không có học phí chưa đóng)");
            eligibilityLabel.setForeground(Color.RED);
        }
    }
    
    private void loadFeeDebts() {
        feeDebtsTableModel.setRowCount(0);
        
        List<FeeDebt> feeDebts = retakeService.getUnpaidFeeDebts(msv);
        
        for (FeeDebt feeDebt : feeDebts) {
            Object[] rowData = {
                    feeDebt.getDebtId(),
                    feeDebt.getKhoanThu(),
                    feeDebt.getSoTien(),
                    feeDebt.getHanThu().format(DATE_FORMATTER),
                    feeDebt.getStatus().getValue()
            };
            feeDebtsTableModel.addRow(rowData);
        }
    }
    
    private void loadRetakeRequests() {
        retakeRequestsTableModel.setRowCount(0);
        
        List<RetakeRequest> requests = retakeService.getRetakeRequestsByStudent(msv);
        
        for (RetakeRequest request : requests) {
            // Use empty string for tenLop if it's null
            String tenLop = (request.getTenLop() != null) ? request.getTenLop() : "";
            
            Object[] rowData = {
                    request.getRequestId(),
                    request.getMaLop(),
                    tenLop,
                    request.getNgayDangKi().format(DATE_FORMATTER),
                    request.getStatus().getValue()
            };
            retakeRequestsTableModel.addRow(rowData);
            
            // Set row color based on status
            int row = retakeRequestsTableModel.getRowCount() - 1;
            switch (request.getStatus()) {
                case CHẤP_NHẬN:
                    // Green for accepted
                    retakeRequestsTable.setForeground(new Color(0, 128, 0));
                    break;
                case TỪ_CHỐI:
                    // Red for rejected
                    retakeRequestsTable.setForeground(Color.RED);
                    break;
                default:
                    // Default color for others
                    retakeRequestsTable.setForeground(UIManager.getColor("Table.foreground"));
                    break;
            }
        }
    }
    
    private void registerForRetake(ActionEvent e) {
        if (!retakeService.isRegistrationOpen()) {
            JOptionPane.showMessageDialog(this,
                    "Đăng ký học lại hiện đang đóng. Vui lòng thử lại sau.",
                    "Không thể đăng ký", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!retakeService.isEligibleForRetake(msv)) {
            JOptionPane.showMessageDialog(this,
                    "Bạn không có học phí chưa đóng nên không thể đăng ký học lại.",
                    "Không thể đăng ký", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get student's class directly
        String maLop = studentService.getStudentClass(msv);
        
        if (maLop == null || maLop.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Không tìm thấy lớp học của bạn. Vui lòng liên hệ quản trị viên.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Confirm registration
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn đăng ký học lại cho lớp " + maLop + "?\n" +
                "Đăng ký này cần được admin phê duyệt trước khi có hiệu lực.",
                "Xác nhận đăng ký",
                JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Use the direct registration method with the student's class ID
                boolean success = retakeService.registerForRetakeDirectClass(msv, maLop);
                
                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "Đăng ký học lại thành công. Vui lòng đợi phê duyệt từ quản trị viên.",
                            "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    loadRetakeRequests();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Không thể đăng ký học lại. Vui lòng thử lại sau.",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (ServiceException ex) {
                JOptionPane.showMessageDialog(this,
                        ex.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void cancelRetakeRequest(ActionEvent e) {
        int selectedRow = retakeRequestsTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        int requestId = (Integer) retakeRequestsTable.getValueAt(selectedRow, 0);
        String maLop = (String) retakeRequestsTable.getValueAt(selectedRow, 1);
        
        // Confirm cancellation
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn hủy đăng ký học lại lớp " + maLop + "?",
                "Xác nhận hủy đăng ký",
                JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = retakeService.cancelRetakeRequest(requestId);
                
                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "Hủy đăng ký học lại thành công.",
                            "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    loadRetakeRequests();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Không thể hủy đăng ký học lại. Vui lòng thử lại sau.",
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