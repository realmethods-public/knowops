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
package com.realmethods.server.action;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.realmethods.api.GitParams;
import com.realmethods.common.helpers.AppGenHelper;
import com.realmethods.entity.Build;
import com.realmethods.entity.Project;
import com.realmethods.entity.User;
import com.realmethods.entity.dao.ProjectDAO;
import com.realmethods.foundational.common.exception.FrameworkDAOException;
import com.realmethods.foundational.common.exception.ProcessingException;
import com.realmethods.server.action.api.BaseAPIActionHelper;

/**
 * Action handler for all Project actions.
 * 
 * @author realMethods, Inc.
 *
 */
public class ProjectHandlerAction extends FileHandlerAction {
	/**
	 * default constructor
	 */
	public ProjectHandlerAction() {
		// no_op
	}

	/** 
	 * Save the inputs as a Project YAML file
	 * 
	 * @return
	 */
	public String saveProject() {
		try {
			User user 		= getUser();
			ProjectDAO dao 	= new ProjectDAO();

			// ===============================================
			// if no projectId provided by caller, create
			// ===============================================
			if ( projectId == null ) {
				project = new Project();			
				project.setName( name );
				project.setDescription( description );
				project.setOwnerId( user.getId() );
			} 
			// ===============================================
			// otherwise a providedId indicates a save 
			// ===============================================
			else {
				project = dao.findProject( projectId );
			}

			
			project.setTechStackPackageName( techStackPackageName );
			project.setModelId( modelId );
			project.setOptions( options );
			project.setGitParams( gitParams );

			if ( projectId == null ) 
				project.setId( dao.create( project ) );
			else
				dao.save( project );
		} catch( FrameworkDAOException exc ) {
			LOGGER.log(Level.SEVERE, "Saving project failed", exc);
		}
		return SUCCESS;
	}
	
	public String findProject() throws FrameworkDAOException {
		project = new ProjectDAO().findProject(projectId);
		return SUCCESS;
	}
	
	public String deleteProject() {
		try {
			new ProjectDAO().delete(projectId);
		} catch( FrameworkDAOException exc ) {
			addErrorMessage( exc.getMessage() );
		}
		return SUCCESS;
	}

	public String loadAllProjects() {
		// get projects will do the work
		return SUCCESS;		
	}

	public String loadAllProjectsForUser() {
		// get projects will do the work
		return SUCCESS;		
	}

	public Project getProject() throws FrameworkDAOException {
		return project;
	}

	/**
	 * returns all projects for any user
	 * @return
	 * @throws FrameworkDAOException
	 */
	public List<Project> getProjects() throws FrameworkDAOException {
		return new ProjectDAO().findAllProject();
	}

	/**
	 * returns all builds for the associated project
	 * @return
	 * @throws FrameworkDAOException
	 */
	public String loadBuilds() throws FrameworkDAOException {
		try {
			builds = new ProjectDAO().findProject( projectId ).getBuilds();
		} catch( Exception exc ) {
			LOGGER.log( Level.SEVERE, "Error finding builds for project" , exc );
			return ERROR;
		}
		return SUCCESS;
	
	}

	public List<Build> getBuilds() {
		return builds;
	}

	/**
	 * returns all projects for current user
	 * @return
	 * @throws FrameworkDAOException
	 */
	public List<Project> getProjectsForUser() throws FrameworkDAOException {
		List<Project> projects = new ProjectDAO().findAllProjectByOwner(getUser().getId());

		////////////////////////////////////////////////////////////////
		//  sort the projects by the name
		////////////////////////////////////////////////////////////////
		BaseAPIActionHelper base = new BaseAPIActionHelper();
		Collections.sort(projects, base.new SortByName()); 

		return projects;
	}

	/**
	 * Assign the name parameter
	 * @param name
	 */
	public void setName( String name ) {
		this.name = name;
	}
	
	/**
	 * Assign the description parameter
	 * @param description
	 */
	public void setDescription( String description ) {
		this.description = description;
	}

	/**
	 * Assigns the projectId field the provided argument.
	 * 
	 * @param id
	 */
	public void setProjectId( Long id ) {
		projectId = id;
	}

	/**
	 * Assigns the techStackPackageName field the provided argument.
	 * 
	 * @param name
	 */
	public void setTechStackPackageName( String name ) {
		techStackPackageName = name;
	}

	/**
	 * Assigns the gitParms field the provided argument.
	 * 
	 * @param params
	 */
	public void setGitParams( GitParams params ) {
		gitParams = params;
	}

	/**
	 * Assigns the modelId field the provided argument.
	 * 
	 * @param id
	 */
	public void setModelId( Long id ) {
		modelId = id;
	}

	/**
	 * Assign the application options of the project
	 * 
	 * @param inputOptions
	 */
	public void setOptions(String inputOptions) {
		options = AppGenHelper.dissectOptions(inputOptions);
		
		final String msg = "input options dissected to " + options;		
		LOGGER.info(msg);
	}
	
// attributes

	// attributes
	
	protected String name									= null;
	protected String description 							= null;
	protected Long projectId								= null;
	protected Long modelId							 		= null;
	protected String techStackPackageName					= null;
	protected GitParams gitParams							= null;
	protected transient Map<String, String> options 		= null;
	protected Project project							 	= null;
	protected List<Build> builds							= null;
	private static final Logger LOGGER 						= Logger.getLogger(ProjectHandlerAction.class.getName());
}
