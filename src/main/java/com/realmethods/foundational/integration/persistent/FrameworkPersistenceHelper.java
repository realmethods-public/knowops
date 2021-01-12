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
package com.realmethods.foundational.integration.persistent;

import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.*;
import org.hibernate.service.*;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.realmethods.foundational.common.namespace.FrameworkNameSpace;


/**
 * Helper class to help get the persistence layer activated during server start up.
 * 
 * For example, typically a Servlet will invoke this method within it's init() method
 * @author realMethods, Inc.
 *
 */
public class FrameworkPersistenceHelper
{

	/**
	 * Factory method
	 * 
	 * @return
	 */
    public static FrameworkPersistenceHelper self() {
    	if ( self == null ) {
    		self = new FrameworkPersistenceHelper();
    		createSessionFactory();
    	}
    	return( self );
    }
    
    /**
     * Returns the sessionFactory field.
     * 
     * @return
     */
    public SessionFactory getSessionFactory() {
    	if ( sessionFactory == null || sessionFactory.isClosed() )
    		createSessionFactory();
    	
        return sessionFactory;
    }
    
    /**
     * Returns the current Hibernate Session for the current Thread
     * @return Session
     */
    public Session getCurrentSession() {
    	Session s = (Session) session.get();

    	// Open a new Session, if this Thread has none yet or has been closed or disconnected
		if (s == null || !s.isOpen() || !s.isConnected() ) 
		{
			// Note: dynamically create the class Interceptor and apply here,			
			// if one is in use ...			
			s = getSessionFactory().openSession( /*FrameworkHibernatorInterceptorFactory.getInstance().getHibernateInterceptor()*/);
			session.set(s);
			s.setHibernateFlushMode( FlushMode.COMMIT );
		}
		return s;    	
    }
    
    /**
     * Close the Hibernate Session if open
     */
    public void closeSession() {
		Session s = (Session) session.get();
		session.set(null);
		
		if (s != null) {
			s.close();
		}
    }

    /**
     * Creates the Hibernate session factory by locating the hibernate.cfg.xml file in the classpath.
     */
	protected static void createSessionFactory() {
	    try {
            // up front, create the SessionFactory from providided hibernate configuraetion file since it takes a bit to load
	    	StandardServiceRegistryBuilder standardRegistryBuilder = new StandardServiceRegistryBuilder().configure(FrameworkNameSpace.getHibernateCfgFile());
            String envVar = null;
            
	    	LOGGER.log(Level.INFO, "Using hibernate file {0}", FrameworkNameSpace.getHibernateCfgFile() );
	    	
	    	// handle DATABASE_URL
	    	envVar = System.getenv(DATABASE_URL);
            if ( envVar != null ) {
            	LOGGER.log(Level.INFO, "Using Database Url {0}", envVar );
            	
            	//////////////////////////////////////////////////
            	// try to fix-it up into a JDBC format for MySQL
            	//////////////////////////////////////////////////
            	            	
            	// only touch if the URL is not intentionally JDBC formatted
            	if ( envVar.startsWith("jdbc") == false ) {
            		// this assumes only the MySQL server IP Address is provided
            		
            		// check for 3306
            		if ( envVar.endsWith( MYSQL_PORT ) == false )
            			envVar = envVar + MYSQL_PORT;
            		
            		// envVar is now simply the URL with port to a MySQL server instance
            		envVar = "jdbc:mysql://" + envVar + REALMETHODS_TABLENAME + "?" +  MYSQL_ARGS;            		
            	}
            	
            	standardRegistryBuilder = standardRegistryBuilder.applySetting("hibernate.connection.url", envVar );
            }
            
            // handle DATABASE_USERNAME
            envVar = System.getenv(DATABASE_USERNAME); 
            if ( envVar != null ) {
            	LOGGER.log(Level.INFO, "Using Database Name {0}", envVar );
            	standardRegistryBuilder = standardRegistryBuilder.applySetting("hibernate.connection.username", envVar );
            }
            
            // handle DATABASE_PASSWORD
            envVar = System.getenv(DATABASE_PASSWORD);             
            if ( envVar != null ) {
            	LOGGER.log(Level.INFO, "Using Database Password {0}", envVar );
            	standardRegistryBuilder = standardRegistryBuilder.applySetting("hibernate.connection.password", envVar );
            }
            
            // handle DATABASE_DIALECT
            envVar = System.getenv(DATABASE_DIALECT);
            if ( envVar != null ) {
            	LOGGER.log(Level.INFO, "Using Database Dialet{0}", envVar );
            	standardRegistryBuilder = standardRegistryBuilder.applySetting("hibernate.dialect", envVar );
            }
            
            // handle DATABASE_DRIVER
            envVar = System.getenv(DATABASE_DRIVER);
            if ( envVar != null ) {
            	LOGGER.log(Level.INFO, "Using Database Driver {0}", envVar );
            	standardRegistryBuilder = standardRegistryBuilder.applySetting("hibernate.connection.driver_class", envVar );
            }
            
            StandardServiceRegistry standardRegistry = standardRegistryBuilder.build();
            Metadata metaData = new MetadataSources(standardRegistry).getMetadataBuilder()
                                                                     .build();
            sessionFactory = metaData.getSessionFactoryBuilder().build();            
    	}
	    catch (Exception exc) {
	    	sessionFactory = null;
	    	LOGGER.log( Level.SEVERE, "Warning!!! - Hibernate Session Failed to Initialize", exc);
	    }
		
	}
		
	// attributes
    private static final ThreadLocal session 		= new ThreadLocal();
    private static FrameworkPersistenceHelper self 	= null;
    private static SessionFactory sessionFactory	= null;
    private static final String DATABASE_URL		= "DATABASE_URL";
    private static final String DATABASE_USERNAME	= "DATABASE_USERNAME";
    private static final String DATABASE_PASSWORD	= "DATABASE_PASSWORD";
    private static final String DATABASE_DIALECT	= "DATABASE_DIALECT";
    private static final String DATABASE_DRIVER		= "DATABASE_DRIVER";
    private static final String MYSQL_ARGS			= "createDatabaseIfNotExist=true&autoReconnect=true&useSSL=false&allowPublicKeyRetrieval=true";
    private static final String MYSQL_PORT			= ":3306";
    private static final String REALMETHODS_TABLENAME = System.getProperty("realmethods.tablename");
	private static final Logger LOGGER = Logger.getLogger(FrameworkPersistenceHelper.class.getName());
	
}
