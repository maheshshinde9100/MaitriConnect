@echo off
echo ========================================
echo Stopping Kafka and Zookeeper
echo ========================================
echo.

set KAFKA_HOME=D:\kafka_2.13-3.9.1

echo Stopping Kafka...
cd /d %KAFKA_HOME%
bin\windows\kafka-server-stop.bat

echo.
echo Waiting 5 seconds...
timeout /t 5 /nobreak >nul

echo.
echo Stopping Zookeeper...
bin\windows\zookeeper-server-stop.bat

echo.
echo ========================================
echo Kafka and Zookeeper stopped!
echo ========================================
echo.
echo Note: You may need to close the terminal windows manually.
echo.
pause
