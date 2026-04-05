package com.example.java_lms_group_01.model;

public class Attendance {

    private int totalSessions;
    private int attended;
    private int medical;

    // Calculate percentage
    public double getPercentage() {
        return ((double)(attended + medical) / totalSessions) * 100;
    }

    // Check eligibility
    public boolean isEligible() {
        return getPercentage() >= 80;
    }
}
