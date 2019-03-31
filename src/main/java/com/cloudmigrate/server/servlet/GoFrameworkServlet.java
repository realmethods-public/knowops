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
package com.cloudmigrate.server.servlet;

import com.cloudmigrate.foundational.integration.persistent.FrameworkPersistenceHelper;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

/**
 * The AppGen Servlet
 * 
 * @author realMethods, Inc.
 */

// AIB : \#getServletClassDecl()
public class GoFrameworkServlet extends com.cloudmigrate.foundational.presentation.servlet.FrameworkBaseServlet {
	@Override
	/**
	 * Initialization method, get the underlying framework persistent mechanism
	 * started.  More importantly, uses AppGenHelper to get things jump started
	 * 
	 * @exception ServletException
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		try {
			FrameworkPersistenceHelper.self().getCurrentSession().beginTransaction();
			com.cloudmigrate.common.helpers.AppGenHelper.self(config.getServletContext()).jumpStart();
			FrameworkPersistenceHelper.self().getCurrentSession().getTransaction().commit();
		} catch (Exception exc) {
			LOGGER.log( Level.SEVERE, "GoFrameworkServlet.init()", exc);
		} finally {
			FrameworkPersistenceHelper.self().closeSession();
		}
	}
	
	// attributes
    private static final Logger LOGGER = Logger.getLogger(GoFrameworkServlet.class.getName());
	
}
