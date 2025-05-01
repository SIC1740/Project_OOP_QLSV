package com.myuniv.sm.dao;

import com.myuniv.sm.model.Lecturer;
import java.util.List;

public interface LecturerDao {
    List<Lecturer> findAll();
    Lecturer findById(String maGiangVien);
    boolean add(Lecturer lecturer);
    boolean update(Lecturer lecturer);
    boolean delete(String maGiangVien);
} 