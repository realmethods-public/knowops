from django.urls import path
#set( $appName = $aib.getApplicationNameFormatted() )
#set( $lowercaseAppName = ${Utils.lowercaseFirstLetter(${appName})} )
#set( $className = $classObject.getName() )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter(${className})} )
from ${lowercaseAppName}.views import ${className}View

urlpatterns = [
    path('', ${className}View.index, name='index'),
	path('create', ${className}View.get, name='create'),
	path('get/<int:${lowercaseClassName}Id>/', ${className}View.get, name='get'),
	path('save', ${className}View.save, name='save'),
	path('getAll', ${className}View.getAll, name='getAll'),
	path('delete/<int:${lowercaseClassName}Id>/', ${className}View.delete, name='delete'),
#set( $includeComposites = false )
#foreach( $singleAssociation in $classObject.getSingleAssociations( ${includeComposites} ) )
#set( $roleName = $singleAssociation.getRoleName() )
#set( $childName = $singleAssociation.getType() )
#set( $parentName  = $className )
	path('assign${roleName}/<int:${lowercaseClassName}Id>/<int:${roleName}Id>/', ${className}View.assign${roleName}, name='assign${roleName}'),
	path('unassign${roleName}/<int:${lowercaseClassName}Id>/', ${className}View.unassign${roleName}, name='unassign${roleName}'),
#end##foreach( $singleAssociation in $classObject.getSingleAssociations( ${includeComposites} ) )
#foreach( $multiAssociation in $classObject.getMultipleAssociations() )
#set( $roleName = $multiAssociation.getRoleName() )
#set( $childName = $multiAssociation.getType() )
#set( $parentName  = $className )
	path('add${roleName}/<int:${lowercaseClassName}Id>/<${roleName}Ids>/', ${className}View.add${roleName}, name='add${roleName}'),
	path('remove${roleName}/<int:${lowercaseClassName}Id>/<${roleName}Ids>/', ${className}View.remove${roleName}, name='remove${roleName}'),
#end##foreach( $multiAssociation in $classObject.getMultipleAssociations() )
]
