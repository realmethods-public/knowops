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
 * Base Exception class for database related actions
 * 
 * <p>
 * @author    realMethods, Inc.
 */
public class FileAlreadyExistsException extends FrameworkCheckedException
{

//************************************************************************    
// Public Methods
//************************************************************************

    /** 
     * Constructor with message.
     *
     * @param message   text of the exception
     */
    public FileAlreadyExistsException( String message )
    {
        super( message );    
    }

    /**
     * Constructor with a Throwable for chained exception and a message.
     * 
     * @param message
     * @param exception
     */
    public FileAlreadyExistsException( String message, Throwable exception )
    {
        super( message, exception ); 
    }

}



