#header()
package ${aib.getRootPackageName()}.#getBOPackageName();

import java.util.*;

import ${aib.getRootPackageName()}.#getBOPackageName().*;

/**
 * Base class for application Value Objects.
 * 
 * @author $aib.getAuthor()
 */ 
public abstract class Base
{
	/**
	 * Default Constructor
	 */
	protected Base()
	{
		super();
	}
	
    public abstract String getIdentity();
    public abstract String getObjectType();
    
    public boolean equals( Object object )
    { return true; }
    public void copyShallow( Object object )
    {}
    public void copy( Object object )
    {}
	
	public Collection<String> getAttributesByNameUserIdentifiesBy()
	{
		Collection<String> names = new ArrayList<String>();
		// if not overloaded, try looking for a name attribute in the hierarchy
		// if none found
		names.add( "No name assigned..." );
		return ( names );
	}	
	
	public String getAttributesByNameUserIdentifiesByAsString()
	{
		Collection<String> names = getAttributesByNameUserIdentifiesBy();
		Iterator<String> iter = names.iterator();
		String name = "";
		
		while( iter.hasNext() )
		{
			name += iter.next();
			if ( iter.hasNext() )
				name += " ";
		}
		return( name );						
	}

}


