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
package com.cloudmigrate.common.helpers;

import com.cloudmigrate.foundational.common.properties.PropertyFileLoader;

/**
 * Encapsulates the attributes for database connections
 *  
 * @author realMethods, Inc.
 */
public class DBConnInfo {
	
	public DBConnInfo() {
		org.hibernate.cfg.Configuration cfg = PropertyFileLoader.getInstance().getHibernateConfiguration();
		if ( cfg != null ) {
			driver 			= cfg.getProperty("hibernate.connection.driver_class");
			dialect 		= cfg.getProperty("hibernate.dialect");
			connectionUrl 	= cfg.getProperty("hibernate.connection.url");
			userId 			= cfg.getProperty("hibernate.connection.username");
			password 		= cfg.getProperty("hibernate.connection.password");
		}
		
	}
	
	/**
	 * if databaseName is null, attempts to get the data from System properties and deducing
	 * from the Hibernate ORM properties. Instead of assuming Hibernate ORM, will soon abstract out a registered ORM
	 * handler.
	 * 
	 * System properties looked for are DB_DRIVER, DB_DIALECT, DB_CONNECTION_URL, DB_USER_NAME, DB_PASSWORD, DB_TYPE.
	 * 
	 * ex: jdbc:mysql://localhost:3306/mydatabase
	 * @return
	 */
	public String determineConnectionUrl()
	{
		//ex: jdbc:mysql://localhost:3306/sakila
		if ( databaseName != null )
			return ("jdbc:" 
						+ subProtocol + "://" 
						+ serverName + ":" 
						+ port + "/" + databaseName + "?createDatabaseIfNotExist=true");
		else
		{
			driver = java.lang.System.getProperty("DB_DRIVER");
			dialect = java.lang.System.getProperty("DB_DIALECT");
			this.connectionUrl = java.lang.System.getProperty("DB_CONNECTION_URL");
			userId = java.lang.System.getProperty("DB_USER_NAME");
			password = java.lang.System.getProperty("DB_PASSWORD");
			dbType = java.lang.System.getProperty("DB_TYPE");

			if (dbType == null)
				dbType = "MySQL";


			// go to the hibernate cfg to get the connection if not provided as
			// system parameters
			if (connectionUrl == null) {
				org.hibernate.cfg.Configuration cfg = PropertyFileLoader.getInstance().getHibernateConfiguration();

				driver = cfg.getProperty("hibernate.connection.driver_class");
				dialect = cfg.getProperty("hibernate.dialect");
				this.connectionUrl = cfg.getProperty("hibernate.connection.url");

				userId = cfg.getProperty("hibernate.connection.username");
				password = cfg.getProperty("hibernate.connection.password");
			}

			// assume mysql
			if (driver == null)
				driver = "com.mysql.jdbc.Driver";

			// assume mysql
			if (dialect == null)
				dialect = "org.hibernate.dialect.MySQLDialect";

			
			return ( connectionUrl );
		}
	}	

	/**
	 * Returns the userId field.
	 * 
	 * @return String
	 */
	public String getUserId() {
		return userId;
	}
	
	/**
	 * Assigns the userId field from the provided argument.
	 * 
	 * @param userId
	 */
	public void setUserId( String userId ) {
		this.userId = userId;
	}
	 
	/**
	 * Returns the password field.
	 * 
	 * @return String
	 */
	public String getPassword() {
		return password;
	}
	
	/**
	 * Assign the password field using the provided argument.
	 * 
	 * @param pw
	 */
	public void setPassword( String pw ) {
		password = pw;
	}
	
	/**
	 * Returns the driver field. 
	 * 
	 * This is the JDBC driver.
	 * 
	 * Defaults to com.mysql.jdbc.Driver.
	 * 
	 * @return String
	 */
	public String getDriver() {
		return driver;
	}
	
	/**
	 * Assigns the driver field using the provided argument.
	 * 
	 * This is the JDBC driver.
	 * 
	 * Defaults to com.mysql.jdbc.Driver.
	 * 
	 * @param driver
	 */
	public void setDriver( String driver ) {
		this.driver = driver;
	}
	
	/**
	 * Returns the connection URL.
	 * 
	 * @return String
	 */
	public String getConnectionUrl() {
		return connectionUrl;
	}
	
	/**
	 * Assigns the connection URL using the provided argument.
	 * 
	 * @param url
	 */
	public void setConnectionUrl( String url ) {
		connectionUrl = url;
	}
	
	/**
	 * Returns the dialect field.
	 * 
	 * @return String
	 */
	public String getDialect() {
		return dialect;
	}
	
	/**
	 * Assigns the dialect field using the provided argument.
	 * 
	 * @param dialect
	 */
	public void setDialect( String dialect ) {
		this.dialect = dialect;
	}
	
	/**
	 * Returns the dbType field.
	 * 
	 * Defaults to MySQL.
	 * 
	 * @return String
	 */
	public String getDbType() {
		return dbType;
	}
	
	/**
	 * Assigns the dbType field using the provided argument.
	 * 
	 * @param dbType
	 */
	public void setDbType( String dbType ) {
		this.dbType = dbType;
	}
	
	/**
	 * Returns the serverName field.
	 * 
	 * Defaults to localhost.
	 * 
	 * @return String
	 */
	public String getServerName() {
		return serverName;
	}
	
	/**
	 * Assigns the serverName field using the provided argument.
	 * 
	 * Defaults to localhost.
	 * 
	 * @param serverName
	 */
	public void setServerName( String serverName ) {
		this.serverName = serverName;
	}
	
	/**
	 * Returns the port field.
	 * 
	 * Defaults to 3006.
	 * 
	 * @return String
	 */
	public String getPort() {
		return port;
	}
	
	/**
	 * Assigns the port field using the provided argument.
	 * 
	 * Defaults to 3306.
	 * 
	 * @param port
	 */
	public void setPort( String port ) {
		this.port = port;
	}
	
	/**
	 * Returns the subProtocol field.
	 * 
	 * Defaults to mysql.
	 * 
	 * @return String
	 */
	public String getSubProtocol() {
		return subProtocol;
	}
	
	/**
	 * Assigns the protocol field using the provided argument.
	 * 
	 * Defaults to mysql.
	 * 
	 * @param protocol
	 */
	public void setSubProtocol( String protocol ) {
		subProtocol = protocol;
	}
	
	/**
	 * Return the databaseName field.
	 * 
	 * @return String
	 */
	public String getDatabaseName() {
		return databaseName;
	}

	/**
	 * Assigns the databaseName field using the provided argument.
	 * 
	 * @param dbName
	 */
	public void setDatabaseName( String dbName ) {
		databaseName = dbName;
	}

// attributes
	
	protected String userId 		= null;
	protected String password 		= null;
	protected String connectionUrl 	= null;
	protected String dialect 		= null;
	protected String databaseName 	= null;
	protected String driver 		= "com.mysql.jdbc.Driver";
	protected String dbType 		= "MySQL";
	protected String serverName 	= "localhost";
	protected String port 			= "3306";
	protected String subProtocol 	= "mysql";


}
