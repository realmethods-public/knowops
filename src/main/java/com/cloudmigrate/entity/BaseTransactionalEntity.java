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
 */
package com.cloudmigrate.entity;

import java.util.Date;

/**
 * Encapsulates data the base class of all transactional entities
 *
 * @author realMethods, Inc.
 */
public class BaseTransactionalEntity extends Base {

	/**
	 * Default Constructor
	 */
	public BaseTransactionalEntity() {
		assignDateTimeToNow();
	}

	/**
	 * Constructor with a BaseTransactionalEntity.
	 * 
	 * Internally calls copy( BaseTransactionalEntity ).
	 * 
	 * @param object
	 *            copy source
	 */
	public BaseTransactionalEntity(BaseTransactionalEntity object) {
		super();

		if (object == null) {
			throw new IllegalArgumentException(
					"BaseTransactionalEntity( BaseTransactionalEntity object ) - object arg is null.");
		}

		// internal deep copy
		copy(object);
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
	 * Returns the ownerId field.
	 * 
	 * @return Long
	 */
	public Long getOwnerId() {
		return this.ownerId;
	}

	/**
	 * Assigns the ownerId field using the provided argument.
	 * 
	 * @param ownerId
	 */
	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	/**
	 * Returns the filePath field.
	 * 
	 * @return
	 */
	public String getFilePath() {
		return this.filePath;
	}

	/**
	 * Assigns the filePath field using the provided argument.
	 * 
	 * Uses the extension of the the filePath to assign the modelType field.
	 * 
	 * @param filePath
	 */
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	/**
	 * Returns the deleted flag.
	 * 
	 * When assigned to true, implies this instance is deleted even if it
	 * continues to be persisted.
	 * 
	 * Defaults to false.
	 * 
	 * @return boolean
	 */
	public boolean getDeleted() {
		return this.deleted;
	}

	/**
	 * Assigns the deleted flag to the provided argument.
	 * 
	 * When assigned to true, implies this instance is deleted even if it
	 * continues to be persisted.
	 * 
	 * @param deleted
	 */
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	/**
	 * Returns the scopeType field.
	 * 
	 * @return ScopeType
	 */
	public ScopeType getScopeType() {
		return this.scopeType;
	}

	/**
	 * Assigns the scopeType field the provided argument
	 * 
	 * @param scopeType
	 */
	public void setScopeType(ScopeType scopeType) {
		this.scopeType = scopeType;
	}

	/**
	 * Returns the dateTime field.
	 * 
	 * @return String
	 */
	public String getDateTime() {
		return this.dateTime;
	}

	/**
	 * Assigns the dateTime field the provided argument
	 * 
	 * @param dateTime
	 */
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	/**
	 * Returns the checkSum field.
	 * 
	 * @return String
	 */
	public String getCheckSum() {
		return this.checkSum;
	}

	/**
	 * Assigns the checkSum field the provided argument
	 * 
	 * @param checkSum
	 */
	public void setCheckSum(String checkSum) {
		this.checkSum = checkSum;
	}

	/**
	 * Returns the cost field.
	 * 
	 * @return Float
	 */
	public Float getCost() {
		return this.cost;
	}

	/**
	 * Assigns the cost field the provided argument
	 * 
	 * @param cost
	 */
	public void setCost(Float cost) {
		this.cost = cost;
	}

	public void assignDateTimeToNow() {
		java.text.SimpleDateFormat sdf 	= new java.text.SimpleDateFormat(DATE_TIME_FORMAT);

		dateTime = sdf.format(new Date());

	}

	/**
	 * Returns the name field .
	 * 
	 * @return
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Assigns the name parameter from the provide parameter.
	 * 
	 * @param name
	 */
	public void setName( String name ) {
		this.name = name;
	}

	/**
	 * Returns the description field 
	 * 
	 * @return
	 */
	public String getDescription() {
		return this.description;
	}
	
	/**
	 * Assigns the name description from the provided parameter
	 * 
	 * @param description
	 */
	public void setDescription( String description ) {
		this.description = description;
	}
	
	public SaveParams getSaveParams() {
		return new SaveParams(name, description);
	}
	/**
	 * Performs a shallow copy.
	 * 
	 * @param object
	 *            BaseTransactionalEntity copy source
	 */
	public void shallowCopy(BaseTransactionalEntity object) {
		if (object == null) {
			throw new IllegalArgumentException(
					" BaseTransactionalEntity(..) - object cannot be null.");
		}

		// Set member attributes

		this.deleted = object.deleted;
		this.name = object.name;
		this.description = object.description;
		this.filePath = object.filePath;
		this.cost = object.cost;
		this.ownerId = object.ownerId;
		this.dateTime = object.dateTime;
		this.checkSum = object.checkSum;
	}

	/**
	 * Performs a deep copy.
	 * 
	 * @param object
	 *            BaseTransactionalEntity copy source
	 */
	public void copy(BaseTransactionalEntity object) {
		if (object == null) {
			throw new IllegalArgumentException(
					" BaseTransactionalEntity(..) - object cannot be null.");
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
		returnString.append("name = " + this.name + ", ");
		returnString.append("description = " + this.description + ", ");
		returnString.append("filePath = " + this.filePath + ", ");
		returnString.append("checkSum = " + this.checkSum + ", ");
		returnString.append("deleted = " + this.deleted + ", ");
		returnString.append("cost = " + this.cost + ", ");
		returnString.append("dateTime = " + this.dateTime + ", ");

		returnString.append(super.toString());

		return returnString.toString();
	}

	@Override
	/**
	 * Hash off of non-null fields id and name.
	 * 
	 * @return int
	 */
	public int hashCode() {
		if (id != null)
			return id.hashCode() * cost.hashCode() * checkSum.hashCode();
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
	 * Returns the type of this object.
	 * 
	 * @return String
	 */
	public String getObjectType() {
		return ("LocalModel");
	}

	@Override
	/**
	 * Comparison method to determine if the provided object is equivalent to
	 * this instance.
	 * 
	 * It does so by ensuring it is non-null, of LocalModel type, and the id
	 * fields are equivalent.
	 * 
	 * @param object
	 * @return boolean
	 */
	public boolean equals(Object object) {
		if (this == object)
			return true;

		if (object == null)
			return false;

		if (!(object instanceof BaseTransactionalEntity))
			return false;

		BaseTransactionalEntity bo = (BaseTransactionalEntity) object;

		return (id.equals(bo.id));

	}

	// attributes

	protected Long id 				= null;
	protected Long ownerId 			= null;
	protected String filePath 		= null;
	protected String dateTime 		= null;
	protected String name 			= null;
	protected String description 	= null;
	protected String checkSum 		= "0";
	protected boolean deleted 		= false;
	protected Float cost 			= new Float(0.0);
	protected ScopeType scopeType 	= ScopeType.getDefaultValue();
	public static final String DATE_TIME_FORMAT = "MM-dd-yyyy_HH.mm.ss";

}
