import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class IDSVManager {
    Set<String> idsvs = new HashSet<>();

    public IDSVManager() {
        load();
    }

    public void load() {
        idsvs.clear();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(FileUtils.FILE_IDSV), StandardCharsets.UTF_8))) {
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

    public boolean exists(String idsv) {
        return idsvs.contains(idsv);
    }

    public void addIDSV(String idsv) {
        if (idsv.isEmpty() || idsvs.contains(idsv)) return;
        idsvs.add(idsv);
        save();
    }

    public void save() {
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FileUtils.FILE_IDSV), StandardCharsets.UTF_8))) {
            for (String id : idsvs) {
                bw.write(id);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Loi ghi file IDSV: " + e.getMessage());
        }
    }
}
