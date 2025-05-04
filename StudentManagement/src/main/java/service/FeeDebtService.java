package service;

import dao.FeeDebtDAO;
import model.FeeDebt;
import java.sql.SQLException;
import java.util.List;

public class FeeDebtService {
    private FeeDebtDAO feeDebtDAO;

    public FeeDebtService() {
        this.feeDebtDAO = new FeeDebtDAO();
    }

    public List<FeeDebt> thongKeNoHocPhi() {
        try {
            List<FeeDebt> dsNo = feeDebtDAO.thongKeNoHocPhi();
            if (dsNo == null || dsNo.isEmpty()) {
                System.out.println("Không có dữ liệu nợ học phí");
            }
            return dsNo;
        } catch (SQLException e) {
            System.err.println("Lỗi khi thống kê nợ học phí: " + e.getMessage());
            return null;
        }
    }

    public double tinhTongNoHocPhi(String msv) {
        try {
            return feeDebtDAO.tinhTongNoHocPhi(msv);
        } catch (SQLException e) {
            System.err.println("Lỗi khi tính tổng nợ: " + e.getMessage());
            return -1; // Giá trị âm để biểu thị lỗi
        }
    }
}