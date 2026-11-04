@echo off
setlocal
cd /d "%~dp0"
set "STARTUP=%APPDATA%\Microsoft\Windows\Start Menu\Programs\Startup"
(
echo Set WshShell = CreateObject("WScript.Shell"^)
echo WshShell.Run Chr(34^) ^& "%~dp0run.bat" ^& Chr(34^), 1, False
) > "%STARTUP%\VOLTA-MIS-Startup.vbs"
echo Startup shortcut created.
echo VOLTA MIS will start when you sign in to Windows and will ask for the MySQL password.
echo You can remove autostart by deleting VOLTA-MIS-Startup.vbs from:
echo %STARTUP%
pause
