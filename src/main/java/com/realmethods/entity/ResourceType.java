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
 * Enumeration of resource types.
 * 
 * @author realMethods, Inc.
 *
 */
public enum ResourceType
{
	DOCKERFILE,
    CI_CONFIG,
    TERRAFORM,
    PROJECT_AS_CODE,
	GENERIC;
    
	/**
	 * Static method to return a List (ArrayList) of the enumerated values.
	 * 
	 * @return List<ResourceType>
	 */
	public static List<ResourceType> getValues()
	{
		return Arrays.asList(ResourceType.values());
	}

	/**
	 * Static method returns the default value of MYSQL.
	 * 
	 * @return ResourceType
	 */
	public static ResourceType getDefaultValue()
	{
		return (GENERIC);
	}

	/**
	 * Static method determines which value of this enum best matches
	 * the provided name.
	 * 
	 * @return ResourceType
	 */	
	public static ResourceType whichOne(String name)
	{	  
		if (name.equalsIgnoreCase("DOCKERFILE"))
	        return (ResourceType.DOCKERFILE);
	    else if (name.equalsIgnoreCase("CI_CONFIG"))
	        return (ResourceType.CI_CONFIG);
	    else if (name.equalsIgnoreCase("TERRAFORM"))
	        return (ResourceType.TERRAFORM);
	    else if (name.equalsIgnoreCase("PROJECT_AS_CODE"))
	        return (ResourceType.PROJECT_AS_CODE);		
	    else if (name.equalsIgnoreCase("GENERIC"))
	        return (ResourceType.GENERIC);
	    else
	        return getDefaultValue();
	}  
}
