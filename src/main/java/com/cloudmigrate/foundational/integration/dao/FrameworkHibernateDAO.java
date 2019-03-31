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
package com.cloudmigrate.foundational.integration.dao;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.hibernate.*;

import com.cloudmigrate.foundational.business.bo.FrameworkHibernateBusinessObject;
import com.cloudmigrate.foundational.common.FrameworkBaseObject;
import com.cloudmigrate.foundational.common.exception.FrameworkDAOException;
import com.cloudmigrate.foundational.integration.persistent.FrameworkHibernatorInterceptor;
import com.cloudmigrate.foundational.integration.persistent.FrameworkHibernatorInterceptorFactory;

import com.cloudmigrate.foundational.integration.persistent.FrameworkPersistenceHelper;

/**
 * Base class for all Hibernate DAO classes generated and supported
 * by the framework.
 * 
 * @author	cloudMigrate, Inc.
 */
public class FrameworkHibernateDAO   extends FrameworkBaseObject
{
	
	public static SessionFactory getSessionFactory()
	{
		return( FrameworkPersistenceHelper.self().getSessionFactory() );
	}
	
	public Session currentSession() 
	{
		if ( externalSession != null )
			return( externalSession );
		else
		{
			Session s = (Session) session.get();
		
			// Open a new Session, if this Thread has none yet or the session is closed or not connected
			if (s == null || !s.isOpen() || !s.isConnected() ) 
			{
				// Note: dynamically create the class Interceptor and apply here,			
				// if one is in use ...			
				
				s = getSessionFactory().openSession( /*getAuditTrailInterceptor() */);
				session.set(s);
				s.setFlushMode( FlushMode.COMMIT );
			}
			return s;
		}
	}

	public void flushAndCloseSession() {
		if ( externalSession == null ) {
			Session s = (Session) session.get();
			try {
				if ( s != null )
					s.flush();
			} catch( HibernateException exc ) {
				LOGGER.log( Level.SEVERE, "Hibernate Session Flush Failure", exc );
			}
			closeSession();
		}
	}
	
	public void closeSession() 
	{
		if ( externalSession == null )
		{
			Session s = (Session) session.get();
			session.set(null);
			
			if (s != null)
			{
				try {
					s.close();
				} catch( HibernateException exc ) {
					LOGGER.log( Level.SEVERE, "Hibernate Session Closure Failure", exc );
				}
				
			}
		}
	}
	
	protected void create( FrameworkHibernateBusinessObject businessObject ) throws FrameworkDAOException {
		Transaction tx = null;
		Session localSession = null;
		
		try {
			localSession = currentSession();
			tx = currentTransaction(localSession);

			localSession.save(businessObject);
			commitTransaction(tx);

		} catch (Exception exc) {
			rollbackTransaction(tx);
			throw new FrameworkDAOException("FrameworkHibernateDAO.create failed - " + exc);
		} finally {
			flushAndCloseSession();
		}
		
	}
	
	protected void save( FrameworkHibernateBusinessObject businessObject  ) throws FrameworkDAOException {
		Transaction tx = null;
		Session localSession = null;

		try {
			localSession = currentSession();
			tx = currentTransaction(localSession);

			localSession.merge(businessObject);
			commitTransaction(tx);
		} catch (Exception exc) {
			rollbackTransaction(tx);
			throw new FrameworkDAOException("FrameworkHibernateDAO.save failed - " + exc);
		} finally {
			flushAndCloseSession();
		}	
	}

	protected void delete( FrameworkHibernateBusinessObject businessObject  ) throws FrameworkDAOException {
		Transaction tx = null;
		Session localSession = null;

		try {
			localSession = currentSession();
			tx = currentTransaction(localSession);
			localSession.delete(businessObject);
		commitTransaction(tx);
		} catch (Exception exc) {
			rollbackTransaction(tx);
			throw new FrameworkDAOException("FrameworkHibernateDAO.delete failed - " + exc);
		} finally {
			flushAndCloseSession();
		}
	}
	
	protected void commitTransaction( Transaction transaction )
	{
		if ( externalTransaction != transaction )
			transaction.commit();
		
		// do nothing otherwise since the transaction is external to us...
	}

	protected void rollbackTransaction( Transaction transaction )
	{
		if ( externalTransaction != transaction ) {
			try {
				transaction.rollback();
			} catch( HibernateException exc ) {
				LOGGER.log( Level.SEVERE, "Hibernate Transaction Rollback Failure", exc);
			}
		}
			
		
		// do nothing otherwise since the transaction is external to us...
	}
	
	protected Transaction currentTransaction( Session s )
	{
		if ( externalTransaction != null )
			return( externalTransaction );
		else
			return( s.beginTransaction() );
	}
	
	public void assignExternalSession( Session session )
	{
		externalSession = session;
	}
	
	public void assignExternalTransaction( Transaction transaction )
	{
		externalTransaction = transaction;
	}
	
	protected FrameworkHibernatorInterceptor getAuditTrailInterceptor()
	{
		return FrameworkHibernatorInterceptorFactory.getInstance().getHibernateInterceptor();		
	}
	
	// attributes
	public static final ThreadLocal session = new ThreadLocal();
	protected transient Session externalSession = null;
	protected transient Transaction externalTransaction = null;
	private static final Logger LOGGER = Logger.getLogger(FrameworkHibernateDAO.class.getName());
	
}
