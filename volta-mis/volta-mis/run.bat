@echo off
setlocal
cd /d "%~dp0"
title VOLTA MIS Launcher
where java >nul 2>&1
if errorlevel 1 (
  echo Java was not found. Install Java 21.
  pause
  exit /b 1
)
where mvn >nul 2>&1
if errorlevel 1 (
  echo Maven was not found. Install Apache Maven 3.9 or later.
  pause
  exit /b 1
)
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0run.ps1"
echo.
echo VOLTA MIS has stopped. Review any error message above.
pause
