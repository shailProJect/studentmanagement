# Student Management System

Production-style Spring Boot REST API for managing student records.

## Tech Stack
Java 21 · Spring Boot 3.3.2 · Maven · Spring Data JPA · MySQL · Bean Validation
Lombok · Spring Boot DevTools · Springdoc OpenAPI (Swagger) · JUnit 5 · Mockito

## Package Structure
```
com.example.studentmanagement
├── controller/        StudentController
├── service/           StudentService (interface)
├── service/impl/      StudentServiceImpl
├── repository/        StudentRepository
├── entity/            Student
├── dto/                StudentRequestDto, StudentResponseDto
├── mapper/             StudentMapper
├── exception/          ResourceNotFoundException, DuplicateResourceException,
│                        ErrorResponse, GlobalExceptionHandler
└── config/             OpenApiConfig
```

---

## 1. Prerequisites

- JDK 21
- Maven 3.9+
- MySQL 8.x running locally (or reachable)

## 2. Configure MySQL

Create nothing manually — `createDatabaseIfNotExist=true` in `application.yml` will
create the `student_db` schema automatically on first run. Just update credentials in
`src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    username: root
    password: your_mysql_password
```

## 3. Run the application (development mode)

```bash
mvn spring-boot:run
```

The app starts on **http://localhost:8080**.

## 4. Explore the API with Swagger

Open: **http://localhost:8080/swagger-ui.html**

This gives you an interactive UI to try every endpoint without Postman.

## 5. Sample Requests (Postman or curl)

### Create a student — `POST /students`
Request:
```json
{
  "firstName": "Rahul",
  "lastName": "Sharma",
  "email": "rahul.sharma@example.com",
  "age": 21,
  "department": "Computer Science"
}
```
Response — `201 Created`:
```json
{
  "id": 1,
  "firstName": "Rahul",
  "lastName": "Sharma",
  "email": "rahul.sharma@example.com",
  "age": 21,
  "department": "Computer Science",
  "createdAt": "2026-07-04T10:15:30"
}
```

### Get all students — `GET /students` → `200 OK`
```json
[
  {
    "id": 1,
    "firstName": "Rahul",
    "lastName": "Sharma",
    "email": "rahul.sharma@example.com",
    "age": 21,
    "department": "Computer Science",
    "createdAt": "2026-07-04T10:15:30"
  }
]
```

### Get one student — `GET /students/1` → `200 OK` (shape as above)

### Update a student — `PUT /students/1`
Request body: same shape as create. Response: `200 OK` with updated `StudentResponseDto`.

### Delete a student — `DELETE /students/1` → `204 No Content`

### Validation error example — `POST /students` with bad data
Request:
```json
{ "firstName": "", "email": "not-an-email", "age": 200 }
```
Response — `400 Bad Request`:
```json
{
  "timestamp": "2026-07-04T10:16:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed for one or more fields",
  "path": "/students",
  "fieldErrors": {
    "firstName": "First name is required",
    "lastName": "Last name is required",
    "email": "Email should be a valid email address",
    "age": "Age must be realistic",
    "department": "Department is required"
  }
}
```

### Not found example — `GET /students/999`
Response — `404 Not Found`:
```json
{
  "timestamp": "2026-07-04T10:17:00",
  "status": 404,
  "error": "Not Found",
  "message": "Student not found with id: 999",
  "path": "/students/999"
}
```

### Duplicate email example — `POST /students` with existing email
Response — `409 Conflict`:
```json
{
  "timestamp": "2026-07-04T10:18:00",
  "status": 409,
  "error": "Conflict",
  "message": "A student with email 'rahul.sharma@example.com' already exists",
  "path": "/students"
}
```

---

## 6. Run unit tests

```bash
mvn test
```

Runs the JUnit 5 + Mockito test suite in `StudentServiceImplTest`, covering
success paths and every business-rule failure path (duplicate email,
not-found, cross-user email conflict on update).

## 7. Build a JAR with Maven

```bash
mvn clean package
```

This runs tests, then produces `target/student-management-system.jar`
(the `-DskipTests` flag can be added if you need to skip tests for a quick build).

## 8. Run the JAR

```bash
java -jar target/student-management-system.jar
```

You can override config at runtime without rebuilding, e.g.:
```bash
java -jar target/student-management-system.jar \
  --spring.datasource.url=jdbc:mysql://localhost:3306/student_db \
  --spring.datasource.password=secret
```

---

## 9. Where this goes next: Docker, Jenkins, AWS

This project is deliberately structured so it drops cleanly into a CI/CD pipeline:

**Dockerize:**
A multi-stage `Dockerfile` (build stage with Maven + JDK, runtime stage with
just a JRE and the JAR) keeps the final image small. Environment-specific
values (DB URL, credentials) get passed in as environment variables at
`docker run` time rather than baked into `application.yml` — this is why
Spring's externalized configuration (env vars override YAML) matters.

**Jenkins CI/CD:**
A `Jenkinsfile` would define stages: Checkout → `mvn clean test` → `mvn package`
→ `docker build` → push to a registry (ECR) → deploy. Test failures halt the
pipeline before a bad build ever reaches a server — this is exactly why we
wrote the service-layer unit tests now.

**AWS EC2 deployment:**
The Docker image gets pulled and run on an EC2 instance (or better, run via
ECS/Fargate for managed container orchestration). Security groups control
inbound access to port 8080 (or behind an ALB on 443 with TLS termination).

**AWS RDS:**
Swap the local MySQL for an RDS MySQL instance — this requires zero code
changes, only updating `spring.datasource.url` to the RDS endpoint and
storing credentials securely (AWS Secrets Manager or Parameter Store,
injected as environment variables) instead of committing them to
`application.yml`.

We'll build each of these pieces (Dockerfile, Jenkinsfile, EC2 setup, RDS
connection) step by step in the next stage of this mentorship track.
