package dao;

import model.MonHoc;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MonHocDAO {
    // Thêm môn học
    public boolean themMonHoc(MonHoc monHoc) throws SQLException {
        String sql = "INSERT INTO MonHoc (ma_mon, ten_mon, so_tin_chi) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, monHoc.getMaMon());
            stmt.setString(2, monHoc.getTenMon());
            stmt.setInt(3, monHoc.getSoTinChi());
            return stmt.executeUpdate() > 0;
        }
    }

    // Sửa môn học
    public boolean suaMonHoc(MonHoc monHoc) throws SQLException {
        String sql = "UPDATE MonHoc SET ten_mon = ?, so_tin_chi = ? WHERE ma_mon = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, monHoc.getTenMon());
            stmt.setInt(2, monHoc.getSoTinChi());
            stmt.setString(3, monHoc.getMaMon());
            return stmt.executeUpdate() > 0;
        }
    }

    // Xóa môn học
    public boolean xoaMonHoc(String maMon) throws SQLException {
        String sql = "DELETE FROM MonHoc WHERE ma_mon = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maMon);
            return stmt.executeUpdate() > 0;
        }
    }

    // Lấy tất cả môn học
    public List<MonHoc> layTatCaMonHoc() throws SQLException {
        List<MonHoc> dsMonHoc = new ArrayList<>();
        String sql = "SELECT * FROM MonHoc";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                MonHoc monHoc = new MonHoc(
                    rs.getString("ma_mon"),
                    rs.getString("ten_mon"),
                    rs.getInt("so_tin_chi")
                );
                dsMonHoc.add(monHoc);
            }
        }
        return dsMonHoc;
    }

    // Thêm vào MonHocDAO.java
    public boolean themMonHoc(MonHoc monHoc) throws SQLException {
        if (monHoc == null || monHoc.getMaMon() == null || monHoc.getMaMon().isEmpty()) {
            return false;
        }
    
        String sql = "INSERT INTO MonHoc (ma_mon, ten_mon, so_tin_chi) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, monHoc.getMaMon());
            stmt.setString(2, monHoc.getTenMon());
            stmt.setInt(3, monHoc.getSoTinChi());
            return stmt.executeUpdate() > 0;
        }
}

    // Tìm môn học theo mã
    public MonHoc timMonHocTheoMa(String maMon) throws SQLException {
        String sql = "SELECT * FROM MonHoc WHERE ma_mon = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maMon);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new MonHoc(
                        rs.getString("ma_mon"),
                        rs.getString("ten_mon"),
                        rs.getInt("so_tin_chi")
                    );
                }
            }
        }
        return null;
    }
}