package com.myuniv.sm.model;

import java.time.LocalDateTime;

/**
 * Model class for grade entry periods (NhapDiem table)
 */
public class GradeEntryPeriod {
    private int nhapdiem_id;
    private int gv_lop_mon_id;
    private LocalDateTime thoi_gian_bat_dau_nhap;
    private LocalDateTime thoi_gian_ket_thuc_nhap;
    private boolean choPhepNhapDiem;
    
    // Additional fields for display purposes
    private String maMon;
    private String maLop;
    private String tenMon;
    private String tenGiangVien;

    public GradeEntryPeriod() {
    }

    public GradeEntryPeriod(int nhapdiem_id, int gv_lop_mon_id, LocalDateTime thoi_gian_bat_dau_nhap, 
                            LocalDateTime thoi_gian_ket_thuc_nhap) {
        this.nhapdiem_id = nhapdiem_id;
        this.gv_lop_mon_id = gv_lop_mon_id;
        this.thoi_gian_bat_dau_nhap = thoi_gian_bat_dau_nhap;
        this.thoi_gian_ket_thuc_nhap = thoi_gian_ket_thuc_nhap;
        this.choPhepNhapDiem = true;
    }

    public int getNhapdiem_id() {
        return nhapdiem_id;
    }

    public void setNhapdiem_id(int nhapdiem_id) {
        this.nhapdiem_id = nhapdiem_id;
    }

    public int getGv_lop_mon_id() {
        return gv_lop_mon_id;
    }

    public void setGv_lop_mon_id(int gv_lop_mon_id) {
        this.gv_lop_mon_id = gv_lop_mon_id;
    }

    public LocalDateTime getThoi_gian_bat_dau_nhap() {
        return thoi_gian_bat_dau_nhap;
    }

    public void setThoi_gian_bat_dau_nhap(LocalDateTime thoi_gian_bat_dau_nhap) {
        this.thoi_gian_bat_dau_nhap = thoi_gian_bat_dau_nhap;
    }

    public LocalDateTime getThoi_gian_ket_thuc_nhap() {
        return thoi_gian_ket_thuc_nhap;
    }

    public void setThoi_gian_ket_thuc_nhap(LocalDateTime thoi_gian_ket_thuc_nhap) {
        this.thoi_gian_ket_thuc_nhap = thoi_gian_ket_thuc_nhap;
    }
    
    public boolean isChoPhepNhapDiem() {
        return choPhepNhapDiem;
    }

    public void setChoPhepNhapDiem(boolean choPhepNhapDiem) {
        this.choPhepNhapDiem = choPhepNhapDiem;
    }

    public String getMaMon() {
        return maMon;
    }

    public void setMaMon(String maMon) {
        this.maMon = maMon;
    }

    public String getMaLop() {
        return maLop;
    }

    public void setMaLop(String maLop) {
        this.maLop = maLop;
    }

    public String getTenMon() {
        return tenMon;
    }

    public void setTenMon(String tenMon) {
        this.tenMon = tenMon;
    }

    public String getTenGiangVien() {
        return tenGiangVien;
    }

    public void setTenGiangVien(String tenGiangVien) {
        this.tenGiangVien = tenGiangVien;
    }
} 