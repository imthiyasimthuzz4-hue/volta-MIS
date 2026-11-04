# VOLTA ELECTRICAL ASSOCIATES MIS - CLOUD DEPLOYMENT GUIDE

This document provides complete instructions to deploy **VOLTA MIS** to cloud hosting platforms (**Render**, **Railway**, **Docker**, or **Linux VPS**).

---

## 📋 Architecture Overview

* **Backend**: Spring Boot 3.4.3 (Java 21)
* **Databases Supported**: MySQL 8.0+ / PostgreSQL 15+ / Embedded H2 (Zero-code switching via standard JDBC)
* **Frontend**: Thymeleaf, HTML5 Camera API, Responsive CSS (No external CDN required)
* **Storage**: Persistent disk / volume mounted at `/app/uploads` for live worker site camera photos
* **Default Port**: `8080` (automatically binds to cloud `$PORT` e.g. on Render/Railway)

---

## 🚀 Option 1: Deploy to Render Cloud (Recommended Free / Low-Cost)

Render provides free/starter web services and managed databases with zero server configuration.

### Steps:
1. **Push your code to GitHub**:
   ```bash
   git init
   git add .
   git commit -m "Deploy VOLTA MIS"
   git remote add origin https://github.com/YOUR_USERNAME/volta-mis.git
   git push -u origin main
   ```

2. **Deploy with 1-Click Blueprint**:
   * Log in to [Render Dashboard](https://dashboard.render.com).
   * Click **New +** > **Blueprint**.
   * Connect your GitHub repository `volta-mis`.
   * Render will automatically detect [`render.yaml`](file:///c:/Users/user/volta%20MIS/volta-mis/render.yaml) and configure:
     * **Web Service**: Multi-stage Docker container build.
     * **Database**: Managed PostgreSQL database.
     * **Persistent Disk**: 5GB disk mounted at `/app/uploads` to store attendance selfies safely.
   * Click **Apply**.

3. **Access Your Live System**:
   * Render provides an automatic, free HTTPS URL: `https://volta-mis-xxxx.onrender.com`.
   * Open `/attendance/self-punch` to test worker live camera site check-ins!

---

## 🚂 Option 2: Deploy to Railway

Railway offers fast Docker deployment with one-click MySQL plugins.

### Steps:
1. Log in to [Railway](https://railway.app).
2. Click **New Project** > **Deploy from GitHub repo**.
3. Select your `volta-mis` repository.
4. Click **+ New** > **Database** > **Add MySQL**.
5. In your application settings on Railway:
   * Railway uses [`railway.json`](file:///c:/Users/user/volta%20MIS/volta-mis/railway.json) and [`Dockerfile`](file:///c:/Users/user/volta%20MIS/volta-mis/Dockerfile) automatically.
   * Add a persistent volume under **Settings** > **Volumes** mounted to `/app/uploads`.
6. Railway will automatically inject `MYSQL_URL` / `DATABASE_URL` and deploy the application.
7. Click **Generate Domain** under Networking to get your public HTTPS URL.

---

## 🐧 Option 3: Deploy to Linux VPS (Ubuntu / Debian / AWS EC2 / DigitalOcean)

If you have a Linux VPS or cloud virtual machine, use the automated setup script.

### 1-Step Automated Installation:
1. Connect to your VPS via SSH:
   ```bash
   ssh root@your-server-ip
   ```
2. Upload the project folder to `/opt/volta-mis` or clone the repository:
   ```bash
   git clone https://github.com/YOUR_USERNAME/volta-mis.git /opt/volta-mis
   cd /opt/volta-mis
   ```
3. Run the automated deployment script:
   ```bash
   sudo bash deploy-vps.sh your-domain.com
   ```
   *(If you don't have a domain name yet, run `sudo bash deploy-vps.sh` without a domain to use the server IP).*

### What the script automatically does:
* Installs OpenJDK 21, MySQL 8 Server, Nginx, UFW Firewall, and Certbot.
* Creates the `volta_mis` database and a dedicated production user.
* Configures systemd service `volta-mis.service` (auto-restarts on crash and boots on system reboot).
* Sets up Nginx reverse proxy with live camera video streaming support and large photo upload limits.
* Configures free SSL certificate from Let's Encrypt for HTTPS.

### Managing the Linux Service:
* **Check Status**: `sudo systemctl status volta-mis`
* **Restart Service**: `sudo systemctl restart volta-mis`
* **View Live Application Logs**: `sudo journalctl -u volta-mis -f` or `tail -f /opt/volta-mis/logs/app.log`

---

## 🐳 Option 4: Deploy with Docker Compose (Any Server or PC)

Deploy the complete multi-container stack (App + MySQL 8.0) with a single command.

### Steps:
1. Ensure Docker and Docker Compose are installed:
   ```bash
   docker --version
   docker compose version
   ```
2. In the project root, start the containers:
   ```bash
   docker compose up -d --build
   ```
3. Check running status:
   ```bash
   docker compose ps
   ```
4. View real-time logs:
   ```bash
   docker compose logs -f app
   ```
5. To stop the application:
   ```bash
   docker compose down
   ```

*(All database records and photo proof uploads are persisted in named volumes `volta_mysql_data` and `volta_uploads`).*

---

## 🔐 Environment Variables Reference

| Variable | Description | Default |
| :--- | :--- | :--- |
| `PORT` | HTTP Web Port | `8080` |
| `DB_URL` / `DATABASE_URL` | JDBC Connection String | `jdbc:mysql://localhost:3306/volta_mis...` |
| `DB_USERNAME` | Database Username | `root` |
| `DB_PASSWORD` | Database Password | `Volta@2026` |
| `UPLOAD_DIR` | Directory for Attendance Photos | `uploads` (or `/app/uploads`) |
| `JPA_DDL_AUTO` | Hibernate Schema Strategy | `update` |
| `JAVA_OPTS` | JVM Memory & GC Flags | `-XX:MaxRAMPercentage=75.0 -XX:+UseG1GC` |

---

## 👥 Default Production Credentials

| Role | Username | Password |
| :--- | :--- | :--- |
| **Admin (Director)** | `admin` | `Admin@12345` |
| **Site Engineer** | `engineer` | `Engineer@123` |
| **Chief Accountant** | `accountant` | `Account@123` |
| **Auditor / Viewer** | `viewer` | `Viewer@123` |

*(Note: Change passwords upon initial deployment via the User Management screen at `/users`).*
