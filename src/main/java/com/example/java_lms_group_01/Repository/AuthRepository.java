package com.example.java_lms_group_01.Repository;

import com.example.java_lms_group_01.model.users.UserRole;
import com.example.java_lms_group_01.util.DBConnection;
import com.example.java_lms_group_01.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthRepository {

    public UserRole findRoleByRegistrationNo(String registrationNo, String rawPassword) throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT password FROM admin WHERE registrationNo = ?")) {
                statement.setString(1, registrationNo);
                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        String storedPassword = rs.getString("password");
                        if (storedPassword != null && PasswordUtil.matches(rawPassword, storedPassword)) {
                            return UserRole.ADMIN;
                        }
                    }
                }
            }

            try (PreparedStatement statement = connection.prepareStatement("SELECT password FROM lecturer WHERE registrationNo = ?")) {
                statement.setString(1, registrationNo);
                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        String storedPassword = rs.getString("password");
                        if (storedPassword != null && PasswordUtil.matches(rawPassword, storedPassword)) {
                            return UserRole.LECTURER;
                        }
                    }
                }
            }

            try (PreparedStatement statement = connection.prepareStatement("SELECT password FROM student WHERE registrationNo = ?")) {
                statement.setString(1, registrationNo);
                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        String storedPassword = rs.getString("password");
                        if (storedPassword != null && PasswordUtil.matches(rawPassword, storedPassword)) {
                            return UserRole.STUDENT;
                        }
                    }
                }
            }

            try (PreparedStatement statement = connection.prepareStatement("SELECT password FROM tech_officer WHERE registrationNo = ?")) {
                statement.setString(1, registrationNo);
                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        String storedPassword = rs.getString("password");
                        if (storedPassword != null && PasswordUtil.matches(rawPassword, storedPassword)) {
                            return UserRole.TECHNICAL_OFFICER;
                        }
                    }
                }
            }
        }

        return null;
    }
}
