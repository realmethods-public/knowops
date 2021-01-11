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

/**
 * Controller class to handle the uploading of a YAML file
 * for parsing of the model into a realMethods Generation-Time Model.
 * 
 * @author realMethods, Inc.
 * 
 */
public class YamlHandler implements IModelParser {
	/**
	 * Constructor
	 */
	public YamlHandler() {
		// no_op
	}
	
	/**
	 * Uses an YamlParser to load the provided YAML (.yaml or .yml) file into the
	 * common realMethods Generation-Time Model.
	 * 
	 * Returns true/false on success or failure of loading the file into a Generation-Time model
	 * 
	 * @param emfFileName
	 * @return	boolean
	 */
	public boolean loadModel( String yamlFileName ) {
		try {
			new com.realmethods.codetemplate.parser.YamlParser(yamlFileName).run();
			return true;
		} catch (Exception exc) {
			LOGGER.log( Level.SEVERE, "YAML Parsing Failed", exc);
			return false;
		}
	}

// attributes
	
	private static final Logger LOGGER = Logger.getLogger(YamlHandler.class.getName());
}
