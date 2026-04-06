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

public class LecturerStudentsController {

    @FXML
    private TextField txtSearch;
    @FXML
    private TableView<StudentRow> tblStudents;
    @FXML
    private TableColumn<StudentRow, String> colRegNo;
    @FXML
    private TableColumn<StudentRow, String> colName;
    @FXML
    private TableColumn<StudentRow, String> colEmail;
    @FXML
    private TableColumn<StudentRow, String> colPhone;
    @FXML
    private TableColumn<StudentRow, String> colDepartment;
    @FXML
    private TableColumn<StudentRow, String> colStatus;
    @FXML
    private TableColumn<StudentRow, String> colGpa;

    @FXML
    public void initialize() {
        colRegNo.setCellValueFactory(d -> d.getValue().regNoProperty());
        colName.setCellValueFactory(d -> d.getValue().nameProperty());
        colEmail.setCellValueFactory(d -> d.getValue().emailProperty());
        colPhone.setCellValueFactory(d -> d.getValue().phoneProperty());
        colDepartment.setCellValueFactory(d -> d.getValue().departmentProperty());
        colStatus.setCellValueFactory(d -> d.getValue().statusProperty());
        colGpa.setCellValueFactory(d -> d.getValue().gpaProperty());
        loadStudents(null);
    }

    @FXML
    private void searchStudents() {
        loadStudents(txtSearch.getText());
    }

    @FXML
    private void refreshStudents() {
        txtSearch.clear();
        loadStudents(null);
    }

    private void loadStudents(String keyword) {
        String safeKeyword = keyword == null ? "" : keyword.trim();
        String sql = """
                SELECT DISTINCT s.registrationNo, u.firstName, u.lastName, u.email, u.phoneNumber, s.department, s.status, s.GPA
                FROM student s
                INNER JOIN users u ON u.user_id = s.registrationNo
                INNER JOIN enrollment e ON e.studentReg = s.registrationNo
                INNER JOIN course c ON c.courseCode = e.courseCode
                WHERE c.lecturerRegistrationNo = ?
                  AND (? = '' OR s.registrationNo LIKE ? OR u.firstName LIKE ? OR u.lastName LIKE ? OR s.department LIKE ?)
                ORDER BY s.registrationNo
                """;

        List<StudentRow> rows = new ArrayList<>();
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
                        String fullName = safe(rs.getString("firstName")) + " " + safe(rs.getString("lastName"));
                        rows.add(new StudentRow(
                                safe(rs.getString("registrationNo")),
                                fullName.trim(),
                                safe(rs.getString("email")),
                                safe(rs.getString("phoneNumber")),
                                safe(rs.getString("department")),
                                safe(rs.getString("status")),
                                rs.getObject("GPA") == null ? "" : String.format("%.2f", ((Number) rs.getObject("GPA")).doubleValue())
                        ));
                    }
                }
            }
            tblStudents.getItems().setAll(rows);
        } catch (SQLException e) {
            showError("Failed to load undergraduate details.", e);
        }
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

    public static class StudentRow {
        private final SimpleStringProperty regNo;
        private final SimpleStringProperty name;
        private final SimpleStringProperty email;
        private final SimpleStringProperty phone;
        private final SimpleStringProperty department;
        private final SimpleStringProperty status;
        private final SimpleStringProperty gpa;

        public StudentRow(String regNo, String name, String email, String phone, String department, String status, String gpa) {
            this.regNo = new SimpleStringProperty(regNo);
            this.name = new SimpleStringProperty(name);
            this.email = new SimpleStringProperty(email);
            this.phone = new SimpleStringProperty(phone);
            this.department = new SimpleStringProperty(department);
            this.status = new SimpleStringProperty(status);
            this.gpa = new SimpleStringProperty(gpa);
        }

        public SimpleStringProperty regNoProperty() { return regNo; }
        public SimpleStringProperty nameProperty() { return name; }
        public SimpleStringProperty emailProperty() { return email; }
        public SimpleStringProperty phoneProperty() { return phone; }
        public SimpleStringProperty departmentProperty() { return department; }
        public SimpleStringProperty statusProperty() { return status; }
        public SimpleStringProperty gpaProperty() { return gpa; }
    }
}
