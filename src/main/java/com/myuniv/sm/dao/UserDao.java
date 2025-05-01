package com.myuniv.sm.dao;

import com.myuniv.sm.model.User;
import java.util.List;

public interface UserDao {
    User findByUsername(String username);
    boolean updateLastLogin(int userId);
    List<User> findAll();
    User findById(int userId);
    boolean save(User user);
    boolean update(User user);
    boolean delete(int userId);
}
