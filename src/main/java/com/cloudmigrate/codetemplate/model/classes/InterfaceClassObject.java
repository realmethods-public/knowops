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
package com.cloudmigrate.codetemplate.model.classes;

/**
 * Wraps a ClassObject to represent an interface as discovered during model parsing. Simply assigns
 * the internal isInterface boolean to true.
 * 
 * @author realMethods, Inc.
 *
 */
public class InterfaceClassObject extends ClassObject
{
    public InterfaceClassObject() {
    	isInterface = true;
    }

    public InterfaceClassObject( ClassObject classObject ) {
    	super( classObject );
    	isInterface = true;
    }

}
