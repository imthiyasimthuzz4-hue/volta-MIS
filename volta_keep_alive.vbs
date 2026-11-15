Set WshShell = CreateObject("WScript.Shell")
WshShell.Run "powershell.exe -NoProfile -ExecutionPolicy Bypass -WindowStyle Hidden -File ""c:\Users\user\volta MIS\volta-mis\keep_alive_local.ps1""", 0, False
Set WshShell = Nothing
