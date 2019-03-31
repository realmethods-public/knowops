#header()
package ${aib.getRootPackageName()}.#getPrimaryKeyPackageName();

import java.util.*;


/**
 * Base class for application PrimaryKey classes.
 * 
 * @author    $aib.getAuthor()
 */
public abstract class BasePrimaryKey
{
	// for backward compatibility
    public Collection valuesAsCollection()
    {
        return (keys());
    }
    
    public abstract List keys();
    
    public boolean hasBeenAssigned()
    {
    	List keys = keys();
    	return( keys != null && keys.size() > 0 );
    }
}


