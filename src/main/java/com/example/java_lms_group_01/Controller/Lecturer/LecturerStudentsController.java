package com.example.java_lms_group_01.Controller.Lecturer;

import com.example.java_lms_group_01.Repository.LecturerRepository;
import com.example.java_lms_group_01.model.Student;
import com.example.java_lms_group_01.util.LecturerContext;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Shows students enrolled in courses taught by the logged-in lecturer.
 */
public class LecturerStudentsController {

    @FXML
    private TextField txtSearch;
    @FXML
    private TableView<Student> tblStudents;
    @FXML
    private TableColumn<Student, String> colRegNo;
    @FXML
    private TableColumn<Student, String> colName;
    @FXML
    private TableColumn<Student, String> colEmail;
    @FXML
    private TableColumn<Student, String> colPhone;
    @FXML
    private TableColumn<Student, String> colDepartment;
    @FXML
    private TableColumn<Student, String> colStatus;
    @FXML
    private TableColumn<Student, String> colGpa;

    private final LecturerRepository lecturerRepository = new LecturerRepository();

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
        try {
            List<LecturerRepository.StudentRecord> recordList =
                    lecturerRepository.findStudentsByLecturer(currentLecturer(), keyword);
            List<Student> rows = new ArrayList<>();
            for (LecturerRepository.StudentRecord record : recordList) {
                rows.add(new Student(
                        record.getRegNo(),
                        record.getName(),
                        record.getEmail(),
                        record.getPhone(),
                        record.getDepartment(),
                        record.getStatus(),
                        record.getGpa()
                ));
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

    private void showError(String message, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Database Error");
        alert.setHeaderText(null);
        alert.setContentText(message + "\n" + e.getMessage());
        alert.showAndWait();
    }

}
