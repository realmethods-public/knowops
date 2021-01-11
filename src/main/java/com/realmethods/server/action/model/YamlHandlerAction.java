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
package com.realmethods.server.action.model;

import com.realmethods.server.service.YamlHandler;

/**
 * Controller class to handle the uploading of a model by delegating to a Yamlandler.
 * 
 * @author realMethods, Inc.
 * 
 */
public class YamlHandlerAction extends ModelFileHandlerAction {
	
	/**
	 * default constructor
	 */
	public YamlHandlerAction() {
		// no_op
	}
		
	
	@Override
	/**
	 * Handles parsing the model by delegating to a YamlHandler.
	 * 
	 * @param	modelFileName
	 * @return	String
	 */
	protected String handleParseModel( String modelFileName ) {
		if ( new YamlHandler().loadModel( modelFileName ) )
			return SUCCESS;
		else
			return ERROR;
	}
}
