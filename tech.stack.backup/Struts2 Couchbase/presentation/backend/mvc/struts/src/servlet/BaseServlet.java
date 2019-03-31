#header()
package ${aib.getRootPackageName()}.#getServletPackageName();

import ${aib.getRootPackageName()}.#getDAOPackageName().*;

import javax.servlet.*;
import javax.servlet.http.*;

/**
 * Base class for application servlet(s)
 * 
 * @author $aib.getAuthor()
 */

public class BaseServlet extends HttpServlet
{
	public void init(ServletConfig config) throws ServletException 
	{
	    super.init(config);
	    
	    // warm up the Couchbase NoSQL system for faster response on initial request
	    BaseDAO.getCluster();
	}
	
	public void destroy()
	{
		BaseDAO.done();
		super.destroy();
	}
}


