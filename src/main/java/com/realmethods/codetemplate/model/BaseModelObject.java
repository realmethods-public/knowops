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
package com.realmethods.codetemplate.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.realmethods.codetemplate.parser.ModelParser;
import com.realmethods.codetemplate.model.classes.*;
import com.realmethods.codetemplate.AppGenObject;
import com.realmethods.common.helpers.Utils;

/**
 * Abstract base class for all objects of this model system
 * 
 * @author realMethods, Inc.
 * 
 */
public abstract class BaseModelObject extends AppGenObject implements IBaseModelObjectData, IBaseModelObjectAction {

// abstract methods
	/**
	 * Expectation - returns all BaseModelObjects in the global hierarchy
	 * @return
	 */
	public abstract List<BaseModelObject> getAllInHierarchy();
	
	/**
	 * Expectation - returns all ClassObjects in an instance hierarchy
	 * @return
	 */
	public abstract List<ClassObject> getAllClassesInHierarchy();

	/**
	 * By default return true/false based on getAllInHierarchy.
	 * Override if this is not an appropriate way to respond to the query.
	 * @return
	 */
	public boolean isEmpty() {
		return( getAllInHierarchy().isEmpty() );
	}
	
	/**
	 * Returns the not of isEmpty()
	 * @return
	 */
	public boolean isNotEmpty() {
		return !isEmpty();
	}
	/**
	 * Expectation - return all of like kind for an instance
	 * @return List<? extends BaseModelObject>
	 */	
	protected abstract List<? extends BaseModelObject> getAllOfLikeType();

	/**
	 * Returns the name field.
	 * 
	 * @return	String
	 */
	public String getName() {
		return name;
	}

	/**
	 * Assigns the name field using the provided argument.
	 * Also assigns the value to the displayName field.
	 * 
	 * @param	name
	 */
	public void setName(String name) {
		this.name = Utils.removeSpaces(name);
		this.displayName = this.name;
	}

	/**
	 * Returns the displayName field.
	 * 
	 * @return	String
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * Assigns the displayName field the provided argument.
	 * 
	 * @param	name
	 */
	public void setDisplayName(String name) {
		this.displayName = name.trim();
	}

	/**
	 * Assigns the parentName field.
	 * 
	 * @return String
	 */
	public String getParentName() {
		return parentName;
	}

	/**
	 * Assigns the parentName field using the provided argument.  Trims it before hand.
	 * 
	 * @param parentName
	 */
	public void setParentName(String parentName) {
		this.parentName = parentName.trim();
	}

	/**
	 * Determines if the instance has identity.
	 * 
	 * Defaults to false. Should be overloaded
	 */
	public boolean hasIdentity() {
		return false;
	}

	/**
	 * Determines if the instance has a single primary key.
	 * 
	 * Defaults to false. Should be overloaded
	 */
	public boolean hasSimplePrimaryKey() {
		return false;
	}

	/**
	 * Determines if the instance has a compound primary key.
	 * 
	 * Defaults to false. Should be overloaded
	 */
	public boolean hasCompoundPrimaryKey() {
		return false;
	}

	/**
	 * Returns the isAbstract field
	 * 
	 * @return boolean
	 */
	public boolean isAbstract() {
		return isAbstract;
	}

	/** 
	 * Assigns the isAbstract field using the provided argument.
	 * 
	 * @param isAbstract
	 */
	public void isAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}

	/**
	 * Returns the noDuplicates field.
	 * 
	 * @return boolean
	 */
	public boolean noDuplicates() {
		return noDuplicates;
	}

	/**
	 * Assigns the noDuplicates field using the provided argument.
	 * 
	 * @param	noDuplicates
	 */
	public void noDuplicates(boolean noDuplicates) {
		this.noDuplicates = noDuplicates;
	}

	/**
	 * Returns the isUserCreateable field.
	 * 
	 * @return boolean
	 */
	public boolean isUserCreateable() {
		return isUserCreateable;
	}

	/**
	 * Assigns the isUserCreateable field using the provided argument.
	 * 
	 * @param isUserCreateable
	 */
	public void isUserCreateable(boolean isUserCreateable) {
		this.isUserCreateable = isUserCreateable;
	}

	/**
	 * Returns the isUserEditable field.
	 * 
	 * @return boolean
	 */
	public boolean isUserEditable() {
		return isUserEditable;
	}

	/**
	 * Assigns the isUserEditable field using the provided argument.
	 */
	public void isUserEditable(boolean isUserEditable) {
		this.isUserEditable = isUserEditable;
	}

	/**
	 * Returns the isUserDeletable field.
	 * 
	 * @return boolean
	 */
	public boolean isUserDeletable() {
		return isUserDeletable;
	}

	/**
	 * Assigns the isUserDeletable field using the provided argument.
	 * 
	 * @param isUserDeletable
	 */
	public void isUserDeletable(boolean isUserDeletable) {
		this.isUserDeletable = isUserDeletable;
	}

	/**
	 * Returns the isUserDefined field.
	 * 
	 * @return boolean
	 */
	public boolean isUserDefined() {
		return (isUserDefined);
	}

	/** 
	 * Assigns the isUserDefined field using the provided argument.
	 * 
	 * @param isUserDefined
	 */
	public void isUserDefined(boolean isUserDefined) {
		this.isUserDefined = isUserDefined;
	}

	/**
	 * Returns true if the stereotype is set to Auditable
	 * 
	 * @return boolean
	 */
	public boolean isAuditable() {
		return stereotype != null && stereotype.equalsIgnoreCase("Auditable");
	}

	/**
	 * Returns true if the sterotype is set to enum.
	 * 
	 * @return boolean
	 */
	public boolean isEnumerator() {
		return stereotype != null && stereotype.equalsIgnoreCase("enum");
	}

	/**
	 * Assigns the stereotype field to "enum" if the input arg is true3.
	 * Otherwise, it sets the stereotype field to null.
	 * 
	 * @param is
	 */
	public void isEnumerator(boolean is) {
		stereotype = is ? "enum" : null;
	}

	/**
	 * Returns true if the stereotype is set to "service"
	 * 
	 * @return boolean
	 */
	public boolean isService() {
		return stereotype != null && stereotype.equalsIgnoreCase("service");
	}

	/**
	 * Assigns the stereotype field to "service" if the input arg is true3.
	 * Otherwise, it sets the stereotype field to null.
	 * 
	 * @param is
	 */
	public void isService(boolean is) {
		stereotype = is ? "service" : null;
	}

	/**
	 * Returns the isRoot field. 
	 * 
	 * For consideration when used in a graphing scenario.
	 * 
	 * @return boolean
	 */
	public boolean isRootObject() {
		return isRoot;
	}

	/**
	 * Returns the isLeaf field.
	 * 
	 * For consideration when used in a graphing scenario.
	 * 
	 * @returns boolean
	 */
	public boolean isLeafObject() {
		return isLeaf;
	}

	/**
	 * Returns the canBeGenerated field.
	 * 
	 * @return boolean
	 */
	public boolean canBeGenerated() {
		return canBeGenerated;
	}

	/**
	 * Returns the canBeVersioned field.
	 * 
	 * @return boolean
	 */	
	public boolean canBeVersioned() {
		return canBeVersioned;
	}

	/**
	 * Assign the canBeGenerated field with the provided argument.
	 * 
	 * @param canBeGenerated
	 */
	public void canBeGenerated(boolean canBeGenerated) {
		this.canBeGenerated = canBeGenerated;
	}

	/**
	 * Assign the canBeVersioned field with the provided argument.
	 * 
	 * @param canBeGenerated
	 */
	public void canBeVersioned(boolean canBeVersioned) {
		this.canBeVersioned = canBeVersioned;
	}

	/**
	 * Helper method to return true/false if the source field is equal to ModelSource.XMIFILE
	 * 
	 * @return boolean
	 */
	public boolean isFromXMLFile() {
		return source == ModelSource.XMIFILE;
	}

	/**
	 * Returns true if the parentName is non-null and not empty.
	 * 
	 * @return boolean
	 */
	public boolean hasParent() {
		if ( parentName != null && !parentName.isEmpty() )
			return ( true );
		else
			return( false );
	}

	/**
	 * Returns the packageName field.
	 * 
	 * @return String
	 */
	public String getPackageName() {
		return packageName;
	}

	/**
	 * Assigns the packageName field the argument provided.
	 * 
	 * Removes spaces
	 * 
	 * @param name
	 */
	public void setPackageName(String name) {
		// ignore spaces
		packageName = Utils.removeSpaces(name);
	}

	/**
	 * Append a '.' to the packageName if available.
	 * 
	 * @return String
	 */
	public String getFormattedPackageName() {
		StringBuilder pkgName = new StringBuilder();
		if (packageName != null && !packageName.isEmpty()) {
			pkgName.append(packageName);
			pkgName.append(".");
		}
		return pkgName.toString();
	}

	/**
	 * Returns the stereotype field.
	 * 
	 * @return String
	 */
	public String getStereotype() {
		return stereotype;
	}

	/**
	 * Assign the stereotype field the provided argument.
	 * 
	 * @param stereotype
	 */
	public void setStereotype(String stereotype) {
		this.stereotype = stereotype;
	}

	/**
	 * Returns true if the documentation field is non-null and not empty.
	 * 
	 * @return boolean
	 */
	public boolean hasDocumentation() {
		return documentation != null && !documentation.isEmpty();
	}

	/**
	 * Returns the documentation field.
	 * 
	 * @return String
	 */
	public String getDocumentation() {
		return documentation;
	}

	/**
	 * Assigns the document field the provided argument.
	 * 
	 * @param documentation
	 */
	public void setDocumentation(String documentation) {
		this.documentation = documentation;
	}

	/**
	 * Returns the superTypes field.
	 * 
	 * @return List<String>
	 */
	public List<String> getInterfaces() {
		return superTypes;
	}

	/**
	 * Assigns the superTypes field the provided argument.
	 */
	public void setInterfaces(List<String> interfaces) {
		superTypes = interfaces;
	}

	/**
	 * Returns a String containing each interface, delimited by a comma
	 * 
	 * The prefix and suffix are added to the beginning and end of the return value.
	 * 
	 * @param prefix
	 * @param suffix
	 * @return String
	 */
	public String getInterfacesAsString(String prefix, String suffix) {
		StringBuilder s = new StringBuilder();
		for (Iterator<String> iter = superTypes.iterator(); iter.hasNext();) {
			if (prefix != null)
				s.append(prefix);
			s.append((String) iter.next());
			if (suffix != null)
				s.append(suffix);
			if (iter.hasNext())
				s.append(", ");
		}

		return s.toString();
	}

	/**
	 * If hasParent() returns true, searches the larger model for the parent, 
	 * using the parentName field.
	 * 
	 * @return BaseModelObject
	 */
	public BaseModelObject getParent() {
		BaseModelObject parent = null;
		if (hasParent()) {
			parent = ModelParser.modelParser().getBaseModelObject(parentName);

			if (parent == null) {
				final String msg = "Class " + name + " has parent name " + parentName
									+ " but it is not a class found in the model. Will try to instantiate";
				LOGGER.log(Level.WARNING, msg);
				try {
					Class.forName( parentName );
				}
				catch( ClassNotFoundException exc ) {
					LOGGER.log(Level.SEVERE, () -> 
						"Unable to instantiate " 
							+ parentName 
							+ ". Try including the relevant jar in the Maven pom.xml file." );
					parentName = null;
				}
			}
		}

		return parent;
	}

	/**
	 * Returns true/false if the instance has a parent by not only comparing
	 * the parentName field to the input argument, but also ensure the model itself
	 * contains such a parent by the same name.
	 */
	public boolean hasAsParent(String parentName) {
		boolean has = false;
		if (hasParent()) {
			has = getParentName().equalsIgnoreCase(parentName);
			if (!has)
				has = getParent().hasAsParent(parentName);
		}
		return has;
	}

	/**
	 * Returns the names of this instance and it its ancestors (parent, parents-parent, etc..)
	 * 
	 * @return List<String>
	 */
	public List<String> getNamesInHierarchy() {
		List<String> classNames = new ArrayList<>();
		classNames.add(name);

		if (hasParent())
			classNames.addAll(getParent().getNamesInHierarchy());

		return classNames;
	}

	/**
	 * Returns the superTypes field.
	 * 
	 * @return superTypes
	 */
	public List<String> getSuperTypes() {
		return superTypes;
	}

	/**
	 * Assigns the superTypes field the provided argument.
	 * 
	 * @param superTypes
	 */
	public void setSuperTypes(List<String> superTypes) {
		this.superTypes = superTypes;
	}

	/**
	 * Does the work to figure out the parent and extended interfaces on behalf
	 * of a derived class
	 */
	public void reconcileSuperTypes() {
		List<? extends BaseModelObject> allOfLikeType = getAllOfLikeType();

		if (hasParent()) {
			boolean found = false;
			Iterator<? extends BaseModelObject> iter = allOfLikeType.iterator();

			// look for the parent amongst all of like type
			while (iter.hasNext() && !found)
				found = iter.next().getName().equalsIgnoreCase(parentName);

			if (!found) {
				final String msg = "Unable to locate parent class.  Class name " +
										name + " has parent "
										+ parentName + ", but it wasn't loaded in model.  It will be ignored.";
				LOGGER.log(Level.WARNING, msg);
			}
		}

		Iterator<String> iter = superTypes.iterator();
		String superType = null;

		while (parentName == null && iter.hasNext()) {
			superType = iter.next();

			for (BaseModelObject obj : allOfLikeType) {

				if (superType.equalsIgnoreCase(obj.getName())) {
					parentName = superType;
					// since accounted for in the parentName, can remove it here
					superTypes.remove(superType);
					final String msg = "Parent reconciled to " + parentName;
					LOGGER.log(Level.INFO, msg);
					return;
				}
			}
		}
	}

	/**
	 * Shallow copy implementation
	 * 
	 * @param baseModelObject
	 */
	public void copy(BaseModelObject baseModelObject) {
		name = baseModelObject.name;
		parentName = baseModelObject.parentName;
		stereotype = baseModelObject.stereotype;
		packageName = baseModelObject.packageName;
		documentation = baseModelObject.documentation;
		displayName = baseModelObject.displayName;
		fromXMLFile = baseModelObject.fromXMLFile;
		isAbstract = baseModelObject.isAbstract;
		canBeGenerated = baseModelObject.canBeGenerated;
		canBeVersioned = baseModelObject.canBeVersioned;
		isUserDeletable = true;
		isUserCreateable = true;
		isUserEditable = true;
		noDuplicates = false;
		isRoot = baseModelObject.isRoot;
		isLeaf = baseModelObject.isLeaf;
		source = baseModelObject.source;
	}

	/**
	 * Returns the source field.
	 * 
	 * Default to XMIFILE
	 * 
	 * @return ModelSource
	 */
	public ModelSource getModelSource() {
		return source;
	}

	/**
	 * Assigns the source field the provided argument.
	 * 
	 * @param source
	 */
	public void setModelSource(ModelSource source) {
		this.source = source;
	}

	// attributes

	protected String name 				= null;
	protected String parentName 		= null;
	protected String stereotype 		= null;
	protected String packageName 		= null;
	protected String documentation 		= null;
	protected String displayName 		= null;
	protected boolean fromXMLFile 		= true;
	protected boolean isAbstract 		= false;
	protected boolean canBeGenerated 	= true;
	protected boolean canBeVersioned 	= false;
	protected boolean isUserDefined 	= false;
	protected boolean isUserDeletable 	= true;
	protected boolean isUserCreateable 	= true;
	protected boolean isUserEditable 	= true;
	protected boolean noDuplicates 		= false;
	protected boolean isRoot 			= true;
	protected boolean isLeaf 			= true;

	protected ModelSource source 		= ModelSource.XMIFILE;
	protected List<String> superTypes 	= new ArrayList<>();
	private static final Logger LOGGER 	= Logger.getLogger(BaseModelObject.class.getName());

}
