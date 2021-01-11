##--------------------------------------------------------------
##
##  realMethods Confidential
##  
##  2021 realMethods, Inc.
##  All Rights Reserved.
##  
##  This file is subject to the terms and conditions defined in
##  file 'license.txt', which is part of this source code package.
##   
##  Contributors :
##        realMethods Inc - General Release
##--------------------------------------------------------------
FROM tomcat:9.0

RUN apt-get update --fix-missing && \
apt-get install -y maven=3.3.9 && \
apt-get install -y git && \
apt-get install -y dos2unix && 
apt-get install -y nano

######################################################
##  final session prep
######################################################
COPY /target/*.war /usr/local/tomcat/webapps
COPY C:\Users\srand\realmethods\tools\apache\apache-tomcat-9.0.14\conf\catalina.properties /usr/local/tomcat/conf
COPY C:\Users\srand\realmethods\tools\apache\apache-tomcat-9.0.14\conf\server.xml /usr/local/tomcat/conf

ARG appName=realmethods
## ARG version=1.2
# make the app war the root war so all default requests are directed to it
RUN mv /usr/local/tomcat/webapps/${appName}.war /usr/local/tomcat/webapps/ROOT.war
RUN mv /usr/local/tomcat/webapps/ROOT /usr/local/tomcat/webapps/ROOT_OLD

# run tomcat
CMD ["/usr/local/tomcat/bin/catalina.sh", "run"]
