package com.myuniv.sm.model;

import java.time.LocalDate;

public class Student {
    private int STT;
    private String msv;
    private String hoTen;
    private LocalDate ngaySinh;
    private String email;
    private String soDienThoai;
    private String maLop;
    
    public Student() {
    }
    
    public Student(String msv, String hoTen, LocalDate ngaySinh, 
                  String email, String soDienThoai, String maLop) {
        this.msv = msv;
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.email = email;
        this.soDienThoai = soDienThoai;
        this.maLop = maLop;
    }
    
    public int getSTT() {
        return STT;
    }
    
    public void setSTT(int STT) {
        this.STT = STT;
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
    
    public LocalDate getNgaySinh() {
        return ngaySinh;
    }
    
    public void setNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getSoDienThoai() {
        return soDienThoai;
    }
    
    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }
    
    public String getMaLop() {
        return maLop;
    }
    
    public void setMaLop(String maLop) {
        this.maLop = maLop;
    }
    
    // For backwards compatibility with any existing code
    public String getGioiTinh() {
        return null;
    }
    
    public String getDiaChi() {
        return null;
    }
    
    public String getTenLop() {
        return null;
    }
    
    @Override
    public String toString() {
        return "Student{" +
                "msv='" + msv + '\'' +
                ", hoTen='" + hoTen + '\'' +
                ", ngaySinh=" + ngaySinh +
                ", email='" + email + '\'' +
                ", soDienThoai='" + soDienThoai + '\'' +
                ", maLop='" + maLop + '\'' +
                '}';
    }
} 