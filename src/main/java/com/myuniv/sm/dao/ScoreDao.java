// File: src/main/java/com/myuniv/sm/dao/ScoreDao.java
package com.myuniv.sm.dao;

import com.myuniv.sm.model.Score;
import java.util.List;

public interface ScoreDao {
    List<Score> findAll();
    Score findById(int id);
    boolean create(Score s);
    boolean update(Score s);
    boolean delete(int id);
}