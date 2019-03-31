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
 * Used to indicate that the User unable to be authorized.
 *
 * <p>
 * @author    realMethods, Inc.
 */
public class UserFailedAuthorizationException extends FrameworkCheckedException
{

//************************************************************************    
// Public Methods
//************************************************************************

    /** 
     * Base constructor.
     */
    public UserFailedAuthorizationException()
    {
        super(); 
    }

    /** 
     * Constructor with message.
     * @param message   text of the exception
     */
    public UserFailedAuthorizationException( String message )
    {
        super( message ); 
    }

    /**
     * Constructor with a Throwable for chained exception and a message.
     * @param message 	text of the exception
     * @param exception Throwable that is the prior chained exception.
     */
    public UserFailedAuthorizationException( String message, Throwable exception )
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
 * $Log: UserFailedAuthorizationException.java,v $
 */




