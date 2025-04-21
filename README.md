# Task Manager Application
A simple web app built with **Angular** and **Spring Boot**.  
The project demonstrates a full-stack task management application with in-memory storage by H2DB and responsive design using Angular Material.

## Technologies Used

### Frontend
- Angular 17
- Angular Material
- RxJS & FormsModule
- Material Table, Dialog, Snackbar
- Standalone components

### Backend
- Java 21
- Spring Boot 3
- RESTful API with in-memory persistence using H2 Database
- DTO mapping with MapStruct
- JUnit 5 & Mockito for unit testing
- Swagger/OpenAPI for documentation

---

## Features

### Task Management
- Create and update a Task
- Each task has a description and status (Completed / Uncompleted)

### Filtering & Pagination
- Filter tasks by status
- Paginate task list

### Unit Testing
- Angular component testing with Karma/Jasmine
- Spring Boot services and controllers tested with JUnit/Mockito

---

## Useful Links

### Spring-boot server
- http://localhost:8080/api/

### Angular server
- http://localhost:4200/

### H2 Database
- http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:task
Username: admin
Password: admin

### Swagger interface
- http://localhost:8080/swagger-ui/index.html
