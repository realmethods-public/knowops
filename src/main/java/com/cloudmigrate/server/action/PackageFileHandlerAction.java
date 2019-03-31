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
package com.cloudmigrate.server.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.ByteArrayInputStream;
import java.io.File;

import javax.servlet.http.*;

import com.cloudmigrate.common.helpers.AppGenHelper;
import com.cloudmigrate.common.helpers.FrameworkPackageValidator;
import com.cloudmigrate.codetemplate.GenerateApp;
import com.cloudmigrate.entity.FrameworkPackage;
import com.cloudmigrate.entity.dao.FrameworkPackageDAO;
import com.cloudmigrate.foundational.common.exception.FrameworkDAOException;
import com.cloudmigrate.foundational.common.exception.ProcessingException;

/**
 * Action handler for all Package (Tech Stack Package) actions.
 * 
 * @author realMethods, Inc.
 *
 */
public class PackageFileHandlerAction extends FileHandlerAction {
	/**
	 * default constructor
	 */
	public PackageFileHandlerAction() {
		// no_op
	}

	/**
	 * Returns the frameworkPackageNameToUse field.
	 * 
	 * @return String
	 */
	public String getFrameworkPackageNameToUse() {
		return this.frameworkPackageNameToUse;
	}

	/**
	 * Assigns the frameworkPackageNameToUse field from the provided argument.
	 * 
	 * @param frameworkPackageNameToUse
	 */
	public void setFrameworkPackageNameToUse(String frameworkPackageNameToUse) {
		this.frameworkPackageNameToUse = frameworkPackageNameToUse;
	}

	/**
	 * Returns the frameworkPackages field.
	 * 
	 * @return
	 */
	public List<FrameworkPackage> getFrameworkPackages() {
		return this.frameworkPackages;
	}

	/**
	 * Loads and returns all the framework package (tech stack packages) XML
	 * definition files as a single XML structure.
	 * 
	 * @return String
	 */
	public String retrievePackages() {
		StringBuilder content 	= new StringBuilder("<framework_packages>");
		String[] extensions 	= { "xml" };
		String fileToLookFor 	= FRAMEWORK_PACKAGE_FILE_NAME;
		String workingDir 		= getTheSession().getServletContext().getRealPath("/" + WORKING_DIR);

		content.append(AppGenHelper.fileRecurseConcatHelper(workingDir, extensions, fileToLookFor));
		content.append("</framework_packages>");

		// must remove the XML version header
		this.fileStream = new ByteArrayInputStream(content.toString().replace("<?xml version=\"1.0\"?>", "")
										.getBytes(java.nio.charset.StandardCharsets.UTF_8));
		return SUCCESS;
	}

	/**
	 * Action handler, but delays loading until the getDirMappings is called.
	 * 
	 * @return String
	 */
	public String loadDirMappings() {
		// no_op...handled in the getDirMappings() method
		return (SUCCESS);
	}

	/**
	 * This is method obsolete and no longer required of the goFramework client.
	 *  
	 * @return Properties
	 */
	public Properties getDirMappings() {
		Properties dirMappings = new Properties();
		String mappingsFile = getWorkingDirPath() + File.separator + frameworkPackageNameToUse + File.separator
				+ GenerateApp.MASTER_DIR_MAP_FILE;

		try {
			dirMappings.load(new java.io.FileReader(mappingsFile));
		} catch (java.io.IOException exc) {
			addErrorMessage("Unable to read the " + mappingsFile
					+ ". Make sure it is  located in the server working directory.</br>" + exc.getMessage());
		}

		return dirMappings;
	}

	/**
	 * Action method to upload the package referenced by the frameworkPackageNameToUse identified.
	 * 
	 * @return String
	 */
	public String uploadPackage() {
		try {
			
			if (uploadHelper() &&
					new FrameworkPackageValidator(getTheSession().getServletContext().getRealPath("/"))
					.validate(uploadedFileSystemName)) {
				return SUCCESS;
			}
		}
		catch( ProcessingException exc ) {
			LOGGER.log(Level.SEVERE, "Failed to upload tech stack package", exc);
		}
		return ERROR;		
	}

	/**
	 * Helper method to validate all package required files exist.  This includes the 
	 * framework-package.xml, options.xml, and the mappings.properties files.
	 * 
	 * @return boolean
	 */
	protected boolean validateFilesExist() {
		boolean existsFrameworkPackageXml = validateFile(FRAMEWORK_PACKAGE_FILE_NAME);
		boolean existsOptionsXml = validateFile("options.xml");
		boolean existsMappingsProps = validateFile("mappings.properties");

		return (existsFrameworkPackageXml && existsOptionsXml && existsMappingsProps);
	}

	/**
	 * Helper method to determine the existence of the provided file name.
	 * 
	 * @param fileName
	 * @return boolean
	 */
	protected boolean validateFile(String fileName) {
		boolean valid = false;
		try {
			valid = new File(unzipRootDir + File.separator + fileName).exists();

			if (valid)
				addInfoMessage(fileName + " passed validated");
			else
				addErrorMessage(unzipRootDir + File.separator + fileName
						+ " failed valiation. File not found in the Tech Stack Package");
		} catch (Exception exc) {
			final String msg = "Package does not contain required file " + fileName;
			addErrorMessage(msg);
			LOGGER.log(Level.SEVERE, msg, exc);
		}
		return (valid);
	}

	protected String getTempDirByName() {
		return this.frameworkPackageNameToUse;
	}

	/**
	 * Loads all the FrameworkPackage instance from the persistant store.
	 * 
	 * @return	String
	 */
	public String loadAllPackages()  {
		try {
			frameworkPackages = new FrameworkPackageDAO().findAllFrameworkPackage();
		} catch( Exception exc ) {
			LOGGER.log( Level.WARNING, "Failed to load all packages", exc);
			return ERROR;
		}
		
		return (SUCCESS);
	}

	/**
	 * Action handler for retrieving the options for the package referenced by
	 * the frameworkPackageId field.
	 * 
	 * @return
	 */
	public String retrieveOptions() {
		try {
			String options = AppGenHelper.getPackageOptions( this.frameworkPackageId );
			
			if ( options != null ) {
				setFileStream( new ByteArrayInputStream(
										options.getBytes(java.nio.charset.StandardCharsets.UTF_8)
									) );
			}
		}
		catch( Exception exc )
		{	
			final String msg = "Package retrieval error";
			LOGGER.log(Level.WARNING, msg, exc);
		}
				
		return SUCCESS;
	}
	
	public String findPackage() {
		return SUCCESS;
	}
	
	public FrameworkPackage getPackage() throws FrameworkDAOException {
		return new FrameworkPackageDAO().findFrameworkPackage( frameworkPackageId );
	}
	/**
	 * Assigns the frameworkPackageId field the provided argument.
	 * 
	 * @param id
	 */
	public void setFrameworkPackageId( Long id ) {
		frameworkPackageId = id;
	}
	
// attributes

	// attributes
	private List<FrameworkPackage> frameworkPackages 		= new ArrayList<>();
	protected Long frameworkPackageId 						= null;
	protected String frameworkPackageNameToUse 				= null;
	protected String unzipRootDir 							= null;
	private static final String FRAMEWORK_PACKAGE_FILE_NAME = "framework-package.xml";
	private static final Logger LOGGER 						= Logger.getLogger(PackageFileHandlerAction.class.getName());
}
