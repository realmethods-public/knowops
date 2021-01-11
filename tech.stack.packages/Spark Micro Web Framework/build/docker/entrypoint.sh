#if( ${containerObject} )
#set( $appName = "Container-${containerObject.getName()}" )
#else
#set( $appName = $aib.getApplicationNameFormatted() )
#end
#!/bin/bash

java -jar -Dserver.port=8000 ${appName}-${aib.getVersion()}.jar