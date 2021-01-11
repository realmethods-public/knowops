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
/**
 *  Workfile: $ Revision: $
 *  Last Modified by:   Author: $ on Date: $
 *
 */package com.realmethods.entity.dao;

import java.io.*;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.realmethods.codetemplate.DBSchemaHelper;

import org.apache.commons.io.*;

import com.google.gson.*;

/**
 * Implements the Hibernate persistence processing for business entity DBSchemaHelper.
 * 
 * @author realMethods, Inc.
 */

public class DBSchemaHelperDAO {
	

	/**
	 * default constructor
	 */
	public DBSchemaHelperDAO() 
	{
		// empty since no member data to assign or no initialization
	}

	public DBSchemaHelper findDBSchemaHelper(String databaseName) 
	{
		DBSchemaHelper helper = null;
		
		try
		{
			String data = FileUtils.readFileToString(getFile(databaseName), "UTF-8" );
			helper = new Gson().fromJson( data, DBSchemaHelper.class );
		}
		catch( Exception exc )
		{
			LOGGER.log( Level.WARNING, "DBSchemaHelperDAO.findDBSchemaHelper", exc);
		}
		return( helper );
	}

	public DBSchemaHelper createDBSchemaHelper(DBSchemaHelper helper) 
	{
		try
		{
			FileUtils.writeStringToFile(getFile( helper.getDatabaseName()), new Gson().toJson( helper ), "UTF-8" );
		}
		catch( Exception exc )
		{
			LOGGER.log( Level.WARNING, "DBSchemaHelperDAO:createDBSchemaHelper - " + exc.getMessage() );
		}
		
		return (helper);
	}

	/**
	 * Removes a DBSchemaHelper from the persistent store.
	 * 
	 * @param dbName
	 *            
	 * @exception FrameworkDAOException
	 */
	public void deleteDBSchemaHelper(String dbName)  
	{
		try
		{
			if ( dbName != null )
				FileUtils.forceDelete( getFile( dbName ) );
		}
		catch( Exception exc )
		{
			LOGGER.log( Level.WARNING, "DBSchemaHelperDAO.deleteDBSchemaHelper", exc);
		}
	}

	protected File getFile( String dbName )
	{
		return new File( "C:\\eclipse4.2\\workspace\\appgen\\target\\classes" /*System.getProperty("resourceDir")*/ +  File.separator + dbName + ".txt" );
	}
	// ~AIB

	// *****************************************************
	// Attributes
	// *****************************************************
	private static final Logger LOGGER = Logger.getLogger(DBSchemaHelperDAO.class.getName());

}
