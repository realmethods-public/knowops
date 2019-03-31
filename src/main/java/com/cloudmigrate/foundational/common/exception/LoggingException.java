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

//***********************************
// Imports
//***********************************

/**
 * Exception class for failures in logging.
 * 
 * <p>
 * @author    realMethods, Inc.
 */
public class LoggingException extends FrameworkCheckedException
{

//************************************************************************    
// Public Methods
//************************************************************************

    /** 
     * Base constructor.
     */
    public LoggingException()
    {
        super();
    }   
    /** Constructor with message.
     * @param message text of the exception
     */
    public LoggingException( String message )
    {
        super( message );
    }

    /**
     * Constructor with a Throwable for chained exception and a message.
     * 
     * @param message
     * @param exception
     */
    public LoggingException( String message, Throwable exception )
    {
        super( message, exception ); 
    }

//************************************************************************    
// Private / Protected Methods
//************************************************************************

//************************************************************************    
// Attributes
//************************************************************************
}

/*
 * Change Log:
 * $Log: LoggingException.java,v $
 * Revision 1.2  2003/08/05 12:16:17  tylertravis
 * - Updated header language to LGPL
 * - Improved exception handling globally
 * - Modified member variables to conform to Java Beans std.
 *
 * Revision 1.1.1.1  2003/07/05 03:05:18  tylertravis
 * initial sourceforge cvs revision
 *
 */




