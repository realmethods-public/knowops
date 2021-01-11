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

package com.realmethods.common.helpers;

import java.io.File;
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

import org.apache.commons.io.FilenameUtils;

import org.w3c.dom.Document;

import com.realmethods.entity.dao.FrameworkPackageDAO;
import com.realmethods.foundational.common.exception.ProcessingException;

/**
 * Helper class to handler validating a technology stack package
 * 
 * @author realMethods, Inc.
 *
 */
public class FrameworkPackageValidator {

	/**
	 * default constructor
	 * @param workingDir
	 */
	public FrameworkPackageValidator(String workingDir) {
		this.workingDir = workingDir;
	}

	/**
	 * Validates the structure and contents of the referenced decompressed 
	 * archived technology stack package 
	 * by the input path.
	 * 
	 * @param techStackPackagePath
	 * @return
	 */
	public boolean validate(String unzippedTechStackPkgRootDir) throws ProcessingException {
		
		this.unzippedTechStackPkgRootDir 	= unzippedTechStackPkgRootDir;		
		boolean validated 					= false;

		// =====================================================
		// the tech stack package should already be decompressed
		// =====================================================
		try {
			
			// =====================================================
			// Turn the FRAMEWORK_PACKAGE_FILE_NAME into a DOM Document
			// =====================================================
			prepareDOMDocument( FRAMEWORK_PACKAGE_FILE_NAME );

			// =====================================================
			// validate derivation first then the existence of all required files 
			// registered (stored) tech stacks.			
			// validate techstackpackage.xml against XSD			
			// =====================================================
			if (validateDerivedFrom()
				&& validateRequiredFilesExist() 
				&& validateXSD(FRAMEWORK_PACKAGE_FILE_NAME, TSP_XSD_FILE_NAME)) {
				// =====================================================
				// validate an application can be generated against the package			
				// =====================================================
				validated = validateCanGenerateApp();
			}
		} catch(Exception exc ) {
			final String msg = "Error validating tech stack package - " + exc.getMessage();
			LOGGER.log( Level.WARNING, msg, exc );
			throw new ProcessingException( msg, exc );
		}
		return validated;
	}

	/**
	 * Attempt to generate and build an app using a standard model
	 * against this loaded tech stack package
	 */
	private boolean validateCanGenerateApp() {
		return true;
	}
	
	/**
	 * Validates the provided file up against its XSD.
	 * 
	 * @param fileToValidate
	 * @param xsdPath
	 * @return
	 */
	private boolean validateXSD(String fileToValidate, String xsdPath) {
		boolean valid = false;
		try {

			// =====================================================
			// create a SchemaFactory capable of understanding WXS schemas
			// =====================================================
			SchemaFactory factory = SchemaFactory
					.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

			// =====================================================			
			// load a WXS schema, represented by a Schema instance
			// =====================================================			
			String xsdFileName = workingDir + File.separator + xsdPath;
			File xsdFile = new File(xsdFileName);

			Source schemaFile = new StreamSource(xsdFile);
			Schema schema = factory.newSchema(schemaFile);

			// =====================================================			
			// create a Validator instance, which can be used to validate an
			// instance document
			// =====================================================			
			Validator validator = schema.newValidator();

			// =====================================================			
			// validate the framework.xml
			// =====================================================			
			validator.validate(new DOMSource(document));
			final String msg = "XSD validation of " + fileToValidate + " was successful";
			LOGGER.info(msg);

			valid = true;
		} catch (Exception exc) {
			final String msg = "Invalid XML structure for file " + FilenameUtils.getName(fileToValidate) 
						+ ". Please check against the realMethods teck stack package XSD";
			LOGGER.log(Level.WARNING, msg, exc);
		}
		
		return valid;
	}


	/**
	 * Helper method to validate all package required files exist. This includes
	 * the framework-package.xml, options.xml, sample-project-as-code.yaml and the mappings.properties
	 * files.
	 * 
	 * @return boolean
	 */
	protected boolean validateRequiredFilesExist() throws ProcessingException {
		boolean valid = validateFile(FRAMEWORK_PACKAGE_FILE_NAME);

		if ( valid && !isDerivedFrom ) {
			valid = (validateFile(PACKAGE_OPTIONS_FILE_NAME)
						&& validateFile(MAPPING_PROPERTIES_FILE_NAME)
						&& validateFile(PROJECT_AS_CODE_YAML_FILE));
		}
		return valid;
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
			if ( new File(unzippedTechStackPkgRootDir + File.separator + fileName).exists() ) {
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

    /**
	 * checks for the optional derivedFrom attribute and if existent, does it reference by name
	 * a valid tech stack package
	 */
	protected boolean validateDerivedFrom() throws ProcessingException {
		boolean valid = true;

		// =====================================================
		// Locate the derivedFrom Node in the DOM
		// =====================================================
		org.w3c.dom.Node derivedFromNode = document.getElementsByTagName(TECH_STACK_PKG_NODE)
									.item(0)
									.getAttributes()
									.getNamedItem("derivedFrom");

		if ( derivedFromNode != null ) {
			String derivedFrom = derivedFromNode.getNodeValue();

			// =====================================================
			//  continue on the a non-null or non-empty value 
			// =====================================================
			if ( derivedFrom != null && !derivedFrom.isEmpty() ) {
				try {
					// =====================================================
					// determine if the derivedFrom value is a valid tech stack package
					// =====================================================
					if ( new FrameworkPackageDAO().findByNameorId( derivedFrom ) == null )
						valid = false;
					else
						isDerivedFrom = true;

				} catch( Exception exc ) {
					final String msg = "Failure locating parent tech stack package " + derivedFrom + ". " + exc.getMessage();
					LOGGER.log( Level.SEVERE, msg, exc);
					valid = false;
				}
			}
		}

		return valid;
	}

	/**
	 * turns the file to validate located at the provided tech stack pkg root dir
	 * into a DOM Document
	 * 
	 * @param	fileToLoad		the file to load
	 */
	private void prepareDOMDocument(String fileToLoad) throws ProcessingException {
		try {
			DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
			dbfactory.setNamespaceAware(true);

			DocumentBuilder parser 	= dbfactory.newDocumentBuilder();
			document 				= parser.parse(
										new File(unzippedTechStackPkgRootDir + File.separator + fileToLoad));
		} catch( Exception exc ) {
			throw new ProcessingException( "Failed to prepare the DOM XML Document", exc );
		}
	}

	// attributes

	// =====================================================
	// XML DOM as Document
	// =====================================================
	Document document 											= null;
	// location where package resides decompressed
	// =====================================================
	protected String unzippedTechStackPkgRootDir 				= null;
	// =====================================================
	// internal working directory
	// =====================================================	
	protected String workingDir 								= null;
	// =====================================================
	// derivation indicator
	// =====================================================	
	protected boolean isDerivedFrom								= false;
	// =====================================================
	// static declarations
	// =====================================================	
	protected static final String MAPPING_PROPERTIES_FILE_NAME 	= "mappings.properties";
	protected static final String TSP_XSD_FILE_NAME 			= "/xsd/techstackpackage.xsd";
	protected static final String OPTIONS_XSD_FILE_NAME 		= "/xsd/options.xsd";
	protected static final String WORKING_DIR 					= "tech.stack.packages";
	protected static final String TSP_ARCHIVE_DIR 				= "tsp.archive";
	protected static final String PACKAGE_OPTIONS_FILE_NAME 	= "options.xml";
	private static final String FRAMEWORK_PACKAGE_FILE_NAME 	= "techstackpackage.xml";
	private static final String PROJECT_AS_CODE_YAML_FILE 		= "sample-project-as-code.yaml";
	private static final String TECH_STACK_PKG_NODE 			= "tech-stack-package";
	// =====================================================
	// Logging implementation
	// =====================================================
	private static final Logger LOGGER = Logger.getLogger(FrameworkPackageValidator.class.getName());

}
