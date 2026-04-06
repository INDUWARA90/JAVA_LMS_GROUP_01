package com.example.java_lms_group_01.Controller.Student;

import com.example.java_lms_group_01.util.DBConnection;
import com.example.java_lms_group_01.util.StudentContext;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentGradePageController {

    @FXML
    private TableView<GradeRow> tblGrades;
    @FXML
    private TableColumn<GradeRow, String> colCourseCode;
    @FXML
    private TableColumn<GradeRow, String> colQuiz1;
    @FXML
    private TableColumn<GradeRow, String> colQuiz2;
    @FXML
    private TableColumn<GradeRow, String> colQuiz3;
    @FXML
    private TableColumn<GradeRow, String> colAssessment1;
    @FXML
    private TableColumn<GradeRow, String> colAssessment2;
    @FXML
    private TableColumn<GradeRow, String> colMidTerm;
    @FXML
    private TableColumn<GradeRow, String> colFinalTheory;
    @FXML
    private TableColumn<GradeRow, String> colFinalPractical;
    @FXML
    private TableColumn<GradeRow, String> colTotal;
    @FXML
    private TableColumn<GradeRow, String> colGrade;
    @FXML
    private Label lblGpa;

    @FXML
    public void initialize() {
        colCourseCode.setCellValueFactory(d -> d.getValue().courseCodeProperty());
        colQuiz1.setCellValueFactory(d -> d.getValue().quiz1Property());
        colQuiz2.setCellValueFactory(d -> d.getValue().quiz2Property());
        colQuiz3.setCellValueFactory(d -> d.getValue().quiz3Property());
        colAssessment1.setCellValueFactory(d -> d.getValue().assessment1Property());
        colAssessment2.setCellValueFactory(d -> d.getValue().assessment2Property());
        colMidTerm.setCellValueFactory(d -> d.getValue().midTermProperty());
        colFinalTheory.setCellValueFactory(d -> d.getValue().finalTheoryProperty());
        colFinalPractical.setCellValueFactory(d -> d.getValue().finalPracticalProperty());
        colTotal.setCellValueFactory(d -> d.getValue().totalProperty());
        colGrade.setCellValueFactory(d -> d.getValue().gradeProperty());
        loadGradesAndGpa();
    }

    private void loadGradesAndGpa() {
        String regNo = StudentContext.getRegistrationNo();
        if (regNo == null || regNo.isBlank()) {
            return;
        }

        String marksSql = """
                SELECT courseCode, quiz_1, quiz_2, quiz_3, assessment_1, assessment_2, mid_term, final_theory, final_practical
                FROM marks
                WHERE StudentReg = ?
                ORDER BY courseCode
                """;

        List<GradeRow> rows = new ArrayList<>();
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            try (PreparedStatement statement = connection.prepareStatement(marksSql)) {
                statement.setString(1, regNo);
                try (ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) {
                        double total = calculateAverage(rs);
                        rows.add(new GradeRow(
                                safe(rs.getString("courseCode")),
                                safeDecimal(rs.getObject("quiz_1")),
                                safeDecimal(rs.getObject("quiz_2")),
                                safeDecimal(rs.getObject("quiz_3")),
                                safeDecimal(rs.getObject("assessment_1")),
                                safeDecimal(rs.getObject("assessment_2")),
                                safeDecimal(rs.getObject("mid_term")),
                                safeDecimal(rs.getObject("final_theory")),
                                safeDecimal(rs.getObject("final_practical")),
                                String.format("%.2f", total),
                                toGrade(total)
                        ));
                    }
                }
            }

            tblGrades.getItems().setAll(rows);

            try (PreparedStatement gpaStmt = connection.prepareStatement("SELECT GPA FROM student WHERE registrationNo = ?")) {
                gpaStmt.setString(1, regNo);
                try (ResultSet rs = gpaStmt.executeQuery()) {
                    if (rs.next() && rs.getObject("GPA") != null) {
                        lblGpa.setText("GPA : " + String.format("%.2f", ((Number) rs.getObject("GPA")).doubleValue()));
                    } else {
                        lblGpa.setText("GPA : 0.00");
                    }
                }
            }
        } catch (SQLException e) {
            showError("Failed to load grades and GPA.", e);
        }
    }

    private double calculateAverage(ResultSet rs) throws SQLException {
        String[] fields = {"quiz_1", "quiz_2", "quiz_3", "assessment_1", "assessment_2", "mid_term", "final_theory", "final_practical"};
        double sum = 0.0;
        int count = 0;
        for (String field : fields) {
            Object value = rs.getObject(field);
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

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String safeDecimal(Object value) {
        if (value == null) {
            return "";
        }
        return String.format("%.2f", ((Number) value).doubleValue());
    }

    private void showError(String message, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Database Error");
        alert.setHeaderText(null);
        alert.setContentText(message + "\n" + e.getMessage());
        alert.showAndWait();
    }

    public static class GradeRow {
        private final SimpleStringProperty courseCode;
        private final SimpleStringProperty quiz1;
        private final SimpleStringProperty quiz2;
        private final SimpleStringProperty quiz3;
        private final SimpleStringProperty assessment1;
        private final SimpleStringProperty assessment2;
        private final SimpleStringProperty midTerm;
        private final SimpleStringProperty finalTheory;
        private final SimpleStringProperty finalPractical;
        private final SimpleStringProperty total;
        private final SimpleStringProperty grade;

        public GradeRow(String courseCode, String quiz1, String quiz2, String quiz3, String assessment1, String assessment2, String midTerm, String finalTheory, String finalPractical, String total, String grade) {
            this.courseCode = new SimpleStringProperty(courseCode);
            this.quiz1 = new SimpleStringProperty(quiz1);
            this.quiz2 = new SimpleStringProperty(quiz2);
            this.quiz3 = new SimpleStringProperty(quiz3);
            this.assessment1 = new SimpleStringProperty(assessment1);
            this.assessment2 = new SimpleStringProperty(assessment2);
            this.midTerm = new SimpleStringProperty(midTerm);
            this.finalTheory = new SimpleStringProperty(finalTheory);
            this.finalPractical = new SimpleStringProperty(finalPractical);
            this.total = new SimpleStringProperty(total);
            this.grade = new SimpleStringProperty(grade);
        }

        public SimpleStringProperty courseCodeProperty() { return courseCode; }
        public SimpleStringProperty quiz1Property() { return quiz1; }
        public SimpleStringProperty quiz2Property() { return quiz2; }
        public SimpleStringProperty quiz3Property() { return quiz3; }
        public SimpleStringProperty assessment1Property() { return assessment1; }
        public SimpleStringProperty assessment2Property() { return assessment2; }
        public SimpleStringProperty midTermProperty() { return midTerm; }
        public SimpleStringProperty finalTheoryProperty() { return finalTheory; }
        public SimpleStringProperty finalPracticalProperty() { return finalPractical; }
        public SimpleStringProperty totalProperty() { return total; }
        public SimpleStringProperty gradeProperty() { return grade; }
    }
}
