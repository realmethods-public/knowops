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
package com.realmethods.exception;

import com.realmethods.foundational.common.exception.FrameworkCheckedException;
/**
 * Uses for trapping a general exception.  realMethods normally propagates exception cases to the user
 * with verbal meaning.
 * 
 * @author realMethods, Inc.
 *
 */
public class GenerationException extends FrameworkCheckedException {

	/**
	 * default constructor
	 */
	public GenerationException() {
	}

	/**
	 * Constructor, delegates to super class
	 * 
	 * @param message
	 */
	public GenerationException(String message) {
		super(message);
	}

	/**
	 * Constructor, delegates to super class
	 * 
	 * @param message
	 */	
	public GenerationException(String message, Throwable exception) {
		super(message, exception);
	}
}
