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
package com.realmethods.foundational.common.properties;

import com.realmethods.foundational.common.FrameworkBaseObjectFactory;

import com.realmethods.foundational.common.exception.ObjectCreationException;

/**
 * Factory singleton pattern implementation for a FrameworkPropertiesHandler.
 * <p>
 * @author    realMethods, Inc.
 */
public class FrameworkPropertiesHandlerFactory extends FrameworkBaseObjectFactory
{
    /** 
     * constructor - deter instantiation 
     */ 
    protected FrameworkPropertiesHandlerFactory()
    {
    	super( true /*cache all distinct class instances as singletons*/ );
    }
     
//	factory method
	 
	 public static FrameworkPropertiesHandlerFactory getInstance()
	 { 
	 	if ( instance == null )
	 		instance  =  new FrameworkPropertiesHandlerFactory();
	 		
		return( instance );	 		
	 }	 
    
	/**
	 * Factory method used to create a IPropertiesHandler, using the provided class name.
	 * <p>
	 * @param		nameToInstantiate
	 * @return      IBasePropertiesHandler
	 * @exception   ObjectCreationException
	 */
	public IPropertiesHandler getPropertyHandler( String nameToInstantiate )
	throws ObjectCreationException
	{	    
		IPropertiesHandler handler = null;
		   
		try
		{
			handler = (IPropertiesHandler)getObject( nameToInstantiate );	
		}
		catch( Exception exc )
		{
			throw new  ObjectCreationException( "FrameworkPropertiesHandlerFactory.getPropertyHandler(String) - " + exc, exc );
		}
		
		return( handler );    	
	}
		

// attributes

	/**
	 * Singleton factory instance
	 */
    private static FrameworkPropertiesHandlerFactory instance = null;
}


