echo "Initializing EC2 instance for Discord bot deployment"
echo "Installing dependencies: Java 21, Git, Redis 6"
yum install maven-amazon-corretto21 git redis6 -y
echo "Cloning Discord bot project repository"
git clone https://github.com/cs220s26/Discord-bot-project-group-6.git /home/ec2-user/Discord-bot-project-group-6
echo "Packaging the bot application with Maven"
cd /home/ec2-user/Discord-bot-project-group-6
mvn package
echo "Starting Redis server"
systemctl enable redis6
systemctl start redis6
echo "Putting service file in systemd"
cp deploy/typingracebot.service /etc/systemd/system/
systemctl daemon-reload
sudo systemctl enable typingracebot
sudo systemctl start typingracebot
echo "Checking service status"
sudo systemctl status typingracebot
journalctl -u typingracebot.service -f
