package com.example.java_lms_group_01.model;

import javafx.beans.property.SimpleStringProperty;

/**
 * JavaFX table row that shows attendance percentage and exam eligibility by course.
 */
public class AttendanceEligibilitySummary {
    private final SimpleStringProperty courseCode;
    private final SimpleStringProperty eligibleSessions;
    private final SimpleStringProperty totalSessions;
    private final SimpleStringProperty attendancePct;
    private final SimpleStringProperty eligibility;

    public AttendanceEligibilitySummary(String courseCode, String eligibleSessions, String totalSessions,
                                        String attendancePct, String eligibility) {
        this.courseCode = new SimpleStringProperty(courseCode);
        this.eligibleSessions = new SimpleStringProperty(eligibleSessions);
        this.totalSessions = new SimpleStringProperty(totalSessions);
        this.attendancePct = new SimpleStringProperty(attendancePct);
        this.eligibility = new SimpleStringProperty(eligibility);
    }

    public SimpleStringProperty courseCodeProperty() { return courseCode; }
    public SimpleStringProperty eligibleSessionsProperty() { return eligibleSessions; }
    public SimpleStringProperty totalSessionsProperty() { return totalSessions; }
    public SimpleStringProperty attendancePctProperty() { return attendancePct; }
    public SimpleStringProperty eligibilityProperty() { return eligibility; }

    public String getCourseCode() { return courseCode.get(); }
    public String getEligibleSessions() { return eligibleSessions.get(); }
    public String getTotalSessions() { return totalSessions.get(); }
    public String getAttendancePct() { return attendancePct.get(); }
    public String getEligibility() { return eligibility.get(); }
}
