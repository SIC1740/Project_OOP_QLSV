package com.myuniv.sm.dao;

import com.myuniv.sm.model.Class;
import java.util.List;

/**
 * Data Access Object for Class entity
 */
public interface ClassDao {
    
    /**
     * Find a class by its ID
     * @param maLop The class ID
     * @return The class if found, null otherwise
     */
    Class findByMaLop(String maLop);
    
    /**
     * Get list of all classes
     * @return List of all classes
     */
    List<Class> findAll();
    
    /**
     * Save a class (create or update)
     * @param classObj The class to save
     * @return true if successful, false otherwise
     */
    boolean save(Class classObj);
    
    /**
     * Delete a class
     * @param maLop The class ID to delete
     * @return true if successful, false otherwise
     */
    boolean delete(String maLop);
} 