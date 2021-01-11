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


/**
 * Encapsulates data for a Resource file
 * 
 */
public class Resource extends BaseTransactionalEntity {

	/**
	 * Default Constructor
	 */
	public Resource() {
	}

	/**
	 * Constructor with a Resource
	 * 
	 * @param object copy source
	 */
	public Resource(Resource object) {
		super();

		if (object == null) {
			throw new IllegalArgumentException("Resource( Resource object ) - object arg is null.");
		}

		copy(object);
	}

	/**
	 * Returns the ResourceType field.
	 * 
	 * @return ResourceType
	 */
	public ResourceType getResourceType() {
		return this.resourceType;
	}

	/**
	 * Assigns the resourceType with the provided argument.
	 * 
	 * @param resourceType
	 */
	public void setResourceType(ResourceType resourceType) {
		this.resourceType = resourceType;
	}

	/**
	 * Performs a shallow copy of intrinsic and enumerated types only.
	 * 
	 * @param object 	Resource copy source
	 */
	public void shallowCopy(Resource object){
		if (object == null) {
			throw new IllegalArgumentException(" Resource:shallowCopy(..) - object cannot be null.");
		}

		// Set member attributes
		this.resourceType = object.resourceType;
		
		super.shallowCopy( object );

	}

	/**
	 * Performs a deep copy which is a shallow copy but includes
	 * member attributes that behave as associations to other classes.
	 * 
	 * @param object	Resource copy source
	 */
	public void copy(Resource object) {
		if (object == null) {
			throw new IllegalArgumentException(" Resource:copy(..) - object cannot be null.");
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

		returnString.append("resourceType = " + this.resourceType);
        returnString.append( super.toString() );

		return returnString.toString();
	}

	@Override
    /**
     * Returns the type of this object.
     * 
     * @return	String
     */
	public String getObjectType() {
		return ("Resource");
	}

	@Override
	/**
	 * Comparison method to determine if the provided object is equivalent to this instance.
	 * 
	 * It does so by ensuring it is non-null, of Resource type, and the id fields are equivalent.
	 * 
	 * @param	object
	 * @return	boolean
	 */	
	public boolean equals(Object object) {
		return ( super.equals(object) && (object instanceof Resource) );
	}
	
	
	@Override
	/**
	 * Hash off the super class
	 * 
	 * @return	int
	 */
	public int hashCode() {
		return super.hashCode();
	}
	
	// attributes
	protected ResourceType resourceType = ResourceType.getDefaultValue();
}
