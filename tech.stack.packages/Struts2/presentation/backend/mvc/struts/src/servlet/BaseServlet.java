#header()
package ${aib.getRootPackageName()}.#getServletPackageName();

import javax.servlet.ServletConfig; 
import javax.servlet.http.*;

import ${aib.getRootPackageName()}.persistent.FrameworkPersistenceHelper;

/**
 * Base class for application servlet(s)
 * 
 * @author $aib.getAuthor()
 */
public class BaseServlet extends HttpServlet
{
	public void init(ServletConfig config) throws javax.servlet.ServletException
	{
		super.init(config);
		// give a Hibernate a kick-start during servlet initialization instead of
		// hitting it cold
		FrameworkPersistenceHelper.self();
	}
}


