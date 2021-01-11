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
 * Enumeration of level of usage a user or item has.
 * 
 * @author realMethods, Inc.
 *
 */
public enum UserType
{
	HOBBYIST,
    PROFESSIONAL,
    ENTERPRISE,
	EVALUATOR;
    
	/**
	 * Static method to return a List (ArrayList) of the enumerated values.
	 * 
	 * @return List<UserType>
	 */
	public static List<UserType> getValues()
	{
		return Arrays.asList(UserType.values());
	}

	/**
	 * Static method returns the default value of MYSQL.
	 * 
	 * @return UserType
	 */
	public static UserType getDefaultValue()
	{
		return (EVALUATOR);
	}

	/**
	 * Static method determines which value of this enum best matches
	 * the provided name.
	 * 
	 * @return UserType
	 */	
	public static UserType whichOne(String name)
	{	  
		if (name.equalsIgnoreCase("HOBBYIST"))
	        return (UserType.HOBBYIST);
	    else if (name.equalsIgnoreCase("PROFESSIONAL"))
	        return (UserType.PROFESSIONAL);
	    else if (name.equalsIgnoreCase("ENTERPRISE"))
	        return (UserType.ENTERPRISE);
	    else if (name.equalsIgnoreCase("EVALUATOR"))
	        return (UserType.EVALUATOR);
	    else
	        return getDefaultValue();
	}  
}
