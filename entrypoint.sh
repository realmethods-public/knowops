#!/bin/bash

service mysql start 
mysql -e "SET PASSWORD FOR 'root'@'localhost'=PASSWORD('6969Cutlass!!')"
export M2_HOME=/usr/share/maven
/usr/local/tomcat/bin/startup.sh
