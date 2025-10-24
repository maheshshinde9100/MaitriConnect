# 🚀 MaitriConnect Backend - Microservices Architecture

A complete real-time chat application backend built with Spring Boot microservices, featuring authentication, messaging, notifications, and video/audio calling capabilities.

## 📋 Table of Contents

- [Architecture Overview](#architecture-overview)
- [Microservices](#microservices)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
- [API Documentation](#api-documentation)
- [Use Cases](#use-cases)
- [Configuration](#configuration)

## 🏗️ Architecture Overview

```
┌─────────────────┐
│   API Gateway   │ :8080
│   (Port 8080)   │
└────────┬────────┘
         │
    ┌────┴────────────────────────────────┐
    │                                     │
┌───▼────────┐  ┌──────────┐  ┌─────────▼──┐
│  Service   │  │  Kafka   │  │   Redis    │
│ Discovery  │  │  :9092   │  │   :6379    │
│  :8761     │  └──────────┘  └────────────┘
└────────────┘
         │
    ┌────┴──────────────────────────────────┐
    │                                        │
┌───▼────────┐ ┌──────────┐ ┌──────────┐ ┌─▼──────────┐
│   Auth     │ │   Chat   │ │  Notif   │ │   Call     │
│  Service   │ │ Service  │ │ Service  │ │  Service   │
│   :8081    │ │  :8082   │ │  :8083   │ │   :8084    │
└────────────┘ └──────────┘ └──────────┘ └────────────┘
         │                                        │
    ┌────┴────────────────────────────────────────┘
    │
┌───▼────────┐
│  MongoDB   │
│   :27017   │
└────────────┘
```

## 🔧 Microservices

### 1. **Service Discovery** (Port 8761)
- Eureka Server for service registration and discovery
- Health monitoring and load balancing
- Dashboard: http://localhost:8761

### 2. **API Gateway** (Port 8080)
- Single entry point for all client requests
- Dynamic routing and load balancing
- CORS configuration
- WebSocket support

### 3. **Auth Service** (Port 8081)
- User registration and authentication
- JWT token management (access & refresh tokens)
- Password reset flow with email
- Session management with device tracking
- Role-based access control (USER, ADMIN, MODERATOR)
- Account lockout after failed attempts
- Redis caching for user profiles

### 4. **Chat Service** (Port 8082)
- Real-time messaging via WebSocket
- Room/Channel management (Direct, Group, Channel)
- Message reactions and read receipts
- Typing indicators
- Message search and pagination
- File/Media message support
- Redis caching for recent messages

### 5. **Notification Service** (Port 8083)
- Real-time notifications via WebSocket
- Notification CRUD operations
- Unread notification tracking
- Kafka event consumers
- Automatic cleanup of old notifications

### 6. **Call Service** (Port 8084)
- WebRTC-based audio/video calling
- Call signaling (Offer/Answer/ICE)
- Call history tracking
- Call status management
- Real-time call notifications

## 💻 Tech Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| Spring Boot | 3.5.6 | Application Framework |
| Spring Cloud | 2025.0.0 | Microservices Infrastructure |
| Spring Security | 3.5.6 | Authentication & Authorization |
| MongoDB | 7.0+ | Database |
| Redis | 7.2+ | Caching & Session Storage |
| Apache Kafka | 7.5.0+ | Event Streaming |
| Eureka | 2025.0.0 | Service Discovery |
| Spring Cloud Gateway | 2025.0.0 | API Gateway |
| WebSocket | - | Real-time Communication |
| JWT | 0.12.3 | Token Management |
| Lombok | Latest | Code Generation |

## 📦 Prerequisites

Before running the services, ensure you have:

1. **Java 17** or higher
2. **Maven 3.8+**
3. **MongoDB 7.0+** running on `localhost:27017`
4. **Redis 7.2+** running on `localhost:6379`
5. **Apache Kafka 7.5.0+** running on `localhost:9092`
6. **Zookeeper** (required for Kafka)

## 🚀 Quick Start

### 1. Start Infrastructure Services

```bash
# Start MongoDB
mongod --dbpath /path/to/data

# Start Redis
redis-server

# Start Zookeeper
zookeeper-server-start.sh config/zookeeper.properties

# Start Kafka
kafka-server-start.sh config/server.properties
```

### 2. Start Microservices (in order)

```bash
# 1. Service Discovery
cd service-discovery
mvn spring-boot:run

# 2. API Gateway
cd api-gateway
mvn spring-boot:run

# 3. Auth Service
cd auth-service
mvn spring-boot:run

# 4. Chat Service
cd chat-service
mvn spring-boot:run

# 5. Notification Service
cd notification-service
mvn spring-boot:run

# 6. Call Service
cd call-service
mvn spring-boot:run
```

### 3. Verify Services

- Eureka Dashboard: http://localhost:8761
- API Gateway: http://localhost:8080
- All services should be registered in Eureka

## 📚 API Documentation

All API requests go through the API Gateway at `http://localhost:8080`

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

Response:
{
  "success": true,
  "data": {
    "user": {...},
    "accessToken": "eyJhbGc...",
    "refreshToken": "eyJhbGc..."
  }
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
  "status": "online"
}
```

#### Search Users
```http
GET /api/users/search?q=john
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

### Chat Endpoints

#### Create Room
```http
POST /api/rooms
Authorization: Bearer {access-token}
Content-Type: application/json

{
  "name": "My Group",
  "description": "Group description",
  "type": "GROUP",
  "memberIds": ["user1", "user2"]
}
```

#### Get User Rooms
```http
GET /api/rooms?page=0&size=20
Authorization: Bearer {access-token}
```

#### Send Message
```http
POST /api/messages
Authorization: Bearer {access-token}
Content-Type: application/json

{
  "roomId": "room123",
  "content": "Hello, World!",
  "type": "TEXT"
}
```

#### Get Room Messages
```http
GET /api/messages/room/{roomId}?page=0&size=50
Authorization: Bearer {access-token}
```

#### Add Reaction
```http
POST /api/messages/{messageId}/reactions
Authorization: Bearer {access-token}
Content-Type: application/json

{
  "emoji": "👍"
}
```

#### Mark Messages as Read
```http
POST /api/messages/room/{roomId}/read-all
Authorization: Bearer {access-token}
```

### Notification Endpoints

#### Get Notifications
```http
GET /api/notifications?page=0&size=20
Authorization: Bearer {access-token}
```

#### Get Unread Count
```http
GET /api/notifications/unread/count
Authorization: Bearer {access-token}
```

#### Mark as Read
```http
PUT /api/notifications/{notificationId}/read
Authorization: Bearer {access-token}
```

#### Mark All as Read
```http
PUT /api/notifications/read-all
Authorization: Bearer {access-token}
```

### Call Endpoints

#### Initiate Call
```http
POST /api/calls
Authorization: Bearer {access-token}
Content-Type: application/json

{
  "receiverId": "user456",
  "type": "VIDEO"
}
```

#### Accept Call
```http
PUT /api/calls/{callId}/accept
Authorization: Bearer {access-token}
```

#### Reject Call
```http
PUT /api/calls/{callId}/reject
Authorization: Bearer {access-token}
```

#### End Call
```http
PUT /api/calls/{callId}/end
Authorization: Bearer {access-token}
```

#### Get Call History
```http
GET /api/calls/history?page=0&size=20
Authorization: Bearer {access-token}
```

### Admin Endpoints

#### Get All Users
```http
GET /api/admin/users?page=0&size=10
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

## 🔌 WebSocket Connections

### Chat WebSocket
```javascript
const chatSocket = new SockJS('http://localhost:8080/chat-ws');
const stompClient = Stomp.over(chatSocket);

stompClient.connect({ Authorization: `Bearer ${token}` }, () => {
  // Subscribe to room messages
  stompClient.subscribe('/topic/room/{roomId}/messages', (message) => {
    console.log('New message:', JSON.parse(message.body));
  });
  
  // Subscribe to typing indicators
  stompClient.subscribe('/topic/room/{roomId}/typing', (event) => {
    console.log('Typing:', JSON.parse(event.body));
  });
  
  // Send message
  stompClient.send('/app/chat/message', {}, JSON.stringify({
    roomId: 'room123',
    content: 'Hello!',
    type: 'TEXT'
  }));
});
```

### Notification WebSocket
```javascript
const notificationSocket = new SockJS('http://localhost:8080/notification-ws');
const stompClient = Stomp.over(notificationSocket);

stompClient.connect({ Authorization: `Bearer ${token}` }, () => {
  stompClient.subscribe('/topic/user/{userId}/notifications', (notification) => {
    console.log('New notification:', JSON.parse(notification.body));
  });
});
```

### Call WebSocket
```javascript
const callSocket = new SockJS('http://localhost:8080/call-ws');
const stompClient = Stomp.over(callSocket);

stompClient.connect({ Authorization: `Bearer ${token}` }, () => {
  // Subscribe to call events
  stompClient.subscribe('/topic/user/{userId}/calls', (event) => {
    console.log('Call event:', JSON.parse(event.body));
  });
  
  // Send signaling message
  stompClient.send('/app/call/signal', {}, JSON.stringify({
    callId: 'call123',
    type: 'OFFER',
    senderId: 'user123',
    receiverId: 'user456',
    data: sdpOffer
  }));
});
```

## 🎯 Use Cases

### 1. User Registration and Login
```
1. User registers → POST /api/auth/register
2. System sends verification email
3. User logs in → POST /api/auth/login
4. System returns JWT tokens
5. User authenticated for subsequent requests
```

### 2. One-on-One Chat
```
1. User A creates direct room → POST /api/rooms (type: DIRECT)
2. User A sends message → POST /api/messages
3. User B receives message via WebSocket
4. User B reads message → POST /api/messages/{messageId}/read
5. User A gets read receipt via WebSocket
```

### 3. Group Chat
```
1. User creates group → POST /api/rooms (type: GROUP)
2. Add members → POST /api/rooms/{roomId}/members/{userId}
3. Members send messages → POST /api/messages
4. All members receive via WebSocket
5. Members can react → POST /api/messages/{messageId}/reactions
```

### 4. Video/Audio Call
```
1. User A initiates call → POST /api/calls
2. User B receives notification via WebSocket
3. User B accepts → PUT /api/calls/{callId}/accept
4. WebRTC signaling via WebSocket
5. Call established
6. Either user ends → PUT /api/calls/{callId}/end
```

### 5. Notifications
```
1. Event occurs (new message, call, etc.)
2. Kafka event published
3. Notification service consumes event
4. Notification created and stored
5. User receives real-time notification via WebSocket
6. User marks as read → PUT /api/notifications/{id}/read
```

### 6. Admin Operations
```
1. Admin logs in with admin credentials
2. View all users → GET /api/admin/users
3. Update user roles → PUT /api/admin/users/{userId}/roles
4. View statistics → GET /api/admin/statistics
5. Delete user if needed → DELETE /api/admin/users/{userId}
```

## ⚙️ Configuration

### Environment Variables

Create `.env` files in each service directory:

```bash
# Common for all services
JWT_SECRET=your-super-secret-jwt-key-minimum-256-bits
MONGODB_URI=mongodb://localhost:27017/maitriconnect
REDIS_HOST=localhost
REDIS_PORT=6379
KAFKA_BOOTSTRAP_SERVERS=localhost:9092
EUREKA_SERVER_URL=http://localhost:8761/eureka/

# Auth Service specific
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
FRONTEND_URL=http://localhost:3000
```

### Database Collections

**Auth Service:**
- `users` - User accounts and profiles
- `sessions` - Active user sessions
- `password_reset_tokens` - Password reset tokens

**Chat Service:**
- `messages` - Chat messages
- `rooms` - Chat rooms/channels

**Notification Service:**
- `notifications` - User notifications

**Call Service:**
- `calls` - Call history and metadata

### Kafka Topics

**Producers:**
- `user.registered` - User registration events
- `user.logged_in` - User login events
- `user.updated` - User profile updates
- `user.deleted` - User deletion events
- `chat.events` - Chat-related events

**Consumers:**
- All services consume relevant topics for cross-service communication

## 📊 Service Ports

| Service | Port | Purpose |
|---------|------|---------|
| API Gateway | 8080 | Main entry point |
| Auth Service | 8081 | Authentication |
| Chat Service | 8082 | Messaging |
| Notification Service | 8083 | Notifications |
| Call Service | 8084 | Video/Audio calls |
| Service Discovery | 8761 | Eureka Server |
| MongoDB | 27017 | Database |
| Redis | 6379 | Cache |
| Kafka | 9092 | Message Broker |

## 🔒 Security

- **JWT Authentication**: All protected endpoints require valid JWT token
- **Password Hashing**: BCrypt with salt
- **Token Expiration**: Access token (1 hour), Refresh token (7 days)
- **Account Lockout**: After 5 failed login attempts (15 minutes)
- **CORS**: Configured in API Gateway
- **Rate Limiting**: Can be added at API Gateway level

## 📈 Monitoring

- **Eureka Dashboard**: http://localhost:8761
- **Actuator Endpoints**: `/actuator/health`, `/actuator/metrics`
- **Logs**: Each service logs to console with configurable levels

## 🧪 Testing

```bash
# Test service health
curl http://localhost:8080/actuator/health

# Test authentication
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"emailOrUsername":"test@test.com","password":"Test1234"}'

# Test with JWT
curl http://localhost:8080/api/users/profile \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open Pull Request

## 📝 License

This project is licensed under the MIT License.

## 👥 Support

For issues or questions:
1. Check service-specific README files
2. Review logs in each service
3. Verify all dependencies are running
4. Check Eureka dashboard for service registration

---

**Built with ❤️ using Spring Boot Microservices Architecture**
