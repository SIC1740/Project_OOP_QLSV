# Hệ thống Quản lý Sinh viên

Ứng dụng quản lý sinh viên viết bằng Java với giao diện Swing.

## Các Tính Năng

- **Đăng nhập**: Hỗ trợ đăng nhập với nhiều vai trò (admin, giảng viên, sinh viên)
- **Quản lý thông tin sinh viên**: Xem, thêm, sửa, xóa thông tin sinh viên
- **Quản lý giảng viên**: Xem, thêm, sửa, xóa thông tin giảng viên
- **Quản lý lớp học**: Xem danh sách lớp, thêm/sửa/xóa lớp
- **Quản lý điểm số**: Nhập điểm, xem bảng điểm, thống kê
- **Quản lý học phí**: Theo dõi thanh toán học phí, nợ học phí

## Cấu Trúc Dự Án

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── myuniv/
│   │           └── sm/
│   │               ├── dao/         # Data Access Objects
│   │               ├── model/       # Các model (Student, Lecturer, ...)
│   │               ├── service/     # Business logic 
│   │               ├── util/        # Các tiện ích
│   │               └── view/        # Giao diện người dùng
│   │                   ├── admin/      # Giao diện admin
│   │                   ├── teacher/    # Giao diện giảng viên
│   │                   └── student/    # Giao diện sinh viên
│   └── resources/    # Tài nguyên (hình ảnh, icon, ...)
└── test/           # Unit tests
```

## Cài Đặt và Chạy

### Yêu Cầu Hệ Thống

- Java JDK 11 trở lên
- MySQL 5.7 trở lên

### Cài Đặt Cơ Sở Dữ Liệu

1. Tạo cơ sở dữ liệu MySQL:

```sql
CREATE DATABASE myuniv;
```

2. Thêm các bảng cần thiết:

```sql
USE myuniv;

-- Bảng User
CREATE TABLE User (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    last_login DATETIME
);

-- Bảng LopHoc
CREATE TABLE LopHoc (
    ma_lop VARCHAR(20) PRIMARY KEY,
    ten_lop VARCHAR(100) NOT NULL,
    khoa VARCHAR(50) NOT NULL
);

-- Bảng SinhVien
CREATE TABLE SinhVien (
    STT INT AUTO_INCREMENT,
    msv VARCHAR(20) PRIMARY KEY,
    ho_ten VARCHAR(100) NOT NULL,
    ngay_sinh DATE NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    so_dien_thoai VARCHAR(20),
    ma_lop VARCHAR(20) NOT NULL,
    CONSTRAINT STT UNIQUE (STT),
    CONSTRAINT fk_sv_lop FOREIGN KEY (ma_lop) REFERENCES LopHoc(ma_lop) ON UPDATE CASCADE
);

-- Bảng GiangVien
CREATE TABLE GiangVien (
    ma_giangvien VARCHAR(10) PRIMARY KEY,
    ho_ten VARCHAR(100) NOT NULL,
    ngay_sinh DATE NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    so_dien_thoai VARCHAR(20),
    hoc_vi ENUM('Ths', 'TS', 'PGS', 'GS', 'Khác')
);

-- Các bảng khác...
```

3. Thêm dữ liệu mẫu:

```sql
-- Thêm tài khoản admin
INSERT INTO User (username, password_hash, role) VALUES 
('admin', '$2a$10$WbHpOKQOJC6AqFmMl04n5.TxWx.yXfIPmjEf1kI6C3cJ90uLYA3hy', 'admin');

-- Thêm tài khoản giảng viên
INSERT INTO User (username, password_hash, role) VALUES 
('GV001', '$2a$10$WbHpOKQOJC6AqFmMl04n5.TxWx.yXfIPmjEf1kI6C3cJ90uLYA3hy', 'giangvien');

-- Thêm tài khoản sinh viên
INSERT INTO User (username, password_hash, role) VALUES 
('SV001', '$2a$10$WbHpOKQOJC6AqFmMl04n5.TxWx.yXfIPmjEf1kI6C3cJ90uLYA3hy', 'sinhvien');

-- Thêm dữ liệu khác...
```

### Biên Dịch và Chạy Ứng Dụng

1. Biên dịch dự án:

```
javac -d out src/main/java/com/myuniv/sm/Main.java
```

2. Chạy ứng dụng:

```
java -cp out com.myuniv.sm.Main
```

### Đăng Nhập Mẫu

- Admin: username = `admin`, password = `admin`
- Giảng viên: username = `GV001`, password = `password`
- Sinh viên: username = `SV001`, password = `password`

## Phát Triển

### Thêm Tính Năng Mới

1. Thêm model mới trong `com.myuniv.sm.model`
2. Thêm DAO trong `com.myuniv.sm.dao` và triển khai trong `com.myuniv.sm.dao.impl`
3. Thêm service trong `com.myuniv.sm.service`
4. Thêm giao diện trong `com.myuniv.sm.view`

## Tác Giả

Phát triển bởi: Team OOP

## Giấy Phép

Dự án này được phân phối theo giấy phép MIT.