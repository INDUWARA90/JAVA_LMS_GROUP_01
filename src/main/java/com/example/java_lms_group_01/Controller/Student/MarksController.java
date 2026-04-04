package com.example.java_lms_group_01.Controller.Student;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;

public class MarksController {

    @FXML
    private TableView<?> tableView;

    @FXML
    private TableColumn<?, ?> subjectColumn;

    @FXML
    private TableColumn<?, ?> theoryColumn;

    @FXML
    private TableColumn<?, ?> practicalColumn;

    @FXML
    private TableColumn<?, ?> totalColumn;

    @FXML
    public void initialize() {
        // Initialize data here if needed
        System.out.println("Marks Table Loaded");
    }
}