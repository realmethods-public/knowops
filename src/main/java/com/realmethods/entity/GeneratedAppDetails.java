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
 * Encapsulates data for GeneratedAppDetails, the details related to a app
 * creation session, including the package and model names used to create the
 * app and the path to the zip file containing the created and compiled app
 * 
 */
public class GeneratedAppDetails extends BaseTransactionalEntity {

	/**
	 * Default Constructor
	 */
	public GeneratedAppDetails() {
	}

	/**
	 * Constructor with a GeneratedAppDetails.
	 * 
	 * Delegates internally to copy(GeneratedAppDetails).
	 * 
	 * Throws an IllegalArgumentException if the provided argument is null.
	 * 
	 * @param object copy source
	 */
	public GeneratedAppDetails(GeneratedAppDetails object) {
		super();

		if (object == null) {
			throw new IllegalArgumentException(
					"GeneratedAppDetails( GeneratedAppDetails object ) - object arg is null.");
		}

		copy(object);
	}

	/**
	 * Returns the modeId field.
	 * 
	 * @return Long
	 */
	public Long getModelId() {
		return this.modelId;
	}

	/**
	 * Assigns the modelId field.
	 * 
	 * @param modelId
	 */
	public void setModelId(Long modelId) {
		this.modelId = modelId;
	}

	/**
	 * Returns the packageIdfield.
	 * 
	 * @return String
	 */
	public String getPackageId() {
		return this.packageId;
	}

	/**
	 * Assigns the packageId field.
	 * 
	 * @param packageId
	 */
	public void setPackageId(String packageId) {
		this.packageId = packageId;
	}
		
	
	/**
	 * Performs a shallow copy of intrinsic and enumerated types only.
	 * 
	 * @param object	GeneratedAppDetails copy source
	 */
	public void shallowCopy(GeneratedAppDetails object) {
		if (object == null) {
			throw new IllegalArgumentException(" GeneratedAppDetails:shallowCopy(..) - object cannot be null.");
		}

		// Set member attributes
		this.packageId				= object.packageId;
		this.modelId				= object.modelId;
		
		super.shallowCopy( object );
	}

	/**
	 * Performs a deep copy which is a shallow copy but includes
	 * member attributes that behave as associations to other classes.
	 * 
	 * @param object	GeneratedAppDetails copy source
	 */
	public void copy(GeneratedAppDetails object) {
		if (object == null) {
			throw new IllegalArgumentException(" GeneratedAppDetails:copy(..) - object cannot be null.");
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

		returnString.append("id = " + this.id + ", ");
        returnString.append("packageId = " + this.packageId + ", ");
        returnString.append("modelId = " + this.modelId + ", ");
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
		return ("GeneratedAppDetails");
	}

	@Override
	/**
	 * Comparison method to determine if the provided object is equivalent to this instance.
	 * 
	 * It does so by ensuring it is non-null, of GeneratedAppDetails type, and the id fields are equivalent.
	 * 
	 * @param	object
	 * @return	boolean
	 */
	public boolean equals(Object object) {
		return ( super.equals(object) && (object instanceof GeneratedAppDetails) );
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

	protected String packageId 				= null;
	protected Long modelId 					= null;

}
