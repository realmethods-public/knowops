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
package com.realmethods.foundational.presentation.servlet;

import com.realmethods.foundational.integration.persistent.FrameworkPersistenceHelper;


import javax.servlet.*;
import javax.servlet.http.*;


public class FrameworkBaseStrutsFilterDispatcher
    extends org.apache.struts2.dispatcher.filter.StrutsPrepareAndExecuteFilter
{
	@Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) 
    throws java.io.IOException, ServletException
    {
        try {
            // Begin unit of Hibernate work
        	FrameworkPersistenceHelper.self().getCurrentSession().beginTransaction();

            super.doFilter(req, res, chain);

            // End unit of Hibernate work
            FrameworkPersistenceHelper.self().getCurrentSession().getTransaction().commit();	
        }
        catch (Exception ex) {
        	FrameworkPersistenceHelper.self().getCurrentSession().getTransaction().rollback();
            throw new ServletException(ex);
        }
        finally {
        	FrameworkPersistenceHelper.self().closeSession();
        }
    }	
}
