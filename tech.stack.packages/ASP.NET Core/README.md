# realMethods

This ASP.NET web application project was generated with realMethods version ${aib.getPlatformVersion()}.

The ASP.NET technology stack package it was generated from has been tested with Visual Studio 2017.

## Create a New Project

In Visual Studio, create an ASP.NET Core 2.0 project by the name of ${aib.getApplicationName()} that uses the .NET Framework. (not the .NET Core).  


## Option 1 - Using NuGet

The following steps require the use of NuGet to package and publish the generated application files.


### Step 1 - Package Application with NuGet

Assuming nuget is installed, issue the following command within the root directory of the generated application files:

`nuget sources Add -Name __<name_of_repository>__ -Source __<url_to_the_NuGet_repository>__`

For example, if using the jFrog Artifactory, the command looks like:

`nuget sources Add -Name Artifactory -Source http://<jfrog_artifactory_url>/artifactory/api/nuget/<repository key>`


### Step 2 - NuGet API Key Authentication

NuGet tools require that sensitive operations are authenticated with the server using an apikey. 

`nuget setapikey <user_id>:<password> -Source __<name_of_repository>__`

For example, if using the jFrog Artifactory, the command looks like:

`nuget setapikey <user_id>:<password> -Source Artifactory`


### Step 3 - Package the Application

Next, NuGet requires the application be packaged:

`nuget pack application.nuspec`

This will create a file in the same directory with a __.nupkg__ extension.  You will push this file in the next step


### Step 4 - Push the Application

Finally, the packaged application is ready to be pushed to a NuGet repository:

`nuget push demo.0.0.1.nupkg -Source __<name_of_repository>__`

For example, if using the jFrog Artifactory, the command looks like:

`nuget push demo.0.0.1.nupkg -Source Artifactory`


### Import application

Use NuGet within Visual Studio to load the previously pushed application from the repository.


## Option 2 - Not Using NuGet - copy generated files

Download from GitHub (or copy) the generated application files overwriting into the Visual Studio project normally located at:

__C:\Users\<your_user_name>\source\repos\<project_name>__

Notice if Visual Studio auto-refreshes the project with the generated application files. If the project does not auto-refresh, force it to.  If the files still do not
appear, re-check they were copied into the correct directory.

## Locate hibernate.cfg.xml

Within Visual Studio, locate the __hibernate.cfg.xml__ file, right-click it and assign __Build Action__ to __Embedded Resource__ and 
__Copy to Output__ to __Copy Always__.

  
### Build

Build then run the project.  Visual Studio will launch the app in the browser.

