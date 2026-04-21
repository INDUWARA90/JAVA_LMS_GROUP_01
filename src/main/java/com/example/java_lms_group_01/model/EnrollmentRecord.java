package com.example.java_lms_group_01.model;

import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDate;

/**
 * Read-only enrollment row model for admin enrollment table.
 */
public class EnrollmentRecord {

    private final int enrollmentId;
    private final String studentReg;
    private final String studentName;
    private final String batch;
    private final String courseCode;
    private final String courseName;
    private final LocalDate enrollmentDate;
    private final String status;

    public EnrollmentRecord(int enrollmentId, String studentReg, String studentName, String batch,
                            String courseCode, String courseName,
                            LocalDate enrollmentDate, String status) {
        this.enrollmentId = enrollmentId;
        this.studentReg = studentReg;
        this.studentName = studentName;
        this.batch = batch;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.enrollmentDate = enrollmentDate;
        this.status = status;
    }

    public int getEnrollmentId() {
        return enrollmentId;
    }

    public String getStudentReg() {
        return studentReg;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getBatch() {
        return batch;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    public String getStatus() {
        return status == null ? "" : status;
    }

    public boolean hasEnrollment() {
        return enrollmentId > 0;
    }

    public SimpleStringProperty enrollmentIdProperty() {
        return new SimpleStringProperty(String.valueOf(enrollmentId));
    }

    public SimpleStringProperty studentRegProperty() {
        return property(studentReg);
    }

    public SimpleStringProperty studentNameProperty() {
        return property(studentName);
    }

    public SimpleStringProperty batchProperty() {
        return property(batch);
    }

    public SimpleStringProperty courseCodeProperty() {
        return property(courseCode);
    }

    public SimpleStringProperty courseNameProperty() {
        return property(courseName);
    }

    public SimpleStringProperty enrollmentDateProperty() {
        return property(enrollmentDate == null ? "" : enrollmentDate.toString());
    }

    public SimpleStringProperty statusProperty() {
        return property(getStatus());
    }

    private SimpleStringProperty property(String value) {
        return new SimpleStringProperty(value == null ? "" : value);
    }
}
