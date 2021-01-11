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
 * Exception class for when processing is already in place.
 * 
 * <p>
 * @author    realMethods, Inc.
 */
public class AlreadyProcessingException extends FrameworkCheckedException
{

//************************************************************************    
// Public Methods
//************************************************************************

    /** 
     * Base constructor.
     */
    public AlreadyProcessingException()
    {
        super();
    }   

    /** 
     * Constructor with message.
     * 
     * @param message   text of the exception
     */
    public AlreadyProcessingException( String message )
    {
        super( message );
    }

    /**
     * Constructor with a Throwable for chained exception and a message.
     * 
     * @param message
     * @param exception
     */
    public AlreadyProcessingException( String message, Throwable exception )
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
 * $Log: AlreadyProcessingException.java,v $
 */




