package com.example.java_lms_group_01.Repository;

import com.example.java_lms_group_01.model.Attendance;
import com.example.java_lms_group_01.model.ExamAttendance;
import com.example.java_lms_group_01.model.request.AttendanceRequest;
import com.example.java_lms_group_01.model.request.ExamAttendanceRequest;
import com.example.java_lms_group_01.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AttendanceRepository {

    public void addExamAttendance(ExamAttendanceRequest request) throws SQLException {
        String sql = "INSERT INTO exam_attendance (studentReg, courseCode, status, attendanceDate) VALUES (?, ?, ?, ?)";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, request.getStudentRegNo());
            statement.setString(2, request.getCourseCode());
            statement.setString(3, request.getStatus());
            statement.setDate(4, Date.valueOf(request.getAttendanceDate()));
            statement.executeUpdate();
        }
    }

    public void updateExamAttendance(int examAttendanceId, ExamAttendanceRequest request) throws SQLException {
        String sql = "UPDATE exam_attendance SET studentReg=?, courseCode=?, status=?, attendanceDate=? WHERE exam_attendance_id=?";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, request.getStudentRegNo());
            statement.setString(2, request.getCourseCode());
            statement.setString(3, request.getStatus());
            statement.setDate(4, Date.valueOf(request.getAttendanceDate()));
            statement.setInt(5, examAttendanceId);
            statement.executeUpdate();
        }
    }

    public void deleteExamAttendance(int examAttendanceId) throws SQLException {
        String sql = "DELETE FROM exam_attendance WHERE exam_attendance_id = ?";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, examAttendanceId);
            statement.executeUpdate();
        }
    }

    public List<ExamAttendance> findExamAttendance(String keyword) throws SQLException {
        String sql = "SELECT exam_attendance_id, studentReg, courseCode, status, attendanceDate FROM exam_attendance";
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();

        if (hasKeyword) {
            sql += " WHERE studentReg LIKE ? OR courseCode LIKE ?";
        }

        sql += " ORDER BY exam_attendance_id DESC";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            if (hasKeyword) {
                String pattern = "%" + keyword.trim() + "%";
                statement.setString(1, pattern);
                statement.setString(2, pattern);
            }

            try (ResultSet rs = statement.executeQuery()) {
                List<ExamAttendance> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(new ExamAttendance(
                            String.valueOf(rs.getInt("exam_attendance_id")),
                            rs.getString("studentReg"),
                            rs.getString("courseCode"),
                            rs.getString("status"),
                            rs.getDate("attendanceDate") == null ? "" : rs.getDate("attendanceDate").toString()
                    ));
                }
                return list;
            }
        }
    }

    public void addAttendance(AttendanceRequest request) throws SQLException {
        String sql = "INSERT INTO attendance (StudentReg, courseCode, tech_officer_reg, SubmissionDate, session_type, attendance_status) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, request.getStudentRegNo());
            statement.setString(2, request.getCourseCode());
            statement.setString(3, request.getTechOfficerReg());
            statement.setDate(4, Date.valueOf(request.getSubmissionDate()));
            statement.setString(5, request.getSessionType());
            statement.setString(6, request.getStatus());
            statement.executeUpdate();
        }
    }

    public void updateAttendance(int attendanceId, AttendanceRequest request) throws SQLException {
        String sql = "UPDATE attendance SET StudentReg=?, courseCode=?, SubmissionDate=?, session_type=?, attendance_status=?, tech_officer_reg=? WHERE attendance_id=?";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, request.getStudentRegNo());
            statement.setString(2, request.getCourseCode());
            statement.setDate(3, Date.valueOf(request.getSubmissionDate()));
            statement.setString(4, request.getSessionType());
            statement.setString(5, request.getStatus());
            statement.setString(6, request.getTechOfficerReg());
            statement.setInt(7, attendanceId);
            statement.executeUpdate();
        }
    }

    public void deleteAttendance(int attendanceId) throws SQLException {
        String sql = "DELETE FROM attendance WHERE attendance_id = ?";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, attendanceId);
            statement.executeUpdate();
        }
    }

    public List<Attendance> findAttendance(String keyword) throws SQLException {
        String sql = "SELECT attendance_id, StudentReg, courseCode, SubmissionDate, session_type, attendance_status, tech_officer_reg FROM attendance";
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();

        if (hasKeyword) {
            sql += " WHERE StudentReg LIKE ? OR courseCode LIKE ?";
        }

        sql += " ORDER BY attendance_id DESC";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            if (hasKeyword) {
                String pattern = "%" + keyword.trim() + "%";
                statement.setString(1, pattern);
                statement.setString(2, pattern);
            }

            try (ResultSet rs = statement.executeQuery()) {
                List<Attendance> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(new Attendance(
                            String.valueOf(rs.getInt("attendance_id")),
                            rs.getString("StudentReg"),
                            rs.getString("courseCode"),
                            rs.getDate("SubmissionDate") == null ? "" : rs.getDate("SubmissionDate").toString(),
                            rs.getString("session_type"),
                            rs.getString("attendance_status"),
                            rs.getString("tech_officer_reg")
                    ));
                }
                return list;
            }
        }
    }

    public int countAttendance() throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM attendance");
             ResultSet rs = statement.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public List<Attendance> findAttendanceByStudent(String studentReg) throws SQLException {
        String sql = "SELECT attendance_id, StudentReg, courseCode, SubmissionDate, session_type, attendance_status, tech_officer_reg FROM attendance WHERE StudentReg = ? ORDER BY attendance_id DESC";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, studentReg);

            try (ResultSet rs = statement.executeQuery()) {
                List<Attendance> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(new Attendance(
                            String.valueOf(rs.getInt("attendance_id")),
                            rs.getString("StudentReg"),
                            rs.getString("courseCode"),
                            rs.getDate("SubmissionDate") == null ? "" : rs.getDate("SubmissionDate").toString(),
                            rs.getString("session_type"),
                            rs.getString("attendance_status"),
                            rs.getString("tech_officer_reg")
                    ));
                }
                return list;
            }
        }
    }
}
