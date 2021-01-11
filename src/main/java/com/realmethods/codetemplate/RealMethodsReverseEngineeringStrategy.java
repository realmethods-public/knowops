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
package com.realmethods.codetemplate;

import java.util.List;
import java.util.logging.Logger;

import org.hibernate.cfg.reveng.*;

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
	 * @param delegate	Hibernate reverse engineering strategy instance
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
	 * @param rti		Hibernate reverse engineering runtime info
	 */
	public void configure(ReverseEngineeringRuntimeInfo rti) {
		LOGGER.info("reverse engineering in process...");
		super.configure( rti );
	}
	
	
	@Override
	/**
	 * Force primary key columns to long type
	 * 
	 * @param table			table identifier
	 * @param columnName	column name
	 * @param sqlType		sql type
	 * @param length		length
	 * @param precision		precision
	 * @param scale			scale
	 * @param nullable		can be nulled
	 * @param generatedIdentifier identifier was generated or now
	 * @return				the column type
	 */
	public String columnToHibernateTypeName(TableIdentifier table, String columnName, int sqlType, int length, int precision, int scale, boolean nullable, boolean generatedIdentifier) 
	{
		if ( isPrimaryKey( table, columnName) )
			return "long";
		else
			return super.columnToHibernateTypeName(table, columnName, sqlType, length, precision, scale, nullable, generatedIdentifier );
	}


	/**
	 * Helper method to identify if a column in the given TableIdentifier is a
	 * a primary key or not.
	 * 
	 * @param ti			table identifier
	 * @param colName		column name
	 * @return				true/false if a primary key column
	 */
	private boolean isPrimaryKey( TableIdentifier ti, String colName ) 
	{
		List primaryKeys = getPrimaryKeyColumnNames(ti);
		
		return ( primaryKeys != null && primaryKeys.contains( colName) );
	}
	
	
	// attributes
	private static final Logger LOGGER 				= Logger.getLogger(RealMethodsReverseEngineeringStrategy.class.getName());
	
}
