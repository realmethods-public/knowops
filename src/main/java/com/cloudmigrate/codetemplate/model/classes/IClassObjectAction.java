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

import com.cloudmigrate.codetemplate.model.association.AssociationEndObject;
import com.cloudmigrate.codetemplate.model.attribute.AttributeObject;
import com.cloudmigrate.codetemplate.model.method.MethodObject;

/**
 * Simple interface for ClassObjectAction
 * @author realMethods, Inc.
 *
 */
public interface IClassObjectAction
{
	public void applySelfToAssociations();
	public void sortAttributes();
	public void addMethod(MethodObject method);
	public boolean methodIsUnique(MethodObject method);
	public void addAttribute(AttributeObject attributeObject);
	public void addAssociation(AssociationEndObject association);
	public void removeAttribute(AttributeObject attribute);
	public void removeAssociation(AssociationEndObject association);
	public void modifyPrimaryKeysTo( String transferToType );
	public AttributeObject transformAssociationToAttribute(AssociationEndObject associationEndObject);
	public AttributeObject findAttribute( String attributeName );
	
}
