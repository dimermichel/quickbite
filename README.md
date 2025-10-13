
#  QuickBite

> **FIAP 2025 - Java Tech Challenge**
>
> A modern Spring Boot application for food service management following **Clean Architecture** principles with JWT authentication, PostgreSQL database, and Docker containerization.

##  Table of Contents

- [Features](#-features)
- [Architecture](#-architecture)
- [Tech Stack](#-tech-stack)
- [Quick Start](#-quick-start)
- [Installation](#-installation)
- [Usage](#-usage)
- [API Documentation](#-api-documentation)
- [Development](#-development)
- [Testing](#-testing)
- [Docker](#-docker)
- [Clean Architecture](#-clean-architecture)
- [Contributing](#-contributing)
- [License](#-license)

## ✨ Features

-  **Clean Architecture** - Domain-centric design with clear separation of concerns
-  **Use Case Pattern** - Single responsibility for each business operation
-  **JWT Authentication** - Secure token-based authentication
- ️ **Spring Security** - Comprehensive security configuration with role-based access control
-  **User Management** - Complete user CRUD operations with pagination and enhanced error handling
-  **Restaurant Management** - Create and manage restaurant profiles with full CRUD operations
- ️ **Menu Management** - Full menu item management with availability tracking and search functionality
-  **Advanced Search** - Search menu items by name with partial matching (case-insensitive LIKE queries)
-  **PostgreSQL** - Robust database with Flyway migrations
-  **OpenAPI Documentation** - Interactive Swagger UI
-  **Docker Ready** - Complete containerization setup
- ✅ **Input Validation** - Comprehensive request validation with proper error responses
-  **Hot Reload** - Development productivity tools
-  **Domain-Driven Design** - Pure domain entities with business logic
-  **Enhanced Error Handling** - Global exception handling with meaningful messages

##  Architecture

This application follows **Clean Architecture** principles, ensuring:
- ✅ **Independence of Frameworks** - Business logic doesn't depend on Spring
- ✅ **Testability** - Business rules can be tested without UI, database, or external elements
- ✅ **Independence of UI** - Easy to swap presentation layers
- ✅ **Independence of Database** - Business rules are not bound to the database
- ✅ **Independence of External Agencies** - Business rules don't know about the outside world

### Architecture Layers

```
┌─────────────────────────────────────────────────────────┐
│                   Presentation Layer                     │
│              (REST Controllers, DTOs)                    │
└────────────────────┬────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────┐
│                  Application Layer                       │
│        (Use Cases, Application Services)                 │
└────────────────────┬────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────┐
│                    Domain Layer                          │
│     (Entities, Business Rules, Repository Interfaces)    │
└────────────────────┬────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────┐
│                 Infrastructure Layer                     │
│    (Repository Implementations, Security, Config)        │
└─────────────────────────────────────────────────────────┘
```

##  Tech Stack

### Backend
- **Java 17** - Programming language
- **Spring Boot 3.5.3** - Application framework
- **Spring Security** - Authentication & authorization
- **Spring Web** - REST API development
- **Spring JDBC** - Database connectivity with JdbcClient
- **JDBC** instead of **JPA** - Better control over SQL queries and performance

### Database
- **PostgreSQL 15.3** - Primary database
- **Flyway** - Database migration management

### Security
- **JWT (JJWT 0.12.6)** - JSON Web Token implementation
- **BCrypt** - Password hashing

### Documentation
- **SpringDoc OpenAPI 3** - API documentation

### Development Tools
- **Maven** - Build automation
- **Docker** - Containerization

##  Quick Start

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
./mvn clean compile

# Run the application
./mvn spring-boot:run
```

##  Installation

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
   ./mvn clean install
   ```

##  Usage

### Starting the Application

**With Docker Compose:**
```bash
docker-compose up -d
```

**Local Development:**
```bash
./mvn spring-boot:run
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

##  API Documentation

The application includes comprehensive API documentation using OpenAPI 3.0.

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **JSON Spec**: http://localhost:8080/v3/api-docs

### API Modules

####  Authentication
```
# Login
POST /api/login

# Change Password
POST /api/change-password

# Register (Open Route)
POST /api/users/register
```

####  User Management
```
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

####  Restaurant Management
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

#### ️ Menu Item Management
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

### Role-Based Access Control

The application implements three user roles:

- **USER** - Can view restaurants and menu items
- **OWNER** - Can create, update, and delete their own restaurants and menu items
- **ADMIN** - Full access to all endpoints including user management

##  Development

### Project Structure (Clean Architecture)

```
src/main/java/com/michelmaia/quickbite/
├── domain/                           # Business Logic (Framework-Independent)
│   ├── user/
│   │   ├── entity/                   # User, Role (Pure domain entities)
│   │   ├── repository/               # UserRepository interface
│   │   └── exception/                # Domain exceptions
│   ├── restaurant/
│   │   ├── entity/                   # Restaurant entity
│   │   ├── repository/               # RestaurantRepository interface
│   │   └── exception/                # Restaurant exceptions
│   ├── menuitem/
│   │   ├── entity/                   # MenuItem entity
│   │   ├── repository/               # MenuItemRepository interface
│   │   └── exception/                # MenuItem exceptions
│   ├── auth/
│   │   └── exception/                # Authentication exceptions
│   └── common/
│       └── entity/                   # Shared entities (Address)
│
├── application/                      # Use Cases & Application Services
│   ├── usecase/
│   │   ├── user/                     # User use cases
│   │   │   ├── RegisterUserUseCase
│   │   │   ├── CreateUserUseCase
│   │   │   ├── GetUserUseCase
│   │   │   ├── ListUsersUseCase
│   │   │   ├── UpdateUserUseCase
│   │   │   └── DeleteUserUseCase
│   │   ├── auth/                     # Auth use cases
│   │   │   ├── LoginUseCase
│   │   │   └── ChangePasswordUseCase
│   │   ├── restaurant/               # Restaurant use cases
│   │   │   ├── CreateRestaurantUseCase
│   │   │   ├── GetRestaurantUseCase
│   │   │   ├── ListRestaurantsUseCase
│   │   │   ├── UpdateRestaurantUseCase
│   │   │   └── DeleteRestaurantUseCase
│   │   └── menuitem/                 # MenuItem use cases
│   │       ├── CreateMenuItemUseCase
│   │       ├── GetMenuItemUseCase
│   │       ├── ListMenuItemsUseCase
│   │       ├── UpdateMenuItemUseCase
│   │       └── DeleteMenuItemUseCase
│   ├── service/                      # Application service interfaces
│   │   ├── PasswordEncoder
│   │   └── TokenGenerator
│   └── dto/                          # Shared DTOs
│       └── PageResponseDTO
│
├── infrastructure/                   # External Concerns
│   ├── persistence/                  # Repository implementations
│   │   ├── user/
│   │   │   ├── UserRepositoryAdapter
│   │   │   └── UserJdbcMapper
│   │   ├── restaurant/
│   │   │   ├── RestaurantRepositoryAdapter
│   │   │   └── RestaurantJdbcMapper
│   │   └── menuitem/
│   │       ├── MenuItemRepositoryAdapter
│   │       └── MenuItemJdbcMapper
│   ├── security/                     # Security adapters
│   │   ├── PasswordEncoderAdapter
│   │   ├── TokenGeneratorAdapter
│   │   ├── JWTCreator
│   │   ├── JWTFilter
│   │   ├── SecurityConfig
│   │   └── WebSecurityConfig
│   └── config/                       # Configuration
│       ├── UseCaseConfig
│       └── OpenAPIConfig
│
└── presentation/                     # External Interfaces
    └── rest/                         # REST API
        ├── user/
        │   ├── UserController
        │   └── dto/                  # User DTOs
        ├── auth/
        │   ├── AuthController
        │   └── dto/                  # Auth DTOs
        ├── restaurant/
        │   ├── RestaurantController
        │   └── dto/                  # Restaurant DTOs
        ├── menuitem/
        │   ├── MenuItemController
        │   └── dto/                  # MenuItem DTOs
        └── common/
            ├── GlobalExceptionHandler
            └── ErrorDTO
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

### Clean Architecture Principles

#### 1. Domain Layer (Business Logic)
- **Pure Java** - No framework dependencies
- **Entities** - Encapsulate business rules and validations
- **Repository Interfaces** - Defined by domain needs
- **Exceptions** - Domain-specific exceptions

**Example:**
```java
// Domain Entity - Pure business logic
public class Restaurant {
    private final Long id;
    private String name;
    
    private Restaurant(...) {
        validate();
    }
    
    private void validate() {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidRestaurantDataException("Name cannot be empty");
        }
    }
    
    public void updateInfo(String name, ...) {
        this.name = name;
        validate();
    }
}
```

#### 2. Application Layer (Use Cases)
- **Single Responsibility** - Each use case does ONE thing
- **Orchestration** - Coordinates domain entities and repositories
- **Business Workflows** - Implements application-specific business rules

**Example:**
```java
public class CreateRestaurantUseCase {
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    
    public Restaurant execute(CreateRestaurantCommand command) {
        // Verify owner exists
        userRepository.findById(command.ownerId())
            .orElseThrow(() -> new UserNotFoundException(...));
        
        // Create domain entity
        Restaurant restaurant = Restaurant.createNew(...);
        
        // Persist
        return restaurantRepository.save(restaurant);
    }
}
```

#### 3. Infrastructure Layer (Technical Details)
- **Repository Implementations** - JDBC, JPA, etc.
- **External Services** - JWT, password encoding
- **Framework Configuration** - Spring beans, security

**Example:**
```java
@Repository
public class RestaurantRepositoryAdapter implements RestaurantRepository {
    private final JdbcClient jdbcClient;
    
    @Override
    public Restaurant save(Restaurant restaurant) {
        // JDBC implementation details
        // Maps domain entity to database
    }
}
```

#### 4. Presentation Layer (External Interfaces)
- **Controllers** - Thin layer, delegates to use cases
- **DTOs** - Request/Response models
- **Exception Handling** - Converts domain exceptions to HTTP responses

**Example:**
```java
@RestController
public class RestaurantController {
    private final CreateRestaurantUseCase createRestaurantUseCase;
    
    @PostMapping("/api/restaurants")
    public ResponseEntity<RestaurantResponse> create(@Valid @RequestBody CreateRestaurantRequest request) {
        var command = new CreateRestaurantUseCase.CreateRestaurantCommand(...);
        Restaurant restaurant = createRestaurantUseCase.execute(command);
        return ResponseEntity.ok(RestaurantResponse.fromDomain(restaurant));
    }
}
```

### Code Style

- **Domain Entities** - No framework annotations, pure Java
- **Use Cases** - Constructor injection, immutable commands
- **Repository Adapters** - Implement domain interfaces
- **Controllers** - Thin, validation only
- **DTOs** - Java records for immutability
- **Exception Handling** - Global exception handler

##  Testing

### Running Tests

```bash
# Run all tests
./mvn test

# Run with coverage
./mvn test jacoco:report

# Run specific test class
./mvn test -Dtest=UserControllerIntegrationTest
```

### Test Categories

- **Unit Tests** - Test use cases in isolation (without Spring)
- **Integration Tests** - Full application context with test data
    - LoginControllerIntegrationTest
    - MenuItemControllerIntegrationTest
    - RestaurantControllerIntegrationTest
    - UserControllerIntegrationTest
- **Domain Tests** - Test domain entity business rules

### Test Infrastructure
- **Test Database**: PostgreSQL with Flyway migrations
- **Test Isolation**: Each test runs with fresh data
- **Test Data**: Managed via SQL scripts
- **BaseIntegrationTest**: Common test configuration

##  Docker

### Docker Compose Services

```yaml
services:
  app:          # Spring Boot application (Java 17)
  postgres:     # PostgreSQL database
```

### Docker Commands

```bash
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

```bash
# Build production image
docker build -t quickbite:latest .

# Run production container
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://your-db:5432/quickbite \
  quickbite:latest
```

##  Clean Architecture

### Benefits Achieved

1. ✅ **Framework Independence** - Business logic doesn't depend on Spring
2. ✅ **Testability** - Easy to unit test without infrastructure
3. ✅ **Maintainability** - Clear separation of concerns
4. ✅ **Flexibility** - Easy to swap implementations
5. ✅ **Scalability** - Clean boundaries for growth

### Dependency Flow

```
Presentation → Application → Domain ← Infrastructure
                                ↑
                          (implements)
```

- **Presentation** depends on Application
- **Application** depends on Domain
- **Infrastructure** implements Domain interfaces
- **Domain** depends on nothing (pure business logic)

### Key Patterns

- **Use Case Pattern** - Single responsibility for each operation
- **Repository Pattern** - Abstract data access
- **Adapter Pattern** - Infrastructure implements domain interfaces
- **DTO Pattern** - Separate API contracts from domain
- **Factory Pattern** - Create domain entities safely

##  Contributing

1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/amazing-feature`)
3. **Commit** your changes (`git commit -m 'Add amazing feature'`)
4. **Push** to the branch (`git push origin feature/amazing-feature`)
5. **Open** a Pull Request

### Development Guidelines

- Follow Clean Architecture principles
- Keep domain layer pure (no framework dependencies)
- Write use cases for business operations
- Test domain entities and use cases
- Use meaningful commit messages
- Ensure all tests pass before submitting PR

##  License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

##  Authors

- **Michel Maia** - *Initial work* - [GitHub Profile](https://github.com/dimermichel)

##  Acknowledgments

- FIAP 2025 Java Tech Challenge
- Clean Architecture by Robert C. Martin
- Domain-Driven Design community
- Spring Boot community
- PostgreSQL team

---

##  Support

If you have any questions or need help, please:

1. Check the [documentation](http://localhost:8080/swagger-ui.html)
2. Search existing [issues](https://github.com/dimermichel/quickbite/issues)
3. Create a [new issue](https://github.com/dimermichel/quickbite/issues/new)

---

##  Recent Updates

### Latest Changes (v3.0.0) - Clean Architecture Refactor
-  **Complete Refactor**: Migrated entire application to Clean Architecture
-  **Domain Layer**: Pure business logic with no framework dependencies
-  **Use Cases**: Single-responsibility operations for all features
-  **Repository Pattern**: Domain-defined interfaces, infrastructure implementations
-  **Testability**: Easy to unit test business logic in isolation
-  **Maintainability**: Clear separation between layers
-  **User Feature**: 6 use cases (Register, Create, Get, List, Update, Delete)
-  **Auth Feature**: 2 use cases (Login, ChangePassword)
-  **Restaurant Feature**: 5 use cases (Create, Get, List, Update, Delete)
-  **MenuItem Feature**: 5 use cases (Create, Get, List, Update, Delete)
-  **Global Error Handling**: Centralized exception handling
-  **Pure Domain Entities**: Business rules encapsulated in entities

### Previous Updates (v2.2.0)
- ️ **Enhanced Security**: Updated RBAC - Owners can now delete their own restaurants and menu items
- ✨ **Improved Error Handling**: Registration now returns proper 409 status for duplicate usernames/emails
-  **User Deletion Protection**: Prevents deletion of users who own restaurants with detailed error messages
-  **Comprehensive Testing**: Added full integration test suite for all controllers
-  **Password Validation**: Enhanced password encoding and validation in update operations

---

**Made with ❤️ for FIAP 2025 Java Tech Challenge**

**Following Clean Architecture by Robert C. Martin (Uncle Bob)**