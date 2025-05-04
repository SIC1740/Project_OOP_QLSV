package com.myuniv.sm.service;

import com.myuniv.sm.dao.ProjectDao;
import com.myuniv.sm.dao.impl.ProjectDaoJdbc;
import com.myuniv.sm.model.Project;
import com.myuniv.sm.model.ProjectRegistrationPeriod;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service for managing project data and registration periods
 */
public class ProjectService {
    private final ProjectDao projectDao;
    private static final Logger logger = Logger.getLogger(ProjectService.class.getName());
    
    /**
     * Default constructor that initializes with JDBC DAO implementation
     */
    public ProjectService() {
        this.projectDao = new ProjectDaoJdbc();
    }
    
    /**
     * Constructor with dependency injection for testing
     * @param projectDao The ProjectDao implementation to use
     */
    public ProjectService(ProjectDao projectDao) {
        this.projectDao = projectDao;
    }
    
    /**
     * Find a project by ID
     * @param id The project ID to look up
     * @return The project if found
     * @throws ServiceException If there is an error retrieving the project
     */
    public Project getProjectById(int id) throws ServiceException {
        try {
            Project project = projectDao.findById(id);
            if (project == null) {
                throw new ServiceException("Không tìm thấy đồ án với ID: " + id);
            }
            return project;
        } catch (Exception e) {
            if (e instanceof ServiceException) {
                throw (ServiceException) e;
            }
            logger.log(Level.SEVERE, "Error getting project by ID: " + id, e);
            throw new ServiceException("Lỗi hệ thống khi tìm kiếm đồ án: " + e.getMessage());
        }
    }
    
    /**
     * Get a list of all projects
     * @return List of all projects
     * @throws ServiceException If there is an error retrieving projects
     */
    public List<Project> findAllProjects() throws ServiceException {
        try {
            return projectDao.findAll();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding all projects", e);
            throw new ServiceException("Lỗi khi lấy danh sách đồ án: " + e.getMessage());
        }
    }
    
    /**
     * Find projects by student ID
     * @param msv The student ID to search for
     * @return List of projects for the specified student
     * @throws ServiceException If there is an error retrieving projects
     */
    public List<Project> findProjectsByStudent(String msv) throws ServiceException {
        try {
            if (msv == null || msv.trim().isEmpty()) {
                throw new ServiceException("Mã sinh viên không được để trống");
            }
            return projectDao.findByStudent(msv);
        } catch (Exception e) {
            if (e instanceof ServiceException) {
                throw (ServiceException) e;
            }
            logger.log(Level.SEVERE, "Error finding projects by student ID: " + msv, e);
            throw new ServiceException("Lỗi khi tìm đồ án theo mã sinh viên: " + e.getMessage());
        }
    }
    
    /**
     * Find projects by lecturer ID
     * @param maGiangvien The lecturer ID to search for
     * @return List of projects for the specified lecturer
     * @throws ServiceException If there is an error retrieving projects
     */
    public List<Project> findProjectsByLecturer(String maGiangvien) throws ServiceException {
        try {
            if (maGiangvien == null || maGiangvien.trim().isEmpty()) {
                throw new ServiceException("Mã giảng viên không được để trống");
            }
            return projectDao.findByLecturer(maGiangvien);
        } catch (Exception e) {
            if (e instanceof ServiceException) {
                throw (ServiceException) e;
            }
            logger.log(Level.SEVERE, "Error finding projects by lecturer ID: " + maGiangvien, e);
            throw new ServiceException("Lỗi khi tìm đồ án theo mã giảng viên: " + e.getMessage());
        }
    }
    
    /**
     * Save a project
     * @param project The project to save
     * @return true if successful, false otherwise
     * @throws ServiceException if an error occurs
     */
    public boolean saveProject(Project project) throws ServiceException {
        try {
            validateProject(project);
            return projectDao.save(project);
        } catch (Exception e) {
            if (e instanceof ServiceException) {
                throw (ServiceException) e;
            }
            logger.log(Level.SEVERE, "Error saving project", e);
            throw new ServiceException("Lỗi khi lưu đồ án: " + e.getMessage());
        }
    }
    
    /**
     * Add a new project
     * @param project The project to add
     * @return true if successful, false otherwise
     * @throws ServiceException if registration is closed or an error occurs
     */
    public boolean registerProject(Project project) throws ServiceException {
        try {
            // Check if registration is open
            if (!projectDao.isRegistrationOpen()) {
                throw new ServiceException("Đăng ký đồ án hiện đang đóng. Vui lòng liên hệ quản trị viên.");
            }
            
            validateProject(project);
            
            // Set registration date to today if not already set
            if (project.getNgayDangKy() == null) {
                project.setNgayDangKy(LocalDate.now());
            }
            
            // Check if student already has a project
            List<Project> existingProjects = projectDao.findByStudent(project.getMsv());
            if (!existingProjects.isEmpty()) {
                throw new ServiceException("Sinh viên đã đăng ký đồ án. Nếu muốn thay đổi, vui lòng liên hệ quản trị viên.");
            }
            
            return projectDao.add(project);
        } catch (Exception e) {
            if (e instanceof ServiceException) {
                throw (ServiceException) e;
            }
            logger.log(Level.SEVERE, "Error registering project", e);
            throw new ServiceException("Lỗi khi đăng ký đồ án: " + e.getMessage());
        }
    }
    
    /**
     * Update an existing project
     * @param project The project to update
     * @return true if successful, false otherwise
     * @throws ServiceException if an error occurs
     */
    public boolean updateProject(Project project) throws ServiceException {
        try {
            validateProject(project);
            
            // Check if project exists
            Project existingProject = projectDao.findById(project.getDoanId());
            if (existingProject == null) {
                throw new ServiceException("Không tìm thấy đồ án để cập nhật");
            }
            
            return projectDao.update(project);
        } catch (Exception e) {
            if (e instanceof ServiceException) {
                throw (ServiceException) e;
            }
            logger.log(Level.SEVERE, "Error updating project", e);
            throw new ServiceException("Lỗi khi cập nhật đồ án: " + e.getMessage());
        }
    }
    
    /**
     * Delete a project
     * @param id The project ID to delete
     * @return true if successful, false otherwise
     * @throws ServiceException if an error occurs
     */
    public boolean deleteProject(int id) throws ServiceException {
        try {
            // Check if project exists
            Project existingProject = projectDao.findById(id);
            if (existingProject == null) {
                throw new ServiceException("Không tìm thấy đồ án để xóa");
            }
            
            return projectDao.delete(id);
        } catch (Exception e) {
            if (e instanceof ServiceException) {
                throw (ServiceException) e;
            }
            logger.log(Level.SEVERE, "Error deleting project", e);
            throw new ServiceException("Lỗi khi xóa đồ án: " + e.getMessage());
        }
    }
    
    /**
     * Get the current registration period
     * @return The current registration period
     * @throws ServiceException if an error occurs
     */
    public ProjectRegistrationPeriod getCurrentRegistrationPeriod() throws ServiceException {
        try {
            return projectDao.getCurrentRegistrationPeriod();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting current registration period", e);
            throw new ServiceException("Lỗi khi lấy thông tin kỳ đăng ký: " + e.getMessage());
        }
    }
    
    /**
     * Start a new registration period
     * @param description Description of the registration period
     * @param durationHours Duration in hours (default: 24 hours)
     * @return The created registration period
     * @throws ServiceException if an error occurs
     */
    public ProjectRegistrationPeriod startRegistrationPeriod(String description, int durationHours) throws ServiceException {
        try {
            if (durationHours <= 0) {
                durationHours = 24; // Default to 24 hours
            }
            
            ProjectRegistrationPeriod period = new ProjectRegistrationPeriod();
            period.setStartTime(LocalDateTime.now());
            period.setEndTime(LocalDateTime.now().plusHours(durationHours));
            period.setActive(true);
            period.setDescription(description);
            
            boolean success = projectDao.saveRegistrationPeriod(period);
            if (!success) {
                throw new ServiceException("Không thể tạo kỳ đăng ký mới");
            }
            
            return period;
        } catch (Exception e) {
            if (e instanceof ServiceException) {
                throw (ServiceException) e;
            }
            logger.log(Level.SEVERE, "Error starting registration period", e);
            throw new ServiceException("Lỗi khi bắt đầu kỳ đăng ký: " + e.getMessage());
        }
    }
    
    /**
     * End the current registration period
     * @return true if successful, false otherwise
     * @throws ServiceException if an error occurs
     */
    public boolean endRegistrationPeriod() throws ServiceException {
        try {
            ProjectRegistrationPeriod period = projectDao.getCurrentRegistrationPeriod();
            if (period == null) {
                throw new ServiceException("Không có kỳ đăng ký nào đang mở");
            }
            
            period.setActive(false);
            return projectDao.updateRegistrationPeriod(period);
        } catch (Exception e) {
            if (e instanceof ServiceException) {
                throw (ServiceException) e;
            }
            logger.log(Level.SEVERE, "Error ending registration period", e);
            throw new ServiceException("Lỗi khi kết thúc kỳ đăng ký: " + e.getMessage());
        }
    }
    
    /**
     * Check if registration is currently open
     * @return true if registration is open, false otherwise
     */
    public boolean isRegistrationOpen() {
        try {
            return projectDao.isRegistrationOpen();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error checking registration status", e);
            return false;
        }
    }
    
    /**
     * Validate project data
     * @param project The project to validate
     * @throws ServiceException if validation fails
     */
    private void validateProject(Project project) throws ServiceException {
        if (project == null) {
            throw new ServiceException("Thông tin đồ án không được để trống");
        }
        
        if (project.getMsv() == null || project.getMsv().trim().isEmpty()) {
            throw new ServiceException("Mã sinh viên không được để trống");
        }
        
        if (project.getTenDeTai() == null || project.getTenDeTai().trim().isEmpty()) {
            throw new ServiceException("Tên đề tài không được để trống");
        }
    }
} 