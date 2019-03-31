#header()
#set( $className = $classObject.getName() )
#set( $lowercaseClassName = $Utils.lowercaseFirstLetter( $className ) )

package ${aib.getRootPackageName(true)}.#getDAOPackageName();

import java.util.*;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.couchbase.client.java.*;
import com.couchbase.client.java.document.*;
import com.couchbase.client.java.document.json.*;
import com.couchbase.client.java.query.*;

import ${aib.getRootPackageName(true)}.exception.*;

#set( $imports = [ "#getPrimaryKeyPackageName()", "#getBOPackageName()", "#getDAOPackageName()" ] )
#importStatements( $imports )

import ${aib.getRootPackageName()}.#getDAOPackageName().*;
/** 
 * Implements the couchbase nosql persistence processing for business entity ${className}.
 *
 * @author $aib.getAuthor()
 */
#set( $parentName = "" )        
#if( $classObject.hasParent() == true )    
#set( $parentName = "${parentName}${classObject.getParentName()}DAO" )   
##set( $parentName = "${parentName}BaseDAO" )
#else ## no parent, so come directly off of the FrameworkBusinessObject
#set( $parentName = "${parentName}BaseDAO" )		
#end
public class ${classObject.getName()}DAO extends $parentName
{
    /**
     * default constructor
     */
    public ${className}DAO()
    {
    }

#if ( $aib.hasIdentity( $classObject ) == true )		


   /**
    * Removes the associated businessObject the persistent store.
    * Delegates internally to delegate${className}
    * @param        key		${className}PrimaryKey
    * @return		T/F success of delete operation
    */
    public boolean delete( ${className}PrimaryKey key )
    {
        boolean deleted = true;

        try
        {
        	delete${className}( (${className}PrimaryKey) key );
        }
        catch (Exception exc)
        {
            deleted = false;
        }
        return (deleted);
    }

//*****************************************************
// CRUD methods
//*****************************************************

    /**
     * Retrieves a ${className} from the persistent store, using the provided primary key. 
     * If no match is found, a null ${className} is returned.
     * <p>
     * @param       pk
     * @return      ${className}
     * @exception   FrameworkDAOException
     */
    public ${className} find${className}( ${className}PrimaryKey pk ) 
    throws FrameworkDAOException
    {
        if (pk == null)
        {
            throw new FrameworkDAOException("${className}DAO.find${className}(...) cannot have a null primary key argument");
        }
    
		${className} businessObject = null;
		Bucket bucket 				= getBucket();
		
	    StringBuilder fromClause = new StringBuilder( "select * from ${className} use keys ${className}" );
		
        try
        {
        	String key			= "${lowercaseClassName}::" + pk.getFirstKey().toString();
        	JsonDocument doc	= bucket.get( key );
        	String json			= doc.content().toString();
        	businessObject		= (${className})(fromJson( json, ${className}.class ));
		}
		catch( Throwable exc )
		{
			businessObject = null;
			exc.printStackTrace();
			throw new FrameworkDAOException( "${className}DAO.find${className} failed for primary key " + pk + " - " + exc );		
		}		
		finally
		{
			releaseBucket( bucket );
		}		    
		
        return( businessObject );
    }
    
    /**
     * Inserts a new ${classObject.Name} into the persistent store.
     * @param       businessObject
     * @return      newly persisted ${className}
     * @exception   FrameworkDAOException
     */
    public ${className} create${className}( ${className} businessObject )
    throws FrameworkDAOException
    {
    	Bucket bucket = getBucket();
    	
    	try
    	{
            long id = 0;
            String counterId = "counter::${className}";

            try
            {
                long delta = 1;
                id = getBucket().counter(counterId, delta, id).content();
            }
            catch (Exception exc)
            {
                bucket.upsert(toDocument(counterId));
            }

    		businessObject.set${className}ID( id );
    		
    		JsonDocument doc = toDocument( businessObject );
    		
    		if ( doc != null )
	    		bucket.upsert( doc );
    	}
		catch( Throwable exc )
		{
		}		
		finally
		{
			releaseBucket( bucket );			
		}		
		
        // return the businessObject
        return(  businessObject );
    }
    
    /**
     * Stores the provided ${className} to the persistent store.
     *
     * @param       businessObject
     * @return      ${className}	stored entity
     * @exception   FrameworkDAOException 
     */
    public ${className} save${className}( ${className} businessObject )
    throws FrameworkDAOException
    {
    	Bucket bucket = getBucket();
    	
    	try
    	{
    		JsonDocument doc = toDocument( businessObject );

            // apply to the bucket
    		if ( doc != null )
    			bucket.upsert( doc );
            
		}
		catch( Throwable exc )
		{
		}		
		finally
		{
			releaseBucket( bucket );
		}		    

    	return( businessObject );
    }
    
    /**
    * Removes a ${className} from the persistent store.
    *
    * @param        pk		identity of object to remove
    * @exception    FrameworkDAOException
    */
    public void delete${className}( ${className}PrimaryKey pk ) 
    throws FrameworkDAOException 
    {
    	Bucket bucket = getBucket();
    	
    	try
    	{
            String identity 	= "${lowercaseClassName}::" + pk.getFirstKey().toString();
            JsonDocument doc	= bucket.get( identity );
    		bucket.remove(doc);
		}
		catch( Throwable exc )
		{
			exc.printStackTrace();			
			throw new FrameworkDAOException( "${className}DAO.delete${className} failed - " + exc );					
		}		
		finally
		{
			releaseBucket( bucket );
		}
    }

    /**
     * returns a Collection of all ${className}s
     * @return		ArrayList<${className}>
     * @exception   FrameworkDAOException
     */
    public ArrayList<${className}> findAll${className}()
    throws FrameworkDAOException
    {
		ArrayList<${className}> list 	= new ArrayList<${className}>();
		Bucket bucket 					= getBucket();
		N1qlQueryResult result			= null;
		try
		{
			String query 	= "select ${aib.getParam("couchbase.bucket")}.* from `${aib.getParam("couchbase.bucket")}` where ${lowercaseClassName}ID >= 0";			
			result			= bucket.query(N1qlQuery.simple(query));
			
			for (N1qlQueryRow row : result.allRows()) 
			{
				list.add((${className}) fromJson( row.value().toString(), ${className}.class ) );
			}
		}
		catch( Throwable exc )
		{
			if ( result != null )
				System.out.println( "${className}DAO.findAll() errors - " + result.errors() );
			throw new FrameworkDAOException( "${className}DAO.findAll${className} failed - " + exc );		
		}		
		finally
		{
			releaseBucket( bucket );
		}
		
		if ( list.size() <= 0 )
		{
			LOGGER.info( "${className}DAO:findAll${className}s() - List is empty.");
		}
        
		return( list );		        
    }
	
    		
#end ##if ( $aib.hasIdentity( $classObject ) == true )		


// AIB : \#outputDAOFindAllImplementations()
##outputDAOFindAllImplementations()
// ~AIB


//*****************************************************
// Attributes
//*****************************************************
    private static final Logger LOGGER = Logger.getLogger(${classObject.getName()}.class.getName());

}


