package student;

import student.dao.StudentDAO;

import student.ui.ConsoleUI;

public class Main {
    public static void main(String[] args) {
        ConsoleUI ui = new ConsoleUI();
        while(true) {
            ui.start();
        }
//        // Hiển thị tất cả sinh viên
//        StudentDAO.displayAllStudents();
//
//        System.out.println("\nTìm kiếm sinh viên:");
//        // Tìm một sinh viên cụ thể - thay "SV001" bằng mã sinh viên thật
//        StudentDAO.findStudentByID("B21DCVT053");
    }
}