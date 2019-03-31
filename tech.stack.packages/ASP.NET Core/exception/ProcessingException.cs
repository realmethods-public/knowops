#header()

#set( $appName = $aib.getApplicationNameFormatted() )
#set( $className = $className )

/**
 * Used to indicate an error occurred in generic processing.
 * <p>
 * @author ${aib.getAuthor()}
 */

namespace ${appName}.Exceptions
{
    /// <summary>
    /// Common  exception thrown during the processing a business request
    /// </summary>
	public class ProcessingException: System.Exception
	{
        /// <summary>
        /// sole constructor
        /// </summary>
        /// <param name="message"></param>
   		public ProcessingException(string message): base(message)
   		{
   		}
	}
}

