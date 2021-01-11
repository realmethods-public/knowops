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
package com.realmethods.entity;

import java.util.Arrays;
import java.util.List;

/**
 * enumeration of processed model types.  Support SUCCESS, PAUSED, FAILED
 * 
 * @author realMethods, Inc.
 *
 */
public enum BuildStatus 
{	SUCCESS,
    FAILED,
    PAUSED,
    UNKNOWN;


	/**
	 * Static method to return a List (ArrayList) of the enumerated values.
	 * 
	 * @return List<BuildStatus>
	 */	
	public static List<BuildStatus> getValues()
	{
		return Arrays.asList(BuildStatus.values());
	}

	/**
	 * Static method returns the default value of UML.
	 * 
	 * @return BuildStatus
	 */
	public static BuildStatus getDefaultValue()
	{
		return (SUCCESS);
	}

	/**
	 * Static method determines which value of this enum best matches
	 * the provided name.
	 * 
	 * @return BuildStatus
	 */	 
	public static BuildStatus whichOne(String name)
	{	  
		name = name.toUpperCase();
		
		switch( name ) {
			case "SUCCESS" 		: return SUCCESS;
			case "FAILED"		: return FAILED;
			case "PAUSED" 		: return PAUSED;
			default: return UNKNOWN;
		}
	}


  
//************************************************************************
//Protected / Private Methods
//************************************************************************

//************************************************************************
//Attributes
//************************************************************************
}
