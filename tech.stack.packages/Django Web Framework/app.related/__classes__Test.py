#set( $class = $classObject )
#set( $className = $class.getName() )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter(${className})} )
#set( $appName = $aib.getApplicationNameFormatted() )
#set( $lowercaseAppName = ${Utils.lowercaseFirstLetter(${appName})} )
import datetime

from django.test import TestCase
from django.utils import timezone
from ${lowercaseAppName}.models.${class.getName()} import ${class.getName()}
from ${lowercaseAppName}.delegates.${class.getName()}Delegate import ${class.getName()}Delegate

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
# Class ${className}Test Declaration
#======================================================================
class ${className}Test (TestCase) :
	def test_crud(self) :
		$lowercaseClassName = ${className}()
#set( $includePKs = false )
#set( $attributes = $classObject.getAttributesOrdered( $includePKs ) )
#foreach( $attribute in $attributes )
#if( $attribute.isFromAssociation() == false || (${attribute.isFromAssociation()} == ${useAssociation}) )
#set( $type = $attribute.getType() )
#if( $type.equalsIgnoreCase("string") )
#set( $defaultValue = "${esc.q}default ${attribute.getAsArgument()} field value${esc.q}" )
#elseif( $type.equalsIgnoreCase("float") || $type.equalsIgnoreCase("double") )
#set( $defaultValue = "25.0" )
#elseif( $type.equalsIgnoreCase("boolean") )
#set( $defaultValue = "False" )
#elseif( $type.equalsIgnoreCase("char") )
#set( $defaultValue = "Z" )
#elseif( $type.equalsIgnoreCase("java.sql.Date") || $type.equalsIgnoreCase("Date") )
#set( $defaultValue = "datetime.datetime.now()" )
#elseif( $type.equalsIgnoreCase("int") || $type.equalsIgnoreCase("integer") || $type.equalsIgnoreCase("short") )
#set( $defaultValue = "22" )
#else
#set( $defaultValue = "${esc.q}default ${attribute.getAsArgument()} field value${esc.q}" )
#end##if( $type.equalsIgnoreCase("string") )
		${lowercaseClassName}.${attribute.getAsArgument()} = ${defaultValue}
#end##if( $attribute.isFromAssociation() == useAssociation )
#end##for each $key in $attributes.keySet()   		
		
		delegate = ${className}Delegate()
		responseObj = delegate.create($lowercaseClassName)
		
		self.assertEqual(responseObj, delegate.get( responseObj.id ))
	
		allObj = delegate.getAll()
		self.assertEqual(allObj.count(), 1 )		
		delegate.delete(responseObj.id)
		
		allObj = delegate.getAll()
		self.assertEqual(allObj.count(), 0 )		


