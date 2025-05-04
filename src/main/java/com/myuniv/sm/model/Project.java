package com.myuniv.sm.model;

import java.time.LocalDate;

/**
 * Model class for DoAn (Project) table
 */
public class Project {
    private int doanId;
    private String msv;
    private String tenDeTai;
    private String maGiangvien;
    private LocalDate ngayDangKy;
    
    // Transient fields for UI display
    private transient String tenSinhVien;
    private transient String tenGiangVien;
    
    public Project() {
        // Default constructor
        this.ngayDangKy = LocalDate.now();
    }
    
    public Project(int doanId, String msv, String tenDeTai, String maGiangvien, LocalDate ngayDangKy) {
        this.doanId = doanId;
        this.msv = msv;
        this.tenDeTai = tenDeTai;
        this.maGiangvien = maGiangvien;
        this.ngayDangKy = ngayDangKy;
    }
    
    public int getDoanId() {
        return doanId;
    }
    
    public void setDoanId(int doanId) {
        this.doanId = doanId;
    }
    
    public String getMsv() {
        return msv;
    }
    
    public void setMsv(String msv) {
        this.msv = msv;
    }
    
    public String getTenDeTai() {
        return tenDeTai;
    }
    
    public void setTenDeTai(String tenDeTai) {
        this.tenDeTai = tenDeTai;
    }
    
    public String getMaGiangvien() {
        return maGiangvien;
    }
    
    public void setMaGiangvien(String maGiangvien) {
        this.maGiangvien = maGiangvien;
    }
    
    public LocalDate getNgayDangKy() {
        return ngayDangKy;
    }
    
    public void setNgayDangKy(LocalDate ngayDangKy) {
        this.ngayDangKy = ngayDangKy;
    }
    
    public String getTenSinhVien() {
        return tenSinhVien;
    }
    
    public void setTenSinhVien(String tenSinhVien) {
        this.tenSinhVien = tenSinhVien;
    }
    
    public String getTenGiangVien() {
        return tenGiangVien;
    }
    
    public void setTenGiangVien(String tenGiangVien) {
        this.tenGiangVien = tenGiangVien;
    }
    
    @Override
    public String toString() {
        return tenDeTai;
    }
} 