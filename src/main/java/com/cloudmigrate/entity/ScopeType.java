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

import java.util.Arrays;
import java.util.List;

/**
 * Enumeration of support the level of Scope a user or item has.
 * 
 * @author realMethods, Inc.
 *
 */
public enum ScopeType
{
	PUBLIC,
    PRIVATE,
    COMMUNITY,
    ALL;
    
	/**
	 * Static method to return a List (ArrayList) of the enumerated values.
	 * 
	 * @return List<ScopeType>
	 */
	public static List<ScopeType> getValues()
	{
		return Arrays.asList(ScopeType.values());
	}

	/**
	 * Static method returns the default value of MYSQL.
	 * 
	 * @return ScopeType
	 */
	public static ScopeType getDefaultValue()
	{
		return (PUBLIC);
	}

	/**
	 * Static method determines which value of this enum best matches
	 * the provided name.
	 * 
	 * @return ScopeType
	 */	
	public static ScopeType whichOne(String name)
	{	  
		if (name.equalsIgnoreCase("PUBLIC"))
	        return (ScopeType.PUBLIC);
	    else if (name.equalsIgnoreCase("PRIVATE"))
	        return (ScopeType.PRIVATE);
	    else if (name.equalsIgnoreCase("COMMUNITY"))
	        return (ScopeType.COMMUNITY);
	    else if (name.equalsIgnoreCase("ALL"))
	        return (ScopeType.COMMUNITY);
	    else
	        return getDefaultValue();
	}  
}
