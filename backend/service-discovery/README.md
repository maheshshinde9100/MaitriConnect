# 🔍 Service Discovery - MaitriConnect

Eureka Server for service registration and discovery in the MaitriConnect microservices architecture.

## Features

- ✅ Service registration
- ✅ Service discovery
- ✅ Health monitoring
- ✅ Load balancing support
- ✅ Dashboard UI

## Configuration

- **Port**: 8761
- **Dashboard**: http://localhost:8761

## Registered Services

- **auth-service** (Port 8081)
- **chat-service** (Port 8082)
- **notification-service** (Port 8083)
- **call-service** (Port 8084)
- **api-gateway** (Port 8080)

## Running

```bash
mvn spring-boot:run
```

Access Eureka Dashboard at: http://localhost:8761
