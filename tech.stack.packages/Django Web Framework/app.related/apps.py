from django.apps import AppConfig

#set( $appName = $aib.getApplicationNameFormatted() )
#set( $lowercaseAppName = ${Utils.lowercaseFirstLetter(${appName})} )
class ${appName}Config(AppConfig):
    name = '${lowercaseAppName}'
