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
package com.cloudmigrate.codetemplate.parser;

import com.cloudmigrate.codetemplate.model.*;
import com.cloudmigrate.codetemplate.model.attribute.*;
import com.cloudmigrate.codetemplate.model.classes.*;
import com.cloudmigrate.codetemplate.model.container.*;
import com.cloudmigrate.codetemplate.model.subsystem.*;
import com.cloudmigrate.foundational.common.exception.ProcessingException;
import com.cloudmigrate.codetemplate.model.component.*;
import com.cloudmigrate.codetemplate.model.classes.enumerate.*;
import com.cloudmigrate.codetemplate.model.association.*;
import com.cloudmigrate.codetemplate.model.method.*;
import com.cloudmigrate.codetemplate.*;

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
		clear();

		allBaseModelObjects = new HashMap<>();

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
		List<BaseModelObject> classContainers = new ArrayList<>();
		final List<ClassObject> tmp = new ArrayList<>();

		classContainers.addAll(getContainers());
		classContainers.addAll(getSubsystems());
		classContainers.addAll(getComponents());

		tmp.addAll(getClasses());

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
	 * Searches the hierarchy for the first BaseModelObject with a matching
	 * name.
	 * 
	 * @param name
	 * @return BaseModelObject
	 */
	public BaseModelObject getBaseModelObject(final String name) {
		// iterate through all the nodes using the custom iterator

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

	public List<SubsystemObject> getSubsystems() {
		return (List<SubsystemObject>) allBaseModelObjects.get(SUBSYSTEM_KEY);
	}

	public void setSubsystems(List<SubsystemObject> list) {
		this.allBaseModelObjects.put(SUBSYSTEM_KEY, list);
	}

	public List<ComponentObject> getComponents() {
		return (List<ComponentObject>) allBaseModelObjects.get(COMPONENT_KEY);
	}

	public void setComponents(List<ComponentObject> list) {
		allBaseModelObjects.put(COMPONENT_KEY, list);
	}

	public List<ClassObject> getClasses() {
		return (List<ClassObject>) allBaseModelObjects.get(CLASS_KEY);
	}

	public void setClasses(List<ClassObject> list) {
		allBaseModelObjects.put(CLASS_KEY, list);
	}

	public List<ClassObject> getInterfaces() {
		return (List<ClassObject>) allBaseModelObjects.get(INTERFACE_KEY);
	}

	public void setInterfaces(List<ClassObject> list) {
		allBaseModelObjects.put(INTERFACE_KEY, list);
	}

	public List<ClassObject> getServices() {
		return (List<ClassObject>) allBaseModelObjects.get(SERVICE_KEY);
	}

	public void setServices(List<ClassObject> list) {
		allBaseModelObjects.put(SERVICE_KEY, list);
	}

	public List<EnumClassObject> getEnums() {
		return (List<EnumClassObject>) allBaseModelObjects.get(ENUM_KEY);
	}

	public void setEnums(List<EnumClassObject> list) {
		allBaseModelObjects.put(ENUM_KEY, list);
	}

	/**
	 * Traverses the model and locates the fist BaseModelObject with a matching
	 * name.
	 * 
	 * @param name
	 * @return boolean
	 */
	public boolean remove(String name) {
		BaseModelObject baseModelObject;

		baseModelObject = findEnum(name);
		if (baseModelObject != null) {
			getEnums().remove(baseModelObject);
			return (true);
		}

		baseModelObject = findContainer(name);
		if (baseModelObject != null) {
			getContainers().remove(baseModelObject);
			return (true);
		}

		baseModelObject = findClass(name);
		if (baseModelObject != null) {
			getClasses().remove(baseModelObject);
			return (true);
		}

		baseModelObject = findSubsystem(name);
		if (baseModelObject != null) {
			getSubsystems().remove(baseModelObject);
			return (true);
		}

		baseModelObject = findComponent(name);
		if (baseModelObject != null) {
			getComponents().remove(baseModelObject);
			return (true);
		}

		baseModelObject = findService(name);
		if (baseModelObject != null) {
			getServices().remove(baseModelObject);
			return (true);
		}

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

		getAllInHierarchy().forEach(baseModelObject -> {
			String pkgName = baseModelObject.getPackageName();

			// if a non-null package and not yet accounted for
			if (pkgName != null && !pkgs.contains(pkgName)) {
				pkgs.add(pkgName);
			}
		});

		return (pkgs);
	}

	public void addSubsystem(SubsystemObject subsystemObject) {
		this.getSubsystems().add(subsystemObject);
	}

	public void addComponent(ComponentObject componentObject) {
		this.getComponents().add(componentObject);
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
		else {
			ClassObject foundClassObject = this
					.findClass(classObject.getName());

			if (foundClassObject != null)
				blendClasses(foundClassObject, classObject);
			else
				getClasses().add(classObject);
		}

	}

	public void addService(ClassObject classObject) {
		this.getServices().add(classObject);
	}

	public void addEnum(EnumClassObject classObject) {
		this.getEnums().add(classObject);
	}

	public void addInterface(ClassObject classObject) {
		this.getInterfaces().add(classObject);
	}

	public boolean hasIdentity(ClassObject classObject) {
		boolean hasIdentity = false;

		// not to applied to abstract classes...
		if (classObject != null)
			hasIdentity = classObject.hasIdentity();
		else
			return hasIdentity;

		if (!hasIdentity)
			// try up the chain...
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
	 * findHelper method.
	 * 
	 * @param name
	 * @return
	 */
	public ContainerObject findContainer(String name) {
		return ((ContainerObject) findHelper(this.getContainers(), name));
	}

	public EnumClassObject findEnum(String name) {
		return ((EnumClassObject) findHelper(this.getEnums(), name));
	}

	public EnumClassObject findEnumInHiearchy(String name) {
		EnumClassObject enumClassObject = findEnum(name);

		if (enumClassObject == null && name != null) {
			// look through containers
			for (ContainerObject container : getContainers()) {
				for (EnumClassObject tmpEnumClassObject : container
						.getChildrenEnumClassObjects()) {
					if (name.equals(tmpEnumClassObject.getName())) {
						return (tmpEnumClassObject);
					}
				}
			}
		}

		return (enumClassObject);
	}

	public ClassObject findClass(String name) {
		return ((ClassObject) findHelper(this.getClasses(), name));
	}

	public ClassObject findInterface(String name) {
		return ((ClassObject) findHelper(this.getInterfaces(), name));
	}

	public ComponentObject findComponent(String name) {
		return ((ComponentObject) findHelper(this.getComponents(), name));
	}

	public SubsystemObject findSubsystem(String name) {
		return ((SubsystemObject) findHelper(this.getSubsystems(), name));
	}

	public ClassObject findService(String name) {
		return ((ClassObject) findHelper(this.getServices(), name));
	}

	protected BaseModelObject findHelper(List<? extends BaseModelObject> list, String name) {
		
		List<BaseModelObject> filteredList = list.stream()
				.filter(baseModelObject -> baseModelObject.getName().equalsIgnoreCase(name))
				.collect(Collectors.toList());
		if ( filteredList.isEmpty() )
			return (null);
		else
			return(filteredList.get(0));
	}

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
	 * Clears all 
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

	public void assignPrimaryKeyType(final String type) {
		if (type != null) {
			for (ClassObject classObject : getAllClassesInHierarchy()) {
				classObject.getPrimaryKeyAttributes().forEach(pk -> pk.setType(type));
			}
		}
	}

	/**
	 * Add the attributes, associations, and business method of the reference class to the source
	 * class only if they do not exist.  There is no overwriting that takes place
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

	// singleton model parser, only one allowed per thread
	protected static Map<String, ModelParser> modelParsers = new HashMap<>();
	// thread specific model parser
	protected static ModelParser currentModelParser 		= null;
	// helper to build out the messages during parsing
	protected StringBuilder msgsWhileParsing 				= null;
	// Map of root elements of the model
	protected Map<String, List<? extends BaseModelObject>> allBaseModelObjects = new HashMap<>();

	// keys into the Map of root elements
	private static final String CONTAINER_KEY 				= "container";
	private static final String SUBSYSTEM_KEY 				= "subsystem";
	private static final String COMPONENT_KEY 				= "component";
	private static final String CLASS_KEY 					= "class";
	private static final String INTERFACE_KEY 				= "interface";
	private static final String SERVICE_KEY 				= "service";
	private static final String ENUM_KEY 					= "enum";

	// helper object used to finish the model parsing
	private ModelParserFinisher parserFinisher 				= null;
	private static final Logger LOGGER 						= Logger.getLogger(ModelParser.class.getName());

}
