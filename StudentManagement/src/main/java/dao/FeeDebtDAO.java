package dao;

import model.FeeDebt;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FeeDebtDAO {
    // Thống kê nợ học phí
    public List<FeeDebt> thongKeNoHocPhi() throws SQLException {
        List<FeeDebt> dsNoHocPhi = new ArrayList<>();
        String sql = "SELECT * FROM FeeDebt WHERE status = 'chưa đóng'";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                FeeDebt feeDebt = new FeeDebt(
                    rs.getInt("debt_id"),
                    rs.getString("msv"),
                    rs.getString("khoan_thu"),
                    rs.getDouble("so_tien"),
                    rs.getDate("han_thu"),
                    rs.getString("status")
                );
                dsNoHocPhi.add(feeDebt);
            }
        }
        return dsNoHocPhi;
    }

    // Thống kê nợ học phí theo MSV
    public List<FeeDebt> thongKeNoHocPhiTheoMSV(String msv) throws SQLException {
        List<FeeDebt> dsNoHocPhi = new ArrayList<>();
        String sql = "SELECT * FROM FeeDebt WHERE msv = ? AND status = 'chưa đóng'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, msv);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    FeeDebt feeDebt = new FeeDebt(
                        rs.getInt("debt_id"),
                        rs.getString("msv"),
                        rs.getString("khoan_thu"),
                        rs.getDouble("so_tien"),
                        rs.getDate("han_thu"),
                        rs.getString("status")
                    );
                    dsNoHocPhi.add(feeDebt);
                }
            }
        }
        return dsNoHocPhi;
    }
        // Thêm vào FeeDebtDAO.java
    public double tinhTongNoHocPhi(String msv) throws SQLException {
        String sql = "SELECT SUM(so_tien) FROM FeeDebt WHERE msv = ? AND status = 'chưa đóng'";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, msv);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : 0;
            }
        }
    }
}