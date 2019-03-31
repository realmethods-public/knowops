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
package com.cloudmigrate.codetemplate.model;

import java.util.Arrays;
import java.util.List;

/**
 * enumeration of sources of the input model to parse and apply during app generation
 * 
 * @author realMethods, Inc.
 *
 */
public enum ModelSource 
{
	XMIFILE,
    POJO,
    DATABASE,
    JSON,
    USER_ENTERED;



	/**
	 * Static method to return a List (ArrayList) of the enumerated values.
	 * 
	 * @return List<ModelType>
	 */	
	public static List<ModelSource> getValues() {
		return Arrays.asList(ModelSource.values()); 
	}

	/**
	 * Static method returns the default value of UML.
	 * 
	 * @return ModelType
	 */
	public static ModelSource getDefaultValue() {
		return (XMIFILE);
	}

	/**
	 * Static method determines which value of this enum best matches
	 * the provided name.
	 * 
	 * @return ModelType
	 */	 

	public static ModelSource whichOne(String name)
	{	    
		if (name.equalsIgnoreCase("xmiFile")) {
			return (ModelSource.XMIFILE);
		}

		if (name.equalsIgnoreCase("pojo")) {
			return (ModelSource.POJO);
		}
     
		if (name.equalsIgnoreCase("database")) {
			return (ModelSource.DATABASE);
		}

		if (name.equalsIgnoreCase("json")) {
			return (ModelSource.JSON);
		}

		if (name.equalsIgnoreCase("userEntered")) {
			return (ModelSource.USER_ENTERED);
		}
		else {
         return (getDefaultValue());
		}
	}

}
