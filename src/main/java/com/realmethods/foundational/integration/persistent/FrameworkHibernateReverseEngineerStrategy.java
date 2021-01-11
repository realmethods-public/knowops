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
package com.realmethods.foundational.integration.persistent;

import java.util.*;
import java.util.logging.Logger;

import org.hibernate.cfg.reveng.*;


public class FrameworkHibernateReverseEngineerStrategy extends DelegatingReverseEngineeringStrategy  
{

    public FrameworkHibernateReverseEngineerStrategy(ReverseEngineeringStrategy delegate) 
    {
    	super(delegate);
    }

    @Override
    public String foreignKeyToEntityName(String keyname, 
    		TableIdentifier fromTable, 
    		List fromColumnNames, 
    		TableIdentifier referencedTable, 
    		List referencedColumnNames, 
    		boolean uniqueReference) 
    {
    	String key = super.foreignKeyToEntityName(keyname, fromTable, fromColumnNames, referencedTable, referencedColumnNames, uniqueReference);
    	final String msg = "key.entityName  = " + key;
    	LOGGER.info(msg);
    	return( key );
    }

	@Override
    public String foreignKeyToCollectionName(String keyname, TableIdentifier fromTable, List fromColumns, TableIdentifier referencedTable, List referencedColumns, boolean uniqueReference)
	{
		String key = super.foreignKeyToCollectionName(keyname, fromTable, fromColumns, referencedTable, referencedColumns, uniqueReference);
		final String msg = "key.collectionname = " + key;
		LOGGER.info(msg);
		return( key );
	}

	@Override
	public String columnToHibernateTypeName(TableIdentifier table, String columnName, int sqlType, int length, int precision, int scale, boolean nullable, boolean generatedIdentifier) 
	{
		String type = super.columnToHibernateTypeName(table, columnName, sqlType, length, precision, scale, nullable, generatedIdentifier );

		String msg = "hibernate.type = "  + type;
		LOGGER.info( msg );
		
		if ( type != null && type.equalsIgnoreCase("set") )
		{
			type = "array";
		}
		return( type );
		
	}

	@Override
	public String getTableIdentifierStrategyName(org.hibernate.cfg.reveng.TableIdentifier t) 
	{
		return "native"; 
	}
	
	@Override
	public String tableToClassName(TableIdentifier tableIdentifier) 
	{
		return( super.tableToClassName(tableIdentifier));
	}	
	
	// attributes
	private static final Logger LOGGER = Logger.getLogger(FrameworkHibernateReverseEngineerStrategy.class.getName());


}
