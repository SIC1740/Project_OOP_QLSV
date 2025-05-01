package com.myuniv.sm.dao;

import com.myuniv.sm.model.Lecturer;
import java.util.List;

/**
 * Data Access Object for Lecturer entity
 */
public interface LecturerDao {
    
    /**
     * Find a lecturer by their ID
     * @param id The lecturer ID to look up
     * @return The lecturer if found, null otherwise
     */
    Lecturer findById(String id);
    
    /**
     * Get list of all lecturers
     * @return List of all lecturers
     */
    List<Lecturer> findAll();
    
    /**
     * Find lecturers by department
     * @param department The department
     * @return List of lecturers in the specified department
     */
    List<Lecturer> findByDepartment(String department);
    
    /**
     * Save a lecturer (create or update)
     * @param lecturer The lecturer to save
     * @return true if successful, false otherwise
     */
    boolean save(Lecturer lecturer);
    
    /**
     * Delete a lecturer
     * @param id The lecturer ID to delete
     * @return true if successful, false otherwise
     */
    boolean delete(String id);

    boolean add(Lecturer lecturer);

    boolean update(Lecturer lecturer);
} 