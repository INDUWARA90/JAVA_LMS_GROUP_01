package com.example.java_lms_group_01.Repository;

import com.example.java_lms_group_01.model.Attendance;
import com.example.java_lms_group_01.model.Eligibility;
import com.example.java_lms_group_01.model.Mark;
import com.example.java_lms_group_01.model.Material;
import com.example.java_lms_group_01.model.Performance;
import com.example.java_lms_group_01.model.Student;
import com.example.java_lms_group_01.model.Timetable;
import com.example.java_lms_group_01.model.request.MarkRequest;
import com.example.java_lms_group_01.model.request.MaterialRequest;
import com.example.java_lms_group_01.model.summary.AcademicSummary;
import com.example.java_lms_group_01.model.summary.UndergraduateSummary;
import com.example.java_lms_group_01.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LecturerRepository {

    private final EnrollmentRepository enrollmentRepository = new EnrollmentRepository();
    private final EligibilityRepository eligibilityRepository = new EligibilityRepository();
    private final MarkRepository markRepository = new MarkRepository();
    private final MedicalRepository medicalRepository = new MedicalRepository();
    private final TimetableRepository timetableRepository = new TimetableRepository();
    //gets attendance records for course taught by a lecturer
    public List<Attendance> findAttendanceMedicalByLecturer(String lecturerReg, String keyword) throws SQLException {
        return medicalRepository.findAttendanceMedicalByLecturer(lecturerReg, keyword);
    }
    //updates the attendance status based on that decision
    public void updateMedicalDecision(String lecturerReg, int medicalId, int attendanceId,
                                      String approvalStatus, String attendanceStatus) throws SQLException {
        medicalRepository.updateMedicalDecision(lecturerReg, medicalId, attendanceId, approvalStatus, attendanceStatus);
    }
    //decides if each student is eligible or not eligible using attendance and marks
    public List<Eligibility> findEligibilityByLecturer(String lecturerReg, String studentKeyword,
                                                       String courseCode, String batch) throws SQLException {
        return eligibilityRepository.findEligibilityByLecturer(lecturerReg, studentKeyword, courseCode, batch);
    }
    //calc student ca,final marks and total return performance report
    public List<Performance> findPerformanceByLecturer(String lecturerReg, String studentKeyword,
                                                       String courseCode, String batch) throws SQLException {
        return markRepository.findPerformanceByLecturer(lecturerReg, studentKeyword, courseCode, batch);
    }
    //calc sgpa and cgpa and return summary
    public List<UndergraduateSummary> findUndergraduateSummariesByLecturer(String lecturerReg, String studentKeyword,
                                                                           String courseCode, String batch)
            throws SQLException {
        return markRepository.findUndergraduateSummariesByLecturer(lecturerReg, studentKeyword, courseCode, batch);
    }
    //addMarks for a student in a course
    public void addMarks(String lecturerReg, MarkRequest request) throws SQLException {
        markRepository.addMarks(lecturerReg, request);
    }
    //updates an exiting marks record
    public void updateMarks(String lecturerReg, int markId, MarkRequest request) throws SQLException {
        markRepository.updateMarks(lecturerReg, markId, request);
    }
    //delete marks record
    public void deleteMarks(String lecturerReg, int markId) throws SQLException {
        markRepository.deleteMarks(lecturerReg, markId);
    }
    //retrives marks entered by a specific lecturer
    public List<Mark> findMarksByLecturer(String lecturerReg, String keyword) throws SQLException {
        return markRepository.findMarksByLecturer(lecturerReg, keyword);
    }
    //get all unique student batches for this lecturer
    public List<String> findBatchesByLecturer(String lecturerReg) throws SQLException {
        return enrollmentRepository.findBatchesByLecturer(lecturerReg);
    }
    //insert if lecturer owns course
    public int addMaterial(String lecturerReg, MaterialRequest request) throws SQLException {
        String sql = "INSERT INTO lecture_materials (courseCode, name, path, material_type) SELECT ?, ?, ?, ? FROM course WHERE courseCode = ? AND lecturerRegistrationNo = ?";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, request.getCourseCode());
            statement.setString(2, request.getName());
            statement.setString(3, request.getPath());
            statement.setString(4, request.getMaterialType());
            statement.setString(5, request.getCourseCode());
            statement.setString(6, lecturerReg);
            return statement.executeUpdate();
        }
    }
    //modify if lecturer owns course
    public int updateMaterial(String lecturerReg, int materialId, MaterialRequest request) throws SQLException {
        String sql = "UPDATE lecture_materials lm INNER JOIN course c ON c.courseCode = lm.courseCode SET lm.courseCode = ?, lm.name = ?, lm.path = ?, lm.material_type = ? WHERE lm.material_id = ? AND c.lecturerRegistrationNo = ?";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, request.getCourseCode());
            statement.setString(2, request.getName());
            statement.setString(3, request.getPath());
            statement.setString(4, request.getMaterialType());
            statement.setInt(5, materialId);
            statement.setString(6, lecturerReg);
            return statement.executeUpdate();
        }
    }
    //remove if lecturer owns course
    public int deleteMaterial(String lecturerReg, int materialId) throws SQLException {
        String sql = "DELETE FROM lecture_materials WHERE material_id = ? AND courseCode IN (SELECT courseCode FROM course WHERE lecturerRegistrationNo = ?)";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, materialId);
            statement.setString(2, lecturerReg);
            return statement.executeUpdate();
        }
    }
    //returns a list of material object
    public List<Material> findMaterialsByLecturer(String lecturerReg) throws SQLException {
        String sql = "SELECT material_id, courseCode, name, path, material_type " +
                "FROM lecture_materials " +
                "WHERE courseCode IN (" +
                "   SELECT courseCode FROM course WHERE lecturerRegistrationNo = ?" +
                ") ORDER BY material_id DESC";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, lecturerReg);

            try (ResultSet rs = statement.executeQuery()) {
                List<Material> list = new ArrayList<>();

                while (rs.next()) {
                    list.add(new Material(
                            rs.getString("material_id"),
                            rs.getString("courseCode"),
                            rs.getString("name"),
                            rs.getString("path"),
                            rs.getString("material_type")
                    ));
                }
                return list;
            }
        }
    }
    //returns a list of student object
    public List<Student> findStudentsByLecturer(String lecturerReg, String keyword) throws SQLException {
        String sql = "SELECT DISTINCT s.registrationNo, u.firstName, u.lastName, u.email, u.phoneNumber, s.department, s.status FROM student s INNER JOIN users u ON u.user_id = s.registrationNo INNER JOIN enrollment e ON e.studentReg = s.registrationNo INNER JOIN course c ON c.courseCode = e.courseCode WHERE c.lecturerRegistrationNo = ? AND (? = '' OR s.registrationNo LIKE ? OR u.firstName LIKE ? OR u.lastName LIKE ? OR s.department LIKE ?) ORDER BY s.registrationNo";
        String key = keyword == null ? "" : keyword.trim();
        String pattern = "%" + key + "%";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, lecturerReg);
            statement.setString(2, key);
            statement.setString(3, pattern);
            statement.setString(4, pattern);
            statement.setString(5, pattern);
            statement.setString(6, pattern);

            try (ResultSet rs = statement.executeQuery()) {
                List<Student> list = new ArrayList<>();
                while (rs.next()) {
                    AcademicSummary summary = markRepository.calculateAcademicSummary(connection, rs.getString("registrationNo"));

                    list.add(new Student(
                            rs.getString("registrationNo"),
                            rs.getString("firstName") + " " + rs.getString("lastName"),
                            rs.getString("email"),
                            rs.getString("phoneNumber"),
                            rs.getString("department"),
                            rs.getString("status"),
                            String.format("%.2f", summary.getCgpa())
                    ));
                }
                return list;
            }
        }
    }
    //returns a list of timetable object
    public List<Timetable> findTimetableByLecturer(String lecturerReg, String keyword) throws SQLException {
        return timetableRepository.findByLecturer(lecturerReg, keyword);
    }
}
