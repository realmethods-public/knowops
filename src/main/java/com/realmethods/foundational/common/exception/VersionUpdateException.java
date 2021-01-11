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
 * Exception class for failure of making use of a FrameworkDAO object
 * 
 * <p>
 * @author    realMethods, Inc.
 */
public class VersionUpdateException extends FrameworkCheckedException
{
    /** 
     * Base constructor.
     */
    public VersionUpdateException()
    {
        super(); 
    }

    /** 
     * Constructor with message.
     * 
     * @param message   text of the exception
     */
    public VersionUpdateException( String message )
    {
        super( message ); 
    }
    
}





