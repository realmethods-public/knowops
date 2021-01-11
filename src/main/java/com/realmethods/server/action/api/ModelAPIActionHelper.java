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
package com.realmethods.server.action.api;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.realmethods.api.JsonInput;
import com.realmethods.api.JsonResult;
import com.realmethods.api.JsonResultCode;

import com.realmethods.common.helpers.AppGenHelper;
import com.realmethods.common.helpers.AWSHelper;
import com.realmethods.entity.LocalModel;
import com.realmethods.entity.ModelType;
import com.realmethods.entity.ScopeType;
import com.realmethods.entity.User;
import com.realmethods.entity.dao.LocalModelDAO;
import com.realmethods.entity.dao.UserDAO;
import com.realmethods.foundational.common.exception.FrameworkDAOException;

import org.apache.commons.io.FilenameUtils;

import com.google.gson.*;

/**
 * Handles model related API invocations
 * 
 * @author realMethods, Inc.
 *
 */
public class ModelAPIActionHelper extends BaseAPIActionHelper {
	/**
	 * default constructor
	 */
	public ModelAPIActionHelper() {		
	}
	
	/**
	 * constructor
	 * @param input
	 * @param user
	 */
	public ModelAPIActionHelper(JsonInput input, User user ) {
		super( input, user );
	}

	/**
	 * Returns the requested LocalModel
	 * @return
	 */
	public JsonResult getModel() {
		try {
			////////////////////////////////////////////////////////////////////////////
			// helper method to get a LocalModel from the persistent store based on
			// the invocator's request
			////////////////////////////////////////////////////////////////////////////			
			final LocalModel model = this.determineLocalModelHelper();

			////////////////////////////////////////////////////////////////////////////
			// only allows access to a public or owned model
			////////////////////////////////////////////////////////////////////////////
			if ( model != null 
				&& (model.getScopeType() == ScopeType.PUBLIC || belongsToUser(model)) ) {
					jsonResult.success();
					jsonResult.setResult(gson.toJson(model));
					return jsonResult;
			}
		} catch (Exception exc) {
			LOGGER.log( Level.WARNING, "Failure during finding a model", exc );
		}

		jsonResult.error(String.format(labels.getString("ModelReadError"), jsonInput.getModelId()));
		jsonResult.setResultCode(JsonResultCode.REQUEST_EXECUTION_ERROR);

		return jsonResult;

	}

	/**
	 * Register a model.
	 * 
	 * @return
	 */
	public JsonResult registerModel() {
		////////////////////////////////////////////////
		// validate then save
		////////////////////////////////////////////////
		if (validateModel().isSuccess()) {
			
			/////////////////////////////////////////////////////
			// persist this thread's model to a local file as JSON
			/////////////////////////////////////////////////////
			try {
				final String newModelFileName = FilenameUtils.getName(jsonInput.getS3FileLocation());
				LocalModel localModel = AppGenHelper.saveModel( user, 
										newModelFileName, 
										AppGenHelper.remedyFileLocation(jsonInput.getS3FileLocation(), "model_"), 
										jsonInput.getSaveParams(), 
										jsonInput.getPojoParams() );

				if ( localModel != null ) {
					jsonResult.success(labels.getString("ModelRegistrationSuccess"));
					jsonResult.setResult(localModel.getId().toString());
					return jsonResult;
				}
			} catch( Exception exc ) {
				LOGGER.log( Level.WARNING, "Failure during registering a model", exc );
			}
		}

		jsonResult.error(labels.getString("ModelRegistratonFailure"));
		jsonResult.setResultCode(JsonResultCode.REQUEST_EXECUTION_ERROR);
		return jsonResult;
	}


	/**
	 * Delete a model
	 * @return
	 */
	public JsonResult deleteModel() {
		try {
			////////////////////////////////////////////////////////////////////////////
			// helper method to get a LocalModel from the persistent store based on
			// the invocator's request
			////////////////////////////////////////////////////////////////////////////			
			LocalModel model = determineLocalModelHelper();

			/////////////////////////////////////////////////////////////
			// can only delete if requested by the owner and not public
			/////////////////////////////////////////////////////////////
			if (model != null && model.getScopeType() == ScopeType.PRIVATE 
					&& belongsToUser(model)) {
				new LocalModelDAO().delete(jsonInput.getModelId());
				
				jsonResult.success(labels.getString("DeleteModelSuccess"));
				return jsonResult;
			}
			else {
					jsonResult.error(labels.getString("DeleteModelFailure"));
			}

		} catch (Exception exc) {
			LOGGER.log( Level.WARNING, "Failure during deleting a model", exc );
		}

		jsonResult.setResultCode(JsonResultCode.REQUEST_EXECUTION_ERROR);
		jsonResult.setProcessingMessage(String.format(
				labels.getString("ModelReadError"), jsonInput.getModelId()));

		return jsonResult;

	}

	/**
	 * Promote or demote the model, depending on the public flag
	 * @param toPublic
	 * @return
	 */
	public JsonResult promoteModel( boolean toPublic ) {
		try {
			////////////////////////////////////////////////////////////////////////////
			// helper method to get a LocalModel from the persistent store based on
			// the invocator's request
			////////////////////////////////////////////////////////////////////////////			
			LocalModel model = determineLocalModelHelper();

			////////////////////////////////////////////////////////////////////////////
			// if the model belongs to the user assign ScopyType according to 
			// toPublic boolean value.
			////////////////////////////////////////////////////////////////////////////
			if (belongsToUser( model)) {
				model.setScopeType( toPublic ? ScopeType.PUBLIC : ScopeType.PRIVATE );

				if ( jsonInput.getCost() != null )
					model.setCost( jsonInput.getCost() );

				////////////////////////////////////////////////
				// persist using related DAO
				////////////////////////////////////////////////
				new LocalModelDAO().save(model);

				jsonResult.success(String.format(labels.getString(toPublic ? PROMOTE_SUCCESS : DEMOTE_SUCCESS), "model"));
				return jsonResult;
			}
		} catch (Exception exc) {
			LOGGER.log( Level.WARNING, "Failure during promoting a model", exc );
		}

		jsonResult.setResultCode(JsonResultCode.REQUEST_EXECUTION_ERROR);
		jsonResult.setProcessingMessage(
				String.format(labels.getString(toPublic ? PROMOTE_FAILURE : DEMOTE_FAILURE),
						"model",
						jsonInput.getModelId()));

		return jsonResult;

	}

	/**
	 * return the list of models
	 * 
	 * @return
	 */
	public JsonResult modelList() {
		final JsonArray modelArray 		= new JsonArray();
		final UserDAO userDAO			= new UserDAO();				
		JsonObject modelData 			= null;

		try {
			final List<LocalModel> models	= determineWhichModelList();

			////////////////////////////////////////////////////////////////
			//  sort the packages by the short name
			////////////////////////////////////////////////////////////////
			Collections.sort(models, new SortByName()); 

			for( LocalModel model : models ) {
					modelData = new JsonObject();
					modelData.addProperty("id", model.getId());
					modelData.addProperty("modelType", model.getModelType().toString());
					modelData.addProperty("scopeType", model.getScopeType().toString());
					modelData.addProperty("dateTime", model.getDateTime());
					modelData.addProperty("fileName", model.getOriginalFileName());
					modelData.addProperty(SAVE_PARAMS, gson.toJson(model.getSaveParams()));
					modelData.addProperty(CONTRIBUTOR, userDAO.findUser(model.getOwnerId()).getEmail());
		
					modelArray.add( modelData );
					
			}
		} catch (FrameworkDAOException exc) {
			final String msg = exc.getMessage();
			LOGGER.log(Level.WARNING, "Failed to retrieve models", exc);

			jsonResult.error(msg);
			jsonResult.setResultCode(JsonResultCode.REQUEST_EXECUTION_ERROR);
			return jsonResult;
		}

		////////////////////////////////////////////////////
		// assign the Json version of the model array as
		// the result
		////////////////////////////////////////////////////
		jsonResult.success();
		jsonResult.setResult(gson.toJson(modelArray));
		return jsonResult;
	}

	/**
	 * Validate the model by executing it through the meta-model subsystem, 
	 * but do not persist it
	 * @return
	 */
	public JsonResult validateModel() {
		String msg = "";
		try {			
			////////////////////////////////////////////////////////////////////////////
			// turn a remote model as an S3 object (file) into one that is local.
			// .git files are treated slightly different since they reference the actual
			// URL of the object and are not S3 objects.
			////////////////////////////////////////////////////////////////////////////						
			String modelFilePath = handleLoadingModelIntoFile(jsonInput.getS3FileLocation());
			
			////////////////////////////////////////////////////////////////////////////
			// helper method to turn a file system based model into a realMethods
			// meta-model instance on the current thread
			////////////////////////////////////////////////////////////////////////////			
			if (this.loadModelHelper(modelFilePath)) {
				jsonResult.success(labels.getString("ModelValidationSuccess"));
				return jsonResult;
			}
		} catch (Exception exc) {
			LOGGER.log( Level.WARNING, "Failure during validating a model", exc );
			msg = exc.getMessage();
		}
		jsonResult.error(labels.getString("ModelValidationFailure") + " : " + msg);
		jsonResult.setResultCode(JsonResultCode.REQUEST_EXECUTION_ERROR);
		return jsonResult;
	}
	
	// attributes
	private static final Logger LOGGER 	= Logger.getLogger(ModelAPIActionHelper.class.getName());
	
}