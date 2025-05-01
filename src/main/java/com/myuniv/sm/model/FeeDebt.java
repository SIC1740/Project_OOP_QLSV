package com.myuniv.sm.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FeeDebt {
    private int debtId;
    private String msv;
    private String khoanThu;
    private BigDecimal soTien;
    private LocalDate hanThu;
    private String status;
    
    // Student name for display purposes (not stored in database)
    private String studentName;

    public FeeDebt() {
    }

    public FeeDebt(int debtId, String msv, String khoanThu, BigDecimal soTien, LocalDate hanThu, String status) {
        this.debtId = debtId;
        this.msv = msv;
        this.khoanThu = khoanThu;
        this.soTien = soTien;
        this.hanThu = hanThu;
        this.status = status;
    }

    public int getDebtId() {
        return debtId;
    }

    public void setDebtId(int debtId) {
        this.debtId = debtId;
    }

    public String getMsv() {
        return msv;
    }

    public void setMsv(String msv) {
        this.msv = msv;
    }

    public String getKhoanThu() {
        return khoanThu;
    }

    public void setKhoanThu(String khoanThu) {
        this.khoanThu = khoanThu;
    }

    public BigDecimal getSoTien() {
        return soTien;
    }

    public void setSoTien(BigDecimal soTien) {
        this.soTien = soTien;
    }

    public LocalDate getHanThu() {
        return hanThu;
    }

    public void setHanThu(LocalDate hanThu) {
        this.hanThu = hanThu;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getStudentName() {
        return studentName;
    }
    
    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }
    
    @Override
    public String toString() {
        return "FeeDebt{" +
                "debtId=" + debtId +
                ", msv='" + msv + '\'' +
                ", khoanThu='" + khoanThu + '\'' +
                ", soTien=" + soTien +
                ", hanThu=" + hanThu +
                ", status='" + status + '\'' +
                '}';
    }
} 