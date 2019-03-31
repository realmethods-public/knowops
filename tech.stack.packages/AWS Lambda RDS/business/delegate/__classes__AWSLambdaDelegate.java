#header()
#set( $className = $classObject.getName() )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter( ${className} )} )
package ${aib.getRootPackageName(true)}.#getDelegatePackageName();

import java.util.*;
import java.io.IOException;

//import java.util.logging.Level;
//import java.util.logging.Logger;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.*;

import io.swagger.annotations.*;

import com.amazonaws.services.lambda.runtime.Context;

#set( $imports = [ "#getPrimaryKeyPackageName()", "#getBOPackageName()"] )
#importStatements( $imports )

import ${aib.getRootPackageName()}.exception.CreationException;
import ${aib.getRootPackageName()}.exception.DeletionException;
import ${aib.getRootPackageName()}.exception.NotFoundException;
import ${aib.getRootPackageName()}.exception.SaveException;

/**
 * ${classObject.getName()} AWS Lambda Proxy delegate class.
 * <p>
 * This class implements the Business Delegate design pattern for the purpose of:
 * <ol>
 * <li>Reducing coupling between the business tier and a client of the business tier by hiding all business-tier implementation details</li>
 * <li>Improving the available of ${classObject.getName()} related services in the case of a ${classObject.getName()} business related service failing.</li>
 * <li>Exposes a simpler, uniform ${classObject.getName()} interface to the business tier, making it easy for clients to consume a simple Java object.</li>
 * <li>Hides the communication protocol that may be required to fulfill ${classObject.getName()} business related services.</li>
 * </ol>
 * <p>
 * @author ${aib.getAuthor()}
 */
@Api(value = "${className}", description = "RESTful API to interact with ${className} resources.")
@Path("/${className}")
#set( $fullClassName = "${classObject.getName()}AWSLambdaDelegate" )
#if ( $classObject.isAbstract() == false )
public class $fullClassName 
#else
public abstract class $fullClassName 
#end
#if ( $classObject.hasParent() == true )
extends ${classObject.getParentName()}AWSLambdaDelegate
#else
extends BaseAWSLambdaDelegate
#end{
//************************************************************************
// Public Methods
//************************************************************************
    /** 
     * Default Constructor 
     */
    public ${fullClassName}() {
	}


    /**
     * Creates the provided ${className}
     * @param		businessObject 	${className}
	 * @param		context		Context	
     * @return     	${className}
     * @exception   CreationException
     */
    @ApiOperation(value = "Creates a ${className}", notes = "Creates ${className} using the provided data" )
    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    public static ${className} create${className}( 
    		@ApiParam(value = "${className} entity to create", required = true) ${className} businessObject, 
    		Context context ) 
    	throws CreationException {
    	
		if ( businessObject == null )
        {
            String errMsg = "Null ${className} provided but not allowed " + getContextDetails(context);
            context.getLogger().log( errMsg );
            throw new CreationException( errMsg ); 
        }
      
        try
        {
            String actionName 	= "save";
            String result 		= call(PACKAGE_NAME, actionName, businessObject);
            businessObject 		= (${className})fromJson(result, ${className}.class);
        }
        catch (Exception exc)
        {
        	String errMsg = "${className}AWSLambdaDelegate:create${className}() - Unable to create ${className}" + getContextDetails(context) + exc;
        	context.getLogger().log( errMsg );
            throw new CreationException( errMsg );
        }
        finally
        {
        }        
         
        return( businessObject );
         
     }

    /**
     * Method to retrieve the ${className} via a supplied ${className}PrimaryKey.
     * @param 	key
	 * @param	context		Context
     * @return 	${className}
     * @exception NotFoundException - Thrown if processing any related problems
     */
    @ApiOperation(value = "Gets a ${className}", notes = "Gets the ${className} associated with the provided primary key", response = ${className}.class)
    @GET
    @Path("/find")
    @Produces(MediaType.APPLICATION_JSON)    
    public static ${className} get${className}( 
    		@ApiParam(value = "${className} primary key", required = true) ${className}PrimaryKey key, 
    		Context context  ) 
    	throws NotFoundException {
        
        ${className} businessObject  = null;                
        
        try
        {
            String actionName 	= "load";
            String result 		= call(PACKAGE_NAME, actionName, key);
            businessObject 		= (${className})fromJson(result, ${className}.class);
        }
        catch( Exception exc )
        {
            String errMsg = "Unable to locate ${className} with key " + key.toString() + " - " + getContextDetails(context) + exc;
            context.getLogger().log( errMsg );
            throw new NotFoundException( errMsg );
        }
        finally
        {
        }        
        
        return businessObject;
    }
     
   /**
    * Saves the provided ${className}
    * @param		businessObject		${className}
	* @param		context		Context	
    * @return       what was just saved
    * @exception    SaveException
    */
    @ApiOperation(value = "Saves a ${className}", notes = "Saves ${className} using the provided data" )
    @PUT
    @Path("/save")
    @Consumes(MediaType.APPLICATION_JSON)
    public static ${className} save${className}( 
    		@ApiParam(value = "${className} entity to save", required = true) ${className} businessObject, Context context  ) 
    	throws SaveException {

    	if ( businessObject == null )
        {
            String errMsg = "Null ${className} provided but not allowed " + getContextDetails(context);
            context.getLogger().log( errMsg );
            throw new SaveException( errMsg ); 
        }
    	
        // --------------------------------
        // If the businessObject has a key, find it and apply the businessObject
        // --------------------------------
        ${className}PrimaryKey key = businessObject.get${className}PrimaryKey();
                    
        if ( key != null )
        {
            try
            {                    
                String actionName 	= "save";
                String result 		= call(PACKAGE_NAME, actionName, businessObject);
                businessObject 		= (${className})fromJson(result, ${className}.class);
            }
            catch (Exception exc)
            {
                String errMsg = "Unable to save ${className}" + getContextDetails(context) + exc;
                context.getLogger().log( errMsg );
                throw new SaveException( errMsg );
            }
            finally
            {
            }
        }
        else
        {
            String errMsg = "Unable to create ${className} due to it having a null ${className}PrimaryKey."; 
            context.getLogger().log( errMsg );
            throw new SaveException( errMsg );
        }
		        
        return( businessObject );
        
    }
     

	/**
     * Method to retrieve a collection of all ${className}s
     * @param		context		Context
     * @return 	ArrayList<${className}> 
     */
    @ApiOperation(value = "Get all ${className}", notes = "Get all ${className} from storage", responseContainer = "ArrayList", response = ${className}.class)
    @GET
    @Path("/getAll")
    @Produces(MediaType.APPLICATION_JSON)    
    public static ArrayList<${className}> getAll${className}( Context context ) 
    	throws NotFoundException {

        ArrayList<${className}> array	= null;
    
        try
        {
            String actionName = "viewAll";
            String result = call(PACKAGE_NAME, actionName, null);
            array = (ArrayList<${className}>) fromJson(result, ArrayList.class);
        }
        catch( Exception exc )
        {
            String errMsg = "failed to getAll${className} - " + getContextDetails(context) + exc.getMessage();
            context.getLogger().log( errMsg );
            throw new NotFoundException( errMsg );
        }
        finally
        {
        }        
        
        return array;
    }
           
     
    /**
     * Deletes the associated business object using the provided primary key.
     * @param		key 	${className}PrimaryKey
     * @param		context		Context    
     * @exception 	DeletionException
     */
    @ApiOperation(value = "Deletes a ${className}", notes = "Deletes the ${className} associated with the provided primary key", response = ${className}.class)
    @DELETE
    @Path("/delete")
    @Consumes(MediaType.APPLICATION_JSON)    
	public static void delete${className}( 
			@ApiParam(value = "${className} primary key", required = true) ${className}PrimaryKey key, 
			Context context  ) 
    	throws DeletionException {    	

    	if ( key == null )
        {
            String errMsg = "Null key provided but not allowed " + getContextDetails(context) ;
            context.getLogger().log( errMsg );
            throw new DeletionException( errMsg );
        }

        try
        {                    
            String actionName = "delete";
            String result = call(PACKAGE_NAME, actionName, key);
        }
        catch (Exception exc)
        {
        	String errMsg = "Unable to delete ${className} using key = "  + key + ". " + getContextDetails(context) + exc;
        	context.getLogger().log( errMsg );
            throw new DeletionException( errMsg );
        }
        finally
        {
        }
         		
        return;
     }

// role related methods
#set( $includeComposites = false )

#foreach( $singleAssociation in $classObject.getSingleAssociations( ${includeComposites} ) )
#set( $roleName = $singleAssociation.getRoleName() )
#set( $childName = $singleAssociation.getType() )
#set( $parentName  = $classObject.getName() )
    /**
     * Gets the ${roleName} using the provided primary key of a ${className}
     * @param		parentKey	${className}PrimaryKey
     * @return    	${childName}
     * @exception	NotFoundException
     */
	public static $childName get${roleName}( ${className}PrimaryKey parentKey, Context context )
		throws NotFoundException {
		
		$className $lowercaseClassName 	= get${className}( parentKey, context );
		${childName}PrimaryKey childKey = ${lowercaseClassName}.get${roleName}().get${childName}PrimaryKey(); 
		${childName} child 				= ${childName}AWSLambdaDelegate.get${childName}( childKey, context );
		
		return( child );
	}

    /**
     * Assigns the ${roleName} on a ${className} using the provided primary key of a ${childName}
     * @param		parentKey	${className}PrimaryKey
     * @param		parentKey	${className}PrimaryKey
     * @param		context		Context
     * @return    	${className}
     * @exception	SaveException
     * @exception	NotFoundException
     */
	public static ${className} save${roleName}( ${className}PrimaryKey parentKey, 
												${childName}PrimaryKey childKey,
												Context context)
		throws SaveException, NotFoundException {
		
		$className $lowercaseClassName 	= get${className}( parentKey, context );
		${childName} child 				= ${childName}AWSLambdaDelegate.get${childName}( childKey, context );

		// assign the ${roleName}
		${lowercaseClassName}.set${roleName}( child );
	
		// save the ${className} 
		${lowercaseClassName} = ${className}AWSLambdaDelegate.save${className}( ${lowercaseClassName}, context );
		
		return( $lowercaseClassName );
	}

    /**
     * Unassigns the ${roleName} on a ${className}
     * @param		parentKey	${className}PrimaryKey
     * @param		Context		context
     * @return    	${className}
     * @exception	SaveException
     * @exception	NotFoundException
	 * @exception	SaveException	
     */
	public static ${className} delete${roleName}( ${className}PrimaryKey parentKey, Context context )
	throws DeletionException, NotFoundException, SaveException {

		$className $lowercaseClassName 	= get${className}( parentKey, context );
		
		if ( ${lowercaseClassName}.get${roleName}() != null )
		{
			${childName}PrimaryKey pk = ${lowercaseClassName}.get${roleName}().get${childName}PrimaryKey();
			
			// first null out the ${childName} on the parent so there's no constraint during deletion
			${lowercaseClassName}.set${roleName}( null );
			${className}AWSLambdaDelegate.save${className}( ${lowercaseClassName}, context );
			
			// now it is safe to delete the ${roleName} 
			${childName}AWSLambdaDelegate.delete${childName}( pk, context );
		}

		return( $lowercaseClassName );
	}
		
#end ##foreach( $singleAssociation in $classObject.getSingleAssociations( ${includeComposites} ) )

#foreach( $multiAssociation in $classObject.getMultipleAssociations() )
#set( $roleName = $multiAssociation.getRoleName() )
#set( $childName = $multiAssociation.getType() )
#set( $parentName  = $classObject.getName() )
    /**
     * Retrieves the ${roleName} on a ${className}
     * @param		parentKey	${className}PrimaryKey
     * @param		context		Context
     * @return    	Set<${childName}>
     * @exception	NotFoundException
     */
	public static Set<${childName}> get${roleName}( ${className}PrimaryKey parentKey, Context context )
		throws NotFoundException
    {
    	$className $lowercaseClassName 	= get${className}( parentKey, context );
    	return (${lowercaseClassName}.get${roleName}());
    }
    
    /**
     * Add the assigned ${childName} into the ${roleName} of the relevant ${className}
     * @param		parentKey	${className}PrimaryKey
     * @param		childKey	${childName}PrimaryKey
	 * @param		context		Context
     * @return    	${className}
     * @exception	NotFoundException
     */
	public static ${className} add${roleName}( 	${className}PrimaryKey parentKey, 
									${childName}PrimaryKey childKey, 
									Context context )
	throws SaveException, NotFoundException
	{
		$className $lowercaseClassName 	= get${className}( parentKey, context );

		// find the ${childName}
		${childName} child = ${childName}AWSLambdaDelegate.get${childName}( childKey, context );
		
		// add it to the ${roleName} 
		${lowercaseClassName}.get${roleName}().add( child );				
		
		// save the ${className}
		${lowercaseClassName} = ${className}AWSLambdaDelegate.save${className}( ${lowercaseClassName}, context );

		return ( ${lowercaseClassName} );
	}

    /**
     * Saves multiple ${childName} entities as the ${roleName} to the relevant ${className}
     * @param		parentKey	${className}PrimaryKey
     * @param		List<${childName}PrimaryKey> childKeys
     * @return    	${className}
     * @exception	SaveException
     * @exception	NotFoundException
     */
	public ${className} assign${roleName}( ${className}PrimaryKey parentKey, 
											List<${childName}PrimaryKey> childKeys, 
											Context context )
		throws SaveException, NotFoundException {

		$className $lowercaseClassName 	= get${className}( parentKey, context );
		
		// clear out the ${roleName} 
		${lowercaseClassName}.get${roleName}().clear();
		
		// finally, find each child and add
		if ( childKeys != null )
		{
			${childName} child = null;
			for( ${childName}PrimaryKey childKey : childKeys )
			{
				// retrieve the ${childName}
				child = ${childName}AWSLambdaDelegate.get${childName}( childKey, context );

				// add it to the ${roleName} List
				${lowercaseClassName}.get${roleName}().add( child );
			}
		}
		
		// save the ${className}
		${lowercaseClassName} = ${className}AWSLambdaDelegate.save${className}( ${lowercaseClassName}, context );

		return( ${lowercaseClassName} );
	}

    /**
     * Delete multiple ${childName} entities as the ${roleName} to the relevant ${className}
     * @param		parentKey	${className}PrimaryKey
     * @param		List<${childName}PrimaryKey> childKeys
     * @return    	${className}
     * @exception	DeletionException
     * @exception	NotFoundException
     * @exception	SaveException
     */
	public ${className} delete${roleName}( ${className}PrimaryKey parentKey, 
											List<${childName}PrimaryKey> childKeys, 
											Context context )
		throws DeletionException, NotFoundException, SaveException {		
		$className $lowercaseClassName 	= get${className}( parentKey, context );

		if ( childKeys != null )
		{
			Set<${childName}> children	= ${lowercaseClassName}.get${roleName}();
			${childName} child 			= null;
			
			for( ${childName}PrimaryKey childKey : childKeys )
			{
				try
				{
					// first remove the relevant child from the list
					child = ${childName}AWSLambdaDelegate.get${childName}( childKey, context );
					children.remove( child );
					
					// then safe to delete the child				
					${childName}AWSLambdaDelegate.delete${childName}( childKey, context );
				}
				catch( Exception exc )
				{
					String errMsg = "Deletion failed - " + exc.getMessage();
					context.getLogger().log( errMsg );
					throw new DeletionException( errMsg );
				}
			}
			
			// assign the modified list of ${childName} back to the ${lowercaseClassName}
			${lowercaseClassName}.set${roleName}( children );			
			// save it 
			${lowercaseClassName} = ${className}AWSLambdaDelegate.save${className}( ${lowercaseClassName}, context );
		}
		
		return ( ${lowercaseClassName} );
	}

#end ##foreach( $multiAssociation in $classObject.getMultipleAssociations() )
		
//************************************************************************
// Attributes
//************************************************************************

//    private static final Logger LOGGER = Logger.getLogger(${fullClassName}.class.getName());
	private static final String PACKAGE_NAME = "${className}";
}

