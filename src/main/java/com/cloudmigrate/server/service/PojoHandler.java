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

import com.cloudmigrate.codetemplate.parser.ModelParserFactory;
import com.cloudmigrate.common.helpers.AppGenHelper;

/**
 * Controller class to handle the uploading of a JAR  file containing plain old java objects,
 * calling the appropriate parser to handle parsing of the model into a realMethods
 * Generation-Time Model.
 * 
 * @author realMethods, Inc.
 * 
 */
public class PojoHandler implements IModelParser {
	/**
	 * Default constructor
	 */
	public PojoHandler() {
		// no_op
	}
	
	/**
	 * Constructor
	 * 
	 * The rootPackageName, if provided, is used by registered POJO handler as to where
	 * the required POJOs are located within the JAR file.
	 * 
	 * @param rootPackageName
	 */
	public PojoHandler( String rootPackageName ) {
		this.rootPackageName = rootPackageName;
	}

	/**
	 * Uses the ModelParserFactory to help parse the provided POJO file into the common
	 * cloudMigrate Generation-Time Model.
	 * 
	 * @param pojoFile
	 * @return
	 */
	public boolean loadModel( String pojoFile ) 
	{
		try {
			// the JAR file should be an archive requiring Unzipping.
			// The return is the root directory of the unzipped contents.
			String unzipRootDir = AppGenHelper.unzip( pojoFile );
			
			// access the ModelParserFactory to leverage the registered handler to perform
			// model parsing and common cloudMigrate Generation-Time model
			ModelParserFactory.getInstance().createJavaDirParser(unzipRootDir, rootPackageName).run();
		} catch (Exception exc) {
			String errMsg = "Failed to reverse engineer from JAR file " + pojoFile
					+ ". Checks it only contains POJOs - ";
			LOGGER.log(Level.SEVERE, errMsg, exc );
			return( false );
		}
		return (true);
	}

	// attributes
	private String rootPackageName 		= null;
	private static final Logger LOGGER 	= Logger.getLogger(PojoHandler.class.getName());

}
