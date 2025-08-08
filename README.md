
# 🍕 QuickBite

> **FIAP 2025 - Java Tech Challenge**

A modern Spring Boot application for food service management with JWT authentication, PostgreSQL database, and Docker containerization.

## 📋 Table of Contents

- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Quick Start](#-quick-start)
- [Installation](#-installation)
- [Usage](#-usage)
- [API Documentation](#-api-documentation)
- [Development](#-development)
- [Testing](#-testing)
- [Docker](#-docker)
- [Contributing](#-contributing)
- [License](#-license)

## ✨ Features

- 🔐 **JWT Authentication** - Secure token-based authentication
- 🛡️ **Spring Security** - Comprehensive security configuration
- 🐘 **PostgreSQL** - Robust database with Flyway migrations
- 📚 **OpenAPI Documentation** - Interactive Swagger UI
- 🐳 **Docker Ready** - Complete containerization setup
- ✅ **Input Validation** - Comprehensive request validation
- 🔄 **Hot Reload** - Development productivity tools

## 🛠 Tech Stack

### Backend
- **Java 21** - Programming language
- **Spring Boot 3.5.3** - Application framework
- **Spring Security** - Authentication & authorization
- **Spring Web** - REST API development
- **Spring JDBC** - Database connectivity

### Database
- **PostgreSQL 15.3** - Primary database
- **Flyway** - Database migration management

### Security
- **JWT (JJWT 0.12.6)** - JSON Web Token implementation

### Documentation
- **SpringDoc OpenAPI 3** - API documentation

### Development Tools
- **Lombok** - Boilerplate code reduction
- **Spring Boot DevTools** - Development productivity
- **Maven** - Build automation

## 🚀 Quick Start

### Using Docker Compose (Recommended)
```
bash
# Clone the repository
git clone https://github.com/dimermichel/quickbite.git
cd quickbite

# Start the application with Docker Compose
docker-compose up -d

# Access the application
open http://localhost:8080
```
### Local Development
```
bash
# Prerequisites: Java 21+, Maven 3.6+, PostgreSQL

# Clone and navigate to project
git clone https://github.com/dimermichel/quickbite.git
cd quickbite

# Build the project
./mvnw clean compile

# Run the application
./mvnw spring-boot:run
```
## 📦 Installation

### Prerequisites

- **Java 21** or higher
- **Maven 3.6+**
- **Docker & Docker Compose** (for containerized setup)
- **PostgreSQL 15+** (for local development)

### Environment Setup

1. **Clone the repository:**
   ```bash
   git clone https://github.com/dimermichel/quickbite.git
   cd quickbite
   ```

2. **Set up environment variables (optional):**
   ```bash
   export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/quickbite
   export SPRING_DATASOURCE_USERNAME=postgres
   export SPRING_DATASOURCE_PASSWORD=your_password
   ```

3. **Build the project:**
   ```bash
   ./mvnw clean install
   ```

## 🎯 Usage

### Starting the Application

**With Docker Compose:**
```
bash
docker-compose up -d
```
**Local Development:**
```bash
./mvnw spring-boot:run
```

### Application URLs

- **Application**: http://localhost:8080
- **API Documentation**: http://localhost:8080/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8080/v3/api-docs

### Database Access

- **Host**: localhost
- **Port**: 5432
- **Database**: quickbite
- **Username**: postgres
- **Password**: postgres (development)

## 📖 API Documentation

The application includes comprehensive API documentation using OpenAPI 3.0.

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **JSON Spec**: http://localhost:8080/v3/api-docs

### Sample API Endpoints

```shell script
# Authentication
POST /api/auth/login
POST /api/auth/register

# Protected endpoints (require JWT token)
GET /api/users
POST /api/users
```


## 🔧 Development

### Project Structure

```
src/
├── main/
│   ├── java/com/michelmaia/quickbite/
│   │   ├── config/          # Configuration classes
│   │   ├── controller/      # REST controllers
│   │   ├── service/         # Business logic
│   │   ├── repository/      # Data access layer
│   │   ├── model/           # Entity models
│   │   ├── dto/             # Data transfer objects
│   │   └── security/        # Security configuration
│   └── resources/
│       ├── db/migration/    # Flyway migration scripts
│       └── application.yml  # Application configuration
└── test/                    # Test classes
```


### Code Style

- Use **Lombok** annotations to reduce boilerplate
- Follow **Spring Boot** best practices
- Implement proper **validation** on DTOs
- Use **@RestController** for API endpoints

### Database Migrations

Create new migration files in `src/main/resources/db/migration/`:

```sql
-- V1__create-initial-schemas.sql
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    address_id BIGINT,
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (address_id) REFERENCES addresses(id) ON DELETE SET NULL
);
```


## 🧪 Testing

### Running Tests

```shell script
# Run all tests
./mvnw test

# Run with coverage
./mvnw test jacoco:report

# Run specific test class
./mvnw test -Dtest=UserServiceTest
```


### Test Categories

- **Unit Tests** - Service layer testing
- **Integration Tests** - Full application context
- **Security Tests** - Authentication & authorization

## 🐳 Docker

### Docker Compose Services

```yaml
services:
  app:          # Spring Boot application
  postgres:     # PostgreSQL database
```


### Docker Commands

```shell script
# Build and start all services
docker-compose up -d

# View logs
docker-compose logs -f app

# Stop all services
docker-compose down

# Rebuild and start
docker-compose up --build -d
```


### Production Deployment

```shell script
# Build production image
docker build -t quickbite:latest .

# Run production container
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://your-db:5432/quickbite \
  quickbite:latest
```


## 🤝 Contributing

1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/amazing-feature`)
3. **Commit** your changes (`git commit -m 'Add amazing feature'`)
4. **Push** to the branch (`git push origin feature/amazing-feature`)
5. **Open** a Pull Request

### Development Guidelines

- Follow existing code style and patterns
- Write tests for new features
- Update documentation as needed
- Use meaningful commit messages

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Authors

- **Michel Maia** - *Initial work* - [GitHub Profile](https://github.com/dimermichel)

## 🙏 Acknowledgments

- FIAP 2025 Java Tech Challenge
- Spring Boot community
- PostgreSQL team

---

## 📞 Support

If you have any questions or need help, please:

1. Check the [documentation](http://localhost:8080/swagger-ui.html)
2. Search existing [issues](https://github.com/dimermichel/quickbite/issues)
3. Create a [new issue](https://github.com/dimermichel/quickbite/issues/new)

---

**Made with ❤️ for FIAP 2025 Java Tech Challenge**

