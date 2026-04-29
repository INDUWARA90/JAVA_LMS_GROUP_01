//time table ui control
package com.example.java_lms_group_01.Controller.TechnicalOfficer;

import com.example.java_lms_group_01.Repository.TimetableRepository;
import com.example.java_lms_group_01.model.Timetable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.sql.SQLException;
import java.util.List;

public class TechnicalOfficerTimetableController {

    //dropdown(dep)
    @FXML private ComboBox<String> cmbDepartment;

    //timetable
    @FXML private TableView<Timetable> tblTimetables;

    //table column
    @FXML private TableColumn<Timetable, String> colTimetableId, colDepartment, colLecturerId,
            colCourseCode, colAdminId, colDay, colStartTime, colEndTime, colSessionType;

    //access db
    private final TimetableRepository timetableRepository = new TimetableRepository();

    //intialize
    @FXML
    public void initialize() {
        // Step 1: Tell each column which data to pull from the Timetable model
        configureColumns();

        // Step 2: Fill the ComboBox with department names from the database
        loadDepartmentList();

        // Step 3: Load all timetables by default (passing null shows everything)
        refreshTableData(null);
    }

    //method of table column bind
    private void configureColumns() {
        // Standard readable way to link columns to model properties
        colTimetableId.setCellValueFactory(data -> { return data.getValue().timetableIdProperty(); });
        colDepartment.setCellValueFactory(data -> { return data.getValue().departmentProperty(); });
        colLecturerId.setCellValueFactory(data -> { return data.getValue().lecturerIdProperty(); });
        colCourseCode.setCellValueFactory(data -> { return data.getValue().courseCodeProperty(); });
        colAdminId.setCellValueFactory(data -> { return data.getValue().adminIdProperty(); });
        colDay.setCellValueFactory(data -> { return data.getValue().dayProperty(); });
        colStartTime.setCellValueFactory(data -> { return data.getValue().startTimeProperty(); });
        colEndTime.setCellValueFactory(data -> { return data.getValue().endTimeProperty(); });
        colSessionType.setCellValueFactory(data -> { return data.getValue().sessionTypeProperty(); });
    }

    //dep filter button
    @FXML
    private void filterByDepartment(ActionEvent event) {
        // Get the selected department from the dropdown
        String selectedDept = cmbDepartment.getValue();

        // Reload the table using this filter
        refreshTableData(selectedDept);
    }

    //load dep for drop down
    private void loadDepartmentList() {
        try {
            // Get unique departments from the repository
            List<String> departments = timetableRepository.findAllDepartments();
            cmbDepartment.getItems().setAll(departments);
        } catch (SQLException e) {
            showErrorMessage("Database Error", "Failed to load departments.");
        }
    }

    //load table data method
    private void refreshTableData(String department) {
        try {
            // Fetch filtered list (using null for lecturer and course filters)
            List<Timetable> list = timetableRepository.findByDepartment(department);

            // Update the table
            tblTimetables.getItems().setAll(list);
        } catch (SQLException e) {
            showErrorMessage("Database Error", "Failed to load timetables.");
        }
    }

    //error mzg
    private void showErrorMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}