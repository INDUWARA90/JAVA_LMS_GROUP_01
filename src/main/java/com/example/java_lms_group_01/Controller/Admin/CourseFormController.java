package com.example.java_lms_group_01.Controller.Admin;

import com.example.java_lms_group_01.model.Course;
import com.example.java_lms_group_01.model.CourseType;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class CourseFormController {

    @FXML
    private TextField txtCourseCode;

    @FXML
    private TextField txtCredit;

    @FXML
    private TextField txtDepartment;

    @FXML
    private TextField txtLecturerRegNo;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtSemester;

    @FXML
    private ComboBox<CourseType> cmbCourseType;

    // Prepares form for creating a new course.
    public void setupForCreate() {
        txtCourseCode.setDisable(false); // Allow editing course code for new course
        cmbCourseType.getItems().setAll(CourseType.values()); // Load all available course types into ComboBox
        cmbCourseType.setValue(CourseType.THEORY); // Set default course type
    }

    // Prepares form for editing an existing course.
    public void setupForEdit(Course course) {
        setupForCreate(); // Initialize common setup

        txtCourseCode.setText(course.getCourseCode()); // Populate fields with existing course data
        txtCourseCode.setDisable(true); // Disable editing of course code
        txtName.setText(course.getName());
        txtCredit.setText(String.valueOf(course.getCredit()));
        txtLecturerRegNo.setText(getSafeText(course.getLecturerRegistrationNo()));
        txtDepartment.setText(getSafeText(course.getDepartment()));
        txtSemester.setText(getSafeText(course.getSemester()));
        cmbCourseType.setValue(course.getCourseTypeEnum()); // Set selected course type
    }

    // Builds a Course object from form input.
    public Course buildCourse() {
        // Retrieve and sanitize input values
        String courseCode = getTextFieldValue(txtCourseCode);
        String courseName = getTextFieldValue(txtName);
        String lecturerRegistrationNumber = getTextFieldValue(txtLecturerRegNo);
        String department = getTextFieldValue(txtDepartment);
        String semester = getTextFieldValue(txtSemester);
        CourseType selectedCourseType = cmbCourseType.getValue();

        if (courseCode.isBlank()) {
            throw new IllegalArgumentException("Course code is required.");
        }

        if (courseName.isBlank()) {
            throw new IllegalArgumentException("Course name is required.");
        }

        if (department.isBlank()) {
            throw new IllegalArgumentException("Department is required.");
        }

        if (semester.isBlank()) {
            throw new IllegalArgumentException("Semester is required.");
        }

        if (selectedCourseType == null) {
            throw new IllegalArgumentException("Course type is required.");
        }

        int creditValue;
        try {
            creditValue = Integer.parseInt(getTextFieldValue(txtCredit));
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Credit must be a valid number.");
        }

        if (creditValue <= 0) {
            throw new IllegalArgumentException("Credit must be greater than 0.");
        }

        return new Course(
                courseCode,
                courseName,
                lecturerRegistrationNumber.isBlank() ? null : lecturerRegistrationNumber,
                department,
                semester,
                creditValue,
                selectedCourseType
        );
    }

    private String getTextFieldValue(TextField textField) {
        if (textField.getText() == null) {
            return "";
        }
        return textField.getText().trim();
    }

    private String getSafeText(String text) {
        if (text == null) {
            return "";
        }
        return text;
    }
}