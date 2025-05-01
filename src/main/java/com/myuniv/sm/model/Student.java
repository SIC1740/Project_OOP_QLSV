package com.myuniv.sm.model;

import java.time.LocalDate;

public class Student {
    private String msv;
    private String hoTen;
    private String gioiTinh;
    private LocalDate ngaySinh;
    private String email;
    private String soDienThoai;
    private String diaChi;
    private String maLop;
    
    public Student() {
    }
    
    public Student(String msv, String hoTen, String gioiTinh, LocalDate ngaySinh, 
                  String email, String soDienThoai, String diaChi, String maLop) {
        this.msv = msv;
        this.hoTen = hoTen;
        this.gioiTinh = gioiTinh;
        this.ngaySinh = ngaySinh;
        this.email = email;
        this.soDienThoai = soDienThoai;
        this.diaChi = diaChi;
        this.maLop = maLop;
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
    
    public String getGioiTinh() {
        return gioiTinh;
    }
    
    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
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
    
    public String getDiaChi() {
        return diaChi;
    }
    
    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }
    
    public String getMaLop() {
        return maLop;
    }
    
    public void setMaLop(String maLop) {
        this.maLop = maLop;
    }
    
    @Override
    public String toString() {
        return "Student{" +
                "msv='" + msv + '\'' +
                ", hoTen='" + hoTen + '\'' +
                ", gioiTinh='" + gioiTinh + '\'' +
                ", ngaySinh=" + ngaySinh +
                ", maLop='" + maLop + '\'' +
                '}';
    }
} 