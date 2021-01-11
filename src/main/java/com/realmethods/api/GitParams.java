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
package com.realmethods.api;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper class to encapsulate the concept of parameters for a Git implementation.
 * 
 * @author realMethods, Inc.
 *
 */
public class GitParams
{
	/**
	 * Returns the name field.
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Assigns the name field to the provided argument.
	 * 
	 * @param name
	 */
	public void seName( String name ) {
		this.name = name;
	}


	/**
	 * Returns the username field.
	 * 
	 * @return
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Assigns the username field to the provided argument.
	 * 
	 * @param username
	 */
	public void setUsername( String username ) {
		this.username = username;
	}

	/**
	 * Returns the password field.
	 * 
	 * @return
	 */
	public String getPassword() {
		return password;
	}

	/** 
	 * Assigns the password field to the provided argument.
	 * 
	 * @param password
	 */
	public void setPassword( String password ) {
		this.password = password;
	}

	/**
	 * Returns the repository field.
	 * 
	 * @return
	 */
	public String getRepository() {
		return repository;
	}

	/**
	 * Assigns the repository field the provided argument.
	 * 
	 * @param repository
	 */
	public void setRepository( String repository ) {
		this.repository = repository;
	}

	/**
	 * Returns the tag field. Defaults to latest.
	 * 
	 * @return
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * Assigns the tag field the provided argument.
	 * 
	 * @param tag
	 */
	public void setTag( String tag ) {
		this.tag = tag;
	}

	/**
	 * Returns the host field. Defaults to git.com.
	 * 
	 * @return
	 */
	public String getHost() {
		return host;
	}

	/**
	 * Assigns the host field the provided argument.
	 * 
	 * @param host
	 */
	public void setHost( String host ) {
		this.host = host;
	}

	/**
	 * Returns a Map representation of the internal fields as a name/value pairing.
	 * @return
	 */
	public Map<String,String> asMap()
	{
		Map<String,String> map = new HashMap<>();
		map.put( "git.host", host );
		map.put( "git.username", username );
		map.put( "git.password", password );
		map.put( "git.repository", repository );
		map.put( "git.tag", tag );
		
		return( map );
	}

	/**
	 * Returns a readable String representation of the instance's data
	 * 
	 * @return String
	 */	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("name:" + name );
		builder.append("username:" + username );
		builder.append(", password:" + password );
		builder.append(", repository:" + repository );
		builder.append(", tag:" + tag );
		builder.append(", host:" + host );
		
		return builder.toString();
	}
	
// attributes
	protected String name			= null;
	protected String username		= null;
	protected String password		= null;
	protected String repository		= null;
	protected String tag 			= "latest";
	protected String host 			= "github.com";
}
