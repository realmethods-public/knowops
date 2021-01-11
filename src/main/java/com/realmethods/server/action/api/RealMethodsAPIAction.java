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

import com.realmethods.api.JsonInput;
import com.realmethods.api.JsonResult;
import com.realmethods.api.JsonResultCode;
import com.realmethods.api.JsonServiceRequestType;
import com.realmethods.common.helpers.AWSHelper;
import com.realmethods.common.helpers.AppGenHelper;
import com.realmethods.entity.FrameworkPackage;
import com.realmethods.entity.GeneratedAppDetails;
import com.realmethods.entity.LocalModel;
import com.realmethods.entity.Resource;
import com.realmethods.entity.ScopeType;
import com.realmethods.entity.User;
import com.realmethods.entity.dao.FrameworkPackageDAO;
import com.realmethods.entity.dao.GeneratedAppDetailsDAO;
import com.realmethods.entity.dao.LocalModelDAO;
import com.realmethods.entity.dao.UserDAO;
import com.realmethods.foundational.common.exception.FrameworkDAOException;
import com.realmethods.foundational.common.exception.ProcessingException;
import com.realmethods.server.action.GenerateAppAction;
import com.realmethods.server.service.*;

import com.google.gson.*;

import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Serves as the RESTful API gateway to the realMethods services.
 * 
 * @author realMethods, Inc.
 *
 */
public class RealMethodsAPIAction extends GenerateAppAction {

	/**
	 * Default constructor.
	 * 
	 * Loads the labels field as a Resource bundle named jsonAPIMessages.
	 * 
	 */
	public RealMethodsAPIAction() {
		// intentionally left empty
	}

	/**
	 * Assigns the input field using the provided argument.
	 * 
	 * @param input
	 */
	public void setInput(String input) {
		this.input = input;
	}

	/**
	 * Returns the jsonResult field.
	 * 
	 * @return
	 */
	public JsonResult getResult() {
		return jsonResult;
	}

	/**
	 * Single entry point for service invocation. Supports all Service type
	 * calls as reference by enum com.realmethods.api.JsonServiceRequestType.
	 * 
	 * The services calls require input, as referenced by class
	 * com.realmethods.api.JsonInput.
	 * 
	 * The service calls return results in the form JSON according to the
	 * structure of class com.realmethods.api.JsonResult
	 */
	@Override
	public String execute() {
		jsonResult = internalExecute();
		
		String msg = "JSON output is " + jsonResult.toString();
		LOGGER.info(msg);
		
		return jsonResult.isSuccess() ? SUCCESS : ERROR;
	}

	/**
	 * internal execution method.  Handles validated token before proceeding to handle the
	 * invocated action.
	 * @return
	 */
	private JsonResult internalExecute() {
		
		gson = new GsonBuilder().create();

		try {
			String msg = "input is " + input;
			LOGGER.info(msg);

			//////////////////////////////////////////////////
			// validate the content of the jsonInput
			//////////////////////////////////////////////////
			if (input == null || input.isEmpty()) { //have to snatch it from the body
				String bodyString = org.apache.commons.io.IOUtils.toString(getServletRequest().getReader());
				bodyString = bodyString.substring(bodyString.indexOf('{'),
						bodyString.lastIndexOf('}') + 1);
				jsonInput = gson.fromJson(bodyString, JsonInput.class);
			} else {
				jsonInput = gson.fromJson(input, JsonInput.class);
			}

			msg = "JSON input is " + jsonInput.toString();
			LOGGER.info(msg);

		} catch (Exception exc) {
			LOGGER.log( Level.SEVERE, "Problem parsing jsonInput from body", exc );
			jsonResult.setResultCode(JsonResultCode.BAD_INPUT_STRUCTURE);;
			jsonResult.setResult(exc.getMessage());
			return jsonResult;
		}

		///////////////////////////////////////////////////////////////////////		
		// first always evaluate the token, either explicitly or implicitly.
		///////////////////////////////////////////////////////////////////////				
		if (!validateToken().isSuccess())
			return jsonResult;
		
		try {
			
			switch (jsonInput.getServiceRequestType()) {
				case VALIDATE_TOKEN: 
					jsonResult.setResultCode(JsonResultCode.SUCCESS);
					jsonResult.setProcessingMessage(String.format(labels.getString("ValidToken")));
					return jsonResult; // already validated prior
					
				//////////////////////////////////////////////////////////////////////
				// app related services	
				//////////////////////////////////////////////////////////////////////					
				case DELETE_APP:
					return new AppAPIActionHelper(this.jsonInput, this.user).deleteApp();
				case GET_APP:
					return new AppAPIActionHelper(this.jsonInput, this.user).getApp();				
				case ARCHIVED_APP_LIST:
					return new AppAPIActionHelper(this.jsonInput, this.user).archivedAppList();
				case PROMOTE_APP: 
					return new AppAPIActionHelper(this.jsonInput, this.user).promoteApp(true);
				case DEMOTE_APP: 
					return new AppAPIActionHelper(this.jsonInput, this.user).promoteApp(false);
				case CREATE_APP:
					return createApp();					

				//////////////////////////////////////////////////////////////////////
				// model related services	
				//////////////////////////////////////////////////////////////////////					
				case GET_MODEL:
					return new ModelAPIActionHelper(this.jsonInput, this.user).getModel();
				case DELETE_MODEL:
					return new ModelAPIActionHelper(this.jsonInput, this.user).deleteModel();
				case VALIDATE_MODEL:
					return new ModelAPIActionHelper(this.jsonInput, this.user).validateModel();
				case MODEL_LIST:
					return new ModelAPIActionHelper(this.jsonInput, this.user).modelList();
				case REGISTER_MODEL:
					return new ModelAPIActionHelper(this.jsonInput, this.user).registerModel();
				case PROMOTE_MODEL: 
					return new ModelAPIActionHelper(this.jsonInput, this.user).promoteModel(true);
				case DEMOTE_MODEL: 
					return new ModelAPIActionHelper(this.jsonInput, this.user).promoteModel(false);
					
				//////////////////////////////////////////////////////////////////////
				// stack related services	
				//////////////////////////////////////////////////////////////////////					
				case GET_TECH_STACK:
					return new TechStackAPIActionHelper(this.jsonInput, this.user).getTechStack();				
				case DELETE_TECH_STACK:
					return new TechStackAPIActionHelper(this.jsonInput, this.user).deleteTechStack();				
				case TECH_STACK_PACKAGE_LIST:
					return new TechStackAPIActionHelper(this.jsonInput, this.user).techStackPackageList();
				case VALIDATE_TECH_STACK:
					return new TechStackAPIActionHelper(this.jsonInput, this.user).validateTechStack();
				case REGISTER_TECH_STACK:
					return new TechStackAPIActionHelper(this.jsonInput, this.user).registerTechStack();
				case TECH_STACK_PACKAGE_OPTIONS:
					return new TechStackAPIActionHelper(this.jsonInput, this.user).techStackOptions();
				case PROMOTE_TECH_STACK: 
					return new TechStackAPIActionHelper(this.jsonInput, this.user).promoteTechStack(true);
				case DEMOTE_TECH_STACK: 
					return new TechStackAPIActionHelper(this.jsonInput, this.user).promoteTechStack(false);
					
				//////////////////////////////////////////////////////////////////////
				// resource related services	
				//////////////////////////////////////////////////////////////////////										
				case GET_RESOURCE:
					return new ResourceAPIActionHelper(this.jsonInput, this.user).getResource();					
				case REGISTER_RESOURCE:
					return new ResourceAPIActionHelper(this.jsonInput, this.user).registerResource();
				case RESOURCE_LIST:
					return new ResourceAPIActionHelper(this.jsonInput, this.user).resourceList();
				case DELETE_RESOURCE:
					return new ResourceAPIActionHelper(this.jsonInput, this.user).deleteResource();
				case DEMOTE_RESOURCE: 
					return new ResourceAPIActionHelper(this.jsonInput, this.user).promoteResource(false);
				case PROMOTE_RESOURCE: 
					return new ResourceAPIActionHelper(this.jsonInput, this.user).promoteResource(true);

				//////////////////////////////////////////////////////////////////////
				// project related services	
				//////////////////////////////////////////////////////////////////////										
				case GET_PROJECT:
					return new ProjectAPIActionHelper(this.jsonInput, this.user).getProject();					
				case REGISTER_PROJECT:
					setupOptions();
					return new ProjectAPIActionHelper(this.jsonInput, this.user).registerProject(options);
				case PROJECT_LIST:
					return new ProjectAPIActionHelper(this.jsonInput, this.user).projectList();
				case DELETE_PROJECT:
					return new ProjectAPIActionHelper(this.jsonInput, this.user).deleteProject();
				case DEMOTE_PROJECT: 
					return new ProjectAPIActionHelper(this.jsonInput, this.user).promoteProject(false);
				case PROMOTE_PROJECT: 
					return new ProjectAPIActionHelper(this.jsonInput, this.user).promoteProject(true);

				case USER_INFO: 
					return userInfo();
				
				default:
					StringBuilder requests = new StringBuilder();
					for (JsonServiceRequestType type : JsonServiceRequestType
							.values()) {
						if (type != JsonServiceRequestType.UNKNOWN)
							requests.append(type.name() + " ");
					}
	
					jsonResult
							.setResultCode(JsonResultCode.INVALID_SERVICE_REQUEST);
					jsonResult.setProcessingMessage(
							String.format(labels.getString("InvalidServiceRequest"),
									requests.toString()));
					return jsonResult;
			}
		} catch (ProcessingException exc) {
			LOGGER.warning("Generate Processing Error");
		}

		jsonResult.setResultCode(JsonResultCode.INVALID_SERVICE_REQUEST);
		jsonResult.setProcessingMessage(String.format(labels.getString("InvalidServiceRequest")));
		return jsonResult;
	}

	/**
	 * Return the current user's information
	 * @return
	 */
	private JsonResult userInfo() {
		try {
			///////////////////////////////////////////////////////////////////////		
			// locate the user using the provided token
			///////////////////////////////////////////////////////////////////////		
			user = new UserDAO().findUserByToken( jsonInput.getToken() );
			
			if ( user != null ) {
				JsonObject modelData = new JsonObject();
				
				modelData.addProperty("name", user.getFullName());
				modelData.addProperty("email", user.getEmail());
				modelData.addProperty("company", user.getCompany());
				modelData.addProperty("usageLevel", user.getUserType().toString());
				
				jsonResult.success();
				jsonResult.setResult(gson.toJson(modelData));
				
				return jsonResult;				
			}
		} catch( FrameworkDAOException exc ) {			
			LOGGER.log(Level.WARNING, "Failure acquiring user info", exc);
		}
		
		jsonResult.setResultCode(JsonResultCode.REQUEST_EXECUTION_ERROR);
		jsonResult.setProcessingMessage(
				String.format(labels.getString("UserInfoFailure")));
		return jsonResult;

	}
	
	/**
	 * 
	 * @return
	 */
	private JsonResult validateToken() {
		// check as input
			
		try {
			String inputToken = this.jsonInput.getToken();

			// if none provided, check if there is a cached User
			if (inputToken == null || inputToken.isEmpty() ) {

				LOGGER.info("No token provided as part of input. Checking if there is a cached user.");
		   		user = getUser();
				if ( user != null ) {
					inputToken = user.getInternalIdentifier();
					jsonInput.setToken(inputToken);
					LOGGER.log(Level.INFO, "Using token identifier {0} as the input token.", inputToken );
				}
			}
			else
				user = new UserDAO().findUserByToken(inputToken);

			if (user != null) {
				setUser( user ); // cache it in the HttpSession
				JsonObject propValue = new JsonObject();
				propValue.addProperty("token",
						user.getInternalIdentifier());
				jsonResult.success();
				jsonResult.setResult(gson.toJson(propValue));
				jsonResult.setProcessingMessage(labels.getString("Success"));
				return jsonResult;
			}
		} catch (Exception exc) {
			// no_op
		}

		jsonResult.error();
		jsonResult.setResultCode(JsonResultCode.INVALID_TOKEN);
		jsonResult.setProcessingMessage(labels.getString(INVALID_TOKEN));
		return jsonResult;
	}

	/**
	 * validates the input for correctness and completeness, then creates the
	 * app by first delegating to the correct model handler then delegate to the
	 * base class to create the app based on the correctly provided tech stack
	 * package reference (id, name, or shortName)
	 * 
	 * @return
	 */
	private JsonResult createApp() throws ProcessingException {
		//////////////////////////////////////////////////////////////////////
		// assigns the tech stack package internally 
		//////////////////////////////////////////////////////////////////////
		if (!deduceFrameworkPackage(jsonInput.getTechStackPackageId())
				|| jsonInput.getModelId() == null) {
			jsonResult.error();
			jsonResult.setProcessingMessage(String.format(
			labels.getString("UnknownTechStackPackage"), jsonInput.getTechStackPackageId() ));

			return jsonResult;
		}

		/////////////////////////////////////////////////
		// need to load the model into the current thread
        /////////////////////////////////////////////////
		String modelFileFullPath;
		try {
			LocalModel localModel = new LocalModelDAO()
					.findLocalModel(jsonInput.getModelId());
			modelFileFullPath = localModel.getFilePath();
			
			////////////////////////////////////////////////////////
			// if a non .git file, need to turn it into a local
			// file path
			///////////////////////////////////////////////////////
			/*if ( !modelFileFullPath.endsWith(".git") ) {
				modelFileFullPath = AppGenHelper
					.remedyFileLocation(AWSHelper.self().getS3BucketLocation()
							+ modelFileFullPath, "model_");
			}*/
			
		} catch (FrameworkDAOException exc) {
			jsonResult.error();
			jsonResult.setResultCode(JsonResultCode.REQUEST_EXECUTION_ERROR);
			jsonResult.setProcessingMessage(String.format(
					labels.getString("ModelLoadError"), exc.getMessage()));
			return jsonResult;
		}

		///////////////////////////////////////////////////
		// Handle loading the model
		///////////////////////////////////////////////////
		applyUniqueThreadName(false /* append model */ );
		boolean status = new BaseAPIActionHelper(jsonInput, user).loadModelHelper(modelFileFullPath);

		if (!status)
			return jsonResult;

		///////////////////////////////////////////////////
		// Set up options
		///////////////////////////////////////////////////
		setupOptions();

		///////////////////////////////////////////////////
		// Generate the application
		///////////////////////////////////////////////////
		try {
			if (internalGenerateApp()) {
				jsonResult.success(labels.getString("CreateSuccess"));
				return jsonResult;
			}
		} catch (Exception exc) {
			// no_op
		}

		jsonResult.error(String.format(
				labels.getString("AppCreationFailure"),
				jsonInput.getTechStackPackageId(), jsonInput.getModelId()));
		return jsonResult;

	}

	/**
	 * delegate app generation to the base class
	 * 
	 * @return
	 */
	private String generateApp() {
		try {
			return super.exec();
		} catch (Exception exc) {
			return ERROR;
		}
	}


   /**
    * assigns the input options to the internal format
    */
	private void setupOptions() {
		assignOptionsInternally(
				AppGenHelper.dissectOptions(jsonInput.getAppOptions()));
	}

	/**
	 * internal helpe to generating an app (project)
	 * @return
	 */
	private boolean internalGenerateApp() {

		////////////////////////////////////////////////////////
		// initialize
		////////////////////////////////////////////////////////
		setGitProject(jsonInput.getUseGit());
		setGitParams(jsonInput.getGitParams());
		setOverWrite(false);

		////////////////////////////////////////////////////////
		// delegate to worker
		////////////////////////////////////////////////////////
		String generateAppStatus = generateApp();

		if (generateAppStatus != SUCCESS ) {
			jsonResult.error( generateAppStatus + " : " + getStatus().getErrMessages() );
			return false;
		}
		
		return true;
	}

	@Override
	/**
	 * Save the generated application details
	 * 
	 * @param zipFileName
	 * @return
	 */
	protected GeneratedAppDetails persistApp(String zipFileName) {
		GeneratedAppDetails genAppDetails = null;

		try {
			genAppDetails = new GeneratedAppDetails();

			////////////////////////////////////////////////////////////////////////////////
			// use the name and description provided as part of the call's save params,
			// and if not available, use the application.name/description found in 
			// the package options that are also provided as part of the input
			////////////////////////////////////////////////////////////////////////////////			
			String name = jsonInput.getSaveParams().getName();
			
			if ( name == null || name.isEmpty() )
				name = this.options.get("application.name");
				
			genAppDetails.setName(name);

			String description = jsonInput.getSaveParams().getDescription();
			
			if ( description == null || description.isEmpty() )
				description = this.options.get("application.description");
				
			genAppDetails.setDescription( description );
			
			genAppDetails.setScopeType(jsonInput.getScopeType());
			genAppDetails.setOwnerId(user.getId());
			genAppDetails.setPackageId(jsonInput.getTechStackPackageId());
			genAppDetails.setModelId(jsonInput.getModelId());
			genAppDetails.setFilePath(
					AWSHelper.self().stripAWSUrlParts(zipFileName));
			new GeneratedAppDetailsDAO().create(genAppDetails);
			addInfoMessage("Successfully persited the bundled generated app");
		} catch (Exception exc) {
			LOGGER.log(Level.WARNING, "Failed to persist the app", exc);
			addInfoMessage("failed to bundle the generated app - "
					+ exc.getMessage());
		}

		return genAppDetails;
	}

	/**
	 * Helper method use to determine the framework package from the inputPkgId.
	 * The id could be the package identified, the package name, or the package
	 * short name.
	 * 
	 * @param inputPkgId
	 * @return boolean
	 * @throws ProcessingException
	 */
	protected boolean deduceFrameworkPackage(final String inputPkgId) {
		try {
			if (inputPkgId != null) {
				////////////////////////////////////////////////////////////////////////////////
				// locate the tech stack package using the input pkg id
				////////////////////////////////////////////////////////////////////////////////
				FrameworkPackage pkg= new FrameworkPackageDAO().findByNameorId(inputPkgId);

				////////////////////////////////////////////////////////////////////////////////
				// delegate and return with success if the tech stack package is either public
				// or owned by this user
				////////////////////////////////////////////////////////////////////////////////
				if ( pkg.getScopeType() == ScopeType.PUBLIC
									|| new BaseAPIActionHelper(jsonInput,user).belongsToUser( pkg )) {
					super.setPackageId(pkg.getId());
					jsonResult.success();
					return true;
				}
			}
		} catch (Exception exc) {
			final String msg = "Failed during call looking for a Tech Stack Pkg using identifier " + inputPkgId;
			LOGGER.log(Level.WARNING, msg, exc);
		}

		jsonResult.setResultCode(JsonResultCode.UNKNOWN_TECH_STACK_PACKAGE);
		jsonResult.setProcessingMessage(String.format(
				labels.getString("UnknownTechStackPackage"), inputPkgId));
		return false;
	}



	// attributes
	////////////////////////////////////////////////////////////////
	// input data as call for service
	////////////////////////////////////////////////////////////////
	protected String input 						= null;
	////////////////////////////////////////////////////////////////
	// JSON input data holder
	////////////////////////////////////////////////////////////////
	protected transient JsonInput jsonInput 	= null;
	////////////////////////////////////////////////////////////////
	// JSON service invocation result data holder
	////////////////////////////////////////////////////////////////
	protected transient JsonResult jsonResult 	= new JsonResult();
	////////////////////////////////////////////////////////////////
	// Google JSON implementation
	////////////////////////////////////////////////////////////////
	protected transient Gson gson 				= null;
	////////////////////////////////////////////////////////////////
	// the user as service invoker
	////////////////////////////////////////////////////////////////
	protected User user							= null;
	////////////////////////////////////////////////////////////////
	// static declarations
	////////////////////////////////////////////////////////////////
	private static final String INVALID_TOKEN 	= "InvalidToken";
	////////////////////////////////////////////////////////////////
	// Logging implementation
	////////////////////////////////////////////////////////////////
	private static final Logger LOGGER 			= Logger.getLogger(RealMethodsAPIAction.class.getName());

}
