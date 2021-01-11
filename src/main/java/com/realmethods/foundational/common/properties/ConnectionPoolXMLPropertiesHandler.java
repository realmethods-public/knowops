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

//***************************
//Imports
//***************************

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Provides property access to the contents of the connection pool related config file.
 * <p>
 * @author		realMethods, Inc.
 */
public class ConnectionPoolXMLPropertiesHandler 
	extends XMLPropertiesHandler
	implements IConnectionPoolPropertiesHandler
{

//************************************************************************    
// Constructors
//************************************************************************


	public ConnectionPoolXMLPropertiesHandler()
	{
		super();
	}
	
//************************************************************************    
// Public Methods
//************************************************************************
  	

  /**
   * Returns a Collection of Strings, representing the names of the log handlers.
   * @return		log handler names
   */
   public Collection getConnectionPoolNames()
   {
	   return( getFrameworkXMLParser().getAttributesForEachOccurance( "connectionpool", "name" ) );
   }
		
  /**
   * Returns the key/value pairings as parameters for the provided pool name.
   * @param 		poolName	
   * @return		key/value pairings of log handler  parameters.
   */
   public Map getConnectionPoolParams( String poolName )
   {
	   Map params = getFrameworkXMLParser().getAttributesForEachOccuranceBy( "connectionpool",
																		 "name",
																		 poolName );    	
	   return( params );
   }
	  
   /**
   	* Returns a Map where the key is the name of a connection pool, and the value is a Map
   	* of its values.
   	* <p>
   	* @return	Map
   	*/
   	public Map getConnectionPools()
   	{
		Map pools = (Map)cache( "connection-pools" );		
		
		if ( pools == null )
		{
			pools = new HashMap();
			Collection names 	= getConnectionPoolNames();
			Iterator iter		= names.iterator();
			String name			= null;
				
			while( iter.hasNext() )
			{
				name = (String)iter.next();
				pools.put( name, getConnectionPoolParams( name ) );
			}
				
			cache( "connection-pools", pools );			
		}
		
		return( pools ); 
   }
     	
//*************************************************************************    
// Protected / Protected Methods
//************************************************************************

//************************************************************************    
// Attributes
//************************************************************************

}

/*
 * Change Log:
 * $Log$
 */
