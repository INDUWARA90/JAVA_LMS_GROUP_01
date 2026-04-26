package com.example.java_lms_group_01.Repository;

import com.example.java_lms_group_01.model.UserRecord;

import java.sql.SQLException;

public class UserProfileRepository {

    private final UserRepository userRepository = new UserRepository();

    public UserRecord findStudentProfile(String regNo) throws SQLException {
        return userRepository.findStudentProfile(regNo);
    }

    public UserRecord findLecturerProfile(String regNo) throws SQLException {
        return userRepository.findLecturerProfile(regNo);
    }

    public UserRecord findTechnicalOfficerProfile(String regNo) throws SQLException {
        return userRepository.findTechnicalOfficerProfile(regNo);
    }

    public void updateStudentProfile(String regNo, String email, String phone, String address,
                                     String image, String currentPw, String newPw) throws SQLException {
        userRepository.updateStudentProfile(regNo, email, phone, address, image, currentPw, newPw);
    }

    public void updateLecturerProfile(String regNo, String first, String last, String email,
                                      String address, String phone, String dep, String pos, String image)
            throws SQLException {
        userRepository.updateLecturerProfile(regNo, first, last, email, address, phone, dep, pos, image);
    }

    public void updateTechnicalOfficerProfile(String regNo, String first, String last,
                                              String email, String phone, String address, String image)
            throws SQLException {
        userRepository.updateTechnicalOfficerProfile(regNo, first, last, email, phone, address, image);
    }
}
