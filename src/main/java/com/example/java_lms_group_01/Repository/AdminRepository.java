package com.example.java_lms_group_01.Repository;

import com.example.java_lms_group_01.model.Course;
import com.example.java_lms_group_01.model.EnrollmentRecord;
import com.example.java_lms_group_01.model.Notice;
import com.example.java_lms_group_01.model.Timetable;
import com.example.java_lms_group_01.model.UserRecord;

import java.sql.SQLException;
import java.util.List;

public class AdminRepository {

    private final UserRepository userRepository = new UserRepository();
    private final CourseRepository courseRepository = new CourseRepository();
    private final EnrollmentRepository enrollmentRepository = new EnrollmentRepository();
    private final NoticeRepository noticeRepository = new NoticeRepository();
    private final TimetableRepository timetableRepository = new TimetableRepository();

    public List<UserRecord> findAdmins() throws SQLException {
        return userRepository.findAdmins();
    }

    public List<UserRecord> findLecturers() throws SQLException {
        return userRepository.findLecturers();
    }

    public List<UserRecord> findStudents() throws SQLException {
        return userRepository.findStudents();
    }

    public List<UserRecord> findTechnicalOfficers() throws SQLException {
        return userRepository.findTechnicalOfficers();
    }

    public boolean createAdmin(UserRecord row) throws SQLException {
        return userRepository.createAdmin(row);
    }

    public boolean createLecturer(UserRecord row) throws SQLException {
        return userRepository.createLecturer(row);
    }

    public boolean createStudent(UserRecord row) throws SQLException {
        return userRepository.createStudent(row);
    }

    public boolean createTechnicalOfficer(UserRecord row) throws SQLException {
        return userRepository.createTechnicalOfficer(row);
    }

    public boolean updateAdmin(UserRecord row) throws SQLException {
        return userRepository.updateAdmin(row);
    }

    public boolean updateLecturer(UserRecord row) throws SQLException {
        return userRepository.updateLecturer(row);
    }

    public boolean updateStudent(UserRecord row) throws SQLException {
        return userRepository.updateStudent(row);
    }

    public boolean updateTechnicalOfficer(UserRecord row) throws SQLException {
        return userRepository.updateTechnicalOfficer(row);
    }

    public boolean deleteAdmin(String userId) throws SQLException {
        return userRepository.deleteAdmin(userId);
    }

    public boolean deleteLecturer(String userId) throws SQLException {
        return userRepository.deleteLecturer(userId);
    }

    public boolean deleteStudent(String userId) throws SQLException {
        return userRepository.deleteStudent(userId);
    }

    public boolean deleteTechnicalOfficer(String userId) throws SQLException {
        return userRepository.deleteTechnicalOfficer(userId);
    }

    public List<Course> findCoursesByFilters(String department, String keyword) throws SQLException {
        return courseRepository.findByFilters(department, keyword);
    }

    public List<String> findAllCourseDepartments() throws SQLException {
        return courseRepository.findAllDepartments();
    }

    public boolean saveCourse(Course course) throws SQLException {
        return courseRepository.save(course);
    }

    public boolean updateCourse(Course course) throws SQLException {
        return courseRepository.update(course);
    }

    public boolean deleteCourseByCode(String courseCode) throws SQLException {
        return courseRepository.deleteByCourseCode(courseCode);
    }

    public List<String> findStudentBatches() throws SQLException {
        return enrollmentRepository.findStudentBatches();
    }

    public List<Course> findAvailableCoursesForStudent(String studentReg) throws SQLException {
        return enrollmentRepository.findAvailableCoursesForStudent(studentReg);
    }

    public List<EnrollmentRecord> findEnrollments(String keyword, String batch) throws SQLException {
        return enrollmentRepository.findEnrollments(keyword, batch);
    }

    public boolean createEnrollment(String studentReg, String courseCode) throws SQLException {
        return enrollmentRepository.createEnrollment(studentReg, courseCode);
    }

    public boolean updateEnrollmentStatus(int enrollmentId, String status) throws SQLException {
        return enrollmentRepository.updateEnrollmentStatus(enrollmentId, status);
    }

    public boolean enrollStudentToCourse(String studentReg, String courseCode, String status) throws SQLException {
        return enrollmentRepository.enrollStudentToCourse(studentReg, courseCode, status);
    }

    public List<Notice> findAllNotices() throws SQLException {
        return noticeRepository.findAll();
    }

    public List<Notice> findNoticesByKeyword(String keyword) throws SQLException {
        return noticeRepository.findByKeyword(keyword);
    }

    public boolean saveNotice(Notice notice) throws SQLException {
        return noticeRepository.save(notice);
    }

    public boolean updateNotice(Notice notice) throws SQLException {
        return noticeRepository.update(notice);
    }

    public boolean deleteNoticeById(int noticeId) throws SQLException {
        return noticeRepository.deleteById(noticeId);
    }

    public List<Timetable> findTimetablesByFilters(String department, String day, String keyword) throws SQLException {
        return timetableRepository.findByFilters(department, day, keyword);
    }

    public List<String> findAllTimetableDepartments() throws SQLException {
        return timetableRepository.findAllDepartments();
    }

    public List<String> findAllTimetableDays() throws SQLException {
        return timetableRepository.findAllDays();
    }

    public boolean saveTimetable(Timetable timetable) throws SQLException {
        return timetableRepository.save(timetable);
    }

    public boolean updateTimetable(Timetable timetable) throws SQLException {
        return timetableRepository.update(timetable);
    }

    public boolean deleteTimetableById(String timetableId) throws SQLException {
        return timetableRepository.deleteById(timetableId);
    }
}
