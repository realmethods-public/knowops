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
 * Exception class for authorization related errors
 * <p>
 * @author    realMethods, Inc.
 * @see		  com.realmethods.foundational.presentation.request.HTTPRequestHandler
 */
public class AuthorizationException extends FrameworkCheckedException
{
//****************************************************
// Public Methods
//****************************************************
    /**
     * default constructor
     */
    public AuthorizationException()
    {
    }

    /**
     * constructor
     *
     * @param message   text of the exception
     */
    public AuthorizationException( String message )
    {
        super( message );
    }
    
    /**
     * Constructor with a Throwable for chained exception and a message.
     * @param message
     * @param exception
     */
    public AuthorizationException( String message, Throwable exception )
    {
        super( message, exception ); 
    }
    
}

/*
 * Change Log:
 * $Log: AuthorizationException.java,v $
 */




