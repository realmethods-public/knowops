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

import com.realmethods.codetemplate.model.classes.ClassObject;
import com.realmethods.codetemplate.model.association.AssociationEndObject;
import com.realmethods.codetemplate.parser.ModelParser;

/**
 * Encapsulates the idea of an association being an attribute.  It has all the same behavior and includes
 * the designation of being from an association along with the originating association. Attribute related method 
 * invocations are delegated to the bound ClassObject.  Association related method invocations are delegated to the
 * applied AssociationEndObject.
 * 
 * @author realMethods, Inc.
 *
 */
public class AssociationAttributeObject extends AttributeObject
{
	/**
	 * Constructor
	 * 
	 * @param classObject
	 * @param attribute
	 */
    public AssociationAttributeObject( ClassObject classObject, AttributeObject attribute ) {
        super( classObject, attribute );
        
        sourceAssociation = null;
        sourceClassObject = null;
        isFromAssociation(true);
    }
    
    /**
     * assigns the association, determines if the association type is a ClassObject already parsed
     * into the realMethods App Creation Model
     * @param association
     */
    public void applyAssociation( AssociationEndObject association ) {
        sourceAssociation = association;
        
        // need to determine the context of the association.  If it is from a class in the global context (context is null) 
        // then we can look within the modelParser itself.  
        // If it is from a class within a container (context is the container), need to look there
                
        if ( getClassObject().getContainmentContext() == null )
        	sourceClassObject = ModelParser.modelParser().findClass( association.getType() );
        else
        	sourceClassObject = getClassObject().getContainmentContext()
        								.getChildrenClassObjects()
        								.stream()
        								.filter(classObject -> association.getType().equalsIgnoreCase(classObject.getName())) 
        								.collect(java.util.stream.Collectors.toList())
        								.get(0);
       
        if(sourceClassObject == null && association.isNavigable())
        {
            LOGGER.severe( "Invalid Association : " 
            								+ "Class " 
            								+ getClassObject().getName() 
            								+ " has an association with " 
            								+ association.getType() 
            								+ " but a Class by the name of " 
            								+ association.getType() 
            								+ " cannot be located in the loaded model" );
        }
        
        setName(association.getRoleName());
        
        if( association.isComposite() )
            isComposite(true);
        
        if( association.isAggregate() )
            isAggregate(true);
        
    }

    /**
     * attributes actually come from the bound class object
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
    public ClassObject getSourceClassObject() {
        return sourceClassObject;
    }

    /**
     * Returns the sourceAssociation field.
     *
     * @return
     */
    public AssociationEndObject getAssociation() {
        return sourceAssociation;
    }

    @Override
    /**
     * Delegates to the bound sourceAssociation returning true/false
     * if it itself is isMultivalued()
     * 
     * @return
     */
    public boolean isFromMultiValueAssociation() {
        return getAssociation().isMultivalued();
    }

    @Override
    /**
     * Delegates to the isFromMultiValueAssociation()
     * and returns the NOT of it.
     * 
     * @return
     */
    public boolean isFromSingleValueAssociation() {
        return !isFromMultiValueAssociation();
    }

// attributes
    
    protected AssociationEndObject sourceAssociation	= null;
    protected ClassObject sourceClassObject				= null;
    private static final Logger LOGGER 					= Logger.getLogger(AssociationAttributeObject.class.getName());
}
