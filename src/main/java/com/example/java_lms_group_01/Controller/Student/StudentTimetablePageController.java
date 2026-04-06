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

public class StudentTimetablePageController {

    @FXML
    private TableView<TimetableRow> tblTimetable;
    @FXML
    private TableColumn<TimetableRow, String> colTimetableId;
    @FXML
    private TableColumn<TimetableRow, String> colDepartment;
    @FXML
    private TableColumn<TimetableRow, String> colLecId;
    @FXML
    private TableColumn<TimetableRow, String> colCourseCode;
    @FXML
    private TableColumn<TimetableRow, String> colAdminId;
    @FXML
    private TableColumn<TimetableRow, String> colDay;
    @FXML
    private TableColumn<TimetableRow, String> colStartTime;
    @FXML
    private TableColumn<TimetableRow, String> colEndTime;
    @FXML
    private TableColumn<TimetableRow, String> colSession;

    @FXML
    public void initialize() {
        colTimetableId.setCellValueFactory(d -> d.getValue().timetableIdProperty());
        colDepartment.setCellValueFactory(d -> d.getValue().departmentProperty());
        colLecId.setCellValueFactory(d -> d.getValue().lecIdProperty());
        colCourseCode.setCellValueFactory(d -> d.getValue().courseCodeProperty());
        colAdminId.setCellValueFactory(d -> d.getValue().adminIdProperty());
        colDay.setCellValueFactory(d -> d.getValue().dayProperty());
        colStartTime.setCellValueFactory(d -> d.getValue().startTimeProperty());
        colEndTime.setCellValueFactory(d -> d.getValue().endTimeProperty());
        colSession.setCellValueFactory(d -> d.getValue().sessionTypeProperty());
        loadTimetable();
    }

    private void loadTimetable() {
        String regNo = StudentContext.getRegistrationNo();
        if (regNo == null || regNo.isBlank()) {
            return;
        }

        List<TimetableRow> rows = new ArrayList<>();
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            if (!loadFromTable(connection, regNo, "timetable", rows)) {
                loadFromTable(connection, regNo, "timeTable", rows);
            }
            tblTimetable.getItems().setAll(rows);
        } catch (SQLException e) {
            showError("Failed to load timetable details.", e);
        }
    }

    private boolean loadFromTable(Connection connection, String regNo, String tableName, List<TimetableRow> rows) throws SQLException {
        String sql = """
                SELECT t.time_table_id, t.department, t.lec_id, t.courseCode, t.admin_id, t.day, t.start_time, t.end_time, t.session_type
                FROM %s t
                INNER JOIN student s ON s.department = t.department
                WHERE s.registrationNo = ?
                ORDER BY t.day, t.start_time
                """.formatted(tableName);
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, regNo);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    rows.add(new TimetableRow(
                            safe(rs.getString("time_table_id")),
                            safe(rs.getString("department")),
                            safe(rs.getString("lec_id")),
                            safe(rs.getString("courseCode")),
                            safe(rs.getString("admin_id")),
                            safe(rs.getString("day")),
                            rs.getTime("start_time") == null ? "" : rs.getTime("start_time").toString(),
                            rs.getTime("end_time") == null ? "" : rs.getTime("end_time").toString(),
                            safe(rs.getString("session_type"))
                    ));
                }
                return true;
            }
        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("doesn't exist")) {
                return false;
            }
            throw e;
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

    public static class TimetableRow {
        private final SimpleStringProperty timetableId;
        private final SimpleStringProperty department;
        private final SimpleStringProperty lecId;
        private final SimpleStringProperty courseCode;
        private final SimpleStringProperty adminId;
        private final SimpleStringProperty day;
        private final SimpleStringProperty startTime;
        private final SimpleStringProperty endTime;
        private final SimpleStringProperty sessionType;

        public TimetableRow(String timetableId, String department, String lecId, String courseCode, String adminId, String day, String startTime, String endTime, String sessionType) {
            this.timetableId = new SimpleStringProperty(timetableId);
            this.department = new SimpleStringProperty(department);
            this.lecId = new SimpleStringProperty(lecId);
            this.courseCode = new SimpleStringProperty(courseCode);
            this.adminId = new SimpleStringProperty(adminId);
            this.day = new SimpleStringProperty(day);
            this.startTime = new SimpleStringProperty(startTime);
            this.endTime = new SimpleStringProperty(endTime);
            this.sessionType = new SimpleStringProperty(sessionType);
        }

        public SimpleStringProperty timetableIdProperty() { return timetableId; }
        public SimpleStringProperty departmentProperty() { return department; }
        public SimpleStringProperty lecIdProperty() { return lecId; }
        public SimpleStringProperty courseCodeProperty() { return courseCode; }
        public SimpleStringProperty adminIdProperty() { return adminId; }
        public SimpleStringProperty dayProperty() { return day; }
        public SimpleStringProperty startTimeProperty() { return startTime; }
        public SimpleStringProperty endTimeProperty() { return endTime; }
        public SimpleStringProperty sessionTypeProperty() { return sessionType; }
    }
}
