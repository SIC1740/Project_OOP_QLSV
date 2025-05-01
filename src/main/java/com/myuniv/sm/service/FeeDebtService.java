package com.myuniv.sm.service;

import com.myuniv.sm.dao.FeeDebtDao;
import com.myuniv.sm.dao.impl.FeeDebtDaoJdbc;
import com.myuniv.sm.model.FeeDebt;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class FeeDebtService {
    private final FeeDebtDao feeDebtDao;
    
    public FeeDebtService() {
        this.feeDebtDao = new FeeDebtDaoJdbc();
    }
    
    public List<FeeDebt> findAllFeeDebts() {
        return feeDebtDao.findAll();
    }
    
    public List<FeeDebt> findUnpaidFeeDebts() {
        return feeDebtDao.findByStatus("chưa đóng");
    }
    
    public List<FeeDebt> findPaidFeeDebts() {
        return feeDebtDao.findByStatus("đã đóng");
    }
    
    public FeeDebt findFeeDebtById(int debtId) {
        return feeDebtDao.findById(debtId);
    }
    
    public boolean saveFeeDebt(String msv, String khoanThu, BigDecimal soTien, 
                             LocalDate hanThu, String status) {
        FeeDebt feeDebt = new FeeDebt();
        feeDebt.setMsv(msv);
        feeDebt.setKhoanThu(khoanThu);
        feeDebt.setSoTien(soTien);
        feeDebt.setHanThu(hanThu);
        feeDebt.setStatus(status);
        
        return feeDebtDao.save(feeDebt);
    }
    
    public boolean updateFeeDebt(int debtId, String msv, String khoanThu, 
                               BigDecimal soTien, LocalDate hanThu, String status) {
        FeeDebt feeDebt = feeDebtDao.findById(debtId);
        if (feeDebt == null) {
            return false;
        }
        
        feeDebt.setMsv(msv);
        feeDebt.setKhoanThu(khoanThu);
        feeDebt.setSoTien(soTien);
        feeDebt.setHanThu(hanThu);
        feeDebt.setStatus(status);
        
        return feeDebtDao.update(feeDebt);
    }
    
    public boolean markAsPaid(int debtId) {
        FeeDebt feeDebt = feeDebtDao.findById(debtId);
        if (feeDebt == null) {
            return false;
        }
        
        feeDebt.setStatus("đã đóng");
        return feeDebtDao.update(feeDebt);
    }
    
    public boolean markAsUnpaid(int debtId) {
        FeeDebt feeDebt = feeDebtDao.findById(debtId);
        if (feeDebt == null) {
            return false;
        }
        
        feeDebt.setStatus("chưa đóng");
        return feeDebtDao.update(feeDebt);
    }
    
    public boolean deleteFeeDebt(int debtId) {
        return feeDebtDao.delete(debtId);
    }
} 