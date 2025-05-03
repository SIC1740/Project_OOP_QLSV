import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ReRegister {
    Map<String, ReRegisterItem> reRegisters = new LinkedHashMap<>();

    public ReRegister() {
        load();
    }

    public void load() {
        reRegisters.clear();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(FileUtils.FILE_REREGISTER), StandardCharsets.UTF_8))) {
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

    public void save() {
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FileUtils.FILE_REREGISTER), StandardCharsets.UTF_8))) {
            for (ReRegisterItem item : reRegisters.values()) {
                bw.write(item.IDSV + "|" + item.IDMon + "|" + item.LyDo);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Loi ghi file ReRegister: " + e.getMessage());
        }
    }

    public boolean addReRegister(String idSV, String idMon, String lyDo) {
        String key = idSV + "|" + idMon;
        if (reRegisters.containsKey(key)) {
            return false;
        }
        reRegisters.put(key, new ReRegisterItem(idSV, idMon, lyDo));
        save();
        return true;
    }

    public boolean deleteReRegister(String idSV, String idMon) {
        String key = idSV + "|" + idMon;
        if (reRegisters.remove(key) != null) {
            save();
            return true;
        }
        return false;
    }

    public List<ReRegisterItem> getByStudent(String idSV) {
        List<ReRegisterItem> list = new ArrayList<>();
        for (ReRegisterItem item : reRegisters.values()) {
            if (item.IDSV.equals(idSV)) {
                list.add(item);
            }
        }
        return list;
    }

    public List<ReRegisterItem> getAll() {
        return new ArrayList<>(reRegisters.values());
    }

    public static class ReRegisterItem {
        String IDSV;
        String IDMon;
        String LyDo;

        public ReRegisterItem(String idSV, String idMon, String lyDo) {
            this.IDSV = idSV;
            this.IDMon = idMon;
            this.LyDo = lyDo;
        }
    }
}
