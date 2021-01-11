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
 * Used to indicate an error occurred during general processing.
 * <p>
 * @author    realMethods, Inc.
 */
public class ProcessingException extends FrameworkCheckedException
{

//************************************************************************    
// Public Methods
//************************************************************************

    /** 
     * Base constructor.
     */
    public ProcessingException()
    {
        super(); 
    }

    /** Constructor with message.
     * @param message text of the exception
     */
    public ProcessingException( String message )
    {
        super( message ); 
    }

    /**
     * Constructor with a Throwable for chained exception and a message.
     * 
     * @param message		text of the exception
     * @param exception		Throwable to bind to
     */
    public ProcessingException( String message, Throwable exception )
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

