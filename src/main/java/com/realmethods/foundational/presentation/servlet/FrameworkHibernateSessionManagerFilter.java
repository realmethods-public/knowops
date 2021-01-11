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

import com.realmethods.foundational.integration.persistent.*;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

public class FrameworkHibernateSessionManagerFilter implements Filter
{
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		try {
			FrameworkPersistenceHelper.self().getCurrentSession();
			chain.doFilter(req, res);
		}
		catch( Exception exc ) {
			// no_op
		}
		finally {
			FrameworkPersistenceHelper.self().closeSession();
		}	
	}	
	
	public void init(FilterConfig filerConfig)
          throws ServletException {
		//no_op
	}	

	public void destroy() {
		// no_op
	}

}
