@echo off
setlocal
echo Stopping VOLTA MIS background process...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr :8080 ^| findstr LISTENING') do (
    taskkill /f /pid %%a >nul 2>nul
    echo Stopped process with PID %%a listening on port 8080.
)
echo VOLTA MIS has been stopped.
pause
