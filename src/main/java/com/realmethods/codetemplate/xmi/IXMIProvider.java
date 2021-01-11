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
package com.realmethods.codetemplate.xmi;

import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;
import org.apache.xpath.CachedXPathAPI;

import com.realmethods.codetemplate.model.classes.*;
import com.realmethods.codetemplate.model.attribute.*;
import com.realmethods.codetemplate.model.association.*;
import com.realmethods.codetemplate.model.method.*;
import com.realmethods.codetemplate.model.component.*;
import com.realmethods.codetemplate.model.subsystem.*;

/***
 * interface to an XMI Provider implementation to handle parsing an appropriate
 * XMI file
 * 
 * @author realMethods, Inc.
 * 
 */
public interface IXMIProvider {
	/**
	 * Returns the XMI version of the implementation class
	 * 
	 * @return XMI version
	 */
	public String getXMIVersion();

	/**
	 * Returns the Node XMI declaration of the implementation class
	 * 
	 * @return node XMI declaration
	 */
	public String getNodeXMIDecl();

	/**
	 * Returns the Component XMI declaration of the implementation class
	 * 
	 * @return component XMI declaration
	 */
	public String getComponentXMIDecl();

	/**
	 * Returns the Class XMI declaration of the implementation class
	 * 
	 * @return class XMI declaration
	 */
	public String getClassXMIDecl();

	/**
	 * Returns the Interface XMI declaration of the implementation class
	 * 
	 * @return interface XMI declaration
	 */
	public String getInterfaceXMIDecl();

	/**
	 * Returns the Enumeration XMI declaration of the implementation class
	 * 
	 * @return enumeration XMI declaration
	 */
	public String getEnumXMIDecl();
	
	/**
	 * Returns the documentation element of the implementation class
	 * 
	 * @return documentation
	 */
	public String getDocumentation(Node node);

	/**
	 * Find all attributes for the provided class object and it's relevant node
	 * in the DOM
	 * 
	 * @param classobject
	 * @param node
	 * @return collection of AttributeObject
	 */
	public List<AttributeObject> findAttributes(ClassObject classobject, Node node);

	/**
	 * Returns the value of the stereotype element for the provided Node
	 * 
	 * @param node
	 * @return
	 */
	public String findStereotype(Node node);

	/**
	 * Find the child node (subsystems) elements of the provided Node
	 * 
	 * @param node
	 * @return
	 */
	public List<SubsystemObject> findSubsystems(Node node);

	/**
	 * Find the child component elements of the provided Node
	 * 
	 * @param node
	 * @return
	 */
	public List<ComponentObject> findComponents(Node node);

	/**
	 * Find the child class elements of the provided Node
	 * 
	 * @param node
	 * @return
	 */
	public List<ClassObject> findClasses(Node node);

	/**
	 * Find the chld method elements of the provided Node
	 * 
	 * @param node
	 * @return
	 */
	public List<MethodObject> findMethods(Node node);

	/**
	 * Find the child association elements of the provided Node
	 * 
	 * @param node
	 * @return
	 */
	public List<AssociationEndObject> findAssociations(Node node);

	/**
	 * Return the free form tag/value pairings for the provided Node
	 * 
	 * @param node
	 * @return
	 */
	public Map<String, String> getTagNamesAndValues(Node node);

	/**
	 * Return the value of the isAbstract attribute of the provided Node
	 * 
	 * @param node
	 * @return
	 */
	public boolean findIsAbstract(Node node);

	/**
	 * Return the all the generalized types of the provided node. Will include 0
	 * to N interfaces and 0 to 1 super classes.
	 * 
	 * @param node
	 * @return
	 */
	public List<String> findSuperTypes(Node node);

	/**
	 * Returns the name of the provided Node
	 * 
	 * @param nodes
	 * @return
	 */
	public String getElementName(Node node);

	/**
	 * Returns the name of the element associated with the Node and id
	 * 
	 * @param node
	 * @return
	 */
	public String getElementNameforID(String id, Node node);

	/**
	 * Finds the package structure for the provided Node.
	 * 
	 * @param node
	 * @return
	 */
	public String findPackage(Node node);

	public String getAttributeType(Node node);

	/**
	 * Returns the value of the static indicator attribute for the affiliated
	 * Node
	 * 
	 * @param node
	 * @return
	 */
	public boolean getStaticIndicator(Node node);

	/**
	 * Returns the value of the final indicator attribute for the affiliated
	 * Node
	 * 
	 * @param node
	 * @return
	 */
	public boolean getFinalIndicator(Node node);

	/**
	 * Returns the value of the transient indicator attribute for the affiliated
	 * Node
	 * 
	 * @param node
	 * @return
	 */
	public boolean getTransientIndicator(Node node);

	/**
	 * Returns the value of the volatile indicator attribute for the affiliated
	 * Node
	 * 
	 * @param node
	 * @return
	 */
	public boolean getVolatileIndicator(Node node);

	/**
	 * Returns the value of the user identifiable indicator attribute for the
	 * affiliated Node
	 * 
	 * @param node
	 * @return
	 */
	public boolean getUserIdentifiableIndicator(Node node);

	/**
	 * Returns the value of the default value attribute for the affiliated Node
	 * 
	 * @param node
	 * @return
	 */
	public String getDefaultValue(Node node);

	/**
	 * Returns the value of the required indicator attribute for the affiliated
	 * Node
	 * 
	 * @param node
	 * @return
	 */
	public boolean getRequiredIndicator(Node node);

	/**
	 * Returns the value of the final indicator attribute for the affiliated
	 * association Node
	 * 
	 * @param node
	 * @return
	 */
	public boolean getFinalIndicatorForAssociation(Node node);

	/**
	 * Returns the value of the addOnly indicator attribute for the affiliated
	 * association Node
	 * 
	 * @param node
	 * @return
	 */
	public boolean getAddOnlyIndicatorForAssociation(Node node);

	/**
	 * Returns the value of the null indicator attribute for the provided Node
	 * 
	 * @param node
	 * @return
	 */
	public boolean getNullIndicator(Node node);

	/**
	 * Returns the value of the intrinsic type indicator attribute for the
	 * provided Node
	 * 
	 * @param node
	 * @return
	 */
	public boolean getIntrinsicTypeIndicator(Node node);

	/**
	 * Returns the sibling (other end) of the provided association Node
	 * 
	 * @param node
	 * @return
	 */
	public AssociationEndObject getAssociationSibling(Node node);

	/**
	 * Returns the type of the role for the association affiliated with the
	 * provided Node
	 * 
	 * @param node
	 * @return
	 */
	public String getAssociationEndType(Node node);

	/**
	 * Returns the name of the role for the association affiliated with the
	 * provided Node
	 * 
	 * @param node
	 * @return
	 */
	public String getAssociationRoleName(Node node);

	/**
	 * Returns the low and high values of the upper/lower range elements for the
	 * provided Node
	 * 
	 * @param node
	 * @return
	 */
	public int[] getLowerUpperRange(Node node);

	/**
	 * Returns the value of the aggregaton indicator attribute for the provided
	 * Node
	 * 
	 * @param node
	 * @return
	 */
	public boolean getAggregationIndicator(Node node);

	/**
	 * Returns the value of the composite indicator attribute for the provided
	 * Node
	 * 
	 * @param node
	 * @return
	 */
	public boolean getCompositeIndicator(Node node);

	/**
	 * Returns the value of the navigable indicator attribute for the provided
	 * Node
	 * 
	 * @param node
	 * @return
	 */
	public boolean getNavigableIndicator(Node node);

	/**
	 * Returns the value of the ordered indicator attribute for the provided
	 * Node
	 * 
	 * @param node
	 * @return
	 */
	public boolean getOrderedIndicator(Node node);

	/**
	 * Returns the value of the visibility attribute for the provided Node
	 * 
	 * @param node
	 * @return
	 */
	public String getVisibility(Node node);

	/**
	 * Returns the value of the isStatic attribute for the provide Method Node
	 * 
	 * @param node
	 * @return
	 */
	public boolean isMethodStatic(Node node);

	/**
	 * Returns the method arguments for the provided Node.
	 * 
	 * @param node
	 * @return
	 */
	public MethodArgs getMethodArguments(Node node);

	/**
	 * The cached XPathAPI used to navigate and investigate the DOM of the
	 * provided UML/XMI representation
	 * 
	 * @return
	 */
	public CachedXPathAPI getXPathAPI();
}
