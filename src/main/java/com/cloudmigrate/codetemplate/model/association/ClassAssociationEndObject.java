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

import java.lang.reflect.Field;

/**
 * Encapsulates the notion of an association on a Java Class instance.
 * 
 * @author realMethods, Inc.
 *
 */
public class ClassAssociationEndObject extends AssociationEndObject
{
	/**
	 * Extracts the field data in order to populate data found in the parent AssociationEndObject
	 * 
	 * @param refClassName
	 * @param field
	 * @param findSibling
	 */
    public ClassAssociationEndObject(String refClassName, Field field, boolean findSibling)
    {
	    // take a look and see if the field is an array or not
	    Class array = field.getClass().getComponentType();
	    
	    if ( array != null )
	    	setRanges( 0, -1);
	    else
	    	setRanges( 1, 1 );
	    
	    setType( field.getClass().getSimpleName() );
    	setRoleName( field.getName() );
	    isNavigable( true );
	    isOrdered( false );

    	// create a non-navigable one and bind it to ourself, and ourself to it

	    if ( findSibling )
	    {
	        siblingAssociationEndObject = new AssociationEndObject();
	        siblingAssociationEndObject.setVisibility("protected");
	        siblingAssociationEndObject.setType(refClassName);
	        siblingAssociationEndObject.setRoleName( refClassName + "-" + getRoleName() );
	        siblingAssociationEndObject.setLowerRange(1);
	        siblingAssociationEndObject.setUpperRange(1);
	        
        	siblingAssociationEndObject.siblingAssociationEndObject = this;	        
	    }   
	}    	

// attributes
    
}
