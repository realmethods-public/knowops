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

import java.util.Map;

/**
 * Interface to the provider of property access to the contents of the framework related config file.
 * <p>
 * @author		cloudMigrate, Inc.
 */
public interface IFrameworkPropertiesHandler extends IPropertiesHandler
{

//************************************************************************    
// Public Methods
//************************************************************************
  	
   /**
   	* Returns the root parameters.
   	* @return	presentation and integration tier parameters
   	*/
  	public Map getParams();

   /**
   	* Returns the parameter value for the provided key.
   	* <p>
   	* @param 	key
   	* @return	presentation and integration tier parameters
   	*/
  	public String getParam( String key );
		
   /**
   	* Returns the parameter value for the provided key.  If the key doesn't exist, or no
   	* value has been applied, returns the defValue.
   	* <p>
   	* @param	key		key to parameter value
   	* @param 	defValue	default value
   	* @return	discovered parameter, or the defValue if not found
   	*/		
  	public String getParam( String key, String defValue );
  	
  	/**
  	 * Returns the mapping of jndi args.
  	 * @return	jndi args
  	 */
  	public Map getJNDIArgs();
  	
  	/**
  	 * Returns the mapping of startup declarations.
  	 * @return	startup definitions
  	 */
  	public Map getStartups();
  	
  	/**
  	 * Returns the mapping of hook declarations.
  	 * @return	hook definitions
  	 */
  	public Map getHooks();
  	
  	/**
  	 * Returs the mapping of factory declarations.
  	 * @return	factory declarations
  	 */
  	public Map getFactoryDecls();
  	
  	
//************************************************************************    
// Attributes
//************************************************************************

}

/*
 * Change Log:
 * $Log$
 */
