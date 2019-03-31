#header()
package ${aib.getRootPackageName(true)}.#getDAOPackageName();

import java.util.*;
import java.util.logging.Logger;

import java.sql.*;

import org.hibernate.*;
import org.hibernate.cfg.*;

import ${aib.getRootPackageName()}.exception.ProcessingException;

#set( $imports = [ "#getPrimaryKeyPackageName()", "#getBOPackageName()" ] )
#importStatements( $imports )

import ${aib.getRootPackageName()}.#getDAOPackageName().*;

/** 
 * Implements the Hibernate persistence processing for business entity ${classObject.getName()}.
 *
 * @author $aib.getAuthor()
 */

// AIB : \#getDAOClassDecl()
#getDAOClassDecl()
// ~AIB
{
    /**
     * default constructor
     */
    public ${classObject.getName()}DAO()
    {
    }

    /**
     * Retrieves a ${classObject.getName()} from the persistent store, using the provided primary key. 
     * If no match is found, a null ${classObject.getName()} is returned.
     * <p>
     * @param       pk
     * @return      ${classObject.getName()}
     * @exception   ProcessingException
     */
    public ${classObject.getName()} find${classObject.getName()}( ${classObject.getName()}PrimaryKey pk ) 
    throws ProcessingException
    {
        if (pk == null)
        {
            throw new ProcessingException("${classObject.getName()}DAO.find${classObject.getName()}(...) cannot have a null primary key argument");
        }
    
		Query query 	= null;		
		${classObject.getName()} businessObject = null;
		
	    StringBuilder fromClause = new StringBuilder( "from ${aib.getRootPackageName(true)}.#getBOPackageName().${classObject.getName()} as ${classObject.getName().toLowerCase()} where " );
		
        Session session = null;
        Transaction tx = null;

        try
        {
            session = currentSession();
            tx      = currentTransaction(session);
            
// AIB : \#getHibernateFindFromClause()
#getHibernateFindFromClause()
// ~AIB

	 		query = session.createQuery( fromClause.toString() );
	 		
	 		if ( query != null )
	 		{
                businessObject = new ${classObject.getName()}();
                businessObject.copy((${classObject.getName()})query.list().iterator().next());
	 		}

			commitTransaction(tx);						
		}
		catch( Throwable exc )
		{
			businessObject = null;
			exc.printStackTrace();
			throw new ProcessingException( "${classObject.getName()}DAO.find${classObject.getName()} failed for primary key " + pk + " - " + exc );		
		}		
		finally
		{
			try
			{
				closeSession();			
			}
			catch( Throwable exc )
			{		
				LOGGER.info( "${classObject.getName()}DAO.find${classObject.getName()} - Hibernate failed to close the Session - " + exc );
			}
		}		    
				
        return( businessObject );
    }
        
##if ( $aib.hasIdentity( $classObject ) == true )		
	    /**
	     * returns a Collection of all ${classObject.getName()}s
	     * @return		ArrayList<${classObject.getName()}>
	     * @exception   ProcessingException
	     */
	    public ArrayList<${classObject.getName()}> findAll${classObject.getName()}()
	    throws ProcessingException
	    {
			ArrayList<${classObject.getName()}> list = new ArrayList<${classObject.getName()}>();
			ArrayList<${classObject.getName()}> refList = new ArrayList<${classObject.getName()}>();
			Query query 							= null;		
			StringBuilder buf 						= new StringBuilder( "from ${aib.getRootPackageName(true)}.#getBOPackageName().${classObject.getName()}" );

			try
			{
				Session session = currentSession();

		 		query = session.createQuery( buf.toString() );
		 		
				if ( query != null )
				{
					list = (ArrayList<${classObject.getName()}>)query.list();
					${classObject.getName()} tmp = null;
					
	                for (${classObject.getName()} listEntry : list)
	                {
	                    tmp = new ${classObject.getName()}();
	                    tmp.copyShallow(listEntry);
	                    refList.add(tmp);
	                }
				}
			}
			catch( Throwable exc )
			{
				exc.printStackTrace();		
				throw new ProcessingException( "${classObject.getName()}DAO.findAll${classObject.getName()} failed - " + exc );		
			}		
			finally
			{
				try
				{
					closeSession();			
				}
				catch( Throwable exc )
				{		
					LOGGER.info( "${classObject.getName()}DAO.findAll${classObject.getName()} - Hibernate failed to close the Session - " + exc );
				}		
			}
			
			if ( list.size() <= 0 )
			{
				LOGGER.info( "${classObject.getName()}DAO:findAll${classObject.getName()}s() - List is empty.");
			}
	        
			return( refList );		        
	    }
		

	    /**
	     * Inserts a new ${classObject.Name} into the persistent store.
	     * @param       businessObject
	     * @return      newly persisted ${classObject.getName()}
	     * @exception   ProcessingException
	     */
	    public ${classObject.getName()} create${classObject.getName()}( ${classObject.getName()} businessObject )
	    throws ProcessingException
	    {
		    Transaction tx 	= null;
			Session session	= null;
			
	    	try
	    	{    				
				session	= currentSession();
				tx 		= currentTransaction( session );
		
				session.save( businessObject );
				commitTransaction( tx );	
				
			}
			catch( Throwable exc )
			{
				try
				{
					if ( tx != null )
						rollbackTransaction( tx );				
				}
				catch( Throwable exc1 )
				{
					LOGGER.info( "${classObject.getName()}DAO.create${classObject.getName()} - Hibernate failed to rollback - " + exc1 );
				}
				exc.printStackTrace();			
				throw new ProcessingException( "${classObject.getName()}DAO.create${classObject.getName()} failed - " + exc );
			}		
			finally
			{
				try
				{
					session.flush();
					closeSession();			
				}
				catch( Throwable exc )
				{		
					LOGGER.info( "${classObject.getName()}DAO.create${classObject.getName()} - Hibernate failed to close the Session - " + exc );
				}
			}		
			
	        // return the businessObject
	        return(  businessObject );
	    }
	    
    /**
     * Stores the provided ${classObject.getName()} to the persistent store.
     *
     * @param       businessObject
     * @return      ${classObject.getName()}	stored entity
     * @exception   ProcessingException 
     */
    public ${classObject.getName()} save${classObject.getName()}( ${classObject.getName()} businessObject )
    throws ProcessingException
    {
		Transaction tx 	= null;
		Session session = null;
		
    	try
    	{
			session = currentSession();
			tx		= currentTransaction( session );
	
			session.update( businessObject );
			commitTransaction( tx );	
		}
		catch( Throwable exc )
		{
			try
			{
				if ( tx != null )
					rollbackTransaction( tx );
			}
			catch( Throwable exc1 )
			{
				LOGGER.info( "${classObject.getName()}DAO.save${classObject.getName()} - Hibernate failed to rollback - " + exc1 );
			}
			exc.printStackTrace();			
			throw new ProcessingException( "${classObject.getName()}DAO.save${classObject.getName()} failed - " + exc );		
		}		
		finally
		{
			try
			{
				session.flush();
				closeSession();			
			}
			catch( Throwable exc )
			{		
				LOGGER.info( "${classObject.getName()}DAO.save${classObject.getName()} - Hibernate failed to close the Session - " + exc );
			}
		}		    
        return( businessObject );
    }
    
    /**
    * Removes a ${classObject.getName()} from the persistent store.
    *
    * @param        pk		identity of object to remove
    * @exception    ProcessingException
    */
    public void delete${classObject.getName()}( ${classObject.getName()}PrimaryKey pk ) 
    throws ProcessingException 
    {
	    Transaction tx 	= null;
	    Session session = null;
	    
    	try
    	{    	
			${classObject.getName()} bo = find${classObject.getName()}(pk);    	
			
			session = currentSession();
			tx		= currentTransaction( session );						
			session.delete( bo );
			commitTransaction( tx );
		}
		catch( Throwable exc )
		{
			try
			{
				if ( tx != null )
					rollbackTransaction( tx );				
			}
			catch( Throwable exc1 )
			{
				LOGGER.info( "${classObject.getName()}DAO.delete${classObject.getName()} - Hibernate failed to rollback - " + exc1 );
			}
			exc.printStackTrace();			
			throw new ProcessingException( "${classObject.getName()}DAO.delete${classObject.getName()} failed - " + exc );					
		}		
		finally
		{
			try
			{
				session.flush();
				closeSession();			
			}
			catch( Throwable exc )
			{		
				LOGGER.info( "${classObject.getName()}DAO.delete${classObject.getName()} - Hibernate failed to close the Session - " + exc );
			}
		}
    }

##end ##if ( $aib.hasIdentity( $classObject ) == true )		

// AIB : \#outputDAOFindAllImplementations()
##outputDAOFindAllImplementations()
// ~AIB


//*****************************************************
// Attributes
//*****************************************************
	private static final Logger LOGGER 	= Logger.getLogger(${classObject.getName()}.class.getName());

}
