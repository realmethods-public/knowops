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
package com.cloudmigrate.server.action.model;

import com.cloudmigrate.server.service.JsonHandler;

/**
 * Controller class to handle the uploading of a model by delegating to a JsonHandler.
 * 
 * @author realMethods, Inc.
 * 
 */
public class JsonHandlerAction extends ModelFileHandlerAction {
	
	/**
	 * default constructor
	 */
	public JsonHandlerAction() {
		// no_op
	}
		
	
	@Override
	/**
	 * Handles parsing the model by delegating to a JsonHandler.
	 * 
	 * @param	modelFileName
	 * @return	String
	 */
	protected String handleParseModel( String modelFileName ) {
		if ( new JsonHandler().loadModel( modelFileName ) )
			return SUCCESS;
		else
			return ERROR;
	}
}
