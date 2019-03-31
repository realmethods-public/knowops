#header()
package ${aib.getRootPackageName(true)}.#getBOPackageName();
#set( $className = $classObject.getName() )
#set( $commonType = $classObject.getCommonType() )

import java.util.*;

/**
 * ${className} enumerator class.  
 * 
 * Enumerated types are handled on behalf of Hiberate as VARCHARs.  The necessary
 * methods that implement Hibernat's UserType interface assume that Enumerated
 * types contain one or more values, each named uniquely and declared (modeled) with
 * order, although the order is assumed.
 * 
// AIB : \#enumDocumentation()
#enumDocumentation()
// ~AIB
 * @author $aib.getAuthor()
 */
public enum ${className}
{
#set( $enumVarDecl = "" )
#set( $attributes = $classObject.getAttributes() )
#foreach ( $attribute in $attributes )
	#set( $enumVarDecl = "${enumVarDecl}${attribute.getName()}")
	#if( $velocityCount < $attributes.size() )		
		#set( $enumVarDecl = "${enumVarDecl},")
	#else
		#set( $enumVarDecl = "${enumVarDecl};")	
	#end
#end
$enumVarDecl

//************************************************************************
// Access Methods
//************************************************************************

    public static List<${className}> getValues()
    {
        return Arrays.asList(${className}.values());
    }


    public static $className getDefaultValue()
    {
        return( ${classObject.getAttributes().iterator().next().getName()} );
    }

    
//************************************************************************
// Helper Methods
//************************************************************************

//************************************************************************
// static implementations
//************************************************************************
#set( $attributeNames = $classObject.getAttributeNames() )
    
    public static $className whichOne( String name ) 
    {
#foreach( $attribute in $classObject.getAttributes() )    
        if ( name.equalsIgnoreCase("${attribute.getName()}" ) )
        {
            return (${className}.${attribute.getName()});
        }
#end        
        else
        {
            return (getDefaultValue());
        }
    }

//************************************************************************
// Protected / Private Methods
//************************************************************************
    
//************************************************************************
// Attributes
//************************************************************************

}


