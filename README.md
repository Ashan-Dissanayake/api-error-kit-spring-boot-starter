# ApiErrorKit

A lightweight Spring Boot library for converting custom exceptions into standardized RFC 9457 API error responses.

[![Java](https://img.shields.io/badge/Java-17+-orange)](#)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.x-brightgreen)](#)
[![RFC](https://img.shields.io/badge/RFC-9457-blue)](#)
[![Tests](https://img.shields.io/badge/tests-12%20passing-success)](#)

ApiErrorKit provides a simple and extensible way to transform application exceptions into consistent [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457) `ProblemDetail` responses.

Instead of creating a separate `@ExceptionHandler` for every custom exception, developers can annotate their exceptions with `@ApiError` and let ApiErrorKit handle the response mapping automatically.

---

## Why ApiErrorKit?

This project was inspired by a problem I observed during my experience as a Technical Mentor, while coordinating and supporting a team of around 30–40 developers.

When many developers work on the same backend, API error-handling practices can easily become inconsistent. Different developers may return different error formats, status codes, messages, or exception-handling approaches.

Over time, this inconsistency can make debugging more difficult, increase coordination overhead, and make it harder for frontend developers and other team members to work with predictable API contracts.

I built ApiErrorKit to explore how this problem could be addressed through a reusable Spring Boot library and a common API error-handling standard.

### The Problem

Without a common error-handling standard:

- Different developers may return different error response formats.
- The same type of exception may be handled differently across endpoints.
- Frontend developers need to account for inconsistent API error structures.
- Debugging becomes harder because error information is not standardized.
- Changes to error-handling logic may need to be repeated across projects or services.
- Sensitive exception messages may accidentally be exposed to API clients.

### The Goal

ApiErrorKit aims to provide a simple, reusable foundation for consistent API error handling across Spring Boot applications.

The goal is not to replace application-specific exception handling, but to establish a common baseline that teams can extend when necessary.

## ✨ Features

- Custom exception → RFC 9457 `ProblemDetail`
- Simple `@ApiError` annotation
- Custom HTTP status, title, and type
- Automatic exception resolution
- Inheritance-aware exception resolution
- Validation error handling
- Standard `application/problem+json` responses
- Secure-by-default exception message handling
- Configurable exception message exposure
- Spring Boot auto-configuration
- Custom `ExceptionResolver` support
- Lightweight — no external infrastructure required

---

## 🏗️ How It Works

```text
Custom Exception
       │
       ▼
   @ApiError
       │
       ▼
ExceptionResolver
       │
       ▼
ProblemDetailFactory
       │
       ▼
Spring ProblemDetail
       │
       ▼
application/problem+json
```

For example:

```java
@ApiError(
    status = 404,
    title = "User Not Found",
    type = "https://api.example.com/problems/user-not-found"
)
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }
}
```

When this exception is thrown:

```java
throw new UserNotFoundException("User 123 was not found");
```

ApiErrorKit produces a standardized response:

```json
{
  "type": "https://api.example.com/problems/user-not-found",
  "title": "User Not Found",
  "status": 404,
  "detail": "User 123 was not found",
  "instance": "/users/123"
}
```

---

## 📦 Installation

### Maven

Add the ApiErrorKit starter dependency to your Spring Boot application:

```xml
<dependency>
    <groupId>com.apierrorkit</groupId>
    <artifactId>api-error-kit-spring-boot-starter</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

> The current project version is `0.0.1-SNAPSHOT`.

---

## 🚀 Usage

### 1. Create a Custom Exception

ApiErrorKit does not require custom exceptions to extend a library-specific base exception.

You can use your existing application exceptions.

```java
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }
}
```

### 2. Add `@ApiError`

Annotate the exception with the API error metadata:

```java
@ApiError(
    status = 404,
    title = "User Not Found",
    type = "https://api.example.com/problems/user-not-found"
)
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }
}
```

### 3. Throw the Exception

```java
@GetMapping("/users/{id}")
public User getUser(@PathVariable Long id) {

    throw new UserNotFoundException(
        "User " + id + " was not found"
    );
}
```

ApiErrorKit automatically converts the exception into a `ProblemDetail` response.

---

## 🔐 Exception Message Security

ApiErrorKit is **secure by default**.

Exception messages are not exposed to API clients unless explicitly enabled.

Default:

```yaml
api-error-kit:
  include-exception-message: false
```

The response contains:

```json
{
  "status": 500,
  "title": "Internal Server Error",
  "detail": "An unexpected error occurred"
}
```

This helps prevent accidental exposure of sensitive internal information such as:

- database details
- internal service information
- implementation details
- stack-related messages
- infrastructure information

### Enable Exception Messages

For environments where exposing exception messages is appropriate:

```yaml
api-error-kit:
  include-exception-message: true
```

Then an annotated exception such as:

```java
throw new UserNotFoundException("User 123 was not found");
```

can produce:

```json
{
  "type": "https://api.example.com/problems/user-not-found",
  "title": "User Not Found",
  "status": 404,
  "detail": "User 123 was not found",
  "instance": "/users/123"
}
```

---

## ✅ Validation Errors

ApiErrorKit also handles Spring validation failures.

For example:

```java
public class CreateUserRequest {

    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String email;

    // getters and setters
}
```

Invalid input:

```json
{
  "name": "",
  "email": "invalid-email"
}
```

Produces:

```json
{
  "title": "Validation Failed",
  "status": 400,
  "detail": "Request validation failed",
  "instance": "/users",
  "errors": [
    {
      "field": "name",
      "message": "must not be blank"
    },
    {
      "field": "email",
      "message": "must be a well-formed email address"
    }
  ]
}
```

---

## 🧩 Custom Exception Resolution

ApiErrorKit uses an `ExceptionResolver` abstraction.

The default implementation is:

```java
AnnotationExceptionResolver
```

Applications can provide their own implementation when custom exception-resolution rules are required.

Example:

```java
@Component
public class CustomExceptionResolver
        implements ExceptionResolver {

    @Override
    public Optional<ResolvedApiError> resolve(
            Throwable exception) {

        // Custom resolution logic

        return Optional.empty();
    }
}
```

ApiErrorKit's default resolver is only registered when an application does not provide its own implementation.

---

## ⚙️ Configuration

Available configuration:

| Property | Default | Description |
|---|---:|---|
| `api-error-kit.include-exception-message` | `false` | Controls whether exception messages are included in API error responses |

Example:

```yaml
api-error-kit:
  include-exception-message: false
```

---

## 📋 Standard Error Response

ApiErrorKit is built around Spring's `ProblemDetail` and the RFC 9457 problem-details format.

Typical response:

```json
{
  "type": "https://api.example.com/problems/user-not-found",
  "title": "User Not Found",
  "status": 404,
  "detail": "User 123 was not found",
  "instance": "/users/123"
}
```

Validation errors additionally contain an `errors` extension property.

---

## 🧪 Testing

The project includes unit and integration tests covering:

- Annotated exception resolution
- Unannotated exception fallback
- Inherited `@ApiError` metadata
- `ProblemDetail` creation
- Null exception messages
- Empty exception messages
- Secure exception-message handling
- Validation errors
- HTTP response status and content type
- RFC 9457 response fields

Run the test suite with:

```bash
./mvnw test
```

On Windows:

```powershell
.\mvnw.cmd test
```

Current test suite:

```text
Tests run: 12
Failures: 0
Errors: 0
Skipped: 0
```

---

## 🏛️ Project Structure

```text
src
├── main
│   ├── java
│   │   └── com.apierrorkit
│   │       ├── annotation
│   │       │   └── ApiError.java
│   │       │
│   │       ├── config
│   │       │   ├── ApiErrorKitAutoConfiguration.java
│   │       │   └── ApiErrorKitProperties.java
│   │       │
│   │       ├── handler
│   │       │   └── ProblemExceptionHandler.java
│   │       │
│   │       ├── problem
│   │       │   └── ProblemDetailFactory.java
│   │       │
│   │       └── resolver
│   │           ├── AnnotationExceptionResolver.java
│   │           ├── ExceptionResolver.java
│   │           └── ResolvedApiError.java
│   │
│   └── resources
│       └── META-INF
│           └── spring
│               └── org.springframework.boot.autoconfigure.AutoConfiguration.imports
│
└── test
    └── java
        └── com.apierrorkit
            ├── integration
            ├── problem
            └── resolver
```

---

## 🎯 Design Goals

ApiErrorKit intentionally focuses on a small set of responsibilities:

1. Standardize API error responses.
2. Reduce repetitive exception-handler code.
3. Support existing custom exceptions.
4. Follow RFC 9457 problem-details conventions.
5. Avoid exposing sensitive exception information by default.
6. Remain lightweight and easy to integrate.
7. Provide extension points without introducing unnecessary complexity.

The project intentionally does **not** require external infrastructure such as:

- Redis
- Kafka
- Elasticsearch
- Log aggregation platforms
- Authentication providers
- External databases

---

## 🛠️ Technology Stack

- Java 17
- Spring Boot 4.x
- Spring MVC
- Spring `ProblemDetail`
- Jakarta Bean Validation
- Maven
- JUnit
- MockMvc

---

## 📌 Project Status

**Current version:** `0.0.1-SNAPSHOT`

The project is currently under active development as a portfolio-focused Spring Boot library.

---

## 📄 License

License information will be added before the first public release.