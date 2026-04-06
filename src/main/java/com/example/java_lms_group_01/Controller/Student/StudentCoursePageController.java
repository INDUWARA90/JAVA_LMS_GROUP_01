package com.example.java_lms_group_01.Controller.Student;

import com.example.java_lms_group_01.util.DBConnection;
import com.example.java_lms_group_01.util.StudentContext;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentCoursePageController {

    @FXML
    private TableView<CourseRow> tblCourses;
    @FXML
    private TableColumn<CourseRow, String> colCourseCode;
    @FXML
    private TableColumn<CourseRow, String> colName;
    @FXML
    private TableColumn<CourseRow, String> colLecturer;
    @FXML
    private TableColumn<CourseRow, String> colDepartment;
    @FXML
    private TableColumn<CourseRow, String> colSemester;
    @FXML
    private TableColumn<CourseRow, String> colCredit;
    @FXML
    private TableColumn<CourseRow, String> colType;
    @FXML
    private TableColumn<CourseRow, String> colEnrollmentStatus;

    @FXML
    public void initialize() {
        colCourseCode.setCellValueFactory(d -> d.getValue().courseCodeProperty());
        colName.setCellValueFactory(d -> d.getValue().nameProperty());
        colLecturer.setCellValueFactory(d -> d.getValue().lecturerProperty());
        colDepartment.setCellValueFactory(d -> d.getValue().departmentProperty());
        colSemester.setCellValueFactory(d -> d.getValue().semesterProperty());
        colCredit.setCellValueFactory(d -> d.getValue().creditProperty());
        colType.setCellValueFactory(d -> d.getValue().typeProperty());
        colEnrollmentStatus.setCellValueFactory(d -> d.getValue().enrollmentStatusProperty());
        loadCourses();
    }

    private void loadCourses() {
        String regNo = StudentContext.getRegistrationNo();
        if (regNo == null || regNo.isBlank()) {
            return;
        }

        String sql = """
                SELECT c.courseCode, c.name, c.lecturerRegistrationNo, c.department, c.semester, c.credit, c.course_type, e.status
                FROM enrollment e
                INNER JOIN course c ON c.courseCode = e.courseCode
                WHERE e.studentReg = ?
                ORDER BY c.courseCode
                """;

        List<CourseRow> rows = new ArrayList<>();
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, regNo);
                try (ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) {
                        rows.add(new CourseRow(
                                safe(rs.getString("courseCode")),
                                safe(rs.getString("name")),
                                safe(rs.getString("lecturerRegistrationNo")),
                                safe(rs.getString("department")),
                                safe(rs.getString("semester")),
                                String.valueOf(rs.getInt("credit")),
                                safe(rs.getString("course_type")),
                                safe(rs.getString("status"))
                        ));
                    }
                }
            }
            tblCourses.getItems().setAll(rows);
        } catch (SQLException e) {
            showError("Failed to load course details.", e);
        }
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

    public static class CourseRow {
        private final SimpleStringProperty courseCode;
        private final SimpleStringProperty name;
        private final SimpleStringProperty lecturer;
        private final SimpleStringProperty department;
        private final SimpleStringProperty semester;
        private final SimpleStringProperty credit;
        private final SimpleStringProperty type;
        private final SimpleStringProperty enrollmentStatus;

        public CourseRow(String courseCode, String name, String lecturer, String department, String semester, String credit, String type, String enrollmentStatus) {
            this.courseCode = new SimpleStringProperty(courseCode);
            this.name = new SimpleStringProperty(name);
            this.lecturer = new SimpleStringProperty(lecturer);
            this.department = new SimpleStringProperty(department);
            this.semester = new SimpleStringProperty(semester);
            this.credit = new SimpleStringProperty(credit);
            this.type = new SimpleStringProperty(type);
            this.enrollmentStatus = new SimpleStringProperty(enrollmentStatus);
        }

        public SimpleStringProperty courseCodeProperty() { return courseCode; }
        public SimpleStringProperty nameProperty() { return name; }
        public SimpleStringProperty lecturerProperty() { return lecturer; }
        public SimpleStringProperty departmentProperty() { return department; }
        public SimpleStringProperty semesterProperty() { return semester; }
        public SimpleStringProperty creditProperty() { return credit; }
        public SimpleStringProperty typeProperty() { return type; }
        public SimpleStringProperty enrollmentStatusProperty() { return enrollmentStatus; }
    }
}
