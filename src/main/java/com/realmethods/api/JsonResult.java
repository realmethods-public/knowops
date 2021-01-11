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

/**
 * Helper class to encapsulate the results of making a call to the goFramework RESTful API.
 * 
 * @author realMethods, Inc.
 *
 */
public class JsonResult
{
	/**
	 * checks if the resultCode type is SUCCESS
	 * @return
	 */
	public boolean isSuccess() {
		return resultCode == JsonResultCode.SUCCESS;		
	}

	/**
	 * Assigns the resultCode to type SUCCESS
	 */
	public void success() {
		resultCode = JsonResultCode.SUCCESS;
	}

	/**
	 * Assigns the resultCode to type SUCCESS and the provided msg
	 * @param	msg
	 */
	public void success( String msg ) {
		this.success();
		setProcessingMessage( msg );
	}

	/**
	 * Assigns the resultCode to type ERROR
	 */
	public void error() {
		resultCode = JsonResultCode.REQUEST_EXECUTION_ERROR;
	}

	/**
	 * Assigns the resultCode to type ERROR and provided msg
	 * @param	msg
	 */
	public void error( String msg ) {
		this.error();
		setProcessingMessage( msg);
	}

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
	 * Returns a readable String representation of the instance's data
	 * 
	 * @return String
	 */
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("resultCode:" + resultCode );
		builder.append(", result:" + result );
		builder.append(", processingMsg:" + processingMsg );
		
		return builder.toString();
	}
	
	// attributes
	
	//////////////////////////////////////////////////////////
	// result code encapsulated
	//////////////////////////////////////////////////////////
	protected JsonResultCode resultCode = JsonResultCode.getDefaultValue();
	//////////////////////////////////////////////////////////
	// result of processing
	//////////////////////////////////////////////////////////
	protected String result 			= null;
	//////////////////////////////////////////////////////////
	// processing message
	//////////////////////////////////////////////////////////
	protected String processingMsg 		= null; 

}
	
