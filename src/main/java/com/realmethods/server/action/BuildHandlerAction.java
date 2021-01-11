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

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.realmethods.common.helpers.AppGenHelper;
import com.realmethods.entity.Build;
import com.realmethods.entity.dao.BuildDAO;
import com.realmethods.foundational.common.exception.FrameworkDAOException;
import com.realmethods.foundational.common.exception.ProcessingException;

/**
 * Action handler for all Build actions.
 * 
 * @author realMethods, Inc.
 *
 */
public class BuildHandlerAction extends BaseStrutsAction {
	/**
	 * default constructor
	 */
	public BuildHandlerAction() {
		// no_op
	}

	public String loadBuildSummary() {
		return SUCCESS;
	}
	
	public String getBuildSummary()  throws FrameworkDAOException {
		return new BuildDAO().findBuild(buildId).readableSummary();		
	}

    public String loadBuilds() {
        return SUCCESS;
    }

	/**
	 * returns all Builds for all projects
	 * @return
	 * @throws FrameworkDAOException
	 */
	public List<Build> getBuilds() throws FrameworkDAOException {
		return new BuildDAO().findAllBuild();
	}


	/**
	 * Assigns the BuildId field the provided argument.
	 * 
	 * @param id
	 */
	public void setBuildId( Long id ) {
		buildId = id;
	}

	
// attributes

	// attributes
	
	protected Long buildId		        = null;
	protected List<Build> builds		= null;
	private static final Logger LOGGER 	= Logger.getLogger(BuildHandlerAction.class.getName());
}
