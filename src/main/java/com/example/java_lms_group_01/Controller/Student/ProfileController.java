package com.example.java_lms_group_01.Controller.Student;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class ProfileController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField addressField;

    @FXML
    public void initialize() {
        System.out.println("Profile Page Loaded");
    }

    @FXML
    private void handleUpdate() {
        System.out.println("Profile Updated");
    }
}