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
package com.realmethods.foundational.business.delegate;

//***************************
//Imports
//***************************

import com.realmethods.foundational.business.bo.IFrameworkBusinessObject;

/**
 * Commmon interface for business delegates generated and used within the
 * framework.
 * <p>
 * 
 * @author realMethods, Inc.
 */
public interface IFrameworkBusinessDelegate {

	/**
	 * Returns the bound business object
	 * 
	 * @return the bound bound object
	 */
	public IFrameworkBusinessObject getFrameworkBusinessObject();
}

/*
 * Change Log: $Log$
 */
