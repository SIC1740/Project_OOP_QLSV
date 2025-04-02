package student;

import student.dao.StudentDAO;

public class Main {
    public static void main(String[] args) {
        // Hiển thị tất cả sinh viên
        StudentDAO.displayAllStudents();

        System.out.println("\nTìm kiếm sinh viên:");
        // Tìm một sinh viên cụ thể - thay "SV001" bằng mã sinh viên thật
        StudentDAO.findStudentByID("SV001");
    }
}