#if ( ${aib.getParam('jfrog.inUse')} == "true" )
#set( $appName = ${aib.getApplicationName()} )
#!/bin/bash
echo reset

echo cd to $appName directory
cd $appName

echo upload archive to jFrog Artifactory
python setup.py sdist upload -r jfrogRepo
#end##if ( ${aib.getParam('jfrog.inUse')} == "true" )

