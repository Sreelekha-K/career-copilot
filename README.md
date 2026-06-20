# Career Copilot 🚀

Career Copilot is an AI-powered platform that helps software engineers manage resumes, track career growth, and prepare for job opportunities.

## Features

### Resume Management

* Upload resume metadata
* View all uploaded resumes
* View resume by ID
* Delete resumes
* PostgreSQL database integration

### Backend Technologies

* Java 21
* Spring Boot 3
* Spring Data JPA
* Spring Security
* PostgreSQL
* Maven

### APIs Implemented

| Method | Endpoint            | Description      |
| ------ | ------------------- | ---------------- |
| POST   | `/api/resumes`      | Save resume      |
| GET    | `/api/resumes`      | Get all resumes  |
| GET    | `/api/resumes/{id}` | Get resume by id |
| DELETE | `/api/resumes/{id}` | Delete resume    |

## Project Structure

backend/
├── controller
├── dto
├── entity
├── repository
├── service
├── exception
└── config

## Getting Started

### Clone Repository

```bash
git clone https://github.com/<your-username>/career-copilot.git
```

### Configure PostgreSQL

Create database:

```sql
CREATE DATABASE career_copilot;
```

### Configure application.properties

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/career_copilot
spring.datasource.username=postgres
spring.datasource.password=postgres
```

### Run Application

```bash
mvn spring-boot:run
```

Application starts on:

```text
http://localhost:8080
```

## Current Progress

### Phase 1 - Backend Foundation

* [x] Spring Boot Setup
* [x] PostgreSQL Integration
* [x] Resume CRUD APIs
* [x] Exception Handling

### Upcoming

* [ ] Resume File Upload
* [ ] Resume Parsing
* [ ] AI Resume Analysis
* [ ] Job Matching
* [ ] Angular Frontend

## Author

Sreel

Software Engineer | Java | Spring Boot | Angular
