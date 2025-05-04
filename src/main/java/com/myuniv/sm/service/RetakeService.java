package com.myuniv.sm.service;

import com.myuniv.sm.dao.FeeDebtDao;
import com.myuniv.sm.dao.RetakeRequestDao;
import com.myuniv.sm.dao.impl.FeeDebtDaoJdbc;
import com.myuniv.sm.dao.impl.RetakeRequestDaoJdbc;
import com.myuniv.sm.model.FeeDebt;
import com.myuniv.sm.model.RetakeRequest;
import com.myuniv.sm.model.RetakeRegistrationPeriod;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service class for handling retake registration business logic
 */
public class RetakeService {
    private final RetakeRequestDao retakeRequestDao;
    private final FeeDebtDao feeDebtDao;
    private final ClassService classService;
    
    public RetakeService() {
        this.retakeRequestDao = new RetakeRequestDaoJdbc();
        this.feeDebtDao = new FeeDebtDaoJdbc();
        this.classService = new ClassService();
    }
    
    /**
     * Open a new registration period
     * @param startTime Start time for the period
     * @param endTime End time for the period
     * @param description Description of the registration period
     * @return true if successful, false otherwise
     */
    public boolean openRegistrationPeriod(LocalDateTime startTime, LocalDateTime endTime, String description) {
        if (endTime.isBefore(startTime)) {
            throw new ServiceException("Thời gian kết thúc phải sau thời gian bắt đầu");
        }
        
        RetakeRegistrationPeriod period = new RetakeRegistrationPeriod(startTime, endTime, true, description);
        return retakeRequestDao.saveRegistrationPeriod(period);
    }
    
    /**
     * Close the current registration period
     * @return true if successful, false otherwise
     */
    public boolean closeRegistrationPeriod() {
        RetakeRegistrationPeriod period = retakeRequestDao.getCurrentRegistrationPeriod();
        if (period == null) {
            throw new ServiceException("Không có đợt đăng ký nào đang mở");
        }
        
        period.setActive(false);
        return retakeRequestDao.updateRegistrationPeriod(period);
    }
    
    /**
     * Get the current registration period
     * @return The current registration period, or null if none exists
     */
    public RetakeRegistrationPeriod getCurrentRegistrationPeriod() {
        return retakeRequestDao.getCurrentRegistrationPeriod();
    }
    
    /**
     * Check if registration is currently open
     * @return true if registration is open, false otherwise
     */
    public boolean isRegistrationOpen() {
        return retakeRequestDao.isRegistrationOpen();
    }
    
    /**
     * Check if a student is eligible for retake registration
     * @param msv Student ID
     * @return true if eligible, false otherwise
     */
    public boolean isEligibleForRetake(String msv) {
        return feeDebtDao.hasUnpaidFees(msv);
    }
    
    /**
     * Register a student for a class retake
     * @param msv Student ID
     * @param maMon Subject ID
     * @return true if successful, false otherwise
     */
    public boolean registerForRetake(String msv, String maMon) {
        // Check if registration is open
        if (!isRegistrationOpen()) {
            throw new ServiceException("Đăng ký học lại hiện đang đóng");
        }
        
        // Check if student has unpaid fees
        if (!isEligibleForRetake(msv)) {
            throw new ServiceException("Sinh viên không có học phí cần đóng, không được đăng ký học lại");
        }
        
        // Find a valid class ID for the subject ID
        String maLop = classService.getValidClassIdForSubject(maMon);
        if (maLop == null) {
            throw new ServiceException("Không tìm thấy lớp học cho môn học có mã " + maMon + ". Vui lòng liên hệ admin để được hỗ trợ.");
        }
        
        // Create and save retake request
        RetakeRequest request = new RetakeRequest();
        request.setMsv(msv);
        request.setMaLop(maLop);
        request.setNgayDangKi(LocalDate.now());
        request.setStatus(RetakeRequest.Status.MỚI);
        
        return retakeRequestDao.add(request);
    }
    
    /**
     * Register a student for a class retake by directly providing a class ID
     * This method bypasses the subject-to-class mapping when we already have a class ID
     * 
     * @param msv Student ID
     * @param maLop Class ID
     * @return true if successful, false otherwise
     */
    public boolean registerForRetakeDirectClass(String msv, String maLop) {
        // Check if registration is open
        if (!isRegistrationOpen()) {
            throw new ServiceException("Đăng ký học lại hiện đang đóng");
        }
        
        // Check if student has unpaid fees
        if (!isEligibleForRetake(msv)) {
            throw new ServiceException("Sinh viên không có học phí cần đóng, không được đăng ký học lại");
        }
        
        // Create and save retake request
        RetakeRequest request = new RetakeRequest();
        request.setMsv(msv);
        request.setMaLop(maLop);
        request.setNgayDangKi(LocalDate.now());
        request.setStatus(RetakeRequest.Status.MỚI);
        
        return retakeRequestDao.add(request);
    }
    
    /**
     * Cancel a retake request
     * @param requestId The request ID to cancel
     * @return true if successful, false otherwise
     */
    public boolean cancelRetakeRequest(int requestId) {
        RetakeRequest request = retakeRequestDao.findById(requestId);
        if (request == null) {
            throw new ServiceException("Không tìm thấy yêu cầu đăng ký học lại");
        }
        
        // Only allow cancellation of new requests
        if (request.getStatus() != RetakeRequest.Status.MỚI) {
            throw new ServiceException("Chỉ có thể hủy yêu cầu đăng ký mới");
        }
        
        return retakeRequestDao.delete(requestId);
    }
    
    /**
     * Approve a retake request
     * @param requestId The request ID to approve
     * @return true if successful, false otherwise
     */
    public boolean approveRetakeRequest(int requestId) {
        RetakeRequest request = retakeRequestDao.findById(requestId);
        if (request == null) {
            throw new ServiceException("Không tìm thấy yêu cầu đăng ký học lại");
        }
        
        request.setStatus(RetakeRequest.Status.CHẤP_NHẬN);
        return retakeRequestDao.update(request);
    }
    
    /**
     * Reject a retake request
     * @param requestId The request ID to reject
     * @return true if successful, false otherwise
     */
    public boolean rejectRetakeRequest(int requestId) {
        RetakeRequest request = retakeRequestDao.findById(requestId);
        if (request == null) {
            throw new ServiceException("Không tìm thấy yêu cầu đăng ký học lại");
        }
        
        request.setStatus(RetakeRequest.Status.TỪ_CHỐI);
        return retakeRequestDao.update(request);
    }
    
    /**
     * Get all retake requests
     * @return List of all retake requests
     */
    public List<RetakeRequest> getAllRetakeRequests() {
        return retakeRequestDao.findAll();
    }
    
    /**
     * Get retake requests by student
     * @param msv Student ID
     * @return List of retake requests for the student
     */
    public List<RetakeRequest> getRetakeRequestsByStudent(String msv) {
        return retakeRequestDao.findByStudent(msv);
    }
    
    /**
     * Get unpaid fee debts for a student
     * @param msv Student ID
     * @return List of unpaid fee debts
     */
    public List<FeeDebt> getUnpaidFeeDebts(String msv) {
        List<FeeDebt> allDebts = feeDebtDao.findByStudent(msv);
        return allDebts.stream()
                .filter(debt -> debt.getStatus() == FeeDebt.Status.CHƯA_ĐÓNG)
                .toList();
    }
} 