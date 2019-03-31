#!/bin/bash

service mysql start
mysql -e "SET PASSWORD FOR '${aib.getParam("hibernate.hibernate.connection.username")}'@'localhost'=PASSWORD('${aib.getParam("hibernate.hibernate.connection.password")}')"
mvn package