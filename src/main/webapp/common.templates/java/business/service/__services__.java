#header()

package ${aib.getRootPackageName(true)}.#getServicePackageName();

import java.util.*;

#set( $imports = [ "#getBOPackageName()" ] )
#importStatements( $imports )

/**
#if( $classObject.hasDocumentation() == false )
 * Encapsulates data for business entity ${classObject.getName()}.
#else
 * $classObject.getDocumentation()
#end 
 * 
 * @author $aib.getAuthor()
 */
// AIB : \#getObjectClassDecl()
#getObjectClassDecl()
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
// Business Methods
//************************************************************************
// AIB : \#getBusinessMethodImplementations( \$classObject.getName() \$classObject \$classObject.getBusinessMethods() \$classObject.getInterfaces() \$aib.getProxyTarget() true \$aib.usingSOAP() \$aib.usingCMP() )
#getBusinessMethodImplementations( $classObject.getName() $classObject $classObject.getBusinessMethods() $classObject.getInterfaces() $aib.getProxyTarget() true $aib.usingSOAP() $aib.usingCMP() )
// ~AIB
	
//************************************************************************
// Protected / Private Methods
//************************************************************************
    
//************************************************************************
// Attributes
//************************************************************************

// AIB : \#getAttributeDeclarations( true  )
#getAttributeDeclarations( true )
// ~AIB

}
