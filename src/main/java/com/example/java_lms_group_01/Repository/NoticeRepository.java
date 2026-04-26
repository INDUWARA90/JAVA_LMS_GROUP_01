package com.example.java_lms_group_01.Repository;

import com.example.java_lms_group_01.model.Notice;
import com.example.java_lms_group_01.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class NoticeRepository {

    public List<Notice> findAll() throws SQLException {
        String sql = "SELECT notice_id, notice_title, notice_content, publishDate, createdBy FROM notice ORDER BY publishDate DESC, notice_id DESC";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            List<Notice> notices = new ArrayList<>();
            while (rs.next()) {
                Date publishDate = rs.getDate("publishDate");
                notices.add(new Notice(
                        rs.getInt("notice_id"),
                        rs.getString("notice_title"),
                        rs.getString("notice_content"),
                        publishDate == null ? null : publishDate.toLocalDate(),
                        rs.getString("createdBy")
                ));
            }
            return notices;
        }
    }

    public List<Notice> findByKeyword(String keyword) throws SQLException {
        String sql = "SELECT notice_id, notice_title, notice_content, publishDate, createdBy FROM notice WHERE notice_title LIKE ? OR notice_content LIKE ? ORDER BY publishDate DESC, notice_id DESC";
        String pattern = "%" + (keyword == null ? "" : keyword.trim()) + "%";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, pattern);
            statement.setString(2, pattern);

            try (ResultSet rs = statement.executeQuery()) {
                List<Notice> notices = new ArrayList<>();
                while (rs.next()) {
                    Date publishDate = rs.getDate("publishDate");
                    notices.add(new Notice(
                            rs.getInt("notice_id"),
                            rs.getString("notice_title"),
                            rs.getString("notice_content"),
                            publishDate == null ? null : publishDate.toLocalDate(),
                            rs.getString("createdBy")
                    ));
                }
                return notices;
            }
        }
    }

    public boolean save(Notice notice) throws SQLException {
        String sql = "INSERT INTO notice (notice_title, notice_content, publishDate, createdBy) VALUES (?, ?, ?, ?)";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, notice.getTitle());
            statement.setString(2, notice.getContent());
            if (notice.getPublishDate() == null) {
                statement.setDate(3, null);
            } else {
                statement.setDate(3, Date.valueOf(notice.getPublishDate()));
            }
            statement.setString(4, notice.getCreatedBy());

            if (statement.executeUpdate() <= 0) {
                return false;
            }

            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    notice.setNoticeId(rs.getInt(1));
                }
            }

            return true;
        }
    }

    public boolean update(Notice notice) throws SQLException {
        String sql = "UPDATE notice SET notice_title=?, notice_content=?, publishDate=?, createdBy=? WHERE notice_id=?";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, notice.getTitle());
            statement.setString(2, notice.getContent());
            if (notice.getPublishDate() == null) {
                statement.setDate(3, null);
            } else {
                statement.setDate(3, Date.valueOf(notice.getPublishDate()));
            }
            statement.setString(4, notice.getCreatedBy());
            statement.setInt(5, notice.getNoticeId());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteById(int noticeId) throws SQLException {
        String sql = "DELETE FROM notice WHERE notice_id = ?";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, noticeId);
            return statement.executeUpdate() > 0;
        }
    }

    public int countAll() throws SQLException {
        String sql = "SELECT COUNT(*) FROM notice";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
}
