# 🛡️ Auth Service - MaitriConnect

Authentication and Authorization microservice for the MaitriConnect chat application.

## Features

### Core Functionality
- ✅ User Registration & Login
- ✅ JWT Token Management (Access & Refresh Tokens)
- ✅ Password Reset Flow
- ✅ Email Verification
- ✅ Session Management
- ✅ Role-Based Access Control (USER, ADMIN, MODERATOR)
- ✅ Account Lockout after Failed Login Attempts
- ✅ Redis Caching for User Profiles
- ✅ Token Blacklisting
- ✅ Rate Limiting

### Security Features
- BCrypt Password Hashing
- JWT with 1-hour expiration
- Refresh Token with 7-day expiration
- Session tracking with device info
- IP address logging
- Rate limiting per endpoint

## Tech Stack

- **Framework**: Spring Boot 3.5.6
- **Database**: MongoDB
- **Cache**: Redis
- **Message Queue**: Apache Kafka
- **Security**: Spring Security + JWT
- **Service Discovery**: Eureka Client
- **Email**: Spring Mail

## Prerequisites

Before running the service, ensure you have:

1. **Java 17** or higher
2. **MongoDB** running on `localhost:27017`
3. **Redis** running on `localhost:6379`
4. **Kafka** running on `localhost:9092`
5. **Eureka Server** running on `localhost:8761`

## Configuration

### Environment Variables

Set the following environment variables:

```bash
JWT_SECRET=your-super-secret-jwt-key-minimum-256-bits
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
FRONTEND_URL=http://localhost:3000
```

### Application Properties

Key configurations in `application.yml`:

```yaml
server:
  port: 8081

jwt:
  expiration: 3600000  # 1 hour
  refresh-expiration: 604800000  # 7 days

security:
  max-login-attempts: 5
  lockout-duration: 900000  # 15 minutes
```

## Database Collections

### users
- User account information
- Credentials and profile data
- Roles and permissions
- Account status

### sessions
- Active user sessions
- JWT and refresh tokens
- Device information
- IP addresses

### password_reset_tokens
- Password reset tokens
- Expiration tracking
- Usage status

## API Endpoints

### Authentication Endpoints

#### Register
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "SecurePass123",
  "displayName": "John Doe"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "emailOrUsername": "john@example.com",
  "password": "SecurePass123"
}
```

#### Refresh Token
```http
POST /api/auth/refresh-token
Content-Type: application/json

{
  "refreshToken": "your-refresh-token"
}
```

#### Logout
```http
POST /api/auth/logout
Authorization: Bearer {access-token}
Content-Type: application/json

{
  "refreshToken": "your-refresh-token"
}
```

#### Logout All Devices
```http
POST /api/auth/logout-all
Authorization: Bearer {access-token}
```

#### Forgot Password
```http
POST /api/auth/forgot-password
Content-Type: application/json

{
  "email": "john@example.com"
}
```

#### Reset Password
```http
POST /api/auth/reset-password
Content-Type: application/json

{
  "token": "reset-token",
  "newPassword": "NewSecurePass123"
}
```

### User Management Endpoints

#### Get Profile
```http
GET /api/users/profile
Authorization: Bearer {access-token}
```

#### Update Profile
```http
PUT /api/users/profile
Authorization: Bearer {access-token}
Content-Type: application/json

{
  "displayName": "John Updated",
  "avatarUrl": "https://example.com/avatar.jpg",
  "status": "online",
  "settings": {
    "theme": "dark",
    "notifications": true
  }
}
```

#### Search Users
```http
GET /api/users/search?q=john
Authorization: Bearer {access-token}
```

#### Get User Basic Info
```http
GET /api/users/{userId}/basic
Authorization: Bearer {access-token}
```

#### Change Password
```http
PUT /api/users/password
Authorization: Bearer {access-token}
Content-Type: application/json

{
  "currentPassword": "OldPass123",
  "newPassword": "NewPass123"
}
```

#### Get Active Sessions
```http
GET /api/users/sessions
Authorization: Bearer {access-token}
```

#### Delete Session
```http
DELETE /api/users/sessions/{sessionId}
Authorization: Bearer {access-token}
```

### Admin Endpoints

#### Get All Users (Paginated)
```http
GET /api/admin/users?page=0&size=10&search=john
Authorization: Bearer {admin-access-token}
```

#### Update User Roles
```http
PUT /api/admin/users/{userId}/roles
Authorization: Bearer {admin-access-token}
Content-Type: application/json

{
  "roles": ["USER", "MODERATOR"]
}
```

#### Delete User
```http
DELETE /api/admin/users/{userId}
Authorization: Bearer {admin-access-token}
```

#### Get Statistics
```http
GET /api/admin/statistics
Authorization: Bearer {admin-access-token}
```

## Kafka Topics

### Producer Topics
- `user.registered` - User registration events
- `user.logged_in` - User login events
- `user.updated` - User profile update events
- `user.deleted` - User deletion events

### Consumer Topics
- `chat.user.joined` - User joined chat (updates status to online)
- `chat.user.left` - User left chat (updates status to offline)

## Redis Key Patterns

- `user:{userId}:profile` - Cached user profile data
- `token:{jwtToken}` - Blacklisted tokens
- `rate_limit:{userId}:{endpoint}` - Rate limiting counters

## Running the Service

### Using Maven

```bash
# Build the project
mvn clean install

# Run the service
mvn spring-boot:run
```

### Using Java

```bash
# Build
mvn clean package

# Run
java -jar target/auth-service-0.0.1-SNAPSHOT.jar
```

## Testing

The service will be available at: `http://localhost:8081`

Health check: `http://localhost:8081/actuator/health`

## Error Handling

The service includes global exception handling for:
- Resource Not Found (404)
- Bad Request (400)
- Unauthorized (401)
- Validation Errors (400)
- Internal Server Errors (500)

## Security Notes

1. **Change JWT Secret**: Always use a strong, unique JWT secret in production
2. **Email Configuration**: Configure proper SMTP settings for production
3. **HTTPS**: Use HTTPS in production environments
4. **Rate Limiting**: Implement additional rate limiting at API Gateway level
5. **Password Policy**: Enforce strong password requirements

## Next Steps

After completing the auth-service, you can proceed with:
1. Chat Service
2. User Service
3. Notification Service
4. API Gateway
5. Frontend Application

## Support

For issues or questions, please refer to the main project documentation.
