# KnowOps : The First and Only DevOps Project Generator

Developers use **KnowOps** to generate DevOps project for both new application and app refactoring scenarios.

Save time and get started with all the code and configuration you need but don't want to have to write.  

With support for many of today's popular tech stacks and toolchains, generation includes:

- All tech stack specific source code (UI, MVC, Serverless, Persistence, Caching, etc...)
- Build file
- Auto commit to a specified Git repository
- YAML configuration for all major CI/CD platforms (Jenkins, CircleCI, Codeship, AWS Codepipelines, Azure Pipelines, Bitbucket pipelines, Gitlab pipelines, and others)
- optional Docker image creation
- Terraform files for infrastructure provision, including Kubernetes

## Getting Started

These instructions will cover usage information for this **KnowOps** Docker image.

### Prerequisities

In order to run this container you'll need Docker installed and optionally Docker compose.

* [Windows](https://docs.docker.com/windows/started)
* [OS X](https://docs.docker.com/mac/started/)
* [Linux](https://docs.docker.com/linux/started/)

Instructions to download Docker Compose are found [here](https://docs.docker.com/compose/install/).

### Access

Before using the **KnowOps** image, it has to first be pulled by issuing the following command:

```
docker pull realmethods/knowops:latest
```

### Usage
There are 2 ways to consider using the platform.  

1. The first is using a Docker Compose file that is self contained with MySQL8, Tomcat 9, and the **KnowOps** platform.  
2. The second is to run the image directly, providing it with the URL (or IP Address) to a running MySQL8 instance.  

Both options are further explained below.

#### Option 1 - Run Via Docker Compose

Download the pre-configured Docker Compose file [here](https://github.com/realmethods-public/knowops/blob/master/docker-compose.yml).

This file provides the necessary directives to install and run MySQL8, Tomcat 9, and the current version of the **KnowOps** Docker image.

Within the directory where the docker-compose.yml file was downloaded to,  issue the following to run the **KnowOps** platform:

```
docker-compose up
```

#### Option 2 
When running directly as a Docker image, connection information is required to an existing running MySQL-8 instance.  Provide the connection parameters to the instance as follows:

```
docker run -it -d -p 8080:8080 
-e DATABASE_URL=THE-MYSQL-URL
-e DATABASE_USERNAME=THE-MYSQL-USER-NAME
-e DATABASE_PASSWORD=THE-MYSQL-PASSWORD
realmethods/knowops:latest
```
When assigning the DATABASE_URL, it is only necessary to assign the IP address of a=the MySQL instance.  The default port of 3306 is assumed.  


##### Run via Docker as Default

To run the image with its defaults, issue the following command:

```
docker run -it -d -p 8080:8080 realmethods/knowops:latest

```

##### Run via Docker with Data Storage to a Local Volume

To run the image using a local volume on the hosting server, issue the following command:

```
docker run -it -d -p 8080:8080 -v /usr/realmethods-data:/realmethods-data realmethods/knowops:latest
```

Directory /usr/realmethods-data must exist for this command to execute successfully.  

### Access The Installation

The application should be running and accessible on the exposed port.  For example:

http://xxx.xxx.xxx.xxx:8080

where xxx.xxx.xxx.xxx is the IP address to the host server of the application.

#### Optional Environment Variables

Required when connecting to a separate MySQL-8 instance.

* `DATABASE_URL` - URL where the MySQL instance is located.  Likely of the form xxx.xxx.xxx:3306
* `DATABASE_USERNAME` - User name with read/write privileges
* `DATABASE_PW` - Password for the user name

#### Volumes

* `/user/realmethods-data` - Optional location on the hosting server where application data will be written to.

#### Useful File Locations

* `/usr/local/tomcat` - Root of the Apache Tomcat 9 installation
  
* `/usr/local/tomcat/webapps/ROOT` - Root directory of the realMethods installation


## Find Us

* [realmethods.com](http://www.realmethods.com)
* [github.com](https://github.com/realmethods-public)


## Authors

* **realMethods**  

## Contributing

We are always interested in supporting new tech stacks.  If you are an expert in something (whether popular or obscure) and want to capture it as a reusable technology stack, contact us at support@realmethods.com.
 
## License

This project is licensed under the MIT License.

## Acknowledgments

* A huge thanks to all the early beta users that have helped to refine and battle test the platform into today's production release. 
