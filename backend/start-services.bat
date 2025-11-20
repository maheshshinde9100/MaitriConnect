@echo off
echo Starting MaitriConnect Microservices...
echo.

echo Step 1: Starting Service Discovery (Eureka) on port 8761...
start "Service Discovery" cmd /k "cd service-discovery && mvn spring-boot:run"
echo Waiting 15 seconds for Eureka to start...
timeout /t 15 /nobreak

echo Step 2: Starting Auth Service on port 8081...
start "Auth Service" cmd /k "cd auth-service && mvn spring-boot:run"
echo Waiting 20 seconds for Auth Service to start...
timeout /t 20 /nobreak

echo Step 3: Starting Chat Service on port 8082...
start "Chat Service" cmd /k "cd chat-service && mvn spring-boot:run" 
echo Waiting 20 seconds for Chat Service to start...
timeout /t 20 /nobreak

echo Step 4: Starting API Gateway on port 8080...
start "API Gateway" cmd /k "cd api-gateway && mvn spring-boot:run"
echo Waiting 25 seconds for API Gateway to start...
timeout /t 25 /nobreak

echo.
echo ===============================================
echo ✅ ALL SERVICES STARTED!
echo ===============================================
echo.
echo IMPORTANT: Services using .properties configuration
echo Wait 1-2 minutes for all services to fully initialize
echo.
echo Test URLs (open in browser):
echo - Eureka Dashboard: http://localhost:8761
echo - Auth Service Health: http://localhost:8081/actuator/health
echo - Chat Service Health: http://localhost:8082/actuator/health
echo - API Gateway Health: http://localhost:8080/actuator/health
echo.
echo API Gateway Endpoints:
echo - Register: http://localhost:8080/api/auth/register
echo - Get Users: http://localhost:8080/api/auth/users
echo - Chat Rooms: http://localhost:8080/api/chat/rooms
echo.
echo Open http://localhost:3000/chat-client.html to test
echo ===============================================
echo.
pause