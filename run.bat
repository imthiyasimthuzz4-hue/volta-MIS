@echo off
setlocal EnableExtensions
cd /d "%~dp0"
title VOLTA MIS - Launcher

echo ===================================================
echo        VOLTA ELECTRICAL ASSOCIATES MIS
echo ===================================================
echo.

:: Check if server is already running on port 8080
netstat -ano | findstr :8080 | findstr LISTENING >nul 2>nul
if not errorlevel 1 (
    echo [OK] VOLTA MIS server is ALREADY ACTIVE and running on port 8080!
    echo.
    echo Opening http://localhost:8080 in your browser...
    echo.
    echo Local PC URL:    http://localhost:8080
    echo Mobile Site URL: http://192.168.1.63:8080/attendance/self-punch
    echo.
    start http://localhost:8080
    echo ---------------------------------------------------
    echo Default Login: admin / Admin@12345
    echo To stop the server, run: stop-volta-mis.bat
    echo ---------------------------------------------------
    echo.
    pause
    exit /b 0
)

where java >nul 2>nul
if errorlevel 1 (
    echo [ERROR] Java 21 was not found on your system.
    echo Please make sure Java 21 is installed and added to PATH.
    pause
    exit /b 1
)

:: Set default MySQL credentials
if "%DB_USERNAME%"=="" set "DB_USERNAME=root"
if "%DB_PASSWORD%"=="" set "DB_PASSWORD=Volta@2026"
if "%DB_URL%"=="" set "DB_URL=jdbc:mysql://localhost:3306/volta_mis?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Kolkata"

echo Database Target: MySQL (localhost:3306 / volta_mis)
echo System User:     %DB_USERNAME%
echo Application URL: http://localhost:8080
echo Mobile Site URL: http://192.168.1.63:8080/attendance/self-punch
echo.
echo Starting VOLTA MIS server...
echo Once started, your browser will automatically open http://localhost:8080
echo Default login:
echo   Username: admin
echo   Password: Admin@12345
echo.
echo (To stop the server, press Ctrl+C in this window)
echo ===================================================
echo.

:: Automatically open browser once port 8080 is listening
start /b powershell -NoProfile -Command "for ($i=0; $i -lt 40; $i++) { Start-Sleep -Seconds 1; try { $tcp = New-Object System.Net.Sockets.TcpClient('127.0.0.1', 8080); if ($tcp.Connected) { $tcp.Close(); Start-Process 'http://localhost:8080'; break; } } catch {} }"

:: Launch pre-built production JAR if available for instant startup (3 seconds)
if exist "%~dp0target\volta-mis-1.0.0.jar" (
    java -jar "%~dp0target\volta-mis-1.0.0.jar"
) else (
    call mvn spring-boot:run
)

if errorlevel 1 (
    echo.
    echo [NOTE] If MySQL failed to start, you can also use 'run-standalone.bat'
    echo to run VOLTA MIS with built-in storage without MySQL!
    echo.
)

pause
endlocal
