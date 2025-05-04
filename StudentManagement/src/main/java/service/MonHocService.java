package service;

import dao.MonHocDAO;
import model.MonHoc;
import java.sql.SQLException;
import java.util.List;

public class MonHocService {
    private MonHocDAO monHocDAO;

    public MonHocService() {
        this.monHocDAO = new MonHocDAO();
    }

    public boolean themMonHoc(MonHoc monHoc) {
        try {
            return monHocDAO.themMonHoc(monHoc);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean suaMonHoc(MonHoc monHoc) {
        try {
            return monHocDAO.suaMonHoc(monHoc);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean xoaMonHoc(String maMon) {
        try {
            return monHocDAO.xoaMonHoc(maMon);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<MonHoc> layTatCaMonHoc() {
        try {
            return monHocDAO.layTatCaMonHoc();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public MonHoc timMonHocTheoMa(String maMon) {
        try {
            return monHocDAO.timMonHocTheoMa(maMon);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}