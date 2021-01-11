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
import java.util.logging.Logger;

import com.realmethods.entity.GeneratedAppDetails;
import com.realmethods.entity.ScopeType;
import com.realmethods.foundational.common.exception.FrameworkDAOException;

import org.hibernate.query.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Implements the Hibernate persistence processing for business entity
 * GeneratedAppDetails.
 * 
 * @author realMethods, Inc.
 */

public class GeneratedAppDetailsDAO extends BaseDAO {
	/**
	 * default constructor
	 */
	public GeneratedAppDetailsDAO() {
		// nothing to initialize
	}

	/**
	 * Retrieves a GeneratedAppDetails from the persistent store, using the
	 * provided primary key. If no match is found, a null GeneratedAppDetails is
	 * returned.
	 * <p>
	 * 
	 * @param pk
	 * @return GeneratedAppDetails
	 * @exception FrameworkDAOException
	 */
	public GeneratedAppDetails findGeneratedAppDetails(Long pk) throws FrameworkDAOException {
		if (pk == null) {
			throw new FrameworkDAOException(
					"GeneratedAppDetailsDAO.findGeneratedAppDetails(...) cannot have a null primary key argument");
		}

		Query query = null;
		GeneratedAppDetails businessObject = null;

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
				businessObject = (GeneratedAppDetails) query.list().iterator().next();
			}

			commitTransaction(tx);
		} catch (Exception exc) {
			businessObject = null;
			throw new FrameworkDAOException("GeneratedAppDetailsDAO.findGeneratedAppDetails failed for primary key "
					+ pk + " - " + exc);
		} finally {
			closeSession();
		}
		return (businessObject);
	}


	/**
	 * returns a Collection of all GeneratedAppDetailss
	 * 
	 * @return ArrayList<GeneratedAppDetails>
	 * @exception FrameworkDAOException
	 */
	public List<GeneratedAppDetails> findAllGeneratedAppDetails() throws FrameworkDAOException {

		ArrayList<GeneratedAppDetails> list = new ArrayList<>();
		Query query = null;
		StringBuilder buf = new StringBuilder("from com.realmethods.entity.GeneratedAppDetails");

		try {
			Session session = currentSession();

			query = session.createQuery(buf.toString());

			if (query != null) {
				list = (ArrayList<GeneratedAppDetails>) query.list();
			}
		} catch (Exception exc) {
			throw new FrameworkDAOException("GeneratedAppDetailsDAO.findAllGeneratedAppDetails failed - " + exc);
		} finally {
			closeSession();
		}

		if (list.isEmpty()) {
			LOGGER.info("List is empty.");
		}

		return (list);
	}

	/**
	 * returns a Collection of all GeneratedAppDetails.  If the scopeType is ScopeType.PUBLIC, then it returns
	 * all PUBLIC entries.  If the scopeType is ScopeType.PRIVATE, then it returns only those associated with
	 * the ownerId.
	 * 
	 * @return List<GeneratedAppDetails>
	 * @exception FrameworkDAOException
	 */
	public List<GeneratedAppDetails> findAllGeneratedAppDetails(ScopeType scopeType, Long ownerId) throws FrameworkDAOException {

		if (ownerId == null) {
			throw new FrameworkDAOException(
					"GeneratedAppDetailsDAO.findAllGeneratedAppDetails(...) cannot have a null ownerId argument");
		}

		List<GeneratedAppDetails> list = new ArrayList<>();
		Query query = null;

		StringBuilder fromClause = new StringBuilder(
				FROM_QUERY_STRING);

		Session session = null;
		Transaction tx = null;

		try {
			session = currentSession();
			tx = currentTransaction(session);

			fromClause.append(helperConstructFromClause(scopeType, ownerId));
			
			query = session.createQuery(fromClause.toString());

			if (query != null) {
				list = (List<GeneratedAppDetails>) query.list();
			}

			commitTransaction(tx);
		} catch (Exception exc) {
			throw new FrameworkDAOException("GeneratedAppDetailsDAO.findAllGeneratedAppDetails failed - " + exc);
		} finally {
			closeSession();
		}

		if (list.isEmpty()) {
			LOGGER.info("List is empty.");
		}

		return (list);
	}

	/**
	 * Inserts a new GeneratedAppDetails into the persistent store.
	 * 
	 * @param businessObject
	 * @return newly persisted GeneratedAppDetails
	 * @exception FrameworkDAOException
	 */
	public GeneratedAppDetails create(GeneratedAppDetails businessObject)
			throws FrameworkDAOException {
		super.create( businessObject );
		
		return (businessObject);
	}

	/**
	 * Stores the provided GeneratedAppDetails to the persistent store.
	 * 
	 * @param businessObject
	 * @return GeneratedAppDetails stored entity
	 * @exception FrameworkDAOException
	 * @exception VersionUpdateException
	 */
	public GeneratedAppDetails save(GeneratedAppDetails businessObject) throws FrameworkDAOException {
		super.save( businessObject);
		return (businessObject);
	}

	/**
	 * Removes a GeneratedAppDetails from the persistent store.
	 * 
	 * @param pk
	 *            identity of object to remove
	 * @exception FrameworkDAOException
	 */
	public void delete(Long pk) throws FrameworkDAOException {
		super.delete( findGeneratedAppDetails(pk) );
	}

	// Attributes
	private static final String FROM_QUERY_STRING = "from com.realmethods.entity.GeneratedAppDetails as field where ";
	private static final Logger LOGGER = Logger.getLogger(GeneratedAppDetailsDAO.class.getName());
	
}
