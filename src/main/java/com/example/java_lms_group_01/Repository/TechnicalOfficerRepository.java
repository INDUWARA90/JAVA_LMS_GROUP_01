package com.example.java_lms_group_01.Repository;

import com.example.java_lms_group_01.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Database access used by technical officer pages.
 * This class handles attendance records, medical records, and dashboard counts.
 */
public class TechnicalOfficerRepository {

    // Add a new attendance record.
    public void addAttendance(AttendanceMutation mutation) throws SQLException {
        String sql = "INSERT INTO attendance (StudentReg, courseCode, tech_officer_reg, SubmissionDate, session_type, attendance_status) VALUES (?, ?, ?, ?, ?, ?)";
        Connection connection = DBConnection.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            bindAttendanceMutation(statement, mutation);
            statement.executeUpdate();
        }
    }

    public void updateAttendance(int attendanceId, AttendanceMutation mutation) throws SQLException {
        String sql = "UPDATE attendance SET StudentReg = ?, courseCode = ?, SubmissionDate = ?, session_type = ?, attendance_status = ?, tech_officer_reg = ? WHERE attendance_id = ?";
        Connection connection = DBConnection.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            bindAttendanceMutation(statement, mutation);
            statement.setInt(7, attendanceId);
            statement.executeUpdate();
        }
    }

    public void deleteAttendance(int attendanceId) throws SQLException {
        String sql = "DELETE FROM attendance WHERE attendance_id = ?";
        Connection connection = DBConnection.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, attendanceId);
            statement.executeUpdate();
        }
    }

    public List<AttendanceRecord> findAttendance(String keyword) throws SQLException {
        String safeKeyword = keyword == null ? "" : keyword.trim();
        String sql = """
                SELECT attendance_id, StudentReg, courseCode, SubmissionDate, session_type, attendance_status, tech_officer_reg
                FROM attendance
                WHERE (? = '' OR StudentReg LIKE ? OR courseCode LIKE ?)
                ORDER BY attendance_id DESC
                """;
        Connection connection = DBConnection.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            String pattern = "%" + safeKeyword + "%";
            statement.setString(1, safeKeyword);
            statement.setString(2, pattern);
            statement.setString(3, pattern);
            try (ResultSet rs = statement.executeQuery()) {
                List<AttendanceRecord> rows = new ArrayList<>();
                while (rs.next()) {
                    rows.add(mapAttendanceRecord(rs));
                }
                return rows;
            }
        }
    }

    public void addMedical(MedicalMutation mutation) throws SQLException {
        Connection connection = DBConnection.getInstance().getConnection();
        ensureMedicalAttendance(connection, mutation);
        executeMedicalUpsert(connection, mutation, null);
    }

    public void updateMedical(int medicalId, MedicalMutation mutation) throws SQLException {
        Connection connection = DBConnection.getInstance().getConnection();
        ensureMedicalAttendance(connection, mutation);
        executeMedicalUpsert(connection, mutation, medicalId);
    }

    public void deleteMedical(int medicalId, int attendanceId) throws SQLException {
        Connection connection = DBConnection.getInstance().getConnection();
        boolean previousAutoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);
        try (PreparedStatement deleteStatement = connection.prepareStatement("DELETE FROM medical WHERE medical_id = ?");
             PreparedStatement attendanceStatement = connection.prepareStatement("UPDATE attendance SET attendance_status = 'absent' WHERE attendance_id = ?")) {
            deleteStatement.setInt(1, medicalId);
            deleteStatement.executeUpdate();
            attendanceStatement.setInt(1, attendanceId);
            attendanceStatement.executeUpdate();
            connection.commit();
        } catch (Exception e) {
            connection.rollback();
            if (e instanceof SQLException) {
                throw (SQLException) e;
            }
            throw new SQLException(e.getMessage(), e);
        } finally {
            connection.setAutoCommit(previousAutoCommit);
        }
    }

    public List<MedicalRecord> findMedical(String keyword) throws SQLException {
        String safeKeyword = keyword == null ? "" : keyword.trim();
        String sql = """
                SELECT medical_id, StudentReg, courseCode, SubmissionDate, Description, session_type, attendance_id, tech_officer_reg, approval_status
                FROM medical
                WHERE (? = '' OR StudentReg LIKE ? OR courseCode LIKE ?)
                ORDER BY medical_id DESC
                """;
        Connection connection = DBConnection.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            String pattern = "%" + safeKeyword + "%";
            statement.setString(1, safeKeyword);
            statement.setString(2, pattern);
            statement.setString(3, pattern);
            try (ResultSet rs = statement.executeQuery()) {
                List<MedicalRecord> rows = new ArrayList<>();
                while (rs.next()) {
                    rows.add(mapMedicalRecord(rs));
                }
                return rows;
            }
        }
    }

    public int countAttendance() throws SQLException {
        return fetchCount("SELECT COUNT(*) FROM attendance");
    }

    public int countMedical() throws SQLException {
        return fetchCount("SELECT COUNT(*) FROM medical");
    }

    public int countNotices() throws SQLException {
        return fetchCount("SELECT COUNT(*) FROM notice");
    }

    private void ensureMedicalAttendance(Connection connection, MedicalMutation mutation) throws SQLException {
        String sql = """
                SELECT attendance_id
                FROM attendance
                WHERE attendance_id = ?
                  AND StudentReg = ?
                  AND courseCode = ?
                  AND session_type = ?
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, mutation.getAttendanceId());
            statement.setString(2, mutation.getStudentRegNo());
            statement.setString(3, mutation.getCourseCode());
            statement.setString(4, mutation.getSessionType());
            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Attendance record does not match the given student, course, and session type.");
                }
            }
        }
    }

    private void executeMedicalUpsert(Connection connection, MedicalMutation mutation, Integer medicalId) throws SQLException {
        String insertSql = """
                INSERT INTO medical (StudentReg, courseCode, tech_officer_reg, SubmissionDate, Description, session_type, attendance_id, approval_status, approved_by_lecturer, approved_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, 'pending', NULL, NULL)
                """;
        String updateSql = """
                UPDATE medical
                SET StudentReg = ?, courseCode = ?, tech_officer_reg = ?, SubmissionDate = ?, Description = ?, session_type = ?, attendance_id = ?,
                    approval_status = 'pending', approved_by_lecturer = NULL, approved_at = NULL
                WHERE medical_id = ?
                """;
        String attendanceSql = "UPDATE attendance SET attendance_status = 'medical', tech_officer_reg = ? WHERE attendance_id = ?";
        boolean previousAutoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);
        try (PreparedStatement medicalStatement = connection.prepareStatement(medicalId == null ? insertSql : updateSql);
             PreparedStatement attendanceStatement = connection.prepareStatement(attendanceSql)) {
            bindMedicalMutation(medicalStatement, mutation);
            if (medicalId != null) {
                medicalStatement.setInt(8, medicalId);
            }
            medicalStatement.executeUpdate();

            attendanceStatement.setString(1, mutation.getTechOfficerReg());
            attendanceStatement.setInt(2, mutation.getAttendanceId());
            attendanceStatement.executeUpdate();
            connection.commit();
        } catch (Exception e) {
            connection.rollback();
            if (e instanceof SQLException) {
                throw (SQLException) e;
            }
            throw new SQLException(e.getMessage(), e);
        } finally {
            connection.setAutoCommit(previousAutoCommit);
        }
    }

    private void bindAttendanceMutation(PreparedStatement statement, AttendanceMutation mutation) throws SQLException {
        statement.setString(1, mutation.getStudentRegNo());
        statement.setString(2, mutation.getCourseCode());
        statement.setString(3, mutation.getTechOfficerReg());
        statement.setDate(4, Date.valueOf(mutation.getSubmissionDate()));
        statement.setString(5, mutation.getSessionType());
        statement.setString(6, mutation.getStatus());
    }

    private void bindMedicalMutation(PreparedStatement statement, MedicalMutation mutation) throws SQLException {
        statement.setString(1, mutation.getStudentRegNo());
        statement.setString(2, mutation.getCourseCode());
        statement.setString(3, mutation.getTechOfficerReg());
        statement.setDate(4, Date.valueOf(mutation.getSubmissionDate()));
        statement.setString(5, mutation.getDescription());
        statement.setString(6, mutation.getSessionType());
        statement.setInt(7, mutation.getAttendanceId());
    }

    private int fetchCount(String sql) throws SQLException {
        Connection connection = DBConnection.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private AttendanceRecord mapAttendanceRecord(ResultSet rs) throws SQLException {
        Date submissionDate = rs.getDate("SubmissionDate");
        return new AttendanceRecord(
                String.valueOf(rs.getInt("attendance_id")),
                safe(rs.getString("StudentReg")),
                safe(rs.getString("courseCode")),
                submissionDate == null ? "" : submissionDate.toString(),
                safe(rs.getString("session_type")),
                safe(rs.getString("attendance_status")),
                safe(rs.getString("tech_officer_reg"))
        );
    }

    private MedicalRecord mapMedicalRecord(ResultSet rs) throws SQLException {
        Date submissionDate = rs.getDate("SubmissionDate");
        return new MedicalRecord(
                String.valueOf(rs.getInt("medical_id")),
                safe(rs.getString("StudentReg")),
                safe(rs.getString("courseCode")),
                submissionDate == null ? "" : submissionDate.toString(),
                safe(rs.getString("Description")),
                safe(rs.getString("session_type")),
                String.valueOf(rs.getInt("attendance_id")),
                safe(rs.getString("approval_status")),
                safe(rs.getString("tech_officer_reg"))
        );
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }

    public static class AttendanceMutation {
        private final String studentRegNo;
        private final String courseCode;
        private final String techOfficerReg;
        private final java.time.LocalDate submissionDate;
        private final String sessionType;
        private final String status;

        public AttendanceMutation(String studentRegNo, String courseCode, String techOfficerReg,
                                  java.time.LocalDate submissionDate, String sessionType, String status) {
            this.studentRegNo = studentRegNo;
            this.courseCode = courseCode;
            this.techOfficerReg = techOfficerReg;
            this.submissionDate = submissionDate;
            this.sessionType = sessionType;
            this.status = status;
        }

        public String getStudentRegNo() { return studentRegNo; }
        public String getCourseCode() { return courseCode; }
        public String getTechOfficerReg() { return techOfficerReg; }
        public java.time.LocalDate getSubmissionDate() { return submissionDate; }
        public String getSessionType() { return sessionType; }
        public String getStatus() { return status; }
    }

    public static class AttendanceRecord {
        private final String attendanceId;
        private final String studentRegNo;
        private final String courseCode;
        private final String date;
        private final String sessionType;
        private final String status;
        private final String techOfficerReg;

        public AttendanceRecord(String attendanceId, String studentRegNo, String courseCode, String date,
                                String sessionType, String status, String techOfficerReg) {
            this.attendanceId = attendanceId;
            this.studentRegNo = studentRegNo;
            this.courseCode = courseCode;
            this.date = date;
            this.sessionType = sessionType;
            this.status = status;
            this.techOfficerReg = techOfficerReg;
        }

        public String getAttendanceId() { return attendanceId; }
        public String getStudentRegNo() { return studentRegNo; }
        public String getCourseCode() { return courseCode; }
        public String getDate() { return date; }
        public String getSessionType() { return sessionType; }
        public String getStatus() { return status; }
        public String getTechOfficerReg() { return techOfficerReg; }
    }

    public static class MedicalMutation {
        private final String studentRegNo;
        private final String courseCode;
        private final int attendanceId;
        private final java.time.LocalDate submissionDate;
        private final String sessionType;
        private final String description;
        private final String techOfficerReg;

        public MedicalMutation(String studentRegNo, String courseCode, int attendanceId,
                               java.time.LocalDate submissionDate, String sessionType,
                               String description, String techOfficerReg) {
            this.studentRegNo = studentRegNo;
            this.courseCode = courseCode;
            this.attendanceId = attendanceId;
            this.submissionDate = submissionDate;
            this.sessionType = sessionType;
            this.description = description;
            this.techOfficerReg = techOfficerReg;
        }

        public String getStudentRegNo() { return studentRegNo; }
        public String getCourseCode() { return courseCode; }
        public int getAttendanceId() { return attendanceId; }
        public java.time.LocalDate getSubmissionDate() { return submissionDate; }
        public String getSessionType() { return sessionType; }
        public String getDescription() { return description; }
        public String getTechOfficerReg() { return techOfficerReg; }
    }

    public static class MedicalRecord {
        private final String medicalId;
        private final String studentRegNo;
        private final String courseCode;
        private final String date;
        private final String description;
        private final String sessionType;
        private final String attendanceId;
        private final String approvalStatus;
        private final String techOfficerReg;

        public MedicalRecord(String medicalId, String studentRegNo, String courseCode, String date,
                             String description, String sessionType, String attendanceId,
                             String approvalStatus, String techOfficerReg) {
            this.medicalId = medicalId;
            this.studentRegNo = studentRegNo;
            this.courseCode = courseCode;
            this.date = date;
            this.description = description;
            this.sessionType = sessionType;
            this.attendanceId = attendanceId;
            this.approvalStatus = approvalStatus;
            this.techOfficerReg = techOfficerReg;
        }

        public String getMedicalId() { return medicalId; }
        public String getStudentRegNo() { return studentRegNo; }
        public String getCourseCode() { return courseCode; }
        public String getDate() { return date; }
        public String getDescription() { return description; }
        public String getSessionType() { return sessionType; }
        public String getAttendanceId() { return attendanceId; }
        public String getApprovalStatus() { return approvalStatus; }
        public String getTechOfficerReg() { return techOfficerReg; }
    }
}
