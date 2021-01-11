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


import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;

import com.realmethods.foundational.common.properties.PropertyFileLoader;


/**
 * Base class for all Framework Servlets.
 * <p>
 * @author    realMethods, Inc.
 */
public class FrameworkBaseServlet 
extends HttpServlet
{
//************************************************************************    
// Public Methods
//************************************************************************

	@Override
	/**
	 * Initialization method, get the underlying framework persistent mechanism
	 * started.  More importantly, uses AppGenHelper to get things jump started
	 * 
	 * @exception ServletException
	 */
	public void init(ServletConfig config) throws ServletException {
		
        // call base class
        super.init(config);
        
    	
        //------------------------------------------------------
        // Get the Framework started...
        //------------------------------------------------------    
        try {
        	startFramework();
        }
        catch( Exception exc ) {
            throw new ServletException( "FrameworkBaseServlet:init() - ", exc );
        }
        
    }
    
	@Override
    /**
     * Returns the description of the servlet.
     * @return The description of the servlet.
     */
    public String getServletInfo()
    {
        return "Framework Base Servlet";
    }

    /**
     * Method gets the Framework started
     */
    public void startFramework()
    {
        // preload all property files
    	try
		{
			if (! PropertyFileLoader.getInstance().hasLoadedPropertyfiles() )			
				PropertyFileLoader.getInstance().loadPropertyFiles( getServletContext().getResource( "/" ) );
        }
        catch( Exception exc )
        {
            LOGGER.log( Level.SEVERE, "Unable to locate the necessary property in directory /resources under the Servlet context.", exc);
        }    
                
    }

    // attributes

	private static final Logger LOGGER 			= Logger.getLogger(FrameworkBaseServlet.class.getName());

}

