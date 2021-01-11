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

/**
 * Exception class for failure of making use of a FrameworkDAO object.
 * <p>
 * @author    realMethods, Inc.
 */
public class FrameworkDAOException extends FrameworkCheckedException
{
    /** 
     * Base constructor.
     */
    public FrameworkDAOException()
    {
        super(); 
    }

    /** Constructor with message.
     * @param 	message text of the exception
     */
    public FrameworkDAOException( String message )
    {
        super( message ); 
    }
    
    /**
     * Constructor with a Throwable for chained exception and a message.
     * 
     * @param message		message text of the exception
     * @param exception		thrown Throwable to bind to
     */
    public FrameworkDAOException( String message, Throwable exception )
    {
        super( message, exception ); 
    }
        
}

/*
 * Change Log:
 * $Log: FrameworkDAOException.java,v $
 */




