package com.myuniv.sm.util;

import com.myuniv.sm.model.Lecturer;
import com.myuniv.sm.model.Student;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExcelExporter {
    
    public static void exportLecturersToExcel(List<Lecturer> lecturers, String filePath) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Giảng Viên");
            
            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Mã Giảng Viên");
            headerRow.createCell(1).setCellValue("Họ Tên");
            headerRow.createCell(2).setCellValue("Ngày Sinh");
            headerRow.createCell(3).setCellValue("Email");
            headerRow.createCell(4).setCellValue("Số Điện Thoại");
            headerRow.createCell(5).setCellValue("Học Vị");
            
            // Style for header
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            
            for (int i = 0; i < 6; i++) {
                headerRow.getCell(i).setCellStyle(headerStyle);
            }
            
            // Fill data rows
            int rowNum = 1;
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            
            for (Lecturer lecturer : lecturers) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(lecturer.getMaGiangVien());
                row.createCell(1).setCellValue(lecturer.getHoTen());
                
                Cell dateCell = row.createCell(2);
                if (lecturer.getNgaySinh() != null) {
                    dateCell.setCellValue(lecturer.getNgaySinh().format(dateFormatter));
                }
                
                row.createCell(3).setCellValue(lecturer.getEmail());
                row.createCell(4).setCellValue(lecturer.getSoDienThoai());
                row.createCell(5).setCellValue(lecturer.getHocVi());
            }
            
            // Auto-size columns
            for (int i = 0; i < 6; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Write to file
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }
        }
    }
    
    public static void exportStudentsToExcel(List<Student> students, String filePath) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sinh Viên");
            
            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("MSV");
            headerRow.createCell(1).setCellValue("Họ Tên");
            headerRow.createCell(2).setCellValue("Ngày Sinh");
            headerRow.createCell(3).setCellValue("Email");
            headerRow.createCell(4).setCellValue("Số Điện Thoại");
            headerRow.createCell(5).setCellValue("Lớp");
            
            // Style for header
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            
            for (int i = 0; i < 6; i++) {
                headerRow.getCell(i).setCellStyle(headerStyle);
            }
            
            // Fill data rows
            int rowNum = 1;
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            
            for (Student student : students) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(student.getMsv());
                row.createCell(1).setCellValue(student.getHoTen());
                
                Cell dateCell = row.createCell(2);
                if (student.getNgaySinh() != null) {
                    dateCell.setCellValue(student.getNgaySinh().format(dateFormatter));
                }
                
                row.createCell(3).setCellValue(student.getEmail());
                row.createCell(4).setCellValue(student.getSoDienThoai());
                row.createCell(5).setCellValue(student.getTenLop() != null ? student.getTenLop() : student.getMaLop());
            }
            
            // Auto-size columns
            for (int i = 0; i < 6; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Write to file
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }
        }
    }
    
    public static void exportClassStudentsToExcel(String className, List<Student> students, String filePath) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sinh Viên Lớp " + className);
            
            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("STT");
            headerRow.createCell(1).setCellValue("MSV");
            headerRow.createCell(2).setCellValue("Họ Tên");
            headerRow.createCell(3).setCellValue("Email");
            headerRow.createCell(4).setCellValue("Số Điện Thoại");
            
            // Style for header
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            
            for (int i = 0; i < 5; i++) {
                headerRow.getCell(i).setCellStyle(headerStyle);
            }
            
            // Title row for class
            Row titleRow = sheet.createRow(1);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Danh sách sinh viên lớp: " + className);
            
            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 14);
            titleStyle.setFont(titleFont);
            titleCell.setCellStyle(titleStyle);
            
            // Merge cells for title
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 4));
            
            // Add empty row after title
            sheet.createRow(2);
            
            // Fill data rows
            int rowNum = 3;
            int stt = 1;
            
            for (Student student : students) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(stt++);
                row.createCell(1).setCellValue(student.getMsv());
                row.createCell(2).setCellValue(student.getHoTen());
                row.createCell(3).setCellValue(student.getEmail());
                row.createCell(4).setCellValue(student.getSoDienThoai());
            }
            
            // Auto-size columns
            for (int i = 0; i < 5; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Write to file
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }
        }
    }
} 