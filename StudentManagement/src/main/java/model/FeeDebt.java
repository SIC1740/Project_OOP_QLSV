package src.main.java.model;

import java.util.Date;

public class FeeDebt {
    private int debtId;
    private String msv;
    private String khoanThu;
    private double soTien;
    private Date hanThu;
    private String status;

    public FeeDebt(int debtId, String msv, String khoanThu, double soTien, Date hanThu, String status) {
        this.debtId = debtId;
        this.msv = msv;
        this.khoanThu = khoanThu;
        this.soTien = soTien;
        this.hanThu = hanThu;
        this.status = status;
    }

    // Getters và Setters
    public int getDebtId() {
        return debtId;
    }

    public String getMsv() {
        return msv;
    }

    public String getKhoanThu() {
        return khoanThu;
    }

    public double getSoTien() {
        return soTien;
    }

    public Date getHanThu() {
        return hanThu;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Mã nợ: " + debtId + ", MSV: " + msv + ", Khoản thu: " + khoanThu + 
               ", Số tiền: " + soTien + ", Hạn thu: " + hanThu + ", Trạng thái: " + status;
    }
}