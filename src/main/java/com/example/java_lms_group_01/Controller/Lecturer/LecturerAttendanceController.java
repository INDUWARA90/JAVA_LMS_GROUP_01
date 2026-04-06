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

public class LecturerAttendanceController {

    @FXML
    private TextField txtSearch;
    @FXML
    private TableView<AttendanceMedicalRow> tblAttendanceMedical;
    @FXML
    private TableColumn<AttendanceMedicalRow, String> colAttendanceId;
    @FXML
    private TableColumn<AttendanceMedicalRow, String> colStudentReg;
    @FXML
    private TableColumn<AttendanceMedicalRow, String> colCourseCode;
    @FXML
    private TableColumn<AttendanceMedicalRow, String> colDate;
    @FXML
    private TableColumn<AttendanceMedicalRow, String> colSessionType;
    @FXML
    private TableColumn<AttendanceMedicalRow, String> colAttendanceStatus;
    @FXML
    private TableColumn<AttendanceMedicalRow, String> colMedicalId;
    @FXML
    private TableColumn<AttendanceMedicalRow, String> colMedicalDescription;
    @FXML
    private TableColumn<AttendanceMedicalRow, String> colTechOfficerReg;

    @FXML
    public void initialize() {
        colAttendanceId.setCellValueFactory(d -> d.getValue().attendanceIdProperty());
        colStudentReg.setCellValueFactory(d -> d.getValue().studentRegProperty());
        colCourseCode.setCellValueFactory(d -> d.getValue().courseCodeProperty());
        colDate.setCellValueFactory(d -> d.getValue().dateProperty());
        colSessionType.setCellValueFactory(d -> d.getValue().sessionTypeProperty());
        colAttendanceStatus.setCellValueFactory(d -> d.getValue().attendanceStatusProperty());
        colMedicalId.setCellValueFactory(d -> d.getValue().medicalIdProperty());
        colMedicalDescription.setCellValueFactory(d -> d.getValue().medicalDescriptionProperty());
        colTechOfficerReg.setCellValueFactory(d -> d.getValue().techOfficerRegProperty());
        loadRecords(null);
    }

    @FXML
    private void searchRecords() {
        loadRecords(txtSearch.getText());
    }

    @FXML
    private void refreshRecords() {
        txtSearch.clear();
        loadRecords(null);
    }

    private void loadRecords(String keyword) {
        String safeKeyword = keyword == null ? "" : keyword.trim();
        String sql = """
                SELECT a.attendance_id, a.StudentReg, a.courseCode, a.SubmissionDate, a.session_type, a.attendance_status, a.tech_officer_reg,
                       m.medical_id, m.Description
                FROM attendance a
                INNER JOIN course c ON c.courseCode = a.courseCode
                LEFT JOIN medical m ON m.attendance_id = a.attendance_id
                WHERE c.lecturerRegistrationNo = ?
                  AND (? = '' OR a.StudentReg LIKE ? OR a.courseCode LIKE ?)
                ORDER BY a.attendance_id DESC
                """;

        List<AttendanceMedicalRow> rows = new ArrayList<>();
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
                        rows.add(new AttendanceMedicalRow(
                                String.valueOf(rs.getInt("attendance_id")),
                                safe(rs.getString("StudentReg")),
                                safe(rs.getString("courseCode")),
                                rs.getDate("SubmissionDate") == null ? "" : rs.getDate("SubmissionDate").toString(),
                                safe(rs.getString("session_type")),
                                safe(rs.getString("attendance_status")),
                                rs.getObject("medical_id") == null ? "" : String.valueOf(rs.getInt("medical_id")),
                                safe(rs.getString("Description")),
                                safe(rs.getString("tech_officer_reg"))
                        ));
                    }
                }
            }
            tblAttendanceMedical.getItems().setAll(rows);
        } catch (SQLException e) {
            showError("Failed to load attendance/medical records.", e);
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

    public static class AttendanceMedicalRow {
        private final SimpleStringProperty attendanceId;
        private final SimpleStringProperty studentReg;
        private final SimpleStringProperty courseCode;
        private final SimpleStringProperty date;
        private final SimpleStringProperty sessionType;
        private final SimpleStringProperty attendanceStatus;
        private final SimpleStringProperty medicalId;
        private final SimpleStringProperty medicalDescription;
        private final SimpleStringProperty techOfficerReg;

        public AttendanceMedicalRow(String attendanceId, String studentReg, String courseCode, String date, String sessionType, String attendanceStatus, String medicalId, String medicalDescription, String techOfficerReg) {
            this.attendanceId = new SimpleStringProperty(attendanceId);
            this.studentReg = new SimpleStringProperty(studentReg);
            this.courseCode = new SimpleStringProperty(courseCode);
            this.date = new SimpleStringProperty(date);
            this.sessionType = new SimpleStringProperty(sessionType);
            this.attendanceStatus = new SimpleStringProperty(attendanceStatus);
            this.medicalId = new SimpleStringProperty(medicalId);
            this.medicalDescription = new SimpleStringProperty(medicalDescription);
            this.techOfficerReg = new SimpleStringProperty(techOfficerReg);
        }

        public SimpleStringProperty attendanceIdProperty() { return attendanceId; }
        public SimpleStringProperty studentRegProperty() { return studentReg; }
        public SimpleStringProperty courseCodeProperty() { return courseCode; }
        public SimpleStringProperty dateProperty() { return date; }
        public SimpleStringProperty sessionTypeProperty() { return sessionType; }
        public SimpleStringProperty attendanceStatusProperty() { return attendanceStatus; }
        public SimpleStringProperty medicalIdProperty() { return medicalId; }
        public SimpleStringProperty medicalDescriptionProperty() { return medicalDescription; }
        public SimpleStringProperty techOfficerRegProperty() { return techOfficerReg; }
    }
}
