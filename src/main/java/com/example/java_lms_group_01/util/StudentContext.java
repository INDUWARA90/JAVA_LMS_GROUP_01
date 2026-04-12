package com.example.java_lms_group_01.util;

/**
 * Stores the registration number of the student who is currently logged in.
 */
public class StudentContext {

    private static String registrationNo;

    private StudentContext() {
    }

    public static void setRegistrationNo(String regNo) {
        registrationNo = regNo;
    }

    public static String getRegistrationNo() {
        return registrationNo;
    }

    public static void clear() {
        registrationNo = null;
    }
}
