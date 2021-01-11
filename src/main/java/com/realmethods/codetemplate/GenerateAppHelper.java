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
package com.realmethods.codetemplate;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.realmethods.codetemplate.model.association.AssociationEndObject;
import com.realmethods.codetemplate.model.classes.ClassObject;
import com.realmethods.codetemplate.model.classes.enumerate.EnumClassObject;
import com.realmethods.codetemplate.parser.ModelParser;
import com.realmethods.foundational.common.namespace.FrameworkNameSpace;

/**
 * Helper class used to give the Velocity context access to the entire app gen
 * system along with the loaded model, and many other helper methods required to
 * translate the loaded model, selected tech stack, target language, etc.. into
 * a complete and sensible working application
 * 
 * @author realMethods, Inc.
 * 
 */
public class GenerateAppHelper extends AppGenObject {
	/**
	 * default constructor, seeded with classes in this project generate session,
	 * and external configuration of key/value pairings
	 * 
	 * @param 	classesToGenerate		ClassObjects to generate over
	 * @param	params					mapping of the input application options
	 */
	public GenerateAppHelper(List classesToGenerate, Map<String, String> params) {
		this.classesToGenerate 	= classesToGenerate;
		this.params 			= params;

		//////////////////////////////////////////////////////////////
		// gets the internal field ready
		//////////////////////////////////////////////////////////////
		prepare();
	}

	/**
	 * The ClassObject of focus during an project creation session
	 * 
	 * @return	current ClassObject
	 */
	public ClassObject getClassObject() {
		return classObject;
	}

	/**
	 * Assigns the ClassObject of focus during an project creationsession
	 * 
	 * @param classObject	ClassObject of interest
	 */
	public void setClassObject(ClassObject classObject) {
		this.classObject = classObject;
	}

	/**
	 * Helper to return the list of classes to generate over.
	 * 
	 * @return List		list of ClassObjects.
	 */
	public List<ClassObject> getClassesToGenerate() {
		return ModelParser.modelParser().getClasses();
	}

	/**
	 * Helper to return the list of interfaces to generate over.
	 * 
	 * @return List		list of interfaces as ClassObjects
	 */
	public List<ClassObject> getInterfacesToGenerate() {
		return ModelParser.modelParser().getInterfaces();
	}

	/**
	 * Helper to return the list of services to generate over.
	 * 
	 * @return List		list of services as ClassObjects
	 */
	public List<ClassObject> getServicesToGenerate() {
		return ModelParser.modelParser().getServices();
	}

	/**
	 * Helper to return the list of enum to generate over.
	 * 
	 * @return List		list of EnumClassObjects
	 */
	
	public List<EnumClassObject> getEnumClassesToGenerate() {
		return ModelParser.modelParser().getEnums();
	}

	/**
	 * Check to determine if the input enumClassName references a EnumClassObject in the model.
	 * 
	 * @param enumClassName	name of the EnumClassObject to locate and consider
	 * @return boolean		true/false
	 */
	public boolean isEnumerator(String enumClassName) {
		return (getEnumClassObject(enumClassName) != null);
	}

	/**
	 * Locates an EnumClassObject by name in the loaded model hierarchy.
	 * 
	 * @param 	name			name of the EnumClassObject to locate
	 * @return 	EnumClassObject	object to locate, null if not located
	 */
	public EnumClassObject getEnumClassObject(String name) {
		return (ModelParser.modelParser().findEnum(name));
	}

	/**
	 * The entire set of key/value pairings provided as part of a project generation session call.
	 * 
	 * @return Map		project (application) key/value pairings as parameters
	 */
	public Map<String, String> getParams() {
		return params;
	}

	/**
	 * Returns a parameter based on the provided key within the params field. 
	 * For the sake of clarity, it is NOT a case sensitive search using the provide key
	 * 
	 * @param paramKey		key to search by
	 * @return String		value found or "" if not found
	 */
	public String getParam(String paramKey) {
		List<String> values = params.keySet()
							.stream()
							.filter( key -> key.toLowerCase().equalsIgnoreCase( paramKey.toLowerCase() ) )
							.collect(Collectors.toList());

		return (values != null && !values.isEmpty()) ? params.get(values.get(0)) : "";
	}

	/**
	 * Convenience method to return a Map of parameters related to a given prefix.
	 * For example, parameter keys take the form of application.name where application is the prefix.
     * Strip prefix removes the prefix from each found key
	 * 
	 * @param 	prefix			key prefix (keys are in . notation. ex: app.name where prefix would be app
	 * @param 	stripPrefix		true/false to remove the prefix
	 * @return	Map				all matching parameters as their key/value pairings
	 */
	public Map<String, String> getParamsByPrefix(String prefix, boolean stripPrefix) {
		final Map<String, String> parms = new HashMap<>();

		params.keySet().forEach( key -> {
			final String tmpKey = key;
			if (key.startsWith(prefix)) {
				if (stripPrefix)
					key = key.substring(prefix.length());
				parms.put(key, params.get(tmpKey));
			}
		});

		return parms;
	}

	/**
	 * A no_op 
	 * @return String	returns an empty String
	 */
	public String getModelSource() {
		return ("");
	}

	/**
	 * Returns the author parameter from the params field using application.author as the key.
	 * @return	the author name
	 */
	public String getAuthor() {
		return params.get("application.author");
	}

	/**
	 * Delegates to getRooPackageName(boolean) using false to not append the class name.
	 * 
	 * @return String		root package name
	 */
	public String getRootPackageName() {
		return getRootPackageName(false);
	}

	/** 
	 * Returns the name of the application package name from the parameters field using
	 * key 'application.package name'.
	 * 
	 * The argument is ignored and the method is kept around for backwards capability.
	 * 
	 * @param appendClassName		class name to append
	 * @return						resulting application package name
	 */
	public String getRootPackageName(boolean appendClassName) {	
		final String key = "application.package name";
		if( appendClassName )	
			return params.get(key);
			
		return params.get(key);
	}

	/**
	 * Returns the package name for the provided class by appending the classes name to the root
	 * package name as specified by parameter 'application.package name' on the params field.
	 * @param 	refClass	ClassObject of interest
	 * @return				resulting package anem
	 */
	public String getRootPackageName(ClassObject refClass) {
		StringBuilder rootPkgName = new StringBuilder(params.get("application.package name"));
		if (refClass != null) {
			String pkgName = refClass.getPackageName();
			if (pkgName != null && !pkgName.isEmpty()) {
				rootPkgName.append(".");
				rootPkgName.append(pkgName);
			}
		}
		return rootPkgName.toString();
	}

	/**
	 * Delegates internally to getRootPackageNameUnderscored(boolean) providing false to
	 * indicate not to append the class name.
	 * 
	 * @return String	resulting package name replacing dots with underscores
	 */
	public String getRootPackageNameUnderscored() {
		return getRootPackageNameUnderscored(false);
	}

	/**
	 * Returns the root package name, substituting dot notation with underscore
	 * notation.  First, delegates to getRootPackageName(boolean). 
	 * 
	 * @param appendClassName	true/false to append the className to the results
	 * @return String			resulting root package name
	 */
	public String getRootPackageNameUnderscored(boolean appendClassName) {
		return getRootPackageName(appendClassName).replace('.', '_');
	}

	/**
	 * Returns the root package using directory notation. 
	 * 
	 * Delegates internally to getRootPackageName() replacing dots with File separator character
	 * 
	 * @return String			resulting directory
	 */
	public String getRootPackageAsDirectory() {
		return getRootPackageName().replace('.', java.io.File.separatorChar);
	}


	/**
	 * Returns the corporate logo Url using 'application.corporate logo URL' parameter on the params field.
	 * 
	 * @return String	corporate logo Url
	 */
	public String getCorporateLogoURL() {
		return params.get("application.corporate logo URL");
	}

	/**
	 * Returns the application logo Url using 'application.application logo URL' parameter on the params field.
	 * 
	 * @return String	application logo Url
	 */	
	public String getApplicationLogoURL() {
		return params.get("application.application logo URL");
	}

	/**
	 * Returns the realMethods Platform version
	 * 
	 * @return String	platform version
	 */	
	public String getPlatformVersion() {
		return FrameworkNameSpace.VERSION;
	}
	
	/**
	 * Returns the application version  using 'application.version' parameter on the params field.
	 * 
	 * @return String	application version
	 */		
	public String getVersion() {
		return params.get("application.version");
	}

	/**
	 * Returns the application name using 'application.name' parameter on the params field.
	 * 
	 * If none assigned, defaults to no_app_name_assigned.
	 * 
	 * @return String	application name
	 */			
	public String getApplicationName() {
		String appName = params.get("application.name");

		/////////////////////////////////////////////////////////////
		// if none assigned, assign one indicating as such
		/////////////////////////////////////////////////////////////
		if (appName == null || appName.isEmpty())
			appName = "no_app_name_assigned";

		return appName;
	}

	/**
	 * Returns an application name formated by removing single spaces.
	 * 
	 * @return String	application name formatted
	 */
	public String getApplicationNameFormatted()
	{
		return getApplicationName().replaceAll( " ", "" );
	}
	

	/**
	 * Returns the company name using 'application.company name' parameter on the params field.
	 * 
	 * If none assigned, defaults to no_company_name_assigned.
	 * 
	 * @return String	company name
	 */			
	public String getCompanyName() {
		String companyName = params.get("application.company name");

		if (companyName == null || companyName.isEmpty() )
			companyName = "no__company_name_assigned";

		return companyName;
	}

	/**
	 * Returns the application description name using 'application.description' parameter on the params field.
	 * 
	 * @return String	application description
	 */		
	public String getApplicationDescription() {
		return params.get("application.description");
	}

	/**
	 * Helper to apply 64 bit encoding a String.
	 *  
	 * @param 	input	String to encode
	 * @return			encoding String
	 */
	public String encodeString( String input ) {
		return Base64.getEncoder().encodeToString(input.getBytes());
	}
	

	/**
	 * Returns the typesUsed field.
	 * 
	 * @return List		types in use
	 */
	public List getTypesUsed() {
		return typesUsed;
	}

	/**
	 * Clear and return the typesUsed field.
	 * 
	 * @return List		cleared list
	 */
	public List getTypesUsedAndClear() {
		typesUsed.clear();
		return typesUsed;
	}

	
	/**
	 * Test method to determine if the ClassObject referenced by the className contains
	 * at least one primary key attribute.
	 * 
	 * @param className		refers to the ClassObject of interest
	 * @return				true/false if the ClassObject has identity
	 */
	public boolean hasIdentity(String className) {
		return ModelParser.hasIdentity(className);
	}

	/**
	 * Test method to determine if the ClassObject contains
	 * at least one primary key attribute.
	 *  
	 * @param 	object		ClassObject to query
	 * @return				true/flase if the ClassObject has identity
	 */
	public boolean hasIdentity(ClassObject object) {
		if ( object != null )
			return object.hasIdentity();
		return false;
	}

	/**
	 * Test method to determine if the type provided references a class in the loaded model.
	 * 
	 * @param	type		type of model object
	 * @return 	boolean		true/false if is a ClassObject type
	 */
	public boolean isClassType(String type) {
		return (getClassObject(type) != null);
	}

	/**
	 * Returns the ClassObject referred to className if found in the loaded model.
	 * 
	 * @param 	className	refers to the ClassObject of interest
	 * @return				discovered ClassObject, or null if none found
	 */
	public ClassObject getClassObject(String className) {
		ClassObject tempClassObject = ModelParser.modelParser().findClass(className);

		if (tempClassObject == null) {
			tempClassObject = ModelParser.modelParser().findInterface(className);
		}

		return tempClassObject;
	}

	/**
	 * The entire model hierarchy is visited and only classes with an identity are
	 * returned. A class hasIdentity if it has at least one field considered a
	 * primary key field.  Stored in a local variable to speed up subsequent calls.
	 * 
	 * @return	list of classes that have identity
	 */
	public List getClassesWithIdentity() {
		if (classesWithIdentity == null) {
			classesWithIdentity = new ArrayList<>();

			ModelParser.modelParser()
			.getAllClassesInHierarchy().forEach( innerClassObject -> {
					if (innerClassObject != null && hasIdentity(innerClassObject))
						classesWithIdentity.add(innerClassObject);					
			});
		}
		
		return classesWithIdentity;
	}

	/**
	 * return all Containers
	 * @return		list of ContainerObjects
	 */
	public List getContainers() {
		return( ModelParser.modelParser().getContainers() );
	}

	/**
	 * Helper method to return all the associations where the childClassObject is
	 * an end point.
	 * 
	 * @param 	childClassObject	ClassObject of interest to query
	 * @return	List				list of AssociationEndObjects
	 */
	public List<AssociationEndObject> getAssociationsFromParentsAsSiblingEnd(ClassObject childClassObject) {
		List<AssociationEndObject> associations = associationFromParentAsSiblingEnd.get(childClassObject);
		if (associationFromParentAsSiblingEnd.containsKey(childClassObject)) {
			final List<AssociationEndObject> tmpList = new ArrayList<>();
			
					ModelParser.modelParser().getAllClassesInHierarchy()
					.forEach( parentClassObject -> 
						tmpList.addAll( parentClassObject.getAssociations()
										.stream()
										.filter( association -> association.getSiblingAssociationEndObject()
													.getType().equals(childClassObject.getName()) )
										.collect(Collectors.toList()) )
					);
						
			associations = tmpList;
			associationFromParentAsSiblingEnd.put(childClassObject, associations);
		}
		return associations;
	}

	/**
	 * A convenience method, that returns all navigable multivalued association
	 * where the classObject is an endpoint.
	 * 
	 * @param 	classObject		ClassObject of interest
	 * @return	List			list of AssociationEndObjects
	 */
	public List<AssociationEndObject> getNavigableMultiAssocations(ClassObject classObject) {
		final ArrayList<AssociationEndObject> multiValueAssociations = new ArrayList<>();

		getAssociationsFromParentsAsSiblingEnd(classObject).forEach( associationEndObject -> {
			if (associationEndObject.isMultivalued() && associationEndObject.isNavigable())
				multiValueAssociations.add(associationEndObject);
		});
		return multiValueAssociations;
	}

	/**
	 * Returns all the AssociationsEndObjects of the parents of the provided child
	 * ClassObject.
	 * 
	 * @param	childClassObject	ClassObject of interest
	 * @return	List				list of AssociationEndObjects
	 */
	public List<AssociationEndObject> getAssociationsFromParents(ClassObject childClassObject) {
		List<AssociationEndObject> associations = associationsFromParents.get(childClassObject);

		////////////////////////////////////////////////////
		// found in local variable
		////////////////////////////////////////////////////
		if ( associations != null )
			return associations;
		////////////////////////////////////////////////////		
		// none found so go locate in the model hierarchy
		////////////////////////////////////////////////////		
		final List<AssociationEndObject> tmpAssociations = new ArrayList<>();
			
		ModelParser.modelParser().getAllClassesInHierarchy().forEach( parentClassObject -> 
			parentClassObject.getAssociations().forEach( association -> {
				if (association != null && association.getType().equals(childClassObject.getName()))
					tmpAssociations.add(association);					
			})
		);
	
		return tmpAssociations;
	}


	/**
	 * Gets all internal member data ready for a recreation of the model hierarchy
	 */
	private void prepare() {
		associationFromParentAsSiblingEnd 	= new HashMap<>();
		associationsFromParents 			= new HashMap<>();
		classesByName 						= new HashMap<>();
		handledAssociationCombinations 		= new HashMap();
		relationsHolder 					= new HashMap();
		typesUsed 							= new ArrayList();
	}

	// attributes
	////////////////////////////////////////////////////////////////////////////
	// ClassObject in use
	////////////////////////////////////////////////////////////////////////////
	protected ClassObject classObject 				= null;
	////////////////////////////////////////////////////////////////////////////
	// Helper container of ClassObjects
	////////////////////////////////////////////////////////////////////////////
	protected List<ClassObject> classesToGenerate 	= null;
	////////////////////////////////////////////////////////////////////////////
	// Helper container of ClassObjects with identity
	////////////////////////////////////////////////////////////////////////////	
	protected List<ClassObject> classesWithIdentity = null;
	////////////////////////////////////////////////////////////////////////////
	// application options as parameters
	////////////////////////////////////////////////////////////////////////////	
	protected Map<String, String> params 			= null;
	////////////////////////////////////////////////////////////////////////////
	// Helper mapping of ClassObject AssociationEndObjects
	////////////////////////////////////////////////////////////////////////////	
	protected Map<ClassObject, List<AssociationEndObject>> associationFromParentAsSiblingEnd = null;
	////////////////////////////////////////////////////////////////////////////
	// Helper mapping of ClassObject AssociationEndObjects
	////////////////////////////////////////////////////////////////////////////	
	protected Map<ClassObject, List<AssociationEndObject>> associationsFromParents = null;
	////////////////////////////////////////////////////////////////////////////
	// Helper mapping of name/ClassObject
	////////////////////////////////////////////////////////////////////////////	
	protected Map<String, ClassObject> classesByName = null;
	////////////////////////////////////////////////////////////////////////////
	// Helper mapping of AssociationEndObject combinations
	////////////////////////////////////////////////////////////////////////////		
	protected Map handledAssociationCombinations 	= null;
	////////////////////////////////////////////////////////////////////////////
	// Helper mapping of relationships queried
	////////////////////////////////////////////////////////////////////////////		
	protected Map relationsHolder 					= null;
	////////////////////////////////////////////////////////////////////////////
	// Helper list of types used
	////////////////////////////////////////////////////////////////////////////		
	protected List typesUsed 						= null;
}
