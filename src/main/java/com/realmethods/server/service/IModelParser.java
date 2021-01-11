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
package com.realmethods.server.service;

import com.realmethods.foundational.common.exception.ProcessingException;

/**
 * Simple interface to support to be used as a model -parser.
 * 
 * @author realMethods, Inc.
 *
 */
public interface IModelParser {
	public boolean loadModel( String fileName ) throws ProcessingException;
}