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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.cloudmigrate.entity.LocalModel;
import com.cloudmigrate.entity.ModelType;
import com.cloudmigrate.entity.ScopeType;
import com.cloudmigrate.foundational.common.exception.FrameworkDAOException;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Implements the Hibernate persistence processing for business entity
 * LocalModel.
 * 
 * @author realMethods, Inc.
 */

public class LocalModelDAO extends BaseDAO {
	/**
	 * default constructor
	 */
	public LocalModelDAO() {
		// nothing to initialize
	}

	/**
	 * Retrieves a LocalModel from the persistent store, using the provided
	 * primary key. If no match is found, a null LocalModel is returned.
	 * <p>
	 * 
	 * @param pk
	 * @return LocalModel
	 * @exception FrameworkDAOException
	 */
	public LocalModel findLocalModel(Long pk) throws FrameworkDAOException {
		if (pk == null) {
			throw new FrameworkDAOException("LocalModelDAO.findLocalModel(...) cannot have a null primary key argument");
		}

		Query query = null;
		LocalModel businessObject = null;

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
				businessObject = (LocalModel) query.list().iterator().next();
			}

			commitTransaction(tx);
		} catch (Exception exc) {
			businessObject = null;
			throw new FrameworkDAOException("LocalModelDAO.findLocalModel failed for primary key " + pk + " - " + exc);
		} finally {
			closeSession();
		}

		return (businessObject);
	}


	/**
	 * returns a Collection of all LocalModels
	 * 
	 * @return List<LocalModel>
	 * @exception FrameworkDAOException
	 */
	public List<LocalModel> findAllLocalModel() throws FrameworkDAOException {

		ArrayList<LocalModel> list = new ArrayList<>();
		Query query = null;
		StringBuilder buf = new StringBuilder("from com.cloudmigrate.entity.LocalModel");

		try {
			Session session = currentSession();

			query = session.createQuery(buf.toString());

			if (query != null) {
				list = (ArrayList<LocalModel>) query.list();
			}
		} catch (Exception exc) {
			throw new FrameworkDAOException("LocalModelDAO.findAllLocalModel failed - " + exc);
		} finally {
			closeSession();
		}

		if (list.isEmpty()) {
			LOGGER.info("LocalModelDAO:findAllLocalModels() - List is empty.");
		}

		return (list);
	}

	/**
	 * Retrieves all LocalModel from the persistent store, using the
	 * provided token to match the ownerId. If no match is found, a non-null empty list 
	 * is returned.
	 * <p>
	 * 
	 * @param scopeType
	 * @param ownerId
	 * @param filter
	 * @return List<LocalModel>
	 * @exception FrameworkDAOException
	 */
	public List<LocalModel> findAllLocalModel(ScopeType scopeType, Long ownerId, String filter )
			throws FrameworkDAOException {
		if (ownerId == null) {
			throw new FrameworkDAOException(
					"LocalModelDAO.findAllLocalModel(...) cannot have a null ownerId argument");
		}

		Query query = null;
		List<LocalModel> list = new ArrayList<>();

		StringBuilder fromClause = new StringBuilder(
				FROM_QUERY_STRING);

		Session session = null;
		Transaction tx = null;

		try {
			session = currentSession();
			tx = currentTransaction(session);

			fromClause.append(helperConstructFromClause(scopeType, ownerId));


			if ( filter != null ) {
				fromClause.append( " and " + FIELD + ".modelType =:m_type");
			}
			
			query = session.createQuery(fromClause.toString());

			if (query != null) {
				if ( filter == null )
					list = (List<LocalModel>) query.list();
				else {
					ModelType modelType = ModelType.whichOne(filter);
					list = (List<LocalModel>) query.setParameter("m_type", modelType).list();
				}
			}

			commitTransaction(tx);
		} catch (Exception exc) {
			final String msg = "Failed for ownerId  "
					+ ownerId;
			LOGGER.log(Level.SEVERE, msg, exc);
			throw new FrameworkDAOException(
					"LocalModelDAO.findAllLocalModel ", exc);
		} finally {
			closeSession();
		}

		return (list);
	}

	/**
	 * Retrieves all LocalModel from the persistent store where the
	 * ScopeType is public. If no match is found, a null LocalModel is
	 * returned.
	 * <p>
	 * 
	 * @param token
	 * @return LocalModel
	 * @exception FrameworkDAOException
	 */
	public List<LocalModel> findAllPublicLocalModel()
			throws FrameworkDAOException {

		Query query = null;
		List<LocalModel> list = new ArrayList<>();

		StringBuilder fromClause = new StringBuilder(
				FROM_QUERY_STRING);

		Session session = null;
		Transaction tx = null;

		try {
			session = currentSession();
			tx = currentTransaction(session);

			// AIB : #getHibernateFindFromClause()
			fromClause.append("field.scopeType = " + "'" + com.cloudmigrate.entity.ScopeType.PUBLIC.toString() + "'");
			// ~AIB
			query = session.createQuery(fromClause.toString());

			if (query != null) {
				list = (List<LocalModel>) query.list();
			}

			commitTransaction(tx);
		} catch (Exception exc) {
			LOGGER.log(Level.SEVERE, "Failed during query" , exc);
			throw new FrameworkDAOException(
					"LocalModelDAO.findAllLocalModelByToken ", exc);
		} finally {
			closeSession();
		}

		return (list);
	}

	/**
	 * Inserts a new LocalModel into the persistent store.
	 * 
	 * @param businessObject
	 * @return newly persisted LocalModel
	 * @exception FrameworkDAOException
	 */
	public LocalModel create(LocalModel businessObject) throws FrameworkDAOException {
		super.create(businessObject);
		return (businessObject);
	}

	/**
	 * Stores the provided LocalModel to the persistent store.
	 * 
	 * @param businessObject
	 * @return LocalModel stored entity
	 * @exception FrameworkDAOException
	 * @exception VersionUpdateException
	 */
	public LocalModel save(LocalModel businessObject) throws FrameworkDAOException {
		super.save(businessObject);
		return (businessObject);
	}

	/**
	 * Removes a LocalModel from the persistent store.
	 * 
	 * @param pk
	 *            identity of object to remove
	 * @exception FrameworkDAOException
	 */
	public void delete(Long pk) throws FrameworkDAOException {
		super.delete( findLocalModel(pk) );
	}

	// *****************************************************
	// Attributes
	// *****************************************************
	private static final Logger LOGGER = Logger.getLogger(LocalModelDAO.class.getName());
	private static final String FROM_QUERY_STRING = "from com.cloudmigrate.entity.LocalModel as field where ";

}
