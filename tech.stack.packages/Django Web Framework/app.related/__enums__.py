from django.db import models
#set( $class = $classObject )
#set( $className = $class.getName() )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter(${className})} )
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
# Class $className Declaration (enumerated type)
#======================================================================
#getClassDecl( $class "")
#set( $attributes = $class.getAttributes() )
#foreach ( $attribute in $attributes )
	${attribute.getName()} = '${attribute.getName()}'
#end