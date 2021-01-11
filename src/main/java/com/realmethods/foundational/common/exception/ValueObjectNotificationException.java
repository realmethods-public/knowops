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
 * Exception class for ValueObject Notification related errors
 *
 * <p>
 * @author    realMethods, Inc.
 */
public class ValueObjectNotificationException extends FrameworkCheckedException
{
//****************************************************
// Public Methods
//****************************************************
    /**
     * default constructor
     */
    public ValueObjectNotificationException()
    {
    }

    /**
     * constructor
     *
     * @param message   text of the exception
     */
    public ValueObjectNotificationException( String message )
    {
        super( message );
    }
    
    /**
     * Constructor with a Throwable for chained exception and a message.
     * @param message		text of the exception
     * @param exception		Throwable to bind to
     */
    public ValueObjectNotificationException( String message, Throwable exception )
    {
        super( message, exception ); 
    }
    
}




