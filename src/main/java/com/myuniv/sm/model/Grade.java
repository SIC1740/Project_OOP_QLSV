package com.myuniv.sm.model;

import java.math.BigDecimal;

/**
 * Model class for DiemMon (Grade) table
 */
public class Grade {
    private int idDiemMon;
    private String msv;
    private String maLop;
    private String maMon;
    private BigDecimal diemCC;      // Điểm chuyên cần (10%)
    private BigDecimal diemQTrinh;  // Điểm quá trình (30%)
    private BigDecimal diemThi;     // Điểm thi (60%)
    private BigDecimal diemTK;      // Điểm tổng kết (calculated)
    private BigDecimal diemHe4;     // Điểm hệ 4 (calculated)
    private String xepLoai;         // Xếp loại (calculated)
    private String danhGia;         // Đánh giá (calculated)
    
    // Cached student information for UI display
    private transient String tenSinhVien;
    
    public Grade() {
        // Default constructor
        this.diemCC = BigDecimal.ZERO;
        this.diemQTrinh = BigDecimal.ZERO;
        this.diemThi = BigDecimal.ZERO;
    }
    
    public Grade(String msv, String maLop, String maMon, 
                BigDecimal diemCC, BigDecimal diemQTrinh, BigDecimal diemThi) {
        this.msv = msv;
        this.maLop = maLop;
        this.maMon = maMon;
        this.diemCC = diemCC;
        this.diemQTrinh = diemQTrinh;
        this.diemThi = diemThi;
    }

    public int getIdDiemMon() {
        return idDiemMon;
    }

    public void setIdDiemMon(int idDiemMon) {
        this.idDiemMon = idDiemMon;
    }

    public String getMsv() {
        return msv;
    }

    public void setMsv(String msv) {
        this.msv = msv;
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

    public BigDecimal getDiemCC() {
        return diemCC;
    }

    public void setDiemCC(BigDecimal diemCC) {
        this.diemCC = diemCC;
    }

    public BigDecimal getDiemQTrinh() {
        return diemQTrinh;
    }

    public void setDiemQTrinh(BigDecimal diemQTrinh) {
        this.diemQTrinh = diemQTrinh;
    }

    public BigDecimal getDiemThi() {
        return diemThi;
    }

    public void setDiemThi(BigDecimal diemThi) {
        this.diemThi = diemThi;
    }

    public BigDecimal getDiemTK() {
        return diemTK;
    }

    public void setDiemTK(BigDecimal diemTK) {
        this.diemTK = diemTK;
    }

    public BigDecimal getDiemHe4() {
        return diemHe4;
    }

    public void setDiemHe4(BigDecimal diemHe4) {
        this.diemHe4 = diemHe4;
    }

    public String getXepLoai() {
        return xepLoai;
    }

    public void setXepLoai(String xepLoai) {
        this.xepLoai = xepLoai;
    }

    public String getDanhGia() {
        return danhGia;
    }

    public void setDanhGia(String danhGia) {
        this.danhGia = danhGia;
    }
    
    public String getTenSinhVien() {
        return tenSinhVien;
    }
    
    public void setTenSinhVien(String tenSinhVien) {
        this.tenSinhVien = tenSinhVien;
    }

    @Override
    public String toString() {
        return "DiemMon{" + msv + ", điểm TK=" + diemTK + ", xếp loại=" + xepLoai + '}';
    }
} 