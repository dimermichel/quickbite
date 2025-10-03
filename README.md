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
- 🛡️ **Spring Security** - Comprehensive security configuration with role-based access control
- 👥 **User Management** - Complete user CRUD operations with pagination
- 🏪 **Restaurant Management** - Create and manage restaurant profiles
- 🍽️ **Menu Management** - Full menu item management with availability tracking
- 🐘 **PostgreSQL** - Robust database with Flyway migrations
- 📚 **OpenAPI Documentation** - Interactive Swagger UI
- 🐳 **Docker Ready** - Complete containerization setup
- ✅ **Input Validation** - Comprehensive request validation
- 🔄 **Hot Reload** - Development productivity tools

## 🛠 Tech Stack

### Backend
- **Java 17** - Programming language
- **Spring Boot 3.5.3** - Application framework
- **Spring Security** - Authentication & authorization
- **Spring Web** - REST API development
- **Spring JDBC** - Database connectivity

### Database
- **PostgreSQL 15.3** - Primary database
- **Flyway** - Database migration management

### Security
- **JWT (JJWT 0.12.6)** - JSON Web Token implementation
- **BCrypt** - Password hashing

### Documentation
- **SpringDoc OpenAPI 3** - API documentation

### Development Tools
- **Lombok** - Boilerplate code reduction
- **Spring Boot DevTools** - Development productivity
- **Maven** - Build automation

## 🚀 Quick Start

### Using Docker Compose (Recommended)
```bash
# Clone the repository
git clone https://github.com/dimermichel/quickbite.git
cd quickbite

# Start the application with Docker Compose
docker-compose up -d

# Access the application
open http://localhost:8080
```
### Local Development
```bash
# Prerequisites: Java 17+, Maven 3.6+, PostgreSQL

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

- **Java 17** or higher
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
```bash
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

### Default Credentials

The application comes with a pre-configured admin user:
- **Username**: admin
- **Password**: admin
- **Role**: ADMIN

## 📖 API Documentation

The application includes comprehensive API documentation using OpenAPI 3.0.

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **JSON Spec**: http://localhost:8080/v3/api-docs

### API Modules

#### 🔐 Authentication
```shell script
# Login
POST /api/auth/login

# Register
POST /api/auth/register
```


#### 👥 User Management
```shell script
# Get all users (paginated)
GET /api/users

# Get user by ID
GET /api/users/{id}

# Create user (Admin only)
POST /api/users

# Update user
PUT /api/users/{id}

# Delete user (Admin only)
DELETE /api/users/{id}
```


#### 🏪 Restaurant Management
```shell script
# Get all restaurants (paginated)
GET /api/restaurants

# Filter by cuisine
GET /api/restaurants/by-cuisine?cuisine={cuisine}

# Filter by rating
GET /api/restaurants/by-rating?minRating={rating}

# Get restaurant by ID
GET /api/restaurants/{id}

# Create restaurant (Owner/Admin)
POST /api/restaurants

# Update restaurant (Owner/Admin)
PUT /api/restaurants/{id}

# Delete restaurant (Admin only)
DELETE /api/restaurants/{id}
```


#### 🍽️ Menu Item Management
```shell script
# Get menu item by ID
GET /api/menu-items/{id}

# Search by name and restaurant
GET /api/menu-items/restaurant/search?name={name}&restaurantId={id}

# Get all items by restaurant
GET /api/menu-items/restaurant?restaurantId={id}

# Get available items
GET /api/menu-items/restaurant/available?available=true&restaurantId={id}

# Create menu item (Owner/Admin)
POST /api/menu-items

# Update menu item (Owner/Admin)
PUT /api/menu-items/{id}

# Delete menu item (Admin only)
DELETE /api/menu-items/{id}
```

### Role-Based Access Control

The application implements three user roles:

- **USER** - Can view restaurants and menu items
- **OWNER** - Can create and manage restaurants and menu items
- **ADMIN** - Full access to all endpoints including user management

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
│   │   ├── mapper/          # Row mappers for JDBC
│   │   └── security/        # Security configuration
│   └── resources/
│       ├── db/migration/    # Flyway migration scripts
│       │   ├── V1__create-initial-schemas.sql
│       │   └── V2__create-restaurant-menu-schemas.sql
│       └── application.yml  # Application configuration
└── test/                    # Test classes
```


### Database Schema

#### V1 - Initial Schema
- **users** - User accounts with authentication
- **addresses** - Physical addresses
- **roles** - User roles (USER, OWNER, ADMIN)
- **user_roles** - Many-to-many relationship

#### V2 - Restaurant & Menu Schema
- **restaurants** - Restaurant profiles with owner and address
- **menu_items** - Menu items linked to restaurants

### Code Style

- Use **Lombok** annotations to reduce boilerplate
- Follow **Spring Boot** best practices
- Implement proper **validation** on DTOs
- Use **@RestController** for API endpoints
- Apply **role-based security** on endpoints

### Database Migrations

Create new migration files in `src/main/resources/db/migration/`:

```sql
-- V3__your-migration-name.sql
CREATE TABLE IF NOT EXISTS your_table (
    id BIGSERIAL PRIMARY KEY,
    -- your columns
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
  app:          # Spring Boot application (Java 17)
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
- Ensure all tests pass before submitting PR

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

## 📝 Recent Updates

### Latest Changes
- ✅ Added Restaurant Management system with full CRUD operations
- ✅ Implemented Menu Item Management with availability tracking
- ✅ Enhanced role-based access control (USER, OWNER, ADMIN roles)
- ✅ Migrated from Java 21 to Java 17 for broader compatibility
- ✅ Improved security configuration with generic PasswordEncoder
- ✅ Added pagination support for restaurant listings
- ✅ Enhanced data models with proper relationships
- ✅ Updated Docker configuration for Java 17

---

**Made with ❤️ for FIAP 2025 Java Tech Challenge**
