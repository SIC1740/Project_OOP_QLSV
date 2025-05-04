-- Table for managing grade entry periods
CREATE TABLE IF NOT EXISTS GradeEntryPeriod (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ma_mon VARCHAR(20) NOT NULL,
    ma_lop VARCHAR(20) NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_by VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (ma_mon) REFERENCES MonHoc(ma_mon),
    FOREIGN KEY (ma_lop) REFERENCES Lop(ma_lop),
    INDEX idx_entry_period_class_subject (ma_mon, ma_lop)
); 