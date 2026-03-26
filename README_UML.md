# UML Class Diagram - CivicTrack

## Overview
This document describes the UML class diagram for the CivicTrack Urban Issue Reporting System, demonstrating all OOP principles and relationships.

## Viewing the Diagram

The class diagram is provided in PlantUML format (`class_diagram.puml`). You can view it using:

### Online Tools:
1. **PlantUML Online Server**: http://www.plantuml.com/plantuml/uml/
2. **PlantText**: https://www.planttext.com/
3. **PlantUML Editor**: https://plantuml-editor.kkeisuke.com/

### Desktop Tools:
1. **StarUML** (Free) - Import PlantUML or manually recreate
2. **Visual Studio Code** with PlantUML extension
3. **IntelliJ IDEA** with PlantUML plugin
4. **Eclipse** with PlantUML plugin

### Command Line:
```bash
# Install PlantUML
java -jar plantuml.jar class_diagram.puml
```

## Key Relationships Shown

### 1. Inheritance (Generalization)
- `User` ← `Citizen`, `Official`, `Admin`
- Demonstrates **Polymorphism** through overridden `getDisplayRole()` method

### 2. Interface Implementation
- `IssueDAOInterface` ← `IssueDAO`
- Demonstrates **Abstraction** through interface contracts

### 3. Composition (Strong Ownership)
- `Issue` ◆→ `Comment` (Issue contains Comments)
- `Issue` ◆→ `Attachment` (Issue contains Attachments)

### 4. Association (Usage Relationships)
- `Issue` → `User` (reportedBy, assignedOfficial)
- `Issue` → `IssueCategory` (belongsTo)
- `Official` → `Department` (belongsTo)
- Service classes → DAO classes (uses)
- Controller classes → Service classes (uses)

### 5. Singleton Pattern
- `DatabaseManager` (database connection management)
- `SessionManager` (user session management)

## OOP Principles Demonstrated

### Encapsulation
- All model classes have private fields with public getters/setters
- Service and DAO classes encapsulate business logic and data access

### Inheritance
- User hierarchy: `User` → `Citizen`, `Official`, `Admin`
- Common properties and methods inherited from base `User` class

### Polymorphism
- Abstract method `getDisplayRole()` implemented differently in each User subclass
- DAO interface allows different implementations

### Abstraction
- Abstract `User` class defines common interface
- `IssueDAOInterface` abstracts data access operations
- Service layer abstracts business logic from controllers

## Architecture Layers

1. **Model Layer**: Domain entities (User, Issue, Comment, etc.)
2. **DAO Layer**: Data access objects with database operations
3. **Service Layer**: Business logic and transaction management
4. **Controller Layer**: JavaFX controllers for UI interaction
5. **Utility Layer**: Helper classes and singletons
6. **Database Layer**: Connection and configuration management

## Class Count Summary
- **Model Classes**: 9 (User hierarchy, Issue, Comment, etc.)
- **DAO Classes**: 6 (Data access objects + 1 interface)
- **Service Classes**: 3 (Authentication, Issue, Report services)
- **Controller Classes**: 7 (JavaFX UI controllers)
- **Utility Classes**: 4 (Session, Alert, Validation, FileUpload helpers)
- **Database Classes**: 2 (Manager, Config)

**Total**: 31 classes demonstrating comprehensive OOP design