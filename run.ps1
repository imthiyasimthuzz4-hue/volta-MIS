$ErrorActionPreference = 'Stop'
Set-Location -LiteralPath $PSScriptRoot

Write-Host "==============================================" -ForegroundColor Cyan
Write-Host "       VOLTA ELECTRICAL ASSOCIATES MIS        " -ForegroundColor Cyan
Write-Host "==============================================" -ForegroundColor Cyan
Write-Host ""

$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "Volta@2026"
$env:DB_URL = "jdbc:mysql://localhost:3306/volta_mis?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Kolkata"

Write-Host "Connecting to MySQL Database: volta_mis" -ForegroundColor Yellow
Write-Host "Default Login:" -ForegroundColor White
Write-Host "  Username: admin" -ForegroundColor Green
Write-Host "  Password: Admin@12345" -ForegroundColor Green
Write-Host ""
Write-Host "Starting VOLTA MIS. Keep this terminal open." -ForegroundColor Cyan

# Launch browser watcher
Start-Job -ScriptBlock {
    for ($i = 0; $i -lt 40; $i++) {
        Start-Sleep -Seconds 1
        try {
            $tcp = New-Object System.Net.Sockets.TcpClient('127.0.0.1', 8080)
            if ($tcp.Connected) {
                $tcp.Close()
                Start-Process "http://localhost:8080"
                break
            }
        } catch {}
    }
} | Out-Null

mvn spring-boot:run
