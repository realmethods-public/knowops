#set( $class = $classObject )
#set( $className = $class.getName() )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter(${className})} )
#set( $appName = $aib.getApplicationNameFormatted() )
#set( $lowercaseAppName = ${Utils.lowercaseFirstLetter(${appName})} )
from django.core import exceptions
from django.core import serializers
from django.db import models
from django.db import utils

from ${lowercaseAppName}.models.${className} import ${className}
#foreach( $type in $classObject.getAssociationTypes( $className ) )
from ${lowercaseAppName}.models.${type} import ${type}
#end
from ${lowercaseAppName}.exceptions import Exceptions

#if( $class.hasDocumentation() == false )
#set( $doc = "Encapsulates data for model ${className}")
#else
#set( $doc = $class.getDocumentation() )
#end ##if( $class.hasDocumentation() == false )
#======================================================================
# 
# $doc
#
# @author $aib.getAuthor()
#
#======================================================================

#======================================================================
# Class ${className}Delegate Declaration
#======================================================================
class ${className}Delegate :

#======================================================================
# Function Declarations
#======================================================================

	def get(self, ${lowercaseClassName}Id ):
		try:	
			${lowercaseClassName} = ${className}.objects.filter(id=${lowercaseClassName}Id)
			return ${lowercaseClassName}.first();
		except ${className}.DoesNotExist:
			raise ProcessingError("${className} with id " + str(${lowercaseClassName}Id) + " does not exist.")
		except utils.DatabaseError:
			raise StorageReadError()
		except Exception:
			raise GeneralError(errMsg) 

	def createFromJson(self, ${lowercaseClassName}):
		for model in serializers.deserialize("json", ${lowercaseClassName}):
			model.save()
			return model;

	def create(self, ${lowercaseClassName}):
		${lowercaseClassName}.save()
		return ${lowercaseClassName};

	def saveFromJson(self, ${lowercaseClassName}):
		for model in serializers.deserialize("json", ${lowercaseClassName}):
			model.save()
			return ${lowercaseClassName};
	
	def save(self, ${lowercaseClassName}):
		${lowercaseClassName}.save()
		return ${lowercaseClassName};
	
	def delete(self, ${lowercaseClassName}Id ):
		errMsg = "Failed to delete ${className} from db using id " + str(${lowercaseClassName}Id)
		
		try:
			${lowercaseClassName} = ${className}.objects.get(id=${lowercaseClassName}Id)
			${lowercaseClassName}.delete()
			return True
		except ${className}.DoesNotExist:
			raise ProcessingError("${className} with id " + str(${lowercaseClassName}Id) + " does not exist.")
		except utils.DatabaseError:
			raise StorageReadError()
		except Exception:
			raise GeneralError(errMsg) 
	
	def getAll(self):
		try:
			all = ${className}.objects.all()
			return all;
		except utils.DatabaseError:
			raise StorageReadError("Failed to get all ${className} from db")
		except Exception:
			return None;
		
#set( $includeComposites = false )
#foreach( $singleAssociation in $classObject.getSingleAssociations( ${includeComposites} ) )
#set( $roleName = $singleAssociation.getRoleName() )
#set( $lowercaseRoleName = ${Utils.lowercaseFirstLetter(${roleName})} )
#set( $childName = $singleAssociation.getType() )
#set( $lowercaseChildName = ${Utils.lowercaseFirstLetter(${childName})} )
#set( $parentName  = $classObject.getName() )
	def assign${roleName}( self, ${lowercaseClassName}Id, ${lowercaseRoleName}Id ):
		# lazy importing avoids circular dependencies
		from ${lowercaseAppName}.delegates.${childName}Delegate import ${childName}Delegate

		errMsg = "Failed to assign element " + str(${lowercaseRoleName}Id) + " for $roleName on $className"

		try:
			# get the ${className} from db
			${lowercaseClassName} = self.get( ${lowercaseClassName}Id ).first()	
			
			# get the ${childName} from db
			$lowercaseChildName = ${childName}Delegate().get(${lowercaseRoleName}Id).first();
			
			# assign the $roleName		
			${lowercaseClassName}.$lowercaseRoleName = $lowercaseChildName
			
			#save it
			${lowercaseClassName}.save()

			# reload and return the appropriate version					
			return self.get( ${lowercaseClassName}Id );
		except ${className}.DoesNotExist:
			raise ProcessingError(errMsg + " : ${className} with id " + str(${lowercaseClassName}Id) + " does not exist.")
		except ${childName}.DoesNotExist:
			raise ProcessingError(errMsg + " : ${childName} with id " + str(${lowercaseRoleName}Id) + " does not exist.")
		except Exception:
			return None;
				
	def unassign${roleName}( self, ${lowercaseClassName}Id ):
		errMsg = "Failed to unassign element " + str(${lowercaseRoleName}Id) + " for $roleName on $className"

		try:
			# get the ${className} from db
			${lowercaseClassName} = self.get( ${lowercaseClassName}Id ).first()	
			
			# assign to None for unassignment
			${lowercaseClassName}.${lowercaseChildName} = None			

			#save it
			${lowercaseClassName}.save()

			# reload and return the appropriate version					
			return self.get( ${lowercaseClassName}Id );
		except ${className}.DoesNotExist:
			raise ProcessingError(errMsg + " : ${className} with id " + str(${lowercaseClassName}Id) + " does not exist.")
		except Exception:
			return None;
		
#end##foreach( $singleAssociation in $classObject.getSingleAssociations( ${includeComposites} ) )
#foreach( $multiAssociation in $classObject.getMultipleAssociations() )
#set( $roleName = $multiAssociation.getRoleName() )
#set( $lowercaseRoleName = ${Utils.lowercaseFirstLetter(${roleName})} )
#set( $childName = $multiAssociation.getType() )
#set( $lowercaseChildName = ${Utils.lowercaseFirstLetter(${childName})} )
#set( $parentName  = $classObject.getName() )
	def add${roleName}( self, ${lowercaseClassName}Id, ${lowercaseRoleName}Ids ):
		# lazy importing avoids circular dependencies
		from ${lowercaseAppName}.delegates.${childName}Delegate import ${childName}Delegate

		errMsg = "Failed to add elements " + str(${lowercaseRoleName}Ids) + " for $roleName on $className"

		try:
			# get the ${className}
			${lowercaseClassName} = self.get( ${lowercaseClassName}Id ).first()
				
			# split on a comma with no spaces
			idList = ${lowercaseRoleName}Ids.split(',')

			
			# iterate over ids
			for id in idList:
				# read the ${childName}		
				$lowercaseChildName = ${childName}Delegate().get(id).first();	
				# add the ${childName}
				${lowercaseClassName}.${lowercaseRoleName}.add($lowercaseChildName)
				
			# save it		
			${lowercaseClassName}.save()
			
			# reload and return the appropriate version
			return self.get( ${lowercaseClassName}Id );
		except ${className}.DoesNotExist:
			raise ProcessingError(errMsg + " : ${className} with id " + str(${lowercaseClassName}Id) + " does not exist.")
		except ${childName}.DoesNotExist:
			raise ProcessingError(errMsg + " : ${childName} does not exist.")
		except Exception:
			raise ProcessingError(errMsg) 
		
	def remove${roleName}( self, ${lowercaseClassName}Id, ${lowercaseRoleName}Ids ):
		# lazy importing avoids circular dependencies
		from ${lowercaseAppName}.delegates.${childName}Delegate import ${childName}Delegate

		errMsg = "Failed to remove elements " + str(${lowercaseRoleName}Ids) + " for $roleName on $className"

		try:
			# get the ${className}
			${lowercaseClassName} = self.get( ${lowercaseClassName}Id ).first()
				
			# split on a comma with no spaces
			idList = ${lowercaseRoleName}Ids.split(',')
			
			# iterate over ids
			for id in idList:
				# read the ${childName}		
				$lowercaseChildName = ${childName}Delegate().get(id).first();	
				# add the ${childName}
				${lowercaseClassName}.${lowercaseRoleName}.remove($lowercaseChildName)
				
			# save it		
			${lowercaseClassName}.save()
			
			# reload and return the appropriate version
			return self.get( ${lowercaseClassName}Id );
		except ${className}.DoesNotExist:
			raise ProcessingError(errMsg + " : ${className} with id " + str(${lowercaseClassName}Id) + " does not exist.")
		except ${childName}.DoesNotExist:
			raise ProcessingError(errMsg + " : ${childName} does not exist.")
		except utils.DatabaseError:
			raise StorageWriteError()
		except Exception:
			raise GeneralError(errMsg) 
		
#end##foreach( $multiAssociation in $classObject.getMultipleAssociations() )