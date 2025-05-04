package com.myuniv.sm.service;

import com.myuniv.sm.dao.KyHocDao;
import com.myuniv.sm.dao.impl.KyHocDaoJdbc;
import com.myuniv.sm.model.KyHoc;

import java.util.List;
import java.util.Set;

public class KyHocService {
    private final KyHocDao kyHocDao;
    
    public KyHocService() {
        this.kyHocDao = new KyHocDaoJdbc();
    }
    
    public KyHocService(KyHocDao kyHocDao) {
        this.kyHocDao = kyHocDao;
    }
    
    /**
     * Lấy danh sách tất cả các kỳ học
     * @return Danh sách tất cả các kỳ học
     */
    public List<KyHoc> getAllAcademicTerms() {
        return kyHocDao.findAll();
    }
    
    /**
     * Lấy kỳ học theo ID
     * @param kyhocId ID của kỳ học
     * @return Kỳ học nếu tìm thấy, null nếu không tìm thấy
     */
    public KyHoc getAcademicTermById(int kyhocId) {
        return kyHocDao.findById(kyhocId);
    }
    
    /**
     * Lấy danh sách các môn học trong một kỳ học
     * @param tenKyhoc Tên kỳ học
     * @return Danh sách các môn học trong kỳ học đó
     */
    public List<KyHoc> getSubjectsByTerm(String tenKyhoc) {
        return kyHocDao.findByTerm(tenKyhoc);
    }
    
    /**
     * Lấy danh sách tất cả tên các kỳ học (không trùng lặp)
     * @return Set chứa tên các kỳ học
     */
    public Set<String> getAllTermNames() {
        return kyHocDao.findAllTermNames();
    }
    
    /**
     * Thêm một kỳ học mới
     * @param tenKyhoc Tên kỳ học
     * @param maMon Mã môn học
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean addTerm(String tenKyhoc, String maMon) {
        KyHoc kyHoc = new KyHoc();
        kyHoc.setTenKyhoc(tenKyhoc);
        kyHoc.setMaMon(maMon);
        return kyHocDao.add(kyHoc);
    }
    
    /**
     * Cập nhật kỳ học
     * @param kyHoc Kỳ học cần cập nhật
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean updateTerm(KyHoc kyHoc) {
        return kyHocDao.update(kyHoc);
    }
    
    /**
     * Xóa kỳ học theo ID
     * @param kyhocId ID của kỳ học cần xóa
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean deleteTerm(int kyhocId) {
        return kyHocDao.delete(kyhocId);
    }
} 