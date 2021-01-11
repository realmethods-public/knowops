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
package com.realmethods.codetemplate.parser;

import com.realmethods.codetemplate.model.*;
import com.realmethods.codetemplate.model.attribute.*;
import com.realmethods.codetemplate.model.classes.*;
import com.realmethods.codetemplate.model.container.*;
import com.realmethods.codetemplate.model.subsystem.*;
import com.realmethods.foundational.common.exception.ProcessingException;
import com.realmethods.codetemplate.model.component.*;
import com.realmethods.codetemplate.model.classes.enumerate.*;
import com.realmethods.codetemplate.model.association.*;
import com.realmethods.codetemplate.model.method.*;
import com.realmethods.codetemplate.*;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * This class handles the containment and technical parsing of a loaded model
 * into the proprietary structure of the goFramework. Contains a substantial set
 * of helper methods to provide different slices and views of the model
 * 
 * Singleton factory pattern applied.
 * 
 * @author realMethods, Inc.
 *
 */
public class ModelParser extends AppGenObject {

	/**
	 * default constructor
	 */
	protected ModelParser() {
		parserFinisher = new ModelParserFinisher(this);
		refresh();
	}

	/**
	 * Assigns a ModelParser instance to the internal modelParsers map, applying
	 * key.
	 * 
	 * @param key
	 * @param parser
	 */
	protected static void assign(String key, ModelParser parser) {
		modelParsers.put(key, parser);
	}

	/**
	 * Returns a ModelParser instance given the key.
	 * 
	 * @param key
	 * @return
	 */
	private static ModelParser modelParser(String key) {
		ModelParser modelParser = modelParsers.get(key);

		if (modelParser == null) {
			final String msg = "Creating a new ModelParser using key " + key;
			LOGGER.log(Level.INFO, msg);

			modelParser = new ModelParser();
			assign(key, modelParser);
		}

		return (modelParser);
	}

	/**
	 * Returns the current modelParser from the current thread, using the thread
	 * name as the key into the modelParser Map.
	 * 
	 * @return ModelParser
	 */
	public static ModelParser modelParser() {
		return (modelParser(Thread.currentThread().getName()));
	}

	/**
	 * Internal method used to reset all internal containers
	 */
	protected void refresh() {
		//////////////////////////////////////////////////////
		// clear the uber container so gc can take effect
		//////////////////////////////////////////////////////		
		clear();

		//////////////////////////////////////////////////////
		// instantiate a new HashMap
		//////////////////////////////////////////////////////		
		allBaseModelObjects = new HashMap<>();

		//////////////////////////////////////////////////////
		// create a map entry ArrayList for each type to contain
		//////////////////////////////////////////////////////		
		allBaseModelObjects.put(CONTAINER_KEY, new ArrayList<>());
		allBaseModelObjects.put(SUBSYSTEM_KEY, new ArrayList<>());
		allBaseModelObjects.put(COMPONENT_KEY, new ArrayList<>());
		allBaseModelObjects.put(CLASS_KEY, new ArrayList<>());
		allBaseModelObjects.put(SERVICE_KEY, new ArrayList<>());
		allBaseModelObjects.put(INTERFACE_KEY, new ArrayList<>());
		allBaseModelObjects.put(ENUM_KEY, new ArrayList<>());

		msgsWhileParsing = new StringBuilder();

	}

	/**
	 * Return all SubsystemObjects throughout the model hierarchy.
	 * 
	 * @return List<SubsystemObject>
	 */
	public List<SubsystemObject> getAllSubsystemsInHierarchy() {
		final List<SubsystemObject> subsystemObjects = new ArrayList<>();

		getSubsystems().forEach(subsystemObject -> subsystemObjects.addAll(subsystemObject.getAllSubsystemsInHierarchy()));

		return (subsystemObjects);
	}

	/**
	 * Return all ComponentObjects throughout the model hierarchy.
	 * 
	 * @return List<ComponentObject>
	 */
	public List<ComponentObject> getAllComponentsInHierarchy() {
		final List<ComponentObject> componentObjects = new ArrayList<>();

		getComponents().forEach(componentObject -> componentObjects.addAll(componentObject.getAllComponentsInHierarchy()));

		return (componentObjects);

	}

	/**
	 * Returns all ClassObjects in the model hiearchy
	 * 
	 * @return List<ClassObject>
	 */
	public List<ClassObject> getAllClassesInHierarchy() {
		List<BaseModelObject> classContainers 	= new ArrayList<>();
		final List<ClassObject> tmp 			= new ArrayList<>();

		classContainers.addAll(getContainers());
		classContainers.addAll(getSubsystems());
		classContainers.addAll(getComponents());

		tmp.addAll(getClasses());

		/////////////////////////////////////////////////////////////////////
		// iterate over all the containers, subsystems, and components to
		// acquire all classes in each of their hierarchies
		////////////////////////////////////////////////////////////////////
		classContainers.forEach(classContainer -> tmp.addAll(classContainer.getAllClassesInHierarchy()));

		return (tmp);
	}

	/**
	 * Return all containers, subsystems, components, classes, interfaces and
	 * enum in the model.
	 * 
	 * @return List<BaseModelObject>
	 */
	public List<BaseModelObject> getAllInHierarchy() {
		List<BaseModelObject> baseModelObjects = new ArrayList<>();
		final List<BaseModelObject> tmp = new ArrayList<>();

		baseModelObjects.addAll(getContainers());
		baseModelObjects.addAll(getSubsystems());
		baseModelObjects.addAll(getComponents());
		baseModelObjects.addAll(getClasses());
		baseModelObjects.addAll(getInterfaces());
		baseModelObjects.addAll(getEnums());

		tmp.addAll(baseModelObjects);

		baseModelObjects.forEach(baseModelObject -> tmp.addAll(baseModelObject.getAllInHierarchy()));

		return (tmp);
	}

	/**
	 * Searches the entire hierarchy for the first BaseModelObject with a matching
	 * name.
	 * 
	 * @param name
	 * @return BaseModelObject
	 */
	public BaseModelObject getBaseModelObject(final String name) {
		/////////////////////////////////////////////////////////////////////
		// iterate through all the nodes using the custom iterator
		/////////////////////////////////////////////////////////////////////

		if (name != null) {
			List<BaseModelObject> list = getAllInHierarchy().stream()
					.filter(baseModelObject -> name
							.equalsIgnoreCase(baseModelObject.getName()))
					.collect(Collectors.toList());
			
			if (!list.isEmpty())
				return (list.get(0));
		}

		return (null);
	}

	/**
	 * Helper method to check the existing of a name BaseModelObject in the
	 * model.
	 * 
	 * @param nodeName
	 * @return
	 */
	public boolean containsBaseModelObject(String name) {
		if (getBaseModelObject(name) != null)
			return (true);
		else
			return (false);
	}

	/**
	 * Returns all the ContainerModelObjects in the model.
	 * 
	 * @return List<ContainerObject>)
	 */
	public List<ContainerObject> getContainers() {
		return (List<ContainerObject>) this.allBaseModelObjects
				.get(CONTAINER_KEY);
	}

	/**
	 * Assigns the list as the list of ContainerObjects to the model.
	 * 
	 * @return List<ContainerObject>)
	 */
	public void setContainers(List<ContainerObject> list) {
		this.allBaseModelObjects.put(CONTAINER_KEY, list);
	}

	/**
	 * returns all root level subsystems
	 * @return
	 */
	public List<SubsystemObject> getSubsystems() {
		return (List<SubsystemObject>) allBaseModelObjects.get(SUBSYSTEM_KEY);
	}

	/**
	 * Assigns the list as the list of Subsystems to the model
	 * @return
	 */
	public void setSubsystems(List<SubsystemObject> list) {
		this.allBaseModelObjects.put(SUBSYSTEM_KEY, list);
	}

	/**
	 * returns all root level components
	 * @return
	 */
	public List<ComponentObject> getComponents() {
		return (List<ComponentObject>) allBaseModelObjects.get(COMPONENT_KEY);
	}

	/**
	 * Assigns the list as the list of Components to the model
	 * @return
	 */
	public void setComponents(List<ComponentObject> list) {
		allBaseModelObjects.put(COMPONENT_KEY, list);
	}

	/**
	 * returns all root level classes
	 * @return
	 */
	public List<ClassObject> getClasses() {
		return (List<ClassObject>) allBaseModelObjects.get(CLASS_KEY);
	}

	/**
	 * Assigns the list as the list of Classes to the model
	 * @return
	 */
	public void setClasses(List<ClassObject> list) {
		allBaseModelObjects.put(CLASS_KEY, list);
	}

	/**
	 * returns all root level interfaces
	 * @return
	 */
	public List<ClassObject> getInterfaces() {
		return (List<ClassObject>) allBaseModelObjects.get(INTERFACE_KEY);
	}

	/**
	 * Assigns the list as the list of interfaces to the model
	 * @return
	 */
	public void setInterfaces(List<ClassObject> list) {
		allBaseModelObjects.put(INTERFACE_KEY, list);
	}

	/**
	 * returns all root level services
	 * @return
	 */
	public List<ClassObject> getServices() {
		return (List<ClassObject>) allBaseModelObjects.get(SERVICE_KEY);
	}

	/**
	 * Assigns the list as the list of services to the model
	 * @return
	 */
	public void setServices(List<ClassObject> list) {
		allBaseModelObjects.put(SERVICE_KEY, list);
	}

	/**
	 * returns all root level enums
	 * @return
	 */
	public List<EnumClassObject> getEnums() {
		return (List<EnumClassObject>) allBaseModelObjects.get(ENUM_KEY);
	}

	/**
	 * Assigns the list as the list of Enums to the model
	 * @return
	 */
	public void setEnums(List<EnumClassObject> list) {
		allBaseModelObjects.put(ENUM_KEY, list);
	}

	/**
	 * Traverses the root level of the model and locates the first BaseModelObject 
	 * with a matching name then removes it
	 * 
	 * @param name
	 * @return boolean
	 */
	public boolean remove(String name) {
		BaseModelObject baseModelObject;

		/////////////////////////////////////////////////////////////
		// search for as a ClassObject
		/////////////////////////////////////////////////////////////
		baseModelObject = findClass(name);
		if (baseModelObject != null) {
			getClasses().remove(baseModelObject);
			return (true);
		}

		/////////////////////////////////////////////////////////////
		// search for as an EnumClassObject
		/////////////////////////////////////////////////////////////
		baseModelObject = findEnum(name);
		if (baseModelObject != null) {
			getEnums().remove(baseModelObject);
			return (true);
		}

		/////////////////////////////////////////////////////////////
		// search for as a ContainerObject
		/////////////////////////////////////////////////////////////
		baseModelObject = findContainer(name);
		if (baseModelObject != null) {
			getContainers().remove(baseModelObject);
			return (true);
		}

		/////////////////////////////////////////////////////////////
		// search for as a SubsystemObject
		/////////////////////////////////////////////////////////////
		baseModelObject = findSubsystem(name);
		if (baseModelObject != null) {
			getSubsystems().remove(baseModelObject);
			return (true);
		}

		/////////////////////////////////////////////////////////////
		// search for as a ComponentObject
		/////////////////////////////////////////////////////////////
		baseModelObject = findComponent(name);
		if (baseModelObject != null) {
			getComponents().remove(baseModelObject);
			return (true);
		}

		/////////////////////////////////////////////////////////////
		// search for as a ServiceObject
		/////////////////////////////////////////////////////////////
		baseModelObject = findService(name);
		if (baseModelObject != null) {
			getServices().remove(baseModelObject);
			return (true);
		}

		/////////////////////////////////////////////////////////////
		// search for as a InterfaceObject
		/////////////////////////////////////////////////////////////
		baseModelObject = findInterface(name);
		if (baseModelObject != null) {
			getInterfaces().remove(baseModelObject);
			return (true);
		}

		return (false);
	}

	/**
	 * Returns a List<String> of all the declared package names in the model.
	 * 
	 * @return List<String>
	 */
	public List<String> getPackages() {
		final List<String> pkgs = new ArrayList<>();

		/////////////////////////////////////////////////////////////
		// Iterate everything in the hierarchy inquiring of 
		// its package name
		/////////////////////////////////////////////////////////////
		getAllInHierarchy().forEach(baseModelObject -> {
			String pkgName = baseModelObject.getPackageName();

			// if a non-null package and not yet accounted for
			if (pkgName != null && !pkgs.contains(pkgName)) {
				pkgs.add(pkgName);
			}
		});

		return (pkgs);
	}

	/**
	 * Add a subsystem to the root level
	 * @param subsystemObject
	 */
	public void addSubsystem(SubsystemObject subsystemObject) {
		this.getSubsystems().add(subsystemObject);
	}

	/**
	 * Add a component to the root level
	 * @param componentObject
	 */
	public void addComponent(ComponentObject componentObject) {
		this.getComponents().add(componentObject);
	}

	/**
	 * Add a container to the root level
	 * @param containerObject
	 */
	public void addContainer(ContainerObject containerObject) {
		this.getContainers().add(containerObject);
	}

	/**
	 * Determines the ClassObject type and adds it the correct Map.
	 * 
	 * If the type turns out to be a ClassObject (not a sub-class) and it
	 * already exists by name in the model, the ModelParser will attempt to
	 * blend the ClassObject instances together.
	 * 
	 * See the blend(ClassObject) method for more details.
	 * 
	 * @param classObject
	 */
	public void addClass(ClassObject classObject) {
		if (classObject.isEnumerator())
			addEnum(new EnumClassObject(classObject));
		else if (classObject.isService())
			addService(classObject);
		else if (classObject.isInterface())
			addInterface(classObject);
		else { // default as ClassObject
			ClassObject foundClassObject = this
					.findClass(classObject.getName());

			if (foundClassObject != null)
				blendClasses(foundClassObject, classObject);
			else
				getClasses().add(classObject);
		}

	}

	/**
	 * Add the ClassObject to the root level Service container
	 * @param serviceObject
	 */
	public void addService(ClassObject serviceObject) {
		this.getServices().add(serviceObject);
	}

	/**
	 * Add Enum to the root level Enum container
	 * @param enumObject
	 */
	public void addEnum(EnumClassObject enumObject) {
		this.getEnums().add(enumObject);
	}

	/**
	 * Add ClassObject to the root level interface container
	 * @param interfaceObject
	 */
	public void addInterface(ClassObject interfaceObject) {
		this.getInterfaces().add(interfaceObject);
	}

	/**
	 * Determines if the ClassObject has at least one attribute designated as a primary key either
	 * directly or via it's parent.  The method is iterative and will traverse the child-->parent tree
	 * until exhausted or such an attribute is found.
	 * 
	 * @param classObject
	 * @return
	 */
	public boolean hasIdentity(ClassObject classObject) {
		boolean hasIdentity = false;

		if (classObject != null)
			hasIdentity = classObject.hasIdentity();
		else
			return hasIdentity;

		if (!hasIdentity)
			///////////////////////////////////////////////////
			// try up the chain...
			///////////////////////////////////////////////////
			hasIdentity = hasIdentityHelper(classObject);

		return hasIdentity;
	}

	/**
	 * Delegates to the getBaseModelObject(String) method.
	 * 
	 * @param name
	 * @return BaseModelObject
	 */
	public BaseModelObject locateBaseModelObject(String name) {
		return (getBaseModelObject(name));
	}

	/**
	 * Attempts to locate a ContainerObject by name using the internal
	 * findHelper method.  This search is within the root level of the model
	 * 
	 * @param name
	 * @return
	 */
	public ContainerObject findContainer(String name) {
		return ((ContainerObject) findHelper(this.getContainers(), name));
	}

	/**
	 * Attempts to locate a EnumClassObject by name using the internal
	 * findHelper method.  This search is within the root level of the model
	 * 
	 * @param name
	 * @return
	 */
	public EnumClassObject findEnum(String name) {
		return ((EnumClassObject) findHelper(this.getEnums(), name));
	}

	/**
	 * Attempts to locate a EnumClassObject by name using the internal
	 * findHelper method.  This search is throughout the hierarchy and
	 * completes either when the navigation is exhausted or a match is found
	 * 
	 * @param name
	 * @return
	 */
	public EnumClassObject findEnumInHiearchy(String name) {
		EnumClassObject enumClassObject = findEnum(name);

		if (enumClassObject == null && name != null) {
			//////////////////////////////////////////////////
			// look through containers
			//////////////////////////////////////////////////
			for (ContainerObject container : getContainers()) {
				for (EnumClassObject tmpEnumClassObject : container
						.getChildrenEnumClassObjects()) {
					///////////////////////////////////////////////////////
					// compare names
					///////////////////////////////////////////////////////
					if (name.equals(tmpEnumClassObject.getName())) {
						return (tmpEnumClassObject);
					}
				}
			}
		}

		return (enumClassObject);
	}

	/**
	 * Attempts to locate a ClassObject by name using the internal
	 * findHelper method.  This search is within the root level of the model
	 * 
	 * @param name
	 * @return
	 */	
	public ClassObject findClass(String name) {
		return ((ClassObject) findHelper(this.getClasses(), name));
	}

	/**
	 * Attempts to locate a ClassObject as an interface by name using the internal
	 * findHelper method.  This search is within the root level of the model
	 * 
	 * @param name
	 * @return
	 */
	public ClassObject findInterface(String name) {
		return ((ClassObject) findHelper(this.getInterfaces(), name));
	}

	/**
	 * Attempts to locate a ComponentObject by name using the internal
	 * findHelper method.  This search is within the root level of the model
	 * 
	 * @param name
	 * @return
	 */
	public ComponentObject findComponent(String name) {
		return ((ComponentObject) findHelper(this.getComponents(), name));
	}

	/**
	 * Attempts to locate a SubsystemObject by name using the internal
	 * findHelper method.  This search is within the root level of the model
	 * 
	 * @param name
	 * @return
	 */	
	public SubsystemObject findSubsystem(String name) {
		return ((SubsystemObject) findHelper(this.getSubsystems(), name));
	}

	/**
	 * Attempts to locate a Service as a ClassObject by name using the internal
	 * findHelper method.  This search is within the root level of the model
	 * 
	 * @param name
	 * @return
	 */
	public ClassObject findService(String name) {
		return ((ClassObject) findHelper(this.getServices(), name));
	}

	/**
	 * Helper method used to locate a named BaseModelObject within the provided list
	 * 
	 * @param list
	 * @param name
	 * @return
	 */
	protected BaseModelObject findHelper(List<? extends BaseModelObject> list, String name) {
		
		List<BaseModelObject> filteredList = list.stream()
				.filter(baseModelObject -> baseModelObject.getName().equalsIgnoreCase(name))
				.collect(Collectors.toList());
		if ( filteredList.isEmpty() )
			return (null);
		else
			return(filteredList.get(0));
	}

	/**
	 * Helper method used to test the classObject's identity. 
	 * If none, attempt to ask its parent if one. 
	 * @param classObject
	 * @return
	 */
	protected boolean hasIdentityHelper(ClassObject classObject) {
		boolean hasIdentity = classObject.hasIdentity();

		if (!hasIdentity) {
			String parentName = classObject.getParentName();
			if (parentName != null && parentName.length() > 0) {
				BaseModelObject tmp = locateBaseModelObject(parentName);
				if (tmp != null)
					hasIdentity = hasIdentityHelper((ClassObject) tmp);
				else
					LOGGER.log(Level.SEVERE, () -> classObject.getName()
							+ " has parent " + parentName
							+ " which was not included during loading.");
			}
		}

		return (hasIdentity);
	}

	/**
	 * Allows for the external notification that loading of the model is at the end.
	 * 
	 * The internal ModelParserFinisher instance will be asked to finish.
	 * 
	 * @throws ProcessingException
	 */
	public void postModelLoading() throws ProcessingException {
		LOGGER.log(Level.INFO, "ModelParser.postModelLoading() - Executing logic to finish model loading");

		try {
			parserFinisher.finish();
		} catch (Exception exc) {
			LOGGER.log( Level.WARNING, "Post Model Loading Failed", exc );
			throw new ProcessingException( "Failure during ModelParser.postModelLoading()", exc);
		}
	}

	/**
	 * If the provided ClassObject has no identity (no primary key attributes) and
	 * it has no parent, an attribute with the name <className>Id will be created,
	 * have the isPrimaryKey flag set to true and added to the provided classObject.
	 * 
	 * @param classObject
	 */
	public void applyIdentityHelper(ClassObject classObject) {
		AttributeObject pkAttribute = null;
		String id = null;

		///////////////////////////////////////////////////////////
		// apply identity if none and no parent
		///////////////////////////////////////////////////////////
		if (!hasIdentity(classObject) && !classObject.hasParent()) {
			log("Applying identity for " + classObject.getName() + "...");

			id = classObject.getName().toLowerCase() + "Id";
			pkAttribute = classObject.findAttribute(id);

			if (pkAttribute == null) {
				pkAttribute = new AttributeObject();
				pkAttribute.setClassObject(classObject);
				pkAttribute.setName(id);
				pkAttribute.setType("Long");
				pkAttribute.setDefaultValue("null");
				pkAttribute.setVisibility("protected");
				pkAttribute.isPrimaryKey(true);
				pkAttribute.canBeNull(false);
				pkAttribute.isIntrinsicType(false);
				pkAttribute.isStatic(false);
				pkAttribute.isFinal(false);
				pkAttribute.isTransient(false);
				pkAttribute.isVolatile(false);

				log("assigning attribute primary key " + pkAttribute.getName()
						+ " to " + classObject.getName());

				classObject.addAttribute(pkAttribute);
			} else {
				pkAttribute.isPrimaryKey(true);
			}
			classObject.hasForcedIdentity(true);
		} else {
			log("Ignored identity for class " + classObject.getName() + "...");
		}

	}

	/**
	 * Runs through the model and applies identity to ClassObjects that do not have
	 * identity.
	 * 
	 * Delegates internally to applyIdentityHelper(ClassObject).
	 */
	public void applyIdentity() {
		for (ClassObject classObject : this.getAllClassesInHierarchy()) {
			applyIdentityHelper(classObject);
		}
	}

	/**
	 * Helper method to locate the ClassObject in the model by className and
	 * discover if it has identity.
	 * 
	 * @param className
	 * @return
	 */
	public static boolean hasIdentity(String className) {
		ModelParser modelParser = modelParser();
		return modelParser.hasIdentity(modelParser.findClass(className));
	}

	/**
	 * Clears all Lists from the root level container HashMap
	 */
	protected void clear() {
		this.allBaseModelObjects.clear();
		this.msgsWhileParsing = null;
	}

	public void applyMessage(String msg) {
		LOGGER.log(Level.INFO, msg);
	}

	public String toString() {
		return ( "all nodes = " + allBaseModelObjects );
	}

	public String getMessages() {
		return this.msgsWhileParsing.toString();
	}

	/**
	 * This method allows for the external assignment of types to primary key attributes.
	 * By default, they are assigned a Long value if created implicitly or assigned the 
	 * specified value if from a source where it can be assigned.
	 * @param type
	 */
	public void assignPrimaryKeyType(final String type) {
		if (type != null) {
			for (ClassObject classObject : getAllClassesInHierarchy()) {
				classObject.getPrimaryKeyAttributes().forEach(pk -> pk.setType(type));
			}
		}
	}

	/**
	 * Add the attributes, associations, and business method of the reference class to the source
	 * class only if they do not exist.
	 * No overwriting takes place for similarly named attributes, but not for associations or methods
	 * 
	 * @param sourceClass
	 * @param referenceClass
	 * @return ClassObjeect
	 */
	protected ClassObject blendClasses(ClassObject sourceClass,
			ClassObject referenceClass) {
		if (sourceClass != null && referenceClass != null) {
			referenceClass.getAttributes().forEach( attribute -> {
				if (sourceClass.findAttribute(attribute.getName()) == null)
					sourceClass.addAttribute(attribute);
			});
			sourceClass.getAssociations()
					.addAll(referenceClass.getAssociations());
			sourceClass.getBusinessMethods()
					.addAll(referenceClass.getBusinessMethods());
		}

		return (sourceClass);
	}

	//////////////////////////////////////////////////////////////////////////////
	// singleton model parser, only one allowed per thread
	//////////////////////////////////////////////////////////////////////////////
	protected static Map<String, ModelParser> modelParsers = new HashMap<>();
	//////////////////////////////////////////////////////////////////////////////	
	// thread specific model parser
	//////////////////////////////////////////////////////////////////////////////
	protected static ModelParser currentModelParser 		= null;
	//////////////////////////////////////////////////////////////////////////////
	// helper to build out the messages during parsing
	//////////////////////////////////////////////////////////////////////////////
	protected StringBuilder msgsWhileParsing 				= null;
	//////////////////////////////////////////////////////////////////////////////	
	// Map of root elements of the model
	//////////////////////////////////////////////////////////////////////////////
	protected Map<String, List<? extends BaseModelObject>> allBaseModelObjects = new HashMap<>();

	//////////////////////////////////////////////////////////////////////////////
	// keys into the Map of root elements
	//////////////////////////////////////////////////////////////////////////////
	private static final String CONTAINER_KEY 				= "container";
	private static final String SUBSYSTEM_KEY 				= "subsystem";
	private static final String COMPONENT_KEY 				= "component";
	private static final String CLASS_KEY 					= "class";
	private static final String INTERFACE_KEY 				= "interface";
	private static final String SERVICE_KEY 				= "service";
	private static final String ENUM_KEY 					= "enum";

	//////////////////////////////////////////////////////////////////////////////
	// helper object used to finish the model parsing
	//////////////////////////////////////////////////////////////////////////////
	private ModelParserFinisher parserFinisher 				= null;
	
	private static final Logger LOGGER 						= Logger.getLogger(ModelParser.class.getName());

}
