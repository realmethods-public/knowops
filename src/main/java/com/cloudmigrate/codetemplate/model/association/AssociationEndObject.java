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
package com.cloudmigrate.codetemplate.model.association;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.cloudmigrate.codetemplate.AppGenObject;
import com.cloudmigrate.codetemplate.model.attribute.AttributeObject;
import com.cloudmigrate.codetemplate.model.classes.ClassObject;
import com.cloudmigrate.codetemplate.parser.*;

/**
 * Encapsulates the concept of an association which forms the basis of a relationship
 * between two ClassObjects.
 * 
 * @author realMethods, Inc.
 *
 */
public class AssociationEndObject extends AppGenObject {
    /**
     * default constructor
     */
    public AssociationEndObject() {
    	// no_op
    }

    /**
     * Returns the classObject field
     * 
     * @return
     */
    public ClassObject getClassObject() {
    	return (classObject);
    }

    /**
     * Assign the parent ClassObject
     * 
     * @param classObject
     */
    public void setClassObject(ClassObject classObject) {
    	this.classObject = classObject;
    }

    /**
     * Returns the visibility field.
     * 
     * @return
     */
    public String getVisibility() {
    	return visibility;
    }

    /**
     * Assigns the visibility field the provided argument.
     * 
     * @param visibility
     */
    public void setVisibility(String visibility) {
    	this.visibility = visibility;
    }

    /**
     * Return the type field.
     * 
     * @return String
     */
    public String getType() {
    	return type;
    }

    /**
     * Assigns the type field the provided argument.
     * 
     * @param type
     */
    public void setType(String type) {
    	this.type = type;
    }

    /**
     * Return the roleName field.
     * 
     * @return String
     */
    public String getRoleName() {
    	return roleName;
    }

    /**
     * Delegates to the getRoleName() since the roleName is the name of the association.
     * 
     * @return String
     */
    public String getName() {
    	return getRoleName();
    }

    /**
     * Returns true/false if the tags field contains a tag named 'sourceRole'
     * 
     * @return boolean
     */
    public boolean hasSourceRole() {
    	return getSourceRole() != null;
    }

    /**
     * If located returns a tag name 'sourceRole' from the tags field.
     * Null is a possibility.
     * 
     * @return
     */
    public String getSourceRole() {
		return tags.get(SOURCE_ROLE);
	}

    /**
     * Adds the srcRole to the tags mapping using key 'sourceRole'.
     * 
     * @param srcRole
     */
    public void setSourceRole(String srcRole) {
    	tags.put(SOURCE_ROLE, srcRole);
    }

    /**
     * Assigns the roleName, ensuring the first char is uppercase and the ends are trimmed.
     * 
     * @param name
     */
    public void setRoleName(String name) {
		if (name != null && name.length() > 0) {
		    if (!Character.isUpperCase(name.charAt(0))) {
		    	roleName = Character.toUpperCase(name.charAt(0)) + name.substring(1, name.length());
		    }
		    else {
		    	roleName = name.trim();
		    }
		} else {
		    roleName = "";
		}
    }

    /**
     * Returns the navigable field.
     * 
     * @return
     */
    public boolean isNavigable() {
    	return isNavigable;
    }

    /**
     * Assigns the isNavigable field the provided argument.
     * 
     * @param is
     */
    public void isNavigable(boolean is) {
    	isNavigable = is;
    }

    /**
     * Returns the isOrdered field.
     * 
     * @return
     */
    public boolean isOrdered() {
    	return isOrdered;
    }

    /**
     * Assigns the isOrdered field the provided argument.
     * 
     * @param is
     */
    public void isOrdered(boolean is) {
    	isOrdered = is;
    }

    /**
     * Returns the isAggregate field.
     * 
     * @return
     */
    public boolean isAggregate() {
    	return isAggregate;
    }

    /**
     * Assigns the isAggregate field the provided argument.
     * 
     * @param is
     */
    public void isAggregate(boolean is) {
    	isAggregate = is;
    }

    /**
     * Returns true/false if there is a sibling end and it is a composite.
     * 
     * @return
     */
    public boolean isComposite() {
		return siblingAssociationEndObject != null
			&& siblingAssociationEndObject.isCompositeHelper();
    }

    /**
     * Assign the composite flag on the sibling association.
     * @param composite
     */
    public void isComposite(boolean composite) {
		if (siblingAssociationEndObject != null)
		    siblingAssociationEndObject.setLocalComposite(composite);
    }

    /**
     * Assigns the composite field the provided argument.
     * 
     * @param composite
     */
    protected void setLocalComposite(boolean composite) {
    	this.isComposite = composite;
    }

    /**
     * Delegates to isComposite()
     * 
     * @return
     */
    protected boolean getLocalComposite() {
    	return (isComposite());
    }

    /**
     * Returns the composite field
     * 
     * @return
     */
    public boolean isCompositeHelper() {
    	return isComposite;
    }

    /**
     * Returns the lowerRange field.
     * 
     * @return
     */
    public int getLowerRange() {
    	return lowerRange;
    }

    /**
     * Assigns the lowerRange field the provided argument.
     * 
     * @param lower
     */
    public void setLowerRange(int lower) {
    	lowerRange = lower;
    }

    /**
     * Returns the upperRange field.
     * 
     * @return
     */
    public int getUpperRange() {
    	return upperRange;
    }

    /**
     * Assigns the upperRange field.
     * 
     * @param upper
     */
    public void setUpperRange(int upper) {
    	upperRange = upper;
    }

    /**
     * determines if the association is part of a many-* relationship
     * 
     * @return
     */
    public boolean isMultivalued() {
    	return upperRange > 1 || upperRange == -1;
    }

    /**
     * determines if the association is part of a one-* relationship
     * 
     * @return
     */
    public boolean isSinglevalued() {
    	return (lowerRange == 0 || lowerRange == 1) && upperRange == 1;
    }

    /**
     * Determines if the association is 0-to-0.
     * 
     * @return
     */
    public boolean isZeroToZero() {
    	return lowerRange == 0 && upperRange == 0;
    }

    /**
     * Determines if the association is 0-to-1.
     * 
     * @return
     */
    public boolean isZeroToOne() {
    	return lowerRange == 0 && upperRange == 1;
    }

    /**
     * Determines if the association is 1-to-1 by ensuring both the instance
     * and it sibling are single valued.
     * 
     * @return
     */
    public boolean isOneToOne() {
    	return siblingAssociationEndObject.isSinglevalued() && isSinglevalued();
    }

    /**
     * Determines if the association is 1-to-* by ensuring this instance is 
     * multivalued and it's sibling is single valued.
     * 
     * @return
     */
    public boolean isOneToMany() {
    	return siblingAssociationEndObject.isSinglevalued() && isMultivalued();
    }

    /**
     * Determines if the association is n-to-* by ensuring this instance is 
     * multi-valued and it's sibling is muli-valued.
     * 
     * @return
     */
    public boolean isManyToMany() {
    	return siblingAssociationEndObject.isMultivalued() && isMultivalued();
    }

    /**
     * Determines if the association is n-to-* by ensuring this instance is 
     * single values and it's sibling is multi-valued.
     * 
     * @return
     */
    public boolean isManyToOne() {
    	return siblingAssociationEndObject.isMultivalued() && isSinglevalued();
    }

    /**
     * only if the lowerRange is 0
     * 
     * @return
     */
    public boolean isNullable() {
    	return lowerRange == 0;
    }

    /**
     * Returns the isFrozen field.
     * 
     * @return
     */
    public boolean isFrozen() {
    	return isFrozen;
    }

    /**
     * Assigns the isFrozen field using the provided argument.
     * 
     * @param isFrozen
     */
    public void isFrozen(boolean isFrozen) {
    	this.isFrozen = isFrozen;
    }

    /**
     * Returns the isAddOnly field.
     * 
     * @return
     */
    public boolean isAddOnly() {
    	return isAddOnly;
    }

    /**
     * Assigns the isAddOnly field the provided argument.
     * 
     * @param isAddOnly
     */
    public void isAddOnly(boolean isAddOnly) {
    	this.isAddOnly = isAddOnly;
    }

    /**
     * Returns the isLazy field.
     * 
     * @return
     */
    public boolean isLazy() {
    	return isLazy;
    }

    /**
     * Assigns the isLazy field the provided argument.
     * 
     * @param lazy
     */
    public void isLazy(boolean lazy) {
    	isLazy = lazy;
    }

    /**
     * Returns the cascadeDelete field.
     * 
     * @return
     */
    public boolean canCascadeDelete() {
    	return cascadeDelete;
    }

    /**
     * Returns the isListable field.
     * 
     * @return
     */
    public boolean isListable() {
    	return (isListable);
    }

    /**
     * Assigns the isListable field the provide argument.
     * 
     * @param b
     */
    public void isListable(boolean b) {
    	isListable = b;
    }

    /**
     * Returns the isPartOfDefaultFetchGroup field.
     * 
     * @return
     */
    public boolean isPartOfDefaultFetchGroup() {
    	return isPartOfDefaultFetchGroup;
    }

    /**
     * Assigns the isPartOfDefaultFetchGroup field the provided argument.
     * 
     * @param b
     */
    public void isPartOfDefaultFetchGroup(boolean b) {
    	isPartOfDefaultFetchGroup = b;
    }

    /**
     * Returns true if both this instance and its sibling are navigable.
     * 
     * @return
     */
    public boolean isBidirectional() {
    	boolean is = false;
		if (isNavigable() && 
				siblingAssociationEndObject != null) {
			is = siblingAssociationEndObject.isNavigable();
		}
	
		return (is);
    }

    /**
     * Returns true false if the association has at least one attribute with an orderBy field of true.
     * 
     * Delegates internally 
     * @return
     */
    public boolean hasOrder() {
    	return (getAttributeOrderedBy() != null);
    }

    /**
     * Discovers the first attribute of the association denoted as orderedBy
     * 
     * @return
     */
    public AttributeObject getAttributeOrderedBy() {
    	List<AttributeObject> list = getAttributes().stream()
    								.filter( AttributeObject::isOrderedBy )
    								.collect(Collectors.toList());
    	if ( !list.isEmpty() )
    		return (list.get(0));
    	else
    		return( null );
    }

    /**
     * Assigns the provided attribute as ordereBy. Removes the designation if an
     * AttributeObject was previously designated as orderedBy
     * 
     * @param attr
     */
    public void setAttributeOrderedBy(AttributeObject attr) {
		AttributeObject currAttr = getAttributeOrderedBy();
	
		if (currAttr != null)
		    currAttr.isOrderedBy(false);
	
		if (attr != null)
		    attr.isOrderedBy(true);
    }

    /**
     * Locates the associated attribute and applies orderBy to it.
     * 
     * @param attrName
     */
    public void setAttributeOrderedBy(String attrName) {
    	setAttributeOrderedBy(getAttribute(attrName));
    }

    /**
     * Returns an attribute with the same name, if found.
     * 
     * @param name
     * @return
     */
    public AttributeObject getAttribute(final String name) {
		AttributeObject attr = null;
	
		if (name != null && name.length() >= 0) {
			attr = attributes
						.stream()
						.filter( attribute -> name.equalsIgnoreCase(attribute.getName()) )
						.collect(Collectors.toList())
						.get(0);
		}
	
		return (attr);
    }

    /**
     * Discovers which AttributeObjects are viewable, meaning not a primarykey,
     * not from an association and designated as viewable in the source model.
     * 
     * @return
     */
    public List<String> getViewableAttributeNames() {
    	return getAttributes()
    			.stream()
    			.filter( attribute -> !attribute.isPrimaryKey() && !attribute.isFromAssociation() && attribute.isUserViewable())
    			.map(AttributeObject::getName)
    			.collect(Collectors.toList());
	}

    /**
     * Assign the cascadeDelete field.
     * 
     * @param delete
     */
    public void canCascadeDelete(boolean delete) {
	cascadeDelete = delete;
    }

    /**
     * Return siblingAssociationEndObject field.
     * 
     * @return
     */
    public boolean hasSiblingAssociationEndObject() {
	return (siblingAssociationEndObject != null);
    }

    /**
     * Returns true if there is a navigable sibling.
     * 
     * @return
     */
    public boolean hasNavigableSiblingAssociationEndObject() {
    	return (siblingAssociationEndObject != null)
    			&& (siblingAssociationEndObject.isNavigable());
    }

    /**
     * Returns the siblingAssociationEndObject field.
     * 
     * @return
     */
    public AssociationEndObject getSiblingAssociationEndObject() {
    	return siblingAssociationEndObject;
    }

    /**
     * Assigns the siblingAssociationEndObject field the provided argument.
     * @param sibling
     */
    public void setSiblingAssociationEndObject(AssociationEndObject sibling) {
    	siblingAssociationEndObject = sibling;
    }

    /**
     * Assigns the attributes field the provided argument.
     * 
     * @param attribs
     */
    public void setAttributes(List<AttributeObject> attribs) {
    	this.attributes = attribs;
    }

    /**
     * Returns the internal List<AttributeObject> as attributes. If not
     * yet assigned, will get them from the containing (bound) classObject.
     * 
     * @return
     */
    public List<AttributeObject> getAttributes() {
		
    	if (attributes != null && !attributes.isEmpty())
		    return attributes;
	
		// if we don't already have a previously assigned set of attributes, go and get them
		// from the bound class
		if (classObject != null) {
		    attributes 						= new ArrayList<>();
		    Iterator<AttributeObject> iter 	= classObject.getAttributesOrderedInHierarchy(true).iterator();
		    AttributeObject attrib 			= null;
	
		    while (iter.hasNext()) {
				try {
				    // make a copy
				    attrib = new AttributeObject(iter.next());
				    attributes.add(attrib);
				} catch (Exception exc) {
				    LOGGER.log( Level.WARNING, "AssociationEndObject.getAttributes()", exc);
				}
		    }
		} else {
		    LOGGER.log( Level.WARNING, "AssociationEndObject:getAttribute() - unable to locate ClassObject "
				    				+ getType()
				    				+ " for association end type : "
				    				+ getRoleName());
		}
		return attributes;
    }

    /**
     * Returns true if the attributes field is non-null and not empty.
     * 
     * @return
     */
    public boolean hasAttributes() {
    	List<AttributeObject> attribs = getAttributes();
    	return (attribs != null && !attribs.isEmpty());
    }

    /**
     * Returns the isFromXMLFile.
     * 
     * No longer required.  Kept for backwards compatibility.
     * 
     * @return
     */
    public boolean isFromXMLFile() {
    	return isFromXMLFile;
    }

    /**
     * Assigns the isFromXMLFile field from the provided argument.
     * 
     * No longer required.  Kept for backwards compatibility.
     * 
     * @return
     */
    public void isFromXMLFile(boolean is) {
    	isFromXMLFile = is;
    }

    /**
     * Combines the name of the association with that of it's sibling, if not
     * null
     * 
     * @return
     */
    public String getCombinedAssociationName() {
		StringBuilder combinedRolename = new StringBuilder();
		String thisRolename = getRoleName();
	
		if (siblingAssociationEndObject != null) {
		    String siblingRolename = siblingAssociationEndObject.getRoleName();
		    if (thisRolename.compareTo(siblingRolename) > 0) {
		    	combinedRolename.append(thisRolename);
		    	combinedRolename.append(siblingRolename);
		    } else {
		    	combinedRolename.append(siblingRolename);
		    	combinedRolename.append(thisRolename);
		    }
		} else
		    combinedRolename.append(thisRolename);
	
		return combinedRolename.toString();
    }

    @Override
    public boolean equals(Object o) {
		boolean b = false;
		if (o instanceof AssociationEndObject)
		    b = toString().equals(o.toString());
		return b;
    }
    
    @Override
    public int hashCode() {
    	return( roleName.hashCode() );
    }
    
    /**
     * the name of the role
     * 
     * @return
     */
    @Override
    public String toString() {
    	return getRoleName();
    }

    /**
     * pretty print - role name with the multiplicity string
     * 
     * @return
     */
    public String toFormattedString() {
    	return getRoleName() + " : " + getMultiplicityAsString();
    }

    /**
     * Pretty print used to return a phrase representation of the multiplicity
     * 
     * @return
     */
    public String getMultiplicityAsString() {
		// only support OneToOne, OneToMany, ManyToOne, and ManyToMany
		String multiplicity = "nothing deduced";
	
		if (isZeroToZero() || isZeroToOne() || isOneToOne())
		    multiplicity = "OneToOne";
		else if (isOneToMany())
		    multiplicity = "OneToMany";
		else if (isManyToMany())
		    multiplicity = "ManyToMany";
		else if (isManyToOne())
		    multiplicity = "ManyToOne";
		else
		{
			final String msg1 = "lower:upper is " + this.lowerRange + ":" + this.upperRange;					
			LOGGER.log( Level.INFO, msg1 );
			
			if ( siblingAssociationEndObject != null ) {
				final String msg2 = "sibling lower:upper is " + this.siblingAssociationEndObject.getLowerRange() + ":" + this.siblingAssociationEndObject.getUpperRange();
				LOGGER.log( Level.INFO, msg2 );
			}
			
		}
		return multiplicity;
    }
    
    /**
     * Assigns the lowerRange and upperRange fields the provied arguments.
     * 
     * @param lower
     * @param upper
     */
    protected void setRanges(int lower, int upper) {
		if (lower == 0 && upper == 1)
		    lower = 1;
	
		lowerRange = lower;
		upperRange = upper;
    }

    // attributes
    protected String refClassName					= null;
    protected String type 							= null;
    protected String visibility 					= "protected";
    protected String roleName 						= "unassigned";
    protected boolean isNavigable 					= false;
    protected boolean isOrdered 					= false;
    protected boolean isAggregate 					= false;
    protected boolean isComposite 					= false;
    protected boolean isFrozen 						= false;
    protected boolean isAddOnly 					= false;
    protected boolean isListable 					= true;
    protected boolean cascadeDelete 				= false;
    protected boolean isPartOfDefaultFetchGroup 	= false;
    protected boolean isFromXMLFile 				= true;
    protected boolean isLazy 						= false;
    protected int lowerRange 						= 0;
    protected int upperRange 						= -1;
    protected AssociationEndObject siblingAssociationEndObject = null;
    protected ClassObject classObject 				= null;
    protected List<AttributeObject> attributes 		= new ArrayList<>();

    private static final String SOURCE_ROLE 		= "sourceRole";
    private static final Logger LOGGER 				= Logger.getLogger(AssociationEndObject.class.getName());
}
