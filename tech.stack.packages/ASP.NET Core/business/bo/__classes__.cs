#header()
#set( $appName = $aib.getApplicationNameFormatted() )
#set( $className = $classObject.getName() )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter(${className})} )
using System;
using System.Collections;
using System.Collections.Generic;
using Microsoft.Extensions.Logging;

using ${appName}.Exceptions;
using ${appName}.PrimaryKeys;

namespace ${appName}.Models
{
    /// <summary>
#if( $classObject.hasDocumentation() == false )
    /// Encapsulates data for ${className} model
#else
    /// $classObject.getDocumentation()
#end 
    /// 
    /// @author $aib.getAuthor()
    /// </summary>
#getBOClassDecl()

        //************************************************************************
        // Constructors
        //************************************************************************

        /// <summary>
        /// default constructor
        /// </summary>
        public ${className}()
		{
		}

//************************************************************************
// Accessor Methods
//************************************************************************
        /// <summary>
        /// returns the key fields wrapped in a ${className}PrimaryKey
        /// 
        /// <returns></returns>
        /// </summary>
	    public virtual ${className}PrimaryKey get${className}PrimaryKey() 
	    {    
	    	${className}PrimaryKey key = new ${className}PrimaryKey();
#set( $attributes = $classObject.getPrimaryKeyAttributes() )
#foreach( $attribute in $attributes )
			key.${attribute.getName()} = this.${attribute.getName()};
#end## $attribute in $attributes     	
    	    return( key );
    	}

        /// <summary>
        /// Perfoms a copy of only the direct attributes but not the associations
        /// </summary>
        /// <param name="obj"></param>
        /// <returns></returns>
        public virtual ${className} copyShallow( ${className} obj ) 
	    {
	        if ( obj == null )
	        {
	            throw ( new ProcessingException(" ${className}:copy(..) - object cannot be null.") );           
	        }
	
	        // Call base class copy
	        base.copy( obj );
        
#getCopyString( false )

			return this;
    	}

        /// <summary>
        /// Performs a deeper copy which includes copying the direct attributes and the associations
        /// </summary>
        /// <param name="obj"></param>
        /// <returns></returns>
        public virtual ${className} copy( ${className} obj ) 
	    {
	        if ( obj == null )
	        {
	            throw ( new ProcessingException(" ${className}:copy(..) - object cannot be null.") );           
	        }
	
	        // Call base class copy
	        base.copy( obj );
	        
	        // shallow copy first
	        copyShallow( obj );
	        
#getCopyString( true )
	
			return( this );
	    }

        /// <summary>
        /// returns a string representation of the object model
        /// </summary>
	    public override string ToString()
	    {
	        string returnString = base.ToString() + ", " ;     
	
#getToString( false )
	        return returnString;
	    }

        /// <summary>
        /// Return the names the model is identified by
        /// </summary>
        /// <returns></returns>
public override List<String> getAttributesByNameUserIdentifiesBy()
		{
			List<String> names = new List<String>();
					
#foreach( $attribute in $classObject.getUserIdentfiableAttributes() )
			names.Add( ${attribute} != null ? ${attribute}.ToString() : "" );
#end	
			return( names );
		}

        /// <summary>
        /// Return it's unique identity which is a concatenation of the model name 
        /// and the names of its primary key
        /// </summary>
        /// <returns></returns>
        public override String getIdentity()
	    {
			string identity = "${className}";
			
#foreach( $pk in $classObject.getPrimaryKeyAttributes() )
#set( $pkName = $pk.getName() )	
			identity = identity + "::" ;
			identity = identity + "${pkName}" ;
#end
	        return ( identity );
	    }

        /// <summary>
        /// Return the model type    
        /// 
        /// <returns></returns>
        public override string getObjectType()
	    {
	        return ("${className}");
	    }

        /// <summary>
        /// Compares this instance to the provided Object
        /// <param name="obj"></param>
        /// <returns></returns>
        /// </summary> 
        public override bool equals( Object obj )
		{
		    if (this == obj) 
		        return true;
		        
			if ( obj == null )
				return false;
				
			${className} bo = (${className})obj;
			
			return( get${className}PrimaryKey().Equals( bo.get${className}PrimaryKey() ) ); 
		}

// attributes
#getAttributeDeclarations( true )
    }
}


