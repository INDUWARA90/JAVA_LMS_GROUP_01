package com.example.java_lms_group_01.Controller.Student;

import com.example.java_lms_group_01.Repository.UserProfileRepository;
import com.example.java_lms_group_01.model.UserManagementRow;
import com.example.java_lms_group_01.model.users.Student;
import com.example.java_lms_group_01.util.StudentContext;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.SQLException;

/**
 * Lets the student view basic profile details and update profile information,
 * including the account password.
 */
public class StudentProfilePageController {

    @FXML
    private TextField txtRegistrationNo;
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtPhone;
    @FXML
    private TextField txtAddress;
    @FXML
    private TextField txtPicturePath;
    @FXML
    private TextField txtDepartment;
    @FXML
    private TextField txtGpa;
    @FXML
    private TextField txtStatus;
    @FXML
    private PasswordField txtCurrentPassword;
    @FXML
    private PasswordField txtNewPassword;
    @FXML
    private PasswordField txtConfirmPassword;

    private final UserProfileRepository userProfileRepository = new UserProfileRepository();
    private Student currentStudent;

    @FXML
    public void initialize() {
        txtRegistrationNo.setEditable(false);
        txtName.setEditable(false);
        txtDepartment.setEditable(false);
        txtGpa.setEditable(false);
        txtStatus.setEditable(false);
        loadProfile();
    }

    @FXML
    private void saveProfile() {
        String regNo = StudentContext.getRegistrationNo();
        if (regNo == null || regNo.isBlank()) {
            show(Alert.AlertType.WARNING, "Session Error", "Student session not found. Please login again.");
            return;
        }
        if (currentStudent == null) {
            show(Alert.AlertType.WARNING, "Profile Error", "Student profile is not loaded.");
            return;
        }

        currentStudent.setEmail(value(txtEmail));
        currentStudent.setPhoneNumber(value(txtPhone));
        currentStudent.setAddress(value(txtAddress));
        String currentPassword = passwordValue(txtCurrentPassword);
        String newPassword = passwordValue(txtNewPassword);
        String confirmPassword = passwordValue(txtConfirmPassword);

        try {
            validatePasswordChange(currentPassword, newPassword, confirmPassword);
            userProfileRepository.updateStudentProfile(
                    currentStudent.getRegistrationNo(),
                    currentStudent.getEmail(),
                    currentStudent.getPhoneNumber(),
                    currentStudent.getAddress(),
                    value(txtPicturePath),
                    currentPassword,
                    newPassword
            );
            clearPasswordFields();
            show(Alert.AlertType.INFORMATION, "Profile Updated",
                    hasPasswordChange(currentPassword, newPassword, confirmPassword)
                            ? "Profile details and password updated successfully."
                            : "Contact details and profile picture path updated successfully.");
        } catch (IllegalArgumentException e) {
            show(Alert.AlertType.WARNING, "Validation Error", e.getMessage());
        } catch (SQLException e) {
            show(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    private void loadProfile() {
        String regNo = StudentContext.getRegistrationNo();
        if (regNo == null || regNo.isBlank()) {
            return;
        }

        try {
            UserManagementRow profile = userProfileRepository.findStudentProfile(regNo);
            if (profile == null) {
                return;
            }
            currentStudent = mapStudent(profile);
            txtRegistrationNo.setText(currentStudent.getRegistrationNo());
            txtName.setText(safe(currentStudent.getFirstName()) + " " + safe(currentStudent.getLastName()));
            txtEmail.setText(safe(currentStudent.getEmail()));
            txtPhone.setText(safe(currentStudent.getPhoneNumber()));
            txtAddress.setText(safe(currentStudent.getAddress()));
            txtDepartment.setText(safe(profile.getDepartment()));
            txtGpa.setText(profile.getGpa() == null ? "0.00" : String.format("%.2f", profile.getGpa()));
            txtStatus.setText(safe(profile.getStatus()));
            txtPicturePath.setText(safe(profile.getProfileImagePath()));
        } catch (SQLException e) {
            show(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    private Student mapStudent(UserManagementRow row) {
        Student student = new Student();
        student.setRegistrationNo(row.getUserId());
        student.setUserId(row.getUserId());
        student.setFirstName(row.getFirstName());
        student.setLastName(row.getLastName());
        student.setEmail(row.getEmail());
        student.setPhoneNumber(row.getPhoneNumber());
        student.setAddress(row.getAddress());
        student.setGPA(row.getGpa() == null ? 0.0f : row.getGpa().floatValue());
        student.setStatus(row.getStatus());
        return student;
    }

    private String value(TextField field) {
        return field.getText() == null ? "" : field.getText().trim();
    }

    private String passwordValue(PasswordField field) {
        return field.getText() == null ? "" : field.getText().trim();
    }

    private void validatePasswordChange(String currentPassword, String newPassword, String confirmPassword) {
        if (!hasPasswordChange(currentPassword, newPassword, confirmPassword)) {
            return;
        }
        if (currentPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank()) {
            throw new IllegalArgumentException("Enter current, new, and confirm password to change the password.");
        }
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("New password and confirm password do not match.");
        }
        if (newPassword.equals(currentPassword)) {
            throw new IllegalArgumentException("New password must be different from the current password.");
        }
    }

    private boolean hasPasswordChange(String currentPassword, String newPassword, String confirmPassword) {
        return !currentPassword.isBlank() || !newPassword.isBlank() || !confirmPassword.isBlank();
    }

    private void clearPasswordFields() {
        txtCurrentPassword.clear();
        txtNewPassword.clear();
        txtConfirmPassword.clear();
    }

    private String safe(String text) {
        return text == null ? "" : text;
    }

    private void show(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
