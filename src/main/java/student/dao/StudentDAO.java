package student.dao;

import java.sql.*;
import java.text.SimpleDateFormat;

public class StudentDAO {

    // Hiển thị tất cả sinh viên
    public static void displayAllStudents() {
        String query = "SELECT * FROM ThongTinSinhVien";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.println("Danh sách sinh viên:");
            System.out.printf("%-5s %-10s %-30s %-15s %-10s\n", "TT", "Mã SV", "Họ Tên", "Ngày Sinh", "Lớp");
            System.out.println("------------------------------------------------------------");

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            while (rs.next()) {
                int thuTu = rs.getInt("ThuTu");
                String maSV = rs.getString("MaSV");
                String hoTen = rs.getString("HoTen");
                Date ngaySinh = rs.getDate("NgaySinh");
                String lop = rs.getString("Lop");

                String ngaySinhStr = ngaySinh != null ? dateFormat.format(ngaySinh) : "N/A";

                System.out.printf("%-5d %-10s %-30s %-15s %-10s\n", thuTu, maSV, hoTen, ngaySinhStr, lop);
            }

        } catch (SQLException e) {
            System.err.println("Lỗi truy vấn: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Tìm sinh viên theo mã số
    public static void findStudentByID(String studentID) {
        String query = "SELECT * FROM ThongTinSinhVien WHERE MaSV = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, studentID);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Thông tin sinh viên:");
                    System.out.println("Thứ tự: " + rs.getInt("ThuTu"));
                    System.out.println("Mã SV: " + rs.getString("MaSV"));
                    System.out.println("Họ tên: " + rs.getString("HoTen"));
                    System.out.println("Ngày sinh: " + rs.getDate("NgaySinh"));
                    System.out.println("Lớp: " + rs.getString("Lop"));
                } else {
                    System.out.println("Không tìm thấy sinh viên với mã: " + studentID);
                }
            }

        } catch (SQLException e) {
            System.err.println("Lỗi truy vấn: " + e.getMessage());
            e.printStackTrace();
        }
    }
}