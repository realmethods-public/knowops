#header()
package ${aib.getRootPackageName()}.exception;

//***********************************
// Imports
//***********************************

/**
 * Used to indicate an error occurred in using DynamoDB as a resource.
 * <p>
 * @author ${aib.getAuthor()}
 */
public class DynamoDBException extends Exception
{

//************************************************************************    
// Public Methods
//************************************************************************

    /** 
     * Base constructor.
     */
    public DynamoDBException()
    {
        super(); 
    }

    /** Constructor with message.
     * @param message text of the exception
     */
    public DynamoDBException( String message )
    {
        super( message ); 
    }

    /**
     * Constructor with a Throwable for chained exception and a message.
     * 
     * @param message
     * @param exception
     */
    public DynamoDBException( String message, Throwable exception )
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
