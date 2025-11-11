#!/bin/bash

set -e  # Stop on error

BUCKET_NAME="sm-jar-bucket"
APP_DIR="/opt/springapp"
JAR_NAME="app-latest.jar"
APP_NAME="app.jar"

echo "---- Downloading latest application JAR from S3 ----"
aws s3 cp s3://$BUCKET_NAME/$JAR_NAME $APP_DIR/$JAR_NAME

echo "---- Stopping Spring service ----"
sudo systemctl stop springapp || true

echo "---- Replacing old JAR ----"
mv $APP_DIR/$JAR_NAME $APP_DIR/$APP_NAME

echo "---- Setting permissions ----"
chmod 755 $APP_DIR/$APP_NAME

echo "---- Starting Spring service ----"
sudo systemctl start springapp

echo "---- Deployment complete ----"
