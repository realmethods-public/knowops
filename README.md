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

These instructions will cover hopw to build the KnowOps platform into a deployable WAR file.

### Prerequisities

In order to build KnowOps you will need the following installed:

* (Java)[https://www.oracle.com/java/technologies/javase-downloads.html] (minimum 8) {Required}
* [Apache Maven](https://maven.apache.org/) (minimum 3.3.9) {Required}
* An IDE (ex: Eclipse) "Suggested"

### Build
To build the KnowOps project, first verify Java is installed:

```
java -version
```

Then verify Apache Maven is installed:

```
mvn --version
``` 

Once both are verified, build the KnowOps project by issuing the following command in the root directory of the install:

```
mvn package
```

The result of the build is a WAR file located in the **target** directory.  The WAR file can be deployed to any Servlet Engine, but development has been done solely on [Apache Tomcat 9+](http://tomcat.apache.org/)

## Find Us

* [LnowOps](http://www.knowops.dev)
* [realMethods](http://www.realmethods.com)
* [github](https://github.com/realmethods-public/knowops)


## Authors

* **realMethods**  

## Contributing

We welcome your willingness help us make KnowOps the first choice of developers starting a new project or migrating an existing one.  Please contact support at support@realmethods.com.
 
## License

This project is licensed under the MIT License.

## Acknowledgments

* A huge thanks to all the early beta users that have helped to refine and battle test the platform into today's production release. 
