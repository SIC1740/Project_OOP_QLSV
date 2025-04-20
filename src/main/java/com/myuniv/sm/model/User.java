package com.myuniv.sm.model;

import java.time.LocalDateTime;

public class User {
    private int userId;
    private String username;
    private String passwordHash;
    private String role;
    private LocalDateTime lastLogin;

    // constructors
    public User() {}
    public User(int userId, String username, String passwordHash,
                String role, LocalDateTime lastLogin) {
        this.userId       = userId;
        this.username     = username;
        this.passwordHash = passwordHash;
        this.role         = role;
        this.lastLogin    = lastLogin;
    }

    // getters & setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                ", lastLogin=" + lastLogin +
                '}';
    }
}
