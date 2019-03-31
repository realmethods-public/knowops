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

import java.util.List;

import com.cloudmigrate.codetemplate.model.association.AssociationEndObject;
import com.cloudmigrate.codetemplate.model.attribute.*;

/**
 * Simple interface for ClassObjectData
 * 
 * @author realMethods, Inc.
 *
 */
public interface IClassObjectData
{
	public List<AttributeObject> getUserIdentifiableAttributes();
	public String getUserIdentifiableNames();
	public List<AttributeObject> getPrimaryKeyAttributes();	
	public List<String> getClassNamesInHierarchy();
	public boolean hasAttributes();
	public List<AttributeObject> getEnumerators();
	public List<AttributeObject> getUserModifiableAttributes();
	public List<AttributeObject> getUserViewableAttributes();
	public List<AttributeObject> getAttributesOrdered(boolean includePKs);
	public List<AggregateAttributeObject> getAggregateAttributes();
	public List<AttributeObject> getRequiredDirectAttributes( boolean includePKs );
	public List<AttributeObject> getDirectAttributes( boolean includePKs );
	public List<AttributeObject> getAttributesOrderedInHierarchy();
	public List<AttributeObject> getAttributesOrderedInHierarchy(boolean includePKs);
	public List<List> getAttributesOrderedAsCollectionsInHierarchy(boolean includePKs);
	public List<AttributeObject> getAttributesOrderedInHierarchyHelper(boolean includePKs);
	public AttributeObject getFirstPrimaryKey();	
	public List<AttributeObject> getAllPrimaryKeysInHierarchy();
	public List<AttributeObject> getAllComposites();
	public List<AttributeObject> getAllKeyFieldsInHierchy( List<AttributeObject> coll);
	public List<AssociationEndObject> getSingleAssociationsWithoutSourceRole();	
	public List<AssociationEndObject> getSingleAssociationsWithSourceRole();
	public List<AssociationEndObject> getSingleAssociations();	
	public List<AssociationEndObject> getSingleAssociations(boolean bIncludeComposites);
	public List<AssociationEndObject> getMultipleAssociations();
	public boolean hasUIListableMultiAssociation();
}
