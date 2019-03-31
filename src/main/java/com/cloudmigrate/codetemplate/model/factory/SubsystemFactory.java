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
package com.cloudmigrate.codetemplate.model.factory;

import java.util.logging.Logger;
import java.lang.reflect.*;

import org.w3c.dom.Node;

import com.cloudmigrate.codetemplate.model.subsystem.SubsystemObject;
import com.cloudmigrate.codetemplate.model.subsystem.NodeSubsystemObject;
import com.cloudmigrate.codetemplate.xmi.IXMIProvider;
import com.cloudmigrate.codetemplate.xmi.XMIProviderFactory;

public class SubsystemFactory extends BaseModelFactory {
	
	/**
	 * Default constructor - intentionally inaccessible
	 */
	protected SubsystemFactory() {
	}

	/**
	 * Singleton factory pattern
	 * 
	 * @return
	 */
	public static SubsystemFactory getInstance() {
		if (self == null)
			self = new SubsystemFactory();

		return (self);
	}

	/**
	 * Factory method to create a SubystemObject from a DOM Node
	 * 
	 * @param node
	 * @return
	 */
	public SubsystemObject createSubsystemObject(Node node) {
		SubsystemObject subsystemObject = new NodeSubsystemObject(node);

		IXMIProvider xmiProvider = XMIProviderFactory.getInstance().getXMIProvider();

		subsystemObject.setChildrenSubsystemObjects(xmiProvider.findSubsystems(node));
		subsystemObject.setChildrenComponentObjects(xmiProvider.findComponents(node));
		subsystemObject.setChildrenClassObjects(xmiProvider.findClasses(node));

		assignData(node, subsystemObject);

		LOGGER.info(" - Done Subsystem Factory Creating : " + subsystemObject.getName());

		return (subsystemObject);

	}

	// attributes
	private static SubsystemFactory self = null;
	private static final Logger LOGGER = Logger.getLogger(SubsystemFactory.class.getName());

}
