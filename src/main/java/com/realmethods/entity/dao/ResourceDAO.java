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
 */package com.realmethods.entity.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.realmethods.entity.Resource;
import com.realmethods.entity.ResourceType;
import com.realmethods.entity.ScopeType;
import com.realmethods.foundational.common.exception.FrameworkDAOException;

import org.hibernate.query.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Implements the Hibernate persistence processing for business entity
 * Resource.
 * 
 * @author realMethods, Inc.
 */

public class ResourceDAO extends BaseDAO {
	/**
	 * default constructor
	 */
	public ResourceDAO() {
		// nothing to initialize
	}

	/**
	 * Retrieves a Resource from the persistent store, using the provided
	 * primary key. If no match is found, a null Resource is returned.
	 * <p>
	 * 
	 * @param pk
	 * @return Resource
	 * @exception FrameworkDAOException
	 */
	public Resource findResource(Long pk) throws FrameworkDAOException {
		if (pk == null) {
			throw new FrameworkDAOException("ResourceDAO.findResource(...) cannot have a null primary key argument");
		}

		Query query = null;
		Resource businessObject = null;

		StringBuilder fromClause = new StringBuilder(FROM_QUERY_STRING);

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
				businessObject = (Resource) query.list().iterator().next();
			}

			commitTransaction(tx);
		} catch (Exception exc) {
			businessObject = null;
			throw new FrameworkDAOException("ResourceDAO.findResource failed for primary key " + pk + " - " + exc);
		} finally {
			closeSession();
		}

		return (businessObject);
	}


	/**
	 * Retrieves the associated resources for the provide userId
	 * <p>
	 * 
	 * @param pk
	 * @return Resource
	 * @exception FrameworkDAOException
	 */
	public List<Resource> findResourcesByUserId(Long userId) throws FrameworkDAOException {
		if (userId == null) {
			throw new FrameworkDAOException("ResourceDAO.findResourcesByUserId(...) cannot have a null primary key argument");
		}

		Query query = null;
		List<Resource> list = new ArrayList<>();

		StringBuilder fromClause = new StringBuilder(FROM_QUERY_STRING);

		Session session = null;
		Transaction tx = null;

		try {
			session = currentSession();
			tx = currentTransaction(session);

			// AIB : #getHibernateFindFromClause()
			fromClause.append("field.owerId = " + userId);
			// ~AIB
			query = session.createQuery(fromClause.toString());

			if (query != null) {
				list = (List<Resource>) query.list();
			}

			commitTransaction(tx);
		} catch (Exception exc) {
			list = null;
			throw new FrameworkDAOException("ResourceDAO.findResourcesByUserId failed for userId " + userId + " - " + exc);
		} finally {
			closeSession();
		}

		return (list);
	}


	/**
	 * returns a Collection of all Resources
	 * 
	 * @return List<Resource>
	 * @exception FrameworkDAOException
	 */
	public List<Resource> findAllResource() throws FrameworkDAOException {

		ArrayList<Resource> list = new ArrayList<>();
		Query query = null;
		StringBuilder buf = new StringBuilder("from com.realmethods.entity.Resource");

		try {
			Session session = currentSession();

			query = session.createQuery(buf.toString());

			if (query != null) {
				list = (ArrayList<Resource>) query.list();
			}
		} catch (Exception exc) {
			throw new FrameworkDAOException("ResourceDAO.findAllResource failed - " + exc);
		} finally {
			closeSession();
		}

		if (list.isEmpty()) {
			LOGGER.info("ResourceDAO:findAllResources() - List is empty.");
		}

		return (list);
	}

	/**
	 * Retrieves all Resource from the persistent store, using the
	 * provided token to match the ownerId. If no match is found, a non-null empty list 
	 * is returned.
	 * <p>
	 * 
	 * @param scopeType
	 * @param ownerId
	 * @param filter
	 * @return List<Resource>
	 * @exception FrameworkDAOException
	 */
	public List<Resource> findAllResource(ScopeType scopeType, Long ownerId, String filter )
			throws FrameworkDAOException {

		List<Resource> list = null;

		try
		{
			Query query = findAllHelper( scopeType, ownerId, filter, FROM_QUERY_STRING );

			if (query != null) {
				if ( filter == null )
					list = (List<Resource>) query.list();
				else {
					ResourceType resourceType = ResourceType.whichOne(filter);
					list = (List<Resource>) query.setParameter("m_type", resourceType).list();
				}
			}
			commitTransaction(currentTransaction(currentSession()));
		} catch (Exception exc) {
			final String msg = "Failed for ownerId  "
					+ ownerId;
			LOGGER.log(Level.SEVERE, msg, exc);
			throw new FrameworkDAOException(
					"ResourceDAO.findAllResource ", exc);
		} finally {
			closeSession();
		}

		return (list);
	}

	/**
	 * Retrieves all Resource from the persistent store where the
	 * ScopeType is public. If no match is found, a null Resource is
	 * returned.
	 * <p>
	 * 
	 * @param token
	 * @return Resource
	 * @exception FrameworkDAOException
	 */
	public List<Resource> findAllPublicResource()
			throws FrameworkDAOException {

		Query query = null;
		List<Resource> list = new ArrayList<>();

		StringBuilder fromClause = new StringBuilder(
				FROM_QUERY_STRING);

		Session session = null;
		Transaction tx = null;

		try {
			session = currentSession();
			tx = currentTransaction(session);

			// AIB : #getHibernateFindFromClause()
			fromClause.append("field.scopeType = " + "'" + com.realmethods.entity.ScopeType.PUBLIC.toString() + "'");
			// ~AIB
			query = session.createQuery(fromClause.toString());

			if (query != null) {
				list = (List<Resource>) query.list();
			}

			commitTransaction(tx);
		} catch (Exception exc) {
			LOGGER.log(Level.SEVERE, "Failed during query" , exc);
			throw new FrameworkDAOException(
					"ResourceDAO.findAllResourceByToken ", exc);
		} finally {
			closeSession();
		}

		return (list);
	}

	/**
	 * Inserts a new Resource into the persistent store.
	 * 
	 * @param businessObject
	 * @return newly persisted Resource
	 * @exception FrameworkDAOException
	 */
	public Resource create(Resource businessObject) throws FrameworkDAOException {
		super.create(businessObject);
		return (businessObject);
	}

	/**
	 * Stores the provided Resource to the persistent store.
	 * 
	 * @param businessObject
	 * @return Resource stored entity
	 * @exception FrameworkDAOException
	 * @exception VersionUpdateException
	 */
	public Resource save(Resource businessObject) throws FrameworkDAOException {
		super.save(businessObject);
		return (businessObject);
	}

	/**
	 * Removes a Resource from the persistent store.
	 * 
	 * @param pk
	 *            identity of object to remove
	 * @exception FrameworkDAOException
	 */
	public void delete(Long pk) throws FrameworkDAOException {
		super.delete( findResource(pk) );
	}

	// *****************************************************
	// Attributes
	// *****************************************************
	private static final Logger LOGGER = Logger.getLogger(ResourceDAO.class.getName());
	private static final String FROM_QUERY_STRING = "from com.realmethods.entity.Resource as field where ";

}
