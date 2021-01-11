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
package com.realmethods.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.realmethods.api.GitParams;

/**
 * Encapsulates data for a Project, to the core components of a project-a-code YAML file
 * 
 */
public class Project extends BaseTransactionalEntity {

	/**
	 * Default Constructor
	 */
	public Project() {
	}

	/**
	 * Constructor with a Project
	 * 
	 * @param object	copy source
	 */
	public Project(Project object) {
		super();

		if (object == null) {
			throw new IllegalArgumentException("Project( Project object ) - object arg is null.");
		}

		copy(object);
	}

	/**
	 * Returns the tech stack package id field.
	 * 
	 * @return	tech stack package name
	 */
	public String getTechStackPackageName() {
		return this.techStackPackageName;
	}

	/**
	 * Assigns the techStackPackageName  field using the provided argument.
	 * 
	 * @param techStackPackageName	tech stack package name
	 */
	public void setTechStackPackageName(String techStackPackageName) {
		this.techStackPackageName = techStackPackageName;
	}

	/**
	 * Returns the modelId field.
	 * 
	 * @return 	modelId		model id
	 */
	public Long getModelId() {
		return this.modelId;
	}

	/**
	 * Assigns the modelId field using the provided argument.
	 * 
	 * @param modelId 		the model id
	 */
	public void setModelId (Long modelId ) {
		this.modelId  = modelId ;
	}

	/**
	 * Returns the options field.
	 * 
	 * @return 	options		project options as a Map 
	 */
	public Map<String,String> getOptions() {
		return this.options;
	}

	/**
	 * Assigns the options field using the provided argument.
	 * 
	 * @param	options		project options
	 */
	public void setOptions(Map<String,String> options) {
		this.options = options;
	}
	
	/**
	 * history of builds
	 * @return	builds
	 */
	public List<Build> getBuilds() {
		return this.builds;		
	}

	/**
	 * returns the gitParams variable
	 * @return		git params
	 */
	public GitParams getGitParams() {
		return this.gitParams;
	}

    /**
	 * Assigns the gitParmsfield using the provided argument
	 *
	 * @params	gitParams	input git parameters
	 */
	public void setGitParams( GitParams gitParams ) {
		this.gitParams = gitParams;
	}
	
	/**
	 * assign builds - only useful for ORM and not for direct invocation
	 * 
	 * @param builds	list of builds
	 */
	public void setBuilds( List<Build> builds ) {
		this.builds = builds;

		// ==============================
		// descending sort by build number 
		// ==============================
		if ( this.builds != null )
			Collections.sort(this.builds, new SortByBuildNumber()); 
	}
	
	/**
	 * Add a build to the list of builds
	 * @param	Build
	 */
	public void addBuild( Build build) {
		builds.add(build);
	}

	/**
	 * Performs a shallow copy of intrinsic and enumerated types only.
	 * 
	 * @param object 	Project copy source
	 */
	public void shallowCopy(Project object){
		if (object == null) {
			throw new IllegalArgumentException(" Project:shallowCopy(..) - object cannot be null.");
		}

		// ------------------------------------------
		// Set member attributes
		// ------------------------------------------
		this.techStackPackageName = object.techStackPackageName;
		this.modelId 		= object.modelId;
		this.options		= object.options;
		this.gitParams		= object.gitParams;

		super.shallowCopy( object );

	}

	/**
	 * Performs a deep copy which is a shallow copy but includes
	 * member attributes that behave as associations to other classes.
	 * 
	 * @param object	Project copy source
	 */
	public void copy(Project object) {
		if (object == null) {
			throw new IllegalArgumentException(" Project:copy(..) - object cannot be null.");
		}
		
		shallowCopy(object);
	}

	@Override
	/**
	 * Returns a string representation of the object.
	 * 
	 * @return String
	 */
	public String toString() {
		StringBuilder returnString = new StringBuilder();

		returnString.append("modelId = " 			+ this.modelId + ", ");
		returnString.append("techStackPackageName = " + this.techStackPackageName + ", ");
		returnString.append("options = " 			+ this.options);
        returnString.append( super.toString() );

		return returnString.toString();
	}

	@Override
    /**
     * Returns the type of this object.
     * 
     * @return	object type
     */
	public String getObjectType() {
		return ("Project");
	}

	@Override
	/**
	 * Comparison method to determine if the provided object is equivalent to this instance.
	 * 
	 * It does so by ensuring it is non-null, of Project type, and the id fields are equivalent.
	 * 
	 * @param	object	object to compare to
	 * @return	boolean
	 */	
	public boolean equals(Object object) {
		return ( super.equals(object) );
	}
	
	
	@Override
	/**
	 * Hash off the super class
	 * 
	 * @return	hash code
	 */
	public int hashCode() {
		return super.hashCode();
	}
	
	// attributes
	protected Long modelId					= null;
	protected String techStackPackageName		= null;
	protected Map<String, String> options 	= null;
	protected List<Build> builds		 	= new ArrayList();
	protected GitParams gitParams			= new GitParams();

	// ============================================= 
	// inner class for Sorting the builds by build number
	// ============================================= 
	class SortByBuildNumber implements Comparator<Build> { 
		// ============================================= 
		// Used for sorting in descending order of 
		// build number9
		// ============================================= 
		public int compare(Build a, Build b) { 
			return b.buildNumber - a.buildNumber; 
		} 
	} 	
}
