#header()
#set( $className = $classObject.getName() )
package ${aib.getRootPackageName(true)}.#getDelegatePackageName();

import java.util.*;
import java.util.HashMap;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

#set( $imports = [ "#getPrimaryKeyPackageName()", "#getDAOPackageName()", "#getBOPackageName()"] )
#importStatements( $imports )

import ${aib.getRootPackageName()}.exception.*;

/**
 * ${classObject.getName()} business delegate class.
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
#if ( $classObject.isAbstract() == false )
public class ${classObject.getName()}BusinessDelegate 
#else
public abstract class ${classObject.getName()}BusinessDelegate 
#end
#if ( $classObject.hasParent() == true )
extends ${classObject.getParentName()}BusinessDelegate
#else
extends BaseBusinessDelegate
#end
{
//************************************************************************
// Public Methods
//************************************************************************
    /** 
     * Default Constructor 
     */
    public ${classObject.getName()}BusinessDelegate() 
	{
	}

#if ( $classObject.isAbstract() == false )
        
   /**
	* ${className} Business Delegate Factory Method
	*
	* Returns a singleton instance of ${className}BusinessDelegate().
	* All methods are expected to be self-sufficient.
	*
	* @return 	${className}BusinessDelegate
	*/
	public static ${className}BusinessDelegate get${className}Instance()
	{
	    if ( singleton == null )
	    {
	    	singleton = new ${className}BusinessDelegate();
	    }
	    
	    return( singleton );
	}
 
    /**
     * Method to retrieve the ${className} via an ${className}PrimaryKey.
     * @param 	key
     * @return 	${className}
     * @exception ProcessingException - Thrown if processing any related problems
     * @exception IllegalArgumentException 
     */
    public ${className} get${className}( ${className}PrimaryKey key ) 
    throws ProcessingException, IllegalArgumentException
    {
    	String msgPrefix = "${className}BusinessDelegate:get${className} - ";
        if ( key == null )
        {
            String errMsg = msgPrefix + "null key provided.";
            LOGGER.warning( errMsg );
            throw new IllegalArgumentException( errMsg );
        }
        
        ${className} returnBO = null;
                
        ${className}DAO dao = get${className}DAO();
        
        try
        {
            returnBO = dao.find${className}( key );
        }
        catch( Exception exc )
        {
            String errMsg = "${className}BusinessDelegate:get${className}( ${className}PrimaryKey key ) - unable to locate ${className} with key " + key.toString() + " - " + exc;
            LOGGER.warning( errMsg );
            throw new ProcessingException( errMsg );
        }
        finally
        {
            release${className}DAO( dao );
        }        
        
        return returnBO;
    }


    /**
     * Method to retrieve a collection of all ${className}s
     *
     * @return 	ArrayList<${className}> 
     * @exception ProcessingException Thrown if any problems
     */
    public ArrayList<${className}> getAll${className}() 
    throws ProcessingException
    {
    	String msgPrefix				= "${className}BusinessDelegate:getAll${className}() - ";
        ArrayList<${className}> array	= null;
        
        ${className}DAO dao = get${className}DAO();
    
        try
        {
            array = dao.findAll${className}();
        }
        catch( Exception exc )
        {
            String errMsg = msgPrefix + exc;
            LOGGER.warning( errMsg );
            throw new ProcessingException( errMsg );
        }
        finally
        {
            release${className}DAO( dao );
        }        
        
        return array;
    }

   /**
    * Creates the provided BO.
    * @param		businessObject 	${className}
    * @return       ${className}
    * @exception    ProcessingException
    * @exception	IllegalArgumentException
    */
	public ${className} create${className}( ${className} businessObject )
    throws ProcessingException, IllegalArgumentException
    {
		String msgPrefix = "${className}BusinessDelegate:create${className} - ";
		
		if ( businessObject == null )
        {
            String errMsg = msgPrefix + "null businessObject provided";
            LOGGER.warning( errMsg );
            throw new IllegalArgumentException( errMsg );
        }
        
        // return value once persisted
        ${className}DAO dao  = get${className}DAO();
        
        try
        {
            businessObject = dao.create${className}( businessObject );
        }
        catch (Exception exc)
        {
            String errMsg = "${className}BusinessDelegate:create${className}() - Unable to create ${className}" + exc;
            LOGGER.warning( errMsg );
            throw new ProcessingException( errMsg );
        }
        finally
        {
            release${className}DAO( dao );
        }        
        
        return( businessObject );
        
    }

   /**
    * Saves the underlying BO.
    * @param		businessObject		${className}
    * @return       what was just saved
    * @exception    ProcessingException
    * @exception  	IllegalArgumentException
    */
    public ${className} save${className}( ${className} businessObject ) 
    throws ProcessingException, IllegalArgumentException
    {
    	String msgPrefix = "${className}BusinessDelegate:save${className} - ";
    	
		if ( businessObject == null )
        {
            String errMsg = msgPrefix + "null businessObject provided.";
            LOGGER.warning( errMsg );
            throw new IllegalArgumentException( errMsg );
        }
                
        // --------------------------------
        // If the businessObject has a key, find it and apply the businessObject
        // --------------------------------
        ${className}PrimaryKey key = businessObject.get${className}PrimaryKey();
                    
        if ( key != null )
        {
            ${className}DAO dao = get${className}DAO();

            try
            {                    
                businessObject = (${className})dao.save${className}( businessObject );
            }
            catch (Exception exc)
            {
                String errMsg = "${className}BusinessDelegate:save${className}() - Unable to save ${className}" + exc;
                LOGGER.warning( errMsg );
                throw new ProcessingException( errMsg  );
            }
            finally
            {
                release${className}DAO( dao );
            }
            
        }
        else
        {
            String errMsg = "${className}BusinessDelegate:save${className}() - Unable to create ${className} due to it having a null ${className}PrimaryKey."; 
            
            LOGGER.warning( errMsg );
            throw new ProcessingException( errMsg );
        }
		        
        return( businessObject );
        
    }
   
   /**
    * Deletes the associatied value object using the provided primary key.
    * @param		key 	${className}PrimaryKey    
    * @exception 	ProcessingException
    */
    public void delete( ${className}PrimaryKey key ) 
    throws ProcessingException, IllegalArgumentException
    {    	
    	String msgPrefix = "${className}BusinessDelegate:save${className} - ";
    	
		if ( key == null )
        {
            String errMsg = msgPrefix + "null key provided.";
            LOGGER.warning( errMsg );
            throw new IllegalArgumentException( errMsg );
        }
        
        ${className}DAO dao  = get${className}DAO();

        try
        {                    
            dao.delete${className}( key );
        }
        catch (Exception exc)
        {
            String errMsg = msgPrefix + "Unable to delete ${className} using key = "  + key + ". " + exc;
            LOGGER.warning( errMsg );
            throw new ProcessingException( errMsg );
        }
        finally
        {
            release${className}DAO( dao );
        }
        		
        return;
    }
        
#end

// business methods
    /**
     * Returns the ${classObject.getName()} specific DAO.
     *
     * @return      ${classObject.getName()} DAO
     */
    public ${classObject.getName()}DAO get${classObject.getName()}DAO()
    {
        return( new ${aib.getRootPackageName(true)}.#getDAOPackageName().${classObject.getName()}DAO() ); 
    }

    /**
     * Release the ${classObject.getName()}DAO back to the FrameworkDAOFactory
     */
    public void release${classObject.getName()}DAO( ${aib.getRootPackageName(true)}.#getDAOPackageName().${classObject.getName()}DAO dao )
    {
        dao = null;
    }

// AIB : \#getBusinessMethodImplementations( \$classObject.getName() \$classObject \$classObject.getBusinessMethods() \$classObject.getInterfaces() )
##getBusinessMethodImplementations( $classObject.getName() $classObject $classObject.getBusinessMethods() $classObject.getInterfaces() )
// ~AIB
     
//************************************************************************
// Attributes
//************************************************************************

   /**
    * Singleton instance
    */
    protected static ${classObject.getName()}BusinessDelegate singleton = null;
    private static final Logger LOGGER = Logger.getLogger(${classObject.getName()}.class.getName());
    
}



