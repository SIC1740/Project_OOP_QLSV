package com.myuniv.sm.model;

import java.time.LocalDate;

public class Lecturer {
    private String maGiangVien;
    private String hoTen;
    private LocalDate ngaySinh;
    private String email;
    private String soDienThoai;
    private String hocVi;

    // Constructors
    public Lecturer() {}

    public Lecturer(String maGiangVien, String hoTen, LocalDate ngaySinh, 
                   String email, String soDienThoai, String hocVi) {
        this.maGiangVien = maGiangVien;
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.email = email;
        this.soDienThoai = soDienThoai;
        this.hocVi = hocVi;
    }

    // Getters and Setters
    public String getMaGiangVien() {
        return maGiangVien;
    }

    public void setMaGiangVien(String maGiangVien) {
        this.maGiangVien = maGiangVien;
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

    public String getHocVi() {
        return hocVi;
    }

    public void setHocVi(String hocVi) {
        this.hocVi = hocVi;
    }

    @Override
    public String toString() {
        return "Lecturer{" +
                "maGiangVien='" + maGiangVien + '\'' +
                ", hoTen='" + hoTen + '\'' +
                ", ngaySinh=" + ngaySinh +
                ", email='" + email + '\'' +
                ", soDienThoai='" + soDienThoai + '\'' +
                ", hocVi='" + hocVi + '\'' +
                '}';
    }
} 