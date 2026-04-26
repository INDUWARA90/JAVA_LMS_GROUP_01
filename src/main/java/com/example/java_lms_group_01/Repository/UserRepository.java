package com.example.java_lms_group_01.Repository;

import com.example.java_lms_group_01.model.UserRecord;
import com.example.java_lms_group_01.util.DBConnection;
import com.example.java_lms_group_01.util.PasswordUtil;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    private final UserImageRepository userImageRepository = new UserImageRepository();

    public List<UserRecord> findAdmins() throws SQLException {
        String sql = "SELECT u.user_id, u.firstName, u.lastName, u.email, u.address, u.phoneNumber, u.dateOfBirth, u.gender, a.registrationNo FROM users u INNER JOIN admin a ON u.user_id = a.registrationNo ORDER BY a.registrationNo";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            List<UserRecord> list = new ArrayList<>();
            while (rs.next()) {
                Date dob = rs.getDate("dateOfBirth");
                list.add(new UserRecord(
                        rs.getString("user_id"),
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("email"),
                        rs.getString("address"),
                        rs.getString("phoneNumber"),
                        dob == null ? null : dob.toLocalDate(),
                        rs.getString("gender"),
                        "Admin",
                        rs.getString("registrationNo"),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                ));
            }
            return list;
        }
    }

    public List<UserRecord> findLecturers() throws SQLException {
        String sql = "SELECT u.user_id, u.firstName, u.lastName, u.email, u.address, u.phoneNumber, u.dateOfBirth, u.gender, l.registrationNo, l.department, l.position FROM users u INNER JOIN lecturer l ON u.user_id = l.registrationNo ORDER BY l.registrationNo";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            List<UserRecord> list = new ArrayList<>();
            while (rs.next()) {
                Date dob = rs.getDate("dateOfBirth");
                list.add(new UserRecord(
                        rs.getString("user_id"),
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("email"),
                        rs.getString("address"),
                        rs.getString("phoneNumber"),
                        dob == null ? null : dob.toLocalDate(),
                        rs.getString("gender"),
                        "Lecturer",
                        rs.getString("registrationNo"),
                        null,
                        rs.getString("department"),
                        null,
                        null,
                        null,
                        rs.getString("position"),
                        null
                ));
            }
            return list;
        }
    }

    public List<UserRecord> findStudents() throws SQLException {
        String sql = "SELECT u.user_id, u.firstName, u.lastName, u.email, u.address, u.phoneNumber, u.dateOfBirth, u.gender, s.registrationNo, s.department, s.batch, s.GPA, s.status FROM users u INNER JOIN student s ON u.user_id = s.registrationNo ORDER BY s.registrationNo";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            List<UserRecord> list = new ArrayList<>();
            while (rs.next()) {
                Date dob = rs.getDate("dateOfBirth");
                list.add(new UserRecord(
                        rs.getString("user_id"),
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("email"),
                        rs.getString("address"),
                        rs.getString("phoneNumber"),
                        dob == null ? null : dob.toLocalDate(),
                        rs.getString("gender"),
                        "Student",
                        rs.getString("registrationNo"),
                        null,
                        rs.getString("department"),
                        rs.getString("batch"),
                        rs.getObject("GPA") == null ? null : ((Number) rs.getObject("GPA")).doubleValue(),
                        rs.getString("status"),
                        null,
                        null
                ));
            }
            return list;
        }
    }

    public List<UserRecord> findTechnicalOfficers() throws SQLException {
        String sql = "SELECT u.user_id, u.firstName, u.lastName, u.email, u.address, u.phoneNumber, u.dateOfBirth, u.gender, t.registrationNo, t.department FROM users u INNER JOIN tech_officer t ON u.user_id = t.registrationNo ORDER BY t.registrationNo";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            List<UserRecord> list = new ArrayList<>();
            while (rs.next()) {
                Date dob = rs.getDate("dateOfBirth");
                list.add(new UserRecord(
                        rs.getString("user_id"),
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("email"),
                        rs.getString("address"),
                        rs.getString("phoneNumber"),
                        dob == null ? null : dob.toLocalDate(),
                        rs.getString("gender"),
                        "TechnicalOfficer",
                        rs.getString("registrationNo"),
                        null,
                        rs.getString("department"),
                        null,
                        null,
                        null,
                        null,
                        null
                ));
            }
            return list;
        }
    }

    public boolean createAdmin(UserRecord row) throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection()) {
            connection.setAutoCommit(false);

            try {
                try (PreparedStatement userStatement = connection.prepareStatement("INSERT INTO users (user_id, firstName, lastName, email, address, phoneNumber, dateOfBirth, gender) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
                    userStatement.setString(1, row.getRegistrationNo());
                    userStatement.setString(2, row.getFirstName());
                    userStatement.setString(3, row.getLastName());
                    userStatement.setString(4, row.getEmail());
                    userStatement.setString(5, row.getAddress());
                    userStatement.setString(6, row.getPhoneNumber());
                    if (row.getDateOfBirth() == null) userStatement.setNull(7, Types.DATE); else userStatement.setDate(7, Date.valueOf(row.getDateOfBirth()));
                    userStatement.setString(8, row.getGender());
                    userStatement.executeUpdate();
                }

                try (PreparedStatement adminStatement = connection.prepareStatement("INSERT INTO admin (registrationNo, password) VALUES (?, ?)")) {
                    adminStatement.setString(1, row.getRegistrationNo());
                    adminStatement.setString(2, PasswordUtil.hashPassword(row.getPassword()));
                    boolean inserted = adminStatement.executeUpdate() > 0;
                    connection.commit();
                    return inserted;
                }
            } catch (SQLException | RuntimeException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public boolean createLecturer(UserRecord row) throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection()) {
            connection.setAutoCommit(false);

            try {
                try (PreparedStatement userStatement = connection.prepareStatement("INSERT INTO users (user_id, firstName, lastName, email, address, phoneNumber, dateOfBirth, gender) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
                    userStatement.setString(1, row.getRegistrationNo());
                    userStatement.setString(2, row.getFirstName());
                    userStatement.setString(3, row.getLastName());
                    userStatement.setString(4, row.getEmail());
                    userStatement.setString(5, row.getAddress());
                    userStatement.setString(6, row.getPhoneNumber());
                    if (row.getDateOfBirth() == null) userStatement.setNull(7, Types.DATE); else userStatement.setDate(7, Date.valueOf(row.getDateOfBirth()));
                    userStatement.setString(8, row.getGender());
                    userStatement.executeUpdate();
                }

                try (PreparedStatement lecturerStatement = connection.prepareStatement("INSERT INTO lecturer (registrationNo, password, department, position) VALUES (?, ?, ?, ?)")) {
                    lecturerStatement.setString(1, row.getRegistrationNo());
                    lecturerStatement.setString(2, PasswordUtil.hashPassword(row.getPassword()));
                    lecturerStatement.setString(3, row.getDepartment());
                    lecturerStatement.setString(4, row.getPosition());
                    boolean inserted = lecturerStatement.executeUpdate() > 0;
                    connection.commit();
                    return inserted;
                }
            } catch (SQLException | RuntimeException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public boolean createStudent(UserRecord row) throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection()) {
            connection.setAutoCommit(false);

            try {
                try (PreparedStatement userStatement = connection.prepareStatement("INSERT INTO users (user_id, firstName, lastName, email, address, phoneNumber, dateOfBirth, gender) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
                    userStatement.setString(1, row.getRegistrationNo());
                    userStatement.setString(2, row.getFirstName());
                    userStatement.setString(3, row.getLastName());
                    userStatement.setString(4, row.getEmail());
                    userStatement.setString(5, row.getAddress());
                    userStatement.setString(6, row.getPhoneNumber());
                    if (row.getDateOfBirth() == null) userStatement.setNull(7, Types.DATE); else userStatement.setDate(7, Date.valueOf(row.getDateOfBirth()));
                    userStatement.setString(8, row.getGender());
                    userStatement.executeUpdate();
                }

                try (PreparedStatement studentStatement = connection.prepareStatement("INSERT INTO student (registrationNo, password, department, batch, GPA, status) VALUES (?, ?, ?, ?, ?, ?)")) {
                    studentStatement.setString(1, row.getRegistrationNo());
                    studentStatement.setString(2, PasswordUtil.hashPassword(row.getPassword()));
                    studentStatement.setString(3, row.getDepartment());
                    studentStatement.setString(4, row.getBatch());
                    if (row.getGpa() == null) studentStatement.setNull(5, Types.DECIMAL); else studentStatement.setDouble(5, row.getGpa());
                    studentStatement.setString(6, row.getStatus());
                    boolean inserted = studentStatement.executeUpdate() > 0;
                    connection.commit();
                    return inserted;
                }
            } catch (SQLException | RuntimeException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public boolean createTechnicalOfficer(UserRecord row) throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection()) {
            connection.setAutoCommit(false);

            try {
                try (PreparedStatement userStatement = connection.prepareStatement("INSERT INTO users (user_id, firstName, lastName, email, address, phoneNumber, dateOfBirth, gender) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
                    userStatement.setString(1, row.getRegistrationNo());
                    userStatement.setString(2, row.getFirstName());
                    userStatement.setString(3, row.getLastName());
                    userStatement.setString(4, row.getEmail());
                    userStatement.setString(5, row.getAddress());
                    userStatement.setString(6, row.getPhoneNumber());
                    if (row.getDateOfBirth() == null) userStatement.setNull(7, Types.DATE); else userStatement.setDate(7, Date.valueOf(row.getDateOfBirth()));
                    userStatement.setString(8, row.getGender());
                    userStatement.executeUpdate();
                }

                try (PreparedStatement technicalOfficerStatement = connection.prepareStatement("INSERT INTO tech_officer (registrationNo, password, department) VALUES (?, ?, ?)")) {
                    technicalOfficerStatement.setString(1, row.getRegistrationNo());
                    technicalOfficerStatement.setString(2, PasswordUtil.hashPassword(row.getPassword()));
                    technicalOfficerStatement.setString(3, row.getDepartment());
                    boolean inserted = technicalOfficerStatement.executeUpdate() > 0;
                    connection.commit();
                    return inserted;
                }
            } catch (SQLException | RuntimeException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public boolean updateAdmin(UserRecord row) throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection()) {
            connection.setAutoCommit(false);

            try {
                try (PreparedStatement userStatement = connection.prepareStatement("UPDATE users SET firstName=?, lastName=?, email=?, address=?, phoneNumber=?, dateOfBirth=?, gender=? WHERE user_id=?")) {
                    userStatement.setString(1, row.getFirstName());
                    userStatement.setString(2, row.getLastName());
                    userStatement.setString(3, row.getEmail());
                    userStatement.setString(4, row.getAddress());
                    userStatement.setString(5, row.getPhoneNumber());
                    if (row.getDateOfBirth() == null) userStatement.setNull(6, Types.DATE); else userStatement.setDate(6, Date.valueOf(row.getDateOfBirth()));
                    userStatement.setString(7, row.getGender());
                    userStatement.setString(8, row.getRegistrationNo());
                    userStatement.executeUpdate();
                }

                if (row.getPassword() != null && !row.getPassword().trim().isEmpty()) {
                    try (PreparedStatement passwordStatement = connection.prepareStatement("UPDATE admin SET password=? WHERE registrationNo=?")) {
                        passwordStatement.setString(1, PasswordUtil.hashPassword(row.getPassword()));
                        passwordStatement.setString(2, row.getRegistrationNo());
                        passwordStatement.executeUpdate();
                    }
                }

                connection.commit();
                return true;
            } catch (SQLException | RuntimeException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public boolean updateLecturer(UserRecord row) throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection()) {
            connection.setAutoCommit(false);

            try {
                try (PreparedStatement userStatement = connection.prepareStatement("UPDATE users SET firstName=?, lastName=?, email=?, address=?, phoneNumber=?, dateOfBirth=?, gender=? WHERE user_id=?")) {
                    userStatement.setString(1, row.getFirstName());
                    userStatement.setString(2, row.getLastName());
                    userStatement.setString(3, row.getEmail());
                    userStatement.setString(4, row.getAddress());
                    userStatement.setString(5, row.getPhoneNumber());
                    if (row.getDateOfBirth() == null) userStatement.setNull(6, Types.DATE); else userStatement.setDate(6, Date.valueOf(row.getDateOfBirth()));
                    userStatement.setString(7, row.getGender());
                    userStatement.setString(8, row.getRegistrationNo());
                    userStatement.executeUpdate();
                }

                try (PreparedStatement lecturerStatement = connection.prepareStatement("UPDATE lecturer SET department=?, position=? WHERE registrationNo=?")) {
                    lecturerStatement.setString(1, row.getDepartment());
                    lecturerStatement.setString(2, row.getPosition());
                    lecturerStatement.setString(3, row.getRegistrationNo());
                    lecturerStatement.executeUpdate();
                }

                if (row.getPassword() != null && !row.getPassword().trim().isEmpty()) {
                    try (PreparedStatement passwordStatement = connection.prepareStatement("UPDATE lecturer SET password=? WHERE registrationNo=?")) {
                        passwordStatement.setString(1, PasswordUtil.hashPassword(row.getPassword()));
                        passwordStatement.setString(2, row.getRegistrationNo());
                        passwordStatement.executeUpdate();
                    }
                }

                connection.commit();
                return true;
            } catch (SQLException | RuntimeException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public boolean updateStudent(UserRecord row) throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection()) {
            connection.setAutoCommit(false);

            try {
                try (PreparedStatement userStatement = connection.prepareStatement("UPDATE users SET firstName=?, lastName=?, email=?, address=?, phoneNumber=?, dateOfBirth=?, gender=? WHERE user_id=?")) {
                    userStatement.setString(1, row.getFirstName());
                    userStatement.setString(2, row.getLastName());
                    userStatement.setString(3, row.getEmail());
                    userStatement.setString(4, row.getAddress());
                    userStatement.setString(5, row.getPhoneNumber());
                    if (row.getDateOfBirth() == null) userStatement.setNull(6, Types.DATE); else userStatement.setDate(6, Date.valueOf(row.getDateOfBirth()));
                    userStatement.setString(7, row.getGender());
                    userStatement.setString(8, row.getRegistrationNo());
                    userStatement.executeUpdate();
                }

                try (PreparedStatement studentStatement = connection.prepareStatement("UPDATE student SET department=?, batch=?, GPA=?, status=? WHERE registrationNo=?")) {
                    studentStatement.setString(1, row.getDepartment());
                    studentStatement.setString(2, row.getBatch());
                    if (row.getGpa() == null) studentStatement.setNull(3, Types.DECIMAL); else studentStatement.setDouble(3, row.getGpa());
                    studentStatement.setString(4, row.getStatus());
                    studentStatement.setString(5, row.getRegistrationNo());
                    studentStatement.executeUpdate();
                }

                if (row.getPassword() != null && !row.getPassword().trim().isEmpty()) {
                    try (PreparedStatement passwordStatement = connection.prepareStatement("UPDATE student SET password=? WHERE registrationNo=?")) {
                        passwordStatement.setString(1, PasswordUtil.hashPassword(row.getPassword()));
                        passwordStatement.setString(2, row.getRegistrationNo());
                        passwordStatement.executeUpdate();
                    }
                }

                connection.commit();
                return true;
            } catch (SQLException | RuntimeException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public boolean updateTechnicalOfficer(UserRecord row) throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection()) {
            connection.setAutoCommit(false);

            try {
                try (PreparedStatement userStatement = connection.prepareStatement("UPDATE users SET firstName=?, lastName=?, email=?, address=?, phoneNumber=?, dateOfBirth=?, gender=? WHERE user_id=?")) {
                    userStatement.setString(1, row.getFirstName());
                    userStatement.setString(2, row.getLastName());
                    userStatement.setString(3, row.getEmail());
                    userStatement.setString(4, row.getAddress());
                    userStatement.setString(5, row.getPhoneNumber());
                    if (row.getDateOfBirth() == null) userStatement.setNull(6, Types.DATE); else userStatement.setDate(6, Date.valueOf(row.getDateOfBirth()));
                    userStatement.setString(7, row.getGender());
                    userStatement.setString(8, row.getRegistrationNo());
                    userStatement.executeUpdate();
                }

                try (PreparedStatement technicalOfficerStatement = connection.prepareStatement("UPDATE tech_officer SET department=? WHERE registrationNo=?")) {
                    technicalOfficerStatement.setString(1, row.getDepartment());
                    technicalOfficerStatement.setString(2, row.getRegistrationNo());
                    technicalOfficerStatement.executeUpdate();
                }

                if (row.getPassword() != null && !row.getPassword().trim().isEmpty()) {
                    try (PreparedStatement passwordStatement = connection.prepareStatement("UPDATE tech_officer SET password=? WHERE registrationNo=?")) {
                        passwordStatement.setString(1, PasswordUtil.hashPassword(row.getPassword()));
                        passwordStatement.setString(2, row.getRegistrationNo());
                        passwordStatement.executeUpdate();
                    }
                }

                connection.commit();
                return true;
            } catch (SQLException | RuntimeException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public boolean deleteAdmin(String registrationNo) throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection()) {
            connection.setAutoCommit(false);

            try {
                try (PreparedStatement roleStatement = connection.prepareStatement("DELETE FROM admin WHERE registrationNo=?")) {
                    roleStatement.setString(1, registrationNo);
                    roleStatement.executeUpdate();
                }

                try (PreparedStatement userStatement = connection.prepareStatement("DELETE FROM users WHERE user_id=?")) {
                    userStatement.setString(1, registrationNo);
                    boolean deleted = userStatement.executeUpdate() > 0;
                    connection.commit();
                    return deleted;
                }
            } catch (SQLException | RuntimeException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public boolean deleteLecturer(String registrationNo) throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection()) {
            connection.setAutoCommit(false);

            try {
                try (PreparedStatement roleStatement = connection.prepareStatement("DELETE FROM lecturer WHERE registrationNo=?")) {
                    roleStatement.setString(1, registrationNo);
                    roleStatement.executeUpdate();
                }

                try (PreparedStatement userStatement = connection.prepareStatement("DELETE FROM users WHERE user_id=?")) {
                    userStatement.setString(1, registrationNo);
                    boolean deleted = userStatement.executeUpdate() > 0;
                    connection.commit();
                    return deleted;
                }
            } catch (SQLException | RuntimeException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public boolean deleteStudent(String registrationNo) throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection()) {
            connection.setAutoCommit(false);

            try {
                try (PreparedStatement roleStatement = connection.prepareStatement("DELETE FROM student WHERE registrationNo=?")) {
                    roleStatement.setString(1, registrationNo);
                    roleStatement.executeUpdate();
                }

                try (PreparedStatement userStatement = connection.prepareStatement("DELETE FROM users WHERE user_id=?")) {
                    userStatement.setString(1, registrationNo);
                    boolean deleted = userStatement.executeUpdate() > 0;
                    connection.commit();
                    return deleted;
                }
            } catch (SQLException | RuntimeException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public boolean deleteTechnicalOfficer(String registrationNo) throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection()) {
            connection.setAutoCommit(false);

            try {
                try (PreparedStatement roleStatement = connection.prepareStatement("DELETE FROM tech_officer WHERE registrationNo=?")) {
                    roleStatement.setString(1, registrationNo);
                    roleStatement.executeUpdate();
                }

                try (PreparedStatement userStatement = connection.prepareStatement("DELETE FROM users WHERE user_id=?")) {
                    userStatement.setString(1, registrationNo);
                    boolean deleted = userStatement.executeUpdate() > 0;
                    connection.commit();
                    return deleted;
                }
            } catch (SQLException | RuntimeException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public UserRecord findStudentProfile(String registrationNo) throws SQLException {
        String sql = "SELECT u.user_id, u.firstName, u.lastName, u.email, u.address, u.phoneNumber, s.department, s.batch, s.GPA, s.status FROM users u INNER JOIN student s ON u.user_id = s.registrationNo WHERE s.registrationNo = ?";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, registrationNo);

            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                return new UserRecord(
                        rs.getString("user_id"),
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("email"),
                        rs.getString("address"),
                        rs.getString("phoneNumber"),
                        null,
                        null,
                        "Student",
                        rs.getString("user_id"),
                        null,
                        rs.getString("department"),
                        rs.getString("batch"),
                        rs.getObject("GPA") == null ? null : ((Number) rs.getObject("GPA")).doubleValue(),
                        rs.getString("status"),
                        null,
                        userImageRepository.findImagePathByUserId(connection, registrationNo)
                );
            }
        }
    }

    public UserRecord findLecturerProfile(String registrationNo) throws SQLException {
        String sql = "SELECT u.user_id, u.firstName, u.lastName, u.email, u.address, u.phoneNumber, l.department, l.position FROM users u INNER JOIN lecturer l ON u.user_id = l.registrationNo WHERE l.registrationNo = ?";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, registrationNo);

            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                return new UserRecord(
                        rs.getString("user_id"),
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("email"),
                        rs.getString("address"),
                        rs.getString("phoneNumber"),
                        null,
                        null,
                        "Lecturer",
                        rs.getString("user_id"),
                        null,
                        rs.getString("department"),
                        null,
                        null,
                        null,
                        rs.getString("position"),
                        userImageRepository.findImagePathByUserId(connection, registrationNo)
                );
            }
        }
    }

    public UserRecord findTechnicalOfficerProfile(String registrationNo) throws SQLException {
        String sql = "SELECT u.user_id, u.firstName, u.lastName, u.email, u.address, u.phoneNumber, t.department FROM users u INNER JOIN tech_officer t ON u.user_id = t.registrationNo WHERE t.registrationNo = ?";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, registrationNo);

            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                return new UserRecord(
                        rs.getString("user_id"),
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("email"),
                        rs.getString("address"),
                        rs.getString("phoneNumber"),
                        null,
                        null,
                        "TechnicalOfficer",
                        rs.getString("user_id"),
                        null,
                        rs.getString("department"),
                        null,
                        null,
                        null,
                        null,
                        userImageRepository.findImagePathByUserId(connection, registrationNo)
                );
            }
        }
    }

    public void updateStudentProfile(String registrationNo, String email, String phone, String address, String image, String currentPw, String newPw) throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection()) {
            connection.setAutoCommit(false);

            try {
                try (PreparedStatement statement = connection.prepareStatement("UPDATE users SET email=?, phoneNumber=?, address=? WHERE user_id=?")) {
                    statement.setString(1, email);
                    statement.setString(2, phone);
                    statement.setString(3, address);
                    statement.setString(4, registrationNo);
                    statement.executeUpdate();
                }

                if (newPw != null && !newPw.trim().isEmpty()) {
                    String storedPassword = null;

                    try (PreparedStatement statement = connection.prepareStatement("SELECT password FROM student WHERE registrationNo=?")) {
                        statement.setString(1, registrationNo);
                        try (ResultSet rs = statement.executeQuery()) {
                            if (rs.next()) {
                                storedPassword = rs.getString("password");
                            }
                        }
                    }

                    if (storedPassword == null || !PasswordUtil.matches(currentPw, storedPassword)) {
                        throw new IllegalArgumentException("Wrong current password");
                    }

                    try (PreparedStatement statement = connection.prepareStatement("UPDATE student SET password=? WHERE registrationNo=?")) {
                        statement.setString(1, PasswordUtil.hashPassword(newPw));
                        statement.setString(2, registrationNo);
                        statement.executeUpdate();
                    }
                }

                userImageRepository.upsertImagePath(connection, registrationNo, image);
                connection.commit();
            } catch (SQLException | RuntimeException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public void updateLecturerProfile(String registrationNo, String firstName, String lastName, String email, String address, String phone, String department, String position, String image) throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection()) {
            connection.setAutoCommit(false);

            try {
                try (PreparedStatement userStatement = connection.prepareStatement("UPDATE users SET firstName=?, lastName=?, email=?, address=?, phoneNumber=? WHERE user_id=?")) {
                    userStatement.setString(1, firstName);
                    userStatement.setString(2, lastName);
                    userStatement.setString(3, email);
                    userStatement.setString(4, address);
                    userStatement.setString(5, phone);
                    userStatement.setString(6, registrationNo);
                    userStatement.executeUpdate();
                }

                try (PreparedStatement lecturerStatement = connection.prepareStatement("UPDATE lecturer SET department=?, position=? WHERE registrationNo=?")) {
                    lecturerStatement.setString(1, department);
                    lecturerStatement.setString(2, position);
                    lecturerStatement.setString(3, registrationNo);
                    lecturerStatement.executeUpdate();
                }

                userImageRepository.upsertImagePath(connection, registrationNo, image);
                connection.commit();
            } catch (SQLException | RuntimeException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public void updateTechnicalOfficerProfile(String registrationNo, String firstName, String lastName, String email, String phone, String address, String image) throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection()) {
            connection.setAutoCommit(false);

            try {
                try (PreparedStatement statement = connection.prepareStatement("UPDATE users SET firstName=?, lastName=?, email=?, phoneNumber=?, address=? WHERE user_id=?")) {
                    statement.setString(1, firstName);
                    statement.setString(2, lastName);
                    statement.setString(3, email);
                    statement.setString(4, phone);
                    statement.setString(5, address);
                    statement.setString(6, registrationNo);
                    statement.executeUpdate();
                }

                userImageRepository.upsertImagePath(connection, registrationNo, image);
                connection.commit();
            } catch (SQLException | RuntimeException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }
}
