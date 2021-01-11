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
package com.realmethods.codetemplate.model.subsystem;

import com.realmethods.codetemplate.model.BaseModelObject;
import com.realmethods.codetemplate.model.classes.ClassObject;
import com.realmethods.codetemplate.model.component.ComponentObject;
import com.realmethods.codetemplate.model.method.MethodObject;
import com.realmethods.codetemplate.parser.ModelParser;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Is the notion of a System which can contain components, classes, methods, and
 * other subsystems. Not all models support this concept.
 * 
 * Abstractly, a subsystem consists of other subsystems, components, classes, 
 * and businessMethods
 * 
 * @author realMethods, Inc.
 */
public class SubsystemObject extends BaseModelObject implements ISubsystemObjectData, ISubsystemObjectAction {

	/**
	 * Default constructor
	 */
	public SubsystemObject() {
		action = new SubsystemObjectAction(this);
	}

	/**
	 * Returns the action field.
	 * 
	 * @return SubsystemObjectAction
	 */
	public SubsystemObjectAction getAction() {
		return action;
	}
	

	@Override
	/**
	 * Delegates super type to interfaces
	 * 
	 * @param List<String>
	 */
	public void setSuperTypes(List<String> superTypes) {
		setInterfaces(superTypes);
	}

	/**
	 * Returns the subsystems field.  Stored as children of this instance
	 * 
	 * @return List<SubsystemObject>
	 */
	public List<SubsystemObject> getChildrenSubsystemObjects() {
		return this.subsystems;
	}

	/**
	 * Returns the components field.
	 * 
	 * @return List<ComponentObject>
	 */
	public List<ComponentObject> getChildrenComponentObjects() {
		return this.components;
	}

	/**
	 * Returns the classes field.
	 * 
	 * @return List<ClassObject>
	 */
	public List<ClassObject> getChildrenClassObjects() {
		return this.classes;
	}

	/***
	 * Assigns the provided list to the subsystems field.
	 * 
	 * @param list
	 */
	public void setChildrenSubsystemObjects(List<SubsystemObject> list) {
		this.subsystems.addAll(list);
	}

	/**
	 * Assigns the provided list to the components field.
	 * @param list
	 */
	public void setChildrenComponentObjects(List<ComponentObject> list) {
		this.components.addAll(list);
	}

	/**
	 * Assigns the provided list to the classes field.
	 * @param list
	 */
	public void setChildrenClassObjects(List<ClassObject> list) {
		this.classes.addAll(list);
	}

	/**
	 * Adds the provided subsystem to the collection of children subsystems.
	 * 
	 * @param subsystem
	 */
	public void addSubsystem(SubsystemObject subsystem) {
		getChildrenSubsystemObjects().add(subsystem);
	}

	/**
	 * Adds the provided component to the collection of children components.
	 * 
	 * @param component
	 */	
	public void addComponent(ComponentObject component) {
		getChildrenComponentObjects().add(component);
	}

	/**
	 * Adds the provided classobject to the collection of children classobjects.
	 * 
	 * @param classArg
	 */	
	public void addClass(ClassObject classArg) {
		getChildrenClassObjects().add(classArg);
	}

	/**
	 * Deep copy method.
	 * 
	 * @param c
	 */
	public void copy(SubsystemObject c) {
		super.copy(c);
		this.action = c.action;
		this.components = c.components;
		this.subsystems = c.subsystems;
		this.classes = c.classes;
	}

	/**
	 * A no_op for this implementation.
	 */
	public void finishLoading() {
		LOGGER.log( Level.INFO, "done" );
	}

	@Override
	/**
	 * Returns all BaseModelObjects with this instance as the root of the hierarchy.
	 * 
	 * This includes direct siblings and recursive walking the tree for their sibling, etc..
	 * 
	 * @return List<BaseModelObject>
	 */
	public List<BaseModelObject> getAllInHierarchy() {
		List<BaseModelObject> all = new ArrayList<>();
		List<BaseModelObject> tmp = new ArrayList<>();

		all.addAll(this.getChildrenSubsystemObjects());
		all.addAll(this.getChildrenComponentObjects());
		all.addAll(this.getChildrenClassObjects());

		tmp.addAll(all);

		for (BaseModelObject obj : all) {
			tmp.addAll(obj.getAllInHierarchy());
		}

		return (tmp);
	}

	/**
	 * Returns all SubsystemObject with this instance as the root of the hierarchy.
	 * 
	 * This includes direct siblings and recursive walking the tree for their sibling, etc..
	 * 
	 * @return List<BaseModelObject>
	 */
	public List<SubsystemObject> getAllSubsystemsInHierarchy() {
		List<SubsystemObject> container = new ArrayList<>();
		List<SubsystemObject> tmp = new ArrayList<>();

		container.addAll(this.getChildrenSubsystemObjects());

		tmp.addAll(this.getChildrenSubsystemObjects());

		for (SubsystemObject obj : container) {
			tmp.addAll(obj.getAllSubsystemsInHierarchy());
		}

		return (tmp);
	}

	/**
	 * Returns all ComponentObject with this instance as the root of the hierarchy.
	 * 
	 * This includes direct siblings and recursive walking the tree for their sibling, etc..
	 * 
	 * @return List<ComponentObject>
	 */
	public List<ComponentObject> getAllComponentsInHierarchy() {
		List<ComponentObject> container = new ArrayList<>();
		List<ComponentObject> tmp 		= new ArrayList<>();

		container.addAll(this.getChildrenComponentObjects());

		tmp.addAll(this.getChildrenComponentObjects());

		for (ComponentObject obj : container) {
			tmp.addAll(obj.getAllComponentsInHierarchy());
		}

		return (tmp);
	}

	@Override
	/**
	 * Returns all ClassObject with this instance as the root of the hierarchy.
	 * 
	 * This includes direct siblings and recursive walking the tree for their sibling, etc..
	 * 
	 * @return List<ClassObject>
	 */
	public List<ClassObject> getAllClassesInHierarchy() {
		List<BaseModelObject> classContainers 	= new ArrayList<>();
		List<ClassObject> tmp 					= new ArrayList<>();

		classContainers.addAll(this.getChildrenSubsystemObjects());
		classContainers.addAll(this.getChildrenComponentObjects());

		tmp.addAll(this.getChildrenClassObjects());

		for (BaseModelObject obj : classContainers) {
			tmp.addAll(obj.getAllClassesInHierarchy());
		}

		return (tmp);
	}

	@Override
	/**
	 * Forces to reconcile its own interfaces as valid
	 */
	public void reconcileSuperTypes() {
		action.reconcileSuperTypes();
	}

	/**
	 * Returns the businessMethods field.
	 * 
	 * @return List<MethodObject>
	 */
	public List<MethodObject> getBusinessMethods() {
		return businessMethods;
	}

	/**
	 * Add the provided MethodObject to the businessMethods field.
	 * 
	 * @param method
	 */
	public void addMethod(MethodObject method) {
		businessMethods.add(method);
	}

	/**
	 * Removes the provided MethodObject from the businessMethods field.
	 * 
	 * @param method
	 */
	public void removeMethod(MethodObject method) {
		businessMethods.remove(method);
	}

	/**
	 * The method is unique amongst current methods.
	 * 
	 * @param method
	 * @return boolean
	 */
	public boolean methodIsUnique(MethodObject method) {
		return businessMethods.contains(method);
	}

	@Override
	/**
	 * Returns all Subsystems through the entire hierarhcy.
	 * 
	 * This method provides type assurance when looking for all of a certain
	 * in the mode, but the actual type is not known.
	 * 
	 * return List<? extends BaseModelObject>
	 */
	protected List<? extends BaseModelObject> getAllOfLikeType() {
		return (ModelParser.modelParser().getAllSubsystemsInHierarchy());
	}

	// attributes
	protected SubsystemObjectAction action 		= null;
	protected List<SubsystemObject> subsystems 	= new ArrayList<>();
	protected List<ComponentObject> components 	= new ArrayList<>();
	protected List<ClassObject> classes 		= new ArrayList<>();
	protected List<MethodObject> businessMethods= new ArrayList<>();

	private static final Logger LOGGER 			= Logger.getLogger(SubsystemObject.class.getName());
}
