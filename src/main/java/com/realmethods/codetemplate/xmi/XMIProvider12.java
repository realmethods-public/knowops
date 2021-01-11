/* realMethods Confidential
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
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeIterator;

import com.realmethods.codetemplate.model.attribute.AttributeObject;
import com.realmethods.codetemplate.model.association.AssociationEndObject;
import com.realmethods.codetemplate.model.classes.ClassObject;
import com.realmethods.codetemplate.model.component.ComponentObject;
import com.realmethods.codetemplate.model.subsystem.SubsystemObject;
import com.realmethods.codetemplate.model.factory.AssociationFactory;
import com.realmethods.codetemplate.model.factory.AttributeFactory;
import com.realmethods.codetemplate.model.factory.ClassFactory;
import com.realmethods.codetemplate.model.factory.ComponentFactory;
import com.realmethods.codetemplate.model.factory.MethodFactory;
import com.realmethods.codetemplate.model.factory.SubsystemFactory;
import com.realmethods.codetemplate.model.method.MethodArg;
import com.realmethods.codetemplate.model.method.MethodArgs;
import com.realmethods.codetemplate.model.method.MethodObject;
import com.realmethods.codetemplate.parser.XMIParser;
import com.realmethods.common.helpers.Utils;

/**
 * XMI 1.2 model handler for turning DOM Nodes and elements into realMethods AppGen Model.
 * 
 * @author realMethods, Inc.
 */
public class XMIProvider12 extends XMIProvider
{
	/**
	 * default constructor
	 */
	public XMIProvider12() {
		// intentionally empty
	}

	/**
	 * Max version this instance can support
	 * 
	 * @return String
	 */
	public String getXMIVersion()
	{
		return MAX_XMI_VERSION_SUPPORTED;
	}

	/**
	 * Return the call to obtain DOM Element that represents the Node Declaration.
	 * 
	 * @return String
	 */
	public String getNodeXMIDecl()
	{
		return "/XMI/XMI.content/UML:Model/UML:Namespace.ownedElement/UML:Node[@xmi.id]";
	}

	/**
	 * Return the call to obtain DOM Element that represents the Component Declaration.
	 * 
	 * @return String
	 */
	public String getComponentXMIDecl()
	{
		return "//UML:Model/UML:Namespace.ownedElement/UML:Component[@xmi.id]";
	}

	/**
	 * Return the call to obtain DOM Element that represents the Class Declaration.
	 * 
	 * @return String
	 */
	public String getClassXMIDecl()
	{
		return "//UML:Model/UML:Namespace.ownedElement/UML:Class[@xmi.id]";
	}

	/**
	 * Return the call to obtain DOM Element that represents the Interface Declaration.
	 * 
	 * @return String
	 */
	public String getInterfaceXMIDecl()
	{
		return "//UML:Interface[@xmi.id]";
	}

	/**
	 * Searches for subsystem nodes under the parentNode and returns them as a List<SubsystemObject>
	 * 
	 * @return	List<SubsystemObject>
	 */
    @Override
	public List<SubsystemObject> findSubsystems(Node parentNode)
    {
		String subSystemPath 					= "*/UML:Node";	
		List<SubsystemObject> subsystemObjects 	= new ArrayList<>();
		Node subSystemNode 						= null;

		try
		{
			NodeList nl 					= getXPathAPI().selectNodeList(parentNode, subSystemPath);
			SubsystemObject subystemObject	= null;

			for ( int index = 0; index < nl.getLength(); index++ )
			{	
				subSystemNode = nl.item(index);
				LOGGER.info(PARENT_MSG_PREFIX+ getElementName(parentNode) + " contains sub system : " + getElementName(subSystemNode));
				
				subystemObject = SubsystemFactory.getInstance().createSubsystemObject( subSystemNode );
				subsystemObjects.add(subystemObject);
			}
		}
		catch (Exception exc)
		{
			final String msg = "Error creating a SubsytemObject instance for Node " + getElementName(subSystemNode);
			LOGGER.log(Level.WARNING,
						msg,
						exc);
		} 
		return subsystemObjects;
    }

	/**
	 * Searches for component nodes under the parentNode and returns them as a List<ComponentObject>
	 * 
	 * @return	String
	 */
	@Override
    public List<ComponentObject> findComponents(Node parentNode)
    {
		String classPath 					= "*/UML:Component";	
		List<ComponentObject> components 	= new ArrayList<>();
		Node componentNode 					= null;
		
		try
		{
			NodeIterator nl 					= getXPathAPI().selectNodeIterator(parentNode, classPath);			
			ComponentObject componentObject 	= null;

			while ((componentNode = nl.nextNode()) != null)
			{
				LOGGER.info(PARENT_MSG_PREFIX+ getElementName(parentNode) + " contains component: " + getElementName(componentNode));
				
				componentObject = ComponentFactory.getInstance().createComponentObject( componentNode );
				components.add(componentObject);
			}
		}
		catch (Exception exc)
		{
			final String msg = "Error creating a ComponentObject instance for Node " + getElementName(componentNode);

			LOGGER.log(Level.WARNING,
					msg,
					exc);
		}
		return components;
    }

	/**
	 * Searches for class nodes under the parentNode and returns them as a List<ClassObject>
	 * 
	 * @return	List<ClassObject>
	 */
    @Override
    public List<ClassObject> findClasses(Node parentNode)
    {
		String classPath 			= "*/UML:Class";	
		List<ClassObject> classes 	= new ArrayList<>();
		Node classNode	 			= null;

		try
		{
			NodeIterator nl 			= getXPathAPI().selectNodeIterator(parentNode, classPath);			
			ClassObject classdObject 	= null;

			while ((classNode = nl.nextNode()) != null)
			{
				LOGGER.info(PARENT_MSG_PREFIX+ getElementName(parentNode) + " contains component: " + getElementName(classNode));

				classdObject = ClassFactory.getInstance().createClassObject( classNode);
				classes.add(classdObject);
			}
		}
		catch (Exception exc)
		{
			final String msg = "Error creating a ClassObject instance for Node " + getElementName(classNode);

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
	public List<AttributeObject> findAttributes(ClassObject classObject, Node classNode)
	{
		String attributePath 				= "*/UML:Attribute ";
		List<AttributeObject> attributes 	= new ArrayList<>();

		try
		{
			NodeIterator nl 	= getXPathAPI().selectNodeIterator(classNode, attributePath);
			Node attributeNode	= null;
			
			while ((attributeNode = nl.nextNode()) != null) {
					attributes.add(AttributeFactory.getInstance().createInstance(classObject, attributeNode));
			}
		}
		catch (Exception exc)
		{
			final String msg = "Error creating a AttributeObject instance for Node " + getElementName(classNode);

			LOGGER.log(Level.WARNING,
					msg,
					exc);
		}
		return attributes;
	}

	/**
     * Searches for the method nodes of the classNode and creates a MethodObject
     * for each and returns the entire list.
     * 
     * @return List<MethodObject>
     */	
    public List<MethodObject> findMethods(Node classNode)
	{
		String methodPath 			= getOperationDecl();
		List<MethodObject> methods 	= new ArrayList<>();

		try
		{
			NodeIterator nl = getXPathAPI().selectNodeIterator(classNode, methodPath);
			Node methodNode = null;
			MethodObject methodObject = null;
			
			while ((methodNode = nl.nextNode()) != null)
			{
				methodObject = MethodFactory.getInstance().createInstance(methodNode);
				methods.add(methodObject);
				final String msg = "For class:" + getElementName(classNode) + ", method discovered: " + methodObject.toString();
				LOGGER.log( Level.INFO, msg);
			}
		}
		catch (Exception exc)
		{
			final String msg = "Error creating a MethodObject instance for Node " + getElementName(classNode);
			LOGGER.log(Level.WARNING,
					msg,
					exc);
		}
		return methods;
	}

	/**-
     * Searches for the association nodes of the classNode and creates a AssociationEndObject
     * for each and returns the entire list.
     * 
     * @return List<AssociationEndObject>
     */	
	public List<AssociationEndObject> findAssociations(Node classNode) {
		List<AssociationEndObject> associations = new ArrayList<>();

		try {
			String associationEndPath 					= ".//UML:AssociationEnd/UML:AssociationEnd.participant/UML:Class[@xmi.idref='"
															+ getNodeID(classNode) + "']";

			String siblingAssociationEndPath 			= "preceding-sibling::UML:AssociationEnd | "
															+ "following-sibling::UML:AssociationEnd";
			NodeIterator nl 							= getXPathAPI()
														.selectNodeIterator(XMIParser.xmiDocument(), associationEndPath);
			Node associationEndNode 					= null;
			Node siblingAssociationEndNode 				= null;
			AssociationEndObject associationEndObject 	= null;

			while ((associationEndNode = nl.nextNode()) != null) {
				// back it up...
				associationEndNode 			= associationEndNode.getParentNode().getParentNode();
				siblingAssociationEndNode 	= getXPathAPI().
												selectSingleNode( associationEndNode, siblingAssociationEndPath);
				
				if (siblingAssociationEndNode != null) {
					associationEndObject = AssociationFactory.getInstance().createInstance((Node)siblingAssociationEndNode, true);
					associations.add(associationEndObject);
				}
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
	 * Returns the tag names and values of an association node as a Map.
	 * 
	 * @return Map<String, String>
	 */
	public Map<String, String> getTagNamesAndValues(Node associationNode)
	{
		Map<String, String> map 	= new HashMap<>();
		String keyPath  			= DOCUMENT_PATH;
		String valuePath 			= "UML:ModelElement.taggedValue/UML:TaggedValue/UML:TaggedValue.type/UML:TagDefinition";

		try {
			NodeList list	= getXPathAPI().selectNodeList(associationNode, keyPath);
			Node keyNode 	= null;
			Node valueNode 	= null;
			String xmiId	= null;
			
			if (list != null) {
				for (int i = 0; i < list.getLength(); i++) {
					keyNode 	= list.item(i);
					valueNode 	= getXPathAPI().selectSingleNode(associationNode, valuePath);					
					xmiId 		= locateChildNodeByName(valueNode, XMI_ID_REF).getNodeValue();					
					valueNode 	= getXPathAPI().selectSingleNode( XMIParser.xmiDocument(), "//UML:TagDefinition[@xmi.id='" + xmiId + "']" );
					
					if (valueNode != null)
						map.put(keyNode.getNodeValue(), valueNode.getNodeValue());
					else
						LOGGER.warning("XMIProvider12.getAssociationAttributes(Node) - failed to locate name for " + getElementName(keyNode));
				}
			}
		}
		catch (Exception exc) {
			LOGGER.log(Level.WARNING, "XMIProvider12.getAssociationAttributes(Node) for association:" + getElementName(associationNode), exc);
		}
		return map;
	}

	/**
	 * Returns the stereotype for the given node.
	 * 
	 * @return	String
	 */
	public String findStereotype(Node theNode)
	{
		Node n 					= locateChildNodeByName(theNode, "stereotype");
		String stereotype 		= null;
		String stereoTypeRef 	= null;
		
		if (n != null)
			stereoTypeRef = n.getNodeValue();
		
		if (stereoTypeRef != null) {
			stereotype = findStereotypeByXMIId(stereoTypeRef);
		}
		else { // get it a different way
			stereotype = findStereotypeByXMIIdRef(theNode);
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
		String packagePath 			= getPackageDecl();
		String errMsgPrefix			= "Failed to find package on ";
		Node nameNode 				= null;

		try
		{
			for (; parentNode != null; parentNode = parentNode.getParentNode())
			{
				if (parentNode.getNodeName() != null 
						&& parentNode.getNodeName().equalsIgnoreCase(packagePath))
				{
					nameNode = locateChildNodeByName(parentNode, "name");
					if (nameNode != null)
					{
						packageName.insert(0, nameNode.getNodeValue() + ".");
					}
					else {
						final String msg = errMsgPrefix + getElementName(nameNode);
						LOGGER.warning(msg);
					}
				}
			}
		}
		catch (Exception exc)
		{
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
	public List<String> findSuperTypes(Node classNode)
	{
		String classID 					= getID(classNode);
		List<String> superTypes 		= new ArrayList<>();
		String superTypePath 			= ".//UML:Generalization/UML:Generalization.child/*[@xmi.idref='"+ classID + "']";
		NodeIterator nl 				= null;
		String errMsgPrefix				= "Failed to acquire parent classes for class node ";

		try
		{
			// look for generalizations
			Node superTypeNode = getXPathAPI().selectSingleNode(XMIParser.xmiDocument(), superTypePath);
			
			if (superTypeNode != null)
			{
				Node parentNode = getXPathAPI().selectSingleNode( superTypeNode.getParentNode().getParentNode(),
																	"UML:Generalization.parent/*");
				if (parentNode != null)
				{
					parentNode = getClassNode(locateChildNodeByName(parentNode, XMI_ID_REF).getNodeValue());
					if (parentNode != null)
					{
						parentNode = locateChildNodeByName(parentNode, "name");
						superTypes.add(parentNode.getNodeValue());
					}
				}
			}
		}
		catch (Exception exc)
		{
			final String msg = errMsgPrefix + getElementName(classNode);
			LOGGER.log(Level.WARNING, msg, exc);
		}

		try
		{							   
			String interfacePath 	= ".//UML:Abstraction/UML:Dependency.client/*[@xmi.idref='" + classID + "']";			
			Node supplierNode 		= null;
			nl 						= getXPathAPI().selectNodeIterator(XMIParser.xmiDocument(), interfacePath);

			for (Node clientNode = null; (clientNode = nl.nextNode()) != null;)
			{
				supplierNode = getXPathAPI().selectSingleNode( clientNode.getParentNode().getParentNode(),
															"UML:Dependency.supplier/*");

				if (supplierNode != null)
				{
					supplierNode = getInterfaceNode(locateChildNodeByName(supplierNode, XMI_ID_REF).getNodeValue());
					if (supplierNode != null)
					{
						final String msg = "-- located supertype interface = " + supplierNode.getNodeValue();
						LOGGER.info(msg);
						supplierNode = locateChildNodeByName(supplierNode, "name");
						superTypes.add(supplierNode.getNodeValue());
					}
				}
			}
		}
		catch (Exception exc)
		{
			final String msg = errMsgPrefix + getElementName(classNode);
			LOGGER.log(Level.WARNING, msg, exc);
		}
		return superTypes;
	}

	/**
	 * Returns the name association with the node
	 * @return	String
	 */
	public String getElementName(Node node)
	{
		String name = "unresolved name";
		try
		{
			Node n = locateChildNodeByName(node, "name");
			if (n != null)
				name = n.getNodeValue();
				
			if ( name == null ) {
				final String msg = "Name value is null for node " + getNodeID(node);			
				LOGGER.warning(msg);
			}
		}
		catch (Exception exc) {
			final String msg = "Failed to acquire the name value for node " + getNodeID(node);
			LOGGER.log(Level.WARNING, msg, exc);
		}
		return name;
	}

	/**
	 * Returns the name of the element referenced by the node and it's XMI ID
	 * 
	 * @return String
	 */
	public String getElementNameforID(String xmiID, Node node)
	{
		String idPath 		= getElementIdPath(xmiID);
		String name 		= null;
		try
		{
			Node n = getXPathAPI().selectSingleNode(node, idPath);
			if (n != null)
				name = getElementName(n);
			else {
				final String msg = "No Node affiliated with " + xmiID + " and node " + getNodeID(node);
				LOGGER.log(Level.WARNING, msg);
			}
		}
		catch (Exception exc)
		{
			final String msg = "Failed to get an element id using xmiID " + xmiID + " and node " + getNodeID(node);
			LOGGER.log(Level.WARNING, msg, exc);
		}
		return name;
	}

	/**
	 * Locates the sibling AssociationEndObject of the provided associationEndNode.
	 * 
	 * @return AssociationEndObject
	 */ 
	public AssociationEndObject getAssociationSibling(Node associationNode)
	{
		AssociationEndObject associationEndObject	= null;
		String siblingAssociationEndPath 			= "preceding-sibling::UML:AssociationEnd | following-sibling::UML:AssociationEnd";
		String errMsgPrefix							= "Failed to locate sibling Node for ";
		try
		{
			Node siblingAssociationEndNode = getXPathAPI().selectSingleNode(associationNode, siblingAssociationEndPath);
			if (siblingAssociationEndNode != null)
				associationEndObject = AssociationFactory.getInstance().createInstance(siblingAssociationEndNode, false);
			else {
				final String msg = errMsgPrefix + getElementName(associationNode);
				LOGGER.log(Level.WARNING, msg);
			}
		}
		catch (Exception exc)
		{
			final String msg = errMsgPrefix + getElementName(associationNode);
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
			// dig deeper for actual classNode of this associationNode
			Node classNode = getClassNodeFromAssociationNode(associationNode);

			if( classNode != null ) { 
				classNode 	= getClassNode(classNode.getNodeValue());
		
				if (classNode != null)
				{
					Node nameNode = locateChildNodeByName(classNode, "name");
					if (nameNode != null)
					{
						type = nameNode.getNodeValue();
						final String msg = "XMIProvider12.getAssociationEndType() is " + type;
						LOGGER.log(Level.WARNING, msg);
					}
				}
			}
		}
		catch (Exception exc)
		{
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
	public String getAssociationRoleName(Node associationNode)
	{
		String roleName = "none";
		try
		{
			final Node nameNode = locateChildNodeByName(associationNode, "name");
			
			if (nameNode != null)
				roleName = nameNode.getNodeValue();
		}
		catch (Exception exc)
		{
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
	public int[] getLowerUpperRange(Node associationNode)
	{
		int lowerRange = 1;
		int upperRange = 1;
		
		try {
			String multiplicityPath = "UML:AssociationEnd.multiplicity/UML:Multiplicity/UML:Multiplicity.range/UML:MultiplicityRange";
			Node multiplicityNode	= getXPathAPI().selectSingleNode(associationNode, multiplicityPath);

			if (multiplicityNode != null) {
				Node lowerRangeNode = locateChildNodeByName(multiplicityNode,"lower");
				Node upperRangeNode = locateChildNodeByName(multiplicityNode,"upper");
			
				lowerRange = determineLowerRange( lowerRangeNode, associationNode );
				upperRange = determineUpperRange( upperRangeNode, associationNode, lowerRange );
				
			}
			else
			{
				final String msg = "Failed to get multiplicity node for path " + multiplicityPath;
				LOGGER.warning(msg);
			}
		}
		catch (Exception exc)
		{
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
	public boolean getCompositeIndicator(Node associationNode)
	{
		boolean isComposite = false;
		try
		{
			Node n = locateChildNodeByName(associationNode, "aggregation");
			if (n != null)
			{
				String value = n.getNodeValue();
				if (value.equalsIgnoreCase("composite"))
					isComposite = true;
			}
		}
		catch (Exception exc)
		{
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
	public boolean getAggregationIndicator(Node associationNode)
	{
		boolean isAggregate = false;
		try
		{
			Node n = locateChildNodeByName(associationNode, "aggregation");
			if (n != null)
			{
				String value = n.getNodeValue();
				if (value.equalsIgnoreCase("aggregate"))
					isAggregate = true;
			}
		}
		catch (Exception exc)
		{
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
	public boolean getNavigableIndicator(Node associationNode)
	{
		boolean isNavigable = false;
		try {
			Node n = locateChildNodeByName(associationNode, "isNavigable");
			if (n != null) {
				String value = n.getNodeValue();
				if (value.equalsIgnoreCase("true"))
					isNavigable = true;
			}
		}
		catch (Exception exc) {
			
			final String msg = "Failed during locating of navigable indicator for " + getElementName(associationNode); 
			LOGGER.log(Level.INFO, msg, exc);
		}
		return isNavigable;
	}

	/**
	 * Returns true/false indicator if this association node is ordered.
	 * 
	 * @return boolean
	 */
	public boolean getOrderedIndicator(Node associationNode)
	{
		boolean isOrdered = false;
		try
		{
			Node n = locateChildNodeByName(associationNode, "ordered");
			if (n != null)
			{
				String value = n.getNodeValue();
				if (value.equalsIgnoreCase("true"))
					isOrdered = true;
			}
		}
		catch (Exception exc)
		{
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
	public String getVisibility(Node methodNode)
	{
		String visibility = "public";
		try {
			Node n = locateChildNodeByName(methodNode, "visibility");
			if (n != null) {
				LOGGER.log( Level.INFO, "XMIProvider12.getVisibility(Node) - visibility is " + n.getNodeValue());
				visibility = n.getNodeValue();
			} else
			{
				final String msg = "Method visibility not specified for node " + getElementName(methodNode) + "Defaulting to public";
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
	public String getDocumentation(Node node)
	{
		String doc = null;
		String documentPath = getDocumentPath();
		try {
			Node documentNode = getXPathAPI().selectSingleNode(node, documentPath);
			if (documentNode != null) {
				doc = documentNode.getFirstChild().getNodeValue();
			}
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
	public boolean isMethodStatic(Node methodNode)
	{
		return this.getStaticIndicator(methodNode);
	}

	/**
	 * Returns the return and input args for the provided method node.
	 * 
	 * return MethodArgs
	 */
	public MethodArgs getMethodArguments(Node methodNode)
	{
		MethodArgs methodArgs	= new MethodArgs();
		String parameterPath 	= "*/UML:Parameter";
		try {
			NodeIterator nl = getXPathAPI().selectNodeIterator(methodNode,parameterPath);
			
			Node parameterNode = null;
			Node nameNode = null;
			Node classNode = null;
			String parameterName = null;
			
			// loop through all the applicable arguments...includes the return arg as well
			while ((parameterNode = nl.nextNode()) != null) {				
				nameNode = locateChildNodeByName(parameterNode, "name");
				
				if( nameNode == null )
					continue;
				
				parameterName 	= parameterNode.getNodeValue();
				classNode 		= findParameterByClass(parameterNode);

				if ( classNode == null )				
					classNode = findParameterByInterface(parameterNode);
				
				if ( classNode == null )				
					classNode = findParameterByDataType(parameterNode);

				if (classNode != null)
					classNode = locateChildNodeByName(classNode, "name");
				
				methodArgs = determineMethodArgs( classNode, parameterNode, parameterName );
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
	public String getAttributeType(Node attributeNode)
	{
		return getNodeType(attributeNode);
	}

	/**
	 * Returns true/false indicating if the node is defined as static.
	 * 
	 * In XMI 1.2, it is indicated by a child node with the name ownerScope having
	 * a value of 'classifier'.	 
	 */
	public boolean getStaticIndicator(Node attributeNode)
	{
		boolean isStatic = false;
		Node n = locateChildNodeByName(attributeNode, "ownerScope");
		if (n != null)
			isStatic = n.getNodeValue().equalsIgnoreCase("classifier");
		return isStatic;
	}

	/** 
	 * Return true/false indicating if the node is declared as final.
	 * 
	 * In XMI 1.2, it is indicated by a child node with the name changeability having
	 * a value frozen.	 
	 */
	public boolean getFinalIndicator(Node node)
	{
		boolean finalIndicator = false;
		try
		{
			Node n = locateChildNodeByName(node, CHANGE_ABILITY);
			if (n != null)
				finalIndicator = n.getNodeValue().equalsIgnoreCase("frozen");
		}
		catch (Exception exc)
		{
			final String msg = "Failed to determine the final value for node " + getElementName(node);
			LOGGER.log(Level.WARNING, msg, exc);
		}
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
	 * In XMI 1.2, it is indicated by a child node with the name changeability having
	 * a value addOnly.	 
	 */
	public boolean getAddOnlyIndicatorForAssociation(Node associationNode)
	{
		Node changeabilityNode = locateChildNodeByName(associationNode,CHANGE_ABILITY);
		boolean addOnly = false;
		
		if (changeabilityNode != null
				&& changeabilityNode.getNodeValue().equalsIgnoreCase("addOnly"))
			addOnly = true;
		
		return addOnly;
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
	 * In XMI 1.2, that is assigned to the body of child node with the name 'initialValue'.
	 * 
	 * @param	Node
	 * @return	String
	 */
	public String getDefaultValue(Node attributeNode) {
		String defaultValue = null;
		try {
			String attributeDefaultValuePath 	= "UML:Attribute.initialValue/UML:Expression.body";
			Node n 								= getXPathAPI().selectSingleNode(attributeNode,attributeDefaultValuePath);
			
			if (n == null)
				n = getXPathAPI().selectSingleNode(attributeNode,"UML:Attribute.initialValue/UML:Expression/@body");
			
			defaultValue = n == null ? null : n.getNodeValue();
			
			if (defaultValue != null)
				defaultValue = defaultValue.trim();
		}
		catch (Exception exc) {
			LOGGER.info("XMIProvider12.getDefaultValue(Node) for attribute:" + getElementName(attributeNode) + " - " + exc);
		}
		return defaultValue;
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
	public boolean getRequiredIndicator(Node attributeNode)
	{

		boolean required 	= false;
		String oid 			= this.findStereotype(attributeNode);
		
		try
		{
			if (oid != null && oid.toLowerCase().equalsIgnoreCase("oid"))
			{
				required = true;
				final String msg = "OID attribute discovered for node " + getElementName(attributeNode);
				LOGGER.info(msg);
			}
		}
		catch (Exception exc)
		{
			final String msg = "Failed to determine the required indicator for " + getElementName(attributeNode);
			LOGGER.log(Level.WARNING, msg, exc);
		}

		return required;

	}

	/**
	 * Determine if the attribute is nullable.
	 * 
	 * In XMI 1.2, an attribute with a multiplicity lower range value of 0 is considered able
	 * to be null.
	 * 
	 * @return boolean
	 */
	public boolean getNullIndicator(Node attributeNode)
	{
		boolean canBeNull = false;

		try {
			String multiplicityPath = "UML:StructuralFeature.multiplicity/UML:Multiplicity/UML:Multiplicity.range/UML:MultiplicityRange";
			Node multiplicityNode 	= getXPathAPI().selectSingleNode(attributeNode, multiplicityPath);

			if (multiplicityNode != null) {
				Node lowerNode = locateChildNodeByName(multiplicityNode,"lower");

				if (lowerNode != null) {
					int lowerValue = Integer.parseInt(lowerNode.getNodeValue());
					if (lowerValue == 0)
						canBeNull = true;
				}
			}
		}
		catch (Exception exc) {
			final String msg = "Failed to determine the null indicator on " + getElementName(attributeNode);
			LOGGER.log(Level.WARNING, msg, exc);
		}

		return (canBeNull);
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
	protected String getNodeID(Node node)
	{
		String id 	= null;
		Node idNode = null;
		try
		{
			idNode = locateChildNodeByName(node, "xmi.id");
			
			if (idNode != null)
				id = idNode.getNodeValue();
			else
				LOGGER.info("Node located for the provided node.");
		}
		catch (Exception exc)
		{
			final String msg = "Failed to locate the id for node " + getElementName(node); 
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
	protected String getNodeType(Node node) {
		String type 	= getNodeTypeByXMIId(node);		
		
		if (type == null ){
			type = getNodeTypeByXMIIDRef(node);
		}
		return type;
	}

	/**
	 * A locator method to help find a Class Node by it's unique identifier.
	 * 
	 * @param classID
	 * @return Node
	 */
	protected Node getClassNode(String classID) {
		String classPath 	= getClassPath(classID);
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
	 * A locator method to help find an Interface Node by it's unique identifier.
	 * 
	 * @param interfaceId
	 * @return Node
	 */	
	protected Node getInterfaceNode(String interfaceId) {
		String interfacePath 	= getInterfacePath(interfaceId);
		
		Node interfaceNode		= null;

		try {
			interfaceNode = getXPathAPI().selectSingleNode(XMIParser.xmiDocument(), interfacePath);
		}
		catch (Exception exc) {
			final String msg = "Failed to locate an interface node using the node id " + interfaceId;
			LOGGER.log(Level.WARNING, msg, exc);
		}
		return interfaceNode;
		
	}

	/**
	 * A locator method to help find a node using the date type id.
	 * 
	 * @param dataTypeID
	 * @return Node
	 */		
	protected Node getDataTypeNode(String dataTypeID) {
		Node classNode 		= null;
		String classPath 	= ".//UML:DataType[@xmi.id='" + dataTypeID + "']";
		
		try {
			classNode = getXPathAPI().selectSingleNode(XMIParser.xmiDocument(),classPath);
		}
		catch (Exception exc) {
			final String msg = "Failed to locate a date type node using the class id " + dataTypeID;
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
			lowerRange = Integer.parseInt(lowerNode.getNodeValue());
			final String msg = getAssociationRoleName(associationNode) 
				+ " of " + getAssociationEndType(associationNode) 
				+ " lower range is " + lowerRange;
			LOGGER.log(Level.WARNING, msg);
		}
		return lowerRange;
	}

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
			final String msg = 	getAssociationRoleName(associationNode) 
				+ " of " + getAssociationEndType(associationNode) 
				+ " has no upper range specified - assigning to " 
				+ upperRange 
				+ (upperRange != -1 ? "" : " (unlimited)");
			LOGGER.log(Level.WARNING, msg);
		}
		else
		{
			String upperValue = upperNode.getNodeValue();
			upperRange = !upperValue.equals("*") && !upperValue.equals("n") ? Integer.parseInt(upperValue) : -1;
			final String msg =  getAssociationRoleName(associationNode) + " of "
								+ getAssociationEndType(associationNode) 
								+ " upper range is " + upperValue;
			LOGGER.log(Level.INFO, msg);
		}
		
		return upperRange;
	}
	
	protected String getNodeTypeByXMIId(Node node) {
		String type		= null;
		String typeRef 	= null;
		Node n 			= locateChildNodeByName(node, "type");

		if (n != null)
			typeRef = n.getNodeValue();

		if (typeRef != null) {
			String typePath = getDataTypePath(typeRef);
			try {
				n = getXPathAPI().selectSingleNode(XMIParser.xmiDocument(),typePath);
				if (n != null) {
					n = locateChildNodeByName(n, "type");
					if (n != null)
						type = n.getNodeValue();
					else {
						final String msg = "Unable to find a child node by the name \\'type\\' on node " + getElementName(n); 
						LOGGER.warning(msg);
					}
				}
			} catch (Exception exc) {
				final String msg = "Failed to locate type for " + getElementName(n);
				LOGGER.log(Level.WARNING, msg, exc);
			}
		}
		
		return type;
	}

	protected String getNodeTypeByXMIIDRef(Node node) {
		String type		= null;
		String typePath = "UML:StructuralFeature.type/UML:DataType";
		String umlType 	= "Class";
		
		try {
			Node n = getXPathAPI().selectSingleNode(node, typePath);
			
			if ( n == null ) {			    
				typePath 	= "UML:StructuralFeature.type/UML:Class";
				n 			= getXPathAPI().selectSingleNode(node, typePath);
				umlType 	= "Class";
			}
			else {
				umlType = "DataType";
			}
			
			if (n != null) {
				String idRef = locateChildNodeByName(n, XMI_ID_REF).getNodeValue();
				typePath 		= ".//UML:" + umlType + "[@xmi.id='" + idRef + "']";
				n 				= getXPathAPI().selectSingleNode(XMIParser.xmiDocument(),
													typePath);
				
				if (n != null)
					n = locateChildNodeByName(n, "name");

				if (n != null)
					type = n.getNodeValue();
				else {
					final String msg = "Unable to locate a child node with the name \'name\' for node " + getElementName(n);
					LOGGER.warning(msg);
				}
			}
		}
		catch (Exception exc) {
			final String msg = "Failed to locate the type for node " + getElementName(node);
			LOGGER.log(Level.WARNING, msg, exc);
		}
		
		return type;
	}

	protected String findStereotypeByXMIId(String stereoTypeRef) {
		String stereotype 			= null;
		Node n						= null;
		final String errMsgPrefix	= "Failed to find a stereotype for ";
		final String stereoTypePath = getStereoTypePath(stereoTypeRef);
		
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
	
	protected String findStereotypeByXMIIdRef(Node theNode) { 
		String stereotype 			= "";
		String stereoTypePath 		= "UML:ModelElement.stereotype/UML:Stereotype";
		final String errMsgPrefix	= "Failed to find a stereotype for ";
		
		try {
			Node stereoTypeNode = getXPathAPI().selectSingleNode(theNode, stereoTypePath);
			if (stereoTypeNode != null) {
				String idRef = locateChildNodeByName(stereoTypeNode, XMI_ID_REF).getNodeValue();
	
				stereoTypePath 	= ".//UML:Stereotype[@xmi.id='" + idRef + "']";
				theNode 		= getXPathAPI().selectSingleNode(XMIParser.xmiDocument(), stereoTypePath);
				
				if (theNode != null)
					theNode = locateChildNodeByName(theNode, "name");
	
				if (theNode != null)
					stereotype = theNode.getNodeValue();
				else {
					final String msg = errMsgPrefix + getElementName(theNode);
					LOGGER.warning(msg);
				}
			}
		}
		catch (Exception exc) {
			final String msg = errMsgPrefix + getElementName(theNode);
			LOGGER.log(Level.WARNING, msg, exc);
		}
		
		return stereotype;
	}

	protected Node findParameterByClass(Node parameterNode) 
			throws javax.xml.transform.TransformerException {	
		String parameterPath = "UML:Parameter.type/UML:Class";
		Node classNode = getXPathAPI().selectSingleNode(parameterNode, parameterPath);
	
		if (classNode != null) {					
			Node tmpNode = locateChildNodeByName(classNode, XMI_ID_REF);
			
			if ( tmpNode != null )
				classNode = getClassNode(tmpNode.getNodeValue());
		}
		
		return classNode;
	}	
	
	protected Node findParameterByInterface(Node parameterNode)
			throws javax.xml.transform.TransformerException {
		String parameterPath = "UML:Parameter.type/UML:Interface";
		Node classNode = getXPathAPI().selectSingleNode(parameterNode, parameterPath);

		if (classNode != null) {
			Node tmpNode = locateChildNodeByName(classNode, XMI_ID_REF);
		
			if ( tmpNode != null )
				classNode = getInterfaceNode(tmpNode.getNodeValue());
		}		
		
		return classNode;
	}

	protected Node findParameterByDataType(Node parameterNode)
			throws javax.xml.transform.TransformerException {
		final String parameterPath 	= "UML:Parameter.type/UML:DataType";
		Node classNode 				= getXPathAPI().selectSingleNode(parameterNode, parameterPath);

		if (classNode != null) {
			Node tmpNode = locateChildNodeByName(classNode, "href");
			
			if ( tmpNode != null )
				classNode = getDataTypeNode(tmpNode.getNodeValue());
		}		
		
		return classNode;
	}

	protected MethodArgs determineMethodArgs( Node classNode, Node parameterNode, String parameterName ) {
		Node kindNode 			= null;
		String parameterType 	= null;
		MethodArgs methodArgs	= new MethodArgs();
		
		if (classNode != null)
			parameterType = classNode.getNodeValue();
		else
			parameterType = "void";
	
		kindNode = locateChildNodeByName(parameterNode, "kind");
	
		if (kindNode != null) {
			if (kindNode.getNodeValue().equalsIgnoreCase("in"))
				methodArgs.getArgs().add(new MethodArg(parameterName,parameterType));
			else if (kindNode.getNodeValue().equalsIgnoreCase("return"))
				methodArgs.setReturnType( parameterType );
		}
		else { // assume in
			methodArgs.getArgs().add(new MethodArg(parameterName,
					parameterType));
		}
		
		return methodArgs;
	}

	protected String getOperationDecl() {
		return OPERATION_DECL;
	}
	
	protected String getPackageDecl() {
		return PACKAGE_DECL;
	}
	
	protected String getElementIdPath(String xmiID) {
		return "//*[@xmi.id='" + xmiID + "']/UML:Model";
	}
	
	protected Node getClassNodeFromAssociationNode(Node associationNode) {
		String associationEndPath 	= ".//UML:AssociationEnd.participant/UML:Class";
		Node classNode 				= null;

		try {
			classNode = getXPathAPI().selectSingleNode(associationNode, associationEndPath);
			classNode = locateChildNodeByName(classNode, XMI_ID_REF);
		} catch( Exception exc ) {
			LOGGER.log( Level.WARNING, "Failed to locate a class node for a given association node", exc);
		}

		return( classNode );
	}
	
	protected String getDocumentPath() {
		return DOCUMENT_PATH;
	}
	
	protected String getStereoTypePath( String stereoTypeRef ) {
	    return "//UML:Stereotype[@xmi.id ='" + stereoTypeRef + "']";	
	}
	
	protected String getInterfacePath( String interfaceId ) {
		return ".//UML:Interface[@xmi.id='" + interfaceId + "']";
	}

	protected String getClassPath( String classID ) {
		return 	".//UML:Class[@xmi.id='" + classID + "']";
	}

	protected String getDataTypePath( String typeRef ) {
		return "//UML:DataType[@xmi.id ='" + typeRef + "']";
	}
	
	// attributes
	public static final String RM_RETURN_KEY 				= "rmReturnKey";
	private static final String XMI_ID_REF					= "xmi.idref";
	private static final String CHANGE_ABILITY				= "changeability";
	private static final String PARENT_MSG_PREFIX 			= "  - Parent ";
	private static final String MAX_XMI_VERSION_SUPPORTED 	= "1.2";
	private static final String OPERATION_DECL				= "*/UML:Operation";
	private static final String PACKAGE_DECL				= "UML:Package";
	private static final String DOCUMENT_PATH				= "UML:ModelElement.taggedValue/UML:TaggedValue/UML:TaggedValue.dataValue";
	private static final Logger LOGGER 						= Logger.getLogger(XMIProvider12.class.getName());

}
