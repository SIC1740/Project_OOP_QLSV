package com.myuniv.sm.service;

import com.myuniv.sm.model.Student;
import com.myuniv.sm.dao.StudentDao;
import com.myuniv.sm.dao.impl.StudentDaoJdbc;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service for managing student data
 */
public class StudentService {
    private final StudentDao studentDao;
    private static final Logger logger = Logger.getLogger(StudentService.class.getName());
    
    /**
     * Default constructor uses JDBC implementation of StudentDao
     */
    public StudentService() {
        this.studentDao = new StudentDaoJdbc();
    }
    
    /**
     * Constructor with dependency injection for testing
     * @param studentDao The StudentDao implementation to use
     */
    public StudentService(StudentDao studentDao) {
        this.studentDao = studentDao;
    }
    
    /**
     * Find a student by their student ID (MSV)
     * @param msv The student ID to look up
     * @return The student if found, null otherwise
     */
    public Student findByMsv(String msv) {
        try {
            return studentDao.findByMsv(msv);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding student by MSV: " + msv, e);
            return null;
        }
    }
    
    /**
     * Get a student by their student ID (MSV) - alias for findByMsv for consistency
     * with other service classes
     * @param msv The student ID to look up
     * @return The student if found, null otherwise
     * @throws ServiceException if an error occurs
     */
    public Student getStudentByMsv(String msv) throws ServiceException {
        try {
            Student student = studentDao.findByMsv(msv);
            if (student == null) {
                if (msv.equals("admin") || msv.equals("giangvien")) {
                    throw new ServiceException("Tài khoản này không có quyền truy cập vào giao diện sinh viên");
                }

                logger.log(Level.WARNING, "Không tìm thấy sinh viên " + msv + " trong database!");
                throw new ServiceException("Không tìm thấy sinh viên với mã: " + msv);
            }
            return student;
        } catch (Exception e) {
            if (e instanceof ServiceException) {
                throw (ServiceException) e;
            }
            logger.log(Level.SEVERE, "Error getting student by MSV: " + msv, e);
            throw new ServiceException("Lỗi hệ thống khi tìm kiếm sinh viên: " + e.getMessage());
        }
    }
    
    /**
     * Get a list of all students
     * @return List of all students
     */
    public List<Student> findAll() {
        try {
            return studentDao.findAll();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding all students", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Find students by class ID
     * @param maLop The class ID to search for
     * @return List of students in the specified class
     */
    public List<Student> findByClass(String maLop) {
        try {
            return studentDao.findByClass(maLop);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding students by class: " + maLop, e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Save a student (create or update)
     * @param student The student to save
     * @return true if successful, false otherwise
     * @throws ServiceException if an error occurs
     */
    public boolean saveStudent(Student student) throws ServiceException {
        try {
            if (student == null) {
                throw new ServiceException("Không thể lưu thông tin sinh viên null");
            }
            
            if (student.getMsv() == null || student.getMsv().isEmpty()) {
                throw new ServiceException("Mã sinh viên không được để trống");
            }
            
            if (student.getHoTen() == null || student.getHoTen().isEmpty()) {
                throw new ServiceException("Họ tên sinh viên không được để trống");
            }
            
            return studentDao.save(student);
        } catch (Exception e) {
            if (e instanceof ServiceException) {
                throw (ServiceException) e;
            }
            logger.log(Level.SEVERE, "Error saving student: " + student, e);
            throw new ServiceException("Lỗi hệ thống khi lưu sinh viên: " + e.getMessage());
        }
    }
    
    /**
     * Delete a student
     * @param msv The student ID to delete
     * @return true if successful, false otherwise
     * @throws ServiceException if an error occurs
     */
    public boolean deleteStudent(String msv) throws ServiceException {
        try {
            if (msv == null || msv.isEmpty()) {
                throw new ServiceException("Mã sinh viên không được để trống");
            }
            
            return studentDao.delete(msv);
        } catch (Exception e) {
            if (e instanceof ServiceException) {
                throw (ServiceException) e;
            }
            logger.log(Level.SEVERE, "Error deleting student: " + msv, e);
            throw new ServiceException("Lỗi hệ thống khi xóa sinh viên: " + e.getMessage());
        }
    }
} 