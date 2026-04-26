package com.example.java_lms_group_01.Repository;

import com.example.java_lms_group_01.model.Eligibility;
import com.example.java_lms_group_01.model.summary.MarkBreakdown;
import com.example.java_lms_group_01.util.AssessmentStructureUtil;
import com.example.java_lms_group_01.util.AttendanceEligibilityUtil;
import com.example.java_lms_group_01.util.DBConnection;
import com.example.java_lms_group_01.util.GradeScaleUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EligibilityRepository {

    public List<Eligibility> findAttendanceEligibilityByStudent(String studentReg) throws SQLException {
        String courseSql = "SELECT courseCode FROM enrollment WHERE studentReg = ? ORDER BY courseCode";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement courseStatement = connection.prepareStatement(courseSql)) {
            courseStatement.setString(1, studentReg);

            try (ResultSet courseRs = courseStatement.executeQuery()) {
                List<Eligibility> list = new ArrayList<>();
                while (courseRs.next()) {
                    String courseCode = courseRs.getString("courseCode");

                    int eligibleSessions = 0;
                    int totalSessions = 0;

                    try (PreparedStatement attendanceStatement = connection.prepareStatement(
                            "SELECT COALESCE(SUM(CASE WHEN a.attendance_status = 'present' THEN 1 WHEN a.attendance_status = 'medical' AND EXISTS (SELECT 1 FROM medical m WHERE m.attendance_id = a.attendance_id AND m.approval_status = 'approved') THEN 1 ELSE 0 END), 0) AS eligible_sessions, COUNT(*) AS total_sessions FROM attendance a WHERE a.StudentReg = ? AND a.courseCode = ?")) {
                        attendanceStatement.setString(1, studentReg);
                        attendanceStatement.setString(2, courseCode);
                        try (ResultSet rs = attendanceStatement.executeQuery()) {
                            if (rs.next()) {
                                eligibleSessions = rs.getInt("eligible_sessions");
                                totalSessions = rs.getInt("total_sessions");
                            }
                        }
                    }

                    MarkBreakdown breakdown = null;

                    try (PreparedStatement markStatement = connection.prepareStatement(
                            "SELECT quiz_1, quiz_2, quiz_3, assessment, Project, mid_term, final_theory, final_practical FROM marks WHERE StudentReg = ? AND courseCode = ?")) {
                        markStatement.setString(1, studentReg);
                        markStatement.setString(2, courseCode);
                        try (ResultSet rs = markStatement.executeQuery()) {
                            if (rs.next()) {
                                breakdown = AssessmentStructureUtil.calculateMarkBreakdown(
                                        connection,
                                        courseCode,
                                        rs.getObject("quiz_1") == null ? null : ((Number) rs.getObject("quiz_1")).doubleValue(),
                                        rs.getObject("quiz_2") == null ? null : ((Number) rs.getObject("quiz_2")).doubleValue(),
                                        rs.getObject("quiz_3") == null ? null : ((Number) rs.getObject("quiz_3")).doubleValue(),
                                        rs.getObject("assessment") == null ? null : ((Number) rs.getObject("assessment")).doubleValue(),
                                        rs.getObject("Project") == null ? null : ((Number) rs.getObject("Project")).doubleValue(),
                                        rs.getObject("mid_term") == null ? null : ((Number) rs.getObject("mid_term")).doubleValue(),
                                        rs.getObject("final_theory") == null ? null : ((Number) rs.getObject("final_theory")).doubleValue(),
                                        rs.getObject("final_practical") == null ? null : ((Number) rs.getObject("final_practical")).doubleValue()
                                );
                            }
                        }
                    }

                    double attendancePercentage = AttendanceEligibilityUtil.calculatePercentage(eligibleSessions, totalSessions);
                    double caMarks = breakdown == null ? 0.0 : breakdown.getCaMarks();
                    double caThreshold = breakdown == null ? 0.0 : GradeScaleUtil.minimumRequiredMark(breakdown.getCaMaximum());

                    boolean attendanceEligible = attendancePercentage >= AttendanceEligibilityUtil.MIN_ELIGIBILITY_PERCENTAGE;
                    boolean caEligible = caMarks >= caThreshold;
                    String status;

                    if (attendanceEligible && caEligible) {
                        status = "Eligible";
                    } else if (!attendanceEligible && !caEligible) {
                        status = "Attendance + CA Shortage";
                    } else if (!attendanceEligible) {
                        status = "Attendance Shortage";
                    } else {
                        status = "CA Shortage";
                    }

                    list.add(new Eligibility(
                            "",
                            "",
                            courseCode,
                            String.valueOf(eligibleSessions),
                            String.valueOf(totalSessions),
                            String.format("%.2f%%", attendancePercentage),
                            String.format("%.2f", caMarks),
                            String.format("%.2f", caThreshold),
                            status
                    ));
                }
                return list;
            }
        }
    }

    public List<Eligibility> findEligibilityByLecturer(String lecturerReg, String studentKeyword, String courseCode, String batch) throws SQLException {
        String sql = "SELECT e.studentReg, u.firstName, u.lastName, e.courseCode, SUM(CASE WHEN a.attendance_id IS NOT NULL AND (a.attendance_status = 'present' OR (a.attendance_status = 'medical' AND m.approval_status = 'approved')) THEN 1 ELSE 0 END) AS eligible_sessions, COUNT(a.attendance_id) AS total_sessions, MAX(mk.quiz_1) AS quiz_1, MAX(mk.quiz_2) AS quiz_2, MAX(mk.quiz_3) AS quiz_3, MAX(mk.assessment) AS assessment, MAX(mk.Project) AS Project, MAX(mk.mid_term) AS mid_term FROM enrollment e INNER JOIN course c ON c.courseCode = e.courseCode INNER JOIN student s ON s.registrationNo = e.studentReg INNER JOIN users u ON u.user_id = s.registrationNo LEFT JOIN attendance a ON a.StudentReg = e.studentReg AND a.courseCode = e.courseCode LEFT JOIN medical m ON m.attendance_id = a.attendance_id LEFT JOIN marks mk ON mk.StudentReg = e.studentReg AND mk.courseCode = e.courseCode WHERE c.lecturerRegistrationNo = ? AND (? = '' OR s.batch = ?) AND (? = '' OR e.studentReg LIKE ? OR u.firstName LIKE ? OR u.lastName LIKE ?) AND (? = '' OR e.courseCode = ?) GROUP BY e.studentReg, u.firstName, u.lastName, e.courseCode ORDER BY e.studentReg, e.courseCode";
        String keyword = studentKeyword == null ? "" : studentKeyword.trim();
        String selectedCourse = courseCode == null ? "" : courseCode.trim();
        String selectedBatch = batch == null ? "" : batch.trim();
        String pattern = "%" + keyword + "%";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, lecturerReg);
            statement.setString(2, selectedBatch);
            statement.setString(3, selectedBatch);
            statement.setString(4, keyword);
            statement.setString(5, pattern);
            statement.setString(6, pattern);
            statement.setString(7, pattern);
            statement.setString(8, selectedCourse);
            statement.setString(9, selectedCourse);

            try (ResultSet rs = statement.executeQuery()) {
                List<Eligibility> list = new ArrayList<>();
                while (rs.next()) {
                    int eligibleSessions = rs.getInt("eligible_sessions");
                    int totalSessions = rs.getInt("total_sessions");
                    double attendance = AttendanceEligibilityUtil.calculatePercentage(eligibleSessions, totalSessions);

                    MarkBreakdown breakdown = AssessmentStructureUtil.calculateMarkBreakdown(
                            connection,
                            rs.getString("courseCode"),
                            rs.getObject("quiz_1") == null ? null : ((Number) rs.getObject("quiz_1")).doubleValue(),
                            rs.getObject("quiz_2") == null ? null : ((Number) rs.getObject("quiz_2")).doubleValue(),
                            rs.getObject("quiz_3") == null ? null : ((Number) rs.getObject("quiz_3")).doubleValue(),
                            rs.getObject("assessment") == null ? null : ((Number) rs.getObject("assessment")).doubleValue(),
                            rs.getObject("Project") == null ? null : ((Number) rs.getObject("Project")).doubleValue(),
                            rs.getObject("mid_term") == null ? null : ((Number) rs.getObject("mid_term")).doubleValue(),
                            null,
                            null
                    );

                    double caMarks = breakdown.getCaMarks();
                    double caThreshold = GradeScaleUtil.minimumRequiredMark(breakdown.getCaMaximum());
                    boolean eligible = attendance >= AttendanceEligibilityUtil.MIN_ELIGIBILITY_PERCENTAGE && caMarks >= caThreshold;

                    list.add(new Eligibility(
                            rs.getString("studentReg"),
                            rs.getString("firstName") + " " + rs.getString("lastName"),
                            rs.getString("courseCode"),
                            String.valueOf(eligibleSessions),
                            String.valueOf(totalSessions),
                            String.format("%.2f%%", attendance),
                            String.format("%.2f", caMarks),
                            String.format("%.2f", caThreshold),
                            eligible ? "Eligible" : "Not Eligible"
                    ));
                }
                return list;
            }
        }
    }

    public boolean isAttendanceEligible(String studentReg, String courseCode) throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection()) {
            return isAttendanceEligible(connection, studentReg, courseCode);
        }
    }

    boolean isAttendanceEligible(Connection connection, String studentReg, String courseCode) throws SQLException {
        String sql = "SELECT COALESCE(SUM(CASE WHEN a.attendance_status = 'present' THEN 1 WHEN a.attendance_status = 'medical' AND EXISTS (SELECT 1 FROM medical m WHERE m.attendance_id = a.attendance_id AND m.approval_status = 'approved') THEN 1 ELSE 0 END), 0) AS eligible_sessions, COUNT(*) AS total_sessions FROM attendance a WHERE a.StudentReg = ? AND a.courseCode = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, studentReg);
            statement.setString(2, courseCode);

            try (ResultSet rs = statement.executeQuery()) {
                int eligibleSessions = 0;
                int totalSessions = 0;

                if (rs.next()) {
                    eligibleSessions = rs.getInt("eligible_sessions");
                    totalSessions = rs.getInt("total_sessions");
                }

                return AttendanceEligibilityUtil.calculatePercentage(eligibleSessions, totalSessions)
                        >= AttendanceEligibilityUtil.MIN_ELIGIBILITY_PERCENTAGE;
            }
        }
    }
}
