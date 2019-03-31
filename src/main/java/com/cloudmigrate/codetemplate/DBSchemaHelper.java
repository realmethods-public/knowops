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

import com.google.gson.*;
import java.util.*;

/**
 * Helper class to encapsulate the notion of tables and their columns for the sake
 * of display and modification of relevant attributes
 * 
 * @author realMethods, Inc.
 *
 */
public class DBSchemaHelper
{
	/**
	 * default constructor
	 */
	public DBSchemaHelper() {
		// no_op
	}

	/**
	 * Constructor
	 * 
	 * @param databaseName
	 */
	public DBSchemaHelper( String databaseName ) {
		this.databaseName  = databaseName;
	}
		
	/**
	 * Returns the id field.
	 * 
	 * @return Long
	 */
	public Long getId() { 
		return id; 
	}
	
	/**
	 * Assigns the id field.
	 * 
	 * @param id
	 */
	public void setId( Long id ) { 
		this.id = id; 
	}
	
	/**
	 * Returns the tables field.
	 * 
	 * @return
	 */
	public List<Table> getTables() {
		return tables;
	}
	
	/**
	 * Assigns the tables field with the provided argument.
	 * 
	 * @param tables
	 */
	public void setTables( List<Table> tables ) {
		this.tables = tables;
	}

	/**
	 * Returns the databaseName field.
	 * 
	 * @return String
	 */
	public String getDatabaseName() {
		return this.databaseName;
	}
	
	/**
	 * Assigns the databaseName field with the provided argument.
	 * 
	 * @param databaseName
	 */
	public void setDatabaseName( String databaseName ) { 
		this.databaseName = databaseName; 
	}
	

	/**
	 * Returns a JSON representation of this instance.
	 * 
	 * Uses Googles GSON API.
	 * 
	 * @return String
	 */
	public String getSchemaAsJson() {
		return new Gson().toJson( this );
	}
	
	/**
	 * Assigns itself the data obtains by instantiation an object of like type, then
	 * invoking the copy method on it.
	 * 
	 * Uses Googles GSON API.
	 * 
	 * @param json
	 */
	public void setSchemaAsJson( String json ) {
		DBSchemaHelper helper = new Gson().fromJson( json, DBSchemaHelper.class );
		copy( helper );
	}
	
	/**
	 * Copy fields from provided argument.
	 * 
	 * @param helper
	 */
	protected void copy( DBSchemaHelper helper ) {
		this.databaseName 	= helper.getDatabaseName();
		this.tables 		= helper.getTables();
	}
	
// attributes
	protected Long id				= null;
	protected List<Table> tables 	= new ArrayList<>();
	protected String databaseName	= null;
	
	/**
	 * Inner class lightly representing a database table.
	 * @author realMethods, Inc.
	 *
	 */
	public class Table
	{
		// intentional empty constructor
		public Table() { 
			// no init 
		}
		
		public String getName()
		{ return name; }
		
		public void setName( String name )
		{ this.name = name; }

		public String getOldName()
		{ return oldName; }
		
		public void setOldName( String oldName )
		{ this.oldName = oldName; }

		public Boolean getExclude()
		{ return exclude; }
		
		public void setExclude( Boolean exclude )
		{ this.exclude = exclude; }

		public List<Column> getColumns()
		{ return columns; }
		
		public void setColumns( List<Column> columns )
		{ this.columns = columns; }

		protected String name = null;
		protected String oldName = null;
		protected Boolean exclude = Boolean.FALSE;
		protected List<Column> columns = new ArrayList<>();
	}
	
	/**
	 * Inner class lightly representing a database column.
	 * @author realMethods, Inc.
	 *
	 */
	public class Column
	{
		// intentional empty constructor
		public Column() {
			// no init
		}
		
		public String getName()
		{ return name; }

		public void setName( String name )
		{ this.name = name; }

		public String getOldName()
		{ return oldName; }
		
		public void setOldName( String oldName )
		{ this.oldName = oldName; }

		public String getType()
		{ return type; }

		public void setType( String type )
		{ this.type = type; }

		public String getColSize()
		{ return colSize; }

		public void setColSize( String colSize )
		{ this.colSize = colSize; }

		public Boolean getIsNullable()
		{ return isNullable; }

		public void setIsNullable( Boolean isNullable )
		{ this.isNullable = isNullable; }

		public Boolean getExclude()
		{ return exclude; }

		public void setExclude( Boolean exclude )
		{ this.exclude = exclude; }

		protected String name 		= null;
		protected String oldName 	= null;
		protected String type 		= null;
		protected String colSize 	= null;
		protected Boolean isNullable = Boolean.TRUE;
		protected Boolean exclude 	=	Boolean.FALSE;
	}
}
