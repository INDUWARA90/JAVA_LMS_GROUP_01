package com.example.java_lms_group_01.model.request;

import java.time.LocalDate;


public class ExamAttendanceRequest {
    private String studentRegNo;
    private String courseCode;
    private String status;
    private LocalDate attendanceDate;

    public ExamAttendanceRequest() {
    }

    public ExamAttendanceRequest(String studentRegNo, String courseCode, String status, LocalDate attendanceDate) {
        this.studentRegNo = studentRegNo;
        this.courseCode = courseCode;
        this.status = status;
        this.attendanceDate = attendanceDate;
    }

    public String getStudentRegNo() {
        return studentRegNo;
    }

    public void setStudentRegNo(String studentRegNo) {
        this.studentRegNo = studentRegNo;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(LocalDate attendanceDate) {
        this.attendanceDate = attendanceDate;
    }
}
