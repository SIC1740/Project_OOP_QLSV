package com.myuniv.sm.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class SecurityUtil {
    /**
     * Verifies if a plain password matches a stored password hash
     * 
     * @param plainPassword the plain password to check
     * @param storedHash the stored hash from the database
     * @return true if the password matches, false otherwise
     */
    public static boolean verifyPassword(String plainPassword, String storedHash) {
        try {
            String hashedInput = hashPassword(plainPassword);
            return hashedInput.equals(storedHash);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Creates a SHA-256 hash of the password (for backward compatibility)
     * Note: In a real application, this should use a stronger algorithm with salt
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Hash password failed", e);
        }
    }
} 