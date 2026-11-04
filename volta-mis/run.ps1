$ErrorActionPreference = 'Stop'
Set-Location -LiteralPath $PSScriptRoot
Write-Host "==============================================" -ForegroundColor Cyan
Write-Host "       VOLTA ELECTRICAL ASSOCIATES MIS" -ForegroundColor Cyan
Write-Host "==============================================" -ForegroundColor Cyan
Write-Host "Enter the MySQL root password. Input is hidden."
$secure = Read-Host "MySQL root password" -AsSecureString
$ptr = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($secure)
try {
    $env:DB_PASSWORD = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($ptr)
    $env:DB_USERNAME = "root"
    $env:DB_URL = "jdbc:mysql://localhost:3306/volta_mis?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Kolkata"
    Write-Host "Starting VOLTA MIS. Keep this window open." -ForegroundColor Green
    mvn spring-boot:run
} finally {
    [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($ptr)
    Remove-Item Env:DB_PASSWORD -ErrorAction SilentlyContinue
}
