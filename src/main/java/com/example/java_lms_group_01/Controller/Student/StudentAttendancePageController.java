package com.example.java_lms_group_01.Controller.Student;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class StudentAttendancePageController {

    @FXML
    private TableView<?> tblAttendance;

    @FXML
    private TableColumn<?, ?> colSubject;

    @FXML
    private TableColumn<?, ?> colTheory;

    @FXML
    private TableColumn<?, ?> colPractical;

    @FXML
    private TableColumn<?, ?> colTotal;

    @FXML
    public void initialize() {
        System.out.println("Student attendance page loaded");
    }
}
