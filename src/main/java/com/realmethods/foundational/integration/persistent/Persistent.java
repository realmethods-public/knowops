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
package com.realmethods.foundational.integration.persistent;

/**
 * Common interface for any persistent entity.
 * 
 * @author realMethods, Inc.
 */
public interface Persistent
{
	/**
	 * Called when saved
	 */	
	public void onSave();
	
	/**
	 * Called when loaded
	 */
	public void onLoad();
	
	/**
	 * Returns a saved T/F indicated
	 * @return	boolean
	 */
	public boolean isSaved();
}
