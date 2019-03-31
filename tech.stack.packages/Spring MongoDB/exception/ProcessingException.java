#header()
package ${aib.getRootPackageName()}.exception;

//***********************************
// Imports
//***********************************

/**
 * Used to indicate an error occured in generic processing.
 * <p>
 * @author ${aib.getAuthor()}
 */
public class ProcessingException extends Exception
{

//************************************************************************    
// Public Methods
//************************************************************************

    /** 
     * Base constructor.
     */
    public ProcessingException()
    {
        super(); 
    }

    /** Constructor with message.
     * @param message text of the exception
     */
    public ProcessingException( String message )
    {
        super( message ); 
    }

    /**
     * Constructor with a Throwabe for chained exception and a message.
     * 
     * @param message
     * @param exception
     */
    public ProcessingException( String message, Throwable exception )
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




