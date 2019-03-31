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
package com.cloudmigrate.foundational.common.exception;

//************************************
// Imports
//************************************

/**
 * Exception class for xml file parsing related errors. 
 *
 * <p>
 * @author    realMethods, Inc.
 * @see       com.cloudmigrate.foundational.presentation.parser.FrontControllerXMLParser
 */
public class XMLFileParsingException extends FrameworkCheckedException
{
//****************************************************
// Public Methods
//****************************************************
    /**
     * default constructor
     */
    public XMLFileParsingException()
    {
    }

    /**
     * constructor
     *
     * @param	message		
     */
    public XMLFileParsingException( String message )
    {
        super( message );
    }
    
    /**
     * Constructor with a Throwable for chained exception and a message.
     * @param message
     * @param exception
     */
    public XMLFileParsingException( String message, Throwable exception )
    {
        super( message, exception ); 
    }
    
}

/*
 * Change Log:
 * $Log: XMLFileParsingException.java,v $
 */




