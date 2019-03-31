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
package com.cloudmigrate.api;

import java.util.Map;
import java.util.HashMap;

/**
 * Helper class to encapsulate the minimal concept of application parameters.
 * 
 * @author realMethods, Inc.
 *
 */
public class AppParams
{
	/**
	 * Returns the name field.
	 * 
	 * @return
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Assigns the name field the provided argument.
	 * 
	 * @param name
	 */
	public void setName( String name ) {
		this.name = name;
	}

	/**
	 * Returns the description field.
	 * 
	 * @return
	 */
	public String getDescription() {
		return this.description;
	}
	
	/**
	 * Assigns the description field the provided argument.
	 * 
	 * @param description
	 */
	public void setDescription( String description ) {
		this.description = description;
	}

	/**
	 * Returns the companyName field.
	 * 
	 * @return
	 */
	public String getCompanyName() {
		return this.companyName;
	}

	/**
	 * Assigns the companyName field the provided argument.
	 * 
	 * @param companyName
	 */
	public void setCompanyName( String companyName ) {
		this.companyName = companyName;
	}

	/** 
	 * Returns the appLogoUrl field.
	 * 
	 * @return
	 */
	public String getAppLogoUrl() {
		return this.appLogoUrl;
	}

	/**
	 * Assigns the appLogoUrl field the provided argument.
	 * 
	 * @param url
	 */
	public void setAppLogoUrl( String url ) {
		this.appLogoUrl = url;
	}

	/**
	 * Returns the companyLogoUrl field.
	 * 
	 * @return
	 */
	public String getCompanyLogoUrl() {
		return this.companyLogoUrl;
	}

	/**
	 * Assigns the companyLogoUrl field the provided argument.
	 * 
	 * @param url
	 */
	public void setCompanyLogoUrl( String url ) {
		this.companyLogoUrl = url;
	}

	/**
	 * Returns the author field.  Author infers the originator of the application itselt.
	 * 
	 * @return
	 */
	public String getAuthor() {
		return this.author;
	}

	/**
	 * Assigns the author field the provided argument.
	 * 
	 * @param author
	 */
	public void setAuthor( String author ) {
		this.author = author;
	}

	/**
	 * Returns the version field. Defaults to 1.0.
	 * @return
	 */
	public String getVersion() {
		return this.version;
	}

	/**
	 * Assigns the version field the provided argument.
	 * 
	 * @param version
	 */
	public void setVersion( String version ) {
		this.version = version;
	}
	
	/**
	 * Returns a Map representation of the fields as a name/value pairing.
	 * 
	 * @return
	 */
	public Map<String,String> asMap()
	{
		Map<String,String> map = new HashMap<>();
		map.put( "application.name", name );
		map.put( "application.description", name );
		map.put( "application.company name", name );
		map.put( "application.application logo URL", appLogoUrl );
		map.put( "application.corporate logo URL", companyLogoUrl );
		map.put( "application.author", author );
		map.put( "application.version", version );
		
		return( map );
	}

// attributes
	protected String name 			= "";
	protected String description 	= "";
	protected String companyName 	= "";
	protected String appLogoUrl 	= "";
	protected String companyLogoUrl = "";
	protected String author 		= "";
	protected String version 		= "1.0";
}
