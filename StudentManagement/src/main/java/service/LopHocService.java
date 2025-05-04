package service;

import dao.LopHocDAO;
import model.LopHoc;
import java.sql.SQLException;
import java.util.List;

public class LopHocService {
    private LopHocDAO lopHocDAO;

    public LopHocService() {
        this.lopHocDAO = new LopHocDAO();
    }

    public boolean themLopHoc(LopHoc lopHoc) {
        try {
            return lopHocDAO.themLopHoc(lopHoc);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean suaLopHoc(LopHoc lopHoc) {
        try {
            return lopHocDAO.suaLopHoc(lopHoc);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean xoaLopHoc(String maLop) {
        try {
            return lopHocDAO.xoaLopHoc(maLop);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<LopHoc> layTatCaLopHoc() {
        try {
            return lopHocDAO.layTatCaLopHoc();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public LopHoc timLopHocTheoMa(String maLop) {
        try {
            return lopHocDAO.timLopHocTheoMa(maLop);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}