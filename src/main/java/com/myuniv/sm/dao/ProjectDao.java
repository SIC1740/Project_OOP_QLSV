package com.myuniv.sm.dao;

import com.myuniv.sm.model.Project;
import com.myuniv.sm.model.ProjectRegistrationPeriod;

import java.util.List;

/**
 * Data Access Object for Project entity
 */
public interface ProjectDao {
    
    /**
     * Find a project by ID
     * @param id The project ID
     * @return The project if found, null otherwise
     */
    Project findById(int id);
    
    /**
     * Get list of all projects
     * @return List of all projects
     */
    List<Project> findAll();
    
    /**
     * Find projects by student ID
     * @param msv The student ID
     * @return List of projects for the specified student
     */
    List<Project> findByStudent(String msv);
    
    /**
     * Find projects by lecturer ID
     * @param maGiangvien The lecturer ID
     * @return List of projects for the specified lecturer
     */
    List<Project> findByLecturer(String maGiangvien);
    
    /**
     * Save a project (create or update)
     * @param project The project to save
     * @return true if successful, false otherwise
     */
    boolean save(Project project);
    
    /**
     * Add a new project
     * @param project The project to add
     * @return true if successful, false otherwise
     */
    boolean add(Project project);
    
    /**
     * Update an existing project
     * @param project The project to update
     * @return true if successful, false otherwise
     */
    boolean update(Project project);
    
    /**
     * Delete a project
     * @param id The project ID to delete
     * @return true if successful, false otherwise
     */
    boolean delete(int id);
    
    /**
     * Get the current registration period
     * @return The current registration period, or null if none exists
     */
    ProjectRegistrationPeriod getCurrentRegistrationPeriod();
    
    /**
     * Save a registration period
     * @param period The registration period to save
     * @return true if successful, false otherwise
     */
    boolean saveRegistrationPeriod(ProjectRegistrationPeriod period);
    
    /**
     * Update a registration period
     * @param period The registration period to update
     * @return true if successful, false otherwise
     */
    boolean updateRegistrationPeriod(ProjectRegistrationPeriod period);
    
    /**
     * Check if project registration is currently open
     * @return true if registration is open, false otherwise
     */
    boolean isRegistrationOpen();
} 