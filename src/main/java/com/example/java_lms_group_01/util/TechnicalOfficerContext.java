package com.example.java_lms_group_01.util;

public class TechnicalOfficerContext {

    private static String registrationNo;

    private TechnicalOfficerContext() {
    }

    public static String getRegistrationNo() {
        return registrationNo;
    }

    public static void setRegistrationNo(String registrationNo) {
        TechnicalOfficerContext.registrationNo = registrationNo;
    }

    public static void clear() {
        registrationNo = null;
    }
}
