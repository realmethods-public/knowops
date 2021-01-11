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

import java.util.Map;


/**
 * Encapsulates data for build options
 * 
 */
public class BuildOptions{

	/**
	 * default constructor for ORM
	 */
	public BuildOptions() {
		// intentionally does nothing
	}

	/**
	 * default constructor for internal invocation
	 */
	public BuildOptions(Map<String,String> options) {
		assignOptions( options ); 
	}


	/**
	 * Constructor with a BuildOptions
	 * 
	 * @param object	copy source
	 */
	public BuildOptions(BuildOptions object) {
		super();

		if (object == null) {
			throw new IllegalArgumentException("BuildOptions( BuildOptions object ) - object arg is null.");
		}

		copy(object);
	}
	public String getUsingKubernetes() {
		return( this.usingKubernetes );
	}

	public void setUsingKubernetes( String usingKubernetes ) {
		this.usingKubernetes = usingKubernetes;
	}

	public String getUsingTerraform() {
		return( this.usingTerraform );
	}

	public void setUsingTerraform( String usingTerraform ) {
		this.usingTerraform = usingTerraform;
	}

	public String getUsingArtifactRepo() {
		return( this.usingArtifactRepo );
	}

	public void setUsingArtifactRepo( String usingArtifactRepo ) {
		this.usingArtifactRepo = usingArtifactRepo;
	}

	public String getDockerHost() {
		return( this.dockerHost );
	}

	public void setDockerHost( String dockerHost ) {
		this.dockerHost = dockerHost;
	}

	public String getDockerRepo() {
		return( this.dockerRepo );
	}

	public void setDockerRepo( String dockerRepo ) {
		this.dockerRepo = dockerRepo;
	}

	public String getGitHost() {
		return( this.gitHost );
	}

	public void setGitHost( String gitHost ) {
		this.gitHost = gitHost;
	}

	public String getGitRepo() {
		return( this.gitRepo );
	}

	public void setGitRepo( String gitRepo ) {
		this.gitRepo = gitRepo;
	}

	public String getK8Host() {
		return( this.k8Host );
	}

	public void setK8Host( String k8Host ) {
		this.k8Host = k8Host;
	}

	public String getK8HostTarget() {
		return( this.k8HostTarget );
	}

	public void setK8HostTarget( String k8HostTarget ) {
		this.k8HostTarget = k8HostTarget;
	}

	public String getTerraformProvider() {
		return( this.terraformProvider );
	}

	public void setTerraformProvider( String terraformProvider ) {
		this.terraformProvider = terraformProvider;
	}

	public String getArtifactType() {
		return( this.artifactType );
	}

	public void setArtifactType( String artifactType ) {
		this.artifactType = artifactType;
	}

	public String getAppName() {
		return( this.appName );
	}

	public void setAppName( String appName ) {
		this.appName = appName;
	}

	public String getAppAuthor() {
		return( this.appAuthor );
	}

	public void setAppAuthor( String appAuthor ) {
		this.appAuthor = appAuthor;
	}

	public String getAppVersion() {
		return( this.appVersion );
	}

	public void setAppVersion( String appVersion ) {
		this.appVersion = appVersion;
	}

	public String getCicdPlatform() {
		return( this.cicdPlatform );
	}

	public void setCicdPlatform( String cicdPlatform ) {
		this.cicdPlatform = cicdPlatform;
	}

	/**
	 * Performs a shallow copy of intrinsic and enumerated types only.
	 * 
	 * @param object 	BuildOptions copy source
	 */
	public void shallowCopy(BuildOptions object){
		if (object == null) {
			throw new IllegalArgumentException(" BuildOptions:shallowCopy(..) - object cannot be null.");
		}

	}
	
	/**
	 * Performs a deep copy which is a shallow copy but includes
	 * member attributes that behave as associations to other classes.
	 * 
	 * @param object	BuildOptions copy source
	 */
	public void copy(BuildOptions object) {
		if (object == null) {
			throw new IllegalArgumentException(" BuildOptions:copy(..) - object cannot be null.");
		}
		
		usingKubernetes 	= object.usingKubernetes;
		usingTerraform		= object.usingTerraform;
		usingArtifactRepo 	= object.usingArtifactRepo;
		
		
		dockerHost			= object.dockerHost;
		dockerRepo			= object.dockerRepo;
		
		gitHost				= object.gitHost;
		gitRepo		 		= object.gitRepo;
		
		k8Host				= object.k8Host;
		k8HostTarget		= object.k8HostTarget;
		
		terraformProvider 	= object.terraformProvider;	
		artifactType		= object.artifactType;
		
		appName				= object.appName;
		appAuthor			= object.appAuthor;
		appVersion			= object.appVersion;
		
		cicdPlatform		= null;
	}

	/**
	 * Returns a string representation of the object.
	 * 
	 * @return String
	 */
	public String toString() {
		StringBuilder returnString = new StringBuilder();

		returnString.append( "usingKubernetes=" + usingKubernetes);
		returnString.append( "usingTerraform=" + usingTerraform );
		returnString.append( "usingArtifactRepo=" + usingArtifactRepo );
		returnString.append( "dockerHost=" + dockerHost );
		returnString.append( "dockerRepo=" + dockerRepo );
		returnString.append( "gitHost=" + gitHost );
		returnString.append( "gitRepo=" + gitRepo );
		returnString.append( "k8Host=" + k8Host );
		returnString.append( "k8HostTarget=" + k8HostTarget );
		returnString.append( "terraformProvider=" + terraformProvider );	
		returnString.append( "artifactType=" + artifactType );
		returnString.append( "appName=" + appName );
		returnString.append( "appAuthor=" + appAuthor );
		returnString.append( "appVersion=" + appVersion );
		returnString.append( "cicdPlatform=" + cicdPlatform );
        returnString.append( super.toString() );

		return returnString.toString();
	}

	/**
	 * Comparison method to determine if the provided object is equivalent to this instance.
	 * 
	 * It does so by ensuring it is non-null, of BuildOptions type, and the id fields are equivalent.
	 * 
	 * @param	object	object to compare to
	 * @return	boolean
	 */	
	public boolean equals(Object object) {
		return ( super.equals(object) );
	}
	
	/**
	 * Hash off the super class
	 * 
	 * @return	hash code
	 */
	public int hashCode() {
		return super.hashCode();
	}

	/**
	 * validate the combination of options
	 * @return	validation issue
	 */
	protected boolean assignOptions(Map<String,String> options ) {		
		if ( options == null )
			return false;
		
		// ======================================
		// in-use options
		// ======================================		
		usingKubernetes 	= options.get( "kubernetes.inUse");		
		usingTerraform		= options.get( "terraform.inUse" );
		usingArtifactRepo 	= options.get( "artifact-repo.inUse" );
		
		// ======================================
		// Tracked Docker options
		// ======================================
		dockerRepo 		= options.get( "docker.repo" );		
		dockerHost		= options.get( "docker.host" );
		
		if ( dockerRepo == null )
			validationMsg.append( "Docker repo not specified" );
		
		if ( dockerHost == null )	// default to docker.io
			dockerHost = "docker.io";
		
		
		// ======================================
		// Tracked Git options
		// ======================================
		gitRepo			= options.get( "git.repository");
		gitHost			= options.get( "git.host");

		if ( gitHost == null )
			validationMsg.append( "Git host not specified" );

		if ( gitRepo == null )
			validationMsg.append( "Git repo not specified" );

		// ======================================
		// Tracked k8 options
		// ======================================
		k8Host			= options.get( "kubernetes.host");
		k8HostTarget	= options.get( "kubernetes.hostTarget");
		
		if ( usingKubernetes() ) {
			if( k8Host == null )
				validationMsg.append( "K8 in use but host not specified" );
			if( k8HostTarget == null )
				validationMsg.append( "K8 in use but host target not specified" );
		}

		// ======================================
		// artifact options
		// ======================================
		artifactType	= options.get( "artifact-repo.type");

		if ( usingArtifactRepo() ) {
			if ( artifactType == null )
				validationMsg.append( "Artifact repo in use but type not specified" );
		}
				
		// ======================================
		// Terraform options
		// ======================================
		terraformProvider	= options.get( "terraform.provider");

		if ( usingTerraform() ) {
			if ( terraformProvider == null )
				validationMsg.append( "Terraform in use but provider not specified" );
		}
		
		// ======================================
		// CICD Platform
		// ======================================
		cicdPlatform	= options.get( "cicd.platform");

		// ======================================
		// App options
		// ======================================
		appName			= options.get( "application.name");
		appAuthor		= options.get( "application.author");
		appVersion		= options.get( "application.version");

		// ======================================
		// an empty validation msg means no validation issues
		// ======================================
		return( validationMsg.toString().isEmpty() );
	}

	public String validationMsg() {
		return validationMsg.toString();
	}
	
	protected boolean usingKubernetes() {
		return usingKubernetes != null && usingKubernetes.equalsIgnoreCase("true");
	}

	protected boolean usingTerraform() {
		return usingTerraform != null && usingTerraform.equalsIgnoreCase("true");
	}

	protected boolean usingArtifactRepo() {
		return usingArtifactRepo != null && usingArtifactRepo.equalsIgnoreCase("true");
	}
	
	// attributes
	protected String usingKubernetes 	= null;
	protected String usingTerraform		= null;
	protected String usingArtifactRepo 	= null;
	
	
	protected String dockerHost			= "docker.io";
	protected String dockerRepo			= null;
	
	protected String gitHost			= "github.com";
	protected String gitRepo		 	= null;
	
	protected String k8Host				= null;
	protected String k8HostTarget		= null;
	
	protected String terraformProvider 	= null;	
	protected String artifactType		= null;
	
	protected String appName			= null;
	protected String appAuthor			= null;
	protected String appVersion			= null;
	
	protected String cicdPlatform		= null;
	
	/**
	 * no need to persist
	 */
	private transient StringBuilder validationMsg = new StringBuilder();

}
