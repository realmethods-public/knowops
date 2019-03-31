#header()
#set( $className = $classObject.getName() )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter(${classObject.getName()})} )
package ${aib.getRootPackageName(true)}.#getBOPackageName();

import java.util.*;

#set( $imports = [ "#getPrimaryKeyPackageName()", "#getBOPackageName()" ] )
#importStatements( $imports )

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
#if( $classObject.hasDocumentation() == false )
 * Encapsulates data for business entity ${classObject.getName()}.
#else
 * $classObject.getDocumentation()
#end 
 * 
 * @author $aib.getAuthor()
 */

@JsonIgnoreProperties(ignoreUnknown = true)
// AIB : \#getBOClassDecl()
#getBOClassDecl()
// ~AIB

//************************************************************************
// Constructors
//************************************************************************

    /** 
     * Default Constructor 
     */
    public ${classObject.getName()}() 
    {
    }   

//************************************************************************
// Accessor Methods
//************************************************************************

    /** 
     * Returns the ${classObject.getName()}PrimaryKey
     * @return ${classObject.getName()}PrimaryKey   
     */
    public ${classObject.getName()}PrimaryKey get${classObject.getName()}PrimaryKey() 
    {    
    	##${classObject.getName()}PrimaryKey key = new ${classObject.getName()}PrimaryKey( get${className}Id() );
    	${classObject.getName()}PrimaryKey key = new ${classObject.getName()}PrimaryKey();
#set( $attributes = $classObject.getAllPrimaryKeysInHierarchy() )
#foreach( $attribute in $attributes )
		key.set${Utils.capitalizeFirstLetter( $attribute.getAsArgument() )}( this.${attribute.getAsArgument()} );
#end## $attribute in $attributes     	
        return( key );
    } 

#if ( ${classObject.hasParent()} == true )
/*	public Long get${className}Id()
	{ 
		return get${classObject.getParentName()}Id(); 
	}
	
	public void set${className}Id( Long id )
	{ 
		set${classObject.getParentName()}Id( id ); 
	}
*/	
#end

// AIB : \#getBOAccessorMethods(true)
#getBOAccessorMethods(true)
// ~AIB
 
    /**
     * Performs a shallow copy.
     * @param object 	${classObject.getName()}		copy source
     * @exception IllegalArgumentException 	Thrown if the passed in obj is null. It is also
     * 							thrown if the passed in businessObject is not of the correct type.
     */
    public ${classObject.getName()} copyShallow( ${classObject.getName()} object ) 
    throws IllegalArgumentException
    {
        if ( object == null )
        {
            throw new IllegalArgumentException(" ${classObject.getName()}:copy(..) - object cannot be null.");           
        }

        // Call base class copy
        super.copy( object );
        
        // Set member attributes
##		${classObject.getName().toLowerCase()}data = object.get${classObject.getName()}Data();
                
// AIB : \#getCopyString( false )
#getCopyString( false )
// ~AIB 

		return this;
    }

    /**
     * Performs a deep copy.
     * @param object 	${classObject.getName()}		copy source
     * @exception IllegalArgumentException 	Thrown if the passed in obj is null. It is also
     * 							thrown if the passed in businessObject is not of the correct type.
     */
    public ${classObject.getName()} copy( ${classObject.getName()} object ) 
    throws IllegalArgumentException
    {
        if ( object == null )
        {
            throw new IllegalArgumentException(" ${classObject.getName()}:copy(..) - object cannot be null.");           
        }

        // Call base class copy
        super.copy( object );
        
        copyShallow( object );
        // Set member attributes
##		${classObject.getName().toLowerCase()}data = object.get${classObject.getName()}Data();
                
// AIB : \#getCopyString( true )
#getCopyString( true )
// ~AIB 

		return( this );
    }

    /**
     * Returns a string representation of the object.
     * @return String
     */
    public String toString()
    {
        StringBuilder returnString = new StringBuilder();

        returnString.append( super.toString() + ", " );     
##        returnString.append( ${classObject.getName().toLowerCase()}data.toString() + " " );

// AIB : \#getToString( false )
#getToString( false )
// ~AIB 

        return returnString.toString();
    }

	public java.util.Collection<String> getAttributesByNameUserIdentifiesBy()
	{
		Collection<String> names = new java.util.ArrayList<String>();
				
#foreach( $attribute in $classObject.getUserIdentfiableAttributes() )
			names.add( ${attribute} != null ? ${attribute}.toString() : "" );
#end	
	return( names );
	}	
	
    public String getIdentity()
    {
		StringBuilder identity = new StringBuilder( "${className}" );
		
	#foreach( $pk in $classObject.getPrimaryKeyAttributes() )
		#set( $pkName = $pk.getName() )	
		identity.append(  "::" );
		identity.append( ${pkName} );
	#end
        return ( identity.toString() );
    }

    public String getObjectType()
    {
        return ("${classObject.getName()}");
    }	

//************************************************************************
// Object Overloads
//************************************************************************

	public boolean equals( Object object )
	{
	    Object tmpObject = null;	    
	    if (this == object) 
	        return true;
	        
		if ( object == null )
			return false;
			
	    if (!(object instanceof ${classObject.getName()})) 
	        return false;
	        
		${classObject.getName()} bo = (${classObject.getName()})object;
		
		return( get${classObject.getName()}PrimaryKey().equals( bo.get${classObject.getName()}PrimaryKey() ) ); 
	}
	
	
// attributes

// AIB : \#getAttributeDeclarations( true  )
#getAttributeDeclarations( true )
// ~AIB

}


