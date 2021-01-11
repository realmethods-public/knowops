#set( $appName = ${aib.getApplicationName()} )
#!/bin/bash
echo reset
#if ( ${aib.getParam('jfrog.inUse')} == "true" )
#set( $repoName = "jfrogRepo" )

echo cd to $appName directory
cd $appName
echo upload archive to ${repoName} Artifactory
python setup.py sdist upload -r ${repoName}

#elseif( ${aib.getParam('nexus.inUse')} == "true" )
#set( $repoName = "nexusRepo" )
echo cd to $appName directory
cd $appName
echo installing twine
pip install twine
echo creating distribution
python setup.py sdist
echo upload archive to ${repoName} repository
twine upload -r nexusRepo dist/*.gz
#end##if ( ${aib.getParam('jfrog.inUse')} == "true" )
