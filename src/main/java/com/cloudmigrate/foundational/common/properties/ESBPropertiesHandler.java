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
package com.cloudmigrate.foundational.common.properties;

//***************************
//Imports
//***************************

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Provides property access to the contents of the ESB related config file.
 * <p>
 * @author		cloudMigrate, Inc.
 */
public class ESBPropertiesHandler 
	extends XMLPropertiesHandler
	implements IESBPropertiesHandler
{

//************************************************************************    
// Constructors
//************************************************************************


	public ESBPropertiesHandler()
	{
		super();
	}
	
//************************************************************************    
// Public Methods
//************************************************************************
  	
  	/**
  	 * Returns a Collection of Strings, representing the names of the components.
  	 * @return		log handler names
  	 */
	public Collection getESBComponentNames()
    {
    	return( getFrameworkXMLParser().getAttributesForEachOccurance( "component", "name" ) );
    }
	
	/**
	 * Returns the key/value pairings as parameters for the provided component
	 * @param 		componentName	
	 * @return		key/value pairings of component parameters.
	 */
	public Map getESBComponentData( String componentName )
    {
    	Map params = getFrameworkXMLParser().getAttributesForEachOccuranceBy( "component",
    																	"name",
    																	componentName );    	
    	return( params );
    }
  
	/**
	 * Returns a Map where the key is the name of a componentName, and the value is a Map
	 * of its values.
	 * <p>
	 * @return	Map
	 */
	public Map getESBComponents()
	{	
		Map endPoints = (Map)cache( "components" );		
		
		if ( endPoints == null )
		{
			endPoints = new HashMap();
			Collection names 	= getESBComponentNames();
			Iterator iter		= names.iterator();
			String name			= null;
				
			while( iter.hasNext() )
			{
				name = (String)iter.next();
				endPoints.put( name, getESBComponentData( name ) );
			}
			
			cache( "components", endPoints );
		}
		
		return( endPoints );
	}
	
	/**
	 * Returns the key/value pairings 
	 * @return	key/value pairings of JMS connector configuration values
	 */
	public Map getJMSConnectorConfig()
	{
		Map params = getParams( "jmsConnectorConfig" );
		
		return( params );
	}
	

  	/**
  	 * Returns a Collection of Strings, representing the names of the manager.
  	 * @return		log handler names
  	 */
	public Collection getESBManagerNames()
    {
    	return( getFrameworkXMLParser().getAttributesForEachOccurance( "esbManager", "name" ) );
    }
	
	/**
	 * Returns the key/value pairings as parameters for the provided esb manager name
	 * @param 		managerName	
	 * @return		key/value pairings of esb manager parameters.
	 */
	public Map getESBManagerData( String managerName )
    {
    	Map params = getFrameworkXMLParser().getAttributesForEachOccuranceBy( "esbManager",
    																	"name",
    																	managerName );
    	
    	return( params );
    }

	/**
	 * Returns a Map where the key is the name of a manager, and the value is a Map
	 * of its values.
	 * <p>
	 * @return	Map
	 */
	public Map getESBManagers()
	{
		Map mgrs = (Map)cache( "esbManagers" );		
		
		if ( mgrs == null )
		{
			mgrs = new HashMap();
			Collection names 	= getESBManagerNames();
			
			Iterator iter		= names.iterator();
			String name			= null;
				
			while( iter.hasNext() )
			{
				name = (String)iter.next();
				mgrs.put( name, getESBManagerData( name ) );
			}
			
			cache( "esbManagers", mgrs );
		}
		
		return( mgrs );
		
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
