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
package com.cloudmigrate.server.service;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller class to handle the uploading of a Eclipse Model Framework model
 * the Ecore handler for parsing of the model into a realMethods
 * Generation-Time Model.
 * 
 * @author realMethods, Inc.
 * 
 */
public class EMFHandler implements IModelParser {
	/**
	 * Constructor
	 */
	public EMFHandler() {
		// no_op
	}
	
	/**
	 * Uses an EcoreParser to load the provided EMF (.core or .ecore) file into the
	 * common realMethods Generation-Time Model.
	 * 
	 * Returns true/false on success or failure of loading the file into a Generation-Time model
	 * 
	 * @param emfFileName
	 * @return	boolean
	 */
	public boolean loadModel( String emfFileName ) {
		try {
			new com.cloudmigrate.codetemplate.parser.EcoreParser(emfFileName).run();
			return true;
		} catch (Exception exc) {
			LOGGER.log( Level.SEVERE, "Ecore Parsing Failed", exc);
			return false;
		}
	}

// attributes
	
	private static final Logger LOGGER = Logger.getLogger(EMFHandler.class.getName());
}
