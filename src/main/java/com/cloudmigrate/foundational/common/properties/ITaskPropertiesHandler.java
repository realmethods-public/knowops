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
 * Interface to the provider of property access to the contents of the task related config file.
 * <p>
 * @author		cloudMigrate, Inc.
 */
public interface ITaskPropertiesHandler extends IPropertiesHandler
{

//************************************************************************    
// Public Methods
//************************************************************************

   /**
   	* Returns a Collection of Strings, representing the names of the declared  tasks.
   	* @return		task names
   	*/
  	public Collection getTaskNames();
  
   /**
   	* Returns the attribute names and values for the provided task name.
   	* @param 	taskName
   	* @return	the properties related to the named task handler  
   	*/
   public Map getTaskParams( String taskName );
  
   /**
	* Returns a Map where the key is the name of a task, and the value is a Map
	* of its values.
	* <p>
	* @return	all task handler properties, where the key is a task hanndler name, and the
	 * 			related value is a Map of its properties 
	*/
   public Map getTasks();
     	
//************************************************************************    
// Attributes
//************************************************************************

}

/*
 * Change Log:
 * $Log$
 */
