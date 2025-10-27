#!/bin/bash

set -e

# Variables injectées depuis Terraform
db_endpoint="${db_endpoint}"
db_user="${db_user}"
db_name="${db_name}"
db_password="${db_password}"
alb_domain="${alb_domain}"

echo "===== Installation des dépendances ====="
apt update && apt upgrade -y
apt install -y git openjdk-17-jdk nginx

echo "===== Clonage du repo ====="
cd /opt
git clone https://github.com/Sougoumay/social_media/tree/deploy-ec2-alb-rds
cd social_media
chmod +x mvnw
./mvnw -DskipTests package


echo "===== Configuration Spring App ====="
mkdir -p /etc/springapp
cat <<EOF > /etc/springapp/springapp.env
DB_USER=${db_user}
DB_PASSWORD=${db_password}
DB_HOST=${db_endpoint}
DB_NAME=${db_name}
DB_PORT=3306
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080
EOF

mkdir -p /opt/springapp
cp target/*.jar /opt/springapp/app.jar

cat <<EOF > /etc/systemd/system/springapp.service
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

systemctl daemon-reload
systemctl enable springapp
systemctl start springapp

echo "===== Configuration Nginx ====="
cat <<EOF > /etc/nginx/sites-available/socialmedia
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

rm -f /etc/nginx/sites-enabled/default
ln -s /etc/nginx/sites-available/socialmedia /etc/nginx/sites-enabled/
nginx -t
systemctl restart nginx

echo "Installation terminée"