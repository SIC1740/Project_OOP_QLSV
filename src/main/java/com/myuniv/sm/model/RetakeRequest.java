package com.myuniv.sm.model;

import java.time.LocalDate;

/**
 * Model class for RetakeRequest table
 */
public class RetakeRequest {
    public enum Status {
        MỚI("mới"),
        DUYỆT("duyệt"),
        TỪ_CHỐI("từ chối"),
        CHẤP_NHẬN("chấp nhận");
        
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
    
    private int requestId;
    private String msv;
    private String maLop;
    private LocalDate ngayDangKi;
    private Status status;
    
    // Transient fields for UI display
    private transient String tenSinhVien;
    private transient String tenLop;
    
    public RetakeRequest() {
        // Default constructor
        this.ngayDangKi = LocalDate.now();
        this.status = Status.MỚI;
    }
    
    public RetakeRequest(int requestId, String msv, String maLop, LocalDate ngayDangKi, Status status) {
        this.requestId = requestId;
        this.msv = msv;
        this.maLop = maLop;
        this.ngayDangKi = ngayDangKi;
        this.status = status;
    }
    
    public int getRequestId() {
        return requestId;
    }
    
    public void setRequestId(int requestId) {
        this.requestId = requestId;
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
    
    public LocalDate getNgayDangKi() {
        return ngayDangKi;
    }
    
    public void setNgayDangKi(LocalDate ngayDangKi) {
        this.ngayDangKi = ngayDangKi;
    }
    
    public Status getStatus() {
        return status;
    }
    
    public void setStatus(Status status) {
        this.status = status;
    }
    
    public String getTenSinhVien() {
        return tenSinhVien;
    }
    
    public void setTenSinhVien(String tenSinhVien) {
        this.tenSinhVien = tenSinhVien;
    }
    
    public String getTenLop() {
        return tenLop;
    }
    
    public void setTenLop(String tenLop) {
        this.tenLop = tenLop;
    }
    
    @Override
    public String toString() {
        return "RetakeRequest{" +
                "requestId=" + requestId +
                ", msv='" + msv + '\'' +
                ", maLop='" + maLop + '\'' +
                ", status=" + status +
                '}';
    }
} 