# 🔔 Notification Service - MaitriConnect

Real-time notification microservice for the MaitriConnect application with WebSocket support and Kafka event processing.

## Features

- ✅ Real-time notifications via WebSocket
- ✅ Notification CRUD operations
- ✅ Unread notification tracking
- ✅ Mark as read functionality
- ✅ Kafka event consumers (chat, user events)
- ✅ MongoDB persistence
- ✅ Service Discovery (Eureka)
- ✅ Automatic cleanup of old notifications

## Tech Stack

- **Framework**: Spring Boot 3.5.6
- **Database**: MongoDB
- **Message Queue**: Apache Kafka
- **WebSocket**: Spring WebSocket + STOMP
- **Service Discovery**: Eureka Client

## API Endpoints

### Create Notification
```http
POST /api/notifications
Content-Type: application/json

{
  "userId": "user123",
  "title": "New Message",
  "message": "You have a new message",
  "type": "MESSAGE",
  "priority": "HIGH"
}
```

### Get User Notifications
```http
GET /api/notifications?page=0&size=20
```

### Get Unread Notifications
```http
GET /api/notifications/unread?page=0&size=20
```

### Get Unread Count
```http
GET /api/notifications/unread/count
```

### Mark as Read
```http
PUT /api/notifications/{notificationId}/read
```

### Mark All as Read
```http
PUT /api/notifications/read-all
```

### Delete Notification
```http
DELETE /api/notifications/{notificationId}
```

### Delete All Notifications
```http
DELETE /api/notifications
```

## WebSocket

### Subscribe to Notifications
```javascript
stompClient.subscribe('/topic/user/{userId}/notifications', (notification) => {
  const data = JSON.parse(notification.body);
  console.log('New notification:', data);
});
```

## Kafka Topics Consumed

- `chat.events` - Chat-related events
- `user.registered` - New user registrations
- `user.logged_in` - User login events

## Running

```bash
mvn spring-boot:run
```

Service runs on port **8083**
