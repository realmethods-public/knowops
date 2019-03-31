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
package com.cloudmigrate.codetemplate.xmi;

import com.cloudmigrate.codetemplate.AppGenObject;
import com.cloudmigrate.codetemplate.model.classes.ClassObject;
import com.cloudmigrate.codetemplate.model.component.ComponentObject;
import com.cloudmigrate.codetemplate.model.subsystem.SubsystemObject;
import com.cloudmigrate.codetemplate.parser.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.xpath.CachedXPathAPI;

import org.w3c.dom.*;

/**
 * Abstract base class of all XMI Providers. 
 * 
 * @author realMethods, Inc.
 * 
 */
public abstract class XMIProvider extends AppGenObject implements IXMIProvider {

	public XMIProvider() {
	}

	// no support for this here
	public List<SubsystemObject> findSubsystems(Node node) {
		return (new ArrayList<>());

	}

	// no support for this here
	public List<ComponentObject> findComponents(Node node) {
		return (new ArrayList<>());

	}

	// no support for this here
	public List<ClassObject> findClasses(Node node) {
		return (new ArrayList<>());
	}


	protected String getID(Node node) {
		Node n = null;
		String id = null;

		try {
			n = getXPathAPI().selectSingleNode(node, "@xmi.id");
			id = n.getNodeValue();
		} catch (Exception exc) {
			final String msg = "Failed to get ID for element " + getElementName(node);
			LOGGER.log(Level.WARNING, msg, exc);
		}

		return id;
	}

	protected Node locateChildNodeByName(Node parentNode, String childNodeName) {
		NamedNodeMap list 	= parentNode.getAttributes();
		Node childNode 		= null;
		boolean found 		= false;

		for (int i = 0; !found && i < list.getLength(); i++) {
			childNode = list.item(i);
			if (childNode.getNodeName().equalsIgnoreCase(childNodeName))
				found = true;
		}

		if (!found) {
			try {
				childNode = getXPathAPI().selectSingleNode(parentNode, "@" + childNodeName);
				if (childNode != null)
					found = true;
			} catch (Exception exc) {
				final String msg = "Failed on selectSingleNode call for " + childNodeName; 
				LOGGER.log(Level.WARNING, msg, exc);
			}
			if (!found)
				childNode = null;
		}
		return childNode;
	}

	public CachedXPathAPI getXPathAPI() {
		return xpathapi;
	}

	public boolean getUserIdentifiableIndicator(Node attributeNode) {

		boolean is = false;

		String stereotype = findStereotype(attributeNode);
		try {
			if (stereotype != null && stereotype.toLowerCase().lastIndexOf("userid") >= 0) {
				is = true;
				LOGGER.info("UserIdentifiable attribute discovered");
			}
		} catch (Exception exc) {
			final String msg = "Failed to discover a user identifiable indicator for " + getElementName(attributeNode); 
			LOGGER.log(Level.WARNING, msg);
		}

		return is;

	}

	/**
	 * Returns the Enumeration XMI declaration of the implementation class
	 * 
	 * @return enumeration XMI declaration
	 */
	public String getEnumXMIDecl() {
		return null;
	}
	// attributes
	private CachedXPathAPI xpathapi 	= new CachedXPathAPI();
	private static final Logger LOGGER 	= Logger.getLogger(XMIProvider.class.getName());

}
