package com.myuniv.sm.view.admin;

import com.myuniv.sm.model.Student;
import com.myuniv.sm.service.ServiceException;
import com.myuniv.sm.service.StudentService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Form chi tiết dùng cho Thêm / Sửa Sinh viên
 */
public class StudentDetailPanel extends JPanel {
    private final StudentService studentService = new StudentService();
    private final boolean isNew;
    private Student student;    // nếu sửa, tồn tại; nếu thêm mới, null

    // Form fields
    private final JTextField txtMsv  = new JTextField(10);
    private final JTextField txtName = new JTextField(20);
    private final JTextField txtDob  = new JTextField(10);
    private final JTextField txtEmail= new JTextField(20);
    private final JTextField txtPhone= new JTextField(15);
    private final JTextField txtClass= new JTextField(10);
    private final JButton btnSave   = new JButton("Lưu");
    private final JButton btnCancel = new JButton("Hủy");

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public StudentDetailPanel(Student existing) {
        this.student = existing;
        this.isNew = existing == null;
        buildUI();
        if (!isNew) loadFormData();
    }

    private void buildUI() {
        setLayout(new BorderLayout(10,10));
        setBorder(new EmptyBorder(15,15,15,15));

        JPanel form = new JPanel(new GridBagLayout());
        var gbc = new GridBagConstraints();
        gbc.insets = new Insets(4,4,4,4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        String[] labels = {"MSV","Họ tên","Ngày sinh (yyyy-MM-dd)","Email","SĐT","Mã lớp"};
        JTextField[] fields = {txtMsv,txtName,txtDob,txtEmail,txtPhone,txtClass};
        for(int i=0;i<labels.length;i++){
            gbc.gridx=0; gbc.gridy=i;
            form.add(new JLabel(labels[i]+":"), gbc);
            gbc.gridx=1;
            form.add(fields[i], gbc);
        }
        // MSV khóa khi sửa
        if (!isNew) {
            txtMsv.setEditable(false);
        }

        // Nút Lưu / Hủy
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btns.add(btnSave);
        btns.add(btnCancel);
        gbc.gridx=0; gbc.gridy=labels.length; gbc.gridwidth=2;
        form.add(btns, gbc);

        add(form, BorderLayout.CENTER);

        // Action
        btnSave.addActionListener(e-> onSave());
        btnCancel.addActionListener(e-> {
            // chỉ tắt tab (AdminFrame sẽ xử lý)
            Container c = this.getParent();
            if(c instanceof JTabbedPane) {
                JTabbedPane tabs = (JTabbedPane)c;
                int idx = tabs.indexOfComponent(this);
                if(idx>=0) tabs.removeTabAt(idx);
            }
        });
    }

    private void loadFormData() {
        txtMsv  .setText(student.getMsv());
        txtName .setText(student.getHoTen());
        txtDob  .setText(student.getNgaySinh().format(DATE_FMT));
        txtEmail.setText(student.getEmail());
        txtPhone.setText(student.getSoDienThoai());
        txtClass.setText(student.getMaLop());
    }

    private void onSave() {
        try {
            Student s = new Student();
            s.setMsv(txtMsv.getText().trim());
            s.setHoTen(txtName.getText().trim());
            s.setNgaySinh(LocalDate.parse(txtDob.getText().trim(), DATE_FMT));
            s.setEmail(txtEmail.getText().trim());
            s.setSoDienThoai(txtPhone.getText().trim());
            s.setMaLop(txtClass.getText().trim());

            if(isNew) {
                studentService.createStudent(s);
                JOptionPane.showMessageDialog(this, "Đã thêm sinh viên");
            } else {
                s.setSTT(student.getSTT());
                studentService.updateStudent(s);
                JOptionPane.showMessageDialog(this, "Đã cập nhật sinh viên");
            }

            // Sau khi lưu xong, làm mới tab danh sách
            Container parent = this.getParent();
            if (parent instanceof JTabbedPane) {
                JTabbedPane tabs = (JTabbedPane) parent;
                // 1) Reload dữ liệu trên StudentPanel
                for (int i = 0; i < tabs.getTabCount(); i++) {
                    Component c = tabs.getComponentAt(i);
                    if (c instanceof StudentPanel) {
                        ((StudentPanel)c).loadData();
                        // 2) Chuyển về tab danh sách
                        tabs.setSelectedIndex(i);
                        break;
                    }
                }
                // 3) Đóng tab form chi tiết
                int idx = tabs.indexOfComponent(this);
                if (idx >= 0) tabs.removeTabAt(idx);
            }


        } catch(ServiceException se) {
            JOptionPane.showMessageDialog(this, se.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch(Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi định dạng: "+ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
