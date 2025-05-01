package com.myuniv.sm.service;

import com.myuniv.sm.model.Student;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

/**
 * Service for managing student data
 */
public class StudentService {
    
    /**
     * Find a student by their student ID (MSV)
     * @param msv The student ID to look up
     * @return The student if found, null otherwise
     */
    public Student findByMsv(String msv) {
        // TODO: In a real implementation, this would fetch data from the database
        // using a DAO - for now, it just simulates a lookup with sample data
        
        // Simple implementation for testing - in a real app, we'd query from DAO
        if (msv != null && msv.matches("^[0-9]{8}$")) {
            Student student = new Student();
            student.setMsv(msv);
            student.setHoTen("Nguyễn Văn A");
            student.setGioiTinh("Nam");
            student.setNgaySinh(LocalDate.of(2000, 1, 1));
            student.setEmail("nva@example.com");
            student.setSoDienThoai("0987654321");
            student.setDiaChi("Hà Nội");
            student.setMaLop("CNTT01");
            return student;
        }
        return null;
    }
    
    /**
     * Get a list of all students
     * @return List of all students
     */
    public List<Student> findAll() {
        // TODO: In a real implementation, this would fetch all students from the database
        List<Student> students = new ArrayList<>();
        
        // Add some sample students for testing
        Student s1 = new Student();
        s1.setMsv("20200001");
        s1.setHoTen("Nguyễn Văn A");
        s1.setGioiTinh("Nam");
        students.add(s1);
        
        Student s2 = new Student();
        s2.setMsv("20200002");
        s2.setHoTen("Trần Thị B");
        s2.setGioiTinh("Nữ");
        students.add(s2);
        
        Student s3 = new Student();
        s3.setMsv("20200003");
        s3.setHoTen("Lê Văn C");
        s3.setGioiTinh("Nam");
        students.add(s3);
        
        return students;
    }
    
    /**
     * Find students by class ID
     * @param maLop The class ID to search for
     * @return List of students in the specified class
     */
    public List<Student> findByClass(String maLop) {
        // TODO: In a real implementation, this would fetch students filtered by class
        // For now, we'll just return sample data
        List<Student> students = new ArrayList<>();
        
        if ("CNTT01".equals(maLop)) {
            Student s1 = new Student();
            s1.setMsv("20200001");
            s1.setHoTen("Nguyễn Văn A");
            s1.setGioiTinh("Nam");
            s1.setMaLop(maLop);
            students.add(s1);
            
            Student s2 = new Student();
            s2.setMsv("20200002");
            s2.setHoTen("Trần Thị B");
            s2.setGioiTinh("Nữ");
            s2.setMaLop(maLop);
            students.add(s2);
        }
        
        return students;
    }
} 