#header()

#set( $appName = $aib.getApplicationNameFormatted() )
#set( $className = $classObject.getName() )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter(${className})} )
#set( $pk = ${classObject.getFirstPrimaryKey()} )		
#set( $pkExpression = "${pk.getName()}" ) 

/** 
 * Implements Struts action processing for model ${className}.
 *
 * @author $aib.getAuthor()
 */


using System;
using System.Collections;
using System.Collections.Generic;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;

using ${appName}.Delegates;
using ${appName}.Exceptions;
using ${appName}.Models;
using ${appName}.PrimaryKeys;

namespace ${appName}.Controllers
{
	public class ${className}Controller : BaseController
	{
		/**
		 * default constructor, using dependency injection to acquire a
		 * ILogger implementation
		 */
		public ${className}Controller( ILogger<${className}Controller> _logger )
		{
			logger = _logger;
		}

		/**
		 * redirect to the profile .cshtml 
		 */	
	    public IActionResult Profile( string ${pkExpression}, string action, string parentUrl )
        {
            ViewData["${pkExpression}"] = ${pkExpression};
            ViewData["action"]			= action;
            ViewData["parentUrl"] 		= parentUrl;
        
            return PartialView("~/Views/${className}Profile.cshtml");
        }
	
		/**
		 * redirect to the list .cshtml 
		 */
	    public IActionResult List( string roleName, string addUrl, string deleteUrl, string modelUrl, string parentUrl )
        {
            ViewData["roleName"] = roleName;
            ViewData["addUrl"] = addUrl;
            ViewData["deleteUrl"] = deleteUrl;
            ViewData["modelUrl"] = modelUrl;
            ViewData["parentUrl"] = parentUrl;

            return PartialView("~/Views/${className}List.cshtml");
        }
	
	    /**
	     * handles saving a ${className} BO.  if no key provided, calls create, otherwise calls save
	     */
	    public JsonResult save( [FromBody] ${className} model )
	    {
			${lowercaseClassName} = model;
			
			logger.LogInformation( "${className}.save() on - " + model.ToString()  );
			
	        if ( hasPrimaryKey() )
	        {
	            ${lowercaseClassName} = update();
	        }
	        else
	        {
	            ${lowercaseClassName} = create();
	        }
	        
	        return Json(${lowercaseClassName});
	    }
	
	    /**
	     * handles updating a ${className} model
	     */    
	    protected ${className} update()
	    {
	    	// store provided data
	        $className model = ${lowercaseClassName};
	
	        // load actual data from storage
	        loadHelper( ${lowercaseClassName}.get${className}PrimaryKey() );
	    	
	    	// copy provided data into actual data
	    	${lowercaseClassName}.copyShallow( model );
	    	
	        try
	        {                        	        
	            this.${lowercaseClassName} = ${className}Delegate.get${className}Instance().save${className}( ${lowercaseClassName} );
	            
	            if ( this.${lowercaseClassName} != null )
	            	logger.LogInformation( "${className}Controller:update() - successfully updated ${className} - " + ${lowercaseClassName}.ToString() );
	        }
	        catch( Exception exc )
	        {
	        	signalBadRequest();
	        	
	        	string errMsg = "${className}Controller:update() - successfully update ${className} - " + exc.ToString();
	        	logger.LogError( errMsg );
	        	throw ( new ProcessingException( errMsg ) );
	        }
	        
	        return this.${lowercaseClassName};
	        
	    }
	
	    /**
	     * handles creating a ${className} model
	     */
	    protected ${className} create()
	    {
	        try
	        {       
				this.${lowercaseClassName} 	= ${className}Delegate.get${className}Instance().create${className}( ${lowercaseClassName} );
	        }
	        catch( Exception exc )
	        {
	        	signalBadRequest();
	        	
	        	string errMsg = "${className}Controller:create() - exception ${className} - " + exc.ToString();
	        	logger.LogError( errMsg );
	        	throw ( new ProcessingException( errMsg ) );
	        }
	        
	        return this.${lowercaseClassName};
	    }
	
	    /**
	     * handles deleting a ${className} model
	     */
	    public JsonResult delete( String ${pkExpression}, String[] childIds ) 
	    {                
	        try
	        {
	        	if ( childIds == null || childIds.Length == 0 )
	        	{
	        		long parentId  =  convertToLong( ${pkExpression} );
	        		${className}Delegate.get${className}Instance().delete( new ${className}PrimaryKey( parentId  ) );
	        		logger.LogInformation( "${className}Controller:delete() - successfully deleted ${className} with key " + ${lowercaseClassName}.get${className}PrimaryKey().keys().ToString());
	        	}
	        	else
	        	{
	        		long tmpId;
	        		foreach( String id in childIds )
	        		{
	        			try
	        			{
	        				tmpId = convertToLong( id );
	        				if ( tmpId != 0 )
	        					${className}Delegate.get${className}Instance().delete( new ${className}PrimaryKey( tmpId ) );
	        			}
		                catch( Exception exc )
		                {
		                	signalBadRequest();
	
		                	string errMsg = "${className}Controller:delete() - " + exc.ToString();
		                	logger.LogError( errMsg );
		                	//throw ( new ProcessingException( errMsg ) );
		                }
	        		}
	        	}
	        }
	        catch( Exception exc )
	        {
	        	signalBadRequest();
	        	string errMsg = "${className}Controller:delete() - " + exc.ToString();
	        	logger.LogError( errMsg );
	        	//throw ( new ProcessingException( errMsg ) );
	        }
	        
	        return null;
		}        
		
	    /**
	     * handles loading a ${className} model
	     */    
	    public JsonResult load( String ${pkExpression} ) 
	    {    	
	        ${className}PrimaryKey pk 	= null;
			long id 					= convertToLong( ${pkExpression} );
			
	    	try
	        {
	    		logger.LogInformation( "${className}.load pk is " + id );
	    		
	        	if ( id != 0 )
	        	{
	        		pk = new ${className}PrimaryKey( id );
	
	        		loadHelper( pk );
			            
		            logger.LogInformation( "${className}Controller:load() - successfully loaded - " + this.${lowercaseClassName}.ToString() );             
				}
				else
				{
		        	signalBadRequest();
	
					string errMsg = "${className}Controller:load() - unable to locate the primary key as an attribute or a selection for - " + ${lowercaseClassName}.ToString();				
					logger.LogError( errMsg );
		            throw ( new ProcessingException( errMsg ) );
				}	            
	        }
	        catch( Exception exc )
	        {
	        	signalBadRequest();
	
	        	string errMsg = "${className}Controller:load() - failed to load ${className} using Id " + id + ", " + exc.ToString();				
				logger.LogError( errMsg );
	            throw ( new ProcessingException( errMsg ) );
	        }
	       return Json(${lowercaseClassName});
	    }
	
	    /**
	     * handles loading all ${className} models
	     */
	    public JsonResult loadAll()
	    {                
	        List<$className> ${lowercaseClassName}List = null;
	        
	    	try
	        {                        
	            // load the ${className}
	            ${lowercaseClassName}List = ${className}Delegate.get${className}Instance().getAll${className}();
	            
	            if ( ${lowercaseClassName}List != null && ${lowercaseClassName}List.Count > 0 )
	            	logger.LogInformation(  "${className}Controller:loadAll${className}() - successfully loaded all ${className}s" );
	        }
	        catch( Exception exc )
	        {
	        	signalBadRequest();
	
	        	string errMsg = "${className}Controller:loadAll() - failed to load all ${className}s - " + exc.ToString();				
				logger.LogError( errMsg );
	            throw new ProcessingException( errMsg );            
	        }

	       	return Json(${lowercaseClassName}List);
	                            
	    }
	
// findAllBy methods
#foreach( $method in $classObject.getFindAllByMethods() )
#if ( ${method.hasArguments()} )	## should only be one argument
	#set( $argType = ${method.getArguments().getArgs().get(0).getType()} )
	#set( $argName = ${method.getArguments().getArgs().get(0).getName()} )
    /**
     * finder method to ${method.getName()}
     */     
	
	/* 
		public JsonResult ${method.getName()}()
		{		
			List<${className}> ${lowercaseClassName}List = null;
	        try
	        {  
	        	$argType argName = new $argType( request.getParams( "${argName}" ) );
	            // load the ${className}
	            ${lowercaseClassName}List = ${className}Delegate.get${className}Instance().${method.getName()}(${arg});
	            
	            if ( ${lowercaseClassName}List != null )
	            	logger.LogInformation(  "${className}Controller:${method.getName()}() - successfully loaded" );
	        }
	        catch( Exception exc )
	        {
	        	signalBadRequest();
	
	        	string errMsg = "${className}Controller:loadAll() - failed to load all ${className}s - " + exc.ToString()				
				logger.LogError( errMsg );
	            throw ( new ProcessingException( errMsg ) );            
	        }
	        return Json(${lowercaseClassName}List);
		} 
		*/
	#end ##if ( ${method.hasArguments()} )	## should only be one argument
#end ##foreach( $method in $classObject.getFindAllByMethods() )

#set( $includeComposites = false )
#foreach( $singleAssociation in $classObject.getSingleAssociations( ${includeComposites} ) )
#set( $roleName = $singleAssociation.getRoleName() )
#set( $childName = $singleAssociation.getType() )
#set( $parentName  = $classObject.getName() )
	    /**
	     * save ${roleName} on ${className}
	     */     
		public JsonResult save${roleName}( String ${pkExpression}, String childId )
		{
			long parentId 	= convertToLong( ${pkExpression} );
			
			if ( loadHelper( new ${className}PrimaryKey( parentId ) ) == null )
				return( null );
			
			if ( childId != null )
			{
				long tmpId 			= convertToLong( childId );
				${childName} child 	= null;
	
				try
				{
					if ( tmpId != 0 )
						child = ${childName}Delegate.get${childName}Instance().get${childName}( new ${childName}PrimaryKey( tmpId ) );
				}
	            catch( Exception exc )
	            {
	            	signalBadRequest();
	
	            	string errMsg = "${className}Controller:save${roleName}() failed to get ${childName} using id " + childId + " - " + exc.ToString();
	            	logger.LogError( errMsg);
	            	throw ( new ProcessingException( errMsg ) );
	            }
		
				${lowercaseClassName}.${roleName} = child;
			
				try
				{
					// save it
					${className}Delegate.get${className}Instance().save${className}( ${lowercaseClassName} );
				}
				catch( Exception exc )
				{
		        	signalBadRequest();
	
					string errMsg = "${className}Controller:save${roleName}() failed saving parent ${className} - " + exc.ToString();
					logger.LogError( errMsg );
					throw new ProcessingException( errMsg );
				}
			}
			return Json(${lowercaseClassName});
		}
	
	    /**
	     * delete ${roleName} on ${className}
	     */     
		public JsonResult delete${roleName}( String ${pkExpression} )
		{
			long parentId = convertToLong( ${pkExpression} );
			
	 		if ( loadHelper( new ${className}PrimaryKey( parentId ) ) == null )
				return( null );
	
			if ( ${lowercaseClassName}.${roleName} != null )
			{
				${childName}PrimaryKey pk = ${lowercaseClassName}.${roleName}.get${childName}PrimaryKey();
				
				// null out the parent first so there's no constraint during deletion
				${lowercaseClassName}.${roleName} = null;
				try
				{
					// save it
					${lowercaseClassName} = ${className}Delegate.get${className}Instance().save${className}( ${lowercaseClassName} );
				}
				catch( Exception exc )
				{
		        	signalBadRequest();
	
					string errMsg = "${className}Controller:delete${roleName}() failed to save ${className} - " + exc.ToString();
					logger.LogError( errMsg );
					throw ( new ProcessingException( errMsg ) );
				}
				
				try
				{
					// safe to delete the child			
					${childName}Delegate.get${childName}Instance().delete( pk );
				}
				catch( Exception exc )
				{
		        	signalBadRequest();
	
					string errMsg = "${className}Controller:delete${roleName}() failed  - " + exc.ToString();
					logger.LogError( errMsg );
					throw ( new ProcessingException( errMsg ) );
				}
			}
			
			return Json(${lowercaseClassName});			
		}
	
#end

#foreach( $multiAssociation in $classObject.getMultipleAssociations() )
#set( $roleName = $multiAssociation.getRoleName() )
#set( $childName = $multiAssociation.getType() )
#set( $parentName  = $classObject.getName() )

	    /**
	     * load ${roleName} on ${className}
	     */     
		public JsonResult load${roleName}( String ${pkExpression} )
		{
			long parentId = convertToLong( ${pkExpression} );
			
			if ( loadHelper( new ${className}PrimaryKey( parentId ) ) == null )
				throw ( new ProcessingException( "${className}.load${roleName}() - failed to load parent using Id " + parentId ) );
				
			return Json(${lowercaseClassName}.${roleName});  				
		}
		
	    /**
	     * save ${roleName} on ${className}
	     */     
		public JsonResult save${roleName}( String ${pkExpression}, String childId, String[] childIds )
		{
			long parentId = convertToLong( ${pkExpression} );
			
			if ( loadHelper( new ${className}PrimaryKey( parentId ) ) == null )
				throw ( new ProcessingException( "${className}.save${roleName}() - failed to load parent using Id " + parentId ) );
			 
			${childName}PrimaryKey pk 		= null;
			${childName} child				= null;
			
			long tmpChildId = convertToLong( childId );
			
			if ( tmpChildId != 0  )// creating or saving one
			{
				pk = new ${childName}PrimaryKey( tmpChildId );
				
				try
				{
					// find the ${childName}
					child = ${childName}Delegate.get${childName}Instance().get${childName}( pk );
					logger.LogInformation( "LeagueController:save${className}() - found ${childName}" );
				}
				catch( Exception exc )
				{
		        	signalBadRequest();
	
					string errMsg = "${className}Controller:save${roleName}() failed get child ${childName} using id " + childId  + "- " + exc.ToString();
					logger.LogError( errMsg );
					throw ( new ProcessingException( errMsg ) );
				}
				
				// add it to the ${roleName}, check for null
				if ( ${lowercaseClassName}.${roleName} != null )
					${lowercaseClassName}.${roleName}.Add( child );
	
				logger.LogInformation( "LeagueController:save${className}() - added ${childName} to parent" );
	
			}
			else
			{
				// clear or create the ${roleName}
				if ( ${lowercaseClassName}.${roleName} != null )
					${lowercaseClassName}.${roleName}.Clear();
				else
					${lowercaseClassName}.${roleName} = new List<${childName}>();
				
				// finally, find each child and add it
				if ( childIds != null )
				{
					foreach( String id in childIds )
					{
						pk = new ${childName}PrimaryKey( convertToLong( id ) );
						try
						{
							// find the ${childName}
							child = ${childName}Delegate.get${childName}Instance().get${childName}( pk );
							// add it to the ${roleName} List
							${lowercaseClassName}.${roleName}.Add( child );
						}
						catch( Exception exc )
						{
				        	signalBadRequest();
	
							string errMsg = "${className}Controller:save${roleName}() failed get child ${childName} using id " + id  + "- " + exc.ToString();
							logger.LogError( errMsg );
							throw ( new ProcessingException( errMsg ) );
						}
					}
				}
			}
	
			try
			{
				// save the ${className}
				${className}Delegate.get${className}Instance().save${className}( ${lowercaseClassName} );
				logger.LogInformation( "LeagueController:save${className}() - saved successfully" );
	
			}
			catch( Exception exc )
			{
	        	signalBadRequest();
	
				string errMsg = "${className}Controller:save${roleName}() failed saving parent ${className} - " + exc.ToString();
				logger.LogError( errMsg );
				throw ( new ProcessingException( errMsg ) );
			}
			return Json(${lowercaseClassName});	
		}
	
	    /**
	     * delete ${roleName} on ${className}
	     */     	
		public JsonResult delete${roleName}(String ${pkExpression}, String[] childIds)
		{		
			long parentId = convertToLong( ${pkExpression} );
			
			if ( loadHelper( new ${className}PrimaryKey( parentId ) ) == null )
				throw new ProcessingException( "${className}.delete${roleName}() - failed to load using Id " + parentId );
	
			if ( childIds.Length > 0 )
			{
				${childName}PrimaryKey pk 					= null;
				IList<${childName}> children				= ${lowercaseClassName}.${roleName};
				${childName} child 							= null;
				
				foreach( String id in childIds )
				{
					try
					{
						pk = new ${childName}PrimaryKey( convertToLong( id ) );
						
						// first remove the relevant child from the list
						child = ${childName}Delegate.get${childName}Instance().get${childName}( pk );
						children.Remove( child );
						
						// then safe to delete the child				
						${childName}Delegate.get${childName}Instance().delete( pk );
					}
					catch( Exception exc )
					{
						signalBadRequest();
						string errMsg = "${className}Controller:delete${roleName}() failed - " + exc.ToString();
						logger.LogError( errMsg );
						throw ( new ProcessingException( errMsg ) );
					}
				}
				
				// assign the modified list of ${childName} back to the ${lowercaseClassName}
				${lowercaseClassName}.${roleName} = children;
				
				// save it 
				try
				{
					${lowercaseClassName} = ${className}Delegate.get${className}Instance().save${className}( ${lowercaseClassName} );
				}
				catch( Exception exc )
				{
					signalBadRequest();
					
					string errMsg = "${className}Controller:delete${roleName}() failed to save the ${className} - " + exc.ToString();				
					logger.LogError( errMsg );
					throw ( new ProcessingException( errMsg ) );
				}
			}
			
			return Json(${lowercaseClassName});
		}
#end
	    protected ${className} loadHelper( ${className}PrimaryKey pk )
	    {
	    	try
	        {
	    		logger.LogInformation( "${className}.loadHelper primary key is " + pk);
	    		
	        	if ( pk != null )
	        	{
	        		// load the contained instance of ${className}
		            this.${lowercaseClassName} = ${className}Delegate.get${className}Instance().get${className}( pk );
		            
		            logger.LogInformation( "${className}Controller:loadHelper() - successfully loaded - " + this.${lowercaseClassName}.ToString() );             
				}
				else
				{
		        	signalBadRequest();
	
					string errMsg = "${className}Controller:loadHelper() - null primary key provided.";				
					logger.LogError( errMsg );
		            throw ( new ProcessingException( errMsg ) );
				}	            
	        }
	        catch( Exception exc )
	        {
	        	signalBadRequest();
	
	        	string errMsg = "${className}Controller:load() - failed to load ${className} using pk " + pk + ", " + exc.ToString();				
				logger.LogError( errMsg );
	            throw ( new ProcessingException( errMsg ) );
	        }
	
	        return ${lowercaseClassName};
	
	    }
	

	    /**
	     * returns true if the $lowercaseClassName is non-null and has it's primary key field(s) set
	     */
	    protected bool hasPrimaryKey()
	    {
	    	bool hasPK = false;
	
			if ( ${lowercaseClassName} != null )
#set( $attributes = $classObject.getPrimaryKeyAttributes() )
#foreach( $attribute in $attributes )
				if ( ${lowercaseClassName}.${attribute.getName()} != 0 )
#end## $attribute in $attributes     	
			   hasPK = true;
			
			return( hasPK );
	    }
	
		protected string getSubclassName()
		{ return( "${className}Controller" ); }
		
		override protected ILogger getLogger()
		{ return( logger ); }

	
//************************************************************************    
// Attributes
//************************************************************************
	    private $className ${lowercaseClassName} 			= null;
		private readonly ILogger<${className}Controller> logger;
	}    
}


