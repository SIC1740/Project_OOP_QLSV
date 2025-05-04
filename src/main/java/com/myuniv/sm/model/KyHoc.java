package com.myuniv.sm.model;

public class KyHoc {
    private int kyhocId;
    private String tenKyhoc;
    private String maMon;
    
    // Transient field - not in database but useful for display
    private String tenMon;
    private int soTinChi;
    
    public KyHoc() {
    }
    
    public KyHoc(int kyhocId, String tenKyhoc, String maMon) {
        this.kyhocId = kyhocId;
        this.tenKyhoc = tenKyhoc;
        this.maMon = maMon;
    }
    
    public int getKyhocId() {
        return kyhocId;
    }
    
    public void setKyhocId(int kyhocId) {
        this.kyhocId = kyhocId;
    }
    
    public String getTenKyhoc() {
        return tenKyhoc;
    }
    
    public void setTenKyhoc(String tenKyhoc) {
        this.tenKyhoc = tenKyhoc;
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
        return tenKyhoc + " - " + tenMon;
    }
} 