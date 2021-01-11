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
package com.realmethods.codetemplate.model.classes.enumerate;

import com.realmethods.codetemplate.model.classes.ClassClassObject;

public class ClassEnumClassObject extends ClassClassObject {
	public ClassEnumClassObject(Class theClass) {
		super(theClass);
	}

	@Override
	/**
	 * Returns true
	 */
	public boolean isEnumerator() {
		return true;
	}
}
