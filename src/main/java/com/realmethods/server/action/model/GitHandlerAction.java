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

import com.realmethods.api.PojoParams;
import com.realmethods.server.service.GitHandler;

/**
 * Controller class to handle the uploading of a model by delegating to a JsonHandler.
 * 
 * @author realMethods, Inc.
 * 
 */
public class GitHandlerAction extends ModelFileHandlerAction {

	/**
	 * default constructor
	 */
	public GitHandlerAction() {
		// no_op
	}
	

	/**
	 * Assigns the javaRootPackageNames field to the provided argument.
	 * 
	 * @param javaRootPackageNames
	 */
	public void setJavaRootPackageNames( String[] javaRootPackageNames ) {
		this.javaRootPackageNames = javaRootPackageNames;
	}

	/** 
	 * Assigns the primaryKeyPattern field to the provided argument.
	 * 
	 * @param primaryKeyPattern
	 */
	public void setPrimaryKeyPattern( String primaryKeyPattern) {
		this.primaryKeyPattern = primaryKeyPattern;
	}	
	
	/** 
	 * Git URL to pull from
	 * @param gitPullUrl
	 */
	public void setGitPullUrl( String gitPullUrl ) {
		this.gitPullUrl = gitPullUrl;
	}

	@Override
	/**
	 * Core execution routine, delegates internally to uploadModel.
	 * 
	 * @return String
	 */
	public String execute() {
		String handled = handleParseModel(this.gitPullUrl);
		
		if ( handled == SUCCESS ) {
			saveModel( this.gitPullUrl, this.gitPullUrl );
		}
		
		return handled;
	}
	
	@Override
	/**
	 * Handler method use to parse the provided modelFileName by delegating to a GitHandler.
	 * 
	 * @param 	modelFileName
	 * @return	String
	 */
	protected String handleParseModel( String modelFileName ) {
		/////////////////////////////////////////////////////////////////////
		// if pojoParams have not been explicitly assigned, interrogate 
		// the internal shared localModel
		/////////////////////////////////////////////////////////////////////
		if ( javaRootPackageNames == null ) {
			if ( getSharedModel() != null )
				pojoParams = getSharedModel().getPojoParams();
		}
		else {
			pojoParams = new PojoParams( this.javaRootPackageNames, this.primaryKeyPattern );
		}
		
		if ( new GitHandler(pojoParams).loadModel( modelFileName ) ) {
			return SUCCESS;
		}
		else
			return ERROR;
	}

	
	// attributes
	protected String[] javaRootPackageNames		= null;
	protected String primaryKeyPattern			= null;	
	protected String gitPullUrl 				= null;
}
