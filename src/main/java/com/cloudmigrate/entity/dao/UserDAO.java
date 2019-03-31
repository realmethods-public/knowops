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
import java.util.logging.Logger;

import com.cloudmigrate.entity.User;

import com.cloudmigrate.foundational.common.exception.FrameworkDAOException;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Implements the Hibernate persistence processing for business entity User.
 * 
 * @author realMethods, Inc.
 */

public class UserDAO extends BaseDAO {
	/**
	 * default constructor
	 */
	public UserDAO() {
		// nothing to initialize
	}

	/**
	 * Retrieves a User from the persistent store, using the provided primary
	 * key. If no match is found, a null User is returned.
	 * <p>
	 * 
	 * @param pk
	 * @return User
	 * @exception FrameworkDAOException
	 */
	public User findUser(Long pk) throws FrameworkDAOException {
		if (pk == null) {
			throw new FrameworkDAOException("UserDAO.findUser(...) cannot have a null primary key argument");
		}

		Query query = null;
		User businessObject = null;

		StringBuilder fromClause = new StringBuilder("from com.cloudmigrate.entity.User as field where ");

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
				businessObject = (User) query.list().iterator().next();
			}

			commitTransaction(tx);
		} catch (Exception exc) {
			businessObject = null;
			throw new FrameworkDAOException("UserDAO.findUser failed for primary key " + pk + " - " + exc);
		} finally {
			closeSession();
		}

		return (businessObject);
	}

	/**
	 * Retrieves a User from the persistent store, using the token to match the unique internal identifier for the 
	 * user when created. If no match is found, a null User is returned.
	 * <p>
	 * 
	 * @param token
	 * @return User
	 * @exception FrameworkDAOException
	 */
	public User findUserByToken( String token ) throws FrameworkDAOException {
		if (token == null) {
			throw new FrameworkDAOException("UserDAO.findUser(...) cannot have a null token argument");
		}

		Query query = null;
		User businessObject = null;

		StringBuilder fromClause = new StringBuilder(FROM_CLAUSE);

		Session session = null;
		Transaction tx = null;

		try {
			session = currentSession();
			tx = currentTransaction(session);
			fromClause.append("user.internalIdentifier = '" + token + "'");
			query = session.createQuery(fromClause.toString());

			if (query != null) {
				List<User> list = query.list();
				if ( !list.isEmpty() )
					businessObject = list.get(0);
				else {
					final String msg = "Unable to locate User using token " + token;
					LOGGER.info(msg);
				}
			}			
			commitTransaction(tx);
		} catch (Exception exc) {
			businessObject = null;
			throw new FrameworkDAOException("UserDAO.findUserByToken failed for token " + token + " - " + exc);
		} finally {
			closeSession();
		}

		return (businessObject);
	}

	/**
	 * Retrieves a User from the persistent store, using a userId
	 * If no match is found, a null User is returned.
	 * <p>
	 * 
	 * @param userId
	 * @return User
	 * @exception FrameworkDAOException
	 */
	public User findUserByUserId( String userId ) throws FrameworkDAOException {
		if (userId == null) {
			throw new FrameworkDAOException("UserDAO.findUserByUserId(...) cannot have a null userId argument");
		}

		Query query = null;
		User businessObject = null;

		StringBuilder fromClause = new StringBuilder(FROM_CLAUSE);

		Session session = null;
		Transaction tx = null;

		try {
			session = currentSession();
			tx = currentTransaction(session);
			fromClause.append("user.userId = '" + userId + "'");
			query = session.createQuery(fromClause.toString());

			if (query != null) {
				List<User> list = query.list();
				if ( !list.isEmpty() )
					businessObject = list.get(0);
				else {
					final String msg = "Unable to locate User using userId " + userId;
					LOGGER.info(msg);
				}
			}
			
			commitTransaction(tx);
		} catch (Exception exc) {
			businessObject = null;
			throw new FrameworkDAOException("UserDAO.findUserByUserId failed for userId " + userId + " - " + exc);
		} finally {
			closeSession();
		}

		return (businessObject);
	}

	/**
	 * Retrieves a User from the persistent store, using the userId and password.
	 * If no match is found, a null User is returned.
	 * 
	 * @param userId
	 * @param password
	 * @return User
	 * @exception FrameworkDAOException
	 */
	public User findUserByCredentials( String userId, String password) throws FrameworkDAOException {
		if (userId == null) {
			throw new FrameworkDAOException("UserDAO.findUserByCredentials(...) cannot have a null userId argument");
		}

		if (password == null) {
			throw new FrameworkDAOException("UserDAO.findUserByCredentials(...) cannot have a null password argument");
		}

		Query query = null;
		User businessObject = null;

		StringBuilder fromClause = new StringBuilder(FROM_CLAUSE);

		Session session = null;
		Transaction tx = null;

		try {
			session = currentSession();
			tx = currentTransaction(session);
			fromClause.append("user.userId = '" + userId + "'");
			fromClause.append(" and ");
			fromClause.append("user.password = '" + password + "'");
			
			query = session.createQuery(fromClause.toString());

			if (query != null) {
				List<User> list = query.list();
				if ( !list.isEmpty() )
					businessObject = list.get(0);
				else {
					final String msg = "Unable to locate User using credentials " + userId + " : " + password;
					LOGGER.info(msg);
				}
			}
			
			commitTransaction(tx);
		} catch (Exception exc) {
			businessObject = null;			
			throw new FrameworkDAOException("UserDAO.findUserByCredentials failed for userId " + userId + " - " + exc);
		} finally {
			closeSession();
		}

		return (businessObject);
	}

	/**
	 * returns a Collection of all Users
	 * 
	 * @return List<User>
	 * @exception FrameworkDAOException
	 */
	public List<User> findAllUser() throws FrameworkDAOException {
		ArrayList<User> list = new ArrayList<>();
		Query query = null;
		StringBuilder buf = new StringBuilder("from com.cloudmigrate.entity.User");

		try {
			Session session = currentSession();

			query = session.createQuery(buf.toString());

			if (query != null) {
				list = (ArrayList<User>) query.list();
			}
		} catch (Exception exc) {
			throw new FrameworkDAOException("UserDAO.findAllUser failed - " + exc);
		} finally {
			closeSession();
		}

		if (list.isEmpty()) {
			LOGGER.info("UserDAO:findAllUsers() - List is empty.");
		}

		return (list);
	}

	/**
	 * Inserts a new User into the persistent store. 
	 * 
	 * First requests it to generate its random internal identifier.
	 * 
	 * @param businessObject
	 * @return newly persisted User
	 * @exception FrameworkDAOException
	 */
	public User create(User businessObject) throws FrameworkDAOException {
		super.create( businessObject );		
		return (businessObject);
	}

	/**
	 * Stores the provided User to the persistent store.
	 * 
	 * @param businessObject
	 * @return User stored entity
	 * @exception FrameworkDAOException
	 */
	public User save(User businessObject) throws FrameworkDAOException {
		super.save(businessObject);
		return (businessObject);
	}

	/**
	 * Removes a User from the persistent store.
	 * 
	 * @param pk
	 *            identity of object to remove
	 * @exception FrameworkDAOException
	 */
	public void delete(Long pk) throws FrameworkDAOException {
		super.delete(findUser(pk));
	}

	// *****************************************************
	// Attributes
	// *****************************************************
	private static final String FROM_CLAUSE	= "from com.cloudmigrate.entity.User as user where ";
	private static final Logger LOGGER		= Logger.getLogger(UserDAO.class.getName());

}
