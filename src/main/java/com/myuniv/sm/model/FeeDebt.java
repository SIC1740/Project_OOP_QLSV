package com.myuniv.sm.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Model class for FeeDebt table
 */
public class FeeDebt {
    public enum Status {
        CHƯA_ĐÓNG("chưa đóng"),
        ĐÃ_ĐÓNG("đã đóng");
        
        private final String value;
        
        Status(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static Status fromString(String text) {
            for (Status status : Status.values()) {
                if (status.value.equalsIgnoreCase(text)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("No status with value " + text + " found");
        }
    }
    
    private int debtId;
    private String msv;
    private String khoanThu;
    private BigDecimal soTien;
    private LocalDate hanThu;
    private Status status;
    
    // Transient fields for UI display
    private transient String tenSinhVien;
    
    public FeeDebt() {
        // Default constructor
        this.soTien = BigDecimal.ZERO;
        this.status = Status.CHƯA_ĐÓNG;
        this.hanThu = LocalDate.now().plusMonths(1); // Default due date is 1 month from now
    }
    
    public FeeDebt(int debtId, String msv, String khoanThu, BigDecimal soTien, LocalDate hanThu, Status status) {
        this.debtId = debtId;
        this.msv = msv;
        this.khoanThu = khoanThu;
        this.soTien = soTien;
        this.hanThu = hanThu;
        this.status = status;
    }

    // Constructor with string status for JDBC implementation
    public FeeDebt(int debtId, String msv, String khoanThu, BigDecimal soTien, LocalDate hanThu, String status) {
        this(debtId, msv, khoanThu, soTien, hanThu, Status.fromString(status));
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
    
    // Method to set status from string
    public void setStatus(String statusStr) {
        this.status = Status.fromString(statusStr);
    }
    
    public String getTenSinhVien() {
        return tenSinhVien;
    }
    
    public void setTenSinhVien(String tenSinhVien) {
        this.tenSinhVien = tenSinhVien;
    }
    
    // Alias for backward compatibility with existing code
    public String getHoTen() {
        return tenSinhVien;
    }
    
    public void setHoTen(String hoTen) {
        this.tenSinhVien = hoTen;
    }
    
    // Alias for backward compatibility with FeeDebtPanel
    public String getStudentName() {
        return tenSinhVien;
    }
    
    public void setStudentName(String studentName) {
        this.tenSinhVien = studentName;
    }
    
    @Override
    public String toString() {
        return "FeeDebt{" +
                "debtId=" + debtId +
                ", msv='" + msv + '\'' +
                ", khoanThu='" + khoanThu + '\'' +
                ", soTien=" + soTien +
                ", status=" + status +
                '}';
    }
} 