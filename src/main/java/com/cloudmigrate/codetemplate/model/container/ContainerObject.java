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
package com.cloudmigrate.codetemplate.model.container;

import com.cloudmigrate.codetemplate.model.BaseModelObject;
import com.cloudmigrate.codetemplate.model.classes.ClassObject;
import com.cloudmigrate.codetemplate.model.classes.enumerate.EnumClassObject;
import com.cloudmigrate.codetemplate.parser.ModelParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulate the notion of a Container which can contain classes, services, methods, and
 * other subsystems. Not all models support this concept.
 * 
 * @author realMethods, Inc.
 */
public class ContainerObject extends BaseModelObject {
	/**
	 * Default constructor
	 */
	public ContainerObject() {
		//no_op
	}

	/**
	 * Returns the classes field.
	 *   
	 * @return List<ClassObject>
	 */
	public List<ClassObject> getChildrenClassObjects() {
		return this.classes;
	}

	/**
	 * Assigns the list field the contents of the provided list argument.
	 * 
	 * @param list
	 */
	public void setChildrenClassObjects(List<ClassObject> list) {
		this.classes.addAll(list);
	}

	/**
	 * Assigns the enums fields with the provided list.
	 * 
	 * @param list
	 */
	public void setChildrenEnumClassObjects(List<EnumClassObject> list) {
		this.enums.addAll(list);
	}

	/**
	 * Returns the enums field.
	 * 
	 * @return List<EnumClassObject>
	 */
	public List<EnumClassObject> getChildrenEnumClassObjects() {
		return this.enums;
	}

	/**
	 * Returns the services field.
	 * 
	 * @return List<ClassObject>
	 */
	public List<ClassObject> getChildrenServiceObjects() {
		return this.services;
	}

	/**
	 * Assigns the services the contents of the provided list.
	 * @param list
	 */
	public void setChildrenServiceObjects(List<ClassObject> list) {
		this.services.addAll(list);
	}

	/**
	 * Returns the dtos field.
	 * 
	 * @return List<ClassObject>
	 */
	public List<ClassObject> getChildrenDTOObjects() {
		return this.dtos;
	}

	public void setChildrenDTOObjects(List<ClassObject> list) {
		this.dtos.addAll(list);
	}
	
	/**
	 * Adds a class to the container of children ClassObjects.
	 * 
	 * @param classArg
	 */
	public void addClass(ClassObject classArg) {
		getChildrenClassObjects().add(classArg);
	}

	/**
	 * Adds a class to the enum container of children EnumClassObject.
	 * 
	 * @param classArg
	 */
	public void addClass(EnumClassObject classArg) {
		getChildrenEnumClassObjects().add(classArg);
	}

	/**
	 * Adds a class to the service container of children ClassObject.
	 * 
	 * @param classArg
	 */	
	public void addService(ClassObject classArg) {
		getChildrenServiceObjects().add(classArg);
	}

	/**
	 * Adds a class to the dto container of children ClassObject.
	 * 
	 * @param classArg
	 */	
	public void addDTO(ClassObject classArg) {
		getChildrenDTOObjects().add(classArg);
	}

	/**
	 * Copy method performs a deep copy.
	 * 
	 * @param c
	 */
	public void copy(ContainerObject c) {
		super.copy(c);
		this.classes = c.classes;
		this.enums = c.enums;
		this.services = c.services;
		this.dtos = c.dtos;
	}

	/**
	 * a no_op
	 */
	public void finishLoading() {
		// no_op
	}

	@Override
	/**
	 * Since a Container is at the top of model hierarchy, 
	 * it only needs to return it's own classes.
	 * 
	 * @return List<BaseModelObject>
	 */
	public List<BaseModelObject> getAllInHierarchy() {
		List<BaseModelObject> all = new ArrayList<>();

		all.addAll(this.classes);

		return (all);
	}

	@Override
	/**
	 * Since a Container is at the top of model hierarchy, 
	 * it only needs to return it's own classes.
	 * 
	 * @return List<ClassObject>
	 */
	public List<ClassObject> getAllClassesInHierarchy() {
		return ( this.classes );
	}

	/**
	 * Returns the host field.
	 * 
	 * @return
	 */
	public String getHost() {
		return host;
	}
	
	/**
	 * Assigns the host field using the provided argument.
	 * 
	 * @param host
	 */
	public void setHost( String host ) {
		this.host = host;
	}

	/**
	 * Returns the port field.
	 * 
	 * @return
	 */
	public String getPort() {
		return port;
	}

	/**
	 * Assigns the port field using the provided argument.
	 * 
	 * @param port
	 */
	public void setPort( String port ) {
		this.port = port;
	}

	@Override
	/**
	 * Returns all ContainerObjects in the model hierarchy.  No need
	 * for traversing the hierarchy since ContainerObjects are at the root.
	 */
	protected List<? extends BaseModelObject> getAllOfLikeType() {
		return (ModelParser.modelParser().getContainers());
	}

	// attributes
	protected String host 					= null;
	protected String port 					= null;
	protected List<ClassObject> classes 	= new ArrayList<>();
	protected List<EnumClassObject> enums 	= new ArrayList<>();
	protected List<ClassObject> services 	= new ArrayList<>();
	protected List<ClassObject> dtos 		= new ArrayList<>();


}
