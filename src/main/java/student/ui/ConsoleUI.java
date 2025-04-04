package student.ui;

import student.dao.TaiKhoanDAO;
import student.model.TaiKhoan;

import java.util.Scanner;

public class ConsoleUI {

    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();
    private final Scanner scanner = new Scanner(System.in);

    public void start() {
        System.out.println("==== Hệ thống quản lý sinh viên ====");

        int attempts = 0;
        final int MAX_ATTEMPTS = 5;

        while (attempts < MAX_ATTEMPTS) {
            System.out.print("Tên đăng nhập: ");
            String username = scanner.nextLine();

            System.out.print("Mật khẩu: ");
            String password = scanner.nextLine();

            TaiKhoan tk = taiKhoanDAO.checkLogin(username, password);

            if (tk != null) {
                // Đăng nhập thành công → kiểm tra vai trò
                switch (tk.getVaiTro()) {
                    case "admin" -> hienThiGiaoDienAdmin();
                    case "sinhvien" -> hienThiGiaoDienSinhVien(tk.getTenDangNhap());
                    default -> System.out.println("❌ Vai trò không hợp lệ.");
                }
                return; // Thoát sau khi đăng nhập thành công
            } else {
                attempts++;
                System.out.println("❌ Sai thông tin đăng nhập. Lần thử: " + attempts + "/" + MAX_ATTEMPTS);
                if (attempts == MAX_ATTEMPTS) {
                    System.out.println("🔒 Bạn đã nhập sai quá 5 lần. Hệ thống thoát.");
                }
            }
        }
    }

    private void hienThiGiaoDienAdmin() {
        System.out.println("✅ Xin chào ADMIN! Đây là giao diện quản lý.");
        while (true) {
            System.out.println("\n===== MENU QUẢN TRỊ ADMIN =====");
            System.out.println("1. Xem danh sách sinh viên");
            System.out.println("2. Thêm sinh viên mới");
            System.out.println("3. Sửa thông tin sinh viên");
            System.out.println("4. Xoá sinh viên");
            System.out.println("5. Đăng xuất");

            System.out.print("Chọn chức năng (1-5): ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> xemDanhSachSinhVien();
                case "2" -> themSinhVien();
                case "3" -> suaThongTinSinhVien();
                case "4" -> xoaSinhVien();
                case "5" -> {
                    System.out.println("⬅️ Đã đăng xuất về trang đăng nhập.");
                    return; // quay lại hàm start()
                }
                default -> System.out.println("❌ Lựa chọn không hợp lệ. Vui lòng chọn lại.");
            }
        }
    }

    private void hienThiGiaoDienSinhVien(String mssv) {
        System.out.println("✅ Xin chào sinh viên: " + mssv);
        // TODO: Thêm các chức năng sinh viên ở đây
    }
    private void xemDanhSachSinhVien() {
        System.out.println("📋 [Fake] Hiển thị danh sách sinh viên...");
    }

    private void themSinhVien() {
        System.out.println("➕ [Fake] Giao diện thêm sinh viên...");
    }

    private void suaThongTinSinhVien() {
        System.out.println("✏️ [Fake] Giao diện sửa thông tin sinh viên...");
    }

    private void xoaSinhVien() {
        System.out.println("❌ [Fake] Giao diện xoá sinh viên...");
    }
}
