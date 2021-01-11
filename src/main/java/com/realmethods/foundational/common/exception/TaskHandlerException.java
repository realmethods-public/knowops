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
 * This exception is thrown in response to a TaskHandler failure.
 *
 * <p>
 * @author    realMethods, Inc.
 * @see		  com.realmethods.foundational.integration.task.TaskHandler
 */
public class TaskHandlerException extends FrameworkCheckedException
{
//****************************************************
// Public Methods
//****************************************************
    /**
     * default constructor
     */
    public TaskHandlerException()
    {
    }

    /**
     * constructor
     *
     @param message   text of the exception
     */
    public TaskHandlerException( String message )
    {
        super( message );
    }
    
    /**
     * Constructor with a Throwable for chained exception and a message.
     * @param message
     * @param exception
     */
    public TaskHandlerException( String message, Throwable exception )
    {
        super( message, exception ); 
    }
    
}

/*
 * Change Log:
 * $Log: TaskHandlerException.java,v $
 */




