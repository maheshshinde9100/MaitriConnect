# MaitriConnect - Realtime Chat Application

A microservices-based realtime chat application built with Spring Boot, Java, MongoDB, and WebSocket.

## Architecture

- **Service Discovery** (Port 8761) - Eureka Server for service registration and discovery
- **API Gateway** (Port 8080) - Routes requests to appropriate microservices
- **Auth Service** (Port 8081) - Handles user authentication and JWT token management
- **Chat Service** (Port 8082) - Manages chat messages and WebSocket connections

## Prerequisites

- Java 17+
- Maven 3.6+
- MongoDB Atlas account (connection string provided)

## Important Notes

- `@EnableEurekaClient` annotation is not needed in newer Spring Cloud versions - services auto-register when eureka-client dependency is present
- Removed unused dependencies: Redis, Kafka from chat-service; Security from api-gateway
- Services will automatically register with Eureka when they start

## Quick Start

### 1. Start Service Discovery
```bash
cd service-discovery
./mvnw spring-boot:run
```

### 2. Start Auth Service
```bash
cd auth-service
./mvnw spring-boot:run
```

### 3. Start Chat Service
```bash
cd chat-service
./mvnw spring-boot:run
```

### 4. Start API Gateway
```bash
cd api-gateway
./mvnw spring-boot:run
```

### 5. Start Chat Client
```bash
# Option 1: Use Node.js server (recommended)
node serve-client.js

# Option 2: Use batch file
start-client.bat

# Option 3: Open directly (may have CORS issues)
# Open chat-client.html in your web browser
```

## Service URLs

- **Eureka Dashboard**: http://localhost:8761
- **API Gateway**: http://localhost:8080
- **Auth Service**: http://localhost:8081
- **Chat Service**: http://localhost:8082

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login user
- `GET /api/auth/user/{userId}` - Get user details

### Chat
- `GET /api/chat/rooms/{roomId}/messages` - Get room messages
- `POST /api/chat/rooms` - Create chat room
- `GET /api/chat/users/{userId}/rooms` - Get user's chat rooms

### WebSocket
- **Endpoint**: `/ws`
- **Send Message**: `/app/chat.sendMessage`
- **Join Chat**: `/app/chat.addUser`
- **Subscribe**: `/topic/public`

## Database Configuration

The application uses MongoDB Atlas with the provided connection string. Each service uses a separate database:
- Auth Service: `authdb`
- Chat Service: `chatdb`

## Features

- User registration and authentication with JWT
- Real-time messaging using WebSocket
- Service discovery with Eureka
- API Gateway for routing
- MongoDB for data persistence
- Cross-origin resource sharing (CORS) enabled

## Testing

1. Open multiple browser tabs with `chat-client.html`
2. Register/login with different users
3. Send messages and see real-time updates across all connected clients

## Development Notes

- All services are configured to register with Eureka
- JWT tokens are used for authentication
- WebSocket connections handle real-time messaging
- MongoDB Atlas is used for cloud database storage