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

/**
 * Exception class for failure of making use of a FrameworkDAO object
 * 
 * <p>
 * @author    realMethods, Inc.
 * @see		  com.cloudmigrate.foundational.integration.dao.FrameworkDAO
 * @see		  com.cloudmigrate.foundational.business.vo.FrameworkValueObject
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

/*
 * Change Log:
 * $Log: VersionUpdateException.java,v $
 */




