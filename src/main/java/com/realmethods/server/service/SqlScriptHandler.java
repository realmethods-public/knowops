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
package com.realmethods.server.service;

import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibatis.common.jdbc.ScriptRunner;

import com.realmethods.common.helpers.DBConnInfo;

import org.apache.commons.io.FilenameUtils;

/**
 * Controller class to handle the uploading of a SQL script, determining its type,
 * calling the appropriate parser to handle parsing of the model into a realMethods
 * Geneation-Time Model.  
 * 
 * Extends a DBSchemaHandler, which helps with the database related data and actions.
 * 
 * @author realMethods, Inc.
 * 
 */
public class SqlScriptHandler extends DBSchemaHandler implements IModelParser {

	/** 
	 * Default constructor, delegates to super class.
	 */
	public SqlScriptHandler() {
		super();
	}

	/**
	 * Constructor, delegates to super class.
	 * 
	 * @param dbConnInfo
	 */
	public SqlScriptHandler(DBConnInfo dbConnInfo) {
		super( dbConnInfo );
	}

	/**
	 * implementation of IModelParser
	 * 
	 * @param fileName
	 * @return
	 */
	public boolean loadModel( String fileName ) {
		return loadModel( fileName, FilenameUtils.getName(fileName) );
	}
	
	/**
	 * Loads a SQL script file from the provided absolute path, relative to this running instance.
	 * 
	 * Delegates to the super class to reverseEngineer() 
	 * 
	 * @param sqlScriptAbsolutePath
	 * @param sqlScriptFileName
	 * @return boolean
	 */
	public boolean loadModel( String sqlScriptAbsolutePath, String sqlScriptFileName ) {
		if (sqlScriptAbsolutePath != null 
				&& sqlScriptFileName != null
				&& executeSqlScript(sqlScriptAbsolutePath, sqlScriptFileName)) {
			return ( super.reverseEngineer() );
		}
		return (true);
	}

	/**
	 * Executes the provided SQL Script against making a database connection using the
	 * given connection parameters
	 * 
	 * @param sqlScript
	 * @return boolean
	 */
	protected boolean executeSqlScript(String sqlScriptAbsolutePath, String sqlScriptFileName) {	
		String msg = "Running the SQL script file " + sqlScriptFileName;
		LOGGER.info( msg );
				
		// if a name of the database is not provided using constructor SqlScriptHandler( DBConnInfo), 
		// deduce from the SQL Script file name
		// and delimit on the String prior to the '.', '-', or '_' characters
		if ( databaseName == null || databaseName.isEmpty() )
		{
			String dbNameToReplaceWith	= sqlScriptFileName;
			StringTokenizer tokenizer 	= new StringTokenizer( sqlScriptFileName, "-_.");
			
			if ( tokenizer.hasMoreTokens() )
				dbNameToReplaceWith = tokenizer.nextToken();
			
			// assign internally
			setDatabaseName( dbNameToReplaceWith );
		}

		LOGGER.severe("Connection URL is " + getConnectionUrl());
		try (java.io.Reader reader = new java.io.FileReader(sqlScriptAbsolutePath) ){
			dbConn = getConnection();

			statement = dbConn.createStatement();
			statement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + getDatabaseName() );
			// initialize the ScriptRunner and execute the SQL Script
			new ScriptRunner(dbConn, false, false).runScript(reader);

			LOGGER.info( "Successfully executed SQL script file" );
			
			// adjust the connectionUrl to point to the imported database
			connectionUrl = getConnectionUrl().replace("realmethods", getDatabaseName() );
		} catch (Exception e) {
			LOGGER.log( Level.WARNING, "SQL Script Execution failed", e );
			return false;
		}
		
		return true;
	}
	
// attributes
	private static final Logger LOGGER = Logger.getLogger(SqlScriptHandler.class.getName());
	
}
