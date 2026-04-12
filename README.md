# JAVA LMS Group 01

This is a JavaFX Learning Management System project with four roles:

- `Admin`
- `Lecturer`
- `Student`
- `Technical Officer`

The project uses:

- `Java 21`
- `JavaFX`
- `Maven`
- `MySQL`

## 1. Project idea in simple words

This system starts with one login screen.  
After login, the user is sent to a dashboard based on the user role.

- `Admin` manages users, courses, notices, and timetables.
- `Lecturer` manages marks, materials, eligibility, attendance view, students, and timetable.
- `Student` views profile, attendance, courses, materials, grades, notices, and timetable.
- `Technical Officer` manages attendance and medical records, and can also view notices and timetable.

## 2. Folder structure

### `src/main/java/com/example/java_lms_group_01`

- `Main.java`
  Starts the JavaFX application and opens the login page.

### `Controller`

- Handles button clicks, form actions, navigation, and table loading.
- Each controller is connected to an `.fxml` file.

#### `Controller/AdminDashboard`

- `AdminDashboard.java`
  Main admin dashboard.
- `ManageUsersController.java`
  Add, update, delete, and view users.
- `ManageCoursesController.java`
  Add, update, delete, and search courses.
- `ManageNoticesController.java`
  Add, update, delete, and search notices.
- `ManageTimetablesController.java`
  Add, update, delete, and search timetable records.
- `CourseFormController.java`
  Helps with the course form dialog.

#### `Controller/Lecturer`

- `LecturerDashboardController.java`
  Main lecturer dashboard.
- `LecturerProfileController.java`
  Lecturer profile screen.
- `LecturerMaterialsController.java`
  Lecturer materials management.
- `LecturerMarksController.java`
  Lecturer marks management.
- `LecturerStudentsController.java`
  Lecturer student list.
- `LecturerEligibilityController.java`
  Attendance eligibility view.
- `LecturerGpaController.java`
  GPA and performance view.
- `LecturerAttendanceController.java`
  Attendance and medical combined view.
- `LecturerTimetableController.java`
  Lecturer timetable view.
- `LecturerNoticesController.java`
  Lecturer notice view.

#### `Controller/Student`

- `StudentDashboardController.java`
  Main student dashboard.
- `StudentProfilePageController.java`
  Student profile screen.
- `StudentAttendancePageController.java`
  Attendance and eligibility screen.
- `StudentMedicalPageController.java`
  Medical records screen.
- `StudentCoursePageController.java`
  Student course list.
- `StudentMaterialsPageController.java`
  Student materials screen with file open/download support.
- `StudentGradePageController.java`
  Student grade, GPA, and SGPA screen.
- `StudentNoticePageController.java`
  Notice list for students.
- `StudentTimetablePageController.java`
  Student timetable screen.

#### `Controller/TechnicalOfficer`

- `TechnicalOfficerDashboardController.java`
  Main dashboard.
- `TechnicalOfficerAttendanceController.java`
  Attendance CRUD screen.
- `TechnicalOfficerMedicalController.java`
  Medical CRUD screen.
- `TechnicalOfficerProfileController.java`
  Profile screen.
- `TechnicalOfficerNoticesController.java`
  Notice view screen.
- `TechnicalOfficerTimetableController.java`
  Timetable view screen.

### `Repository`

These classes run SQL queries and talk to the MySQL database.

- `AuthRepository`
  Checks login details and finds the role.
- `UserRepository`
  Used by admin user management.
- `UserProfileRepository`
  Loads and updates profile data.
- `CourseRepository`
  Course table operations.
- `NoticeRepository`
  Notice table operations.
- `TimetableRepository`
  Timetable table operations.
- `LecturerRepository`
  Lecturer-related queries.
- `StudentRepository`
  Student-related queries.
- `TechnicalOfficerRepository`
  Technical officer-related queries.
- `UserImageRepository`
  Stores and reads profile image paths.

### `model`

These classes hold data that is shown in JavaFX tables and forms.

- Examples: `Course`, `Notice`, `Timetable`, `Mark`, `Medical`, `Material`

### `util`

Small helper classes used across the project.

- `DBConnection`
  Opens the database connection.
- `PasswordUtil`
  Hashes and checks passwords.
- `ProfileImageUtil`
  Loads profile images safely.
- `GradeScaleUtil`
  Converts marks to grades and grade points.
- `AssessmentStructureUtil`
  Calculates course marks using assessment weights.
- `StudentContext`, `LecturerContext`, `TechnicalOfficerContext`
  Keep the logged-in registration number in memory.

## 3. How the project flows

### Step 1: Start the app

- `Main.java` opens `login_page.fxml`.

### Step 2: Login

- `LoginController.java` reads the registration number and password.
- It calls `AuthRepository`.
- `AuthRepository` checks the password in the correct database table.
- If login is correct, the controller opens the correct dashboard.

### Step 3: Dashboard navigation

Each dashboard has a center `contentArea`.

- When the user clicks a menu button, the controller loads another `.fxml` page.
- The page controller then reads data from a repository.
- The repository gets data from MySQL.
- The controller converts that data into model objects and shows it in a JavaFX table.

## 4. Easy way to understand MVC in this project

You can read the project using this order:

1. `Main.java`
2. `LoginController.java`
3. One dashboard controller
4. One page controller
5. The repository used by that page
6. The model used by that page

Example:

1. `StudentDashboardController`
2. `StudentGradePageController`
3. `StudentRepository`
4. `Grade`

## 5. Database note

The project expects a MySQL database named:

- `lms02`

Database connection settings are currently hardcoded in:

- `src/main/java/com/example/java_lms_group_01/util/DBConnection.java`

Current values:

- URL: `jdbc:mysql://localhost:3306/lms02`
- User: `root`
- Password: `2003`

For a real project, these should normally be moved to a config file.

## 6. Important beginner notes

- `@FXML` means the field or method is connected to an FXML screen.
- `TableView` shows rows of data.
- `TableColumn` tells JavaFX which property to display in each column.
- `Repository` classes contain SQL code.
- `Model` classes carry data.
- `Controller` classes connect the screen and the data.
- `Context` classes temporarily remember who is logged in.

## 7. How to run

### Requirements

- JDK `21`
- `JAVA_HOME` set correctly
- MySQL running
- Database `lms02` created with the required tables

### Run with Maven wrapper

On Windows:

```powershell
.\mvnw.cmd javafx:run
```

## 8. If you want to study this project slowly

Recommended reading order:

1. `Main.java`
2. `LoginController.java`
3. `AuthRepository.java`
4. `DBConnection.java`
5. One dashboard controller
6. One page controller from that dashboard
7. The repository used by that page
8. The model class used by the table

## 9. Suggested next learning steps

- Learn how `FXMLLoader.load()` opens a page.
- Learn how `TableView` and `TableColumn` work.
- Learn basic SQL used in repositories.
- Learn how `PreparedStatement` prevents SQL injection.
- Learn why password hashing is safer than plain text passwords.
