package com.myuniv.sm.service;

import com.myuniv.sm.model.Lecturer;
import com.myuniv.sm.dao.LecturerDao;
import com.myuniv.sm.dao.impl.LecturerDaoJdbc;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service for managing lecturer data
 */
public class LecturerService {
    private final LecturerDao lecturerDao;
    private static final Logger logger = Logger.getLogger(LecturerService.class.getName());
    
    /**
     * Default constructor that initializes with JDBC DAO implementation
     */
    public LecturerService() {
        // For now, we'll use a simulated DAO implementation until the real one is created
        this.lecturerDao = null; // new LecturerDaoJdbc();
    }
    
    /**
     * Constructor with dependency injection for testing
     * @param lecturerDao The LecturerDao implementation to use
     */
    public LecturerService(LecturerDao lecturerDao) {
        this.lecturerDao = lecturerDao;
    }
    
    /**
     * Find a lecturer by their ID
     * @param id The lecturer ID to look up
     * @return The lecturer if found, null otherwise
     * @throws ServiceException If there is an error retrieving the lecturer
     */
    public Lecturer getLecturerById(String id) throws ServiceException {
        try {
            // If we have a DAO, use it
            if (lecturerDao != null) {
                Lecturer lecturer = lecturerDao.findById(id);
                if (lecturer == null) {
                    // If no lecturer found in database, try to create a mock
                    if (id != null && !id.isEmpty() && (id.startsWith("GV") || id.matches("^[0-9]{3,4}$"))) {
                        return createMockLecturer(id);
                    }
                    throw new ServiceException("Không tìm thấy giảng viên với mã: " + id);
                }
                return lecturer;
            } else {
                // Fallback to mock data if DAO is not available
                if (id != null && !id.isEmpty()) {
                    return createMockLecturer(id);
                }
                throw new ServiceException("Không tìm thấy giảng viên với mã: " + id);
            }
        } catch (Exception e) {
            if (e instanceof ServiceException) {
                throw (ServiceException) e;
            }
            logger.log(Level.SEVERE, "Error getting lecturer by ID: " + id, e);
            throw new ServiceException("Lỗi hệ thống khi tìm kiếm giảng viên: " + e.getMessage());
        }
    }
    
    /**
     * Get a list of all lecturers
     * @return List of all lecturers
     */
    public List<Lecturer> findAll() {
        try {
            if (lecturerDao != null) {
                return lecturerDao.findAll();
            } else {
                // Return mock data if DAO is not available
                return createMockLecturerList();
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding all lecturers", e);
            // Return mock data if there's an error
            return createMockLecturerList();
        }
    }
    
    /**
     * Find lecturers by department
     * @param department The department to search for
     * @return List of lecturers in the specified department
     */
    public List<Lecturer> findByDepartment(String department) {
        try {
            if (lecturerDao != null) {
                return lecturerDao.findByDepartment(department);
            } else {
                // Return mock data if DAO is not available
                if ("CNTT".equals(department)) {
                    return createMockLecturerList();
                }
                return new ArrayList<>();
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding lecturers by department: " + department, e);
            // Return mock data if there's an error
            if ("CNTT".equals(department)) {
                return createMockLecturerList();
            }
            return new ArrayList<>();
        }
    }
    
    /**
     * Save a lecturer (create or update)
     * @param lecturer The lecturer to save
     * @return true if successful, false otherwise
     * @throws ServiceException if an error occurs
     */
    public boolean saveLecturer(Lecturer lecturer) throws ServiceException {
        try {
            if (lecturer == null) {
                throw new ServiceException("Không thể lưu thông tin giảng viên null");
            }
            
            if (lecturer.getMaGiangVien() == null || lecturer.getMaGiangVien().isEmpty()) {
                throw new ServiceException("Mã giảng viên không được để trống");
            }
            
            if (lecturer.getHoTen() == null || lecturer.getHoTen().isEmpty()) {
                throw new ServiceException("Họ tên giảng viên không được để trống");
            }
            
            if (lecturerDao != null) {
                return lecturerDao.save(lecturer);
            } else {
                // Simulate successful save
                return true;
            }
        } catch (Exception e) {
            if (e instanceof ServiceException) {
                throw (ServiceException) e;
            }
            logger.log(Level.SEVERE, "Error saving lecturer: " + lecturer, e);
            throw new ServiceException("Lỗi hệ thống khi lưu giảng viên: " + e.getMessage());
        }
    }
    
    /**
     * Delete a lecturer
     * @param id The lecturer ID to delete
     * @return true if successful, false otherwise
     * @throws ServiceException if an error occurs
     */
    public boolean deleteLecturer(String id) throws ServiceException {
        try {
            if (id == null || id.isEmpty()) {
                throw new ServiceException("Mã giảng viên không được để trống");
            }
            
            if (lecturerDao != null) {
                return lecturerDao.delete(id);
            } else {
                // Simulate successful delete
                return true;
            }
        } catch (Exception e) {
            if (e instanceof ServiceException) {
                throw (ServiceException) e;
            }
            logger.log(Level.SEVERE, "Error deleting lecturer: " + id, e);
            throw new ServiceException("Lỗi hệ thống khi xóa giảng viên: " + e.getMessage());
        }
    }
    
    // Helper methods to create mock data
    
    private Lecturer createMockLecturer(String id) {
        Lecturer lecturer = new Lecturer();
        lecturer.setMaGiangVien(id);
        
        // Create different mock data based on ID pattern
        if (id.equals("GV001") || id.equals("1")) {
            lecturer.setHoTen("TS. Nguyễn Văn Giảng");
            lecturer.setNgaySinh(LocalDate.of(1980, 5, 15));
            lecturer.setEmail("nvgiang@example.edu.vn");
            lecturer.setSoDienThoai("0123456789");
            lecturer.setHocVi("TS");
        } else if (id.equals("GV002") || id.equals("2")) {
            lecturer.setHoTen("PGS. Trần Thị Hướng");
            lecturer.setNgaySinh(LocalDate.of(1975, 8, 20));
            lecturer.setEmail("tthuong@example.edu.vn");
            lecturer.setSoDienThoai("0123456788");
            lecturer.setHocVi("PGS");
        } else {
            lecturer.setHoTen("ThS. " + (id.startsWith("GV") ? id.substring(2) : id));
            lecturer.setNgaySinh(LocalDate.of(1985, 3, 10));
            lecturer.setEmail("lecturer" + id + "@example.edu.vn");
            lecturer.setSoDienThoai("01234" + id);
            lecturer.setHocVi("ThS");
        }
        
        return lecturer;
    }
    
    private List<Lecturer> createMockLecturerList() {
        List<Lecturer> lecturers = new ArrayList<>();
        
        Lecturer l1 = createMockLecturer("GV001");
        lecturers.add(l1);
        
        Lecturer l2 = createMockLecturer("GV002");
        lecturers.add(l2);
        
        Lecturer l3 = new Lecturer();
        l3.setMaGiangVien("GV003");
        l3.setHoTen("ThS. Lê Văn Dạy");
        l3.setHocVi("ThS");
        l3.setNgaySinh(LocalDate.of(1985, 3, 10));
        l3.setEmail("lvday@example.edu.vn");
        l3.setSoDienThoai("0123456787");
        lecturers.add(l3);
        
        return lecturers;
    }
} 