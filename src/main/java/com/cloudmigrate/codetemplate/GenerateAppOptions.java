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
package com.cloudmigrate.codetemplate;

import java.util.Map;

/**
 * Helper class to hold the runtime variables required as input to the app
 * generation process
 * 
 * @author realMethods, Inc.
 * 
 */
public class GenerateAppOptions {
	/**
	 * default constructor - inaccessible
	 */
	protected GenerateAppOptions() {
		// no_op
	}

	/**
	 * Sole external facing constructor
	 * 
	 * @param options
	 * @param workingDir
	 * @param microServices
	 */
	public GenerateAppOptions(Map<String, String> options, String workingDir, boolean microServices) {
		this.options = options;
		this.workingDir = workingDir;
		this.microServices = microServices;
	}

	/**
	 * The entire set of key/value pairing seenn as application generation
	 * options. These options are comprised primarily of all the values read in
	 * at runtime found in the options.xml file of any framework packaging where
	 * options are required. The root directory of any package MUST contain an
	 * options.xml file to help determine the overall options of the application
	 * which include such thing as application name, logo url, author name,
	 * package name, and so. options in this file are labeled mandatory or not
	 * 
	 * @return
	 */
	public Map<String, String> getOptions() {
		return this.options;
	}

	/**
	 * The absolute path to begin generating into
	 * 
	 * @return String
	 */
	public String getWorkingDir() {
		return this.workingDir;
	}

	/**
	 * flag to indicate that architecture type (true:microservice,
	 * false:monolithic
	 * 
	 * @return boolean
	 */
	public boolean getMicroServices() {
		return this.microServices;
	}

	// attributes
	protected Map<String, String> options	= null;
	protected String workingDir				= null;
	protected boolean microServices 		= false;
	
}
