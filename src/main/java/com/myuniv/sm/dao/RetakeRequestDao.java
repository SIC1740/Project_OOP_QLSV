package com.myuniv.sm.dao;

import com.myuniv.sm.model.RetakeRequest;
import com.myuniv.sm.model.RetakeRegistrationPeriod;

import java.util.List;

/**
 * Data Access Object for RetakeRequest entity
 */
public interface RetakeRequestDao {

    /**
     * Find a retake request by ID
     * @param id The request ID
     * @return The request if found, null otherwise
     */
    RetakeRequest findById(int id);

    /**
     * Get list of all retake requests
     * @return List of all retake requests
     */
    List<RetakeRequest> findAll();

    /**
     * Find retake requests by student ID
     * @param msv The student ID
     * @return List of retake requests for the specified student
     */
    List<RetakeRequest> findByStudent(String msv);

    /**
     * Find retake requests by class ID
     * @param maLop The class ID
     * @return List of retake requests for the specified class
     */
    List<RetakeRequest> findByClass(String maLop);

    /**
     * Find retake requests by status
     * @param status The status to filter by
     * @return List of retake requests with the specified status
     */
    List<RetakeRequest> findByStatus(RetakeRequest.Status status);

    /**
     * Save a retake request (create or update)
     * @param request The request to save
     * @return true if successful, false otherwise
     */
    boolean save(RetakeRequest request);

    /**
     * Add a new retake request
     * @param request The request to add
     * @return true if successful, false otherwise
     */
    boolean add(RetakeRequest request);

    /**
     * Update an existing retake request
     * @param request The request to update
     * @return true if successful, false otherwise
     */
    boolean update(RetakeRequest request);

    /**
     * Delete a retake request
     * @param id The request ID to delete
     * @return true if successful, false otherwise
     */
    boolean delete(int id);

    /**
     * Get the current registration period
     * @return The current registration period, or null if none exists
     */
    RetakeRegistrationPeriod getCurrentRegistrationPeriod();

    /**
     * Save a registration period
     * @param period The registration period to save
     * @return true if successful, false otherwise
     */
    boolean saveRegistrationPeriod(RetakeRegistrationPeriod period);

    /**
     * Update a registration period
     * @param period The registration period to update
     * @return true if successful, false otherwise
     */
    boolean updateRegistrationPeriod(RetakeRegistrationPeriod period);

    /**
     * Check if retake registration is currently open
     * @return true if registration is open, false otherwise
     */
    boolean isRegistrationOpen();
} 