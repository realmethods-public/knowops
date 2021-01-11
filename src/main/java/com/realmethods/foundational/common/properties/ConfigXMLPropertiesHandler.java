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
import java.util.Map;

/**
 * Provides property access to the contents of the config.xml file.
 * <p>
 * @author		realMethods, Inc.
 */
public class ConfigXMLPropertiesHandler 
	extends XMLPropertiesHandler
	implements IConfigPropertiesHandler
{
	
//************************************************************************    
// Constructors
//************************************************************************


	public ConfigXMLPropertiesHandler()
	{
		super();
	}
	
//************************************************************************    
// Public Methods
//************************************************************************
  	
  	/**
  	 * Returns the log4j attributes from config.xml.
  	 * <p>
  	 * @return		key/value pairs of attribute names/values
  	 */
	public Map getLog4JParams()
	{
		Map props = (Map)cache( getFrameworkXMLParser().getAttributesForFirstOccurance( FRAMEWORK_LOG4J_FILE ) );
		
		if ( props == null )
		{		
			props = getFrameworkXMLParser().getAttributesForFirstOccurance( FRAMEWORK_LOG4J_FILE );
			cache(FRAMEWORK_LOG4J_FILE, props );
		}
		
		return ( props );
	}
	
	/**
	 * Returns a Collection of HashMaps, representing the values for the app-config-file element
	 * from config.xml.
	 * <p>
	 * @return		Collection of Hashaps
	 */
	public Collection getAppConfigFiles()
	{
		Collection props = (Collection)cache( APP_CONFIG_FILE );
		
		if ( props == null )
		{
			props = getFrameworkXMLParser().getAttributesForEachOccurance( APP_CONFIG_FILE );
			cache( props, APP_CONFIG_FILE );
		}
		return( props );
	}
	
	
// Attributes
	private static final String APP_CONFIG_FILE 		= "app-config-file";
	private static final String FRAMEWORK_LOG4J_FILE 	= "framework-log4j-file";
	
}

/*
 * Change Log:
 * $Log$
 */
