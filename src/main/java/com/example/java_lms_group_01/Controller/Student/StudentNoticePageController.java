package com.example.java_lms_group_01.Controller.Student;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class StudentNoticePageController {

    @FXML
    private TableView<?> tblNotices;

    @FXML
    private TableColumn<?, ?> colDate;

    @FXML
    private TableColumn<?, ?> colTitle;

    @FXML
    private TableColumn<?, ?> colContent;

    @FXML
    private TableColumn<?, ?> colBy;

    @FXML
    public void initialize() {
        System.out.println("Student notices page loaded");
    }
}
