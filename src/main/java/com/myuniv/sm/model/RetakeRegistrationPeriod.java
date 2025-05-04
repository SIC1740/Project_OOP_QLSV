package com.myuniv.sm.model;

import java.time.LocalDateTime;

/**
 * Model class for tracking retake registration periods
 */
public class RetakeRegistrationPeriod {
    private int id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean isActive;
    private String description;
    
    public RetakeRegistrationPeriod() {
        // Default constructor
        this.startTime = LocalDateTime.now();
        this.endTime = LocalDateTime.now().plusDays(1);
        this.isActive = false;
    }
    
    public RetakeRegistrationPeriod(LocalDateTime startTime, LocalDateTime endTime, boolean isActive, String description) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.isActive = isActive;
        this.description = description;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    
    public LocalDateTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Check if registration is currently open
     * @return true if registration is open, false otherwise
     */
    public boolean isRegistrationOpen() {
        if (!isActive) return false;
        
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(startTime) && now.isBefore(endTime);
    }
    
    /**
     * Get status description
     * @return String describing status
     */
    public String getStatusDescription() {
        if (!isActive) {
            return "Đã đóng";
        }
        
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(startTime)) {
            return "Chưa mở (bắt đầu " + startTime.toLocalDate() + ")";
        } else if (now.isAfter(endTime)) {
            return "Đã kết thúc";
        } else {
            return "Đang mở";
        }
    }
} 