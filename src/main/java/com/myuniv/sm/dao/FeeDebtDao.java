package com.myuniv.sm.dao;

import com.myuniv.sm.model.FeeDebt;

import java.util.List;

/**
 * Data Access Object for FeeDebt entity
 */
public interface FeeDebtDao {

    /**
     * Find a fee debt by ID
     * @param id The debt ID
     * @return The debt if found, null otherwise
     */
    FeeDebt findById(int id);

    /**
     * Get list of all fee debts
     * @return List of all fee debts
     */
    List<FeeDebt> findAll();

    /**
     * Find fee debts by student ID
     * @param msv The student ID
     * @return List of fee debts for the specified student
     */
    List<FeeDebt> findByStudent(String msv);
    
    /**
     * Find fee debts by status
     * @param status The status to filter by
     * @return List of fee debts with the specified status
     */
    List<FeeDebt> findByStatus(FeeDebt.Status status);
    
    /**
     * Find fee debts by status string
     * @param statusStr The status string to filter by
     * @return List of fee debts with the specified status
     */
    List<FeeDebt> findByStatus(String statusStr);

    /**
     * Save a fee debt (create or update)
     * @param feeDebt The fee debt to save
     * @return true if successful, false otherwise
     */
    boolean save(FeeDebt feeDebt);

    /**
     * Add a new fee debt
     * @param feeDebt The fee debt to add
     * @return true if successful, false otherwise
     */
    boolean add(FeeDebt feeDebt);

    /**
     * Update an existing fee debt
     * @param feeDebt The fee debt to update
     * @return true if successful, false otherwise
     */
    boolean update(FeeDebt feeDebt);

    /**
     * Delete a fee debt
     * @param id The debt ID to delete
     * @return true if successful, false otherwise
     */
    boolean delete(int id);
    
    /**
     * Check if a student has any unpaid fee debts
     * @param msv The student ID
     * @return true if student has unpaid fees, false otherwise
     */
    boolean hasUnpaidFees(String msv);
} 