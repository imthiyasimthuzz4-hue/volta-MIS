import glob
import re

for f in glob.glob('src/main/resources/templates/**/*.html', recursive=True):
    content = open(f, encoding='utf-8').read()
    # find inline styles with width >= 350px
    matches = re.findall(r'style="[^"]*(?:width|min-width):\s*(\d{3,})px[^"]*"', content)
    big = [int(m) for m in matches if int(m) > 320]
    if big:
        print(f, big)
