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

import com.cloudmigrate.codetemplate.*;
import com.cloudmigrate.codetemplate.model.*;
import com.cloudmigrate.codetemplate.model.association.*;
import com.cloudmigrate.codetemplate.model.attribute.*;
import com.cloudmigrate.codetemplate.model.method.*;
import com.cloudmigrate.codetemplate.parser.*;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Handles the implementation of all state related methods declared on a ClassObject
 * @author cloudMigrate, Inc.
 *
 */
public class ClassObjectData extends AppGenObject 
	implements IClassObjectData
{
	/**
	 * Constructor - binds with the source classObject
	 * @param classObject
	 */
	public ClassObjectData( ClassObject classObject)
	{ this.classObject = classObject; }
	
	public boolean hasAttributes()
	{ return (!classObject.getAttributesOrderedInHierarchy(true).isEmpty()); }

	public List<AttributeObject> getEnumerators() {
		return this.classObject.getAttributes().stream()
					.filter(AttributeObject::isFromEnumerator)
					.collect(Collectors.toList());
	}
	

	public List<List> getAggregateAttributesAsAttributes() {
		List<List> aggregates = new ArrayList<>();

		this.classObject.getAggregateAttributes().forEach( aggregate -> aggregates.add(aggregate.getAttributes() ));

		return aggregates;
	}

	private boolean hasParent()
	{ return classObject.hasParent(); }
	
	private ClassObject getParent()
	{ return (ClassObject)(classObject.getParent()); }
	
	public AssociationEndObject getAssociation(String roleName)
	{
		for( AssociationEndObject association : classObject.getAssociations() )
		{
			if( roleName.equalsIgnoreCase( association.getRoleName() ) )
				return( association );
		}
		
		AssociationEndObject associationFromParent = null;
		
		
		getParentHelper();
		
		if ( parentClassObject != null )
			associationFromParent = parentClassObject.getAssociation( roleName );

		if ( associationFromParent == null )
			AppGenObject.logInfoMessage("Unable to find an association by the name of "
											+ roleName + " for entity : " + classObject.getName());
		
		return associationFromParent;
	}
	
	public List<AttributeObject> getUserIdentifiableAttributes()
	{
		return classObject.getAttributes().stream()
				.filter( AttributeObject::isUserIdentifiable )
				.collect(Collectors.toList());
	}

	public List<AttributeObject> getUserModifiableAttributes() {
		return classObject.getAttributes().stream()
				.filter( AttributeObject::isUserModifiable )
				.collect(Collectors.toList());
	}

	public String getUserIdentifiableNames() { 
		StringBuilder userIdentifiableName =  new StringBuilder();

		for( AttributeObject attribute : this.getUserIdentifiableAttributes() ) {
			if (userIdentifiableName.length() != 0)
				userIdentifiableName.append( " " );
			
			userIdentifiableName.append( attribute.getName() );
		}

		return (userIdentifiableName.toString());
	}

	public List<AttributeObject> getUserViewableAttributes() { 
		return getAttributesOrderedInHierarchy(false)
				.stream()
				.filter( AttributeObject::isUserViewable )
				.collect(Collectors.toList());	
	}

	public List<AttributeObject> getAttributesOrdered(boolean includePKs) {
		List<AttributeObject> attributesOrdered = new ArrayList<>();

		if (includePKs) {
			attributesOrdered = getPrimaryKeyAttributes();
		}

		List<AttributeObject> fks 				= new ArrayList<>();
		List<AttributeObject> notViewable 		= new ArrayList<>();
		List<AttributeObject> notModifiable 	= new ArrayList<>();
		List<AttributeObject> others 			= new ArrayList<>();

		
		classObject.getAttributes(false).forEach( attributeObject -> {
			if (attributeObject.isForeignKey()  ) {
					fks.add(attributeObject);
			}
			else {
				if (!attributeObject.isUserModifiable())
					notModifiable.add(attributeObject);
				else if (!attributeObject.isUserViewable())
					notViewable.add(attributeObject);
				else
					others.add(attributeObject);
			}
		});

		attributesOrdered.addAll(fks);
		attributesOrdered.addAll(others);
		attributesOrdered.addAll(notViewable);
		attributesOrdered.addAll(notModifiable);

		if (classObject.canBeVersioned()) {
			try {
				AttributeObject attribute = new AttributeObject();
				attribute.setClassObject(classObject);
				attribute.setName( "versionID" );
				attribute.setType( "Long" );
				attribute.setDefaultValue("1");
				attribute.setVisibility("protected");
				attribute.isPrimaryKey(false);
				attribute.canBeNull(true);
				attribute.isIntrinsicType(false);
				attribute.isStatic(false);
				attribute.isFinal(false);
				attribute.isTransient(false);
				attribute.isVolatile(false);
				
				attribute.isFromUserModel(false);
				attribute.isUserViewable(false);
				attributesOrdered.add(attribute);
				logInfoMessage("Versioning applied...creating attribute versionID of type Long");
			}
			catch (Exception exc) {
				logErrorMessage("Failed to create an attribute for version id.");
			}
		}

		return attributesOrdered;
	}

	public List<AttributeObject> getRequiredDirectAttributes( boolean includePKs )
	{
		List<AttributeObject> requiredAttributes = getDirectAttributes( includePKs );
		Iterator<AttributeObject> iter = requiredAttributes.iterator();
		AttributeObject attribute = null;
		
		while ( iter.hasNext() ) {
			attribute = iter.next();
			
			if ( attribute.canBeNull() || attribute.isBoolean() || attribute.isFromEnumerator() ) {
				iter.remove();
			}
		}
		return( requiredAttributes );
	}
	
	public List<AttributeObject> getDirectAttributes( boolean includePKs )
	{
		return classObject.getAttributes(includePKs)
					.stream()
					.filter(attributeObject -> 	!attributeObject.isFromAssociation() &&
												!attributeObject.isComposite() )
					.collect(Collectors.toList());
	}

	public List<AttributeObject> getAttributesOrderedInHierarchy()
	{
		return getAttributesOrderedInHierarchy(false);
	}

	public List<AttributeObject> getAttributesOrderedInHierarchy(boolean includePKs)
	{
		List<AttributeObject> coll = new ArrayList<>();
		List<AttributeObject> pks = new ArrayList<>();
		List<AttributeObject> fks = new ArrayList<>();
		List<AttributeObject> direct = new ArrayList<>();
		List<AttributeObject> composites = new ArrayList<>();
		List<AttributeObject> associations = new ArrayList<>();
		List<AttributeObject> enumerators = new ArrayList<>();
		Iterator iter = getAttributesOrderedInHierarchyHelper(includePKs)
				.iterator();
		AttributeObject attributeObject = null;

		while (iter.hasNext()) {
			attributeObject = (AttributeObject) iter.next();
			if (includePKs && attributeObject.isPrimaryKey())
				pks.add(attributeObject);
			else if (!attributeObject.isComposite())
				composites.add(attributeObject);
			else if (attributeObject.isFromAssociation())
				associations.add(attributeObject);
			else if (attributeObject.isFromEnumerator())
				enumerators.add(attributeObject);
			else if (attributeObject.isForeignKey() && includePKs){
				fks.add(attributeObject);
			}
		}
		coll.addAll(pks);
		coll.addAll(direct);
		coll.addAll(enumerators);
		coll.addAll(composites);
		coll.addAll(associations);
		coll.addAll(fks);
		return coll;
	}

	public List<List> getAttributesOrderedAsCollectionsInHierarchy(
			boolean includePKs)
	{
		List<List> coll = new ArrayList<>();
		List<AttributeObject> pks = new ArrayList<>();
		List<AttributeObject> fks = new ArrayList<>();
		List<AttributeObject> direct = new ArrayList<>();
		List<AttributeObject> composites = new ArrayList<>();
		List<AttributeObject> singleAssociations = new ArrayList<>();
		List<AttributeObject> multipleAssociations = new ArrayList<>();
		
		getAttributesOrderedInHierarchyHelper(includePKs).forEach( attributeObject -> {	

			if (includePKs && attributeObject.isPrimaryKey())
				pks.add(attributeObject);
			else if (attributeObject.isComposite())
				composites.add(attributeObject);
			else if (attributeObject.isFromAssociation())
			{
				if (attributeObject.isFromSingleValueAssociation())
					singleAssociations.add(attributeObject);
				else
					multipleAssociations.add(attributeObject);
			}
			else if (attributeObject.isFromEnumerator())
				direct.add(attributeObject);
			else if (attributeObject.isForeignKey())
			{
				if (includePKs)
					fks.add(attributeObject);
			}
			else // must be a direct attribute
			{
				direct.add(attributeObject);
			}
		});
		coll.add(pks);
		coll.add(direct);
		coll.add(composites);
		coll.add(singleAssociations);
		coll.add(multipleAssociations);
		coll.add(fks);

		return coll;
	}

	public List<AttributeObject> getAttributesOrderedInHierarchyHelper(boolean includePKs)
	{
		List<AttributeObject> coll = new ArrayList<>();

		getParentHelper();
		if (parentClassObject != null)
			coll.addAll(parentClassObject.getAttributesOrderedInHierarchyHelper(
					includePKs));

		coll.addAll(getAttributesOrdered(includePKs));

		return coll;
	}

	public AttributeObject getFirstPrimaryKey()
	{
		List<AttributeObject> attribs = getAllPrimaryKeysInHierarchy();
		
		return( !attribs.isEmpty() ? attribs.iterator().next() : null );
	}

	public List<AttributeObject> getAllPrimaryKeysInHierarchy()
	{
		List<AttributeObject> keys = this.classObject.getAttributes()
					.stream()
					.filter( AttributeObject::isPrimaryKey )
					.collect(Collectors.toList());
		
		getParentHelper();
		if ( parentClassObject != null )
			keys.addAll( parentClassObject.getAllPrimaryKeysInHierarchy());

		return keys;
	}

	public List<AttributeObject> getAllComposites()
	{
		List<AttributeObject> keys = classObject.getAttributes()
				.stream()
				.filter( AttributeObject::isComposite )
				.collect(Collectors.toList());

		ClassObject parent = getParent(); 
		if ( parent != null )
			keys.addAll( parent.getAllComposites() );
		
		return keys;
	}
	
	/**
	 * Walks the tree starting at this node interrogating each parent for
	 * it's primary key attributes.
	 * 
	 * @param coll
	 */
	public List<AttributeObject> getAllKeyFieldsInHierchy(List<AttributeObject> coll)
	{
		getParentHelper();
		if (parentClassObject != null)
			coll = parentClassObject.getAllKeyFieldsInHierchy(coll);
		
		coll.addAll( classObject.getAttributes()
						.stream()
						.filter(AttributeObject::isPrimaryKey)
						.collect(Collectors.toList()) );

		return coll;
	}

	public List<AssociationEndObject> getSingleAssociationsWithoutSourceRole()
	{
		List<AssociationEndObject> coll 	= getSingleAssociations( false /*no composites*/ );
		Iterator<AssociationEndObject> iter  	= coll.iterator();
		AssociationEndObject association		= null;
		
		while( iter.hasNext() )
		{
			association = iter.next();
			if ( association.hasSourceRole() )
				iter.remove();
		}
		return( coll );
	}

	public List<AssociationEndObject> getSingleAssociationsWithSourceRole()
	{
		List<AssociationEndObject> coll 	= getSingleAssociations( false /*no composites*/ );
		Iterator<AssociationEndObject> iter  	= coll.iterator();
		AssociationEndObject association 		= null;
		
		while( iter.hasNext() )
		{
			association = iter.next();
			if ( !association.hasSourceRole() )
				iter.remove();
		}
		
		return( coll );
	}
	
	public List<AssociationEndObject> getSingleAssociations()
	{
		return (getSingleAssociations(true /* include composistes */));
	}

	public List<AssociationEndObject> getSingleAssociations(boolean bIncludeComposites)
	{
		return classObject.getAssociations()
					.stream()
					.filter( associationEndObject -> !associationEndObject.isMultivalued()
														&& !associationEndObject.isZeroToZero()
														&& associationEndObject.isNavigable()
														&& ModelParser.hasIdentity(associationEndObject.getType())
														&& (bIncludeComposites || !associationEndObject.isComposite()))
					.collect(Collectors.toList());
		
	}

	public List<AssociationEndObject> getMultipleAssociations()
	{
		return classObject.getAssociations()
				.stream()
				.filter( associationEndObject -> associationEndObject.isMultivalued()
												&& associationEndObject.isNavigable() )
				.collect(Collectors.toList());
	}

	public boolean hasUIListableMultiAssociation()
	{
		Iterator<AssociationEndObject> iter = getMultipleAssociations().iterator();
		
		while (iter.hasNext()) {
			if (iter.next().isListable())
				return true;
		}
		return (false);
	}

	public void addMethod(MethodObject method) {
		if (methodIsUnique(method)){
			final String msg = "Adding method " + method.toString() 
								+ " on class " + classObject.getName();
			LOGGER.info(msg); 
			classObject.getBusinessMethods().add(method);
		}
		else {
			final String msg = "Duplicate method " + method.toString()
			+ " found, but will not be added to class " + classObject.getName();
			LOGGER.info(msg);
		}
	}

	public void removeMethod(MethodObject method) {
		classObject.getBusinessMethods().remove(method);
	}

	public boolean methodIsUnique(MethodObject methodArg) {
		for( MethodObject method : classObject.getBusinessMethods() )
		{
			// found one,so not unique
			if (method.toString().equals(methodArg.toString()))
				return( false );
		}
		return (true);
	}

	public void addAttribute(AttributeObject attributeObject) {
		String attributeName = attributeObject.getName();
		if (attributeName.indexOf("lnk") == -1)
		{
			if (classObject.findAttribute( attributeName ) == null)
			{
				final String msg = "Adding attribute " + attributeName
									+ ":" + attributeObject.getType() + "["
									+ attributeObject.getDefaultValue() + "]";
			
				LOGGER.info(msg);
				
				classObject.getAttributes().add( attributeObject );
			}
			else
			{
				final String msg = "Class " + classObject.getName() + " already has an attribute name " + attributeName + ".";
				LOGGER.log(Level.WARNING, msg );
			}
		}
	}

	/**
	 * Returns all ClassObject names in the current hierarchy.
	 * 
	 * @return
	 */
	public List<String> getClassNamesInHierarchy()
	{
		List<String> classNames = new ArrayList<>();
		classNames.add(classObject.getName());

		getParentHelper();
		if ( parentClassObject != null )
			classNames.addAll(parentClassObject.getClassNamesInHierarchy());

		return classNames;
	}

	public List<String> getAssociationTypes( String excludeType ) {
		return this.classObject.getAssociations()
					.stream()
					.map(AssociationEndObject::getType)
					.distinct()
					.filter( type -> !type.equalsIgnoreCase( excludeType ) )
					.collect(Collectors.toList());
	}
	
	public List<AttributeObject> getPrimaryKeyAttributes()
	{
		return this.classObject.getAttributes()
					.stream()
					.filter( attributeObject -> attributeObject != null && attributeObject.isPrimaryKey())
					.collect(Collectors.toList());
	}

	public List<AggregateAttributeObject> getAggregateAttributes()
	{
		final List<AggregateAttributeObject> aggregates = new ArrayList<>();
		
		this.classObject.getAttributes()
					.stream()
					.filter( attributeObject -> attributeObject instanceof AggregateAttributeObject )
					.collect(Collectors.toList()).forEach( aggregate -> aggregates.add((AggregateAttributeObject)aggregate));
		return aggregates;
	}
	
	public String toString()
	{
		return classObject.getName();
	}
	
	protected void getParentHelper()
	{
		if ( hasParent() && parentClassObject == null ) {
				parentClassObject = getParent();
		}
	}
	// attributes
	
	protected ClassObject classObject		= null;
	protected ClassObject parentClassObject	= null;
	private static final Logger LOGGER 		= Logger.getLogger(ClassObjectData.class.getName());

}