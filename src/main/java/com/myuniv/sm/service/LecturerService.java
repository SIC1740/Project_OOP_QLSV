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
        this.lecturerDao = new LecturerDaoJdbc();
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
     * @return The lecturer if found
     * @throws ServiceException If there is an error retrieving the lecturer
     */
    public Lecturer getLecturerById(String id) throws ServiceException {
        try {
            Lecturer lecturer = lecturerDao.findById(id);
            if (lecturer == null) {
                throw new ServiceException("Không tìm thấy giảng viên với mã: " + id);
            }
            return lecturer;
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
     * @throws ServiceException If there is an error retrieving lecturers
     */
    public List<Lecturer> findAll() throws ServiceException {
        try {
            return lecturerDao.findAll();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding all lecturers", e);
            throw new ServiceException("Lỗi khi lấy danh sách giảng viên: " + e.getMessage());
        }
    }
    
    /**
     * Find lecturers by department
     * @param department The department to search for
     * @return List of lecturers in the specified department
     * @throws ServiceException If there is an error retrieving lecturers
     */
    public List<Lecturer> findByDepartment(String department) throws ServiceException {
        try {
            return lecturerDao.findByDepartment(department);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding lecturers by department: " + department, e);
            throw new ServiceException("Lỗi khi tìm giảng viên theo bộ môn: " + e.getMessage());
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
            
            return lecturerDao.save(lecturer);
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
            
            return lecturerDao.delete(id);
        } catch (Exception e) {
            if (e instanceof ServiceException) {
                throw (ServiceException) e;
            }
            logger.log(Level.SEVERE, "Error deleting lecturer: " + id, e);
            throw new ServiceException("Lỗi hệ thống khi xóa giảng viên: " + e.getMessage());
        }
    }
    
    /**
     * Add a new lecturer
     * @param lecturer The lecturer to add
     * @return true if successful, false otherwise 
     * @throws ServiceException if an error occurs
     */
    public boolean addLecturer(Lecturer lecturer) throws ServiceException {
        try {
            if (lecturer == null) {
                throw new ServiceException("Không thể thêm giảng viên null");
            }
            
            validateLecturer(lecturer);
            
            return lecturerDao.add(lecturer);
        } catch (Exception e) {
            if (e instanceof ServiceException) {
                throw (ServiceException) e;
            }
            logger.log(Level.SEVERE, "Error adding lecturer", e);
            throw new ServiceException("Lỗi khi thêm giảng viên: " + e.getMessage());
        }
    }
    
    /**
     * Update an existing lecturer
     * @param lecturer The lecturer to update
     * @return true if successful, false otherwise
     * @throws ServiceException if an error occurs
     */
    public boolean updateLecturer(Lecturer lecturer) throws ServiceException {
        try {
            if (lecturer == null) {
                throw new ServiceException("Không thể cập nhật giảng viên null");
            }
            
            validateLecturer(lecturer);
            
            // Check if the lecturer exists
            Lecturer existingLecturer = lecturerDao.findById(lecturer.getMaGiangVien());
            if (existingLecturer == null) {
                throw new ServiceException("Không tìm thấy giảng viên để cập nhật");
            }
            
            return lecturerDao.update(lecturer);
        } catch (Exception e) {
            if (e instanceof ServiceException) {
                throw (ServiceException) e;
            }
            logger.log(Level.SEVERE, "Error updating lecturer", e);
            throw new ServiceException("Lỗi khi cập nhật giảng viên: " + e.getMessage());
        }
    }
    
    /**
     * Validate lecturer data
     * @param lecturer The lecturer to validate
     * @throws ServiceException if validation fails
     */
    private void validateLecturer(Lecturer lecturer) throws ServiceException {
        if (lecturer.getMaGiangVien() == null || lecturer.getMaGiangVien().isEmpty()) {
            throw new ServiceException("Mã giảng viên không được để trống");
        }
        
        if (lecturer.getHoTen() == null || lecturer.getHoTen().isEmpty()) {
            throw new ServiceException("Họ tên giảng viên không được để trống");
        }
        
        if (lecturer.getNgaySinh() == null) {
            throw new ServiceException("Ngày sinh không được để trống");
        }
        
        if (lecturer.getEmail() == null || lecturer.getEmail().isEmpty()) {
            throw new ServiceException("Email không được để trống");
        }
        
        // Validate học vị is one of the enum values
        String hocVi = lecturer.getHocVi();
        if (hocVi != null && !hocVi.isEmpty() && 
            !hocVi.equals("Ths") && !hocVi.equals("TS") && 
            !hocVi.equals("PGS") && !hocVi.equals("GS") && 
            !hocVi.equals("Khác")) {
            throw new ServiceException("Học vị không hợp lệ. Giá trị hợp lệ: Ths, TS, PGS, GS, Khác");
        }
    }
} 