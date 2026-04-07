# CivicTrack – Urban Issue Reporting System

A Java/JavaFX application for reporting and managing urban issues (potholes, street lights, etc.) with MySQL storage. Built to demonstrate **Encapsulation**, **Inheritance**, **Polymorphism**, and **Abstraction**.

## Prerequisites

- **JDK 11+** (with JavaFX; Maven uses OpenJFX 21)
- **MySQL 8** (or compatible)
- **Maven 3.6+**

## Database setup

1. Start MySQL and run the schema, then optional demo users (Citizen, Official, Admin):

```bash
mysql -u root -p < schema.sql
mysql -u root -p < seed-demo.sql
```

Demo logins: `citizen@civictrack.local`, `official@civictrack.local`, `admin@civictrack.local` — password `pass123`.

2. Update `src/main/java/com/urbanissue/db/DBConfig.java` with your MySQL URL, username, and password.

## Build and run

From **this folder** (`oop-java-project`, where `pom.xml` lives):

```bash
cd /path/to/CivicTrack/oop-java-project
mvn clean compile
mvn javafx:run
```

Or use the Linux helper (works from any directory):

```bash
chmod +x scripts/run-javafx-linux.sh
./scripts/run-javafx-linux.sh
```

**If you see “No such file”** when loading the database, you are not in the folder that contains `schema.sql`, or you need the full path:

```bash
mysql -u root -p < /full/path/to/oop-java-project/schema.sql
```

Step-by-step feature walkthrough: see [USER_GUIDE.md](USER_GUIDE.md). In the running app, use **User guide** on **Register** and on **dashboards** (Citizen, Official, Admin).

Or run `com.urbanissue.Main` from your IDE with VM options for JavaFX if needed.

## Core features (demo)

- **Self-service registration** (Create Account → Citizen); Official/Admin via seed or organisation
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
