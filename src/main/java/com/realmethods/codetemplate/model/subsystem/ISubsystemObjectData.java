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

import java.util.List;

import com.realmethods.codetemplate.model.classes.ClassObject;
import com.realmethods.codetemplate.model.component.ComponentObject;

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
