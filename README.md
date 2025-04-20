# Card Manager API

![Java](https://img.shields.io/badge/java-21-blue)
![Spring Boot](https://img.shields.io/badge/spring%20boot-3.2.4-green)
![MySQL](https://img.shields.io/badge/mysql-8.0-blue)

## Key Features
- Secure card management with AES-256 encryption
- JWT authentication with role-based access
- Transaction processing with daily/monthly limits
- MySQL database integration
- Liquibase for schema migrations

## Tech Stack
- **Backend**: Java 21, Spring Boot 3.2.4
- **Database**: MySQL 8.0
- **Security**: Spring Security 6, JWT
- **Testing**: JUnit 5, Mockito

## Prerequisites
- Java 21 JDK
- MySQL 8.0+
- Maven 3.9+

## Configuration
`.env` setup:
```yaml
MYSQL_PASSWORD=EXAMPLEPASSWORD
MYSQL_USERNAME=EXAMPLEUSERNAME
MYSQL_URL=EXAMPLEURL
REDIS_HOST=EXAMPLEHOST
REDIS_PORT=EXAMPLEPORT
```

## Running the Application
1. Start MySQL:
```bash
docker run --name card-mysql -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=card_db -p 3306:3306 -d mysql:8.0
```

2. Build and run:
```bash
mvn clean install
mvn spring-boot:run
```

3. Or u can use docker:
```bash
docker-compose build
```

## Database Setup
Liquibase migrations are automatically applied on startup. Key tables:
- `cards` - Payment card information
- `users` - User accounts
- `transactions` - Transaction records
- `card_limits` - Spending limits

## Testing
Run tests with:
```bash
mvn test
```

## Security Implementation
- JWT authentication filter
- Password encryption with BCrypt
- Role hierarchy (ADMIN > USER)
- Secure headers configuration