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
package com.cloudmigrate.codetemplate.model.component;

import java.util.*;
import java.util.logging.Logger;

import org.w3c.dom.Node;

import com.cloudmigrate.codetemplate.model.method.MethodObject;
import com.cloudmigrate.codetemplate.xmi.XMIProviderFactory;

/**
 * Wrapper class to handle the concept of a Component with it's origin from a DOM Node.
 * 
 * @author realMethods, Inc.
 *
 */
public class NodeComponentObject extends ComponentObject
{
	/**
	 * Constructor
	 * @param node
	 */
    public NodeComponentObject( Node node ) {
    	this.node = node ;
    }
    
    @Override
    /**
     * Redirects to method loadMethods()
     */
    public void finishLoading() {
    	loadMethods();
    }

    /**
     * Loads the MethodObjects from the bound Node, using an IXMIProvider
     */
    public void loadMethods()
    {
    	LOGGER.info("Loading Component methods for " + getName() );

		XMIProviderFactory.getInstance().getXMIProvider()
										.findMethods( node )
										.forEach( this::addMethod ); 
    }

// attributes
    protected Node node 				= null;
    private static final Logger LOGGER 	= Logger.getLogger(NodeComponentObject.class.getName());    
}
	
