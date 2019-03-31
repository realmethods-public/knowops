#header()
#set( $appName = $aib.getApplicationNameFormatted() )
#set( $className = $className )
using System;
using System.Collections;

namespace ${appName}.PrimaryKeys
{
    /// <summary>
    /// abstract base class for all Primary Key classes
    /// 
    /// @author    $aib.getAuthor()
    /// </summary>
	public abstract class BasePrimaryKey
	{
        /// <summary>
        /// returns an ArrayList of all the keys, ordered as discovered during model parsing
        /// </summary>
        /// <returns></returns>
        public abstract ArrayList keys();

        /// <summary>
        /// returns the first key which is handy if the implementation model guarantees a single
        /// primary key for each model, saving the effort of treating primary keys as a group
        /// </summary>
        /// <returns></returns>
    	public abstract Object getFirstKey();
    
        /// <summary>
        /// determines if minimally the first key has been assigned to a non-null value
        /// </summary>
        /// <returns></returns>
    	public bool hasBeenAssigned()
    	{
    		return( getFirstKey() != null );
    	}
	}
}

