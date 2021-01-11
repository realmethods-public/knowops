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
package com.realmethods.server.servlet;

import com.realmethods.foundational.integration.persistent.FrameworkPersistenceHelper;

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
public class RealMethodsFrameworkServlet extends com.realmethods.foundational.presentation.servlet.FrameworkBaseServlet {
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
			com.realmethods.common.helpers.AppGenHelper.self(config.getServletContext()).jumpStart();
			FrameworkPersistenceHelper.self().getCurrentSession().getTransaction().commit();
		} catch (Exception exc) {
			LOGGER.log( Level.SEVERE, "servlet initialization failure", exc);
		} finally {
			FrameworkPersistenceHelper.self().closeSession();
		}
	}
	
	// attributes
    private static final Logger LOGGER = Logger.getLogger(RealMethodsFrameworkServlet.class.getName());
	
}
