package com.myuniv.sm.service;

import com.myuniv.sm.dao.UserDao;
import com.myuniv.sm.dao.impl.UserDaoJdbc;
import com.myuniv.sm.model.User;
import com.myuniv.sm.util.SecurityUtil;

import java.util.List;

public class UserService {
    private final UserDao userDao;
    
    public UserService() {
        this.userDao = new UserDaoJdbc();
    }
    
    public List<User> findAllUsers() {
        return userDao.findAll();
    }
    
    public User findUserById(int userId) {
        return userDao.findById(userId);
    }
    
    public User findUserByUsername(String username) {
        return userDao.findByUsername(username);
    }
    
    public boolean createUser(String username, String password, String role) {
        // Check if user already exists
        if (userDao.findByUsername(username) != null) {
            return false; // Username already exists
        }
        
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(SecurityUtil.hashPassword(password));
        user.setRole(role);
        
        return userDao.save(user);
    }
    
    public boolean updateUser(int userId, String username, String password, String role) {
        User user = userDao.findById(userId);
        if (user == null) {
            return false;
        }
        
        // Check if username is already taken by another user
        User existingUser = userDao.findByUsername(username);
        if (existingUser != null && existingUser.getUserId() != userId) {
            return false; // Username already exists for another user
        }
        
        user.setUsername(username);
        // Only update password if provided
        if (password != null && !password.isEmpty()) {
            user.setPasswordHash(SecurityUtil.hashPassword(password));
        }
        user.setRole(role);
        
        return userDao.update(user);
    }
    
    public boolean deleteUser(int userId) {
        return userDao.delete(userId);
    }
} 