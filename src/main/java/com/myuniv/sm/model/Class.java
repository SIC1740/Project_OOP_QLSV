package com.myuniv.sm.model;

/**
 * Model class representing a class/LopHoc
 */
public class Class {
    private String maLop;
    private int studentCount;
    private String tenLop;
    // Add additional fields as needed
    
    public Class() {
    }
    
    public Class(String maLop) {
        this.maLop = maLop;
    }
    
    public Class(String maLop, int studentCount) {
        this.maLop = maLop;
        this.studentCount = studentCount;
    }
    
    public Class(String maLop, String tenLop, int studentCount) {
        this.maLop = maLop;
        this.tenLop = tenLop;
        this.studentCount = studentCount;
    }
    
    public String getMaLop() {
        return maLop;
    }
    
    public void setMaLop(String maLop) {
        this.maLop = maLop;
    }
    
    public int getStudentCount() {
        return studentCount;
    }
    
    public void setStudentCount(int studentCount) {
        this.studentCount = studentCount;
    }
    
    public String getTenLop() {
        return tenLop;
    }
    
    public void setTenLop(String tenLop) {
        this.tenLop = tenLop;
    }
    
    @Override
    public String toString() {
        return maLop;
    }
} 