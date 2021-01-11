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
 * Exception class associated with failure during Task creation.
 *
 * <p>
 * @author    realMethods, Inc.
 * @see		  com.realmethods.foundational.integration.task.FrameworkTask
 */
public class TaskCreationException extends FrameworkCheckedException
{
//****************************************************
// Public Methods
//****************************************************
    /**
     * default constructor
     */
    public TaskCreationException()
    {
    }

    /**
     * constructor
     *
     @param message   text of the exception
     */
    public TaskCreationException( String message )
    {
        super( message );
    }
    
    /**
     * Constructor with a Throwable for chained exception and a message.
     * 
     * @param message
     * @param exception
     */
    public TaskCreationException( String message, Throwable exception )
    {
        super( message, exception ); 
    }
    
}

/*
 * Change Log:
 * $Log: TaskCreationException.java,v $
 */




