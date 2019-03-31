#header()
#set( $className = ${classObject.getName()} )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter( ${className} )} )
#set( $pk = ${classObject.getFirstPrimaryKey()} )		
#set( $pkExpression = "${lowercaseClassName}.${pk.getName()}" ) 
package ${aib.getRootPackageName(true)}.#getRestControllerPackageName();

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

#set( $imports = [ "#getPrimaryKeyPackageName()", "#getDelegatePackageName()", "#getBOPackageName()" ] )
#importStatements( $imports )

/** 
 * Implements Struts action processing for business entity ${className}.
 *
 * @author $aib.getAuthor()
 */
##if ( $classObject.hasParent() == true )
##	#set( $parentController = "${classObject.getParentName()}RestController" )
##	#set( $parentName = $classObject.getParentName() )
##else
	#set( $parentController = "BaseSpringRestController" )
##end
@RestController
@RequestMapping("/${className}")
public class ${className}RestController extends $parentController
{

    /**
     * Handles saving a ${className} BO.  if not key provided, calls create, otherwise calls save
     * @param		${className} $lowercaseClassName
     * @return		${className}
     */
	@RequestMapping("/save")
    public ${className} save( @RequestBody ${className} $lowercaseClassName )
    {
    	// assign locally
    	this.${lowercaseClassName} = $lowercaseClassName;
    	
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
     * Handles deleting a ${className} BO
     * @param		Long ${lowercaseClassName}Id
     * @param 		Long[] childIds
     * @return		boolean
     */
    @RequestMapping("/delete")    
    public boolean delete( @RequestParam(value="${pkExpression}", required=false) Long ${lowercaseClassName}Id, 
    						@RequestParam(value="childIds", required=false) Long[] childIds )
    {                
        try
        {
        	${className}BusinessDelegate delegate = ${className}BusinessDelegate.get${className}Instance();
        	
        	if ( childIds == null || childIds.length == 0 )
        	{
        		delegate.delete( new ${className}PrimaryKey( ${lowercaseClassName}Id ) );
        		LOGGER.info( "${className}Controller:delete() - successfully deleted ${className} with key " + ${lowercaseClassName}.get${className}PrimaryKey().valuesAsCollection() );
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
	                	LOGGER.info( "${className}Controller:delete() - " + exc.getMessage() );
	                	return false;
	                }
        		}
        	}
        }
        catch( Throwable exc )
        {
        	LOGGER.info( "${className}Controller:delete() - " + exc.getMessage() );
        	return false;        	
        }
        
        return true;
	}        
	
    /**
     * Handles loading a ${className} BO
     * @param		Long ${lowercaseClassName}Id
     * @return		${className}
     */    
    @RequestMapping("/load")
    public ${className} load( @RequestParam(value="${pkExpression}", required=true) Long ${lowercaseClassName}Id )
    {    	
        ${className}PrimaryKey pk = null;

    	try
        {  
    		System.out.println( "\n\n****${className}.load pk is " + ${lowercaseClassName}Id );
        	if ( ${lowercaseClassName}Id != null )
        	{
        		pk = new ${className}PrimaryKey( ${lowercaseClassName}Id );
        		
        		// load the ${className}
	            this.${lowercaseClassName} = ${className}BusinessDelegate.get${className}Instance().get${className}( pk );
	            
	            LOGGER.info( "${className}Controller:load() - successfully loaded - " + this.${lowercaseClassName}.toString() );             
			}
			else
			{
	            LOGGER.info( "${className}Controller:load() - unable to locate the primary key as an attribute or a selection for - " + ${lowercaseClassName}.toString() );
	            return null;
			}	            
        }
        catch( Throwable exc )
        {
            LOGGER.info( "${className}Controller:load() - failed to load ${className} using Id " + ${lowercaseClassName}Id + ", " + exc.getMessage() );
            return null;
        }

        return ${lowercaseClassName};

    }

    /**
     * Handles loading all ${className} business objects
     * @return		List<${className}>
     */
    @RequestMapping("/loadAll")
    public List<${className}> loadAll()
    {                
        List<$className> ${lowercaseClassName}List = null;
        
    	try
        {                        
            // load the ${className}
            ${lowercaseClassName}List = ${className}BusinessDelegate.get${className}Instance().getAll${className}();
            
            if ( ${lowercaseClassName}List != null )
                LOGGER.info(  "${className}Controller:loadAll${className}() - successfully loaded all ${className}s" );
        }
        catch( Throwable exc )
        {
            LOGGER.info(  "${className}Controller:loadAll() - failed to load all ${className}s - " + exc.getMessage() );
        	return null;
            
        }

        return ${lowercaseClassName}List;
                            
    }


// findAllBy methods
#*
#foreach( $method in $classObject.getFindAllByMethods() )
	#if ( ${method.hasArguments()} )	## should only be one argument
    	#set( $argType = ${method.getArguments().getArgs().get(0).getType()} )
    	#set( $argName = ${method.getArguments().getArgs().get(0).getName()} )
    /**
     * finder method to ${method.getName()}
     * @param 		$argType $argName
     * @return		List<${className}>
     */     
	@RequestMapping("/${method.getName()}")
	public List<${className}> ${method.getName()}( @RequestParam(value="arg", required=true) $argType $argName )
	{
		
		List<${className}> ${lowercaseClassName}List = null;
        try
        {  
            // load the ${className}
            ${lowercaseClassName}List = ${className}BusinessDelegate.get${className}Instance().${method.getName()}(${arg});
            
            if ( ${lowercaseClassName}List != null )
                LOGGER.info(  "${className}Controller:${method.getName()}() - successfully loaded" );
        }
        catch( Throwable exc )
        {
        	LOGGER.info(  "${className}Controller:loadAll() - failed to load all ${className}s - " + exc );
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
     * @param		Long	${lowercaseClassName}Id
     * @param		Long	childId
     * @param		${className} $lowercaseClassName
     * @return		${className}
     */     
	@RequestMapping("/save${roleName}")
	public ${className} save${roleName}( @RequestParam(value="${pkExpression}", required=true) Long ${lowercaseClassName}Id, 
											@RequestParam(value="childIds", required=true) Long childId, @RequestBody ${className} $lowercaseClassName )
	{
		if ( load( ${lowercaseClassName}Id ) == null )
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
            	LOGGER.info( "${className}Controller:save${roleName}() failed to get ${childName} using id " + childId + " - " + exc.getMessage() );
            	return null;
            }
	
			${lowercaseClassName}.set${roleName}( child );
		
			try
			{
				// save it
				parentDelegate.save${className}( ${lowercaseClassName} );
			}
			catch( Exception exc )
			{
				LOGGER.info( "${className}Controller:save${roleName}() failed saving parent ${className} - " + exc.getMessage() );
			}
		}
		
		return ${lowercaseClassName};
	}

    /**
     * delete ${roleName} on ${className}
     * @param		Long ${lowercaseClassName}Id
     * @return		${className}
     */     
	@RequestMapping("/delete${roleName}")
	public ${className} delete${roleName}( @RequestParam(value="${pkExpression}", required=true) Long ${lowercaseClassName}Id )
	
	{
		if ( load( ${lowercaseClassName}Id ) == null )
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
				LOGGER.info( "${className}Controller:delete${roleName}() failed to save ${className} - " + exc.getMessage() );
			}
			
			try
			{
				// safe to delete the child			
				${childName}BusinessDelegate childDelegate = ${childName}BusinessDelegate.get${childName}Instance();
				childDelegate.delete( pk );
			}
			catch( Exception exc )
			{
				LOGGER.info( "${className}Controller:delete${roleName}() failed  - " + exc.getMessage() );
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
     * @param		Long[] childIds
     * @return		${className}
     */     
	@RequestMapping("/save${roleName}")
	public ${className} save${roleName}( @RequestParam(value="${pkExpression}", required=false) Long ${lowercaseClassName}Id, 
											@RequestParam(value="childIds", required=false) Long childId, @RequestParam("") Long[] childIds )
	{
		if ( load( ${lowercaseClassName}Id ) == null )
			return( null );
		
		${childName}PrimaryKey pk 					= null;
		${childName} child							= null;
		${childName}BusinessDelegate childDelegate 	= ${childName}BusinessDelegate.get${childName}Instance();
		${className}BusinessDelegate parentDelegate = ${className}BusinessDelegate.get${className}Instance();		
		
		if ( childId != null || childIds.length == 0 )// creating or saving one
		{
			pk = new ${childName}PrimaryKey( childId );
			
			try
			{
				// find the ${childName}
				child = childDelegate.get${childName}( pk );
			}
			catch( Exception exc )
			{
				LOGGER.info( "${className}Controller:save${roleName}() failed get child ${childName} using id " + childId  + "- " + exc.getMessage() );
				return( null );
			}
			
			// add it to the ${roleName} 
			${lowercaseClassName}.get${roleName}().add( child );				
		}
		else
		{
			// clear out the ${roleName} but 
			${lowercaseClassName}.get${roleName}().clear();
			
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
						LOGGER.info( "${className}Controller:save${roleName}() failed get child ${childName} using id " + id  + "- " + exc.getMessage() );
					}
				}
			}
		}

		try
		{
			// save the ${className}
			parentDelegate.save${className}( ${lowercaseClassName} );
		}
		catch( Exception exc )
		{
			LOGGER.info( "${className}Controller:save${roleName}() failed saving parent ${className} - " + exc.getMessage() );
		}

		return ${lowercaseClassName};
	}

    /**
     * delete ${roleName} on ${className}
     * @param		Long ${lowercaseClassName}Id
     * @param		Long[] childIds
     * @return		${className}
     */     	
	@RequestMapping("/delete${roleName}")
	public ${className} delete${roleName}( @RequestParam(value="${pkExpression}", required=true) Long ${lowercaseClassName}Id, 
											@RequestParam(value="childIds", required=false) Long[] childIds )
	{		
		if ( load( ${lowercaseClassName}Id ) == null )
			return( null );

		if ( childIds != null || childIds.length == 0 )
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
					LOGGER.info( "${className}Controller:delete${roleName}() failed - " + exc.getMessage() );
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
				LOGGER.info( "${className}Controller:delete${roleName}() failed to save the ${className} - " + exc.getMessage() );
			}
		}
		
		return ${lowercaseClassName};
	}

#end

    /**
     * Handles creating a ${className} BO
     * @return		${className}
     */
    protected ${className} create()
    {
        try
        {       
			this.${lowercaseClassName} = ${className}BusinessDelegate.get${className}Instance().create${className}( ${lowercaseClassName} );
        }
        catch( Throwable exc )
        {
        	LOGGER.info( "${className}Controller:create() - exception ${className} - " + exc.getMessage());        	
        	return null;
        }
        
        return this.${lowercaseClassName};
    }

    /**
     * Handles updating a ${className} BO
     * @return		${className}
     */    
    protected ${className} update()
    {
    	// store provided data
        $className tmp = ${lowercaseClassName};

        // load actual data from db
    	load();
    	
    	// copy provided data into actual data
    	${lowercaseClassName}.copyShallow( tmp );
    	
        try
        {                        	        
			// create the ${className}Business Delegate            
			${className}BusinessDelegate delegate = ${className}BusinessDelegate.get${className}Instance();
            this.${lowercaseClassName} = delegate.save${className}( ${lowercaseClassName} );
            
            if ( this.${lowercaseClassName} != null )
                LOGGER.info( "${className}Controller:update() - successfully updated ${className} - " + ${lowercaseClassName}.toString() );
        }
        catch( Throwable exc )
        {
        	LOGGER.info( "${className}Controller:update() - successfully update ${className} - " + exc.getMessage());        	
        	return null;
        }
        
        return this.${lowercaseClassName};
        
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

    protected ${className} load()
    {
    	return( load( new Long( ${lowercaseClassName}.get${className}PrimaryKey().getFirstKey().toString() ) ));
    }

//************************************************************************    
// Attributes
//************************************************************************
    protected $className $lowercaseClassName = null;
    private static final Logger LOGGER = Logger.getLogger(${classObject.getName()}.class.getName());
    
}


