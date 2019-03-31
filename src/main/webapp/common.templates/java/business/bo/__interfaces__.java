#header()
#set( $name = $classObject.getName() )
package ${aib.getRootPackageName(true)}.#getBOPackageName();

import java.util.*;

#set( $imports = [ "#getPrimaryKeyPackageName()", "#getBOPackageName()" ] )
#importStatements( $imports )

/**
 * Interface for business entity ${name}.
 * <p>
 * @author    $aib.getAuthor()
 *  
 */
// AIB : \#getInterfaceDecl()
#getInterfaceDecl()
// ~AIB

// AIB : \#getBOAccessorMethodDeclarations()
##getBOAccessorMethodDeclarations()
// ~AIB

}


