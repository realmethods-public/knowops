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
import java.util.logging.Logger;

import com.cloudmigrate.codetemplate.model.attribute.AttributeObject;
import com.cloudmigrate.codetemplate.model.classes.ClassObject;

public class EnumClassObject extends ClassObject {
	
	public EnumClassObject(ClassObject classObject) {
		classObject.setStereotype("enum");

		innerClassObject = classObject;

		copy(classObject);
		
	}
	
	@Override
	public boolean isEnumerator() {
		return true;
	}

	public String getCommonType() {
		String commonAttributeType = null;

		Iterator iter = innerClassObject.getAttributes().iterator();
		if (iter.hasNext()) {
			AttributeObject attribute = (AttributeObject) iter.next();
			commonAttributeType = attribute.getType();
		} else {
			LOGGER.warning("Failure to appropriately define enumerator " + getName()
					+ ". Assuming types to be of Integer type.");
			commonAttributeType = "Integer";
		}

		return commonAttributeType;
	}

	public String getValues() {
		StringBuilder values = new StringBuilder();

		Iterator<AttributeObject> iter = innerClassObject.getAttributes().iterator();
		String name = null;

		while (iter.hasNext()) {
			name = iter.next().getName();
			values.append(name);
			values.append(":");
			values.append(name);
			values.append(";");
		}

		return (values.toString());
	}

	protected void validateAttributeTypes() {
		Iterator iter = getAttributes().iterator();
		String type = null;
		String testType = null;
		AttributeObject attribute = null;
		int index = 0;

		while (iter.hasNext()) {
			attribute = (AttributeObject) iter.next();
			testType = attribute.getType();

			if (type != null) {
				if (!type.equalsIgnoreCase(testType)) {
					String msg = "Enumerator " + getName() + " contains type " + attribute.getName() + ":" + testType
							+ " which is different than main type " + type + ".";
					LOGGER.info(msg);
					// if it's junk,remove it from the name array and attribute
					// array
					iter.remove();
				}
			} else {
				type = testType;
			}
			if (attribute.getDefaultValue() == null) {
				String msg = "Invalid Enumerator Attribute : Enumerator " + getName() +
				" contains attribute " + attribute.getName() +
				" which has no default value.";
				LOGGER.warning( msg );
				attribute.setDefaultValue("\"" + index + "\"");
			}
			index++;
		}
	}

	@Override
	/**
	 * Delegates to loadAttributes()
	 */
	public void finishLoading() {
		loadAttributes();
	}

	/**
	 * Loads the attributes of an enum class.
	 */
	public void loadAttributes() {
		LOGGER.info("Loading enum attributes");

		if (innerClassObject != null) {
			innerClassObject.finishLoading();
			copy(innerClassObject);
		}

		validateAttributeTypes();

	}

	@Override
	/**
	 * This is a no op
	 */
	public void reconcileSuperTypes() {
		// no_op
	}

	// attributes

	protected ClassObject innerClassObject 	= null;
	private static final Logger LOGGER 		= Logger.getLogger(EnumClassObject.class.getName());

}
