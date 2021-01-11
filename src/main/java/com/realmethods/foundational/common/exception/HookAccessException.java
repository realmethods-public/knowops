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
 * Exception class for when accessing a Framework hook fails.
 * <p>
 * @author    realMethods, Inc.
 */
public class HookAccessException extends FrameworkCheckedException
{
    /** 
     * Base constructor.
     */
    public HookAccessException()
    {
        super(); 
    }

    /** 
     * Constructor with message.
     * 
     * @param message   text of the exception
     */
    public HookAccessException( String message )
    {
        super( message ); 
    }
    
    /**
     * Constructor with a Throwable for chained exception and a message.
     * 
     * @param message		text of exception message
     * @param exception		Throwable to bind to
     */
    public HookAccessException( String message, Throwable exception )
    {
        super( message, exception ); 
    }

}




