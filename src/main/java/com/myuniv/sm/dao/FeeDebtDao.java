package com.myuniv.sm.dao;

import com.myuniv.sm.model.FeeDebt;

import java.util.List;

public interface FeeDebtDao {
    List<FeeDebt> findAll();
    List<FeeDebt> findByStatus(String status);
    FeeDebt findById(int debtId);
    boolean save(FeeDebt feeDebt);
    boolean update(FeeDebt feeDebt);
    boolean delete(int debtId);
} 