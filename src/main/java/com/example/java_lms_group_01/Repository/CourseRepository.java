package com.example.java_lms_group_01.Repository;

import com.example.java_lms_group_01.model.Course;
import com.example.java_lms_group_01.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CourseRepository {

    public List<Course> findByFilters(String department, String keyword) throws SQLException {
        String sql = "SELECT courseCode, name, lecturerRegistrationNo, department, semester, credit, course_type FROM course WHERE 1=1";
        List<String> params = new ArrayList<>();

        if (department != null && !department.trim().isEmpty()) {
            sql += " AND department = ?";
            params.add(department.trim());
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql += " AND (courseCode LIKE ? OR name LIKE ?)";
            String pattern = "%" + keyword.trim() + "%";
            params.add(pattern);
            params.add(pattern);
        }

        sql += " ORDER BY courseCode";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.size(); i++) {
                statement.setString(i + 1, params.get(i));
            }

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

    public List<String> findAllDepartments() throws SQLException {
        String sql = "SELECT DISTINCT department FROM course WHERE department IS NOT NULL AND TRIM(department) <> '' ORDER BY department";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            List<String> departments = new ArrayList<>();
            while (rs.next()) {
                departments.add(rs.getString("department"));
            }
            return departments;
        }
    }

    public boolean save(Course course) throws SQLException {
        String sql = "INSERT INTO course (courseCode, name, lecturerRegistrationNo, department, semester, credit, course_type) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, course.getCourseCode());
            statement.setString(2, course.getName());
            statement.setString(3, course.getLecturerRegistrationNo());
            statement.setString(4, course.getDepartment());
            statement.setString(5, course.getSemester());
            statement.setInt(6, course.getCredit());
            statement.setString(7, course.getCourseType());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean update(Course course) throws SQLException {
        String sql = "UPDATE course SET name=?, lecturerRegistrationNo=?, department=?, semester=?, credit=?, course_type=? WHERE courseCode=?";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, course.getName());
            statement.setString(2, course.getLecturerRegistrationNo());
            statement.setString(3, course.getDepartment());
            statement.setString(4, course.getSemester());
            statement.setInt(5, course.getCredit());
            statement.setString(6, course.getCourseType());
            statement.setString(7, course.getCourseCode());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteByCourseCode(String courseCode) throws SQLException {
        String sql = "DELETE FROM course WHERE courseCode = ?";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, courseCode);
            return statement.executeUpdate() > 0;
        }
    }
}
