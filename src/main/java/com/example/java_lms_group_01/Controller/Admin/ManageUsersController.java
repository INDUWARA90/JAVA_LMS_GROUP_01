package com.example.java_lms_group_01.Controller.Admin;

import com.example.java_lms_group_01.Repository.AdminRepository;
import com.example.java_lms_group_01.model.UserRecord;
import com.example.java_lms_group_01.model.users.UserRole;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;

public class ManageUsersController implements Initializable {

    // ========== FXML UI Components for Tab Navigation ==========
    @FXML
    private TabPane tabUsers;
    @FXML
    private Tab tabAdmins;
    @FXML
    private Tab tabLecturers;
    @FXML
    private Tab tabStudents;
    @FXML
    private Tab tabTechnicalOfficers;

    // ========== FXML Table Columns for Admin Users ==========
    @FXML
    private TableColumn<UserRecord, String> adminAccessLevel;
    @FXML
    private TableColumn<UserRecord, String> adminDeptId;
    @FXML
    private TableColumn<UserRecord, String> adminEmail;
    @FXML
    private TableColumn<UserRecord, String> adminFirstName;
    @FXML
    private TableColumn<UserRecord, String> adminGender;
    @FXML
    private TableColumn<UserRecord, String> adminId;
    @FXML
    private TableColumn<UserRecord, String> adminLastName;
    @FXML
    private TableColumn<UserRecord, String> adminPhone;
    @FXML
    private TableView<UserRecord> tblAdmins;

    // ========== FXML Table Columns for Lecturer Users ==========
    @FXML
    private TableColumn<UserRecord, String> lecDeptId;
    @FXML
    private TableColumn<UserRecord, String> lecEmail;
    @FXML
    private TableColumn<UserRecord, String> lecFirstName;
    @FXML
    private TableColumn<UserRecord, String> lecGender;
    @FXML
    private TableColumn<UserRecord, String> lecId;
    @FXML
    private TableColumn<UserRecord, String> lecLastName;
    @FXML
    private TableColumn<UserRecord, String> lecPhone;
    @FXML
    private TableColumn<UserRecord, String> lecPosition;
    @FXML
    private TableColumn<UserRecord, String> lecRegNo;
    @FXML
    private TableView<UserRecord> tblLecturers;

    // ========== FXML Table Columns for Student Users ==========
    @FXML
    private TableColumn<UserRecord, String> stuBatchId;
    @FXML
    private TableColumn<UserRecord, String> stuDeptId;
    @FXML
    private TableColumn<UserRecord, String> stuEmail;
    @FXML
    private TableColumn<UserRecord, String> stuFirstName;
    @FXML
    private TableColumn<UserRecord, String> stuGender;
    @FXML
    private TableColumn<UserRecord, String> stuId;
    @FXML
    private TableColumn<UserRecord, String> stuLastName;
    @FXML
    private TableColumn<UserRecord, String> stuPhone;
    @FXML
    private TableColumn<UserRecord, String> stuRegNo;
    @FXML
    private TableColumn<UserRecord, String> stuStatus;
    @FXML
    private TableView<UserRecord> tblStudents;

    // ========== FXML Table Columns for Technical Officer Users ==========
    @FXML
    private TableColumn<UserRecord, String> toDeptId;
    @FXML
    private TableColumn<UserRecord, String> toEmail;
    @FXML
    private TableColumn<UserRecord, String> toFirstName;
    @FXML
    private TableColumn<UserRecord, String> toGender;
    @FXML
    private TableColumn<UserRecord, String> toId;
    @FXML
    private TableColumn<UserRecord, String> toLab;
    @FXML
    private TableColumn<UserRecord, String> toLastName;
    @FXML
    private TableColumn<UserRecord, String> toPhone;
    @FXML
    private TableColumn<UserRecord, String> toPosition;
    @FXML
    private TableColumn<UserRecord, String> toShift;
    @FXML
    private TableView<UserRecord> tblTechnicalOfficers;

    private final AdminRepository adminRepository = new AdminRepository();
    private Map<UserRole, Tab> tabs;
    private Map<UserRole, TableView<UserRecord>> tables;


    // Initializes UI by binding roles to views, configuring tables, and loading data
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        bindRoleViews();
        configureTables();
        loadAllTables();
    }


    // Refresh button: Reloads all user tables from database
    @FXML
    void btnOnActionRefresh(ActionEvent event) {
        loadAllTables();
    }

    // Add button: Opens dialog to create new user
    @FXML
    void btnOnActionAdd(ActionEvent event) {
        handleSave(false);
    }

    // Edit button: Opens dialog to update selected user
    @FXML
    void btnOnActionEdit(ActionEvent event) {
        handleSave(true);
    }

    // Delete button: Removes selected user after confirmation
    @FXML
    void btnOnActionDelete(ActionEvent event) {
        // Retrieve the currently active tab's user role
        UserRole role = getActiveRole();
        if (role == null) {
            showInfo("Please select a valid role tab.");
            return;
        }

        // Get the selected user from the active table
        UserRecord selected = getSelectedRowByRole(role);
        if (selected == null) {
            showInfo("Please select a row in the active tab.");
            return;
        }

        // Create and display confirmation dialog with user details
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setHeaderText("Delete " + role.getValue());
        confirmation.setContentText("Delete registration number " + selected.getRegistrationNo() + "?");
        Optional<ButtonType> answer = confirmation.showAndWait();
        // Exit if user cancels or dismisses the dialog
        if (answer.isEmpty() || answer.get() != ButtonType.OK) {
            return;
        }

        // Attempt to delete the user from database
        try {
            if (deleteUser(role, selected.getUserId())) {
                // Refresh all tables after successful deletion
                loadAllTables();
                showInfo(role.getValue() + " deleted successfully.");
            }
        } catch (SQLException e) {
            // Display error message if database operation fails
            showError("Failed to delete " + role.getValue() + ".", e);
        }
    }

    // ========== Save Operation Handler ==========
    // Orchestrates add/edit operations: validates selection, shows dialog, saves to database
    private void handleSave(boolean edit) {
        // Retrieve the currently active tab to determine user role
        UserRole role = getActiveRole();
        if (role == null) {
            showInfo("Please select a valid role tab.");
            return;
        }

        // Initialize selected record as null (for add operation)
        UserRecord selected = null;

        // If editing, retrieve the selected row from the active table
        if (edit) {
            selected = getSelectedRowByRole(role);
            if (selected == null) {
                showInfo("Please select a row in the active tab.");
                return;
            }
        }

        // Process add/edit in a try-catch block to handle errors gracefully
        try {
            // Display role-specific dialog form with pre-filled data (if editing)
            UserRecord row = showRoleDialog(role, selected);
            if (row == null) {
                // User cancelled the dialog
                return;
            }

            // Determine whether to add new user or update existing user
            boolean changed = edit ? updateUser(role, row) : addUser(role, row);
            if (changed) {
                // Refresh all tables to reflect database changes
                loadAllTables();
                // Display success message based on operation type
                showInfo(role.getValue() + (edit ? " updated successfully." : " created successfully."));
            }
        } catch (IllegalArgumentException e) {
            // Handle validation errors from form input
            showInfo(e.getMessage());
        } catch (SQLException e) {
            // Handle database-related errors
            showError("Failed to " + (edit ? "update " : "add ") + role.getValue() + ".", e);
        }
    }

    // ========== UI Component Binding Methods ==========
    // Maps each user role to its corresponding tab and table view
    private void bindRoleViews() {
        // Initialize map to store role-to-tab relationships
        tabs = new EnumMap<>(UserRole.class);
        tabs.put(UserRole.ADMIN, tabAdmins);
        tabs.put(UserRole.LECTURER, tabLecturers);
        tabs.put(UserRole.STUDENT, tabStudents);
        tabs.put(UserRole.TECHNICAL_OFFICER, tabTechnicalOfficers);

        // Initialize map to store role-to-table relationships
        tables = new EnumMap<>(UserRole.class);
        tables.put(UserRole.ADMIN, tblAdmins);
        tables.put(UserRole.LECTURER, tblLecturers);
        tables.put(UserRole.STUDENT, tblStudents);
        tables.put(UserRole.TECHNICAL_OFFICER, tblTechnicalOfficers);
    }

    // Sets up all table columns with their data bindings
    private void configureTables() {
        configureAdminTable();
        configureLecturerTable();
        configureStudentTable();
        configureTechnicalOfficerTable();
    }

    // ========== Role and Selection Query Methods ==========
    // Retrieves the currently selected tab's user role
    private UserRole getActiveRole() {
        // Get the currently selected tab from the TabPane
        Tab selectedTab = tabUsers.getSelectionModel().getSelectedItem();
        // Iterate through role-tab mappings to find matching role
        for (Map.Entry<UserRole, Tab> entry : tabs.entrySet()) {
            if (entry.getValue() == selectedTab) {
                return entry.getKey();
            }
        }
        // Return null if no matching role found (invalid state)
        return null;
    }

    // Gets the user record selected in the table for the given role
    private UserRecord getSelectedRowByRole(UserRole role) {
        // Retrieve table for the specified role and get selected item
        return tableFor(role).getSelectionModel().getSelectedItem();
    }

    // ========== Dialog and Form Creation Methods ==========
    // Shows dialog form for creating or editing a user, returns filled UserRecord
    private UserRecord showRoleDialog(UserRole role, UserRecord existing) {
        // Determine if this is an edit or add operation
        boolean edit = existing != null;
        // Create base dialog with appropriate title
        Dialog<UserRecord> dialog = baseDialog(dialogTitle(role, edit), edit);
        // Create form with input fields pre-filled if editing
        RoleForm form = createRoleForm(existing);

        // Build and set the form grid layout in the dialog
        dialog.getDialogPane().setContent(buildFormGrid(role, form, edit));
        // Define what happens when user clicks OK or Cancel
        dialog.setResultConverter(button ->
                button.getButtonData() == ButtonBar.ButtonData.OK_DONE
                        ? buildUserRecord(role, existing, form, edit)
                        : null);

        // Display dialog and return the result (null if cancelled)
        return dialog.showAndWait().orElse(null);
    }

    // Creates base dialog template with title and buttons
    private Dialog<UserRecord> baseDialog(String title, boolean edit) {
        // Initialize new dialog for user input
        Dialog<UserRecord> dialog = new Dialog<>();
        dialog.setTitle(title);
        // Set header text based on operation type (add vs edit)
        dialog.setHeaderText(edit ? "Update selected record." : "Enter details.");
        // Create save button with appropriate label
        ButtonType save = new ButtonType(edit ? "Update" : "Save", ButtonBar.ButtonData.OK_DONE);
        // Add Save/Update and Cancel buttons to dialog
        dialog.getDialogPane().getButtonTypes().addAll(save, ButtonType.CANCEL);
        return dialog;
    }

    // Generates appropriate dialog title based on operation and role
    private String dialogTitle(UserRole role, boolean edit) {
        return (edit ? "Edit " : "Add ") + role.getValue();
    }

    // Creates form with all input fields needed for a user role
    private RoleForm createRoleForm(UserRecord existing) {
        // Create common fields shared by all user types
        UserFormFields common = createCommonFields(existing);
        // Return form record with all input fields for the role
        return new RoleForm(
                common,
                dateOfBirthPicker(existing),
                genderBox(existing),
                // Pre-fill registration number if editing
                textField(existing == null ? "" : value(existing.getRegistrationNo())),
                new PasswordField(),
                // Pre-fill department if editing
                textField(existing == null ? "" : value(existing.getDepartment())),
                // Pre-fill batch if editing
                textField(existing == null ? "" : value(existing.getBatch())),
                // Pre-fill GPA if editing and value exists
                textField(existing != null && existing.getGpa() != null ? value(existing.getGpa()) : ""),
                statusBox(existing),
                // Pre-fill position if editing
                textField(existing == null ? "" : value(existing.getPosition()))
        );
    }

    // ========== Form Grid Building Methods ==========
    // Arranges form fields in grid layout based on user role
    private GridPane buildFormGrid(UserRole role, RoleForm form, boolean edit) {
        // Create grid pane with horizontal and vertical spacing
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        // Add common fields (first name, last name, email, etc.)
        int row = addCommonGrid(grid, form.commonFields(), form.dob(), form.gender());
        // Add registration number field
        row = addRow(grid, "Registration No:", form.registrationNoField(), row);
        // Add password field (required for new users, optional for editing)
        row = addRow(grid, edit ? "New Password (optional):" : "Password:", form.passwordField(), row);

        // Add role-specific fields based on user type
        if (role == UserRole.LECTURER) {
            row = addRow(grid, "Department:", form.departmentField(), row);
            row = addRow(grid, "Position:", form.positionField(), row);
        } else if (role == UserRole.STUDENT) {
            row = addRow(grid, "Department:", form.departmentField(), row);
            row = addRow(grid, "Batch:", form.batchField(), row);
            row = addRow(grid, "GPA (optional):", form.gpaField(), row);
            row = addRow(grid, "Status:", form.statusField(), row);
        }

        return grid;
    }

    // ========== User Record Construction ==========
    // Constructs UserRecord object from form inputs, role-specific field handling
    private UserRecord buildUserRecord(UserRole role, UserRecord existing, RoleForm form, boolean edit) {
        // Validate and extract required registration number field
        String registrationNo = required(form.registrationNoField(), "Registration No");
        // Validate password (required only for new users)
        String password = requirePasswordForCreate(edit, form.passwordField());
        // Use existing user ID for edits, registration number for new users
        String userId = edit ? existing.getUserId() : registrationNo;

        // Create appropriate UserRecord based on role type
        return switch (role) {
            // Construct Admin user record with relevant fields
            case ADMIN -> new UserRecord(
                    userId,
                    required(form.commonFields().getFirstNameField(), "First name"),
                    required(form.commonFields().getLastNameField(), "Last name"),
                    required(form.commonFields().getEmailField(), "Email"),
                    value(form.commonFields().getAddressField()),
                    value(form.commonFields().getPhoneField()),
                    form.dob().getValue(),
                    form.gender().getValue(),
                    UserRole.ADMIN.getValue(),
                    registrationNo,
                    password,
                    null,
                    null,
                    null,
                    null,
                    null,
                    value(form.commonFields().getImagePathField())
            );
            // Construct Lecturer user record with department and position
            case LECTURER -> new UserRecord(
                    userId,
                    required(form.commonFields().getFirstNameField(), "First name"),
                    required(form.commonFields().getLastNameField(), "Last name"),
                    required(form.commonFields().getEmailField(), "Email"),
                    value(form.commonFields().getAddressField()),
                    value(form.commonFields().getPhoneField()),
                    form.dob().getValue(),
                    form.gender().getValue(),
                    UserRole.LECTURER.getValue(),
                    registrationNo,
                    password,
                    required(form.departmentField(), "Department"),
                    null,
                    null,
                    null,
                    required(form.positionField(), "Position"),
                    value(form.commonFields().getImagePathField())
            );
            // Construct Student user record with batch, GPA, and status
            case STUDENT -> new UserRecord(
                    userId,
                    required(form.commonFields().getFirstNameField(), "First name"),
                    required(form.commonFields().getLastNameField(), "Last name"),
                    required(form.commonFields().getEmailField(), "Email"),
                    value(form.commonFields().getAddressField()),
                    value(form.commonFields().getPhoneField()),
                    form.dob().getValue(),
                    form.gender().getValue(),
                    UserRole.STUDENT.getValue(),
                    registrationNo,
                    password,
                    required(form.departmentField(), "Department"),
                    required(form.batchField(), "Batch"),
                    parseOptionalDouble(form.gpaField()),
                    requiredCombo(form.statusField(), "Status"),
                    null,
                    value(form.commonFields().getImagePathField())
            );
            // Construct Technical Officer user record
            case TECHNICAL_OFFICER -> new UserRecord(
                    userId,
                    required(form.commonFields().getFirstNameField(), "First name"),
                    required(form.commonFields().getLastNameField(), "Last name"),
                    required(form.commonFields().getEmailField(), "Email"),
                    value(form.commonFields().getAddressField()),
                    value(form.commonFields().getPhoneField()),
                    form.dob().getValue(),
                    form.gender().getValue(),
                    UserRole.TECHNICAL_OFFICER.getValue(),
                    registrationNo,
                    password,
                    null,
                    null,
                    null,
                    null,
                    null,
                    value(form.commonFields().getImagePathField())
            );
        };
    }

    // ========== Form Field Creation Methods ==========
    // Creates common input fields shared by all user types
    private UserFormFields createCommonFields(UserRecord existing) {
        // Create text fields for common user information, pre-filled if editing
        return new UserFormFields(
                textField(existing == null ? "" : value(existing.getFirstName())),
                textField(existing == null ? "" : value(existing.getLastName())),
                textField(existing == null ? "" : value(existing.getEmail())),
                textField(existing == null ? "" : value(existing.getAddress())),
                textField(existing == null ? "" : value(existing.getPhoneNumber())),
                textField(existing == null ? "" : value(existing.getProfileImagePath()))
        );
    }

    private DatePicker dateOfBirthPicker(UserRecord existing) {
        // Create date picker with pre-filled date if editing
        return new DatePicker(existing == null ? null : existing.getDateOfBirth());
    }

    private ComboBox<String> genderBox(UserRecord existing) {
        // Create gender dropdown with gender options
        ComboBox<String> cmbGender = new ComboBox<>();
        cmbGender.getItems().addAll("Male", "Female", "Other");
        // Pre-fill with existing value if editing
        cmbGender.setValue(existing == null ? null : existing.getGender());
        return cmbGender;
    }

    private ComboBox<String> statusBox(UserRecord existing) {
        // Create status dropdown for student records
        ComboBox<String> cmbStatus = new ComboBox<>();
        cmbStatus.getItems().addAll("proper", "repeat");
        // Default to "proper" if new record, use existing value if editing
        String status = existing == null ? "proper" : value(existing.getStatus());
        cmbStatus.setValue(status.isBlank() ? "proper" : status);
        return cmbStatus;
    }

    // Populates grid with common fields for all user types
    private int addCommonGrid(GridPane grid, UserFormFields fields, DatePicker dob, ComboBox<String> gender) {
        // Start adding rows from row 0
        int row = 0;
        // Add basic personal information fields
        row = addRow(grid, "First Name:", fields.getFirstNameField(), row);
        row = addRow(grid, "Last Name:", fields.getLastNameField(), row);
        row = addRow(grid, "Email:", fields.getEmailField(), row);
        row = addRow(grid, "Address:", fields.getAddressField(), row);
        row = addRow(grid, "Phone:", fields.getPhoneField(), row);

        // Create profile image field with browse button
        HBox imageBox = new HBox(8.0);
        Button btnBrowseImage = new Button("Browse");
        // Set action to open file chooser
        btnBrowseImage.setOnAction(event -> chooseImageFile(fields.getImagePathField()));
        imageBox.getChildren().addAll(fields.getImagePathField(), btnBrowseImage);
        row = addRow(grid, "Profile Image:", imageBox, row);

        // Add date of birth and gender fields
        row = addRow(grid, "Date of Birth:", dob, row);
        return addRow(grid, "Gender:", gender, row);
    }

    private int addRow(GridPane grid, String label, Node field, int row) {
        // Add label to first column and field to second column
        grid.add(new Label(label), 0, row);
        grid.add(field, 1, row);
        // Return next row number for subsequent fields
        return row + 1;
    }

    // ========== File Selection Utility ==========
    // Opens file chooser for profile image selection
    private void chooseImageFile(TextField targetField) {
        // Create file chooser dialog
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Profile Image");
        // Restrict file types to image files
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp")
        );
        // Show dialog and get selected file
        File file = chooser.showOpenDialog(targetField.getScene() == null ? null : targetField.getScene().getWindow());
        // Set the selected file path in the text field
        if (file != null) {
            targetField.setText(file.getAbsolutePath());
        }
    }

    // ========== Database Operations: Load, Add, Update, Delete ==========
    // Loads all user records from database into respective tables
    private void loadAllTables() {
        try {
            // Iterate through all user roles and populate their tables
            for (UserRole role : UserRole.values()) {
                // Fetch users for the role and update table items
                tableFor(role).getItems().setAll(findUsers(role));
            }
        } catch (SQLException e) {
            // Display error if database operation fails
            showError("Failed to load user tables.", e);
        }
    }

    // Retrieves list of users for specified role from database
    private List<UserRecord> findUsers(UserRole role) throws SQLException {
        // Query database based on role type
        return switch (role) {
            case ADMIN -> adminRepository.findAdmins();
            case LECTURER -> adminRepository.findLecturers();
            case STUDENT -> adminRepository.findStudents();
            case TECHNICAL_OFFICER -> adminRepository.findTechnicalOfficers();
        };
    }

    // Adds new user record to database based on role
    private boolean addUser(UserRole role, UserRecord row) throws SQLException {
        // Insert user into database using role-specific repository method
        return switch (role) {
            case ADMIN -> adminRepository.createAdmin(row);
            case LECTURER -> adminRepository.createLecturer(row);
            case STUDENT -> adminRepository.createStudent(row);
            case TECHNICAL_OFFICER -> adminRepository.createTechnicalOfficer(row);
        };
    }

    // Updates existing user record in database
    private boolean updateUser(UserRole role, UserRecord row) throws SQLException {
        // Update user in database using role-specific repository method
        return switch (role) {
            case ADMIN -> adminRepository.updateAdmin(row);
            case LECTURER -> adminRepository.updateLecturer(row);
            case STUDENT -> adminRepository.updateStudent(row);
            case TECHNICAL_OFFICER -> adminRepository.updateTechnicalOfficer(row);
        };
    }

    // Deletes user record from database based on role and user ID
    private boolean deleteUser(UserRole role, String userId) throws SQLException {
        // Remove user from database using role-specific repository method
        return switch (role) {
            case ADMIN -> adminRepository.deleteAdmin(userId);
            case LECTURER -> adminRepository.deleteLecturer(userId);
            case STUDENT -> adminRepository.deleteStudent(userId);
            case TECHNICAL_OFFICER -> adminRepository.deleteTechnicalOfficer(userId);
        };
    }

    // ========== Table Binding and Display Utility Methods ==========
    // Returns table view associated with the given user role
    private TableView<UserRecord> tableFor(UserRole role) {
        // Look up table in the role-to-table map
        return tables.get(role);
    }

    // Binds table column to user record property using extractor function
    private void bind(TableColumn<UserRecord, String> column, Function<UserRecord, ?> extractor) {
        // Set cell value factory to extract and display data from UserRecord
        column.setCellValueFactory(d -> new SimpleStringProperty(display(extractor.apply(d.getValue()))));
    }

    // Converts object to displayable string, handling null values
    private String display(Object value) {
        return value == null ? "" : value.toString();
    }

    // Safely retrieves string value, returning empty string if null
    private String value(String text) {
        return text == null ? "" : text;
    }

    // Formats double value with 2 decimal places
    private String value(Double value) {
        return value == null ? "" : String.format("%.2f", value);
    }

    // Extracts and trims text from text field
    private String value(TextField textField) {
        return textField.getText() == null ? "" : textField.getText().trim();
    }

    // Creates new text field with initial value
    private TextField textField(String value) {
        return new TextField(value == null ? "" : value);
    }

    // Validates that required text field has content
    private String required(TextField textField, String fieldName) {
        String text = value(textField);
        // Throw error if field is blank
        if (text.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        return text;
    }

    // Validates that combo box has a selection
    private String requiredCombo(ComboBox<String> comboBox, String fieldName) {
        String selected = comboBox.getValue();
        // Throw error if no selection made
        if (selected == null || selected.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        return selected;
    }

    // Parses optional double value (GPA) with validation
    private Double parseOptionalDouble(TextField textField) {
        String text = value(textField);
        // Return null if field is empty (optional)
        if (text.isBlank()) {
            return null;
        }
        try {
            // Parse string to double
            double gpa = Double.parseDouble(text);
            // Validate GPA range
            if (gpa < 0 || gpa > 4.0) {
                throw new IllegalArgumentException("GPA must be between 0.00 and 4.00.");
            }
            return gpa;
        } catch (NumberFormatException e) {
            // Throw error if value is not a valid number
            throw new IllegalArgumentException("GPA must be a valid number.");
        }
    }

    // ========== Input Validation Methods ==========
    // Validates password requirement based on create vs edit operation
    private String requirePasswordForCreate(boolean edit, TextField txtPassword) {
        // Extract password value from field
        String password = value(txtPassword);
        // Throw error if creating new user without password
        if (!edit && password.isBlank()) {
            throw new IllegalArgumentException("Password is required.");
        }
        return password;
    }

    // ========== User Notification Methods ==========
    // Displays information alert dialog
    private void showInfo(String message) {
        // Create and display information alert
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Displays error alert dialog with exception details
    private void showError(String message, Exception e) {
        // Create and display error alert with exception message
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Database Error");
        alert.setContentText(message + "\n" + e.getMessage());
        alert.showAndWait();
    }

    // ========== Data Transfer Object for Form Fields ==========
    // Record holding all form input fields for a user role
    private record RoleForm(
            UserFormFields commonFields,
            DatePicker dob,
            ComboBox<String> gender,
            TextField registrationNoField,
            PasswordField passwordField,
            TextField departmentField,
            TextField batchField,
            TextField gpaField,
            ComboBox<String> statusField,
            TextField positionField
    ) {
    }

    // ========== Table Column Configuration Methods ==========
    // Binds Admin table columns to UserRecord properties
    private void configureAdminTable() {
        // Map each table column to corresponding UserRecord getter method
        bind(adminId, UserRecord::getUserId);
        bind(adminFirstName, UserRecord::getFirstName);
        bind(adminLastName, UserRecord::getLastName);
        bind(adminEmail, UserRecord::getEmail);
        bind(adminPhone, UserRecord::getPhoneNumber);
        bind(adminGender, UserRecord::getGender);
        bind(adminDeptId, UserRecord::getAddress);
        bind(adminAccessLevel, UserRecord::getRegistrationNo);
    }

    // Binds Lecturer table columns to UserRecord properties
    private void configureLecturerTable() {
        // Map each table column to corresponding UserRecord getter method
        bind(lecId, UserRecord::getUserId);
        bind(lecFirstName, UserRecord::getFirstName);
        bind(lecLastName, UserRecord::getLastName);
        bind(lecEmail, UserRecord::getEmail);
        bind(lecPhone, UserRecord::getPhoneNumber);
        bind(lecGender, UserRecord::getGender);
        bind(lecRegNo, UserRecord::getRegistrationNo);
        bind(lecDeptId, UserRecord::getDepartment);
        bind(lecPosition, UserRecord::getPosition);
    }

    // Binds Student table columns to UserRecord properties
    private void configureStudentTable() {
        // Map each table column to corresponding UserRecord getter method
        bind(stuId, UserRecord::getUserId);
        bind(stuFirstName, UserRecord::getFirstName);
        bind(stuLastName, UserRecord::getLastName);
        bind(stuEmail, UserRecord::getEmail);
        bind(stuPhone, UserRecord::getPhoneNumber);
        bind(stuGender, UserRecord::getGender);
        bind(stuRegNo, UserRecord::getRegistrationNo);
        bind(stuDeptId, UserRecord::getDepartment);
        bind(stuBatchId, UserRecord::getBatch);
        bind(stuStatus, UserRecord::getStatus);
    }

    // Binds Technical Officer table columns to UserRecord properties
    private void configureTechnicalOfficerTable() {
        // Map each table column to corresponding UserRecord getter method
        bind(toId, UserRecord::getUserId);
        bind(toFirstName, UserRecord::getFirstName);
        bind(toLastName, UserRecord::getLastName);
        bind(toEmail, UserRecord::getEmail);
        bind(toPhone, UserRecord::getPhoneNumber);
        bind(toGender, UserRecord::getGender);
        bind(toDeptId, UserRecord::getAddress);
        bind(toPosition, UserRecord::getRegistrationNo);
        bind(toLab, UserRecord::getDateOfBirth);
        bind(toShift, UserRecord::getRole);
    }

}
