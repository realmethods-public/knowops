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
package com.realmethods.api;

import java.util.Arrays;
import java.util.List;

/**
 * Enumeration of support Relation DB Types for the purpose of assignment during invocation
 * of the goFramework RESTful API.  This list should expand to the complete set as supported
 * by the underlying ORM system.
 * 
 * @author realMethods, Inc.
 *
 */
public enum JsonRelationalDBType
{
	MYSQL,
    SQLSERVER,
    ORACLE,
    POSTGRES,
    UNKNOWN;

	/**
	 * Static method to return a List (ArrayList) of the enumerated values.
	 * 
	 * @return List<JsonRelationalDBType>
	 */
	public static List<JsonRelationalDBType> getValues()
	{
		return Arrays.asList(JsonRelationalDBType.values());
	}

	/**
	 * Static method returns the default value of MYSQL.
	 * 
	 * @return JsonRelationalDBType
	 */
	public static JsonRelationalDBType getDefaultValue()
	{
		return (MYSQL);
	}

	/**
	 * Static method determines which value of this enum best matches
	 * the provided name.
	 * 
	 * @return JsonRelationalDBType
	 */	
	public static JsonRelationalDBType whichOne(String name)
	{	  
		if (name.equalsIgnoreCase("MYSQL"))
	        return (JsonRelationalDBType.MYSQL);
	    else if (name.equalsIgnoreCase("SQLSERVER"))
	        return (JsonRelationalDBType.SQLSERVER);
	    else if (name.equalsIgnoreCase("ORACLE"))
	        return (JsonRelationalDBType.ORACLE);
	    else if (name.equalsIgnoreCase("POSTGRES"))
	        return (JsonRelationalDBType.POSTGRES);	 
	    else
	    	return (JsonRelationalDBType.UNKNOWN);
	}  
}
