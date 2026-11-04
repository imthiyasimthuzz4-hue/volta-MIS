@echo off
setlocal
cd /d "%~dp0"
title VOLTA MIS Launcher
echo ============================================
echo        VOLTA ELECTRICAL ASSOCIATES MIS
echo ============================================
echo.
where java >nul 2>nul
if errorlevel 1 (echo Java was not found. Install Java 21 first.& pause& exit /b 1)
where mvn >nul 2>nul
if errorlevel 1 (echo Maven was not found. Install Apache Maven 3.9+ first.& echo Then double-click run.bat again.& pause& exit /b 1)
echo MySQL should be installed and its service should be running.
echo Database: volta_mis  |  Username: root
echo.
set /p DB_PASSWORD=Enter your MySQL root password: 
set DB_USER=root
set VOLTA_ADMIN_USER=admin
set /p VOLTA_ADMIN_PASSWORD=Set a new VOLTA MIS login password (not your MySQL password): 
if "%VOLTA_ADMIN_PASSWORD%"=="" set VOLTA_ADMIN_PASSWORD=ChangeMe-12345
set /p DB_PASSWORD=Enter your MySQL root password:
set DB_PASSWORD=%DB_PASSWORD%
echo.
echo Starting VOLTA MIS. Keep this window open while using the system.
echo When startup completes, your browser will open http://localhost:8080
start "" /min cmd /c "timeout /t 18 ^>nul ^& start http://localhost:8080/login"
call mvn spring-boot:run
if errorlevel 1 (echo.& echo Startup failed. Take a screenshot of the error above and ask for help.)
pause
