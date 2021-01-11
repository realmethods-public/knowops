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
package com.realmethods.codetemplate.model.factory;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Node;

import com.realmethods.codetemplate.AppGenObject;
import com.realmethods.codetemplate.model.BaseModelObject;
import com.realmethods.codetemplate.xmi.IXMIProvider;
import com.realmethods.codetemplate.xmi.XMIProviderFactory;
import com.realmethods.common.helpers.Utils;

public abstract class BaseModelFactory extends AppGenObject {
	/**
	 * intentional limited accessibility
	 */
	protected BaseModelFactory() {
	}

	protected void assignData(Node node, BaseModelObject baseModelObject) {
		IXMIProvider xmiProvider = XMIProviderFactory.getInstance()
				.getXMIProvider();

		assignName(baseModelObject, xmiProvider.getElementName(node));
		assignAbstract(baseModelObject, xmiProvider.findIsAbstract(node));
		assignPackage(baseModelObject, xmiProvider.findPackage(node));
		assignStereoType(baseModelObject, xmiProvider.findStereotype(node));
		assignDocumentation(baseModelObject,
				xmiProvider.getDocumentation(node));
		assignInterfaces(baseModelObject, xmiProvider.findSuperTypes(node));

		baseModelObject.setTags(xmiProvider.getTagNamesAndValues(node));

	}

	protected void assignName(BaseModelObject baseModelObject, String name) {
		if (name != null) {
			name = name.trim();
			name = Utils.capitalizeFirstLetter(name);
		} else {
			LOGGER.warning(
					"BaseModel with no name. There is an entity in the model with no name.  This will likely lead to unpredictable results.");
		}

		baseModelObject.setName(name);
	}

	protected void assignAbstract(BaseModelObject baseModelObject,
			boolean isAbstract) {
		baseModelObject.isAbstract(isAbstract);
	}

	protected void assignPackage(BaseModelObject baseModelObject,
			String packageName) {

		if (packageName != null) {
			packageName = packageName.trim();
			int len = packageName.length();
			StringBuilder buf = new StringBuilder();
			for (int i = 0; i < len; i++) {
				char c = packageName.charAt(i);
				if (!Character.isWhitespace(c))
					buf.append(c);
				else
					buf.append(".");
			}
		} else {
			packageName = "";
		}

		baseModelObject.setPackageName(packageName);

		LOGGER.log(Level.INFO,
				"BaseModelFactor.assignPackage() - "
						+ baseModelObject.getPackageName() + " on class "
						+ baseModelObject.getName());

	}

	protected void assignStereoType(BaseModelObject baseModelObject,
			String stereotype) {
		baseModelObject.setStereotype(stereotype);
		final String msg = "Setting stereotype " + stereotype + FOR_CLASS + baseModelObject.getName();
		LOGGER.log(Level.INFO, msg );
	}

	protected void assignDocumentation(BaseModelObject baseModelObject,
			String documentation) {
		baseModelObject.setDocumentation(documentation);
		final String msg = "Setting documentation " + documentation + FOR_CLASS + baseModelObject.getName();
		LOGGER.log(Level.INFO, msg);
	}

	/**
	 * Assigns the parent name, conditional on the type of the 
	 * 
	 * @param baseModelObject
	 * @param parentName
	 */
	protected void assignParentName(BaseModelObject baseModelObject, String parentName) {
		if (parentName != null && 
			(parentName.equals("java.lang.Object") || parentName.equals("Object"))) {
				LOGGER.info("BaseModelFactory.assignParentName() - Ignoring parent type java.lang.Object");
				parentName = "";
		}
	
		baseModelObject.setParentName(parentName);
		final String msg = "BaseModelFactory.assignParentName() " 
				+ baseModelObject.getParentName() 
				+ FOR_CLASS + baseModelObject.getName();
		LOGGER.log(Level.INFO, msg);
	}

	/**
	 * Helper method to assign the interfaces to the provided baseModelObject
	 * @param baseModelObject
	 * @param interfaces
	 */
	protected void assignInterfaces(BaseModelObject baseModelObject, List<String> interfaces) {
		baseModelObject.setInterfaces(interfaces);
		LOGGER.log(Level.INFO, "Assigned interfaces " 
				+ baseModelObject.getInterfaces() + FOR_CLASS + baseModelObject.getName());
	}

	// attributes
	private static final String FOR_CLASS	= " for class ";				
	private static final Logger LOGGER 		= Logger.getLogger(BaseModelFactory.class.getName());
}
