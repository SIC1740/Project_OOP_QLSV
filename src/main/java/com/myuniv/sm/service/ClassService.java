package com.myuniv.sm.service;

import com.myuniv.sm.model.Class;
import com.myuniv.sm.dao.ClassDao;
import com.myuniv.sm.dao.impl.ClassDaoJdbc;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service for managing class data
 */
public class ClassService {
    private final ClassDao classDao;
    private static final Logger logger = Logger.getLogger(ClassService.class.getName());
    
    /**
     * Default constructor uses JDBC implementation of ClassDao
     */
    public ClassService() {
        this.classDao = new ClassDaoJdbc();
    }
    
    /**
     * Constructor with dependency injection for testing
     * @param classDao The ClassDao implementation to use
     */
    public ClassService(ClassDao classDao) {
        this.classDao = classDao;
    }
    
    /**
     * Find a class by its ID
     * @param maLop The class ID to look up
     * @return The class if found, null otherwise
     */
    public Class findByMaLop(String maLop) {
        try {
            return classDao.findByMaLop(maLop);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding class by ID: " + maLop, e);
            return null;
        }
    }
    
    /**
     * Get a class by its ID with error handling
     * @param maLop The class ID to look up
     * @return The class object
     * @throws ServiceException if an error occurs
     */
    public Class getClassByMaLop(String maLop) throws ServiceException {
        try {
            Class classObj = classDao.findByMaLop(maLop);
            if (classObj == null) {
                logger.log(Level.WARNING, "Không tìm thấy lớp với mã: " + maLop);
                throw new ServiceException("Không tìm thấy lớp với mã: " + maLop);
            }
            return classObj;
        } catch (Exception e) {
            if (e instanceof ServiceException) {
                throw (ServiceException) e;
            }
            logger.log(Level.SEVERE, "Error getting class by ID: " + maLop, e);
            throw new ServiceException("Lỗi hệ thống khi tìm kiếm lớp: " + e.getMessage());
        }
    }
    
    /**
     * Get a list of all classes
     * @return List of all classes
     */
    public List<Class> findAll() {
        try {
            return classDao.findAll();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding all classes", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Save a class (create or update)
     * @param classObj The class to save
     * @return true if successful, false otherwise
     * @throws ServiceException if an error occurs
     */
    public boolean saveClass(Class classObj) throws ServiceException {
        try {
            if (classObj == null) {
                throw new ServiceException("Không thể lưu thông tin lớp null");
            }
            
            if (classObj.getMaLop() == null || classObj.getMaLop().isEmpty()) {
                throw new ServiceException("Mã lớp không được để trống");
            }
            
            return classDao.save(classObj);
        } catch (Exception e) {
            if (e instanceof ServiceException) {
                throw (ServiceException) e;
            }
            logger.log(Level.SEVERE, "Error saving class: " + classObj, e);
            throw new ServiceException("Lỗi hệ thống khi lưu lớp: " + e.getMessage());
        }
    }
    
    /**
     * Delete a class
     * @param maLop The class ID to delete
     * @return true if successful, false otherwise
     * @throws ServiceException if an error occurs
     */
    public boolean deleteClass(String maLop) throws ServiceException {
        try {
            if (maLop == null || maLop.isEmpty()) {
                throw new ServiceException("Mã lớp không được để trống");
            }
            
            return classDao.delete(maLop);
        } catch (Exception e) {
            if (e instanceof ServiceException) {
                throw (ServiceException) e;
            }
            logger.log(Level.SEVERE, "Error deleting class: " + maLop, e);
            throw new ServiceException("Lỗi hệ thống khi xóa lớp: " + e.getMessage());
        }
    }
} 