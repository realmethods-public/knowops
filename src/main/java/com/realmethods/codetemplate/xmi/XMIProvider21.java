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

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;

import com.realmethods.codetemplate.model.attribute.AttributeObject;
import com.realmethods.codetemplate.model.association.AssociationEndObject;
import com.realmethods.codetemplate.model.classes.ClassObject;
import com.realmethods.codetemplate.model.factory.AssociationFactory;
import com.realmethods.codetemplate.model.factory.AttributeFactory;
import com.realmethods.codetemplate.model.factory.ClassFactory;
import com.realmethods.codetemplate.model.method.MethodArg;
import com.realmethods.codetemplate.model.method.MethodArgs;
import com.realmethods.codetemplate.parser.XMIParser;

/**
 * XMI UML 2.0 model handler for turning DOM Nodes and elements into realMethods AppGen Model.
 * 
 * @author realMethods, Inc.
 */
public class XMIProvider21 extends XMIProvider12
{
	/**
	 * default constructor
	 */
	public XMIProvider21() {
		super();
		// intentionally empty
	}

	@Override
	/**
	 * Max version this instance can support
	 * 
	 * @return String
	 */
	public String getXMIVersion() {
		return MAX_XMI_VERSION_SUPPORTED;
	}

	@Override
	/**
	 * Return the call to obtain DOM Element that represents the Node Declaration.
	 * 
	 * @return String
	 */
	public String getNodeXMIDecl() {
		return null;
	}

	@Override
	/**
	 * Return the call to obtain DOM Element that represents the Component Declaration.
	 * 
	 * @return String
	 */
	public String getComponentXMIDecl() {
		return null;
	}

	@Override
	/**
	 * Return the call to obtain DOM Element that represents the Class Declaration.
	 * 
	 * @return String
	 */
	public String getClassXMIDecl() {
		return ".//packagedElement[@xmi:type='uml:Class']";
	}

	@Override
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

    @Override
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

	@Override
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


	@Override
	/**
	 * Returns the tag names and values of an association node as a Map.
	 * 
	 * @return Map<String, String>
	 */
	public Map<String, String> getTagNamesAndValues(Node associationNode) {
		return new HashMap<>();
	}

	@Override
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


	@Override
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

	@Override
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

	@Override
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

	@Override
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
			upperRange = determineUpperRange( upperRangeNode, associationNode, lowerRange );
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


	@Override
	/**
	 * Returns true/false indicator if this association node is navigable.
	 * 
	 * @return boolean
	 */
	public boolean getNavigableIndicator(Node associationNode) {
		return true;
	}

	@Override
	/**
     * Named convenience method that delegates internally to getStaticIndicator(Node).
	 * 
	 * @return String
	 */
	public boolean isMethodStatic(Node methodNode) {
		return this.getStaticIndicator(methodNode);
	}

	@Override
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

	@Override
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

	@Override
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

	@Override
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


	@Override
	/** 
	 * Return true/false indicating if the node is declared as addOnly.
	 * 
	 */
	public boolean getAddOnlyIndicatorForAssociation(Node associationNode) {
		return false;
	}

	@Override
	/**
	 * Returns the default value for an attribute.  Forced to null for now
	 * 
	 * @param	Node
	 * @return	String
	 */
	public String getDefaultValue(Node attributeNode) {
		return null;
	}


	@Override
	/**
	 * Determine if the attribute is nullable.
	 * 
	 * @return boolean
	 */
	public boolean getNullIndicator(Node attributeNode) {
		return true;
	}


	@Override
	/**
	 * Returns the id for the provided node.
	 * 
	 * @param node
	 * @return
	 */
	protected String getNodeID(Node node) {
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

	@Override
	/**
	 * Return the type of the provided node.
	 * 
	 * @param node
	 * @return String
	 */
	protected String getNodeType(Node node) {
		return getNodeTypeByXMIId(node);
	}
	
	@Override
	/**
	 * Helper method to determine the lowerRange of the association node.
	 * 
	 * @param lowerNode
	 * @param associationNode
	 * @return
	 */
	protected int determineLowerRange( Node lowerNode, Node associationNode ) {
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

	@Override
	/**
	 * Helper method to determine the upperRange of the association node.
	 * 
	 * @param upperNode
	 * @param associationNode
	 * @param lowerRange
	 * @return
	 */
	protected int determineUpperRange( Node upperNode, Node associationNode, int lowerRange ) {

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
	
	@Override
	protected String getOperationDecl() {
		return OPERATION_DECL;
	}

	@Override
	protected String getPackageDecl() {
		return PACKAGE_DECL;
	}
	
	@Override
	protected String getElementIdPath(String xmiID) {
		return 	"*[@xmi:id='" + xmiID + "']";
	}

	@Override
	protected Node getClassNodeFromAssociationNode(Node associationNode) { 
		String associationEndPath 	= ".//@type";
		Node classNode 				= null;
		
		try {
			classNode = getXPathAPI().selectSingleNode(associationNode, associationEndPath);
		} catch( Exception exc ) {
			LOGGER.log( Level.WARNING, "Failed to locate a class node for a given association node", exc);
		}		
		return ( classNode );	
	}

	@Override
	protected String getDocumentPath() {
		return DOCUMENT_PATH;
	}

	@Override
	protected String getStereoTypePath( String stereoTypeRef ) {
	    return "//ownedStereotype[@xmi:id ='" + stereoTypeRef + "']";	
	}

	@Override
	protected String getClassPath( String classID ) {
		return 	".//packagedElement[@xmi:id='" + classID + "']";
	}

	@Override
	protected String getInterfacePath( String interfaceId ) {
		return ".//packagedElement[@xmi:id='" + interfaceId + "']";
	}
	
	@Override
	protected String getDataTypePath( String typeRef ) {
		return "//packagedElement[@xmi:id ='" + typeRef + "']";
	}


	// attributes
	private static final String XMIID					 	= "xmi:id";
	private static final String PARENT_MSG_PREFIX 			= "Parent ";
	private static final String MAX_XMI_VERSION_SUPPORTED 	= "2.1";
	private static final String OPERATION_DECL				= "*/UML:Operation"; 
	private static final String PACKAGE_DECL				= "packagedElement";
	private static final String DOCUMENT_PATH				= "body";	
	private static final Logger LOGGER 						= Logger.getLogger(XMIProvider21.class.getName());

}
