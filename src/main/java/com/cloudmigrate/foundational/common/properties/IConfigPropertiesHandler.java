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
import java.util.Map;

/**
 * Interface to the provider of property access to the contents of the configs file.
 * <p>
 * @author		cloudMigrate, Inc.
 */
public interface IConfigPropertiesHandler extends IPropertiesHandler
{
	
	
//************************************************************************    
// Public Methods
//************************************************************************
  	
  	/**
  	 * Returns the log4j attributes from config.xml.
  	 * <p>
  	 * @return		key/value pairs of attribute names/values
  	 */
	public Map getLog4JParams();
	
	/**
	 * Returns a Collection of HashMaps, representing the values for the app-config-file element
	 * from config.xml.
	 * <p>
	 * @return		Collection of Hashaps
	 */
	public Collection getAppConfigFiles();
		
//************************************************************************    
// Attributes
//************************************************************************

}

/*
 * Change Log:
 * $Log$
 */
