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
package com.realmethods.codetemplate.model.classes;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.stream.Collectors;

import com.realmethods.codetemplate.model.BaseModelObject;
import com.realmethods.codetemplate.model.association.AssociationEndObject;
import com.realmethods.codetemplate.model.attribute.AttributeObject;
import com.realmethods.codetemplate.model.attribute.AggregateAttributeObject;
import com.realmethods.codetemplate.model.method.MethodObject;
import com.realmethods.codetemplate.parser.ModelParser;
import com.realmethods.codetemplate.model.container.ContainerObject;

/**
 * Represents the notion of a Class entity discovered in a supported model type.
 * 
 * @author realMethods, Inc.
 * 
 */
public class ClassObject extends BaseModelObject implements IClassObjectData, IClassObjectAction {
	/**
	 * Default constructor
	 * 
	 * Binds it with its data handler and its action handler
	 */
	public ClassObject() {
		data 	= new ClassObjectData(this);
		action 	= new ClassObjectAction(this);
	}

	/**
	 * Copy constructor
	 * 
	 * @param classObject
	 */
	public ClassObject(ClassObject classObject) {
		this();
		copy(classObject);
	}

	public String getAttributesAsString(boolean bIncludePrimaryKeys, 
										boolean bIncludeTypes, 
										boolean bIncludeAssociations,
										String delim,
										String suffix ) {
		List<AttributeObject> attribs = 		getAttributesOrderedInHierarchy(bIncludePrimaryKeys).stream()             
										        .filter(entry -> (bIncludeAssociations) || !entry.isFromAssociation())
										        .filter(entry -> !entry.isComposite())
										        .collect(Collectors.toList());

		
		StringBuilder builder	= new StringBuilder();
		int count				= 0;
		
		for( AttributeObject attribute : attribs ) {
			if ( bIncludeTypes ) {
				builder.append( attribute.getType() );
				builder.append( " " );
			}
			builder.append( attribute );
			if ( suffix != null )
				builder.append( suffix );
			if ( delim != null && ++count < attribs.size() )
				builder.append( delim );
		}
		
		return builder.toString();
		
	}

	public List<AttributeObject> getAttributesOnly(boolean includeHierarchy, boolean includePKs) {
		List<AttributeObject> all 	= !includeHierarchy 
										? this.getAttributes(includePKs)
										: this.getAttributesOrderedInHierarchy(includePKs);
		
		return all.stream()             
                .filter(entry -> !entry.isFromAssociation())
                .filter(entry -> !entry.isComposite())
                .collect(Collectors.toList());		
	}
	
	public AttributeObject findAttributeAsBestFitName()  {
		
		List<AttributeObject> all 				= this.getAttributesOrderedInHierarchy(false);
		
		List<AttributeObject> results = all.stream()             
                .filter(entry -> !entry.isFromAssociation())
                .filter(entry -> !entry.isComposite())
                .filter(entry -> entry.getName().toUpperCase().equalsIgnoreCase("NAME") ||  entry.getName().toUpperCase().contains("NAME") )
                .collect(Collectors.toList());
		
		if ( results.isEmpty())
			return (all.get(0) ); // return the first and take a chance it is the most meaninful
		else
			return( results.get(0) ); // return the first one found
	}

	/**
	 * Returns the attributes with order.
	 * 
	 * @return
	 */
	public List<AttributeObject> getAttributes() {
		return this.getAttributes(true);
	}

	/**
	 * Assigns the attributes field with the provided argument.
	 * 
	 * @param attributes
	 */
	public void setAttributes( List<AttributeObject> attributes) {
		this.attributes = attributes;
	}

	/**
	 * Returns the attributes, conditionally including the primary keys.
	 * 
	 * @param includePrimaryKeys
	 * @return
	 */
	public List<AttributeObject> getAttributes(boolean includePrimaryKeys)  {
		List<AttributeObject> returnList = null;
		
		if( includePrimaryKeys )
			returnList = this.attributes;
		else {
			returnList = new ArrayList<>();
			for( AttributeObject attributeObject : this.attributes ) {
				if ( !attributeObject.isPrimaryKey() )
					returnList.add( attributeObject );
			}
		}
		
		return( returnList );
	}

	/**
	 * Returns the attributes.
	 * 
	 * @return
	 */
	public List<AttributeObject> getAttributeValues() {
		return getAttributes();
	}

	/**
	 * Returns the associations field.
	 * 
	 * @return
	 */
	public List<AssociationEndObject> getAssociations() {
		return associations;
	}

	/*
	 * Assigns the associations field with the provided argument.
	 * 
	 * @param associations
	 */
	public void setAssociations( List<AssociationEndObject> associations ) {
		if ( associations != null ) {
			for ( AssociationEndObject association : associations )
				addAssociation( association );
		}
	}

	/**
	 * Returns the attributes designated as aggregates.
	 * 
	 * @return 
	 */
	public List<AggregateAttributeObject> getAggregateAttributes() {
		return data.getAggregateAttributes();
	}

	/**
	 * Returns the enumerators as a List of AttributeObjects.
	 * 
	 * @return
	 */
	public List<AttributeObject> getEnumerators() {
		return data.getEnumerators();
	}

	/**
	 * Returns a List of aggregate attribute lists.
	 * 
	 * @return
	 */
	public List<List> getAggregateAttributesAsAttributes() {
		return data.getAggregateAttributesAsAttributes();
	}

	/**
	 * Returns the association with the roleName.
	 * 
	 * @param roleName
	 * @return
	 */
	public AssociationEndObject getAssociation(String roleName) {
		return data.getAssociation(roleName);
	}

	/**
	 * Returns the businessMethods field.
	 * 
	 * @return
	 */
	public List<MethodObject> getBusinessMethods() {
		return businessMethods;
	}

	/**
	 * Assigns the businessMethods field from the provided argument.
	 * 
	 * @param methods
	 */
	public void setBusinessMethods(List<MethodObject> methods) {
		businessMethods = methods;
	}

	/**
	 * Return all methods designated as findAllBy
	 * 
	 * @return
	 */
	public List<MethodObject> getFindAllByMethods() {
		return businessMethods.stream()
				.filter( MethodObject::isFindAllBy )
				.collect(Collectors.toList() );
	}

	/**
	 * Returns all the children in the hierarchy with this instance as the root node.
	 * 
	 * @return
	 */
	public List<ClassObject> getChildren() {
		return ModelParser.modelParser().getAllClassesInHierarchy().stream()
			.filter(classObject -> classObject != this && classObject.hasParent() && classObject.getParentName().equalsIgnoreCase(name))
			.collect(Collectors.toList());
	}

	/**
	 * Returns true/false if children exist
	 * 
	 * @return
	 */
	public boolean hasChildren() {
		return (!getChildren().isEmpty());
	}

	/**
	 * Returns only the names of the ClassObjects in this hierarchy.
	 * 
	 *  @return
	 */
	public List<String> getClassNamesInHierarchy() {
		return data.getClassNamesInHierarchy();
	}

	/**
	 * Returns the attributes designated as UserModifiable.
	 * 
	 *  @return
	 */
	public List<AttributeObject> getUserModifiableAttributes() {
		return data.getUserModifiableAttributes();
	}

	/**
	 * Returns the attributes designated as UserViewable.
	 * 
	 *  @return
	 */
	public List<AttributeObject> getUserViewableAttributes() {
		return data.getUserViewableAttributes();
	}

	/**
	 * Returns the attributes order, optionally include the primary key attributes
	 * 
	 * @param includePKs
	 * @return
	 */
	public List<AttributeObject> getAttributesOrdered(boolean includePKs) {
		return data.getAttributesOrdered(includePKs);
	}

	public List<AttributeObject> getRequiredDirectAttributes(boolean includePKs) {
		return data.getRequiredDirectAttributes(includePKs);
	}

	public List<AttributeObject> getDirectAttributes(boolean includePKs) {
		return data.getDirectAttributes(includePKs);
	}

	public List<AttributeObject> getAttributesOrderedInHierarchy() {
		return data.getAttributesOrderedInHierarchy();
	}

	public List<AttributeObject> getAttributesOrderedInHierarchy(boolean includePKs) {
		return data.getAttributesOrderedInHierarchy(includePKs);
	}

	public List<List> getAttributesOrderedAsCollectionsInHierarchy(boolean includePKs) {
		return data.getAttributesOrderedAsCollectionsInHierarchy(includePKs);
	}

	public List<AttributeObject> getAttributesOrderedInHierarchyHelper(boolean includePKs) {
		return data.getAttributesOrderedInHierarchyHelper(includePKs);
	}

	public AttributeObject getFirstPrimaryKey() {
		return data.getFirstPrimaryKey();
	}

	public List<AttributeObject> getAllPrimaryKeysInHierarchy() {
		return data.getAllPrimaryKeysInHierarchy();
	}

	public List<AttributeObject> getAllComposites() {
		return data.getAllComposites();
	}

	public List<AttributeObject> getAllKeyFieldsInHierchy(List<AttributeObject> coll) {
		return data.getAllKeyFieldsInHierchy(coll);
	}

	public List<AssociationEndObject> getSingleAssociationsWithoutSourceRole() {
		return data.getSingleAssociationsWithoutSourceRole();
	}

	public List<AssociationEndObject> getSingleAssociationsWithSourceRole() {
		return data.getSingleAssociationsWithSourceRole();
	}

	public List<AssociationEndObject> getSingleAssociations() {
		return data.getSingleAssociations();
	}

	public List<AssociationEndObject> getSingleAssociations(boolean bIncludeComposites) {
		return data.getSingleAssociations(bIncludeComposites);
	}

	public List<AssociationEndObject> getMultipleAssociations() {
		return data.getMultipleAssociations();
	}

	public boolean hasUIListableMultiAssociation() {
		return data.hasUIListableMultiAssociation();
	}

	public boolean hasAttributes() {
		return data.hasAttributes();
	}

	public void applySelfToAssociations() {
		action.applySelfToAssociations();
	}

	public void sortAttributes() {
		action.sortAttributes();
	}

	public void addMethod(MethodObject method) {
		action.addMethod(method);
	}

	public void removeMethod(MethodObject method) {
		action.removeMethod(method);
	}

	public boolean methodIsUnique(MethodObject method) {
		return action.methodIsUnique(method);
	}

	public void addAttribute(AttributeObject attributeObject) {
		action.addAttribute(attributeObject);
	}

	public void addAssociation(AssociationEndObject association) {
		action.addAssociation(association);
	}

	public void removeAttribute(AttributeObject attribute) {
		action.removeAttribute(attribute);
	}

	public void removeAssociation(AssociationEndObject association) {
		action.removeAssociation(association);
	}

	public void modifyPrimaryKeysTo(String transferToType) {
		action.modifyPrimaryKeysTo(transferToType);
	}

	public AttributeObject transformAssociationToAttribute(AssociationEndObject associationEndObject) {
		return action.transformAssociationToAttribute(associationEndObject);
	}

	public List<AttributeObject> getUserIdentifiableAttributes() {
		return data.getUserIdentifiableAttributes();
	}

	public String getUserIdentifiableNames() {
		return data.getUserIdentifiableNames();
	}

	public List<String> getAssociationTypes( String excludeType ) {		
		return data.getAssociationTypes( excludeType );
	}
	
	public List<String> getAssociationTypes() {
		return getAssociationTypes( null );
	}
	
	/**
	 * Returns all attributes designated as primary keys.
	 * 
	 * @return
	 */
	public List<AttributeObject> getPrimaryKeyAttributes() {
		return data.getPrimaryKeyAttributes();
	}

	protected List<? extends BaseModelObject> getAllOfLikeType() {
		// it immediately contains none but UML/XMI notation may differ in the
		// future
		return (ModelParser.modelParser().getAllClassesInHierarchy());
	}

	@Override
	/**
	 * Returns true if it has one or more attributes designated as a primary key
	 */
	public boolean hasIdentity() {
		return !getPrimaryKeyAttributes().isEmpty();
	}

	/**
	 * Returns the hasForcedIdentity field.
	 * 
	 * @return
	 */
	public boolean hasForcedIdentity() {
		return hasForcedIdentity;
	}

	/**
	 * Assigns the hasForcedIdentity field from the provided attribute.
	 * 
	 * @param b
	 */
	public void hasForcedIdentity(boolean b) {
		hasForcedIdentity = b;
	}

	@Override
	/**
	 * Returns true if it only has one attribute designated as a primary key.
	 * 
	 * @return
	 */
	public boolean hasSimplePrimaryKey() {
		return getAllPrimaryKeysInHierarchy().size() == 1;
	}

	@Override
	/**
	 * Returns true if it has more than one attribute designated as a primary key 
	 * 
	 * @return
	 */
	public boolean hasCompoundPrimaryKey() {
		return getAllPrimaryKeysInHierarchy().size() > 1;
	}

	/**
	 * Returns the isInterface field.
	 * 
	 * @return
	 */
	public boolean isInterface() {
		return isInterface;
	}

	/**
	 * Assigns the isInterface field using the provided argument.
	 * 
	 * @param b
	 */
	public void isInterface(boolean b) {
		isInterface = b;
	}

	@Override
	public boolean isEnumerator() {
		boolean b = false;
		if (stereotype != null
				&& (stereotype.equalsIgnoreCase("enum") || stereotype.equalsIgnoreCase("enumerator") || stereotype
						.equalsIgnoreCase("enumeration")))
			b = true;
		return b;
	}

	@Override
	public void isEnumerator(boolean b) {
		stereotype = b ? "enum" : null;
	}

	@Override
	public boolean isService() {
		return stereotype != null && stereotype.equalsIgnoreCase("Service");
	}

	/**
	 * Turns a primary key into a non-primary key attribute by setting its
	 * isPrimaryKey(boolean) flag into false
	 */
	public void reCategorizePrimaryKeyAttributes() {
		Iterator<AttributeObject> pks = getPrimaryKeyAttributes().iterator();
		AttributeObject attributeObject;

		while (pks.hasNext()) {
			attributeObject = pks.next();
			attributeObject.isPrimaryKey(false);
		}

	}

	/**
	 * Performs a deep copy of all attributes including Collections
	 * 
	 * @param c
	 */
	public void copy(ClassObject c) {
		super.copy(c);
		isInterface = c.isInterface;
		hasForcedIdentity = c.hasForcedIdentity;
		associations = c.associations;
		attributes = c.attributes;
		businessMethods = c.businessMethods;
	}

	/**
	 * Returns an empty list since from a hierarchy perspective, a class has no children
	 * that themselves are possible a root.
	 * 
	 * @return
	 */
	public List<BaseModelObject> getAllInHierarchy() {
		List<BaseModelObject> all = new ArrayList<>();
		return (all);
	}

	/**
	 * Returns an empty list since from a hierarchy perspective, a class has no children
	 * that themselves are possible a root.
	 * 
	 * @return
	 */
	public List<ClassObject> getAllClassesInHierarchy() {
		List<ClassObject> all = new ArrayList<>();
		return (all);
	}

	@Override
	/**
	 * Returns true/false if there are no non primary key attributes within
	 * the instance or it's hierarchy
	 * @return
	 */
	public boolean isEmpty() {
		boolean includePKs 	= false;
		
		////////////////////////////////////////////////////////////////////////
		// check for non primary key attributes in the hierarchy
		////////////////////////////////////////////////////////////////////////
		return this.getAttributesOrderedInHierarchy(includePKs).isEmpty();
	}
	
	/**
	 * Locates the attribute with the designated name.
	 * 
	 * @param attributeName
	 * @return
	 */
	public AttributeObject findAttribute(String attributeName) {
		return (action.findAttribute(attributeName));
	}

	/**
	 * A sub-class will likely have something to do here
	 */
	public void finishLoading() {
		// no_op
	}
	
	/**
	 * Helper method to appropriate addAssociations with sibling fix-up when there
	 * @param classObject
	 * @param associations
	 * @return
	 */
	public static ClassObject addAssociationsHelper( ClassObject classObject, List<AssociationEndObject> associations ) {
		classObject.setAssociations( associations );
		ClassObject refClassModelObject;
		AssociationEndObject sibling;
		for ( AssociationEndObject association : associations ) {
			refClassModelObject 	= (ClassObject) ModelParser.modelParser().locateBaseModelObject( association.getType() );
			if ( refClassModelObject != null ) {
				sibling = association.getSiblingAssociationEndObject();
				if ( sibling != null ) {
				    sibling.setType( refClassModelObject.getName() );
					refClassModelObject.addAssociation(sibling);
				}
			}
		}

		return classObject;
	}

    /**
     * Returns the context this instance is contained within.  Null is the global context
     * @return
     */
    public ContainerObject getContainmentContext() {
    	return containmentContext;
    }

    /**
     * Assigns the context this instance is contained within.  Null is the global context
     * @return
     */
    public void setContainmentContext( ContainerObject  context ) {
    	containmentContext = context;
    }
	// attributes

	protected boolean isInterface 						= false;
	protected boolean hasForcedIdentity 				= false;

	protected List<AssociationEndObject> associations 	= new ArrayList<>();
	protected List<MethodObject> businessMethods 		= new ArrayList<>();
	protected List<AttributeObject> attributes 			= new ArrayList<>();

	protected ClassObjectData data 						= null;
	protected ClassObjectAction action 					= null;
    protected ContainerObject containmentContext		= null;


}
