package com.example.java_lms_group_01.Controller.Student;

import com.example.java_lms_group_01.model.users.Student;
import com.example.java_lms_group_01.util.DBConnection;
import com.example.java_lms_group_01.util.StudentContext;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

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

        String updateSql = "UPDATE users SET email = ?, phoneNumber = ?, address = ? WHERE user_id = ?";
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            try (PreparedStatement statement = connection.prepareStatement(updateSql)) {
                statement.setString(1, currentStudent.getEmail());
                statement.setString(2, currentStudent.getPhoneNumber());
                statement.setString(3, currentStudent.getAddress());
                statement.setString(4, currentStudent.getRegistrationNo());
                statement.executeUpdate();
            }

            savePicturePath(regNo, value(txtPicturePath));
            show(Alert.AlertType.INFORMATION, "Profile Updated",
                    "Contact details and profile picture path updated successfully.");
        } catch (Exception e) {
            show(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    private void loadProfile() {
        String regNo = StudentContext.getRegistrationNo();
        if (regNo == null || regNo.isBlank()) {
            return;
        }

        String profileSql = """
                SELECT u.user_id, u.firstName, u.lastName, u.email, u.phoneNumber, u.address,
                       s.department, s.GPA, s.status
                FROM users u
                INNER JOIN student s ON s.registrationNo = u.user_id
                WHERE s.registrationNo = ?
                """;

        try {
            Connection connection = DBConnection.getInstance().getConnection();
            try (PreparedStatement statement = connection.prepareStatement(profileSql)) {
                statement.setString(1, regNo);
                try (ResultSet rs = statement.executeQuery()) {
                    if (!rs.next()) {
                        return;
                    }
                    currentStudent = mapStudent(rs);
                    txtRegistrationNo.setText(currentStudent.getRegistrationNo());
                    txtName.setText(safe(currentStudent.getFirstName()) + " " + safe(currentStudent.getLastName()));
                    txtEmail.setText(safe(currentStudent.getEmail()));
                    txtPhone.setText(safe(currentStudent.getPhoneNumber()));
                    txtAddress.setText(safe(currentStudent.getAddress()));
                    txtDepartment.setText(safe(rs.getString("department")));
                    Object gpaValue = rs.getObject("GPA");
                    txtGpa.setText(gpaValue == null ? "0.00" : String.format("%.2f", ((Number) gpaValue).doubleValue()));
                    txtStatus.setText(safe(rs.getString("status")));
                    txtPicturePath.setText(loadPicturePath(regNo));
                }
            }
        } catch (SQLException e) {
            show(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    private Student mapStudent(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setRegistrationNo(rs.getString("user_id"));
        student.setUserId(rs.getString("user_id"));
        student.setFirstName(rs.getString("firstName"));
        student.setLastName(rs.getString("lastName"));
        student.setEmail(rs.getString("email"));
        student.setPhoneNumber(rs.getString("phoneNumber"));
        student.setAddress(rs.getString("address"));
        Object gpa = rs.getObject("GPA");
        student.setGPA(gpa == null ? 0.0f : ((Number) gpa).floatValue());
        student.setStatus(rs.getString("status"));
        return student;
    }

    private String pictureStoreKey(String regNo) {
        return "student.picture." + regNo;
    }

    private Path pictureStoreFile() throws IOException {
        Path dir = Paths.get(System.getProperty("user.home"), ".lms");
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }
        return dir.resolve("student_profile_pictures.properties");
    }

    private String loadPicturePath(String regNo) {
        try {
            Path file = pictureStoreFile();
            if (!Files.exists(file)) {
                return "";
            }
            Properties properties = new Properties();
            try (InputStream in = Files.newInputStream(file)) {
                properties.load(in);
            }
            return properties.getProperty(pictureStoreKey(regNo), "");
        } catch (Exception ignored) {
            return "";
        }
    }

    private void savePicturePath(String regNo, String picturePath) {
        try {
            Path file = pictureStoreFile();
            Properties properties = new Properties();
            if (Files.exists(file)) {
                try (InputStream in = Files.newInputStream(file)) {
                    properties.load(in);
                }
            }
            properties.setProperty(pictureStoreKey(regNo), picturePath);
            try (OutputStream out = Files.newOutputStream(file)) {
                properties.store(out, "Student profile picture paths");
            }
        } catch (Exception ignored) {
            // Keep profile update successful even if local picture path persistence fails.
        }
    }

    private String value(TextField field) {
        return field.getText() == null ? "" : field.getText().trim();
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
