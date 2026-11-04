import os
import urllib.request
import urllib.parse
import shutil

app_url = 'https://filtering-sodium-emily-stud.trycloudflare.com/mobile'
qr_api = 'https://api.qrserver.com/v1/create-qr-code/?size=320x320&data=' + urllib.parse.quote(app_url)

# Save QR image
out1 = 'src/main/resources/static/images/mobile-qr.png'
out2 = 'target/classes/static/images/mobile-qr.png'
out3 = r'C:\Users\user\.gemini\antigravity\brain\cafd2742-0e34-45b1-8dc2-0ca82f941215\mobile-qr.png'
urllib.request.urlretrieve(qr_api, out1)
shutil.copyfile(out1, out2)
shutil.copyfile(out1, out3)

html_content = f"""<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>VOLTA Electrical Associates - Official Mobile App</title>
    <style>
        body {{
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
            background: linear-gradient(135deg, #071529, #003366);
            color: #ffffff;
            margin: 0;
            padding: 30px 15px;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 90vh;
        }}
        .card {{
            background: #ffffff;
            color: #1e293b;
            max-width: 540px;
            width: 100%;
            border-radius: 20px;
            box-shadow: 0 20px 50px rgba(0,0,0,0.5);
            padding: 32px 24px;
            text-align: center;
        }}
        .logo-badge {{
            background: #003366;
            color: #ffcc00;
            display: inline-block;
            padding: 6px 16px;
            border-radius: 30px;
            font-weight: 800;
            font-size: 13px;
            letter-spacing: 0.5px;
            margin-bottom: 12px;
        }}
        h1 {{
            color: #003366;
            margin: 0 0 6px 0;
            font-size: 24px;
            font-weight: 800;
        }}
        p.subtitle {{
            color: #64748b;
            font-size: 13.5px;
            margin: 0 0 20px 0;
        }}
        .qr-box {{
            background: #f8fafc;
            border: 2px dashed #003366;
            border-radius: 14px;
            padding: 16px;
            display: inline-block;
            margin: 8px 0;
        }}
        .qr-box img {{
            width: 240px;
            height: 240px;
            display: block;
            margin: 0 auto;
        }}
        .btn-open {{
            display: block;
            background: #003366;
            color: #ffffff;
            text-decoration: none;
            padding: 14px 20px;
            border-radius: 12px;
            font-size: 16px;
            font-weight: 800;
            margin: 18px 0 12px 0;
            box-shadow: 0 4px 15px rgba(0,51,102,0.3);
        }}
        .features-grid {{
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 10px;
            margin: 18px 0;
            text-align: left;
        }}
        .feature-item {{
            background: #f1f5f9;
            border-radius: 10px;
            padding: 10px 12px;
            font-size: 12px;
            font-weight: 700;
            color: #0f172a;
        }}
        .instructions {{
            text-align: left;
            background: #eff6ff;
            border-left: 4px solid #003366;
            padding: 14px;
            border-radius: 8px;
            margin-top: 16px;
            font-size: 12.5px;
            line-height: 1.5;
        }}
        .instructions h3 {{
            margin: 0 0 6px 0;
            font-size: 13px;
            color: #003366;
        }}
        .instructions ul {{
            margin: 0;
            padding-left: 18px;
        }}
    </style>
</head>
<body>
    <div class="card">
        <div class="logo-badge">⚡ OFFICIAL ENTERPRISE APP</div>
        <h1>VOLTA Electrical Associates</h1>
        <p class="subtitle">Mobile Field Billing, Site Attendance & Live Operations</p>

        <div class="qr-box">
            <img src="{qr_api}" alt="VOLTA Mobile App QR">
        </div>

        <p style="font-size: 13px; color: #64748b; margin: 8px 0;">Point your iPhone or Android camera at the QR code</p>

        <a class="btn-open" href="{app_url}" target="_blank">
            Open VOLTA Mobile App →
        </a>

        <div class="features-grid">
            <div class="feature-item">🧾 Site Spot Billing</div>
            <div class="feature-item">⚡ Camera & GPS Punch</div>
            <div class="feature-item">📦 Godown Stock Check</div>
            <div class="feature-item">📱 1-Tap WhatsApp Bills</div>
        </div>

        <div class="instructions">
            <h3>How to Install as App on Phone:</h3>
            <ul>
                <li><b>Android (Chrome):</b> Tap the <i>'Install Official App'</i> banner or tap 3 dots (⋮) &rarr; <b>'Install app'</b>.</li>
                <li><b>iPhone (Safari):</b> Tap the <b>Share</b> button (square with arrow) &rarr; tap <b>'Add to Home Screen'</b>.</li>
            </ul>
        </div>
    </div>
</body>
</html>
"""

paths = [
    os.path.join(os.environ['USERPROFILE'], 'OneDrive', 'Desktop', 'VOLTA Mobile Attendance.html'),
    os.path.join(os.environ['USERPROFILE'], 'Desktop', 'VOLTA Mobile Attendance.html'),
    os.path.join(os.environ['USERPROFILE'], 'OneDrive', 'Desktop', 'VOLTA Electrical Associates Mobile App.html'),
    os.path.join(os.environ['USERPROFILE'], 'Desktop', 'VOLTA Electrical Associates Mobile App.html')
]

for p in paths:
    try:
        with open(p, 'w', encoding='utf-8') as f:
            f.write(html_content)
        print('Written successfully to:', p)
    except Exception as e:
        print('Error:', e)
