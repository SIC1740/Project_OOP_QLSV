package com.myuniv.sm.dao;

import com.myuniv.sm.model.KyHoc;
import java.util.List;
import java.util.Set;

public interface KyHocDao {
    /**
     * Tìm tất cả các kỳ học
     * @return Danh sách tất cả các kỳ học và môn học của chúng
     */
    List<KyHoc> findAll();
    
    /**
     * Tìm kỳ học theo ID
     * @param kyhocId ID của kỳ học
     * @return Kỳ học tìm thấy hoặc null nếu không tìm thấy
     */
    KyHoc findById(int kyhocId);
    
    /**
     * Tìm danh sách các môn học theo tên kỳ học
     * @param tenKyhoc Tên kỳ học cần tìm
     * @return Danh sách các môn học trong kỳ học đó
     */
    List<KyHoc> findByTerm(String tenKyhoc);
    
    /**
     * Lấy danh sách tất cả các tên kỳ học (không trùng lặp)
     * @return Set chứa tên các kỳ học
     */
    Set<String> findAllTermNames();
    
    /**
     * Lưu (thêm mới hoặc cập nhật) kỳ học
     * @param kyHoc Kỳ học cần lưu
     * @return true nếu thành công, false nếu thất bại
     */
    boolean save(KyHoc kyHoc);
    
    /**
     * Thêm mới kỳ học
     * @param kyHoc Kỳ học cần thêm
     * @return true nếu thành công, false nếu thất bại
     */
    boolean add(KyHoc kyHoc);
    
    /**
     * Cập nhật kỳ học
     * @param kyHoc Kỳ học cần cập nhật
     * @return true nếu thành công, false nếu thất bại
     */
    boolean update(KyHoc kyHoc);
    
    /**
     * Xóa kỳ học theo ID
     * @param kyhocId ID của kỳ học cần xóa
     * @return true nếu thành công, false nếu thất bại
     */
    boolean delete(int kyhocId);
} 