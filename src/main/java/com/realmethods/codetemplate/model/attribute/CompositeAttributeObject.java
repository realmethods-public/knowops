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
package com.realmethods.codetemplate.model.attribute;

import java.util.Collection;
import java.util.logging.Logger;

import com.realmethods.codetemplate.model.association.AssociationEndObject;
import com.realmethods.codetemplate.model.classes.ClassObject;
import com.realmethods.codetemplate.parser.ModelParser;

/**
 * Represents a composite association in the form of an AtributeObject
 * 
 * @author realMethods, Inc.
 *
 */
public class CompositeAttributeObject extends AttributeObject
{
	public CompositeAttributeObject() {
		// no_op
	}
	
	/**
     * Assigns the association by validating its parent class exists within the parsed model
     * 
     * @param composite
     */
    public void applyComposite( AssociationEndObject composite ) {
        sourceAssociation = composite;
        sourceClassObject = ModelParser.modelParser().findClass(composite.getType());
        
        if( sourceClassObject == null ) {
            LOGGER.severe( "Invalid Composite Association - Class "
            							 	+ getClassObject().getName() 
            							 	+ " has a composite association with " 
            							 	+ composite.getType() 
            							 	+ " but a Class by the name of " 
            							 	+ composite.getType() 
            							 	+ " cannot be located.");        
        }
        setName(composite.getRoleName());
    }

    /**
     * Returns the unordered attributes of the bound sourceClassObject field.
     * 
     * @return
     */
    public Collection<AttributeObject> getAttributes() {
        return sourceClassObject.getAttributesOrdered(false);
    }

    /**
     * Returns the sourceClassObject field.
     * 
     * @return
     */
    public ClassObject getSourceClassObject()
    {
        return sourceClassObject;
    }

 // attributes
    protected AssociationEndObject sourceAssociation	= null;
    protected ClassObject sourceClassObject				= null;
    private static final Logger LOGGER 					= Logger.getLogger(CompositeAttributeObject.class.getName());

}
