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

public class LecturerMarksController {

    @FXML
    private TextField txtStudentReg;
    @FXML
    private TextField txtCourseCode;
    @FXML
    private TextField txtQuiz1;
    @FXML
    private TextField txtQuiz2;
    @FXML
    private TextField txtQuiz3;
    @FXML
    private TextField txtAssessment1;
    @FXML
    private TextField txtAssessment2;
    @FXML
    private TextField txtMidTerm;
    @FXML
    private TextField txtFinalTheory;
    @FXML
    private TextField txtFinalPractical;
    @FXML
    private TextField txtSearch;
    @FXML
    private TableView<MarksRow> tblMarks;
    @FXML
    private TableColumn<MarksRow, String> colMarkId;
    @FXML
    private TableColumn<MarksRow, String> colStudentReg;
    @FXML
    private TableColumn<MarksRow, String> colCourseCode;
    @FXML
    private TableColumn<MarksRow, String> colQuiz1;
    @FXML
    private TableColumn<MarksRow, String> colQuiz2;
    @FXML
    private TableColumn<MarksRow, String> colQuiz3;
    @FXML
    private TableColumn<MarksRow, String> colA1;
    @FXML
    private TableColumn<MarksRow, String> colA2;
    @FXML
    private TableColumn<MarksRow, String> colMid;
    @FXML
    private TableColumn<MarksRow, String> colFinalTheory;
    @FXML
    private TableColumn<MarksRow, String> colFinalPractical;

    @FXML
    public void initialize() {
        colMarkId.setCellValueFactory(d -> d.getValue().markIdProperty());
        colStudentReg.setCellValueFactory(d -> d.getValue().studentRegProperty());
        colCourseCode.setCellValueFactory(d -> d.getValue().courseCodeProperty());
        colQuiz1.setCellValueFactory(d -> d.getValue().quiz1Property());
        colQuiz2.setCellValueFactory(d -> d.getValue().quiz2Property());
        colQuiz3.setCellValueFactory(d -> d.getValue().quiz3Property());
        colA1.setCellValueFactory(d -> d.getValue().assessment1Property());
        colA2.setCellValueFactory(d -> d.getValue().assessment2Property());
        colMid.setCellValueFactory(d -> d.getValue().midTermProperty());
        colFinalTheory.setCellValueFactory(d -> d.getValue().finalTheoryProperty());
        colFinalPractical.setCellValueFactory(d -> d.getValue().finalPracticalProperty());
        loadMarks(null);

        tblMarks.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, row) -> {
            if (row == null) {
                return;
            }
            txtStudentReg.setText(row.getStudentReg());
            txtCourseCode.setText(row.getCourseCode());
            txtQuiz1.setText(row.getQuiz1());
            txtQuiz2.setText(row.getQuiz2());
            txtQuiz3.setText(row.getQuiz3());
            txtAssessment1.setText(row.getAssessment1());
            txtAssessment2.setText(row.getAssessment2());
            txtMidTerm.setText(row.getMidTerm());
            txtFinalTheory.setText(row.getFinalTheory());
            txtFinalPractical.setText(row.getFinalPractical());
        });
    }

    @FXML
    private void addMarks() {
        if (!validForm()) {
            return;
        }
        String sql = """
                INSERT INTO marks (
                  LectureReg, StudentReg, courseCode, quiz_1, quiz_2, quiz_3,
                  assessment_1, assessment_2, mid_term, final_theory, final_practical
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, currentLecturer());
                statement.setString(2, value(txtStudentReg));
                statement.setString(3, value(txtCourseCode));
                setDecimal(statement, 4, txtQuiz1);
                setDecimal(statement, 5, txtQuiz2);
                setDecimal(statement, 6, txtQuiz3);
                setDecimal(statement, 7, txtAssessment1);
                setDecimal(statement, 8, txtAssessment2);
                setDecimal(statement, 9, txtMidTerm);
                setDecimal(statement, 10, txtFinalTheory);
                setDecimal(statement, 11, txtFinalPractical);
                statement.executeUpdate();
            }
            loadMarks(txtSearch.getText());
            clearForm();
        } catch (Exception e) {
            showError("Failed to add marks.", e);
        }
    }

    @FXML
    private void updateMarks() {
        MarksRow selected = tblMarks.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarn("Select a marks record to update.");
            return;
        }
        if (!validForm()) {
            return;
        }
        String sql = """
                UPDATE marks SET
                  StudentReg = ?, courseCode = ?, quiz_1 = ?, quiz_2 = ?, quiz_3 = ?,
                  assessment_1 = ?, assessment_2 = ?, mid_term = ?, final_theory = ?, final_practical = ?
                WHERE mark_id = ? AND LectureReg = ?
                """;
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, value(txtStudentReg));
                statement.setString(2, value(txtCourseCode));
                setDecimal(statement, 3, txtQuiz1);
                setDecimal(statement, 4, txtQuiz2);
                setDecimal(statement, 5, txtQuiz3);
                setDecimal(statement, 6, txtAssessment1);
                setDecimal(statement, 7, txtAssessment2);
                setDecimal(statement, 8, txtMidTerm);
                setDecimal(statement, 9, txtFinalTheory);
                setDecimal(statement, 10, txtFinalPractical);
                statement.setInt(11, Integer.parseInt(selected.getMarkId()));
                statement.setString(12, currentLecturer());
                statement.executeUpdate();
            }
            loadMarks(txtSearch.getText());
        } catch (Exception e) {
            showError("Failed to update marks.", e);
        }
    }

    @FXML
    private void deleteMarks() {
        MarksRow selected = tblMarks.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarn("Select a marks record to delete.");
            return;
        }
        String sql = "DELETE FROM marks WHERE mark_id = ? AND LectureReg = ?";
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, Integer.parseInt(selected.getMarkId()));
                statement.setString(2, currentLecturer());
                statement.executeUpdate();
            }
            loadMarks(txtSearch.getText());
            clearForm();
        } catch (Exception e) {
            showError("Failed to delete marks.", e);
        }
    }

    @FXML
    private void searchMarks() {
        loadMarks(txtSearch.getText());
    }

    @FXML
    private void refreshMarks() {
        txtSearch.clear();
        loadMarks(null);
    }

    @FXML
    private void clearForm() {
        txtStudentReg.clear();
        txtCourseCode.clear();
        txtQuiz1.clear();
        txtQuiz2.clear();
        txtQuiz3.clear();
        txtAssessment1.clear();
        txtAssessment2.clear();
        txtMidTerm.clear();
        txtFinalTheory.clear();
        txtFinalPractical.clear();
        tblMarks.getSelectionModel().clearSelection();
    }

    private void loadMarks(String keyword) {
        String safeKeyword = keyword == null ? "" : keyword.trim();
        String sql = """
                SELECT mark_id, StudentReg, courseCode, quiz_1, quiz_2, quiz_3, assessment_1, assessment_2, mid_term, final_theory, final_practical
                FROM marks
                WHERE LectureReg = ? AND (? = '' OR StudentReg LIKE ? OR courseCode LIKE ?)
                ORDER BY mark_id DESC
                """;

        List<MarksRow> rows = new ArrayList<>();
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                String pattern = "%" + safeKeyword + "%";
                statement.setString(1, currentLecturer());
                statement.setString(2, safeKeyword);
                statement.setString(3, pattern);
                statement.setString(4, pattern);
                try (ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) {
                        rows.add(new MarksRow(
                                String.valueOf(rs.getInt("mark_id")),
                                safe(rs.getString("StudentReg")),
                                safe(rs.getString("courseCode")),
                                safeDecimal(rs.getObject("quiz_1")),
                                safeDecimal(rs.getObject("quiz_2")),
                                safeDecimal(rs.getObject("quiz_3")),
                                safeDecimal(rs.getObject("assessment_1")),
                                safeDecimal(rs.getObject("assessment_2")),
                                safeDecimal(rs.getObject("mid_term")),
                                safeDecimal(rs.getObject("final_theory")),
                                safeDecimal(rs.getObject("final_practical"))
                        ));
                    }
                }
            }
            tblMarks.getItems().setAll(rows);
        } catch (SQLException e) {
            showError("Failed to load marks.", e);
        }
    }

    private void setDecimal(PreparedStatement statement, int index, TextField textField) throws SQLException {
        String value = value(textField);
        if (value.isBlank()) {
            statement.setNull(index, java.sql.Types.DECIMAL);
            return;
        }
        try {
            double numeric = Double.parseDouble(value);
            if (numeric < 0 || numeric > 100) {
                throw new IllegalArgumentException("Exam marks must be between 0 and 100.");
            }
            statement.setDouble(index, numeric);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Marks must be numeric.");
        }
    }

    private boolean validForm() {
        if (value(txtStudentReg).isBlank() || value(txtCourseCode).isBlank()) {
            showWarn("Student registration and course code are required.");
            return false;
        }
        try {
            validateDecimal(txtQuiz1);
            validateDecimal(txtQuiz2);
            validateDecimal(txtQuiz3);
            validateDecimal(txtAssessment1);
            validateDecimal(txtAssessment2);
            validateDecimal(txtMidTerm);
            validateDecimal(txtFinalTheory);
            validateDecimal(txtFinalPractical);
        } catch (IllegalArgumentException e) {
            showWarn(e.getMessage());
            return false;
        }
        return true;
    }

    private void validateDecimal(TextField textField) {
        String value = value(textField);
        if (value.isBlank()) {
            return;
        }
        try {
            double numeric = Double.parseDouble(value);
            if (numeric < 0 || numeric > 100) {
                throw new IllegalArgumentException("Exam marks must be between 0 and 100.");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Marks must be numeric.");
        }
    }

    private String currentLecturer() {
        String reg = LecturerContext.getRegistrationNo();
        return reg == null ? "" : reg.trim();
    }

    private String value(TextField textField) {
        return textField.getText() == null ? "" : textField.getText().trim();
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

    private void showWarn(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Validation");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Database Error");
        alert.setHeaderText(null);
        alert.setContentText(message + "\n" + e.getMessage());
        alert.showAndWait();
    }

    public static class MarksRow {
        private final SimpleStringProperty markId;
        private final SimpleStringProperty studentReg;
        private final SimpleStringProperty courseCode;
        private final SimpleStringProperty quiz1;
        private final SimpleStringProperty quiz2;
        private final SimpleStringProperty quiz3;
        private final SimpleStringProperty assessment1;
        private final SimpleStringProperty assessment2;
        private final SimpleStringProperty midTerm;
        private final SimpleStringProperty finalTheory;
        private final SimpleStringProperty finalPractical;

        public MarksRow(String markId, String studentReg, String courseCode, String quiz1, String quiz2, String quiz3, String assessment1, String assessment2, String midTerm, String finalTheory, String finalPractical) {
            this.markId = new SimpleStringProperty(markId);
            this.studentReg = new SimpleStringProperty(studentReg);
            this.courseCode = new SimpleStringProperty(courseCode);
            this.quiz1 = new SimpleStringProperty(quiz1);
            this.quiz2 = new SimpleStringProperty(quiz2);
            this.quiz3 = new SimpleStringProperty(quiz3);
            this.assessment1 = new SimpleStringProperty(assessment1);
            this.assessment2 = new SimpleStringProperty(assessment2);
            this.midTerm = new SimpleStringProperty(midTerm);
            this.finalTheory = new SimpleStringProperty(finalTheory);
            this.finalPractical = new SimpleStringProperty(finalPractical);
        }

        public SimpleStringProperty markIdProperty() { return markId; }
        public SimpleStringProperty studentRegProperty() { return studentReg; }
        public SimpleStringProperty courseCodeProperty() { return courseCode; }
        public SimpleStringProperty quiz1Property() { return quiz1; }
        public SimpleStringProperty quiz2Property() { return quiz2; }
        public SimpleStringProperty quiz3Property() { return quiz3; }
        public SimpleStringProperty assessment1Property() { return assessment1; }
        public SimpleStringProperty assessment2Property() { return assessment2; }
        public SimpleStringProperty midTermProperty() { return midTerm; }
        public SimpleStringProperty finalTheoryProperty() { return finalTheory; }
        public SimpleStringProperty finalPracticalProperty() { return finalPractical; }

        public String getMarkId() { return markId.get(); }
        public String getStudentReg() { return studentReg.get(); }
        public String getCourseCode() { return courseCode.get(); }
        public String getQuiz1() { return quiz1.get(); }
        public String getQuiz2() { return quiz2.get(); }
        public String getQuiz3() { return quiz3.get(); }
        public String getAssessment1() { return assessment1.get(); }
        public String getAssessment2() { return assessment2.get(); }
        public String getMidTerm() { return midTerm.get(); }
        public String getFinalTheory() { return finalTheory.get(); }
        public String getFinalPractical() { return finalPractical.get(); }
    }
}
