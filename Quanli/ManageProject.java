import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ManageProject {
    Map<String, Project> projects = new HashMap<>();

    public ManageProject() {
        load();
    }

    public void load() {
        projects.clear();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(FileUtils.FILE_PROJECT), StandardCharsets.UTF_8))) {
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

    public void save() {
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FileUtils.FILE_PROJECT), StandardCharsets.UTF_8))) {
            for (Project p : projects.values()) {
                bw.write(p.IDSV + "|" + p.IDDoAn + "|" + p.TenDoAn);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Loi ghi file ManageProject: " + e.getMessage());
        }
    }

    public boolean addProject(Project p) {
        if (projects.containsKey(p.IDDoAn)) {
            return false;
        }
        projects.put(p.IDDoAn, p);
        save();
        return true;
    }

    public boolean updateProject(String idDoAn, String newName) {
        Project p = projects.get(idDoAn);
        if (p == null) return false;
        p.TenDoAn = newName;
        save();
        return true;
    }

    public boolean deleteProject(String idDoAn) {
        if (projects.remove(idDoAn) != null) {
            save();
            return true;
        }
        return false;
    }

    public List<Project> getProjectsByStudent(String idSV) {
        List<Project> res = new ArrayList<>();
        for (Project p : projects.values()) {
            if (p.IDSV.equals(idSV)) {
                res.add(p);
            }
        }
        return res;
    }

    public List<Project> getAllProjects() {
        return new ArrayList<>(projects.values());
    }

    public static class Project {
        String IDSV;
        String IDDoAn;
        String TenDoAn;

        public Project(String idSV, String idDoAn, String tenDoAn) {
            this.IDSV = idSV;
            this.IDDoAn = idDoAn;
            this.TenDoAn = tenDoAn;
        }
    }
}
