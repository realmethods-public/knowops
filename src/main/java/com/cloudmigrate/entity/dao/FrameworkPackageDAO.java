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
 */
package com.cloudmigrate.entity.dao;

import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.cloudmigrate.entity.FrameworkPackage;
import com.cloudmigrate.entity.ScopeType;
import com.cloudmigrate.foundational.common.exception.FrameworkDAOException;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Implements the Hibernate persistence processing for business entity
 * FrameworkPackage.
 * 
 * @author realMethods, Inc.
 */

public class FrameworkPackageDAO extends BaseDAO {
	/**
	 * default constructor
	 */
	public FrameworkPackageDAO() {
		// no setup required
	}

	/**
	 * Retrieves a FrameworkPackage from the persistent store, using the
	 * provided primary key. If no match is found, a null FrameworkPackage is
	 * returned.
	 * <p>
	 * 
	 * @param pk
	 * @return FrameworkPackage
	 * @exception FrameworkDAOException
	 */
	public FrameworkPackage findFrameworkPackage(Long pk)
			throws FrameworkDAOException {
		if (pk == null) {
			throw new FrameworkDAOException(
					"FrameworkPackageDAO.findFrameworkPackage(...) cannot have a null primary key argument");
		}

		Query query = null;
		FrameworkPackage businessObject = null;

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
				businessObject = (FrameworkPackage) query.list().iterator()
						.next();
			}

			commitTransaction(tx);
		} catch (Exception exc) {
			businessObject = null;
			final String msg = "FrameworkPackageDAO.findFrameworkPackage failed for primary key "
					+ pk;
			LOGGER.log(Level.SEVERE, msg, exc);
			throw new FrameworkDAOException(
					"FrameworkPackageDAO.findFrameworkPackage ", exc);
		} finally {
			closeSession();
		}

		return (businessObject);
	}

	/**
	 * Retrieves all FrameworkPackage from the persistent store, using the
	 * provided token to match the ownerId. If no match is found, a non-null empty list 
	 * is returned.
	 * <p>
	 * 
	 * @param scopeType
	 * @param ownerId
	 * @param filter
	 * @return List<FrameworkPackage>
	 * @exception FrameworkDAOException
	 */
	public List<FrameworkPackage> findAllFrameworkPackage(ScopeType scopeType, Long ownerId, String filter)
			throws FrameworkDAOException {
		if (ownerId == null) {
			throw new FrameworkDAOException(
					"FrameworkPackageDAO.findAllFrameworkPackage(...) cannot have a null ownerId argument");
		}
		
		Query query = null;
		List<FrameworkPackage> list = new ArrayList<>();

		StringBuilder fromClause = new StringBuilder(
				FROM_QUERY_STRING);

		Session session = null;
		Transaction tx = null;

		try {
			session = currentSession();
			tx = currentTransaction(session);
			
			fromClause.append(helperConstructFromClause(scopeType, ownerId));

			if ( filter != null ) {
				fromClause.append( " and " + FIELD + ".appType = '" + filter + "'");
			}

			query = session.createQuery(fromClause.toString());

			if (query != null) {
				list = (List<FrameworkPackage>) query.list();
			}

			commitTransaction(tx);
		} catch (Exception exc) {
			final String msg = "Failed for ownerId  "
					+ ownerId;
			LOGGER.log(Level.SEVERE, msg, exc);
			throw new FrameworkDAOException(
					"FrameworkPackageDAO.findAllFrameworkPackageByToken ", exc);
		} finally {
			closeSession();
		}

		return (list);
	}

	/**
	 * returns a Collection of all FrameworkPackages
	 * 
	 * @return List<FrameworkPackage>
	 * @exception FrameworkDAOException
	 */
	public List<FrameworkPackage> findAllFrameworkPackage()
			throws FrameworkDAOException {

		List<FrameworkPackage> list = new ArrayList<>();
		Query query = null;
		StringBuilder buf = new StringBuilder(
				"from com.cloudmigrate.entity.FrameworkPackage");

		try {
			Session session = currentSession();

			query = session.createQuery(buf.toString());

			if (query != null) {
				list = (List<FrameworkPackage>) query.list();
			}
		} catch (Exception exc) {
			throw new FrameworkDAOException(
					"FrameworkPackageDAO.findAllFrameworkPackage failed - "
							+ exc);
		} finally {
			closeSession();
		}

		if (list.isEmpty()) {
			LOGGER.info(
					"FrameworkPackageDAO:findAllFrameworkPackages() - List is empty.");
		}

		return (list);
	}

	// *****************************************************
	// Protected/Private Methods
	// *****************************************************

	/**
	 * Inserts a new FrameworkPackage into the persistent store.
	 * 
	 * @param businessObject
	 * @return newly persisted FrameworkPackage
	 * @exception FrameworkDAOException
	 */
	public FrameworkPackage create(FrameworkPackage businessObject)
			throws FrameworkDAOException {
		super.create(businessObject);
		return (businessObject);
	}

	/**
	 * Stores the provided FrameworkPackage to the persistent store.
	 * 
	 * @param businessObject
	 * @return FrameworkPackage stored entity
	 * @exception FrameworkDAOException
	 */
	public FrameworkPackage save(FrameworkPackage businessObject)
			throws FrameworkDAOException {
		super.save(businessObject);
		return (businessObject);
	}

	/**
	 * Removes a FrameworkPackage from the persistent store.
	 * 
	 * @param pk
	 *            identity of object to remove
	 * @exception FrameworkDAOException
	 */
	public void delete(Long pk) throws FrameworkDAOException {
		super.delete(findFrameworkPackage(pk));
	}

	/**
	 * Find a FrameworkPackage using the given name.
	 * 
	 * @param name
	 * @return FrameworkPackage
	 * @throws FrameworkDAOException
	 */
	public FrameworkPackage findByNameorId(String nameOrId)
			throws FrameworkDAOException {
		FrameworkPackage pkg = null;
		Session session = null;
		StringBuilder sql = new StringBuilder( FROM_QUERY_STRING );
		
		sql.append( "field.name = '");
		sql.append(nameOrId);
		sql.append("' OR field.shortName = '");
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
			Query query = session.createQuery(sql.toString());;
			
			// fire off the query
			List<FrameworkPackage> list = (List<FrameworkPackage>) query.list();

			if (list != null && !list.isEmpty()) {
				pkg = list.get(0);
			}
		} catch (Exception exc) {
			LOGGER.log(Level.INFO,
					"Failed to find a FrameworkPackage by identifier "
							+ nameOrId,exc);
		} finally {
			closeSession();
		}
		return (pkg);
	}

	/**
	 * Find a FrameworkPackage using the given checksum.
	 * 
	 * @param name
	 * @return FrameworkPackage
	 * @throws FrameworkDAOException
	 */
	public FrameworkPackage findByChecksum(String checksum)
			throws FrameworkDAOException {
		FrameworkPackage pkg = null;
		Session session = null;
		StringBuilder sql = new StringBuilder( FROM_QUERY_STRING );
		
		sql.append( "field.checkSum = '");
		sql.append(checksum);
		sql.append("'");

		try {
			session = currentSession();
			
			// create query
			Query query = session.createQuery(sql.toString());;
			
			// fire off the query
			List<FrameworkPackage> list = (List<FrameworkPackage>) query.list();

			if (list != null && !list.isEmpty()) {
				pkg = list.get(0);
			}
		} catch (Exception exc) {
			LOGGER.log(Level.INFO,
					"Failed to find FrameworkPackage by checksum "
							+ checksum,exc);
		} finally {
			closeSession();
		}
		return (pkg);
	}
	
	// Attributes
	private static final String FROM_QUERY_STRING = "from com.cloudmigrate.entity.FrameworkPackage as field where ";
	private static final Logger LOGGER = Logger
			.getLogger(FrameworkPackageDAO.class.getName());
}
