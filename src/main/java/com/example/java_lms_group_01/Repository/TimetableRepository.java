package com.example.java_lms_group_01.Repository;

import com.example.java_lms_group_01.model.Timetable;
import com.example.java_lms_group_01.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class TimetableRepository {

    public List<Timetable> findByFilters(String department, String day, String keyword) throws SQLException {
        String sql = "SELECT time_table_id, department, lec_id, courseCode, admin_id, day, start_time, end_time, session_type FROM timetable WHERE 1=1";
        List<String> params = new ArrayList<>();

        if (department != null && !department.trim().isEmpty()) {
            sql += " AND department = ?";
            params.add(department.trim());
        }

        if (day != null && !day.trim().isEmpty()) {
            sql += " AND day = ?";
            params.add(day.trim());
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql += " AND (time_table_id LIKE ? OR courseCode LIKE ? OR lec_id LIKE ?)";
            String pattern = "%" + keyword.trim() + "%";
            params.add(pattern);
            params.add(pattern);
            params.add(pattern);
        }

        sql += " ORDER BY day, start_time, time_table_id";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.size(); i++) {
                statement.setString(i + 1, params.get(i));
            }

            try (ResultSet rs = statement.executeQuery()) {
                List<Timetable> timetables = new ArrayList<>();
                while (rs.next()) {
                    timetables.add(new Timetable(
                            rs.getString("time_table_id"),
                            rs.getString("department"),
                            rs.getString("lec_id"),
                            rs.getString("courseCode"),
                            rs.getString("admin_id"),
                            rs.getString("day"),
                            rs.getTime("start_time") == null ? null : rs.getTime("start_time").toLocalTime(),
                            rs.getTime("end_time") == null ? null : rs.getTime("end_time").toLocalTime(),
                            rs.getString("session_type")
                    ));
                }
                return timetables;
            }
        }
    }

    public List<String> findAllDepartments() throws SQLException {
        String sql = "SELECT DISTINCT department FROM timetable WHERE department IS NOT NULL AND TRIM(department) <> '' ORDER BY department";

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

    public List<String> findAllDays() throws SQLException {
        String sql = "SELECT DISTINCT day FROM timetable WHERE day IS NOT NULL AND TRIM(day) <> '' ORDER BY day";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            List<String> days = new ArrayList<>();
            while (rs.next()) {
                days.add(rs.getString("day"));
            }
            return days;
        }
    }

    public boolean save(Timetable timetable) throws SQLException {
        String sql = "INSERT INTO timetable (time_table_id, department, lec_id, courseCode, admin_id, day, start_time, end_time, session_type) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, timetable.getTimeTableId());
            statement.setString(2, timetable.getDepartment());
            statement.setString(3, timetable.getLecId());
            statement.setString(4, timetable.getCourseCode());
            statement.setString(5, timetable.getAdminId());
            statement.setString(6, timetable.getDay());
            statement.setTime(7, timetable.getStartTime() == null ? null : Time.valueOf(timetable.getStartTime()));
            statement.setTime(8, timetable.getEndTime() == null ? null : Time.valueOf(timetable.getEndTime()));
            statement.setString(9, timetable.getSessionType());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean update(Timetable timetable) throws SQLException {
        String sql = "UPDATE timetable SET department=?, lec_id=?, courseCode=?, admin_id=?, day=?, start_time=?, end_time=?, session_type=? WHERE time_table_id=?";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, timetable.getDepartment());
            statement.setString(2, timetable.getLecId());
            statement.setString(3, timetable.getCourseCode());
            statement.setString(4, timetable.getAdminId());
            statement.setString(5, timetable.getDay());
            statement.setTime(6, timetable.getStartTime() == null ? null : Time.valueOf(timetable.getStartTime()));
            statement.setTime(7, timetable.getEndTime() == null ? null : Time.valueOf(timetable.getEndTime()));
            statement.setString(8, timetable.getSessionType());
            statement.setString(9, timetable.getTimeTableId());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteById(String timeTableId) throws SQLException {
        String sql = "DELETE FROM timetable WHERE time_table_id = ?";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, timeTableId);
            return statement.executeUpdate() > 0;
        }
    }

    public List<Timetable> findByLecturer(String lecturerReg, String keyword) throws SQLException {
        String sql = "SELECT time_table_id, department, lec_id, courseCode, admin_id, day, start_time, end_time, session_type FROM timetable WHERE lec_id = ? AND (? = '' OR courseCode LIKE ? OR day LIKE ? OR time_table_id LIKE ?) ORDER BY day, start_time, time_table_id";
        String key = keyword == null ? "" : keyword.trim();
        String pattern = "%" + key + "%";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, lecturerReg);
            statement.setString(2, key);
            statement.setString(3, pattern);
            statement.setString(4, pattern);
            statement.setString(5, pattern);

            try (ResultSet rs = statement.executeQuery()) {
                List<Timetable> timetables = new ArrayList<>();
                while (rs.next()) {
                    timetables.add(new Timetable(
                            rs.getString("time_table_id"),
                            rs.getString("department"),
                            rs.getString("lec_id"),
                            rs.getString("courseCode"),
                            rs.getString("admin_id"),
                            rs.getString("day"),
                            rs.getTime("start_time") == null ? null : rs.getTime("start_time").toLocalTime(),
                            rs.getTime("end_time") == null ? null : rs.getTime("end_time").toLocalTime(),
                            rs.getString("session_type")
                    ));
                }
                return timetables;
            }
        }
    }

    public List<Timetable> findByStudent(String studentReg) throws SQLException {
        String sql = "SELECT t.time_table_id, t.department, t.lec_id, t.courseCode, t.admin_id, t.day, t.start_time, t.end_time, t.session_type FROM timetable t INNER JOIN student s ON s.department = t.department WHERE s.registrationNo = ? ORDER BY t.day, t.start_time, t.time_table_id";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, studentReg);

            try (ResultSet rs = statement.executeQuery()) {
                List<Timetable> timetables = new ArrayList<>();
                while (rs.next()) {
                    timetables.add(new Timetable(
                            rs.getString("time_table_id"),
                            rs.getString("department"),
                            rs.getString("lec_id"),
                            rs.getString("courseCode"),
                            rs.getString("admin_id"),
                            rs.getString("day"),
                            rs.getTime("start_time") == null ? null : rs.getTime("start_time").toLocalTime(),
                            rs.getTime("end_time") == null ? null : rs.getTime("end_time").toLocalTime(),
                            rs.getString("session_type")
                    ));
                }
                return timetables;
            }
        }
    }
}
