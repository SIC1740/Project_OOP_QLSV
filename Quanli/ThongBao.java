import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ThongBao {
    List<String> messages = new ArrayList<>();

    public ThongBao() {
        load();
    }

    public void load() {
        messages.clear();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(FileUtils.FILE_THONGBAO), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                messages.add(line);
            }
        } catch (IOException e) {
            System.out.println("Loi doc file ThongBao: " + e.getMessage());
        }
    }

    public void save() {
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FileUtils.FILE_THONGBAO), StandardCharsets.UTF_8))) {
            for (String m : messages) {
                bw.write(m);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Loi ghi file ThongBao: " + e.getMessage());
        }
    }

    public void addMessage(String msg) {
        messages.add(msg);
        save();
    }

    public void showMessages() {
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
