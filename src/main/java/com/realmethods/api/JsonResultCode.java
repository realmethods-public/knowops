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
 * Enumeration of supported result codes for calls made to the goFramework RESTful API.
 * 
 * @author realMethods, Inc.
 *
 */
public enum JsonResultCode
{
	SUCCESS,
    BAD_INPUT_STRUCTURE,
    BAD_INPUT_VALUE,
    MODEL_PARSER_ERROR,
    UKNOWN_DATA_TYPE,
    NO_ASSOCIATION_FOUND,
    UNKNOWN_TECH_STACK_PACKAGE,
    UNKNOWN_RELATIONAL_DB_TYPE,
    UNKNOWN_MODEL_TYPE,
    INVALID_SERVICE_REQUEST,
    INVALID_ACCESS_KEY,
    INVALID_TOKEN,
    TECH_STACK_PACKAGE_LIST,
    TECH_STACK_OPTIONS,
    ARCHIVED_APP_LIST,
    REQUEST_EXECUTION_ERROR;

	/**
	 * Static method to return a List (ArrayList) of the enumerated values.
	 * 
	 * @return List<JsonResultCode>
	 */
	public static List<JsonResultCode> getValues()
	{
		return Arrays.asList(JsonResultCode.values());
	}

	/**
	 * Static method returns the default value of SUCCESS.
	 * 
	 * @return JsonRelationalDBType
	 */
	public static JsonResultCode getDefaultValue()
	{
		return (SUCCESS);
	}

	/**
	 * Static method determines which value of this enum best matches
	 * the provided name.
	 * 
	 * @return JsonResultCode
	 */		
	public static JsonResultCode whichOne(String name)
	{	  
		name = name.toUpperCase();
		
		switch( name ) {
			case "SUCCESS": 
				return (JsonResultCode.SUCCESS);			
			case "BAD_INPUT_STRUCTURE":
				return (JsonResultCode.BAD_INPUT_STRUCTURE);
			case "BAD_INPUT_VALUE":
				return (JsonResultCode.BAD_INPUT_VALUE);
			case "MODEL_PARSER_ERROR":
				return (JsonResultCode.MODEL_PARSER_ERROR);
			case "UKNOWN_DATA_TYPE":
				return (JsonResultCode.UKNOWN_DATA_TYPE);
			case "NO_ASSOCIATION_FOUND":
				return (JsonResultCode.NO_ASSOCIATION_FOUND);
			case "UNKNOWN_TECH_STACK_PACKAGE":
				return (JsonResultCode.UNKNOWN_TECH_STACK_PACKAGE);
			case "UNKNOWN_RELATIONAL_DB_TYPE":
				return (JsonResultCode.UNKNOWN_RELATIONAL_DB_TYPE);
			case "UNKNOWN_MODEL_TYPE":
				return (JsonResultCode.UNKNOWN_MODEL_TYPE);
			case "INVALID_SERVICE_REQUEST":
				return (JsonResultCode.INVALID_SERVICE_REQUEST);
			case "INVALID_ACCESS_KEY":
				return (JsonResultCode.INVALID_ACCESS_KEY);
			case "INVALID_TOKEN":
				return (JsonResultCode.INVALID_TOKEN);
			case "REQUEST_EXECUTION_ERROR":
				return (JsonResultCode.REQUEST_EXECUTION_ERROR);
			case "ARCHIVED_APP_LIST":
				return (JsonResultCode.ARCHIVED_APP_LIST);
			case "TECH_STACK_OPTIONS":
				return (JsonResultCode.TECH_STACK_OPTIONS);	 
			default:
				return (getDefaultValue());
		}
 }
  
//************************************************************************
//Protected / Private Methods
//************************************************************************

//************************************************************************
//Attributes
//************************************************************************
}
