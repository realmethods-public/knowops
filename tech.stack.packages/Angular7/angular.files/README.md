# realMethods

This Angular/MongoDB (not AngularJS) project was generated with realMethods version ${aib.getPlatformVersion()}.

The Angular technology stack package it was generated from has been tested with Angular version 7.2.0.

## To get started

The following instructions assume NPM is installed.  If not, you can download it by installing Node.js at
[Node.js Download Page](https://nodejs.org/en/download/)

### Install the Angular CLI

The realMethods Angular technology stack was developed and tested for Angular 7.

If you have an older version of Angular, you should consider un-installing it in favor of Angular v7:

`npm uninstall -g @angular/cli`
`npm cache verify`

To install Angular, issue the following:

`npm install -g @angular/cli`

Verify the installation with `ng --version`

### Create a new project by the name of ${aib.getApplicationName()}.  

**Note: It is important to use this name because the next step expects this name.**

`ng new ${aib.getApplicationName()} --defaults`

The default application files and packages will be installed.  The next steps will copy the generated files, overwrite some of the default files and install 
the remaining packages.

## Copying generated files

Either pull the generated files from your Git repository, 
or download the generated application archive file and unzip into the parent directory of the **${aib.getApplicationName()}** directory
in order to overlay the generated files into it. If asked to replace existing files, answer _Yes_.

## Required extras to install

Change to the **${aib.getApplicationName()}** directory and install the remaining required packages:

`npm run setup`

#if ( ${aib.getParam('jfrog.inUse')} == "true" || ${aib.getParam('nexus.inUse')} == "true" )
## Publish to Repository
This command will do a build on the project and publish the build to an assigned NPM repository managed within the either JFrog Artifactory or Nexus Sonatype.

`npm run deploy`

During this phase, a login to the repository is attempted requiring your attention.  Make sure to enter your credentials correctly, as well as the public email assigned
to the user within.

Once complete, the build will be available within the named NPM Artifactory repository.
#end##if ( ${aib.getParam('jfrog.inUse')} == "true" || ${aib.getParam('nexus.inUse')} == "true" )

## MongoDB setup

It is assumed MongoDB is running at:

`mongodb://${aib.getParam('mongodb.server address')}/${aib.getParam('mongodb.database name')}`

See file ./config/mongoDb.js to make changes to the database location.

## Mongoose startup

The application leverages Mongoose to assist in persisting data to MongoDB.

From the ${aib.getApplicationName()} directory, start mongoose:

`node server.js`

If it runs successfully, mongoose is listening on port 4000.

## Development server

You now have a fully functional application to use, test, and build out.

To run the dev server:

`ng serve`

Using your browser, navigate to: 

`http://localhost:4200/`. 

The app will automatically reload if you change any of the source files.

## Running unit tests

Run `ng test` to execute the unit tests via [Karma](https://karma-runner.github.io).

## Running end-to-end tests

Run `ng e2e` to execute the end-to-end tests via [Protractor](http://www.protractortest.org/).

## Build

Run `ng build` to build the project. The build artifacts will be stored in the `dist/` directory. Use the `--prod` flag for a production build.

Learn more at [Angular Build](https://angular.io/cli/build/)

## Further help

To get more help on the Angular CLI use `ng help` or go check out the [Angular CLI README](https://github.com/angular/angular-cli/blob/master/README.md).
