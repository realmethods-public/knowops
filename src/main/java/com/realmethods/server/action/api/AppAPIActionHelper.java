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

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.realmethods.api.JsonInput;
import com.realmethods.api.JsonResult;
import com.realmethods.api.JsonResultCode;
import com.realmethods.common.helpers.AWSHelper;
import com.realmethods.entity.User;
import com.realmethods.entity.dao.GeneratedAppDetailsDAO;
import com.realmethods.entity.dao.UserDAO;
import com.realmethods.entity.GeneratedAppDetails;
import com.realmethods.entity.ScopeType;
import com.realmethods.foundational.common.exception.FrameworkDAOException;

import com.google.gson.*;


/**
 * Handles app related API invocations
 * 
 * @author realMethods, Inc.
 *
 */
public class AppAPIActionHelper extends BaseAPIActionHelper {
	/**
	 * default constructor
	 */
	public AppAPIActionHelper() {		
	}
	
	/**
	 * constructor
	 * @param input
	 * @param user
	 */
	public AppAPIActionHelper(JsonInput input, User user ) {
		super( input, user );
	}
	
	/**
	 * Returns the app requested by the invocator
	 * @return
	 */
	public JsonResult getApp() {
		String msg = "";
		try {
			////////////////////////////////////////////////////////////////////
			// returns the requested app using the input id
			////////////////////////////////////////////////////////////////////
			GeneratedAppDetails appDetails = new GeneratedAppDetailsDAO()
					.findGeneratedAppDetails(jsonInput.getGeneratedAppId());
			jsonResult.success();
			jsonResult.setResult(gson.toJson(appDetails));
			return jsonResult;
		} catch (Exception exc) {
			msg = exc.getMessage();
		}
		jsonResult.error(
				String.format(labels.getString("RequestExecutionError"),
						jsonInput.getServiceRequestType(), msg));

		return jsonResult;
	}

	/**
	 * Promote or demote the app to/from public from/to private
	 * @param toPublic
	 * @return
	 */
	public JsonResult promoteApp( boolean toPublic ) {
		try {
			final GeneratedAppDetailsDAO dao = new GeneratedAppDetailsDAO();
			GeneratedAppDetails app = dao
					.findGeneratedAppDetails(jsonInput.getGeneratedAppId());

			///////////////////////////////////////////////////////////////////////
			// determine if the app belongs to the invocating user
			///////////////////////////////////////////////////////////////////////
			if (belongsToUser(app)) {
				app.setScopeType(toPublic ? ScopeType.PUBLIC : ScopeType.PRIVATE);
				
				if ( jsonInput.getCost() != null )
					app.setCost( jsonInput.getCost() );
				
				dao.save(app);
				
				jsonResult.success(
						String.format(labels.getString(toPublic ? PROMOTE_SUCCESS : DEMOTE_SUCCESS), "application"));

				return jsonResult;
			}
		} catch (Exception exc) {
			LOGGER.log( Level.WARNING, "Failure during application promotion", exc );
		}

		jsonResult.error(
				String.format(labels.getString(toPublic ? PROMOTE_FAILURE : DEMOTE_FAILURE),
						"application",
						jsonInput.getGeneratedAppId()));

		return jsonResult;

	}
	
	/**
	 * delete the application 
	 * 
	 * @return
	 */
	public JsonResult deleteApp() {
		Long generatedAppId = this.jsonInput.getGeneratedAppId();
		String msg = "Unable to delete the application archive referenced by "
				+ generatedAppId + ". ";
		try {
			final GeneratedAppDetailsDAO dao = new GeneratedAppDetailsDAO();
			GeneratedAppDetails appDetails = dao
					.findGeneratedAppDetails(generatedAppId);
			if (appDetails != null ) {
				if ( appDetails.getScopeType() == ScopeType.PRIVATE 
						&& belongsToUser(appDetails)) {
					AWSHelper.self().deleteS3BucketFile(appDetails.getFilePath());
					dao.delete(generatedAppId);
	
					jsonResult.success(labels.getString("DeleteAppSuccess"));	
				}
				else {
					jsonResult.error(labels.getString("DeleteAppFailure"));					
				}
				return jsonResult;
			}
		} catch (Exception exc) {
			msg += "[Exception : " + exc.getMessage() + "]";
		}

		jsonResult.error(msg);
		return jsonResult;
	}

	/**
	 * return the list of previously generated apps
	 * 
	 * @return
	 */
	public JsonResult archivedAppList() {
		final JsonArray appArray				= new JsonArray();
		final UserDAO userDAO					= new UserDAO();
		JsonObject appData 						= null;

		try {					
			final List<GeneratedAppDetails> apps 	= determineWhichAppList();
			for( GeneratedAppDetails app: apps ){
					appData = new JsonObject();
					appData.addProperty("id", app.getId());
					appData.addProperty(SAVE_PARAMS, gson.toJson(app.getSaveParams()));
					appData.addProperty("scopeType", app.getScopeType().toString());
					appData.addProperty("dateTime", app.getDateTime());
					appData.addProperty(CONTRIBUTOR, userDAO.findUser(app.getOwnerId()).getEmail());
					appData.addProperty("filePath", app.getFilePath() );
					appArray.add( appData );
			}
		} catch (FrameworkDAOException exc) {
			final String msg = exc.getMessage();
			LOGGER.log(Level.WARNING, "RealMethodsAPIAction.archivedAppList()",
					exc);
			jsonResult.setResultCode(JsonResultCode.REQUEST_EXECUTION_ERROR);
			jsonResult.setProcessingMessage(msg);
			return jsonResult;
		}
		
		/////////////////////////////////////////////////////////////
		// assign the app array to the result as a Json Array
		/////////////////////////////////////////////////////////////
		jsonResult.success();
		jsonResult.setResult(gson.toJson(appArray));
		return jsonResult;

	}
	// attributes
	private static final Logger LOGGER 	= Logger.getLogger(AppAPIActionHelper.class.getName());
	
}