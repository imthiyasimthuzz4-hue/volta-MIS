@echo off
setlocal
cd /d "%~dp0"
set "STARTUP=%APPDATA%\Microsoft\Windows\Start Menu\Programs\Startup"

(
echo Set WshShell = CreateObject("WScript.Shell"^)
echo WshShell.CurrentDirectory = "%~dp0"
echo WshShell.Run """C:\Program Files\Java\jdk-21.0.12.1\bin\javaw.exe"" -jar ""%~dp0target\volta-mis-1.0.0.jar""", 0, False
) > "%STARTUP%\VOLTA-MIS-AutoStart.vbs"

echo ========================================================
echo       VOLTA MIS Auto-Start Configured!
echo ========================================================
echo Whenever you start Windows, VOLTA MIS will automatically
echo run in the background.
echo.
pause
