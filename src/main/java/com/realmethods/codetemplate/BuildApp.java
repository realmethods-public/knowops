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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.maven.shared.invoker.*;

import com.realmethods.foundational.common.exception.ProcessingException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Handles the necessary responsibilities to build the generated application.
 * For now, we will use Maven, but other build platforms will soon follow, and
 * this class will act as the base for them.
 * 
 * @author realMethods, Inc.
 * 
 */
public class BuildApp 
	extends AppGenObject 
	implements org.apache.maven.shared.invoker.InvocationOutputHandler {

	public BuildApp() {
	}

	/**
	 * constructor, receives the absolute path to the generated pom.xml file
	 * 
	 * @param buildRootDir
	 */
	public BuildApp(GenerateAppOptions appOptions) {
		this.appOptions = appOptions;
	}

	/**
	 * Uses maven to execute a full deployment
	 * 
	 * Return true/false indicating the success
	 * @return	String 
	 */
	public boolean deploy(Properties props) {
		LOGGER.info( "Starting to deploy using Maven..." );
		
		try {
			List<String> goals = new ArrayList<>();
			
			goals.add( "deploy" );
			mavenHelper(props, goals);			
		} catch (Exception exc) {
			LOGGER.log(Level.WARNING, BUILD_APP, exc);
			executionStatus = "Git commit through Maven Failed";
			return (false);
		}
		executionStatus = "Git commit through Maven Successful";
		LOGGER.info( executionStatus );
		return (true);
	}

	/**
	 * performs a compile only by invoking the mvn process. must make sure the
	 * necessary Maven environment variables are configured.
	 * 
	 * Returns true/false indicating it's success
	 * 
	 * @return
	 */
	public boolean compile() {
		LOGGER.info( "Starting compile..." );
		
		try {
			List<String> goals = new ArrayList<>();
			Properties props = new Properties();
						
			goals.add( "compile" );
			mavenHelper(props, goals);
			
		} catch (Exception exc) {
			LOGGER.log(Level.WARNING, BUILD_APP, exc);
			executionStatus = "Build through Maven Failed";
			return (false);
		}
		executionStatus = "Build through Maven Successful";
		LOGGER.info( executionStatus );
		return ( true );
	}

	/**
	 * Performs running the application through Maven using the comma delimited options
	 * designated by application.run directives.
	 * 
	 * Returns true/false indicating its success
	 * @return boolean
	 */
	public boolean run() {
		LOGGER.info( "Starting run..." );
		try {
			// attempt to stop the forked run()
			mavenHelper(null, Arrays.asList("jetty:stop"));
			
			List<String> goals 			= new ArrayList<>();
			StringTokenizer tokenizer 	= new StringTokenizer(appOptions.getOptions().get("application.run directives"), ",");

			while (tokenizer.hasMoreTokens())
				goals.add(tokenizer.nextToken());

			String runParams = appOptions.getOptions().get("application.run parameters");
			Properties props = new Properties();

			if ( runParams != null ) {
				tokenizer = new StringTokenizer(runParams, ",");
	
				String fullParam;
				String key;
				String param;
				while (tokenizer.hasMoreTokens()) {
					fullParam = tokenizer.nextToken();
	
					key = fullParam.substring(0, fullParam.indexOf('=')).replace("-D", "");
					param = fullParam.substring(fullParam.indexOf('=') + 1);
	
					props.put(key, param);
				}
			}
			mavenHelper(props, goals);

		} catch (Exception exc) {
			LOGGER.log(Level.WARNING, BUILD_APP, exc);
			executionStatus = "Run through Maven Failed";
			return ( false );
		}
		executionStatus = "Run through Maven Successful";
		LOGGER.info( executionStatus );
		return ( true );

	}

	/**
	 * Returns the status of the execution.
	 * 
	 * @return	String
	 */
	public String getExecutionStatus() {
		return this.executionStatus;
	}

	/**
	 * Returns the maven output.
	 * 
	 * @return	String
	 */
	public String getErrorOutput() {
		return this.mavenOutput.toString();
	}
	
	/**
	 * Helper method used to execute maven through an InvocationRequest
	 * @param props
	 * @param goals
	 * @throws ProcessingException
	 */
	private void mavenHelper(Properties inputProps, List<String> goals) throws ProcessingException {
		try
		{
			mavenOutput 				= new StringBuilder();

			Properties props = null;
			
			if( inputProps != null)
				props = inputProps;
			else
				props = new Properties();
			
			// apply AWS Credentials if available
			props = applyAWSCredentials( props );
			
			//the platform always performs a Git commit
			props.setProperty("git", "true");

			// limit logging during test
			props.setProperty("limitTestLogging", "true");
			
			runMaven( inputProps, goals, appOptions.getWorkingDir(), this );
			
		} catch( Exception exc ) {
			LOGGER.log( Level.WARNING, BUILD_APP, exc);
			throw new ProcessingException( "BuildApp:mavenHelper - " + exc.getMessage() );
		}
	}

	public void runMaven(Properties inputProps, 
									List<String> goals, 
									String pomFileDir, 
									InvocationOutputHandler handler ) throws ProcessingException {
		LOGGER.info( "" );
		try
		{
			InvocationRequest request 	= new DefaultInvocationRequest();
			Invoker invoker 			= new DefaultInvoker();
			mavenOutput 				= new StringBuilder();
			
			request.setPomFile(new File(pomFileDir));
			request.setGoals(goals);
			request.setOutputHandler(new SystemOutHandler());
			if ( handler != null)
				request.setErrorHandler(handler);
			
			Properties props = null;
			
			if( inputProps != null)
				props = inputProps;
			else
				props = new Properties();
			
			//apply to the request
			request.setProperties(props);
	
			String mavenHome = System.getenv("M2_HOME");
			if ( mavenHome == null )
				mavenHome = System.getenv("MAVEN_HOME");
	
			invoker.setMavenHome(new File(mavenHome));
			invoker.execute(request);
		} catch( Exception exc ) {
			LOGGER.log( Level.WARNING, BUILD_APP, exc);
			throw new ProcessingException( "BuildApp:runMaven - " + exc.getMessage() );
		}
	}

	
	/**
	 * internal helper method used to stream the build process output into the
	 * provided InputStream
	 * 
	 * @param inputStream
	 * @return	String
	 */
	protected static String output(InputStream inputStream) {
		StringBuilder sb = new StringBuilder();

		try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line + System.getProperty("line.separator"));
			}
		} catch (IOException ioExc) {
			sb.append(ioExc.getMessage());
		} 
		return sb.toString();
	}

	/**
	 * implementation for interface InvocationOutputHandler
	 * @param line
	 */
	public void consumeLine(String line){
		String lc = line.toLowerCase();
		if ( lc.contains("error") || lc.contains("fail") )
			mavenOutput.append( line );
	}
	
	/**
	 * Apply the AWS accesskey and secretkey to the Maven Properties 
	 * @param props
	 * @return Properties
	 */
	private Properties applyAWSCredentials( Properties props ) {
		if ( props != null ) {
			Object key = System.getenv("USER_AWS_ACCESSKEY");
			if ( key != null ) {
				key = appOptions.getOptions().get("aws-lambda.accessKey") ;
			}

			if ( key != null  ) {
				props.put( "AWS_ACCESSKEY", key );
			}

			key = System.getenv("USER_AWS_SECRETKEY");
			if ( key != null ) {
				key = appOptions.getOptions().get("aws-lambda.secretKey") ;
			}

			if ( key != null  ) {
				props.put( "AWS_SECRETKEY", key );
			}

		}
	
		return props;
	}
	
	// attributes
	protected GenerateAppOptions appOptions 	= null;
	protected StringBuilder mavenOutput 		= null;
	protected String executionStatus			= null;
	private static final String BUILD_APP		= "BuildApp";
	private static final Logger LOGGER 			= Logger.getLogger(BuildApp.class.getName());

}
