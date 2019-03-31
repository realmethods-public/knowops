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
package com.cloudmigrate.foundational.business.bo;

//***********************
// Imports
//***********************

/** 
 * Base interface for all application Enumerators
 * <p> 
 * @author    realMethods, Inc.
 */
public interface IFrameworkEnumerator extends java.io.Serializable
{
	/**
	 * Returns the names and values of the applicable enumerated values
	 * @return	Map
	 */
	public java.util.Map getValues();
	
	public Object getValue();
	
    public String getDescription();
    
    public Integer getIndex();

	/**
	 * Returns a string representation of the object.
	 * @return String
	 */
	public String toString();
}

/*
 * Change Log:
 * $Log: IFrameworkEnumerator.java,v $
 */
