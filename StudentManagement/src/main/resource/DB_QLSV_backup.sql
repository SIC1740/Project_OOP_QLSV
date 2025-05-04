-- MySQL dump 10.13  Distrib 8.2.0, for Linux (x86_64)
--
-- Host: localhost    Database: DB_QLSV
-- ------------------------------------------------------
-- Server version	8.2.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `DiemMon`
--

DROP TABLE IF EXISTS `DiemMon`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `DiemMon` (
  `msv` varchar(20) NOT NULL,
  `ma_lop` varchar(20) NOT NULL,
  `ma_mon` varchar(20) NOT NULL,
  `diem_cc` decimal(4,2) NOT NULL COMMENT '10%',
  `diem_qtrinh` decimal(4,2) NOT NULL COMMENT '30%',
  `diem_thi` decimal(4,2) NOT NULL COMMENT '60%',
  `diem_tk` decimal(4,2) GENERATED ALWAYS AS (round((((`diem_cc` * 0.1) + (`diem_qtrinh` * 0.3)) + (`diem_thi` * 0.6)),2)) STORED,
  `diem_he4` decimal(2,1) GENERATED ALWAYS AS ((case when (`diem_tk` < 4.0) then 0 when (`diem_tk` < 5.0) then 1 when (`diem_tk` < 5.5) then 1.5 when (`diem_tk` < 6.5) then 2 when (`diem_tk` < 7.0) then 2.5 when (`diem_tk` < 8.0) then 3 when (`diem_tk` < 8.5) then 3.5 when (`diem_tk` < 9.0) then 3.7 else 4.0 end)) STORED,
  `xep_loai` varchar(2) GENERATED ALWAYS AS ((case when (`diem_he4` = 0) then _utf8mb4'F' when (`diem_he4` = 1) then _utf8mb4'D' when (`diem_he4` = 1.5) then _utf8mb4'D+' when (`diem_he4` = 2) then _utf8mb4'C' when (`diem_he4` = 2.5) then _utf8mb4'C+' when (`diem_he4` = 3) then _utf8mb4'B' when (`diem_he4` = 3.5) then _utf8mb4'B+' when (`diem_he4` = 3.7) then _utf8mb4'A' when (`diem_he4` = 4.0) then _utf8mb4'A+' end)) STORED,
  `danh_gia` char(1) GENERATED ALWAYS AS (if((`xep_loai` = _utf8mb4'F'),_utf8mb4'x',_utf8mb4'✓')) STORED,
  PRIMARY KEY (`msv`,`ma_lop`,`ma_mon`),
  KEY `fk_dm_lhmh` (`ma_lop`,`ma_mon`),
  CONSTRAINT `fk_dm_lhmh` FOREIGN KEY (`ma_lop`, `ma_mon`) REFERENCES `LopHoc_MonHoc` (`ma_lop`, `ma_mon`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_dm_sv` FOREIGN KEY (`msv`) REFERENCES `SinhVien` (`msv`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `DiemMon`
--

LOCK TABLES `DiemMon` WRITE;
/*!40000 ALTER TABLE `DiemMon` DISABLE KEYS */;
INSERT INTO `DiemMon` (`msv`, `ma_lop`, `ma_mon`, `diem_cc`, `diem_qtrinh`, `diem_thi`) VALUES ('B21DCVT378','D21VTMD1','TEL1448',10.00,8.00,7.00);
/*!40000 ALTER TABLE `DiemMon` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `DoAn`
--

DROP TABLE IF EXISTS `DoAn`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `DoAn` (
  `doan_id` int NOT NULL AUTO_INCREMENT,
  `msv` varchar(20) NOT NULL,
  `ten_de_tai` varchar(200) NOT NULL,
  `ma_giangvien` varchar(10) DEFAULT NULL,
  `ngay_dang_ky` date NOT NULL,
  PRIMARY KEY (`doan_id`),
  KEY `fk_doan_sv` (`msv`),
  KEY `fk_doan_gv` (`ma_giangvien`),
  CONSTRAINT `fk_doan_gv` FOREIGN KEY (`ma_giangvien`) REFERENCES `GiangVien` (`ma_giangvien`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_doan_sv` FOREIGN KEY (`msv`) REFERENCES `SinhVien` (`msv`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `DoAn`
--

LOCK TABLES `DoAn` WRITE;
/*!40000 ALTER TABLE `DoAn` DISABLE KEYS */;
/*!40000 ALTER TABLE `DoAn` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `FeeDebt`
--

DROP TABLE IF EXISTS `FeeDebt`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `FeeDebt` (
  `debt_id` int NOT NULL AUTO_INCREMENT,
  `msv` varchar(20) NOT NULL,
  `khoan_thu` varchar(100) NOT NULL,
  `so_tien` decimal(12,2) NOT NULL,
  `han_thu` date NOT NULL,
  `status` enum('chưa đóng','đã đóng') NOT NULL,
  PRIMARY KEY (`debt_id`),
  KEY `fk_feedebt_sv` (`msv`),
  CONSTRAINT `fk_feedebt_sv` FOREIGN KEY (`msv`) REFERENCES `SinhVien` (`msv`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `FeeDebt`
--

LOCK TABLES `FeeDebt` WRITE;
/*!40000 ALTER TABLE `FeeDebt` DISABLE KEYS */;
/*!40000 ALTER TABLE `FeeDebt` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `GiangVien`
--

DROP TABLE IF EXISTS `GiangVien`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `GiangVien` (
  `ma_giangvien` varchar(10) NOT NULL,
  `ho_ten` varchar(100) NOT NULL,
  `ngay_sinh` date NOT NULL,
  `email` varchar(100) NOT NULL,
  `so_dien_thoai` varchar(20) DEFAULT NULL,
  `hoc_vi` enum('Ths','TS','PGS','GS','Khác') DEFAULT NULL,
  PRIMARY KEY (`ma_giangvien`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `GiangVien`
--

LOCK TABLES `GiangVien` WRITE;
/*!40000 ALTER TABLE `GiangVien` DISABLE KEYS */;
INSERT INTO `GiangVien` VALUES ('GV001','Nguyễn Văn An','1982-04-12','an.nguyen@university.edu','0898123456','TS'),('GV002','Trần Thị Mai','1979-07-22','mai.tran@university.edu','0889345671','PGS'),('GV003','Lê Hoàng Nam','1988-11-03','nam.le@university.edu','0879234500','Ths'),('GV004','Phạm Thu Hương','1980-03-14','huong.pham@university.edu','0867892345','GS'),('GV005','Đỗ Quang Huy','1990-01-25','huy.do@university.edu','0834567890','Khác'),('GV006','Vũ Thị Lan','1985-08-09','lan.vu@university.edu','0899988776','PGS'),('GV007','Bùi Mạnh Cường','1975-10-30','cuong.bui@university.edu','0823456123','GS'),('GV008','Hoàng Minh Phúc','1992-06-01','phuc.hoang@university.edu','0856677889','Ths'),('GV009','Ngô Thị Thanh','1983-12-18','thanh.ngo@university.edu','0887766554','TS'),('GV010','Lý Hải Nam','1987-09-05','nam.ly@university.edu','0865432198','PGS');
/*!40000 ALTER TABLE `GiangVien` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `LopHoc`
--

DROP TABLE IF EXISTS `LopHoc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `LopHoc` (
  `ma_lop` varchar(20) NOT NULL,
  `so_luong_sinh_vien` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`ma_lop`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `LopHoc`
--

LOCK TABLES `LopHoc` WRITE;
/*!40000 ALTER TABLE `LopHoc` DISABLE KEYS */;
INSERT INTO `LopHoc` VALUES ('D21VTMD1',0),('D21VTMD2',0);
/*!40000 ALTER TABLE `LopHoc` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `LopHoc_MonHoc`
--

DROP TABLE IF EXISTS `LopHoc_MonHoc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `LopHoc_MonHoc` (
  `ma_lop` varchar(20) NOT NULL,
  `ma_mon` varchar(20) NOT NULL,
  PRIMARY KEY (`ma_lop`,`ma_mon`),
  KEY `fk_lhmh_mon` (`ma_mon`),
  CONSTRAINT `fk_lhmh_lop` FOREIGN KEY (`ma_lop`) REFERENCES `LopHoc` (`ma_lop`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_lhmh_mon` FOREIGN KEY (`ma_mon`) REFERENCES `MonHoc` (`ma_mon`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `LopHoc_MonHoc`
--

LOCK TABLES `LopHoc_MonHoc` WRITE;
/*!40000 ALTER TABLE `LopHoc_MonHoc` DISABLE KEYS */;
INSERT INTO `LopHoc_MonHoc` VALUES ('D21VTMD1','TEL1447'),('D21VTMD2','TEL1447'),('D21VTMD1','TEL1448'),('D21VTMD2','TEL1448'),('D21VTMD1','TEL1450'),('D21VTMD2','TEL1450'),('D21VTMD1','TEL1455'),('D21VTMD2','TEL1455'),('D21VTMD1','TEL1456'),('D21VTMD2','TEL1456'),('D21VTMD1','TEL1458'),('D21VTMD2','TEL1458'),('D21VTMD1','TEL1459'),('D21VTMD2','TEL1459');
/*!40000 ALTER TABLE `LopHoc_MonHoc` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MonHoc`
--

DROP TABLE IF EXISTS `MonHoc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `MonHoc` (
  `ma_mon` varchar(20) NOT NULL,
  `ten_mon` varchar(100) NOT NULL,
  `so_tin_chi` tinyint NOT NULL,
  PRIMARY KEY (`ma_mon`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MonHoc`
--

LOCK TABLES `MonHoc` WRITE;
/*!40000 ALTER TABLE `MonHoc` DISABLE KEYS */;
INSERT INTO `MonHoc` VALUES ('TEL1447','Điện toán đám mây',2),('TEL1448','Lập trình hướng đối tượng',3),('TEL1450','SDN & NFV',2),('TEL1455','Quản trị mạng',2),('TEL1456','Mạng truyền thông vô tuyến',3),('TEL1458','Mạng cảm biến không dây',3),('TEL1459','Thiết kế và hiệu năng mạng',3);
/*!40000 ALTER TABLE `MonHoc` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `RetakeRequest`
--

DROP TABLE IF EXISTS `RetakeRequest`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `RetakeRequest` (
  `request_id` int NOT NULL AUTO_INCREMENT,
  `msv` varchar(20) NOT NULL,
  `ma_lop` varchar(20) NOT NULL,
  `ngay_dang_ki` date NOT NULL,
  `status` enum('mới','duyệt','từ chối','chấp nhận') NOT NULL DEFAULT 'mới',
  PRIMARY KEY (`request_id`),
  KEY `fk_re_sv` (`msv`),
  KEY `fk_re_lh` (`ma_lop`),
  CONSTRAINT `fk_re_lh` FOREIGN KEY (`ma_lop`) REFERENCES `LopHoc` (`ma_lop`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_re_sv` FOREIGN KEY (`msv`) REFERENCES `SinhVien` (`msv`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `RetakeRequest`
--

LOCK TABLES `RetakeRequest` WRITE;
/*!40000 ALTER TABLE `RetakeRequest` DISABLE KEYS */;
/*!40000 ALTER TABLE `RetakeRequest` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SinhVien`
--

DROP TABLE IF EXISTS `SinhVien`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `SinhVien` (
  `STT` int NOT NULL AUTO_INCREMENT,
  `msv` varchar(20) NOT NULL,
  `ho_ten` varchar(100) NOT NULL,
  `ngay_sinh` date NOT NULL,
  `email` varchar(100) NOT NULL,
  `so_dien_thoai` varchar(20) DEFAULT NULL,
  `ma_lop` varchar(20) NOT NULL,
  PRIMARY KEY (`msv`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `STT` (`STT`),
  KEY `fk_sv_lop` (`ma_lop`),
  CONSTRAINT `fk_sv_lop` FOREIGN KEY (`ma_lop`) REFERENCES `LopHoc` (`ma_lop`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=106 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SinhVien`
--

LOCK TABLES `SinhVien` WRITE;
/*!40000 ALTER TABLE `SinhVien` DISABLE KEYS */;
INSERT INTO `SinhVien` VALUES (79,'B19DCVT192','Hoàng Trung Kiên','2001-03-18','hk.b19dcvt192@stu.ptit.edu.vn','0873763116','D21VTMD1'),(59,'B21DCVT009','Hà Văn Cường','2003-07-28','hc.b21dcvt009@stu.ptit.edu.vn','0803413164','D21VTMD1'),(33,'B21DCVT035','Nguyễn Hoàng Nam','2003-01-25','nn.b21dcvt035@stu.ptit.edu.vn','0806112648','D21VTMD2'),(93,'B21DCVT041','Phạm Sỹ Quý','2003-02-14','pq.b21dcvt041@stu.ptit.edu.vn','0884251354','D21VTMD1'),(52,'B21DCVT049','Đỗ Ngọc An','2003-09-29','đa.b21dcvt049@stu.ptit.edu.vn','0810433218','D21VTMD1'),(4,'B21DCVT053','Phạm Hồng Ân','2003-11-15','pâ.b21dcvt053@stu.ptit.edu.vn','0838930555','D21VTMD2'),(1,'B21DCVT061','Nguyễn Hoàng Anh','2003-10-24','na.b21dcvt061@stu.ptit.edu.vn','0830392137','D21VTMD2'),(53,'B21DCVT065','Nguyễn Tuấn Anh','2003-12-18','na.b21dcvt065@stu.ptit.edu.vn','0819600133','D21VTMD1'),(54,'B21DCVT066','Nguyễn Tuấn Anh','2003-01-22','na.b21dcvt066@stu.ptit.edu.vn','0889083863','D21VTMD1'),(2,'B21DCVT067','Nguyễn Việt Anh','2003-02-24','na.b21dcvt067@stu.ptit.edu.vn','0865821972','D21VTMD2'),(3,'B21DCVT069','Phạm Việt Anh','2003-07-24','pa.b21dcvt069@stu.ptit.edu.vn','0896687577','D21VTMD2'),(55,'B21DCVT073','Chu Xuân Bách','2003-10-21','cb.b21dcvt073@stu.ptit.edu.vn','0879402654','D21VTMD1'),(56,'B21DCVT074','Nguyễn Trần Bách','2003-12-12','nb.b21dcvt074@stu.ptit.edu.vn','0823511615','D21VTMD1'),(5,'B21DCVT083','Nguyễn Minh Chiến','2003-12-12','nc.b21dcvt083@stu.ptit.edu.vn','0808249269','D21VTMD2'),(6,'B21DCVT085','Nguyễn Đức Chính','2003-12-12','nc.b21dcvt085@stu.ptit.edu.vn','0847118013','D21VTMD2'),(57,'B21DCVT086','Nguyễn Mậu Chiến','2003-12-28','nc.b21dcvt086@stu.ptit.edu.vn','0859407816','D21VTMD1'),(58,'B21DCVT089','Nguyễn Thành Công','2003-12-27','nc.b21dcvt089@stu.ptit.edu.vn','0818495931','D21VTMD1'),(60,'B21DCVT090','Nguyễn Phúc Cường','2003-08-09','nc.b21dcvt090@stu.ptit.edu.vn','0875255341','D21VTMD1'),(7,'B21DCVT091','Nguyễn Thị Cúc','2003-03-26','nc.b21dcvt091@stu.ptit.edu.vn','0820407522','D21VTMD2'),(61,'B21DCVT098','Nguyễn Tiến Cường','2003-11-02','nc.b21dcvt098@stu.ptit.edu.vn','0892832764','D21VTMD1'),(8,'B21DCVT099','Nguyễn Viết Cường','2003-11-08','nc.b21dcvt099@stu.ptit.edu.vn','0875868809','D21VTMD2'),(9,'B21DCVT100','Phạm Đình Cường','2003-09-17','pc.b21dcvt100@stu.ptit.edu.vn','0818916348','D21VTMD2'),(10,'B21DCVT101','Phạm Mạnh Cường','2003-04-22','pc.b21dcvt101@stu.ptit.edu.vn','0896769930','D21VTMD2'),(14,'B21DCVT107','Đàm Tiến Đạt','2003-03-15','đđ.b21dcvt107@stu.ptit.edu.vn','0827912560','D21VTMD2'),(15,'B21DCVT109','Đỗ Đức Đạt','2003-08-30','đđ.b21dcvt109@stu.ptit.edu.vn','0897670172','D21VTMD2'),(66,'B21DCVT114','Lê Huy Đạt','2003-07-08','lđ.b21dcvt114@stu.ptit.edu.vn','0871012269','D21VTMD1'),(16,'B21DCVT115','Lê Thành Đạt','2003-10-15','lđ.b21dcvt115@stu.ptit.edu.vn','0800992518','D21VTMD2'),(67,'B21DCVT121','Nguyễn Tiến Đạt','2003-12-13','nđ.b21dcvt121@stu.ptit.edu.vn','0816697848','D21VTMD1'),(68,'B21DCVT122','Nguyễn Tiến Đạt','2003-11-17','nđ.b21dcvt122@stu.ptit.edu.vn','0801845146','D21VTMD1'),(17,'B21DCVT125','Đoàn Văn Điệp','2003-06-21','đđ.b21dcvt125@stu.ptit.edu.vn','0853671097','D21VTMD2'),(69,'B21DCVT130','Đào Minh Đức','2003-01-07','đđ.b21dcvt130@stu.ptit.edu.vn','0827048281','D21VTMD1'),(18,'B21DCVT131','Đoàn Trung Đức','2003-07-11','đđ.b21dcvt131@stu.ptit.edu.vn','0895194264','D21VTMD2'),(19,'B21DCVT133','Ngô Minh Đức','2003-05-29','nđ.b21dcvt133@stu.ptit.edu.vn','0818306753','D21VTMD2'),(62,'B21DCVT145','Nguyễn Trí Dũng','2003-01-24','nd.b21dcvt145@stu.ptit.edu.vn','0883503056','D21VTMD1'),(63,'B21DCVT146','Nguyễn Văn Dũng','2003-01-20','nd.b21dcvt146@stu.ptit.edu.vn','0841395376','D21VTMD1'),(11,'B21DCVT148','Trần Mạnh Dũng','2003-10-15','td.b21dcvt148@stu.ptit.edu.vn','0802489451','D21VTMD2'),(65,'B21DCVT154','Nguyễn Quang Dương','2003-08-30','nd.b21dcvt154@stu.ptit.edu.vn','0896965328','D21VTMD1'),(12,'B21DCVT163','Nguyễn Nhất Duy','2003-08-26','nd.b21dcvt163@stu.ptit.edu.vn','0874466602','D21VTMD2'),(13,'B21DCVT165','Vũ Công Duy','2003-12-22','vd.b21dcvt165@stu.ptit.edu.vn','0823450076','D21VTMD2'),(70,'B21DCVT170','Trương Trường Giang','2003-11-20','tg.b21dcvt170@stu.ptit.edu.vn','0848932528','D21VTMD1'),(20,'B21DCVT171','Lê Văn Giáo','2003-05-29','lg.b21dcvt171@stu.ptit.edu.vn','0875100740','D21VTMD2'),(21,'B21DCVT179','Đào Tiến Hân','2003-09-04','đh.b21dcvt179@stu.ptit.edu.vn','0889933188','D21VTMD2'),(71,'B21DCVT186','Trần Duy Hiệp','2003-04-21','th.b21dcvt186@stu.ptit.edu.vn','0880957015','D21VTMD1'),(22,'B21DCVT189','Đinh Văn Hiếu','2003-04-08','đh.b21dcvt189@stu.ptit.edu.vn','0868412696','D21VTMD2'),(72,'B21DCVT202','Bùi Tiến Hoàng','2003-11-18','bh.b21dcvt202@stu.ptit.edu.vn','0843039117','D21VTMD1'),(73,'B21DCVT209','Trần Huy Hoàng','2003-01-25','th.b21dcvt209@stu.ptit.edu.vn','0818227824','D21VTMD1'),(74,'B21DCVT210','Trần Minh Hoàng','2003-07-12','th.b21dcvt210@stu.ptit.edu.vn','0889638346','D21VTMD1'),(23,'B21DCVT211','Vũ Nguyên Hoàng','2003-09-10','vh.b21dcvt211@stu.ptit.edu.vn','0811611620','D21VTMD2'),(24,'B21DCVT212','Nguyễn Quang Học','2003-08-24','nh.b21dcvt212@stu.ptit.edu.vn','0876607541','D21VTMD2'),(75,'B21DCVT226','Lê Quang Huy','2003-03-31','lh.b21dcvt226@stu.ptit.edu.vn','0857871331','D21VTMD1'),(25,'B21DCVT227','Lương Xuân Huy','2003-10-16','lh.b21dcvt227@stu.ptit.edu.vn','0851150552','D21VTMD2'),(26,'B21DCVT243','Nguyễn Hữu Hồng Khải','2003-08-11','nk.b21dcvt243@stu.ptit.edu.vn','0803519230','D21VTMD2'),(76,'B21DCVT249','Phí Đức Khánh','2003-02-20','pk.b21dcvt249@stu.ptit.edu.vn','0850983930','D21VTMD1'),(77,'B21DCVT250','Trần Nam Khánh','2003-12-13','tk.b21dcvt250@stu.ptit.edu.vn','0810310518','D21VTMD1'),(27,'B21DCVT251','Vũ Văn Khánh','2003-05-04','vk.b21dcvt251@stu.ptit.edu.vn','0831045093','D21VTMD2'),(78,'B21DCVT257','Dương Trung Kiên','2003-01-27','dk.b21dcvt257@stu.ptit.edu.vn','0834738299','D21VTMD1'),(28,'B21DCVT259','Nguyễn Trung Kiên','2003-12-10','nk.b21dcvt259@stu.ptit.edu.vn','0822717542','D21VTMD2'),(80,'B21DCVT265','Bùi Tùng Lâm','2003-09-23','bl.b21dcvt265@stu.ptit.edu.vn','0856670106','D21VTMD1'),(81,'B21DCVT266','Nguyễn Hoàng Lâm','2003-10-09','nl.b21dcvt266@stu.ptit.edu.vn','0851333872','D21VTMD1'),(29,'B21DCVT268','Đặng Ngọc Lân','2003-07-25','đl.b21dcvt268@stu.ptit.edu.vn','0803557448','D21VTMD2'),(30,'B21DCVT276','Trần Võ Hoàng Long','2002-11-16','tl.b21dcvt276@stu.ptit.edu.vn','0852911894','D21VTMD2'),(31,'B21DCVT283','Hoàng Đức Mạnh','2003-12-04','hm.b21dcvt283@stu.ptit.edu.vn','0826223583','D21VTMD2'),(32,'B21DCVT307','Khuất Tiến Nam','2003-07-05','kn.b21dcvt307@stu.ptit.edu.vn','0832456029','D21VTMD2'),(82,'B21DCVT312','Lê Bá Khánh Minh','2003-02-06','lm.b21dcvt312@stu.ptit.edu.vn','0862473178','D21VTMD1'),(83,'B21DCVT313','Nguyễn Thành Nam','2003-07-19','nn.b21dcvt313@stu.ptit.edu.vn','0810801326','D21VTMD1'),(84,'B21DCVT314','Nguyễn Văn Nam','2003-06-20','nn.b21dcvt314@stu.ptit.edu.vn','0877360260','D21VTMD1'),(85,'B21DCVT322','Hoàng Hiếu Nghĩa','2003-07-05','hn.b21dcvt322@stu.ptit.edu.vn','0864746872','D21VTMD1'),(34,'B21DCVT323','Ngô Trung Nghĩa','2003-07-23','nn.b21dcvt323@stu.ptit.edu.vn','0862964513','D21VTMD2'),(35,'B21DCVT324','Ngô Trung Nghĩa','2003-08-21','nn.b21dcvt324@stu.ptit.edu.vn','0875806606','D21VTMD2'),(36,'B21DCVT331','Dương Nguyên Nguyên','2003-09-29','dn.b21dcvt331@stu.ptit.edu.vn','0857354712','D21VTMD2'),(86,'B21DCVT337','Nguyễn Văn Hải Ninh','2003-01-07','nn.b21dcvt337@stu.ptit.edu.vn','0834309805','D21VTMD1'),(37,'B21DCVT339','Hoàng Trần Phong','2003-04-11','hp.b21dcvt339@stu.ptit.edu.vn','0814189276','D21VTMD2'),(87,'B21DCVT345','Vũ Hòa Phong','2003-08-17','vp.b21dcvt345@stu.ptit.edu.vn','0800978820','D21VTMD1'),(88,'B21DCVT346','Nguyễn Hồng Phúc','2003-01-05','np.b21dcvt346@stu.ptit.edu.vn','0881219136','D21VTMD1'),(90,'B21DCVT354','Dương Văn Quân','2003-09-16','dq.b21dcvt354@stu.ptit.edu.vn','0869985435','D21VTMD1'),(91,'B21DCVT361','Vũ Minh Quân','2003-06-12','vq.b21dcvt361@stu.ptit.edu.vn','0834624751','D21VTMD1'),(89,'B21DCVT362','Hà Minh Quang','2003-02-04','hq.b21dcvt362@stu.ptit.edu.vn','0819399091','D21VTMD1'),(92,'B21DCVT369','Lê Ngọc Quý','2003-06-14','lq.b21dcvt369@stu.ptit.edu.vn','0807991183','D21VTMD1'),(94,'B21DCVT370','Nguyễn Thị Thu Quyên','2003-09-27','nq.b21dcvt370@stu.ptit.edu.vn','0827849808','D21VTMD1'),(38,'B21DCVT378','Vũ Văn Sĩ','2003-07-01','vs.b21dcvt378@stu.ptit.edu.vn','0826623758','D21VTMD2'),(39,'B21DCVT379','Bùi Xuân Sơn','2003-02-07','bs.b21dcvt379@stu.ptit.edu.vn','0825717340','D21VTMD2'),(40,'B21DCVT380','Hoàng Tiến Sơn','2003-12-31','hs.b21dcvt380@stu.ptit.edu.vn','0879703414','D21VTMD2'),(41,'B21DCVT387','Bùi Quyết Thắng','2003-10-01','bt.b21dcvt387@stu.ptit.edu.vn','0889267173','D21VTMD2'),(97,'B21DCVT393','Phạm Võ Anh Thắng','2003-09-23','pt.b21dcvt393@stu.ptit.edu.vn','0874016400','D21VTMD1'),(42,'B21DCVT394','Sái Văn Thắng','2003-02-06','st.b21dcvt394@stu.ptit.edu.vn','0886401400','D21VTMD2'),(43,'B21DCVT395','Trần Đức Thắng','2003-03-01','tt.b21dcvt395@stu.ptit.edu.vn','0846896714','D21VTMD2'),(95,'B21DCVT401','Lê Xuân Thành','2003-12-08','lt.b21dcvt401@stu.ptit.edu.vn','0841241182','D21VTMD1'),(96,'B21DCVT409','Nguyễn Phương Thảo','2003-05-07','nt.b21dcvt409@stu.ptit.edu.vn','0844935348','D21VTMD1'),(44,'B21DCVT410','Nguyễn Quang Thế','2003-10-06','nt.b21dcvt410@stu.ptit.edu.vn','0854391014','D21VTMD2'),(45,'B21DCVT411','Vương Quốc Thiện','2003-02-09','vt.b21dcvt411@stu.ptit.edu.vn','0848518321','D21VTMD2'),(98,'B21DCVT417','Phan Bá Thục','2003-10-10','pt.b21dcvt417@stu.ptit.edu.vn','0852427868','D21VTMD1'),(99,'B21DCVT425','Nguyễn Văn Trọng','2003-08-08','nt.b21dcvt425@stu.ptit.edu.vn','0801128059','D21VTMD1'),(100,'B21DCVT433','Kiều Anh Trường','2003-02-18','kt.b21dcvt433@stu.ptit.edu.vn','0882620450','D21VTMD1'),(46,'B21DCVT434','Lê Văn Trường','2003-10-04','lt.b21dcvt434@stu.ptit.edu.vn','0864482983','D21VTMD2'),(47,'B21DCVT436','Nguyễn Nam Trường','2003-02-28','nt.b21dcvt436@stu.ptit.edu.vn','0881686447','D21VTMD2'),(101,'B21DCVT441','Lê Thanh Tú','2003-10-06','lt.b21dcvt441@stu.ptit.edu.vn','0853315869','D21VTMD1'),(103,'B21DCVT449','Trần Duy Tuấn','2003-11-08','tt.b21dcvt449@stu.ptit.edu.vn','0863421607','D21VTMD1'),(48,'B21DCVT451','Đỗ Mạnh Tùng','2003-04-14','đt.b21dcvt451@stu.ptit.edu.vn','0859221166','D21VTMD2'),(49,'B21DCVT459','Đỗ Quốc Việt','2003-06-20','đv.b21dcvt459@stu.ptit.edu.vn','0897284576','D21VTMD2'),(50,'B21DCVT460','Hoàng Quốc Việt','2003-10-16','hv.b21dcvt460@stu.ptit.edu.vn','0837785707','D21VTMD2'),(104,'B21DCVT464','Nguyễn Đức Văn','2003-12-13','nv.b21dcvt464@stu.ptit.edu.vn','0833754330','D21VTMD1'),(105,'B21DCVT465','Nguyễn Thế Vinh','2003-03-17','nv.b21dcvt465@stu.ptit.edu.vn','0836541458','D21VTMD1'),(51,'B21DCVT468','Trần Long Vũ','2003-12-14','tv.b21dcvt468@stu.ptit.edu.vn','0842709305','D21VTMD2'),(102,'B21DCVT473','Lương Anh Tú','2003-03-13','lt.b21dcvt473@stu.ptit.edu.vn','0823226025','D21VTMD1'),(64,'N21DCVT017B','Nguyễn Khánh Duy','2003-02-24','nd.n21dcvt017b@stu.ptit.edu.vn','0872423884','D21VTMD1');
/*!40000 ALTER TABLE `SinhVien` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `User`
--

DROP TABLE IF EXISTS `User`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `User` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password_hash` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `role` enum('sinhvien','admin','giangvien') COLLATE utf8mb4_unicode_ci NOT NULL,
  `last_login` datetime DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=117 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `User`
--

LOCK TABLES `User` WRITE;
/*!40000 ALTER TABLE `User` DISABLE KEYS */;
INSERT INTO `User` VALUES (1,'admin','8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918','admin','2025-04-21 08:54:53'),(2,'B21DCVT061','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(3,'B21DCVT067','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(4,'B21DCVT069','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(5,'B21DCVT053','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(6,'B21DCVT083','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(7,'B21DCVT085','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(8,'B21DCVT091','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(9,'B21DCVT099','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(10,'B21DCVT100','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(11,'B21DCVT101','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(12,'B21DCVT148','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(13,'B21DCVT163','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(14,'B21DCVT165','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(15,'B21DCVT107','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(16,'B21DCVT109','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(17,'B21DCVT115','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(18,'B21DCVT125','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(19,'B21DCVT131','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(20,'B21DCVT133','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(21,'B21DCVT171','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(22,'B21DCVT179','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(23,'B21DCVT189','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(24,'B21DCVT211','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(25,'B21DCVT212','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(26,'B21DCVT227','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(27,'B21DCVT243','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(28,'B21DCVT251','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(29,'B21DCVT259','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(30,'B21DCVT268','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(31,'B21DCVT276','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(32,'B21DCVT283','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(33,'B21DCVT307','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(34,'B21DCVT035','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(35,'B21DCVT323','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(36,'B21DCVT324','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(37,'B21DCVT331','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(38,'B21DCVT339','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(39,'B21DCVT378','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien','2025-04-20 22:10:43'),(40,'B21DCVT379','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(41,'B21DCVT380','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(42,'B21DCVT387','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(43,'B21DCVT394','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(44,'B21DCVT395','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(45,'B21DCVT410','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(46,'B21DCVT411','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(47,'B21DCVT434','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(48,'B21DCVT436','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(49,'B21DCVT451','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(50,'B21DCVT459','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(51,'B21DCVT460','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(52,'B21DCVT468','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(53,'B21DCVT049','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(54,'B21DCVT065','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(55,'B21DCVT066','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(56,'B21DCVT073','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(57,'B21DCVT074','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(58,'B21DCVT086','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(59,'B21DCVT089','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(60,'B21DCVT009','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(61,'B21DCVT090','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(62,'B21DCVT098','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(63,'B21DCVT145','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(64,'B21DCVT146','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(65,'N21DCVT017B','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(66,'B21DCVT154','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(67,'B21DCVT114','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(68,'B21DCVT121','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(69,'B21DCVT122','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(70,'B21DCVT130','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(71,'B21DCVT170','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(72,'B21DCVT186','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(73,'B21DCVT202','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(74,'B21DCVT209','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(75,'B21DCVT210','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(76,'B21DCVT226','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(77,'B21DCVT249','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(78,'B21DCVT250','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(79,'B21DCVT257','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(80,'B19DCVT192','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(81,'B21DCVT265','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(82,'B21DCVT266','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(83,'B21DCVT312','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(84,'B21DCVT313','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(85,'B21DCVT314','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(86,'B21DCVT322','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(87,'B21DCVT337','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(88,'B21DCVT345','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(89,'B21DCVT346','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(90,'B21DCVT362','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(91,'B21DCVT354','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(92,'B21DCVT361','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(93,'B21DCVT369','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(94,'B21DCVT041','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(95,'B21DCVT370','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(96,'B21DCVT401','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(97,'B21DCVT409','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(98,'B21DCVT393','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(99,'B21DCVT417','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(100,'B21DCVT425','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(101,'B21DCVT433','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(102,'B21DCVT441','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(103,'B21DCVT473','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(104,'B21DCVT449','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(105,'B21DCVT464','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(106,'B21DCVT465','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','sinhvien',NULL),(107,'GV001','472bbe83616e93d3c09a79103ae47d8f71e3d35a966d6e8b22f743218d04171d','giangvien','2025-04-20 22:23:08'),(108,'GV002','472bbe83616e93d3c09a79103ae47d8f71e3d35a966d6e8b22f743218d04171d','giangvien',NULL),(109,'GV003','472bbe83616e93d3c09a79103ae47d8f71e3d35a966d6e8b22f743218d04171d','giangvien',NULL),(110,'GV004','472bbe83616e93d3c09a79103ae47d8f71e3d35a966d6e8b22f743218d04171d','giangvien',NULL),(111,'GV005','472bbe83616e93d3c09a79103ae47d8f71e3d35a966d6e8b22f743218d04171d','giangvien',NULL),(112,'GV006','472bbe83616e93d3c09a79103ae47d8f71e3d35a966d6e8b22f743218d04171d','giangvien',NULL),(113,'GV007','472bbe83616e93d3c09a79103ae47d8f71e3d35a966d6e8b22f743218d04171d','giangvien',NULL),(114,'GV008','472bbe83616e93d3c09a79103ae47d8f71e3d35a966d6e8b22f743218d04171d','giangvien',NULL),(115,'GV009','472bbe83616e93d3c09a79103ae47d8f71e3d35a966d6e8b22f743218d04171d','giangvien',NULL),(116,'GV010','472bbe83616e93d3c09a79103ae47d8f71e3d35a966d6e8b22f743218d04171d','giangvien',NULL);
/*!40000 ALTER TABLE `User` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-04-22 12:18:49
