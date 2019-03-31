/*******************************************************************************
 * realMethods Confidential
 * 
 * 2018 realMethods, Inc.
 * All Rights Reserved.
 * 
 * This file is subject to the terms and conditions defined in
 * file 'license.txt', which is part of this source code package.
 *  
 * Contributors :
 *       realMethods Inc - General Release
 ******************************************************************************/
/**
 *  Workfile: $ Revision: $
 *  Last Modified by:   Author: $ on Date: $
 *
 */package com.cloudmigrate.entity.dao;

import java.util.List;
import java.util.logging.Logger;

import org.hibernate.Session;

import com.cloudmigrate.entity.Base;
import com.cloudmigrate.entity.ScopeType;
import com.cloudmigrate.foundational.common.exception.FrameworkDAOException;
import com.cloudmigrate.foundational.integration.dao.FrameworkHibernateDAO;

/**
 * Base class for all cloudMigrate Data Access Objects.
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
            StringBuilder sql = new StringBuilder("from com.cloudmigrate.entity." + className + " as " + className + " where " + className + ".name='");
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
			sb.append(FIELD + '.' + OWNERID + " = '" + token + "'");
		else {
			switch( scopeType ) {
				case ALL: 
					sb.append("((field.scopeType = 'PUBLIC' or field.scopeType = 'PRIVATE') AND field.ownerId = '" + token + "') OR " );	
					sb.append("(field.scopeType = 'PUBLIC' and field.ownerId <> '" + token + "')");
				break;

				case PUBLIC: 
					sb.append(FIELD + '.' + SCOPETYPE + " = '" + com.cloudmigrate.entity.ScopeType.PUBLIC.toString() + "' and ");	
					sb.append(FIELD + '.' + OWNERID + " = '" + token + "'");
					break;
				
				case PRIVATE:
					sb.append(FIELD + '.' + SCOPETYPE + " = '" + com.cloudmigrate.entity.ScopeType.PRIVATE.toString() + "' and ");
					sb.append(FIELD + '.' + OWNERID + " = '" + token + "'");
					break;
					
				case COMMUNITY:	// all public, excluding our own
					sb.append(FIELD + '.' + SCOPETYPE + " = '" + com.cloudmigrate.entity.ScopeType.PUBLIC.toString() + "' and ");
					sb.append(FIELD + '.' + OWNERID + " <> '" + token + "'");
					break;
	
				default:
					sb.append(FIELD + '.' + OWNERID + " <> '" + token + "'");
					break;
			}
		}		
		return( sb.toString() );
    }
    
    // attributes
	private static final Logger LOGGER 			= Logger.getLogger(BaseDAO.class.getName());	
	protected static final String FIELD 		= "field";
	protected static final String SCOPETYPE 	= "scopeType";
	protected static final String OWNERID		= "ownerId";
	
}
