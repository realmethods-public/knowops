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
 * Exception class for class initialization related errors.  
 * Normally associated with a singleton or factory method.
 * <p>
 * @author    realMethods, Inc.
 */
public class InitializationException extends FrameworkCheckedException
{
//****************************************************
// Public Methods
//****************************************************
    /**
     * default constructor
     */
    public InitializationException()
    {
    }

    /**
     * constructor
     *
     * @param message   text of the exception
     */
    public InitializationException( String message )
    {
        super( message );
    }
    
    /**
     * Constructor with a Throwable for chained exception and a message.
     * @param message
     * @param exception
     */
    public InitializationException( String message, Throwable exception )
    {
        super( message, exception ); 
    }
    
}

/*
 * Change Log:
 * $Log: InitializationException.java,v $
 * Revision 1.1  2003/08/05 12:41:08  tylertravis
 * - initial release
 *
 *
 */




