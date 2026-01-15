# OnlineCourseSpringProject

## 💌 Overzicht

Spring Boot applicatie voor **online cursussen** met:

* Rolgebaseerde toegang: `VISITOR`, `STUDENT`, `INSTRUCTOR`, `ADMIN`
* JWT-gebaseerde authenticatie
* MySQL database met automatische testdata
* RESTful API endpoints
* Unit tests met JUnit 5 + Mockito
* Docker + Docker Compose ondersteuning

---

## 👥 Rollen & Autorisatie

| Actie                                  | VISITOR | STUDENT | INSTRUCTOR | ADMIN |
| -------------------------------------- | :-----: | :-----: | :--------: | :---: |
| Register / Login                       |    ✅    |    ✅    |      ✅     |   ✅   |
| Alle courses bekijken                  |    ✅    |    ✅    |      ✅     |   ✅   |
| Course aanmaken                        |    ❌    |    ❌    |      ✅     |   ✅   |
| Course updaten (eigen)                 |    ❌    |    ❌    |      ✅     |   ✅   |
| Course verwijderen                     |    ❌    |    ❌    |      ❌     |   ✅   |
| Inschrijven voor course (self)         |    ❌    |    ✅    |      ❌     |   ✅   |
| Inschrijvingen bekijken (self / eigen) |    ❌    |    ✅    |      ✅     |   ✅   |
| Users beheren                          |    ❌    |    ❌    |      ❌     |   ✅   |

> `(self)` betekent: enkel eigen gegevens

---

## 🔒 Security

* **BCrypt** password hashing
* **JWT tokens** met:

    * `sub` → username
    * `role` → gebruikersrol
* JWT wordt meegestuurd via de `Authorization` header
* `JwtAuthFilter` valideert tokens bij elke request
* Stateless session policy
* Autorisatie via `@PreAuthorize`

**Authorization header voorbeeld:**

```
Authorization: Bearer <JWT_TOKEN>
```

---

## 🌐 API Endpoints

### AuthController (Public)

| Methode | Endpoint           | Beschrijving                 |
| ------- | ------------------ | ---------------------------- |
| POST    | /api/auth/register | Nieuwe gebruiker registreren |
| POST    | /api/auth/login    | Login en JWT-token ontvangen |

### CourseController

| Methode | Endpoint          | Autorisatie              |
| ------- | ----------------- | ------------------------ |
| GET     | /api/courses      | Public                   |
| GET     | /api/courses/{id} | Public                   |
| POST    | /api/courses      | INSTRUCTOR / ADMIN       |
| PUT     | /api/courses/{id} | INSTRUCTOR (own) / ADMIN |
| DELETE  | /api/courses/{id} | ADMIN                    |

### EnrollmentController

| Methode | Endpoint                                         | Autorisatie            |
| ------- | ------------------------------------------------ | ---------------------- |
| POST    | /api/courses/{id}/enroll                         | STUDENT                |
| GET     | /api/enrollments/me                              | STUDENT                |
| GET     | /api/instructor/enrollments                      | INSTRUCTOR             |
| POST    | /api/admin/courses/{courseId}/enroll/{studentId} | ADMIN                  |
| GET     | /api/admin/enrollments                           | ADMIN                  |
| DELETE  | /api/enrollments/{id}                            | STUDENT (self) / ADMIN |

### AdminController (ADMIN-only)

| Methode | Endpoint                   | Beschrijving            |
| ------- | -------------------------- | ----------------------- |
| GET     | /api/admin/users           | Alle gebruikers ophalen |
| PUT     | /api/admin/users/{id}/role | Rol aanpassen           |
| DELETE  | /api/admin/users/{id}      | Gebruiker verwijderen   |

---

## ⚠️ Exception Handling

Alle fouten worden centraal afgehandeld via `GlobalExceptionHandler (@RestControllerAdvice)` en geven een consistent JSON-formaat terug.

### HTTP Status Codes

| Exception                           | Status |
| ----------------------------------- | ------ |
| MethodArgumentNotValidException     | 400    |
| MethodArgumentTypeMismatchException | 400    |
| InvalidCredentialsException         | 401    |
| JwtAuthenticationException          | 401    |
| UnauthorizedActionException         | 403    |
| ResourceNotFoundException           | 404    |
| DuplicateEnrollmentException        | 409    |
| DuplicateResourceException          | 409    |
| Andere exceptions                   | 500    |

**Error Response Voorbeeld**

```json
{
  "timestamp": "2025-01-01T12:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Course not found",
  "path": "/api/courses/99"
}
```

**Validatie Errors (@Valid)**

```json
{
  "timestamp": "2025-01-01T12:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/auth/register",
  "fields": {
    "username": "Username is required",
    "password": "Password must be at least 6 characters"
  }
}
```

---

## 👍 Database & DataLoader

* MySQL database met automatische testdata via `DataLoader`.
* Data wordt alleen geïnitialiseerd als de database leeg is.
* Geseede gebruikers:

| Username | Rol        | Password |
| -------- | ---------- | -------- |
| admin    | ADMIN      | admin123 |
| hilal    | INSTRUCTOR | inst123  |
| teodora  | INSTRUCTOR | inst123  |
| tamara   | STUDENT    | stud123  |
| eva      | STUDENT    | stud123  |
| vika     | STUDENT    | stud123  |

* Geseede courses:

    * Java Fundamentals (Instructor: hilal)
    * Spring Boot (Instructor: hilal)
    * Nederlands (Instructor: teodora)
* Studenten zijn automatisch ingeschreven voor verschillende courses.

---

## 🧑‍ Postman Tests

* Authentificatie per rol
* Role-based toegang (401 / 403)
* Ownership van courses
* Enrollments (self, instructor, admin)
* Validatie en foutafhandeling
* Correcte HTTP statuscodes
* JWT tokens automatisch opgeslagen als **collection variables**
* **Voorbeeld instructie:**

    1. Voer eerst login uit om JWT-token op te slaan.
    2. Gebruik token voor alle andere endpoints via `Authorization: Bearer {{jwt_token}}`.

---

## 📄 Voorbeeld Requests & Responses

**POST /api/courses**

```json
{
  "title": "Spring Boot",
  "description": "Build secure APIs"
}
```

**Response 201 Created**

```json
{
  "id": 2,
  "title": "Spring Boot",
  "description": "Build secure APIs",
  "instructor": "hilal",
  "createdAt": "2026-01-15T14:00:00",
  "updatedAt": "2026-01-15T14:00:00"
}
```

**POST /api/auth/register**

```json
{
  "username": "tamara",
  "email": "tamara@example.com",
  "password": "stud123"
}
```

**Response 200 OK**

```json
{
  "token": "<JWT_TOKEN>",
  "username": "tamara",
  "email": "tamara@example.com",
  "role": "STUDENT"
}
```

---

## 🧑‍ Unit Tests

* **CourseServiceTest** → 6 tests
* **EnrollmentServiceTest** → 8 tests
* **UserServiceTest** → 8 tests
* **AuthServiceTest** → 6 tests

**Test tips:**

* Mockito stubbings moeten correct zijn, anders krijg je `UnnecessaryStubbingException`.
* Testgevallen dekken: success, invalid input, duplicate entries, unauthorized access.

---

## ⚙️ Configuratie

**MySQL**

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/onlinecoursedb?useSSL=false&serverTimezone=UTC
spring.datasource.username=intec
spring.datasource.password=intec-123
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

**JWT**

```properties
jwt.secret=${JWT_SECRET:abcdefghijklmnopqrstuvwxyz1234567890ABCDEFGHIJKL}
jwt.expiration-hours=24
```

> In productie: JWT_SECRET via environment variable, nooit hardcoded.

---

## ▶️ Applicatie starten

**Vereisten:**

* Java 17+
* Maven
* MySQL

**Stappen:**

1. Database aanmaken:

```sql
CREATE DATABASE onlinecoursedb;
```

2. Configureer `application.properties`

3. Applicatie starten:

```bash
mvn spring-boot:run
```

API beschikbaar op: `http://localhost:8080`

---

## 🐳 Docker

**Dockerfile**

```dockerfile
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY target/OnlineCourseSpringProject-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
```

**docker-compose.yml**

```yaml
version: '3.8'

services:
  db:
    image: mysql:8.1
    container_name: onlinecourse-db
    environment:
      MYSQL_ROOT_PASSWORD: root123
      MYSQL_DATABASE: onlinecoursedb
      MYSQL_USER: intec
      MYSQL_PASSWORD: intec-123
    ports:
      - "3306:3306"
    volumes:
      - db_data:/var/lib/mysql

  app:
    build: .
    container_name: onlinecourse-app
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://db:3306/onlinecoursedb?useSSL=false&serverTimezone=UTC
      SPRING_DATASOURCE_USERNAME: intec
      SPRING_DATASOURCE_PASSWORD: intec-123
      JWT_SECRET: mysecretjwtkey
    ports:
      - "8080:8080"
    depends_on:
      - db

volumes:
  db_data:
```

**Starten:**

```bash
docker-compose up --build
```

**Stoppen:**

```bash
docker-compose down
```
