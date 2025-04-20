package com.myuniv.sm.dao;

import com.myuniv.sm.model.User;

public interface UserDao {
    User findByUsername(String username);
    boolean updateLastLogin(int userId);
}
