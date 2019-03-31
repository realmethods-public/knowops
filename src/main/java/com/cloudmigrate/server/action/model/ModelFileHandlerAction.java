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
package com.cloudmigrate.server.action.model;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.*;

import com.cloudmigrate.codetemplate.tools.JSONModelHandler;
import com.cloudmigrate.server.action.FileHandlerAction;
import com.cloudmigrate.entity.*;
import com.cloudmigrate.entity.dao.*;
import com.cloudmigrate.common.helpers.AWSHelper;
import com.cloudmigrate.common.helpers.AppGenHelper;
import com.cloudmigrate.entity.SaveParams;

/**
 * Serves as the base class of all ModelFileHandlers. Helper methods include
 * model file uploading, loading from disk, saving, and an overload to handle
 * parsing the model file.
 * 
 * @author realMethods, Inc.
 * 
 */
public class ModelFileHandlerAction extends FileHandlerAction {

	/**
	 * Default constructor
	 */
	public ModelFileHandlerAction() {
		// no_opf
	}

	/**
	 * Flag to determine if the newly referenced model should be tagged as
	 * sharable
	 * 
	 * @param shareModel
	 */
	public void setShareModel(boolean shareModel) {
		this.shareModel = shareModel;
	}

	/**
	 * Return the flag that determines if the newly referenced model should be
	 * appended to a previously loaded model. This feature is essential for the
	 * purpose of renovation, as well as the simple separation and combination
	 * of multiple models
	 * 
	 * @return boolean
	 */
	public boolean getAppendModel() {
		return this.appendModel;
	}

	/**
	 * Assign the flag that determines if the newly referenced model should be
	 * appended to a previously loaded model. This feature is essential for the
	 * purpose of renovation, as well as the simple separation and combination
	 * of multiple models
	 * 
	 * @param appendModel
	 */
	public void setAppendModel(boolean appendModel) {
		this.appendModel = appendModel;
	}

	/*
	 * Returns the JSON represented of the current loaded model
	 * 
	 * @return String
	 */
	public String getModel() {
		return (new JSONModelHandler().json());
	}

	/**
	 * Returns the sharedLocalModel field.
	 * 
	 * @return LocalModel
	 */
	public LocalModel getSharedModel() {

		return sharedLocalModel;
	}

	/**
	 * Assigns the sharedLocalModel field to the provided argument.
	 * 
	 * @param localModel
	 */
	public void setSharedModel(LocalModel localModel) {
		this.sharedLocalModel = localModel;
	}

	/**
	 * Returns the sharedModels field.
	 * 
	 * @return List<LocalModel>
	 */
	public List<LocalModel> getSharedModels() {
		return (this.sharedModels);
	}

	@Override
	/**
	 * Core execution routine, delegates internally to uploadModel.
	 * 
	 * @return String
	 */
	public String execute() {
		return (uploadModel());
	}

	/**
	 * Action handler used in conjunction with the base class' method
	 * uploadHelper() to: 1.) parse the model into the cloudMigrate App Creaton
	 * Model 2.) if the model is flagged as to be shared, persist necessary
	 * details about the model, including it's location on the file system
	 * 
	 * @return
	 */
	public String uploadModel() {
		if (uploadHelper()) {
			return handleModels(uploadedFiles, uploadedFileNames);
		}

		return (ERROR);
	}

	/**
	 * Bulk handler to deal with loading models
	 * @param modelFiles
	 * @param modelFileNames
	 * @return
	 */
	public String handleModels( final File[] modelFiles, final String [] modelFileNames ) {
		try {
			// force model appending on when processing more than one file
			if ( modelFiles.length > 1 )
				appendModel = true;
			
			AtomicInteger index = new AtomicInteger();
			new ArrayList<File>(Arrays.asList(modelFiles)).forEach( modelFile -> {
				parseModel( modelFile.getAbsolutePath());
				saveModel( modelFileNames[index.getAndIncrement()], modelFile.getAbsolutePath());
			});
			
		} catch (Exception exc) {
			LOGGER.info("ModelFileHandlerAction.handleModels() - " + exc.getMessage());
			addErrorMessage("Failed to load file " + this.uploadedFileName
					+ ". Make sure it exists and it is an XMI compliant file.");
			return( ERROR );
		}
		return( SUCCESS );
	}

	/**
	 * Action method used to return a JSON version of the related model found in
	 * the data source using a key field value provided by the client
	 * 
	 * @return
	 */
	public String findModel() {
		String retVal = SUCCESS;

		if (getId() != null) {
			try {
				sharedLocalModel = new LocalModelDAO().findLocalModel(getId());

				// run through and parse the model internally
				retVal = parseModel(AppGenHelper.remedyFileLocation(
										AWSHelper.self().getS3BucketLocation() + sharedLocalModel.getFilePath()));
			} catch (Exception exc) {
				addErrorMessage(
						"Failed to load model from the shared repository - "
								+ exc.getMessage());
				LOGGER.log(Level.WARNING, "Find model failed", exc);
				retVal = ERROR;
			}
		}

		return (retVal);
	}

	/**
	 * Loads all the model from the persistent store, and returns a JSON
	 * representation of them.
	 * 
	 * @return
	 */
	public String loadAllModels() {
		try {
			sharedModels = new LocalModelDAO().findAllLocalModel();
		} catch (Exception exc) {
			LOGGER.info(
					"ModelFileHandler.findAllModel() - failed to load all shared models : "
							+ exc.getMessage());
			return (ERROR);
		}

		return (SUCCESS);

	}

	/**
	 * Delegate method to first prepare the Session and current Thread before
	 * calling the sub-class to handleParseModel.
	 * 
	 * @param modelFileName
	 * @return String
	 */
	protected String parseModel(String modelFileName) {
		// assign to the session for use later for app generation
		getTheSession().setAttribute("modelFileName", modelFileName);
		applyUniqueThreadName(this.appendModel);
		return handleParseModel(modelFileName);
	}

	/**
	 * handler method to invoke on a sub-class to deal with loading the model,
	 * should be overloaded
	 * 
	 * @param modelFileName
	 * @return
	 */
	protected String handleParseModel(String modelFileName) {
		final String msg = "Method should be overloaded to handle file -" + modelFileName;
		LOGGER.severe(msg);
		return ERROR;
	}

	/**
	 * Save the current model.
	 * 
	 * @param	fileName
	 * @param	systemFileName
	 */
	protected void saveModel(String fileName, String systemFileName) {
		try {
			if (this.shareModel) {
				// persist the model name, description, and location on the
				// server

				// copy the file to the users directory
				java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
						"HHmmss");
				String usersDirectoryAndFile = FileUtils.getUserDirectoryPath()
						+ File.separator + "goFramework" + File.separator
						+ sdf.format(new Date()) + "__" + fileName;
				FileUtils.copyFile(new File(systemFileName),
						new File(usersDirectoryAndFile));

				AppGenHelper.saveModel(getUser(), fileName, usersDirectoryAndFile, saveParams);
			}
		} catch (Exception exc) {
			LOGGER.warning(
					"ModelFileHandlerAction:saveModel() exception caught - "
							+ exc.getMessage());
			addErrorMessage("Failed to save model file " + fileName);
		}
	}
	// attributes

	protected SaveParams saveParams 		= new SaveParams();
	protected boolean shareModel 			= false;
	protected boolean appendModel 			= false;
	protected LocalModel sharedLocalModel 	= null;
	protected List<LocalModel> sharedModels = null;
	private static final Logger LOGGER 		= Logger.getLogger(ModelFileHandlerAction.class.getName());
}
