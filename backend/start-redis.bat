@echo off
echo ========================================
echo Starting Redis
echo ========================================
echo.

echo Checking if Redis is already running...
redis-cli ping >nul 2>&1
if %errorlevel% equ 0 (
    echo [INFO] Redis is already running!
    echo.
    pause
    exit /b 0
)

echo Starting Redis Server...
echo Opening new window for Redis...
start "Redis Server" cmd /k "redis-server"

echo.
echo Waiting 3 seconds for Redis to start...
timeout /t 3 /nobreak >nul

echo.
echo Verifying Redis...
redis-cli ping >nul 2>&1
if %errorlevel% equ 0 (
    echo [SUCCESS] Redis is running!
    echo Redis: localhost:6379
) else (
    echo [WARNING] Redis may not be running properly
    echo Please check the Redis window
)

echo.
pause
