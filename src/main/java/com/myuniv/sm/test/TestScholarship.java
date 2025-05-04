package com.myuniv.sm.test;

import com.myuniv.sm.model.StudentGPASummary;
import com.myuniv.sm.service.GradeService;
import com.myuniv.sm.service.ServiceException;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Test class for scholarship functionality
 */
public class TestScholarship {
    public static void main(String[] args) {
        try {
            GradeService gradeService = new GradeService();
            Map<String, StudentGPASummary> studentGPAs = gradeService.calculateStudentGPASummary();
            
            // Convert to list and sort by GPA (descending)
            List<StudentGPASummary> sortedStudents = studentGPAs.values().stream()
                    .filter(s -> s.getTotalCredits() > 0) // Only include students with credits
                    .sorted(Comparator.comparing(StudentGPASummary::getAverageGPA).reversed())
                    .collect(Collectors.toList());
            
            if (sortedStudents.isEmpty()) {
                System.out.println("No student GPA data found");
                return;
            }
            
            // Calculate top 10% of students
            int totalStudents = sortedStudents.size();
            int scholarshipCount = Math.max(1, (int) Math.ceil(totalStudents * 10 / 100.0));
            
            // Get minimum GPA threshold for scholarship
            BigDecimal minGpaThreshold = BigDecimal.ZERO;
            if (scholarshipCount < totalStudents) {
                minGpaThreshold = sortedStudents.get(scholarshipCount - 1).getAverageGPA();
            } else {
                minGpaThreshold = sortedStudents.get(totalStudents - 1).getAverageGPA();
            }
            
            System.out.println("===== SCHOLARSHIP STUDENTS (TOP 10%) =====");
            System.out.printf("Total students: %d, Scholarship recipients: %d\n", totalStudents, scholarshipCount);
            System.out.printf("Minimum GPA threshold: %.2f\n", minGpaThreshold);
            System.out.println("-------------------------------------------");
            
            // Print top students
            for (int i = 0; i < scholarshipCount; i++) {
                if (i < sortedStudents.size()) {
                    StudentGPASummary student = sortedStudents.get(i);
                    // Only include students with the minimum GPA threshold
                    if (student.getAverageGPA().compareTo(minGpaThreshold) >= 0) {
                        System.out.printf("%d. %s - %s (GPA: %.2f, Credits: %d)\n",
                                i + 1, student.getMsv(), student.getHoTen(),
                                student.getAverageGPA(), student.getTotalCredits());
                    }
                }
            }
            
        } catch (ServiceException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 