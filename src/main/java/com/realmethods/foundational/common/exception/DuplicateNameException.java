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
 * Exception class for failure to exchange data between two sources
 * <p>
 * @author    realMethods, Inc.
 */
public class DuplicateNameException extends FrameworkCheckedException
{

// constructors
	
    public DuplicateNameException()
    {
        super();
    }
       
    public DuplicateNameException( String message )
    {
        super( message );
    }    
    
	/**
	 * Constructor with a Throwable for chained exception and a message.
     * @param message
     * @param exception
	 */
	public DuplicateNameException( String message, Throwable exception )
	{
		super( message, exception ); 
	}    
}

/*
 * Change Log:
 * $Log: DuplicateNameException.java,v $
 */



