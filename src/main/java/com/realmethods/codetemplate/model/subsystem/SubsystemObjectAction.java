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
package com.realmethods.codetemplate.model.subsystem;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.realmethods.codetemplate.AppGenObject;
import com.realmethods.codetemplate.parser.ModelParser;

/**
 * Handles the action related methods on behalf of a SystemObject
 * 
 * @author realMethods, Inc.
 */
public class SubsystemObjectAction extends AppGenObject implements ISubsystemObjectAction {
	/**
	 * Intentionally protected
	 */
	protected SubsystemObjectAction() {
	}

	/**
	 * Constructor 
	 * @param subsystemObject
	 */
	public SubsystemObjectAction(SubsystemObject subsystemObject) {
		this.subsystemObject = subsystemObject;
	}

	/**
	 * Helper method to figure out the parent class and interfaces
	 */
	public void reconcileSuperTypes() {
		String parentName = subsystemObject.getParentName();
		final String name = subsystemObject.getName();

		if (subsystemObject.hasParent() &&
				ModelParser.modelParser().locateBaseModelObject(parentName) == null) {
				final String msg = "Unable to locate parent class. Class name " 
						+ name + " has parent " + parentName 
						+ ", but it wasn't loaded in model.  It will be ignored.";
				LOGGER.log(Level.WARNING, msg );
		}

		Iterator<String> iter = subsystemObject.getSuperTypes().iterator();
		String superType = null;

		while (parentName == null && iter.hasNext()) {
			superType = iter.next();

			if (ModelParser.modelParser().locateBaseModelObject(superType) != null) {
				parentName = superType;
				subsystemObject.getSuperTypes().remove(superType);
			}
		}
		
		final String msg = "SystemObjectAction.reconcileSuperTypes() - Parent Interface : " + parentName;
		LOGGER.log(Level.INFO, msg );
	}

	// attributes
	private SubsystemObject subsystemObject = null;
	private static final Logger LOGGER = Logger.getLogger(SubsystemObjectAction.class.getName());

}
