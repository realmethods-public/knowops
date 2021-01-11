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

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helper class to hold the general statics accumulated during an applicaton
 * generation session.  File counts are incremented by receiving a fileName and
 * increasing its count by examining its extension and adding one if already accounted for or
 * assigning one for the first occurence.
 * 
 * @author realMethods, Inc.
 * 
 */
public class GenerateAppStats extends AppGenObject {
	
	/**
	 * Default constructor
	 */
	public GenerateAppStats() {
		// nothing to initialize
	}

	/** 
	 * Increments the overall line count by the provided argument.
	 * 
	 * @param numLines
	 */
	public void incrementLineCount( long numLines )
	{ this.totalLines += numLines; }
	
	/**
	 * Return the totalLines field.
	 * 
	 * @return long
	 */
	public long getTotalLines()
	{ return this.totalLines; }
	
	/**
	 * Increment the file counter by examining the fileName's extension.
	 * 
	 * @param fileName
	 */
	public void increment(String fileName) {
		int lastExt = fileName.lastIndexOf('.');
		Long total = 0L;
		String key = "Other Files";

		if (lastExt > -1) {
			key = fileName.substring(lastExt);
		}

		if (extMapping.containsKey(key)) {
			total = extMapping.get(key);
		}

		total++;

		extMapping.put(key, total);

		this.totalFilesIncrement();

	}

	/**
	 * increment the internal counters that track by
	 * UMLRootType, including subsystems, components, and classes
	 * 
	 * @param type
	 */
	public void increment(UMLRootType type) {
		switch (type) {
		case SUBSYSTEMS:
			this.subsystemIncrement();
			break;
		case COMPONENTS:
			this.componentIncrement();
			break;
		case CLASSES:
			this.classIncrement();
			break;
		case INTERFACES:
			this.interfaceIncrement();
			break;
		case SERVICES:
			this.serviceIncrement();
			break;
		case ENUMS:
			this.enumIncrement();
			break;
		default:
			final String msg = "GenerateAppStats.increment() - unrecognized UML Root Type : " + type.name();
			LOGGER.log( Level.WARNING, msg );
			break;
		}
	}

	/**
	 * Return the totalNumSubsystems field.
	 * 
	 * @return long
	 */
	public long totalNumSubsystems() {
		return totalNumSubsystems;
	}

	/**
	 * Returns the totalNumComponents field.
	 * 
	 * @return long
	 */
	public long totalNumComponents() {
		return totalNumComponents;
	}

	/**
	 * Returns the totalNumClasses field.
	 * 
	 * @return long
	 */
	public long totalNumClasses() {
		return totalNumClasses;
	}

	/**
	 * Returns the totalNumServices field.
	 * 
	 * @return long
	 */
	public long totalNumServices() {
		return this.totalNumServices;
	}

	/**
	 * Returns the totalNumInterfaces field.
	 * 
	 * @return long
	 */
	public long totalNumInterfaces() {
		return this.totalNumInterfaces;
	}

	/**
	 * Returns the totalNumEnums field.
	 * 
	 * @return long
	 */
	public long totalNumEnums() {
		return this.totalNumEnums;
	}

	/**
	 * Increments the totalNumSubsystems by 1
	 */
	public void subsystemIncrement() {
		totalNumSubsystems++;
	}

	/**
	 * Increments the totalNumComponents by 1
	 */
	public void componentIncrement() {
		totalNumComponents++;
	}

	/**
	 * Increments the totalNumClasses by 1
	 */	
	public void classIncrement() {
		totalNumClasses++;
	}

	/**
	 * Increments the totalNumServices by 1
	 */		
	public void serviceIncrement() {
		this.totalNumServices++;
	}

	/**
	 * Increments the totalNumInterfaces by 1
	 */			
	public void interfaceIncrement() {
		this.totalNumInterfaces++;
	}

	/**
	 * Increments the totalNumEnums by 1
	 */				
	public void enumIncrement() {
		this.totalNumEnums++;
	}

	/**
	 * Increments the totalFilesProcessed by 1
	 */				
	public void totalFilesIncrement() {
		this.totalFilesProcessed++;
	}

	/**
	 * Returns the totalFilesProcessed field.
	 * @return long
	 */
	public long totalFilesProcessed() {
		return this.totalFilesProcessed;
	}

	/**
	 * Returns the totalFilesProcessed field.
	 * @return long
	 */
	public long getTotalProcessed() {
		return this.totalFilesProcessed;
	}

	/**
	 * Returns the extMapping field which is Map that contains the
	 * file extensions as the key, the totals count as the value.
	 * 
	 * @return Map<String,String>
	 */
	public Map<String, Long> getExtMapping() {
		return extMapping;
	}

	/**
	 * Returns generateStatus field.
	 * 
	 * @return String
	 */
	public String getGenerateStatus() {
		return this.generateStatus;
	}

	/**
	 * Assigns the generateStatus field the provided argument.
	 * 
	 * @param status
	 */
	public void setGenerateStatus(String status) {
		this.generateStatus = status;
	}

	/**
	 * Returns the build status field.
	 * 
	 * @return String
	 */
	public String getBuildStatus() {
		return this.buildStatus;
	}

	/**
	 * Assign the buildStatus field the provided argument.
	 * 
	 * @param status
	 */
	public void setBuildStatus(String status) {
		this.buildStatus = status;
	}

	/**
	 * Returns the executeStatus field.
	 * 
	 * @return String
	 */
	public String getExecuteStatus() {
		return this.executeStatus;
	}

	/**
	 * Assigns the executionStatus field the provided argument.
	 * 
	 * @param status
	 */
	public void setExecuteStatus(String status) {
		this.executeStatus = status;
	}
	
	/**
	 * Returns the resultLocation field.
	 * 
	 * @return String
	 */
	public String getResultLocation() {
		return resultLocation;
	}

	/**
	 * Assigns the resultLocation field the provided argument.
	 * 
	 * @param resultLocation
	 */
	public void setResultLocation(String resultLocation) {
		this.resultLocation = resultLocation;
	}

	/**
	 * Returns the resultPath field.
	 * 
	 * @return String
	 */
	public String getResultPath() {
		return resultPath;
	}

	/**
	 * Assigns the resultPath field the provided argument.
	 * 
	 * @param resultPath
	 */
	public void setResultPath(String resultPath) {
		this.resultPath = resultPath;
	}

	// attributes
	protected long totalFilesProcessed 		= 0;
	protected long totalNumEnums 			= 0;
	protected long totalNumInterfaces 		= 0;
	protected long totalNumServices 		= 0;
	protected long totalNumClasses 			= 0;
	protected long totalNumComponents 		= 0;
	protected long totalNumSubsystems 		= 0;
	protected String generateStatus 		= null;
	protected String buildStatus 			= null;
	protected String executeStatus 			= null;
	protected Map<String, Long> extMapping = new HashMap<>();
	protected String resultLocation 		= null;
	protected String resultPath 			= null;
	protected long totalLines				= 0;
	private static final Logger LOGGER = Logger.getLogger(GenerateAppStats.class.getName());

}
