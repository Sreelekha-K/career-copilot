FROM maven:3.9.9-eclipse-temurin-21-alpine AS build

WORKDIR /workspace

COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

COPY --from=build /workspace/target/*.jar app.jar

RUN mkdir -p /app/uploads/resumes && chown -R appuser:appgroup /app

USER appuser

ENV RESUME_UPLOAD_DIR=/app/uploads/resumes

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
