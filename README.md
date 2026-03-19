# CivicTrack – Urban Issue Reporting System

A Java/JavaFX application for reporting and managing urban issues (potholes, street lights, etc.) with MySQL storage. Built to demonstrate **Encapsulation**, **Inheritance**, **Polymorphism**, and **Abstraction**.

## Prerequisites

- **JDK 11+** (with JavaFX; Maven uses OpenJFX 21)
- **MySQL 8** (or compatible)
- **Maven 3.6+**

## Database setup

1. Start MySQL and run the schema:

```bash
mysql -u root -p < schema.sql
```

2. Update `src/main/java/com/urbanissue/db/DBConfig.java` with your MySQL URL, username, and password.

## Build and run

```bash
cd CivicTrack
mvn clean compile
mvn javafx:run
```

Or run `com.urbanissue.Main` from your IDE with VM options for JavaFX if needed.

## Core features (demo)

- **User registration** (Citizen, Official, Admin)
- **Login** with role-based dashboards
- **Report issue** (title, description, location, category, optional image)
- **View reported issues** (Citizen: My Issues; Official: Assigned; Admin: All)
- **Assign issue to official** (Admin)
- **Update issue status** (Official: PENDING → IN_PROGRESS → RESOLVED)
- **Add comments** on an issue
- **Reports** (Admin: issues by status and priority)
- **MySQL** for all persistent data

## Project structure (summary)

| Layer      | Purpose |
|-----------|---------|
| `model/`  | POJOs: User (abstract), Citizen, Official, Admin, Issue, Comment, Attachment, etc. |
| `db/`     | `DatabaseManager` (singleton), `DBConfig` |
| `dao/`    | JDBC access: UserDAO, IssueDAO, CommentDAO, etc. |
| `service/`| Business logic: AuthenticationService, IssueService, ReportService |
| `controller/` | JavaFX FXML controllers |
| `util/`   | SessionManager, AlertHelper, ValidationHelper, FileUploadHelper |
| `resources/` | FXML, CSS, images |

## OOP in this project

- **Abstraction**: `User` abstract class; `IssueDAOInterface`; `getDisplayRole()` overridden per role.
- **Encapsulation**: Private fields with getters/setters in models; `SessionManager` and `DatabaseManager` singletons.
- **Inheritance**: `Citizen`, `Official`, `Admin` extend `User`.
- **Polymorphism**: `UserDAO.mapToUser()` returns the correct subclass from DB role; controllers navigate by `user.getRole()`.

## Tests

```bash
mvn test
```

`IssueDAOTest` and `IssueServiceTest` require the database to be running for full coverage.
