import urllib.request
import urllib.parse
import http.cookiejar
import re

cj = http.cookiejar.CookieJar()
opener = urllib.request.build_opener(urllib.request.HTTPCookieProcessor(cj))

# 1. Get login CSRF
res = opener.open('http://localhost:8080/login')
html = res.read().decode('utf-8')
csrf_match = re.search(r'name="_csrf"\s+value="([^"]+)"', html)
csrf = csrf_match.group(1) if csrf_match else ''

# 2. Login
data = urllib.parse.urlencode({'username': 'admin', 'password': 'Admin@12345', '_csrf': csrf}).encode('utf-8')
res = opener.open('http://localhost:8080/login', data=data)

# 3. Check Payroll
res = opener.open('http://localhost:8080/payroll')
p_html = res.read().decode('utf-8')

for name in ['Arun Kumar K', 'Muhammed Shafi']:
    if name in p_html:
        print('Employee Verified in Payroll:', name)
    else:
        print('NOT found:', name)

hours = re.findall(r'(\d+(?:\.\d+)?)\s*hrs worked', p_html)
print('Hours worked values:', hours)

rates = re.findall(r'Rate:\s*₹\s*(\d+(?:\.\d+)?)/hr', p_html)
print('Hourly rates found:', rates)

wages = re.findall(r'₹\s*0\.00', p_html)
print('Count of Zero Balance (₹ 0.00) cells:', len(wages))
