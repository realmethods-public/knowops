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
FROM tomcat:9.0-jdk8

RUN apt-get update --fix-missing && \
apt-get install -y maven=3.3.9 && \
apt-get install -y git && \
apt-get install -y dos2unix && 
apt-get install -y nano

######################################################
##  final session prep
######################################################
ARG tomcatRoot=/usr/local/tomcat
ARG appName=realmethods
## ARG version=1.2

COPY ${tomcatRoot}/webapps/${appName}.war ${tomcatRoot}/webapps
COPY ${tomcatRoot}/conf/catalina.properties ${tomcatRoot}/conf
COPY ${tomcatRoot}/conf/server.xml ${tomcatRoot}/conf

# make the app war the root war so all default requests are directed to it
RUN mv ${tomcatRoot}/webapps/${appName}.war ${tomcatRoot}/webapps/ROOT.war
RUN mv ${tomcatRoot}/webapps/ROOT ${tomcatRoot}/webapps/ROOT_OLD

# run tomcat
CMD ["${tomcatRoot}/bin/catalina.sh", "run"]
