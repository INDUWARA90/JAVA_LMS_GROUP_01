package com.example.java_lms_group_01.model.users;

import com.example.java_lms_group_01.Repository.CourseRepository;
import com.example.java_lms_group_01.Repository.NoticeRepository;
import com.example.java_lms_group_01.Repository.TimetableRepository;
import com.example.java_lms_group_01.Repository.UserRepository;
import com.example.java_lms_group_01.model.Course;
import com.example.java_lms_group_01.model.Notice;
import com.example.java_lms_group_01.model.Timetable;
import com.example.java_lms_group_01.model.UserManagementRow;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Admin object that also acts as a simple service layer for admin screens.
 */
public class Admin extends User {
    private String registrationNo;
    private String password;
    private final UserRepository userRepository = new UserRepository();
    private final CourseRepository courseRepository = new CourseRepository();
    private final TimetableRepository timetableRepository = new TimetableRepository();
    private final NoticeRepository noticeRepository = new NoticeRepository();

    public Admin() {
    }

    public Admin(String userId, String firstName, String lastName, String email, String address, String phoneNumber, LocalDate dateOfBirth, String gender, String registrationNo, String password) {
        super(userId, firstName, lastName, email, address, phoneNumber, dateOfBirth, gender);
        this.registrationNo = registrationNo;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRegistrationNo() {
        return registrationNo;
    }

    public void setRegistrationNo(String registrationNo) {
        this.registrationNo = registrationNo;
    }

    public List<UserManagementRow> getAdmins() throws SQLException {
        return userRepository.findAdmins();
    }

    public List<UserManagementRow> getLecturers() throws SQLException {
        return userRepository.findLecturers();
    }

    public List<UserManagementRow> getStudents() throws SQLException {
        return userRepository.findStudents();
    }

    public List<UserManagementRow> getTechnicalOfficers() throws SQLException {
        return userRepository.findTechnicalOfficers();
    }

    public boolean addUser(UserRole role, UserManagementRow row) throws SQLException {
        if (role == UserRole.ADMIN) {
            return userRepository.createAdmin(row);
        }
        if (role == UserRole.LECTURER) {
            return userRepository.createLecturer(row);
        }
        if (role == UserRole.STUDENT) {
            return userRepository.createStudent(row);
        }
        if (role == UserRole.TECHNICAL_OFFICER) {
            return userRepository.createTechnicalOfficer(row);
        }
        throw new IllegalArgumentException("Unknown role: " + role);
    }

    public boolean updateUser(UserRole role, UserManagementRow row) throws SQLException {
        if (role == UserRole.ADMIN) {
            return userRepository.updateAdmin(row);
        }
        if (role == UserRole.LECTURER) {
            return userRepository.updateLecturer(row);
        }
        if (role == UserRole.STUDENT) {
            return userRepository.updateStudent(row);
        }
        if (role == UserRole.TECHNICAL_OFFICER) {
            return userRepository.updateTechnicalOfficer(row);
        }
        throw new IllegalArgumentException("Unknown role: " + role);
    }

    public boolean deleteUser(UserRole role, String userId) throws SQLException {
        if (role == UserRole.ADMIN) {
            return userRepository.deleteAdmin(userId);
        }
        if (role == UserRole.LECTURER) {
            return userRepository.deleteLecturer(userId);
        }
        if (role == UserRole.STUDENT) {
            return userRepository.deleteStudent(userId);
        }
        if (role == UserRole.TECHNICAL_OFFICER) {
            return userRepository.deleteTechnicalOfficer(userId);
        }
        throw new IllegalArgumentException("Unknown role: " + role);
    }

    public List<Course> getCourses(String department, String keyword) throws SQLException {
        return courseRepository.findByFilters(department, keyword);
    }

    public List<String> getCourseDepartments() throws SQLException {
        return courseRepository.findAllDepartments();
    }

    public boolean addCourse(Course course) throws SQLException {
        return courseRepository.save(course);
    }

    public boolean updateCourse(Course course) throws SQLException {
        return courseRepository.update(course);
    }

    public boolean deleteCourse(String courseCode) throws SQLException {
        return courseRepository.deleteByCourseCode(courseCode);
    }

    public List<Timetable> getTimetables(String department, String day, String keyword) throws SQLException {
        return timetableRepository.findByFilters(department, day, keyword);
    }

    public List<String> getTimetableDepartments() throws SQLException {
        return timetableRepository.findAllDepartments();
    }

    public List<String> getTimetableDays() throws SQLException {
        return timetableRepository.findAllDays();
    }

    public boolean addTimetable(Timetable timetable) throws SQLException {
        return timetableRepository.save(timetable);
    }

    public boolean updateTimetable(Timetable timetable) throws SQLException {
        return timetableRepository.update(timetable);
    }

    public boolean deleteTimetable(String timetableId) throws SQLException {
        return timetableRepository.deleteById(timetableId);
    }

    public List<Notice> getNotices(String keyword) throws SQLException {
        if (keyword == null || keyword.isBlank()) {
            return noticeRepository.findAll();
        }
        return noticeRepository.findByKeyword(keyword);
    }

    public boolean createNotice(Notice notice) throws SQLException {
        return noticeRepository.save(notice);
    }

    public boolean updateNotice(Notice notice) throws SQLException {
        return noticeRepository.update(notice);
    }

    public boolean deleteNotice(int noticeId) throws SQLException {
        return noticeRepository.deleteById(noticeId);
    }

    @Override
    public String toString() {
        return "Admin{" +
                "registrationNo='" + registrationNo + '\'' +
                '}';
    }
}
