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
package com.cloudmigrate.codetemplate;

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

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.SystemOutHandler;

import com.cloudmigrate.exception.GenerationException;

/**
 * Uses Hibernate to reverse engineer from an database schema to a set of associated Java class files. Requires
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
	 * @param buildRootDir
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
			// get directory related matters ready
			prepare();

			// assign properties for use within the pom.xml file
			Properties props = new Properties();

			props.setProperty("destDirForPojos", pojoRootDir);
			props.setProperty("cfgXmlFile", this.createHibernateCfgFile(pojoRootDir));
			props.setProperty("revEngXmlFile", buildRootDir + File.separator + "hibernate.reveng.xml");
			props.setProperty("appCreateBuildDir", classesRootDir);

			props = handleMavenDbOptionsAssignment(props);

			msg = "Maven DB properties are " + props;
			LOGGER.log( Level.INFO, msg );

			mavenExecute(Arrays.asList("antrun:run@hbm2java"), buildRootDir, props);

		} catch (Exception exc) {
			LOGGER.log( Level.INFO, "Reverse Engineer Error", exc );
		}
		
		// let the client know where things have been reversed to
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
			mavenExecute(Arrays.asList("compile"), pojoRootDir, null);
		} catch (Exception exc) {
			LOGGER.log( Level.WARNING, "Build of RevEng Java Files Failed", exc );
		}
	}

	/**
	 * Internal helper method used to prepare for Reverse Engineering
	 * 
	 * @throws	IOException
	 */
	protected void prepare() throws IOException {
		// develope a uniquely named output directory
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMddyyyyHHmmss");
		pojoRootDir = buildRootDir + File.separator + "pojo_tmp_" + sdf.format(new Date());
		
		// create the temp dir for the pojos to be reversed into
		if ( new File(pojoRootDir).mkdir() ) {
			final String msg = "Created temp POJO directory " + pojoRootDir;
			
			LOGGER.log(Level.INFO, msg);
		}
		
		// copy the pojo_pom.xml file into the tmp dir
		String pojoPOMFile = buildRootDir + File.separator + "pojo_pom.xml";
		Files.copy(Paths.get(pojoPOMFile), Paths.get(pojoRootDir + File.separator + "pom.xml"),
				StandardCopyOption.REPLACE_EXISTING);

		this.dbMavenOptions = new Properties();
		dbMavenOptions.load(new java.io.FileReader(this.buildRootDir + File.separator + MASTER_DB_OPTIONS_FILE));
	}

	/**
	 * Dynamically create a hibernate.cfg.xml file using the hibernateProps field.
	 * 
	 * @param outputDir
	 * @return String
	 */
	protected String createHibernateCfgFile(String outputDir) {
		String hibernateCfgFile = outputDir + File.separator + "hibernate.cfg.xml";
		StringBuilder cfgFileBuffer = new StringBuilder();

		cfgFileBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		cfgFileBuffer.append("\n");
		cfgFileBuffer
				.append("<!DOCTYPE hibernate-configuration PUBLIC \"-//Hibernate/Hibernate Configuration DTD 3.0//EN\" \"http://www.hibernate.org/dtd/hibernate-configuration-3.0.dtd\">");
		cfgFileBuffer.append("\n");
		cfgFileBuffer.append("<hibernate-configuration>");
		cfgFileBuffer.append("\n");
		cfgFileBuffer.append("<session-factory>");
		cfgFileBuffer.append("\n");
		cfgFileBuffer.append("<property name=\"hibernate.dialect\">" + hibernateProps.getProperty("dialect")
				+ XML_PROPERTY_TERMINATOR);
		cfgFileBuffer.append("<property name=\"hibernate.connection.driver_class\">"
				+ hibernateProps.getProperty("driver") + XML_PROPERTY_TERMINATOR);
		cfgFileBuffer.append("<property name=\"hibernate.connection.url\">" + hibernateProps.getProperty("url").replace("&", "&amp;") 
				+ XML_PROPERTY_TERMINATOR);
		cfgFileBuffer.append("<property name=\"hibernate.connection.username\">"
				+ hibernateProps.getProperty("username") + XML_PROPERTY_TERMINATOR);
		cfgFileBuffer.append("<property name=\"hibernate.connection.password\">"
				+ hibernateProps.getProperty("password") + XML_PROPERTY_TERMINATOR);
		cfgFileBuffer.append("</session-factory>");
		cfgFileBuffer.append("</hibernate-configuration>");

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(hibernateCfgFile))){
			
			writer.write(cfgFileBuffer.toString());
		} catch (IOException e) {
			LOGGER.log( Level.WARNING, "Failed to dynamically created hibernate.cfg.xml file.", e );
		} 
		
		// the absoluate path of the hibernate cfg file
		return (hibernateCfgFile);
	}

	/**
	 * Executes a list of maven goals with the maven installation.
	 * 
	 * @param mavenGoals
	 * @param pomFileLocation
	 * @param props
	 * @throws GenerationException
	 */
	private void mavenExecute(List<String> mavenGoals, String pomFileLocation, Properties props) throws GenerationException {
		try {
			InvocationRequest request = new DefaultInvocationRequest();
			Invoker invoker = new DefaultInvoker();
	
			request.setPomFile(new File(pomFileLocation ));
			request.setGoals(mavenGoals);
			request.setOutputHandler(new SystemOutHandler());
	
			if (props != null)
				request.setProperties(props);
	
			String mavenHome = System.getenv("M2_HOME");
			if ( mavenHome == null )
				mavenHome = System.getenv("MAVEN_HOME");
			
			invoker.setMavenHome(new File(mavenHome));
	
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
	 * Internal helper for assigning Maven DB options using the hibernateProps field.
	 * 
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
	protected String classesRootDir 					= null;
	protected String buildRootDir 				 		= null;
	protected String pojoRootDir 						= null;
	protected Properties hibernateProps 				= null;
	protected Properties dbMavenOptions 				= null;
	protected ByteArrayOutputStream stream 				= new ByteArrayOutputStream();
	private static final String MASTER_DB_OPTIONS_FILE 	= "maven_db_options.properties";
	private static final String XML_PROPERTY_TERMINATOR	= "</property>";
	private static final Logger LOGGER 	= Logger.getLogger(RunHibernateRevEng.class.getName());
}
