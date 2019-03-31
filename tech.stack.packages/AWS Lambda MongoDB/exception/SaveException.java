#header()
package ${aib.getRootPackageName()}.exception;

//***********************************
// Imports
//***********************************

/**
 * Used to indicate an error occurred in saving a resource.
 * <p>
 * @author ${aib.getAuthor()}
 */
public class SaveException extends Exception
{

//************************************************************************    
// Public Methods
//************************************************************************

    /** 
     * Base constructor.
     */
    public SaveException()
    {
        super(); 
    }

    /** Constructor with message.
     * @param message text of the exception
     */
    public SaveException( String message )
    {
        super( message ); 
    }

    /**
     * Constructor with a Throwable for chained exception and a message.
     * 
     * @param message
     * @param exception
     */
    public SaveException( String message, Throwable exception )
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
