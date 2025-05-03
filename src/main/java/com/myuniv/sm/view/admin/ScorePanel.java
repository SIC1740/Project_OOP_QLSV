// File: src/main/java/com/myuniv/sm/view/admin/ScorePanel.java
package com.myuniv.sm.view.admin;

import com.myuniv.sm.dao.ScoreDao;
import com.myuniv.sm.dao.impl.ScoreDaoJdbc;
import com.myuniv.sm.model.Score;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;

public class ScorePanel extends JPanel {
    private final ScoreDao dao = new ScoreDaoJdbc();
    private final JTable table;
    private final DefaultTableModel model;

    public ScorePanel() {
        setLayout(new BorderLayout());
        model = new DefaultTableModel(
                new Object[]{"ID","MSV","Điểm TB","Ngày tạo","Ngày sửa"}, 0
        ) { @Override public boolean isCellEditable(int r,int c){return false;} };
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel btns = new JPanel();
        JButton add = new JButton("Thêm"), edit = new JButton("Sửa"), del = new JButton("Xóa");
        btns.add(add); btns.add(edit); btns.add(del);
        add(btns, BorderLayout.SOUTH);
        add.addActionListener(e -> openDialog(null));
        edit.addActionListener(e -> {
            int r = table.getSelectedRow(); if(r<0)return;
            int id = (int)model.getValueAt(r,0);
            openDialog(dao.findById(id));
        });
        del.addActionListener(e -> {
            int r = table.getSelectedRow(); if(r<0)return;
            int id = (int)model.getValueAt(r,0);
            if(JOptionPane.showConfirmDialog(this,"Xác nhận xóa?","Xóa",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
                dao.delete(id); loadData();
            }
        });
        loadData();
    }

    private void loadData() {
        model.setRowCount(0);
        for(Score s: dao.findAll()) {
            model.addRow(new Object[]{
                    s.getId(), s.getMsv(), s.getAvgScore(),
                    s.getDateCreated(), s.getDateModified()
            });
        }
    }

    private void openDialog(Score exist) {
        Frame owner = JOptionPane.getFrameForComponent(this);
        JDialog dlg = new JDialog(owner, exist==null?"Thêm Điểm TB":"Sửa Điểm TB", true);
        dlg.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets=new Insets(4,4,4,4); g.fill=GridBagConstraints.HORIZONTAL;
        JTextField txtMsv=new JTextField(15), txtScore=new JTextField(5);
        if(exist!=null){ txtMsv.setText(exist.getMsv()); txtMsv.setEditable(false);
            txtScore.setText(String.valueOf(exist.getAvgScore())); }
        g.gridx=0; g.gridy=0; dlg.add(new JLabel("MSV:"),g);
        g.gridx=1; dlg.add(txtMsv,g);
        g.gridx=0; g.gridy=1; dlg.add(new JLabel("Điểm TB:"),g);
        g.gridx=1; dlg.add(txtScore,g);
        JPanel p=new JPanel(); JButton ok=new JButton("OK"), cancel=new JButton("Cancel");
        p.add(ok); p.add(cancel); g.gridx=0; g.gridy=2; g.gridwidth=2; dlg.add(p,g);
        ok.addActionListener(a->{
            if(exist!=null) {
                if(exist.getDateCreated().plusDays(3).isBefore(LocalDate.now())){
                    JOptionPane.showMessageDialog(dlg,"Hết hạn sửa (3 ngày)"); return; }
                exist.setAvgScore(Double.parseDouble(txtScore.getText().trim()));
                exist.setDateModified(LocalDate.now()); dao.update(exist);
            } else {
                Score s=new Score();
                s.setMsv(txtMsv.getText().trim());
                s.setAvgScore(Double.parseDouble(txtScore.getText().trim()));
                s.setDateCreated(LocalDate.now()); s.setDateModified(LocalDate.now());
                dao.create(s);
            }
            loadData(); dlg.dispose();
        });
        cancel.addActionListener(a->dlg.dispose());
        dlg.pack(); dlg.setLocationRelativeTo(this); dlg.setVisible(true);
    }
}