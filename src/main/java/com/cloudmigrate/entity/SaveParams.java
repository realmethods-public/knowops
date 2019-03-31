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
package com.cloudmigrate.entity;

/**
 * Encapsulates the data related to saving a provided model during a call to the goFramework RESTful API.
 * 
 * @author realMethods, Inc.
 *
 */
public class SaveParams
{
	/**
	 * Default constructor
	 */
	public SaveParams() {
		
	}
	
	/**
	 * Initialization constructor
	 * 
	 * @param name
	 * @param description
	 */
	public SaveParams( String name, String description ) {
		this.name = name;
		this.description = description;
	}
	
	/**
	 * Returns the name field.
	 * 
	 * @return String
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Assigns the name field the provided argument.
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the description field.
	 * 
	 * @return String
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Assigns the description field the provided argument.
	 * 
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	
// attributes
	
	protected String name 			= null;
	protected String description 	= null;
}
