@echo off
setlocal EnableExtensions
cd /d "%~dp0"
title VOLTA MIS - Standalone Mode (Zero MySQL)

echo ===================================================
echo        VOLTA ELECTRICAL ASSOCIATES MIS
echo             (Standalone Offline Mode)
echo ===================================================
echo.
echo Running with built-in embedded database.
echo No MySQL server or root password required!
echo Data is saved permanently in the ./data folder.
echo.

where java >nul 2>nul
if errorlevel 1 (
    echo [ERROR] Java 21 was not found.
    pause
    exit /b 1
)

where mvn >nul 2>nul
if errorlevel 1 (
    echo [ERROR] Maven was not found.
    pause
    exit /b 1
)

echo Starting VOLTA MIS server in standalone mode...
echo Default login:
echo   Username: admin
echo   Password: Admin@12345
echo.

start /b powershell -NoProfile -Command "for ($i=0; $i -lt 40; $i++) { Start-Sleep -Seconds 1; try { $tcp = New-Object System.Net.Sockets.TcpClient('127.0.0.1', 8080); if ($tcp.Connected) { $tcp.Close(); Start-Process 'http://localhost:8080'; break; } } catch {} }"

call mvn spring-boot:run -Dspring-boot.run.profiles=h2

pause
endlocal
