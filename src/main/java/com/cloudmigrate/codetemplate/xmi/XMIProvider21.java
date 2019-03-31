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
package com.cloudmigrate.codetemplate.xmi;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;

import com.cloudmigrate.codetemplate.model.attribute.AttributeObject;
import com.cloudmigrate.codetemplate.model.association.AssociationEndObject;
import com.cloudmigrate.codetemplate.model.classes.ClassObject;
import com.cloudmigrate.codetemplate.model.factory.AssociationFactory;
import com.cloudmigrate.codetemplate.model.factory.AttributeFactory;
import com.cloudmigrate.codetemplate.model.factory.ClassFactory;
import com.cloudmigrate.codetemplate.model.factory.MethodFactory;
import com.cloudmigrate.codetemplate.model.method.MethodArg;
import com.cloudmigrate.codetemplate.model.method.MethodArgs;
import com.cloudmigrate.codetemplate.model.method.MethodObject;
import com.cloudmigrate.codetemplate.parser.XMIParser;
import com.cloudmigrate.common.helpers.Utils;

/**
 * XMI UML 2.0 model handler for turning DOM Nodes and elements into realMethods AppGen Model.
 * 
 * @author realMethods, Inc.
 */
public class XMIProvider21 extends XMIProvider
{
	/**
	 * default constructor
	 */
	public XMIProvider21() {
		// intentionally empty
	}

	/**
	 * Max version this instance can support
	 * 
	 * @return String
	 */
	public String getXMIVersion() {
		return MAX_XMI_VERSION_SUPPORTED;
	}

	/**
	 * Return the call to obtain DOM Element that represents the Node Declaration.
	 * 
	 * @return String
	 */
	public String getNodeXMIDecl() {
		return null;
	}

	/**
	 * Return the call to obtain DOM Element that represents the Component Declaration.
	 * 
	 * @return String
	 */
	public String getComponentXMIDecl() {
		return null;
	}

	/**
	 * Return the call to obtain DOM Element that represents the Class Declaration.
	 * 
	 * @return String
	 */
	public String getClassXMIDecl() {
		return ".//packagedElement[@xmi:type='uml:Class']";
	}

	
	/**
	 * Return the call to obtain DOM Element that represents the Interface Declaration.
	 * 
	 * @return String
	 */
	public String getInterfaceXMIDecl() {
		return ".//packagedElement[@xmi:type='uml:Interface']";
	}

	@Override
	/**
	 * Returns the Enumeration XMI declaration of the implementation class
	 * 
	 * @return enumeration XMI declaration
	 */
	public String getEnumXMIDecl() {
		return ".//packagedElement[@xmi:type='uml:Enumeration']";
	}
	
	@Override
	/**
	 * Searches for class nodes under the parentNode and returns them as a List<ClassObject>
	 * 
	 * @return	List<ClassObject>
	 */
    public List<ClassObject> findClasses(Node parentNode) {
		String classPath 			= ".//packagedElement[@xmi:type='uml:Class']";	
		List<ClassObject> classes 	= new ArrayList<>();
		Node classNode	 			= null;
		String className			= null;
		
		try {
			NodeIterator nl 			= getXPathAPI().selectNodeIterator(parentNode, classPath);			
			ClassObject classdObject 	= null;
			String msg				 	= null;
			
			while ((classNode = nl.nextNode()) != null) {
				className	= getElementName(classNode);
				msg = PARENT_MSG_PREFIX + getElementName(parentNode) + " contains class: " + className;
				LOGGER.info(msg);

				classdObject = ClassFactory.getInstance().createClassObject( classNode);
				classes.add(classdObject);
			}
		}
		catch (Exception exc) {
			final String msg = "Error creating a ClassObject instance for Node " + className;

			LOGGER.log(Level.WARNING,
					msg,
					exc);
		}

		// next treat enums as classes
		try {
			String enumPath 			= ".//packagedElement[@xmi:type='uml:Enumeration']";	
			NodeIterator nl 			= getXPathAPI().selectNodeIterator(parentNode, enumPath);			
			ClassObject classdObject 	= null;
			String msg				 	= null;
			
			while ((classNode = nl.nextNode()) != null) {
				className	= getElementName(classNode);
				msg = PARENT_MSG_PREFIX + getElementName(parentNode) + " contains enumeration: " + className;
				LOGGER.info(msg);

				classdObject = ClassFactory.getInstance().createClassObject( classNode );
				classdObject.setStereotype("enum");	// tag it as an enum
				classes.add(classdObject);
			}
		}
		catch (Exception exc) {
			final String msg = "Error creating a ClassObject instance for Node " + className;

			LOGGER.log(Level.WARNING,
					msg,
					exc);
		}

		return classes;
    }

    /**
     * Searches for the attribute nodes of the classNode and creates an AttributeObject
     * for each and returns the entire list.
     * 
     * @return List<AttributeObject>	
     */
	public List<AttributeObject> findAttributes(ClassObject classObject, Node classNode) {
		String attributePath 				= ".//ownedAttribute[not(@association)]";
		List<AttributeObject> attributes 	= new ArrayList<>();

		// enumerated types use ownedLiteral, not ownedAttribute
		if ( classObject.isEnumerator() )
			attributePath = "ownedLiteral";
		
		try {
			NodeIterator nl 	= getXPathAPI().selectNodeIterator(classNode, attributePath);
			Node attributeNode	= null;
			
			while ((attributeNode = nl.nextNode()) != null) {
					attributes.add(AttributeFactory.getInstance().createInstance(classObject, attributeNode));
			}
		}
		catch (Exception exc) {
			final String msg = "Error creating a AttributeObject instance for Node " + getElementName(classNode);

			LOGGER.log(Level.WARNING,
					msg,
					exc);
		}

		return attributes;
	}

	/**
     * Searches for the association nodes of the classNode and creates a AssociationEndObject
     * for each and returns the entire list.
     * 
     * @return List<AssociationEndObject>
     */	
	public List<AssociationEndObject> findAssociations(Node classNode) {
		List<AssociationEndObject> associations = new ArrayList<>();
		String associationEndPath 				= ".//ownedAttribute[@association]";
		
		try {
			NodeIterator nl 							= getXPathAPI()
														.selectNodeIterator(classNode, associationEndPath);
			Node associationEndNode 					= null;
			AssociationEndObject associationEndObject 	= null;
			
			while ((associationEndNode = nl.nextNode()) != null) {
				boolean findSibling = true;
				associationEndObject = AssociationFactory.getInstance().createInstance(associationEndNode, findSibling);
				associations.add(associationEndObject);

			}
		}
		catch (Exception exc) {
			final String msg = "Error creating a AssociationObject instance for Node " + getElementName(classNode);

			LOGGER.log(Level.WARNING,
					msg,
					exc);
		}
		return associations;
	}


	/**
     * Searches for the method nodes of the classNode and creates a MethodObject
     * for each and returns the entire list.
     * 
     * @return List<MethodObject>
     */	
    public List<MethodObject> findMethods(Node classNode) {
		String methodPath 			= ".//ownedOperation";
		List<MethodObject> methods 	= new ArrayList<>();

		try {
			NodeIterator nl = getXPathAPI().selectNodeIterator(classNode, methodPath);
			Node methodNode = null;
			MethodObject methodObject = null;
			
			while ((methodNode = nl.nextNode()) != null) {
				methodObject = MethodFactory.getInstance().createInstance(methodNode);
				methods.add(methodObject);
				final String msg = "For class: " + getElementName(classNode) + ", method discovered: " + methodObject.toString();
				LOGGER.log( Level.INFO, msg);
			}
		}
		catch (Exception exc) {
			final String msg = "Error creating a MethodObject instance for Node " + getElementName(classNode);
			LOGGER.log(Level.WARNING,
					msg,
					exc);
		}
		return methods;
	}

	/**
	 * Returns the tag names and values of an association node as a Map.
	 * 
	 * @return Map<String, String>
	 */
	public Map<String, String> getTagNamesAndValues(Node associationNode) {
		return new HashMap<>();
	}

	/**
	 * Returns the stereotype for the given node.
	 * 
	 * @return	String
	 */
	public String findStereotype(Node theNode) {
		Node n 					= locateChildNodeByName(theNode, "ownedStereotype");
		String stereotype 		= null;
		String stereoTypeRef 	= null;
		
		if (n != null)
			stereoTypeRef = n.getNodeValue();
		
		if (stereoTypeRef != null) {
			stereotype = findStereotypeByXMIId(stereoTypeRef);
		}

		return stereotype;
	}

	/**
	 * Returns true/false indicating if the class is abstract
	 * 
	 * @return boolean
	 */
	public boolean findIsAbstract(Node classNode) {
		Node n 				= locateChildNodeByName(classNode, "isAbstract");
		boolean isAbstract 	= false;
		
		if (n != null && n.getNodeName().equalsIgnoreCase("true"))
			isAbstract = true;
		
		return isAbstract;
	}

	/**
	 * Returns the package associated with the referenceNode.  Removes any whitespace.
	 * 
	 * @return String
	 */
	public String findPackage(Node referenceNode) {
		StringBuilder packageName 	= new StringBuilder();
		Node parentNode 			= referenceNode.getParentNode();
		String packagePath 			= "packagedElement";
		String errMsgPrefix			= "Failed to find package on ";
		Node nameNode 				= null;

		try {
			for (; parentNode != null; parentNode = parentNode.getParentNode()) {
				if (parentNode.getNodeName() != null 
						&& parentNode.getNodeName().equalsIgnoreCase(packagePath)) {
					nameNode = locateChildNodeByName(parentNode, "name");
					if (nameNode != null) {
						packageName.insert(0, nameNode.getNodeValue() + ".");
					}
					else {
						final String msg = errMsgPrefix + getElementName(nameNode);
						LOGGER.warning(msg);
					}
				}
			}
		}
		catch (Exception exc) {
			final String msg = errMsgPrefix + getElementName(referenceNode);
			LOGGER.log(Level.WARNING, msg, exc);
		}

		if (packageName.length() > 0)
			packageName = new StringBuilder(packageName.toString().substring(0,packageName.length() - 1));

		return Utils.removeSpaces(packageName.toString());
	}

	/**
	 * Returns a list of the parents of the provided node.
	 * 
	 * @return List<String>
	 */
	public List<String> findSuperTypes(Node classNode) {
		List<String> superTypes = new ArrayList<>();
		String superTypePath 	= ".//generalization";
		String errMsgPrefix		= "Failed to acquire parent classes for class node ";
		String className		= getElementName(classNode);

		try {
			// look for generalizations
			Node superTypeNode = getXPathAPI().selectSingleNode(classNode, superTypePath);
			
			if (superTypeNode != null) {
				
				// get the general attribute value
				superTypeNode = locateChildNodeByName(superTypeNode, "general");
				
				// locate the class by the general attribute's value
				superTypeNode = getClassNode(superTypeNode.getNodeValue());										
				Node nameNode = this.locateChildNodeByName(superTypeNode, "name");

				superTypes.add(nameNode.getNodeValue());
			}
			else {
				final String msg = "No genernalization found for " + className;
				LOGGER.warning(msg);
			}

		}
		catch (Exception exc) {
			final String msg = errMsgPrefix + className;
			LOGGER.log(Level.WARNING, msg, exc);
		}

		// looking for interfaces
		try {							   			
			String interfacePath 	= ".//interfaceRealization";			
			Node supplierNode 		= null;
			NodeIterator nl 		= getXPathAPI().selectNodeIterator(classNode, interfacePath);
			

			while ((supplierNode = nl.nextNode()) != null)
			{
				supplierNode = getXPathAPI().selectSingleNode( supplierNode, ".//@supplier" );

				if (supplierNode != null)
				{
					supplierNode = getInterfaceNode(supplierNode.getNodeValue());
					
					if (supplierNode != null)
					{
						Node nameNode = this.locateChildNodeByName(supplierNode, "name");
						superTypes.add(nameNode.getNodeValue());						
						final String msg = "Located supertype interface = " + nameNode.getNodeValue();
						LOGGER.info(msg);

					}
					else {
						final String msg = "Failed to discover the class node for interface realization supplier " + supplierNode;
						LOGGER.warning(msg);
					}
				}
			}
		}
		catch (Exception exc) {
			final String msg = errMsgPrefix + className;
			LOGGER.log(Level.WARNING, msg, exc);
		}
		
		return superTypes;
	}

	/**
	 * Returns the name association with the node
	 * @return	String
	 */
	public String getElementName(Node node) {
		String name = "unresolved name";
		try {
			Node n = locateChildNodeByName(node, "name");
			if (n != null)
				name = n.getNodeValue();
				
			if ( name == null ) {
				final String msg = "Name value is null for node " + node;			
				LOGGER.warning(msg);
			}
		}
		catch (Exception exc) {
			final String msg = "Failed to acquire the name value for node " + node;
			LOGGER.log(Level.WARNING, msg, exc);
		}
		return name;
	}

	/**
	 * Returns the name of the element referenced by the node and it's XMI ID
	 * 
	 * @return String
	 */
	public String getElementNameforID(String xmiID, Node node) {
		String idPath 		= "*[@xmi:id='" + xmiID + "']";
		String name 		= null;
		try {
			Node n = getXPathAPI().selectSingleNode(node, idPath);
			if (n != null)
				name = getElementName(n);
			else {
				final String msg = "No Node affiliated with " + xmiID + " and node " + getNodeID(node);
				LOGGER.log(Level.WARNING, msg);
			}
		}
		catch (Exception exc) {
			final String msg = "Failed to get an element id using xmiID " + xmiID + " and node " + getNodeID(node);
			LOGGER.log(Level.WARNING, msg, exc);
		}
		return name;
	}

	/**
	 * Locates the  AssociationEndObject of the provided associationEndNode.
	 * 
	 * @return AssociationEndObject
	 */ 
	public AssociationEndObject getAssociationSibling(Node inputAssociationNode) {
		AssociationEndObject associationEndObject	= null;
		String errMsgPrefix							= "Failed to locate sibling Node for ";
		
		try {
			// locate the owned element that shares the same 'association' attribute value
			String associationId		= locateChildNodeByName(inputAssociationNode, "association" ).getNodeValue();
			String associationEndPath	= ".//ownedAttribute[@association='" + associationId + "']";
			NodeIterator nl 			= getXPathAPI()
											.selectNodeIterator(XMIParser.xmiDocument(), 
												associationEndPath);
			Node associationEndNode						= null;

			while ((associationEndNode = nl.nextNode()) != null) {
				// the sibling is the other one
				String inputXMIID 	= locateChildNodeByName(inputAssociationNode,XMIID).getNodeValue();
				String compareXMIID = locateChildNodeByName(associationEndNode,XMIID).getNodeValue();
				
				if (!inputXMIID.equalsIgnoreCase( compareXMIID))
					associationEndObject = AssociationFactory.getInstance().createInstance(associationEndNode, false);
			}
		}
		catch (Exception exc) {
			final String msg = errMsgPrefix + getElementName(inputAssociationNode);
			LOGGER.log(Level.WARNING, msg, exc);
		}
		return associationEndObject;
	}

	/**
	 * Returns the type of the assocationNode.
	 * 
	 * @return 	String
	 */
	public String getAssociationEndType(Node associationNode) {
		String type = null;
		try {
			String associationEndPath 	= ".//@type";
			Node classNode 				= getXPathAPI().selectSingleNode(associationNode, associationEndPath);
			
			if (classNode != null) {
				String classID 	= classNode.getNodeValue();
				classNode 		= getClassNode(classID);

				if (classNode != null) {
					Node nameNode = locateChildNodeByName(classNode, "name");
				
					if (nameNode != null) {
						type = nameNode.getNodeValue();
						final String msg = "XMIProvider12.getAssociationEndType() is " + type;
						LOGGER.log(Level.WARNING, msg);
					}
				}
			}
		}
		catch (Exception exc) {
			final String msg = "Failed to determine end type for association " + getElementName(associationNode);
			LOGGER.log(Level.WARNING, msg, exc);
		}
		return type;
	}

	/**
	 * Returns the role name of the assocationNode.
	 * 
	 * @return 	String
	 */	
	public String getAssociationRoleName(Node associationNode) {
		String roleName = "none";
		
		try {
			final Node nameNode = locateChildNodeByName(associationNode, "name");
			
			if (nameNode != null)
				roleName = nameNode.getNodeValue();
		}
		catch (Exception exc) {
			final String msg = "Failed to determine role name for association " + getElementName(associationNode);
			LOGGER.log(Level.WARNING, msg, exc);
		}
		
		return Utils.removeSpaces(roleName);
	}

	/**
	 * Returns the upper and lower range values for the provided association node.
	 * 
	 * Returns an int array, [lowerRange,upperRange].
	 * 
	 * @return	int[]
	 */
	public int[] getLowerUpperRange(Node associationNode) {
		int lowerRange = 1;
		int upperRange = 1;
		
		try {
			Node lowerRangeNode = getXPathAPI().selectSingleNode(associationNode, ".//lowerValue");
			Node upperRangeNode = getXPathAPI().selectSingleNode(associationNode, ".//upperValue");
			
			lowerRange = determineLowerRange( lowerRangeNode, associationNode );
			upperRange = determineUpperRange( upperRangeNode, lowerRange );
		}
		catch (Exception exc) {
			final String msg = "Failed to determine range for node " + getElementName(associationNode);
			LOGGER.log(Level.WARNING, msg, exc);
		}
		
		// package up as a 2 element array
		int [] range = new int[2];
		range[0] = lowerRange;
		range[1] = upperRange;
		return range;
	}

	/**
	 * Returns true/false indicating a composition indicator.  In this case, it is a
	 * child node with the name aggregation with a value of composite.
	 * 
	 * @return	boolean
	 */
	public boolean getCompositeIndicator(Node associationNode) {
		boolean isComposite = false;
		
		try {
			Node n = locateChildNodeByName(associationNode, "aggregation");
			
			if (n != null) {
				String value = n.getNodeValue();
			
				if (value.equalsIgnoreCase("composite"))
					isComposite = true;
			}
		}
		catch (Exception exc) {
			final String msg = "Failed during locating of composite indicator for " + getElementName(associationNode);
			LOGGER.log(Level.WARNING, msg, exc);
		}
		return isComposite;
	}

	/**
	 * Returns true/false indicating a composition indicator.  In this case, it is a
	 * child node with the name aggregation with a value of aggregation.
	 * 
	 * @return	boolean
	 */
	public boolean getAggregationIndicator(Node associationNode) {
		boolean isAggregate = false;
		
		try {
			Node n = locateChildNodeByName(associationNode, "aggregation");
			if (n != null) {
				String value = n.getNodeValue();
				if (value.equalsIgnoreCase("aggregate"))
					isAggregate = true;
			}
		}
		catch (Exception exc) {
			final String msg = "Failed during locating of aggregation indicator for " + getElementName(associationNode);
			LOGGER.log(Level.WARNING, msg, exc);
		}
		return isAggregate;
	}

	/**
	 * Returns true/false indicator if this association node is navigable.
	 * 
	 * @return boolean
	 */
	public boolean getNavigableIndicator(Node associationNode) {
		return true;
	}

	/**
	 * Returns true/false indicator if this association node is ordered.
	 * 
	 * @return boolean
	 */
	public boolean getOrderedIndicator(Node associationNode) {
		boolean isOrdered = false;
		
		try {
			Node n = locateChildNodeByName(associationNode, "ordered");
			
			if (n != null) {
				String value = n.getNodeValue();
				
				if (value.equalsIgnoreCase("true"))
					isOrdered = true;
			}
		}
		catch (Exception exc) {
			final String msg = "Failed during locating of ordered indicator for " + getElementName(associationNode); 
			LOGGER.log(Level.INFO, msg, exc);
		}
		
		return isOrdered;
	}

	/**
	 * Returns the visibility value for the provided method node.
	 * 
	 * @return	String
	 */
	public String getVisibility(Node methodNode) {
		String visibility = "public";
		
		try {
			Node n = locateChildNodeByName(methodNode, "visibility");
		
			if (n != null) {
				LOGGER.log( Level.INFO, "XMIProvider12.getVisibility(Node) - visibility is " + n.getNodeValue());
				visibility = n.getNodeValue();
			} 
			else {
				final String msg = "Method visibility not specified for node " + getElementName(methodNode) + ". Defaulting to public";
				LOGGER.info(msg);
			}
		}
		catch (Exception exc) {
			final String msg = "Failed during locating of visiblity value for " + getElementName(methodNode); 
			LOGGER.log(Level.INFO, msg, exc);
		}

		return visibility;
	}

	/**
	 * Returns the documentation for the provided node
	 * 
	 * @return	String
	 */
	public String getDocumentation(Node node) {
		String doc 			= null;
		String documentPath = "body";
		
		try {
			Node documentNode = locateChildNodeByName(node, documentPath);
		
			if (documentNode != null) 
				doc = documentNode.getFirstChild().getNodeValue();			
		}
		catch (Exception exc) {
			final String msg = "Failed to acquire document for node " + getElementName(node);
			LOGGER.log(Level.WARNING,  msg, exc);
		}
		return doc;
	}

	/**
     * Named convenience method that delegates internally to getStaticIndicator(Node).
	 * 
	 * @return String
	 */
	public boolean isMethodStatic(Node methodNode) {
		return this.getStaticIndicator(methodNode);
	}

	/**
	 * Returns the return and input args for the provided method node.
	 * 
	 * return MethodArgs
	 */
	public MethodArgs getMethodArguments(Node methodNode) {
		MethodArgs methodArgs	= new MethodArgs();
		String parameterPath 	= ".//ownedParameter";
		
		try {
			NodeIterator nl 	= getXPathAPI().selectNodeIterator(methodNode,parameterPath);
			Node parameterNode	= null;
			
			// loop through all the applicable arguments...includes the return arg as well
			while ((parameterNode = nl.nextNode()) != null) {
				Node returnNode			= locateChildNodeByName(parameterNode, "direction");
				
				// handle return node
				if ( returnNode != null && returnNode.getNodeValue().equalsIgnoreCase("return") ) {
					String returnVal 	= getNodeTypeByXMIId(parameterNode);
					methodArgs.setReturnType( returnVal );
				}
				else { // handle as an in parameter
					String parameterType 	= getNodeTypeByXMIId(parameterNode);
					String parameterName 	= locateChildNodeByName(parameterNode, "name").getNodeValue();
					
					methodArgs.getArgs().add(new MethodArg(parameterName,parameterType));
				}
			}
			final String msg = "Method Args for class node " + getElementName(methodNode) 
				+ " are: return type - " + methodArgs.getReturnType() 
				+ " input args " + methodArgs.getArgs();
			LOGGER.info(msg);
		}
		catch (Exception exc) {
			final String msg = "Failed to determine the return and input args for method " + getElementName(methodNode);
			LOGGER.log(Level.WARNING, msg, exc);
		}
		return methodArgs;
	}

	/**
	 * Named convenience method that delegates internally to the getNodeType(Node)
	 * 
	 * @return String
	 */
	public String getAttributeType(Node attributeNode) {
		// enums are treated as classes and therefor have attributes, however
		// their attributes are considered Strings
		if ( !attributeNode.getNodeName().equalsIgnoreCase( "ownedLiteral" ) )
				return getNodeType(attributeNode);
		else
			return( "String" );
	}

	/**
	 * Returns true/false indicating if the node is defined as static.
	 */
	public boolean getStaticIndicator(Node attributeNode) {
		boolean isStatic 	= false;
		Node n 				= locateChildNodeByName(attributeNode, "isStatic");
		
		if (n != null)
			isStatic = n.getNodeValue().equalsIgnoreCase("true");
		
		return isStatic;
	}

	/** 
	 * Return true/false indicating if the node is declared as final.
	 */
	public boolean getFinalIndicator(Node node) {
		boolean finalIndicator 	= false;
		Node finalValue 		= locateChildNodeByName(node, "isReadOnly");
		
		if ( finalValue != null && finalValue.getNodeValue().equalsIgnoreCase("true") )
			finalIndicator = true;
			
		return finalIndicator;
	}

	/**
	 * A naming convenience method that delegates internally to getFinalIndicator(Node).
	 * 
	 * @return boolean
	 */
	public boolean getFinalIndicatorForAssociation(Node associationNode) {
		return getFinalIndicator(associationNode);
	}

	/** 
	 * Return true/false indicating if the node is declared as addOnly.
	 * 
	 */
	public boolean getAddOnlyIndicatorForAssociation(Node associationNode) {
		return false;
	}

	/**
	 * Forced to false
	 * 
	 * @param 	Node
	 * @return`	boolean
	 */
	public boolean getTransientIndicator(Node attributeNode) {
		return false;
	}

	/**
	 * Forced to false
	 * 
	 * @param 	Node
	 * @return`	boolean
	 */	
	public boolean getVolatileIndicator(Node attributeNode) {
		return false;
	}

	/**
	 * Returns the default value for an attribute.
	 * 
	 * @param	Node
	 * @return	String
	 */
	public String getDefaultValue(Node attributeNode) {
		return null;
	}

	/**
	 * Determine if the attribute is required.
	 * 
	 * For goFramework model specification, any attribute node having a stereotype labeled 'oid' 
	 * is considered required.
	 * 
	 * This is for backwards compatibility and as of v7.x, this field ignored during model processing.
	 * 
	 * @return boolean
	 */
	public boolean getRequiredIndicator(Node attributeNode) {

		boolean required 	= false;
		String oid 			= this.findStereotype(attributeNode);
		
		try {
			if (oid != null && oid.toLowerCase().equalsIgnoreCase("oid")) {
				required = true;
				final String msg = "OID attribute discovered for node " + getElementName(attributeNode);
				LOGGER.info(msg);
			}
		}
		catch (Exception exc) {
			final String msg = "Failed to determine the required indicator for " + getElementName(attributeNode);
			LOGGER.log(Level.WARNING, msg, exc);
		}

		return required;

	}

	/**
	 * Determine if the attribute is nullable.
	 * 
	 * @return boolean
	 */
	public boolean getNullIndicator(Node attributeNode) {
		return true;
	}

	/**
	 * Force to false.
	 * 
	 * @param	Node
	 * @return	boolean
	 */
	public boolean getIntrinsicTypeIndicator(Node attributeNode) {
		return false;
	}

	/**
	 * Returns the id for the provided node.
	 * 
	 * @param node
	 * @return
	 */
	private String getNodeID(Node node) {
		String id 	= null;
		Node idNode = null;
		
		try {
			idNode = locateChildNodeByName(node, XMIID);
			
			if (idNode != null)
				id = idNode.getNodeValue();
			else
				LOGGER.info("Node located for the provided node.");
		}
		catch (Exception exc) {
			final String msg = "Failed to locate the id for node " + node; 
			LOGGER.log(Level.WARNING, msg, exc);
		}
		return id;
	}

	/**
	 * Return the type of the provided node.
	 * 
	 * @param node
	 * @return String
	 */
	private String getNodeType(Node node) {
		return getNodeTypeByXMIId(node);
	}
	
	/**
	 * A locator method to help find an Interface Node by it's unique identifier.
	 * 
	 * @param interfaceId
	 * @return Node
	 */	
	private Node getInterfaceNode(String interfaceId)
	{
		String interfacePath 	= ".//packagedElement[@xmi:id='" + interfaceId + "']";
		Node interfaceNode		= null;

		try {
			interfaceNode = getXPathAPI().selectSingleNode(XMIParser.xmiDocument(),interfacePath);
		}
		catch (Exception exc) {
			final String msg = "Failed to locate an interface node using the node id " + interfaceId;
			LOGGER.log(Level.WARNING, msg, exc);
		}
		return interfaceNode;
	}

	/**
	 * A locator method to help find a Class Node by it's unique identifier.
	 * 
	 * @param classID
	 * @return Node
	 */
	private Node getClassNode(String classID) {
		String classPath 	= ".//packagedElement[@xmi:id='" + classID + "']";
		Node classNode 		= null;
		
		try {
			classNode = getXPathAPI().selectSingleNode(XMIParser.xmiDocument(),classPath);
		}
		catch (Exception exc) {
			final String msg = "Failed to locate a class node using the class id " + classID;
			LOGGER.log(Level.WARNING, msg, exc);
		}
		return classNode;
	}

	/**
	 * Helper method to determine the lowerRange of the association node.
	 * 
	 * @param lowerNode
	 * @param associationNode
	 * @return
	 */
	private int determineLowerRange( Node lowerNode, Node associationNode ) {
		int lowerRange = 0;
		
		if (lowerNode == null) {
			lowerRange = 1;
			final String msg = getAssociationRoleName(associationNode) 
					+ " of " + getAssociationEndType(associationNode) 
					+ " has no lower range specified - assigning 1";
			LOGGER.log(Level.WARNING, msg );
		}
		else {
			String lowerValue = lowerNode.getNodeValue();
			if ( lowerValue == null )
				lowerRange = 0;
			else if ( lowerValue.equalsIgnoreCase("*") )
				lowerRange = -1;
			else
				lowerRange = Integer.parseInt(lowerValue);			
		}
		return lowerRange;
	}

	/**
	 * Helper method to determine the upperRange of the association node.
	 * 
	 * @param upperNode
	 * @param lowerRange
	 * @return
	 */
	private int determineUpperRange( Node upperNode, int lowerRange ) {

		int upperRange = 1;
		
		if (upperNode == null) {
			if (lowerRange <= 1)
				upperRange = 1;
			else
				upperRange = -1;
		}
		else {
			String upperValue = upperNode.getNodeValue();
			upperRange = upperValue != null && !upperValue.equals("*") && !upperValue.equals("n") ? Integer.parseInt(upperValue) : -1;
		}
		
		return upperRange;
	}
	
	private String getNodeTypeByXMIId(Node node) {
		String type		= null;
		String typeRef 	= null;
		Node n 			= locateChildNodeByName(node, "type");

		if (n != null)
			typeRef = n.getNodeValue();

		if (typeRef != null) {
			String typePath = "//packagedElement[@xmi:id ='" + typeRef + "']";
			try {
				n = getXPathAPI().selectSingleNode(XMIParser.xmiDocument(),typePath);
				
				if (n != null) {
					n = locateChildNodeByName(n, "name");
					if (n != null) {
						type = n.getNodeValue();
					}
					else {
						final String msg = "Unable to find a child node by the name " + type + " on node " + n; 
						LOGGER.warning(msg);
					}
				}
			} 
			catch (Exception exc) {
				final String msg = "Failed to locate type for " + n;
				LOGGER.log(Level.WARNING, msg, exc);
			}
		}
		
		return type;
	}

	private String findStereotypeByXMIId(String stereoTypeRef) {
		String stereotype 			= null;
		Node n						= null;
		final String errMsgPrefix	= "Failed to find a stereotype for ";
		final String stereoTypePath = "//ownedStereotype[@xmi:id ='" + stereoTypeRef + "']";
		
		try {
			n = getXPathAPI().selectSingleNode(XMIParser.xmiDocument(), stereoTypePath);
			if (n != null) {
				n = locateChildNodeByName(n, "name");
				if (n != null)
					stereotype = n.getNodeValue();
				else {
					final String msg = errMsgPrefix + getElementName(n);	
					LOGGER.warning(msg);
				}
			}
		}
		catch (Exception exc) {
			final String msg = errMsgPrefix + getElementName(n);
			LOGGER.log(Level.WARNING, msg, exc);
		}
		
		return( stereotype );
	}
	
	// attributes
	private static final String XMIID					 	= "xmi:id";
	private static final String PARENT_MSG_PREFIX 			= "Parent ";
	private static final String MAX_XMI_VERSION_SUPPORTED 	= "2.1";
	private static final Logger LOGGER 						= Logger.getLogger(XMIProvider21.class.getName());

}
