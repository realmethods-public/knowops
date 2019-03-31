#set( $appName = $aib.getApplicationNameFormatted() )
#set( $description = $aib.getParam("application.description") )
#set( $version = $aib.getParam("application.version") )
#set( $author = $aib.getParam("application.author") )
#set( $licenseUrl = $aib.getParam("nuget.licenseUrl") )
#set( $projectUrl = $aib.getParam("nuget.projectUrl") )
#set( $iconUrl = $aib.getParam("nuget.iconUrl") )
#set( $requireLicenseAcceptance = $aib.getParam("nuget.requireLicenseAcceptance") )
#set( $releaseNotes = $aib.getParam("nuget.releaseNotes") )
#set( $copyRight = $aib.getParam("nuget.copyRight") )
#set( $tags = $aib.getParam("nuget.tags") )
This file uses markdown conventions. Rename it to readme.md for better viewing within a markdown viewer.

${esc.hash}${esc.hash}Application - 
$appName

${esc.hash}${esc.hash}${esc.hash} Description
$description

${esc.hash}${esc.hash}${esc.hash} Version
$version

${esc.hash}${esc.hash}${esc.hash} Author(s)
$author

${esc.hash}${esc.hash}${esc.hash} Important Urls
**license** :  $licenseUrl
**project** : $projectUrl

${esc.hash}${esc.hash}${esc.hash} Release Notes
$releaseNotes

${esc.hash}${esc.hash}${esc.hash} Tags
$tags

${esc.hash}${esc.hash}${esc.hash} $copyRight
