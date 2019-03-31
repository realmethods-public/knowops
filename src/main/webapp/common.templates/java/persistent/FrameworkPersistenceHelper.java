#header()
package ${aib.getRootPackageName()}.persistent;

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


/**
 * Helper class to help get the persistence layer activated during server start up.
 * 
 * Typically, a servlet will invoke this method within it's init() method
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
    public static FrameworkPersistenceHelper self()
    {
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
    public SessionFactory getSessionFactory() 
    {
    	if ( sessionFactory == null || sessionFactory.isClosed() )
    		createSessionFactory();
    	
        return sessionFactory;
    }
    
    /**
     * Returns the current Hibernate Session for the current Thread
     * @return Session
     */
    public Session getCurrentSession()
    {
    	Session s = (Session) session.get();

    	// Open a new Session, if this Thread has none yet or has been closed or disconnected
		if (s == null || !s.isOpen() || !s.isConnected() ) 
		{
			// Note: dynamically create the class Interceptor and apply here,			
			// if one is in use ...			
			s = getSessionFactory().openSession( /*FrameworkHibernatorInterceptorFactory.getInstance().getHibernateInterceptor()*/);
			session.set(s);
			s.setFlushMode( FlushMode.COMMIT );
		}
		return s;    	
    }
    
    /**
     * Close the Hibernate Session if open
     */
    public void closeSession()
    {
		Session s = (Session) session.get();
		session.set(null);
		
		if (s != null) {
			s.close();
		}
    }

    /**
     * Creates the Hibernate session factory by locating the hibernate.cfg.xml file in the classpath.
     */
	protected static void createSessionFactory()
	{
	    try 
	    {
            // up front, create the SessionFactory from hibernate.cfg.xml since it takes a bit to load
	    	StandardServiceRegistryBuilder standardRegistryBuilder = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml");
            
            if ( System.getenv("DATABASE_URL") != null ) {
            	LOGGER.info( "Using DATABASE_URL " + System.getenv("DATABASE_URL") );
            	standardRegistryBuilder = standardRegistryBuilder.applySetting("hibernate.connection.url", System.getenv("DATABASE_URL") );
            }
            
            if ( System.getenv("DATABASE_USERNAME") != null ) {
            	LOGGER.info( "Using DATABASE_USERNAME " + System.getenv("DATABASE_USERNAME") );
            	standardRegistryBuilder = standardRegistryBuilder.applySetting("hibernate.connection.username", System.getenv("DATABASE_USERNAME") );
            }
            
            if ( System.getenv("DATABASE_PASSWORD") != null ) {
            	LOGGER.info( "Using DATABASE_PASSWORD " + System.getenv("DATABASE_PASSWORD") );
            	standardRegistryBuilder = standardRegistryBuilder.applySetting("hibernate.connection.password", System.getenv("DATABASE_PASSWORD") );
            }
            
            if ( System.getenv("DATABASE_DIALECT") != null ) {
            	LOGGER.info( "Using DATABASE_DIALECT " + System.getenv("DATABASE_DIALECT") );
            	standardRegistryBuilder = standardRegistryBuilder.applySetting("hibernate.dialect", System.getenv("DATABASE_DIALECT") );
            }
            
            if ( System.getenv("DATABASE_DRIVER") != null ) {
            	LOGGER.info( "Using DATABASE_DRIVER " + System.getenv("DATABASE_DRIVER") );
            	standardRegistryBuilder = standardRegistryBuilder.applySetting("hibernate.connection.driver_class", System.getenv("DATABASE_DRIVER") );
            }
            
            StandardServiceRegistry standardRegistry = standardRegistryBuilder.build();
            Metadata metaData = new MetadataSources(standardRegistry).getMetadataBuilder()
                                                                     .build();
            sessionFactory = metaData.getSessionFactoryBuilder().build();    	    		
    	}
	    catch (Throwable exc) 
	    {
	    	sessionFactory = null;
	    	LOGGER.log( Level.SEVERE, "Warning!!! - Hibernate Session Failed to Initialize", exc);
	    }
		
	}
	
	// attributes
    private static final ThreadLocal session 		= new ThreadLocal();
    private static FrameworkPersistenceHelper self 	= null;
    private static SessionFactory sessionFactory	= null;
	private static final Logger LOGGER = Logger.getLogger(FrameworkPersistenceHelper.class.getName());
	
}
