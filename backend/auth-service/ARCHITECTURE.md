# 🏗️ Auth Service Architecture

## System Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                         CLIENT LAYER                             │
│  (Web Browser, Mobile App, Postman, Other Microservices)        │
└────────────────────────┬────────────────────────────────────────┘
                         │ HTTP/HTTPS
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│                    API GATEWAY (Future)                          │
│              (Load Balancing, Rate Limiting)                     │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│                      AUTH SERVICE (Port 8081)                    │
│                                                                   │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │                   CONTROLLER LAYER                        │  │
│  │  ┌────────────┐  ┌────────────┐  ┌────────────┐         │  │
│  │  │   Auth     │  │   User     │  │   Admin    │         │  │
│  │  │ Controller │  │ Controller │  │ Controller │         │  │
│  │  └─────┬──────┘  └─────┬──────┘  └─────┬──────┘         │  │
│  └────────┼───────────────┼───────────────┼────────────────┘  │
│           │               │               │                     │
│  ┌────────▼───────────────▼───────────────▼────────────────┐  │
│  │                   SERVICE LAYER                           │  │
│  │  ┌────────────┐  ┌────────────┐  ┌────────────┐         │  │
│  │  │   Auth     │  │   User     │  │   Admin    │         │  │
│  │  │  Service   │  │  Service   │  │  Service   │         │  │
│  │  └─────┬──────┘  └─────┬──────┘  └─────┬──────┘         │  │
│  │        │               │               │                  │  │
│  │  ┌─────▼───────────────▼───────────────▼──────┐          │  │
│  │  │         Email Service, Redis Service        │          │  │
│  │  └─────────────────────┬─────────────────────┘          │  │
│  └────────────────────────┼──────────────────────────────────┘  │
│                            │                                     │
│  ┌────────────────────────▼──────────────────────────────────┐  │
│  │                  REPOSITORY LAYER                          │  │
│  │  ┌────────────┐  ┌────────────┐  ┌──────────────────┐    │  │
│  │  │   User     │  │  Session   │  │  PasswordReset   │    │  │
│  │  │ Repository │  │ Repository │  │    Repository    │    │  │
│  │  └─────┬──────┘  └─────┬──────┘  └────────┬─────────┘    │  │
│  └────────┼───────────────┼──────────────────┼──────────────┘  │
│           │               │                  │                  │
│  ┌────────▼───────────────▼──────────────────▼──────────────┐  │
│  │                  SECURITY LAYER                            │  │
│  │  ┌──────────────────┐  ┌─────────────────────────────┐   │  │
│  │  │  JWT Filter      │  │  Security Configuration     │   │  │
│  │  │  (Authentication)│  │  (Authorization)            │   │  │
│  │  └──────────────────┘  └─────────────────────────────┘   │  │
│  └────────────────────────────────────────────────────────────┘  │
│                                                                   │
│  ┌────────────────────────────────────────────────────────────┐  │
│  │                    KAFKA LAYER                              │  │
│  │  ┌────────────────┐              ┌────────────────┐        │  │
│  │  │    Producer    │              │    Consumer    │        │  │
│  │  │  (Publish      │              │  (Subscribe    │        │  │
│  │  │   Events)      │              │   Events)      │        │  │
│  │  └────────────────┘              └────────────────┘        │  │
│  └────────────────────────────────────────────────────────────┘  │
└───────────────────────┬───────────────────┬───────────────────────┘
                        │                   │
        ┌───────────────┼───────────────────┼───────────────┐
        │               │                   │               │
        ▼               ▼                   ▼               ▼
┌──────────────┐ ┌──────────┐ ┌──────────────┐ ┌──────────────┐
│   MongoDB    │ │  Redis   │ │    Kafka     │ │   Eureka     │
│  (Database)  │ │ (Cache)  │ │  (Message    │ │  (Service    │
│              │ │          │ │   Queue)     │ │  Discovery)  │
│ Port: 27017  │ │Port: 6379│ │ Port: 9092   │ │ Port: 8761   │
└──────────────┘ └──────────┘ └──────────────┘ └──────────────┘
```

## Component Interactions

### 1. Authentication Flow

```
Client → AuthController → AuthService → UserRepository → MongoDB
                              ↓
                         JwtUtil (Generate Tokens)
                              ↓
                         SessionRepository → MongoDB
                              ↓
                         KafkaProducer → Kafka (user.logged_in)
                              ↓
                         EmailService → SMTP Server
                              ↓
                         Response → Client
```

### 2. Authorization Flow

```
Client Request → JwtAuthenticationFilter
                      ↓
                 Extract JWT Token
                      ↓
                 Validate Token (JwtUtil)
                      ↓
                 Check Redis (Blacklist)
                      ↓
                 Load User Details (CustomUserDetailsService)
                      ↓
                 Set Security Context
                      ↓
                 Controller → Service → Repository
```

### 3. Caching Flow

```
Get User Profile Request
         ↓
    Check Redis Cache
         ↓
    ┌────┴────┐
    │         │
  Found    Not Found
    │         │
    │         ▼
    │    Query MongoDB
    │         │
    │         ▼
    │    Cache in Redis (1 hour TTL)
    │         │
    └────┬────┘
         ▼
    Return to Client
```

### 4. Event Publishing Flow

```
User Action (Register/Login/Update/Delete)
              ↓
         Service Layer
              ↓
         Create UserEvent
              ↓
         KafkaProducer
              ↓
         Kafka Topic
              ↓
    Other Microservices (Consumers)
```

## Data Flow Patterns

### Registration Flow
```
1. Client sends registration request
2. Controller validates request
3. Service checks for existing user
4. Service hashes password (BCrypt)
5. Repository saves user to MongoDB
6. Service generates JWT tokens
7. Service creates session
8. Service publishes Kafka event
9. Service sends verification email
10. Response sent to client
```

### Login Flow
```
1. Client sends login credentials
2. Controller validates request
3. Service finds user by email/username
4. Service checks account lock status
5. AuthenticationManager authenticates
6. Service generates new tokens
7. Service creates new session
8. Service updates user status to "online"
9. Service publishes Kafka event
10. Response with tokens sent to client
```

### Token Refresh Flow
```
1. Client sends refresh token
2. Controller validates request
3. Service validates refresh token
4. Service finds session by refresh token
5. Service generates new tokens
6. Service updates session
7. Response with new tokens sent to client
```

## Security Layers

### Layer 1: Network Security
- HTTPS (in production)
- CORS Configuration
- Rate Limiting (Redis-based)

### Layer 2: Authentication
- JWT Token Validation
- Token Expiration Check
- Token Blacklist Check (Redis)
- Session Validation

### Layer 3: Authorization
- Role-Based Access Control
- Method-Level Security (@PreAuthorize)
- Resource Ownership Validation

### Layer 4: Data Security
- Password Hashing (BCrypt)
- Sensitive Data Encryption
- SQL Injection Prevention (MongoDB)
- XSS Prevention

## Database Schema Design

### Users Collection
```
users {
  _id: ObjectId (Primary Key)
  username: String (Unique Index)
  email: String (Unique Index)
  password: String (Hashed)
  displayName: String
  avatarUrl: String
  status: String
  roles: Array<String>
  settings: Object
  createdAt: DateTime
  lastSeen: DateTime
  emailVerified: Boolean
  accountLocked: Boolean
  failedLoginAttempts: Number
  lockoutEndTime: DateTime
}
```

### Sessions Collection
```
sessions {
  _id: ObjectId (Primary Key)
  userId: String (Index)
  deviceInfo: Object
  ipAddress: String
  jwtToken: String (Unique Index)
  refreshToken: String (Unique Index)
  createdAt: DateTime
  expiresAt: DateTime
  isActive: Boolean
  lastAccessedAt: DateTime
}
```

### Password Reset Tokens Collection
```
password_reset_tokens {
  _id: ObjectId (Primary Key)
  userId: String (Index)
  token: String (Unique Index)
  expiresAt: DateTime
  used: Boolean
  createdAt: DateTime
}
```

## Redis Key Patterns

```
user:{userId}:profile          → User profile cache (1 hour TTL)
token:{jwtToken}               → Blacklisted tokens (TTL = token expiry)
rate_limit:{userId}:{endpoint} → Rate limiting counters (1 minute TTL)
session:{userId}:count         → Active session count
```

## Kafka Topics Structure

### Producer Topics
```
user.registered
├── Key: userId
└── Value: UserEvent {
    userId, username, email, displayName,
    avatarUrl, status, roles, timestamp, eventType
}

user.logged_in
├── Key: userId
└── Value: UserEvent {...}

user.updated
├── Key: userId
└── Value: UserEvent {...}

user.deleted
├── Key: userId
└── Value: UserEvent {...}
```

### Consumer Topics
```
chat.user.joined
├── Key: userId
└── Value: ChatUserEvent {
    userId, status, timestamp, eventType
}

chat.user.left
├── Key: userId
└── Value: ChatUserEvent {...}
```

## API Endpoint Organization

```
/api
├── /auth (Public + Authenticated)
│   ├── POST /register
│   ├── POST /login
│   ├── POST /refresh-token
│   ├── POST /logout (Authenticated)
│   ├── POST /logout-all (Authenticated)
│   ├── POST /forgot-password
│   ├── POST /reset-password
│   └── POST /verify-email
│
├── /users (Authenticated)
│   ├── GET /profile
│   ├── PUT /profile
│   ├── GET /search
│   ├── GET /{userId}/basic
│   ├── PUT /password
│   ├── GET /sessions
│   └── DELETE /sessions/{sessionId}
│
└── /admin (Admin Role Required)
    ├── GET /users
    ├── PUT /users/{userId}/roles
    ├── DELETE /users/{userId}
    └── GET /statistics
```

## Error Handling Architecture

```
Exception Occurs
      ↓
Global Exception Handler (@RestControllerAdvice)
      ↓
Match Exception Type
      ↓
┌─────┴─────┬─────────┬──────────┬─────────┐
│           │         │          │         │
ResourceNot BadRequest Unauthorized Validation Server
Found       Exception  Exception   Error    Error
│           │         │          │         │
404         400       401        400       500
│           │         │          │         │
└───────────┴─────────┴──────────┴─────────┘
                      ↓
            Formatted Error Response
                      ↓
                   Client
```

## Scalability Considerations

### Horizontal Scaling
- Stateless service design
- JWT tokens (no server-side session)
- Redis for shared cache
- Kafka for async communication
- Load balancer ready

### Performance Optimization
- Database indexing
- Redis caching
- Connection pooling
- Async event publishing
- Pagination for large datasets

### High Availability
- Multiple service instances
- Database replication
- Redis clustering
- Kafka partitioning
- Circuit breakers (future)

## Monitoring & Observability

```
Application Logs → Console/File
      ↓
Spring Actuator Endpoints
      ↓
┌─────┴─────┬─────────┬──────────┐
│           │         │          │
Health    Metrics   Info      Custom
Check     Endpoint  Endpoint  Endpoints
│           │         │          │
└───────────┴─────────┴──────────┘
            ↓
    Monitoring Tools (Future)
    (Prometheus, Grafana, ELK)
```

## Deployment Architecture (Future)

```
┌─────────────────────────────────────────┐
│         Load Balancer / API Gateway      │
└────────────┬────────────────────────────┘
             │
    ┌────────┼────────┐
    │        │        │
    ▼        ▼        ▼
┌────────┐ ┌────────┐ ┌────────┐
│ Auth   │ │ Auth   │ │ Auth   │
│Service │ │Service │ │Service │
│Instance│ │Instance│ │Instance│
│   1    │ │   2    │ │   3    │
└────────┘ └────────┘ └────────┘
    │        │        │
    └────────┼────────┘
             │
    ┌────────┼────────┬────────┐
    │        │        │        │
    ▼        ▼        ▼        ▼
┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐
│MongoDB │ │ Redis  │ │ Kafka  │ │Eureka  │
│Cluster │ │Cluster │ │Cluster │ │Server  │
└────────┘ └────────┘ └────────┘ └────────┘
```

## Technology Stack Summary

| Layer | Technology | Purpose |
|-------|-----------|---------|
| Framework | Spring Boot 3.5.6 | Application foundation |
| Security | Spring Security | Authentication & Authorization |
| Database | MongoDB | Data persistence |
| Cache | Redis | Performance optimization |
| Messaging | Apache Kafka | Event-driven architecture |
| Service Discovery | Eureka | Microservices communication |
| Token | JWT (jjwt) | Stateless authentication |
| Email | Spring Mail | Notifications |
| Build | Maven | Dependency management |
| Container | Docker | Deployment |

---

This architecture is designed for:
- ✅ **Scalability** - Can handle increasing load
- ✅ **Reliability** - Fault-tolerant design
- ✅ **Security** - Multiple security layers
- ✅ **Performance** - Optimized with caching
- ✅ **Maintainability** - Clean code structure
- ✅ **Extensibility** - Easy to add features
