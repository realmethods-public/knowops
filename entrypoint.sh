#!/bin/bash

service mysql start 
mysql -e "SET PASSWORD FOR 'root'@'localhost'=PASSWORD('6969Cutlass!!')"
export M2_HOME=/usr/share/maven
awsInstanceId=$(curl http://169.254.169.254/latest/meta-data/instance-id)
echo AWS instance id is $awsInstanceId
mvn jetty:run -DAWSInstanceId=$awsInstanceId