#header()
package ${aib.getRootPackageName(true)}.#getDelegatePackageName();

import java.util.*;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import ${aib.getRootPackageName(true)}.exception.*;

import ${aib.getRootPackageName()}.#getPrimaryKeyPackageName().*;
#set( $imports = [ "#getPrimaryKeyPackageName()", "#getDAOPackageName()", "#getBOPackageName()"] )
#importStatements( $imports )
import ${aib.getRootPackageName()}.#getDelegatePackageName().*;

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

// AIB : \#delegateCRUDMethodsForDAO()
#delegateCRUDMethodsForDAO()
// ~AIB

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
#getBusinessMethodImplementations( $classObject.getName() $classObject $classObject.getBusinessMethods() $classObject.getInterfaces() )
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



