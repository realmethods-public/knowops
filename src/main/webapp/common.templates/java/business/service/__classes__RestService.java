#header()
#set( $className = ${classObject.getName()} )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter( ${className} )} )
#set( $pk = ${classObject.getFirstPrimaryKey()} )		
#set( $pkExpression = "${pk.getName()}" ) 
package ${aib.getRootPackageName(true)}.service;

import java.text.SimpleDateFormat;
import java.util.logging.Logger;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import static spark.Spark.get;
import static spark.Spark.post;

import spark.Request;
import spark.Response;
import spark.Route;

import ${aib.getRootPackageName(true)}.bo.Base;
import ${aib.getRootPackageName(true)}.bo.*;
import ${aib.getRootPackageName(true)}.common.JsonTransformer;
import ${aib.getRootPackageName(true)}.delegate.*;
import ${aib.getRootPackageName(true)}.primarykey.*;
import ${aib.getRootPackageName(true)}.exception.ProcessingException;

/** 
 * Implements Struts action processing for business entity ${className}.
 *
 * @author $aib.getAuthor()
 */
#set( $parentService = "BaseRestService" )

public class ${className}RestService extends $parentService
{

	public ${className}RestService()
	{}
	
    /**
     * Handles saving a ${className} BO.  if not key provided, calls create, otherwise calls save
     * @exception	ProcessingException
     */
    protected ${className} save()
    	throws ProcessingException
    {
		// doing it here helps
		get${className}();

		LOGGER.info( "${className}.save() on - " + ${lowercaseClassName} );
		
        if ( hasPrimaryKey() )
        {
            return (update());
        }
        else
        {
            return (create());
        }
    }

    /**
     * Returns true if the $lowercaseClassName is non-null and has it's primary key field(s) set
     * @return		boolean
     */
    protected boolean hasPrimaryKey()
    {
    	boolean hasPK = false;

		if ( ${lowercaseClassName} != null && ${lowercaseClassName}.get${className}PrimaryKey().hasBeenAssigned() == true )
		   hasPK = true;
		
		return( hasPK );
    }

    /**
     * Handles updating a ${className} BO
     * @return		${className}
     * @exception	ProcessingException
     */    
    protected ${className} update()
    	throws ProcessingException
    {
    	// store provided data
        $className tmp = ${lowercaseClassName};

        // load actual data from storage
        loadHelper( ${lowercaseClassName}.get${className}PrimaryKey() );
    	
    	// copy provided data into actual data
    	${lowercaseClassName}.copyShallow( tmp );
    	
        try
        {                        	        
			// create the ${className}Business Delegate            
			${className}BusinessDelegate delegate = ${className}BusinessDelegate.get${className}Instance();
            this.${lowercaseClassName} = delegate.save${className}( ${lowercaseClassName} );
            
            if ( this.${lowercaseClassName} != null )
            	LOGGER.info( "${className}RestService:update() - successfully updated ${className} - " + ${lowercaseClassName}.toString() );
        }
        catch( Throwable exc )
        {
        	signalBadRequest();
        	
        	String errMsg = "${className}RestService:update() - successfully update ${className} - " + exc.getMessage();
        	LOGGER.severe( errMsg );
        	throw new ProcessingException( errMsg );
        }
        
        return this.${lowercaseClassName};
        
    }

    /**
     * Handles creating a ${className} BO
     * @return		${className}
     */
    protected ${className} create()
    	throws ProcessingException
    {
        try
        {       
        	${lowercaseClassName} 		= get${className}();
			this.${lowercaseClassName} 	= ${className}BusinessDelegate.get${className}Instance().create${className}( ${lowercaseClassName} );
        }
        catch( Throwable exc )
        {
        	signalBadRequest();
        	
        	String errMsg = "${className}RestService:create() - exception ${className} - " + exc.getMessage();
        	LOGGER.severe( errMsg );
        	throw new ProcessingException( errMsg );
        }
        
        return this.${lowercaseClassName};
    }


    
    /**
     * Handles deleting a ${className} BO
     * @exception	ProcessingException
     */
    protected void delete() 
    	throws ProcessingException
    {                
        try
        {
        	${className}BusinessDelegate delegate = ${className}BusinessDelegate.get${className}Instance();

        	Long[] childIds = getChildIds();
        	
        	if ( childIds == null || childIds.length == 0 )
        	{
        		Long ${lowercaseClassName}Id  = parseId( "${pkExpression}" );
        		delegate.delete( new ${className}PrimaryKey( ${lowercaseClassName}Id  ) );
        		LOGGER.info( "${className}RestService:delete() - successfully deleted ${className} with key " + ${lowercaseClassName}.get${className}PrimaryKey().valuesAsCollection() );
        	}
        	else
        	{
        		for ( Long id : childIds )
        		{
        			try
        			{
        				delegate.delete( new ${className}PrimaryKey( id ) );
        			}
	                catch( Throwable exc )
	                {
	                	signalBadRequest();

	                	String errMsg = "${className}RestService:delete() - " + exc.getMessage();
	                	LOGGER.severe( errMsg );
	                	throw new ProcessingException( errMsg );
	                }
        		}
        	}
        }
        catch( Throwable exc )
        {
        	signalBadRequest();
        	String errMsg = "${className}RestService:delete() - " + exc.getMessage();
        	LOGGER.severe( errMsg );
        	throw new ProcessingException( errMsg );
        }
	}        
	
    /**
     * Handles loading a ${className} BO
     * @param		Long ${lowercaseClassName}Id
     * @exception	ProcessingException
     * @return		${className}
     */    
    protected ${className} load() 
    	throws ProcessingException
    {    	
        ${className}PrimaryKey pk 	= null;
		Long ${lowercaseClassName}Id  = parseId( "${pkExpression}" );

    	try
        {
    		LOGGER.info( "${className}.load pk is " + ${lowercaseClassName}Id );
    		
        	if ( ${lowercaseClassName}Id != null )
        	{
        		pk = new ${className}PrimaryKey( ${lowercaseClassName}Id );

        		loadHelper( pk );

        		// load the contained instance of ${className}
	            this.${lowercaseClassName} = ${className}BusinessDelegate.get${className}Instance().get${className}( pk );
	            
	            LOGGER.info( "${className}RestService:load() - successfully loaded - " + this.${lowercaseClassName}.toString() );             
			}
			else
			{
	        	signalBadRequest();

				String errMsg = "${className}RestService:load() - unable to locate the primary key as an attribute or a selection for - " + ${lowercaseClassName}.toString();				
				LOGGER.severe( errMsg );
	            throw new ProcessingException( errMsg );
			}	            
        }
        catch( Throwable exc )
        {
        	signalBadRequest();

        	String errMsg = "${className}RestService:load() - failed to load ${className} using Id " + ${lowercaseClassName}Id + ", " + exc.getMessage();				
			LOGGER.severe( errMsg );
            throw new ProcessingException( errMsg );
        }

        return ${lowercaseClassName};

    }

    /**
     * Handles loading all ${className} business objects
     * @return		List<${className}>
     * @exception	ProcessingException
     */
    protected List<${className}> loadAll()
    	throws ProcessingException
    {                
        List<$className> ${lowercaseClassName}List = null;
        
    	try
        {                        
            // load the ${className}
            ${lowercaseClassName}List = ${className}BusinessDelegate.get${className}Instance().getAll${className}();
            
            if ( ${lowercaseClassName}List != null )
            	LOGGER.info(  "${className}RestService:loadAll${className}() - successfully loaded all ${className}s" );
        }
        catch( Throwable exc )
        {
        	signalBadRequest();

        	String errMsg = "${className}RestService:loadAll() - failed to load all ${className}s - " + exc.getMessage();				
			LOGGER.severe( errMsg );
            throw new ProcessingException( errMsg );            
        }

        return ${lowercaseClassName}List;
                            
    }

#*
// findAllBy methods
#foreach( $method in $classObject.getFindAllByMethods() )
	#if ( ${method.hasArguments()} )	## should only be one argument
    	#set( $argType = ${method.getArguments().getArgs().get(0).getType()} )
    	#set( $argName = ${method.getArguments().getArgs().get(0).getName()} )
    /**
     * finder method to ${method.getName()}
     * @param 		$argType $argName
     * @return		List<${className}>
     * @exception	ProcessingException
     * 
     */     
	protected List<${className}> ${method.getName()}()
		throws ProcessingException
	{		
		List<${className}> ${lowercaseClassName}List = null;
        try
        {  
        	$argType argName = new $argType( request.getParams( "${argName}" ) );
            // load the ${className}
            ${lowercaseClassName}List = ${className}BusinessDelegate.get${className}Instance().${method.getName()}(${arg});
            
            if ( ${lowercaseClassName}List != null )
            	LOGGER.info(  "${className}RestService:${method.getName()}() - successfully loaded" );
        }
        catch( Throwable exc )
        {
        	signalBadRequest();

        	String errMsg = "${className}RestService:loadAll() - failed to load all ${className}s - " + exc.getMessage()				
			LOGGER.severe( errMsg );
            throw new ProcessingException( errMsg );            
        }
        return {lowercaseClassName}List;
	}
	#end ##if ( ${method.hasArguments()} )	## should only be one argument
#end ##foreach( $method in $classObject.getFindAllByMethods() )
*#

#set( $includeComposites = false )
#foreach( $singleAssociation in $classObject.getSingleAssociations( ${includeComposites} ) )
#set( $roleName = $singleAssociation.getRoleName() )
#set( $childName = $singleAssociation.getType() )
#set( $parentName  = $classObject.getName() )
    /**
     * save ${roleName} on ${className}
     * @param		${className} $lowercaseClassName
     * @return		${className}
     * @exception	ProcessingException
     */     
	protected ${className} save${roleName}()
		throws ProcessingException
	{
		Long ${lowercaseClassName}Id 	= parseId( "${pkExpression}" );
		Long childId 					= parseId( "childId" );
		
		if ( loadHelper( new ${className}PrimaryKey( ${lowercaseClassName}Id ) ) == null )
			return( null );
		
		if ( childId != null )
		{
			${childName}BusinessDelegate childDelegate 	= ${childName}BusinessDelegate.get${childName}Instance();
			${className}BusinessDelegate parentDelegate = ${className}BusinessDelegate.get${className}Instance();			
			${childName} child 							= null;

			try
			{
				child = childDelegate.get${childName}( new ${childName}PrimaryKey( childId ) );
			}
            catch( Throwable exc )
            {
            	signalBadRequest();

            	String errMsg = "${className}RestService:save${roleName}() failed to get ${childName} using id " + childId + " - " + exc.getMessage();
            	LOGGER.severe( errMsg);
            	throw new ProcessingException( errMsg );
            }
	
			${lowercaseClassName}.set${roleName}( child );
		
			try
			{
				// save it
				parentDelegate.save${className}( ${lowercaseClassName} );
			}
			catch( Exception exc )
			{
	        	signalBadRequest();

				String errMsg = "${className}RestService:save${roleName}() failed saving parent ${className} - " + exc.getMessage();
				LOGGER.severe( errMsg );
				throw new ProcessingException( errMsg );
			}
		}
		
		return ${lowercaseClassName};
	}

    /**
     * delete ${roleName} on ${className}
     * @return		${className}
     * @exception	ProcessingException
     */     
	protected ${className} delete${roleName}()
		throws ProcessingException
	{
		Long ${lowercaseClassName}Id = parseId( "${pkExpression}" );
		
 		if ( loadHelper( new ${className}PrimaryKey( ${lowercaseClassName}Id ) ) == null )
			return( null );

		if ( ${lowercaseClassName}.get${roleName}() != null )
		{
			${childName}PrimaryKey pk = ${lowercaseClassName}.get${roleName}().get${childName}PrimaryKey();
			
			// null out the parent first so there's no constraint during deletion
			${lowercaseClassName}.set${roleName}( null );
			try
			{
				${className}BusinessDelegate parentDelegate = ${className}BusinessDelegate.get${className}Instance();

				// save it
				${lowercaseClassName} = parentDelegate.save${className}( ${lowercaseClassName} );
			}
			catch( Exception exc )
			{
	        	signalBadRequest();

				String errMsg = "${className}RestService:delete${roleName}() failed to save ${className} - " + exc.getMessage();
				LOGGER.severe( errMsg );
				throw new ProcessingException( errMsg );
			}
			
			try
			{
				// safe to delete the child			
				${childName}BusinessDelegate childDelegate = ${childName}BusinessDelegate.get${childName}Instance();
				childDelegate.delete( pk );
			}
			catch( Exception exc )
			{
	        	signalBadRequest();

				String errMsg = "${className}RestService:delete${roleName}() failed  - " + exc.getMessage();
				LOGGER.severe( errMsg );
				throw new ProcessingException( errMsg );
			}
		}
		
		return ${lowercaseClassName};
	}
	
#end

#foreach( $multiAssociation in $classObject.getMultipleAssociations() )
#set( $roleName = $multiAssociation.getRoleName() )
#set( $childName = $multiAssociation.getType() )
#set( $parentName  = $classObject.getName() )
    /**
     * save ${roleName} on ${className}
     * @param		Long ${lowercaseClassName}Id
     * @param		Long childId
     * @param		String[] childIds
     * @return		${className}
     * @exception	ProcessingException
     */     
	protected ${className} save${roleName}()
		throws ProcessingException
	{
		Long ${lowercaseClassName}Id = parseId( "${pkExpression}" );
		Long childId = parseId( "childId" );
		
		if ( loadHelper( new ${className}PrimaryKey( ${lowercaseClassName}Id ) ) == null )
			throw new ProcessingException( "${className}.save${roleName}() - failed to load parent using Id " + ${lowercaseClassName}Id );
		 
		${childName}PrimaryKey pk 					= null;
		${childName} child							= null;
		List<${childName}> childList				= null;
		${childName}BusinessDelegate childDelegate 	= ${childName}BusinessDelegate.get${childName}Instance();
		${className}BusinessDelegate parentDelegate = ${className}BusinessDelegate.get${className}Instance();		
		
		Long[] childIds = getChildIds();
		
		if ( childId != null || childIds.length == 0 )// creating or saving one
		{
			pk = new ${childName}PrimaryKey( childId );
			
			try
			{
				// find the ${childName}
				child = childDelegate.get${childName}( pk );
				LOGGER.info( "LeagueRestService:save${className}() - found ${childName}" );
			}
			catch( Exception exc )
			{
	        	signalBadRequest();

				String errMsg = "${className}RestService:save${roleName}() failed get child ${childName} using id " + childId  + "- " + exc.getMessage();
				LOGGER.severe( errMsg );
				throw new ProcessingException( errMsg );
			}
			
			// add it to the ${roleName}, check for null
			if ( ${lowercaseClassName}.get${roleName}() != null )
				${lowercaseClassName}.get${roleName}().add( child );

			LOGGER.info( "LeagueRestService:save${className}() - added ${childName} to parent" );

		}
		else
		{
			// clear or create the ${roleName}
			if ( ${lowercaseClassName}.get${roleName}() != null )
				${lowercaseClassName}.get${roleName}().clear();
			else
				${lowercaseClassName}.set${roleName}( new HashSet<${childName}>() );
			
			// finally, find each child and add it
			if ( childIds != null )
			{
				for( Long id : childIds )
				{
					pk = new ${childName}PrimaryKey( id );
					try
					{
						// find the ${childName}
						child = childDelegate.get${childName}( pk );
						// add it to the ${roleName} List
						${lowercaseClassName}.get${roleName}().add( child );
					}
					catch( Exception exc )
					{
			        	signalBadRequest();

						String errMsg = "${className}RestService:save${roleName}() failed get child ${childName} using id " + id  + "- " + exc.getMessage();
						LOGGER.severe( errMsg );
						throw new ProcessingException( errMsg );
					}
				}
			}
		}

		try
		{
			// save the ${className}
			parentDelegate.save${className}( ${lowercaseClassName} );
			LOGGER.info( "LeagueRestService:save${className}() - saved successfully" );

		}
		catch( Exception exc )
		{
        	signalBadRequest();

			String errMsg = "${className}RestService:save${roleName}() failed saving parent ${className} - " + exc.getMessage();
			LOGGER.severe( errMsg );
			throw new ProcessingException( errMsg );
		}

		return ${lowercaseClassName};
	}

    /**
     * delete ${roleName} on ${className}
     * @return		${className}
     * @exception	ProcessingException
     */     	
	protected ${className} delete${roleName}()
		throws ProcessingException
	{		
		Long ${lowercaseClassName}Id = parseId( "${pkExpression}" );
		Long childId = parseId( "childId" );
		
		if ( loadHelper( new ${className}PrimaryKey( ${lowercaseClassName}Id ) ) == null )
			throw new ProcessingException( "${className}.delete${roleName}() - failed to load using Id " + ${lowercaseClassName}Id );

		Long[] childIds = getChildIds();
		
		if ( childIds.length > 0 )
		{
			${childName}PrimaryKey pk 					= null;
			${childName}BusinessDelegate childDelegate 	= ${childName}BusinessDelegate.get${childName}Instance();
			${className}BusinessDelegate parentDelegate = ${className}BusinessDelegate.get${className}Instance();
			Set<${childName}> children					= ${lowercaseClassName}.get${roleName}();
			${childName} child 							= null;
			
			for( Long id : childIds )
			{
				try
				{
					pk = new ${childName}PrimaryKey( id );
					
					// first remove the relevant child from the list
					child = childDelegate.get${childName}( pk );
					children.remove( child );
					
					// then safe to delete the child				
					childDelegate.delete( pk );
				}
				catch( Exception exc )
				{
					signalBadRequest();
					String errMsg = "${className}RestService:delete${roleName}() failed - " + exc.getMessage();
					LOGGER.severe( errMsg );
					throw new ProcessingException( errMsg );
				}
			}
			
			// assign the modified list of ${childName} back to the ${lowercaseClassName}
			${lowercaseClassName}.set${roleName}( children );
			
			// save it 
			try
			{
				${lowercaseClassName} = parentDelegate.save${className}( ${lowercaseClassName} );
			}
			catch( Throwable exc )
			{
				signalBadRequest();
				
				String errMsg = "${className}RestService:delete${roleName}() failed to save the ${className} - " + exc.getMessage();				
				LOGGER.severe( errMsg );
				throw new ProcessingException( errMsg );
			}
		}
		
		return ${lowercaseClassName};
	}

#end


    protected ${className} loadHelper( ${className}PrimaryKey pk )
    		throws ProcessingException
    {
    	try
        {
    		LOGGER.info( "${className}.loadHelper primary key is " + pk);
    		
        	if ( pk != null )
        	{
        		// load the contained instance of ${className}
	            this.${lowercaseClassName} = ${className}BusinessDelegate.get${className}Instance().get${className}( pk );
	            
	            LOGGER.info( "${className}RestService:loadHelper() - successfully loaded - " + this.${lowercaseClassName}.toString() );             
			}
			else
			{
	        	signalBadRequest();

				String errMsg = "${className}RestService:loadHelper() - null primary key provided.";				
				LOGGER.severe( errMsg );
	            throw new ProcessingException( errMsg );
			}	            
        }
        catch( Throwable exc )
        {
        	signalBadRequest();

        	String errMsg = "${className}RestService:load() - failed to load ${className} using pk " + pk + ", " + exc.getMessage();				
			LOGGER.severe( errMsg );
            throw new ProcessingException( errMsg );
        }

        return ${lowercaseClassName};

    }

    // overloads from BaseRestService
    
    /**
     * main handler for execution
     * @param action
     * @param response
     * @param request
     * @return
     * @throws ProcessingException
     */
	public Object handleExec( String action, spark.Response response, spark.Request request )
		throws ProcessingException
	{
		// store locally
		this.response = response;
		this.request = request;
				
		if ( action == null )
		{
			signalBadRequest();
			throw new ProcessingException();
		}

		Object returnVal = null;

		switch (action) {
	        case "save":
	        	returnVal = save();
	            break;
	        case "load":
	        	returnVal = load();
	            break;
	        case "delete":
	        	delete();
	            break;
	        case "loadAll":
	        case "viewAll":
	        	returnVal = loadAll();
	            break;
## handle multiple associations	            
#foreach( $multiAssociation in $classObject.getMultipleAssociations() )
#set( $roleName = $multiAssociation.getRoleName() )
#set( $childName = $multiAssociation.getType() )
#set( $parentName  = $classObject.getName() )
	        case "save${roleName}":
	        	returnVal = save${roleName}().get${roleName}();
	        	break;
	        case "delete${roleName}":
	        	returnVal = delete${roleName}().get${roleName}();
	        	break;
	        case "load${roleName}" :
	        	returnVal = load().get${roleName}();
	        	break;
#end ##foreach( $multiAssociation in $classObject.getMultipleAssociations() )
## handle single associations
#set( $includeComposites = false )
#foreach( $singleAssociation in $classObject.getSingleAssociations( ${includeComposites} ) )
#set( $roleName = $singleAssociation.getRoleName() )
#set( $childName = $singleAssociation.getType() )
#set( $parentName  = $classObject.getName() )
	        case "save${roleName}":
	        	returnVal = save${roleName}().get${roleName}();
	        	break;
	        case "delete${roleName}":
	        	returnVal = delete${roleName}().get${roleName}();
	        	break;
	        case "load${roleName}" :
	        	returnVal = load().get${roleName}();
	        	break;
#end ##foreach( $singleAssociation in $classObject.getSingleAssociations( ${includeComposites} ) )	        	
        default:
        	signalBadRequest();
            throw new ProcessingException("${className}.execute(...) - unable to handle action " + action);
		}
		
		return returnVal;
	}
	
	/**
	 * Uses ObjectMapper to map from Json to a ${className}. Found in the request body.
	 * 
	 * @return ${className}
	 */
	private ${className} get${className}()
	{
		if ( ${lowercaseClassName} == null )
		{
			try {
				ObjectMapper mapper = new ObjectMapper();
				mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
				${lowercaseClassName} = mapper.readValue(java.net.URLDecoder.decode(request.queryString(),"UTF-8"), ${className}.class);
				
	        } catch (Exception exc) 
	        {
	            signalBadRequest();
	            LOGGER.severe( "${className}RestService.get${className}() - failed to Json map from String to ${className} - " + exc.getMessage() );
	        }			
		}
		return( ${lowercaseClassName} );
	}
	
	protected String getSubclassName()
	{ return( "${className}RestService" ); }
	
//************************************************************************    
// Attributes
//************************************************************************
    private $className ${lowercaseClassName} 			= null;
    private static final Logger LOGGER 	= Logger.getLogger(BaseRestService.class.getName());
    
}
