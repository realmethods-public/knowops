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

import com.realmethods.codetemplate.model.attribute.*;
import com.realmethods.codetemplate.model.classes.*;
import com.realmethods.codetemplate.model.classes.enumerate.*;
import com.realmethods.codetemplate.model.association.*;
import com.realmethods.codetemplate.model.method.*;
import com.realmethods.foundational.common.exception.ProcessingException;
import com.realmethods.codetemplate.*;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Base class for all model parsers. Operationally it provides not function and expects
 * to delegate to a sub-class which should override particular method.
 * 
 * @author realMethods, Inc.
 *
 */
public abstract class BaseModelParser extends AppGenObject
{
	/**
	 * Default constructor
	 */
	public BaseModelParser() {
		// no_op
	}
	
	/** 
	 * Parse model method
	 */
	public void parse() {
		// no_op
	}

	public void run() throws ProcessingException
	{
		LOGGER.log(Level.INFO, "Beginning the parsing of Model");

		try {
			doRun();
			ModelParser.modelParser().postModelLoading();
			doneRunning();
		}
		catch (Exception exc) {
			LOGGER.log(Level.WARNING, "BaseModelParser.run()", exc);
			throw new ProcessingException( "BaseModelParser.run()", exc);
		}
	}

	/**
	 * internal notification method that the parser is done running
	 */
	protected void doneRunning() {
		
	}

	/**
	 * Expected to be overloaded by the sub-class
	 * @throws ProcessingException
	 */
	protected abstract void doRun() throws ProcessingException;
	
// attributes
	private static final Logger LOGGER = Logger.getLogger(BaseModelParser.class.getName());

}
