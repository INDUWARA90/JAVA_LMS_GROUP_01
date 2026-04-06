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

public class StudentAttendancePageController {

    @FXML
    private TableView<AttendanceRow> tblAttendance;
    @FXML
    private TableColumn<AttendanceRow, String> colAttendanceId;
    @FXML
    private TableColumn<AttendanceRow, String> colStudentReg;
    @FXML
    private TableColumn<AttendanceRow, String> colCourseCode;
    @FXML
    private TableColumn<AttendanceRow, String> colSubmissionDate;
    @FXML
    private TableColumn<AttendanceRow, String> colSessionType;
    @FXML
    private TableColumn<AttendanceRow, String> colAttendanceStatus;
    @FXML
    private TableColumn<AttendanceRow, String> colTechOfficerReg;

    @FXML
    public void initialize() {
        colAttendanceId.setCellValueFactory(d -> d.getValue().attendanceIdProperty());
        colStudentReg.setCellValueFactory(d -> d.getValue().studentRegProperty());
        colCourseCode.setCellValueFactory(d -> d.getValue().courseCodeProperty());
        colSubmissionDate.setCellValueFactory(d -> d.getValue().submissionDateProperty());
        colSessionType.setCellValueFactory(d -> d.getValue().sessionTypeProperty());
        colAttendanceStatus.setCellValueFactory(d -> d.getValue().attendanceStatusProperty());
        colTechOfficerReg.setCellValueFactory(d -> d.getValue().techOfficerRegProperty());
        loadAttendance();
    }

    private void loadAttendance() {
        String regNo = StudentContext.getRegistrationNo();
        if (regNo == null || regNo.isBlank()) {
            return;
        }

        String sql = """
                SELECT attendance_id, StudentReg, courseCode, SubmissionDate, session_type, attendance_status, tech_officer_reg
                FROM attendance
                WHERE StudentReg = ?
                ORDER BY SubmissionDate DESC, attendance_id DESC
                """;

        List<AttendanceRow> rows = new ArrayList<>();
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, regNo);
                try (ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) {
                        rows.add(new AttendanceRow(
                                String.valueOf(rs.getInt("attendance_id")),
                                safe(rs.getString("StudentReg")),
                                safe(rs.getString("courseCode")),
                                rs.getDate("SubmissionDate") == null ? "" : rs.getDate("SubmissionDate").toString(),
                                safe(rs.getString("session_type")),
                                safe(rs.getString("attendance_status")),
                                safe(rs.getString("tech_officer_reg"))
                        ));
                    }
                }
            }
            tblAttendance.getItems().setAll(rows);
        } catch (SQLException e) {
            showError("Failed to load attendance details.", e);
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

    public static class AttendanceRow {
        private final SimpleStringProperty attendanceId;
        private final SimpleStringProperty studentReg;
        private final SimpleStringProperty courseCode;
        private final SimpleStringProperty submissionDate;
        private final SimpleStringProperty sessionType;
        private final SimpleStringProperty attendanceStatus;
        private final SimpleStringProperty techOfficerReg;

        public AttendanceRow(String attendanceId, String studentReg, String courseCode, String submissionDate, String sessionType, String attendanceStatus, String techOfficerReg) {
            this.attendanceId = new SimpleStringProperty(attendanceId);
            this.studentReg = new SimpleStringProperty(studentReg);
            this.courseCode = new SimpleStringProperty(courseCode);
            this.submissionDate = new SimpleStringProperty(submissionDate);
            this.sessionType = new SimpleStringProperty(sessionType);
            this.attendanceStatus = new SimpleStringProperty(attendanceStatus);
            this.techOfficerReg = new SimpleStringProperty(techOfficerReg);
        }

        public SimpleStringProperty attendanceIdProperty() { return attendanceId; }
        public SimpleStringProperty studentRegProperty() { return studentReg; }
        public SimpleStringProperty courseCodeProperty() { return courseCode; }
        public SimpleStringProperty submissionDateProperty() { return submissionDate; }
        public SimpleStringProperty sessionTypeProperty() { return sessionType; }
        public SimpleStringProperty attendanceStatusProperty() { return attendanceStatus; }
        public SimpleStringProperty techOfficerRegProperty() { return techOfficerReg; }
    }
}
