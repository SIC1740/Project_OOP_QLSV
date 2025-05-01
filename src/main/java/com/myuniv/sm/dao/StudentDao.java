package com.myuniv.sm.dao;

import com.myuniv.sm.model.Student;
import java.util.List;

/**
 * Data Access Object for Student entity
 */
public interface StudentDao {
    
    /**
     * Find a student by their student ID (MSV)
     * @param msv The student ID
     * @return The student if found, null otherwise
     */
    Student findByMsv(String msv);
    
    /**
     * Get list of all students
     * @return List of all students
     */
    List<Student> findAll();
    
    /**
     * Find students by class ID
     * @param maLop The class ID
     * @return List of students in the specified class
     */
    List<Student> findByClass(String maLop);
    
    /**
     * Save a student (create or update)
     * @param student The student to save
     * @return true if successful, false otherwise
     */
    boolean save(Student student);
    
    /**
     * Delete a student
     * @param msv The student ID to delete
     * @return true if successful, false otherwise
     */
    boolean delete(String msv);
} 