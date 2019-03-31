#header()
#set( $appName = $aib.getApplicationNameFormatted() )
#set( $className = $classObject.getName() )
using System;
using System.Collections;
using System.Collections.Generic;

using Newtonsoft.Json;
using Newtonsoft.Json.Converters;

using ${appName}.DAOs;
using ${appName}.Models;
using ${appName}.PrimaryKeys;

namespace ${appName}.Models
{
    /// <summary>
    /// ${className} enumerator class
    /// </summary>
	[JsonConverter(typeof(StringEnumConverter))]	 
	public enum ${className}
	{
#set( $enumVarDecl = "" )
#set( $attributes = $classObject.getAttributes() )
#foreach ( $attribute in $attributes )
#set( $enumVarDecl = "${enumVarDecl}${attribute.getName()}=${velocityCount}")
#if( $velocityCount < $attributes.size() )		
#set( $enumVarDecl = "${enumVarDecl},")
#else
#set( $enumVarDecl = "${enumVarDecl}")	
#end
#end
		$enumVarDecl
	}

    /// <summary>
    /// Extension of capabilities for the ${className} enum
    /// </summary>
	public static class ${className}Extensions
	{
		//************************************************************************
		// static implementations
		//************************************************************************

	    /// <summary>
        /// Returns a list containing a string representation of each enumerated object
        /// </summary>
	    static public List<${className}> getValues()
	    {
            List<${className}> types = new List<${className}>();

#foreach( $attribute in $classObject.getAttributes() )    
            types.Add(${className}.${attribute.getName()});
#end            

	        return types;
	    }
	
        /// <summary>
        /// Returns the designated default enum value, which is the first of the orderd primary keys discovered
        /// during parsing of the entity model
        /// </summary>
	    static public $className getDefaultValue()
	    {
	        return( $className.${classObject.getAttributes().iterator().next().getName()} );
	    }
#set( $attributeNames = $classObject.getAttributeNames() )
	    
        /// <summary>
        /// Compares the input arg with the named value of each enumeration, returning a match if found, othewise
        ///    
        /// <returns></returns>
        /// </summary>
        /// <param name="name"></param>
	    static public $className whichOne( String name ) 
	    {
#foreach( $attribute in $classObject.getAttributes() )    
	        if ( name.Equals( "${attribute.getName()}" ) )
	        {
	            return (${className}.${attribute.getName()});
	        }
#end        
	        else
	        {
	            return (getDefaultValue());
	        }
	    }
	}
}


