//session class -store data curruntly looged in tec officers
package com.example.java_lms_group_01.session;

// Stores the current logged-in technical officer registration number.
public class LoggedInTechnicalOfficer {

    //static veriable (whole app)
    private static String registrationNo;

    // privete constructor
    private LoggedInTechnicalOfficer() {
    }

    //get logged in user id method
    public static String getRegistrationNo() {
        return registrationNo;
    }

    // set logeed in user id method
    public static void setRegistrationNo(String registrationNo) {
        LoggedInTechnicalOfficer.registrationNo = registrationNo;
    }

    //logout -session clear
    public static void clear() {
        registrationNo = null;
    }
}
