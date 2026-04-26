package com.example.java_lms_group_01.Repository;

import com.example.java_lms_group_01.model.Grade;
import com.example.java_lms_group_01.model.Mark;
import com.example.java_lms_group_01.model.Performance;
import com.example.java_lms_group_01.model.request.MarkRequest;
import com.example.java_lms_group_01.model.summary.AcademicSummary;
import com.example.java_lms_group_01.model.summary.GradeResult;
import com.example.java_lms_group_01.model.summary.MarkBreakdown;
import com.example.java_lms_group_01.model.summary.StudentGradeSummary;
import com.example.java_lms_group_01.model.summary.UndergraduateSummary;
import com.example.java_lms_group_01.util.AssessmentStructureUtil;
import com.example.java_lms_group_01.util.DBConnection;
import com.example.java_lms_group_01.util.GradeScaleUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class MarkRepository {

    private final EligibilityRepository eligibilityRepository = new EligibilityRepository();

    public List<Performance> findPerformanceByLecturer(String lecturerReg, String studentKeyword, String courseCode, String batch) throws SQLException {
        String sql = "SELECT m.StudentReg, u.firstName, u.lastName, m.courseCode, c.name, c.credit, m.quiz_1, m.quiz_2, m.quiz_3, m.assessment, m.Project, m.mid_term, m.final_theory, m.final_practical, (SELECT ea.status FROM exam_attendance ea WHERE ea.studentReg = m.StudentReg AND ea.courseCode = m.courseCode LIMIT 1) AS exam_status, EXISTS (SELECT 1 FROM medical md WHERE md.StudentReg = m.StudentReg AND md.courseCode = m.courseCode AND md.approval_status = 'approved' AND LOWER(COALESCE(md.session_type, '')) = 'exam') AS approved_exam_medical FROM marks m INNER JOIN course c ON c.courseCode = m.courseCode INNER JOIN student s ON s.registrationNo = m.StudentReg INNER JOIN users u ON u.user_id = s.registrationNo WHERE c.lecturerRegistrationNo = ? AND (? = '' OR s.batch = ?) AND (? = '' OR m.StudentReg LIKE ? OR u.firstName LIKE ? OR u.lastName LIKE ?) AND (? = '' OR m.courseCode = ?) ORDER BY m.StudentReg, m.courseCode";
        String keyword = studentKeyword == null ? "" : studentKeyword.trim();
        String selectedBatch = batch == null ? "" : batch.trim();
        String selectedCourse = courseCode == null ? "" : courseCode.trim();
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
                List<Performance> list = new ArrayList<>();
                while (rs.next()) {
                    String studentReg = rs.getString("StudentReg");
                    String currentCourse = rs.getString("courseCode");

                    MarkBreakdown breakdown = AssessmentStructureUtil.calculateMarkBreakdown(
                            connection,
                            currentCourse,
                            rs.getObject("quiz_1") == null ? null : ((Number) rs.getObject("quiz_1")).doubleValue(),
                            rs.getObject("quiz_2") == null ? null : ((Number) rs.getObject("quiz_2")).doubleValue(),
                            rs.getObject("quiz_3") == null ? null : ((Number) rs.getObject("quiz_3")).doubleValue(),
                            rs.getObject("assessment") == null ? null : ((Number) rs.getObject("assessment")).doubleValue(),
                            rs.getObject("Project") == null ? null : ((Number) rs.getObject("Project")).doubleValue(),
                            rs.getObject("mid_term") == null ? null : ((Number) rs.getObject("mid_term")).doubleValue(),
                            rs.getObject("final_theory") == null ? null : ((Number) rs.getObject("final_theory")).doubleValue(),
                            rs.getObject("final_practical") == null ? null : ((Number) rs.getObject("final_practical")).doubleValue()
                    );

                    boolean attendanceEligible = eligibilityRepository.isAttendanceEligible(connection, studentReg, currentCourse);

                    GradeResult gradeResult = GradeScaleUtil.evaluatePublishedGrade(
                            breakdown,
                            attendanceEligible,
                            rs.getString("exam_status"),
                            rs.getInt("approved_exam_medical") == 1
                    );

                    String gradePoint = gradeResult.getGradePoint() == null ? "0.00" : String.format("%.2f", gradeResult.getGradePoint());

                    list.add(new Performance(
                            studentReg,
                            rs.getString("firstName") + " " + rs.getString("lastName"),
                            currentCourse,
                            rs.getString("name"),
                            String.format("%.2f", breakdown.getCaMarks()),
                            String.format("%.2f", breakdown.getEndMarks()),
                            String.format("%.2f", breakdown.getTotalMarks()),
                            gradeResult.getPublishedGrade(),
                            gradePoint,
                            gradePoint
                    ));
                }
                return list;
            }
        }
    }

    public List<UndergraduateSummary> findUndergraduateSummariesByLecturer(String lecturerReg, String studentKeyword, String courseCode, String batch) throws SQLException {
        String sql = "SELECT DISTINCT s.registrationNo, u.firstName, u.lastName FROM student s INNER JOIN users u ON u.user_id = s.registrationNo INNER JOIN enrollment e ON e.studentReg = s.registrationNo INNER JOIN course c ON c.courseCode = e.courseCode WHERE c.lecturerRegistrationNo = ? AND (? = '' OR s.batch = ?) AND (? = '' OR s.registrationNo LIKE ? OR u.firstName LIKE ? OR u.lastName LIKE ?) AND (? = '' OR e.courseCode = ?) ORDER BY s.registrationNo";
        String keyword = studentKeyword == null ? "" : studentKeyword.trim();
        String selectedBatch = batch == null ? "" : batch.trim();
        String selectedCourse = courseCode == null ? "" : courseCode.trim();
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
                List<UndergraduateSummary> list = new ArrayList<>();
                while (rs.next()) {
                    AcademicSummary summary = calculateAcademicSummary(connection, rs.getString("registrationNo"));
                    list.add(new UndergraduateSummary(
                            rs.getString("registrationNo"),
                            rs.getString("firstName") + " " + rs.getString("lastName"),
                            summary.isWithheld() ? "WH" : String.format("%.2f", summary.getSgpa()),
                            summary.isWithheld() ? "WH" : String.format("%.2f", summary.getCgpa())
                    ));
                }
                return list;
            }
        }
    }

    public void addMarks(String lecturerReg, MarkRequest request) throws SQLException {
        String sql = "INSERT INTO marks (LectureReg, StudentReg, courseCode, quiz_1, quiz_2, quiz_3, assessment, Project, mid_term, final_theory, final_practical) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, lecturerReg);
            statement.setString(2, request.getStudentReg());
            statement.setString(3, request.getCourseCode());
            if (request.getQuiz1() == null) statement.setNull(4, Types.DECIMAL); else statement.setDouble(4, request.getQuiz1());
            if (request.getQuiz2() == null) statement.setNull(5, Types.DECIMAL); else statement.setDouble(5, request.getQuiz2());
            if (request.getQuiz3() == null) statement.setNull(6, Types.DECIMAL); else statement.setDouble(6, request.getQuiz3());
            if (request.getAssessment() == null) statement.setNull(7, Types.DECIMAL); else statement.setDouble(7, request.getAssessment());
            if (request.getProject() == null) statement.setNull(8, Types.DECIMAL); else statement.setDouble(8, request.getProject());
            if (request.getMidTerm() == null) statement.setNull(9, Types.DECIMAL); else statement.setDouble(9, request.getMidTerm());
            if (request.getFinalTheory() == null) statement.setNull(10, Types.DECIMAL); else statement.setDouble(10, request.getFinalTheory());
            if (request.getFinalPractical() == null) statement.setNull(11, Types.DECIMAL); else statement.setDouble(11, request.getFinalPractical());
            statement.executeUpdate();
        }
    }

    public void updateMarks(String lecturerReg, int markId, MarkRequest request) throws SQLException {
        String sql = "UPDATE marks SET StudentReg=?, courseCode=?, quiz_1=?, quiz_2=?, quiz_3=?, assessment=?, Project=?, mid_term=?, final_theory=?, final_practical=? WHERE mark_id=? AND LectureReg=?";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, request.getStudentReg());
            statement.setString(2, request.getCourseCode());
            if (request.getQuiz1() == null) statement.setNull(3, Types.DECIMAL); else statement.setDouble(3, request.getQuiz1());
            if (request.getQuiz2() == null) statement.setNull(4, Types.DECIMAL); else statement.setDouble(4, request.getQuiz2());
            if (request.getQuiz3() == null) statement.setNull(5, Types.DECIMAL); else statement.setDouble(5, request.getQuiz3());
            if (request.getAssessment() == null) statement.setNull(6, Types.DECIMAL); else statement.setDouble(6, request.getAssessment());
            if (request.getProject() == null) statement.setNull(7, Types.DECIMAL); else statement.setDouble(7, request.getProject());
            if (request.getMidTerm() == null) statement.setNull(8, Types.DECIMAL); else statement.setDouble(8, request.getMidTerm());
            if (request.getFinalTheory() == null) statement.setNull(9, Types.DECIMAL); else statement.setDouble(9, request.getFinalTheory());
            if (request.getFinalPractical() == null) statement.setNull(10, Types.DECIMAL); else statement.setDouble(10, request.getFinalPractical());
            statement.setInt(11, markId);
            statement.setString(12, lecturerReg);
            statement.executeUpdate();
        }
    }

    public void deleteMarks(String lecturerReg, int markId) throws SQLException {
        String sql = "DELETE FROM marks WHERE mark_id = ? AND LectureReg = ?";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, markId);
            statement.setString(2, lecturerReg);
            statement.executeUpdate();
        }
    }

    public List<Mark> findMarksByLecturer(String lecturerReg, String keyword) throws SQLException {
        String sql = "SELECT mark_id, StudentReg, courseCode, quiz_1, quiz_2, quiz_3, assessment, Project, mid_term, final_theory, final_practical FROM marks WHERE LectureReg = ? AND (? = '' OR StudentReg LIKE ? OR courseCode LIKE ?) ORDER BY mark_id DESC";
        String key = keyword == null ? "" : keyword.trim();
        String pattern = "%" + key + "%";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, lecturerReg);
            statement.setString(2, key);
            statement.setString(3, pattern);
            statement.setString(4, pattern);

            try (ResultSet rs = statement.executeQuery()) {
                List<Mark> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(new Mark(
                            rs.getString("mark_id"),
                            rs.getString("StudentReg"),
                            rs.getString("courseCode"),
                            rs.getObject("quiz_1") == null ? "" : rs.getObject("quiz_1").toString(),
                            rs.getObject("quiz_2") == null ? "" : rs.getObject("quiz_2").toString(),
                            rs.getObject("quiz_3") == null ? "" : rs.getObject("quiz_3").toString(),
                            rs.getObject("assessment") == null ? "" : rs.getObject("assessment").toString(),
                            rs.getObject("Project") == null ? "" : rs.getObject("Project").toString(),
                            rs.getObject("mid_term") == null ? "" : rs.getObject("mid_term").toString(),
                            rs.getObject("final_theory") == null ? "" : rs.getObject("final_theory").toString(),
                            rs.getObject("final_practical") == null ? "" : rs.getObject("final_practical").toString()
                    ));
                }
                return list;
            }
        }
    }

    public StudentGradeSummary findGradeSummary(String studentReg) throws SQLException {
        String sql = "SELECT m.StudentReg, m.courseCode, c.name, c.credit, m.quiz_1, m.quiz_2, m.quiz_3, m.assessment, m.Project, m.mid_term, m.final_theory, m.final_practical, (SELECT ea.status FROM exam_attendance ea WHERE ea.studentReg = m.StudentReg AND ea.courseCode = m.courseCode LIMIT 1) AS exam_status, EXISTS (SELECT 1 FROM medical md WHERE md.StudentReg = m.StudentReg AND md.courseCode = m.courseCode AND md.approval_status = 'approved' AND LOWER(COALESCE(md.session_type, '')) = 'exam') AS approved_exam_medical FROM marks m INNER JOIN course c ON c.courseCode = m.courseCode WHERE m.StudentReg = ? ORDER BY m.courseCode";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, studentReg);

            try (ResultSet rs = statement.executeQuery()) {
                List<Grade> grades = new ArrayList<>();
                double cgpaPoints = 0.0;
                int cgpaCredits = 0;
                double sgpaPoints = 0.0;
                int sgpaCredits = 0;
                boolean withheld = false;

                while (rs.next()) {
                    String currentCourse = rs.getString("courseCode");
                    int credit = rs.getInt("credit");

                    MarkBreakdown breakdown = AssessmentStructureUtil.calculateMarkBreakdown(
                            connection,
                            currentCourse,
                            rs.getObject("quiz_1") == null ? null : ((Number) rs.getObject("quiz_1")).doubleValue(),
                            rs.getObject("quiz_2") == null ? null : ((Number) rs.getObject("quiz_2")).doubleValue(),
                            rs.getObject("quiz_3") == null ? null : ((Number) rs.getObject("quiz_3")).doubleValue(),
                            rs.getObject("assessment") == null ? null : ((Number) rs.getObject("assessment")).doubleValue(),
                            rs.getObject("Project") == null ? null : ((Number) rs.getObject("Project")).doubleValue(),
                            rs.getObject("mid_term") == null ? null : ((Number) rs.getObject("mid_term")).doubleValue(),
                            rs.getObject("final_theory") == null ? null : ((Number) rs.getObject("final_theory")).doubleValue(),
                            rs.getObject("final_practical") == null ? null : ((Number) rs.getObject("final_practical")).doubleValue()
                    );

                    boolean attendanceEligible = eligibilityRepository.isAttendanceEligible(connection, studentReg, currentCourse);

                    GradeResult result = GradeScaleUtil.evaluatePublishedGrade(
                            breakdown,
                            attendanceEligible,
                            rs.getString("exam_status"),
                            rs.getInt("approved_exam_medical") == 1
                    );

                    if ("MC".equalsIgnoreCase(result.getPublishedGrade())) {
                        withheld = true;
                    }

                    grades.add(new Grade(
                            currentCourse,
                            rs.getString("name"),
                            String.format("%.2f", breakdown.getEndMarks()),
                            String.format("%.2f", breakdown.getTotalMarks()),
                            result.getPublishedGrade()
                    ));

                    if (result.getGradePoint() != null) {
                        double gradePoint = result.getGradePoint();
                        sgpaPoints += gradePoint * credit;
                        sgpaCredits += credit;

                        if (!GradeScaleUtil.isEnglishCourse(currentCourse)) {
                            cgpaPoints += gradePoint * credit;
                            cgpaCredits += credit;
                        }
                    }
                }

                double cgpa = cgpaCredits == 0 ? 0.0 : cgpaPoints / cgpaCredits;
                double sgpa = sgpaCredits == 0 ? 0.0 : sgpaPoints / sgpaCredits;
                return new StudentGradeSummary(grades, cgpa, sgpa, withheld);
            }
        }
    }

    AcademicSummary calculateAcademicSummary(Connection connection, String studentReg) throws SQLException {
        String sql = "SELECT m.StudentReg, m.courseCode, c.credit, m.quiz_1, m.quiz_2, m.quiz_3, m.assessment, m.Project, m.mid_term, m.final_theory, m.final_practical, (SELECT ea.status FROM exam_attendance ea WHERE ea.studentReg = m.StudentReg AND ea.courseCode = m.courseCode LIMIT 1) AS exam_status, EXISTS (SELECT 1 FROM medical md WHERE md.StudentReg = m.StudentReg AND md.courseCode = m.courseCode AND md.approval_status = 'approved' AND LOWER(COALESCE(md.session_type, '')) = 'exam') AS approved_exam_medical FROM marks m INNER JOIN course c ON c.courseCode = m.courseCode WHERE m.StudentReg = ?";

        double cgpaPoints = 0.0;
        int cgpaCredits = 0;
        double sgpaPoints = 0.0;
        int sgpaCredits = 0;
        boolean withheld = false;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, studentReg);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    String currentCourse = rs.getString("courseCode");
                    int credit = rs.getInt("credit");

                    MarkBreakdown breakdown = AssessmentStructureUtil.calculateMarkBreakdown(
                            connection,
                            currentCourse,
                            rs.getObject("quiz_1") == null ? null : ((Number) rs.getObject("quiz_1")).doubleValue(),
                            rs.getObject("quiz_2") == null ? null : ((Number) rs.getObject("quiz_2")).doubleValue(),
                            rs.getObject("quiz_3") == null ? null : ((Number) rs.getObject("quiz_3")).doubleValue(),
                            rs.getObject("assessment") == null ? null : ((Number) rs.getObject("assessment")).doubleValue(),
                            rs.getObject("Project") == null ? null : ((Number) rs.getObject("Project")).doubleValue(),
                            rs.getObject("mid_term") == null ? null : ((Number) rs.getObject("mid_term")).doubleValue(),
                            rs.getObject("final_theory") == null ? null : ((Number) rs.getObject("final_theory")).doubleValue(),
                            rs.getObject("final_practical") == null ? null : ((Number) rs.getObject("final_practical")).doubleValue()
                    );

                    boolean attendanceEligible = eligibilityRepository.isAttendanceEligible(connection, studentReg, currentCourse);

                    GradeResult result = GradeScaleUtil.evaluatePublishedGrade(
                            breakdown,
                            attendanceEligible,
                            rs.getString("exam_status"),
                            rs.getInt("approved_exam_medical") == 1
                    );

                    if ("MC".equalsIgnoreCase(result.getPublishedGrade())) {
                        withheld = true;
                    }

                    if (result.getGradePoint() != null) {
                        double gradePoint = result.getGradePoint();
                        sgpaPoints += gradePoint * credit;
                        sgpaCredits += credit;

                        if (!GradeScaleUtil.isEnglishCourse(currentCourse)) {
                            cgpaPoints += gradePoint * credit;
                            cgpaCredits += credit;
                        }
                    }
                }
            }
        }

        double cgpa = cgpaCredits == 0 ? 0.0 : cgpaPoints / cgpaCredits;
        double sgpa = sgpaCredits == 0 ? 0.0 : sgpaPoints / sgpaCredits;
        return new AcademicSummary(cgpa, sgpa, withheld);
    }
}
