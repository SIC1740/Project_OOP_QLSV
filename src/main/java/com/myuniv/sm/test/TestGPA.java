package com.myuniv.sm.test;

import com.myuniv.sm.model.StudentGPASummary;
import com.myuniv.sm.service.GradeService;
import com.myuniv.sm.service.ServiceException;

import java.util.Map;

/**
 * Test class for GPA calculation functionality
 */
public class TestGPA {
    public static void main(String[] args) {
        try {
            GradeService gradeService = new GradeService();
            Map<String, StudentGPASummary> studentGPAs = gradeService.calculateStudentGPASummary();
            
            System.out.println("===== STUDENT GPA SUMMARY =====");
            for (Map.Entry<String, StudentGPASummary> entry : studentGPAs.entrySet()) {
                StudentGPASummary summary = entry.getValue();
                System.out.printf("Student: %s - %s\n", summary.getMsv(), summary.getHoTen());
                System.out.printf("Total Credits: %d\n", summary.getTotalCredits());
                System.out.printf("Average GPA: %.2f\n", summary.getAverageGPA());
                System.out.println("------------------------");
            }
            
            if (studentGPAs.isEmpty()) {
                System.out.println("No student GPA data found");
            }
            
        } catch (ServiceException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 