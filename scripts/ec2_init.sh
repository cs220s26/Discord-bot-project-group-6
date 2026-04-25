#!/bin/bash
echo "Initializing EC2 instance for Discord bot deployment"

echo "Installing dependencies: Java 21, Git, Redis 6"
sudo yum install maven-amazon-corretto21 git redis6 -y

echo "Cloning Discord bot project repository"
cd /home/ec2-user
# Clone into the user home directory instead of / root
git clone https://github.com/cs220s26/Discord-bot-project-group-6.git

echo "Packaging the bot application with Maven"
cd /home/ec2-user/Discord-bot-project-group-6
mvn package

echo "Starting Redis server"
sudo systemctl enable redis6
sudo systemctl start redis6

echo "Putting service file in systemd"
# Copy from the project folder to the system folder
sudo cp deploy/typingracebot.service /etc/systemd/system/
sudo systemctl daemon-reload
sudo systemctl enable typingracebot
sudo systemctl restart typingracebot

echo "Checking service status"
sudo systemctl status typingracebot
