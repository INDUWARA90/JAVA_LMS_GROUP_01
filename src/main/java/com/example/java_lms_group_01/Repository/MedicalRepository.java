package com.example.java_lms_group_01.Repository;

import com.example.java_lms_group_01.model.Attendance;
import com.example.java_lms_group_01.model.Medical;
import com.example.java_lms_group_01.model.request.MedicalRequest;
import com.example.java_lms_group_01.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MedicalRepository {

    public void addMedical(MedicalRequest request) throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection()) {
            connection.setAutoCommit(false);

            try {
                try (PreparedStatement medicalStatement = connection.prepareStatement("INSERT INTO medical (StudentReg, courseCode, tech_officer_reg, SubmissionDate, Description, session_type, attendance_id, approval_status) VALUES (?, ?, ?, ?, ?, ?, ?, 'pending')")) {
                    medicalStatement.setString(1, request.getStudentRegNo());
                    medicalStatement.setString(2, request.getCourseCode());
                    medicalStatement.setString(3, request.getTechOfficerReg());
                    medicalStatement.setDate(4, Date.valueOf(request.getSubmissionDate()));
                    medicalStatement.setString(5, request.getDescription());
                    medicalStatement.setString(6, request.getSessionType());
                    medicalStatement.setInt(7, request.getAttendanceId());
                    medicalStatement.executeUpdate();
                }

                try (PreparedStatement attendanceStatement = connection.prepareStatement("UPDATE attendance SET attendance_status='medical' WHERE attendance_id=?")) {
                    attendanceStatement.setInt(1, request.getAttendanceId());
                    attendanceStatement.executeUpdate();
                }

                connection.commit();
            } catch (SQLException | RuntimeException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public void updateMedical(int medicalId, MedicalRequest request) throws SQLException {
        String sql = "UPDATE medical SET StudentReg=?, courseCode=?, tech_officer_reg=?, SubmissionDate=?, Description=?, session_type=?, attendance_id=? WHERE medical_id=?";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, request.getStudentRegNo());
            statement.setString(2, request.getCourseCode());
            statement.setString(3, request.getTechOfficerReg());
            statement.setDate(4, Date.valueOf(request.getSubmissionDate()));
            statement.setString(5, request.getDescription());
            statement.setString(6, request.getSessionType());
            statement.setInt(7, request.getAttendanceId());
            statement.setInt(8, medicalId);
            statement.executeUpdate();
        }
    }

    public void deleteMedical(int medicalId, int attendanceId) throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection()) {
            connection.setAutoCommit(false);

            try {
                try (PreparedStatement deleteStatement = connection.prepareStatement("DELETE FROM medical WHERE medical_id=?")) {
                    deleteStatement.setInt(1, medicalId);
                    deleteStatement.executeUpdate();
                }

                try (PreparedStatement attendanceStatement = connection.prepareStatement("UPDATE attendance SET attendance_status='absent' WHERE attendance_id=?")) {
                    attendanceStatement.setInt(1, attendanceId);
                    attendanceStatement.executeUpdate();
                }

                connection.commit();
            } catch (SQLException | RuntimeException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public List<Medical> findMedical(String keyword) throws SQLException {
        String sql = "SELECT medical_id, StudentReg, courseCode, SubmissionDate, Description, session_type, attendance_id, tech_officer_reg, approval_status FROM medical WHERE StudentReg LIKE ? OR courseCode LIKE ? ORDER BY medical_id DESC";
        String pattern = "%" + (keyword == null ? "" : keyword.trim()) + "%";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, pattern);
            statement.setString(2, pattern);

            try (ResultSet rs = statement.executeQuery()) {
                List<Medical> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(new Medical(
                            String.valueOf(rs.getInt("medical_id")),
                            rs.getString("StudentReg"),
                            rs.getString("courseCode"),
                            rs.getDate("SubmissionDate") == null ? "" : rs.getDate("SubmissionDate").toString(),
                            rs.getString("Description"),
                            rs.getString("session_type"),
                            String.valueOf(rs.getInt("attendance_id")),
                            rs.getString("approval_status"),
                            rs.getString("tech_officer_reg")
                    ));
                }
                return list;
            }
        }
    }

    public int countMedical() throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM medical");
             ResultSet rs = statement.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public List<Medical> findMedicalByStudent(String studentReg) throws SQLException {
        String sql = "SELECT medical_id, StudentReg, courseCode, SubmissionDate, Description, session_type, attendance_id, approval_status, tech_officer_reg FROM medical WHERE StudentReg = ? ORDER BY medical_id DESC";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, studentReg);

            try (ResultSet rs = statement.executeQuery()) {
                List<Medical> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(new Medical(
                            String.valueOf(rs.getInt("medical_id")),
                            rs.getString("StudentReg"),
                            rs.getString("courseCode"),
                            rs.getDate("SubmissionDate") == null ? "" : rs.getDate("SubmissionDate").toString(),
                            rs.getString("Description"),
                            rs.getString("session_type"),
                            String.valueOf(rs.getInt("attendance_id")),
                            rs.getString("approval_status"),
                            rs.getString("tech_officer_reg")
                    ));
                }
                return list;
            }
        }
    }

    public List<Attendance> findAttendanceMedicalByLecturer(String lecturerReg, String keyword) throws SQLException {
        String sql = "SELECT a.attendance_id, a.StudentReg, a.courseCode, a.SubmissionDate, a.session_type, a.attendance_status, a.tech_officer_reg, m.medical_id, m.Description, m.approval_status FROM attendance a INNER JOIN course c ON c.courseCode = a.courseCode LEFT JOIN medical m ON m.attendance_id = a.attendance_id WHERE c.lecturerRegistrationNo = ? AND (? = '' OR a.StudentReg LIKE ? OR a.courseCode LIKE ?) ORDER BY a.attendance_id DESC";
        String key = keyword == null ? "" : keyword.trim();
        String pattern = "%" + key + "%";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, lecturerReg);
            statement.setString(2, key);
            statement.setString(3, pattern);
            statement.setString(4, pattern);

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
                            rs.getString("tech_officer_reg"),
                            rs.getObject("medical_id") == null ? "" : String.valueOf(rs.getInt("medical_id")),
                            rs.getString("Description"),
                            rs.getString("approval_status")
                    ));
                }
                return list;
            }
        }
    }

    public void updateMedicalDecision(String lecturerReg, int medicalId, int attendanceId, String approvalStatus, String attendanceStatus) throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection()) {
            connection.setAutoCommit(false);

            try {
                int updated;

                try (PreparedStatement medicalStatement = connection.prepareStatement("UPDATE medical SET approval_status=?, approved_by_lecturer=?, approved_at=CURRENT_DATE WHERE medical_id=?")) {
                    medicalStatement.setString(1, approvalStatus);
                    medicalStatement.setString(2, lecturerReg);
                    medicalStatement.setInt(3, medicalId);
                    updated = medicalStatement.executeUpdate();
                }

                if (updated == 0) {
                    throw new SQLException("Medical record not found");
                }

                try (PreparedStatement attendanceStatement = connection.prepareStatement("UPDATE attendance SET attendance_status=? WHERE attendance_id=?")) {
                    attendanceStatement.setString(1, attendanceStatus);
                    attendanceStatement.setInt(2, attendanceId);
                    attendanceStatement.executeUpdate();
                }

                connection.commit();
            } catch (SQLException | RuntimeException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }
}
