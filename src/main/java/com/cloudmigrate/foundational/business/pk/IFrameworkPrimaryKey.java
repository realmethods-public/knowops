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
package com.cloudmigrate.foundational.business.pk;

/**
 * Standard interface for an aggregation of objects which represent a single unique key.
 * <p>
 * Used in the unique identity related to FrameworkValueObjects.
 * <p> 
 * @author    realMethods, Inc.
 * @see		  com.cloudmigrate.foundational.business.vo.FrameworkValueObject
 * @see		  com.cloudmigrate.foundational.common.parameter.Parameter
 */
public interface IFrameworkPrimaryKey
{
	/**
	 * Retrieves the values associated with the implementation of this key.
	 * 
	 * @return	as an array of values
	 */
	public Object[] values();
    /**
     * Returns a String representation.
     * @return	String
     */
    public String toString();
    
	/**
	 * Are the contained key values non-null.
	 * @return boolean
	 */
	public boolean isEmpty();
	
	/**
	 * Returns true if a non-null, non-default value has been assigned
	 * as a primary key value.
	 * 
	 * @return	boolean
	 */
	public boolean hasBeenAssigned();
    
}

/*
 * Change Log:
 * $Log: IFrameworkPrimaryKey.java,v $
 */
