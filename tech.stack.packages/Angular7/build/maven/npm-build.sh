#set( $appName = ${aib.getApplicationName()} )
#!/bin/bash
## if no cicd selected, handle on the realMethods server
## otherwise this is handled on the CI/CD platform
#if ( $cicd == $null || $cicd == "" )
echo reset

echo create and cd to build directory
mkdir build
cd build

echo create ${appName} Angular8 project
ng new ${appName} --defaults

echo copy generated files into the ${appName} project directory
cp -r ../${appName}/. ./${appName}/

echo changing to the project directory
cd ${appName}

echo install the remaining required packages
npm run setup

#if ( ${aib.getParam('artifact-repo.inUse')} == "true" )
echo publish to artifact repository
npm run deploy
#end##if ( ${aib.getParam('artifact-repo.inUse')} == "true" )
#end##if ( $cicd == $null || $cicd == "" ) 

