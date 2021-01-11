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
package com.realmethods.codetemplate.parser;

import com.realmethods.api.PojoParams;
import com.realmethods.codetemplate.model.attribute.AttributeObject;
import com.realmethods.codetemplate.model.classes.*;
import com.realmethods.codetemplate.tools.*;
import com.realmethods.foundational.common.exception.ProcessingException;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Core class for handling the parsing of a set of Plan Old Java Objects (POJOs) into
 * the realMethods meta-model.
 * 
 * @author realMethods, Inc.
 *
 */
public class JavaDirParser extends BaseModelParser
{ 

	/**
	 * Main constructor. 
	 * 
	 * The root directory is the location of the 
	 * @param rootDir
	 * @param pojoParams
	 */
    public JavaDirParser( String rootDir, PojoParams pojoParams) {
    	classRootDirectory 	= rootDir;
    	this.pojoParams 	= pojoParams;
    }
    
    @Override
    /**
     * Called by the base class to perform parsing into the model
     * 
     */
    protected void doRun() throws ProcessingException
    {
        try {
        	reverseEngineerJava();
	    }
	    catch(Exception exc) {
	    	throw new ProcessingException( "JavaDirParser.doRun()", exc );
	    }
    }
    
    /**
     * Handler to reverse engineer a model from Java class files located at and under
     * the classRootDirectory field.
     * 
     * Delegates core responsibility to ReverseEngineerJava instance.
     * 
     * @throws ProcessingException
     */
    public void reverseEngineerJava() throws ProcessingException
    {
        LOGGER.log(Level.INFO, "Starting..." );

    	ReverseEngineerJava reverseEng 	= new ReverseEngineerJava(this.pojoParams);
    	classObjects 					= reverseEng.reverse( classRootDirectory );

    	/////////////////////////////////////////////////////////////////////////////////////////
    	// if no ClassObjects were created, it likely indicates a failure in parsing the 
    	// class root directory contents into a model
    	/////////////////////////////////////////////////////////////////////////////////////////
    	if ( classObjects == null || classObjects.isEmpty() ) {
    		final String msg = "Failed to load any Java class files from path " + classRootDirectory;
    		LOGGER.severe(msg);
    		throw new ProcessingException(msg);
    	}
        
		/////////////////////////////////////////////////////////////////////////////////////////
    	// for each reverse engineered ClassObject, notify it to finishLoading
		/////////////////////////////////////////////////////////////////////////////////////////    	
		classObjects.forEach( classObject -> {
			String name = classObject.getName();
			if ( name != null )
			{
				// if no name, assign one
				if ( name.length() == 0 )
					classObject.setName( "class_has_no_name_" + org.apache.commons.lang.RandomStringUtils.randomAlphabetic(4) );

				//////////////////////////////////////////////////
				// apply to the thread specific modelParser
				//////////////////////////////////////////////////
				ModelParser.modelParser().addClass( classObject );
			}
		});
	}

    /**
     * Returns the classRootDirectory field.
     * @return
     */
    public String getClassRootDirectory()
    {
        return classRootDirectory;
    }
    
    /**
     * Assigns the classRootDirectory field using the provided argument.
     * 
     * @param classRootDir
     */
    public void setClassRootDirectory(String classRootDir)
    {
    	classRootDirectory = classRootDir;
    }

	/**
	 * internal notification method that the parser is done running
	 */
    @Override
	protected void doneRunning() {
		
		if ( classObjects ==null ) 
			return;
		
		classObjects.forEach( classObject -> {
		
			//////////////////////////////////////////////////
			// if a primary key pattern has been applied, 
			// search for an attribute by the modified name
			//////////////////////////////////////////////////
			if ( pojoParams.hasPrimaryKeyPattern() ) {
				LOGGER.info( "POJO has primary key pattern " + pojoParams.getPrimaryKeyPattern() );
				
				String attributeName 		= pojoParams.getPrimaryKeyPattern().replace(PojoParams.POJO_NAME_PATTERN, classObject.getName());
				AttributeObject attribute 	= classObject.findAttribute( attributeName );

				if ( attribute != null ) {
					LOGGER.info( "Found attribute using primarkey pattern for " + attributeName  );
					//////////////////////////////////////////////////
					// if no parent, try to apply identity
					//////////////////////////////////////////////////
					if ( !classObject.hasParent() ) {						
						LOGGER.info( "Class has no parent so forcing attribute to be a primary key"  );

						//////////////////////////////////////////////
						// assign identity and label as forced
						//////////////////////////////////////////////					
						attribute.isPrimaryKey(true);
						classObject.hasForcedIdentity(true);
					} 
					/////////////////////////////////////////////////////////////////
					// has a parent so attempt to remove identity directly from here 
					// since the top most parent will have identity
					/////////////////////////////////////////////////////////////////
					else {
						LOGGER.info( "Class has a parent so removing attribute as primary key for " + attributeName  );
						
						classObject.removeAttribute( attribute );
					}
				}
			}
		});
	}
    
// attributes

    ////////////////////////////////////////////////////////////////
    // ClassObject instances that were reverse engineered
    ////////////////////////////////////////////////////////////////
	protected Collection<ClassObject> classObjects = null;
    ////////////////////////////////////////////////////////////////
    // root directory where the Java class files exists to reverse engineer
    ////////////////////////////////////////////////////////////////
    protected String classRootDirectory = null;
    ////////////////////////////////////////////////////////////////
    // the Pojo params as part of the reverse engineering
    ////////////////////////////////////////////////////////////////
    protected PojoParams pojoParams = null;
    ////////////////////////////////////////////////////////////////
    // Logging implementation
    ////////////////////////////////////////////////////////////////
	private static final Logger LOGGER 	= Logger.getLogger(JavaDirParser.class.getName());

}
