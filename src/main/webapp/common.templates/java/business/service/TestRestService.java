#header()
package ${aib.getRootPackageName()}.service;

import java.io.*;
import java.util.logging.*;

import ${aib.getRootPackageName(true)}.exception.ProcessingException;

/** 
 * Test class for all Rest Controller delegation functions
 *
 * @author $aib.getAuthor()
 */
public class TestRestService extends BaseRestService
{

	protected Object handleExec( String action, spark.Response response, spark.Request request ) 
			throws ProcessingException
    {
		ByteArrayOutputStream os 	= new java.io.ByteArrayOutputStream();
		StreamHandler handler 		= new StreamHandler( os, new LogTestFormatter() );
		
		${aib.getRootPackageName()}.#getTestPackageName().BaseTest.runTheTest(handler);
		
		return( os.toString() );
    }
	
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

    protected String getSubclassName()
    { return( "TesRestService" ); }

	// attributes
	private StreamHandler handler = null;
    private static final Logger LOGGER = Logger.getLogger(TestRestService.class.getName());	
}



