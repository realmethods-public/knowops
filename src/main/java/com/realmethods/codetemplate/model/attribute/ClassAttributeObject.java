/*******************************************************************************
 * realMethods Confidential
 * 
 * 2021 realMethods, Inc.
 * All Rights Reserved.
 * 
 * This file is subject to the terms and conditions defined in
 * file 'license.txt', which is part of this source code package.
 *  
 * Contributors :
 *       realMethods Inc - General Release
 ******************************************************************************/
package com.realmethods.codetemplate.model.attribute;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.logging.Logger;

import com.realmethods.codetemplate.model.classes.ClassObject;

/**
 * Wrapper class for the notion of an AttributeObject derived from the data of a java.lang.reflect.Field
 * 
 * @author realMethods, Inc.
 *
 */
public class ClassAttributeObject extends AttributeObject
{
	/**
	 * Extracts relevant data from the Field to assign internally to the base class
	 * 
	 * @param classObject
	 * @param field
	 */
    public ClassAttributeObject( ClassObject classObject, Field field )
    {
    	super( classObject );

    	LOGGER.info( "Field creation: " + classObject.getName() + ":" + field.getName() );
    	
    	setName( field.getName() );
        
    	try {
            Type genericType = field.getGenericType();
            
            if ( genericType != null && genericType.toString().startsWith( "java.util" ) ) {
            	setQualifiedType( genericType.toString() );
            	setType( genericType.toString() );
            	isCollection( true );
            	isFromMultiValueAssociation( true );
            }
            else {
            	setQualifiedType( field.getType().getName() );
            	setType(field.getType().getSimpleName() );
            	isCollection( false );
            	isFromMultiValueAssociation( false );
            }

    	} catch( TypeNotPresentException typeNotPresentException ) {
    		LOGGER.warning( "Failed to locate genericType for " + typeNotPresentException.typeName() + ". Will treat as a multi-value association" );
        	setQualifiedType(null);
        	setType( "java.util.Set<" + typeNotPresentException.typeName() + ">");
        	isCollection( true );
        	isFromMultiValueAssociation( true );
    	}
    	        
        if ( Modifier.isPublic( field.getModifiers() ) )
        	setVisibility( "public" );
        else
        	setVisibility( "protected" );
        
        isPrimaryKey( false );
        canBeNull( true );
        isIntrinsicType( false );
        isStatic( Modifier.isStatic( field.getModifiers() ) );
        isFinal( Modifier.isFinal( field.getModifiers() ) );
        isTransient( Modifier.isTransient( field.getModifiers() ) );
        isVolatile( Modifier.isVolatile( field.getModifiers() ) );
        applyUniqueName();
    }
    
// attributes
	private static final Logger LOGGER	= Logger.getLogger(ClassAttributeObject.class.getName());	

}
