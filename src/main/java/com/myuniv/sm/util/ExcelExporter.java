package com.myuniv.sm.util;

import com.myuniv.sm.model.Lecturer;
import org.apache.poi.ss.usermodel.*;
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
} 