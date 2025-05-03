import java.util.Scanner;

public class Main {
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            FileUtils.initFiles();
            mainMenu();
        } catch (Exception e) {
            System.out.println("Co loi xay ra: " + e.getMessage());
        }
    }

    static void mainMenu() {
        while (true) {
            FileUtils.printSeparator();
            System.out.println("Chon 1 de vao Student");
            System.out.println("Chon 2 de vao Teacher");
            System.out.println("Chon 0 de thoat");
            System.out.print("Lua chon: ");
            String choice = sc.nextLine();
            switch (choice) {
                case "1":
                    Student.studentMenu();
                    break;
                case "2":
                    Teacher.teacherMenu();
                    break;
                case "0":
                    System.out.println("Ket thuc chuong trinh.");
                    return;
                default:
                    System.out.println("Lua chon khong hop le.");
            }
        }
    }
}
