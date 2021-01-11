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

//***********************************
// Imports
//***********************************

/**
 * Used to indicate an error during Session usage.
 * <p>
 * @author    realMethods, Inc.
 */
public class SessionException extends FrameworkCheckedException
{

//************************************************************************    
// Public Methods
//************************************************************************

    /** 
     * Base constructor.
     */
    public SessionException()
    {
        super(); 
    }

    /** Constructor with message.
     * @param message text of the exception
     */
    public SessionException( String message )
    {
        super( message ); 
    }

    /**
     * Constructor with a Throwabe for chained exception and a message.
     * 
     * @param message
     * @param exception
     */
    public SessionException( String message, Throwable exception )
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






