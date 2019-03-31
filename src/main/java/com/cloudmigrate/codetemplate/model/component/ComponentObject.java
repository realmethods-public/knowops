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
package com.cloudmigrate.codetemplate.model.component;

import com.cloudmigrate.codetemplate.model.BaseModelObject;
import com.cloudmigrate.codetemplate.model.classes.ClassObject;
import com.cloudmigrate.codetemplate.model.subsystem.SubsystemObject;
import com.cloudmigrate.codetemplate.parser.ModelParser;

import java.util.*;

/**
 * Wrapper for the concept of a Component in a model.  
 * A component can contain other Components as well as ClassObjects and is treated as as specialization of a
 * SubsystemObject.
 *  
 * @author realMethods, Inc.
 *
 */
public class ComponentObject extends SubsystemObject
{
	/**
	 * Default constructor
	 */
	public ComponentObject() {
		// no_op
	}

	/**
	 * Returns all Components and ClassObjects in the model hierarchy 
	 * starting with this Component as the root.
	 * @return
	 */
	@Override
	public List<BaseModelObject> getAllInHierarchy()
    {
    	List<BaseModelObject> all = new ArrayList<>();
    	final List<BaseModelObject> tmp = new ArrayList<>();
    	
    	all.addAll( this.getChildrenComponentObjects() );
    	all.addAll( this.getChildrenClassObjects() );
    	
    	tmp.addAll( all );
    	
    	all.forEach(obj -> tmp.addAll( obj.getAllInHierarchy() ));
    	
    	return( tmp );
    }
	
	/**
	 * Returns all Components in the model hiearchy starting with this Component as the root.
	 * @return
	 */
	@Override
	public List<ComponentObject> getAllComponentsInHierarchy()
    {
    	List<ComponentObject> all = new ArrayList<>();
    	List<ComponentObject> tmp = new ArrayList<>();
    	
    	all.addAll( this.getChildrenComponentObjects() );
    	
    	for( ComponentObject obj: all )
    	{
    		tmp.addAll( obj.getAllComponentsInHierarchy() );
    	}
    	
    	return( tmp );
    }

	/**
	 * Returns all ClassObjects in the model hierarchy starting with this Component as the root.
	 * @return
	 */	
	@Override
	public List<ClassObject> getAllClassesInHierarchy()
    {
    	List<ComponentObject> all = new ArrayList<>();
    	List<ClassObject> tmp = new ArrayList<>();
    	
    	all.addAll( this.getChildrenComponentObjects() );
    	tmp.addAll( this.getChildrenClassObjects() );
    	
    	for( ComponentObject obj: all )
    	{
    		tmp.addAll( obj.getAllClassesInHierarchy() );
    	}
    	
    	return( tmp );
    }

	/**
	 * Convenience method to return all Components in the model Hierarchy
	 * 
	 * @return List<? extends BaseModelObject>
	 */
	@Override
	protected List<? extends BaseModelObject> getAllOfLikeType()
	{ return( ModelParser.modelParser().getAllComponentsInHierarchy() ); }

// attributes

}
