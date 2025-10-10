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
- 👥 **User Management** - Complete user CRUD operations with pagination and enhanced error handling
- 🏪 **Restaurant Management** - Create and manage restaurant profiles with DTOs
- 🍽️ **Menu Management** - Full menu item management with availability tracking and search functionality
- 🔍 **Advanced Search** - Search menu items by name with partial matching (case-insensitive LIKE queries)
- 🐘 **PostgreSQL** - Robust database with Flyway migrations
- 📚 **OpenAPI Documentation** - Interactive Swagger UI
- 🐳 **Docker Ready** - Complete containerization setup
- ✅ **Input Validation** - Comprehensive request validation with proper error responses
- 🔄 **Hot Reload** - Development productivity tools
- 🎯 **DTO Pattern** - Clean separation between entities and API responses
- 🚨 **Enhanced Error Handling** - Detailed error messages and HTTP status codes for better debugging

## 🛠 Tech Stack

### Backend
- **Java 17** - Programming language
- **Spring Boot 3.5.3** - Application framework
- **Spring Security** - Authentication & authorization
- **Spring Web** - REST API development
- **Spring JDBC** - Database connectivity with JdbcClient
- - **JDBC** instead of **JPA** for better control over SQL queries and performance.

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
```
# Login
POST /api/auth/login

# Register (Open Route)
POST /api/auth/register
# - Returns 201: User registered successfully
# - Returns 400: Invalid input data
# - Returns 409: Username or email already exists
```



#### 👥 User Management
```
# Get all users (paginated)
GET /api/users

# Get user by ID
GET /api/users/{id}

# Create user (Admin only)
POST /api/users

# Update user (includes password encryption)
PUT /api/users/{id}

# Delete user (Admin only)
DELETE /api/users/{id}
# - Returns 204: User deleted successfully
# - Returns 404: User not found
# - Returns 409: Cannot delete - user owns restaurants
```

#### 🏪 Restaurant Management
```
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

# Delete restaurant (Owner/Admin)
DELETE /api/restaurants/{id}
```



#### 🍽️ Menu Item Management
```
# Get menu item by ID
GET /api/menu-items/{id}

# Search by name and restaurant (partial match, case-insensitive)
GET /api/menu-items/restaurant/search?name={name}&restaurantId={id}

# Get all items by restaurant
GET /api/menu-items/restaurant?restaurantId={id}

# Get available items
GET /api/menu-items/restaurant/available?available=true&restaurantId={id}

# Create menu item (Owner/Admin)
POST /api/menu-items

# Update menu item (Owner/Admin)
PUT /api/menu-items/{id}

# Delete menu item (Owner/Admin)
DELETE /api/menu-items/{id}
```

### Search Functionality
The menu item search endpoint (`/api/menu-items/restaurant/search`) supports:
- **Partial matching** - Search for "burger" finds "Cheeseburger", "Hamburger", etc.
- **Case-insensitive** - Works with any letter case
- **Multiple results** - Returns all matching items as a list
- **Restaurant filtering** - Scoped to a specific restaurant


### Role-Based Access Control

The application implements three user roles with refined permissions:

- **USER** - Can view restaurants and menu items
- **OWNER** - Can create, update, and delete their own restaurants and menu items
- **ADMIN** - Full access to all endpoints including user management

#### Updated Permissions (v2.2.0)
- Restaurant owners can now delete their own restaurants
- Menu item owners can delete their own menu items
- All users can view individual restaurant details

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
├── controller/          # Integration tests
│   ├── LoginControllerIntegrationTest
│   ├── MenuItemControllerIntegrationTest
│   ├── RestaurantControllerIntegrationTest
│   └── UserControllerIntegrationTest
└── resources/
├── application-test.properties
├── cleanup.sql
└── test-data.sql
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

### Architecture Patterns
#### DTO (Data Transfer Object) Pattern
The application uses DTOs to separate the API layer from the database entities:
- **Benefits**: Security, flexibility, and cleaner API contracts
- **Implementation**: All API endpoints return DTOs instead of entity models
- **Mapping**: Custom mappers convert entities to DTOs in repositories

#### Repository Pattern
- **Interface-based design** - Clean contracts for data access
- **JdbcClient usage** - Modern Spring JDBC approach
- **SQL queries** - Explicit SQL for transparency and performance
- **Error handling** - Proper exception handling for database operations


### Code Style

- Use **Lombok** annotations to reduce boilerplate
- Follow **Spring Boot** best practices
- Implement proper **validation** on DTOs
- Use `@RestController` for API endpoints
- Apply **role-based security** on endpoints
- Return **DTOs** from all API endpoints
- Use `JdbcClient` for database operations
- Implement **proper error handling** and logging
- Return appropriate **HTTP status codes** (201, 204, 409, etc.)
- Provide **meaningful error messages** in responses


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

```
# Run all tests
./mvn test

# Run with coverage
./mvn test jacoco:report

# Run specific test class
./mvn test -Dtest=UserServiceTest
```


### Test Categories

- **Unit Tests** - Service layer testing
- **Integration Tests** - Full application context with test data
    - LoginControllerIntegrationTest
    - MenuItemControllerIntegrationTest
    - RestaurantControllerIntegrationTest
    - UserControllerIntegrationTest
- **Security Tests** - Authentication & authorization
- **End to End** - Postman collections

### Test Infrastructure
- **Test Database**: Uses Testcontainers with PostgreSQL similar to production
- **Isolation**: Each test class runs in isolation with fresh data
- **Flyway Migrations**: Applied to test database on startup
- **Test Data**: Managed via SQL scripts (`test-data.sql`, `cleanup.sql`)
- **BaseIntegrationTest**: Common test configuration and utilities

## 🐳 Docker

### Docker Compose Services

```yaml
services:
  app:          # Spring Boot application (Java 17)
  postgres:     # PostgreSQL database
```

### Docker Commands

```
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

```
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

### Latest Changes (v2.2.0)
- 🛡️ **Enhanced Security**: Updated RBAC - Owners can now delete their own restaurants and menu items
- ✨ **Improved Error Handling**: Registration now returns proper 409 status for duplicate usernames/emails
- 🔒 **User Deletion Protection**: Prevents deletion of users who own restaurants with detailed error messages
- 🧪 **Comprehensive Testing**: Added full integration test suite for all controllers
- 🔐 **Password Validation**: Enhanced password encoding and validation in update operations
- 📚 **Better API Documentation**: Updated OpenAPI responses to reflect all possible status codes
- 🎯 **Error DTOs**: Consistent error response format across all endpoints
- 🔄 **Data Integrity**: Improved handling of database constraint violations with user-friendly messages

### Previous Updates (v2.1.0)
- 🐛 **Fixed**: Menu item search now properly returns multiple results
- ✨ **Enhanced**: Search endpoint supports partial name matching with LIKE queries
- 🔄 **Refactored**: Complete migration to DTO pattern for all API endpoints
- 🎯 **Improved**: Restaurant endpoints now return DTOs instead of entities
- 🔍 **Added**: Case-insensitive search functionality for menu items
- 📚 **Updated**: API documentation to reflect DTO responses
- 🛡️ **Enhanced**: Better error handling in repository layer
- 🧹 **Cleaned**: Removed entity exposure from API layer

### Bug Fixes (v2.2.0)
- Fixed user deletion to check for restaurant ownership
- Corrected HTTP status codes for conflict scenarios (409)
- Improved error messages for duplicate username/email registration
- Enhanced password validation to prevent null/empty passwords
- Fixed security configuration for restaurant GET endpoints

### Bug Fixes (v2.1.0)
- Fixed `IncorrectResultSizeDataAccessException` in menu item search
- Corrected SQL queries to use proper column names (`is_available` instead of `available`)
- Improved repository methods to handle multiple results correctly


---

**Made with ❤️ for FIAP 2025 Java Tech Challenge**