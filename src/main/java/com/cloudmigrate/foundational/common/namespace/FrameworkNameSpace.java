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
package com.cloudmigrate.foundational.common.namespace;

/**
 * Provided as a central location for static names in the system.  Once the application is
 * started, they shouldn't require modification.  
 * <p> 
 * @author    realMethods, Inc.
 */
public class FrameworkNameSpace
{
    /**
     * default constructor - deter instantiation
     */
    protected FrameworkNameSpace()
    {}  
	
    public static String getHibernateCfgFile() {
    	if ( HIBERNATE_CONFIG_FILE == null ) {
    		if ( IN_PRODUCTION )
    			HIBERNATE_CONFIG_FILE = "hibernate.cfg.xml";
    		else
    			HIBERNATE_CONFIG_FILE = "hibernate.cfg.dev.xml";
    	}
    	    	
    	return HIBERNATE_CONFIG_FILE;
    }
	
    protected static String HIBERNATE_CONFIG_FILE  = null;

    /**
	 * SMTP related
	 */
    public static final String SMTP_HOST_NAME 		= "SMTP_HOST_NAME";
    public static final String SMTP_AUTH_USER 		= "SMTP_AUTH_USER";
    public static final String SMTP_AUTH_CREDENTIAL = "SMTP_AUTH_CREDENTIAL";
    public static final String SMTP_DEBUG 			= "SMTP_DEBUG";
    public static final String SMTP_PORT 			= "SMTP_PORT";
    
			
	/*
	 * framework.property indicator to use internal caching within the dao abstraction
	 */
	public static final String DAO_CACHE_INTERNALLY  	= "DAO_CACHE_INTERNALLY";
	
	/**
	 * failure during token validation
	 */		
	public static final String TOKEN_VALIDATION_ERROR  = "TOKEN_VALIDATION_ERROR";

	/**
	 * duplicator form submission error
	 */		
	public static final String DUPLICATE_FORM_SUBMISSION_ERROR  = "DUPLICATE_FORM_SUBMISSION_ERROR";
		
	/**
	 * unique session key for the token
	 */		
	public static final String TOKEN_KEY  					= "com.cloudmigrate.foundational.common.namespace.TokenKey";
	/**
	 * framework.xml category for startup class specification
	 */
	public static final String STARTUPS = "FrameworkStartups";
		 
	/**
	 * name ascribed to the security related IFramworkStartup implementation
	 */	 
	public static final String SECURITY_PROPERTIES  		= "security";
	
	/**
	 * framework.xml property name for the uid generator implementation
	 */
	public static final String FRAMEWORK_UID_GENERATOR 		= "FrameworkUIDGenerator";
	
	/**
	 * framework.xml property name for the default db conn. name, as
	 * specified in the connectionpool.properties.
	 */
	public static final String DEFAULT_CONNECTION_NAME 		= "DefaultDatabaseConnectionName";

	/**
	 * framework.xml property name for the default java.sql.Time format.
	 */
	public static final String DEFAULT_TIME_FORMAT 			= "DefaultTimeFormat";
	
	/**
	 * framework.xml property name for the default java.sql.Date format.
	 */
	public static final String DEFAULT_DATE_FORMAT 			= "DefaultDateFormat";
	
	/**
	 * framework.xml property name for the default java.sql.Timestamp format.
	 */
	public static final String DEFAULT_TIMESTAMP_FORMAT 	= "DefaultTimestampFormat";
	
    /**
     * global identifier for the Application MBean Domain
     * default value is "rMFramework"
     */
    public static final String MBEAN_DOMAIN					= "rMFramework";    

    /**
     * global identifier for the name of the servlet
     */
    public static final String SERVLET_NAME 				= "ServletName";    

    /**
     * global identifier for the type of objects to clear from the cache
     */
    public static final String USOM_CACHE_CLEAR_TYPE 		= "USOM_CACHE_CLEAR_TYPE";    

    /**
     * global identifier for the MAX num entries to allow in the USOM before refresh
     */
    public static final String USOM_MAX 					= "USOM_MAX";    

    /**
     * global identifier for the frequency, in milliseconds, to check the USOM size
     */
    public static final String USOM_MAX_CHECK_PERIOD_IN_MILLIS = "USOM_MAX_CHECK_PERIOD_IN_MILLIS";    
	
    /**
     * global identifier for the JMX Server to Register MBeans with
     */
    public static final String JMX_MBEAN_SERVER_IMPLEMENTATION = "JMX_MBEAN_SERVER_IMPLEMENTATION";    

    /**
     * global identifier for the FrameworkEvents being enabled
     */
    public static final String FRAMEWORK_EVENTS_ENABLED 	= "FrameworkEventsEnabled";    

    /**
     * global identifier for the SOAP Server URI
     */
    public static final String SOAP_SERVER_URI 				= "SOAPServerURI";    
    
    /**
     * global identifier for the Locale in Use
     */
    public static final String USER_LOCALE 					= "cloudMigrate_User_Locale";
    
    /**
     * global named used for the Session variable of IFrameworkCache type
     */
    public static final String APPLICATION_CACHE_NAME 		= "UserSessionObjectManagerClassName";                

    
    /**
     * global for the name of the connection pool entry for the JMS related Topic
     * designated for UserSessionAdministration and UserSession listening
     */
    public static final String JMX_SELF_REGISTRATION 		= "JMXSelfRegistration";                

    /**
     * global for the name of the connection pool entry for the JMS related Topic
     * designated for UserSessionAdministration and UserSession listening
     */
    public static final String USER_SESSION_ADMIN_JMS 		= "UserSessionAdminJMS";    

    /**
     * global for the Framework's default Log Task Handler
     */
    public static final String DEFAULT_LOG_TASK_NAME 		= "FrameworkLogTask";
    
    /**
     * global for the Framework's Logging mechanism
     */
    public static final String DEFAULT_LOG_HANDLER_NAME 	= "DEFAULT_LOG_HANDLER_NAME";

    /**
     * global for the Framework's TaskMessageDriveBeanWrapper in use indicator
     */
    public static final String TASK_MDB_WRAPPER_IN_USE 		= "TASK_MDB_WRAPPER_IN_USE";

    /**
     * global for the Framework's Log4J FrameworkCategory configuration parameters
     */
    public static final String ROOT_LOG4J_CATEGORY 			= "Framework Root Category";    

    /**
     * global for the Framework's Log4J default configuration parameters
     */
    public static final String DEFAULT_LOG4J_CONFIGURATION_FILE_PREFIX = "log4j";    

    /**
     * global for the Framework's JNDI EJB name prefix 
     */
    public static final String JNDI_EJB_NAME_PREFIX 		= "JNDIEJBNamePrefix";

    /**
     * global for the Framework's JNDI related args
     */
    public static final String JNDI_ARGS					= "JNDI_ARGS";

    /**
     * global default LDAP Connection Name to use when
     * performing LDAP related actions, such as authentication, if applicable
     */
    public static final String DEFAULT_LDAP_CONNECTION_NAME = "DEFAULT_LDAP_CONNECTION_NAME";    
    
    /**
     * global for the Framework's connectionpool template file
     */
    public static final String CONNECTION_POOL_TEMPLATE 	= "connectionpool.template";
    
    /**
     * Framework's common property file  
     */
    public static final String FRAMEWORK_PROPERTIES_FILE 	= "framework.xml";
    
    /**
     * JNDI name reference for the Framework's properties
     */
    public static final String FRAMEWORK_PROPERTIES	 		= "framework";
    
    /**
     * global connection pool properties file
     */
    public static final String CONNECTION_PROPERTIES_FILE_NAME = "connectionpool.properties";
    
    /**
     * global task related properties file
     */
    public static final String TASK_PROPERTIES_FILE_NAME 	= "task.properties";

    /**
     * global log related properties file
     */
    public static final String LOG_PROPERTIES_FILE_NAME 	= "log.properties";

    /**
     * global security related properties file
     */
    public static final String SECURITY_PROPERTIES_FILE_NAME = "security.properties";
    
    /**
     * global name for the FrameworkLogHandler
     */
    public static final String FRAMEWORK_LOG_HANDLER 		= "logHandler";
    
    /**
     * global name of the Framework as an entity
     */
    public static final String FRAMEWORK_NAME 				= "cloudMigrateFramework";
	
    /**
     * global name of the Framework property UserSessionObjectManager class to dynamically load
     */
    public static final String USOM_CLASS_NAME 				= "UserSessionObjectManagerClassName";
        
    /**
     * global name for the application identifier
     */
    public static final String APPLICATION_ID_TAG 			= "ApplicationID";
    
    /**
     * global JMS Queue Factory related parameters
     */
    public static final String JMS_QUEUE_FACTORY	 		= "JMS_QUEUE_FACTORY";
    
    /**
     * global JMS Topic Factory related parameters
     */
    public static final String JMS_TOPIC_FACTORY 			= "JMS_TOPIC_FACTORY";
        
    /**
     * global name of the Framework Event JMS Queue
     *
     */
     public static final String EVENT_QUEUE_NAME 			= "FrameworkEventJMS";
     
    /**
     * global name of the Framework Task Message JMS Queue
     *
     */
     public static final String TASK_EXECUTION_QUEUE 		= "FrameworkTaskExecutionJMS";
     
     /**
      * global indicator for value object notification
      */
    public static final String VALUE_OBJECT_NOTIFICATION 				= "ValueObjectNotification";    
    public static final String FRAMEWORK_VALUE_OBJECT_NOTIFICATION_JMS 	= "FrameworkValueObjectNotificationJMS";
    
    /**
     * global name of the Framework License File
     *
     */
    public static final String LICENSE_FILE_NAME 			= "framework.license";
     
    /**
     * global name for the Framework Hook Manager
     *
     */
     public static final String FRAMEWORK_HOOK_MANAGER 		= "Hooks";
         
    /**
     * name ascribed to this instance of the Framework.  use the -D argument during
     * the execution of your application. Use the
     * -D parameter when running your application and provide any name,
     * unique if desired.  Uniqueness becomes important for the purpose
     * of having different property settings per instance of the Framework
     * in a clustered environment
     *
     * usage -DFrameworkInstance=MyInstance
     */
    public static final String INSTANCE_NAME 				= "DefaultInstance";
    
    /**
     * home name of this instance of the Framework. Will normally be in the form of a directory
     * structure.  use the -D argument during the execution of your application so the framework.license
     * can be located.
     *
     * usage: -DFRAMEWORK_HOME=c:/myapplication/realMethods/home
     */
    public static final String FRAMEWORK_HOME = 
    		java.lang.System.getProperty( "FRAMEWORK_HOME" ) != null 
    			? java.lang.System.getProperty( "FRAMEWORK_HOME" ).replace( '\\', java.io.File.separatorChar ) 
    			: ".";

    /**
     * name of the framework license file to validate. 
     * If not provided, framework.license is used.  Used in conjunctino with FRAMEWORK_HOME
     *
     * usage: -DFRAMEWORK_LICENSEFILE_NAME=myframework.license
     */
    public static final String FRAMEWORK_LICENSEFILE_NAME = 
    			java.lang.System.getProperty( "FRAMEWORK_LICENSEFILE_NAME" ) != null 
    				? java.lang.System.getProperty( "FRAMEWORK_LICENSEFILE_NAME" ).replace( '\\', java.io.File.separatorChar ) 
    				: LICENSE_FILE_NAME;

    public static final boolean GOOGLE_LICENSE_ENV = 
    			java.lang.System.getProperty( "google.license.env" ) != null;    

    /**
     * Global location of the framework related property files
     */
    public static final String FRAMEWORK_PROPERTIES_LOCATION 	= null;
    public static final boolean USING_JDO 						= java.lang.System.getProperty( "USING_JDO" ) != null;
    public static final boolean USING_HIBERNATE 				= true;
    
	/**
	 * current framework version
	 */
	public static final String VERSION 							= "1.1";	
	public static final String VERSION_SUFFIX 					= "";
	
	/**
	 * Staging Status
	 */
	public static final boolean IN_DEVELOPMENT	= "development".equalsIgnoreCase(java.lang.System.getProperty("staging"))
													? true : false;
	public static final boolean IN_PRODUCTION	= "production".equalsIgnoreCase(java.lang.System.getProperty("staging"))
													? true : false;
	public static final boolean IN_QA			= "qa".equalsIgnoreCase(java.lang.System.getProperty("staging"))
													? true : false;

}
