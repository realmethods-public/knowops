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
 * Encapsulates the concept of data for a goFramework User.
 * 
 */
public class User extends Base {

	/**
	 * Default Constructor
	 */
	public User() {
		generateInternalIdentifier();
	}

	/**
	 * Constructor with a User.
	 * 
	 * Internally calls copy using the provided argument.
	 * 
	 * @param object
	 */
	public User(User object) {
		super();

		if (object == null) {
			throw new IllegalArgumentException("User( User object ) - object arg is null.");
		}

		copy(object);
	}


	/**
	 * Returns the firstName field.
	 * 
	 * @return String
	 */
	public String getFirstName() {
		return this.firstName;
	}

	/**
	 * Assigns the name field with the provided argument.
	 * 
	 * @param name
	 */
	public void setFirstName(String name) {
		this.firstName = name;
	}

	/**
	 * Returns the lastName field.
	 * 
	 * @return String
	 */
	public String getLastName() {
		return this.lastName;
	}

	/**
	 * Assigns the lastName field with the provided argument.
	 * 
	 * @param name
	 */
	public void setLastName(String name) {
		this.lastName = name;
	}

	public String getFullName() {
		StringBuilder builder = new StringBuilder();
		
		if ( firstName != null )
			builder.append( firstName );
		
		if ( lastName != null ) {
			builder.append( " " );
			builder.append( lastName );
		}
		
		return builder.toString();
	}
	/**
	 * Returns the email field.
	 * 
	 * @return String
	 */
	public String getEmail() {
		return this.email;
	}

	/**
	 * Assigns the email field with the provided argument.
	 * 
	 * @param email
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Returns the company field.
	 * 
	 * @return String
	 */
	public String getCompany() {
		return this.company;
	}

	/**
	 * Assigns the company field with the provided argument.
	 * 
	 * @param company
	 */
	public void setCompany(String company) {
		this.company = company;
	}

	/**
	 * Returns the userId field.
	 * 
	 * @return
	 */
	public String getUserId() {
		return this.userId;
	}

	/**
	 * Assigns the userId field with the provided argument.
	 * 
	 * @param userId
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * Returns the password field.
	 * 
	 * @return String
	 */
	public String getPassword() {
		return this.password;
	}

	/**
	 * Assigns the password field with the provided argument.
	 * 
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Returns the userType field.
	 * 
	 * @return UserType
	 */
	public UserType getUserType() {
		return this.userType;
	}

	/**
	 * Assigns the userType with the provided argument.
	 * 
	 * @param userType
	 */
	public void setUserType(UserType userType) {
		this.userType = userType;
	}
	
	/**
	 * Returns the notifyFlag field.
	 * 
	 * @return boolean
	 */
	public boolean getNotifyFlag() {
		return this.notifyFlag;
	}

	/**
	 * Assigns the notifyFlag with the provided argument.
	 * 
	 * @param password
	 */
	public void setNotifyFlag(boolean notifyFlag) {
		this.notifyFlag = notifyFlag;
	}
	

	/**
	 * Returns the internalIdentifier field.
	 * 
	 * Is a unique randomly generated identifier, separate from the id field, which likely comes from a 
	 * persistence storage mechanism.
	 * 
	 * Defaults to org.apache.commons.text.RandomStringGenerator.Builder().withinRange('a', 'z').build()
	 * 
	 * @return String
	 */
	public String getInternalIdentifier() {
		return this.internalIdentifier;
	}

	/**
	 * Assigns the internalIdentifier field with the provided argument.
	 * 
	 * Is a unique randomly generated identifier, separate from the id field, which likely comes from a 
	 * persistence storage mechanism.
	 * 
	 * Defaults to org.apache.commons.text.RandomStringGenerator.Builder().withinRange('a', 'z').build()
	 * 
	 * @param internalIdentifier
	 */
	public void setInternalIdentifier(String internalIdentifier) {
		this.internalIdentifier = internalIdentifier;
	}	


	/**
	 * Performs a shallow copy.
	 * 
	 * @param object
	 */
	public void shallowCopy(User object) {
		if (object == null) {
			throw new IllegalArgumentException(" User:shallowCopy(..) - object cannot be null.");
		}

		this.firstName 			= object.firstName;
		this.lastName 			= object.lastName;
		this.email 				= object.email;
		this.userId 			= object.userId;
		this.password 			= object.password;
		this.company 			= object.company;
		this.internalIdentifier = object.internalIdentifier;
		this.notifyFlag 		= object.notifyFlag;
		this.userType 			= object.userType;
	}

	/**
	 * Performs a deep copy.
	 * 
	 * @param object
	 */
	public void copy(User object) {
		if (object == null) {
			throw new IllegalArgumentException(" User:copy(..) - object cannot be null.");
		}

		// delegate internally
		shallowCopy(object);
	}
	
	/**
	 * Helper method used to determine if the use has valid payment info in place.
	 * 
	 * Once Billing and Payment abstractions are in place, use thos here
	 * 
	 * @return
	 */
	public boolean hasValidPaymentInfo()  {
		return true;
	}

	/**
	 * Returns a string representation of the intrinsic attributes.
	 * 
	 * @return String
	 */
	@Override
	public String toString() {
		StringBuilder returnString = new StringBuilder();

		returnString.append("id = " + this.id + ", ");
		returnString.append("firstName = " + this.firstName + ", ");
		returnString.append("lastName = " + this.lastName + ", ");
		returnString.append("email = " + this.email + ", ");
		returnString.append("company = " + this.company + ", ");
		returnString.append("userId = " + this.userId + ", ");
		returnString.append("password = " + this.password + ", ");
		returnString.append("internalIdentifier = " + this.internalIdentifier + ", ");
		returnString.append("notifyFlag = " + this.notifyFlag + ", ");
		returnString.append("userType = " + this.userType + ", ");
		
		return returnString.toString();
	}


	/**
	 * Returns the type of the object
	 * 
	 * @return String
	 */
	public String getObjectType() {
		return ("User");
	}

	@Override
	/**
	 * Comparison method to determine if the provided object is equivalent to this instance.
	 * 
	 * It does so by ensuring it is non-null, of User type, and the id fields are equivalent.
	 * 
	 * @param	object
	 * @return	boolean
	 */
	public boolean equals(Object object) {	
		return ( super.equals(object) && (object instanceof User) );
	}

	@Override
	/**
	 * Hash off of the internalIdentifier field and a non-null id field.
	 * 
	 * @return	int
	 */
	public int hashCode() {
		if ( id != null )
			return id.hashCode() * internalIdentifier.hashCode() * userType.hashCode();
		else
			return super.hashCode();
	}
	
	public String generateInternalIdentifier() {
		internalIdentifier = org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric(16);
		return(internalIdentifier);
	}
	
	// attributes

	protected String firstName 			= null;
	protected String lastName 			= null;
	protected String email 				= null;
	protected String company 			= null;
	protected String userId 			= null;
	protected String password 			= null;
	protected String internalIdentifier = null;
	protected boolean notifyFlag		= false;
	protected UserType userType			= UserType.getDefaultValue();
											  
}
