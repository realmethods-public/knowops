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
package com.cloudmigrate.foundational.common.properties;

import com.cloudmigrate.foundational.common.xml.FrameworkXMLParser;

/**
 * The implementation of this interface is for parsing an xml file represented by an Input Stream
 * <p>
 * @author    realMethods, Inc.
 */
public interface IXMLPropertiesHandler extends IPropertiesHandler
{

	/**
	 * Returns the framework xml parser in use.
	 * @return	framework xml parser.
	 */
	public FrameworkXMLParser getFrameworkXMLParser();
	
}

/*
 * Change Log:
 * $Log: IFrameworkPropertiesHandler.java,v $
 */
    
