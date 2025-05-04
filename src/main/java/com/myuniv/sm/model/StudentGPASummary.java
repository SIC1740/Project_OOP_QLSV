package com.myuniv.sm.model;

import java.math.BigDecimal;

/**
 * Model class to hold student GPA summary information
 */
public class StudentGPASummary {
    private String msv;
    private String hoTen;
    private int totalCredits;
    private BigDecimal averageGPA;
    
    public StudentGPASummary(String msv, String hoTen) {
        this.msv = msv;
        this.hoTen = hoTen;
        this.totalCredits = 0;
        this.averageGPA = BigDecimal.ZERO;
    }
    
    public String getMsv() {
        return msv;
    }
    
    public void setMsv(String msv) {
        this.msv = msv;
    }
    
    public String getHoTen() {
        return hoTen;
    }
    
    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }
    
    public int getTotalCredits() {
        return totalCredits;
    }
    
    public void setTotalCredits(int totalCredits) {
        this.totalCredits = totalCredits;
    }
    
    public BigDecimal getAverageGPA() {
        return averageGPA;
    }
    
    public void setAverageGPA(BigDecimal averageGPA) {
        this.averageGPA = averageGPA;
    }
    
    @Override
    public String toString() {
        return "StudentGPASummary{" +
               "msv='" + msv + '\'' +
               ", hoTen='" + hoTen + '\'' +
               ", totalCredits=" + totalCredits +
               ", averageGPA=" + averageGPA +
               '}';
    }
} 