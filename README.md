# Career Copilot

Career Copilot is an AI-powered career growth dashboard for software engineers. It supports resume upload, resume parsing, JWT authentication, AI job match analysis, ATS scoring, skill roadmaps, and interview preparation.

## Tech Stack

Backend:
- Java 21
- Spring Boot 3
- Spring Security with JWT
- Spring Data JPA
- PostgreSQL
- Maven

Frontend:
- Angular standalone components
- TypeScript
- HttpClient
- Nginx for production Docker serving

Database:
- PostgreSQL 16

## Project Structure

```text
career-copilot/
  backend/
    Dockerfile
    pom.xml
    src/
  frontend/
    career-copilot-ui/
      Dockerfile
      nginx.conf
      package.json
      src/
  docker-compose.yml
```

## Required Environment Variables

For local Docker Compose, these are the important environment variables:

```text
GEMINI_API_KEY       Required for AI resume/job analysis
JWT_SECRET           Recommended for stable JWT signing
```

The Compose file already provides database defaults:

```text
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/career_copilot?options=-c%20TimeZone=UTC
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
RESUME_UPLOAD_DIR=/app/uploads/resumes
```

Set optional secrets in PowerShell before starting Docker:

```powershell
$env:GEMINI_API_KEY="your-gemini-api-key"
$env:JWT_SECRET="replace-with-a-long-secure-secret"
```

## Build Commands

Backend local build:

```powershell
cd C:\projects\career-copilot\backend
mvn clean package -DskipTests
```

If Maven is not installed globally, use Docker Compose build instead.

Frontend local build:

```powershell
cd C:\projects\career-copilot\frontend\career-copilot-ui
npm install
npm run build
```

## Docker Commands

Build backend image:

```powershell
cd C:\projects\career-copilot
docker build -t career-copilot-backend ./backend
```

Build frontend image:

```powershell
cd C:\projects\career-copilot
docker build -t career-copilot-frontend ./frontend/career-copilot-ui
```

List containers:

```powershell
docker ps
```

View all containers, including stopped ones:

```powershell
docker ps -a
```

View logs:

```powershell
docker logs <container-name>
```

Stop and remove a container:

```powershell
docker rm -f <container-name>
```

## Docker Compose Commands

Start the full application:

```powershell
cd C:\projects\career-copilot
docker-compose up -d
```

Start and rebuild images:

```powershell
docker-compose up -d --build
```

Stop the application:

```powershell
docker-compose down
```

Stop the application and remove volumes:

```powershell
docker-compose down -v
```

Check service status:

```powershell
docker-compose ps
```

View logs for all services:

```powershell
docker-compose logs -f
```

View logs for one service:

```powershell
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f postgres
```

Validate Compose configuration:

```powershell
docker-compose config
```

## Application URLs

Frontend:

```text
http://localhost:4200
```

Backend:

```text
http://localhost:8080
```

PostgreSQL:

```text
localhost:5432
```

Inside Docker, the backend connects to PostgreSQL using:

```text
postgres:5432
```

## Persistent Data

Docker Compose uses named volumes:

```text
career_copilot_data      PostgreSQL database files
career_copilot_uploads   Uploaded resume files
```

Database data remains available after:

```powershell
docker-compose down
```

Database data is deleted only if you run:

```powershell
docker-compose down -v
```

## Troubleshooting

### No configuration file provided

Error:

```text
no configuration file provided: not found
```

Fix: run Docker Compose from the project root:

```powershell
cd C:\projects\career-copilot
docker-compose up -d
```

### Container name already in use

Error:

```text
Conflict. The container name "/career-copilot-postgres" is already in use
```

Fix:

```powershell
docker rm -f career-copilot-postgres
docker-compose up -d
```

### Port already in use

If `4200`, `8080`, or `5432` is already used, find the running container:

```powershell
docker ps
```

Stop the conflicting container:

```powershell
docker rm -f <container-name>
```

Then restart:

```powershell
docker-compose up -d
```

### Docker config access warning

Warning:

```text
Error loading config file: open C:\Users\sreel\.docker\config.json: Access is denied
```

This warning is separate from the application. Docker can still parse the Compose file, but Docker Desktop may have a local permissions issue. Restart Docker Desktop and run the terminal as your normal user.

### Backend cannot connect to database

Check PostgreSQL status:

```powershell
docker-compose ps
docker-compose logs -f postgres
```

The backend must use this URL inside Docker:

```text
jdbc:postgresql://postgres:5432/career_copilot?options=-c%20TimeZone=UTC
```

### AI analysis fails

Check that `GEMINI_API_KEY` is set before starting containers:

```powershell
$env:GEMINI_API_KEY="your-gemini-api-key"
docker-compose up -d --build
```

Then inspect backend logs:

```powershell
docker-compose logs -f backend
```

### Frontend loads but API calls fail

Confirm the backend is running:

```powershell
docker-compose ps
```

Test backend status:

```powershell
curl http://localhost:8080/api/status
```

If authentication-protected endpoints return `401`, login again from the frontend so Angular stores a fresh JWT.

## Clean Rebuild

Use this when Docker cache or old containers are causing confusion:

```powershell
cd C:\projects\career-copilot
docker-compose down
docker-compose up -d --build
```

For a full reset including database and uploads:

```powershell
docker-compose down -v
docker-compose up -d --build
```
