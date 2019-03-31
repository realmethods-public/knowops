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
 * Exception class for ValueObject Notification related errors
 *
 * <p>
 * @author    realMethods, Inc.
 * @see		  com.cloudmigrate.foundational.integration.notify.ValueObjectNotificationManager
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
     * @param message
     * @param exception
     */
    public ValueObjectNotificationException( String message, Throwable exception )
    {
        super( message, exception ); 
    }
    
}

/*
 * Change Log:
 * $Log: ValueObjectNotificationException.java,v $
 */




