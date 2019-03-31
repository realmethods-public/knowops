#header()
package ${aib.getRootPackageName()}.service;

import java.io.IOException;
import java.io.StringWriter;

import java.text.SimpleDateFormat;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import ${aib.getRootPackageName(true)}.exception.ProcessingException;

/** 
 * Base class of all application service classes.
 *
 * @author $aib.getAuthor()
 */
public abstract class BaseRestService
{
	
	protected String objectToJson(Object object) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
            StringWriter writer = new StringWriter();
            mapper.writeValue(writer, object);
            return writer.toString();
        } catch (IOException ioExc){
            throw new RuntimeException("BaseRestService.objectToJson() - failed to translate object to Json - " + ioExc.getMessage() );
        }
    }
	
	public String execute( String action, spark.Request request, spark.Response response )
			throws ProcessingException
	{
		return objectToJson( handleExec( action, response, request ) );
	}
	
    protected Long[] getChildIds()
    {
    	Long[] ids = null;
    	
    	String [] childrenIds = request.queryParamsValues( "childIds" );
    	
    	if( childrenIds != null )
    	{
    		ids = new Long[childrenIds.length];
    		
    		for( int index = 0; index < childrenIds.length; index++  )
    			ids[index] = new Long( childrenIds[index] );
    	}
    	
    	LOGGER.info( "BaseRestService.getChildIds() - " + ids + "\n\n");
    	
    	return( ids );
    }

    protected Long parseId( String rootId )
    {    	
    	String id = null;
    	
    	if ( rootId != null )
    	{
    		id = request.queryParams( rootId );
    		
    		if ( id == null )
    			id = request.queryParams( getSubclassName() + "." + rootId );
    	}
    	
    	LOGGER.info( "BaseRestService.parseId() working on " + rootId + " and found " + id + "\n\n");
    	
    	if ( id != null )
    		return new Long( id );
    	else
    		return null;
    }
    
    /**
     * set the response.status to 400
     */
    protected void signalBadRequest()
    {
    	//if ( response != null )
    		//response.status(HTTP_BAD_REQUEST);
    }
    
    protected abstract String getSubclassName();
	protected abstract Object handleExec( String action, spark.Response response, spark.Request request ) 
			throws ProcessingException;

	// attributes
	private static final int HTTP_BAD_REQUEST 			= 400;
	protected spark.Response response					= null; 
	protected spark.Request request					 	= null;
    private static final Logger LOGGER 	= Logger.getLogger(BaseRestService.class.getName());
}



