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

/**
 * Helper class to encapsulate the results of making a call to the goFramework RESTful API.
 * 
 * @author realMethods, Inc.
 *
 */
public class JsonResult
{
	/**
	 * Returns the resultCode field.
	 * 
	 * @return JsonResultCode
	 */
	public JsonResultCode getResultCode() { 
		return resultCode; 
	}

	/**
	 * Assigns the resultCode field the provided argument.
	 * 
	 * @param resultCode
	 */
	public void setResultCode( JsonResultCode resultCode ) { 
		this.resultCode = resultCode; 
	}
	
	/** 
	 * Returns the result field.
	 * 
	 * @return String
	 */
	public String getResult() {
		return result;
	}

	/**
	 * Assigns the result field the provided argument.
	 * 
	 * Automatically encodes the value to UTF-8 for 
	 * HTTP consistency.
	 * 
	 * @param msg
	 */
	public void setResult( String result) { 
		this.result = result;
	}

	/**
	 * Returns the processingMsg field.
	 * 
	 * @return String
	 */
	public String getProcessingMessage() {
		return processingMsg;
	}

	/**
	 * Assigns the processingMsg field the provided argument. Clips at 200 chars
	 * 
	 * @param processingMsg
	 */
	public void setProcessingMessage( String processingMsg ) { 
		if ( processingMsg != null && processingMsg.length() > 200 )
			this.processingMsg = processingMsg.substring(0,  199);
		else
			this.processingMsg = processingMsg;
	}

	/**
	 * Returns the applicationId field.
	 * 
	 * @return String
	 */
	public String getApplicationId() {
		return applicationId;
	}

	/**
	 * Assigns the applicationId field the provided argument.
	 * 
	 */
	public void setApplicationId( String applicationId ) {
		this.applicationId = applicationId;
	}
		
	/**
	 * Returns the token field.
	 * 
	 * @return
	 */
	public String getToken() {
		return token;
	}
	
	/**
	 * Assign the token field the provided argument.
	 */
	public void setToken( String token ) {
		this.token = token;
	}

	/**
	 * Returns a readable String representation of the instance's data
	 * 
	 * @return String
	 */
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("resultCode:" + resultCode );
		builder.append(", result:" + result );
		builder.append(", processingMsg:" + processingMsg );
		builder.append(", applicationId:" + applicationId );
		builder.append(", token:" + token );
		
		return builder.toString();
	}
	
	// attributes
	
	protected JsonResultCode resultCode = JsonResultCode.getDefaultValue();
	protected String result 			= null;
	protected String processingMsg 		= null; 
	protected String applicationId 		= null;
	protected String token 				= null;

}
	
