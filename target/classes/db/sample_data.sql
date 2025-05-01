-- Dữ liệu mẫu cho bảng LopHoc
INSERT INTO LopHoc (ma_lop, ten_lop, khoa) VALUES 
('D21VTHD1', 'VTHD1 - K21', 'Viễn thông'),
('D21VTHD2', 'VTHD2 - K21', 'Viễn thông');

-- Dữ liệu mẫu cho bảng SinhVien
INSERT INTO SinhVien (STT, msv, ho_ten, ngay_sinh, email, so_dien_thoai, ma_lop) VALUES 
(79, 'B19DCVT192', 'Hoàng Trung Kiên', '2001-03-18', 'hk.b19dcvt192@stu.ptit.edu.vn', '0873763116', 'D21VTHD1'),
(59, 'B21DCVT009', 'Hà Văn Cường', '2003-07-28', 'hc.b21dcvt009@stu.ptit.edu.vn', '0803413104', 'D21VTHD1'),
(33, 'B21DCVT035', 'Nguyễn Hoàng Nam', '2003-01-25', 'nn.b21dcvt035@stu.ptit.edu.vn', '0806112648', 'D21VTHD2'),
(93, 'B21DCVT041', 'Phạm Sỹ Quý', '2003-02-14', 'pq.b21dcvt041@stu.ptit.edu.vn', '0884251354', 'D21VTHD1'),
(52, 'B21DCVT049', 'Đỗ Ngọc An', '2003-09-29', 'da.b21dcvt049@stu.ptit.edu.vn', '0810633218', 'D21VTHD1'),
(4, 'B21DCVT053', 'Phạm Hồng An', '2003-11-15', 'pa.b21dcvt053@stu.ptit.edu.vn', '0838930555', 'D21VTHD1'),
(1, 'B21DCVT061', 'Nguyễn Hoàng Anh', '2003-10-24', 'na.b21dcvt061@stu.ptit.edu.vn', '0830392137', 'D21VTHD2'),
(53, 'B21DCVT065', 'Nguyễn Tuấn Anh', '2003-12-18', 'na.b21dcvt065@stu.ptit.edu.vn', '0819600133', 'D21VTHD1'),
(54, 'B21DCVT066', 'Nguyễn Tuấn Anh', '2003-01-22', 'na.b21dcvt066@stu.ptit.edu.vn', '0889083863', 'D21VTHD1'),
(2, 'B21DCVT067', 'Nguyễn Việt Anh', '2003-02-24', 'na.b21dcvt067@stu.ptit.edu.vn', '0865821972', 'D21VTHD2'),
(3, 'B21DCVT069', 'Phạm Việt Anh', '2003-07-24', 'pa.b21dcvt069@stu.ptit.edu.vn', '0896687577', 'D21VTHD2'),
(55, 'B21DCVT073', 'Chu Xuân Bách', '2003-10-21', 'cb.b21dcvt073@stu.ptit.edu.vn', '0879402654', 'D21VTHD1'),
(56, 'B21DCVT074', 'Nguyễn Trần Bách', '2003-12-12', 'nb.b21dcvt074@stu.ptit.edu.vn', '0823511615', 'D21VTHD1'),
(5, 'B21DCVT083', 'Nguyễn Minh Chiến', '2003-12-12', 'nc.b21dcvt083@stu.ptit.edu.vn', '0808249269', 'D21VTHD2'),
(6, 'B21DCVT085', 'Nguyễn Đức Chính', '2003-12-12', 'nc.b21dcvt085@stu.ptit.edu.vn', '0847118013', 'D21VTHD2'),
(57, 'B21DCVT086', 'Nguyễn Mậu Chiến', '2003-12-28', 'nc.b21dcvt086@stu.ptit.edu.vn', '0859407816', 'D21VTHD1'),
(58, 'B21DCVT089', 'Nguyễn Thành Công', '2003-12-27', 'nc.b21dcvt089@stu.ptit.edu.vn', '0818495931', 'D21VTHD1'),
(60, 'B21DCVT090', 'Nguyễn Phúc Cường', '2003-08-09', 'nc.b21dcvt090@stu.ptit.edu.vn', '0875255341', 'D21VTHD1');

-- Dữ liệu mẫu cho bảng User để đăng nhập với vai trò sinh viên
INSERT INTO User (username, password_hash, role, last_login) VALUES
('B19DCVT192', '$2a$10$WbHpOKQOJC6AqFmMl04n5.TxWx.yXfIPmjEf1kI6C3cJ90uLYA3hy', 'sinhvien', NULL),
('B21DCVT009', '$2a$10$WbHpOKQOJC6AqFmMl04n5.TxWx.yXfIPmjEf1kI6C3cJ90uLYA3hy', 'sinhvien', NULL),
('B21DCVT035', '$2a$10$WbHpOKQOJC6AqFmMl04n5.TxWx.yXfIPmjEf1kI6C3cJ90uLYA3hy', 'sinhvien', NULL),
('B21DCVT041', '$2a$10$WbHpOKQOJC6AqFmMl04n5.TxWx.yXfIPmjEf1kI6C3cJ90uLYA3hy', 'sinhvien', NULL),
('B21DCVT049', '$2a$10$WbHpOKQOJC6AqFmMl04n5.TxWx.yXfIPmjEf1kI6C3cJ90uLYA3hy', 'sinhvien', NULL); 