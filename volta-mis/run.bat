@echo off
setlocal
cd /d "%~dp0"
title VOLTA MIS Launcher
echo ================================================
echo       VOLTA ELECTRICAL ASSOCIATES MIS
echo ================================================
echo.
where java >nul 2>nul
if errorlevel 1 (echo Java was not found. Install Java 21 first.& pause & exit /b 1)
where mvn >nul 2>nul
if errorlevel 1 (echo Maven was not found. Install Apache Maven 3.9+ first.& pause & exit /b 1)
if not exist pom.xml (echo ERROR: pom.xml not found. Keep run.bat beside pom.xml.& pause & exit /b 1)
set /p DB_PASSWORD=Enter your MySQL root password:
if "%DB_PASSWORD%"=="" (echo Password cannot be blank.& pause & exit /b 1)
set DB_USERNAME=root
echo.
echo Starting VOLTA MIS. Keep this window open.
echo Browser will open at http://localhost:8080/login
start "" /min cmd /c "timeout /t 12 >nul & start http://localhost:8080/login"
call mvn spring-boot:run
echo.
echo VOLTA MIS stopped. If an error appeared above, take a screenshot.
pause
