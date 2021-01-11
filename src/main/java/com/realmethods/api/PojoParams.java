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


/**
 * Helper class to encapsulate the concept of POJO parameters
 * 
 * @author realMethods, Inc.
 *
 */
public class PojoParams
{

	/**
	 * default constructor
	 */
	public PojoParams() {
		// intentionally left empty
	}
	
	/**
	 * Population constructor
	 * @param javaRootPackageNames	root package name array
	 * @param primaryKeyPattern		pattern to use to identify an attribute as a primary key
	 */
	public PojoParams( String[] javaRootPackageNames, String primaryKeyPattern ) {
		this.javaRootPackageNames	= javaRootPackageNames;
		this.primaryKeyPattern 		= primaryKeyPattern;
	}
	
	/**
	 * Returns the javaRootPackageNames field.
	 * 
	 * @return
	 */
	public String[] getJavaRootPackageNames() {
		return javaRootPackageNames;
	}

	/**
	 * Assigns the javaRootPackageNames field to the provided argument.
	 * 
	 * @param javaRootPackageNames
	 */
	public void setJavaRootPackageNames( String[] javaRootPackageNames ) {
		this.javaRootPackageNames = javaRootPackageNames;
	}

	/**
	 * Returns the primaryKeyPattern field.
	 * 
	 * @return
	 */
	public String getPrimaryKeyPattern() {
		return primaryKeyPattern;
	}

	/** 
	 * Assigns the primaryKeyPattern field to the provided argument.
	 * 
	 * @param password
	 */
	public void setPrimaryKeyPattern( String primaryKeyPattern) {
		this.primaryKeyPattern = primaryKeyPattern;
	}


	public boolean hasPrimaryKeyPattern() {
		return (primaryKeyPattern != null && !primaryKeyPattern.isEmpty());
	}
	/**
	 * Returns a readable String representation of the instance's data
	 * 
	 * @return String
	 */	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("javaRootPackageNames:");
				
		if ( javaRootPackageNames != null )
			builder.append(String.join(", ", javaRootPackageNames));
		else 
			builder.append("no java root package names assigned");
			
		builder.append(", primaryKeyPattern:" + primaryKeyPattern );
		
		return builder.toString();
	}
	
	// attributes
		protected String[] javaRootPackageNames			= null;
		protected String primaryKeyPattern				= null;
		public static final String POJO_NAME_PATTERN	= "_pojoName_";

}