package com.example.java_lms_group_01.Repository;

import com.example.java_lms_group_01.model.Course;
import com.example.java_lms_group_01.model.EnrollmentRecord;
import com.example.java_lms_group_01.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentRepository {

    public List<String> findStudentBatches() throws SQLException {
        String sql = "SELECT DISTINCT batch FROM student WHERE batch IS NOT NULL ORDER BY batch";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            List<String> batches = new ArrayList<>();
            while (rs.next()) {
                String batch = rs.getString("batch");
                if (batch != null && !batch.trim().isEmpty()) {
                    batches.add(batch);
                }
            }
            return batches;
        }
    }

    public List<String> findBatchesByLecturer(String lecturerReg) throws SQLException {
        String sql = "SELECT DISTINCT s.batch FROM student s INNER JOIN enrollment e ON e.studentReg = s.registrationNo INNER JOIN course c ON c.courseCode = e.courseCode WHERE c.lecturerRegistrationNo = ? AND s.batch IS NOT NULL AND TRIM(s.batch) <> '' ORDER BY s.batch";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, lecturerReg);

            try (ResultSet rs = statement.executeQuery()) {
                List<String> batches = new ArrayList<>();
                while (rs.next()) {
                    batches.add(rs.getString("batch"));
                }
                return batches;
            }
        }
    }

    public List<Course> findAvailableCoursesForStudent(String studentReg) throws SQLException {
        String sql = "SELECT c.courseCode, c.name, c.lecturerRegistrationNo, c.department, c.semester, c.credit, c.course_type FROM course c WHERE NOT EXISTS (SELECT 1 FROM enrollment e WHERE e.studentReg = ? AND e.courseCode = c.courseCode) ORDER BY c.courseCode";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, studentReg);

            try (ResultSet rs = statement.executeQuery()) {
                List<Course> courses = new ArrayList<>();
                while (rs.next()) {
                    courses.add(new Course(
                            rs.getString("courseCode"),
                            rs.getString("name"),
                            rs.getString("lecturerRegistrationNo"),
                            rs.getString("department"),
                            rs.getString("semester"),
                            rs.getInt("credit"),
                            rs.getString("course_type")
                    ));
                }
                return courses;
            }
        }
    }

    public List<EnrollmentRecord> findEnrollments(String keyword, String batch) throws SQLException {
        String sql = "SELECT s.registrationNo, u.firstName, u.lastName, s.batch, e.enrollment_id, e.courseCode, c.name AS course_name, e.enrollment_date, e.status FROM student s INNER JOIN users u ON u.user_id = s.registrationNo LEFT JOIN enrollment e ON e.studentReg = s.registrationNo LEFT JOIN course c ON c.courseCode = e.courseCode WHERE 1=1";
        List<String> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql += " AND s.registrationNo LIKE ?";
            params.add("%" + keyword.trim() + "%");
        }

        if (batch != null && !batch.trim().isEmpty() && !"All".equalsIgnoreCase(batch.trim())) {
            sql += " AND s.batch = ?";
            params.add(batch.trim());
        }

        sql += " ORDER BY s.registrationNo, e.courseCode";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.size(); i++) {
                statement.setString(i + 1, params.get(i));
            }

            try (ResultSet rs = statement.executeQuery()) {
                List<EnrollmentRecord> records = new ArrayList<>();
                while (rs.next()) {
                    Date date = rs.getDate("enrollment_date");
                    records.add(new EnrollmentRecord(
                            rs.getInt("enrollment_id"),
                            rs.getString("registrationNo"),
                            rs.getString("firstName") + " " + rs.getString("lastName"),
                            rs.getString("batch"),
                            rs.getString("courseCode"),
                            rs.getString("course_name"),
                            date == null ? null : date.toLocalDate(),
                            rs.getString("status")
                    ));
                }
                return records;
            }
        }
    }

    public boolean createEnrollment(String studentReg, String courseCode) throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection()) {

            try {
                try (PreparedStatement checkStudent = connection.prepareStatement("SELECT 1 FROM student WHERE registrationNo = ?")) {
                    checkStudent.setString(1, studentReg);
                    try (ResultSet rs = checkStudent.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("Student not found");
                        }
                    }
                }

                try (PreparedStatement checkCourse = connection.prepareStatement("SELECT 1 FROM course WHERE courseCode = ?")) {
                    checkCourse.setString(1, courseCode);
                    try (ResultSet rs = checkCourse.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("Course not found");
                        }
                    }
                }

                try (PreparedStatement checkEnrollment = connection.prepareStatement("SELECT 1 FROM enrollment WHERE studentReg = ? AND courseCode = ?")) {
                    checkEnrollment.setString(1, studentReg);
                    checkEnrollment.setString(2, courseCode);
                    try (ResultSet rs = checkEnrollment.executeQuery()) {
                        if (rs.next()) {
                            throw new IllegalArgumentException("Already enrolled");
                        }
                    }
                }

                try (PreparedStatement statement = connection.prepareStatement("INSERT INTO enrollment (studentReg, courseCode, enrollment_date, status) VALUES (?, ?, ?, ?)")) {
                    statement.setString(1, studentReg);
                    statement.setString(2, courseCode);
                    statement.setDate(3, Date.valueOf(LocalDate.now()));
                    statement.setString(4, "active");
                    boolean created = statement.executeUpdate() > 0;
                    connection.commit();
                    return created;
                }
            } catch (SQLException | RuntimeException e) {
                throw e;
            }
        }
    }

    public boolean updateEnrollmentStatus(int enrollmentId, String status) throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection()) {

            try {
                String currentStatus = null;

                try (PreparedStatement check = connection.prepareStatement("SELECT status FROM enrollment WHERE enrollment_id = ?")) {
                    check.setInt(1, enrollmentId);
                    try (ResultSet rs = check.executeQuery()) {
                        if (rs.next()) {
                            currentStatus = rs.getString("status");
                        }
                    }
                }

                if (currentStatus == null) {
                    throw new SQLException("Enrollment not found");
                }

                if (currentStatus.equalsIgnoreCase(status)) {
                    throw new IllegalArgumentException("Same status already set");
                }

                try (PreparedStatement statement = connection.prepareStatement("UPDATE enrollment SET status = ? WHERE enrollment_id = ?")) {
                    statement.setString(1, status);
                    statement.setInt(2, enrollmentId);
                    boolean updated = statement.executeUpdate() > 0;
                    connection.commit();
                    return updated;
                }
            } catch (SQLException | RuntimeException e) {
                throw e;
            }
        }
    }

    public boolean enrollStudentToCourse(String studentReg, String courseCode, String status) throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection()) {

            try {
                String existingStatus = null;

                try (PreparedStatement check = connection.prepareStatement("SELECT status FROM enrollment WHERE studentReg = ? AND courseCode = ?")) {
                    check.setString(1, studentReg);
                    check.setString(2, courseCode);
                    try (ResultSet rs = check.executeQuery()) {
                        if (rs.next()) {
                            existingStatus = rs.getString("status");
                        }
                    }
                }

                if (existingStatus == null) {
                    if (!"active".equalsIgnoreCase(status)) {
                        throw new IllegalArgumentException("New enrollment must be active");
                    }

                    try (PreparedStatement checkStudent = connection.prepareStatement("SELECT 1 FROM student WHERE registrationNo = ?")) {
                        checkStudent.setString(1, studentReg);
                        try (ResultSet rs = checkStudent.executeQuery()) {
                            if (!rs.next()) {
                                throw new SQLException("Student not found");
                            }
                        }
                    }

                    try (PreparedStatement checkCourse = connection.prepareStatement("SELECT 1 FROM course WHERE courseCode = ?")) {
                        checkCourse.setString(1, courseCode);
                        try (ResultSet rs = checkCourse.executeQuery()) {
                            if (!rs.next()) {
                                throw new SQLException("Course not found");
                            }
                        }
                    }

                    try (PreparedStatement statement = connection.prepareStatement("INSERT INTO enrollment (studentReg, courseCode, enrollment_date, status) VALUES (?, ?, ?, ?)")) {
                        statement.setString(1, studentReg);
                        statement.setString(2, courseCode);
                        statement.setDate(3, Date.valueOf(LocalDate.now()));
                        statement.setString(4, "active");
                        boolean inserted = statement.executeUpdate() > 0;
                        connection.commit();
                        return inserted;
                    }
                }

                if ("active".equalsIgnoreCase(status)) {
                    throw new IllegalArgumentException("Already active");
                }

                try (PreparedStatement statement = connection.prepareStatement("UPDATE enrollment SET status = ? WHERE studentReg = ? AND courseCode = ?")) {
                    statement.setString(1, status);
                    statement.setString(2, studentReg);
                    statement.setString(3, courseCode);
                    boolean updated = statement.executeUpdate() > 0;
                    connection.commit();
                    return updated;
                }
            } catch (SQLException | RuntimeException e) {
                throw e;
            }
        }
    }

    public List<Course> findCoursesByStudent(String studentReg) throws SQLException {
        String sql = "SELECT c.courseCode, c.name, c.lecturerRegistrationNo, c.department, c.semester, c.credit, c.course_type, e.status FROM course c INNER JOIN enrollment e ON c.courseCode = e.courseCode WHERE e.studentReg = ? ORDER BY c.courseCode";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, studentReg);

            try (ResultSet rs = statement.executeQuery()) {
                List<Course> courses = new ArrayList<>();
                while (rs.next()) {
                    Course course = new Course(
                            rs.getString("courseCode"),
                            rs.getString("name"),
                            rs.getString("lecturerRegistrationNo"),
                            rs.getString("department"),
                            rs.getString("semester"),
                            rs.getInt("credit"),
                            rs.getString("course_type")
                    );
                    course.setEnrollmentStatus(rs.getString("status"));
                    courses.add(course);
                }
                return courses;
            }
        }
    }
}
