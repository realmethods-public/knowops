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
package com.cloudmigrate.codetemplate.model.classes.enumerate;

import java.util.Iterator;

import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EEnum;

import com.cloudmigrate.codetemplate.model.classes.ClassObject;
import com.cloudmigrate.codetemplate.model.factory.AttributeFactory;

/**
 * Encapsulate the concept of an enumerated type within the Eclipse Metamodel Framework.
 * 
 * @author realMethods, Inc.
 *
 */
public class EcoreEnumClassObject extends ClassObject {
	
	/**
	 * Constructor
	 * @param eEnum
	 */
	public EcoreEnumClassObject(EEnum eEnum) {
		this.eEnum = eEnum;
	}

	@Override
	/**
	 * Returns true
	 */
	public boolean isEnumerator() {
		return true;
	}

	@Override
	/**
	 * Delegates to loadAttributes()
	 */
	public void finishLoading() {
		loadAttributes();
	}

	/**
	 * Helper method to load the attributes from the Eclipse EMF for this ClassObject instance
	 */
	protected void loadAttributes() {
		Iterator iter 				= eEnum.getELiterals().iterator();
		EEnumLiteral eEnumLiteral 	= null;

		while (iter.hasNext()) {
			eEnumLiteral = (EEnumLiteral) iter.next();
			addAttribute(AttributeFactory.getInstance().createInstance(this, eEnumLiteral));
		}
	}

	// attributes
	protected EEnum eEnum 				= null;
}
