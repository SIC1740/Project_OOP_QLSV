package com.myuniv.sm.view.teacher;

import com.myuniv.sm.model.Lecturer;
import com.myuniv.sm.service.LecturerService;
import com.myuniv.sm.service.ServiceException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;

/**
 * Panel to display detailed lecturer information
 */
public class LecturerInfoPanel extends JPanel {
    
    private final Lecturer lecturer;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Constructor with lecturer data
     * @param lecturer The lecturer whose information to display
     */
    public LecturerInfoPanel(Lecturer lecturer) {
        this.lecturer = lecturer;
        initUI();
    }
    
    /**
     * Constructor that loads lecturer data by ID
     * @param lecturerId The lecturer ID to look up
     */
    public LecturerInfoPanel(String lecturerId) throws ServiceException {
        LecturerService service = new LecturerService();
        this.lecturer = service.getLecturerById(lecturerId);
        initUI();
    }
    
    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Create info panel
        JPanel infoPanel = createInfoPanel();
        add(infoPanel, BorderLayout.CENTER);
    }
    
    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), 
            "Thông tin chi tiết giảng viên", 
            TitledBorder.LEFT, 
            TitledBorder.TOP));
        
        // Add lecturer information fields with labels
        addField(panel, "Mã giảng viên:", lecturer != null ? lecturer.getMaGiangVien() : "N/A");
        addField(panel, "Họ và tên:", lecturer != null ? lecturer.getHoTen() : "N/A");
        addField(panel, "Ngày sinh:", lecturer != null && lecturer.getNgaySinh() != null ? 
                lecturer.getNgaySinh().format(DATE_FORMATTER) : "N/A");
        addField(panel, "Email:", lecturer != null ? lecturer.getEmail() : "N/A");
        addField(panel, "Số điện thoại:", lecturer != null ? lecturer.getSoDienThoai() : "N/A");
        addField(panel, "Học vị:", lecturer != null ? lecturer.getHocVi() : "N/A");
        
        return panel;
    }
    
    private void addField(JPanel panel, String labelText, String value) {
        panel.add(new JLabel(labelText, SwingConstants.RIGHT));
        panel.add(new JLabel(value));
    }
} 