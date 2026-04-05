package com.example.java_lms_group_01.Controller.TechnicalOfficer;

import com.example.java_lms_group_01.util.DBConnection;
import com.example.java_lms_group_01.util.TechnicalOfficerContext;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TechnicalOfficerAttendanceController {

    @FXML
    private TextField txtStudentRegNo;
    @FXML
    private TextField txtCourseCode;
    @FXML
    private DatePicker dpAttendanceDate;
    @FXML
    private ComboBox<String> cmbSessionType;
    @FXML
    private ComboBox<String> cmbStatus;
    @FXML
    private TextField txtSearch;
    @FXML
    private TableView<AttendanceRow> tblAttendance;
    @FXML
    private TableColumn<AttendanceRow, String> colAttendanceId;
    @FXML
    private TableColumn<AttendanceRow, String> colStudentRegNo;
    @FXML
    private TableColumn<AttendanceRow, String> colCourseCode;
    @FXML
    private TableColumn<AttendanceRow, String> colDate;
    @FXML
    private TableColumn<AttendanceRow, String> colSessionType;
    @FXML
    private TableColumn<AttendanceRow, String> colStatus;
    @FXML
    private TableColumn<AttendanceRow, String> colTechOfficerReg;

    @FXML
    public void initialize() {
        cmbSessionType.setItems(FXCollections.observableArrayList("theory", "practical"));
        cmbStatus.setItems(FXCollections.observableArrayList("present", "absent", "medical"));

        colAttendanceId.setCellValueFactory(d -> d.getValue().attendanceIdProperty());
        colStudentRegNo.setCellValueFactory(d -> d.getValue().studentRegNoProperty());
        colCourseCode.setCellValueFactory(d -> d.getValue().courseCodeProperty());
        colDate.setCellValueFactory(d -> d.getValue().dateProperty());
        colSessionType.setCellValueFactory(d -> d.getValue().sessionTypeProperty());
        colStatus.setCellValueFactory(d -> d.getValue().statusProperty());
        colTechOfficerReg.setCellValueFactory(d -> d.getValue().techOfficerRegProperty());

        loadAttendance(null);

        tblAttendance.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, row) -> {
            if (row == null) {
                return;
            }
            txtStudentRegNo.setText(row.getStudentRegNo());
            txtCourseCode.setText(row.getCourseCode());
            dpAttendanceDate.setValue(row.getDate().isBlank() ? null : LocalDate.parse(row.getDate()));
            cmbSessionType.setValue(row.getSessionType());
            cmbStatus.setValue(row.getStatus());
        });
    }

    @FXML
    private void addRecord(ActionEvent event) {
        if (!validForm()) {
            return;
        }
        String sql = "INSERT INTO attendance (StudentReg, courseCode, tech_officer_reg, SubmissionDate, session_type, attendance_status) VALUES (?, ?, ?, ?, ?, ?)";

        try {
            Connection connection = DBConnection.getInstance().getConnection();
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, value(txtStudentRegNo));
                statement.setString(2, value(txtCourseCode));
                statement.setString(3, currentTechOfficerReg());
                statement.setDate(4, Date.valueOf(dpAttendanceDate.getValue()));
                statement.setString(5, cmbSessionType.getValue());
                statement.setString(6, cmbStatus.getValue());
                statement.executeUpdate();
            }
            loadAttendance(txtSearch.getText());
            clearForm(event);
        } catch (Exception e) {
            showError("Failed to add attendance record.", e);
        }
    }

    @FXML
    private void updateRecord(ActionEvent event) {
        AttendanceRow selected = tblAttendance.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarn("Select a record to update.");
            return;
        }
        if (!validForm()) {
            return;
        }
        String sql = "UPDATE attendance SET StudentReg = ?, courseCode = ?, SubmissionDate = ?, session_type = ?, attendance_status = ?, tech_officer_reg = ? WHERE attendance_id = ?";
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, value(txtStudentRegNo));
                statement.setString(2, value(txtCourseCode));
                statement.setDate(3, Date.valueOf(dpAttendanceDate.getValue()));
                statement.setString(4, cmbSessionType.getValue());
                statement.setString(5, cmbStatus.getValue());
                statement.setString(6, currentTechOfficerReg());
                statement.setInt(7, Integer.parseInt(selected.getAttendanceId()));
                statement.executeUpdate();
            }
            loadAttendance(txtSearch.getText());
        } catch (Exception e) {
            showError("Failed to update attendance record.", e);
        }
    }

    @FXML
    private void deleteRecord(ActionEvent event) {
        AttendanceRow selected = tblAttendance.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarn("Select a record to delete.");
            return;
        }
        String sql = "DELETE FROM attendance WHERE attendance_id = ?";
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, Integer.parseInt(selected.getAttendanceId()));
                statement.executeUpdate();
            }
            loadAttendance(txtSearch.getText());
            clearForm(event);
        } catch (Exception e) {
            showError("Failed to delete attendance record.", e);
        }
    }

    @FXML
    private void clearForm(ActionEvent event) {
        txtStudentRegNo.clear();
        txtCourseCode.clear();
        dpAttendanceDate.setValue(null);
        cmbSessionType.setValue(null);
        cmbStatus.setValue(null);
        tblAttendance.getSelectionModel().clearSelection();
    }

    @FXML
    private void searchRecords(ActionEvent event) {
        loadAttendance(txtSearch.getText());
    }

    @FXML
    private void refreshRecords(ActionEvent event) {
        txtSearch.clear();
        loadAttendance(null);
    }

    private boolean validForm() {
        if (value(txtStudentRegNo).isBlank() || value(txtCourseCode).isBlank() || dpAttendanceDate.getValue() == null
                || cmbSessionType.getValue() == null || cmbStatus.getValue() == null) {
            showWarn("Fill all required fields.");
            return false;
        }
        return true;
    }

    private String value(TextField textField) {
        return textField.getText() == null ? "" : textField.getText().trim();
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

    private void loadAttendance(String keyword) {
        String safeKeyword = keyword == null ? "" : keyword.trim();
        String sql = """
                SELECT attendance_id, StudentReg, courseCode, SubmissionDate, session_type, attendance_status, tech_officer_reg
                FROM attendance
                WHERE (? = '' OR StudentReg LIKE ? OR courseCode LIKE ?)
                ORDER BY attendance_id DESC
                """;

        List<AttendanceRow> rows = new ArrayList<>();
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                String pattern = "%" + safeKeyword + "%";
                statement.setString(1, safeKeyword);
                statement.setString(2, pattern);
                statement.setString(3, pattern);

                try (ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) {
                        Date submissionDate = rs.getDate("SubmissionDate");
                        rows.add(new AttendanceRow(
                                String.valueOf(rs.getInt("attendance_id")),
                                safe(rs.getString("StudentReg")),
                                safe(rs.getString("courseCode")),
                                submissionDate == null ? "" : submissionDate.toString(),
                                safe(rs.getString("session_type")),
                                safe(rs.getString("attendance_status")),
                                safe(rs.getString("tech_officer_reg"))
                        ));
                    }
                }
            }
            tblAttendance.getItems().setAll(rows);
        } catch (SQLException e) {
            showError("Failed to load attendance records.", e);
        }
    }

    private String currentTechOfficerReg() {
        String reg = TechnicalOfficerContext.getRegistrationNo();
        return reg == null ? "" : reg.trim();
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    public static class AttendanceRow {
        private final SimpleStringProperty attendanceId;
        private final SimpleStringProperty studentRegNo;
        private final SimpleStringProperty courseCode;
        private final SimpleStringProperty date;
        private final SimpleStringProperty sessionType;
        private final SimpleStringProperty status;
        private final SimpleStringProperty techOfficerReg;

        public AttendanceRow(String attendanceId, String studentRegNo, String courseCode, String date, String sessionType, String status, String techOfficerReg) {
            this.attendanceId = new SimpleStringProperty(attendanceId);
            this.studentRegNo = new SimpleStringProperty(studentRegNo);
            this.courseCode = new SimpleStringProperty(courseCode);
            this.date = new SimpleStringProperty(date);
            this.sessionType = new SimpleStringProperty(sessionType);
            this.status = new SimpleStringProperty(status);
            this.techOfficerReg = new SimpleStringProperty(techOfficerReg);
        }

        public SimpleStringProperty attendanceIdProperty() { return attendanceId; }
        public SimpleStringProperty studentRegNoProperty() { return studentRegNo; }
        public SimpleStringProperty courseCodeProperty() { return courseCode; }
        public SimpleStringProperty dateProperty() { return date; }
        public SimpleStringProperty sessionTypeProperty() { return sessionType; }
        public SimpleStringProperty statusProperty() { return status; }
        public SimpleStringProperty techOfficerRegProperty() { return techOfficerReg; }

        public String getStudentRegNo() { return studentRegNo.get(); }
        public String getCourseCode() { return courseCode.get(); }
        public String getDate() { return date.get(); }
        public String getSessionType() { return sessionType.get(); }
        public String getStatus() { return status.get(); }
        public String getAttendanceId() { return attendanceId.get(); }

        public void setStudentRegNo(String value) { studentRegNo.set(value); }
        public void setCourseCode(String value) { courseCode.set(value); }
        public void setDate(String value) { date.set(value); }
        public void setSessionType(String value) { sessionType.set(value); }
        public void setStatus(String value) { status.set(value); }
    }
}
