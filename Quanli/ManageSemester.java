import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ManageSemester {
    static Map<Integer, List<String>> semesterSubjects = new HashMap<>();
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

    public ManageSemester() {
        load();
    }

    public void load() {
        scores.clear();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(FileUtils.FILE_SEMESTER), StandardCharsets.UTF_8))) {
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

    public void save() {
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FileUtils.FILE_SEMESTER), StandardCharsets.UTF_8))) {
            for (Map.Entry<String, Integer> entry : scores.entrySet()) {
                bw.write(entry.getKey() + "|" + entry.getValue());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Loi ghi file ManageSemester: " + e.getMessage());
        }
    }

    public boolean checkSubjectExist(String subject) {
        for (List<String> subs : semesterSubjects.values()) {
            for (String s : subs) {
                if (FileUtils.removeAccent(s).equalsIgnoreCase(FileUtils.removeAccent(subject))) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getSubjectStandardName(String subject) {
        for (List<String> subs : semesterSubjects.values()) {
            for (String s : subs) {
                if (FileUtils.removeAccent(s).equalsIgnoreCase(FileUtils.removeAccent(subject))) {
                    return s;
                }
            }
        }
        return null;
    }

    public Integer getScore(String idSV, int ky, String subject) {
        String key = idSV + "|" + ky + "|" + subject;
        return scores.get(key);
    }

    public void setScore(String idSV, int ky, String subject, int diem) {
        String key = idSV + "|" + ky + "|" + subject;
        scores.put(key, diem);
        save();
    }

    public void showSemesterForStudent(String idSV, int ky) {
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

    public int getSemesterBySubject(String subject) {
        for (Map.Entry<Integer, List<String>> entry : semesterSubjects.entrySet()) {
            for (String s : entry.getValue()) {
                if (FileUtils.removeAccent(s).equalsIgnoreCase(FileUtils.removeAccent(subject))) {
                    return entry.getKey();
                }
            }
        }
        return -1;
    }
}
