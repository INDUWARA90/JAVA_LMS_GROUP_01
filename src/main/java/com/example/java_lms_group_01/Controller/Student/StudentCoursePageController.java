package com.example.java_lms_group_01.Controller.Student;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class StudentCoursePageController {

    @FXML
    private TableView<?> tblCourses;

    @FXML
    private TableColumn<?, ?> colCode;

    @FXML
    private TableColumn<?, ?> colName;

    @FXML
    private TableColumn<?, ?> colCredit;

    @FXML
    private TableColumn<?, ?> colLecturer;

    @FXML
    private TableColumn<?, ?> colType;

    @FXML
    public void initialize() {
        System.out.println("Student courses page loaded");
    }
}
