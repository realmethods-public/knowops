#header()
package ${aib.getRootPackageName()}.#getDAOPackageName();

import java.util.*;

import com.couchbase.client.java.*;
import com.couchbase.client.java.document.*;
import com.couchbase.client.java.document.json.*;
import com.couchbase.client.java.query.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import ${aib.getRootPackageName()}.#getBOPackageName().*;

/** 
 * Base class for all application data access objects.
 *
 * @author $aib.getAuthor()
 */
public abstract class BaseDAO
{
	public static void done()
	{
		if ( cluster == null )
			cluster.disconnect();
	}
	
	public static CouchbaseCluster getCluster()
	{
		if ( cluster == null )
		{
			List<String> nodes 		= new ArrayList();
			String nodeAddresses 	= "${aib.getParam( "couchbase.node addresses" )}";
			
			if ( nodeAddresses != null )
			{
				StringTokenizer tokenizer = new StringTokenizer( nodeAddresses, "," );
				while( tokenizer.hasMoreTokens() )
					nodes.add( tokenizer.nextToken() );
			}
			else
				nodes.add( DEFAULT_NODE );
			
			cluster = CouchbaseCluster.create(nodes);
		}
		return( cluster );
	}
	
	protected Bucket getBucket()
	{
		return( getBucket( "${aib.getParam("couchbase.bucket")}" ) );
	}
	
	protected Bucket getBucket( String bucketName )
	{
		return( getCluster().openBucket( bucketName ) );
	}
	
	protected Bucket getBucket( String bucketName, String password )
	{
		return( getCluster().openBucket( bucketName, password ) );
	}
	
	protected void releaseBucket( Bucket bucket )
	{
		if ( bucket != null )
			bucket.close();
	}
	
	protected String toJson( Object obj )
	{
		return GoogleJson.toJson( obj );
	}
	
    protected Object fromJson(String json, Class objectClass)
    {
        return GoogleJson.fromJson(json, objectClass);
    }
	
	protected Gson getGoogleJson()
	{
		return GoogleJson;
		
	}

	protected JsonDocument toDocument( Base bo ) 
	{
		JsonDocument toDoc = null;
		
		if ( bo != null )
		{
    		String identity		= bo.getIdentity();
    		JsonDocument doc	= getBucket().get( identity );
    		
    		if ( doc == null )
    			doc = JsonDocument.create( identity );

			String json 	= toJson( bo );
			JsonObject obj 	= JsonObject.fromJson( json );
			toDoc			= JsonDocument.from( doc, obj );
		}
		
		return( toDoc );
	}

    protected JsonDocument toDocument(String identity)
    {
        JsonDocument toDoc = null;

        if (identity != null)
        {
            JsonDocument doc = JsonDocument.create(identity);
            JsonObject obj = JsonObject.fromJson("0");
            toDoc = JsonDocument.from(doc, obj);
        }

        return (toDoc);
    }

	private static CouchbaseCluster cluster = null;
	private static String DEFAULT_NODE		= "127.0.0.1";
	protected final Gson GoogleJson			= new GsonBuilder().setDateFormat("MMMM d, yyyy").create();
}


