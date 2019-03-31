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
 * Exception class for hook processing related errors
 * <p>
 * @author    realMethods, Inc.
 * @see		  com.cloudmigrate.foundational.integration.hook.ICheckedExceptionThrownHook
 * @see		  com.cloudmigrate.foundational.integration.hook.ICommandExecutionFailureHook
 * @see		  com.cloudmigrate.foundational.integration.hook.IHttpServletRequestProcessorErrorHook
 * @see		  com.cloudmigrate.foundational.integration.hook.IPageRequestProcessorErrorHook
 * @see		  com.cloudmigrate.foundational.integration.hook.IPostCommandExecuteHook
 * @see		  com.cloudmigrate.foundational.integration.hook.IPostHttpServletRequestProcessorHook
 * @see		  com.cloudmigrate.foundational.integration.hook.IPostPageRequestProcessorHook
 * @see		  com.cloudmigrate.foundational.integration.hook.IPostTaskExecuteHook
 * @see		  com.cloudmigrate.foundational.integration.hook.IPreCommandExecuteHook
 * @see		  com.cloudmigrate.foundational.integration.hook.IPreHttpServletRequestProcessorHook	
 * @see		  com.cloudmigrate.foundational.integration.hook.IPrePageRequestProcessorHook
 * @see		  com.cloudmigrate.foundational.integration.hook.IPreTaskExecuteHook
 * @see		  com.cloudmigrate.foundational.integration.hook.ITaskExecutionFailureHook
 * @see		  com.cloudmigrate.foundational.integration.hook.IUserSessionUnBoundHook
 * @see		  com.cloudmigrate.foundational.common.exception.HookProcessException
 */
public class HookProcessException extends FrameworkCheckedException
{
//****************************************************
// Public Methods
//****************************************************
    /**
     * default constructor
     */
    public HookProcessException()
    {
    }

    /**
     * constructor
     *
     * @param message   text of the exception
     */
    public HookProcessException( String message )
    {
        super( message );
    }
    
    /**
     * Constructor with a Throwable for chained exception and a message.
     * @param message
     * @param exception
     */
    public HookProcessException( String message, Throwable exception )
    {
        super( message, exception ); 
    }
    
}

/*
 * Change Log:
 * $Log: HookProcessException.java,v $
 * Revision 1.1  2003/08/05 12:41:08  tylertravis
 * - initial release
 *
 *
 */




