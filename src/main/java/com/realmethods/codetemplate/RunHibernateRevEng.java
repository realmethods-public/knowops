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
package com.realmethods.codetemplate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.SystemOutHandler;

import com.realmethods.exception.GenerationException;

/**
 * Uses Hibernate Tools to reverse engineer from an database schema to a set of associated Java class files. Requires
 * typical JDBC connection parameters to connect to the target RDBMS where the schema resides.
 * 
 * @author realMethods, Inc.
 * 
 */
public class RunHibernateRevEng extends AppGenObject {
	/**
	 * empty constructor - inaccessible
	 */
	protected RunHibernateRevEng() {
		// no_op
	}

	/**
	 * constructor
	 * 
	 * @param hibernateProps	database connection properties
	 */
	public RunHibernateRevEng(Properties hibernateProps ) {
		this.hibernateProps = hibernateProps;

		try {
			classesRootDir = new File( RunHibernateRevEng.class.getProtectionDomain()
									.getCodeSource().getLocation().toURI() ).toString();
			buildRootDir = classesRootDir + File.separator +  "hibernate";
		}
		catch( java.net.URISyntaxException exc ) {
			LOGGER.log( Level.WARNING, "Failed to obtain classpath root", exc );
		}
		
		final String msg = "build dir is " + buildRootDir;		
		LOGGER.log( Level.INFO, msg );
	}

	/**
	 * Reverse engineer invocation.
	 * 
	 * @return
	 */
	public String reverse() {
		String msg = "RunHibernateRevnEng().reverse() with build path = " + buildRootDir;
		LOGGER.log( Level.INFO, msg);

		try {
			///////////////////////////////////////////////////////////
			// get directory related matters ready
			///////////////////////////////////////////////////////////			
			prepare();

			///////////////////////////////////////////////////////////
			// assign properties for use within the pom.xml file
			///////////////////////////////////////////////////////////			
			Properties props = new Properties();

			props.setProperty("destDirForPojos", pojoRootDir);
			props.setProperty("cfgXmlFile", this.createHibernateCfgFile(pojoRootDir));
			props.setProperty("revEngXmlFile", buildRootDir + File.separator + "hibernate.reveng.xml");
			props.setProperty("appCreateBuildDir", classesRootDir);

			///////////////////////////////////////////////////////////			
			// apply a unique package suffix per build since all reveng 
			// java classes end up in the same root directory classpath
			// and overwriting needs to be avoided
			///////////////////////////////////////////////////////////			
			generatedPackageName = "com.realmethods." + org.apache.commons.lang3.RandomStringUtils.randomAlphabetic(16);
			props.setProperty("packageName", generatedPackageName);

			///////////////////////////////////////////////////////////
			// handle assigning the maven database options
			///////////////////////////////////////////////////////////
			props = handleMavenDbOptionsAssignment(props);

			msg = "Maven DB properties are " + props;
			LOGGER.log( Level.INFO, msg );

			///////////////////////////////////////////////////////////
			// execute the hbm2java Ant task on the operating system
			///////////////////////////////////////////////////////////
			mavenExecute(Arrays.asList("antrun:run@hbm2java"), buildRootDir, props);

		} catch (Exception exc) {
			LOGGER.log( Level.INFO, "Reverse Engineer Error", exc );
		}
		
		final String msg2 = "Finished reverse engineering, results are located in " + pojoRootDir;
		LOGGER.log( Level.INFO, msg2 );
		
		return (this.pojoRootDir);
	}

	/**
	 * Used to build the reverse engineered Java files.
	 */
	public void build() {
		final String msg = "Build starting on temp Java files located in " + pojoRootDir;
		LOGGER.log(Level.INFO, msg);

		try {
			////////////////////////////////////////////////////////////
			// invoke the compile goal on the pom.xml 
			// in order to compile the reverse engineered Java files
			////////////////////////////////////////////////////////////			
			mavenExecute(Arrays.asList("compile"), pojoRootDir, null);
		} catch (Exception exc) {
			LOGGER.log( Level.WARNING, "Build of RevEng Java Files Failed", exc );
		}
	}
	
	/**
	 * Returned the previously generated package name
	 * @return
	 */
	public String getGeneratedPackageName() {
		return this.generatedPackageName;
	}

	/**
	 * Internal helper method used to prepare for Reverse Engineering
	 * 
	 * @throws	IOException
	 */
	protected void prepare() throws IOException {
		/////////////////////////////////////////////////////////
		// develop a uniquely named output directory
		/////////////////////////////////////////////////////////		
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMddyyyyHHmmss");
		pojoRootDir = buildRootDir + File.separator + "pojo_tmp_" + sdf.format(new Date());
		
		/////////////////////////////////////////////////////////		
		// create the temp dir for the pojos to be reversed into
		/////////////////////////////////////////////////////////		
		if ( new File(pojoRootDir).mkdir() ) {
			final String msg = "Created temp POJO directory " + pojoRootDir;
			
			LOGGER.log(Level.INFO, msg);
		}
		
		/////////////////////////////////////////////////////////		
		// copy the pojo_pom.xml file into the tmp dir
		/////////////////////////////////////////////////////////		
		String pojoPOMFile = buildRootDir + File.separator + "pojo_pom.xml";
		Files.copy(Paths.get(pojoPOMFile), Paths.get(pojoRootDir + File.separator + "pom.xml"),
				StandardCopyOption.REPLACE_EXISTING);

		/////////////////////////////////////////////////////////		
		// load the different db options as maven dependency options
		/////////////////////////////////////////////////////////		
		this.dbMavenOptions = new Properties();
		dbMavenOptions.load(new java.io.FileReader(this.buildRootDir + File.separator + MASTER_DB_OPTIONS_FILE));
	}

	/**
	 * Dynamically create a hibernate.cfg.xml file using the hibernateProps field.
	 * 
	 * @param outputDir		where to write the contents to as a hibernate.cfg.xml file
	 * @return String
	 */
	protected String createHibernateCfgFile(String outputDir) {
		String hibernateCfgFile = outputDir + File.separator + "hibernate.cfg.xml";
		StringBuilder cfgFileBuffer = new StringBuilder();

		cfgFileBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		cfgFileBuffer.append("\n");
		cfgFileBuffer
				.append("<!DOCTYPE hibernate-configuration PUBLIC \"-//Hibernate/Hibernate Configuration DTD 3.0//EN\" \"http://www.hibernate.org/dtd/hibernate-configuration-3.0.dtd\">\n");
		cfgFileBuffer.append("<hibernate-configuration>\n");
		cfgFileBuffer.append("<session-factory>\n");
		cfgFileBuffer.append("<property name=\"hibernate.connection.username\">"
				+ hibernateProps.getProperty("username") + XML_PROPERTY_TERMINATOR);
		cfgFileBuffer.append("\n<property name=\"hibernate.connection.password\">"
				+ hibernateProps.getProperty("password") + XML_PROPERTY_TERMINATOR);
		
		// if we are using MySQL...and we should be...need to force the dialect to v5 in order to run the hbm2java tool well
		String dialect = hibernateProps.getProperty("dialect");
		if ( dialect == null || dialect.lastIndexOf("org.hibernate.dialect.MySQL") > -1 )
				dialect = "org.hibernate.dialect.MySQL5Dialect";
		
		cfgFileBuffer.append("\n<property name=\"hibernate.dialect\">" + dialect
				+ XML_PROPERTY_TERMINATOR);
		
		cfgFileBuffer.append("\n<property name=\"hibernate.connection.driver_class\">"
				+ hibernateProps.getProperty("driver") + XML_PROPERTY_TERMINATOR);
		cfgFileBuffer.append("\n<property name=\"hibernate.connection.url\">" + hibernateProps.getProperty("url").replace("&", "&amp;") 
				+ XML_PROPERTY_TERMINATOR);
		cfgFileBuffer.append("\n</session-factory>");
		cfgFileBuffer.append("\n</hibernate-configuration>");

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(hibernateCfgFile))){
			
			writer.write(cfgFileBuffer.toString());
		} catch (IOException e) {
			LOGGER.log( Level.WARNING, "Failed to dynamically created hibernate.cfg.xml file.", e );
		} 
		
		// the absoluate path of the hibernate cfg file
		return (hibernateCfgFile);
	}

	/**
	 * Executes a list of Maven goals with the server's Maven installation.
	 * 
	 * @param mavenGoals
	 * @param pomFileLocation
	 * @param props
	 * @throws GenerationException
	 */
	private void mavenExecute(List<String> mavenGoals, String pomFileLocation, Properties props) throws GenerationException {
		try {
			InvocationRequest request 	= new DefaultInvocationRequest();
			Invoker invoker 			= new DefaultInvoker();
	
			request.setPomFile(new File(pomFileLocation));
			request.setGoals(mavenGoals);
			request.setOutputHandler(new SystemOutHandler());
	
			if (props != null)
				request.setProperties(props);
	
			/////////////////////////////////////////////////////////
			// if M2_HOME unassigned, assign MAVEN_HOME to it
			/////////////////////////////////////////////////////////
			String mavenHome = System.getenv("M2_HOME");
			if ( mavenHome == null )
				mavenHome = System.getenv("MAVEN_HOME");
			
			/////////////////////////////////////////////////////////
			// this is where Maven considers its home
			/////////////////////////////////////////////////////////
			invoker.setMavenHome(new File(mavenHome));
			
			/////////////////////////////////////////////////////////
			// fire off the request to execute the goals with Maven
			/////////////////////////////////////////////////////////	
			invoker.execute(request);
		} catch( Exception exc) {
			if( mavenGoals != null ) {
				final String msg = "Maven execution failure on goals " + mavenGoals.toString();
				LOGGER.severe(msg);
			}
			throw new GenerationException( "Maven Execution Failed", exc );
		}
	}

	/**
	 * Internal helper for assigning Maven database options using the hibernateProps field.
	 * 
	 * MySQL8 is the default Maven POM dependency, but in the event the software loosens this
	 * requirement and allows storage to any RDBMS Hibernate supports, this will attempt to automatically 
	 * handle establishing the dependencies for the reverese engineered POM build
	 * @param props
	 * @return Properties 
	 */
	private Properties handleMavenDbOptionsAssignment(Properties props) {
		String dbOptions = dbMavenOptions.getProperty(this.hibernateProps.getProperty("dbType"));
		if ( dbOptions != null )
		{
			StringTokenizer tokenizer = new StringTokenizer(dbOptions, ",");
	
			props.setProperty("dbGroupId", tokenizer.nextToken());
			props.setProperty("dbArtifactId", tokenizer.nextToken());
			props.setProperty("dbVersion", tokenizer.nextToken());
		}
		else {
			final String msg = "hibernate info not found in " + MASTER_DB_OPTIONS_FILE + " for dbType " + hibernateProps.getProperty("dbType");
			LOGGER.warning( msg );
		}
		return (props);
	}

	// attributes
	
	//////////////////////////////////////////////////////////////////
	// root directory of source classes to reverse engineer
	//////////////////////////////////////////////////////////////////	
	protected String classesRootDir 					= null;
	//////////////////////////////////////////////////////////////////
	// directory where reverse engineered Java files are built to
	//////////////////////////////////////////////////////////////////	
	protected String buildRootDir 				 		= null;
	//////////////////////////////////////////////////////////////////
	// directory where the reverse engineered Java files are placed
	//////////////////////////////////////////////////////////////////	
	protected String pojoRootDir 						= null;
	//////////////////////////////////////////////////////////////////
	// the generated package name for a reverse engineering session
	//////////////////////////////////////////////////////////////////	
	protected String generatedPackageName				= null;
	//////////////////////////////////////////////////////////////////
	// hibernate properties
	//////////////////////////////////////////////////////////////////
	protected Properties hibernateProps 				= null;
	//////////////////////////////////////////////////////////////////
	// collection of supported Maven database dependency options
	//////////////////////////////////////////////////////////////////	
	protected Properties dbMavenOptions 				= null;
	//////////////////////////////////////////////////////////////////
	// property file containing Maven DB options
	//////////////////////////////////////////////////////////////////
	private static final String MASTER_DB_OPTIONS_FILE 	= "maven_db_options.properties";
	//////////////////////////////////////////////////////////////////
	// static property XML terminator
	//////////////////////////////////////////////////////////////////
	private static final String XML_PROPERTY_TERMINATOR	= "</property>";
	//////////////////////////////////////////////////////////////////
	// Logging factility
	//////////////////////////////////////////////////////////////////	
	private static final Logger LOGGER 					= Logger.getLogger(RunHibernateRevEng.class.getName());
}
