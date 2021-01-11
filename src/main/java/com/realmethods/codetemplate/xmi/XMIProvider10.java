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

import com.realmethods.codetemplate.model.attribute.AttributeObject;
import com.realmethods.codetemplate.model.association.AssociationEndObject;
import com.realmethods.codetemplate.model.classes.ClassObject;
import com.realmethods.codetemplate.model.factory.AssociationFactory;
import com.realmethods.codetemplate.model.factory.AttributeFactory;
import com.realmethods.codetemplate.model.factory.MethodFactory;
import com.realmethods.codetemplate.model.method.MethodArg;
import com.realmethods.codetemplate.model.method.MethodArgs;
import com.realmethods.codetemplate.model.method.MethodObject;
import com.realmethods.codetemplate.parser.XMIParser;
import com.realmethods.common.helpers.Utils;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeIterator;

/**
 * XMIProvider implementation for v 1.0 of the spec
 */
public class XMIProvider10 extends XMIProvider {

	public XMIProvider10() {
		// default constructor, intentionally empty
	}

	public String getXMIVersion() {
		return "1.0";
	}

	public String getNodeXMIDecl() {
		return "//Foundation.Core.Node[@xmi.id]";
	}

	public String getComponentXMIDecl() {
		return "//Foundation.Core.Component[@xmi.id]";
	}

	public String getClassXMIDecl() {
		return "//Foundation.Core.Class[@xmi.id]";
	}

	public String getInterfaceXMIDecl() {
		return "//Foundation.Core.Interface[@xmi.id]";
	}

	public List<AttributeObject> findAttributes(ClassObject classObject,
			Node classNode) {
		String attributePath = "*/Foundation.Core.Attribute";
		List<AttributeObject> attributes = new ArrayList<>();
		NodeIterator nl = null;
		try {
			nl = getXPathAPI().selectNodeIterator(classNode, attributePath);
			for (Node attributeNode = null; (attributeNode = nl
					.nextNode()) != null;)
				attributes.add(AttributeFactory.getInstance()
						.createInstance(classObject, attributeNode));
		} catch (Exception exc) {
			final String msg = "Failed locating attributes for "
					+ classObject.getName();
			LOGGER.log(Level.WARNING, msg, exc);
		}
		return attributes;
	}

	public List<MethodObject> findMethods(Node classNode) {
		List<MethodObject> methods = new ArrayList<>();
		String className = getElementName(classNode);
		try {
			String methodPath = getMethodPath();
			String msg = null;
			NodeIterator nl = getXPathAPI().selectNodeIterator(classNode,
					methodPath);
			Node methodNode = null;
			MethodObject methodObject = null;

			while ((methodNode = nl.nextNode()) != null) {
				methodObject = MethodFactory.getInstance()
						.createInstance(methodNode);
				msg = "Method discovered for class name " + className + " : "
						+ methodObject.toString();
				LOGGER.info(msg);
				methods.add(methodObject);
			}
		} catch (Exception exc) {
			final String msg = "Failed to find method for class Node "
					+ className;
			LOGGER.log(Level.WARNING, msg, exc);
		}

		return methods;
	}

	public List<AssociationEndObject> findAssociations(Node classNode) {
		List<AssociationEndObject> associations = new ArrayList<>();
		String className = getElementName(classNode);
		try {
			String associationEndPath = getAssociationEndPath(classNode);
			NodeIterator nl = getXPathAPI().selectNodeIterator(classNode,
					associationEndPath);
			Node associationEndNode = null;
			Node siblingAssociationEndNode = null;
			AssociationEndObject associationEndObject = null;
			String siblingAssociationEndPath = getSiblingAssociationEndPath();

			while ((associationEndNode = nl.nextNode()) != null) {
				siblingAssociationEndNode = getXPathAPI().selectSingleNode(
						associationEndNode, siblingAssociationEndPath);
				if (siblingAssociationEndNode != null) {
					associationEndObject = AssociationFactory.getInstance()
							.createInstance(siblingAssociationEndNode, true);
					associations.add(associationEndObject);
				}
			}
		} catch (Exception exc) {
			final String msg = "Failed during finding associations for class "
					+ className;
			LOGGER.log(Level.WARNING, msg, exc);
		}

		return associations;
	}

	public Map<String, String> getTagNamesAndValues(Node associationNode) {
		Map<String, String> map = new HashMap<>();

		try {
			String path = getTagNameValuePath(associationNode);
			Document xmiDocument = XMIParser.xmiDocument();
			NodeList list = getXPathAPI().selectNodeList(xmiDocument, path);
			if (list != null) {
				String tagPath = getTagNameValueTagDefinition();
				int numNodes = list.getLength();
				Node tagNode = null;
				Node tempNode = null;

				for (int i = 0; i < numNodes; i++) {
					tempNode = list.item(i);
					tagNode = getXPathAPI().selectSingleNode(tempNode, tagPath);

					if (tagNode != null) {
						tempNode = tagNode.getChildNodes().item(0);
						if (tempNode != null)
							map.put(tempNode.getNodeName(),
									tempNode.getNodeValue());
					}
				}
			}
		} catch (Exception exc) {
			final String msg = "Failed to get tag/name values for association"
					+ getElementName(associationNode);
			LOGGER.log(Level.WARNING, msg, exc);
		}

		return map;
	}

	public boolean findIsAbstract(Node classNode) {
		boolean isAbstract = false;

		try {
			String classScopePath = getAbstractPath();
			Node n = getXPathAPI().selectSingleNode(classNode, classScopePath);
			if (n != null && n.getNodeValue().equals("true"))
				isAbstract = true;
		} catch (Exception exc) {
			final String msg = "Failed to determine abstractness for class "
					+ getElementName(classNode);
			LOGGER.log(Level.WARNING, msg, exc);
		}
		return (isAbstract);
	}

	public String findStereotype(Node classNode) {
		String stereotype = null;
		try {
			String stereoTypePath = getStereotypePath(classNode);
			Node n = getXPathAPI().selectSingleNode(XMIParser.xmiDocument(),
					stereoTypePath);

			if (n != null) {
				stereoTypePath = getStereotypeElementPath();
				n = getXPathAPI().selectSingleNode(n, stereoTypePath);
				if (n != null && n.getChildNodes() != null
						&& n.getChildNodes().item(0) != null)
					stereotype = n.getChildNodes().item(0).getNodeValue();
			}
		} catch (Exception exc) {
			final String msg = "Failed to determine stereotype for class "
					+ getElementName(classNode);
			LOGGER.log(Level.WARNING, msg, exc);
		}

		return stereotype;
	}

	public String findPackage(Node referenceNode) {
		StringBuilder packageName = new StringBuilder();

		try {
			Node parentNode = referenceNode.getParentNode();
			String packagePath = "Model_Management.Package";
			String name = FOUNDATION_CORE_MODELELEMENT_NAME;
			Node nameNode = null;

			for (; parentNode != null; parentNode = parentNode.getParentNode())
				if (parentNode.getLocalName() != null && parentNode
						.getLocalName().equalsIgnoreCase(packagePath)) {
					nameNode = getXPathAPI().selectSingleNode(parentNode, name);
					packageName.insert(0,
							nameNode.getChildNodes().item(0).getNodeValue()
									+ ".");
				}

			if (packageName.length() > 0)
				packageName = new StringBuilder(packageName.toString()
						.substring(0, packageName.length() - 1));
		} catch (Exception exc) {
			final String msg = "Failed to locate package for class "
					+ getElementName(referenceNode);
			LOGGER.log(Level.WARNING, msg, exc);
		}

		return Utils.removeSpaces(packageName.toString());
	}

	public List<String> findSuperTypes(Node classNode)
    {
        List<String> superTypes = new ArrayList<>();
        String className 		= getElementName(classNode);
        try {
	        String generalizationPath 	= getSuperTypeAsClassPath();
	        String superType 			= null;
	        String superTypeIDPath 		= null;
	        String superTypeID 			= null;
	        Node n 						= null;
	        NodeIterator nl 			= getXPathAPI().selectNodeIterator(classNode, generalizationPath);
	        
	        // classes as super types
	        for(Node generalizationNode = null; (generalizationNode = nl.nextNode()) != null;)
	        {
	            n 				= getXPathAPI().selectSingleNode(generalizationNode, XMI_ID_REF);
	            superTypeIDPath = getSupertypeParentAsClassPath(n);	          
	            n 				= getXPathAPI().selectSingleNode(generalizationNode, superTypeIDPath);
	            superTypeID 	= n.getNodeValue();
	            superType 		= getElementNameforID(superTypeID, generalizationNode).trim();
	            
	            superTypes.add(Utils.capitalizeFirstLetter(superType));
	            
	            final String msg = "class " + className + " has parent(s) " + superType;
	            LOGGER.log(Level.INFO, msg);
	        }
	
	        generalizationPath 	= getSuperTypeAsInterfacePath();
	        nl 					= getXPathAPI().selectNodeIterator(classNode, generalizationPath);
	        
	        // interfaces as super types
	        for(Node generalizationNode = null; (generalizationNode = nl.nextNode()) != null;) {
	            n 				= getXPathAPI().selectSingleNode(generalizationNode, XMI_ID_REF);
	            superTypeIDPath = getSupertypeParentAsInterfacePath( n );
	            n 				= getXPathAPI().selectSingleNode(generalizationNode, superTypeIDPath);	      
	            superTypeID 	= n.getNodeValue();
	            superType 		= getElementNameforID(superTypeID, generalizationNode).trim();
	            
	            superTypes.add(Utils.capitalizeFirstLetter(superType));

	            final String msg = "class " + className + " has interface " + superType;
	            LOGGER.log(Level.INFO, msg);

	        }
		}
    	catch( Exception exc ) {
    		final String msg = "Failed to locate parent(s) and interface(s) for class " + className;
    		LOGGER.log(Level.WARNING, msg, exc); 
		}

        return superTypes;
    }

	public String getElementName(Node node) {
		String elementName = "";

		try {
			Node n = getXPathAPI().selectSingleNode(node,
					FOUNDATION_CORE_MODELELEMENT_NAME);
			if (n != null)
				elementName = n.getChildNodes().item(0).getNodeValue();
			else {
				final String msg = "Name element is empty for node with id "
						+ getID(node);
				LOGGER.info(msg);
			}
		} catch (Exception exc) {
			final String msg = "Failed during locating a name for node with id "
					+ getID(node);
			LOGGER.log(Level.WARNING, msg, exc);
		}
		return elementName;
	}

	public String getElementNameforID(String xmiID, Node node) {
		String elementName = "";
		String idpath = getIDPath(xmiID);
		try {
			Node n = getXPathAPI().selectSingleNode(node, idpath);
			if (n != null)
				elementName = n.getChildNodes().item(0).getNodeValue();
			else {
				final String msg = "Element name node is null for node "
						+ getElementName(node);
				LOGGER.info(msg);
			}
		} catch (Exception exc) {
			final String msg = "Failed to located a name for node with id "
					+ xmiID;
			LOGGER.log(Level.WARNING, msg, exc);
		}

		return elementName;
	}

	public AssociationEndObject getSiblingAssociation(Node associationEndNode) {
		AssociationEndObject associationEndObject = null;
		try {
			String siblingAssociationEndPath = SIBLING_ASSOCIATION_END_PHRASE;
			Node siblingAssociationEndNode = getXPathAPI().selectSingleNode(
					associationEndNode, siblingAssociationEndPath);
			if (siblingAssociationEndNode != null)
				associationEndObject = AssociationFactory.getInstance()
						.createInstance(siblingAssociationEndNode, false);
			else {
				final String msg = "Mo sibling node found for "
						+ getElementName(associationEndNode);
				LOGGER.info(msg);
			}
		} catch (Exception exc) {
			final String msg = "Failed during location of a sibling for association:"
					+ getElementName(associationEndNode);
			LOGGER.log(Level.WARNING, msg, exc);
		}
		return associationEndObject;
	}

	public AssociationEndObject getAssociationSibling(Node associationNode) {
		AssociationEndObject associationEndObject = null;
		try {
			String siblingAssociationEndPath = SIBLING_ASSOCIATION_END_PHRASE;
			Node siblingAssociationEndNode = getXPathAPI().selectSingleNode(
					associationNode, siblingAssociationEndPath);
			associationEndObject = AssociationFactory.getInstance()
					.createInstance(siblingAssociationEndNode, false);
		} catch (Exception exc) {
			final String msg = "Failed during location of a sibling for association:"
					+ getElementName(associationNode);
			LOGGER.log(Level.WARNING, msg, exc);
		}
		return associationEndObject;
	}

	public String getAssociationEndType(Node associationNode) {
		String endType = null;
		try {
			String associationEndTypeIDRefPath = "Foundation.Core.AssociationEnd.type/*/@xmi.idref";
			Node n = getXPathAPI().selectSingleNode(associationNode,
					associationEndTypeIDRefPath);
			String typeIDRef = null;
			if (n != null) {
				typeIDRef = n.getNodeValue();
				endType = getElementNameforID(typeIDRef, associationNode);
			} else {
				final String msg = "No type was found for association "
						+ getElementName(associationNode)
						+ " but one is required.";
				LOGGER.warning(msg);
			}
		} catch (Exception exc) {
			final String msg = "Failed during determining type for association:"
					+ getElementName(associationNode);
			LOGGER.log(Level.WARNING, msg, exc);
		}
		return endType;
	}

	public String getAssociationRoleName(Node associationNode) {
		String roleNamePath = FOUNDATION_CORE_MODELELEMENT_NAME;
		String roleName = "none";
		try {
			Node n = getXPathAPI().selectSingleNode(associationNode,
					roleNamePath);
			if (n != null && n.hasChildNodes())
				roleName = n.getChildNodes().item(0).getNodeValue();
			else {
				final String msg = "No role name was found for association "
						+ getElementName(associationNode)
						+ " but one is required.";
				LOGGER.warning(msg);
			}

		} catch (Exception exc) {
			final String msg = "Failed during determining role name for association:"
					+ getElementName(associationNode);
			LOGGER.log(Level.WARNING, msg, exc);
		}
		return Utils.removeSpaces(roleName);
	}

	public int[] getLowerUpperRange(Node associationNode)
    {
        int lowerRange 			= 0;
        int upperRange 			= -1;
        String associationName 	= getElementName( associationNode ) ;

        try {
            String multiplicityPath = ".//Foundation.Data_Types.Multiplicity";
            Node multiplicityNode 	= getXPathAPI().selectSingleNode(associationNode, multiplicityPath);
            
	        if(multiplicityNode != null) { 
	            lowerRange = getLowerRange(multiplicityNode, associationNode);
	            
	            String upperPath = getMultiplicityUpperPath();
	            Node nUpper = getXPathAPI().selectSingleNode(multiplicityNode, upperPath);
	            if(nUpper == null) {
	                if(lowerRange <= 1)
	                    upperRange = 1;
	                
	                final String msg = "AssociationEnd " 
                			+ getAssociationRoleName(associationNode) 
                			+ " of " 
                			+ getAssociationEndType(associationNode) 
                			+ " has no upper range specified - assigning to " 
                			+ upperRange + (upperRange != -1 ? "" : " (unlimited)");	                
	                LOGGER.log(Level.WARNING, msg);
	            } else if(nUpper.hasChildNodes()) {
	                String upperValue = nUpper.getChildNodes().item(0).getNodeValue();
	                upperRange = !upperValue.equals("*") && !upperValue.equals("n") ? Integer.parseInt(nUpper.getChildNodes().item(0).getNodeValue()) : -1;
	            }
	        }
		}
        catch( Exception exc ) {
        	final String msg = "Failure during determing upper and lower range of associaton " + associationName;
        	LOGGER.log(Level.WARNING, msg, exc);
        }
        
        int[] range = new int[2];
        range[0] = lowerRange;
        range[1] = upperRange;
        return range;
    }

	public boolean getAggregationIndicator(Node associationNode) {
		boolean isAggregate = false;
		String aggregationPath = getAggregatorPath();
		String associationName = getElementName(associationNode);
		try {
			Node n = getXPathAPI().selectSingleNode(associationNode,
					aggregationPath);
			if (n != null)
				isAggregate = n.getNodeValue().equalsIgnoreCase("aggregate");
			else {
				LOGGER.log(Level.WARNING, "Aggregator indicator is incorrectly null for {0}", associationName);
			}
		} catch (Exception exc) {
			LOGGER.log(Level.WARNING, "Failure during locating the aggregator indcator for association {0}", associationName);
		}
		return isAggregate;
	}

	public boolean getCompositeIndicator(Node associationNode) {
		boolean isComposite = false;
		String compositePath = getCompositePath();
		String associationName = getElementName(associationNode);

		try {
			Node n = getXPathAPI().selectSingleNode(associationNode,
					compositePath);
			if (n != null)
				isComposite = n.getNodeValue()
						.equalsIgnoreCase("composite");
			else {				
				LOGGER.log(Level.WARNING, "Composite indicator is incorrectly null for {0}", associationName);
			}
		} catch (Exception exc) {
			LOGGER.log(Level.WARNING, "Failure during locating the composite indcator for association {0}", associationName);
		}
		return isComposite;
	}

	public boolean getNavigableIndicator(Node associationNode) {
		boolean isNavigable = false;
		String navigablePath = getNavigablePath();
		String associationName = getElementName(associationNode);

		try {
			Node n = getXPathAPI().selectSingleNode(associationNode,
					navigablePath);
			if (n != null)
				isNavigable = Boolean.parseBoolean(n.getNodeValue());
			else {
				LOGGER.log(Level.WARNING, "Navigation indicator is incorrectly null for {0}", associationName);
			}
		} catch (Exception exc) {
			LOGGER.log(Level.WARNING, "Failure during locating the navigation indcator for association {0}", associationName );
		}

		return isNavigable;
	}

	public boolean getOrderedIndicator(Node associationNode) {
		boolean isOrdered = false;
		String orderingPath = getOrderingPath();
		String associationName = getElementName(associationNode);

		try {
			Node n = getXPathAPI().selectSingleNode(associationNode,
					orderingPath);
			if (n != null)
				isOrdered = n.getNodeValue().equals("ordered");
			else {
				LOGGER.log(Level.WARNING, "Ordered indicator is incorrectly null for {0}", associationName);
			}
		} catch (Exception exc) {
			LOGGER.log(Level.WARNING, "Failure during locating the ordered indcator for association {0}", associationName);
		}

		return isOrdered;
	}

	public String getVisibility(Node methodNode) {
		String visibility = "protected";
		String scopePath = this.getVisibilityPath();
		String methodName = getElementName(methodNode);

		try {
			Node n = getXPathAPI().selectSingleNode(methodNode, scopePath);
			if (n != null)
				visibility = n.getNodeValue();
			else {
				LOGGER.log(Level.WARNING, "Visibility indicator is null for {0}, will default to protected", methodName);
			}
		} catch (Exception exc) {
			LOGGER.log(Level.WARNING, "Failure during locating the visibility indcator for method {0}", methodName);
		}
		return visibility;
	}

	public boolean isMethodStatic(Node methodNode) {
		return getStaticIndicator(methodNode);
	}

	public String getDocumentation(Node node) {
		String docPath 	= getDocumentPath(node);
		String name 	= getElementName(node);
		
		try {
			NodeList nodeList = getXPathAPI()
					.selectNodeList(XMIParser.xmiDocument(), docPath);
			if (nodeList != null) {
				String tagPath = getTagNameValueTagDefinition();
				String valuePath = this.getTagNameValueValuePath();
				int numNodes = nodeList.getLength();

				for (int i = 0; i < numNodes; i++) {
					Node tempNode = nodeList.item(i);
					Node tagNode = getXPathAPI().selectSingleNode(tempNode,
							tagPath);
					Node valueNode = getXPathAPI().selectSingleNode(tempNode,
							valuePath);

					if (tagNode == null || valueNode == null
							|| !tagNode.getChildNodes().item(0).getNodeValue()
									.equalsIgnoreCase("documentation")) {
						// no_op
					}
					else {
						if (valueNode.getChildNodes() != null
							&& valueNode.getChildNodes().item(0) != null)
							return valueNode.getChildNodes().item(0).getNodeValue();
					}
				}
			}
		} catch (Exception exc) {
			final String msg = "Failure during locating the documentation for node "
					+ name;
			LOGGER.log(Level.WARNING, msg, exc);
		}
		return null;
	}

	public MethodArgs getMethodArguments(Node methodNode) {
		MethodArgs methodArgs = new MethodArgs();
		String parameterPath = getParameterPath();
		String methodName = getElementName(methodNode);
		String msg = null;

		try {
			NodeIterator nl = getXPathAPI().selectNodeIterator(methodNode,
					parameterPath);
			Node parameterNode = null;
			Node nodeImpl = null;
			String parameterName = null;
			String parameterType = null;

			while ((parameterNode = nl.nextNode()) != null) {
				nodeImpl = getXPathAPI().selectSingleNode(parameterNode,
						FOUNDATION_CORE_MODELELEMENT_NAME);
				if (nodeImpl != null) {
					parameterName = nodeImpl.getChildNodes().item(0).getNodeValue();
				}

				nodeImpl = getXPathAPI().selectSingleNode(parameterNode,
						getParameterTypePath());

				if (nodeImpl != null) {
					parameterType = getElementNameforID(nodeImpl.getNodeValue(),parameterNode);
				}

				nodeImpl = getXPathAPI().selectSingleNode(parameterNode,
						getParameterKindPath());

				if (nodeImpl != null && nodeImpl.getNodeValue().startsWith("in")) {
					if (parameterType.indexOf("void") == -1) {
						methodArgs.getArgs().add( new MethodArg(parameterName, parameterType));
						msg = "Method " +  methodName + " has parameter name is " + parameterName + " with type " + parameterType;
						LOGGER.log(Level.INFO, msg);
					}
				} else {
					methodArgs.setReturnType(parameterType);
					msg = "Method " +  methodName + " has return type " + parameterType;
					LOGGER.log(Level.INFO, msg);
				}
			}
		} catch (Exception exc) {
			msg = "Failure during locating arguments for method "
					+ methodName;
			LOGGER.log(Level.WARNING, msg, exc);
		}

		return methodArgs;
	}

	public String getAttributeType(Node attributeNode) {
		String type = null;
		try {
			String typeIDRef = getTypeIDRef(attributeNode);
			if (typeIDRef != null) {
				type = getElementNameforID(typeIDRef, attributeNode);
			}
		} catch (Exception exc) {
			final String msg = "Failure during determine type for attribute "
					+ getElementName(attributeNode);
			LOGGER.log(Level.WARNING, msg, exc);
		}
		return type;
	}

	public boolean getStaticIndicator(Node node) {
		boolean isStatic = false;
		String staticPath = getStaticPath();
		String name = getElementName(node);

		try {
			Node n = getXPathAPI().selectSingleNode(node, staticPath);
			if (n != null)
				isStatic = n.getNodeValue().equals("classifier");
			else {
				final String msg = "Static indicator is null for node " + name
						+ ", so will default to false";
				LOGGER.warning(msg);
			}
		} catch (Exception exc) {
			final String msg = "Failure during locating the static indcator for node "
					+ name;
			LOGGER.warning(msg);
		}

		return isStatic;
	}

	public boolean getFinalIndicatorForAssociation(Node associationNode) {
		boolean finalIndicator = false;
		String staticPath = getChangeabilityPath();
		String name = getElementName(associationNode);

		try {
			Node n = getXPathAPI().selectSingleNode(associationNode,
					staticPath);
			if (n != null)
				finalIndicator = n.getNodeValue().equals("frozen");
			else {
				LOGGER.log(Level.WARNING, "Final indicator is null for assoication node {0},  ill default to false", name );
			}
		} catch (Exception exc) {
			LOGGER.log(Level.WARNING, "Failure during locating the final indicator for association node {0}", name );
		}

		return (finalIndicator);
	}

	public boolean getFinalIndicator(Node attributeNode)
    {
    	boolean finalIndicator 	= false;    	
        String staticPath 		= getAttributeFinalIndicatorPath();
        String name				= getElementName(attributeNode);

        try {
        	Node n = getXPathAPI().selectSingleNode(attributeNode, staticPath);
        	if ( n != null )
        		finalIndicator = n.getNodeValue().equals("frozen");
        	else {
	        	LOGGER.log(Level.WARNING, "Final indicator is null for attribute node {0}, will default to false", name );
    		}        
		}
        catch( Exception exc ) {
        	LOGGER.log(Level.WARNING, "Failure during locating the final indicator for attribute node {0}", name);
    	}
        return( finalIndicator );
    }

	public boolean getAddOnlyIndicatorForAssociation(Node associationNode) {
		boolean addOnly = false;
		String staticPath = getChangeabilityPath();
		String name = getElementName(associationNode);

		try {
			Node n = getXPathAPI().selectSingleNode(associationNode,
					staticPath);
			if (n != null)
				addOnly = n.getNodeValue().equalsIgnoreCase("addOnly");
			else {
				final String msg = "Final changeability is null for attribute node "
						+ name + ", so will default to false";
				LOGGER.warning(msg);
			}
		} 
		catch (Exception exc) {
			final String msg = "Failure during locating the changeability indicator for attribute node "
					+ name;
			LOGGER.warning(msg);
		}
		return (addOnly);
	}

	public boolean getTransientIndicator(Node attributeNode) {
		return getIndicator(attributeNode, "transient");
	}

	public boolean getVolatileIndicator(Node attributeNode) {
		return getIndicator(attributeNode, "volatile");
	}

	public String getDefaultValue(Node attributeNode) {
		String defaultValue = null;
		String attributeDefaultValuePath = getDefaultValuePath();

		try {
			Node n = getXPathAPI().selectSingleNode(attributeNode,
					attributeDefaultValuePath);

			if (n != null && n.hasChildNodes())
				defaultValue = n.getChildNodes().item(0).getNodeValue();

			if (defaultValue != null)
				defaultValue = defaultValue.trim();
		} catch (Exception exc) {
			final String msg = "Failure during determining the default value for attribute "
					+ getElementName(attributeNode);
			LOGGER.log(Level.WARNING, msg, exc);
		}
		return defaultValue;
	}

	public boolean getRequiredIndicator(Node attributeNode) {
		boolean required 				= false;
		String isUniqueAttributePath 	= getRequiredIndicatorPath(attributeNode);
		String id 						= this.getID(attributeNode);
		try {
			Node n = getXPathAPI().selectSingleNode(attributeNode,
					isUniqueAttributePath);
			if (n == null) {
				isUniqueAttributePath = "//Foundation.Extension_Mechanisms.Stereotype[contains(Foundation.Core.ModelElement.name/text(), 'OID') and .//Foundation.Core.Attribute/@xmi.idref ='"
						+ id + "']";
				n = getXPathAPI().selectSingleNode(attributeNode,
						isUniqueAttributePath);
			}
			if (n != null)
				required = true;
		} catch (Exception exc) {
			final String msg = "Failure during determining the required indicator for attribute "
					+ getElementName(attributeNode);
			LOGGER.log(Level.WARNING, msg, exc);
		}

		return required;
	}

	public boolean getNullIndicator(Node attributeNode) {
		boolean isNull = false;
		try {
			Node multiplicityNode = getXPathAPI().selectSingleNode(
					attributeNode,
					"Foundation.Core.StructuralFeature.multiplicity");
			if (multiplicityNode != null) {
				String lowerPath = ".//Foundation.Data_Types.Multiplicity.range/*/Foundation.Data_Types.MultiplicityRange.lower";
				Node nLower = getXPathAPI().selectSingleNode(multiplicityNode,
						lowerPath);

				if (nLower != null && nLower.getChildNodes().item(0)
						.getNodeValue().equals("0"))
					isNull = true;
			} else {
				isNull = true;
			}
		} catch (Exception exc) {
			final String msg = "Failure during determining the null indicator for attribute "
					+ getElementName(attributeNode);
			LOGGER.log(Level.WARNING, msg, exc);
		}

		return (isNull);
	}

	public boolean getIntrinsicTypeIndicator(Node attributeNode) {
		boolean intrinsic = false;
		try {
			String typeIDRef = getTypeIDRef(attributeNode);
			String typeIDPath = "//Foundation.Core.DataType[@xmi.id='"
					+ typeIDRef + "']";
			Node n = getXPathAPI().selectSingleNode(attributeNode, typeIDPath);
			if (n == null)
				return false;
			String datatypeClassPath = "//Foundation.Core.Class[contains(Foundation.Core.ModelElement.name/text(), '"
					+ getElementName(attributeNode) + "')]";
			n = getXPathAPI().selectSingleNode(n, datatypeClassPath);
			if (n != null)
				intrinsic = true;
		} catch (Exception exc) {
			final String msg = "Failure during determining the intrinisic indicator for attribute "
					+ getElementName(attributeNode);
			LOGGER.log(Level.WARNING, msg, exc);
		}

		return intrinsic;
	}

	protected String getAssociationEndPath(Node classNode) {
		return "//Foundation.Core.AssociationEnd[Foundation.Core.AssociationEnd.type/*/@xmi.idref='"
				+ getID(classNode) + "']";
	}

	protected int getLowerRange( Node multiplicityNode, Node associationNode ) {
		int lowerRange 		= 0;
    	String lowerPath	= ".//Foundation.Data_Types.MultiplicityRange.lower";
    	
    	try {
	        Node nLower 		= getXPathAPI().selectSingleNode(multiplicityNode, lowerPath);
	
	        if(nLower == null) {
	            String multiplicityPath		= XMI_ID_REF;
	            multiplicityNode			= getXPathAPI().selectSingleNode(multiplicityNode, multiplicityPath);
	            if(multiplicityNode != null) {
	                multiplicityPath 	= getMultiplicityPath(multiplicityNode);
	                multiplicityNode 	= getXPathAPI().selectSingleNode(XMIParser.xmiDocument(), multiplicityPath);
	                nLower 				= getXPathAPI().selectSingleNode(multiplicityNode, lowerPath);
		            if(nLower == null) {
		                lowerRange = 1;
		                LOGGER.log(Level.WARNING, () -> "AssociationEnd " + getAssociationRoleName(associationNode) 
		                												+ "  of " 
		                												+ getAssociationEndType(associationNode) 
		                												+ "  has no lower range specified - assigning 1");
		            } else if(nLower.hasChildNodes())
		                lowerRange = Integer.parseInt(nLower.getChildNodes().item(0).getNodeValue());
	            }
	        }
    	} catch( Exception exc ) {
    		LOGGER.log(Level.SEVERE, "Failure during determining lower range", exc);
    	}
        return lowerRange;
	}
	
	protected String getSiblingAssociationEndPath() {
		return SIBLING_ASSOCIATION_END_PHRASE;
	}

	private String getTagNameValuePath(Node associationNode) {
		return "//Foundation.Extension_Mechanisms.TaggedValue[.//Foundation.Core.ModelElement/@xmi.idref ='"
				+ getID(associationNode) + "']";
	}

	protected String getTagNameValueTagDefinition() {
		return FOUNDATION_EXTENSTION_MECHANISMS_TAGGEDVALUE_TAG;
	}

	protected String getMethodPath() {
		return FOUNDATION_CORE_OPERATION;
	}

	protected String getAbstractPath() {
		return FOUNDATION_CORE_ABSTRACT;
	}

	protected String getStereotypePath(Node classNode) {
		return "//Foundation.Extension_Mechanisms.Stereotype[.//Foundation.Core.ModelElement/@xmi.idref ='"
				+ getID(classNode) + "']";
	}

	protected String getStereotypeElementPath() {
		return FOUNDATION_CORE_MODELELEMENT_NAME;
	}

	protected String getSuperTypeAsClassPath() {
		return FOUNDATION_CORE_GENERALIZATION;
	}

	protected String getSupertypeParentAsClassPath(Node childNode) {
		return XMI_ID_DECL + childNode.getNodeValue()
				+ "']/Foundation.Core.Generalization.parent/*/@xmi.idref";
	}

	protected String getSuperTypeAsInterfacePath() {
		return FOUNDATION_CORE_DEPENDENCY;
	}

	protected String getSupertypeParentAsInterfacePath(Node childNode) {
		return XMI_ID_DECL + childNode.getNodeValue()
				+ "']/Foundation.Core.Dependency.supplier/*/@xmi.idref";
	}

	protected String getIDPath(String xmiID) {
		return XMI_ID_DECL + xmiID + "']/Foundation.Core.ModelElement.name";
	}

	protected String getAggregatorPath() {
		return FOUNDATION_CORE_AGGREGATION;
	}

	protected String getCompositePath() {
		return FOUNDATION_CORE_COMPOSITE;
	}

	protected String getNavigablePath() {
		return FOUNDATION_CORE_NAVIGABLE;
	}

	protected String getOrderingPath() {
		return FOUNDATION_CORE_ORDERING;
	}

	protected String getVisibilityPath() {
		return FOUNDATION_CORE_VISIBILITY;
	}

	protected String getStaticPath() {
		return FOUNDATION_CORE_OWNERSCOPE;
	}

	protected String getDocumentPath(Node node) {
		return "//Foundation.Extension_Mechanisms.TaggedValue[.//Foundation.Extension_Mechanisms.TaggedValue.modelElement/Foundation.Core.ModelElement/@xmi.idref ='"
				+ getID(node) + "']";
	}

	protected String getParameterPath() {
		return FOUNDATION_CORE_PARAMETER;
	}

	protected String getParameterTypePath() {
		return FOUNDATION_CORE_PARAMETER_TYPE;
	}

	protected String getParameterKindPath() {
		return FOUNDATION_CORE_PARAMETER_KIND;
	}

	protected String getAttributeFinalIndicatorPath() {
		return FOUNDATION_CORE_ATTRIBUTE_CHANGEABILITY;
	}

	private String getChangeabilityPath() {
		return FOUNDATION_CORE_CHANGEABILITY;
	}

	protected String getTagNameValueValuePath() {
		return FOUNDATION_EXTENSTION_MECHANISMS_TAGGEDVALUE_VALUE;
	}

	protected String getTaggedValueTaggedValuePath() {
		return FOUNDATION_CORE_TAGGED_VALUE;
	}

	protected String getDefaultValuePath() {
		return FOUNDATION_CORE_INITIAL_VALUE;
	}

	protected String getRequiredIndicatorPath(Node attributeNode) {
		return "//Foundation.Extension_Mechanisms.Stereotype[contains(Foundation.Core.ModelElement.name/text(), 'OID') and .//Foundation.Core.ModelElement/@xmi.idref ='"
				+ getID(attributeNode) + "']";
	}

	protected String getMultiplicityPath(Node node) {
		return ".//Foundation.Data_Types.Multiplicity[@xmi.id='"
				+ node.getNodeValue() + "']";
	}

	protected String getMultiplicityUpperPath() {
		return FOUNDATION_MULTIPLICITY_RANGE;
	}

	private String getTypeIDRef(Node node) {
		String typeIDRef = null;
		String attributeTypeIDRefPath = "Foundation.Core.StructuralFeature.type/*/@xmi.idref";
		try {
			Node n = getXPathAPI().selectSingleNode(node,
					attributeTypeIDRefPath);
			if (n != null)
				typeIDRef = n.getNodeValue();
			else
				logDebugMessage(
						"XMIProvider10:findType() - Foundation.Core.StructuralFeature.type is null for node "
								+ getElementName(node));
		} catch (Exception exc) {
			logErrorMessage("XMIProvider10.getTypeIDRef(Node) element:"
					+ getElementName(node) + " - " + exc);
		}
		return typeIDRef;
	}
	
	private boolean getIndicator( Node attributeNode, String indicator ) {
		String staticPath = getTaggedValueTaggedValuePath();
		String name = getElementName(attributeNode);

		try {
			NodeList nodeList = getXPathAPI().selectNodeList(attributeNode,
					staticPath);
			if (nodeList != null) {
				String tagPath = getTagNameValueTagDefinition();
				String valuePath = FOUNDATION_EXTENSTION_MECHANISMS_TAGGEDVALUE_VALUE;
				int numNodes = nodeList.getLength();

				for (int i = 0; i < numNodes; i++) {
					Node tempNode = nodeList.item(i);
					Node tagNode = getXPathAPI().selectSingleNode(tempNode,
							tagPath);
					Node valueNode = getXPathAPI().selectSingleNode(tempNode,
							valuePath);

					if (tagNode == null || valueNode == null
							|| !tagNode.getChildNodes().item(0).getNodeValue()
									.equals(indicator)
							|| !valueNode.getChildNodes().item(0).getNodeValue()
									.equals("true")) {
						// no_op
					}
					else
						return true;
				}
			}
		} 
		catch (Exception exc) {
			final String msg = "Failure during locating the transient indicator for attribute node "
					+ name;
			LOGGER.warning(msg);
		}

		return false;
		
	}

	// attributes
	private static final String SIBLING_ASSOCIATION_END_PHRASE 						= "preceding-sibling::Foundation.Core.AssociationEnd | following-sibling::Foundation.Core.AssociationEnd";
	private static final String FOUNDATION_CORE_MODELELEMENT_NAME 					= "Foundation.Core.ModelElement.name";
	private static final String FOUNDATION_EXTENSTION_MECHANISMS_TAGGEDVALUE_TAG 	= "Foundation.Extension_Mechanisms.TaggedValue.tag";
	private static final String FOUNDATION_EXTENSTION_MECHANISMS_TAGGEDVALUE_VALUE 	= "Foundation.Extension_Mechanisms.TaggedValue.value";
	private static final String FOUNDATION_CORE_OPERATION							= "*/Foundation.Core.Operation";
	private static final String FOUNDATION_CORE_ABSTRACT							= "Foundation.Core.GeneralizableElement.isAbstract/@xmi.value";
	private static final String FOUNDATION_CORE_GENERALIZATION						= "Foundation.Core.GeneralizableElement.generalization/Foundation.Core.Generalization";
	private static final String FOUNDATION_CORE_DEPENDENCY							= "Foundation.Core.ModelElement.clientDependency/Foundation.Core.Dependency";
	private static final String FOUNDATION_CORE_AGGREGATION							= "Foundation.Core.AssociationEnd.aggregation/@xmi.value";
	private static final String FOUNDATION_CORE_COMPOSITE							= "Foundation.Core.AssociationEnd.composite/@xmi.value";
	private static final String FOUNDATION_CORE_NAVIGABLE							= "Foundation.Core.AssociationEnd.isNavigable/@xmi.value";
	private static final String FOUNDATION_CORE_ORDERING							= "Foundation.Core.AssociationEnd.ordering/@xmi.value";
	private static final String FOUNDATION_CORE_VISIBILITY							= "Foundation.Core.ModelElement.visibility/@xmi.value";
	private static final String FOUNDATION_CORE_OWNERSCOPE							= "Foundation.Core.Feature.ownerScope/@xmi.value";
	private static final String FOUNDATION_CORE_PARAMETER							= "*/Foundation.Core.Parameter";
	private static final String FOUNDATION_CORE_PARAMETER_TYPE						= "Foundation.Core.Parameter.type/*/@xmi.idref";
	private static final String FOUNDATION_CORE_PARAMETER_KIND						= "Foundation.Core.Parameter.kind/@xmi.value";
	private static final String FOUNDATION_CORE_CHANGEABILITY						= "Foundation.Core.AssociationEnd.changeability/@xmi.value";
	private static final String FOUNDATION_CORE_ATTRIBUTE_CHANGEABILITY				= "Foundation.Core.StructuralFeature.changeability/@xmi.value";
	private static final String FOUNDATION_CORE_TAGGED_VALUE						= "Foundation.Core.ModelElement.taggedValue/Foundation.Extension_Mechanisms.TaggedValue";
	private static final String FOUNDATION_CORE_INITIAL_VALUE						= "Foundation.Core.Attribute.initialValue/Foundation.Data_Types.Expression/Foundation.Data_Types.Expression.body";
	private static final String FOUNDATION_MULTIPLICITY_RANGE						= ".//Foundation.Data_Types.MultiplicityRange.upper";
	private static final String XMI_ID_REF 											= "@xmi.idref";
	private static final String XMI_ID_DECL											= "//*[@xmi.id='";
	private static final Logger LOGGER 												= Logger.getLogger(XMIProvider10.class.getName());

}
