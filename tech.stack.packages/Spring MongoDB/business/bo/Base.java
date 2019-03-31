#header()
package ${aib.getRootPackageName()}.#getBOPackageName();

import java.util.*;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Id;

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
    {
    	this.objectId = ((Base)object).objectId;
    }
    
    public void copy( Object object )
    {
    	this.objectId = ((Base)object).objectId;
    }

    public ObjectId getObjectId()
    {
    	return objectId;
    }
    
    public void setObjectId( ObjectId objectId )
    {
    	this.objectId = objectId;
    }
    
    // attributes
    @Id
    protected ObjectId objectId;	// MongoDB unique identified
	
}


