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
package com.realmethods.server.action.model;

import com.realmethods.codetemplate.DBSchemaHelper;
import com.realmethods.common.helpers.DBConnInfo;
import com.realmethods.server.action.BaseStrutsAction;
import com.realmethods.server.service.DBSchemaHandler;
import com.realmethods.codetemplate.tools.JSONModelHandler;

/**
 * Controller class to handle the uploading of a model by delegating to the DBSchemaHandler.
 * 
 * @author realMethods, Inc.
 * 
 */

public class DBSchemaHandlerAction extends BaseStrutsAction {
	
	/**
	 * default constructor
	 */
	public DBSchemaHandlerAction() {
		// no_op
	}

	/**
	 * Returns the JSON representation of the loaded Generation-Type model
	 * 
	 * @return String
	 */
	public String getModel() {
		return (new JSONModelHandler().json());
	}

	/**
	 * Assign the db type, as supported by Hibernate.   
	 * @param dbType
	 */
	public void setDbType(String dbType) {		
		dbConnInfo.setDbType( dbType );
	}

	/**
	 * Assign the JDBC user Id necessary to make a DB connection through Hibernate
	 * @param userId
	 */	
	public void setUserId(String userId) {
		dbConnInfo.setUserId( userId );
	}

	/**
	 * Assign the JDBC password necessary to make a DB connection through Hibernate
	 * @param password
	 */	
	public void setPassword(String password) {
		dbConnInfo.setPassword( password );
	}

	/**
	 * Assign the formal vendor specific JDBC driver name
	 * @param driver
	 */		
	public void setDriver(String driver) {
		dbConnInfo.setDriver( driver );
	}

	/**
	 * Assign a predefined connection url, necessary to deduce a model directly from an accessible
	 * database connection.
	 * @param connectionUrl
	 */		
	public void setConnectionUrl(String connectionUrl) {
		dbConnInfo.setConnectionUrl( connectionUrl );
	}

	/**
	 * Assign the Hibernate dialect in order to allow the ORM to speak to the correct database vendor type.
	 * MySQL is the default
	 * @param dialect
	 */
	public void setDialect(String dialect) {
		dbConnInfo.setDialect( dialect );
	}

	/**
	 * Assign the database port #
	 * @param port
	 */
	public void setPort(String port) {
		dbConnInfo.setPort( port );
	}

	/**
	 * Assign the database server name
	 * @param serverName
	 */
	public void setServerName(String serverName) {
		dbConnInfo.setServerName( serverName );
	}

	/**
	 * Assign the jdbc url subprotocol
	 * @param subProtocol
	 */
	public void setSubProtocol(String subProtocol) {
		dbConnInfo.setSubProtocol( subProtocol );
	}

	/**
	 * Assign the name of the database
	 * @param databaseName
	 */
	public void setDatabaseName(String databaseName) {
		dbConnInfo.setDatabaseName( databaseName );
	}

	/**
	 * Assign the flag that determines if the newly referenced model should be appended to a previously loaded model.
	 * This feature is essential for the purpose of renovation, as well as the simple separation and combination of multiple 
	 * models
	 * @param appendModel
	 */
	public void setAppendModel(boolean appendModel) {
		this.appendModel = appendModel;
	}

	public DBSchemaHelper getDbSchema()
	{ return dbSchemaHandler.getDbSchema(); }
	
	public void setDbSchema( String dbSchema )
	{ 
		dbSchemaHandler.setDbSchema(dbSchema);
	}
	
	@Override
	/**
	 * Single entry point for execution of reverse engineering from DB schema
	 * 
	 * @return	String
	 */
	public String execute() {
		addInfoMessage("reverse engineering starting...");
		applyUniqueThreadName( appendModel );	
		if ( new DBSchemaHandler( dbConnInfo ).reverseEngineer() )
			return SUCCESS;
		else
			return ERROR;
	}
	

	/**
	 * Returns a JSON representation of the database schema being targeted
	 * 
	 * @return
	 */
	public String determineSchema() {
		
		dbSchemaHandler = new DBSchemaHandler( dbConnInfo );
		
		if ( dbSchemaHandler.determineSchema() )
			return SUCCESS;
		else
			return ERROR;
	}

// attributes

	protected transient DBConnInfo dbConnInfo 			= new DBConnInfo();
	protected transient DBSchemaHandler dbSchemaHandler = null;
	protected transient boolean appendModel 			= false;
}
