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

import com.cloudmigrate.codetemplate.parser.*;

import java.util.HashMap;
import java.util.Map;

import java.util.logging.Logger;

/**
 * Base class of all classes in the application generation module.
 * 
 * Provides logging for backwards compatibility situations where the
 * sub-class doesn't have it's own LOGGER.
 * 
 * @author realMethods, Inc.
 * 
 */
public class AppGenObject {

	/**
	 * Default constructor
	 */
	public AppGenObject() {
		// no_op
	}

	public static void log(String msg) {
		logInfoMessage(msg);
	}

	public static void logMessage(String msg) {
		logInfoMessage(msg);
	}

	public static void logWarnMessage(String msg) {
		LOGGER.warning(msg);
	}

	public static void logInfoMessage(String msg) {
		LOGGER.info(msg);
	}

	public static void logErrorMessage(String msg) {
		LOGGER.severe(msg);
	}

	public static void logDebugMessage(String msg) {
		LOGGER.warning(msg);
	}

	public void logError(String msg) {
		logErrorMessage(msg);
	}

	public Map<String, String> getTags() {
		return tags;
	}

	public void setTags(Map<String, String> tags) {
		this.tags = tags;
	}

	// attributes
	protected Map<String, String> tags = new HashMap<>();
    private static final Logger LOGGER = Logger.getLogger(AppGenObject.class.getName());

}
