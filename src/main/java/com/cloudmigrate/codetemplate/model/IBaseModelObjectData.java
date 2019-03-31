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
package com.cloudmigrate.codetemplate.model;

import java.util.List;

/**
 * Interface for BaseModelObject
 * 
 * @author realMethods, Inc.
 * 
 */
public interface IBaseModelObjectData {
	public String getName();

	public void setName(String name);

	public String getDisplayName();

	public void setDisplayName(String name);

	public String getParentName();

	public void setParentName(String s);

	public boolean hasIdentity();

	public boolean hasSimplePrimaryKey();

	public boolean hasCompoundPrimaryKey();

	public boolean isAbstract();

	public void isAbstract(boolean b);

	public boolean noDuplicates();

	public void noDuplicates(boolean noDuplicates);

	public boolean isUserCreateable();

	public void isUserCreateable(boolean b);

	public boolean isUserEditable();

	public void isUserEditable(boolean b);

	public boolean isUserDeletable();

	public void isUserDeletable(boolean b);

	public boolean isUserDefined();

	public void isUserDefined(boolean is);

	public boolean isAuditable();

	public boolean isEnumerator();

	public void isEnumerator(boolean b);

	public boolean isService();

	public void isService(boolean b);

	public boolean isRootObject();

	public boolean isLeafObject();

	public boolean canBeGenerated();

	public boolean canBeVersioned();

	public void canBeGenerated(boolean b);

	public void canBeVersioned(boolean b);

	public boolean isFromXMLFile();

	public boolean hasParent();

	public String getPackageName();

	public void setPackageName(String name);

	public String getFormattedPackageName();

	public String getStereotype();

	public void setStereotype(String type);

	public boolean hasDocumentation();

	public String getDocumentation();

	public void setDocumentation(String docs);

	public List<String> getInterfaces();

	public void setInterfaces(List<String> interfaces);

	public BaseModelObject getParent();

	public boolean hasAsParent(String parentName);

	public List<String> getNamesInHierarchy();

	public List<String> getSuperTypes();

	public void setSuperTypes(List<String> superTypes);
}
