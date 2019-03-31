#header()
using System;
using System.Collections;
using System.Collections.Generic;

using ${appName}.Models;
using ${appName}.PrimaryKeys;

namespace ${appName}.Models
{
    /// <summary>
    /// Abstract ase class for models
    /// 
    /// @author $aib.getAuthor()
    /// </summary>
	public abstract class Base
	{
		
        /// <summary>
        /// A must overload by the child class to return it's unique identity
        /// </summary>
        /// <returns></returns>
	    public abstract string getIdentity();

        /// <summary>
        /// A must overload by the child class to return it's type    
        /// 
        /// <returns></returns>
        public abstract string getObjectType();

	    /// <summary>
        /// Compares this instance to the provided Object
        /// <param name="obj"></param>
        /// <returns></returns>
        /// </summary> 
	    public virtual bool equals( Object obj ) { return false; }

        /// <summary>
        /// Perfoms a copy of only the direct attributes but not the associations
        /// </summary>
        /// <param name="obj"></param>
        /// <returns></returns>
        public virtual Object copyShallow(Object obj) { return obj; }

        /// <summary>
        /// Performs a deeper copy which includes copying the direct attributes and the associations
        /// </summary>
        /// <param name="obj"></param>
        /// <returns></returns>
        public virtual Object copy(Object obj) { return obj; }
		
        /// <summary>
        /// Expected to be overloaded to return the names the model is identified by
        /// </summary>
        /// <returns></returns>
		public virtual List<string> getAttributesByNameUserIdentifiesBy()
		{
            List<string> names = new List<string>();
			// if not overloaded, try looking for a name attribute in the hierarchy
			// if none found
			names.Add( "No name assigned..." );
			return ( names );
		}	
		
        /// <summary>
        /// Returns a single string representing the names the model is identified by
        /// </summary>
        /// <returns></returns>
		public virtual string getAttributesByNameUserIdentifiesByAsString()
		{
			List<string> names 	= getAttributesByNameUserIdentifiesBy();
			String name 				= "";
			int index 					= 1;
			
			foreach( string s in names )
			{
				name = s;
				
				if ( index < names.Count )
					name = name + " ";
									
				index = index + 1;
			}
			return( name );						
		}
	
	}
}

