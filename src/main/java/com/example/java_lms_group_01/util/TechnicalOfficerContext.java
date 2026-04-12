package com.example.java_lms_group_01.util;

/**
 * Stores the registration number of the technical officer who is currently logged in.
 */
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
