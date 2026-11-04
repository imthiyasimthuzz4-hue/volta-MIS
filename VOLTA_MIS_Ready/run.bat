@echo off
setlocal EnableExtensions
cd /d "%~dp0"
title VOLTA MIS Launcher
echo ==============================================
echo       VOLTA ELECTRICAL ASSOCIATES MIS
echo ==============================================
echo.
if not exist "%~dp0pom.xml" (echo ERROR: pom.xml not found beside run.bat. Extract the ZIP and run this file from the VOLTA_MIS_Ready folder.& pause& exit /b 1)
where java >nul 2>nul
if errorlevel 1 (echo ERROR: Java was not found. Install Java 21.& pause& exit /b 1)
where mvn >nul 2>nul
if errorlevel 1 (echo ERROR: Maven was not found. Install Maven 3.9 or later.& pause& exit /b 1)
echo Database: volta_mis
echo MySQL username: root
echo.
set /p "DB_PASSWORD=Enter MySQL root password: "
if not defined DB_PASSWORD (echo ERROR: Password was blank.& pause& exit /b 1)
set "DB_USERNAME=root"
echo.
echo Starting VOLTA MIS. Keep this window open.
echo Wait until you see Started VoltaMisApplication, then open http://localhost:8080/login
echo.
call mvn spring-boot:run
echo.
echo VOLTA MIS stopped. Read the error message above.
pause
endlocal
