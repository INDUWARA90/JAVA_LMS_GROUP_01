package com.example.java_lms_group_01.Controller.Student;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Button;

public class CourseController {

    @FXML
    private TableView<?> courseTable;

    @FXML
    private TableColumn<?, ?> courseIdColumn;

    @FXML
    private TableColumn<?, ?> courseNameColumn;

    @FXML
    private TableColumn<?, ?> creditColumn;

    @FXML
    private TableColumn<?, ?> lecturerColumn;

    @FXML
    private TableColumn<?, ?> typeColumn;

    @FXML
    private Button refreshButton;

    @FXML
    private Button backButton;

    @FXML
    public void initialize() {
        System.out.println("Course Table Loaded");
    }

    @FXML
    private void handleRefresh() {
        System.out.println("Refresh clicked");
    }

    @FXML
    private void handleBack() {
        System.out.println("Back clicked");
    }
}