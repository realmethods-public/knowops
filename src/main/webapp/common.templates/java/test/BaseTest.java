#header()
package ${aib.getRootPackageName()}.#getTestPackageName();

import java.util.logging.*;

/**
 * Base class for application Test classes.
 *
 * @author    $aib.getAuthor()
 */
public class BaseTest
{
	/**
	 * hidden
	 */
	protected BaseTest() {
	}
	
	public static void runTheTest( Handler logHandler ) 
    {
#if( ${containerObject} )
#set( $classesToUse = $containerObject.getChildrenClassObjects() )
#else
#set( $classesToUse = $aib.getClassesWithIdentity() )
#end ##if( ${containerObject} )
        try {
#foreach( $class in $classesToUse )
		    new ${class.getName()}Test().setHandler(logHandler).testCRUD();
#end
        } catch( Throwable exc ) {
        	exc.printStackTrace();
        }
    }
	
}
