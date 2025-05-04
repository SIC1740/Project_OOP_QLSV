package com.myuniv.sm.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Model class for LopHoc (Academic Class) table
 */
public class AcademicClass {
    private String maLop;
    private int soLuongSinhVien;
    private List<Subject> subjects;

    public AcademicClass() {
        // Default constructor
        this.subjects = new ArrayList<>();
    }

    public AcademicClass(String maLop, int soLuongSinhVien) {
        this.maLop = maLop;
        this.soLuongSinhVien = soLuongSinhVien;
        this.subjects = new ArrayList<>();
    }

    public String getMaLop() {
        return maLop;
    }

    public void setMaLop(String maLop) {
        this.maLop = maLop;
    }

    public int getSoLuongSinhVien() {
        return soLuongSinhVien;
    }

    public void setSoLuongSinhVien(int soLuongSinhVien) {
        this.soLuongSinhVien = soLuongSinhVien;
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
    }

    public void addSubject(Subject subject) {
        if (this.subjects == null) {
            this.subjects = new ArrayList<>();
        }
        this.subjects.add(subject);
    }

    public void removeSubject(Subject subject) {
        if (this.subjects != null) {
            this.subjects.remove(subject);
        }
    }

    @Override
    public String toString() {
        return maLop;
    }
} 