#header()
package ${aib.getRootPackageName()}.exception;

//***********************************
// Imports
//***********************************

/**
 * Used to indicate an error occurred in creating a resource.
 * <p>
 * @author ${aib.getAuthor()}
 */
public class CreationException extends Exception
{

//************************************************************************    
// Public Methods
//************************************************************************

    /** 
     * Base constructor.
     */
    public CreationException()
    {
        super(); 
    }

    /** Constructor with message.
     * @param message text of the exception
     */
    public CreationException( String message )
    {
        super( message ); 
    }

    /**
     * Constructor with a Throwable for chained exception and a message.
     * 
     * @param message
     * @param exception
     */
    public CreationException( String message, Throwable exception )
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
