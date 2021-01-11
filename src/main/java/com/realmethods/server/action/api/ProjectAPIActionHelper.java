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
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.realmethods.api.JsonInput;
import com.realmethods.api.JsonResult;
import com.realmethods.api.JsonResultCode;

import com.realmethods.common.helpers.AWSHelper;
import com.realmethods.entity.Project;
import com.realmethods.entity.ScopeType;
import com.realmethods.entity.dao.ProjectDAO;
import com.realmethods.entity.dao.UserDAO;
import com.realmethods.entity.User;
import com.realmethods.foundational.common.exception.FrameworkDAOException;

import com.google.gson.*;

import org.apache.commons.io.FilenameUtils;

/**
 * Handles project related API invocations
 * 
 * @author realMethods, Inc.
 *
 */
public class ProjectAPIActionHelper extends BaseAPIActionHelper {
	/**
	 * default constructor
	 */
	public ProjectAPIActionHelper() {		
	}
	
	/**
	 * constructor
	 * @param input
	 * @param user
	 */
	public ProjectAPIActionHelper(JsonInput input, User user) {
		super( input, user );
	}
	

	/**
	 * Promotes or demotes a Project to/from public/private from/to private/public
	 * @param toPublic
	 * @return
	 */
	public JsonResult promoteProject( boolean toPublic ) {
		try {
			/////////////////////////////////////////////////////////////
			// helper method to return the requested Project
			/////////////////////////////////////////////////////////////
			Project project = determineProjectHelper();
			
			/////////////////////////////////////////////////////////////
			// only promote/demote if the Project is owned by the invocator
			/////////////////////////////////////////////////////////////			
			if (project != null && belongsToUser(project)) {

				project.setScopeType( toPublic ? ScopeType.PUBLIC : ScopeType.PRIVATE );

				if ( jsonInput.getCost() != null )
					project.setCost( jsonInput.getCost() );

				//////////////////////////////////
				// persist the updated Project
				//////////////////////////////////
				new ProjectDAO().save(project);

				jsonResult.success(String.format(labels.getString(toPublic ? PROMOTE_SUCCESS : DEMOTE_SUCCESS), "Project"));				
				return jsonResult;
			}
		} catch (Exception exc) {
			LOGGER.log( Level.WARNING, "Failure during promoting a Project", exc );
		}

		jsonResult.error(String.format(labels.getString(toPublic ? PROMOTE_FAILURE : DEMOTE_FAILURE),
							"Project",
							jsonInput.getSaveParams().getName()));

		return jsonResult;

	}

	/**
	 * Return the requested Project
	 * @return
	 */
	public JsonResult getProject() {
		try {
			/////////////////////////////////////////////////////////////
			// helper method to return the requested Project
			/////////////////////////////////////////////////////////////			
			Project project = determineProjectHelper();

			/////////////////////////////////////////////////////////////
			// only return public or owned Projects
			/////////////////////////////////////////////////////////////
			if	(project != null 
					&& (project.getScopeType() == ScopeType.PUBLIC || belongsToUser(project)) ) {
					jsonResult.success();
					jsonResult.setResult(gson.toJson(project));
					return jsonResult;
			}
		} catch (Exception exc) {
			LOGGER.log( Level.WARNING, "Failure during retrieving a Project", exc );
		}

		jsonResult.error(String.format(
				labels.getString("ModelReadError"), jsonInput.getModelId()));

		return jsonResult;

	}

	/**
	 * validates the Project if it is uniquely named for the current user
	 * @return
	 */
	public JsonResult validateProject() {
		try {
			/////////////////////////////////////////////////////////////
			// helper method to return the requested Project
			/////////////////////////////////////////////////////////////		
			Project project = determineProjectHelper();
			if ( project == null || !belongsToUser(project) ) {
				jsonResult.success(
						labels.getString("ProjectValidationSuccess"));
				return jsonResult;
			}
		} catch (Exception exc) {
			LOGGER.log( Level.WARNING, "Failure during validating a Project", exc );
		}
		jsonResult.error(labels.getString("ProjectValidationFailure"));
		return jsonResult;
	}
	

	/**
	 * return the list of Projects
	 * 
	 * @return
	 */
	public JsonResult projectList() {
		final JsonArray projectArray 	= new JsonArray();
		JsonObject projectData 			= null;
		
		try {
			//////////////////////////////////////////////////////////
			// depending upon filters and scope, return the Projects
			// available to the invocator
			//////////////////////////////////////////////////////////
			List<Project> projects	= determineWhichProjectList();

			Collections.sort(projects, new SortByName()); 

			for( Project project : projects ) {
				projectData = new JsonObject();
				projectData.addProperty("id", project.getId());
				projectData.addProperty("name", project.getName());
				projectData.addProperty("modelId", project.getModelId());
				projectData.addProperty("techStackId", project.getTechStackPackageName());
				projectArray.add( projectData );
			}
			jsonResult.success();
			jsonResult.setResult(gson.toJson(projectArray));
			return jsonResult;
		} catch (FrameworkDAOException exc) {
			final String msg = exc.getMessage();
			LOGGER.log(Level.WARNING, "Failed to retrieve Projects", exc);
			jsonResult.setResultCode(JsonResultCode.REQUEST_EXECUTION_ERROR);
			jsonResult.setProcessingMessage(msg);
			return jsonResult;
		}
	}
	
	/**
	 * Register the provided Project
	 * @return
	 */
	public JsonResult registerProject(Map<String,String> appOptions) {
		///////////////////////////////////////////////////////////////
		// only save the Project if it is valid (unique)
		///////////////////////////////////////////////////////////////
		if ( validateProject().isSuccess() ) {
			Project project = new Project();
			project.setName(jsonInput.getSaveParams().getName());
			project.setDescription(jsonInput.getSaveParams().getDescription());
			project.setOwnerId(user.getId());
			project.setScopeType(jsonInput.getScopeType());
		    project.setTechStackPackageName(jsonInput.getTechStackPackageId());
		    project.setModelId(jsonInput.getModelId());
		    project.setOptions(appOptions);
		    project.setGitParams(jsonInput.getGitParams());
			try {
				project.setId(new ProjectDAO().create(project));
				jsonResult.success(labels.getString("ProjectRegistrationSuccess"));
				jsonResult.setResult(project.getId().toString());
				return jsonResult;
			} catch (FrameworkDAOException exc) {
				LOGGER.log( Level.WARNING, "Failure during registering a Project", exc );
			}
		}
		jsonResult.error(
				labels.getString("ProjectRegistrationFailure"));
		return jsonResult;
	}

	/**
	 * delete the referenced Project
	 * @return
	 */
	public JsonResult deleteProject() {
		try {			
			//////////////////////////////////////////////////////////
			// depending upon filters and scope, return the Projects
			// available to the invocatotr
			//////////////////////////////////////////////////////////
			Project project = determineProjectHelper();
			
			//////////////////////////////////////////////////////////
			// if exists, not public, and requested by the owner
			//////////////////////////////////////////////////////////
			if (project != null ) {
				if ( project.getScopeType() == ScopeType.PRIVATE 
					&& belongsToUser(project)) {
					new ProjectDAO().delete(project.getId());
					jsonResult.success(labels.getString("DeleteProjectSuccess"));
					return jsonResult;
				}
				else {
					jsonResult.error(labels.getString("DeleteProjectFailure"));
					return jsonResult;
				}
			}
		} catch (Exception exc) {
			LOGGER.log( Level.WARNING, "Failure during deleting a Project", exc );
		}

		jsonResult.setResultCode(JsonResultCode.REQUEST_EXECUTION_ERROR);
		jsonResult.setProcessingMessage(String.format(
				labels.getString("ProjectReadError"), jsonInput.getSaveParams().getName()));

		return jsonResult;

	}

	// attributes
	private static final Logger LOGGER 	= Logger.getLogger(ProjectAPIActionHelper.class.getName());

}