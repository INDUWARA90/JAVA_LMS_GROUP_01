package com.example.java_lms_group_01.model;

import javafx.beans.property.SimpleStringProperty;

public class ExamAttendance {
    private String examAttendanceId;
    private String studentRegNo;
    private String courseCode;
    private String status;
    private String attendanceDate;

    public ExamAttendance() {
    }

    public ExamAttendance(String examAttendanceId, String studentRegNo, String courseCode,
                          String status, String attendanceDate) {
        setExamAttendanceId(examAttendanceId);
        setStudentRegNo(studentRegNo);
        setCourseCode(courseCode);
        setStatus(status);
        setAttendanceDate(attendanceDate);
    }

    public String getExamAttendanceId() {
        return examAttendanceId;
    }

    public void setExamAttendanceId(String examAttendanceId) {
        this.examAttendanceId = text(examAttendanceId);
    }

    public String getStudentRegNo() {
        return studentRegNo;
    }

    public void setStudentRegNo(String studentRegNo) {
        this.studentRegNo = text(studentRegNo);
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = text(courseCode);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = text(status);
    }

    public String getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(String attendanceDate) {
        this.attendanceDate = text(attendanceDate);
    }

    public SimpleStringProperty examAttendanceIdProperty() {
        return property(examAttendanceId);
    }

    public SimpleStringProperty studentRegNoProperty() {
        return property(studentRegNo);
    }

    public SimpleStringProperty courseCodeProperty() {
        return property(courseCode);
    }

    public SimpleStringProperty statusProperty() {
        return property(status);
    }

    public SimpleStringProperty attendanceDateProperty() {
        return property(attendanceDate);
    }

    private static SimpleStringProperty property(String value) {
        return new SimpleStringProperty(text(value));
    }

    private static String text(String value) {
        return value == null ? "" : value;
    }
}
