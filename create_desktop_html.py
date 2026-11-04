import os

html_content = """<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>VOLTA MIS - Mobile App & Attendance Access</title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #0b1a30, #1e3c72);
            color: #ffffff;
            margin: 0;
            padding: 30px 15px;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 90vh;
        }
        .card {
            background: #ffffff;
            color: #222222;
            max-width: 520px;
            width: 100%;
            border-radius: 16px;
            box-shadow: 0 15px 35px rgba(0,0,0,0.4);
            padding: 32px 24px;
            text-align: center;
        }
        .logo-badge {
            background: #003366;
            color: #ffcc00;
            display: inline-block;
            padding: 8px 18px;
            border-radius: 30px;
            font-weight: bold;
            font-size: 14px;
            letter-spacing: 1px;
            margin-bottom: 12px;
        }
        h1 {
            color: #003366;
            margin: 0 0 8px 0;
            font-size: 24px;
        }
        p.subtitle {
            color: #666;
            font-size: 14px;
            margin: 0 0 20px 0;
        }
        .qr-box {
            background: #f7f9fc;
            border: 2px dashed #003366;
            border-radius: 12px;
            padding: 16px;
            display: inline-block;
            margin: 10px 0;
        }
        .qr-box img {
            width: 230px;
            height: 230px;
            display: block;
            margin: 0 auto;
        }
        .link-btn {
            display: block;
            background: #003366;
            color: #ffffff;
            text-decoration: none;
            padding: 14px 20px;
            border-radius: 8px;
            font-size: 16px;
            font-weight: 600;
            margin: 18px 0 10px 0;
            transition: 0.2s;
            word-break: break-all;
        }
        .link-btn:hover {
            background: #002244;
        }
        .instructions {
            text-align: left;
            background: #eef4fb;
            border-left: 4px solid #003366;
            padding: 14px;
            border-radius: 6px;
            margin-top: 20px;
            font-size: 13px;
            line-height: 1.5;
        }
        .instructions h3 {
            margin: 0 0 8px 0;
            font-size: 14px;
            color: #003366;
        }
        .instructions ul {
            margin: 0;
            padding-left: 20px;
        }
        .tag {
            background: #28a745;
            color: #fff;
            padding: 2px 8px;
            border-radius: 4px;
            font-size: 11px;
            font-weight: bold;
        }
    </style>
</head>
<body>
    <div class="card">
        <div class="logo-badge">VOLTA ELECTRICAL ASSOCIATES</div>
        <h1>Mobile Attendance App</h1>
        <p class="subtitle">Live Camera & GPS Verified Self-Punch System</p>

        <div class="qr-box">
            <img src="https://api.qrserver.com/v1/create-qr-code/?size=300x300&data=https%3A%2F%2Fshakira-neural-instead-accordance.trycloudflare.com%2Fattendance%2Fself-punch" alt="Mobile Attendance QR">
        </div>

        <p style="font-size: 13px; color: #555; margin: 8px 0;">Point your phone camera at the QR code above</p>

        <a class="link-btn" href="https://shakira-neural-instead-accordance.trycloudflare.com/attendance/self-punch" target="_blank">
            Open Mobile Attendance (Self Punch)
        </a>

        <div class="instructions">
            <h3>How to install on Phone:</h3>
            <ul>
                <li><b>Android (Chrome):</b> Tap the <i>'Install App'</i> button or tap the 3 dots (⋮) and choose <b>'Install app'</b> / <b>'Add to Home screen'</b>.</li>
                <li><b>iPhone (Safari):</b> Tap the <b>Share</b> button (square with arrow) and tap <b>'Add to Home Screen'</b>.</li>
                <li><span class="tag">Anti-Fraud</span> Live camera selfie required. Gallery photo uploads are strictly blocked.</li>
            </ul>
        </div>
    </div>
</body>
</html>
"""

paths = [
    os.path.join(os.environ['USERPROFILE'], 'OneDrive', 'Desktop', 'VOLTA Mobile Attendance.html'),
    os.path.join(os.environ['USERPROFILE'], 'Desktop', 'VOLTA Mobile Attendance.html')
]

for p in paths:
    try:
        with open(p, 'w', encoding='utf-8') as f:
            f.write(html_content)
        print('Written successfully to:', p)
    except Exception as e:
        print('Error writing to', p, e)
