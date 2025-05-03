import java.io.File;
import java.io.IOException;

public class FileUtils {
    public static final String FILE_PROJECT = "ManageProject.txt";
    public static final String FILE_SEMESTER = "ManageSemester.txt";
    public static final String FILE_REREGISTER = "ReRegister.txt";
    public static final String FILE_THONGBAO = "ThongBao.txt";
    public static final String FILE_IDSV = "IDSV.txt";

    public static void initFiles() throws IOException {
        createFileIfNotExist(FILE_PROJECT);
        createFileIfNotExist(FILE_SEMESTER);
        createFileIfNotExist(FILE_REREGISTER);
        createFileIfNotExist(FILE_THONGBAO);
        createFileIfNotExist(FILE_IDSV);
    }

    public static void createFileIfNotExist(String filename) throws IOException {
        File f = new File(filename);
        if (!f.exists()) {
            f.createNewFile();
        }
    }

    public static void printSeparator() {
        System.out.println("-------------------------------------------------");
    }

    public static String removeAccent(String s) {
        java.text.Normalizer.Form form = java.text.Normalizer.Form.NFD;
        String temp = java.text.Normalizer.normalize(s, form);
        return temp.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }
}
