# Career Copilot Render Deployment Guide

This guide deploys Career Copilot to Render with:

- Angular frontend
- Spring Boot backend
- Render PostgreSQL database
- Gemini API integration

Local development remains unchanged.

## 1. Render Services

Create three Render resources:

1. PostgreSQL database
2. Backend web service
3. Frontend static site

Recommended service names:

```text
career-copilot-db
career-copilot-backend
career-copilot-frontend
```

## 2. Create PostgreSQL on Render

1. Open Render Dashboard.
2. Select New.
3. Select PostgreSQL.
4. Name it `career-copilot-db`.
5. Choose a region close to your backend service.
6. Create the database.

After creation, Render will show database connection details.

Use the internal host/connection values for the backend service. The backend expects:

```text
DATABASE_URL
DATABASE_USERNAME
DATABASE_PASSWORD
```

Set `DATABASE_URL` as a JDBC URL:

```text
jdbc:postgresql://<render-internal-host>:5432/<database-name>
```

Example:

```text
jdbc:postgresql://dpg-xxxxx-a.oregon-postgres.render.com:5432/career_copilot
```

Do not paste a raw `postgres://...` URL into `DATABASE_URL`; Spring Boot expects the JDBC format above.

## 3. Deploy Backend on Render

Use the existing Spring Boot backend.

### Backend Settings

Create a new Render Web Service:

```text
Root Directory: backend
Environment: Docker
Dockerfile Path: ./Dockerfile
```

If deploying without Docker, use:

```text
Root Directory: backend
Build Command: ./mvnw clean package -DskipTests
Start Command: java -jar target/backend-0.0.1-SNAPSHOT.jar
```

Docker deployment is recommended because the project already includes a backend Dockerfile.

### Backend Environment Variables

Set these variables in Render:

```text
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=jdbc:postgresql://<render-internal-host>:5432/<database-name>
DATABASE_USERNAME=<render-db-user>
DATABASE_PASSWORD=<render-db-password>
GEMINI_API_KEY=<your-gemini-api-key>
JWT_SECRET=<long-secure-random-secret>
FRONTEND_URL=https://<your-frontend-service>.onrender.com
RESUME_UPLOAD_DIR=/app/uploads/resumes
```

Render automatically provides:

```text
PORT
```

The backend production config uses:

```properties
server.port=${PORT:8080}
```

So the backend will bind correctly to Render's dynamic port.

### Backend URLs

After deployment, the backend URL will look like:

```text
https://career-copilot-backend.onrender.com
```

Health/status endpoint:

```text
https://career-copilot-backend.onrender.com/api/status
```

## 4. Deploy Frontend on Render

Use Render Static Site for the Angular frontend.

### Frontend Settings

Create a new Static Site:

```text
Root Directory: frontend/career-copilot-ui
Build Command: npm ci && npm run build:render
Publish Directory: dist/career-copilot-ui/browser
```

### Frontend Environment Variables

Set:

```text
API_URL=https://<your-backend-service>.onrender.com
```

Example:

```text
API_URL=https://career-copilot-backend.onrender.com
```

The frontend build script reads `API_URL` and writes it into the Angular production environment before building.

Local development still uses:

```text
http://localhost:8080
```

## 5. CORS Configuration

The backend allows:

```text
http://localhost:4200
```

For production, set this backend environment variable:

```text
FRONTEND_URL=https://<your-frontend-service>.onrender.com
```

Example:

```text
FRONTEND_URL=https://career-copilot-frontend.onrender.com
```

If CORS fails, make sure the frontend URL exactly matches the browser URL, including `https://` and no trailing slash.

## 6. Build Commands

Backend Docker build:

```powershell
docker build -t career-copilot-backend ./backend
```

Backend local Maven build:

```powershell
cd C:\projects\career-copilot\backend
mvn clean package -DskipTests
```

Frontend local production build:

```powershell
cd C:\projects\career-copilot\frontend\career-copilot-ui
npm ci
npm run build
```

Frontend Render-style build:

```powershell
cd C:\projects\career-copilot\frontend\career-copilot-ui
$env:API_URL="https://career-copilot-backend.onrender.com"
npm run build:render
```

## 7. Start Commands

Backend Docker local:

```powershell
docker run --rm -p 8080:8080 `
  -e SPRING_PROFILES_ACTIVE=prod `
  -e DATABASE_URL="jdbc:postgresql://host.docker.internal:5432/career_copilot" `
  -e DATABASE_USERNAME="postgres" `
  -e DATABASE_PASSWORD="postgres" `
  -e GEMINI_API_KEY="your-gemini-api-key" `
  -e JWT_SECRET="replace-with-a-long-secure-secret" `
  -e FRONTEND_URL="http://localhost:4200" `
  career-copilot-backend
```

Backend non-Docker:

```powershell
cd C:\projects\career-copilot\backend
$env:SPRING_PROFILES_ACTIVE="prod"
$env:PORT="8080"
$env:DATABASE_URL="jdbc:postgresql://localhost:5432/career_copilot"
$env:DATABASE_USERNAME="postgres"
$env:DATABASE_PASSWORD="postgres"
$env:GEMINI_API_KEY="your-gemini-api-key"
$env:JWT_SECRET="replace-with-a-long-secure-secret"
$env:FRONTEND_URL="http://localhost:4200"
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

Frontend local development:

```powershell
cd C:\projects\career-copilot\frontend\career-copilot-ui
npm start
```

## 8. Docker Compose Local Deployment

Local Docker Compose still works:

```powershell
cd C:\projects\career-copilot
docker-compose up -d --build
```

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

## 9. Troubleshooting

### Backend starts locally but fails on Render

Check that this variable is set:

```text
SPRING_PROFILES_ACTIVE=prod
```

Then verify these variables exist:

```text
DATABASE_URL
DATABASE_USERNAME
DATABASE_PASSWORD
GEMINI_API_KEY
JWT_SECRET
FRONTEND_URL
```

### Backend port error on Render

Render requires the app to bind to its dynamic `PORT`.

The production config already includes:

```properties
server.port=${PORT:8080}
```

Make sure the backend service is using the `prod` profile.

### Database connection fails

Use a JDBC URL:

```text
jdbc:postgresql://<host>:5432/<database>
```

Do not use:

```text
postgres://...
```

Also confirm the backend and database are in the same Render region when using the internal database host.

### CORS error in browser

Set backend variable:

```text
FRONTEND_URL=https://<your-frontend-service>.onrender.com
```

Then redeploy the backend.

The value must exactly match the frontend origin in the browser.

### Frontend calls localhost in production

Set frontend Static Site variable:

```text
API_URL=https://<your-backend-service>.onrender.com
```

Then redeploy the frontend.

The Render build command must be:

```text
npm ci && npm run build:render
```

### AI analysis fails

Check backend logs and confirm:

```text
GEMINI_API_KEY
```

is set on the backend service, not the frontend service.

### JWT tokens reset after backend redeploy

Set a stable backend variable:

```text
JWT_SECRET=<long-secure-random-secret>
```

If `JWT_SECRET` changes, users need to login again.

### Resume uploads disappear after redeploy

Render services have ephemeral filesystems unless persistent disks are configured.

For production, add a Render persistent disk and mount it at:

```text
/app/uploads/resumes
```

Keep this variable:

```text
RESUME_UPLOAD_DIR=/app/uploads/resumes
```

Without a persistent disk, uploaded resume files may be lost after redeploys.

## 10. Deployment Checklist

Backend:

- `SPRING_PROFILES_ACTIVE=prod`
- `DATABASE_URL` is JDBC format
- `DATABASE_USERNAME` is set
- `DATABASE_PASSWORD` is set
- `GEMINI_API_KEY` is set
- `JWT_SECRET` is set
- `FRONTEND_URL` points to deployed frontend

Frontend:

- `API_URL` points to deployed backend
- Build command is `npm ci && npm run build:render`
- Publish directory is `dist/career-copilot-ui/browser`

Database:

- PostgreSQL service exists
- Backend uses internal database host where possible
- Database and backend are in the same region
