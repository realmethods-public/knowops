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

import java.lang.reflect.Field;

import org.w3c.dom.Node;

import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.realmethods.codetemplate.model.attribute.AttributeObject;
import com.realmethods.codetemplate.model.attribute.ClassAttributeObject;
import com.realmethods.codetemplate.model.attribute.EcoreAttributeObject;
import com.realmethods.codetemplate.model.attribute.NodeAttributeObject;
import com.realmethods.codetemplate.model.classes.ClassObject;

/**
 * Factor method for creating the variety of AttributeObject types.
 * 
 * @author realMethods, Inc.
 *
 */
public class AttributeFactory {
	/**
	 * intentionally protected
	 */
	protected AttributeFactory() {
	}

	/**
	 * Singleton factory pattern
	 * 
	 * @return
	 */
	public static AttributeFactory getInstance() {
		if (self == null)
			self = new AttributeFactory();

		return (self);
	}

	/**
	 * Create an instance of an AttributeObject for a parent ClassObject using a DOM Node.
	 * 
	 * @param object
	 * @param node
	 * @return
	 */
	public AttributeObject createInstance(ClassObject object, Node node) {
		NodeAttributeObject attributeObject = new NodeAttributeObject(object, node);
		return (attributeObject);
	}

	/**
	 * Create an instance of an AttributeObject for a parent ClassObject using a Java Field.
	 * @param object
	 * @param field
	 * @return
	 */
	public AttributeObject createInstance(ClassObject object, Field field) {
		ClassAttributeObject attributeObject = new ClassAttributeObject(object, field);
		return (attributeObject);
	}

	/**
	 * Create an instance of an AttributeObject for a parent ClassObject using a ECore EStructuralFeature.
	 * 
	 * @param object
	 * @param feature
	 * @return
	 */
	public AttributeObject createInstance(ClassObject object, EStructuralFeature feature) {
		EcoreAttributeObject attributeObject = new EcoreAttributeObject(object, feature);
		return (attributeObject);
	}

	/**
	 * Create an instance of an AttributeObject for a parent ClassObject using a ECore EEnumLiteral.
	 * 
	 * @param object
	 * @param literal
	 * @return
	 */
	public AttributeObject createInstance(ClassObject object, EEnumLiteral literal) {
		EcoreAttributeObject attributeObject = new EcoreAttributeObject(object, literal);
		return (attributeObject);
	}

	// attribute
	private static AttributeFactory self = null;

}
