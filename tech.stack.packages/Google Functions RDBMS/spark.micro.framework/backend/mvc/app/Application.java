#header()
package ${aib.getRootPackageName()};

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;
import spark.template.velocity.VelocityTemplateEngine;
import spark.utils.IOUtils;

import static spark.Spark.get;
import static spark.Spark.post;

import spark.servlet.SparkApplication;

import ${aib.getRootPackageName(true)}.bo.*;
import ${aib.getRootPackageName(true)}.common.JsonTransformer;
import ${aib.getRootPackageName(true)}.exception.ProcessingException;
import ${aib.getRootPackageName(true)}.service.*;


/**
 * Main Spark Application controller
 * 
 * @author realMethods, Inc.
 *
 */

public class Application //implements SparkApplication 
{
	// example matches "GET /Account/balance/accountNo/22222"
	// request.splat()[0] is 'Account' and request.splat()[2] 'balance', etc...
    //public void init()
	public static void main(String[] args) 
    {
		Spark.staticFileLocation("/public");
#if( ${containerObject} )		
		Spark.port( ${containerObject.getPort()} );
#end		
		get("/", (request, response) -> 
    	{
    		String val = "";
    		try
    		{
    			val = (IOUtils.toString(Spark.class.getResourceAsStream("/html/test.html")));
    		}
    		catch( Exception exc )
    		{
    			exc.printStackTrace();
    		}
    		
			return val;
    	});

    	get("/stop", (request, response) -> 
    	{
    		String val = "";
    		Spark.stop();
    		try
    		{
    			val = (IOUtils.toString(Spark.class.getResourceAsStream("/html/logoff.html")));
    		}
    		catch( Exception exc )
    		{
    			exc.printStackTrace();
    		}    	
			return val;    	
    	});


    	get("/homepage", (request, response) -> 
    	{
    		String val = "";
    		
    		// to do - authenticate
    		System.out.println( "authenticate" );
    		
    		try
    		{
    			val = (IOUtils.toString(Spark.class.getResourceAsStream("/html/homepage.html")));
    		}
    		catch( Exception exc )
    		{
    			exc.printStackTrace();
    		}    	
			return val;    	
    	});

    	get("/logon", (request, response) -> 
    	{
    		String val = "";
    		
    		try
    		{
    			val = (IOUtils.toString(Spark.class.getResourceAsStream("/html/logon.html")));
    		}
    		catch( Exception exc )
    		{
    			exc.printStackTrace();
    		}    	
			return val;    	
    	});


    	get("/logoff", (request, response) -> 
    	{
    		String val = "";
    		
    		try
    		{
    			val = (IOUtils.toString(Spark.class.getResourceAsStream("/html/logoff.html")));
    		}
    		catch( Exception exc )
    		{
    			exc.printStackTrace();
    		}    	
    		Spark.stop();
			return val;    	
    	});

    	get("/html/*", (request, response) -> 
    	{
    		System.out.println( "\n\napplication-->html routing on " + request.splat()[0] );
    		
			Map<String, Object> model = new HashMap<>();
			String parentId = request.queryParams( "parentId" );
			String parentUrl = request.queryParams( "parentUrl" );
			String addUrl = request.queryParams( "addUrl" );
			String deleteUrl = request.queryParams( "deleteUrl" );
			String action = request.queryParams( "action" );
			String modelUrl = request.queryParams( "modelUrl" );
			String sourceUrl = request.queryParams( "sourceUrl" );
			String roleName = request.queryParams( "roleName" );
			String value = request.queryParams( "value" );
			String text = request.queryParams( "text" );
			
			model.put( "parentId", parentId == null ? "" : parentId );
			model.put( "parentUrl", parentUrl == null ? "" : parentUrl );
			model.put( "addUrl", addUrl == null ? "" : addUrl );
			model.put( "deleteUrl", deleteUrl == null ? "" : deleteUrl );
			model.put( "action", action == null ? "" : action );
			model.put( "modelUrl", modelUrl == null ? "" : modelUrl );
			model.put( "sourceUrl", sourceUrl == null ? "" : sourceUrl );
			model.put( "roleName", roleName == null ? "" : roleName );
			model.put( "value", value == null ? "" : value );
			model.put( "text", text == null ? "" : text );
			
    		try
    		{
    			System.out.println( "\n\nquery param model is " + model );
    			// apply the necessary query params
    			
    		}
    		catch( Exception exc )
    		{
    			exc.printStackTrace();
    		}    	
    		return new ModelAndView(model, "/html/" + request.splat()[0] );
        }, new VelocityTemplateEngine());

    	get("/test", (request, response) -> 
    	{
    		System.out.println( "*************************************" );
    		System.out.println( "* test invoked" );
    		System.out.println( "*************************************" );
    		try
    		{
    			final Application.ServiceRegistry serviceRegistry 	= new Application.ServiceRegistry();
    			return ( serviceRegistry.fetchService( "test" ).execute( "test", request, response ).toString() );
    		}
			catch( ProcessingException exc )
			{
				exc.printStackTrace();
				return( "Failed to execute test" );
			}
    	});

		get("/:pkg/:action", (request, response) -> 
    	{
    	    final Application.ServiceRegistry serviceRegistry 	= new Application.ServiceRegistry();
    		final String pkg			 						= request.params( ":pkg" );
			final String action 								= request.params( ":action" );

			try
			{
	    		final BaseRestService service	= serviceRegistry.fetchService( pkg );

	    		response.type("application/json");
	    		
	    		if ( service != null )
	    		{
    				return( service.execute( action, request, response ).toString() );
    			}
	    		else
	    		{    			
	    			String msg = "\n\n*** Application.main() - failed to located package " + pkg + " in registry";
	    			System.out.println( msg );
		    		return( msg );
	    		}    		
			}
			catch( ProcessingException exc )
			{
				exc.printStackTrace();
				return( "Failed to execute action " + action + " on package " + pkg );
			}
    	});
		
    }

    // attributes
  
    // inner class
    public static class ServiceRegistry
    {
    	protected ServiceRegistry()
    	{ registerServices(); }
    	
    	protected final BaseRestService fetchService( String pkgName )
    	throws ProcessingException
    	{
    		if ( pkgName == null )
    		{
    			throw new ProcessingException( "ServiceRegistry.fetchService(...) - null packageName provided" );
    		}
    		
    		// just in case UI is using Struts2 style invocation
    		pkgName = pkgName.replace( ".action", "" );
    			
    		return( services.get( pkgName ) );
    	}
    	
    	private void registerServices()
    	{
## add TestRestService manually    		
    		services.put( "test", new TestRestService() );    		    		
#if( ${containerObject} )
#set( $classesToUse = $containerObject.getChildrenClassObjects() )
#else
#set( $classesToUse = $aib.getClassesWithIdentity() )
#end ##if( ${containerObject} )

#foreach( $class in $classesToUse )
			services.put( "${class.getName()}", new ${class.getName()}RestService() );
#end ##foreach( $class in $aib.getClassesWithIdentity() )
    	}
    	
    	private Map<String, BaseRestService> services = new HashMap<String, BaseRestService>();
    }
}
