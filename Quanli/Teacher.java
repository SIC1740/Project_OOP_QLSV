import java.util.List;
import java.util.Scanner;

public class Teacher {
    static Scanner sc = new Scanner(System.in);
    static ManageProject manageProject = new ManageProject();
    static ReRegister reRegister = new ReRegister();
    static ThongBao thongBao = new ThongBao();
    static ManageSemester manageSemester = new ManageSemester();
    static IDSVManager idsvManager = new IDSVManager();

    public static void teacherMenu() {
        while (true) {
            FileUtils.printSeparator();
            System.out.println("TEACHER MENU");
            System.out.println("1. Xem toan bo do an cua sinh vien");
            System.out.println("2. Xem danh sach mon hoc lai theo IDSV");
            System.out.println("3. Viet thong bao gui sinh vien");
            System.out.println("0. Thoat");
            System.out.print("Lua chon: ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    FileUtils.printSeparator();
                    List<ManageProject.Project> allProjects = manageProject.getAllProjects();
                    if (allProjects.isEmpty()) {
                        System.out.println("Chua co do an nao.");
                    } else {
                        System.out.println("IDSV       | IDDoAn    | TenDoAn");
                        System.out.println("----------------------------------------");
                        for (ManageProject.Project p : allProjects) {
                            System.out.printf("%-10s | %-8s | %s\n", p.IDSV, p.IDDoAn, p.TenDoAn);
                        }
                    }
                    break;
                case "2":
                    System.out.print("Nhap IDSV can xem mon hoc lai: ");
                    String idSV = sc.nextLine().trim();
                    if (!idsvManager.exists(idSV)) {
                        System.out.println("IDSV khong ton tai.");
                        break;
                    }
                    FileUtils.printSeparator();
                    List<ReRegister.ReRegisterItem> list = reRegister.getByStudent(idSV);
                    if (list.isEmpty()) {
                        System.out.println("Sinh vien chua dang ky hoc lai mon nao.");
                    } else {
                        System.out.println("IDSV       | TenMonHoc                   | LyDoHocLai");
                        System.out.println("------------------------------------------------------------");
                        for (ReRegister.ReRegisterItem item : list) {
                            String tenMon = getSubjectNameFromID(item.IDMon, manageSemester);
                            System.out.printf("%-10s | %-27s | %s\n", item.IDSV, tenMon, item.LyDo);
                        }
                    }
                    break;
                case "3":
                    teacherThongBaoMenu();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Lua chon khong hop le.");
            }
        }
    }

    static String getSubjectNameFromID(String idMon, ManageSemester manageSemester) {
        for (List<String> subs : ManageSemester.semesterSubjects.values()) {
            for (String s : subs) {
                if (FileUtils.removeAccent(s).toLowerCase().contains(FileUtils.removeAccent(idMon).toLowerCase())) {
                    return s;
                }
            }
        }
        return idMon;
    }

    static void teacherThongBaoMenu() {
        while (true) {
            FileUtils.printSeparator();
            System.out.println("VIET THONG BAO");
            System.out.println("1. Viet thong bao nghi hoc");
            System.out.println("2. Viet thong bao chuyen phong");
            System.out.println("3. Viet thong bao diem");
            System.out.println("4. Xem thong bao");
            System.out.println("0. Thoat");
            System.out.print("Lua chon: ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    System.out.print("Nhap noi dung thong bao nghi hoc: ");
                    String nghiHoc = sc.nextLine().trim();
                    if (!nghiHoc.isEmpty()) {
                        thongBao.addMessage("[Thong bao nghi hoc] " + nghiHoc);
                        System.out.println("Da gui thong bao nghi hoc.");
                    } else {
                        System.out.println("Noi dung khong duoc de trong.");
                    }
                    break;
                case "2":
                    System.out.print("Nhap noi dung thong bao chuyen phong: ");
                    String chuyenPhong = sc.nextLine().trim();
                    if (!chuyenPhong.isEmpty()) {
                        thongBao.addMessage("[Thong bao chuyen phong] " + chuyenPhong);
                        System.out.println("Da gui thong bao chuyen phong.");
                    } else {
                        System.out.println("Noi dung khong duoc de trong.");
                    }
                    break;
                case "3":
                    System.out.print("Nhap IDSV cua sinh vien: ");
                    String idSV = sc.nextLine().trim();
                    if (idSV.isEmpty()) {
                        System.out.println("IDSV khong duoc de trong.");
                        break;
                    }
                    if (!idsvManager.exists(idSV)) {
                        System.out.println("IDSV khong ton tai.");
                        break;
                    }
                    System.out.print("Nhap ten mon hoc: ");
                    String monHocInput = sc.nextLine().trim();
                    if (monHocInput.isEmpty()) {
                        System.out.println("Ten mon hoc khong duoc de trong.");
                        break;
                    }
                    if (!manageSemester.checkSubjectExist(monHocInput)) {
                        System.out.println("Mon hoc khong ton tai trong danh sach hoc ky.");
                        break;
                    }
                    String monHoc = manageSemester.getSubjectStandardName(monHocInput);
                    int ky = manageSemester.getSemesterBySubject(monHoc);
                    if (ky == -1) {
                        System.out.println("Khong tim thay hoc ky cua mon hoc.");
                        break;
                    }
                    System.out.print("Nhap diem mon hoc (0-10): ");
                    String diemStr = sc.nextLine().trim();
                    int diem;
                    try {
                        diem = Integer.parseInt(diemStr);
                        if (diem < 0 || diem > 10) {
                            System.out.println("Diem phai trong khoang 0-10.");
                            break;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Diem phai la so nguyen.");
                        break;
                    }
                    manageSemester.setScore(idSV, ky, monHoc, diem);
                    thongBao.addMessage("[Thong bao diem] Sinh vien: " + idSV + " - Mon: " + monHoc + " - Diem: " + diem);
                    System.out.println("Da cap nhat diem va gui thong bao diem.");
                    break;
                case "4":
                    FileUtils.printSeparator();
                    thongBao.showMessages();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Lua chon khong hop le.");
            }
        }
    }
}
