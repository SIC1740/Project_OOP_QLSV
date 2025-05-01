package com.myuniv.sm.view.admin;

import com.myuniv.sm.model.User;
import com.myuniv.sm.service.UserService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class UserAccountPanel extends JPanel {
    private final DefaultTableModel model;
    private final JTable table;
    private final JButton btnAdd, btnEdit, btnDelete;
    private final UserService userService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public UserAccountPanel() {
        setLayout(new BorderLayout());
        userService = new UserService();

        // Cấu hình bảng
        model = new DefaultTableModel(
                new Object[] {"ID", "Username", "Role", "Last Login"}, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.getColumnModel().getColumn(0).setMaxWidth(50); // ID column width
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Panel chứa các nút điều khiển
        JPanel btnPanel = new JPanel();
        btnAdd    = new JButton("Thêm");
        btnEdit   = new JButton("Sửa");
        btnDelete = new JButton("Xóa");
        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);
        add(btnPanel, BorderLayout.SOUTH);

        // Thêm listener cho các nút
        btnAdd.addActionListener(e -> onAdd());
        btnEdit.addActionListener(e -> onEdit());
        btnDelete.addActionListener(e -> onDelete());

        // Load dữ liệu khi khởi tạo
        loadData();
    }

    private void loadData() {
        model.setRowCount(0);
        List<User> users = userService.findAllUsers();
        for (User user : users) {
            model.addRow(new Object[]{
                    user.getUserId(),
                    user.getUsername(),
                    user.getRole(),
                    user.getLastLogin() != null ? DATE_FORMATTER.format(user.getLastLogin()) : "N/A"
            });
        }
    }

    private void onAdd() {
        // Sử dụng dialog để lấy thông tin user mới
        UserDialog dialog = new UserDialog((JFrame) SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            // Tạo user mới
            boolean success = userService.createUser(
                    dialog.getUsername(),
                    dialog.getPassword(),
                    dialog.getRole()
            );
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                        "Thêm user thành công", 
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadData(); // Reload data
            } else {
                JOptionPane.showMessageDialog(this, 
                        "Không thể thêm user. Username có thể đã tồn tại.", 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onEdit() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Hãy chọn user để sửa", 
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int userId = (Integer) model.getValueAt(selectedRow, 0);
        User user = userService.findUserById(userId);
        if (user == null) {
            JOptionPane.showMessageDialog(this, 
                    "Không tìm thấy thông tin user", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Hiển thị dialog chỉnh sửa
        UserDialog dialog = new UserDialog((JFrame) SwingUtilities.getWindowAncestor(this), user);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            boolean success = userService.updateUser(
                    userId,
                    dialog.getUsername(),
                    dialog.getPassword(),
                    dialog.getRole()
            );
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                        "Cập nhật user thành công", 
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadData(); // Reload data
            } else {
                JOptionPane.showMessageDialog(this, 
                        "Không thể cập nhật user. Username có thể đã tồn tại.", 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onDelete() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Hãy chọn user để xóa", 
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int userId = (Integer) model.getValueAt(selectedRow, 0);
        String username = (String) model.getValueAt(selectedRow, 1);
        
        // Xác nhận xóa
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa user '" + username + "'?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = userService.deleteUser(userId);
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                        "Xóa user thành công", 
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadData(); // Reload data
            } else {
                JOptionPane.showMessageDialog(this, 
                        "Không thể xóa user. User này có thể đang được sử dụng.", 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
