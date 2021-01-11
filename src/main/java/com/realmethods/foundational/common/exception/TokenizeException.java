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
 * This exception is thrown in response to tokenizing within a TokenizeMessage
 * extension.
 *
 * <p>
 * @author    realMethods, Inc.
 */
public class TokenizeException extends FrameworkCheckedException
{
//****************************************************
// Public Methods
//****************************************************
    /**
     * default constructor
     */
    public TokenizeException()
    {
    }

    /**
     * constructor
     *
     @param message   text of the exception
     */
    public TokenizeException( String message )
    {
        super( message );
    }
    
    /**
     * Constructor with a Throwable for chained exception and a message.
     * @param message   text of the exception
     * @param exception
     */
    public TokenizeException( String message,
                              Throwable exception )
    {
        super( message, exception ); 
    }
    
}

/*
 * Change Log:
 * $Log: TokenizeException.java,v $
 * Revision 1.2  2003/08/05 12:16:17  tylertravis
 * - Updated header language to LGPL
 * - Improved exception handling globally
 * - Modified member variables to conform to Java Beans std.
 *
 * Revision 1.1.1.1  2003/07/05 03:05:19  tylertravis
 * initial sourceforge cvs revision
 *
 */




