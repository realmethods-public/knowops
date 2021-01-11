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
package com.realmethods.server.service;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.*;

/**
 * Controller class to handle the uploading of a JSON file,
 * calling the appropriate parser to handle parsing of the model into a realMethods
 * Generation-Time Model.
 * 
 * @author realMethods, Inc.
 * 
 */
public class JsonHandler implements IModelParser {
	
	/**
	 * Default Constructor
	 */
	public JsonHandler() {
		// no_op
	}

	/**
	 * Load the provided JSON file into a registered JSONModelHandler to create the internal realMethods Creation-Time model
	 * 
	 * @param jsonFile
	 * @return boolean
	 */
	public boolean loadModel( String jsonFile ) {
		try {
			new com.realmethods.codetemplate.tools.JSONModelHandler().loadModelData( jsonFile );
		} catch (Exception exc) {
			final String errMsg = "Failed to create model from JSON file" + jsonFile
					+ ". Checks its structural integrity.";
			LOGGER.log(Level.SEVERE, errMsg, exc);
			return( false );
		}
		return (true);
	}

	// attributes
	private static final Logger LOGGER = Logger.getLogger(JsonHandler.class.getName());

}
