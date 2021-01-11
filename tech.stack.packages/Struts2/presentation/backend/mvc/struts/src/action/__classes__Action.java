#header()
#set( $className = ${classObject.getName()} )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter( ${className} )} )
#set( $singleAssociationsWithSourceRole = $classObject.getSingleAssociationsWithSourceRole() )	
#set( $singleAssociationsWithoutSourceRole = $classObject.getSingleAssociationsWithoutSourceRole() )	
package ${aib.getRootPackageName(true)}.#getActionPackageName();

import javax.servlet.http.*;

import java.util.*;

import java.util.logging.Level;
import java.util.logging.Logger;

import ${aib.getRootPackageName(true)}.exception.*;

#set( $imports = [ "#getPrimaryKeyPackageName()", "#getDelegatePackageName()", "#getBOPackageName()" ] )
#importStatements( $imports )

import ${aib.getRootPackageName()}.#getActionPackageName().*;


/** 
 * Implements Struts action processing for business entity ${className}.
 *
 * @author $aib.getAuthor()
 */
#if ( $classObject.hasParent() == true )
	#set( $parentAction = "${classObject.getParentName()}Action" )
	#set( $parentName = $classObject.getParentName() )
#else
	#set( $parentAction = "BaseStrutsAction" )
#end
public class ${className}Action extends $parentAction
{
 
   	public $className get${className}()
   	{
   		if ( $lowercaseClassName == null )
   		{
   	   		$lowercaseClassName = new ${className}();
#if ( $classObject.hasParent() == true )   	   		
   	   		${lowercaseClassName}.copy( get${parentName}() );
#end   	   		
   	   	}
   		
   	    return( $lowercaseClassName );
   	}

// delegate access methods to underlying model, including primary keys
#foreach( $attribute in ${classObject.getAttributesOnly(true,true)} )
#set( $type = $attribute.getType() )
#set( $name = ${Utils.capitalizeFirstLetter( ${attribute.getName()} )} )
    public $type get${name}() { return get${className}().get${name}(); }
    public void set${name}( $type arg ) { get${className}().set${name}( arg ); }

#end

    public List getGridModel() {
    	return( ${lowercaseClassName}List );
    }
   	
   	public List getList() {
   		return( ${lowercaseClassName}List );
   	}

#foreach( $multiAssociation in $classObject.getMultipleAssociations() )
#set( $roleName = $multiAssociation.getRoleName() )
#set( $type = $multiAssociation.getType() )
    public Set<${type}> get${roleName}() {
        return( ${lowercaseClassName}.get${roleName}() );
    }
#end

	
	public String executeGridAction()
	throws Exception {
		String result = "error";
		String operator = getOper();
		
		if ( operator != null ) {
			if ( operator.equalsIgnoreCase( "add" ) ||  operator.equalsIgnoreCase( "edit" ) ) {
				result = save();
			}
			else if ( operator.equalsIgnoreCase( "del" ) ) {
				result = delete();
			}				
		}
		
		return( result );
		
	}
	
    /**
     * Handles saving a ${className} BO by either creating a new one
     * or updating an existing one.  The lack of or presense of a primary key field is 
     * the difference
     *
     * @exception     Exception
     */
    public String save()
    throws Exception {
        if ( hasPrimaryKey() ) {
            return (update());
        }
        else {
            return (create());
        }
    }

    /**
     * Handles creating a ${className} BO
     *
     * @exception     Exception
     */
    public String create()
    throws Exception {
        try {       
	        $className $lowercaseClassName = get${className}();                 
	        
			// Create the ${className} by calling the 
			// create ${className} method on ${className}BusinessDelegate
			this.${lowercaseClassName} = ${className}BusinessDelegate.get${className}Instance().create${className}( ${lowercaseClassName} );
	
            if ( ${lowercaseClassName} != null ) {
            	LOGGER.info( "${className}Action:create() - successfully created ${className} - " + ${lowercaseClassName}.toString() );
            }
        }
        catch( Throwable exc ) {
        	LOGGER.severe( "${className}Action:create() - failed on ${className} - " + exc.getMessage());        	
        	addMessage( "failed to create ${className}" );
        	return ERROR;
        }
        
        return SUCCESS;
    }

    public String update()
    throws Exception {
    	// store provided data
        $className tmp = get${className}();

        // load actual data from db
    	load();
    	
    	// copy provided data into actual data
    	${lowercaseClassName}.copyShallow( tmp );
    	
        try {                        
			// create the ${className}Business Delegate            
			${className}BusinessDelegate delegate = ${className}BusinessDelegate.get${className}Instance();
            this.${lowercaseClassName} = delegate.save${className}( ${lowercaseClassName} );
            
            if ( this.${lowercaseClassName} != null )
                LOGGER.info( "${className}Action:update() - successfully updated ${className} - " + ${lowercaseClassName}.toString() );
        }
        catch( Throwable exc ) {
        	LOGGER.severe( "${className}Action:update() - failed to update ${className} - " + exc.getMessage());        	
        	addMessage( "failed to update ${className} with key " + ${lowercaseClassName}.get${className}PrimaryKey().valuesAsCollection() );
        	return ERROR;
        }
        
        return SUCCESS;
        
    }

    /**
     * Handles deleting a ${className} BO
     *
     * @exception       Exception
     */                
    public String delete()
    throws Exception {                
        try {
        	${className}BusinessDelegate delegate = ${className}BusinessDelegate.get${className}Instance();
        	
        	if ( getChildIds() == null ) {
        		delegate.delete( ${lowercaseClassName}.get${className}PrimaryKey() );
        		LOGGER.info( "${className}Action:delete() - successfully deleted ${className} with key " + ${lowercaseClassName}.get${className}PrimaryKey().valuesAsCollection() );
        	}
        	else {
        		for ( Long id : getChildIds() ) {
        			delegate.delete( new ${className}PrimaryKey( id ) );
        		}
        	}
 		}                
        catch( Throwable exc ) {
        	LOGGER.info( "${className}Action:delete() - " + exc.getMessage() );
        	addMessage( "failed to delete ${className} with key " + ${lowercaseClassName}.get${className}PrimaryKey().valuesAsCollection() );
        	return ERROR;
        }
        
        return NONE;
	}        
	
    /**
     * Handles loading a ${className} BO, overloaded from BaseStrutsAction
     *
     * @exception       Exception
     */
    public String load()
    throws Exception
    {    	
        ${className}PrimaryKey pk = null;

    	try {  
	        $className $lowercaseClassName = get${className}();
	        
        	if ( hasPrimaryKey() )
	        	pk = ${lowercaseClassName}.get${className}PrimaryKey();
        	
        	if ( pk != null ) {
	            // load the ${className}
	            this.${lowercaseClassName} = ${className}BusinessDelegate.get${className}Instance().get${className}( pk );
	            
	            LOGGER.info( "${className}Action:load() - successfully loaded - " + this.${lowercaseClassName}.toString() );             
			}
			else {
	            LOGGER.warning( "${className}Action:load() - unable to locate the primary key as an attribute or a selection for - " + ${lowercaseClassName}.toString() );
	            addMessage( "failed to load ${className} with key " + pk.valuesAsCollection() );
	            return ERROR;
			}	            
        }
        catch( Throwable exc ) {
            LOGGER.severe( "${className}Action:load() - failed to load - " + this.${lowercaseClassName}.toString() + ", " + exc.getMessage() );
            addMessage( "failed to load ${className} with key " + pk.valuesAsCollection() );            
            return ERROR;
        }

        return SUCCESS;

    }

    /**
     * Handles loading all ${className} BO
     *
     * @exception       ProcessingException
     */
    public String loadAll()
    throws Exception {                
        try {                        
            // load the ${className}
            ${lowercaseClassName}List = ${className}BusinessDelegate.get${className}Instance().getAll${className}();
            
            if ( ${lowercaseClassName}List != null )
                LOGGER.info( "${className}Action:loadAll${className}() - successfully loaded all ${className}s" );
            
            paginate( ${lowercaseClassName}List ); 
        }
        catch( Throwable exc ) {
            LOGGER.warning( "${className}Action:loadAll() - failed to load all ${className}s - " + exc.getMessage() );
            addMessage( "failed to loadAll ${className}" );
        	return ERROR;
        }

        return SUCCESS;
                            
    }


#*
// findAllBy methods
#foreach( $method in $classObject.getFindAllByMethods() )
    /**
     * finder method to locate ${method.getName()}
     * applies the result to this $lowercaseClassName
     * @exception       Exception
     * @return			result		String
     */     
	public String ${method.getName()}()
	throws Exception {
        try {  
    #set( $arg = "" )	## default to empty        
    
    #if ( ${method.hasArguments()} )	## should only be one argument
    
        #set( $argType = ${method.getArguments().getArgs().get(0).getType()} )
        #set( $argName = ${method.getArguments().getArgs().get(0).getName()} )
        
        #if ( $model.isClassType( $argType ) == true )
            #set( $len = ${argType.length()})
            #set( $len = $len - 2 )
            #set( $argType = ${argType.substring( 0, ${len} )} )
			// grab the selection
			String[] selections = getSelections();
			Iterator<Collection> keysIter = getPrimaryKeysFromSelections().iterator();
			${argType}PrimaryKey pk = null;
			${argType} bo = null;
			
			if ( keysIter.hasNext() )
				pk = new ${argType}PrimaryKey(keysIter.next());
			else {
                LOGGER.info( "${className}Action:${method.getName()}() - cannot locate the primary key for $argType in the HttpServletRequest." );
				return SUCCESS;
			}
			bo = ${argType}BusinessDelegate.get${argType}Instance().get${argType}(pk);
            #set( $arg = "bo" )			
		#else
			#set( $arg = "${lowercaseClassName}.get${Utils.capitalizeFirstLetter(${argName})}()" )
		#end ##if ( $argType.endsWith( "BO" ) )
    #end ##if ( $method.hasArguments() )	
            // load the ${className}
            ${lowercaseClassName}List = ${className}BusinessDelegate.get${className}Instance().${method.getName()}(${arg});
            
            if ( ${lowercaseClassName}List != null )
                LOGGER.info( "${className}Action:${method.getName()}() - successfully loaded" );
            
            paginate( ${lowercaseClassName}List );
        }
        catch( Throwable exc ) {
        	LOGGER.warning( "${className}Action:loadAll() - failed to load all ${className}s - " + exc.getMessage())
            throw new Exception( "${className}Action:loadAll() - failed to load all ${className}s - " + exc, exc );
        }
        return SUCCESS;
	}		
#end##foreach( $method in $classObject.getFindAllByMethods() )
*#

    /**
     * Returns true if the $lowercaseClassName is non-null and has it's primary key field(s) set
     */
    protected boolean hasPrimaryKey() {
	    $className $lowercaseClassName = get${className}();
    	boolean hasPK = false;

		if ( ${lowercaseClassName} != null && ${lowercaseClassName}.get${className}PrimaryKey().hasBeenAssigned() == true )
		   hasPK = true;
		return( hasPK );
    }

#set( $includeComposites = false )

#foreach( $singleAssociation in $classObject.getSingleAssociations( ${includeComposites} ) )

#set( $roleName = $singleAssociation.getRoleName() )
#set( $childName = $singleAssociation.getType() )
#set( $parentName  = $classObject.getName() )

	public String save${roleName}()
	throws Exception {
		if ( load() == ERROR )
			return( ERROR );
		
		if ( childId != null ) {
			${childName}BusinessDelegate childDelegate 	= ${childName}BusinessDelegate.get${childName}Instance();
			${className}BusinessDelegate parentDelegate = ${className}BusinessDelegate.get${className}Instance();			
			${childName} child 							= null;

			child = childDelegate.get${childName}( new ${childName}PrimaryKey( childId ) );
	
			${lowercaseClassName}.set${roleName}( child );
		
			// save it
			parentDelegate.save${className}( ${lowercaseClassName} );
		}
		
		return NONE;
	}

	public String delete${roleName}()
	throws Exception {
		if ( load() == ERROR )
			return( ERROR );

		if ( ${lowercaseClassName}.get${roleName}() != null ) {
			${childName}PrimaryKey pk = ${lowercaseClassName}.get${roleName}().get${childName}PrimaryKey();
			
			// null out the parent first so there's no constraint during deletion
			${lowercaseClassName}.set${roleName}( null );
			${className}BusinessDelegate parentDelegate = ${className}BusinessDelegate.get${className}Instance();
			
			parentDelegate.save${className}( ${lowercaseClassName} );
			
			// safe to delete the child
			${childName}BusinessDelegate childDelegate = ${childName}BusinessDelegate.get${childName}Instance();
			childDelegate.delete( pk );
		}
		
		return NONE;
	}
	
#end

#foreach( $multiAssociation in $classObject.getMultipleAssociations() )

#set( $roleName = $multiAssociation.getRoleName() )
#set( $childName = $multiAssociation.getType() )
#set( $parentName  = $classObject.getName() )

    public String load${roleName}() throws Exception {
        String result = load();

        if (result == SUCCESS) {
            paginate(${lowercaseClassName}.get${roleName}());
        }

        return result;
    }

	public String save${roleName}()
	throws Exception {
		if ( load() == ERROR )
			return( ERROR );
		
		${childName}PrimaryKey pk 					= null;
		${childName} child							= null;
		${childName}BusinessDelegate childDelegate 	= ${childName}BusinessDelegate.get${childName}Instance();
		${className}BusinessDelegate parentDelegate = ${className}BusinessDelegate.get${className}Instance();		
		
		// creating or saving one 
		if ( childId != null ) {
			pk = new ${childName}PrimaryKey( childId );
			
			// find the ${childName}
			child = childDelegate.get${childName}( pk );
			// add it to the ${roleName} 
			${lowercaseClassName}.get${roleName}().add( child );				
		}
		else {
			// clear out the ${roleName} but 
			${lowercaseClassName}.get${roleName}().clear();
			
			// finally, find each child and add it
			if ( getChildIds() != null ) {
				for( Long id : getChildIds() ) {
					pk = new ${childName}PrimaryKey( id );
					child = childDelegate.get${childName}( pk );
	
					// add it to the ${roleName} List
					${lowercaseClassName}.get${roleName}().add( child );
				}
			}
		}
		
		// save the ${className}
		parentDelegate.save${className}( ${lowercaseClassName} );

		// paginate it
		paginate( ${lowercaseClassName}.get${roleName}() );
		return NONE;
	}

	public String delete${roleName}()
	throws Exception {		
		if ( load() == ERROR )
			return( ERROR );

		if ( getChildIds() != null ) {
			${childName}PrimaryKey pk 					= null;
			${childName}BusinessDelegate childDelegate 	= ${childName}BusinessDelegate.get${childName}Instance();
			${className}BusinessDelegate parentDelegate = ${className}BusinessDelegate.get${className}Instance();
			Set<${childName}> children					= ${lowercaseClassName}.get${roleName}();
			${childName} child 							= null;
			
			for( Long id : getChildIds() ) {
				try {
					pk = new ${childName}PrimaryKey( id );
					
					// first remove the relevant child from the list
					child = childDelegate.get${childName}( pk );
					children.remove( child );
					
					// then safe to delete the child				
					childDelegate.delete( pk );
				}
				catch( Exception exc ) {
					LOGGER.severe( "${className}Action:delete${roleName}() failed - " + exc.getMessage() );
				}
			}
			
			// assign the modified list of ${childName} back to the ${lowercaseClassName}
			${lowercaseClassName}.set${roleName}( children );			
			// save it 
			parentDelegate.save${className}( ${lowercaseClassName} );
		}
		
		return NONE;
	}

	#end
	

//************************************************************************    
// Attributes
//************************************************************************
    
	protected List<$className> ${lowercaseClassName}List 	= null;
	protected $className $lowercaseClassName 				= null;
	
    private static final Logger LOGGER = Logger.getLogger(${classObject.getName()}.class.getName());
}


