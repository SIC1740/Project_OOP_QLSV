// File: src/main/java/com/myuniv/sm/model/Score.java
package com.myuniv.sm.model;

import java.time.LocalDate;

public class Score {
    private int ID;
    private String MSV;
    private double Diem_TB;
    private LocalDate Ngay_tao;
    private LocalDate Ngay_sua;

    public Score() {}
    public int getId() { return ID; }
    public void setId(int ID) { this.ID = ID; }
    public String getMsv() { return MSV; }
    public void setMsv(String MSV) { this.MSV = MSV; }
    public double getAvgScore() { return Diem_TB; }
    public void setAvgScore(double Diem_TB) { this.Diem_TB = Diem_TB; }
    public LocalDate getDateCreated() { return Ngay_tao; }
    public void setDateCreated(LocalDate Ngay_tao) { this.Ngay_tao = Ngay_tao; }
    public LocalDate getDateModified() { return Ngay_sua; }
    public void setDateModified(LocalDate Ngay_sua) { this.Ngay_sua = Ngay_sua; }
}