# 🌐 API Gateway - MaitriConnect

Spring Cloud Gateway for routing and load balancing across all microservices.

## Features

- ✅ Dynamic routing via Eureka
- ✅ Load balancing
- ✅ CORS configuration
- ✅ WebSocket support
- ✅ Path rewriting
- ✅ Service discovery integration

## Configuration

- **Port**: 8080
- **Eureka**: http://localhost:8761

## Routes

### Auth Service
- `/api/auth/**` → auth-service:8081
- `/api/users/**` → auth-service:8081
- `/api/admin/**` → auth-service:8081

### Chat Service
- `/api/messages/**` → chat-service:8082
- `/api/rooms/**` → chat-service:8082
- `/chat-ws/**` → chat-service:8082/ws (WebSocket)

### Notification Service
- `/api/notifications/**` → notification-service:8083
- `/notification-ws/**` → notification-service:8083/ws (WebSocket)

### Call Service
- `/api/calls/**` → call-service:8084
- `/call-ws/**` → call-service:8084/ws (WebSocket)

## Usage

All client requests should go through the API Gateway:

```
http://localhost:8080/api/auth/login
http://localhost:8080/api/messages
http://localhost:8080/api/notifications
http://localhost:8080/api/calls
```

## WebSocket Connections

```javascript
// Chat WebSocket
const chatSocket = new SockJS('http://localhost:8080/chat-ws');

// Notification WebSocket
const notificationSocket = new SockJS('http://localhost:8080/notification-ws');

// Call WebSocket
const callSocket = new SockJS('http://localhost:8080/call-ws');
```

## Running

```bash
mvn spring-boot:run
```

Gateway runs on port **8080**
