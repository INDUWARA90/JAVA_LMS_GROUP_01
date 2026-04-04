package com.example.java_lms_group_01.Controller.Student;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;

public class MedicalController {

    @FXML
    private TableView<?> tableView;

    @FXML
    private TableColumn<?, ?> dateColumn;

    @FXML
    private TableColumn<?, ?> subjectColumn;

    @FXML
    private TableColumn<?, ?> reasonColumn;

    @FXML
    private TableColumn<?, ?> approvedColumn;

    @FXML
    public void initialize() {
        System.out.println("Medical Table Loaded");
    }
}