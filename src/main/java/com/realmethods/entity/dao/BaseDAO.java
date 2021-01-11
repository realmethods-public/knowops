/*******************************************************************************
 * realMethods Confidential
 * 
 * 2021 realMethods, Inc.
 * All Rights Reserved.
 * 
 * This file is subject to the terms and conditions defined in
 * file 'license.txt', which is part of this source code package.
 *  
 * Contributors :
 *       realMethods Inc - General Release
 ******************************************************************************/
package com.realmethods.entity.dao;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.realmethods.entity.Base;
import com.realmethods.entity.ScopeType;
import com.realmethods.foundational.common.exception.FrameworkDAOException;
import com.realmethods.foundational.integration.dao.FrameworkHibernateDAO;

/**
 * Base class for all realMethods Data Access Objects.
 *
 * @author realMethods, Inc.
 */
public class BaseDAO
    extends FrameworkHibernateDAO
{

	/**
     * Returns the object by name.
     * @param name
     * @return
     */
    public Base findByName(String className, String name) throws FrameworkDAOException
    {
        Base bo = null;
        
        Session session = null;

        try
        {
            session = currentSession();
            session.beginTransaction();

            // create the Hibernate SQL
            StringBuilder sql = new StringBuilder("from com.realmethods.entity." + className + " as " + className + " where " + className + ".name='");
            sql.append(name);
            sql.append("'");

            // fire off the query 
            List list = session.createQuery(sql.toString()).list();

            if ((list != null) && (!list.isEmpty()))
            {
                bo = (Base) list.get(0);
            }
        } catch( Exception exc ) {
        	throw new FrameworkDAOException( "BaseDAO.findByName", exc );
        }
        finally
        {
            try
            {
            	if ( session != null )
            		session.flush();
                closeSession();
            }
            catch (Exception exc)
            {
            	LOGGER.info("SearchAppDAO.findSearchAppByName - Hibernate failed to close the Session - " + exc);
            }
        }
        
        return( bo );
    }

    protected String helperConstructFromClause( ScopeType scopeType, Long token ) {
		StringBuilder sb = new StringBuilder();

		if ( scopeType == null )
			sb.append(FIELD + '.' + OWNERID + EQUAL_EXPRESSION + token + "'");
		else {
			switch( scopeType ) {
				case ALL: 
					sb.append("((field.scopeType = 'PUBLIC' or field.scopeType = 'PRIVATE') AND field.ownerId = '" + token + "') OR " );	
					sb.append("(field.scopeType = 'PUBLIC' and field.ownerId <> '" + token + "')");
				break;

				case PUBLIC: 
					sb.append(FIELD + '.' + SCOPETYPE + EQUAL_EXPRESSION + com.realmethods.entity.ScopeType.PUBLIC.toString() + AND_EXPRESSION);	
					sb.append(FIELD + '.' + OWNERID + EQUAL_EXPRESSION + token + "'");
					break;
				
				case PRIVATE:
					sb.append(FIELD + '.' + SCOPETYPE + EQUAL_EXPRESSION + com.realmethods.entity.ScopeType.PRIVATE.toString() + AND_EXPRESSION);
					sb.append(FIELD + '.' + OWNERID + EQUAL_EXPRESSION + token + "'");
					break;
					
				case COMMUNITY:	// all public, excluding our own
					sb.append(FIELD + '.' + SCOPETYPE + EQUAL_EXPRESSION + com.realmethods.entity.ScopeType.PUBLIC.toString() + AND_EXPRESSION);
					sb.append(FIELD + '.' + OWNERID + NOT_EQUAL_EXPRESSION + token + "'");
					break;
	
				default:
					sb.append(FIELD + '.' + OWNERID + NOT_EQUAL_EXPRESSION + token + "'");
					break;
			}
		}		
		
		return( sb.toString() );
    }
    
	public Query findAllHelper(ScopeType scopeType, Long ownerId, String filter, String queryString )
			throws FrameworkDAOException {
		
		if (ownerId == null) {
			throw new FrameworkDAOException(
					"BaseDAO.findAllHelper(...) cannot have a null ownerId argument");
		}

		Session session 			= null;
		Query query 				= null;
		StringBuilder fromClause 	= new StringBuilder(queryString);

		try {
			session = currentSession();

			fromClause.append(helperConstructFromClause(scopeType, ownerId));

			if ( filter != null ) {
				fromClause.append( " and " + FIELD + ".modelType =:m_type");
			}
			
			query = session.createQuery(fromClause.toString());

		} catch (Exception exc) {
			final String msg = "Failed for ownerId  "
					+ ownerId;
			LOGGER.log(Level.SEVERE, msg, exc);
			throw new FrameworkDAOException(
					"BaseDAO.findAllHelper ", exc);
		} 

		return (query);
	}
	
    // attributes
	protected static final String FIELD 				= "field";
	protected static final String SCOPETYPE 			= "scopeType";
	protected static final String OWNERID				= "ownerId";
	protected static final String AND_EXPRESSION		= "' and ";
	protected static final String EQUAL_EXPRESSION 		= " = '";
	protected static final String NOT_EQUAL_EXPRESSION 	= " <> '";
	private static final Logger LOGGER 					= Logger.getLogger(BaseDAO.class.getName());	

}
