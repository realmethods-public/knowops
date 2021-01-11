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
/**
 *  Workfile: $ Revision: $
 *  Last Modified by:   Author: $ on Date: $
 *
 */
package com.realmethods.entity.dao;

import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.realmethods.entity.Build;
import com.realmethods.foundational.common.exception.FrameworkDAOException;

import org.hibernate.query.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Implements the Hibernate persistence processing for business entity
 * Build.
 * 
 * @author realMethods, Inc.
 */

public class BuildDAO extends BaseDAO {
	/**
	 * default constructor
	 */
	public BuildDAO() {
		// no setup required
	}

	/**
	 * Retrieves a Build from the persistent store, using the
	 * provided primary key. If no match is found, a null Build is
	 * returned.
	 * <p>
	 * 
	 * @param pk
	 * @return Build
	 * @exception FrameworkDAOException
	 */
	public Build findBuild(Long pk)
			throws FrameworkDAOException {
		if (pk == null) {
			throw new FrameworkDAOException(
					"BuildDAO.findBuild(...) cannot have a null primary key argument");
		}

		Query query = null;
		Build businessObject = null;

		StringBuilder fromClause = new StringBuilder(
				FROM_QUERY_STRING);

		Session session = null;
		Transaction tx = null;

		try {
			session = currentSession();
			tx = currentTransaction(session);

			// AIB : #getHibernateFindFromClause()
			fromClause.append("field.id = " + pk);
			// ~AIB
			query = session.createQuery(fromClause.toString());

			if (query != null) {
				businessObject = (Build) query.list().iterator()
						.next();
			}

			commitTransaction(tx);
		} catch (Exception exc) {
			businessObject = null;
			final String msg = "BuildDAO.findBuild failed for primary key "
					+ pk;
			LOGGER.log(Level.SEVERE, msg, exc);
			throw new FrameworkDAOException(
					"BuildDAO.findBuild ", exc);
		} finally {
			closeSession();
		}

		return (businessObject);
	}


	/**
	 * returns a Collection of all Builds
	 * 
	 * @return List<Build>
	 * @exception FrameworkDAOException
	 */
	public List<Build> findAllBuild()
			throws FrameworkDAOException {

		List<Build> list = new ArrayList<>();
		Query query = null;
		StringBuilder buf = new StringBuilder(
				"from com.realmethods.entity.Build");

		try {
			Session session = currentSession();

			query = session.createQuery(buf.toString());

			if (query != null) {
				list = (List<Build>) query.list();
			}
		} catch (Exception exc) {
			throw new FrameworkDAOException(
					"BuildDAO.findAllBuild failed - "
							+ exc);
		} finally {
			closeSession();
		}

		if (list.isEmpty()) {
			LOGGER.info(
					"BuildDAO:findAllBuilds() - List is empty.");
		}

		return (list);
	}

	// *****************************************************
	// Protected/Private Methods
	// *****************************************************

	/**
	 * Inserts a new Build into the persistent store.
	 * 
	 * @param businessObject
	 * @return newly persisted Build
	 * @exception FrameworkDAOException
	 */
	public Long create(Build businessObject)
			throws FrameworkDAOException {
		return (Long)super.create(businessObject);
	}

	/**
	 * Stores the provided Build to the persistent store.
	 * 
	 * @param businessObject
	 * @return Build stored entity
	 * @exception FrameworkDAOException
	 */
	public Build save(Build businessObject)
			throws FrameworkDAOException {
		super.save(businessObject);
		return (businessObject);
	}

	/**
	 * Removes a Build from the persistent store.
	 * 
	 * @param pk
	 *            identity of object to remove
	 * @exception FrameworkDAOException
	 */
	public void delete(Long pk) throws FrameworkDAOException {
		super.delete(findBuild(pk));
	}

	/**
	 * Find a Build using the given name.
	 * 
	 * @param name
	 * @return Build
	 */
	public Build findByNameorId(String nameOrId) {
		Build pkg = null;
		Session session = null;
		StringBuilder sql = new StringBuilder( FROM_QUERY_STRING );
		
		sql.append( "field.name = '");
		sql.append(nameOrId);
		sql.append("'");
		
		// determine if the nameOrId is a Long
		try {
	        double lngId = Long.parseLong(nameOrId);
	        
	        sql.append( " OR field.id = " + lngId );
	    } catch (NumberFormatException | NullPointerException nfe) {
	    	LOGGER.info( "nameOrId arg not a numeric value.");
	    }
				
		try {
			session = currentSession();
			
			// create query
			Query query = session.createQuery(sql.toString());
			
			// fire off the query
			List<Build> list = (List<Build>) query.list();

			if (list != null && !list.isEmpty()) {
				pkg = list.get(0);
			}
		} catch (Exception exc) {
			LOGGER.log(Level.INFO,
					"Failed to find a Build by identifier "
							+ nameOrId,exc);
		} finally {
			closeSession();
		}
		return (pkg);
	}

	
	// Attributes
	private static final String FROM_QUERY_STRING = "from com.realmethods.entity.Build as field where ";
	private static final Logger LOGGER = Logger
			.getLogger(BuildDAO.class.getName());
}
