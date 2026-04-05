package com.example.java_lms_group_01.Controller.TechnicalOfficer;

import com.example.java_lms_group_01.Repository.NoticeRepository;
import com.example.java_lms_group_01.model.Notice;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class TechnicalOfficerNoticesController {

    @FXML
    private TextField txtSearch;
    @FXML
    private DatePicker dpPublishedDate;
    @FXML
    private TableView<NoticeRow> tblNotices;
    @FXML
    private TableColumn<NoticeRow, String> colNoticeId;
    @FXML
    private TableColumn<NoticeRow, String> colTitle;
    @FXML
    private TableColumn<NoticeRow, String> colContent;
    @FXML
    private TableColumn<NoticeRow, String> colPublishDate;
    @FXML
    private TableColumn<NoticeRow, String> colCreatedBy;

    private final NoticeRepository noticeRepository = new NoticeRepository();

    @FXML
    public void initialize() {
        colNoticeId.setCellValueFactory(d -> d.getValue().noticeIdProperty());
        colTitle.setCellValueFactory(d -> d.getValue().titleProperty());
        colContent.setCellValueFactory(d -> d.getValue().contentProperty());
        colPublishDate.setCellValueFactory(d -> d.getValue().publishDateProperty());
        colCreatedBy.setCellValueFactory(d -> d.getValue().createdByProperty());
        loadNotices(null, null);
    }

    @FXML
    private void searchNotices(ActionEvent event) {
        loadNotices(txtSearch.getText(), dpPublishedDate.getValue());
    }

    @FXML
    private void refreshNotices(ActionEvent event) {
        txtSearch.clear();
        dpPublishedDate.setValue(null);
        loadNotices(null, null);
    }

    private void loadNotices(String keyword, LocalDate publishedDate) {
        try {
            List<Notice> notices = noticeRepository.findByKeyword(keyword);
            if (publishedDate != null) {
                notices = notices.stream()
                        .filter(n -> publishedDate.equals(n.getPublishDate()))
                        .collect(Collectors.toList());
            }

            List<NoticeRow> rows = notices.stream()
                    .map(n -> new NoticeRow(
                            String.valueOf(n.getNoticeId()),
                            safe(n.getTitle()),
                            safe(n.getContent()),
                            n.getPublishDate() == null ? "" : n.getPublishDate().toString(),
                            safe(n.getCreatedBy())
                    ))
                    .collect(Collectors.toList());

            tblNotices.getItems().setAll(rows);
        } catch (SQLException e) {
            showError("Failed to load notices.", e);
        }
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private void showError(String message, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Database Error");
        alert.setHeaderText(null);
        alert.setContentText(message + "\n" + e.getMessage());
        alert.showAndWait();
    }

    public static class NoticeRow {
        private final SimpleStringProperty noticeId;
        private final SimpleStringProperty title;
        private final SimpleStringProperty content;
        private final SimpleStringProperty publishDate;
        private final SimpleStringProperty createdBy;

        public NoticeRow(String noticeId, String title, String content, String publishDate, String createdBy) {
            this.noticeId = new SimpleStringProperty(noticeId);
            this.title = new SimpleStringProperty(title);
            this.content = new SimpleStringProperty(content);
            this.publishDate = new SimpleStringProperty(publishDate);
            this.createdBy = new SimpleStringProperty(createdBy);
        }

        public SimpleStringProperty noticeIdProperty() { return noticeId; }
        public SimpleStringProperty titleProperty() { return title; }
        public SimpleStringProperty contentProperty() { return content; }
        public SimpleStringProperty publishDateProperty() { return publishDate; }
        public SimpleStringProperty createdByProperty() { return createdBy; }

        public String getTitle() { return title.get(); }
        public String getContent() { return content.get(); }
        public String getPublishDate() { return publishDate.get(); }
    }
}
