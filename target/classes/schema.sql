-- LopHoc table definition
CREATE TABLE IF NOT EXISTS LopHoc (
    ma_lop VARCHAR(20) NOT NULL PRIMARY KEY,
    ten_lop VARCHAR(100)
);

-- SinhVien table definition
CREATE TABLE IF NOT EXISTS SinhVien (
    STT INT AUTO_INCREMENT,
    msv VARCHAR(20) NOT NULL PRIMARY KEY,
    ho_ten VARCHAR(100) NOT NULL,
    ngay_sinh DATE NOT NULL,
    email VARCHAR(100) NOT NULL,
    so_dien_thoai VARCHAR(20) NULL,
    ma_lop VARCHAR(20) NOT NULL,
    CONSTRAINT STT UNIQUE (STT),
    CONSTRAINT email UNIQUE (email),
    CONSTRAINT fk_sv_lop FOREIGN KEY (ma_lop) REFERENCES LopHoc (ma_lop)
        ON UPDATE CASCADE
);

-- Insert some sample data
INSERT INTO LopHoc (ma_lop, ten_lop) VALUES 
('CNTT01', 'Công nghệ thông tin 1'),
('CNTT02', 'Công nghệ thông tin 2'),
('KTPM01', 'Kỹ thuật phần mềm 1');

-- Note: When inserting students, make sure the ma_lop values exist in the LopHoc table
INSERT INTO SinhVien (msv, ho_ten, ngay_sinh, email, so_dien_thoai, ma_lop) VALUES
('SV001', 'Nguyễn Văn A', '2000-01-01', 'nguyenvana@example.com', '0123456789', 'CNTT01'),
('SV002', 'Trần Thị B', '2000-02-02', 'tranthib@example.com', '0123456788', 'CNTT01'),
('SV003', 'Lê Văn C', '2000-03-03', 'levanc@example.com', '0123456787', 'CNTT02'); 