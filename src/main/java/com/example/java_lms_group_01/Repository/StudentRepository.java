package com.example.java_lms_group_01.Repository;

import com.example.java_lms_group_01.model.Attendance;
import com.example.java_lms_group_01.model.Course;
import com.example.java_lms_group_01.model.Eligibility;
import com.example.java_lms_group_01.model.Material;
import com.example.java_lms_group_01.model.Medical;
import com.example.java_lms_group_01.model.Timetable;
import com.example.java_lms_group_01.model.summary.StudentGradeSummary;
import com.example.java_lms_group_01.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentRepository {

    private final AttendanceRepository attendanceRepository = new AttendanceRepository();
    private final EligibilityRepository eligibilityRepository = new EligibilityRepository();
    private final EnrollmentRepository enrollmentRepository = new EnrollmentRepository();
    private final MarkRepository markRepository = new MarkRepository();
    private final MedicalRepository medicalRepository = new MedicalRepository();
    private final TimetableRepository timetableRepository = new TimetableRepository();

    public List<Attendance> findAttendanceByStudent(String regNo) throws SQLException {
        return attendanceRepository.findAttendanceByStudent(regNo);
    }

    public List<Eligibility> findAttendanceEligibilityByStudent(String regNo) throws SQLException {
        return eligibilityRepository.findAttendanceEligibilityByStudent(regNo);
    }

    public List<Course> findCoursesByStudent(String regNo) throws SQLException {
        return enrollmentRepository.findCoursesByStudent(regNo);
    }

    public StudentGradeSummary findGradeSummary(String regNo) throws SQLException {
        return markRepository.findGradeSummary(regNo);
    }

    public List<Material> findMaterialsByStudent(String regNo, String keyword) throws SQLException {
        String sql = "SELECT lm.material_id, lm.courseCode, lm.name, lm.path, lm.material_type FROM lecture_materials lm INNER JOIN enrollment e ON e.courseCode = lm.courseCode WHERE e.studentReg = ? AND (? = '' OR lm.courseCode LIKE ? OR lm.name LIKE ?) ORDER BY lm.material_id DESC";
        String search = keyword == null ? "" : keyword.trim();
        String pattern = "%" + search + "%";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, regNo);
            statement.setString(2, search);
            statement.setString(3, pattern);
            statement.setString(4, pattern);

            try (ResultSet rs = statement.executeQuery()) {
                List<Material> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(new Material(
                            String.valueOf(rs.getInt("material_id")),
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

    public List<Medical> findMedicalByStudent(String regNo) throws SQLException {
        return medicalRepository.findMedicalByStudent(regNo);
    }

    public List<Timetable> findTimetableByStudent(String regNo) throws SQLException {
        return timetableRepository.findByStudent(regNo);
    }
}
