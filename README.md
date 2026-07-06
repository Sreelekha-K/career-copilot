# 🚀 Career Copilot

> **AI-powered Resume Optimization Platform that helps job seekers land more interviews by tailoring resumes for every job description.**

Career Copilot bridges the gap between **job seekers** and **Applicant Tracking Systems (ATS)**.

Instead of applying to every company with the same resume, users can upload their resume, paste a job description, and receive AI-powered resume improvements tailored specifically for that role.

Unlike traditional job portals such as LinkedIn or Naukri that help users **find jobs**, Career Copilot helps users **increase their chances of getting shortlisted**.

---

## ✨ Features

### 🤖 AI Resume Optimization

* Upload Resume (PDF/DOCX)
* Paste Job Description
* AI-powered Resume Analysis
* ATS Keyword Suggestions
* Resume Refinement
* Optimized Resume Preview

### 🔐 Authentication

* User Registration
* Secure Login
* JWT Authentication
* Spring Security

### 📄 Resume Management

* Resume Upload
* Resume History
* Resume Parsing
* Resume Storage

### 📊 ATS Analysis

* ATS Score
* Resume Match %
* Missing Keywords
* Skills Gap Analysis

### 💼 Career Growth

* Interview Preparation
* Personalized Skill Suggestions
* Career Roadmap
* AI Recommendations

---


# 🛠 Tech Stack

## Backend

* Java 21
* Spring Boot 3
* Spring Security
* JWT Authentication
* Spring Data JPA
* Hibernate
* PostgreSQL
* Maven

---

## Frontend

* Angular 20
* Standalone Components
* TypeScript
* RxJS
* Angular Router
* HttpClient
* CSS
* Responsive UI

---

## AI

* Google Gemini API
* Resume Parsing
* ATS Analysis
* Prompt Engineering

---

## DevOps

* Docker
* Docker Compose
* Nginx
* GitHub

---

# 🏗 Project Architecture

```text
career-copilot/

├── backend/
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── entity/
│   ├── dto/
│   ├── security/
│   ├── config/
│   └── exception/
│
├── frontend/
│   └── career-copilot-ui/
│       ├── src/
│       ├── assets/
│       ├── environments/
│       └── app/
│
├── docker-compose.yml
└── README.md
```

---

# ⚙ Environment Variables

| Variable                   | Description             |
| -------------------------- | ----------------------- |
| GEMINI_API_KEY             | Google Gemini API Key   |
| JWT_SECRET                 | Secret Key for JWT      |
| SPRING_DATASOURCE_URL      | PostgreSQL URL          |
| SPRING_DATASOURCE_USERNAME | Database Username       |
| SPRING_DATASOURCE_PASSWORD | Database Password       |
| RESUME_UPLOAD_DIR          | Resume Upload Directory |

---

# 🚀 Running Locally

## Clone Repository

```bash
git clone https://github.com/<your-username>/career-copilot.git

cd career-copilot
```

---

## Backend

```bash
cd backend

mvn clean install

mvn spring-boot:run
```

---

## Frontend

```bash
cd frontend/career-copilot-ui

npm install

ng serve
```

---

## Docker

Build

```bash
docker-compose up --build
```

Run

```bash
docker-compose up
```

Stop

```bash
docker-compose down
```

Remove Volumes

```bash
docker-compose down -v
```

---

# 🌐 Application URLs

| Service    | URL                   |
| ---------- | --------------------- |
| Frontend   | http://localhost:4200 |
| Backend    | http://localhost:8080 |
| PostgreSQL | localhost:5432        |

---

# 🔄 Application Workflow

```text
User

↓

Upload Resume

↓

Paste Job Description

↓

Resume Parsing

↓

Gemini AI

↓

ATS Analysis

↓

Resume Optimization

↓

Optimized Resume

↓

Download PDF / DOCX
```

---

# 🔐 Authentication Flow

```text
Register

↓

Login

↓

JWT Generated

↓

Protected APIs

↓

Dashboard
```

---

# 📂 Persistent Storage

Docker Volumes

```
career_copilot_data
career_copilot_uploads
```

---

# 📋 Roadmap

## ✅ Completed

* Spring Boot Backend
* Angular Frontend
* JWT Authentication
* Resume Upload
* Resume Parsing
* AI Resume Optimization
* ATS Keyword Analysis
* Docker Support

---

## 🚧 In Progress

* Resume Download (PDF)
* Resume Download (DOCX)
* Job Match %
* ATS Score
* Resume History
* Dashboard Analytics

---

## 🔮 Planned

* Cover Letter Generator
* LinkedIn Profile Optimizer
* Mock Interview AI
* Resume Templates
* AI Career Coach
* Multi-language Resume Support
* Email Resume Assistant

---

# 🤝 Contributing

Contributions, feature requests, and suggestions are welcome.

Feel free to fork the repository and submit a pull request.

---

# 👩‍💻 About the Creator

**Sreelekha Khanderao**

Software Engineer passionate about Java, Spring Boot, Angular, AI, and building products that solve real-world problems.

Career Copilot was created after observing that many candidates apply for hundreds of jobs using the same resume, resulting in ATS rejection.

The vision of Career Copilot is to help every candidate tailor their resume within minutes using AI and improve their chances of landing interviews.

---

# 📬 Contact

📧 Email

[sreelekhakhanderao@gmail.com](mailto:sreelekhakhanderao@gmail.com)

---

# ⭐ Support

If you found this project useful, consider giving it a **⭐ Star** on GitHub.

It motivates me to build more open-source AI projects.

---

## 📄 License

This project is licensed under the MIT License.
