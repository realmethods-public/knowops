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
package com.realmethods.foundational.common.exception;

//************************************
// Imports
//************************************

/**
 * Exception class for xml file parsing related errors. 
 *
 * <p>
 * @author    realMethods, Inc.
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
     * @param	message		text of the Exception		
     */
    public XMLFileParsingException( String message )
    {
        super( message );
    }
    
    /**
     * Constructor with a Throwable for chained exception and a message.
     * @param message		text of the Exception
     * @param exception		Throwable to bind to
     */
    public XMLFileParsingException( String message, Throwable exception )
    {
        super( message, exception ); 
    }
    
}
