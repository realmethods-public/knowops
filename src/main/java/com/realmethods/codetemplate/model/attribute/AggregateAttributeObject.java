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

import java.util.List;
import java.util.logging.Logger;

import com.realmethods.codetemplate.model.classes.ClassObject;
import com.realmethods.codetemplate.model.association.AssociationEndObject;
import com.realmethods.codetemplate.parser.ModelParser;

/**
 * Represents an aggregate association in the form of an AtributeObject
 * 
 * @author realMethods, Inc.
 *
 */
public class AggregateAttributeObject extends AttributeObject
{
    /**
     * Assigns the aggregate association. Validates the aggregate type is that of a ClassObject already
     * loaded in the model. 
     * @param aggregate
     */
    public void applyAggregate(AssociationEndObject aggregate)
    {
        sourceAssociation = aggregate;
        sourceClassObject = ModelParser.modelParser().findClass(aggregate.getType());
        
        if(sourceClassObject == null)
        {
            LOGGER.warning( "Invalid Aggregate Association - " 
            						+ "Class " 
            						+ getClassObject().getName() 
            						+ " has an aggregate association with " 
            						+ aggregate.getType() 
            						+ " but a Class by the name of " 
            						+ aggregate.getType() 
            						+ " cannot be located." );
        }
        setName(aggregate.getRoleName());
    }

    /**
     * Returns the attributes from the sourceClassObject.
     * 
     * @return List<AttributeObject>
     */
    public List<AttributeObject> getAttributes() {
        return sourceClassObject.getAttributesOrdered(false);
    }
    
    /**
     * Returns the sourceClassObject field.
     * 
     * @return
     */
    public ClassObject getSourceClassObject() {
        return sourceClassObject;
    }

 // attributes
    protected AssociationEndObject sourceAssociation	= null;
    protected ClassObject sourceClassObject				= null;

    private static final Logger LOGGER 	= Logger.getLogger(AggregateAttributeObject.class.getName());

}
