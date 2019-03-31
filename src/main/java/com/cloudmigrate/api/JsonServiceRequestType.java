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
package com.cloudmigrate.api;

import java.util.Arrays;
import java.util.List;

/**
 * Enumeration of supported service request types to the goFramework RESTful API.
 * 
 * @author realMethods, Inc.
 *
 */
public enum JsonServiceRequestType
{
	CREATE_APP,
    DELETE_APP,
    GET_APP,
    TECH_STACK_PACKAGE_LIST,
    MODEL_LIST,
    TECH_STACK_PACKAGE_OPTIONS,
    ARCHIVED_APP_LIST,
    SERVICE_LIST,
    VALIDATE_TOKEN,
    VALIDATE_MODEL,
    VALIDATE_TECH_STACK,
    REGISTER_MODEL,
    REGISTER_TECH_STACK,    
    DELETE_MODEL,
    GET_MODEL,     
    DELETE_TECH_STACK,
    GET_TECH_STACK,  
    PROMOTE_MODEL,
    PROMOTE_TECH_STACK,
    PROMOTE_APP,
    DEMOTE_MODEL,
    DEMOTE_TECH_STACK,
    DEMOTE_APP,
    USER_INFO,
    UNKNOWN;

	/**
	 * Static method to return a List (ArrayList) of the enumerated values.
	 * 
	 * @return List<JsonServiceRequestType>
	 */	
	public static List<JsonServiceRequestType> getValues()
	{
		return Arrays.asList(JsonServiceRequestType.values());
	}

	/**
	 * Static method returns the default value of CREATE_APP.
	 * 
	 * @return JsonServiceRequestType
	 */
	public static JsonServiceRequestType getDefaultValue()
	{
		return (SERVICE_LIST);
	}

	/**
	 * Static method determines which value of this enum best matches
	 * the provided name.
	 * 
	 * @return JsonServiceRequestType
	 */	 
	public static JsonServiceRequestType whichOne(String name)
	{	  
		switch( name ) {
			case "CREATE_APP": return (JsonServiceRequestType.CREATE_APP);
			case "DELETE_APP": return (JsonServiceRequestType.DELETE_APP);
			case "GET_APP": return (JsonServiceRequestType.GET_APP);
			case "TECH_STACK_PACKAGE_LIST": return (JsonServiceRequestType.TECH_STACK_PACKAGE_LIST);
			case "MODEL_LIST": return (JsonServiceRequestType.MODEL_LIST);
			case "TECH_STACK_PACKAGE_OPTIONS": return (JsonServiceRequestType.TECH_STACK_PACKAGE_OPTIONS);
			case "ARCHIVED_APP_LIST": return (JsonServiceRequestType.ARCHIVED_APP_LIST);
			case "SERVICE_LIST": return (JsonServiceRequestType.SERVICE_LIST);
			case "VALIDATE_TOKEN": return (JsonServiceRequestType.VALIDATE_TOKEN);
			case "VALIDATE_MODEL": return (JsonServiceRequestType.VALIDATE_MODEL);
			case "VALIDATE_TECH_STACK": return (JsonServiceRequestType.VALIDATE_TECH_STACK);
			case "REGISTER_MODEL": return (JsonServiceRequestType.REGISTER_MODEL);
			case "REGISTER_TECH_STACK": return (JsonServiceRequestType.REGISTER_TECH_STACK);
			case "DELETE_MODEL": return (JsonServiceRequestType.DELETE_MODEL);
			case "DELETE_TECH_STACK": return (JsonServiceRequestType.DELETE_TECH_STACK);
			case "GET_MODEL": return (JsonServiceRequestType.GET_MODEL);
			case "GET_TECH_STACK": return (JsonServiceRequestType.GET_TECH_STACK);
			case "PROMOTE_MODEL": return (JsonServiceRequestType.PROMOTE_MODEL);
			case "PROMOTE_TECH_STACK": return (JsonServiceRequestType.PROMOTE_TECH_STACK);
			case "PROMOTE_APP": return (JsonServiceRequestType.PROMOTE_APP);
			case "DEMOTE_MODEL": return (JsonServiceRequestType.DEMOTE_MODEL);
			case "DEMOTE_TECH_STACK": return (JsonServiceRequestType.DEMOTE_TECH_STACK);
			case "DEMOTE_APP": return (JsonServiceRequestType.DEMOTE_APP);
			case "USER_INFO": return (JsonServiceRequestType.USER_INFO);
			default: return(JsonServiceRequestType.UNKNOWN);
		}
	}
}
