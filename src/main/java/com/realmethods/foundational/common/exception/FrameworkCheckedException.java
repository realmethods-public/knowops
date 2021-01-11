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
 * Base class of all Framework checked exceptions.
 * @author    realMethods, Inc.
 * @see		  com.realmethods.foundational.common.misc.Utility
 */
public class FrameworkCheckedException extends Exception
{
//************************************************************************    
// Public Methods
//************************************************************************
    /** 
     * Base constructor.
     */
    public FrameworkCheckedException()
    {
        super();   
        
    }

    /** 
     * Constructor with message.
     * @param	message		text of the exception
     */
    public FrameworkCheckedException( String message )
    {
        super( message );   
    }    

    /**
     * Constructor with a Throwable for chained exception.
     * 
     * @param 	exception	thrown exception
     */
    public FrameworkCheckedException( Throwable exception )
    {
    	super( exception );
    }

    /**
     * Constructor with a Throwable for chained exception and a message.
     * @param message 	text of the exception
     * @param exception Throwable that is the prior chained exception.
     */
    public FrameworkCheckedException( String message, Throwable exception )
    {
        // call base class
        super(message, exception);
    }
        
}
