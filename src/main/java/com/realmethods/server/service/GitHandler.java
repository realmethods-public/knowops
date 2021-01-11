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
package com.realmethods.server.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.realmethods.api.PojoParams;
import com.realmethods.codetemplate.BuildApp;
import com.realmethods.codetemplate.parser.ModelParserFactory;
import com.realmethods.common.helpers.AppGenHelper;

/**
 * Controller class to handle the loading of a model contained within POJOs of a Git repo.    
 * 
 * A provided Git Repo must contain a valid functional Maven pom.xml in order to build the source
 * into Java .class files.  Also, it is expected the contained Java files will compile without error.
 * 
 * @author realMethods, Inc.
 * 
 */
public class GitHandler extends PojoHandler {
	/**
	 * Default constructor
	 */
	public GitHandler() {
		// no_op
	}
	
	/**
	 * Constructor
	 * 
	 * PojoParams
	 * 
	 * @param rootPackageNames
	 */
	public GitHandler( PojoParams pojoParams ) {
		super( pojoParams );
	}

	@Override
	/**
	 * Uses the ModelParserFactory to help parse the provided POJO file into the common
	 * realMethods Generation Meta-Model.  
	 * 
	 * @param pojoResource
	 * @return
	 */
	public boolean loadModel( String gitResource ) 
	{
		LOGGER.log( Level.INFO, "loading model from Git Repo {0}", gitResource );

		try {
			String modelRootDir = null;
				
			//////////////////////////////////////////////////
			// create a temporary work directory 
			///////////////////////////////////////////////////
			String gitCloneDir = AppGenHelper.createTempWorkspaceDirectory("model_");
			
			///////////////////////////////////////////////////////////////////
			// Git clone the repository into the temporary work directory
			///////////////////////////////////////////////////////////////////
			org.eclipse.jgit.api.Git.cloneRepository()
			  .setURI(gitResource)
			  .setDirectory(new File(gitCloneDir))
			  .call();
			LOGGER.log( Level.INFO, "Done cloning repo to {0}", gitCloneDir );
			
			///////////////////////////////////////////////////////
			// handle building the Git cloned project
			///////////////////////////////////////////////////////
			modelRootDir = handleGitCloneBuild(gitCloneDir);
			
			///////////////////////////////////////////////////////
			// delegate internally to handle loading the model from
			// the root directory of the Java classes
			///////////////////////////////////////////////////////	
			return handleLoadingModelFromDir( modelRootDir );
				
		} catch (Exception exc) {
			final String errMsg = "Failed to reverse engineer from Git resource " + gitResource;
			LOGGER.log(Level.SEVERE, errMsg, exc );
		}
		return (false);
	}

	/**
	 * Package the project found in the cloned git directory.
	 * Looks for the existence of a pom.xml file.
	 * Applies package to the goals and maven.test.skip=true as a property to skip any testing. 
	 * If a target/classes directory is discovered, copy its content to the realMethods classes directory in the class-path.
	 * If there is no such directory, assume each sub-directory is a module and iterate thru each copying
	 * its target/classes directory content to the realMethods classes directory in the class-path.
	 * 
	 * @param gitCloneDir
	 * @return
	 */
	private String handleGitCloneBuild(String gitCloneDir) {
		LOGGER.log( Level.INFO, "Git cloning repository {0}", gitCloneDir );

		String classesRootDir = null;

		/////////////////////////////////////////////////////////////////////
		// test for presence of pom.xml to use Maven
		/////////////////////////////////////////////////////////////////////

		if ( !new File(gitCloneDir, POM_XML).exists() ) {
			LOGGER.log( Level.INFO, "No pom.xml discovered in {0}", gitCloneDir );
			return classesRootDir;
		}
				
		////////////////////////////////////////////////////////////////////////////////////
		// assume for now Maven is able to be used against an existing pom.xml file
		// eventually, have the invocator include the intent with the cal
		////////////////////////////////////////////////////////////////////////////////////		
		final List<String> goals 	= new ArrayList<>();
		final Properties props 		= new Properties();		
	
		////////////////////////////////////////////////////////////////////////////////////
		// signal Maven skip the testing phase
		////////////////////////////////////////////////////////////////////////////////////
		props.put("maven.test.skip", "true");
		
		////////////////////////////////////////////////////////////////////////////////////
		// package the project to create the target archive
		////////////////////////////////////////////////////////////////////////////////////
		goals.add( "package" );
		
		try {
			final String target_classes = "target" + File.separator + "classes";

			///////////////////////////////////////////////////////////////////////
			// invoke the Maven helper with the properties, goals and location 
			///////////////////////////////////////////////////////////////////////
			new BuildApp().runMaven(props, goals, gitCloneDir, null );
			
			LOGGER.info( "Done building Git Hub project." );
			
			////////////////////////////////////////////////////////////////////////
			// assign the gitCloneDir as the root of the class output directory 
			////////////////////////////////////////////////////////////////////////
			classesRootDir = gitCloneDir;
						        
			List<File> targetClassDirs = new ArrayList<>();
			
	        ////////////////////////////////////////////////////////////////////////////
	        // check if the Git clone directory has a target/classes sub-directory
	        ////////////////////////////////////////////////////////////////////////////
	        File targetClassDir = new File( gitCloneDir, target_classes);
	        
			if ( targetClassDir.exists() ) {
				targetClassDirs.add( targetClassDir );
	        } else {
	        	////////////////////////////////////////////////////////////////
	        	// iterate over each sub-directory which are POM submodules
	        	/////////////////////////////////////////////////////////////////
	        	for( File pomModuleDir : new File(gitCloneDir).listFiles() ) {			        		
	        		//////////////////////////////////////////
	        		// only interested in sub-directories
	        		//////////////////////////////////////////
	        		if (pomModuleDir.isDirectory()) {
	        			///////////////////////////////////////////////////////////////
	        			// test directory for target/classes sub-directory			   
	        			///////////////////////////////////////////////////////////////
	        			targetClassDir = new File( pomModuleDir.getAbsolutePath(), target_classes );
	        			
	        			if ( targetClassDir.exists() ) {
	        				targetClassDirs.add( targetClassDir );
	        			}
	        		}
	        	}
	        }
			
			//////////////////////////////////////////////////////////////////
			// internal helper to copy class files of reference target/class
			// sub-directories
			//////////////////////////////////////////////////////////////////
			handleCopyingTargetDirs( targetClassDirs );
				
		} catch( Exception exc ) {
			LOGGER.log( Level.WARNING, "Failure during Git cloning", exc );
		}			

		return classesRootDir;
	}

	// attributes
	private static final String POM_XML		= "pom.xml";
	private static final Logger LOGGER 		= Logger.getLogger(GitHandler.class.getName());

}
