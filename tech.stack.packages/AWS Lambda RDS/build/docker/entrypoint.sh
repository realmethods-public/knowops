#set( $appName = "${aib.getApplicationNameFormatted()}-restful-dao-layer" )
#!/bin/bash

java -jar -Dserver.port=8000 ${appName}-${aib.getVersion()}.jar