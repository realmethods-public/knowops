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

import java.io.StringReader;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.realmethods.api.JsonInput;
import com.realmethods.api.JsonResult;
import com.realmethods.api.JsonResultCode;
import com.realmethods.common.helpers.AWSHelper;
import com.realmethods.common.helpers.AppGenHelper;
import com.realmethods.entity.FrameworkPackage;
import com.realmethods.entity.ScopeType;
import com.realmethods.entity.User;
import com.realmethods.entity.dao.FrameworkPackageDAO;
import com.realmethods.entity.dao.UserDAO;
import com.realmethods.foundational.common.exception.FrameworkDAOException;

import com.google.gson.*;

/**
 * Handles tech stack related API invocations.
 * 
 * @author realMethods, Inc.
 *
 */
public class TechStackAPIActionHelper extends BaseAPIActionHelper {
	
	/**
	 * default constructor
	 */
	public TechStackAPIActionHelper() {		
	}
	
	/**
	 * constructor
	 * @param input
	 * @param user
	 */
	public TechStackAPIActionHelper(JsonInput input, User user ) {
		super( input, user );
	}

	/** 
	 * Returns particular data for the request tech stack
	 * @return
	 */
	public JsonResult getTechStack() {
		try {
			FrameworkPackage pkg = new FrameworkPackageDAO()
					.findByNameorId(jsonInput.getTechStackPackageId());

			////////////////////////////////////////////////////////////////
			// if the package is public or owned by the service invoker
			////////////////////////////////////////////////////////////////
			if ( pkg != null && 
				( pkg.getScopeType() == ScopeType.PUBLIC 
						|| belongsToUser(pkg)) ) {
				jsonResult.success();
				jsonResult.setResult(gson.toJson(pkg));
				return jsonResult;
			}
		} catch (Exception exc) {
			LOGGER.log( Level.WARNING, "Failure during retrieving a tech stack package", exc );
		}

		jsonResult.setResultCode(JsonResultCode.REQUEST_EXECUTION_ERROR);
		jsonResult.setProcessingMessage(
				String.format(labels.getString("TechStackReadError"),
						jsonInput.getTechStackPackageId()));

		return jsonResult;

	}
	
	/**
	 * Register a tech stack package (archive).  The tech stack package is 
	 * first validated. The provided S3 file location
	 * is the URL to the tech stack package.
	 * @return
	 */
	public JsonResult registerTechStack() {

		String msg = "";
		try {
			//////////////////////////////////////////////////////////////
			// validate and create the tech stack package
			//////////////////////////////////////////////////////////////			
			boolean createFlag 		= true; // validate with createion
			FrameworkPackage pkg 	= AppGenHelper.self()
										.validateFrameworkPackage(jsonInput.getS3FileLocation(), user.getId(), createFlag);

			if (pkg != null) {

				////////////////////////////////////////////////////////////////
				// assign the input data to the new instance
				///////////////////////////////////////////////////////////////
				pkg.setScopeType( jsonInput.getScopeType() );
				pkg.setCost(jsonInput.getCost());
				
				////////////////////////////////////////////////////////////////
				// save the technology stack package
				///////////////////////////////////////////////////////////////				
				new FrameworkPackageDAO().save(pkg);
				
				jsonResult.success(labels.getString("TechStackRegistrationSuccess"));
				jsonResult.setResult(pkg.getId().toString()); // return the pkg id only
				return jsonResult;
			}
		} catch (Exception exc) {
			msg = exc.getMessage();
		}
		
		jsonResult.error(
				(String.format(labels.getString("RequestExecutionError"),
						jsonInput.getServiceRequestType(), msg)));

		return jsonResult;
	}

	/**
	 * Validate the tech stack for structure and its content.  Uses a @FrameworkPackageValidor
	 * to do the heavy lifting.
	 * @return
	 */
	public JsonResult validateTechStack() {

		String msg = "";
		try {
			boolean createFlag 		= false;	// validate without creation
			FrameworkPackage pkg 	= AppGenHelper.self()
										.validateFrameworkPackage(jsonInput.getS3FileLocation(), user.getId(), createFlag);
			
			if (pkg != null) {
				jsonResult.success();
				return jsonResult;
			} else {
				jsonResult
						.setResultCode(JsonResultCode.REQUEST_EXECUTION_ERROR);
			}
		} catch (Exception exc) {
			msg = exc.getMessage();
		}
		jsonResult.setResultCode(JsonResultCode.REQUEST_EXECUTION_ERROR);
		jsonResult.setProcessingMessage(
				"Failed to validate the tech stack. The following exception wass occured: "
						+ msg);
						
		return jsonResult;
	}

	/**
	 * delete the corresponding tech stack. Can only delete privately owned tech stacks.
	 * @return
	 */
	public JsonResult deleteTechStack() {
		try {
			FrameworkPackageDAO dao = new FrameworkPackageDAO();
			FrameworkPackage pkg 	= dao.findByNameorId(jsonInput.getTechStackPackageId());
			
			//////////////////////////////////////////////////////////////
			// scope is PRIVATE and the user owns this tech stack package
			//////////////////////////////////////////////////////////////
			if (pkg.getScopeType() == ScopeType.PRIVATE
					&& belongsToUser(pkg)) {
				//////////////////////////////////////////////////////////////
				// delete from the persistence store
				//////////////////////////////////////////////////////////////
				dao.delete(pkg.getId());
				
				//////////////////////////////////////////////////////////////
				// remove it from the AWS S3 Bucket
				//////////////////////////////////////////////////////////////
				AWSHelper.self().deleteS3BucketFile(pkg.getFilePath());
				jsonResult.success();
				jsonResult.setProcessingMessage(
						labels.getString("DeleteTechStackSuccess"));

				return jsonResult;
			}else {
				jsonResult.error();
				jsonResult.setProcessingMessage(
						labels.getString("DeleteTechStackFailure"));					
			}

		} catch (Exception exc) {
			LOGGER.log( Level.WARNING, "Failure during deleting a tech stack package", exc );
		}

		jsonResult.setResultCode(JsonResultCode.REQUEST_EXECUTION_ERROR);
		jsonResult.setProcessingMessage(
				String.format(labels.getString("TechStackDeletionError"),
						jsonInput.getTechStackPackageId()));

		return jsonResult;

	}
	
	/**
	 * promote or demote the referrenced tech stack
	 * @param toPublic
	 * @return
	 */
	public JsonResult promoteTechStack( boolean toPublic ) {
		try {
			FrameworkPackageDAO dao = new FrameworkPackageDAO();
			FrameworkPackage pkg = dao
					.findByNameorId(jsonInput.getTechStackPackageId());

			if ( belongsToUser(pkg) ) {
				pkg.setScopeType( toPublic ? ScopeType.PUBLIC : ScopeType.PRIVATE );
				
				//////////////////////////////////////////////////////////////
				// cost may eventually be real data provided externally
				//////////////////////////////////////////////////////////////
				if ( jsonInput.getCost() != null )
					pkg.setCost( jsonInput.getCost() );
				
				dao.save(pkg);
				jsonResult.success();
				jsonResult.setProcessingMessage(
						String.format(labels.getString(toPublic ? PROMOTE_SUCCESS : DEMOTE_SUCCESS), "technology stack"));

				return jsonResult;
			}
		} catch (Exception exc) {
			// no_op
		}

		jsonResult.setResultCode(JsonResultCode.REQUEST_EXECUTION_ERROR);
		jsonResult.setProcessingMessage(
				String.format(labels.getString(toPublic ? PROMOTE_FAILURE : DEMOTE_FAILURE), "technology stack", 
						jsonInput.getTechStackPackageId()));

		return jsonResult;

	}

	/**
	 * return the list of technology stack packages
	 * 
	 * @param scope
	 * @return
	 */
	public JsonResult techStackPackageList() {		
		final JsonArray techStackArray 	= new JsonArray();
		final UserDAO userDAO			= new UserDAO();
		JsonObject techStackData		= null;
		
		try {
			/////////////////////////////////////////////////////////
			// depending on the tech stack package scope, ownership
			// and what the invocator is looking for
			/////////////////////////////////////////////////////////
			List<FrameworkPackage> packages	= determineWhichFrameworkPackageList();

			/////////////////////////////////////////////////////////////////
			// if there are none, attempt to reload from the remote repo
			/////////////////////////////////////////////////////////////////
			if ( packages == null || packages.isEmpty() ) {
				if ( AppGenHelper.self().loadRemoteFrameworkPackages() == true ) {
					/////////////////////////////////////////////////////////
					// now try again to load the processed remote packages
					/////////////////////////////////////////////////////////
					packages = new FrameworkPackageDAO().findAllFrameworkPackage();
				}
				else {
					// do nothing since an empty package list is a valid option
				}
					

			}

			////////////////////////////////////////////////////////////////
			//  sort the packages by the short name
			////////////////////////////////////////////////////////////////
			Collections.sort(packages, new SortByName()); 

			for( FrameworkPackage pkg : packages ){
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
				techStackData.addProperty("localZipFilePath", pkg.getLocalZipFilePath());
				techStackArray.add( techStackData );				
			}
		} catch (FrameworkDAOException exc) {
			final String msg = exc.getMessage();
			LOGGER.log(Level.WARNING,
					"RealMethodsAPIAction.techStackPageList()", exc);
			jsonResult.setResultCode(JsonResultCode.REQUEST_EXECUTION_ERROR);
			jsonResult.setProcessingMessage(msg);
			return jsonResult;
		}

		/////////////////////////////////////////////////////////
		// if here, things went well so turn the tech stack array
		// into a Json object and assign as the results
		/////////////////////////////////////////////////////////
		jsonResult.success();
		jsonResult.setResult(gson.toJson(techStackArray));
		return jsonResult;

	}

	/**
	 * return the options for the provided tech stack package
	 * 
	 * @return
	 */
	public JsonResult techStackOptions() {
		FrameworkPackage pkg = determineFrameworkPackageHelper();
		String msg = "";

		if (pkg == null) {
			jsonResult.setResultCode(JsonResultCode.BAD_INPUT_VALUE);
			jsonResult
					.setResult(String.format(labels.getString(BAD_INPUT_VALUE),
							jsonInput.getTechStackPackageId()));
			return jsonResult;
		}

		try {

			StringBuilder content 	= new StringBuilder( "<" + AppGenHelper.PKG_OPTIONS_NODE_NAME + ">");
			content.append( AppGenHelper.getPackageOptions(pkg) );
			content.append( "</" + AppGenHelper.PKG_OPTIONS_NODE_NAME + ">" );

			String xmlOptions = content.toString();

			////////////////////////////////////////////////////////////////////////////
			// need to remove certain aspects of the options XML DOM, to
			// leave only the options name and value
			////////////////////////////////////////////////////////////////////////////
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

			jsonResult.success();
			jsonResult.setResult(gson.toJson(optionsValue));
			return jsonResult;
		} catch (Exception exc) {
			LOGGER.log(Level.WARNING, "RealMethodsAPIAction.techStackOptions()",
					exc);
			msg = exc.getMessage();
		}
		jsonResult.setResultCode(JsonResultCode.REQUEST_EXECUTION_ERROR);
		jsonResult.setProcessingMessage(msg);
		return jsonResult;
	}

	/**
	 * Internal helper method that maps a tech stack package's options to a JsonObject
	 * 
	 * @param namedNodeMap
	 * @return
	 */
	protected JsonObject createOptions(NamedNodeMap namedNodeMap) {
		JsonObject propValue = null;
		Node nameNode;
		Node valueNode;

		if (namedNodeMap != null) {
			propValue = new JsonObject();
			nameNode = namedNodeMap.getNamedItem("name");
			if (nameNode != null) {
				propValue.addProperty("name", nameNode.getNodeValue());

				valueNode = namedNodeMap.getNamedItem("value");
				propValue.addProperty("value",
						valueNode != null ? valueNode.getNodeValue() : "");
			}
		}
		return (propValue);
	}

	// attributes
	private static final Logger LOGGER 	= Logger.getLogger(TechStackAPIActionHelper.class.getName());
	
}