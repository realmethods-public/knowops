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
package com.cloudmigrate.foundational.integration.persistent;

import com.cloudmigrate.foundational.common.FrameworkBaseObjectFactory;


public class FrameworkHibernatorInterceptorFactory
extends FrameworkBaseObjectFactory 
{
	
	/** 
	 * constructor - deter instantiation 
	 */ 
	protected FrameworkHibernatorInterceptorFactory()
	{
		super( false );
	}

	
	public static FrameworkHibernatorInterceptorFactory getInstance()
	{ 
	   if ( instance == null )
		   instance  =  new FrameworkHibernatorInterceptorFactory();
	 		
	   return( instance );	 		
	}	 
	
	/**
	 * Factory method used to create a FrameworkHibernatorInterceptor.
	 * The value is retrieved from the framework.xml file AuditTrailInterceptor 
	 * property
	 * <p>
	 * @return      FrameworkHibernatorInterceptor
	 */
	public FrameworkHibernatorInterceptor getHibernateInterceptor()
	{	    
		if ( interceptor == null )
		{
			String nameToInstantiate = "com.cloudmigrate.foundational.integration.persistent.FrameworkHibernatorInterceptor";				
			logDebugMessage( "FrameworkHibernatorInterceptorFactory.getHibernateInterceptor()- framework property AuditTrailInterceptor not found, using default value " + nameToInstantiate );
			
			try
			{
				interceptor = (FrameworkHibernatorInterceptor)getObject( nameToInstantiate );	
			}
			catch( Exception exc )
			{
				logErrorMessage( "FrameworkHibernatorInterceptorFactory.getHibernateInterceptor() - " + exc );
			}
		}
			
		return( interceptor );    	
	}
		

// attributes

	/**
	 * Singleton factory instance
	 */
	private static FrameworkHibernatorInterceptorFactory instance = null;
	protected FrameworkHibernatorInterceptor interceptor = null;

}
 
