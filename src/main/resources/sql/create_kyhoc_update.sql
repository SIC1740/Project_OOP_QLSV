-- Update the KyHoc table to include grade entry time periods
ALTER TABLE KyHoc ADD COLUMN IF NOT EXISTS thoi_gian_bat_dau_nhap TIMESTAMP NULL;
ALTER TABLE KyHoc ADD COLUMN IF NOT EXISTS thoi_gian_ket_thuc_nhap TIMESTAMP NULL;
ALTER TABLE KyHoc ADD COLUMN IF NOT EXISTS cho_phep_nhap_diem BOOLEAN DEFAULT TRUE;

-- Add indexes for optimization
CREATE INDEX IF NOT EXISTS idx_kyhoc_mon ON KyHoc(ma_mon);
CREATE INDEX IF NOT EXISTS idx_kyhoc_nhap_diem ON KyHoc(ma_mon, thoi_gian_bat_dau_nhap, thoi_gian_ket_thuc_nhap, cho_phep_nhap_diem); 