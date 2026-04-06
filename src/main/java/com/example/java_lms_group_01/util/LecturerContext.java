package com.example.java_lms_group_01.util;

public class LecturerContext {

    private static String registrationNo;

    private LecturerContext() {
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
