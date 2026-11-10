import os
import zipfile

desktop_dir = os.path.join(os.environ.get('USERPROFILE', ''), r'OneDrive\Desktop')
zip_path = os.path.join(desktop_dir, 'VOLTA_MIS_Free_Cloud_Deploy.zip')

project_root = r'c:\Users\user\volta MIS\volta-mis'
exclude_dirs = {'.git', 'target', '.idea', 'bin', 'obj', '__pycache__', 'mingit', 'scratch'}
exclude_files = {'cloudflared.exe', 'cloudflared.log', 'cloudflared_tunnel.log', 'cloudflared_live.log', 'VOLTA_MIS_Free_Cloud_Deploy.zip'}

with zipfile.ZipFile(zip_path, 'w', zipfile.ZIP_DEFLATED) as zipf:
    for root, dirs, files in os.walk(project_root):
        dirs[:] = [d for d in dirs if d not in exclude_dirs]
        for file in files:
            if file in exclude_files:
                continue
            full_path = os.path.join(root, file)
            rel_path = os.path.relpath(full_path, project_root)
            zipf.write(full_path, rel_path)

print('Created deployment zip at:', zip_path)
print('Size:', os.path.getsize(zip_path), 'bytes')
