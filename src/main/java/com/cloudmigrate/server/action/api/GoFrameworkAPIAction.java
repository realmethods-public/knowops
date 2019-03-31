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
package com.cloudmigrate.server.action.api;

import com.cloudmigrate.api.JsonInput;
import com.cloudmigrate.api.JsonResult;
import com.cloudmigrate.api.JsonResultCode;
import com.cloudmigrate.api.JsonServiceRequestType;
import com.cloudmigrate.codetemplate.parser.ModelParserFactory;
import com.cloudmigrate.common.helpers.AWSHelper;
import com.cloudmigrate.common.helpers.AppGenHelper;
import com.cloudmigrate.common.helpers.FrameworkPackageValidator;
import com.cloudmigrate.entity.BaseTransactionalEntity;
import com.cloudmigrate.entity.FrameworkPackage;
import com.cloudmigrate.entity.GeneratedAppDetails;
import com.cloudmigrate.entity.LocalModel;
import com.cloudmigrate.entity.ScopeType;
import com.cloudmigrate.entity.User;
import com.cloudmigrate.entity.dao.FrameworkPackageDAO;
import com.cloudmigrate.entity.dao.GeneratedAppDetailsDAO;
import com.cloudmigrate.entity.dao.LocalModelDAO;
import com.cloudmigrate.entity.dao.UserDAO;
import com.cloudmigrate.foundational.common.exception.FrameworkDAOException;
import com.cloudmigrate.foundational.common.exception.ProcessingException;
import com.cloudmigrate.server.action.GenerateAppAction;
import com.cloudmigrate.server.service.*;

import com.google.gson.*;

import java.io.StringReader;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.xml.sax.InputSource;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.apache.commons.io.FilenameUtils;

/**
 * Serves as the RESTful API gateway to the goFramework services.
 * 
 * @author realMethods, Inc.
 *
 */
public class GoFrameworkAPIAction extends GenerateAppAction {

	/**
	 * Default constructor.
	 * 
	 * Loads the labels field as a Resource bundle named jsonAPIMessages.
	 * 
	 */
	public GoFrameworkAPIAction() {
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
	 * calls as reference by enum com.cloudmigrate.api.JsonServiceRequestType.
	 * 
	 * The services calls require input, as referenced by class
	 * com.cloudmigrate.api.JsonInput.
	 * 
	 * The service calls return results in the form JSON according to the
	 * structure of class com.cloudmigrate.api.JsonResult
	 */
	@Override
	public String execute() {
		String status = internalExecute();
		
		String msg = "JSON output is " + jsonResult.toString();
		LOGGER.info(msg);
		
		return status;
	}
	
	private String internalExecute() {
		
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
			jsonResult.setResultCode(JsonResultCode.BAD_INPUT_STRUCTURE);
			jsonResult.setResult(exc.getMessage());
			return ERROR;
		}

		// first always evaluate the token, either explicitly or implicitly.
		if ( validateToken() == ERROR )
			return ERROR;
		
		try {
			
			switch (jsonInput.getServiceRequestType()) {
				case VALIDATE_TOKEN: 
					return SUCCESS; // already validated prior
				case CREATE_APP:
					return createApp();
				case DELETE_APP:
					return deleteApp();
				case GET_APP:
					return getApp();				
				case GET_MODEL:
					return getModel();
				case DELETE_MODEL:
					return deleteModel();
				case GET_TECH_STACK:
					return getTechStack();				
				case DELETE_TECH_STACK:
					return deleteTechStack();				
				case TECH_STACK_PACKAGE_LIST:
					return techStackPackageList();
				case MODEL_LIST:
					return modelList();
				case VALIDATE_MODEL:
					return validateModel();
				case REGISTER_MODEL:
					return registerModel();
				case VALIDATE_TECH_STACK:
					return validateTechStack();
				case REGISTER_TECH_STACK:
					return registerTechStack();
				case TECH_STACK_PACKAGE_OPTIONS:
					return techStackOptions();
				case ARCHIVED_APP_LIST:
					return archivedAppList();
				case PROMOTE_MODEL: return promoteModel(true);
				case PROMOTE_TECH_STACK: return promoteTechStack(true);
				case PROMOTE_APP: return promoteApp(true);
				case DEMOTE_MODEL: return promoteModel(false);
				case DEMOTE_TECH_STACK: return promoteTechStack(false);
				case DEMOTE_APP: return promoteApp(false);

				case USER_INFO : return userInfo();
				
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
			}
		} catch (ProcessingException exc) {
			LOGGER.warning("Generate Processing Error");
		}
		return ERROR;
	}

	private String userInfo() {
		try {
			user = new UserDAO().findUserByToken( jsonInput.getToken() );
			
			if ( user != null ) {
				JsonObject modelData = new JsonObject();
				
				modelData.addProperty("name", user.getFullName());
				modelData.addProperty("email", user.getEmail());
				modelData.addProperty("company", user.getCompany());
				modelData.addProperty("usageLevel", user.getUserType().toString());
				
				jsonResult.setResult(gson.toJson(modelData));
				
				return SUCCESS;				
			}
		} catch( FrameworkDAOException exc ) {			
			LOGGER.log(Level.WARNING, "Failure acquiring user info", exc);
		}
		
		jsonResult.setResultCode(JsonResultCode.REQUEST_EXECUTION_ERROR);
		jsonResult.setProcessingMessage(
				String.format(labels.getString("UserInfoFailure")));
		return ERROR;

	}
	
	private String promoteTechStack( boolean toPublic ) {
		try {
			FrameworkPackageDAO dao = new FrameworkPackageDAO();
			FrameworkPackage pkg = dao
					.findByNameorId(jsonInput.getTechStackPackageId());

			if ( belongsToUser(pkg) ) {
				pkg.setScopeType( toPublic ? ScopeType.PUBLIC : ScopeType.PRIVATE );
				
				if ( jsonInput.getCost() != null )
					pkg.setCost( jsonInput.getCost() );
				
				dao.save(pkg);
				jsonResult.setResultCode(JsonResultCode.SUCCESS);
				jsonResult.setProcessingMessage(
						String.format(labels.getString(toPublic ? PROMOTE_SUCCESS : DEMOTE_SUCCESS), "technology stack"));

				return SUCCESS;
			}
		} catch (Exception exc) {
			// no_op
		}

		jsonResult.setResultCode(JsonResultCode.REQUEST_EXECUTION_ERROR);
		jsonResult.setProcessingMessage(
				String.format(labels.getString(toPublic ? PROMOTE_FAILURE : DEMOTE_FAILURE), "technology stack", 
						jsonInput.getTechStackPackageId()));

		return ERROR;

	}

	private String promoteModel( boolean toPublic ) {
		try {
			LocalModelDAO dao = new LocalModelDAO();
			LocalModel model = dao
					.findLocalModel(jsonInput.getModelId());

			if (belongsToUser( model)) {
				model.setScopeType( toPublic ? ScopeType.PUBLIC : ScopeType.PRIVATE );

				if ( jsonInput.getCost() != null )
					model.setCost( jsonInput.getCost() );

				dao.save(model);
				jsonResult.setResultCode(JsonResultCode.SUCCESS);
				jsonResult.setProcessingMessage(
						String.format(labels.getString(toPublic ? PROMOTE_SUCCESS : DEMOTE_SUCCESS), "model"));

				return SUCCESS;
			}		
			} catch (Exception exc) {
			// no_op
		}

		jsonResult.setResultCode(JsonResultCode.REQUEST_EXECUTION_ERROR);
		jsonResult.setProcessingMessage(
				String.format(labels.getString(toPublic ? PROMOTE_FAILURE : DEMOTE_FAILURE),
						"model",
						jsonInput.getModelId()));

		return ERROR;

	}

	private String promoteApp( boolean toPublic ) {
		try {
			GeneratedAppDetailsDAO dao = new GeneratedAppDetailsDAO();
			GeneratedAppDetails app = dao
					.findGeneratedAppDetails(jsonInput.getGeneratedAppId());

			if (belongsToUser(app)) {
				app.setScopeType(toPublic ? ScopeType.PUBLIC : ScopeType.PRIVATE);
				
				if ( jsonInput.getCost() != null )
					app.setCost( jsonInput.getCost() );
				
				dao.save(app);
				jsonResult.setResultCode(JsonResultCode.SUCCESS);
				jsonResult.setProcessingMessage(
						String.format(labels.getString(toPublic ? PROMOTE_SUCCESS : DEMOTE_SUCCESS), "application"));

				return SUCCESS;
			}
		} catch (Exception exc) {
			// no_op
		}

		jsonResult.setResultCode(JsonResultCode.REQUEST_EXECUTION_ERROR);
		jsonResult.setProcessingMessage(
				String.format(labels.getString(toPublic ? PROMOTE_FAILURE : DEMOTE_FAILURE),
						"application",
						jsonInput.getGeneratedAppId()));

		return ERROR;

	}

	private String deleteTechStack() {
		try {
			FrameworkPackageDAO dao = new FrameworkPackageDAO();
			FrameworkPackage pkg = dao
					.findByNameorId(jsonInput.getTechStackPackageId());

			if (pkg.getScopeType() == ScopeType.PRIVATE 
					&& belongsToUser(pkg)) {
				dao.delete(pkg.getId());
				AWSHelper.self().deleteS3BucketFile(pkg.getFilePath());
				jsonResult.setResultCode(JsonResultCode.SUCCESS);
				jsonResult.setProcessingMessage(
						labels.getString("DeleteTechStackSuccess"));

				return SUCCESS;
			}else {
				jsonResult.setResultCode(JsonResultCode.SUCCESS);
				jsonResult.setProcessingMessage(
						labels.getString("DeleteTechStackFailure"));					
			}

		} catch (Exception exc) {
			// no_op
		}

		jsonResult.setResultCode(JsonResultCode.REQUEST_EXECUTION_ERROR);
		jsonResult.setProcessingMessage(
				String.format(labels.getString("TechStackDeletionError"),
						jsonInput.getTechStackPackageId()));

		return ERROR;

	}

	private String deleteModel() {
		try {
			LocalModelDAO dao = new LocalModelDAO();
			LocalModel model = dao.findLocalModel(jsonInput.getModelId());

			// if requested by the owner and not public
			if (model.getScopeType() == ScopeType.PRIVATE 
					&& belongsToUser(model)) {
				dao.delete(jsonInput.getModelId());
				jsonResult.setResultCode(JsonResultCode.SUCCESS);
				jsonResult.setProcessingMessage(
						labels.getString("DeleteModelSuccess"));

				return SUCCESS;
			}
			else {
					jsonResult.setResultCode(JsonResultCode.SUCCESS);
					jsonResult.setProcessingMessage(
							labels.getString("DeleteModelFailure"));					
			}

		} catch (Exception exc) {
			// no_op
		}

		jsonResult.setResultCode(JsonResultCode.REQUEST_EXECUTION_ERROR);
		jsonResult.setProcessingMessage(String.format(
				labels.getString("ModelReadError"), jsonInput.getModelId()));

		return ERROR;

	}

	private String getTechStack() {
		try {
			FrameworkPackage pkg = new FrameworkPackageDAO()
					.findByNameorId(jsonInput.getTechStackPackageId());

			if (pkg.getScopeType() == ScopeType.PUBLIC 
					|| belongsToUser(pkg)) {
				jsonResult.setResultCode(JsonResultCode.SUCCESS);
				jsonResult.setResult(gson.toJson(pkg));
				return SUCCESS;
			}
		} catch (Exception exc) {
			// no_op
		}

		jsonResult.setResultCode(JsonResultCode.REQUEST_EXECUTION_ERROR);
		jsonResult.setProcessingMessage(
				String.format(labels.getString("TechStackReadError"),
						jsonInput.getTechStackPackageId()));

		return ERROR;

	}

	private String getModel() {
		try {
			LocalModel model = new LocalModelDAO()
					.findLocalModel(jsonInput.getModelId());

			if (model.getScopeType() == ScopeType.PUBLIC 
					|| belongsToUser(model)) {
				jsonResult.setResultCode(JsonResultCode.SUCCESS);
				jsonResult.setResult(gson.toJson(model));
				return SUCCESS;
			}
		} catch (Exception exc) {
			// no_op
		}

		jsonResult.setResultCode(JsonResultCode.REQUEST_EXECUTION_ERROR);
		jsonResult.setProcessingMessage(String.format(
				labels.getString("ModelReadError"), jsonInput.getModelId()));

		return ERROR;

	}

	private String getApp() {
		String msg = "";
		try {
			GeneratedAppDetails appDetails = new GeneratedAppDetailsDAO()
					.findGeneratedAppDetails(jsonInput.getGeneratedAppId());
			jsonResult.setResultCode(JsonResultCode.SUCCESS);
			jsonResult.setResult(gson.toJson(appDetails));
			return SUCCESS;
		} catch (Exception exc) {
			msg = exc.getMessage();
		}
		jsonResult.setResultCode(JsonResultCode.REQUEST_EXECUTION_ERROR);
		jsonResult.setProcessingMessage(
				String.format(labels.getString("RequestExecutionError"),
						jsonInput.getServiceRequestType(), msg));

		return ERROR;

	}

	private String registerTechStack() {

		String msg = "";
		try {
			if (validateTechStack() == SUCCESS) {
				FrameworkPackage pkg = AppGenHelper.self()
						.createFrameworkPackage(jsonInput.getS3FileLocation(), user.getId());
				// helper method defaults to PUBLIC scope, so force to private if needed.
				if ( pkg != null && jsonInput.getScopeType() == ScopeType.PUBLIC) {
					
					pkg.setScopeType( jsonInput.getScopeType() );
					pkg.setCost(jsonInput.getCost());
					new FrameworkPackageDAO().save(pkg);
					
					jsonResult.setResultCode(JsonResultCode.SUCCESS);
					jsonResult.setProcessingMessage(
							labels.getString("TechStackRegistrationSuccess"));
					jsonResult.setResult(pkg.getId().toString()); // return the pkg id only
					return SUCCESS;
				}
			}
		} catch (Exception exc) {
			msg = exc.getMessage();
		}
		jsonResult.setResultCode(JsonResultCode.REQUEST_EXECUTION_ERROR);
		jsonResult.setProcessingMessage(
				(String.format(labels.getString("RequestExecutionError"),
						jsonInput.getServiceRequestType(), msg)));

		return ERROR;
	}

	private String validateTechStack() {
		String msg = "";
		try {
			if (new FrameworkPackageValidator(
					getTheSession().getServletContext().getRealPath("/"))
							.validate(jsonInput.getS3FileLocation())) {
				jsonResult.setResultCode(JsonResultCode.SUCCESS);
				return SUCCESS;
			} else {
				jsonResult
						.setResultCode(JsonResultCode.REQUEST_EXECUTION_ERROR);
			}
		} catch (ProcessingException exc) {
			msg = exc.getMessage();
		}
		jsonResult.setResultCode(JsonResultCode.REQUEST_EXECUTION_ERROR);
		jsonResult.setProcessingMessage(
				"Failed to validate the tech stack. The following exception wass occured: "
						+ msg);
		return ERROR;
	}

	private String registerModel() {
		// validate then save
		if (validateModel() == SUCCESS) {
			LocalModel localModel = new LocalModel();
			localModel.setName(jsonInput.getSaveParams().getName());
			localModel.setDescription(jsonInput.getSaveParams().getDescription());
			localModel.setOwnerId(user.getId());
			localModel.setScopeType(jsonInput.getScopeType());
			localModel.setFilePath(AWSHelper.self()
					.stripAWSUrlParts(jsonInput.getS3FileLocation()));
			localModel.setCost(jsonInput.getCost());
			try {
				localModel = new LocalModelDAO().create(localModel);
				jsonResult.setResultCode(JsonResultCode.SUCCESS);
				jsonResult.setResult(localModel.getId().toString());
				jsonResult.setProcessingMessage(
						labels.getString("ModelRegistrationSuccess"));
				return SUCCESS;
			} catch (FrameworkDAOException exc) {
				// no_op
			}
		}
		jsonResult.setResultCode(JsonResultCode.REQUEST_EXECUTION_ERROR);
		jsonResult.setProcessingMessage(
				labels.getString("ModelRegistratonFailure"));
		return ERROR;
	}

	private String validateModel() {
		// the model file is in an S3 bucket
		String msg = "";
		try {
			if (this.loadModelHelper(handleLoadingModelIntoFile(
					jsonInput.getS3FileLocation()))) {
				jsonResult.setResultCode(JsonResultCode.SUCCESS);
				jsonResult.setProcessingMessage(
						labels.getString("ModelValidationSucess"));
				return SUCCESS;
			}
		} catch (Exception exc) {
			msg = exc.getMessage();
		}
		jsonResult.setResultCode(JsonResultCode.REQUEST_EXECUTION_ERROR);
		jsonResult.setProcessingMessage(
				labels.getString("ModelValidationFailure") + " : " + msg);
		return ERROR;
	}

	private String validateToken() {
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
				JsonObject propValue = new JsonObject();
				propValue.addProperty("token",
						user.getInternalIdentifier());
				jsonResult.setResultCode(JsonResultCode.SUCCESS);
				jsonResult.setResult(gson.toJson(propValue));
				jsonResult.setProcessingMessage(labels.getString("Success"));
				return SUCCESS;
			}
		} catch (Exception exc) {
			// no_op
		}

		jsonResult.setResultCode(JsonResultCode.INVALID_TOKEN);
		jsonResult.setProcessingMessage(labels.getString(INVALID_TOKEN));
		return ERROR;
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
	private boolean deduceFrameworkPackage(final String inputPkgId) {
		try {
			if (inputPkgId != null) {
				FrameworkPackage pkg= new FrameworkPackageDAO().findByNameorId(inputPkgId);

				if ( pkg.getScopeType() == ScopeType.PUBLIC
									|| belongsToUser( pkg )) {
					super.setPackageId(pkg.getId());
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

	/**
	 * validates the input for correctness and completeness, then creates the
	 * app by first delegating to the correct model handler then delegate to the
	 * base class to create the app based on the correctly provided tech stack
	 * package reference (id, name, or shortName)
	 * 
	 * @return
	 */
	private String createApp() throws ProcessingException {
		///////////////////////////////////////////////////
		// Handle validating and inferring the
		// tech stack (framework package_
		///////////////////////////////////////////////////

		// assigns the tech stack package internally within base class
		if (!deduceFrameworkPackage(jsonInput.getTechStackPackageId())
				|| jsonInput.getModelId() == null) {
			jsonResult.setProcessingMessage(String.format(
			labels.getString("UnknownTechStackPackage"), jsonInput.getTechStackPackageId() ));

			return ERROR;
		}

		// need to load the model into memory
		String modelFileFullPath;
		try {
			LocalModel localModel = new LocalModelDAO()
					.findLocalModel(jsonInput.getModelId());
			modelFileFullPath = AppGenHelper
					.remedyFileLocation(AWSHelper.self().getS3BucketLocation()
							+ localModel.getFilePath());
		} catch (FrameworkDAOException exc) {
			jsonResult.setResultCode(JsonResultCode.REQUEST_EXECUTION_ERROR);
			jsonResult.setProcessingMessage(String.format(
					labels.getString("ModelLoadError"), exc.getMessage()));
			return ERROR;
		}

		///////////////////////////////////////////////////
		// Handle loading the model
		///////////////////////////////////////////////////
		applyUniqueThreadName(false /* append model */ );
		boolean status = loadModelHelper(modelFileFullPath);

		if (!status)
			return ERROR;

		///////////////////////////////////////////////////
		// Set up options
		///////////////////////////////////////////////////
		setupOptions();

		///////////////////////////////////////////////////
		// Generate the application
		///////////////////////////////////////////////////
		try {
			if (internalGenerateApp()) {
				jsonResult.setResultCode(JsonResultCode.SUCCESS);
				jsonResult.setProcessingMessage(
						labels.getString("CreateSuccess"));
				return SUCCESS;
			}
		} catch (Exception exc) {
			// no_op
		}

		jsonResult.setResultCode(JsonResultCode.REQUEST_EXECUTION_ERROR);
		jsonResult.setResult(String.format(
				labels.getString("AppCreationFailure"),
				jsonInput.getTechStackPackageId(), jsonInput.getModelId()));
		return ERROR;

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
	 * delete the archived application using the accesskey which is the
	 * subdirectory within the /arc hive directory  can only delete if private
	 * 
	 * @return
	 */
	private String deleteApp() {
		// validate the accessKey
		Long generatedAppId = this.jsonInput.getGeneratedAppId();
		String msg = "Unable to delete the application archive referenced by "
				+ generatedAppId + ". ";
		try {
			GeneratedAppDetailsDAO dao = new GeneratedAppDetailsDAO();
			GeneratedAppDetails appDetails = dao
					.findGeneratedAppDetails(generatedAppId);
			if (appDetails != null ) {
				if ( appDetails.getScopeType() == ScopeType.PRIVATE 
						&& belongsToUser(appDetails)) {
					AWSHelper.self().deleteS3BucketFile(appDetails.getFilePath());
					dao.delete(generatedAppId);
	
					jsonResult.setResultCode(JsonResultCode.SUCCESS);
					jsonResult.setProcessingMessage(
							labels.getString("DeleteAppSuccess"));
	
					return SUCCESS;
				}
				else {
					jsonResult.setResultCode(JsonResultCode.SUCCESS);
					jsonResult.setProcessingMessage(
							labels.getString("DeleteAppFailure"));					
				}
			}
		} catch (Exception exc) {
			msg += "[Exception : " + exc.getMessage() + "]";
		}

		jsonResult.setResultCode(JsonResultCode.REQUEST_EXECUTION_ERROR);
		jsonResult.setProcessingMessage(msg);

		return ERROR;
	}

	/**
	 * return the list of technology stack packages
	 * 
	 * @param scope
	 * @return
	 */
	private String techStackPackageList() {		
		final JsonArray techStackArray 	= new JsonArray();
		UserDAO userDAO					= new UserDAO();
		JsonObject techStackData 		= null;

		try {
			List<FrameworkPackage> packages	= determineWhichFrameworkPackageList();

			for( FrameworkPackage pkg : packages ) {
				techStackData = new JsonObject();
				techStackData.addProperty("id", pkg.getId());
				techStackData.addProperty(SAVE_PARAMS, gson.toJson(pkg.getSaveParams()));
				techStackData.addProperty("version", pkg.getVersion());
				techStackData.addProperty("type", pkg.getAppType());
				techStackData.addProperty("status", pkg.getReleaseStatus());
				techStackData.addProperty(CONTRIBUTOR, userDAO.findUser(pkg.getOwnerId()).getEmail());
				techStackData.addProperty("cost", pkg.getCost());
				techStackData.addProperty("scope", pkg.getScopeType().toString());
				techStackData.addProperty("iconUrl", pkg.getIconUrl());
				techStackData.addProperty("infoPageUrl", pkg.getInfoPageUrl());
				techStackData.addProperty("shortName", pkg.getShortName());
				techStackArray.add( techStackData );				
			}
		} catch (FrameworkDAOException exc) {
			final String msg = exc.getMessage();
			LOGGER.log(Level.WARNING,
					"GoFrameworkAPIAction.techStackPageList()", exc);
			jsonResult.setResultCode(JsonResultCode.REQUEST_EXECUTION_ERROR);
			jsonResult.setProcessingMessage(msg);
			return ERROR;
		}

		jsonResult.setResult(gson.toJson(techStackArray));
		jsonResult.setResultCode(JsonResultCode.SUCCESS);
		return SUCCESS;

	}

	/**
	 * return the list of models
	 * 
	 * @return
	 */
	private String modelList() {
		JsonArray modelArray 	= new JsonArray();
		UserDAO userDAO			= new UserDAO();
		JsonObject modelData 	= null;
		
		try {
			List<LocalModel> models	= determineWhichModelList();

			for( LocalModel model : models ) {
				modelData = new JsonObject();
				modelData.addProperty("id", model.getId());
				modelData.addProperty("modelType", model.getModelType().toString());
				modelData.addProperty("scopeType", model.getScopeType().toString());
				modelData.addProperty("dateTime", model.getDateTime());
				modelData.addProperty("filePath", model.getFilePath() );
				modelData.addProperty(SAVE_PARAMS, gson.toJson(model.getSaveParams()));
				modelData.addProperty(CONTRIBUTOR, userDAO.findUser(model.getOwnerId()).getEmail());

				modelArray.add( modelData );
			}
			jsonResult.setResult(gson.toJson(modelArray));
			return SUCCESS;
		} catch (FrameworkDAOException exc) {
			final String msg = exc.getMessage();
			LOGGER.log(Level.WARNING, "Failed to retrieve models", exc);
			jsonResult.setResultCode(JsonResultCode.REQUEST_EXECUTION_ERROR);
			jsonResult.setProcessingMessage(msg);
			return ERROR;

		}
	}

	/**
	 * return the list of archived generated apps
	 * 
	 * @return
	 */
	private String archivedAppList() {
		JsonArray appArray				= new JsonArray();
		UserDAO userDAO					= new UserDAO();
		JsonObject appData 				= null;

		try {		
			List<GeneratedAppDetails> apps 	= determineWhichAppList();

			for ( GeneratedAppDetails app : apps ) {
				appData = new JsonObject();
				appData.addProperty("id", app.getId());
				appData.addProperty(SAVE_PARAMS, gson.toJson(app.getSaveParams()));
				appData.addProperty("scopeType", app.getScopeType().toString());
				appData.addProperty("dateTime", app.getDateTime());
				appData.addProperty(CONTRIBUTOR, userDAO.findUser(app.getOwnerId()).getEmail());
				appData.addProperty("filePath", app.getFilePath() );
				appArray.add( appData );
			}

			jsonResult.setResult(gson.toJson(appArray));
			return SUCCESS;

		} catch (FrameworkDAOException exc) {
			final String msg = exc.getMessage();
			LOGGER.log(Level.WARNING, "GoFrameworkAPIAction.archivedAppList()",
					exc);
			jsonResult.setResultCode(JsonResultCode.REQUEST_EXECUTION_ERROR);
			jsonResult.setProcessingMessage(msg);
			return ERROR;
		}
	}


	/**
	 * return the options for the provided tech stack package
	 * 
	 * @return
	 */
	private String techStackOptions() {
		FrameworkPackage pkg = getFrameworkPackageHelper();
		String msg = "";

		if (pkg == null) {
			jsonResult.setResultCode(JsonResultCode.BAD_INPUT_VALUE);
			jsonResult
					.setResult(String.format(labels.getString(BAD_INPUT_VALUE),
							jsonInput.getTechStackPackageId()));
			return ERROR;
		}

		try {

			String xmlOptions = AppGenHelper.getPackageOptions(pkg.getId());

			// need to remove certain aspects of the options XML DOM, to
			// leave only the options name and value
			DocumentBuilderFactory dbfactory = DocumentBuilderFactory
					.newInstance();
			dbfactory.setNamespaceAware(true);

			DocumentBuilder parser = dbfactory.newDocumentBuilder();
			Document document = parser
					.parse(new InputSource(new StringReader(xmlOptions)));

			JsonObject optionsValue = new JsonObject();
			JsonArray jsonOptions;
			NodeList optionsNodeList = document.getElementsByTagName("options");
			Node optionsNode;

			for (int i = 0; i < optionsNodeList.getLength(); i++) {
				jsonOptions = new JsonArray();

				optionsNode = optionsNodeList.item(i);

				NodeList optionNodeList = optionsNode.getChildNodes();
				NamedNodeMap namedNodeMap;

				for (int index = 0; index < optionNodeList
						.getLength(); index++) {
					namedNodeMap = optionNodeList.item(index).getAttributes();

					JsonObject op = createOptions(namedNodeMap);
					if (op != null)
						jsonOptions.add(op);
				}
				optionsValue.add(optionsNode.getAttributes()
						.getNamedItem("name").getNodeValue(), jsonOptions);
			}

			jsonResult.setResult(gson.toJson(optionsValue));
			return SUCCESS;
		} catch (Exception exc) {
			LOGGER.log(Level.WARNING, "GoFrameworkAPIAction.techStackOptions()",
					exc);
			msg = exc.getMessage();
		}
		jsonResult.setResultCode(JsonResultCode.REQUEST_EXECUTION_ERROR);
		jsonResult.setProcessingMessage(msg);
		return ERROR;
	}

	private JsonObject createOptions(NamedNodeMap namedNodeMap) {
		JsonObject propValue = null;
		Node nameNode;
		Node valueNode;
		Node requiredNode;

		if (namedNodeMap != null) {
			propValue = new JsonObject();
			nameNode = namedNodeMap.getNamedItem("name");
			if (nameNode != null) {
				propValue.addProperty("name", nameNode.getNodeValue());

				valueNode = namedNodeMap.getNamedItem("value");
				propValue.addProperty("value",
						valueNode != null ? valueNode.getNodeValue() : "");

				// default required to false if not assigned
				requiredNode = namedNodeMap.getNamedItem("required");
				propValue.addProperty("required",
						requiredNode != null ? requiredNode.getNodeValue()
								: "false");
			}
		}
		return (propValue);
	}

	private void setupOptions() {
		assignOptionsInternally(
				AppGenHelper.dissectOptions(jsonInput.getAppOptions()));
	}

	private boolean internalGenerateApp() {

		setGitProject(jsonInput.getUseGit());
		setGitParams(jsonInput.getGitParams());
		setOverWrite(false);

		String generateAppStatus = generateApp();

		// archive the app as ZIP and copy to the appropriate S3 Bucket key
		if (generateAppStatus != SUCCESS ) {
			jsonResult.setResultCode(JsonResultCode.REQUEST_EXECUTION_ERROR);
			jsonResult.setProcessingMessage( generateAppStatus + " : " + getStatus().getErrMessages() );
			return false;
		}
		
		return true;
	}

	@Override
	protected GeneratedAppDetails persistApp(String zipFileName) {
		// persist it
		GeneratedAppDetails genAppDetails = null;

		try {
			genAppDetails = new GeneratedAppDetails();
			
			// use the name and description provided as part of the call's save params,
			// and if not available, use the application.name/description found in 
			// the package options that are also provided as part of the input
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
			genAppDetails
					.setPackageId(jsonInput.getTechStackPackageId());
			genAppDetails.setModelId(jsonInput.getModelId());
			genAppDetails.setFilePath(
					AWSHelper.self().stripAWSUrlParts(zipFileName));
			new GeneratedAppDetailsDAO().create(genAppDetails);
			addInfoMessage("Successfully persited the bundled generated app");
		} catch (Exception exc) {
			LOGGER.log(Level.WARNING, "Perist app failure", exc);
			addInfoMessage("failed to bundle the bundled generated app - "
					+ exc.getMessage());
		}

		return genAppDetails;
	}

	private String handleLoadingModelIntoFile(String filePathAndName)
			throws ProcessingException {

		if (filePathAndName == null || filePathAndName.isEmpty()) {
			final String msg = "filePathAndName arg is null or empty";
			LOGGER.severe(msg);
			throw new ProcessingException(
					"GoFramweorkAPIAction.handleLoadingModelIntoFile() - "
							+ msg);
		}

		filePathAndName = AppGenHelper.remedyFileLocation(filePathAndName);

		return filePathAndName;
	}

	protected boolean loadModelHelper(String modelFileFullPath)
			throws ProcessingException {
		boolean status = false;

		IModelParser modelParser = ModelParserFactory.getInstance()
				.modelParser(modelFileFullPath);

		if (modelParser != null) {
			
			// need to do a bit more with a PojoHandler since it needs the root java package name
			if ( modelParser instanceof PojoHandler )
				modelParser = new PojoHandler(jsonInput.getJavaRootPackageName() );
			
			status = modelParser.loadModel(modelFileFullPath);
		}

		return status;
	}

	private List<FrameworkPackage> determineWhichFrameworkPackageList() throws FrameworkDAOException {
		return new FrameworkPackageDAO().findAllFrameworkPackage(jsonInput.getScopeType(), 
																	user.getId(),
																	jsonInput.getFilter() );	
	}
	
	private List<LocalModel> determineWhichModelList() throws FrameworkDAOException {
		return new LocalModelDAO().findAllLocalModel(jsonInput.getScopeType(), 
														user.getId(),
														jsonInput.getFilter() );
	}

	private List<GeneratedAppDetails> determineWhichAppList() throws FrameworkDAOException {
		return new GeneratedAppDetailsDAO().findAllGeneratedAppDetails(jsonInput.getScopeType(), user.getId());
	}

	private FrameworkPackage getFrameworkPackageHelper() {

		String inputPkgId = jsonInput.getTechStackPackageId();

		if (inputPkgId == null) {
			jsonResult.setResultCode(JsonResultCode.BAD_INPUT_VALUE);
			jsonResult.setResult(String.format(
					labels.getString(BAD_INPUT_VALUE), "techStackPackageId"));
			return null;
		}

		FrameworkPackage pkg = null;
		try {

			FrameworkPackageDAO dao = new FrameworkPackageDAO();
			pkg = dao.findByNameorId(inputPkgId);

			if (pkg == null) {
				jsonResult.setResultCode(JsonResultCode.BAD_INPUT_VALUE);
				jsonResult.setResult(
						String.format(labels.getString(BAD_INPUT_VALUE),
								"techStackPackageId"));
				return null;
			}
		} catch (FrameworkDAOException exc) {
			final String msg = "Failed to find tech stack package using id "
					+ inputPkgId;
			LOGGER.log(Level.SEVERE, msg, exc);
		}
		return pkg;
	}

	private boolean belongsToUser( BaseTransactionalEntity entity ) {
		return entity.getOwnerId().compareTo(user.getId()) == 0;				
	}
	
	// attributes
	protected String input 						= null;
	protected JsonInput jsonInput 				= null;
	protected JsonResult jsonResult 			= new JsonResult();
	protected Gson gson 						= null;
	protected User user							= null;
	private static final String INVALID_TOKEN 	= "InvalidToken";
	private static final String BAD_INPUT_VALUE = "BadInputValue";
	private static final String DEMOTE_SUCCESS	= "DemoteSuccess";
	private static final String PROMOTE_SUCCESS = "PromoteSuccess";
	private static final String DEMOTE_FAILURE = "DemoteFailure";
	private static final String PROMOTE_FAILURE = "PromoteFailure";
	private static final String SAVE_PARAMS 	= "saveParams";
	private static final String CONTRIBUTOR 	= "contributor";
	private static final Logger LOGGER 			= Logger.getLogger(GoFrameworkAPIAction.class.getName());

}
