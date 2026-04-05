package com.example.java_lms_group_01.Controller.Student;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class StudentGradePageController {

    @FXML
    private TableView<?> tblGrades;

    @FXML
    private TableColumn<?, ?> colSubject;

    @FXML
    private TableColumn<?, ?> colMarks;

    @FXML
    private TableColumn<?, ?> colGrade;

    @FXML
    private Label lblGpa;

    @FXML
    public void initialize() {
        System.out.println("Student grades page loaded");
    }
}
