-- Table for managing grade entry periods
CREATE TABLE IF NOT EXISTS NhapDiem (
    nhapdiem_id           INT       AUTO_INCREMENT PRIMARY KEY,
    gv_lop_mon_id         INT       NOT NULL,
    thoi_gian_bat_dau_nhap   DATETIME NOT NULL,
    thoi_gian_ket_thuc_nhap  DATETIME NOT NULL,
    CONSTRAINT fk_nd_gv_lop_mon
        FOREIGN KEY (gv_lop_mon_id)
        REFERENCES GiangVien_Lop_MonHoc(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
); 