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

package com.cloudmigrate.common.helpers;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.io.FileUtils;

import org.w3c.dom.Document;

import com.cloudmigrate.foundational.common.exception.ProcessingException;

/**
 * Helper class to handler validating a framework package
 * 
 * @author realMethods, Inc.
 *
 */
public class FrameworkPackageValidator {

	/**
	 * default constructor
	 */
	public FrameworkPackageValidator(String workingDir) {
		this.workingDir = workingDir;
		// no_op
	}

	/**
	 * Validates the structure and contents of the tech stack package referenced
	 * by the input path.
	 * 
	 * @param techStackPackagePath
	 * @return
	 */
	public boolean validate(String techStackPackagePath) throws ProcessingException {
		boolean validated = false;

		// the framework package should be an archive requiring
		// de-compressing
		try {
			// need to make sure it is unique among other already registered (stored) tech stacks.			
			unzipRootDir = AppGenHelper.unzip(AppGenHelper
					.remedyFileLocation(techStackPackagePath));
			if (validateFilesExist() && validateXSD(FRAMEWORK_PACKAGE_FILE_NAME, TSP_XSD_FILE_NAME))
				validated = true;
		} finally {
			cleanUpOnValidationFailure();
		}
		return validated;
	}

	/**
	 * Validates the provided file up against its XSD.
	 * 
	 * @param fileToValidate
	 * @param xsdPath
	 * @return
	 */
	private boolean validateXSD(String fileToValidate, String xsdPath)  throws ProcessingException {
		boolean valid = false;
		try {
			DocumentBuilderFactory dbfactory = DocumentBuilderFactory
					.newInstance();
			dbfactory.setNamespaceAware(true);

			DocumentBuilder parser = dbfactory.newDocumentBuilder();
			Document document = parser.parse(
					new File(unzipRootDir + File.separator + fileToValidate));

			// create a SchemaFactory capable of understanding WXS schemas
			SchemaFactory factory = SchemaFactory
					.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

			// load a WXS schema, represented by a Schema instance
			String xsdFileName = workingDir + File.separator + xsdPath;
			File xsdFile = new File(xsdFileName);

			Source schemaFile = new StreamSource(xsdFile);
			Schema schema = factory.newSchema(schemaFile);

			// create a Validator instance, which can be used to validate an
			// instance document
			Validator validator = schema.newValidator();

			// step 1 - validate the framework.xml
			validator.validate(new DOMSource(document));
			final String msg = "XSD validation of " + fileToValidate + " was successful";
			LOGGER.info(msg);

			valid = true;
		} catch (Exception exc) {
			final String msg = "Invalid XML structure of file " + fileToValidate 
						+ ". Please check against the realMethods teck stack package XSD "
						+ "located at http://www.realmethods.com/goframework" 
						+ TSP_XSD_FILE_NAME;
			LOGGER.log(Level.WARNING, msg, exc);
//			throw new ProcessingException( msg, exc );
		}
		
		return valid;
	}

	protected void cleanUpOnValidationFailure() {
		// delete the directory
		try {
			// delete the uunzip directory
			if (unzipRootDir != null)
				FileUtils.deleteDirectory(new File(unzipRootDir));
		} catch (IOException exc) {
			LOGGER.log(Level.WARNING, "Failed to clean up temp directory", exc);
		}
	}

	/**
	 * Helper method to validate all package required files exist. This includes
	 * the framework-package.xml, options.xml, and the mappings.properties
	 * files.
	 * 
	 * @return boolean
	 */
	protected boolean validateFilesExist() throws ProcessingException {
		if (validateFile(FRAMEWORK_PACKAGE_FILE_NAME)
				&& validateFile(PACKAGE_OPTIONS_FILE_NAME)
				&& validateFile(MAPPING_PROPERTIES_FILE_NAME))
			return true;
		else 
			return false;
	}

	/**
	 * Helper method to determine the existence of the provided file name.
	 * 
	 * @param fileName
	 * @return boolean
	 */
	protected boolean validateFile(String fileName) throws ProcessingException {
		String msg 		= fileName;
		boolean found	= false;
		try {
			if ( new File(unzipRootDir + File.separator + fileName).exists() ) {
				msg = msg + " successfully found in tech stack package.";
				found = true;
				LOGGER.info(msg);
			}
			else {
				msg = msg + " unable to be found in tech stack package.";
				LOGGER.warning(msg);
				throw new ProcessingException( msg );
			}
		} catch (Exception exc) {
			msg = msg + "does not exist in the tech stack package";
			LOGGER.log(Level.SEVERE, msg, exc);
			throw new ProcessingException( msg, exc );
		}
		return found;
	}

	// attributes
	protected String unzipRootDir 								= null;
	protected String workingDir 								= null;
	protected static final String MAPPING_PROPERTIES_FILE_NAME 	= "mappings.properties";
	private static final String FRAMEWORK_PACKAGE_FILE_NAME 	= "techstackpackage.xml";
	protected static final String TSP_XSD_FILE_NAME 			= "/xsd/techstackpackage.xsd";
	protected static final String OPTIONS_XSD_FILE_NAME 		= "/xsd/options.xsd";
	protected static final String WORKING_DIR 					= "tech.stack.packages";
	protected static final String TSP_ARCHIVE_DIR 				= "tsp.archive";
	protected static final String PACKAGE_OPTIONS_FILE_NAME 	= "options.xml";
	private static final Logger LOGGER = Logger
			.getLogger(FrameworkPackageValidator.class.getName());

}
