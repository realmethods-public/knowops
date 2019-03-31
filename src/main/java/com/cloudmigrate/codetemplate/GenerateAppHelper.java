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
package com.cloudmigrate.codetemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.cloudmigrate.codetemplate.model.association.AssociationEndObject;
import com.cloudmigrate.codetemplate.model.classes.ClassObject;
import com.cloudmigrate.codetemplate.model.classes.enumerate.EnumClassObject;
import com.cloudmigrate.codetemplate.parser.ModelParser;
import com.cloudmigrate.foundational.common.namespace.FrameworkNameSpace;

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
	 * default constructor, seeded with classes in this app generate session,
	 * and external config of key/value pairings
	 * 
	 * @param params
	 */
	public GenerateAppHelper(List classesToGenerate, Map<String, String> params) {
		this.classesToGenerate = classesToGenerate;
		this.params = params;

		// gets the internal field ready
		prepare();
	}

	/**
	 * The ClassObject of focus during an app creation session
	 * 
	 * @return
	 */
	public ClassObject getClassObject() {
		return classObject;
	}

	/**
	 * Assigns the ClassObject of focus during an app creation session
	 * 
	 * @param classObject
	 */
	public void setClassObject(ClassObject classObject) {
		this.classObject = classObject;
	}

	/**
	 * Helper to return the list of classes to generate over.
	 * 
	 * @return List<ClassObject>
	 */
	public List<ClassObject> getClassesToGenerate() {
		return ModelParser.modelParser().getClasses();
	}

	/**
	 * Helper to return the list of interfaces to generate over.
	 * 
	 * @return List<ClassObject>
	 */
	public List<ClassObject> getInterfacesToGenerate() {
		return ModelParser.modelParser().getInterfaces();
	}

	/**
	 * Helper to return the list of services to generate over.
	 * 
	 * @return List<ClassObject>
	 */
	public List<ClassObject> getServicesToGenerate() {
		return ModelParser.modelParser().getServices();
	}

	/**
	 * Helper to return the list of enum to generate over.
	 * 
	 * @return List<EnumClassObject>
	 */
	
	public List<EnumClassObject> getEnumClassesToGenerate() {
		return ModelParser.modelParser().getEnums();
	}

	/**
	 * Check to determine if the input enumClassName references a enum in the model.
	 * 
	 * @param enumClassName
	 * @return boolean
	 */
	public boolean isEnumerator(String enumClassName) {
		return (getEnumClassObject(enumClassName) != null);
	}

	/**
	 * Locates an enum class by name in the loaded model hierarchy.
	 * 
	 * @param name
	 * @return EnumClassObject
	 */
	public EnumClassObject getEnumClassObject(String name) {
		return (ModelParser.modelParser().findEnum(name));
	}

	/**
	 * The entire set of key/value pairings, deduced from the accumulation of
	 * options found in the all options.xml files contained in the tech stack
	 * package in use.
	 * 
	 * @return Map<String, String>
	 */
	public Map<String, String> getParams() {
		return params;
	}

	/**
	 * Returns a parameter based on the primary key within the params field.
	 * 
	 * @param paramKey
	 * @return String
	 */
	public String getParam(String paramKey) {
		return params.get(paramKey);
	}

	/**
	 * Convenince method to return a Map of params related to a given prefix.
	 * For example, all application options, found in the option.xml of the root
	 * directory of a Tech Stack Package, would be returned if the
	 * prefix="application". strip prefix removes the prefix from each found key
	 * itself
	 * 
	 * @param prefix
	 * @param stripPrefix
	 * @return
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
	 * @return String
	 */
	public String getModelSource() {
		return ("");
	}

	/**
	 * Returns the author parameter from the params field using application.author as the key.
	 * 
	 * @return
	 */
	public String getAuthor() {
		return params.get("application.author");
	}

	/**
	 * Delegates to getRooPackageName(boolean) using false to not append the class name.
	 * 
	 * @return String
	 */
	public String getRootPackageName() {
		return getRootPackageName(false);
	}

	/** 
	 * Returns the name of the application package name from the params field using
	 * key 'application.package name'.
	 * 
	 * The argument is ignored and the method is kept around for backwards capability.
	 * 
	 * @param appendClassName
	 * @return
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
	 * @param refClass
	 * @return
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
	 * @return String
	 */
	public String getRootPackageNameUnderscored() {
		return getRootPackageNameUnderscored(false);
	}

	/**
	 * Returns the root package name, substituting dot notation with underscore
	 * notation.  First, delegates to getRootPackageName(boolean). 
	 * 
	 * @param appendClassName
	 * @return String
	 */
	public String getRootPackageNameUnderscored(boolean appendClassName) {
		return getRootPackageName(appendClassName).replace('.', '_');
	}

	/**
	 * Returns the root package using directory notation. 
	 * 
	 * Delegates internally to getRootPackageName();
	 * 
	 * @return String
	 */
	public String getRootPackageAsDirectory() {
		return getRootPackageName().replace('.', java.io.File.separatorChar);
	}

	/**
	 * Returns the servlet name using 'application.servlet name' parameter on the params field.
	 * 
	 * @return String
	 */
	public String getServletName() {
		return params.get("application.servlet name");
	}

	/**
	 * Returns the corporate log url using 'application.corporate logo URL' parameter on the params field.
	 * 
	 * @return String
	 */
	public String getCorporateLogoURL() {
		return params.get("application.corporate logo URL");
	}

	/**
	 * Returns the application log url using 'application.application logo URL' parameter on the params field.
	 * 
	 * @return String
	 */	
	public String getApplicationLogoURL() {
		return params.get("application.application logo URL");
	}

	/**
	 * Returns the realMethods Platform version
	 * 
	 * @return String
	 */	
	public String getPlatformVersion() {
		return FrameworkNameSpace.VERSION;
	}
	
	/**
	 * Returns the application version name using 'application.version' parameter on the params field.
	 * 
	 * @return String
	 */		
	public String getVersion() {
		return params.get("application.version");
	}

	/**
	 * Returns the application name using 'application.name' parameter on the params field.
	 * 
	 * If none assigned, defaults to no_app_name_assigned.
	 * 
	 * @return String
	 */			
	public String getApplicationName() {
		String appName = params.get("application.name");

		if (appName == null || appName.isEmpty())
			appName = "no_app_name_assigned";

		return appName;
	}

	/**
	 * Returns an application name formated by removing single spaces.
	 * 
	 * @return String
	 */
	public String getApplicationNameFormatted()
	{
		return getApplicationName().replaceAll( " ", "" );
	}
	

	/**
	 * Returns the company name using 'application.company name' parameter on the params field.
	 * 
	 * If none assigned, defaults to no_app_name_assigned.
	 * 
	 * @return String
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
	 * @return String
	 */		
	public String getApplicationDescription() {
		return params.get("application.description");
	}

	/**
	 * Returns the multipleProcedures field.
	 * 
	 * @return Map
	 */
	public Map getMultipleProcedures() {
		return multipleProcedures;
	}

	/**
	 * Returns the handledAssociationCombinations field.
	 * 
	 * @return Map
	 */
	public Map getHandledAssociationCombinations() {
		return handledAssociationCombinations;
	}

	/**
	 * Returns the relationsHolder field.
	 * 
	 * @return Map
	 */
	public Map getRelationsHolder() {
		return relationsHolder;
	}

	/**
	 * Returns the typesUsed field.
	 * 
	 * @return List
	 */
	public List getTypesUsed() {
		return typesUsed;
	}

	/**
	 * Clear and return the typesUsed field.
	 * 
	 * @return List
	 */
	public List getTypesUsedAndClear() {
		typesUsed.clear();
		return typesUsed;
	}

	/**
	 * Return the transferObjectNames field.
	 * 
	 * @return List
	 */
	public List getTransferObjectNames() {
		return transferObjectNames;
	}
	
	/**
	 * Test method to determine if the ClassObject referenced by the className contains
	 * at least one primary key attribute.
	 * 
	 * Left over for backward compatibility.
	 * @param className
	 * @return
	 */
	public boolean hasIdentity(String className) {
		return ModelParser.hasIdentity(className);
	}

	/**
	 * Test method to determine if the ClassObject contains
	 * at least one primary key attribute.
	 *  
	 * @param object
	 * @return
	 */
	public boolean hasIdentity(ClassObject object) {
		if ( object != null )
			return object.hasIdentity();
		return false;
	}

	/**
	 * Test method to determine if the type provided references a class in the loaded model.
	 * 
	 * @param type
	 * @return boolean
	 */
	public boolean isClassType(String type) {
		return (getClassObject(type) != null);
	}

	/**
	 * Returns the ClassObject referred to className if found in the loaded model.
	 * 
	 * @param className
	 * @return
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
	 * primary key field
	 * 
	 * @return
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
	 * @return
	 */
	public List getContainers() {
		return( ModelParser.modelParser().getContainers() );
	}

	/**
	 * Helper method to return all the associations where the childClassObject is
	 * an end point.
	 * 
	 * @param childClassObject
	 * @return
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
	 * @param classObject
	 * @return
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
	 * @param childClassObject
	 * @return
	 */
	public List<AssociationEndObject> getAssociationsFromParents(ClassObject childClassObject) {
		List<AssociationEndObject> associations = associationsFromParents.get(childClassObject);

		// found in local variable
		if ( associations != null )
			return associations;
		
		// none found so go locate in the model hierarchy
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
	 * Defaults to true.
	 * 
	 * Held over for backwards compatibility.
	 * 
	 * @return
	 */
	public boolean usingDAOs() {
		return (true);
	}

	/**
	 * Defaults to true.
	 * 
	 * Held over for backwards compatibility.
	 * 
	 * @return
	 */
	public boolean usingLocalDeployment() {
		return true;
	}

	/**
	 * Defaults to true.
	 * 
	 * Held over for backwards compatibility.
	 * 
	 * @return
	 */
	public boolean usingAmazonWebServices() {
		return (false);
	}

	/**
	 * Defaults to false.
	 * 
	 * Held over for backwards compatibility.
	 * 
	 * @return
	 */
	public boolean usingGoogleUser() {
		return (false);
	}

	/**
	 * Defaults to true.
	 * 
	 * Held over for backwards compatibility.
	 * 
	 * @return
	 */
	public boolean usingHibernate() {
		return (true);
	}

	/**
	 * Defaults to true.
	 * 
	 * Held over for backwards compatibility.
	 * 
	 * @return
	 */
	public boolean usingJDO() {
		return (false);
	}

	/**
	 * Defaults to true.
	 * 
	 * Held over for backwards compatibility.
	 * 
	 * @return
	 */
	public boolean usingJPA() {
		return (false);
	}

	/**
	 * Defaults to false.
	 * 
	 * Held over for backwards compatibility.
	 * 
	 * @return
	 */
	public boolean usingGoogleAppEngineAndJDO() {
		return (false);
	}

	/**
	 * Defaults to false.
	 * 
	 * Held over for backwards compatibility.
	 * 
	 * @return
	 */
	public boolean usingGoogleAppEngineAndJPA() {
		return (false);
	}

	/**
	 * Defaults to false.
	 * 
	 * Held over for backwards compatibility.
	 * 
	 * @return
	 */
	public boolean usingStoredProcedure() {
		return (false);
	}

	/**
	 * Defaults to true.
	 * 
	 * Held over for backwards compatibility.
	 * 
	 * @return
	 */
	public boolean notUsingStoredProcedure() {
		return (true);
	}

	/**
	 * Defaults to true.
	 * 
	 * Held over for backwards compatibility.
	 * 
	 * @return
	 */
	public boolean generatePropertyFiles() {
		return (true);
	}

	/**
	 * Defaults to false.
	 * 
	 * Held over for backwards compatibility.
	 * 
	 * @return
	 */
	public boolean autoGeneratePKs() {
		return (false);
	}

	/**
	 * Defaults to false.
	 * 
	 * Held over for backwards compatibility.
	 * 
	 * @return
	 */
	public boolean generateSQLScript() {
		return false;
	}

	/**
	 * Held over for backwards compatibility.
	 * 
	 * @return
	 */
	public String getDeployDir() {
		return params.get("outputDir");
	}

	/**
	 * Held over for backwards compatibility.
	 * 
	 * @return
	 */
	public boolean usingLocalInterface() {
		return true;
	}

	/**
	 * Held over for backwards compatibility.
	 * 
	 * @return
	 */
	public boolean usingRemoteInterface() {
		return !usingLocalInterface();
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
		transferObjectNames 				= new ArrayList();
	}

	// attributes
	protected ClassObject classObject 				= null;
	protected List<ClassObject> classesToGenerate 	= null;
	protected List<ClassObject> classesWithIdentity = null;
	protected Map<String, String> params 			= null;
	protected Map<ClassObject, List> multipleProcedures = null;
	protected Map<ClassObject, List<AssociationEndObject>> associationFromParentAsSiblingEnd = null;
	protected Map<ClassObject, List<AssociationEndObject>> associationsFromParents = null;
	protected Map<String, ClassObject> classesByName = null;
	protected Map handledAssociationCombinations 	= null;
	protected Map relationsHolder 					= null;
	protected List typesUsed 						= null;
	protected List transferObjectNames 				= null;

}
