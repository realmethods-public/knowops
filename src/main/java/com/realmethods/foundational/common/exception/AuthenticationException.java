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
 * Exception class for authentication related errors.
 * @author    realMethods, Inc.
 */
public class AuthenticationException extends FrameworkCheckedException
{
//****************************************************
// Public Methods
//****************************************************
    /**
     * default constructor
     */
    public AuthenticationException() {
    }

    /**
     * constructor
     * @param message   text of the exception
     */
    public AuthenticationException( String message ) {
        super( message );
    }
    
    /**
     * Constructor with a Throwable for chained exception and a message.
     * @param 	message		text of the exception
     * @param 	exception	to be bound
     */
    public AuthenticationException( String message, Throwable exception ) {
        super( message, exception ); 
    }    
}
