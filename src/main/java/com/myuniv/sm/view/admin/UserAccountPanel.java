package com.myuniv.sm.view.admin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class UserAccountPanel extends JPanel {
    private final DefaultTableModel model;
    private final JTable table;
    private final JButton btnAdd, btnEdit, btnDelete;

    public UserAccountPanel() {
        setLayout(new BorderLayout());

        model = new DefaultTableModel(
                new Object[] {"Username", "Role", "Last Login"}, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        btnAdd    = new JButton("Add");
        btnEdit   = new JButton("Edit");
        btnDelete = new JButton("Delete");
        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);
        add(btnPanel, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> onAdd());
        btnEdit.addActionListener(e -> onEdit());
        btnDelete.addActionListener(e -> onDelete());

        loadData();
    }

    private void loadData() {
        model.setRowCount(0);
        // TODO: gọi UserService.findAll() rồi
        // for each user: model.addRow(new Object[]{u.getUsername(), u.getRole(), u.getLastLogin()});
    }

    private void onAdd() {
        // TODO: mở AddUserDialog, nếu OK thì loadData()
    }

    private void onEdit() {
        int i = table.getSelectedRow();
        if (i < 0) {
            JOptionPane.showMessageDialog(this, "Chọn user để sửa");
            return;
        }
        String username = (String) model.getValueAt(i, 0);
        // TODO: mở EditUserDialog với username, nếu OK thì loadData()
    }

    private void onDelete() {
        int i = table.getSelectedRow();
        if (i < 0) {
            JOptionPane.showMessageDialog(this, "Chọn user để xóa");
            return;
        }
        String username = (String) model.getValueAt(i, 0);
        if (JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa " + username + "?",
                "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            // TODO: UserService.delete(username); loadData();
        }
    }
}
