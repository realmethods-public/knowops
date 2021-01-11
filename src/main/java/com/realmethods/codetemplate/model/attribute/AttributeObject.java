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
package com.realmethods.codetemplate.model.attribute;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.realmethods.common.helpers.Utils;
import com.realmethods.codetemplate.*;
import com.realmethods.codetemplate.model.*;
import com.realmethods.codetemplate.model.classes.ClassObject;
import com.realmethods.codetemplate.model.classes.enumerate.EnumClassObject;
import com.realmethods.codetemplate.parser.ModelParser;


/**
 * Encapsulates the concept of an attribute to serve as wrapper to a name, a type,
 * scope, and other fields one commonly affiliates with an attribute.  Although
 * an abstract concept, certain types in the goFramework Generation-Time model either
 * container or sub-class an AttributeObject.
 * 
 * @author realMethods, Inc.
 *
 */
public class AttributeObject extends AppGenObject
{
	/**
	 * Default constructor
	 */
	public AttributeObject() {
		// no_op
	}
	
	/**
	 * Constructor
	 * 
	 * Bind with owning ClassObject.
	 * 
	 * @param owningClassObject
	 */
	public AttributeObject( ClassObject owningClassObject ) {
		this.classObject = owningClassObject;
	}

	/**
	 * Copy constructor
	 * 
	 * @param classObject
	 */
	public AttributeObject( AttributeObject attribute ) {
		copy(attribute);
	}

	/**
	 * Constructor
	 * 
	 * @param classObject
	 * @param attribute
	 */
	public AttributeObject( ClassObject classObject, AttributeObject attribute )  {
		this.classObject = classObject;
		copy(attribute);
	}

	/**
	 * Performs a deep thorough copying of the provided argument.
	 * 
	 * However, it creates a unique name.
	 * 
	 * @param attribute
	 */
	public void copy(AttributeObject attribute)
	{
		setName( attribute.getName() );
		setDisplayName( attribute.getDisplayName() );
		setType( attribute.getType() );
		applyDefaultValue( attribute.getDefaultValue() );
		setVisibility( attribute.getVisibility() );
		isPrimaryKey(attribute.isPrimaryKey());
		isForeignKey = attribute.isForeignKey();
		canBeNull = attribute.canBeNull();
		isIntrinsicType = attribute.isIntrinsicType();
		isFromAssociation(attribute.isFromAssociation());
		isStatic = attribute.isStatic();
		isFinal = attribute.isFinal();
		isTransient = attribute.isTransient();
		isVolatile = attribute.isVolatile();
		isFromUserModel = attribute.isFromUserModel();
		isFromXMLFile = attribute.isFromXMLFile();
		classObject = attribute.getClassObject();
		isFromMultiValuedAssociation = attribute.isFromMultiValueAssociation();
		isFromEnumerator = attribute.isFromEnumerator();
		isAggregate = attribute.isAggregate();
		isComposite = attribute.isComposite();
		isComponent = attribute.isComponent();
		userModifiable = attribute.isUserModifiable();
		userViewable = attribute.isUserViewable();
		toolTip = attribute.getToolTip();
		isUserIdentifiable = attribute.isUserIdentifiable();
		isOrderedBy = attribute.isOrderedBy();

		applyUniqueName();
	}

	/**
	 * Returns the name field.
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Assigns the name field.  Attempts to coerce into a conventional name.
	 * 
	 * @param nameToAssign
	 */
	public void setName( String nameToAssign ) {
		name = nameToAssign;
		
        if( name != null && name.startsWith("m_") ) {
        	final String msg = "Stripping m_ prefix from " + name;
    		LOGGER.log( Level.INFO, msg);
    		name = name.substring(2);
		}
				
		// removing spaces for poorly named attribures
		name = name.replace(" " , "");

		setDisplayName(name);
		applyUniqueName();
	}

	/**
	 * If none assigned, turns the name into a phrase and returns it instead
	 * @return
	 */
	public String getDisplayName()
	{
		if ( displayName == null )
			displayName = Utils.turnIntoPhrase(getName());
		
		return( displayName );
	}

	/**
	 * Assigns the displayName field the provided argument, trimmed.
	 * 
	 * @param name
	 */
	public void setDisplayName(String name) {
		displayName = name.trim();
	}

	/**
	 * Return the uniqueName field.  If none, delegates first to applyUniqueName().
	 * 
	 * @return
	 */
	public String getUniqueName() {
		if (uniqueName != null && uniqueName.length() == 0)
			applyUniqueName();

		return uniqueName;
	}

	/**
	 * Returns the toolTip field.
	 * 
	 * This field is no longer used but remains for backwards compatibility.
	 * 
	 * @return
	 */
	public String getToolTip() {
		return toolTip;
	}

	/**
	 * Assigns the toolTip field the provided argument.
	 * 
 	 * This field is no longer used but remains for backwards compatibility.
 	 * 
	 * @param tip
	 */
	public void setToolTip(String tip) {
		toolTip = tip;
	}

	/**
	 * Returns the type field.
	 * 
	 * @return
	 */
	public String getType() {
		return type;
	}

	/**
	 * Helper method to extract the root from the type if it exists in a dot notation format.  
	 * For example - com.foo.Account would ignore the com.foo. and return the Account
	 * @return
	 */
	public String getRootType()
	{
		String rootType = type;
		int index 		= rootType.lastIndexOf('.');
		
		if (index > -1)
			rootType = type.substring(index + 1);
		
		return rootType;
	}

	/**
	 * Assigns the type field the provided argument.
	 * 
	 * @param type
	 */
	public void setType(String type) {
		if (type != null)
		{
			// assign as is if in dot notation
			if (type.indexOf('.') != -1) {
				this.type = type;
			}
			else { // else capitalize its first letter, then assign the type 
				if (type.equalsIgnoreCase("int"))
					type = "Integer";
				
				this.type = Utils.convertType( Utils.capitalizeFirstLetter(type) );
			}
		}
		else {
			LOGGER.severe( "Null type is invalid for attribute : "
											+ getName() 
											+ ". Will force type to String." );
			this.type = "String";
		}
	}

	/**
	 * Returns the qualified type field if not null.  If null, returns the type field.
	 * 
	 * @return
	 */
	public String getQualifiedType() {
		return (qualifiedType != null ? qualifiedType : type);
	}

	/**
	 * Assigns the qualifiedType field.
	 * 
	 * @param qualifiedType
	 */
	public void setQualifiedType(String qualifiedType) {
		this.qualifiedType = qualifiedType;
	}

	/**
	 * A convenience method for turning an attribute sourced from a enum during
	 * model processing and tagged as an enum into an EnumClassObject.
	 * 
	 * @return
	 */
	public EnumClassObject getEnumClassObject() {
		EnumClassObject enumClassObject = null;

		if (isFromEnumerator()) {
			enumClassObject = ModelParser.modelParser().findEnum( getType() );
		}
		else
			LOGGER.log(Level.WARNING, "The type isn't of enum type. Type is "
							+ getType());

		return (enumClassObject);
	}


	/**
	 * Checks to see if the name ends with the suffix 'PrimraryKey'.
	 * 
	 * This is for backwards compatibility and is no longer used.
	 * 
	 * @return
	 */
	public boolean nameEndsWithPrimaryKey()
	{
		return( getAsArgument().indexOf(PRIMARY_KEY) != -1 );
	}

	/**
	 * Returns the concatenation of getRootPKName with 'PK'.
	 * 
	 * This is for backwards compatibility and is no longer used.
	 * 
	 * @return
	 */	
	public String getAbbrevPKName()
	{
		return getRootPKName() + "PK";
	}

	/**
	 * Attempts to remove PrimaryKey or PK as a suffix from the name.
	 * 
	 * This is for backwards compatibility and is no longer used.
	 * 
	 * @return
	 */		
	public String getRootPKName()
	{
		String rootPKName = getAsArgument();

		// remove the primarykey suffix if there
		if (rootPKName.indexOf(PRIMARY_KEY) != -1)
			rootPKName = rootPKName.substring(0, rootPKName
					.indexOf(PRIMARY_KEY));
		else if (rootPKName.indexOf("PK") != -1)
			rootPKName = rootPKName.substring(0, rootPKName.indexOf("PK"));

		return rootPKName;
	}

	/**
	 * Return visibility field.
	 * 
	 * @return
	 */
	public String getVisibility() {
		return visibility;
	}

	/**
	 * Assign the visibility field the provided argument.
	 * 
	 * @param v
	 */
	public void setVisibility(String v)
	{
		visibility = v;
        if( visibility == null 
        		|| ( !visibility.equals(PROTECTED) 
        		&& !visibility.equals(PUBLIC) 
        		&& !visibility.equals("private") ) )
        {
        	LOGGER.info("Attribute visilibity being set to protected: " + getName() );
            visibility = PROTECTED;
        }				
	}

	/**
	 * Returns a version of the name where the first letter is set to lower case.
	 * 
	 * @return
	 */
	public String getAsArgument() {
		return Utils.lowercaseFirstLetter(name);
	}

	/**
	 * Returns the form 'this.<name> = <argument_form>.
	 * 
	 * For example 'this.foo = foo';
	 * 
	 * @return
	 */
	public String getAsAssignment() {
		return "this." + name + " = " + getAsArgument();
	}

	/**
	 * Returns the defaultValue field.
	 * 
	 * @return
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * Returns the defaultValue field by delegating to getDefaulValue().
	 * 
	 * This is for backwards compatibility and is no longer used.
	 * @return
	 */
	public String getTheDefaultValue() {
		return getDefaultValue();
	}

	/**
	 * Assigns the defaultValue field the provided argument.
	 * 
	 * @param def
	 */
	public void setDefaultValue(String def) {
		defaultValue = def;
	}

	public boolean hasDefaultValue() {
		boolean hasDefault = false;
		
		if ( defaultValue != null && defaultValue.length() > 0 )
			hasDefault = true;
		
		return hasDefault;
	}

	/**
	 * Interrogates the different date/time Java types
	 * 
	 * @return
	 */
	public boolean isDateTimeRelated() {
		return type.equals("java.util.Date") 
				|| type.equals("java.sql.Time")
				|| type.equals("java.sql.Date")
				|| type.equals("java.sql.Timestamp");
	}

	/**
	 * Interrogates the different numeric Java types
	 * 
	 * @return
	 */
	public boolean isNumericRelated() {
		return type.equals("Short") 
				|| type.equals("Integer")
				|| type.equals("Long") 
				|| type.equals("Double");
	}

	/**
	 * Returns the isPrimaryKey field.
	 * 
	 * @return
	 */
	public boolean isPrimaryKey() {
		return (isPrimaryKey);
	}

	/**
	 * If assigned to be a primary key, forces both viewable and modifiable to false
	 * 
	 * @param is
	 */
	public void isPrimaryKey(boolean is) {
		isPrimaryKey = is;

		if (isPrimaryKey) {
			// force to not modifiable, not visible, and of type Long
			userModifiable 	= false;
			userViewable 	= false;
		}
	}

	/**
	 * Returns the isOrderedBy field.
	 * 
	 * @return
	 */
	public boolean isOrderedBy() {
		return isOrderedBy;
	}

	/**
	 * Assigns the isOrderedBy field the provided argument.
	 * 
	 * @param is
	 */
	public void isOrderedBy(boolean is) {
		isOrderedBy = is;
	}

	/**
	 * Returns true if the type field ends with 'oolean'
	 * @return
	 */
	public boolean isBoolean() {
		return type != null && type.endsWith("oolean");
	}

	/**
	 * Returns the isStatic field.
	 * 
	 * @return
	 */
	public boolean isStatic() {
		return isStatic;
	}

	/**
	 * Assigns the isStatic field the provided argument.
	 * 
	 * @param is
	 */
	public void isStatic(boolean is) {
		isStatic = is;
	}

	/**
	 * Returns the isCollection field.
	 * 
	 * @return
	 */
	public boolean isCollection() {
		return isCollection;
	}

	/**
	 * Assigns the isCollection field the provided argument.
	 * 
	 * @param is
	 */
	public void isCollection(boolean is) {
		isCollection = is;
	}

	/**
	 * Returns the isForeignKey field.
	 * 
	 * @return
	 */
	public boolean isForeignKey() {
		return (isForeignKey);
	}

	/**
	 * Assigns the isForeignKey field the provided argument.
	 * 
	 * @param is
	 */
	public void isForeignKey(boolean is) {
		isForeignKey = is;
	}


	/**
	 * Returns the isFinal field.
	 * 
	 * @param is
	 */
	public boolean isFinal() {
		return isFinal;
	}
	
	/**
	 * Assigns the isFinal field the provided argument.
	 * 
	 * @param is
	 */
	public void isFinal(boolean is) {
		isFinal = is;
	}

	/**
	 * Returns the isTransient field.
	 * @param is
	 */
	public boolean isTransient() {
		return isTransient;
	}

	/**
	 * Assigns the isTransient field the provided argument.
	 * 
	 * @return
	 */	
	public void isTransient(boolean is) {
		isTransient = is;
	}

	/**
	 * Returns the isVolatile flag.
	 * 
	 * @return
	 */
	public boolean isVolatile() {
		return isVolatile;
	}

	/**
	 * Assigns the isVolatile flag.
	 * 
	 * @param is
	 */
	public void isVolatile(boolean is) {
		isVolatile = is;
	}

	/**
	 * Returns the isFromAssociation field.
	 * 
	 * @return
	 */
	public boolean isFromAssociation() {
		return isFromAssociation;
	}

	/** 
	 * Assigns the isFromAssociation field from the provided argument.
	 *  
	 * @param is
	 */
	public void isFromAssociation(boolean is) {
		isFromAssociation = is;
	}

	/**
	 * Returns true if from an association and that association
	 * is multi-valued.
	 * 
	 * @return
	 */
	public boolean isFromMultiValueAssociation() {
		return isFromAssociation() && this.isFromMultiValuedAssociation;
	}

	/**
	 * Returns true if from an association and that association
	 * is single3-valued.
	 * 
	 * @return
	 */
	public boolean isFromSingleValueAssociation() {
		return isFromAssociation() && !isFromMultiValueAssociation();
	}

	/**
	 * Returns the isFromMultiValuedAssociation field.
	 * 
	 * @param is
	 */
	public void isFromMultiValueAssociation(boolean is) {
		isFromMultiValuedAssociation = is;
	}

	/**
	 * Returns the isFromXMLFile field.
	 * 
	 * This field is no longer used but kept for backwards compatibility.
	 * 
	 * @return
	 */
	public boolean isFromXMLFile() {
		return isFromXMLFile;
	}

	/**
	 * Assigns the isFromXMLFile field the provided argument.
	 * 
	 * This field is no longer used but kept for backwards compatibility.
	 * 
	 * @return
	 * @param is
	 */
	public void isFromXMLFile(boolean is) {
		isFromXMLFile = is;
	}

	/**
	 * Returns the aggregate field.
	 * 
	 * @return
	 */
	public boolean isAggregate() {
		return isAggregate;
	}

	/**
	 * Assigns the isAggregate field from the provided argument.
	 * 
	 * @param is
	 */
	public void isAggregate(boolean is) {
		isAggregate = is;
	}

	/**
	 * Returns the isComposite field.
	 * 
	 * @return
	 */
	public boolean isComposite() {
		return isComposite;
	}

	/**
	 * Assigns the isComposite field from the provided argument.
	 * 
	 * @param is
	 */
	public void isComposite(boolean is) {
		isComposite = is;
	}

	/**
	 * Returns true if isComposite is true and is from a single association.
	 * 
	 * @return
	 */
	public boolean isSingleComposite() {
		return isComposite() && isFromSingleValueAssociation();
	}

	/**
	 * Returns the isComponent field.
	 * 
	 * @return
	 */
	public boolean isComponent() {
		return isComponent;
	}

	/**
	 * Assigns the isComponent field the provided argument.
	 * 
	 * @param is
	 */
	public void isComponent(boolean is) {
		isComponent = is;
	}

	/**
	 * Assigns the isFromEnumerator field the provided argument.
	 * 
	 * @param is
	 */
	public void isFromEnumerator(boolean is) {
		isFromEnumerator = is;
	}

	/**
	 * Returns the isFromEnumerator field.
	 * 
	 * @return
	 */
	public boolean isFromEnumerator() {
		return isFromEnumerator;
	}

	/**
	 * Returns the userModifiable field.
	 * @return
	 */
	public boolean isUserModifiable() {
		return (userModifiable);
	}

	/**
	 * Assigns the modifiable field from the provided argument.
	 * 
	 * @param modifiable
	 */
	public void isUserModifiable(boolean modifiable) {
		userModifiable = modifiable;
	}

	/**
	 * Returns the userViewable field.
	 * 
	 * @return
	 */
	public boolean isUserViewable() {
		return (userViewable);
	}

	/**
	 * Assigns the userViewable field from the provided argument.
	 * 
	 * @param viewable
	 */
	public void isUserViewable(boolean viewable) {
		userViewable = viewable;
	}

	/**
	 * Returns the canBeNull field.
	 * 
	 * @return
	 */
	public boolean canBeNull() {
		return canBeNull;
	}

	/**
	 * Assigns the canBeNull field from the provided argument.
	 * @param can
	 */
	public void canBeNull(boolean can) {
		canBeNull = can;
	}

	/**
	 * Convenience method that returns the not of canBeNull().
	 * 
	 * @return
	 */
	public boolean cannotBeNull() {
		return !canBeNull();
	}

	/**
	 * Returns the isIntrinsicType field
	 * @return
	 */
	public boolean isIntrinsicType() {
		return isIntrinsicType;
	}

	/**
	 * Assigns the isIntrinsicType field the provided argument.
	 * 
	 * @param is
	 */
	public void isIntrinsicType(boolean is) {
		isIntrinsicType = is;
	}

	/**
	 * Returns the isFromUserModel field.
	 * 
	 * @return
	 */
	public boolean isFromUserModel() {
		return isFromUserModel;
	}

	/**
	 * Assigns the isFromUserModel field the provided argument.
	 * @param is
	 */
	public void isFromUserModel(boolean is) {
		isFromUserModel = is;
	}

	/**
	 * Returns the owning ClassObject field.
	 * 
	 * @return
	 */
	public ClassObject getClassObject() {
		return classObject;
	}

	/**
	 * 
	 * @param classObject
	 */
	public void setClassObject( ClassObject classObject ) {
	    this.classObject = classObject;
	}

	/**
	 * Returns the name of the bound ClassObject.
	 * 
	 * @return
	 */
	public String getClassObjectName() {
		return classObject.getName();
	}

	/**
	 * Stringify its contents
	 * 
	 * @return`
	 */
	public String toString() {
		return getName();
	}

	/**
	 * Convenience method to turn name into a phrase.
	 * 
	 * @return
	 */
	public String turnIntoPhrase() {
		return Utils.turnIntoPhrase(getAsArgument());
	}

	/**
	 * Returns the isPartOfDefaultFetchGroup field.
	 * 
	 * This field is no longer use but is kept for backwards compatibility.
	 * 
	 * @return
	 */
	public boolean isPartOfDefaultFetchGroup() {
		return isPartOfDefaultFetchGroup;
	}

	/**
	 * Assigns the isPartOfDefaultFetchGroup field using the provided argument.
	 * 
	 * This field is no longer used but is kept for backwards compatibility.
	 * 
	 * @param b
	 */
	public void isPartOfDefaultFetchGroup(boolean b) {
		isPartOfDefaultFetchGroup = b;
	}

	/**
	 * This method and field is no longer used but is kept for backwards compatibility.
	 * 
	 * @return
	 */
	public String isPartOfDefaultFetchGroupAsString() {
		return Boolean.toString(isPartOfDefaultFetchGroup);
	}

	/**
	 * Returns the isUserIdentifiable field.
	 * 
	 * @return
	 */
	public boolean isUserIdentifiable() {
		return isUserIdentifiable;
	}

	/**
	 * Assigns the isUserIdentifiable field the provided argument.
	 * 
	 * @param b
	 */
	public void isUserIdentifiable(boolean b){
		isUserIdentifiable = b;
	}

	protected void applyUniqueName()
	{
		if ( classObject != null )
			uniqueName = Utils.buildName(classObject.getName(), name);
	}

	/**
	 * Assign the default value, if the attribute is a primary key, the assigned defValue is ignored
	 * and null is assigned instead.
	 * 
	 * @param defValue
	 */
	protected void applyDefaultValue( String defValue )
	{
		if (isPrimaryKey)
		{
			LOGGER.log(Level.INFO, getName()
										+ " is a primary key field...forcing default value to null" );
			defaultValue = null;
			return;	
		}
		
		if (defValue == null)
		{
			if (type.endsWith("oolean"))
			{
				LOGGER.log(Level.INFO, "AttributeObject.applyDefaulValue() " 
					+ getName() + " has no default value, will set to false");
				
				defValue = "false";
				setDefaultValue( "new Boolean(" + defValue + ")" );
			}
		}
		else
		{
			// just in case the default value is already enclosed in quotes
			defValue = defValue.replace( "\"", "" );
		
			if ( defValue.length() > 0 )
				setDefaultValue( "new " + this.getType() + "(\"" + defValue + "\")" );
			else
				setDefaultValue( "null" );
		}
	}

// attributes
	
	protected String name 			= null;
	protected String displayName 	= null;
	protected String uniqueName 	= null;
	protected String type 			= null;
	protected String defaultValue 	= "";
	protected String visibility 	= PROTECTED;
	protected String toolTip 		= "";
	protected boolean isPrimaryKey 	= false;
	protected boolean canBeNull 	= true;
	protected boolean isIntrinsicType = true;
	protected boolean isFromAssociation = false;
	protected boolean isStatic 		= false;
	protected boolean isFinal 		= false;
	protected boolean isTransient 	= false;
	protected boolean isVolatile 	= false;
	protected boolean isCollection 	= false;
	protected boolean isFromMultiValuedAssociation = false;
	protected boolean isFromUserModel = true;
	protected boolean isFromXMLFile = true;
	protected boolean isAggregate 	= false;
	protected boolean isComposite 	= false;
	protected boolean isComponent 	= false;
	protected boolean isForeignKey 	= false;
	protected boolean userModifiable = true;
	protected boolean userViewable 	= true;
	protected boolean isPartOfDefaultFetchGroup = false;
	protected boolean isUserIdentifiable = false;
	protected boolean isOrderedBy = false;
	protected boolean isFromEnumerator = false;
	
	protected ClassObject classObject 	= null;
	protected String qualifiedType 		= null;
	
	private static final String PROTECTED 	= "protected";
	private static final String PUBLIC 		= "public";
	private static final String PRIMARY_KEY = "PrimaryKey";
	
    private static final Logger LOGGER 	= Logger.getLogger(AttributeObject.class.getName());
	
}
