import java.util.List;
import java.util.Scanner;

public class Student {
    static Scanner sc = new Scanner(System.in);
    static ManageProject manageProject = new ManageProject();
    static ManageSemester manageSemester = new ManageSemester();
    static ReRegister reRegister = new ReRegister();
    static ThongBao thongBao = new ThongBao();
    static IDSVManager idsvManager = new IDSVManager();

    public static void studentMenu() {
        System.out.print("Nhap ID sinh vien (IDSV): ");
        String idSV = sc.nextLine().trim();
        if (!idsvManager.exists(idSV)) {
            System.out.println("IDSV chua ton tai, da them moi.");
            idsvManager.addIDSV(idSV);
        }

        while (true) {
            FileUtils.printSeparator();
            System.out.println("STUDENT MENU");
            System.out.println("1. ManageProject");
            System.out.println("2. ManageSemester");
            System.out.println("3. Dang ky hoc lai (Re-register)");
            System.out.println("4. Xem thong bao tu giao vien");
            System.out.println("0. Thoat");
            System.out.print("Lua chon: ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    manageProjectMenu(idSV);
                    break;
                case "2":
                    manageSemesterMenu(idSV);
                    break;
                case "3":
                    reRegisterMenu(idSV);
                    break;
                case "4":
                    FileUtils.printSeparator();
                    System.out.println("Thong bao tu giao vien:");
                    thongBao.showMessages();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Lua chon khong hop le.");
            }
        }
    }

    static void manageProjectMenu(String idSV) {
        while (true) {
            FileUtils.printSeparator();
            System.out.println("MANAGE PROJECT");
            System.out.println("1. Them do an");
            System.out.println("2. Sua do an");
            System.out.println("3. Xoa do an");
            System.out.println("4. Xem do an");
            System.out.println("0. Thoat");
            System.out.print("Lua chon: ");
            String choice = sc.nextLine();

            try {
                switch (choice) {
                    case "1":
                        System.out.print("Nhap IDDoAn (phai duy nhat): ");
                        String idDoAn = sc.nextLine().trim();
                        if (idDoAn.isEmpty()) {
                            System.out.println("IDDoAn khong duoc de trong.");
                            break;
                        }
                        if (manageProject.projects.containsKey(idDoAn)) {
                            System.out.println("IDDoAn da ton tai.");
                            break;
                        }
                        System.out.print("Nhap TenDoAn: ");
                        String tenDoAn = sc.nextLine().trim();
                        if (tenDoAn.isEmpty()) {
                            System.out.println("TenDoAn khong duoc de trong.");
                            break;
                        }
                        ManageProject.Project newP = new ManageProject.Project(idSV, idDoAn, tenDoAn);
                        if (manageProject.addProject(newP)) {
                            System.out.println("Them do an thanh cong.");
                        } else {
                            System.out.println("Them do an that bai.");
                        }
                        break;
                    case "2":
                        System.out.print("Nhap IDDoAn can sua: ");
                        String idDoAnSua = sc.nextLine().trim();
                        ManageProject.Project p = manageProject.projects.get(idDoAnSua);
                        if (p == null) {
                            System.out.println("IDDoAn khong ton tai.");
                            break;
                        }
                        if (!p.IDSV.equals(idSV)) {
                            System.out.println("Ban chi co the sua do an cua chinh minh.");
                            break;
                        }
                        System.out.print("Nhap TenDoAn moi: ");
                        String tenDoAnMoi = sc.nextLine().trim();
                        if (tenDoAnMoi.isEmpty()) {
                            System.out.println("TenDoAn khong duoc de trong.");
                            break;
                        }
                        if (manageProject.updateProject(idDoAnSua, tenDoAnMoi)) {
                            System.out.println("Sua do an thanh cong.");
                        } else {
                            System.out.println("Sua do an that bai.");
                        }
                        break;
                    case "3":
                        System.out.print("Nhap IDDoAn can xoa: ");
                        String idDoAnXoa = sc.nextLine().trim();
                        ManageProject.Project pDel = manageProject.projects.get(idDoAnXoa);
                        if (pDel == null) {
                            System.out.println("IDDoAn khong ton tai.");
                            break;
                        }
                        if (!pDel.IDSV.equals(idSV)) {
                            System.out.println("Ban chi co the xoa do an cua chinh minh.");
                            break;
                        }
                        if (manageProject.deleteProject(idDoAnXoa)) {
                            System.out.println("Xoa do an thanh cong.");
                        } else {
                            System.out.println("Xoa do an that bai.");
                        }
                        break;
                    case "4":
                        List<ManageProject.Project> list = manageProject.getProjectsByStudent(idSV);
                        if (list.isEmpty()) {
                            System.out.println("Ban chua co do an nao.");
                        } else {
                            System.out.println("IDDoAn    | TenDoAn");
                            System.out.println("----------------------------------------");
                            for (ManageProject.Project pr : list) {
                                System.out.printf("%-10s | %s\n", pr.IDDoAn, pr.TenDoAn);
                            }
                        }
                        break;
                    case "0":
                        return;
                    default:
                        System.out.println("Lua chon khong hop le.");
                }
            } catch (Exception e) {
                System.out.println("Co loi xay ra: " + e.getMessage());
            }
        }
    }

    static void manageSemesterMenu(String idSV) {
        while (true) {
            FileUtils.printSeparator();
            System.out.println("MANAGE SEMESTER");
            System.out.println("Chon hoc ky (1-8) hoac 0 de thoat:");
            System.out.print("Lua chon: ");
            String choice = sc.nextLine();
            try {
                int ky = Integer.parseInt(choice);
                if (ky == 0) return;
                if (ky < 1 || ky > 8) {
                    System.out.println("Hoc ky khong hop le.");
                    continue;
                }
                manageSemester.showSemesterForStudent(idSV, ky);
            } catch (NumberFormatException e) {
                System.out.println("Nhap so hop le.");
            }
        }
    }

    static void reRegisterMenu(String idSV) {
        while (true) {
            FileUtils.printSeparator();
            System.out.println("DANG KY HOC LAI");
            System.out.println("1. Dang ky hoc lai theo IDMon");
            System.out.println("2. Xem danh sach mon dang ky cua ban");
            System.out.println("3. Xoa dang ky hoc lai theo IDMon");
            System.out.println("0. Thoat");
            System.out.print("Lua chon: ");
            String choice = sc.nextLine();

            try {
                switch (choice) {
                    case "1":
                        System.out.print("Nhap IDMon: ");
                        String idMon = sc.nextLine().trim();
                        if (idMon.isEmpty()) {
                            System.out.println("IDMon khong duoc de trong.");
                            break;
                        }
                        if (reRegister.reRegisters.containsKey(idSV + "|" + idMon)) {
                            System.out.println("Ban da dang ky hoc lai mon nay.");
                            break;
                        }
                        System.out.print("Nhap ly do hoc lai: ");
                        String lyDo = sc.nextLine().trim();
                        if (lyDo.isEmpty()) {
                            System.out.println("Ly do khong duoc de trong.");
                            break;
                        }
                        if (reRegister.addReRegister(idSV, idMon, lyDo)) {
                            System.out.println("Dang ky hoc lai thanh cong.");
                        } else {
                            System.out.println("Dang ky hoc lai that bai.");
                        }
                        break;
                    case "2":
                        List<ReRegister.ReRegisterItem> list = reRegister.getByStudent(idSV);
                        if (list.isEmpty()) {
                            System.out.println("Ban chua dang ky hoc lai mon nao.");
                        } else {
                            System.out.println("IDMon      | Ly do hoc lai");
                            System.out.println("----------------------------------------");
                            for (ReRegister.ReRegisterItem item : list) {
                                System.out.printf("%-10s | %s\n", item.IDMon, item.LyDo);
                            }
                        }
                        break;
                    case "3":
                        System.out.print("Nhap IDMon can xoa dang ky hoc lai: ");
                        String idMonXoa = sc.nextLine().trim();
                        if (reRegister.deleteReRegister(idSV, idMonXoa)) {
                            System.out.println("Xoa dang ky hoc lai thanh cong.");
                        } else {
                            System.out.println("Khong tim thay dang ky hoc lai nay.");
                        }
                        break;
                    case "0":
                        return;
                    default:
                        System.out.println("Lua chon khong hop le.");
                }
            } catch (Exception e) {
                System.out.println("Co loi xay ra: " + e.getMessage());
            }
        }
    }
}
