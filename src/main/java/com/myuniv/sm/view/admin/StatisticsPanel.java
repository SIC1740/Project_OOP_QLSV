// File: src/main/java/com/myuniv/sm/view/admin/StatisticsPanel.java
package com.myuniv.sm.view.admin;

import com.myuniv.sm.dao.ScoreDao;
import com.myuniv.sm.dao.impl.ScoreDaoJdbc;
import com.myuniv.sm.dao.StudentDao;
import com.myuniv.sm.dao.impl.StudentDaoJdbc;
import com.myuniv.sm.model.Score;
import com.myuniv.sm.model.Student;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StatisticsPanel extends JPanel {
    private final ScoreDao scoreDao = new ScoreDaoJdbc();
    private final StudentDao studentDao = new StudentDaoJdbc() {
        @Override
        public boolean save(Student student) {
            return false;
        }
    };
    private final DefaultTableModel model;
    private final JTable table;

    public StatisticsPanel() {
        setLayout(new BorderLayout());
        model = new DefaultTableModel(
                new Object[]{"Thứ hạng","ID","MSV","Họ tên","Mã lớp","Điểm TB"},0
        ) { @Override public boolean isCellEditable(int r,int c){return false;} };
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);
        loadData(10);
    }

    private void loadData(double percent) {
        model.setRowCount(0);
        List<Score> list = scoreDao.findAll();
        list.sort((a,b)->Double.compare(b.getAvgScore(), a.getAvgScore()));
        int limit = (int)Math.ceil(list.size()*percent/100.0);
        for(int i=0;i<limit;i++){
            Score s=list.get(i);
            Student st=studentDao.findByMsv(s.getMsv());
            model.addRow(new Object[]{
                    i+1, s.getId(), s.getMsv(),
                    st!=null?st.getHoTen():"",
                    st!=null?st.getMaLop():"",
                    s.getAvgScore()
            });
        }
    }
}
