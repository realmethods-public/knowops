#header()
package ${aib.getRootPackageName()}.#getActionPackageName();

import java.util.logging.Logger;

import javax.servlet.http.*;

#set( $imports = [ "#getDelegatePackageName()", "#getBOPackageName()" ] )
#importStatements( $imports )

/**
 * General Logoff Action.
 
 * @author $aib.getAuthor()
 */
public class LogoffAction extends BaseStrutsAction
{    
    public String execute()
    throws Exception
    {
        try
        {
	        // terminate the session
            HttpSession currentHttpSession = getServletRequest().getSession(false);
            
            if (currentHttpSession != null)
            {
                try
                {
                    currentHttpSession.invalidate();   
                }
                catch (IllegalStateException exc)
                {
                	LOGGER.severe( "LogoffAction.execute() - failed to invalidate session - " + exc.getMessage() );
                    // do nothing because it is thrown if the session is 
                    // already invalidated
                }
            }            
        }
        catch( Throwable generalExc )
        {
        	LOGGER.severe( "LogoffAction.execute() - general exception- " + generalExc.getMessage() );
            return "LOGIN_FAILURE";
        }
        
        return SUCCESS;
    }
    
    // attributes
    private static final Logger LOGGER = Logger.getLogger(LogoffAction.class.getName());
}


