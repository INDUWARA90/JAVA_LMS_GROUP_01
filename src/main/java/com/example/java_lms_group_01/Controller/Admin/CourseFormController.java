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

    public void setupForCreate() {
        txtCourseCode.setDisable(false);
        cmbCourseType.getItems().setAll(CourseType.values());
        cmbCourseType.setValue(CourseType.THEORY);
    }

    public void setupForEdit(Course course) {
        setupForCreate();

        txtCourseCode.setText(course.getCourseCode());
        txtCourseCode.setDisable(true);
        txtName.setText(course.getName());
        txtCredit.setText(String.valueOf(course.getCredit()));
        txtLecturerRegNo.setText(getSafeText(course.getLecturerRegistrationNo()));
        txtDepartment.setText(getSafeText(course.getDepartment()));
        txtSemester.setText(getSafeText(course.getSemester()));
        cmbCourseType.setValue(course.getCourseTypeEnum());
    }

    public Course buildCourse() {
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