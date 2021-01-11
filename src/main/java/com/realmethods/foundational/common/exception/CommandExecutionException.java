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
 * Exception class for failing to execute a Command.
 * <p>
 * @author    realMethods, Inc.
 */
public class CommandExecutionException extends FrameworkCheckedException
{
//****************************************************
// Public Methods
//****************************************************
    /**
     * default constructor
     */
    public CommandExecutionException()
    {
    }

    /**
     * constructor
     *
     * @param message   text of the exception
     */
    public CommandExecutionException( String message )
    {
        super( message );
    }
    
    /**
     * Constructor with a Throwable for chained exception and a message.
     * @param message
     * @param exception
     */
    public CommandExecutionException( String message, Throwable exception )
    {
        super( message, exception ); 
    }
    
}

/*
 * Change Log:
 * $Log: CommandExecutionException.java,v $
 */




