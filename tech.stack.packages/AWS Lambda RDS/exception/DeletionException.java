#header()
package ${aib.getRootPackageName()}.exception;

//***********************************
// Imports
//***********************************

/**
 * Used to indicate an error occurred in deleting a resource.
 * <p>
 * @author ${aib.getAuthor()}
 */
public class DeletionException extends Exception
{

//************************************************************************    
// Public Methods
//************************************************************************

    /** 
     * Base constructor.
     */
    public DeletionException()
    {
        super(); 
    }

    /** Constructor with message.
     * @param message text of the exception
     */
    public DeletionException( String message )
    {
        super( message ); 
    }

    /**
     * Constructor with a Throwable for chained exception and a message.
     * 
     * @param message
     * @param exception
     */
    public DeletionException( String message, Throwable exception )
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
