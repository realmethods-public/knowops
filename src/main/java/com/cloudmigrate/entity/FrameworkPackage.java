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
/**
 *  Workfile: $ Revision: $
 *  Last Modified by:   Author: $ on Date: $
 *
 */package com.cloudmigrate.entity;

import com.cloudmigrate.foundational.business.pk.*;

/**
 * Encapsulates data for a Technology Stack Package, to mimic a portion of the
 * framework-package.xml file.
 * 
 */
public class FrameworkPackage extends BaseTransactionalEntity {

	/**
	 * Default Constructor
	 */
	public FrameworkPackage() {
	}

	/**
	 * Constructor with a FrameworkPackage
	 * 
	 * @param object copy source
	 */
	public FrameworkPackage(FrameworkPackage object) {
		super();

		if (object == null) {
			throw new IllegalArgumentException("FrameworkPackage( FrameworkPackage object ) - object arg is null.");
		}

		copy(object);
	}


	/**
	 * Returns the shortName field.
	 * 
	 * @return String
	 */
	public String getShortName() {
		return this.shortName;
	}

	/**
	 * Assigns the shortName field using the provided argument.
	 * 
	 * @param name
	 */
	public void setShortName(String name) {
		this.shortName = name;
	}

	/**
	 * Returns the version field.
	 * 
	 * @return String
	 */
	public String getVersion() {
		return this.version;
	}

	/**
	 * Assigns the version field using the provided argument.
	 * 
	 * @param version
	 */
	public void setVersion(String version) {
		this.version = version;
	}


	/**
	 * Returns the appType field. This is the server directory location for this package.
	 * 
	 * @return String
	 */
	public String getAppType() {
		return this.appType;
	}

	/**
	 * Assigns the appType field using the provided argument.
	 * 
	 * @param PackageLocation
	 */
	public void setAppType(String appType) {
		this.appType = appType;
	}

	/**
	 * Returns the releaseStatus field. This is the server directory location for this package.
	 * 
	 * @return String
	 */
	public String getReleaseStatus() {
		return this.releaseStatus;
	}

	/**
	 * Assigns the releaseStatus field using the provided argument.
	 * 
	 * @param PackageLocation
	 */
	public void setReleaseStatus(String releaseStatus) {
		this.releaseStatus = releaseStatus;
	}

	/**
	 * Returns the infoPageUrl field. This is the server directory location for this package.
	 * 
	 * @return String
	 */
	public String getInfoPageUrl() {
		return this.infoPageUrl;
	}

	/**
	 * Assigns the infoPageUrl field using the provided argument.
	 * 
	 * @param PackageLocation
	 */
	public void setInfoPageUrl(String infoPageUrl) {
		this.infoPageUrl = infoPageUrl;
	}

	/**
	 * Returns the packageXML field.
	 * 
	 * @return String
	 */
	public String getPackageXML() {
		return this.packageXML;
	}

	/**
	 * Assigns the packageXML field using the provided argument.
	 * 
	 * @param packageXML
	 */
	public void setPackageXML(String packageXML) {
		this.packageXML = packageXML;
	}

	
	/**
	 * Returns the iconUrl field.
	 * 
	 * @return String
	 */
	public String getIconUrl() {
		return this.iconUrl;
	}

	/**
	 * Assigns  the iconUrl field.
	 * 
	 * @return String
	 */
	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	/**
	 * Performs a shallow copy of intrinsic and enumerated types only.
	 * 
	 * @param object 	FrameworkPackage copy source
	 */
	public void shallowCopy(FrameworkPackage object){
		if (object == null) {
			throw new IllegalArgumentException(" FrameworkPackage:shallowCopy(..) - object cannot be null.");
		}

		// Set member attributes
		this.shortName = object.shortName;
		this.version = object.version;
		this.packageXML = object.packageXML;
		this.iconUrl = object.iconUrl;
		this.infoPageUrl = object.infoPageUrl;
		this.releaseStatus = object.releaseStatus;
		this.appType = object.appType;
		
		super.shallowCopy( object );

	}

	/**
	 * Performs a deep copy which is a shallow copy but includes
	 * member attributes that behave as associations to other classes.
	 * 
	 * @param object	FrameworkPackage copy source
	 */
	public void copy(FrameworkPackage object) {
		if (object == null) {
			throw new IllegalArgumentException(" FrameworkPackage:copy(..) - object cannot be null.");
		}
		
		shallowCopy(object);
	}

	/**
	 * Returns a string representation of the object.
	 * 
	 * @return String
	 */
	@Override
	public String toString() {
		StringBuilder returnString = new StringBuilder();

		returnString.append("shortName = " + this.shortName + ", ");
		returnString.append("version = " + this.version + ", ");
		returnString.append("packageXML = " + this.packageXML + ", ");
		returnString.append("iconUrl = " + this.iconUrl);
		returnString.append("infoPageUrl = " + this.infoPageUrl);
		returnString.append("releaseStatus = " + this.releaseStatus);
		returnString.append("appType = " + this.appType);
        returnString.append( super.toString() );

		return returnString.toString();
	}

    /**
     * Returns the type of this object.
     * 
     * @return	String
     */
	public String getObjectType() {
		return ("FrameworkPackage");
	}

	@Override
	/**
	 * Comparison method to determine if the provided object is equivalent to this instance.
	 * 
	 * It does so by ensuring it is non-null, of FrameworkPackage type, and the id fields are equivalent.
	 * 
	 * @param	object
	 * @return	boolean
	 */	
	public boolean equals(Object object) {

		if (this == object) {
			return true;
		}

		if (object == null) {
			return false;
		}

		if (!(object instanceof FrameworkPackage)) {
			return false;
		}

		return (super.equals(object));
	}

	@Override
	/**
	 * Hash off of non-null fields id and name.
	 * 
	 * @return	int
	 */	
	public int hashCode() {
		return super.hashCode();
	}	
	
	// attributes
	protected String shortName 		= null;
	protected String version 		= null;
	protected String infoPageUrl 	= null;
	protected String releaseStatus 	= null;
	protected String appType 		= null;
	protected String packageXML 	= null;
	protected String iconUrl 		= null;
}
