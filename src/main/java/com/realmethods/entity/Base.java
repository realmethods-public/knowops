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
/**
 *  Workfile: $ Revision: $
 *  Last Modified by:   Author: $ on Date: $
 *
 */
package com.realmethods.entity;

/**
 * Base class for all realMethods business objects.
 * 
 * @author realMethods, Inc.
 */
public abstract class Base extends com.realmethods.foundational.business.bo.FrameworkHibernateBusinessObject {
	/**
	 * Default Constructor
	 */
	protected Base() {
		super();
	}

	
	/**
	 * Returns the id field.
	 * 
	 * @return Long
	 */
	public Long getId() {
		return this.id;
	}

	/**
	 * Assigns the id field from the provided argument.
	 * 
	 * @param id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	
	
	/**
	 * Comparison method to determine if the provided object is equivalent to this instance.
	 * 
	 * It does so by ensuring it is non-null, of User type, and the id fields are equivalent.
	 * 
	 * @param	object
	 * @return	boolean
	 */
	public boolean equals(Object object) {
		if (!(object instanceof User)) {
			return false;
		}
		
		Base bo = (Base) object;

		return (id.equals(bo.id));
	}	
	
	@Override
	/**
	 * Hash off of the internalIdentifier field and a non-null id field.
	 * 
	 * @return	int
	 */
	public int hashCode() {
		if ( id != null )
			return id.hashCode();
		else
			return super.hashCode();
	}
		
	/**
	 * Returns a unique identified for this instance type.
	 * 
	 * @return String
	 */
	public String getIdentity() {
		return ("id=" + id);
	}

	
	/**
	 * Returns a string representation of the object.
	 * 
	 * @return String
	 */
	@Override
	public String toString() {
		StringBuilder returnString = new StringBuilder();
		returnString.append("id = " + this.id + ", ");
		return returnString.toString();
	}


	// abstract methods
	public abstract String getObjectType();

	// attributes
	protected Long id 				= null;

}
