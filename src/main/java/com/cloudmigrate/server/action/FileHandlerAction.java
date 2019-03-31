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

import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;

/**
 * Struts action class to handle file uploading. 
 * 
 * Stores the location information internally. 
 * 
 * Subclasses typically
 * handle what to do next once the file is uploaded.
 * 
 * @author realMethods, Inc.
 *
 */
public class FileHandlerAction extends BaseStrutsAction {
	
	/**
	 * default constructor
	 */
	public FileHandlerAction() {
		// no_op
	}

	/**
	 * Returns the password field.
	 * 
	 * @return String
	 */
	public String getPassword() {
		return (password);
	}

	/**
	 * Assigns the password field to the provided argument.
	 * 
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Returns the TYPE field.
	 * 
	 * @return String
	 */
	public String getType() {
		return (TYPE);
	}

	/**
	 * Returns the fileStream field.
	 * 
	 * @return
	 */
	public java.io.InputStream getFileStream() {
		return (fileStream);
	}

	/**
	 * Assigns the fileStream field the provided argument.
	 * 
	 * @param fileStream
	 */
	public void setFileStream( java.io.InputStream fileStream ) {
		this.fileStream = fileStream;
	}
	/**
	 * Returns the uploadedFileName field.
	 * 
	 * @return String
	 */
	public String getUploadedFileName() {
		return (uploadedFileName);
	}

	/**
	 * Returns the uploadedFileSystemName field.
	 * 
	 * @return String
	 */
	public String getUploadedSystemFileName() {
		return (uploadedFileSystemName);
	}

	public String getOutputRootDir() {
		return outputRootDir;
	}

	/**
	 * Action handler for file upload. 
	 * 
	 * Delegates to uploadFileHelper() to do the bulk of the work.
	 * 
	 * @return String
	 */
	public String uploadFile() {
		if (uploadHelper()) {
			try {
				fileStream = new FileInputStream(uploadedFileSystemName);
			} catch (Exception exc) {
				addErrorMessage("File upload problem. " + exc.getMessage());
				LOGGER.info("FileHandlerAction.uploadFile() - " + exc.getMessage());
			}
		}

		return (SUCCESS);
	}

	/**
	 * Does the bulk of the work to receive the uploaded file as a MultiPartRequestWrapper. The resulting file
	 * is uses to set the uploadFilePath, uploadedFileName, and uploadedFileSystemName variables.
	 * @return
	 */
	protected boolean uploadHelper() {		
		try {
			MultiPartRequestWrapper multiWrapper = (MultiPartRequestWrapper) ServletActionContext.getRequest();

			uploadedFiles = multiWrapper.getFiles("files");

			if (uploadedFiles != null && uploadedFiles.length > 0) 
			{
				uploadFilePath 			= uploadedFiles[0].getPath();
				uploadedFileSystemName 	= uploadedFiles[0].getAbsolutePath();
				uploadedFileNames 		= multiWrapper.getFileNames("files");
				uploadedFileName 		= uploadedFileNames[0];
				return (true);
				
			} else {
				final String msg = "No upload files discovered : " + multiWrapper.getErrors(); 
				addErrorMessage(msg);
				LOGGER.info(msg);
			}
		} catch (Exception exc) {
			final String msg = "Failed to upload the file";
			addErrorMessage(msg);
			LOGGER.log(Level.WARNING, msg, exc );
		}

		return (false);
	}

	/**
	 * Returns the actual path of the working directory referenced by the WORKING_DIR field.
	 * 
	 * @return String
	 */
	protected String getWorkingDirPath() {
		return (getTheSession().getServletContext().getRealPath(WORKING_DIR));
	}

	// attributes
	protected String uploadedFileName 		= null;
	protected String uploadedFileSystemName = null;
	protected String uploadFilePath 		= null;
	protected String outputRootDir 			= null;
	protected String password 				= "";
	protected InputStream fileStream	= null;
	protected java.io.File[] uploadedFiles 	= null;
	protected String[] uploadedFileNames		= null;
	protected static final String TYPE		= "text/plain";
	protected static final String WORKING_DIR = "/tech_stack_packages";
	private static final Logger LOGGER = Logger.getLogger(FileHandlerAction.class.getName());
}
