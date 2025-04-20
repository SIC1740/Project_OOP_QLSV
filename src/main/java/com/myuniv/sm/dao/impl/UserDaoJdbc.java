package com.myuniv.sm.dao.impl;

import com.myuniv.sm.dao.UserDao;
import com.myuniv.sm.dao.util.DBConnection;
import com.myuniv.sm.model.User;

import java.sql.*;
import java.time.LocalDateTime;

public class UserDaoJdbc implements UserDao {
    private static final String SELECT_BY_USERNAME =
            "SELECT * FROM `User` WHERE username = ?";
    private static final String UPDATE_LAST_LOGIN  =
            "UPDATE `User` SET last_login = ? WHERE user_id = ?";

    @Override
    public User findByUsername(String username) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_USERNAME)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            rs.getString("password_hash"),
                            rs.getString("role"),
                            rs.getTimestamp("last_login") != null
                                    ? rs.getTimestamp("last_login").toLocalDateTime()
                                    : null
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean updateLastLogin(int userId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_LAST_LOGIN)) {
            Timestamp now = Timestamp.valueOf(LocalDateTime.now());
            ps.setTimestamp(1, now);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
