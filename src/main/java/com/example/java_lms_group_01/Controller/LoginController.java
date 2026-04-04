package com.example.java_lms_group_01.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;

public class LoginController {

    @FXML
    private TextField loginEmail;

    @FXML
    private PasswordField loginPass;

    @FXML
    public void initialize() {
        System.out.println("Login Loaded");
    }

    @FXML
    private void btnOnActionLogin() {

        String username = loginEmail.getText();
        String password = loginPass.getText();

        if (username.equals("admin") && password.equals("1234")) {
            System.out.println("Login Success");
        } else {
            System.out.println("Invalid Login");
        }
    }
}