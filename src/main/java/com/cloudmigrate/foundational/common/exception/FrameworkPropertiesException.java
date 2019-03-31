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

/**
 * Exception for failures associated with establishing or acquiring properties from
 * the FrameworkPropertiesHandler.
 * <p>
 * @author    realMethods, Inc.
 * @see		  com.cloudmigrate.foundational.common.properties.IFrameworkPropertiesHandler
 */
public class FrameworkPropertiesException extends FrameworkCheckedException
{
    /** 
     * Base constructor.
     */
    public FrameworkPropertiesException()
    {
        super(); 
    }

    /** Constructor with message.
     * @param message text of the exception
     */
    public FrameworkPropertiesException( String message )
    {
        super( message ); 
    }
    
    /**
     * Constructor with a Throwable for chained exception and a message.
     * 
     * @param message
     * @param exception
     */
    public FrameworkPropertiesException( String message, Throwable exception )
    {
        super( message, exception ); 
    }
    
}

/*
 * Change Log:
 * $Log: FrameworkPropertiesException.java,v $
 */




