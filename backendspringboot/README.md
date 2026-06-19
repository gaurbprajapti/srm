# 🏛️ SRM Campus Nexus API - Spring Boot Edition

A comprehensive backend API built with **Spring Boot 3.2**, featuring **JWT Authentication**, **Hibernate JPA**, and **Spring AOP** for the SRM Campus Nexus application.

## 🚀 Features

### 🔐 Security & Authentication
- **JWT Authentication** with access & refresh tokens
- **Spring Security** integration
- **BCrypt password** encryption
- **Role-based access control** (USER/ADMIN)
- **CORS configuration** for frontend integration

### 🗄️ Database & Persistence
- **Hibernate JPA** with automatic table generation
- **PostgreSQL** support for production
- **H2 Database** for development/testing
- **Entity auditing** with timestamps
- **Custom repository queries** with JPQL

### 🎯 Aspect-Oriented Programming (AOP)
- **Logging Aspect** - Method execution timing and debugging
- **Security Aspect** - Authentication and authorization auditing
- **Performance monitoring** - Slow query detection
- **Exception tracking** - Comprehensive error logging

### 📊 Monitoring & Management
- **Spring Boot Actuator** for health checks
- **Structured logging** with SLF4J
- **Performance metrics** tracking
- **Database connection monitoring**

### 🌐 API Features
- **RESTful endpoints** for Users, Clubs, and Jobs
- **Pagination support** for large datasets
- **Search and filtering** capabilities
- **File upload** handling
- **Input validation** with Bean Validation

## 🏗️ Architecture

```
src/main/java/com/srm/campusnexus/
├── aspect/          # AOP aspects for cross-cutting concerns
├── config/          # Configuration classes
├── controller/      # REST controllers
├── entity/          # JPA entities
├── repository/      # Data access layer
├── security/        # JWT & Spring Security
├── service/         # Business logic layer
└── CampusNexusApplication.java
```

## 🛠️ Technology Stack

- **Framework**: Spring Boot 3.2.0
- **Security**: Spring Security + JWT
- **Database**: PostgreSQL (Production) / H2 (Development)
- **ORM**: Hibernate JPA
- **AOP**: Spring AOP with AspectJ
- **Build Tool**: Maven
- **Java Version**: 17+

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- PostgreSQL (for production)

### 1. Clone & Navigate
```bash
git clone <repository-url>
cd backendspringboot
```

### 2. Development Setup
```bash
# Run with H2 in-memory database
./mvnw spring-boot:run
```

### 3. Production Setup
```bash
# Set environment variables
export DATABASE_URL=jdbc:postgresql://localhost:5432/campus_nexus
export DB_USERNAME=your_username
export DB_PASSWORD=your_password
export JWT_SECRET=your-super-secret-jwt-key-at-least-32-characters-long
export JWT_REFRESH_SECRET=your-different-refresh-secret-key

# Build and run
./mvnw clean package
java -jar target/campus-nexus-api-2.0.0.jar
```

## 🔧 Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `DATABASE_URL` | Database connection URL | H2 in-memory |
| `DB_USERNAME` | Database username | sa |
| `DB_PASSWORD` | Database password | password |
| `JWT_SECRET` | JWT signing secret | (required) |
| `JWT_REFRESH_SECRET` | Refresh token secret | (required) |
| `FRONTEND_URL` | Frontend URL for CORS | http://localhost:3000 |
| `PORT` | Server port | 5050 |

### Application Profiles

- **Default**: Development with H2 database
- **Production**: PostgreSQL with optimized settings

## 📡 API Endpoints

### 🔐 Authentication
- `POST /api/user/register` - User registration
- `POST /api/user/login` - User login
- `POST /api/user/refresh-token` - Token refresh
- `POST /api/user/logout` - User logout

### 👥 User Management
- `GET /api/user/profile` - Get user profile
- `PUT /api/user/profile` - Update profile
- `PUT /api/user/change-password` - Change password
- `GET /api/user/users` - List users (Admin)
- `DELETE /api/user/users/{id}` - Delete user (Admin)

### 🏢 Club Management
- `GET /api/clubs` - List clubs (with pagination & filters)
- `POST /api/clubs` - Create club
- `GET /api/clubs/{id}` - Get club details
- `PUT /api/clubs/{id}` - Update club
- `DELETE /api/clubs/{id}` - Delete club

### 💼 Job Management
- `GET /api/jobs` - List jobs (with pagination & filters)
- `POST /api/jobs` - Create job posting
- `GET /api/jobs/{id}` - Get job details
- `PUT /api/jobs/{id}` - Update job
- `DELETE /api/jobs/{id}` - Delete job

### 📊 System
- `GET /api/health` - Health check
- `GET /api/` - API documentation
- `GET /actuator/health` - Spring Boot health

## 🎯 AOP Features

### Logging Aspect
- **Method Entry/Exit**: Logs all service and controller method calls
- **Execution Time**: Tracks method performance
- **Exception Logging**: Captures and logs all exceptions
- **Slow Query Detection**: Warns about methods taking >1 second

### Security Aspect
- **Authentication Audit**: Logs login/logout attempts
- **Admin Operations**: Tracks privileged operations
- **Unauthorized Access**: Logs security violations
- **User Activity**: Monitors user actions

## 🔒 Security Features

### JWT Implementation
- **Access Tokens**: 7-day expiration
- **Refresh Tokens**: 30-day expiration
- **Secure Headers**: HMAC-SHA signing
- **Token Validation**: Comprehensive error handling

### CORS Configuration
- **Multiple Origins**: Development and production URLs
- **Credential Support**: Cookie and auth header support
- **Method Whitelist**: Secure HTTP method restrictions

## 📊 Database Schema

### Users Table
- Authentication and profile information
- JSON fields for education, skills, experience, projects
- Role-based access control

### Clubs Table
- Club information and management
- Foreign key relationships to users
- JSON arrays for achievements and members

### Jobs Table
- Job posting management
- Enum types for job type and campus
- Full-text search capabilities

## 🚀 Deployment

### Render Deployment
1. Connect GitHub repository
2. Set root directory to `backendspringboot`
3. Use build command: `./mvnw clean package -DskipTests`
4. Use start command: `java -jar target/campus-nexus-api-2.0.0.jar`
5. Set environment variables in Render dashboard

### Environment Variables for Production
```bash
DATABASE_URL=postgresql://username:password@host:port/database
JWT_SECRET=your-production-secret-key
JWT_REFRESH_SECRET=your-production-refresh-secret
FRONTEND_URL=https://your-frontend-domain.com
DDL_AUTO=update
HIBERNATE_DIALECT=org.hibernate.dialect.PostgreSQLDialect
```

## 🧪 Testing

```bash
# Run all tests
./mvnw test

# Run with coverage
./mvnw test jacoco:report

# Integration tests
./mvnw verify
```

## 🐛 Debugging

### H2 Console (Development)
- URL: `http://localhost:5050/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (empty)

### Logging Levels
```yaml
logging:
  level:
    com.srm.campusnexus: DEBUG
    org.springframework.security: DEBUG
    org.hibernate.SQL: DEBUG
```

## 📈 Performance

### Optimizations
- **Connection Pooling**: HikariCP
- **Query Optimization**: JPQL with indexes
- **Lazy Loading**: JPA relationships
- **Caching**: Method-level caching ready

### Monitoring
- **Actuator Endpoints**: Health and metrics
- **AOP Performance**: Method execution timing
- **Database Metrics**: Query performance tracking

## 🆚 Node.js vs Spring Boot

This Spring Boot version provides:
- ✅ **Better Type Safety** with Java
- ✅ **Enterprise Features** with Spring ecosystem
- ✅ **Powerful AOP** capabilities
- ✅ **Robust Security** with Spring Security
- ✅ **Better Debugging** and tooling
- ✅ **Scalability** for large applications

## 🤝 Contributing

1. Fork the repository
2. Create feature branch
3. Make changes with tests
4. Submit pull request

## 📄 License

This project is licensed under the MIT License.

---

**Built with ❤️ using Spring Boot, Hibernate, and Spring AOP** 