#header()
package ${aib.getRootPackageName()}.#getActionPackageName();

import java.util.logging.Logger;

/**
 * General Logon Action.  
 
 * @author $aib.getAuthor()
 */
public class LogonAction extends BaseStrutsAction
{
    
//************************************************************************    
// Public Methods
//************************************************************************
	public String getUserID()
	{
	    return( userID );
	}
	
	public void setUserID( String id )
	{
	    userID = id;
	}
	
	public String getPassword()
	{
	    return password;
	}
	
	public void setPassword( String pw )
	{
	    password = pw;
	}
	
   /**
    * Struts Action processing method
    */     
    public String execute()
    throws Exception
    {
        try
        {
        	LOGGER.info( "logging in starting..." );
            
            boolean authenticated = false;
            
			if ( !authenticated )
				authenticated = authenticateUser( userID, password );

			if ( authenticated == true )
			{
				LOGGER.info( "logging in success" );
	            return "success";
			}
	        else
	        {
	        	LOGGER.info( "logging in failed" );
	            return "LOGIN_FAILURE";
	        }
        }
        catch( Throwable generalExc )
        {
        	LOGGER.severe( "Authentication failed: userid-" + userID + ", " + "password-" + password );
            return "LOGIN_FAILURE";
        }                
    }

    // attributes
    
    protected String userID = null;
    protected String password = null;
    private static final Logger LOGGER = Logger.getLogger(LogonAction.class.getName());

}




