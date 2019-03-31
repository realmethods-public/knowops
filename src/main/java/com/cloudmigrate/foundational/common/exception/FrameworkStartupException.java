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
 * Exception class for framework startup errors
 *
 * <p>
 * @author    realMethods, Inc.
 * @see		  com.cloudmigrate.foundational.common.startup.IFrameworkStartup
 * @see		  com.cloudmigrate.foundational.common.startup.FrameworkStartup
 * @see		  com.cloudmigrate.foundational.common.startup.StartupManager
 */
public class FrameworkStartupException extends FrameworkCheckedException
{
//****************************************************
// Public Methods
//****************************************************
    /**
     * default constructor
     */
    public FrameworkStartupException()
    {
    }

    /**
     * constructor
     *
     * @param message   text of the exception
     */
    public FrameworkStartupException( String message )
    {
        super( message );
    }
    
    /**
     * Constructor with a Throwable for chained exception and a message.
     * @param message
     * @param exception
     */
    public FrameworkStartupException( String message, Throwable exception )
    {
        super( message, exception ); 
    }
    
}

/*
 * Change Log:
 * $Log:  $
 */




