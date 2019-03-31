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
    
    public boolean hasBeenAssigned()
    {
        return (getFirstKey() != null);
    }

// abstract methods
    
    public abstract List keys();
    public abstract Object getFirstKey();
}
