@echo off
echo Starting MaitriConnect Microservices...
echo.

echo Starting Service Discovery (Eureka Server) on port 8761...
start "Service Discovery" cmd /k "cd service-discovery && mvn spring-boot:run"
echo Waiting for Service Discovery to start...
timeout /t 30 /nobreak

echo Starting Auth Service on port 8081...
start "Auth Service" cmd /k "cd auth-service && mvn spring-boot:run"
echo Waiting for Auth Service to start...
timeout /t 20 /nobreak

echo Starting Chat Service on port 8082...
start "Chat Service" cmd /k "cd chat-service && mvn spring-boot:run"
echo Waiting for Chat Service to start...
timeout /t 20 /nobreak

echo Starting API Gateway on port 8080...
start "API Gateway" cmd /k "cd api-gateway && mvn spring-boot:run"

echo.
echo All services are starting up...
echo.
echo Service URLs:
echo - Eureka Dashboard: http://localhost:8761
echo - API Gateway: http://localhost:8080
echo - Auth Service: http://localhost:8081
echo - Chat Service: http://localhost:8082
echo.
echo Open chat-client.html in your browser to test the application!
pause