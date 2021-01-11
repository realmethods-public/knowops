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
package com.realmethods.codetemplate;

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
	 * @param options		project generation options
	 * @param workingDir	working directory
	 * @param microServices	using microservices
	 */
	public GenerateAppOptions(Map<String, String> options, String workingDir, boolean microServices) {
		this.options = options;
		this.workingDir = workingDir;
		this.microServices = microServices;
	}

	/**
	 * The entire set of key/value pairing seen as project generation
	 * options. These options are comprised primarily of all the values providing
	 * during a project generation session.
	 * 
	 * @return	Map			options
	 */
	public Map<String, String> getOptions() {
		return this.options;
	}

	/**
	 * The absolute path to begin generating into
	 * 
	 * @return String		current working directory
	 */
	public String getWorkingDir() {
		return this.workingDir;
	}

	/**
	 * flag to indicate that architecture type (true:microservice,
	 * false:monolithic
	 * 
	 * @return boolean		true/false using microservices
	 */
	public boolean getMicroServices() {
		return this.microServices;
	}

	// attributes
	// --------------------------------------------------------------------
	// input project generation options
	// --------------------------------------------------------------------
	protected Map<String, String> options	= null;
	// --------------------------------------------------------------------
	// working directory
	// --------------------------------------------------------------------
	protected String workingDir				= null;
	// --------------------------------------------------------------------
	// microservice flag
	// --------------------------------------------------------------------
	protected boolean microServices 		= false;
	
}
