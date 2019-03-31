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
package com.cloudmigrate.server.service;

import com.cloudmigrate.entity.dao.DBSchemaHelperDAO;

import com.google.gson.*;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.cloudmigrate.common.helpers.*;
import com.cloudmigrate.codetemplate.*;
import com.cloudmigrate.codetemplate.parser.ModelParserFactory;
import com.cloudmigrate.codetemplate.tools.*;
import com.cloudmigrate.entity.*;
import com.cloudmigrate.entity.dao.*;
import com.cloudmigrate.common.helpers.DBConnInfo;

/**
 * Controller class to handle deducing a model from database schema, determining its type,
 * calling the appropriate parser to handle parsing of the model into a realMethods
 * Generation-Time Model.
 * 
 * @author realMethods, Inc.
 * 
 */
public class DBSchemaHandler {
	
	/**
	 * default constructor
	 */
	public DBSchemaHandler() {
		// no_op
	}

	/**
	 * Constructor
	 * 
	 * Assigns field dbConnInfo with the provided argument.
	 * 
	 * @param dbConnInfo
	 */
	public DBSchemaHandler( DBConnInfo dbConnInfo ) {
		this.dbConnInfo = dbConnInfo;
	}

	/**
	 * Returns the name of the newly referenced model
	 * @return
	 */
	public DBConnInfo getDBConnInfo() {
		return dbConnInfo;
	}
	
	/**
	 * Returns the dbSchemaHelper field.
	 * 
	 * @return DBSchemaHelper
	 */
	public DBSchemaHelper getDbSchema()
	{ return dbSchemaHelper; }
	
	/**
	 * Assigns the dbSchemaHelper by transposing the dbschema which is expected to be in JSON format.
	 * 
	 * Uses the Google Gson library for JSON to class transformation.
	 * 
	 * @param dbSchema
	 */
	public void setDbSchema( String dbSchema )
	{ 
		dbSchemaHelper = new Gson().fromJson( dbSchema, DBSchemaHelper.class );
	}

	/**
	 * Return the databaseName field.
	 * 
	 * @return
	 */
	public String getDatabaseName() {
		return databaseName;
	}

	/**
	 * Assign the databaseName field and delegate assignment to the dbConnInfo field.
	 * 
	 * @param databaseName
	 */
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
		dbConnInfo.setDatabaseName( databaseName );
	}

	/**
	 * This method helps to internally determine the schema of the provided databaseName.  It works its way
	 * the tables and each table's columns to populate the dbSchemaHelper field.  This field is required
	 * as part of the reverse engineer strategy.
	 * 
	 * @return boolean
	 */
	public boolean determineSchema() {

		ResultSet tableResultSet 	= null;

		LOGGER.info( "Starting to determine the schema for " + this.getConnectionUrl() );

		String errMsg = " failed read schema for " + dbConnInfo.getDatabaseName() + " : ";

		try (Connection conn = DriverManager.getConnection(getConnectionUrl(), dbConnInfo.getUserId(), dbConnInfo.getPassword())) {
			DBSchemaHelper.Table table	= null;
			DBSchemaHelper.Column column = null;
			ResultSet columnResultSet 	= null;
			String tableName 			= null;
			List<DBSchemaHelper.Column> columnArray = new ArrayList<>();				
			List<String> primaryKeys 	= new ArrayList<>();
			
			dbSchemaHelper = new DBSchemaHelper( dbConnInfo.getDatabaseName() );
			
			// loads the JDBC driver class
			loadJDBCDriver();
		
			//retrieve the set of tables associated with the database of interest
			tableResultSet = getTableSet(conn);

			// for each result in the table result set
			while (tableResultSet.next()) {
				tableName 	= tableResultSet.getString(3);
				primaryKeys	= populatePrimaryKeys(conn, tableName);
				
				table 	= createAndInitTable(tableName);

				// get the Resultset of columns for the given tableName
				columnResultSet = conn.getMetaData().getColumns(databaseName, null, tableName, "%");
				columnArray 	= new ArrayList<>();
				
				// iterate the columns
				while (columnResultSet.next()) {										
					column 			= dbSchemaHelper.new Column();
					column.setName( columnResultSet.getString("COLUMN_NAME") );
					column.setOldName( column.getName() );
					
					// if the col has no name or is a primary key, ignore it
					if ( column.getName()!= null && !primaryKeys.contains( column.getName()) )
					{
						column.setType( columnResultSet.getString("TYPE_NAME") );
						column.setColSize( columnResultSet.getString("COLUMN_SIZE") );
						column.setIsNullable(columnResultSet.getString("IS_NULLABLE").equalsIgnoreCase( "YES") ? Boolean.TRUE : Boolean.FALSE ); // javaype
						column.setExclude( Boolean.FALSE );
						columnArray.add(column);
					}
				}

				// finish up for this table
				columnResultSet.close();
				table.setColumns( columnArray );
				this.dbSchemaHelper.getTables().add(table);
				primaryKeys.clear();
			}
		} catch (Exception exc) {
			LOGGER.log(Level.WARNING, errMsg, exc );
			return (false);
		}
		finally {
			if ( tableResultSet != null ) {
				try {
					tableResultSet.close();
				}
				catch( java.sql.SQLException exc ) {
					LOGGER.warning(errMsg+ exc.getMessage());
				}
			}
		}

		LOGGER.log( Level.INFO, "Done determining the schema for " + getConnectionUrl() );
		final String schemaMsg = "Schema is " + new Gson().toJson( getDbSchema() );
		LOGGER.log( Level.INFO, schemaMsg );
		
		return (true);
	}

	
	/**
	 * Does all of the heavy lifting of turning db schema into a
	 * realMethod Generation-Time Model.
	 * 
	 * @return boolean
	 */
	public boolean reverseEngineer() {
		LOGGER.info("Reverse engineering starting...");

		if ( this.dbSchemaHelper != null )
			persistDBSchemaHelper();
		
		Properties props = new Properties();
		
		props.setProperty("username", dbConnInfo.getUserId());
		props.setProperty("password", dbConnInfo.getPassword());
		props.setProperty("dialect", dbConnInfo.getDialect());
		props.setProperty("driver", dbConnInfo.getDriver());
		props.setProperty("dbType", dbConnInfo.getDbType());
		props.setProperty("url", getConnectionUrl());

		RunHibernateRevEng hibernateRevEng = new RunHibernateRevEng(props );
		String pojoDir = null;

		try {
			LOGGER.info("Creating POJOs from DB schema starting...");
			pojoDir = hibernateRevEng.reverse();
		} catch (Exception exc) {
			LOGGER.log(Level.SEVERE, "Creation of POJOs failed", exc);
			return false;
		} finally {
			cleanUpDBRevEng();
		}

		try {
			LOGGER.info("Compiling POJOs starting...");
			hibernateRevEng.build();
		} catch (Exception exc) {
			LOGGER.log(Level.WARNING, "Compilation of created Java class files failed.. ", exc);
			return false;
		}

		try {
			String javaDir = pojoDir + File.separator + "target" + File.separator + "classes";

			LOGGER.info("Turning POJOs into internal model starting...");
			ModelParserFactory.getInstance().createJavaDirParser( javaDir ).run();
		} catch (Exception exc) {
			LOGGER.log(Level.WARNING, "Reverse engineering failed", exc);
			return false;
		} 

		return true;
	}

	/**
	 * Helper method used to construct the JDBC Connection URL using the sub-protocol, server-name
	 * port, and database-name
	 * @return
	 */
	protected String getConnectionUrl() {
		return dbConnInfo.determineConnectionUrl();
	}

	/**
	 * Helper method to create a dbSchemaHelper using the matched DBSchemaHelperDAO.
	 */
	protected void persistDBSchemaHelper()
	{
		try
		{
		//	this.dbSchemaHelper = new DBSchemaHelperDAO().createDBSchemaHelper( dbSchemaHelper );
		}
		catch( Exception exc )
		{
			LOGGER.log( Level.SEVERE, "DBSchemaHelperDAO failed to create a DBSchemaHelper", exc);
		}
	}
	
	/**
	 * Drops the database from the target RDBMS
	 */
	protected void cleanUpDBRevEng() {
		
		LOGGER.info( "Cleaning up reverse engineering work product...");
		
		final String dbName 	= getDatabaseName();
		final String dropSQL 	= "DROP DATABASE " + dbName;
		
		try {
			statement.executeUpdate(dropSQL);
		} catch (Exception exc) {
			final String msg = "Failed to drop database " + dbName;
			LOGGER.log(Level.WARNING, msg, exc);
		} finally {
			try {
				dbConn.close();
			} catch (Exception exc1) {
				LOGGER.log(Level.WARNING, "Failed to close DB Connection", exc1);
			}
		}

		dbConn = null;
		
//		deleteDBSchemaHelper();
	}

	/**
	 * Helper method to delete the DBSchema entry using the database name
	 */
	protected void deleteDBSchemaHelper()
	{
		final String dbName = getDatabaseName();
		try {
			new DBSchemaHelperDAO().deleteDBSchemaHelper( dbName );
		}
		catch( Exception exc ) {
			final String msg = "DBSchemaHelperDAO failed to delete " + dbName;
			LOGGER.log( Level.WARNING, msg, exc );
		}
		
	}
	
	protected Connection getConnection() {
		
		if ( dbConnInfo == null )
			dbConnInfo = new DBConnInfo();
		
		Connection connection = null;
		connectionUrl = dbConnInfo.determineConnectionUrl();

		try {
			Class.forName(dbConnInfo.getDriver());
			connection = DriverManager.getConnection(connectionUrl, dbConnInfo.getUserId(), dbConnInfo.getPassword());
		} catch (Exception exc) {
			LOGGER.log(Level.WARNING, "Failed to get a connection using url " + connectionUrl, exc);
		}

		return (connection);
	}

	private void loadJDBCDriver() throws ClassNotFoundException {
		Class.forName(dbConnInfo.getDriver());		
	}
	
	private ResultSet getTableSet( Connection conn ) throws SQLException {
		String[] types = { "TABLE" };
		return conn.getMetaData().getTables(null, dbConnInfo.getDatabaseName(), "%", types);		
	}

	private DBSchemaHelper.Table createAndInitTable(String tableName) {
		DBSchemaHelper.Table table = dbSchemaHelper.new Table();
		table.setName( tableName );
		table.setOldName( tableName );
		table.setExclude( Boolean.FALSE );		
		return table;
	}

	private List<String> populatePrimaryKeys( Connection conn, String tableName ) throws SQLException {
		ResultSet pkResultSet 		= conn.getMetaData().getPrimaryKeys(null, null, tableName);
		List<String> primaryKeys 	= new ArrayList<>();
				
		// collect the primary-key column names for the 
		// sake of exclusion later
		while( pkResultSet.next() ) {
			primaryKeys.add( pkResultSet.getString("COLUMN_NAME") );
		}
		pkResultSet.close();
		
		return primaryKeys;
	}
	
	// attributes
	
	protected String databaseName 			= null;
	protected DBConnInfo dbConnInfo 		= new DBConnInfo();
	protected DBSchemaHelper dbSchemaHelper = null;
	protected String connectionUrl 			= null;
	protected Connection dbConn 			= null;
	protected Statement statement 			= null;	
	private static final Logger LOGGER = Logger.getLogger(DBSchemaHandler.class.getName());
}
