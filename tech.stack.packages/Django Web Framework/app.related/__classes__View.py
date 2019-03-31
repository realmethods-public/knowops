#set( $class = $classObject )
#set( $className = $class.getName() )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter(${className})} )
#set( $appName = $aib.getApplicationNameFormatted() )
#set( $lowercaseAppName = ${Utils.lowercaseFirstLetter(${appName})} )
import json

from django.core import serializers
from django.shortcuts import render
from django.http import HttpResponse

from ${lowercaseAppName}.delegates.${className}Delegate import ${className}Delegate

#if( $class.hasDocumentation() == false )
#set( $doc = "Encapsulates data for View ${className}")
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
# Class ${className}View function declarations
#======================================================================
def index(request):
	return HttpResponse("Hello, world. You're at the ${classObject.getName()} index.")

def get(request, ${lowercaseClassName}Id ):
	delegate = ${className}Delegate()
	responseData = delegate.get( ${lowercaseClassName}Id )
	asJson = serializers.serialize("json", responseData)
	return HttpResponse(asJson, content_type="application/json");

def create(request):
	$lowercaseClassName = json.loads(request.body)
	delegate = ${className}Delegate()
	responseData = delegate.createFromJson( ${lowercaseClassName} )
	asJson = serializers.serialize("json", responseData)
	return HttpResponse(asJson, content_type="application/json");

def save(request):
	$lowercaseClassName = json.loads(request.body)
	delegate = ${className}Delegate()
	responseData = delegate.save( ${lowercaseClassName} )
	asJson = serializers.serialize("json", responseData)
	return HttpResponse(asJson, content_type="application/json");

def delete(request, ${lowercaseClassName}Id ):
	delegate = ${className}Delegate()
	responseData = delegate.delete( ${lowercaseClassName}Id )
	return HttpResponse(responseData, content_type="application/json");

def getAll(request):
	delegate = ${className}Delegate()
	responseData = delegate.getAll()
	asJson = serializers.serialize("json", responseData)
	return HttpResponse(asJson, content_type="application/json");

#set( $includeComposites = false )
#foreach( $singleAssociation in $classObject.getSingleAssociations( ${includeComposites} ) )
#set( $roleName = $singleAssociation.getRoleName() )
#set( $childName = $singleAssociation.getType() )
#set( $parentName  = $classObject.getName() )
def assign${roleName}( request, ${lowercaseClassName}Id, ${roleName}Id ):
	delegate = ${className}Delegate()
	responseData = delegate.save${roleName}( ${lowercaseClassName}Id, ${roleName}Id )
	asJson = serializers.serialize("json", responseData)
	return HttpResponse(asJson, content_type="application/json");
	
def unassign${roleName}( request, ${lowercaseClassName}Id ):
	delegate = ${className}Delegate()
	responseData = delegate.delete${roleName}( ${lowercaseClassName}Id )
	asJson = serializers.serialize("json", responseData)
	return HttpResponse(asJson, content_type="application/json");

#end##foreach( $singleAssociation in $classObject.getSingleAssociations( ${includeComposites} ) )
#foreach( $multiAssociation in $classObject.getMultipleAssociations() )
#set( $roleName = $multiAssociation.getRoleName() )
#set( $childName = $multiAssociation.getType() )
#set( $parentName  = $classObject.getName() )
def add${roleName}( request, ${lowercaseClassName}Id, ${roleName}Ids ):
	delegate = ${className}Delegate()
	responseData = delegate.add${roleName}( ${lowercaseClassName}Id, ${roleName}Ids )
	asJson = serializers.serialize("json", responseData)
	return HttpResponse(asJson, content_type="application/json");

def remove${roleName}( request, ${lowercaseClassName}Id, ${roleName}Ids ):
	delegate = ${className}Delegate()
	responseData = delegate.remove${roleName}( ${lowercaseClassName}Id, ${roleName}Ids )
	asJson = serializers.serialize("json", responseData)
	return HttpResponse(asJson, content_type="application/json");

#end##foreach( $multiAssociation in $classObject.getMultipleAssociations() )
