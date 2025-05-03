package com.myuniv.sm.view.admin;

import com.myuniv.sm.model.Student;
import com.myuniv.sm.service.ServiceException;
import com.myuniv.sm.service.StudentService;
import com.myuniv.sm.view.admin.StudentDetailPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StudentPanel extends JPanel {
    private static final Logger logger = Logger.getLogger(StudentPanel.class.getName());
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final DefaultTableModel model;
    private final JTable table;
    private final JButton btnAdd, btnEdit, btnDelete;
    private final StudentService studentService;

    public StudentPanel() {
        setLayout(new BorderLayout());
        studentService = new StudentService();

        model = new DefaultTableModel(
                new Object[]{"MSV","Họ Tên","Ngày Sinh","Email","SĐT","Lớp"}, 0
        ) { @Override public boolean isCellEditable(int r,int c){return false;} };
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel btns = new JPanel();
        btnAdd = new JButton("Thêm");
        btnEdit = new JButton("Sửa");
        btnDelete = new JButton("Xóa");
        btns.add(btnAdd);
        btns.add(btnEdit);
        btns.add(btnDelete);
        add(btns, BorderLayout.SOUTH);

        // Thêm/Sửa mở tab detail
        btnAdd.addActionListener(e -> openDetailTab(null));
        btnEdit.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r < 0) {
                JOptionPane.showMessageDialog(this, "Chọn sinh viên để sửa");
                return;
            }
            String msv = (String) model.getValueAt(r, 0);
            Student s = studentService.findByMsv(msv);
            if (s != null) openDetailTab(s);
        });
        // Xóa như cũ
        btnDelete.addActionListener(e -> onDelete());

        loadData();
    }

    public void loadData() {
        model.setRowCount(0);
        try {
            List<Student> list = studentService.findAll();
            for (Student s : list) {
                model.addRow(new Object[]{
                        s.getMsv(), s.getHoTen(),
                        s.getNgaySinh()!=null?DATE_FORMAT.format(s.getNgaySinh()):"",
                        s.getEmail(), s.getSoDienThoai(), s.getMaLop()
                });
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Lỗi tải sinh viên", ex);
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: "+ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDelete() {
        int r = table.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Chọn sinh viên để xóa");
            return;
        }
        String msv = (String) model.getValueAt(r, 0);
        int c = JOptionPane.showConfirmDialog(this, "Xác nhận xóa?", "Xóa", JOptionPane.YES_NO_OPTION);
        if (c != JOptionPane.YES_OPTION) return;
        try {
            studentService.deleteStudent(msv);
            loadData();
            JOptionPane.showMessageDialog(this, "Xóa thành công");
        } catch (ServiceException se) {
            JOptionPane.showMessageDialog(this, se.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Mở 1 tab chi tiết dùng StudentDetailPanel để Thêm/Sửa
     */
    private void openDetailTab(Student exist) {
        Container c = this.getParent();
        while (c != null && !(c instanceof JTabbedPane)) {
            c = c.getParent();
        }
        if (!(c instanceof JTabbedPane)) return;
        JTabbedPane tabs = (JTabbedPane) c;
        String title = exist == null ? "Thêm Sinh viên" : "Sửa Sinh viên " + exist.getMsv();
        StudentDetailPanel detail = new StudentDetailPanel(exist);
        tabs.addTab(title, detail);
        tabs.setSelectedComponent(detail);
    }
}
