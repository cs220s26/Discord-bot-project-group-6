#!/bin/bash
cd /home/ec2-user/Discord-bot-project-group-6
git pull
mvn package
sudo systemctl restart typingracebot
