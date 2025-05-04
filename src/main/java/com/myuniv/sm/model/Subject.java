package com.myuniv.sm.model;

/**
 * Model class for MonHoc (Subject) table
 */
public class Subject {
    private String maMon;
    private String tenMon;
    private int soTinChi;

    public Subject() {
        // Default constructor
    }

    public Subject(String maMon, String tenMon, int soTinChi) {
        this.maMon = maMon;
        this.tenMon = tenMon;
        this.soTinChi = soTinChi;
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

    public int getSoTinChi() {
        return soTinChi;
    }

    public void setSoTinChi(int soTinChi) {
        this.soTinChi = soTinChi;
    }

    @Override
    public String toString() {
        return maMon + " - " + tenMon + " (" + soTinChi + " tín chỉ)";
    }
} 