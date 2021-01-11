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

import com.realmethods.server.service.XMIHandler;

/**
 * Controller class to handle the uploading of a model by delegating to the XMIHandler.
 * 
 * @author realMethods, Inc.
 * 
 */
public class XMIHandlerAction extends ModelFileHandlerAction {

	@Override
	/**
	 * Handles parsing the input model by delegating to the XMIHandler
	 * 
	 * @param	modelFileName
	 * @return	String
	 */
	protected String handleParseModel( String modelFileName ) {
		if ( new XMIHandler().loadModel( modelFileName ) )
			return SUCCESS;
		else
			return ERROR;
	}
}
