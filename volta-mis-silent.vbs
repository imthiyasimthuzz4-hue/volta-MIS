Set WshShell = CreateObject("WScript.Shell")
WshShell.CurrentDirectory = "C:\Users\user\volta MIS\volta-mis"
WshShell.Run """C:\Program Files\Java\jdk-21.0.12.1\bin\javaw.exe"" -jar ""C:\Users\user\volta MIS\volta-mis\target\volta-mis-1.0.0.jar""", 0, False
