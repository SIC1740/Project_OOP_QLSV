package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.LopHoc;

public class LopHocDAO {
    // Thêm lớp học
    public boolean themLopHoc(LopHoc lopHoc) throws SQLException {
        String sql = "INSERT INTO LopHoc (ma_lop, so_luong_sinh_vien) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, lopHoc.getMaLop());
            stmt.setInt(2, lopHoc.getSoLuongSinhVien());
            return stmt.executeUpdate() > 0;
        }
    }

    // Sửa lớp học
    public boolean suaLopHoc(LopHoc lopHoc) throws SQLException {
        String sql = "UPDATE LopHoc SET so_luong_sinh_vien = ? WHERE ma_lop = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, lopHoc.getSoLuongSinhVien());
            stmt.setString(2, lopHoc.getMaLop());
            return stmt.executeUpdate() > 0;
        }
    }

    // Xóa lớp học
    public boolean xoaLopHoc(String maLop) throws SQLException {
        String sql = "DELETE FROM LopHoc WHERE ma_lop = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maLop);
            return stmt.executeUpdate() > 0;
        }
    }

    // Lấy tất cả lớp học
    public List<LopHoc> layTatCaLopHoc() throws SQLException {
        List<LopHoc> dsLopHoc = new ArrayList<>();
        String sql = "SELECT * FROM LopHoc";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                LopHoc lopHoc = new LopHoc(
                    rs.getString("ma_lop"),
                    rs.getInt("so_luong_sinh_vien")
                );
                dsLopHoc.add(lopHoc);
            }
        }
        return dsLopHoc;
    }
    
    public boolean kiemTraLopHocTonTai(String maLop) throws SQLException {
        String sql = "SELECT 1 FROM LopHoc WHERE ma_lop = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maLop);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    // Tìm lớp học theo mã
    public LopHoc timLopHocTheoMa(String maLop) throws SQLException {
        String sql = "SELECT * FROM LopHoc WHERE ma_lop = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maLop);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new LopHoc(
                        rs.getString("ma_lop"),
                        rs.getInt("so_luong_sinh_vien")
                    );
                }
            }
        }
        return null;
    }
}