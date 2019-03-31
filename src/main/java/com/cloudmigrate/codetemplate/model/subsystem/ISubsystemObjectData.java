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
package com.cloudmigrate.codetemplate.model.subsystem;

import java.util.List;

import com.cloudmigrate.codetemplate.model.classes.ClassObject;
import com.cloudmigrate.codetemplate.model.component.ComponentObject;

/**
 * An interface for class SystemObjectData
 * 
 * @author realMethods, Inc.
 * 
 */
public interface ISubsystemObjectData {
	public List<SubsystemObject> getChildrenSubsystemObjects();
	public List<ComponentObject> getChildrenComponentObjects();
	public List<ClassObject> getChildrenClassObjects();
	public void addSubsystem(SubsystemObject node);
	public void addComponent(ComponentObject component);
	public void addClass(ClassObject classArg);
}
