package src.main.java;

import java.util.List;
import java.util.Scanner;
import model.*;
import service.*;
import dao.DatabaseConnection;

public class MainApp {
    private static Scanner scanner = new Scanner(System.in);
    private static MonHocService monHocService = new MonHocService();
    private static LopHocService lopHocService = new LopHocService();
    private static FeeDebtService feeDebtService = new FeeDebtService();

    public static void main(String[] args) {
        if (!DatabaseConnection.testConnection()) {
            System.out.println("Lỗi kết nối database. Vui lòng kiểm tra lại!");
            return;
        }

        boolean running = true;
        while (running) {
            System.out.println("\n=== HỆ THỐNG QUẢN LÝ SINH VIÊN ===");
            System.out.println("1. Quản lý môn học");
            System.out.println("2. Quản lý lớp học");
            System.out.println("3. Thống kê nợ học phí");
            System.out.println("0. Thoát");
            System.out.print("Chọn chức năng: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // Clear buffer
            
            switch (choice) {
                case 1:
                    quanLyMonHoc();
                    break;
                case 2:
                    quanLyLopHoc();
                    break;
                case 3:
                    thongKeNoHocPhi();
                    break;
                case 0:
                    running = false;
                    System.out.println("Đã thoát chương trình.");
                    break;
                default:
                    System.out.println("Lựa chọn không hợp lệ!");
            }
        }
    }

    // ========== QUẢN LÝ MÔN HỌC ==========
    private static void quanLyMonHoc() {
        boolean back = false;
        while (!back) {
            System.out.println("\n=== QUẢN LÝ MÔN HỌC ===");
            System.out.println("1. Thêm môn học");
            System.out.println("2. Sửa môn học");
            System.out.println("3. Xóa môn học");
            System.out.println("4. Xem tất cả môn học");
            System.out.println("0. Quay lại");
            System.out.print("Chọn chức năng: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine();
            
            switch (choice) {
                case 1:
                    themMonHoc();
                    break;
                case 2:
                    suaMonHoc();
                    break;
                case 3:
                    xoaMonHoc();
                    break;
                case 4:
                    xemTatCaMonHoc();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Lựa chọn không hợp lệ!");
            }
        }
    }

    private static void themMonHoc() {
        System.out.print("Nhập mã môn: ");
        String maMon = scanner.nextLine();
        System.out.print("Nhập tên môn: ");
        String tenMon = scanner.nextLine();
        System.out.print("Nhập số tín chỉ: ");
        int soTinChi = scanner.nextInt();
        scanner.nextLine();
        
        MonHoc monHoc = new MonHoc(maMon, tenMon, soTinChi);
        if (monHocService.themMonHoc(monHoc)) {
            System.out.println("Thêm môn học thành công!");
        } else {
            System.out.println("Thêm môn học thất bại!");
        }
    }

    private static void suaMonHoc() {
        System.out.print("Nhập mã môn cần sửa: ");
        String maMon = scanner.nextLine();
        
        MonHoc monHoc = monHocService.timMonHocTheoMa(maMon);
        if (monHoc == null) {
            System.out.println("Không tìm thấy môn học!");
            return;
        }
        
        System.out.print("Nhập tên mới (Enter để giữ nguyên): ");
        String tenMon = scanner.nextLine();
        if (!tenMon.isEmpty()) {
            monHoc.setTenMon(tenMon);
        }
        
        System.out.print("Nhập số tín chỉ mới (0 để giữ nguyên): ");
        int soTinChi = scanner.nextInt();
        scanner.nextLine();
        if (soTinChi > 0) {
            monHoc.setSoTinChi(soTinChi);
        }
        
        if (monHocService.suaMonHoc(monHoc)) {
            System.out.println("Sửa môn học thành công!");
        } else {
            System.out.println("Sửa môn học thất bại!");
        }
    }

    private static void xoaMonHoc() {
        System.out.print("Nhập mã môn cần xóa: ");
        String maMon = scanner.nextLine();
        
        if (monHocService.xoaMonHoc(maMon)) {
            System.out.println("Xóa môn học thành công!");
        } else {
            System.out.println("Xóa môn học thất bại!");
        }
    }

    private static void xemTatCaMonHoc() {
        List<MonHoc> dsMonHoc = monHocService.layTatCaMonHoc();
        if (dsMonHoc.isEmpty()) {
            System.out.println("Không có môn học nào!");
            return;
        }
        
        System.out.println("\nDANH SÁCH MÔN HỌC:");
        for (MonHoc monHoc : dsMonHoc) {
            System.out.println(monHoc);
        }
    }

    // ========== QUẢN LÝ LỚP HỌC ==========
    private static void quanLyLopHoc() {
        boolean back = false;
        while (!back) {
            System.out.println("\n=== QUẢN LÝ LỚP HỌC ===");
            System.out.println("1. Thêm lớp học");
            System.out.println("2. Sửa lớp học");
            System.out.println("3. Xóa lớp học");
            System.out.println("4. Xem tất cả lớp học");
            System.out.println("0. Quay lại");
            System.out.print("Chọn chức năng: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine();
            
            switch (choice) {
                case 1:
                    themLopHoc();
                    break;
                case 2:
                    suaLopHoc();
                    break;
                case 3:
                    xoaLopHoc();
                    break;
                case 4:
                    xemTatCaLopHoc();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Lựa chọn không hợp lệ!");
            }
        }
    }

    private static void themLopHoc() {
        System.out.print("Nhập mã lớp: ");
        String maLop = scanner.nextLine();
        System.out.print("Nhập số lượng sinh viên: ");
        int soLuongSV = scanner.nextInt();
        scanner.nextLine();
        
        LopHoc lopHoc = new LopHoc(maLop, soLuongSV);
        if (lopHocService.themLopHoc(lopHoc)) {
            System.out.println("Thêm lớp học thành công!");
        } else {
            System.out.println("Thêm lớp học thất bại!");
        }
    }

    private static void suaLopHoc() {
        System.out.print("Nhập mã lớp cần sửa: ");
        String maLop = scanner.nextLine();
        
        LopHoc lopHoc = lopHocService.timLopHocTheoMa(maLop);
        if (lopHoc == null) {
            System.out.println("Không tìm thấy lớp học!");
            return;
        }
        
        System.out.print("Nhập số lượng SV mới (0 để giữ nguyên): ");
        int soLuongSV = scanner.nextInt();
        scanner.nextLine();
        if (soLuongSV > 0) {
            lopHoc.setSoLuongSinhVien(soLuongSV);
        }
        
        if (lopHocService.suaLopHoc(lopHoc)) {
            System.out.println("Sửa lớp học thành công!");
        } else {
            System.out.println("Sửa lớp học thất bại!");
        }
    }

    private static void xoaLopHoc() {
        System.out.print("Nhập mã lớp cần xóa: ");
        String maLop = scanner.nextLine();
        
        if (lopHocService.xoaLopHoc(maLop)) {
            System.out.println("Xóa lớp học thành công!");
        } else {
            System.out.println("Xóa lớp học thất bại!");
        }
    }

    private static void xemTatCaLopHoc() {
        List<LopHoc> dsLopHoc = lopHocService.layTatCaLopHoc();
        if (dsLopHoc.isEmpty()) {
            System.out.println("Không có lớp học nào!");
            return;
        }
        
        System.out.println("\nDANH SÁCH LỚP HỌC:");
        for (LopHoc lopHoc : dsLopHoc) {
            System.out.println(lopHoc);
        }
    }

    // ========== THỐNG KÊ NỢ HỌC PHÍ ==========
    private static void thongKeNoHocPhi() {
        boolean back = false;
        while (!back) {
            System.out.println("\n=== THỐNG KÊ NỢ HỌC PHÍ ===");
            System.out.println("1. Xem tất cả nợ học phí");
            System.out.println("2. Xem nợ học phí theo MSV");
            System.out.println("0. Quay lại");
            System.out.print("Chọn chức năng: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine();
            
            switch (choice) {
                case 1:
                    xemTatCaNoHocPhi();
                    break;
                case 2:
                    xemNoHocPhiTheoMSV();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Lựa chọn không hợp lệ!");
            }
        }
    }

    private static void xemTatCaNoHocPhi() {
        List<FeeDebt> dsNoHocPhi = feeDebtService.thongKeNoHocPhi();
        if (dsNoHocPhi.isEmpty()) {
            System.out.println("Không có sinh viên nào nợ học phí!");
            return;
        }
        
        System.out.println("\nDANH SÁCH NỢ HỌC PHÍ:");
        for (FeeDebt noHocPhi : dsNoHocPhi) {
            System.out.println(noHocPhi);
        }
    }

    private static void xemNoHocPhiTheoMSV() {
        System.out.print("Nhập mã sinh viên: ");
        String msv = scanner.nextLine();
        
        List<FeeDebt> dsNoHocPhi = feeDebtService.thongKeNoHocPhiTheoMSV(msv);
        if (dsNoHocPhi.isEmpty()) {
            System.out.println("Sinh viên này không nợ học phí!");
            return;
        }
        
        System.out.println("\nDANH SÁCH NỢ HỌC PHÍ CỦA " + msv + ":");
        for (FeeDebt noHocPhi : dsNoHocPhi) {
            System.out.println(noHocPhi);
        }
    }
}