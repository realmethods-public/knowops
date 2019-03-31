from django.db import models
#set( $class = $classObject )## classObject is from the global context
#set( $className = $class.getName() )
#if( $classObject.hasParent() == true )
from .$classObject.getParentName() import $classObject.getParentName()
#end##if( $classObject.hasParent() == true )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter(${className})} )
#if( $class.hasDocumentation() == false )
#set( $doc = "Encapsulates data for model ${className}")
#else
#set( $doc = $class.getDocumentation() )
#end##if( $class.hasDocumentation() == false )
#======================================================================
# 
# $doc
#
# @author $aib.getAuthor()
#
#======================================================================

#======================================================================
# Class $className Declaration
#======================================================================
#set( $suffixToAdd = "" )
#getDjangoClassDecl( $class $suffixToAdd )

#======================================================================
# attribute declarations
#======================================================================
#getDjangoAttributeDeclarations( $class true )

#======================================================================
# function declarations
#======================================================================
#getDjangoToString( $class false )
    
	def __str__(self):
		return self.toString();

	def identity(self):
		return "${className}";
    
	def objectType(self):
		return "${className}";
