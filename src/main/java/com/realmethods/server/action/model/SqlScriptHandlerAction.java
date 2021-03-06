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

import com.realmethods.common.helpers.DBConnInfo;
import com.realmethods.server.service.SqlScriptHandler;

/**
 * Controller class to handle the uploading of a model by delegating to the SqlScriptHandler.
 * 
 * @author realMethods, Inc.
 * 
 */
public class SqlScriptHandlerAction extends ModelFileHandlerAction {
	
	// default construtor
	public SqlScriptHandlerAction() {
		// no_op
	}

	@Override
	public String getUploadedFileName() {
		
		if ( getSharedModel() != null )
			return( getSharedModel().getOriginalFileName() );
		return (super.getUploadedFileName());
	}

	@Override
	/**
	 * Handles parsing the input model by delegating to the XMIHandler
	 * 
	 * @param	modelFileName
	 * @return	String
	 */
	protected String handleParseModel( String modelFileName ) {
		if ( new SqlScriptHandler(dbConnInfo).loadModel( modelFileName, getUploadedFileName() ) )
			return SUCCESS;
		else
			return ERROR;
	}

	// attributes
	protected transient DBConnInfo dbConnInfo = new DBConnInfo();	
}
