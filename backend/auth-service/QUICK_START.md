# 🚀 Quick Start Guide - Auth Service

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Docker & Docker Compose (for dependencies)

## Step 1: Start Dependencies

Start MongoDB, Redis, and Kafka using Docker Compose:

```bash
docker-compose up -d
```

Verify all services are running:

```bash
docker-compose ps
```

You should see:
- ✅ maitriconnect-mongodb (port 27017)
- ✅ maitriconnect-redis (port 6379)
- ✅ maitriconnect-kafka (port 9092)
- ✅ maitriconnect-zookeeper (port 2181)

## Step 2: Configure Environment Variables

Create a `.env` file or set environment variables:

```bash
# Windows PowerShell
$env:JWT_SECRET="maitriconnect-super-secret-key-change-this-in-production-minimum-256-bits"
$env:MAIL_USERNAME="your-email@gmail.com"
$env:MAIL_PASSWORD="your-app-password"
$env:FRONTEND_URL="http://localhost:3000"

# Linux/Mac
export JWT_SECRET="maitriconnect-super-secret-key-change-this-in-production-minimum-256-bits"
export MAIL_USERNAME="your-email@gmail.com"
export MAIL_PASSWORD="your-app-password"
export FRONTEND_URL="http://localhost:3000"
```

## Step 3: Build the Project

```bash
mvn clean install
```

## Step 4: Run the Service

```bash
mvn spring-boot:run
```

Or run the JAR:

```bash
java -jar target/auth-service-0.0.1-SNAPSHOT.jar
```

## Step 5: Verify Service is Running

Check health endpoint:

```bash
curl http://localhost:8081/actuator/health
```

Expected response:
```json
{
  "status": "UP"
}
```

## Step 6: Test the API

### Register a User

```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "TestPass123",
    "displayName": "Test User"
  }'
```

### Login

```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "emailOrUsername": "test@example.com",
    "password": "TestPass123"
  }'
```

Save the `accessToken` from the response!

### Get Profile

```bash
curl -X GET http://localhost:8081/api/users/profile \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

## Step 7: Create Admin User (Optional)

Connect to MongoDB:

```bash
docker exec -it maitriconnect-mongodb mongosh
```

Switch to database and update user role:

```javascript
use maitriconnect_auth

// Find your user
db.users.find({ username: "testuser" })

// Update to admin
db.users.updateOne(
  { username: "testuser" },
  { $set: { roles: ["USER", "ADMIN"] } }
)

// Verify
db.users.findOne({ username: "testuser" })
```

## Troubleshooting

### Port Already in Use

If port 8081 is already in use, change it in `application.yml`:

```yaml
server:
  port: 8082
```

### MongoDB Connection Error

Verify MongoDB is running:

```bash
docker logs maitriconnect-mongodb
```

### Redis Connection Error

Verify Redis is running:

```bash
docker logs maitriconnect-redis
```

### Kafka Connection Error

Verify Kafka is running:

```bash
docker logs maitriconnect-kafka
```

### Email Sending Issues

For Gmail, you need to:
1. Enable 2-Factor Authentication
2. Generate an App Password
3. Use the App Password in `MAIL_PASSWORD`

Or disable email temporarily by commenting out email service calls.

## Stop Services

Stop the auth service: `Ctrl+C`

Stop Docker containers:

```bash
docker-compose down
```

To remove volumes (delete data):

```bash
docker-compose down -v
```

## Next Steps

1. ✅ Test all API endpoints (see `API_TESTING.md`)
2. ✅ Set up Eureka Server (for service discovery)
3. ✅ Build other microservices
4. ✅ Set up API Gateway
5. ✅ Build frontend application

## Useful Commands

### View MongoDB Data

```bash
docker exec -it maitriconnect-mongodb mongosh
use maitriconnect_auth
db.users.find().pretty()
db.sessions.find().pretty()
```

### View Redis Data

```bash
docker exec -it maitriconnect-redis redis-cli
KEYS *
GET user:USER_ID:profile
```

### View Kafka Topics

```bash
docker exec -it maitriconnect-kafka kafka-topics --list --bootstrap-server localhost:9092
```

### View Kafka Messages

```bash
docker exec -it maitriconnect-kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic user.registered \
  --from-beginning
```

## Production Checklist

Before deploying to production:

- [ ] Change JWT_SECRET to a strong random value
- [ ] Configure proper email SMTP settings
- [ ] Set up SSL/TLS certificates
- [ ] Configure proper MongoDB authentication
- [ ] Set up Redis password
- [ ] Configure Kafka security
- [ ] Enable rate limiting at API Gateway
- [ ] Set up monitoring and logging
- [ ] Configure backup strategies
- [ ] Review and update security settings
- [ ] Set up CI/CD pipeline

## Support

For detailed API documentation, see `API_TESTING.md`

For complete feature list, see `README.md`
