#header()
package ${aib.getRootPackageName()}.exception;

//***********************************
// Imports
//***********************************

/**
 * Used to indicate an error occurred in locating a resource.
 * <p>
 * @author ${aib.getAuthor()}
 */
public class NotFoundException extends Exception
{

//************************************************************************    
// Public Methods
//************************************************************************

    /** 
     * Base constructor.
     */
    public NotFoundException()
    {
        super(); 
    }

    /** Constructor with message.
     * @param message text of the exception
     */
    public NotFoundException( String message )
    {
        super( message ); 
    }

    /**
     * Constructor with a Throwable for chained exception and a message.
     * 
     * @param message
     * @param exception
     */
    public NotFoundException( String message, Throwable exception )
    {
        super( message ); 
    }

//************************************************************************    
// Private / Protected Methods
//************************************************************************

//************************************************************************    
// Attributes
//************************************************************************
}
