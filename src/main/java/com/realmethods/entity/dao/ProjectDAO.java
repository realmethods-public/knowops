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

import com.realmethods.entity.Project;
import com.realmethods.entity.ScopeType;
import com.realmethods.foundational.common.exception.FrameworkDAOException;

import org.hibernate.query.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Implements the Hibernate persistence processing for business entity
 * Project.
 * 
 * @author realMethods, Inc.
 */

public class ProjectDAO extends BaseDAO {
	/**
	 * default constructor
	 */
	public ProjectDAO() {
		// no setup required
	}

	/**
	 * Retrieves a Project from the persistent store, using the
	 * provided primary key. If no match is found, a null Project is
	 * returned.
	 * <p>
	 * 
	 * @param pk
	 * @return Project
	 * @exception FrameworkDAOException
	 */
	public Project findProject(Long pk)
			throws FrameworkDAOException {
		if (pk == null) {
			throw new FrameworkDAOException(
					"ProjectDAO.findProject(...) cannot have a null primary key argument");
		}

		Query query = null;
		Project businessObject = null;

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
				businessObject = (Project) query.list().iterator()
						.next();
			}

			commitTransaction(tx);
		} catch (Exception exc) {
			businessObject = null;
			final String msg = "ProjectDAO.findProject failed for primary key "
					+ pk;
			LOGGER.log(Level.SEVERE, msg, exc);
			throw new FrameworkDAOException(
					"ProjectDAO.findProject ", exc);
		} finally {
			closeSession();
		}

		return (businessObject);
	}


	/**
	 * returns a Collection of all Projects
	 * 
	 * @return List<Project>
	 * @exception FrameworkDAOException
	 */
	public List<Project> findAllProject()
			throws FrameworkDAOException {

		List<Project> list = new ArrayList<>();
		Query query = null;
		StringBuilder buf = new StringBuilder(
				"from com.realmethods.entity.Project");

		try {
			Session session = currentSession();

			query = session.createQuery(buf.toString());

			if (query != null) {
				list = (List<Project>) query.list();
			}
		} catch (Exception exc) {
			throw new FrameworkDAOException(
					"ProjectDAO.findAllProject failed - "
							+ exc);
		} finally {
			closeSession();
		}

		if (list.isEmpty()) {
			LOGGER.info(
					"ProjectDAO:findAllProjects() - List is empty.");
		}

		return (list);
	}

	/**
	 * returns a Collection of all Projects for the provided owner
	 * 
	 * @return List<Project>
	 * @exception FrameworkDAOException
	 */
	public List<Project> findAllProjectByOwner( Long ownerId )
			throws FrameworkDAOException {
		
		final String msg = "ownerId = " + ownerId;
		LOGGER.info(msg);
		
		List<Project> list = new ArrayList<>();
		Query query = null;
		StringBuilder fromClause = new StringBuilder( FROM_QUERY_STRING );
				
		try {
			Session session = currentSession();

			fromClause.append("field.ownerId = " + ownerId);

			query = session.createQuery(fromClause.toString());

			if (query != null) {
				list = (List<Project>) query.list();
			}
		} catch (Exception exc) {
			throw new FrameworkDAOException(
					"ProjectDAO.findAllProjectByOwner failed - "
							+ exc);
		} finally {
			closeSession();
		}

		if (list.isEmpty()) {
			LOGGER.info(
					"List is empty.");
		}

		return (list);
	}

	// *****************************************************
	// Protected/Private Methods
	// *****************************************************

	/**
	 * Inserts a new Project into the persistent store.
	 * 
	 * @param businessObject
	 * @return newly persisted Project
	 * @exception FrameworkDAOException
	 */
	public Long create(Project businessObject)
			throws FrameworkDAOException {
		return (Long)super.create(businessObject);
	}

	/**
	 * Stores the provided Project to the persistent store.
	 * 
	 * @param businessObject
	 * @return Project stored entity
	 * @exception FrameworkDAOException
	 */
	public Project save(Project businessObject)
			throws FrameworkDAOException {
		super.save(businessObject);
		return (businessObject);
	}

	/**
	 * Removes a Project from the persistent store.
	 * 
	 * @param pk
	 *            identity of object to remove
	 * @exception FrameworkDAOException
	 */
	public void delete(Long pk) throws FrameworkDAOException {
		super.delete(findProject(pk));
	}

	/**
	 * Find a Project using the given name.
	 * 
	 * @param name
	 * @return Project
	 */
	public Project findByNameorId(String nameOrId) {
		Project pkg = null;
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
			List<Project> list = (List<Project>) query.list();

			if (list != null && !list.isEmpty()) {
				pkg = list.get(0);
			}
		} catch (Exception exc) {
			LOGGER.log(Level.INFO,
					"Failed to find a Project by identifier "
							+ nameOrId,exc);
		} finally {
			closeSession();
		}
		return (pkg);
	}

	
	// Attributes
	private static final String FROM_QUERY_STRING = "from com.realmethods.entity.Project as field where ";
	private static final Logger LOGGER = Logger
			.getLogger(ProjectDAO.class.getName());
}
