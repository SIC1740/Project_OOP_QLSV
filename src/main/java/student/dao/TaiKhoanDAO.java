package student.dao;

import student.model.TaiKhoan;
import java.sql.*;

public class TaiKhoanDAO {

    public TaiKhoan checkLogin(String tenDangNhap, String matKhau) {
        String sql = "SELECT * FROM TaiKhoan WHERE TenDangNhap = ? AND MatKhau = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tenDangNhap);
            stmt.setString(2, matKhau);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String vaiTro = rs.getString("VaiTro");
                return new TaiKhoan(tenDangNhap, matKhau, vaiTro);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // Đăng nhập thất bại
    }
}
