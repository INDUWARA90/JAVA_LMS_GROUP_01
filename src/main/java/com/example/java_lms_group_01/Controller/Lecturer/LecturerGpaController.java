package com.example.java_lms_group_01.Controller.Lecturer;

import com.example.java_lms_group_01.util.DBConnection;
import com.example.java_lms_group_01.util.LecturerContext;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LecturerGpaController {

    @FXML
    private TextField txtSearch;
    @FXML
    private TableView<PerformanceRow> tblPerformance;
    @FXML
    private TableColumn<PerformanceRow, String> colStudentReg;
    @FXML
    private TableColumn<PerformanceRow, String> colStudentName;
    @FXML
    private TableColumn<PerformanceRow, String> colCourseCode;
    @FXML
    private TableColumn<PerformanceRow, String> colTotalMarks;
    @FXML
    private TableColumn<PerformanceRow, String> colGrade;
    @FXML
    private TableColumn<PerformanceRow, String> colGpa;

    @FXML
    public void initialize() {
        colStudentReg.setCellValueFactory(d -> d.getValue().studentRegProperty());
        colStudentName.setCellValueFactory(d -> d.getValue().studentNameProperty());
        colCourseCode.setCellValueFactory(d -> d.getValue().courseCodeProperty());
        colTotalMarks.setCellValueFactory(d -> d.getValue().totalMarksProperty());
        colGrade.setCellValueFactory(d -> d.getValue().gradeProperty());
        colGpa.setCellValueFactory(d -> d.getValue().gpaProperty());
        loadPerformance(null);
    }

    @FXML
    private void searchPerformance() {
        loadPerformance(txtSearch.getText());
    }

    @FXML
    private void refreshPerformance() {
        txtSearch.clear();
        loadPerformance(null);
    }

    private void loadPerformance(String keyword) {
        String safeKeyword = keyword == null ? "" : keyword.trim();
        String sql = """
                SELECT m.StudentReg, u.firstName, u.lastName, m.courseCode, s.GPA,
                       m.quiz_1, m.quiz_2, m.quiz_3, m.assessment_1, m.assessment_2, m.mid_term, m.final_theory, m.final_practical
                FROM marks m
                INNER JOIN course c ON c.courseCode = m.courseCode
                INNER JOIN student s ON s.registrationNo = m.StudentReg
                INNER JOIN users u ON u.user_id = s.registrationNo
                WHERE c.lecturerRegistrationNo = ?
                  AND (? = '' OR m.StudentReg LIKE ? OR m.courseCode LIKE ? OR u.firstName LIKE ? OR u.lastName LIKE ?)
                ORDER BY m.StudentReg, m.courseCode
                """;

        List<PerformanceRow> rows = new ArrayList<>();
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                String pattern = "%" + safeKeyword + "%";
                statement.setString(1, currentLecturer());
                statement.setString(2, safeKeyword);
                statement.setString(3, pattern);
                statement.setString(4, pattern);
                statement.setString(5, pattern);
                statement.setString(6, pattern);

                try (ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) {
                        double total = calculateAverage(rs);
                        String grade = toGrade(total);
                        String fullName = safe(rs.getString("firstName")) + " " + safe(rs.getString("lastName"));
                        String gpa = rs.getObject("GPA") == null ? "" : String.format("%.2f", ((Number) rs.getObject("GPA")).doubleValue());
                        rows.add(new PerformanceRow(
                                safe(rs.getString("StudentReg")),
                                fullName.trim(),
                                safe(rs.getString("courseCode")),
                                String.format("%.2f", total),
                                grade,
                                gpa
                        ));
                    }
                }
            }
            tblPerformance.getItems().setAll(rows);
        } catch (SQLException e) {
            showError("Failed to load marks/grades/GPA.", e);
        }
    }

    private double calculateAverage(ResultSet rs) throws SQLException {
        String[] columns = {"quiz_1", "quiz_2", "quiz_3", "assessment_1", "assessment_2", "mid_term", "final_theory", "final_practical"};
        double sum = 0.0;
        int count = 0;
        for (String column : columns) {
            Object value = rs.getObject(column);
            if (value != null) {
                sum += ((Number) value).doubleValue();
                count++;
            }
        }
        return count == 0 ? 0.0 : sum / count;
    }

    private String toGrade(double marks) {
        if (marks >= 85) return "A+";
        if (marks >= 75) return "A";
        if (marks >= 70) return "A-";
        if (marks >= 65) return "B+";
        if (marks >= 60) return "B";
        if (marks >= 55) return "B-";
        if (marks >= 50) return "C+";
        if (marks >= 45) return "C";
        if (marks >= 40) return "C-";
        return "F";
    }

    private String currentLecturer() {
        String reg = LecturerContext.getRegistrationNo();
        return reg == null ? "" : reg.trim();
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private void showError(String message, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Database Error");
        alert.setHeaderText(null);
        alert.setContentText(message + "\n" + e.getMessage());
        alert.showAndWait();
    }

    public static class PerformanceRow {
        private final SimpleStringProperty studentReg;
        private final SimpleStringProperty studentName;
        private final SimpleStringProperty courseCode;
        private final SimpleStringProperty totalMarks;
        private final SimpleStringProperty grade;
        private final SimpleStringProperty gpa;

        public PerformanceRow(String studentReg, String studentName, String courseCode, String totalMarks, String grade, String gpa) {
            this.studentReg = new SimpleStringProperty(studentReg);
            this.studentName = new SimpleStringProperty(studentName);
            this.courseCode = new SimpleStringProperty(courseCode);
            this.totalMarks = new SimpleStringProperty(totalMarks);
            this.grade = new SimpleStringProperty(grade);
            this.gpa = new SimpleStringProperty(gpa);
        }

        public SimpleStringProperty studentRegProperty() { return studentReg; }
        public SimpleStringProperty studentNameProperty() { return studentName; }
        public SimpleStringProperty courseCodeProperty() { return courseCode; }
        public SimpleStringProperty totalMarksProperty() { return totalMarks; }
        public SimpleStringProperty gradeProperty() { return grade; }
        public SimpleStringProperty gpaProperty() { return gpa; }
    }
}
