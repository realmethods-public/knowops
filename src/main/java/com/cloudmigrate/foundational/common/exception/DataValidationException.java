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

//************************************
// Imports
//************************************

/**
 * Exception class for failure to validate data
 * <p>
 * @author    realMethods, Inc.
 */
public class DataValidationException extends FrameworkCheckedException
{

// constructors
   /**
	* default constructor
	*/
   public DataValidationException()
   {
   }

   /**
	* constructor
	*
	* @param message   text of the exception
	*/
   public DataValidationException( String message )
   {
	   super( message );
   }
    
   /**
	* Constructor with a Throwable for chained exception and a message.
     * @param message
     * @param exception
	*/
   public DataValidationException( String message, Throwable exception )
   {
	   super( message, exception ); 
   }
}

/*
 * Change Log:
 * $Log: DataValidationException,v $
 */



