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

import com.realmethods.common.helpers.AWSHelper;
import com.realmethods.entity.Resource;
import com.realmethods.entity.ScopeType;
import com.realmethods.entity.dao.ResourceDAO;
import com.realmethods.entity.dao.UserDAO;
import com.realmethods.entity.User;
import com.realmethods.foundational.common.exception.FrameworkDAOException;

import com.google.gson.*;

import org.apache.commons.io.FilenameUtils;

/**
 * Handles resource related API invocations
 * 
 * @author realMethods, Inc.
 *
 */
public class ResourceAPIActionHelper extends BaseAPIActionHelper {
	/**
	 * default constructor
	 */
	public ResourceAPIActionHelper() {		
	}
	
	/**
	 * constructor
	 * @param input
	 * @param user
	 */
	public ResourceAPIActionHelper(JsonInput input, User user) {
		super( input, user );
	}
	

	/**
	 * Promotes or demotes a resource to/from public/private from/to private/public
	 * @param toPublic
	 * @return
	 */
	public JsonResult promoteResource( boolean toPublic ) {
		try {
			/////////////////////////////////////////////////////////////
			// helper method to return the requested resource
			/////////////////////////////////////////////////////////////
			Resource resource = determineResourceHelper();
			
			/////////////////////////////////////////////////////////////
			// only promote/demote if the resource is owned by the invocator
			/////////////////////////////////////////////////////////////			
			if (resource != null && belongsToUser(resource)) {

				resource.setScopeType( toPublic ? ScopeType.PUBLIC : ScopeType.PRIVATE );

				if ( jsonInput.getCost() != null )
					resource.setCost( jsonInput.getCost() );

				//////////////////////////////////
				// persist the updated resource
				//////////////////////////////////
				new ResourceDAO().save(resource);

				jsonResult.success(String.format(labels.getString(toPublic ? PROMOTE_SUCCESS : DEMOTE_SUCCESS), "resource"));				
				return jsonResult;
			}
		} catch (Exception exc) {
			LOGGER.log( Level.WARNING, "Failure during promoting a resource", exc );
		}

		jsonResult.error(String.format(labels.getString(toPublic ? PROMOTE_FAILURE : DEMOTE_FAILURE),
							"resource",
							jsonInput.getResourceId()));

		return jsonResult;

	}

	/**
	 * Return the requested resource
	 * @return
	 */
	public JsonResult getResource() {
		try {
			/////////////////////////////////////////////////////////////
			// helper method to return the requested resource
			/////////////////////////////////////////////////////////////			
			Resource resource = determineResourceHelper();

			/////////////////////////////////////////////////////////////
			// only return public or owned resources
			/////////////////////////////////////////////////////////////
			if	(resource != null 
					&& (resource.getScopeType() == ScopeType.PUBLIC || belongsToUser(resource)) ) {
					jsonResult.success();
					jsonResult.setResult(gson.toJson(resource));
					return jsonResult;
			}
		} catch (Exception exc) {
			LOGGER.log( Level.WARNING, "Failure during retrieving a resource", exc );
		}

		jsonResult.error(String.format(
				labels.getString("ModelReadError"), jsonInput.getModelId()));

		return jsonResult;

	}

	/**
	 * validates the resource if it is uniquely named
	 * @return
	 */
	public JsonResult validateResource() {
		try {
			/////////////////////////////////////////////////////////////
			// helper method to return the requested resource
			/////////////////////////////////////////////////////////////			
			if ( determineResourceHelper() == null ) {
				jsonResult.success(
						labels.getString("ResourceValidationSuccess"));
				return jsonResult;
			}
		} catch (Exception exc) {
			LOGGER.log( Level.WARNING, "Failure during validating a resource", exc );
		}
		jsonResult.error(labels.getString("ResourceValidationFailure"));
		return jsonResult;
	}
	

	/**
	 * return the list of resources
	 * 
	 * @return
	 */
	public JsonResult resourceList() {
		final JsonArray resourceArray 	= new JsonArray();
		JsonObject resourceData 		= null;
		
		try {
			//////////////////////////////////////////////////////////
			// depending upon filters and scope, return the resources
			// available to the invocator
			//////////////////////////////////////////////////////////
			List<Resource> resources	= determineWhichResourceList();

			Collections.sort(resources, new SortByName()); 

			for( Resource resource : resources ) {
				resourceData = new JsonObject();
				resourceData.addProperty("id", resource.getId());
				resourceData.addProperty("resourceType", resource.getResourceType().toString());
				resourceData.addProperty("scopeType", resource.getScopeType().toString());
				resourceData.addProperty("dateTime", resource.getDateTime());
				resourceData.addProperty("fileName", FilenameUtils.getName(resource.getFilePath()) );

				resourceData.addProperty(SAVE_PARAMS, gson.toJson(resource.getSaveParams()));
				resourceData.addProperty(CONTRIBUTOR, new UserDAO().findUser(resource.getOwnerId()).getEmail());
				
				resourceArray.add( resourceData );
			}
			jsonResult.success();
			jsonResult.setResult(gson.toJson(resourceArray));
			return jsonResult;
		} catch (FrameworkDAOException exc) {
			final String msg = exc.getMessage();
			LOGGER.log(Level.WARNING, "Failed to retrieve resources", exc);
			jsonResult.setResultCode(JsonResultCode.REQUEST_EXECUTION_ERROR);
			jsonResult.setProcessingMessage(msg);
			return jsonResult;
		}
	}
	
	/**
	 * Register the provided resource
	 * @return
	 */
	public JsonResult registerResource() {
		///////////////////////////////////////////////////////////////
		// only save the resource if it is valid (unique)
		///////////////////////////////////////////////////////////////
		if ( validateResource().isSuccess() ) {
			Resource resource = new Resource();
			resource.setName(jsonInput.getSaveParams().getName());
			resource.setDescription(jsonInput.getSaveParams().getDescription());
			resource.setOwnerId(user.getId());
			resource.setScopeType(jsonInput.getScopeType());
			resource.setResourceType(jsonInput.getResourceType());
			resource.setFilePath(AWSHelper.self()
					.stripAWSUrlParts(jsonInput.getS3FileLocation()));
			resource.setCost(jsonInput.getCost());
			try {
				resource = new ResourceDAO().create(resource);
				jsonResult.success(labels.getString("ResourceRegistrationSuccess"));
				jsonResult.setResult(resource.getId().toString());
				return jsonResult;
			} catch (FrameworkDAOException exc) {
				LOGGER.log( Level.WARNING, "Failure during registering a resource", exc );
			}
		}
		jsonResult.error(
				labels.getString("ResourceRegistrationFailure"));
		return jsonResult;
	}

	/**
	 * delete the referenced resource
	 * @return
	 */
	public JsonResult deleteResource() {
		try {			
			//////////////////////////////////////////////////////////
			// depending upon filters and scope, return the resources
			// available to the invocatotr
			//////////////////////////////////////////////////////////
			Resource resource = determineResourceHelper();
			
			//////////////////////////////////////////////////////////
			// if exists, not public, and requested by the owner
			//////////////////////////////////////////////////////////
			if (resource != null ) {
				if ( resource.getScopeType() == ScopeType.PRIVATE 
					&& belongsToUser(resource)) {
					new ResourceDAO().delete(resource.getId());
					jsonResult.success(labels.getString("DeleteResourceSuccess"));
					return jsonResult;
				}
				else {
					jsonResult.error(labels.getString("DeleteResourceFailure"));
					return jsonResult;
				}
			}
		} catch (Exception exc) {
			LOGGER.log( Level.WARNING, "Failure during deleting a resource", exc );
		}

		jsonResult.setResultCode(JsonResultCode.REQUEST_EXECUTION_ERROR);
		jsonResult.setProcessingMessage(String.format(
				labels.getString("ResourceReadError"), jsonInput.getResourceId()));

		return jsonResult;

	}

	// attributes
	private static final Logger LOGGER 	= Logger.getLogger(ResourceAPIActionHelper.class.getName());

}