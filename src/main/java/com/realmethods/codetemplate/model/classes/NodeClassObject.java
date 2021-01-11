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
package com.realmethods.codetemplate.model.classes;

import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Node;

import com.realmethods.codetemplate.model.association.AssociationEndObject;
import com.realmethods.codetemplate.model.attribute.AttributeObject;
import com.realmethods.codetemplate.model.method.MethodObject;
import com.realmethods.codetemplate.xmi.XMIProviderFactory;

/**
 * Extends the notion of a ClassObject by binding it with a DOM Node.
 * 
 * @author realMethods, Inc.
 *
 */
public class NodeClassObject extends ClassObject
{
	/**
	 * Constructor to bind with a DOM Node.
	 * 
	 * @param node
	 */
    public NodeClassObject( Node node ) {
    	this.node = node ;
    }

    @Override
    /**
     * Helper method to signal ClassObject loading is complete, and it is safe to load 
     * methods, attributes and associations
     */
    public void finishLoading() {
    	loadMethods();
    	loadAttributes();
    	loadAssociations();
    }
    
    /**
     * Loads the associated methods from the DOM for this class Node.
     */
    public void loadMethods() {
    	final String msg = "Loading methods for class " + getName();
        LOGGER.info(msg);

        Consumer<MethodObject> addMethodMethRef = this.businessMethods::add;
		XMIProviderFactory.getInstance().getXMIProvider().findMethods( node ).forEach( addMethodMethRef );
    }

    /**
     * Loads the attributes from the DOM for this class Node.
     */
    public void loadAttributes() { 
    	final String msg = "Loading attributes for class "+ getName();
    	LOGGER.info(msg);
    	
    	Consumer<AttributeObject> addAttributeMethRef = this.attributes::add;
    	XMIProviderFactory.getInstance().getXMIProvider()
    		.findAttributes(this, node).forEach( addAttributeMethRef );
    }

    public void loadAssociations()
    {    	
    	final String msg = "Loading associations for class "+ getName();
    	LOGGER.info( msg);
        
        List<AssociationEndObject> tempAssocations = XMIProviderFactory
        													.getInstance()
        													.getXMIProvider()
        													.findAssociations(node);
        tempAssocations.forEach( associationEndObject ->{ 
            try {
                if(associationEndObject.getRoleName().equals("(none)") && associationEndObject.getSiblingAssociationEndObject().getRoleName().equals("(none)"))
                	LOGGER.log(Level.WARNING, "NodeClassObject:loadAssociations() for class " + getName() + " Missing Association Role Name. A role name is not defined for association between " 
                    					+ associationEndObject.getType() 
                    					+ " and " 
                    					+ associationEndObject.getSiblingAssociationEndObject().getType());

                if(associationEndObject.isNavigable() 
                		|| (!associationEndObject.isNavigable() && !associationEndObject.getSiblingAssociationEndObject().isNavigable()))
                    addAssociation(associationEndObject);
            }
            catch(Exception exc) {
                LOGGER.log(Level.WARNING, "NodeClassObject:loadAssociations() for class " + getName(), exc );
            }
        });
    }


// attributes
    protected Node node 					= null;
	private static final Logger LOGGER		= Logger.getLogger(NodeClassObject.class.getName());
}
	
