##--------------------------------------------------------------
##
##  realMethods Confidential
##  
##  2018 realMethods, Inc.
##  All Rights Reserved.
##  
##  This file is subject to the terms and conditions defined in
##  file 'license.txt', which is part of this source code package.
##   
##  Contributors :
##        realMethods Inc - General Release
##--------------------------------------------------------------
FROM ubuntu:16.04

RUN mkdir /home/realmethods/
RUN mkdir /home/realmethods/src
RUN mkdir /home/realmethods/src/main
RUN mkdir /home/realmethods/target

COPY pom.xml /home/realmethods

ADD ./src/main/webapp.tar /home/realmethods/src/main
ADD target.tar /home/realmethods

######################################################
##	dos2unix used to clean up the trailing EOF 
######################################################
RUN apt-get update && apt-get install -y dos2unix

######################################################
##	install CURL to ping for AWS EC2 instance info
######################################################
RUN apt-get update && apt-get install -y curl

######################################################
# installing common packages
######################################################
RUN apt-get update && \
apt-get update --fix-missing && \
apt-get install -y wget && \
apt-get install -y nano && \
apt-get install -y git

######################################################
##  install java 8
######################################################
RUN apt-get update && \
    apt-get upgrade -y && \
    apt-get install -y  software-properties-common && \
    add-apt-repository ppa:webupd8team/java -y && \
    apt-get update && \
    echo oracle-java7-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections && \
    apt-get install -y oracle-java8-installer && \
    apt-get clean

######################################################
##  install maven
######################################################
RUN apt-get update
RUN apt-get install -y maven
RUN cd /home/realmethods

EXPOSE 8080

######################################################
##  install mysql
######################################################
RUN groupadd mysql
RUN useradd -g mysql mysql
RUN mkdir -p /var/run/mysqld
RUN chown mysql:mysql /var/run/mysqld

RUN apt-get update && \
  DEBIAN_FRONTEND=noninteractive apt-get install -y mysql-server && \
  rm -rf /var/lib/apt/lists/* && \
  sed -i -e 's/^bind-address\s*=\s*127.0.0.1/bind-address = 0.0.0.0/' /etc/mysql/my.cnf && \
  #sed -i 's/^\(bind-address\s.*\)/# \1/' /etc/mysql/my.cnf && \
  sed -i 's/^\(log_error\s.*\)/# \1/' /etc/mysql/my.cnf

VOLUME ["/etc/mysql", "/var/lib/mysql"]

# Define working directory.
#WORKDIR /data

EXPOSE 3306

######################################################
##  final session prep
######################################################

RUN cd /home/realmethods
WORKDIR /home/realmethods
RUN pwd
ADD entrypoint.sh /home/realmethods/
RUN dos2unix /home/realmethods/entrypoint.sh
RUN chmod +x entrypoint.sh

ENTRYPOINT ["/home/realmethods/entrypoint.sh"]
