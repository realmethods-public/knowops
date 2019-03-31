#header()
#set( $appName = $aib.getApplicationNameFormatted() )
#set( $className = $classObject.getName() )
using System;
using System.Collections;

namespace ${appName}.PrimaryKeys
{
    /// <summary>
    /// ${className} PrimaryKey class, encapsulating the key field(s) of an associated ${className} model 
    /// 
    /// @author    $aib.getAuthor()
    /// </summary>
	public class ${className}PrimaryKey
#set( $pkParentName = "" )
#if ( $classObject.hasParent() == true )
#set( $pkParentName = "${classObject.getParentName()}PrimaryKey" )
#end ##$classObject.hasParent() == true
#if ( $pkParentName.length() > 0 )
	: $pkParentName
#else
    : BasePrimaryKey
#end ##$pkParentName.length() > 0	
	{

//************************************************************************
// Public Methods
//************************************************************************
    	
        /// <summary>
        /// default constructor
        /// </summary>
		public ${className}PrimaryKey()
		{
		}
    
        /// <summary>
        /// Constructor with all arguments relating to the primary key
        /// </summary>
	    public ${classObject.getName()}PrimaryKey(    
#getAllPrimaryKeyArgumentsForDotNet( $classObject true )
		)
	    {
#set( $keyFields = $classObject.getAllPrimaryKeysInHierarchy() )
#foreach( $attribute in $keyFields )
##only provide accessors for primary-key fields
#if ( $attribute.isPrimaryKey() == true )
			${Utils.capitalizeFirstLetter( ${attribute.getAsArgument()} )} = ${attribute.getAsArgument()};
#end ##$attribute.isPrimaryKey() == true
#end ##foreach $attribute in $keyFields
    	}   

//************************************************************************
// Overload 
//************************************************************************

        /// <summary>
        /// Returns the key or keys associated with a associated ${className} model
        /// <returns></returns>
        /// </summary>
	    override public ArrayList keys()
	    {
			// assign the attributes to the Collection back to the parent
	        ArrayList keys = new ArrayList();
	        
#foreach( $primaryKey in $classObject.getAllPrimaryKeysInHierarchy() )
		    keys.Add( ${Utils.capitalizeFirstLetter( ${primaryKey.getAsArgument()} )} );
#end ##foreach $primaryKey in $classObject.getAllPrimaryKeysInHierarchy()

    	    return( keys );
	    }

#if ( $classObject.hasParent() == false )
        /// <summary>
        /// Returns the first assigned key
        /// <returns></returns>
        /// </summary>
		override public Object getFirstKey()
		{				   
#set( $pk = "$classObject.getAllPrimaryKeysInHierarchy().get(0)" )		
			return( ${Utils.capitalizeFirstLetter( $pk )} );
		}
#end
 
    
//************************************************************************
// Attributes
//************************************************************************

#getKeyFieldDeclarationsForDotNet()

	}
}


