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

import java.util.Date;
import java.util.Map;

import com.realmethods.entity.dao.FrameworkPackageDAO;
import com.realmethods.entity.dao.LocalModelDAO;


/**
 * Encapsulates data for a Build
 * 
 */
public class Build extends Base {

	public Build() {
		
	}
	
	/**
	 * Default Constructor
	 */
	public Build( Project project ) {
		modelId 				= project.getModelId();
		techStackPackageName 	= project.getTechStackPackageName();		
		buildOptions			= new BuildOptions(project.getOptions());
		buildNumber				= project.getBuilds().size() + 1;
	}

	/**
	 * Constructor with a Build
	 * 
	 * @param object	copy source
	 */
	public Build(Build object) {
		super();

		if (object == null) {
			throw new IllegalArgumentException("Build( Build object ) - object arg is null.");
		}

		copy(object);
	}

	/**
	 * Returns the tech stack package name field.
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
	 * Returns the build options field.
	 * 
	 * @return 	options		project options as a Map 
	 */
	public BuildOptions getBuildOptions() {
		return this.buildOptions;
	}

	/**
	 * Assigns the build options field using the provided argument.
	 * 
	 * @param	options		project options
	 */
	public void setBuildOptions(BuildOptions options) {
		this.buildOptions = options;
	}
	
	/**
	 * Return the build start date time
	 * @return
	 */
	public Date getStartDateTime() {
		return startDateTime;
	}
	
	/**
	 * Assign the startDateTime attribute
	 * @param dateTime
	 */
	public void setStartDateTime( Date dateTime ) {
		this.startDateTime = dateTime;
	}

	/**
	 * Return the build end date time
	 * @return
	 */
	public Date getEndDateTime() {
		return endDateTime;
	}
	
	/**
	 * Assign the endDateTime attribute
	 * @param dateTime
	 */
	public void setEndDateTime( Date dateTime ) {
		this.endDateTime = dateTime;
	}


	/**
	 * Return the build status
	 * @return
	 */
	public BuildStatus getStatus() {
		return status;
	}
	
	/**
	 * Assign the buildStatus attribute
	 * @param status
	 */
	public void setStatus( BuildStatus status ) {
		this.status = status;
	}

	/**
	 * Return the log file URL
	 * @return	log file URL
	 */
	public String getLogFileURL() {
		return this.logFileURL;
	}
	
	/**
	 * Assign the logFileURL attribute
	 * @param status
	 */
	public void setLogFileURL( String logFileURL ) {
		this.logFileURL = logFileURL;
	}


	/**
	 * Return the buildNubmer
	 * @return	buildNumber
	 */
	public int getBuildNumber() {
		return this.buildNumber;
	}

	/**
	 * Assign the buildNumber attribute
	 * @param buildNumber
	 */
	public void setbuildNumber( int buildNumber ) {
		this.buildNumber = buildNumber;
	}


	/**
	 * Performs a shallow copy of intrinsic and enumerated types only.
	 * 
	 * @param object 	Build copy source
	 */
	public void shallowCopy(Build object){
		if (object == null) {
			throw new IllegalArgumentException(" Build:shallowCopy(..) - object cannot be null.");
		}

		// ------------------------------------------
		// Set member attributes
		// ------------------------------------------
		this.techStackPackageName 	= object.techStackPackageName;
		this.modelId 				= object.modelId;
		this.buildOptions			= object.buildOptions;
		this.startDateTime			= object.startDateTime;
		this.endDateTime			= object.endDateTime;
		this.logFileURL				= object.logFileURL;
		this.buildNumber			= object.buildNumber;
		this.status					= object.status;
		this.id						= object.id;
		
	}

	/**
	 * Performs a deep copy which is a shallow copy but includes
	 * member attributes that behave as associations to other classes.
	 * 
	 * @param object	Build copy source
	 */
	public void copy(Build object) {
		if (object == null) {
			throw new IllegalArgumentException(" Build:copy(..) - object cannot be null.");
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
		returnString.append("buildOptions = " 		+ this.buildOptions);
		returnString.append("startDateTime = " 		+ this.startDateTime );
		returnString.append("endDateTime = " 		+ this.endDateTime );
		returnString.append("logFileURL = " 		+ this.logFileURL);
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
		return ("Build");
	}

	@Override
	/**
	 * Comparison method to determine if the provided object is equivalent to this instance.
	 * 
	 * It does so by ensuring it is non-null, of Build type, and the id fields are equivalent.
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
	
	/**
	 * Creates a readable summary of the build
	 * 
	 * @return		build summary
	 */
	public String readableSummary() {
		StringBuilder summary = new StringBuilder();
		
		try {
			LocalModel model 		= new LocalModelDAO().findLocalModel( this.modelId );
			FrameworkPackage pkg 	= new FrameworkPackageDAO().findByNameorId( this.techStackPackageName );
			
			summary.append( "Build" );
			summary.append( "<br>    Start Date/Time: " + startDateTime );
			summary.append( "<br>    End Date/Time: " + endDateTime );
			summary.append( "<br>    Number:    " + buildNumber );
			summary.append( "<br>    Status:    " + status );
			summary.append( "<br>" );
			summary.append( "<br>Model" );
			summary.append( "<br>    File : " + model.getOriginalFileName() );
			summary.append( "<br>    Type : " + model.getModelType().toString() );
			summary.append( "<br>" );
			summary.append( "<br>Tech Stack Package" );
			summary.append( "<br>    Name :       " + pkg.getName() );
			summary.append( "<br>    Short Name : " + pkg.getShortName());
			summary.append( "<br>    Version :    " + pkg.getVersion() );
			summary.append( "<br>    Release :    " + pkg.getReleaseStatus() );
			summary.append( "<br>" );
			summary.append( "<br>Build Options" );
			summary.append( "<br>    Application" );
			summary.append( "<br>      Name :    " + buildOptions.getAppName() );
			summary.append( "<br>      Author :  " + buildOptions.getAppAuthor() );
			summary.append( "<br>      Version : " + buildOptions.getAppVersion() );
			summary.append( "<br>    Git" );
			summary.append( "<br>      Host : "  + buildOptions.getGitHost() );
			summary.append( "<br>      Repo : "  + buildOptions.getGitRepo() );
			summary.append( "<br>    Docker" );
			summary.append( "<br>      Host : "  + buildOptions.getDockerHost() );
			summary.append( "<br>      Repo : "  + buildOptions.getDockerRepo() );
			summary.append( "<br>    Kubernetes" );
			summary.append( "<br>      inUse :  "  + buildOptions.usingKubernetes() );
			summary.append( "<br>      host :   "  + buildOptions.getK8Host() );
			summary.append( "<br>      target : "  + buildOptions.getK8HostTarget() );
			summary.append( "<br>    Terraform" );
			summary.append( "<br>      inUse :    "  + buildOptions.usingTerraform());
			summary.append( "<br>      provider : "  + buildOptions.getTerraformProvider() );
			
		} catch( Exception exc ) {
			
		}
		
		return( summary.toString() );
	}
	
	
	// attributes
	protected int buildNumber				= 0;
	protected Long modelId					= null;
	protected String techStackPackageName	= null;
	protected BuildOptions buildOptions		= null;
	protected BuildStatus status			= BuildStatus.getDefaultValue();
	protected Date startDateTime			= new Date();
	protected Date endDateTime				= null;
	protected String logFileURL				= null;
}
