import glob
import re

count = 0
for f in glob.glob('src/main/resources/templates/**/*.html', recursive=True):
    content = open(f, encoding='utf-8').read()
    if 'th:href="@{/css/style.css}"' in content:
        new_content = content.replace('th:href="@{/css/style.css}"', 'th:href="@{/css/style.css(v=${#dates.createNow().time})}"')
        open(f, 'w', encoding='utf-8').write(new_content)
        count += 1
        print('Updated:', f)

print(f'Updated {count} templates with cache-busting timestamp.')
