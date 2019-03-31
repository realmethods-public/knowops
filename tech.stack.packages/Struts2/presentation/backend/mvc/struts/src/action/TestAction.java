#header()
package ${aib.getRootPackageName()}.#getActionPackageName();

import java.io.*;
import java.util.logging.*;

/** 
 * Test class for all delegation functions
 *
 * @author $aib.getAuthor()
 */
public class TestAction extends BaseStrutsAction
{
	/**
     * Handles calling the underlying JUnit testing facility
     */
    public String execute()
    {
		StreamHandler handler 		= new StreamHandler( os, new LogTestFormatter() );
		
		${aib.getRootPackageName()}.#getTestPackageName().BaseTest.runTheTest(handler);
		
		return( SUCCESS);
    }
	
    public String getResults()
    {
    	return( os.toString() );
    }
    
	// attributes
	private ByteArrayOutputStream os 	= new java.io.ByteArrayOutputStream();
    private StreamHandler handler 		= null;
	
	class LogTestFormatter extends Formatter 
	{
	    public String format(LogRecord record) 
	    {
	        StringBuilder builder = new StringBuilder(1000);
	        
	        if ( record.getLevel() == Level.WARNING )
	        	builder.append( "<span><style='color:red'>" );
	        else if ( record.getLevel() == Level.SEVERE )
	        	builder.append( "<span><style='color:red;font-weight:bolder'>" );
	        else
	        	builder.append( "<span><style='color:black'>" );
	        
	        builder.append(formatMessage(record));

	        builder.append( "</style></span>" );

	        builder.append("<br>");
	        return builder.toString();
	    }
	}
	
}



