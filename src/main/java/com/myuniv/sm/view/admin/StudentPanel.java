package com.myuniv.sm.view.admin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class StudentPanel extends JPanel {
    private final DefaultTableModel model;
    private final JTable table;
    private final JButton btnAdd, btnEdit, btnDelete;

    public StudentPanel() {
        setLayout(new BorderLayout());

        model = new DefaultTableModel(
                new Object[] {"MSV", "Name", "DOB", "Email", "Phone", "Class"}, 0
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
        // TODO: StudentService.findAll() → addRow(msv, name, dob, email, phone, maLop)
    }

    private void onAdd()   { /* TODO */ }
    private void onEdit()  { /* TODO */ }
    private void onDelete(){ /* TODO */ }
}
