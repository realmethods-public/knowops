#if ( ${aib.getParam('jfrog.inUse')} == "true" )
#set( $appName = ${aib.getApplicationName()} )
#!/bin/bash

echo reset

echo create and cd to build directory
mkdir build
cd build

echo create ${appName} Angular7 project
ng new ${appName} --defaults

echo copy generated files into the ${appName} project directory
cp -r ../${appName}/. ./${appName}/

echo changing to the project directory
cd ${appName}

echo install the remaining required packages
npm run setup

echo publish to jFrog Artifactory
npm run jfrogpublish

#end##if ( ${aib.getParam('jfrog.inUse')} == "true" )

