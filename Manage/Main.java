import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.text.Normalizer;
import java.util.*;

class ManageSystem {

    static final String FILE_PROJECT = "ManageProject.txt";
    static final String FILE_SEMESTER = "ManageSemester.txt";
    static final String FILE_REREGISTER = "ReRegister.txt";
    static final String FILE_THONGBAO = "ThongBao.txt";
    static final String FILE_IDSV = "IDSV.txt";

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            initFiles();
            mainMenu();
        } catch (Exception e) {
            System.out.println("Co loi xay ra: " + e.getMessage());
        }
    }

    static void initFiles() {
        try {
            createFileIfNotExist(FILE_PROJECT);
            createFileIfNotExist(FILE_SEMESTER);
            createFileIfNotExist(FILE_REREGISTER);
            createFileIfNotExist(FILE_THONGBAO);
            createFileIfNotExist(FILE_IDSV);
        } catch (IOException e) {
            System.out.println("Loi khi khoi tao file: " + e.getMessage());
        }
    }

    static void createFileIfNotExist(String filename) throws IOException {
        File f = new File(filename);
        if (!f.exists()) {
            f.createNewFile();
        }
    }

    static String removeAccent(String s) {
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        return temp.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    static void printSeparator() {
        System.out.println("-------------------------------------------------");
    }

    static void mainMenu() {
        while (true) {
            printSeparator();
            System.out.println("Chon 1 de vao Student");
            System.out.println("Chon 2 de vao Teacher");
            System.out.println("Chon 0 de thoat");
            System.out.print("Lua chon: ");
            String choice = sc.nextLine();
            switch (choice) {
                case "1":
                    studentMenu();
                    break;
                case "2":
                    teacherMenu();
                    break;
                case "0":
                    System.out.println("Ket thuc chuong trinh.");
                    return;
                default:
                    System.out.println("Lua chon khong hop le.");
            }
        }
    }

    // ==================== CLASS ManageProject ====================
    static class ManageProject {
        // Lưu theo IDSV|IDDoAn|TenDoAn
        Map<String, Project> projects = new HashMap<>(); // key = IDDoAn

        ManageProject() {
            load();
        }

        void load() {
            projects.clear();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(FILE_PROJECT), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split("\\|");
                    if (parts.length == 3) {
                        projects.put(parts[1], new Project(parts[0], parts[1], parts[2]));
                    }
                }
            } catch (IOException e) {
                System.out.println("Loi doc file ManageProject: " + e.getMessage());
            }
        }

        void save() {
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FILE_PROJECT), StandardCharsets.UTF_8))) {
                for (Project p : projects.values()) {
                    bw.write(p.IDSV + "|" + p.IDDoAn + "|" + p.TenDoAn);
                    bw.newLine();
                }
            } catch (IOException e) {
                System.out.println("Loi ghi file ManageProject: " + e.getMessage());
            }
        }

        boolean addProject(Project p) {
            if (projects.containsKey(p.IDDoAn)) {
                return false;
            }
            projects.put(p.IDDoAn, p);
            save();
            return true;
        }

        boolean updateProject(String idDoAn, String newName) {
            Project p = projects.get(idDoAn);
            if (p == null) return false;
            p.TenDoAn = newName;
            save();
            return true;
        }

        boolean deleteProject(String idDoAn) {
            if (projects.remove(idDoAn) != null) {
                save();
                return true;
            }
            return false;
        }

        List<Project> getProjectsByStudent(String idSV) {
            List<Project> res = new ArrayList<>();
            for (Project p : projects.values()) {
                if (p.IDSV.equals(idSV)) {
                    res.add(p);
                }
            }
            return res;
        }

        List<Project> getAllProjects() {
            return new ArrayList<>(projects.values());
        }

        static class Project {
            String IDSV;
            String IDDoAn;
            String TenDoAn;

            Project(String idSV, String idDoAn, String tenDoAn) {
                this.IDSV = idSV;
                this.IDDoAn = idDoAn;
                this.TenDoAn = tenDoAn;
            }
        }
    }

    // ==================== CLASS ManageSemester ====================
    static class ManageSemester {
        // Lưu môn học cố định theo kỳ
        static Map<Integer, List<String>> semesterSubjects = new HashMap<>();
        // Lưu điểm theo IDSV|Ky|TenMonHoc -> Diem
        Map<String, Integer> scores = new HashMap<>();

        static {
            semesterSubjects.put(1, Arrays.asList("Tin hoc co so 1", "Giai tich 1", "Dai so"));
            semesterSubjects.put(2, Arrays.asList("Tin hoc co so 2", "Vat ly 1 va thi nghiem", "Giai tich 2"));
            semesterSubjects.put(3, Arrays.asList("Tin hieu va He thong", "Linh kien va mach dien tu", "Chu nghia xa hoi khoa hoc"));
            semesterSubjects.put(4, Arrays.asList("Ky thuat sieu cao tan", "Ly thuyet truyen tin", "Xu ly tin hieu so"));
            semesterSubjects.put(5, Arrays.asList("Truyen song va anten", "Ky thuat lap trinh", "Kien truc may tinh"));
            semesterSubjects.put(6, Arrays.asList("Thuc tap co so", "Mo phong he thong truyen thong", "Ky thuat thong tin quang"));
            semesterSubjects.put(7, Arrays.asList("Internet va cac giao thuc", "An toan mang thong tin", "Thong tin di dong"));
            semesterSubjects.put(8, Arrays.asList("Thiet ke va hieu nang mang", "Dien toan dam may", "Lap trinh huong doi tuong"));
        }

        ManageSemester() {
            load();
        }

        // Định dạng file ManageSemester.txt: IDSV|Ky|TenMonHoc|Diem
        void load() {
            scores.clear();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(FILE_SEMESTER), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split("\\|");
                    if (parts.length == 4) {
                        String key = parts[0] + "|" + parts[1] + "|" + parts[2];
                        try {
                            int diem = Integer.parseInt(parts[3]);
                            scores.put(key, diem);
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Loi doc file ManageSemester: " + e.getMessage());
            }
        }

        void save() {
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FILE_SEMESTER), StandardCharsets.UTF_8))) {
                for (Map.Entry<String, Integer> entry : scores.entrySet()) {
                    bw.write(entry.getKey() + "|" + entry.getValue());
                    bw.newLine();
                }
            } catch (IOException e) {
                System.out.println("Loi ghi file ManageSemester: " + e.getMessage());
            }
        }

        boolean checkSubjectExist(String subject) {
            for (List<String> subs : semesterSubjects.values()) {
                for (String s : subs) {
                    if (removeAccent(s).equalsIgnoreCase(removeAccent(subject))) {
                        return true;
                    }
                }
            }
            return false;
        }

        String getSubjectStandardName(String subject) {
            for (List<String> subs : semesterSubjects.values()) {
                for (String s : subs) {
                    if (removeAccent(s).equalsIgnoreCase(removeAccent(subject))) {
                        return s;
                    }
                }
            }
            return null;
        }

        Integer getScore(String idSV, int ky, String subject) {
            String key = idSV + "|" + ky + "|" + subject;
            return scores.get(key);
        }

        void setScore(String idSV, int ky, String subject, int diem) {
            String key = idSV + "|" + ky + "|" + subject;
            scores.put(key, diem);
            save();
        }

        // Hiển thị bảng môn học và điểm của 1 sinh viên theo kỳ
        void showSemesterForStudent(String idSV, int ky) {
            List<String> subjects = semesterSubjects.get(ky);
            if (subjects == null) {
                System.out.println("Khong co hoc ky nay.");
                return;
            }
            System.out.println("Mon hoc                     | Diem");
            System.out.println("----------------------------------------");
            for (String sub : subjects) {
                Integer diem = getScore(idSV, ky, sub);
                String diemStr = (diem == null) ? "-" : diem.toString();
                System.out.printf("%-27s | %s\n", sub, diemStr);
            }
        }

        // Tìm kỳ của môn học (theo tên môn)
        int getSemesterBySubject(String subject) {
            for (Map.Entry<Integer, List<String>> entry : semesterSubjects.entrySet()) {
                for (String s : entry.getValue()) {
                    if (removeAccent(s).equalsIgnoreCase(removeAccent(subject))) {
                        return entry.getKey();
                    }
                }
            }
            return -1;
        }
    }

    // ==================== CLASS ReRegister ====================
    static class ReRegister {
        // Lưu theo IDSV|IDMon|LyDo
        Map<String, ReRegisterItem> reRegisters = new LinkedHashMap<>();
        // key: IDSV|IDMon

        ReRegister() {
            load();
        }

        void load() {
            reRegisters.clear();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(FILE_REREGISTER), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split("\\|");
                    if (parts.length == 3) {
                        String key = parts[0] + "|" + parts[1];
                        reRegisters.put(key, new ReRegisterItem(parts[0], parts[1], parts[2]));
                    }
                }
            } catch (IOException e) {
                System.out.println("Loi doc file ReRegister: " + e.getMessage());
            }
        }

        void save() {
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FILE_REREGISTER), StandardCharsets.UTF_8))) {
                for (ReRegisterItem item : reRegisters.values()) {
                    bw.write(item.IDSV + "|" + item.IDMon + "|" + item.LyDo);
                    bw.newLine();
                }
            } catch (IOException e) {
                System.out.println("Loi ghi file ReRegister: " + e.getMessage());
            }
        }

        boolean addReRegister(String idSV, String idMon, String lyDo) {
            String key = idSV + "|" + idMon;
            if (reRegisters.containsKey(key)) {
                return false;
            }
            reRegisters.put(key, new ReRegisterItem(idSV, idMon, lyDo));
            save();
            return true;
        }

        boolean deleteReRegister(String idSV, String idMon) {
            String key = idSV + "|" + idMon;
            if (reRegisters.remove(key) != null) {
                save();
                return true;
            }
            return false;
        }

        List<ReRegisterItem> getByStudent(String idSV) {
            List<ReRegisterItem> list = new ArrayList<>();
            for (ReRegisterItem item : reRegisters.values()) {
                if (item.IDSV.equals(idSV)) {
                    list.add(item);
                }
            }
            return list;
        }

        List<ReRegisterItem> getAll() {
            return new ArrayList<>(reRegisters.values());
        }

        static class ReRegisterItem {
            String IDSV;
            String IDMon;
            String LyDo;

            ReRegisterItem(String idSV, String idMon, String lyDo) {
                this.IDSV = idSV;
                this.IDMon = idMon;
                this.LyDo = lyDo;
            }
        }
    }

    // ==================== CLASS ThongBao ====================
    static class ThongBao {
        List<String> messages = new ArrayList<>();

        ThongBao() {
            load();
        }

        void load() {
            messages.clear();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(FILE_THONGBAO), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    messages.add(line);
                }
            } catch (IOException e) {
                System.out.println("Loi doc file ThongBao: " + e.getMessage());
            }
        }

        void save() {
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FILE_THONGBAO), StandardCharsets.UTF_8))) {
                for (String m : messages) {
                    bw.write(m);
                    bw.newLine();
                }
            } catch (IOException e) {
                System.out.println("Loi ghi file ThongBao: " + e.getMessage());
            }
        }

        void addMessage(String msg) {
            messages.add(msg);
            save();
        }

        void showMessages() {
            if (messages.isEmpty()) {
                System.out.println("Khong co thong bao nao.");
                return;
            }
            System.out.println("Thong bao:");
            System.out.println("----------------------------------------");
            for (String m : messages) {
                System.out.println(m);
            }
        }
    }

    // ==================== CLASS IDSV ====================
    static class IDSVManager {
        Set<String> idsvs = new HashSet<>();

        IDSVManager() {
            load();
        }

        void load() {
            idsvs.clear();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(FILE_IDSV), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
                        idsvs.add(line.trim());
                    }
                }
            } catch (IOException e) {
                System.out.println("Loi doc file IDSV: " + e.getMessage());
            }
        }

        boolean exists(String idsv) {
            return idsvs.contains(idsv);
        }

        void addIDSV(String idsv) {
            if (idsv.isEmpty() || idsvs.contains(idsv)) return;
            idsvs.add(idsv);
            save();
        }

        void save() {
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FILE_IDSV), StandardCharsets.UTF_8))) {
                for (String id : idsvs) {
                    bw.write(id);
                    bw.newLine();
                }
            } catch (IOException e) {
                System.out.println("Loi ghi file IDSV: " + e.getMessage());
            }
        }
    }

    // ==================== MENU STUDENT ====================
    static void studentMenu() {
        ManageProject manageProject = new ManageProject();
        ManageSemester manageSemester = new ManageSemester();
        ReRegister reRegister = new ReRegister();
        ThongBao thongBao = new ThongBao();
        IDSVManager idsvManager = new IDSVManager();

        System.out.print("Nhap ID sinh vien (IDSV): ");
        String idSV = sc.nextLine().trim();
        if (!idsvManager.exists(idSV)) {
            System.out.println("IDSV chua ton tai, da them moi.");
            idsvManager.addIDSV(idSV);
        }

        while (true) {
            printSeparator();
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
                    manageProjectMenu(manageProject, idSV);
                    break;
                case "2":
                    manageSemesterMenu(manageSemester, idSV);
                    break;
                case "3":
                    reRegisterMenu(reRegister, idSV);
                    break;
                case "4":
                    printSeparator();
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

    static void manageProjectMenu(ManageProject manageProject, String idSV) {
        while (true) {
            printSeparator();
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

    static void manageSemesterMenu(ManageSemester manageSemester, String idSV) {
        while (true) {
            printSeparator();
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

    static void reRegisterMenu(ReRegister reRegister, String idSV) {
        while (true) {
            printSeparator();
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

    // ==================== MENU TEACHER ====================
    static void teacherMenu() {
        ManageProject manageProject = new ManageProject();
        ReRegister reRegister = new ReRegister();
        ThongBao thongBao = new ThongBao();
        ManageSemester manageSemester = new ManageSemester();
        IDSVManager idsvManager = new IDSVManager();

        while (true) {
            printSeparator();
            System.out.println("TEACHER MENU");
            System.out.println("1. Xem toan bo do an cua sinh vien");
            System.out.println("2. Xem danh sach mon hoc lai theo IDSV");
            System.out.println("3. Viet thong bao gui sinh vien");
            System.out.println("0. Thoat");
            System.out.print("Lua chon: ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    printSeparator();
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
                    printSeparator();
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
                    teacherThongBaoMenu(thongBao, manageSemester);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Lua chon khong hop le.");
            }
        }
    }

    static String getSubjectNameFromID(String idMon, ManageSemester manageSemester) {
        // Vì đề bài không có mapping IDMon -> TenMonHoc, ta giả sử IDMon chính là tên môn hoặc gần giống
        // Nên ta tìm môn học trong ManageSemester có tên giống hoặc chứa idMon (xóa dấu)
        for (List<String> subs : ManageSemester.semesterSubjects.values()) {
            for (String s : subs) {
                if (removeAccent(s).toLowerCase().contains(removeAccent(idMon).toLowerCase())) {
                    return s;
                }
            }
        }
        return idMon; // nếu không tìm thấy thì trả về chính IDMon
    }

    static void teacherThongBaoMenu(ThongBao thongBao, ManageSemester manageSemester) {
        while (true) {
            printSeparator();
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
                    printSeparator();
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
