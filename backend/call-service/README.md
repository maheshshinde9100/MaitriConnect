# 📞 Call Service - MaitriConnect

WebRTC-based audio/video calling microservice with signaling support.

## Features

- ✅ Audio and Video calls
- ✅ WebRTC signaling (Offer/Answer/ICE)
- ✅ Call history tracking
- ✅ Call status management
- ✅ Real-time notifications via WebSocket
- ✅ MongoDB persistence
- ✅ Service Discovery (Eureka)

## Tech Stack

- **Framework**: Spring Boot 3.5.6
- **Database**: MongoDB
- **WebSocket**: Spring WebSocket + STOMP
- **Protocol**: WebRTC
- **Service Discovery**: Eureka Client

## API Endpoints

### Initiate Call
```http
POST /api/calls
Content-Type: application/json

{
  "receiverId": "user456",
  "type": "VIDEO"
}
```

### Accept Call
```http
PUT /api/calls/{callId}/accept
```

### Reject Call
```http
PUT /api/calls/{callId}/reject?reason=BUSY
```

### End Call
```http
PUT /api/calls/{callId}/end
```

### Get Call History
```http
GET /api/calls/history?page=0&size=20
```

### Get Active Calls
```http
GET /api/calls/active
```

## WebSocket Signaling

### Connect
```javascript
const socket = new SockJS('http://localhost:8084/ws');
const stompClient = Stomp.over(socket);
```

### Subscribe to Call Events
```javascript
stompClient.subscribe('/topic/user/{userId}/calls', (message) => {
  const data = JSON.parse(message.body);
  console.log('Call event:', data);
});
```

### Send Signaling Message
```javascript
stompClient.send('/app/call/signal', {}, JSON.stringify({
  callId: 'call123',
  type: 'OFFER',
  senderId: 'user123',
  receiverId: 'user456',
  data: sdpOffer
}));
```

## Running

```bash
mvn spring-boot:run
```

Service runs on port **8084**
