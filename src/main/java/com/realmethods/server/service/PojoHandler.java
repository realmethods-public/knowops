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
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.realmethods.api.PojoParams;
import com.realmethods.codetemplate.parser.ModelParserFactory;
import com.realmethods.common.helpers.AppGenHelper;
import com.realmethods.foundational.common.exception.ProcessingException;
import com.realmethods.foundational.common.namespace.FrameworkNameSpace;

import org.apache.commons.io.FileUtils;

/**
 * Controller class to handle the loading of a model contained within POJOs.  These POJOs are
 * included in a java archive file (jar, ear, or war).  
 * 
 * @author realMethods, Inc.
 * 
 */
public class PojoHandler implements IModelParser {
	/**
	 * Default constructor
	 */
	public PojoHandler() {
		// no_op
	}
	
	/**
	 * Constructor
	 *  
	 * PojoParams are the collection of 
	 * 
	 * @param rootPackageNames
	 */
	public PojoHandler( PojoParams pojoParams ) {
		this.pojoParams = pojoParams;
	}

	/**
	 * return the pojoParams
	 * @return
	 */
	public PojoParams getPojoParams() {
		return pojoParams;
	}
	
	/**
	 * assign the PojoParams
	 * @param packageNames
	 */
	public void setPojoParams( PojoParams pojoParams ) {
		this.pojoParams = pojoParams;
	}
	
	/**
	 * Uses the ModelParserFactory to help parse the provided POJO file into the common
	 * realMethods Generation Meta-Model.  Determines if the Java JAR file is provided
	 * or the URL to a public Git Hub repo containing Java class files.
	 * 
	 * @param pojoResource
	 * @return
	 */
	public boolean loadModel( String pojoResource ) 
	{
		try {
			String modelRootDir = null;
			////////////////////////////////////////////////////////////////////////
			// the JAR file should be an archive requiring Unzipping.
			// The return is the root directory of the unzipped contents.
			////////////////////////////////////////////////////////////////////////

			//////////////////////////////////////////////////////////////
			// determine if the pojo resource is a valid archive file type
            //////////////////////////////////////////////////////////////
			if ( testForArchive(pojoResource) ) {
				LOGGER.info( "input pojoResource arg is a Java archive file" );
				
				//////////////////////////////////////////////////////
				// unzip the pojo resource to a temporary directory			
				//////////////////////////////////////////////////////
				modelRootDir = AppGenHelper.unzip( pojoResource );
				
				//////////////////////////////////////////////////////////////////////////////////
				// copy the model root directory to the realMethods class-path so classes can be 
				// discovered during Java class introspection
				//////////////////////////////////////////////////////////////////////////////////
				FileUtils.copyDirectoryToDirectory(new File(modelRootDir), 
						new File(FrameworkNameSpace.REALMETHODS_CLASSPATH_ROOT_DIR) );
			}
			
		} catch (Exception exc) {
			final String errMsg = "Failed to reverse engineer from POJO resource " + pojoResource;
			LOGGER.log(Level.SEVERE, errMsg, exc );
		}
		return (false);
	}

	/**
	 * internal helper method used to turn the Java classes within the provided 
	 * model root directory hierarchy into a realMehods meta-model
	 * @param modelRootDir
	 * @exception ProcessingException
	 * @return
	 */
	protected boolean handleLoadingModelFromDir( String modelRootDir ) 
			throws ProcessingException {
		//////////////////////////////////////////////////////////////////////////////////
		// if a modelRootDir has been created, it contains Java class files that
		// represent the input model.  next, turn it into the realMethods meta-model
		// for the current Thread
		//////////////////////////////////////////////////////////////////////////////////
		if ( modelRootDir != null ) {
			//////////////////////////////////////////////////////////////////////////////////
			// access the ModelParserFactory to leverage the registered handler to perform
			// model parsing and common realMethods meta-model
			//////////////////////////////////////////////////////////////////////////////////
			ModelParserFactory.getInstance().createJavaDirParser(modelRootDir, pojoParams).run();
			
			//////////////////////////////////////////////////////////////////////////////////
			// remove the model root directory
			//////////////////////////////////////////////////////////////////////////////////
			org.apache.commons.io.FileUtils.deleteQuietly(new java.io.File(modelRootDir));
			
			return true;
		}
		else {
			LOGGER.log( Level.SEVERE, "Parsing of the pojo resource inappropriately yielded no output directory." );
		}		
		
		return false;
	}

	/**
	 * Test the file extension for a Java archive file type.
	 * Supported types are .jar, .war. and .ear
	 * @param fileName
	 * @return
	 */
	public static boolean testForArchive( String fileName ) {
		if ( fileName != null )
			return( fileName.endsWith(".jar") 
						|| fileName.endsWith(".war") 
							|| fileName.endsWith(".ear") );
		else
			return false;
	}

	/**
	 * helper method use to copy the Java class file contents to the realMethods
	 * class-path working directory.
	 * 
	 * @param targetClassDirs
	 * @throw IOException
	 */
	protected void handleCopyingTargetDirs( List<File> targetClassDirs ) {
		
		targetClassDirs.stream().forEach( targetDir -> {
			////////////////////////////////////////////////////////
			// copy contents to realMethods class-path directory
			////////////////////////////////////////////////////////
			try {
				FileUtils.copyDirectoryToDirectory(targetDir, 
					new File(FrameworkNameSpace.REALMETHODS_CLASSPATH_ROOT_DIR) );
			} catch( IOException exc ) {
				final String msg = "Failed ot copy class files from " 
									+ targetDir 
									+ " to " 
									+ FrameworkNameSpace.REALMETHODS_CLASSPATH_ROOT_DIR;
				LOGGER.log( Level.WARNING, msg, exc );			
			}
		});		
	}
	
	// attributes
	private PojoParams pojoParams			= null;
	private static final Logger LOGGER 		= Logger.getLogger(PojoHandler.class.getName());

}
