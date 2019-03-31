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
package com.cloudmigrate.codetemplate.parser;

import com.cloudmigrate.codetemplate.model.classes.*;
import com.cloudmigrate.codetemplate.tools.*;
import com.cloudmigrate.foundational.common.exception.ProcessingException;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Core class for handling the parsing of a set of Plan Old Java Objects (POJOs) into
 * the common goFramework Generation-Time model.
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
	 * @param rootPackageName
	 */
    public JavaDirParser( String rootDir, String rootPackageName ) {
    	classRootDirectory 		= rootDir;
    	this.rootPackageName 	= rootPackageName;
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
        LOGGER.log(Level.INFO, "Beginning to reverse engineer from Java class files" );

    	ReverseEngineerJava reverseEng = new ReverseEngineerJava(this.rootPackageName);
    	Collection<ClassObject> classObjects = reverseEng.reverse( classRootDirectory );

    	// if no ClassObjects were created, it indicates a failure in parsing the 
    	// class root directory contents into a model
    	if ( classObjects == null || classObjects.isEmpty() ) {
    		final String msg = "Failed to load any Java class files from path " + classRootDirectory;
    		LOGGER.severe(msg);
    		throw new ProcessingException(msg);
    	}
        
    	
		classObjects.forEach( classObject -> {
			String name = classObject.getName();
			if ( name != null )
			{
				// if no name, assign one
				if ( name.length() == 0 )
					classObject.setName( "class_has_no_name_" + org.apache.commons.lang.RandomStringUtils.randomAlphabetic(4) );
				
				classObject.finishLoading();
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

// attributes
    
    protected String classRootDirectory = null;
    protected String rootPackageName 	= null;
	private static final Logger LOGGER 	= Logger.getLogger(JavaDirParser.class.getName());

}
