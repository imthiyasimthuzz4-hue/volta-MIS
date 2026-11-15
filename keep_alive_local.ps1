# VOLTA MIS Local Silent Background Keep-Alive
$url = "https://volta-mis-app.onrender.com/login"
while ($true) {
    try {
        $null = Invoke-WebRequest -Uri $url -UseBasicParsing -TimeoutSec 15
    } catch {
        # ignore network hiccup
    }
    Start-Sleep -Seconds 180
}
