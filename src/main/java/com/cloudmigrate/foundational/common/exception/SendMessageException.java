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

//***********************************
// Imports
//***********************************

/**
 * Exception class for sending a message.
 * 
 * <p>
 * @author    realMethods, Inc.
 */
public class SendMessageException extends FrameworkCheckedException
{

//************************************************************************    
// Public Methods
//************************************************************************

    /** 
     * Base constructor.
     */
    public SendMessageException()
    {
        super();
    }   

    /** 
     * Constructor with message.
     * @param message	text of the exception
     */
    public SendMessageException( String message )
    {
        super( message );
    }

    /**
     * Constructor with a Throwable for chained exception and a message.
     * 
     * @param message
     * @param exception
     */
    public SendMessageException( String message, Throwable exception )
    {
        super( message, exception ); 
    }

//************************************************************************    
// Private / Protected Methods
//************************************************************************

//************************************************************************    
// Attributes
//************************************************************************
}

/*
 * Change Log:
 * $Log: SendMessageException.java,v $
 */





