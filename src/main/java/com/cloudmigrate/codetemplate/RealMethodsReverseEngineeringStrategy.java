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
package com.cloudmigrate.codetemplate;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.hibernate.cfg.reveng.*;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;

import com.cloudmigrate.common.helpers.Utils;
import com.cloudmigrate.entity.dao.DBSchemaHelperDAO;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Applies table and column manipulation strategies during Hibernate 
 * reverse engineering
 * 
 * @author realMethods, Inc.
 *
 */
public class RealMethodsReverseEngineeringStrategy extends DelegatingReverseEngineeringStrategy   {

	/**
	 * Overridden constructor
	 * 
	 * @param delegate
	 */
	public RealMethodsReverseEngineeringStrategy(ReverseEngineeringStrategy delegate) {
		  super(delegate);
	}

	@Override
	/**
	 * Captures and retains the connection provider to field connProvider.
	 * 
	 * Delegates to super class.
	 * 
	 * @param rti
	 */
	public void configure(ReverseEngineeringRuntimeInfo rti) {
		LOGGER.info("configuring");
		connProvider = rti.getConnectionProvider();
		super.configure( rti );
	}
	
	
	@Override
	/**
	 * Attempts to discover columns related to a primary key field and renaming it
	 * to by appending the Utils.PK_SUFFIX value.
	 * 
	 * @param ti
	 * @param columnName
	 * @return
	 */
	public String columnToPropertyName(final TableIdentifier ti, String columnName) 
	{
		return( super.columnToPropertyName( ti, columnName ) );
	}
	
	@Override
	/**
	 * Interrogates the TableIdentifer and user input to determine if the table should
	 * be excluded.
	 * 
	 * @param ti
	 * @return boolean
	 */
	public boolean excludeTable(TableIdentifier ti) 
	{
		return( super.excludeTable(ti) );
	}
	
	@Override
	/**
	 * Based on the columnName, use the dbSchemaHelper to determine a user's interest
	 * to exclude the column.
	 * 
	 * @param ti
	 * @param columnName
	 * @return boolean
	 */
	public boolean excludeColumn(TableIdentifier ti, final String columnName) 
	{
		return super.excludeColumn(ti, columnName);
	}

	@Override
	/**
	 * Force primary key columns to long type
	 * 
	 * @param table
	 * @param columnName
	 * @param sqlType
	 * @param length
	 * @param precision
	 * @param scale
	 * @param nullable
	 * @param generatedIdentifier
	 * @return
	 */
	public String columnToHibernateTypeName(TableIdentifier table, String columnName, int sqlType, int length, int precision, int scale, boolean nullable, boolean generatedIdentifier) 
	{
		if ( isPrimaryKey( table, columnName) )
			return "long";
		else
			return super.columnToHibernateTypeName(table, columnName, sqlType, length, precision, scale, nullable, generatedIdentifier );
	}

	@Override
	/**
	 * Returns the name to use for a given table.
	 * 
	 * @param ti
	 * @return
	 */
	public String tableToClassName(TableIdentifier ti) 
	{
		return super.tableToClassName(ti);
	}

	/**
	 * Helper method to identify if a column in the given TableIdentifier is a
	 * a primary key or not.
	 * 
	 * @param ti
	 * @param colName
	 * @return
	 */
	private boolean isPrimaryKey( TableIdentifier ti, String colName ) 
	{
		ResultSet rs 	= null;
		
		try {
			if ( connProvider != null ) {
				rs = connProvider.getConnection().getMetaData().getPrimaryKeys(ti.getCatalog(), ti.getSchema(), ti.getName() );

				while( rs.next() )  {
					if ( rs.getString("COLUMN_NAME").equalsIgnoreCase( colName ) ) {
						return true;
					}
				}
			}
			else
				LOGGER.severe( "CloudMigrateReverseEngineeringStratgey.isPrimaryKey() - connection provider is unassigned" );
		}
		catch( SQLException exc ) {
			LOGGER.log( Level.WARNING, "CloudMigrateReverseEngineeringStratgey.isPrimaryKey()", exc);
		}
		catch( Exception throwable ) {
			LOGGER.severe( throwable.getMessage() );
		}
		finally {
			try {
				if ( rs != null && !rs.isClosed() )
					rs.close();
			}
			catch( SQLException exc2 )
			{
				LOGGER.log( Level.WARNING, "CloudMigrateReverseEngineeringStratgey.isPrimaryKey()", exc2);
			}
			
		}
		
		return false;
	}
	
	/**
	 * Helper method used to apply an alphanumeric naming convention to a column.
	 * 
	 * @param colName
	 * @return
	 */
	private String adjustColumnName( String colName )
	{
		if ( colName != null )
		{
			colName = colName.replaceAll( "[^a-zA-Z_0-9]", "" );
		}
		
		return colName;
	}
	
	/**
	 * Helper to determine if the original col name or the altered one should be used.
	 * @param column
	 * @return
	 */
	private String getBestColumnName( DBSchemaHelper.Column column ) {
		if ( !column.getExclude().booleanValue() )
			return column.getName();
		else 
			return column.getOldName();
	}
	
	// attributes
	protected DBSchemaHelper dbSchemaHelper 		= null;
	protected ConnectionProvider connProvider		= null;
	private static final Logger LOGGER 				= Logger.getLogger(RealMethodsReverseEngineeringStrategy.class.getName());
	
}
