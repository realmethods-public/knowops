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
package com.cloudmigrate.foundational.common.properties;

import java.io.*;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import java.net.URL;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hibernate.cfg.*;

import com.cloudmigrate.codetemplate.xmi.XMIProvider12;
import com.cloudmigrate.foundational.common.FrameworkBaseObject;

import com.cloudmigrate.foundational.common.exception.InitializationException;
import com.cloudmigrate.foundational.common.exception.XMLFileParsingException;

import com.cloudmigrate.foundational.common.namespace.FrameworkNameSpace;

/**
 * Handles the duty of getting the framework related configuration files loaded, by reading the
 * contents of the config.xml file, and the creating the specified propery handlers.
 * <p>
 * @author    realMethods, Inc.
 */
public class PropertyFileLoader extends FrameworkBaseObject
{
	
// constructors
	
	/**
	 * default constructor
	 */
	protected PropertyFileLoader()
	{
		propertyHandlers = Collections.synchronizedMap( new HashMap() );
	}
	
	/**
	 * Factory method
	 * <p>
	 * @return PropertyFileLoader
	 */
	public static synchronized PropertyFileLoader getInstance()
	{
		if ( instance == null ) {
			// try to first get it from JNDI if already loaded
			instance = seeIfLoadedInJNDI();

			if ( instance == null  ) {
				instance = new PropertyFileLoader();
				try {
					if ( !FrameworkNameSpace.GOOGLE_LICENSE_ENV )
						new InitialContext().bind( PROPERTYFILELOADER_KEY, instance );
				}
				catch( Exception exc ) {
					LOGGER.log(Level.WARNING, "Property File Loading Failure", exc);
				}
			}
		}	
		
		return instance;			
	}
	
	/**
	 * Delegates to the overloaded loadPropertyFiles( java.net.URL ).
	 * 
	 * <p>
	 * @exception	InitializationException		thrown if unable to parser config.xml
	 */
	public void loadPropertyFiles()
	throws InitializationException
	{
		// no_op
	} 
		
	/**
	 * Attempts to load configuration files, referenced by config.xml
	 * The provided URL represents the location of the config.xml.
	 * <p>
	 * @param		rootURL		root location of the configuration files, provided as a URL.  If
	 * 							NULL, the classpath is used and the -DFRAMEWORK_HOME property...
	 * @exception	InitializationException		thrown if unable to parser config.xml
	 */
	public void loadPropertyFiles( URL rootURL )	
	throws InitializationException
	{		
		if ( !hasLoadedPropertyfiles()  )
		{	
			this.rootURL = rootURL;
			
//			try {
				// parseConfigXMLFile();
			//}
			//catch( XMLFileParsingException exc ) {
				//throw new InitializationException( "PropertyFileLoader.loadPropertyFiles() - " + exc, exc );
		//	}
			
			// loop through the configFileParamMappings	
/*			Iterator iter = configFileParamMappings.iterator();
			HashMap configParam = null;

			while( iter.hasNext() ) { 		
				configParam	= (HashMap)iter.next();
				bindWithPropertyHandler( (String)configParam.get( "name" ), 
								(String)configParam.get( "handler" ), 
								loadProperties( (String)configParam.get( "file" ) ) );																	
			}
*/			
			if ( FrameworkNameSpace.USING_HIBERNATE )
				loadHibernateConfiguration();		
		}
		else	
			LOGGER.warning("PropertyFileLoader has already preloaded the standard property files." );
		
	}

	/**
	 * Indicator for property files having been preloaded.
	 * <p>
	 * @return	boolean
	 */
	public boolean hasLoadedPropertyfiles() {
		return( !propertyHandlers.isEmpty() );
	}
	
	/**
	 * Closes all cached property files
	 */
	public void unload() {
		// closes all cached InputStreams...
		synchronized( propertyHandlers ) {		
			Iterator keys 				= propertyHandlers.keySet().iterator();
			IPropertiesHandler handler	= null;
			String name					= null;
			
			while ( keys.hasNext() ) {		
				name 	= (String)keys.next();	
				handler = (IPropertiesHandler)propertyHandlers.get(name);
						
				logInfoMessage( "Done unloading with " + name + " property handler  --"  );
				
				handler.doneNotification();
			}
		}	
	}
			
	/**
	 * Returns the framework property handler.
	 * @return framework property handler
	 */
	public IFrameworkPropertiesHandler getFrameworkPropertiesHandler() {
		return( (IFrameworkPropertiesHandler)propertyHandlers.get( "framework" ) );		
	}
	
		
	/**
	 * Returns the connection pool property handler.
	 * @return connection pool property handler
	 */
	public IConnectionPoolPropertiesHandler getConectionPoolPropertiesHandler() {
		return( (IConnectionPoolPropertiesHandler)propertyHandlers.get( "connectionpool" ) );		
	}
	
	/**
	 * Returns the security property handler.
	 * @return security property handler
	 */
	public ISecurityPropertiesHandler getSecurityPropertiesHandler() {
		return( (ISecurityPropertiesHandler)propertyHandlers.get( "security" ) );		
	}
	
		
	/**
	 * Returns the log property handler.
	 * @return log property handler
	 */
	public ILogPropertiesHandler getLogPropertiesHandler() {
		return( (ILogPropertiesHandler)propertyHandlers.get( "loghandlers" ) );		
	}

	/**
	 * Returns the task property handler.
	 * @return task property handler
	 */
	public ITaskPropertiesHandler getTaskPropertiesHandler() {
		return( (ITaskPropertiesHandler)propertyHandlers.get( "task" ) );		
	}
	

	/**
	 * Returns the esb property handler.
	 * @return esb handler
	 */
	public IESBPropertiesHandler getESBPropertiesHandler() {
		return( (IESBPropertiesHandler)propertyHandlers.get( "esb" ) );		
	}	
	/**
	 * Returns the loaded property handlers.
	 * @return	property handlers
	 */
	public Map getPropertyHandlers() {
		return( propertyHandlers );
	}
	
	/**
	 * Returns the Hibernate Configuration
	 * @return	Configuration
	 */
	public Configuration getHibernateConfiguration() {
		return( hibernateConfiguration );
	}
	
	public InputStream getJDOInputStream() {
		return( jdoInputStream );
	}
	
	/**
	 * Loads the JDO jdoconfig.xml file.
	 * If the rootURL has been provided, it is used as the base of the search.
	 * If not found there, looks in the classpath.  If still not found
	 * looks in the directory specified by the -DFRAMEWORK_PROPERTY system variable.
	 */
	protected void loadJDOConfiguration() {
		try {
			jdoInputStream = loadProperties( "jdoconfig.xml" );
		}
		catch( Exception exc )
		{
			LOGGER.log( Level.WARNING, "Failed to load jdoconfig.xml ", exc );
		}
	}
	/**
	 * Loads the Hibernate Configuration file.
	 * If the rootURL has been provided, it is used as the base of the search.
	 * If not found there, looks in the classpath.  If still not found
	 * looks in the directory specified by the -DFRAMEWORK_PROPERTY system variable.
	 */			
	protected void loadHibernateConfiguration() {
		String fileName = FrameworkNameSpace.getHibernateCfgFile();

		hibernateConfiguration = loadHibernateConfigNormally( fileName );
		
		if ( hibernateConfiguration == null && rootURL != null )	 {						
			hibernateConfiguration = loadHibernateConfigFromRootURL( fileName );
		}
				
		if ( hibernateConfiguration == null  ) { // try the FRAMEWORK_HOME
			hibernateConfiguration = loadHibernateConfigFromFrameworkHome( fileName );
		}
		
		if ( hibernateConfiguration != null  ) {
			final String msg = "-- Successfully loaded hibernate config file " + fileName ;
			LOGGER.info(msg);			
		}	
		else {
			final String msg = "**\nFailed to load hibernate config file " + fileName 
						+ ".\nIf required, Hibernate related operations will not work.**";
			LOGGER.severe(msg);
		}										
	}
	
	/**
	 * Loads the config file contents into an InputStream, and caches it by name.
	 * <p>
	 * @param	fileName
	 * @exception	InitializationException
	 */			
	protected InputStream loadProperties( String fileName )
	throws InitializationException
	{
		String msg = "Loading property file " + fileName;
		LOGGER.info( msg );

		InputStream inputStream = openPropertiesStreamNormally(fileName);
		
		if (inputStream == null ) {
			inputStream = openPropertiesStreamFromRootURL(fileName);
		}
	
		if( inputStream == null ) {
			inputStream = openPropertiesStreamFromClassPath(fileName);
		}
		
		if( inputStream == null ) {
			inputStream = openPropertiesStreamFromFrameworHome(fileName);
		}

		if ( inputStream != null ) {
			msg = "Done loading property file " + fileName;
			LOGGER.info(msg);		
			return( inputStream );
		}
		else {
			msg = "Failed to load " + fileName;
			throw new InitializationException(msg);
		}
	}

	
	/**
	 * @exception	XMLFileParsingException
	 */			
	protected void parseConfigXMLFile()
	throws XMLFileParsingException
	{			
		IConfigPropertiesHandler handler = null;
		
		try {
			handler = (IConfigPropertiesHandler)FrameworkPropertiesHandlerFactory.getInstance().getPropertyHandler( "com.cloudmigrate.foundational.common.properties.ConfigXMLPropertiesHandler" );
			
			if ( configFileStream == null )
				configFileStream = loadProperties( CONFIG_FILE_NAME );			
			
			handler.parse( configFileStream );
		}
		catch( Exception exc ) {
			throw new XMLFileParsingException( "PropertyFileLoader.parseConfigXMLFile() - failed to load " + CONFIG_FILE_NAME + ".  Validate the files location -  " + exc, exc );
		}
		
		try {
			configFileParamMappings = handler.getAppConfigFiles();

			if ( System.getProperty("DEBUG") != null ) {
				final String msg = "configFileParamMappings = " + configFileParamMappings;
				LOGGER.info(msg);
			}
		}
		catch( Exception exc ) {
			throw new XMLFileParsingException( "PropertyFileLoader.parseConfigXMLFile() - " + exc );			
		}			    
	}
	
	
	/**
	 * Acquires the handler by name from the FrameworkPropertiesHandlerFactory, and then
	 * provides the inputStream for binding, using the name as the root.
     * <p>
     * @param	name		unique identifier of the property handler
	 * @param	handler		class name to handle loading the properties from a given inputStream
	 * @param   inputStream	stream representation of a configuration file
	 * @throws InitializationException
	 */
	private void bindWithPropertyHandler( String name, String handler, InputStream inputStream )	
	throws InitializationException
	{
		// get the property handler
		IPropertiesHandler propsHandler = null;
		try {		
			propsHandler = FrameworkPropertiesHandlerFactory.getInstance().getPropertyHandler( handler );
			propsHandler.parse( inputStream );		

			// cache the property handler						
			propertyHandlers.put( name, propsHandler );
		}
		catch( Exception exc ) {
			LOGGER.log(Level.WARNING, "Failed to load a properties handler", exc );
			throw new InitializationException( "PropertyFileLoader.bindWithPropertyHandler()", exc );
		}		
	}
	
	protected static PropertyFileLoader seeIfLoadedInJNDI()
	{
		PropertyFileLoader loader = null;
		try {
			if( !FrameworkNameSpace.GOOGLE_LICENSE_ENV )
			{
				InitialContext initialContext = new InitialContext();
				loader = (PropertyFileLoader)initialContext.lookup( PROPERTYFILELOADER_KEY );
			}
		}
		catch( NamingException exc ) {
			// no_op
		}

		return( loader );
	}
	
	protected InputStream openPropertiesStreamNormally(String fileName) {
		try (InputStream inputStream = new DataInputStream( Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName))) {
			final String msg = LOOKING_FOR_CONFIG_FILE + fileName + IN_THE_CLASS_PATH;
			LOGGER.info(msg);
			return inputStream;
		}
		catch( Exception exc) {
			final String msg = "Failed to normally open config file " + fileName;
			LOGGER.warning(msg);
		}
		return null;
	}

	protected InputStream openPropertiesStreamFromRootURL(String fileName) {
		String msg = LOOKING_FOR_CONFIG_FILE + fileName + " from the rootURL" + rootURL.toString();
		LOGGER.info(msg);

		try (InputStream inputStream = new java.net.URL( rootURL.toString() + "/" + fileName ).openStream()){	
			return inputStream;
		}
		catch( Exception exc ) {
			msg = "Failed  to load config file from Root URL" + rootURL.toString() + "/" + fileName; 
			LOGGER.warning(msg);			 
		}			
		return null;
	}
	
	protected InputStream openPropertiesStreamFromClassPath(String fileName) {
		String msg = LOOKING_FOR_CONFIG_FILE + fileName + " in the classpath...";
		LOGGER.info(msg);
		
		try( InputStream inputStream = getClass().getClassLoader().getResourceAsStream( fileName ) ){
			return inputStream;
		}catch( Exception exc ) {
			msg = "PropertyFileLoader.loadProperties(...) - failed  to load config file " 
					+ rootURL.toString() + "/" + fileName + ".  Will look for " + fileName + IN_THE_CLASS_PATH ; 
			LOGGER.warning(msg);			 			
		}
		return null;
	}
	
	protected InputStream openPropertiesStreamFromFrameworHome(String fileName) {
		String msg = LOOKING_FOR_CONFIG_FILE + fileName + " in the " + FrameworkNameSpace.FRAMEWORK_HOME + "...";
		LOGGER.info(msg);
		
		try (InputStream inputStream = new java.net.URL( "file://" + java.io.File.separatorChar + FrameworkNameSpace.FRAMEWORK_HOME + java.io.File.separatorChar + fileName ).openStream()){		
			return inputStream;
		}
		catch( Exception exc ) {
			msg = "Failed  to load config file " + fileName + " looking in " + FrameworkNameSpace.FRAMEWORK_HOME;
			LOGGER.warning(msg);
		}			
		return null;
	}
	
	private Configuration loadHibernateConfigNormally( String fileName ) {
		Configuration config = null; 
		
		try {				
			// try the default which uses the classpath
			config = new Configuration().configure();
			final String msg = DONE_LOADING_CONFIG_FILE + fileName + " from the classpath";
			LOGGER.info( msg );
		}
		catch( Exception exc ) {
			final String msg = FAILED_LOAD_CONFIG_FILE + fileName + IN_THE_CLASS_PATH;
			LOGGER.log(Level.WARNING, msg, exc);
		}
		
		return config;
	}
	
	private Configuration loadHibernateConfigFromRootURL( String fileName ) {
		Configuration config = null;
		
		try {
			String msg =  "Loading hibernate config file " + fileName + " from the WAR file...";
			LOGGER.info(msg);				
			config = new Configuration().configure( new java.net.URL(rootURL.toString() + fileName) );
			msg = DONE_LOADING_CONFIG_FILE + fileName + " from the WAR file...";
			LOGGER.info(  msg );			
		}
		catch( Exception exc ) {
			final String msg = "** Failed to load hibernate config file " + rootURL.toString() 
				+ "/hibernate/" + fileName + " from the WAR file due to " 
				+ exc + ".  Will try loading " + fileName + " from the classpath.";
			LOGGER.severe( msg );				 
		}
		
		return config;
	}

	private Configuration loadHibernateConfigFromFrameworkHome( String fileName ) {
		Configuration config = null;

		try {						
			String msg =  "Loading hibernate config file " + fileName 
					+ " from System property -DFRAMEWORK_HOME=" + FrameworkNameSpace.FRAMEWORK_HOME;
			LOGGER.info(msg);
			
			config = new Configuration()
						.configure( new java.net.URL("file://" + java.io.File.separatorChar 
								+ FrameworkNameSpace.FRAMEWORK_HOME + java.io.File.separatorChar + fileName) );
			
			msg = DONE_LOADING_CONFIG_FILE + fileName 
					+ " from System property -DFRAMEWORK_HOME=" + FrameworkNameSpace.FRAMEWORK_HOME;
			LOGGER.info(msg);
		}
		catch( Exception exc ) {
			// no_op
		}			
		return config;
	}
	
	// attributes
	
	/**
	 * singleton instance
	 */		
	static PropertyFileLoader instance = null;
				
	/**
	 * root location of config files
	 */				
	private java.net.URL rootURL = null;
		
	/**
	 * Map of property handler names / property handler pairing
	 */
	protected transient Map propertyHandlers 					= null;					
	protected transient Collection configFileParamMappings 		= null;
	protected transient InputStream configFileStream 			= null;
	protected transient Configuration hibernateConfiguration 	= null;
	protected transient InputStream jdoInputStream 				= null;
		
	/**
	 * standard configuration file that points to the main config files in the system
	 */
	public static final String CONFIG_FILE_NAME 		= "config.xml";
	public static final String PROPERTYFILELOADER_KEY 	= "PropFileLoader";
	public static final String LOOKING_FOR_CONFIG_FILE	= "Looking for config file ";
	public static final String IN_THE_CLASS_PATH		= " in the classpath";
	protected static final String DONE_LOADING_CONFIG_FILE = "Done loading hibernate config file ";
	protected static final String FAILED_LOAD_CONFIG_FILE = "Failed to load hibernate config file ";
	private static final Logger LOGGER 					= Logger.getLogger(XMIProvider12.class.getName());

}

