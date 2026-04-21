package com.example.java_lms_group_01.Controller.TechnicalOfficer;

import com.example.java_lms_group_01.Repository.TechnicalOfficerRepository;
import com.example.java_lms_group_01.model.ExamAttendance;
import com.example.java_lms_group_01.model.request.ExamAttendanceRequest;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.sql.SQLException;
import java.time.LocalDate;

public class TechnicalOfficerExamAttendanceController {

    @FXML
    private TextField txtStudentRegNo;
    @FXML
    private TextField txtCourseCode;
    @FXML
    private ComboBox<String> cmbStatus;
    @FXML
    private DatePicker dpAttendanceDate;
    @FXML
    private TextField txtSearch;
    @FXML
    private TableView<ExamAttendance> tblExamAttendance;
    @FXML
    private TableColumn<ExamAttendance, String> colExamAttendanceId;
    @FXML
    private TableColumn<ExamAttendance, String> colStudentRegNo;
    @FXML
    private TableColumn<ExamAttendance, String> colCourseCode;
    @FXML
    private TableColumn<ExamAttendance, String> colStatus;
    @FXML
    private TableColumn<ExamAttendance, String> colAttendanceDate;

    private final TechnicalOfficerRepository technicalOfficerRepository = new TechnicalOfficerRepository();

    @FXML
    public void initialize() {
        cmbStatus.setItems(FXCollections.observableArrayList("present", "absent"));

        colExamAttendanceId.setCellValueFactory(d -> d.getValue().examAttendanceIdProperty());
        colStudentRegNo.setCellValueFactory(d -> d.getValue().studentRegNoProperty());
        colCourseCode.setCellValueFactory(d -> d.getValue().courseCodeProperty());
        colStatus.setCellValueFactory(d -> d.getValue().statusProperty());
        colAttendanceDate.setCellValueFactory(d -> d.getValue().attendanceDateProperty());

        loadExamAttendance(null);

        tblExamAttendance.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, row) -> {
            if (row == null) {
                return;
            }
            txtStudentRegNo.setText(row.getStudentRegNo());
            txtCourseCode.setText(row.getCourseCode());
            cmbStatus.setValue(row.getStatus());
            dpAttendanceDate.setValue(row.getAttendanceDate().isBlank() ? null : LocalDate.parse(row.getAttendanceDate()));
        });
    }

    @FXML
    private void addRecord(ActionEvent event) {
        if (!validForm()) {
            return;
        }
        try {
            technicalOfficerRepository.addExamAttendance(buildRequest());
            loadExamAttendance(txtSearch.getText());
            clearForm(event);
        } catch (Exception e) {
            showError("Failed to add exam attendance record.", e);
        }
    }

    @FXML
    private void updateRecord(ActionEvent event) {
        ExamAttendance selected = tblExamAttendance.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarn("Select a record to update.");
            return;
        }
        if (!validForm()) {
            return;
        }
        try {
            technicalOfficerRepository.updateExamAttendance(Integer.parseInt(selected.getExamAttendanceId()), buildRequest());
            loadExamAttendance(txtSearch.getText());
        } catch (Exception e) {
            showError("Failed to update exam attendance record.", e);
        }
    }

    @FXML
    private void deleteRecord(ActionEvent event) {
        ExamAttendance selected = tblExamAttendance.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarn("Select a record to delete.");
            return;
        }
        try {
            technicalOfficerRepository.deleteExamAttendance(Integer.parseInt(selected.getExamAttendanceId()));
            loadExamAttendance(txtSearch.getText());
            clearForm(event);
        } catch (Exception e) {
            showError("Failed to delete exam attendance record.", e);
        }
    }

    @FXML
    private void clearForm(ActionEvent event) {
        txtStudentRegNo.clear();
        txtCourseCode.clear();
        cmbStatus.setValue(null);
        dpAttendanceDate.setValue(null);
        tblExamAttendance.getSelectionModel().clearSelection();
    }

    @FXML
    private void searchRecords(ActionEvent event) {
        loadExamAttendance(txtSearch.getText());
    }

    @FXML
    private void refreshRecords(ActionEvent event) {
        txtSearch.clear();
        loadExamAttendance(null);
    }

    private boolean validForm() {
        if (value(txtStudentRegNo).isBlank() || value(txtCourseCode).isBlank()
                || cmbStatus.getValue() == null || dpAttendanceDate.getValue() == null) {
            showWarn("Fill all required fields.");
            return false;
        }
        return true;
    }

    private String value(TextField textField) {
        return textField.getText() == null ? "" : textField.getText().trim();
    }

    private ExamAttendanceRequest buildRequest() {
        return new ExamAttendanceRequest(
                value(txtStudentRegNo),
                value(txtCourseCode),
                cmbStatus.getValue(),
                dpAttendanceDate.getValue()
        );
    }

    private void loadExamAttendance(String keyword) {
        try {
            tblExamAttendance.getItems().setAll(technicalOfficerRepository.findExamAttendance(keyword));
        } catch (SQLException e) {
            showError("Failed to load exam attendance records.", e);
        }
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
}
