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

import com.realmethods.server.service.EMFHandler;

/**
 * Controller class to handle the uploading of a model by delegating to the EMFHandler.
 * 
 * @author realMethods, Inc.
 * 
 */
public class EMFHandlerAction extends ModelFileHandlerAction {

	/**
	 * default constructor
	 */
	public EMFHandlerAction() {
		// no_op
	}

	@Override
	/**
	 * Overload used to handle parsing of an Eclipse EMF file (.ecore or .core), by delegating to an 
	 * EMFHandler instance.
	 * 
	 * @param	modelFileName
	 * @return	String
	 */
	protected String handleParseModel( String modelFileName ) {
		if ( new EMFHandler().loadModel( modelFileName ) )
			return SUCCESS;
		else
			return ERROR;
	}
	
	// attributes 
}
