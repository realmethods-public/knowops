/*******************************************************************************
 * realMethods Confidential
 * 
 * 2018 realMethods, Inc.
 * All Rights Reserved.
 * 
 * This file is subject to the terms and conditions defined in
 * file 'license.txt', which is part of this source code package.
 *  
 * Contributors :
 *       realMethods Inc - General Release
 ******************************************************************************/
package com.cloudmigrate.codetemplate.model.attribute;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

import com.cloudmigrate.codetemplate.model.classes.ClassObject;

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

    	setName( field.getName() );
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
}
