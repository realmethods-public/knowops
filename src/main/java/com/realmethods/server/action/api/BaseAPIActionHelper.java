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

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.realmethods.api.JsonInput;
import com.realmethods.api.JsonResult;
import com.realmethods.codetemplate.parser.ModelParserFactory;
import com.realmethods.entity.BaseTransactionalEntity;
import com.realmethods.entity.FrameworkPackage;
import com.realmethods.entity.GeneratedAppDetails;
import com.realmethods.entity.LocalModel;
import com.realmethods.entity.Project;
import com.realmethods.entity.Resource;
import com.realmethods.entity.User;
import com.realmethods.entity.dao.FrameworkPackageDAO;
import com.realmethods.entity.dao.GeneratedAppDetailsDAO;
import com.realmethods.entity.dao.LocalModelDAO;
import com.realmethods.entity.dao.ProjectDAO;
import com.realmethods.entity.dao.ResourceDAO;
import com.realmethods.foundational.common.exception.FrameworkDAOException;
import com.realmethods.foundational.common.exception.ProcessingException;
import com.realmethods.server.service.IModelParser;
import com.realmethods.server.service.PojoHandler;
import com.realmethods.common.helpers.AppGenHelper;

import com.google.gson.*;

/**
 * Provides base functionality to Restful API Action Helpers
 * 
 * @author realMethods, Inc.
 *
 */
public class BaseAPIActionHelper {
	/**
	 * default constructor
	 */
	public BaseAPIActionHelper() {		
	}
	
	/**
	 * constructor
	 * @param input
	 * @param user
	 */
	public BaseAPIActionHelper(JsonInput input, User user ) {
		this.jsonInput = input;
		this.user = user;
	}
	
	/**
	 * Helper method to determine the tech stacks the current use has access to based on the requested
	 * scope and filter.
	 * @return
	 * @throws FrameworkDAOException
	 */
	protected List<FrameworkPackage> determineWhichFrameworkPackageList() throws FrameworkDAOException {
		return new FrameworkPackageDAO().findAllFrameworkPackage(jsonInput.getScopeType(), 
																	user.getId(),
																	jsonInput.getFilter() );	
	}

	/**
	 * Helper method to determine the models the current use has access to based on the requested
	 * scope and filter.
	 * @return
	 * @throws FrameworkDAOException
	 */
	protected List<LocalModel> determineWhichModelList() throws FrameworkDAOException {
		return new LocalModelDAO().findAllLocalModel(jsonInput.getScopeType(), 
														user.getId(),
														jsonInput.getFilter() );
	}

	/**
	 * Helper method to determine the app the current use has access to based on the requested
	 * scope and filter.
	 * @return
	 * @throws FrameworkDAOException
	 */	
	protected List<GeneratedAppDetails> determineWhichAppList() throws FrameworkDAOException {
		return new GeneratedAppDetailsDAO().findAllGeneratedAppDetails(jsonInput.getScopeType(), user.getId());
	}

	/**
	 * Helper method to determine the resources the current use has access to based on the requested
	 * scope and filter.
	 * @return
	 * @throws FrameworkDAOException
	 */	
	protected List<Resource> determineWhichResourceList() throws FrameworkDAOException {
		return new ResourceDAO().findAllResource(jsonInput.getScopeType(), 
														user.getId(),
														jsonInput.getFilter() );
	}
	
	/**
	 * Helper method to determine the projects the current use has access to based on the requested
	 * scope and filter.
	 * @return
	 * @throws FrameworkDAOException
	 */	
	protected List<Project> determineWhichProjectList() throws FrameworkDAOException {
		return new ProjectDAO().findAllProjectByOwner(user.getId());
	}	
	/**
	 * Helper method to turn the model referenced by the filePathAndName arg into
	 * a JSON model on the local file system.  If it is a .git reference, it is
	 * left as is.  
	 * @return
	 * @throws FrameworkDAOException
	 */	
	protected String handleLoadingModelIntoFile(String filePathAndName)
			throws ProcessingException {
		
		if (filePathAndName == null || filePathAndName.isEmpty()) {
			final String msg = "filePathAndName arg is null or empty";
			LOGGER.severe(msg);
			throw new ProcessingException(msg);
		}

		////////////////////////////////////////////
		// Ignore .git URLs
		////////////////////////////////////////////
		if ( !filePathAndName.endsWith(".git") )
			filePathAndName = AppGenHelper.remedyFileLocation(filePathAndName, "model_");

		return filePathAndName;
	}

	/**
	 * Turns the model referenced by the modelFileFullPath into a realMethods
	 * meta-model on the current thread.
	 * 
	 * @param modelFileFullPath
	 * @return
	 * @throws ProcessingException
	 */
	public boolean loadModelHelper(String modelFileFullPath)
			throws ProcessingException {
		boolean status = false;

		/////////////////////////////////////////////////////////////////////
		// Model parser factory determines which model parser is able to 
		// to handle turning a certain model type into a realMethods meta-model 
		/////////////////////////////////////////////////////////////////////
		IModelParser modelParser = ModelParserFactory.getInstance()
				.modelParser(modelFileFullPath);

		if (modelParser != null) {			
			////////////////////////////////////////////////////////////////////////
			// need to do a bit more with a PojoHandler since it needs the root 
			// java package name
			////////////////////////////////////////////////////////////////////////
			if ( modelParser instanceof PojoHandler )
				((PojoHandler)modelParser).setPojoParams(jsonInput.getPojoParams());
			
			////////////////////////////////////////////////////////////////////////
			// the model parser does all the heavy lifting to turn the model file
			// into an internal realMethods meta-model on the current thread
			////////////////////////////////////////////////////////////////////////			
			status = modelParser.loadModel(modelFileFullPath);
			
		}

		return status;
	}

	/**
	 * Search for the model either the name or by id
	 * @return
	 */
	protected LocalModel determineLocalModelHelper() {
		
		final LocalModelDAO dao 		= new LocalModelDAO();
		LocalModel localModel	= null;
		
		try {
			//////////////////////////////////////////////////////////////////
			// first search by name if one is provided
			//////////////////////////////////////////////////////////////////
			if ( jsonInput.getSaveParams().getName() != null )
					localModel = (LocalModel)dao.findByName("LocalModel", jsonInput.getSaveParams().getName());
 
			//////////////////////////////////////////////////////////////////
			// if not found by name, search using the id
			//////////////////////////////////////////////////////////////////
			if ( localModel == null )
				localModel = dao.findLocalModel( jsonInput.getModelId() );
		} catch( Exception exc ) {
			LOGGER.log( Level.WARNING, "Failure during finding a DAO", exc );
		}
		return( localModel );
		
	}

	/**
	 * Search for the resource either the name or by id
	 * @return
	 */
	protected Resource determineResourceHelper() {
		
		ResourceDAO dao 	= new ResourceDAO();
		Resource resource 	= null;
		
		try {			
			//////////////////////////////////////////////////////////////////
			// first search by name if one is provided
			//////////////////////////////////////////////////////////////////
			if ( jsonInput.getSaveParams().getName() != null )
					resource = (Resource)dao.findByName("Resource", jsonInput.getSaveParams().getName());
 
			//////////////////////////////////////////////////////////////////
			// if not found by name, search using the id
			//////////////////////////////////////////////////////////////////
			if ( resource == null )
				resource = dao.findResource( jsonInput.getResourceId() );
			
		} catch( Exception exc ) {
			LOGGER.log( Level.WARNING, "Failure during finding a Resource", exc );
		}
		return( resource );
		
	}

	/**
	 * Search for the apps either the name or by id
	 * @return
	 */
	protected GeneratedAppDetails determineGeneratedAppDetailsHelper() {
		
		GeneratedAppDetailsDAO dao 		= new GeneratedAppDetailsDAO();
		GeneratedAppDetails appDetails	= null;
		
		try {			
			//////////////////////////////////////////////////////////////////
			// first search by name if one is provided
			//////////////////////////////////////////////////////////////////
			if ( jsonInput.getSaveParams().getName() != null )
				appDetails = (GeneratedAppDetails)dao.findByName("GeneratedAppDetails", jsonInput.getSaveParams().getName());
 
			//////////////////////////////////////////////////////////////////
			// if not found by name, search using the id
			//////////////////////////////////////////////////////////////////
			if ( appDetails == null )
				appDetails = dao.findGeneratedAppDetails( jsonInput.getGeneratedAppId() );
		} catch( Exception exc ) {
			LOGGER.log( Level.WARNING, "Failure during finding a generated application", exc );
		}
		return( appDetails );
		
	}
	
	/**
	 * Check if the entity is owned by the invocating user
	 * @param entity
	 * @return
	 */
	protected boolean belongsToUser( BaseTransactionalEntity entity ) {
		return entity.getOwnerId().compareTo(user.getId()) == 0;				
	}

	/**
	 * Locate the tech stack package by name or id
	 * @return
	 */
	protected FrameworkPackage determineFrameworkPackageHelper() {

		final FrameworkPackageDAO dao 		= new FrameworkPackageDAO();
		FrameworkPackage frameworkPkg 	= null;
		
		try {			
			frameworkPkg = dao.findByNameorId( jsonInput.getTechStackPackageId() );
		} catch( Exception exc ) {
			LOGGER.log( Level.WARNING, "Failure during finding a technology stack package", exc );
		}
		return( frameworkPkg );
		
	}
	
	/**
	 * Search for the project either the name or by id
	 * @return
	 */
	protected Project determineProjectHelper() {
		
		ProjectDAO dao 	= new ProjectDAO();
		Project project	= null;
		
		try {			
			//////////////////////////////////////////////////////////////////
			//  search by name if one is provided
			//////////////////////////////////////////////////////////////////
			if ( jsonInput.getSaveParams().getName() != null )
					project = (Project)dao.findByName("Project", jsonInput.getSaveParams().getName());
 
		} catch( Exception exc ) {
			LOGGER.log( Level.WARNING, "Failure during finding a Project", exc );
		}
		return( project );
		
	}

	// attributes

	/////////////////////////////////////////////////////////
	// input parameters of the current API call
	/////////////////////////////////////////////////////////
	protected JsonInput jsonInput 				= null;
	/////////////////////////////////////////////////////////
	// JSON results for invocated call
	/////////////////////////////////////////////////////////	
	protected JsonResult jsonResult 			= new JsonResult();
	/////////////////////////////////////////////////////////
	// the user calling the API
	/////////////////////////////////////////////////////////
	protected User user							= null;
	/////////////////////////////////////////////////////////
	// resource bundle of api messages
	/////////////////////////////////////////////////////////	
	protected static final ResourceBundle labels = ResourceBundle.getBundle("jsonAPIMessages", new Locale("en", "US"));
	/////////////////////////////////////////////////////////
	// Google JSON handler
	/////////////////////////////////////////////////////////
	protected Gson gson = new GsonBuilder().create();
	/////////////////////////////////////////////////////////
	// static declarations
	/////////////////////////////////////////////////////////	
	protected static final String DEMOTE_SUCCESS	= "DemoteSuccess";
	protected static final String PROMOTE_SUCCESS = "PromoteSuccess";
	protected static final String DEMOTE_FAILURE = "DemoteFailure";
	protected static final String PROMOTE_FAILURE = "PromoteFailure";
	protected static final String BAD_INPUT_VALUE = "BadInputValue";
	protected static final String SAVE_PARAMS 	= "saveParams";
	protected static final String CONTRIBUTOR 	= "contributor";
	/////////////////////////////////////////////////////////
	// Logging provider
	/////////////////////////////////////////////////////////		
	private static final Logger LOGGER 	= Logger.getLogger(BaseAPIActionHelper.class.getName());

	// ============================================= 
	// inner class for Sorting a BaseTransactionalEntity instance by name
	// ============================================= 
	public class SortByName implements Comparator<BaseTransactionalEntity> { 
		// ============================================= 
		// Used for sorting in asscending order by name 
		// ============================================= 
		public int compare(BaseTransactionalEntity a, BaseTransactionalEntity b)  { 
			return a.getName().compareTo(b.getName());
		} 
	} 
	
}