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
package com.cloudmigrate.codetemplate.model.association;

import org.eclipse.emf.ecore.EReference;

import com.cloudmigrate.codetemplate.model.factory.AssociationFactory;

/**
 * Encapsulates the notion of an association on EMF EReference.
 * 
 * @author realMethods, Inc.
 *
 */
public class EcoreAssociationEndObject extends AssociationEndObject
{
	/**
	 * Extracts data from the EReference to popular attributes in the parent AssociationEndObject
	 * 
	 * @param refClassName		class name boun to
	 * @param eReference		Eclipse EMF Ecore object
	 * @param findSibling		
	 */
	public EcoreAssociationEndObject(String refClassName, EReference eReference, boolean findSibling)
    {    	
	    setRoleName( eReference.getName() );
	    setRanges( eReference.getLowerBound(), eReference.getUpperBound());
	    setType( eReference.getEType().getName() );

	    isNavigable( true );
	    isOrdered( eReference.isOrdered() );

	    // if true, will attempt to create a sibling object, binding it to ourself
	    // and ourself to it
	    if ( findSibling )
	    {
	        if(	eReference.getEOpposite() != null )
	        	siblingAssociationEndObject = AssociationFactory.getInstance().createInstance( refClassName, eReference.getEOpposite(), false );
	        else { // create a non-navigable sibling association end
	        	siblingAssociationEndObject = new AssociationEndObject();
	        	siblingAssociationEndObject.setVisibility("protected");
	        	siblingAssociationEndObject.setType(refClassName);
	        	siblingAssociationEndObject.setRoleName( refClassName + "-" + getRoleName() );
	        	siblingAssociationEndObject.setLowerRange(1);
	        	siblingAssociationEndObject.setUpperRange(1);	  
	    	}
        	siblingAssociationEndObject.siblingAssociationEndObject = this;
	    }
	}
        
// attributes
}
