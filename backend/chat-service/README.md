# 💬 Chat Service - MaitriConnect

Real-time chat microservice for the MaitriConnect application with WebSocket support, message management, and room functionality.

## Features

### Core Functionality
- ✅ Real-time messaging via WebSocket
- ✅ Room/Channel Management (Direct, Group, Channel)
- ✅ Message CRUD operations
- ✅ Message reactions and read receipts
- ✅ Typing indicators
- ✅ Message search
- ✅ Unread message tracking
- ✅ File/Media message support
- ✅ Message editing and deletion
- ✅ Reply to messages

### Technical Features
- ✅ JWT Authentication
- ✅ Redis Caching for recent messages
- ✅ MongoDB for message persistence
- ✅ Kafka event publishing
- ✅ WebSocket with STOMP protocol
- ✅ Pagination support
- ✅ Service Discovery (Eureka)

## Tech Stack

- **Framework**: Spring Boot 3.5.6
- **Database**: MongoDB
- **Cache**: Redis
- **Message Queue**: Apache Kafka
- **WebSocket**: Spring WebSocket + STOMP
- **Security**: Spring Security + JWT
- **Service Discovery**: Eureka Client

## Prerequisites

1. **Java 17** or higher
2. **MongoDB** running on `localhost:27017`
3. **Redis** running on `localhost:6379`
4. **Kafka** running on `localhost:9092`
5. **Eureka Server** running on `localhost:8761`

## Configuration

### Environment Variables

```bash
JWT_SECRET=your-super-secret-jwt-key-minimum-256-bits
MONGODB_URI=mongodb://localhost:27017/maitriconnect_chat
REDIS_HOST=localhost
REDIS_PORT=6379
KAFKA_BOOTSTRAP_SERVERS=localhost:9092
EUREKA_SERVER_URL=http://localhost:8761/eureka/
```

### Application Properties

Key configurations in `application.yml`:

```yaml
server:
  port: 8082

chat:
  pagination:
    default-page-size: 50
    max-page-size: 100
```

## Database Collections

### messages
- Message content and metadata
- Reactions and read receipts
- Media attachments
- Reply references

### rooms
- Room/Channel information
- Member management
- Room settings
- Direct message mappings

## API Endpoints

### Message Endpoints

#### Send Message
```http
POST /api/messages
Authorization: Bearer {access-token}
Content-Type: application/json

{
  "roomId": "room123",
  "content": "Hello, World!",
  "type": "TEXT",
  "replyToMessageId": "msg456"
}
```

#### Get Room Messages
```http
GET /api/messages/room/{roomId}?page=0&size=50
Authorization: Bearer {access-token}
```

#### Search Messages
```http
GET /api/messages/room/{roomId}/search?query=hello&page=0&size=50
Authorization: Bearer {access-token}
```

#### Update Message
```http
PUT /api/messages/{messageId}?content=Updated content
Authorization: Bearer {access-token}
```

#### Delete Message
```http
DELETE /api/messages/{messageId}
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

#### Remove Reaction
```http
DELETE /api/messages/{messageId}/reactions/{emoji}
Authorization: Bearer {access-token}
```

#### Mark Message as Read
```http
POST /api/messages/{messageId}/read
Authorization: Bearer {access-token}
```

#### Mark All Room Messages as Read
```http
POST /api/messages/room/{roomId}/read-all
Authorization: Bearer {access-token}
```

#### Get Unread Count
```http
GET /api/messages/room/{roomId}/unread-count
Authorization: Bearer {access-token}
```

### Room Endpoints

#### Create Room
```http
POST /api/rooms
Authorization: Bearer {access-token}
Content-Type: application/json

{
  "name": "My Group",
  "description": "Group description",
  "type": "GROUP",
  "memberIds": ["user1", "user2"],
  "avatarUrl": "https://example.com/avatar.jpg"
}
```

#### Get Room by ID
```http
GET /api/rooms/{roomId}
Authorization: Bearer {access-token}
```

#### Get User Rooms
```http
GET /api/rooms?page=0&size=20
Authorization: Bearer {access-token}
```

#### Search Rooms
```http
GET /api/rooms/search?query=group&page=0&size=20
Authorization: Bearer {access-token}
```

#### Update Room
```http
PUT /api/rooms/{roomId}
Authorization: Bearer {access-token}
Content-Type: application/json

{
  "name": "Updated Name",
  "description": "Updated description"
}
```

#### Delete Room
```http
DELETE /api/rooms/{roomId}
Authorization: Bearer {access-token}
```

#### Add Member
```http
POST /api/rooms/{roomId}/members/{userId}
Authorization: Bearer {access-token}
```

#### Remove Member
```http
DELETE /api/rooms/{roomId}/members/{userId}
Authorization: Bearer {access-token}
```

#### Leave Room
```http
POST /api/rooms/{roomId}/leave
Authorization: Bearer {access-token}
```

## WebSocket Endpoints

### Connection
```javascript
const socket = new SockJS('http://localhost:8082/ws');
const stompClient = Stomp.over(socket);

stompClient.connect(
  { Authorization: `Bearer ${accessToken}` },
  (frame) => {
    console.log('Connected:', frame);
  }
);
```

### Subscribe to Room Messages
```javascript
stompClient.subscribe('/topic/room/{roomId}/messages', (message) => {
  const messageData = JSON.parse(message.body);
  console.log('New message:', messageData);
});
```

### Subscribe to Typing Indicators
```javascript
stompClient.subscribe('/topic/room/{roomId}/typing', (event) => {
  const typingData = JSON.parse(event.body);
  console.log('Typing:', typingData);
});
```

### Subscribe to Read Receipts
```javascript
stompClient.subscribe('/topic/room/{roomId}/read-receipts', (event) => {
  const receiptData = JSON.parse(event.body);
  console.log('Read receipt:', receiptData);
});
```

### Send Message via WebSocket
```javascript
stompClient.send('/app/chat/message', {}, JSON.stringify({
  roomId: 'room123',
  content: 'Hello!',
  type: 'TEXT'
}));
```

### Send Typing Indicator
```javascript
stompClient.send('/app/chat/typing', {}, JSON.stringify({
  roomId: 'room123',
  isTyping: true
}));
```

## Kafka Topics

### Producer Topics
- `chat.events` - All chat-related events
  - MESSAGE_SENT
  - MESSAGE_EDITED
  - MESSAGE_DELETED
  - ROOM_CREATED
  - MEMBER_JOINED
  - MEMBER_LEFT

### Consumer Topics
- `user.registered` - New user registration
- `user.logged_in` - User login events
- `user.updated` - User profile updates
- `user.deleted` - User deletion events

## Redis Key Patterns

- `chat:room:{roomId}:messages` - Cached recent messages (50 messages, 1 hour TTL)

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
java -jar target/chat-service-0.0.1-SNAPSHOT.jar
```

## Testing

The service will be available at: `http://localhost:8082`

Health check: `http://localhost:8082/actuator/health`

WebSocket endpoint: `ws://localhost:8082/ws`

## Message Types

- **TEXT** - Plain text message
- **IMAGE** - Image attachment
- **FILE** - File attachment
- **VIDEO** - Video attachment
- **AUDIO** - Audio/Voice message
- **SYSTEM** - System-generated message

## Room Types

- **DIRECT** - One-on-one chat
- **GROUP** - Group chat with multiple members
- **CHANNEL** - Public channel

## Error Handling

The service includes global exception handling for:
- Resource Not Found (404)
- Bad Request (400)
- Unauthorized (401)
- Validation Errors (400)
- Internal Server Errors (500)

## Security Notes

1. **JWT Authentication**: All REST and WebSocket endpoints require valid JWT token
2. **Authorization**: Users can only access rooms they are members of
3. **Message Ownership**: Users can only edit/delete their own messages
4. **Admin Permissions**: Room admins have additional privileges

## Performance Optimizations

1. **Redis Caching**: Recent messages cached for faster retrieval
2. **Database Indexes**: Optimized queries with compound indexes
3. **Pagination**: Prevents large data transfers
4. **WebSocket**: Real-time updates without polling

## Next Steps

After completing the chat-service, you can proceed with:
1. Notification Service
2. Call Service
3. API Gateway configuration
4. Frontend Integration

## Support

For issues or questions, please refer to the main project documentation.
