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
package com.cloudmigrate.codetemplate.model.subsystem;

import org.w3c.dom.Node;

import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.cloudmigrate.codetemplate.model.method.MethodObject;
import com.cloudmigrate.codetemplate.xmi.XMIProviderFactory;

/**
 * DOM Node representation of a Subsystem, wrapped in a DOM Node
 * required during construction.
 * 
 * @author realMethods, Inc.
 *
 */
public class NodeSubsystemObject extends SubsystemObject
{
	/**
	 * Intentionally protected.  Call NodeSubsystemObject( Node ).
	 */
	protected NodeSubsystemObject() {
	}
	
	/**
	 * Constructor
	 * @param node
	 */
    public NodeSubsystemObject( Node node ) {
    	this.node = node ;
    }

    @Override
    /**
     * 
     */
    public void finishLoading()
    {
    	loadMethods();
    }

    /**
     * Use the prevailing XMIProvider to assist in located a Collection 
     */
    public void loadMethods()
    {
    	final String msg = "Loading methods for Subsystem " + name;
        LOGGER.log(Level.INFO, msg);

        Consumer<MethodObject> addMethodRef = businessMethods::add;
        XMIProviderFactory
        	.getInstance()
        	.getXMIProvider()
        	.findMethods( node )
        	.forEach( addMethodRef );
    }

// attributes
    protected Node node 								= null;
    private static final Logger LOGGER 	= Logger.getLogger(NodeSubsystemObject.class.getName());
}
	
