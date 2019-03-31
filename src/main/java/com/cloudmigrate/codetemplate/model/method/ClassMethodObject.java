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
package com.cloudmigrate.codetemplate.model.method;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import java.util.*;

/**
 * Wrapper class to handle being a MethodObject seeded with data from a reflected Java  Method instance'
 * 
 * @author realMethods, Inc.
 *
 */
public class ClassMethodObject extends MethodObject
{
	/**
	 * Default constructor
	 */
	public ClassMethodObject() {
		// no_op
	}
	
	/**
	 * Constructor - seeds the internal attributes using data from the input Method.
	 *  
	 * @param method
	 */
    public ClassMethodObject( Method method ) {
        setName( method.getName() );
        setVisibility( getVisibility( method ) );
        isStatic( isFromStatic( method ) );
        setArguments( getArguments( method ) );
        setDocumentation( "" );
        setReturnType( getReturnType( method ) );

        reconcileReturnType();
        reconcileArguments();
    }

    /**
     * Returns the visibility, defaults to "public" if none inferred.
     * 
     * @param method
     * @return
     */
    private String getVisibility( Method method ) {
        if ( Modifier.isPublic( method.getModifiers() ) )
        	setVisibility( "public" );
        else
        	setVisibility( "protected" );
        
        return visibility;
    }

    /**
     * Returns the return type of the method encapsulated.
     * 
     * @param method
     * @return
     */
    protected String getReturnType(Method method) {
    	return( determineName( method.getReturnType() ) );
    }
    
    /**
     * Returns true/false if the method is considered static.
     * 
     * @param method
     * @return boolean
     */
    protected boolean isFromStatic(Method method) {
        return Modifier.isStatic(method.getModifiers());
    }

    /**
     * Helper function, used to constructs a MethodArgs from the input Method.
     * 
     * @param 	method
     * @return	MethodArgs
     */
    protected MethodArgs getArguments( Method method ) {
    	MethodArgs methodArgs = new MethodArgs();
    	Class<?>[] paramTypes = method.getParameterTypes();
    	
		methodArgs.setReturnType(  determineName( method.getReturnType() ) ) ;
		
		String name = null;
		String type = null;
		
    	for( int i=0; i < paramTypes.length; i++) {
    		type = determineName( paramTypes[i] );
    		name = "arg" + i;
    		
    		methodArgs.getArgs().add( new MethodArg( name, type ) );
    	}
    	
    	
    	return( methodArgs );
    } 
 
    /**
     * Helper method to determine the name from the input Java Class type.
     * 
     * @param 	theClass
     * @return	String
     */
    protected String determineName( Class<?> theClass ) {
    	Package pkg = theClass.getPackage();
    	
    	if ( pkg != null )
    		return theClass.getName();
    	else
    		return theClass.getSimpleName();

    }
    
// attributes    
}
