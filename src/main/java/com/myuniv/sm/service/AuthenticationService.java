package com.myuniv.sm.service;

import com.myuniv.sm.dao.UserDao;
import com.myuniv.sm.model.User;
import com.myuniv.sm.util.SecurityUtil;

public class AuthenticationService {
    private final UserDao userDao;
    public AuthenticationService(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * @return User đã xác thực (với last_login đã được cập nhật)
     * @throws AuthException nếu sai username/password
     */
    public User authenticate(String username, String password) {
        User user = userDao.findByUsername(username);
        if (user == null) {
            throw new AuthException("User không tồn tại");
        }
        
        if (!SecurityUtil.verifyPassword(password, user.getPasswordHash())) {
            throw new AuthException("Sai mật khẩu");
        }
        
        // cập nhật last_login
        userDao.updateLastLogin(user.getUserId());
        return user;
    }
}
