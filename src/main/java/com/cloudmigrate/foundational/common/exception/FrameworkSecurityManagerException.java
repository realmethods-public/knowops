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
 * Used to indicate an error occured in security processing.
 *
 * <p>
 * @author    realMethods, Inc.
 * @see		  com.cloudmigrate.foundational.integration.security.FrameworkSecurityManager
 * @see		  com.cloudmigrate.foundational.integration.security.FrameworkACLSecurityManager
 * @see		  com.cloudmigrate.foundational.integration.security.jaas.FrameworkJAASSecurityManager
 */
public class FrameworkSecurityManagerException extends FrameworkCheckedException
{

//************************************************************************    
// Public Methods
//************************************************************************

    /** 
     * Base constructor.
     */
    public FrameworkSecurityManagerException()
    {
        super(); 
    }

    /** Constructor with message.
     * @param message text of the exception
     */
    public FrameworkSecurityManagerException( String message )
    {
        super( message ); 
    }

    /**
     * Constructor with a Throwable for chained exception and a message.
     * 
     * @param message
     * @param exception
     */
    public FrameworkSecurityManagerException( String message, Throwable exception )
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
 * $Log: FrameworkSecurityManagerException.java,v $
 * Revision 1.2  2003/08/05 12:16:17  tylertravis
 * - Updated header language to LGPL
 * - Improved exception handling globally
 * - Modified member variables to conform to Java Beans std.
 *
 * Revision 1.1.1.1  2003/07/05 03:05:18  tylertravis
 * initial sourceforge cvs revision
 *
 */




