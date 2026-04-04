package com.example.java_lms_group_01.Controller.Student;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;

public class NoticeController {

    @FXML
    private TableView<?> tableView;

    @FXML
    private TableColumn<?, ?> dayColumn;

    @FXML
    private TableColumn<?, ?> subjectColumn;

    @FXML
    private TableColumn<?, ?> timeColumn;

    @FXML
    private TableColumn<?, ?> venueColumn;

    @FXML
    public void initialize() {
        System.out.println("Timetable Table Loaded");
    }
}