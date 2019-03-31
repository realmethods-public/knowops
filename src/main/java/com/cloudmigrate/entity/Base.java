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
 */package com.cloudmigrate.entity;

/**
 * Base class for all cloudMigrate business objects.
 * 
 * @author realMethods, Inc.
 */
public abstract class Base extends com.cloudmigrate.foundational.business.bo.FrameworkHibernateBusinessObject {
	/**
	 * Default Constructor
	 */
	protected Base() {
		super();
	}


	// abstract methods
	public abstract String getIdentity();
	public abstract String getObjectType();

}
