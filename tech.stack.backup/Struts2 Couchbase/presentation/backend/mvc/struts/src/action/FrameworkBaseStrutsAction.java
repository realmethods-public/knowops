#header()
package ${aib.getRootPackageName()}.#getActionPackageName();

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.ActionSupport;

import ${aib.getRootPackageName()}.exception.AuthenticationException;
import ${aib.getRootPackageName()}.exception.ProcessingException;

import org.apache.struts2.interceptor.ServletRequestAware;

/**
 * Base class for Struts Action classes within the Framework.
 * <p>
 * @author    realMethods, Inc.
 */
public abstract class FrameworkBaseStrutsAction //v5.0 Struts 2 no longer extends Action
extends ActionSupport
implements Preparable, ServletRequestAware
{
	
//************************************************************************    
// Public Methods
//************************************************************************

// ServletRequestAware implementation
	
	public void setServletRequest( HttpServletRequest request )
	{
		servletRequest = request;
	}
	
// helper methods

	protected HttpServletRequest getServletRequest()
	{
		return servletRequest;
	}
	
    
    /**
     * Helper method to return a parameter value from the HttpServletRequest.
     * <p>
     * @param	id				name of the parameter
     * @return	the parameter
     */
    protected String getServletRequestParameter( String id )
    {
        return( getServletRequest().getParameter( id ) );
    }

    
    /**
     * Returns a Collection of String objects that represent all the parameter
     * values based on the provided object id parameter prefix.  It continue
     * to query the parameter set for the first (1), then second (2), and 
     * so on until none in the sequence is located.
     * <p>
     * @param 	objectIDParamPrefix		prefix used to denote the key parameters in the HttpServletRequest
     * @return 	the key fields
     */
    protected Collection getKeysFieldsFromServletRequest( String objectIDParamPrefix )
    {
        Collection v    = new ArrayList();
        String objid  	= null;
        int index     	= 1;
        
        while( (objid = getServletRequestParameter( objectIDParamPrefix + String.valueOf( index ) )) != null )
        {
            v.add( objid );
            index++;
        }
        
		LOGGER.info( "Key Fields for " + objectIDParamPrefix + " are: " + v.toString() );
        
        return( v );
    }
    
// security related helpers
	
    /**
     * Re-uses the proof of concept security adapater to handle 
     * user authentication.
     *
     * @param		userId		user identifier
     * @param		password	user's password
     * @return 		boolean		true if authennticated
     * @exception	AuthenticationException		
     */
    protected boolean authenticateUser( String userId, 
										String password )
    throws AuthenticationException
    {
    	boolean authenticated = true;
    	
    	try
		{
    		// implement user authentication here
        }
        catch( Throwable exc )
        {
        	String errMsg = "FrameworkBaseStrutsAction.authenticateUser(userid, password) - failed to authenticate user " + userId + " - " + exc.getMessage();
        	authenticated = false;
        	LOGGER.warning( errMsg );
            throw new AuthenticationException( errMsg );
        }
        
        return( authenticated );
    }
    
    /**
     * Checks with the FrameworkSecurityManager of the bound HttpSession to see
     * if the user is authorized for the provided role
     * <p> 
     * @param	userId		user identifier
     * @param 	roleName	user's password
     * @return 	boolean		true if authorized
     */
    public boolean isUserAuthorized( String userId, String roleName ) 
	{
    	boolean authorized = true;
    	// place authorization check logic here
        return( authorized );
    	
	}
    
//************************************************************************    
// Attributes
//************************************************************************

		
    /**
     * Default object identifier prefix.
     */
    public final static String DEFAULT_OBJECT_ID_PREFIX = "ObjID";
	private final String REDIRECT_TO 					= "redirectTo";
	public final static String KEY_DELIM 				= "!";
	protected HttpServletRequest servletRequest			= null;
	
	private static final long serialVersionUID 			= -5485382508029951644L;
	private static final Logger LOGGER 	= Logger.getLogger(FrameworkBaseStrutsAction.class.getName());
	
}
