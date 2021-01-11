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


import org.w3c.dom.Node;

import com.realmethods.codetemplate.model.classes.ClassObject;
import com.realmethods.codetemplate.xmi.IXMIProvider;
import com.realmethods.codetemplate.xmi.XMIProviderFactory;

/**
 * Wrapper class to extract attribute data from a DOM Node to assign internally
 * to the subclass AttributeObject
 * 
 * @author realMethods, Inc.
 *
 */
public class NodeAttributeObject extends AttributeObject
{
	/**
	 * Provides the ClassObject to the base class constructor, extracts
	 * data from the DOM Node and assigns to the base class.
	 * 
	 * @param classObject
	 * @param attributeNode
	 */
    public NodeAttributeObject( ClassObject classObject, Node attributeNode )
    {
    	super( classObject );
    	
        IXMIProvider xmiProvider 	= XMIProviderFactory.getInstance().getXMIProvider();
        
        setName( xmiProvider.getElementName( attributeNode ) );
        setType(xmiProvider.getAttributeType(attributeNode));
        setVisibility( xmiProvider.getVisibility(attributeNode) );
        setTags(xmiProvider.getTagNamesAndValues(attributeNode));

        isPrimaryKey( xmiProvider.getRequiredIndicator(attributeNode) );
        canBeNull( xmiProvider.getNullIndicator(attributeNode) );
        isIntrinsicType( xmiProvider.getIntrinsicTypeIndicator(attributeNode) );        
        isStatic( xmiProvider.getStaticIndicator(attributeNode) );
        isFinal( xmiProvider.getFinalIndicator(attributeNode) );
        isTransient( xmiProvider.getTransientIndicator(attributeNode) );
        isVolatile( xmiProvider.getVolatileIndicator(attributeNode) );
        isUserIdentifiable( xmiProvider.getUserIdentifiableIndicator(attributeNode) );
        
        applyDefaultValue( xmiProvider.getDefaultValue(attributeNode) );
        
    }
        
// attributes

}
