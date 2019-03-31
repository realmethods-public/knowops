#header()
package ${aib.getRootPackageName()}.#getDelegatePackageName();

import com.amazonaws.services.lambda.runtime.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.*;


/**
 * Base class for AWS Lambda business delegates.
 * <p>
 * @author ${aib.getAuthor()}
 */
public class BaseAWSLambdaDelegate
{
    protected BaseAWSLambdaDelegate()
    {
    }

    protected static String getContextDetails( Context context ) {
    	StringBuilder details = new StringBuilder();
    	
    	if ( context != null )
    	{
	    	details.append("Function name: " + context.getFunctionName());
	        details.append("\nMax mem allocated: " + context.getMemoryLimitInMB());
	        details.append("\nTime remaining in milliseconds: " + context.getRemainingTimeInMillis());
	        details.append("\nCloudWatch log stream name: " + context.getLogStreamName());
	        details.append("\nCloudWatch log group name: " + context.getLogGroupName()); 
	        details.append("\n");
    	}
    	return( details.toString() );
    }
    
    protected static String toJson(Object obj) {
        return getGoogleJson().toJson(obj);
    }

    protected static Object fromJson(String json, Class objectClass) {
        return getGoogleJson().fromJson(json, objectClass);
    }

    protected static Gson getGoogleJson() {
        return GoogleJson;
    }

    static String call(String packageName, String actionName, Object arg)
        throws IOException {
    	String urlStr = DELEGATE_DAO_URL 
    						+ "/" + packageName + "/" + actionName;
    	
    	if ( arg != null )
    		urlStr = urlStr + "?" + toJson( arg ); 
    		
        URL url = new URL( urlStr );
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        if (conn.getResponseCode() != 200) {
            throw new IOException(conn.getResponseMessage());
        }

        BufferedReader rd = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }

        rd.close();
        conn.disconnect();

        return sb.toString();
    }
    
// attributes
    private static Context context 					= null;
    final private static Gson GoogleJson 			= new Gson();
    protected final static String DELEGATE_DAO_URL 	= java.lang.System.getenv("delegateDAOHost") + ":" + java.lang.System.getenv("delegateDAOPort");
  
}

