#!/bin/bash

set -e

# Variables injectées depuis Terraform
db_endpoint="${db_endpoint}"
db_user="${db_user}"
db_name="${db_name}"
db_password="${db_password}"
alb_domain="${alb_domain}"

echo "===== Installation des dépendances ====="
sudo apt update && sudo apt upgrade -y
sudo apt install -y git openjdk-17-jdk nginx

echo "===== Clonage du repo ====="
cd /opt
git clone https://github.com/Sougoumay/social_media
cd social_media
git checkout deploy-ec2-alb-rds
chmod +x mvnw
./mvnw -DskipTests package

echo "===== Configuration Spring App ====="
sudo mkdir -p /etc/springapp
sudo tee /etc/springapp/springapp.env > /dev/null <<EOF
DB_USER=${db_user}
DB_PASSWORD=${db_password}
DB_HOST=${db_endpoint}
DB_NAME=${db_name}
DB_PORT=3306
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080
EOF

sudo mkdir -p /opt/springapp
sudo cp /opt/social_media/target/*.jar /opt/springapp/app.jar
sudo chown -R ubuntu:ubuntu /opt/springapp

sudo tee /etc/systemd/system/springapp.service > /dev/null <<EOF
[Unit]
Description=Spring Boot Social Media App
After=network.target

[Service]
User=ubuntu
WorkingDirectory=/opt/springapp
EnvironmentFile=/etc/springapp/springapp.env
ExecStart=/usr/bin/java -jar /opt/springapp/app.jar
SuccessExitStatus=143
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
EOF

sudo systemctl daemon-reload
sudo systemctl enable springapp
sudo systemctl start springapp

echo "===== Configuration Nginx ====="
sudo tee /etc/nginx/sites-available/socialmedia > /dev/null <<EOF
server {
    listen 80;
    server_name ${alb_domain};

    location / {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
    }
}
EOF

sudo rm -f /etc/nginx/sites-enabled/default
sudo ln -s /etc/nginx/sites-available/socialmedia /etc/nginx/sites-enabled/
sudo nginx -t && sudo systemctl reload nginx

echo "===== Installation terminée avec succès ====="