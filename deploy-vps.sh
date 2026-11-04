#!/usr/bin/env bash
# ==============================================================================
# VOLTA MIS - Automated Production Deployment Script for Linux VPS
# Compatible with: Ubuntu 22.04 / 24.04 LTS, Debian 12
# Supports: AWS EC2, DigitalOcean, Linode, Hetzner, or any Cloud VPS
# ==============================================================================

set -euo pipefail

RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${BLUE}====================================================================${NC}"
echo -e "${GREEN}      VOLTA ELECTRICAL ASSOCIATES MIS - LINUX VPS DEPLOYMENT        ${NC}"
echo -e "${BLUE}====================================================================${NC}"

# Ensure script is run with root privileges
if [ "$EUID" -ne 0 ]; then
  echo -e "${RED}[ERROR] Please run this script as root (e.g. sudo bash deploy-vps.sh)${NC}"
  exit 1
fi

# Variables (customize as required)
APP_DIR="/opt/volta-mis"
APP_USER="volta"
APP_JAR="$APP_DIR/volta-mis.jar"
DB_NAME="volta_mis"
DB_USER="volta_user"
DB_PASS="VoltaProd@2026"
DOMAIN_NAME="${1:-}"

echo -e "\n${YELLOW}Step 1: Updating system packages and installing prerequisites...${NC}"
apt-get update -y
apt-get install -y openjdk-21-jre-headless mysql-server nginx curl ufw certbot python3-certbot-nginx

echo -e "\n${YELLOW}Step 2: Configuring MySQL Database...${NC}"
systemctl enable --now mysql

# Create database and application user
mysql -u root <<EOF
CREATE DATABASE IF NOT EXISTS ${DB_NAME} CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS '${DB_USER}'@'localhost' IDENTIFIED WITH mysql_native_password BY '${DB_PASS}';
GRANT ALL PRIVILEGES ON ${DB_NAME}.* TO '${DB_USER}'@'localhost';
FLUSH PRIVILEGES;
EOF
echo -e "${GREEN}[OK] Database '${DB_NAME}' and user '${DB_USER}' configured successfully.${NC}"

echo -e "\n${YELLOW}Step 3: Setting up application user and directories...${NC}"
if ! id "$APP_USER" &>/dev/null; then
  useradd -r -s /bin/false -d "$APP_DIR" "$APP_USER"
fi

mkdir -p "$APP_DIR/uploads/attendance" "$APP_DIR/logs"
chown -R "$APP_USER:$APP_USER" "$APP_DIR"
chmod -R 750 "$APP_DIR"

echo -e "\n${YELLOW}Step 4: Copying application JAR...${NC}"
if [ -f "./target/volta-mis-1.0.0.jar" ]; then
  cp "./target/volta-mis-1.0.0.jar" "$APP_JAR"
elif [ -f "./volta-mis.jar" ]; then
  cp "./volta-mis.jar" "$APP_JAR"
else
  echo -e "${RED}[WARNING] No JAR found in ./target/volta-mis-1.0.0.jar. Please copy your JAR to $APP_JAR later.${NC}"
fi

chown "$APP_USER:$APP_USER" "$APP_JAR" 2>/dev/null || true

echo -e "\n${YELLOW}Step 5: Creating production environment configuration...${NC}"
cat > "$APP_DIR/application-prod.properties" <<EOF
spring.application.name=VOLTA MIS
server.port=8080
spring.datasource.url=jdbc:mysql://localhost:3306/${DB_NAME}?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Kolkata
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASS}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.open-in-view=false
spring.thymeleaf.cache=true
volta.upload.dir=$APP_DIR/uploads
EOF
chown "$APP_USER:$APP_USER" "$APP_DIR/application-prod.properties"
chmod 600 "$APP_DIR/application-prod.properties"

echo -e "\n${YELLOW}Step 6: Creating Systemd service (volta-mis.service)...${NC}"
cat > /etc/systemd/system/volta-mis.service <<EOF
[Unit]
Description=VOLTA Electrical Associates Management Information System (MIS)
After=network.target mysql.service
Wants=mysql.service

[Service]
Type=simple
User=$APP_USER
Group=$APP_USER
WorkingDirectory=$APP_DIR
ExecStart=/usr/bin/java -Xms256m -Xmx768m -XX:+UseG1GC -Dspring.config.additional-location=file:$APP_DIR/application-prod.properties -jar $APP_JAR
Restart=always
RestartSec=10
SuccessExitStatus=143
StandardOutput=append:$APP_DIR/logs/app.log
StandardError=append:$APP_DIR/logs/error.log

[Install]
WantedBy=multi-user.target
EOF

systemctl daemon-reload
systemctl enable volta-mis
systemctl restart volta-mis || true
echo -e "${GREEN}[OK] Systemd service 'volta-mis' enabled and started.${NC}"

echo -e "\n${YELLOW}Step 7: Configuring Nginx Reverse Proxy...${NC}"
SERVER_HOST="${DOMAIN_NAME:-_}"
cat > /etc/nginx/sites-available/volta-mis <<EOF
server {
    listen 80;
    server_name ${SERVER_HOST};

    # Maximum file upload size for worker site camera photos
    client_max_body_size 25M;

    location / {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;

        # WebSocket & live stream proxy support
        proxy_http_version 1.1;
        proxy_set_header Upgrade \$http_upgrade;
        proxy_set_header Connection "upgrade";
    }

    # Cache static assets
    location ~* \.(css|js|png|jpg|jpeg|gif|ico|svg)$ {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host \$host;
        expires 7d;
        add_header Cache-Control "public, no-transform";
    }
}
EOF

ln -sf /etc/nginx/sites-available/volta-mis /etc/nginx/sites-enabled/
rm -f /etc/nginx/sites-enabled/default
nginx -t
systemctl reload nginx
echo -e "${GREEN}[OK] Nginx reverse proxy configured successfully.${NC}"

echo -e "\n${YELLOW}Step 8: Configuring UFW Firewall...${NC}"
ufw allow 22/tcp || true
ufw allow 80/tcp || true
ufw allow 443/tcp || true
ufw --force enable || true

# Optional SSL via Certbot if domain name is provided
if [ -n "$DOMAIN_NAME" ] && [ "$DOMAIN_NAME" != "_" ]; then
  echo -e "\n${YELLOW}Step 9: Requesting free SSL certificate from Let's Encrypt for ${DOMAIN_NAME}...${NC}"
  certbot --nginx -d "$DOMAIN_NAME" --non-interactive --agree-tos --email contact@voltaelectricalassociates.com --redirect || true
fi

echo -e "\n${BLUE}====================================================================${NC}"
echo -e "${GREEN}      DEPLOYMENT COMPLETED SUCCESSFULLY!                            ${NC}"
echo -e "${BLUE}====================================================================${NC}"
echo -e "VOLTA MIS is now running in production mode!"
echo -e "Access URL:        http://${DOMAIN_NAME:-YOUR_SERVER_IP}"
echo -e "Service Status:    systemctl status volta-mis"
echo -e "Application Logs:  tail -f $APP_DIR/logs/app.log"
echo -e "Default Admin:     admin / Admin@12345"
echo -e "${BLUE}====================================================================${NC}"
