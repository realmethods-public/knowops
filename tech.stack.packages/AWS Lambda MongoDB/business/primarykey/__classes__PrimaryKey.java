#header()
package ${aib.getRootPackageName(true)}.#getPrimaryKeyPackageName();

import java.util.*;

/**
 * ${classObject.getName()} PrimaryKey class.
 * 
 * @author    $aib.getAuthor()
 */
// AIB : \#getPrimaryKeyClassDecl() 
#getPrimaryKeyClassDecl()
// ~AIB

//************************************************************************
// Public Methods
//************************************************************************

    /** 
     * default constructor - should be normally used for dynamic instantiation
     */
    public ${classObject.getName()}PrimaryKey() 
    {
    }    
    
    /** 
     * Constructor with all arguments relating to the primary key
     * 	        
// AIB : \#getAllAttributeJavaComments( true true )
#getAllAttributeJavaComments( true true )
// ~AIB     
     * @exception IllegalArgumentException
     */
    public ${classObject.getName()}PrimaryKey(    
// AIB : \#getAllPrimaryKeyArguments( \$classObject false )
#getAllPrimaryKeyArguments( $classObject false )
// ~AIB
                         ) 
    throws IllegalArgumentException
    {
        super();
// AIB : \#getKeyFieldAssignments()
#getKeyFieldAssignments()
// ~AIB 
    }   

    
//************************************************************************
// Access Methods
//************************************************************************

// AIB : \#getKeyFieldAccessMethods()
#getKeyFieldAccessMethods()
// ~AIB 	         	     

    /**
     * Retrieves the value(s) as a single List
     * @return List
     */
    public List keys()
    {
		// assign the attributes to the Collection back to the parent
        ArrayList keys = new ArrayList();
        
#foreach( $primaryKey in $classObject.getAllPrimaryKeysInHierarchy() )
		keys.add( $primaryKey.getAsArgument() );
#end## foreach $primaryKey in $classObject.getAllPrimaryKeysInHierarchy()

        return( keys );
    }

#if ( $classObject.hasParent() == false )
	public Object getFirstKey()
	{
		return( ${classObject.getAllPrimaryKeysInHierarchy().get(0)} );
	}
#end
#*
	/**
	 * Retrieves the values as a Collection of com.realmethods.foundational.common.parameters.Parameter parameters, 
	 * either as input or output parameters
	 * 
	 * @param  	asInput	indicates if the Parameters are input or output
	 * @return 	Collection of Parameters 
	 * @see		com.realmethods.foundational.common.parameter.Parameter
	 */
	public List getValuesAsParameters( boolean asInput )
	{
		ArrayList args = new ArrayList();

#foreach( $primaryKey in $classObject.getAllPrimaryKeysInHierarchy() )
#set( $type = $primaryKey.getRootType() )
		args.add( new ${type}Parameter( ${primaryKey.getAsArgument()}, asInput ) );
#end
						
		return args;
	}
*#
 
//************************************************************************
// Protected / Private Methods
//************************************************************************

    
//************************************************************************
// Attributes
//************************************************************************

 	

// DO NOT ASSIGN VALUES DIRECTLY TO THE FOLLOWING ATTRIBUTES.  SET THE VALUES
// WITHIN THE ${classObject.getName()} class.

// AIB : \#getKeyFieldDeclarations()
#getKeyFieldDeclarations()
// ~AIB 	        

}
