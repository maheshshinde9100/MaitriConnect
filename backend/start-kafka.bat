@echo off
echo ========================================
echo Starting Kafka and Zookeeper
echo ========================================
echo.

set KAFKA_HOME=D:\kafka_2.13-3.9.1

REM Check if Kafka directory exists
if not exist "%KAFKA_HOME%" (
    echo [ERROR] Kafka directory not found: %KAFKA_HOME%
    echo Please update KAFKA_HOME in this script
    pause
    exit /b 1
)

echo Kafka Home: %KAFKA_HOME%
echo.

echo Step 1: Starting Zookeeper...
echo Opening new window for Zookeeper...
start "Zookeeper" cmd /k "cd /d %KAFKA_HOME% && bin\windows\zookeeper-server-start.bat config\zookeeper.properties"

echo Waiting 10 seconds for Zookeeper to start...
timeout /t 10 /nobreak >nul

echo.
echo Step 2: Starting Kafka...
echo Opening new window for Kafka...
start "Kafka" cmd /k "cd /d %KAFKA_HOME% && bin\windows\kafka-server-start.bat config\server.properties"

echo.
echo ========================================
echo Kafka and Zookeeper are starting!
echo ========================================
echo.
echo Check the opened windows for status.
echo.
echo Zookeeper: localhost:2181
echo Kafka: localhost:9092
echo.
echo Wait 30 seconds before starting services.
echo.
pause
