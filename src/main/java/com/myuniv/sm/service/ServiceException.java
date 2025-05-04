package com.myuniv.sm.service;

/**
 * Custom exception for service layer
 */
public class ServiceException extends RuntimeException {
    
    /**
     * Constructs a new service exception with null as its detail message.
     */
    public ServiceException() {
        super();
    }
    
    /**
     * Constructs a new service exception with the specified detail message.
     * @param message the detail message
     */
    public ServiceException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new service exception with the specified detail message and cause.
     * @param message the detail message
     * @param cause the cause
     */
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructs a new service exception with the specified cause.
     * @param cause the cause
     */
    public ServiceException(Throwable cause) {
        super(cause);
    }
} 