# 🚀 Quick Start Guide - MaitriConnect Backend

Get the MaitriConnect backend up and running in minutes!

## 📋 Prerequisites Checklist

- [ ] Java 17 or higher installed
- [ ] Maven 3.8+ installed
- [ ] Docker and Docker Compose installed (recommended)
- [ ] Git installed

## ⚡ Quick Setup (Using Docker)

### Step 1: Start Infrastructure Services

```bash
# Navigate to backend directory
cd backend

# Start MongoDB, Redis, Kafka, and Zookeeper
docker-compose up -d

# Verify services are running
docker-compose ps
```

### Step 2: Configure Environment Variables

Create `.env` file in the root directory:

```bash
# Copy example environment file
cp .env.example .env

# Edit .env with your values
JWT_SECRET=your-super-secret-jwt-key-minimum-256-bits-for-hs256-algorithm
MONGODB_URI=mongodb://admin:admin123@localhost:27017/maitriconnect?authSource=admin
REDIS_HOST=localhost
REDIS_PORT=6379
KAFKA_BOOTSTRAP_SERVERS=localhost:9092
EUREKA_SERVER_URL=http://localhost:8761/eureka/
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
FRONTEND_URL=http://localhost:3000
```

### Step 3: Start Microservices

Open 6 terminal windows and run each service:

**Terminal 1 - Service Discovery:**
```bash
cd service-discovery
mvn spring-boot:run
```
Wait for: "Started ServiceDiscoveryApplication" → http://localhost:8761

**Terminal 2 - API Gateway:**
```bash
cd api-gateway
mvn spring-boot:run
```
Wait for: "Started ApiGatewayApplication" → http://localhost:8080

**Terminal 3 - Auth Service:**
```bash
cd auth-service
mvn spring-boot:run
```
Wait for: "Started AuthServiceApplication" → http://localhost:8081

**Terminal 4 - Chat Service:**
```bash
cd chat-service
mvn spring-boot:run
```
Wait for: "Started ChatServiceApplication" → http://localhost:8082

**Terminal 5 - Notification Service:**
```bash
cd notification-service
mvn spring-boot:run
```
Wait for: "Started NotificationServiceApplication" → http://localhost:8083

**Terminal 6 - Call Service:**
```bash
cd call-service
mvn spring-boot:run
```
Wait for: "Started CallServiceApplication" → http://localhost:8084

### Step 4: Verify Setup

1. **Check Eureka Dashboard**: http://localhost:8761
   - All 5 services should be registered

2. **Test API Gateway**: 
   ```bash
   curl http://localhost:8080/actuator/health
   ```

3. **Test Registration**:
   ```bash
   curl -X POST http://localhost:8080/api/auth/register \
     -H "Content-Type: application/json" \
     -d '{
       "username": "testuser",
       "email": "test@example.com",
       "password": "Test1234",
       "displayName": "Test User"
     }'
   ```

4. **Test Login**:
   ```bash
   curl -X POST http://localhost:8080/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{
       "emailOrUsername": "test@example.com",
       "password": "Test1234"
     }'
   ```

## 🛠️ Manual Setup (Without Docker)

### Step 1: Install Dependencies

**MongoDB:**
```bash
# macOS
brew install mongodb-community@7.0
brew services start mongodb-community@7.0

# Ubuntu/Debian
sudo apt-get install mongodb-org
sudo systemctl start mongod

# Windows
# Download from https://www.mongodb.com/try/download/community
```

**Redis:**
```bash
# macOS
brew install redis
brew services start redis

# Ubuntu/Debian
sudo apt-get install redis-server
sudo systemctl start redis

# Windows
# Download from https://github.com/microsoftarchive/redis/releases
```

**Kafka:**
```bash
# Download Kafka 7.5.0
wget https://downloads.apache.org/kafka/7.5.0/kafka_2.13-7.5.0.tgz
tar -xzf kafka_2.13-7.5.0.tgz
cd kafka_2.13-7.5.0

# Start Zookeeper
bin/zookeeper-server-start.sh config/zookeeper.properties

# Start Kafka (in new terminal)
bin/kafka-server-start.sh config/server.properties
```

### Step 2: Follow Steps 2-4 from Quick Setup

## 🧪 Testing the Application

### 1. Register a User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "alice",
    "email": "alice@example.com",
    "password": "Alice1234",
    "displayName": "Alice"
  }'
```

### 2. Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "emailOrUsername": "alice@example.com",
    "password": "Alice1234"
  }'
```

Save the `accessToken` from the response.

### 3. Get Profile
```bash
curl http://localhost:8080/api/users/profile \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

### 4. Create a Chat Room
```bash
curl -X POST http://localhost:8080/api/rooms \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "General",
    "description": "General discussion",
    "type": "GROUP"
  }'
```

### 5. Send a Message
```bash
curl -X POST http://localhost:8080/api/messages \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "roomId": "ROOM_ID_FROM_STEP_4",
    "content": "Hello, World!",
    "type": "TEXT"
  }'
```

## 🔍 Troubleshooting

### Services Not Registering with Eureka
- Wait 30-60 seconds for registration
- Check service logs for errors
- Verify Eureka is running on port 8761

### MongoDB Connection Issues
```bash
# Check if MongoDB is running
mongosh --eval "db.adminCommand('ping')"

# Check connection string in application.yml
```

### Redis Connection Issues
```bash
# Check if Redis is running
redis-cli ping
# Should return: PONG
```

### Kafka Connection Issues
```bash
# Check if Kafka is running
kafka-topics.sh --list --bootstrap-server localhost:9092
```

### Port Already in Use
```bash
# Find process using port (e.g., 8080)
# macOS/Linux
lsof -i :8080

# Windows
netstat -ano | findstr :8080

# Kill the process
kill -9 PID  # macOS/Linux
taskkill /PID PID /F  # Windows
```

## 📊 Service Health Checks

```bash
# Service Discovery
curl http://localhost:8761/actuator/health

# API Gateway
curl http://localhost:8080/actuator/health

# Auth Service
curl http://localhost:8081/actuator/health

# Chat Service
curl http://localhost:8082/actuator/health

# Notification Service
curl http://localhost:8083/actuator/health

# Call Service
curl http://localhost:8084/actuator/health
```

## 🛑 Stopping Services

### Stop Microservices
Press `Ctrl+C` in each terminal window

### Stop Docker Services
```bash
docker-compose down

# To remove volumes as well
docker-compose down -v
```

## 📚 Next Steps

1. Read the [main README.md](README.md) for detailed API documentation
2. Check individual service READMEs for service-specific details
3. Explore the Eureka Dashboard at http://localhost:8761
4. Test WebSocket connections using the examples in README.md
5. Set up a frontend application to interact with the backend

## 💡 Tips

- **Development Mode**: Use `mvn spring-boot:run` for hot reload
- **Production Build**: Use `mvn clean package` then `java -jar target/*.jar`
- **Logs**: Check console output for each service
- **Database**: Use MongoDB Compass to view data: mongodb://localhost:27017
- **Redis**: Use Redis CLI to inspect cache: `redis-cli`

## 🆘 Getting Help

If you encounter issues:
1. Check service logs for error messages
2. Verify all infrastructure services are running
3. Ensure all environment variables are set correctly
4. Check that ports are not already in use
5. Review the troubleshooting section above

---

**Happy Coding! 🎉**
