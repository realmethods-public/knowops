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

import com.realmethods.codetemplate.parser.ModelParserFactory;

/**
 * Controller class to handle the uploading of an XMI file containing UML artifacts,
 * calling the appropriate parser to handle parsing of the model into a realMethods
 * Generation-Time Model.
 * 
 * @author realMethods, Inc.
 * 
 */
public class XMIHandler implements IModelParser {
	
	/**
	 * default constructor
	 */
	public XMIHandler() {
		// no_op
	}

	/**
	 * Uses singleton ModelParserFactory to create an XMI file parser to parse the provided file 
	 * 
	 * Returns true/false depending upon the success of the loading and parsing.
	 * 
	 * @param modelFileName
	 * @return
	 */
	public boolean loadModel( String modelFileName ) 
	{
		try {
			ModelParserFactory.getInstance().createXMLFileParser(modelFileName).run();
		} catch (Exception exc) {
			LOGGER.log( Level.SEVERE, "ModelParserFactory failed creating XMLFileParser for " + modelFileName, exc);
			return false;
		}
		return true;
	}

	// attributes 
	
	private static final Logger LOGGER = Logger.getLogger(XMIHandler.class.getName());
	
}
