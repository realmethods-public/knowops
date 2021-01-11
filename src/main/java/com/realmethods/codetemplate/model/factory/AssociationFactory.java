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
/**
 * Copyright (c) 2021
 *  
 * This file is part of realMethods.
 *
 * realMethods is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * realMethods is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with realMethods.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *   realMethods Inc - initial API and implementation
 */ package com.realmethods.codetemplate.model.factory;

import java.lang.reflect.Field;

import org.eclipse.emf.ecore.EReference;
import org.w3c.dom.Node;

import com.realmethods.codetemplate.model.association.ClassAssociationEndObject;
import com.realmethods.codetemplate.model.association.EcoreAssociationEndObject;
import com.realmethods.codetemplate.model.association.NodeAssociationEndObject;
import com.realmethods.codetemplate.model.association.AssociationEndObject;

/**
 *
 * Factory class used to create the variety of AssocationEndObject types.
 * @author realMethods, Inc.
 *
 */
public class AssociationFactory 
{
	/**
	 * default constructor - intentionally inaccessible, use factory method getInstance().
	 */
	protected AssociationFactory() {
		// no_op
	}
	
	/**
	 * Factory method to return a singleton as a single static instance.
	 * 
	 * @return AssociationFactory
	 */
	public static AssociationFactory getInstance() {
		if ( self == null )
			self = new AssociationFactory();
		
		return( self );
	}
	
	/**
	 * Returns an AssociationEndObject.
	 * 
	 * @param node
	 * @param findTheSibling
	 * @return
	 */
	public AssociationEndObject createInstance( Node node, boolean findTheSibling ) {	
		NodeAssociationEndObject endObj = new NodeAssociationEndObject();
		endObj.acquireAssociationEndInfo(node, findTheSibling);
		return( endObj );
	}
	
	/**
	 * Returns a new, populated AssociationEndObject.
	 * 
	 * @param refClassName
	 * @param field
	 * @param findSibling
	 * @return
	 */
	public AssociationEndObject createInstance( String refClassName, Field field, boolean findSibling ) {
		ClassAssociationEndObject associationEndObject = new ClassAssociationEndObject( refClassName, field, findSibling );
		
		return( associationEndObject );
	}

	/**
	 * Returns a new, populated AssociationEndObject.
	 * 
	 * @param refClassName
	 * @param eReference
	 * @param findSibling
	 * @return
	 */
	public AssociationEndObject createInstance( String refClassName, EReference eReference, boolean findSibling ) {
		EcoreAssociationEndObject associationEndObject = new EcoreAssociationEndObject( refClassName, eReference, findSibling );
		
		return( associationEndObject );
	}

// attributes
	private static AssociationFactory self 	= null;
}
