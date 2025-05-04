package com.myuniv.sm.model;

import java.time.LocalDateTime;

/**
 * Model class for ThongBao (Notification) table
 */
public class Notification {
    private int thongbaoId;
    private int gvLopMonId;
    private String tieuDe;
    private String noiDung;
    private LocalDateTime ngayTao;
    
    // Additional fields for display/reference
    private transient String maGiangVien;
    private transient String tenGiangVien;
    private transient String maLop;
    private transient String maMon;
    private transient String tenMon;
    
    public Notification() {
        // Default constructor
    }
    
    public Notification(int gvLopMonId, String tieuDe, String noiDung) {
        this.gvLopMonId = gvLopMonId;
        this.tieuDe = tieuDe;
        this.noiDung = noiDung;
    }

    public int getThongbaoId() {
        return thongbaoId;
    }

    public void setThongbaoId(int thongbaoId) {
        this.thongbaoId = thongbaoId;
    }

    public int getGvLopMonId() {
        return gvLopMonId;
    }

    public void setGvLopMonId(int gvLopMonId) {
        this.gvLopMonId = gvLopMonId;
    }

    public String getTieuDe() {
        return tieuDe;
    }

    public void setTieuDe(String tieuDe) {
        this.tieuDe = tieuDe;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public LocalDateTime getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao = ngayTao;
    }

    public String getMaGiangVien() {
        return maGiangVien;
    }

    public void setMaGiangVien(String maGiangVien) {
        this.maGiangVien = maGiangVien;
    }

    public String getTenGiangVien() {
        return tenGiangVien;
    }

    public void setTenGiangVien(String tenGiangVien) {
        this.tenGiangVien = tenGiangVien;
    }

    public String getMaLop() {
        return maLop;
    }

    public void setMaLop(String maLop) {
        this.maLop = maLop;
    }

    public String getMaMon() {
        return maMon;
    }

    public void setMaMon(String maMon) {
        this.maMon = maMon;
    }

    public String getTenMon() {
        return tenMon;
    }

    public void setTenMon(String tenMon) {
        this.tenMon = tenMon;
    }
    
    @Override
    public String toString() {
        return tieuDe;
    }
} 