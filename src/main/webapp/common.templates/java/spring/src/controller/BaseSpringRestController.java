#header()
package ${aib.getRootPackageName()}.#getRestControllerPackageName();

import java.util.concurrent.atomic.AtomicLong;

/** 
 * Base class of all application Spring Controller classes.
 *
 * @author $aib.getAuthor()
 */
public class BaseSpringRestController
{
	protected AtomicLong counter()
	{ return counter; }
	
	protected void logMessage( String msg )
	{
		System.out.println( msg );
	}
	
	private final AtomicLong counter = new AtomicLong();
}



