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
package com.realmethods.foundational.common;

// **********************
// Imports
// **********************

import java.io.*;
import java.util.logging.Logger;


/**
 * Base class of many objects utilized and observed by the Framework.  
 * Provides minimal capabilities to access the default Log4J Logger, but
 * mainly serves for type identity.
 * <p> 
 * @author    realMethods, Inc.
 */
public class FrameworkBaseObject extends Object implements IFrameworkBaseObject
{
	
//************************************************************************
// Public Methods
//************************************************************************
    /** 
     * default constructor
     */
    public FrameworkBaseObject()
    {
        super();
    }


    /** 
     * Logs the message as an info.
     * <p>
     * <b>Note:</b></br>
     */
    public void log( String msg )
    {
    	logInfoMessage( msg );
    }
    
    /** 
     * Logs the message to Log4j as default type INFO
     * <p>
     * @param	msg		message to log
     */
    public void logMessage( String msg )
    {
        logInfoMessage(msg);
    }
      
	/** 
	 * Logs the message to Log4j as type INFO
	 * <p>
	 * @param	msg		message to log
	 */        
	public void logInfoMessage( String msg )
	{
		LOGGER.info(msg);
	}
	   
	/** 
	 * Logs the message to Log4j as type WARN
	 * <p>
	 * @param	msg		message to log
	 */	   
	public void logWarnMessage( String msg )
	{
		LOGGER.warning(msg);
	}
	
	/** 
	 * Logs the message to Log4j as type ERROR
	 * <p>
	 * @param	msg		message to log
	 */	
	public void logErrorMessage( String msg ) {
		LOGGER.severe(msg);
	}
	
	/** 
	 * Logs the message to Log4j as type DEBUG
	 * <p>
	 * @param	msg		message to log
	 */	
	public void logDebugMessage( String msg ) {
    		LOGGER.info( msg );
	}
	

	// attributes 
    protected static final  long serialVersionUID = -1;
	private static final Logger LOGGER 	= Logger.getLogger(FrameworkBaseObject.class.getName());

}

/*
 * Change Log:
 * $Log: FrameworkBaseObject.java,v $
 */
