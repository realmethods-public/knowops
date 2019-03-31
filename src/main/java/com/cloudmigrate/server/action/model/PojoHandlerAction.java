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

import com.cloudmigrate.server.service.PojoHandler;

/**
 * Controller class to handle the uploading of a model by delegating to a JsonHandler.
 * 
 * @author realMethods, Inc.
 * 
 */
public class PojoHandlerAction extends ModelFileHandlerAction {

	/**
	 * default constructor
	 */
	public PojoHandlerAction() {
		// no_op
	}
	

	/**
	 * Assigns the rootPackageName field to the provided argument.
	 * 
	 * @param rootPackageName
	 */
	public void setRootPackageName( String rootPackageName ) {
		this.rootPackageName = rootPackageName;
	}
	

	@Override
	/**
	 * Handler method use to parse the provided modelFileName by delegating to a PojoHandler.
	 * 
	 * @param 	modelFileName
	 * @return	String
	 */
	protected String handleParseModel( String modelFileName ) {
		if ( new PojoHandler(rootPackageName).loadModel( modelFileName ) )
			return SUCCESS;
		else
			return ERROR;
	}

	// attributes
	protected String rootPackageName = null;
}
