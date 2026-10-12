# Student Management System

A backend REST API for managing university student information using Java, Spring Boot, Spring Data JPA, and MySQL.

The application provides student management, user registration, login, CRUD operations, validation, exception handling, and a role-based architecture for Admin and Student users.

---

## 🚀 Features

### Admin
- Add students
- View all students
- View student by ID
- Update student details
- Delete students

### Student
- Register / activate account
- Login
- View their own student information
- Read-only access to student data

### Backend
- RESTful APIs
- Spring Boot
- Spring Data JPA
- MySQL database
- DTO-based request handling
- Input validation
- Global exception handling
- Custom exceptions
- User and Student entity relationship
- Layered architecture

---

## 🛠️ Technologies Used

| Technology | Purpose |
|---|---|
| Java | Programming Language |
| Spring Boot | Backend Framework |
| Spring Data JPA | Database Operations |
| Hibernate | ORM |
| MySQL | Database |
| Maven | Dependency Management |
| Postman | API Testing |
| IntelliJ IDEA | Development Environment |
| Git & GitHub | Version Control |

---

## 🏗️ Project Architecture

```text
Client / Postman
       ↓
Controller Layer
       ↓
Service Layer
       ↓
Repository Layer
       ↓
MySQL Database
