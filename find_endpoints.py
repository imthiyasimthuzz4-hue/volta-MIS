import os
import re

for root, dirs, files in os.walk('src/main/java/in/volta/mis/controller'):
    for f in files:
        if f.endswith('.java'):
            p = os.path.join(root, f)
            with open(p, 'r', encoding='utf-8') as fp:
                c = fp.read()
                prefix_m = re.search(r'@RequestMapping\("([^"]*)"\)', c)
                prefix = prefix_m.group(1) if prefix_m else ''
                get_m = re.findall(r'@GetMapping(?:\("([^"]*)"\))?', c)
                post_m = re.findall(r'@PostMapping(?:\("([^"]*)"\))?', c)
                print(f'{f} (prefix={prefix}):')
                print('  GET :', get_m)
                print('  POST:', post_m)
