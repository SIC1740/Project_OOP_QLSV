

package model;

public class LopHoc {
    private String maLop;
    private int soLuongSinhVien;

    public LopHoc(String maLop, int soLuongSinhVien) {
        this.maLop = maLop;
        this.soLuongSinhVien = soLuongSinhVien;
    }

    // Getters và Setters
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

    @Override
    public String toString() {
        return "Mã lớp: " + maLop + ", Số lượng SV: " + soLuongSinhVien;
    }
}