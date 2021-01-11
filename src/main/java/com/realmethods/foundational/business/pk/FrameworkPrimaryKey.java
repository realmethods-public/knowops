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
package com.realmethods.foundational.business.pk;

//************************************
// Imports
//************************************
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/** 
 * Common base class for value objects (or any object) that have one or
 * more primary keys. 
 * <p> 
 * @author    realMethods, Inc.
 */
public class FrameworkPrimaryKey
    implements IFrameworkPrimaryKey, java.io.Serializable
{
//************************************************************************    
// Public Methods
//************************************************************************

    /**
     * default constructor 
     */
    public FrameworkPrimaryKey()
    {
		valuesAsArray = null;    	
    }

    /**
     * Accepts a Collection of values which define this key.
     * <p>
     * Use this constructor for simple or compound key scenarios.
     * <p>
     * @param       values						the key data
     */
    public FrameworkPrimaryKey( Collection values )
    {
        assignValues( values );
    }
    
    /**
     * Accepts an Object as a single key.
     * <p>
     * Use this constructor for simple key scenarios.
     * <p>
     * @param       value						the key data
     * @exception   IllegalArgumentException	thrown if values is null or empty
     */
    public FrameworkPrimaryKey( Object value )
    {
        if ( value == null )
        {
            throw new IllegalArgumentException( "FrameworkPrimaryKey(String) - must provide a non-null value." );
        }
        
        // Create a new Collection
        valuesAsArray = new Object[ 1 ]; 
        valuesAsArray[0] = value;
    }    

    /**
     * Retrieves the values as an array of Objects
     * @return Object[]
     */
    public Object[] values()
    {
    	return( valuesAsArray );
    }

    /**
     * Retrieves the values as a Collection
     * @return Collection
     */
    public Collection valuesAsCollection()
    {
    	Collection coll = new ArrayList();
		
		if ( valuesAsArray != null  )
		{
			for ( int i = 0; i < valuesAsArray.length; i++ )
				coll.add( valuesAsArray[i] );
		}
        return( coll );
    }

    /**
     * Retrieves the keys as values separated by a ","
     * @return 	String
     */
    public String getValuesAsString()
    {
    	String delim = ",";
    	StringBuilder values = new StringBuilder();
		Collection valuesAsCollection = valuesAsCollection();
		Object obj = null;
		
		if ( valuesAsCollection != null  )
		{
			Iterator iter = valuesAsCollection.iterator();
			while ( iter.hasNext() )
			{
				obj = iter.next();
				if ( obj != null )
				{
					values.append( obj.toString() );
					if ( iter.hasNext() )  
						values.append( delim );
				}
			}
		}
        return( values.toString() );
    }
    
    /**
     * Are the contained key values non-null.
     * @return boolean
     */
    public boolean isEmpty()
    {
		// one null key in a multikey scenario denotes an empty one
    	Collection coll = valuesAsCollection();
		boolean bEmpty = false;
		
    	if ( coll != null )
    	{
    		Iterator iter = coll.iterator();
    		while( iter.hasNext() && !bEmpty )
    		{
    			if ( iter.next() == null )
    				bEmpty = true;
    		}
    	}    	 
        
        return( bEmpty );
    	
    }
    
	/**
	 * Returns true if a non-null, non-default value has been assigned
	 * as a primary key value.  This is here as a convenience, and may not
	 * be the best way to determine the create vs. save ability of the
	 * associated entity.
	 * 
	 * @return	boolean
	 */
	public boolean hasBeenAssigned()
	{
		// one null key in a multikey scenario denotes an empty one
		
		Collection coll = valuesAsCollection();
		Object key 		= null;
		boolean bHasBeen = false;
		
		if ( coll != null )
		{
			Iterator iter = coll.iterator();
			while( iter.hasNext() && !bHasBeen )
			{
				key = iter.next();
				
				if ( key != null && !key.toString().equals( defaultValue() ) )
					bHasBeen= true;
			}
		}    	 
        
		return( bHasBeen);
	}    
	
// overloads of Object

	/**
	 * Hashing algorithm applies to the contained collection of key attributes.
	 * <p>
	 * @return		int
	 */
    public int hashCode()
    {
        Collection coll = valuesAsCollection();
        
        return( coll != null ? coll.hashCode() : super.hashCode() );
    }

    /**
     * Returns whether the passed in object is equal.
     * <p>
     * @param 	o		what to compare to
     * @return 	boolean
     */
    public boolean equals( Object o )
    {
		if ( this == o )
			return( true );

		boolean bEquals = false;
			
        if ( o instanceof FrameworkPrimaryKey )
        {
			Collection coll 	= valuesAsCollection();
			Collection coll2	= ((FrameworkPrimaryKey)o).valuesAsCollection();
			
			// need to verify that they are of the same type, and then drill deeper
			// to verify the data they encapsulate is equals
			if( coll.hashCode() == coll2.hashCode() ){			
				bEquals = true;
			}
        }

        return( bEquals );
    }

    /**
     * Returns a String representation of the contained Collection, or 'no values'
     * if the Collection is empty
     * <p>
     * @return      String
     */
    public String toString()
    {
    	String s = getValuesAsString();
    	
    	if( s == null || s.length() == 0 ) {
			s = "no values";    		
    	}
    	
        return( getClass().getSimpleName() + "=" + s );
    }
    
    /**
     * Helper method for external ArrayList assignment of key values.
     * Delegates to assignValues(Collection)
     * <p>
     * @param v		data to assig
     */
    public void valuesAsCollection( Collection v )
    {
    	// still go through assignValues
    	assignValues( v );
    }
    
	/**
	 * Helper method to return the fields as a single String,
	 * using a default delimitter of ','.  Delegates internally to 
	 * keyFieldsDelimitted( String ).
	 * @return	the delimitted String
	 */    
	public String keyFieldsDelimitted()
	{
		return( keyFieldsDelimitted( "," ) );
	}
	
    /**
     * Helper method to return the fields as a single String,
     * delimitted by the provided delim argument.
     * @param 	delim
     * @return	delimitted String
     */
    public String keyFieldsDelimitted( String delim )
    {
    	StringBuilder fields = new StringBuilder();    	
    	Collection values 	= valuesAsCollection();
    	
    	if ( values != null )
    	{
    		Iterator iter = values.iterator();
    		Object obj = null;
    		
			while( iter.hasNext() )
			{
				obj = iter.next();
				if ( obj != null )
					fields.append( obj.toString() );
	    	
				if ( iter.hasNext() )
					fields.append( delim );            		                        		            
			}                
    	}
    	
		return( fields.toString() );    
    }
    
    /**
     * Default value to use in the case of a created, but unassigned
     * pk field.
     * 
     * @return		String
     */
    public static String defaultValue()
    { return( "-1" ); }
    
//************************************************************************    
// Protected/Private Methods
//************************************************************************
    
    /**
     * Internal assignment of key values
     *
     * @param	values		key data
     */
    protected void assignValues( Collection values )
    {
    	if ( values != null )
    	{    
    		valuesAsArray = new Object[ (values.size()) ];
    		valuesAsArray = values.toArray(valuesAsArray);
    	}
    	else
    		valuesAsArray = null;
    }

//************************************************************************    
// Attributes
//************************************************************************
    /**
     * Object array of ordered values used to temporarily hold the key attributes as an array.
     */
    protected transient Object[] valuesAsArray = null;

}

/*
 * Change Log:
 * $Log: FrameworkPrimaryKey.java,v $
 */
