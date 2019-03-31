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
 * Used to indicate that the user was unable to be authenticated.
 *
 * <p>
 * @author    realMethods, Inc.
 */
public class UserFailedAuthenticationException extends FrameworkCheckedException
{

//************************************************************************    
// Public Methods
//************************************************************************

    /** 
     * Base constructor.
     */
    public UserFailedAuthenticationException()
    {
        super(); 
    }

    /** 
     * Constructor with message.
     * @param message   text of the exception
     */
    public UserFailedAuthenticationException( String message )
    {
        super( message ); 
    }

    /**
     * Constructor with a Throwable for chained exception and a message.
     * @param message
     * @param exception
     */
    public UserFailedAuthenticationException(	String message,
                                    			Throwable exception )
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
 * $Log: UserFailedAuthenticationException.java,v $
 */




